package com.behsa.ganjex.util;

import java.util.concurrent.*;

/**
 * This static class is a utility to keep Executors and provide access to them
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class Executors {
	private static ScheduledExecutorService scheduled = new
					ScheduledThreadPoolExecutor(1);
	private static ExecutorService executor =
					new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS,
									new LinkedBlockingQueue<>());

	private Executors() throws IllegalAccessException {
		throw new IllegalAccessException("this method should not be invoked");
	}

	public static ScheduledExecutorService scheduledExecutor() {
		return scheduled;
	}

	public static ExecutorService executor() {
		return executor;
	}

}
