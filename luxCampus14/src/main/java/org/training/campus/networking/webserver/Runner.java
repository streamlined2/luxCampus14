package org.training.campus.networking.webserver;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;

public class Runner {
	private static final int SERVER_COUNT = 1;
	private static final int FIRST_SERVER_PORT = 4444;
	private static final long WORKING_TIME = 50_000;
	private static final Charset CURRENT_CHARSET = StandardCharsets.UTF_8;

	public static void main(String[] args) {
		final ThreadGroup serverGroup = new ThreadGroup("servers");

		RunnableFuture<Void>[] servers = startServers(serverGroup);

		try {
			Thread.sleep(WORKING_TIME);

			terminate(servers, serverGroup);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private static RunnableFuture<Void>[] startServers(ThreadGroup group) {
		RunnableFuture<Void>[] servers = new Server[SERVER_COUNT];
		for (int k = 0; k < SERVER_COUNT; k++) {
			servers[k] = new Server(k, getServerPort(k), CURRENT_CHARSET);
			new Thread(group, servers[k]).start();
		}
		return servers;
	}

	private static int getServerPort(int serverOrdinal) {
		return FIRST_SERVER_PORT + serverOrdinal;
	}

	private static void terminate(RunnableFuture<Void>[] execs, ThreadGroup group) {
		for (RunnableFuture<Void> ex : execs) {
			ex.cancel(true);
		}
		for (RunnableFuture<Void> ex : execs) {
			try {
				ex.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		group.interrupt();
		do {
			Thread[] threads = new Thread[group.activeCount()];
			if (threads.length == 0) {
				break;
			}
			group.enumerate(threads, false);
			for (Thread thread : threads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} while (true);
	}

}
