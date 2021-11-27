package org.training.campus.networking.webserver;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;

public class Runner {
	private static final int SERVER_COUNT = 1;
	private static final int FIRST_SERVER_PORT = 4444;
	private static final long WORKING_TIME = 50_000;

	public static void main(String[] args) {
		final ThreadGroup serverGroup = new ThreadGroup("servers");

		try {
			RequestParser parser = new RequestParser();
			ResponseWriter writer = new ResponseWriter();
			RunnableFuture<Void>[] servers = startServers(serverGroup, parser, writer);

			Thread.sleep(WORKING_TIME);

			terminate(servers, serverGroup);
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}

	}

	private static RunnableFuture<Void>[] startServers(ThreadGroup group, RequestParser parser, ResponseWriter writer)
			throws IOException {
		RunnableFuture<Void>[] servers = new Server[SERVER_COUNT];
		for (int k = 0; k < SERVER_COUNT; k++) {
			servers[k] = new Server(k, getServerPort(k), parser, writer);
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
