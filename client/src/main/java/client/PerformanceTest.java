package client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceTest {
	private static final Logger logger = LoggerFactory.getLogger(PerformanceTest.class);
	private int counterFinishedTasks = 0;
	private long startMillis;
	private int numberOfThreads = 0;
	private int numberOfIterations = 0;
	private int messageSizeInMB;

	public PerformanceTest(int numberOfThreads, int numberOfIterations, int messageSizeInMB) {
		this.numberOfThreads = numberOfThreads;
		this.numberOfIterations = numberOfIterations;
		this.messageSizeInMB = messageSizeInMB;
	}

	public static void main(String args[]) {
		if (args.length != 3) {
			System.err
					.println("Please provide three arguments as numbers: numberOfIterationsPerThread numberOfThreads messsageSizeInMB");
			return;
		}
		int numberOfIterationsPerThread = 0;
		try {
			numberOfIterationsPerThread = Integer.valueOf(args[0]);
		} catch (NumberFormatException e) {
			System.err.println("Please provide a number for numberOfIterationsPerThread.");
			return;
		}
		int numberOfThreads = 0;
		try {
			numberOfThreads = Integer.valueOf(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("Please provide a number for numberOfThreads.");
			return;
		}
		int messageSizeInMB = 0;
		try {
			messageSizeInMB = Integer.valueOf(args[2]);
		} catch (NumberFormatException e) {
			System.err.println("Please provide a number for messageSizeInMB.");
			return;
		}
		PerformanceTest performanceTest = new PerformanceTest(numberOfThreads, numberOfIterationsPerThread,
				messageSizeInMB);
		performanceTest.run();
	}

	public void run() {
		this.startMillis = System.currentTimeMillis();
		BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(numberOfThreads);
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 10,
				TimeUnit.SECONDS, workQueue);
		for (int i = 0; i < numberOfThreads; i++) {
			threadPoolExecutor.execute(new PerformanceTestRunnable(numberOfIterations, this, messageSizeInMB));
		}
		threadPoolExecutor.shutdown();
	}

	public synchronized void tellFinished() {
		this.counterFinishedTasks++;
		if (this.counterFinishedTasks == this.numberOfThreads) {
			long totalMillis = System.currentTimeMillis() - startMillis;
			int totalItems = numberOfThreads * numberOfIterations;
			double itemsPerSecond = ((double) totalItems / (double) totalMillis) * 1000.0;
			double bytesPerSecond = (((double) totalItems * 1024.0 * 1024.0) / (double) totalMillis) * 1000.0;
			double mbBytesPerSecond = bytesPerSecond / (1024 * 1024);
			logger.info(String
					.format("%s Threads, %s iterations per Thread=%s items of %s MB uploaded in %s milliseconds: %f items/s, %f Bytes/s, %f MB/s.",
							numberOfThreads, numberOfIterations, totalItems, messageSizeInMB, totalMillis,
							itemsPerSecond, bytesPerSecond, mbBytesPerSecond));
		}
	}
}
