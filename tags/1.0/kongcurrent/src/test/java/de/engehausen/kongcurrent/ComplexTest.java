package de.engehausen.kongcurrent;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.engehausen.kongcurrent.helper.DefaultDescriptions;
import de.engehausen.kongcurrent.helper.DefaultExceptionHandler;

public class ComplexTest extends AbstractMonitorTest {

	/**
	 * Tests handling an exception occurring under concurrent load.
	 */
	@Test
	public void testHandling() {
		final Description<List<String>> desc = DefaultDescriptions.<String>listDescription();

		final List<String> list = buildList("one", "two", null, "three");
		final List<String> monitored = Monitor.monitor(list, desc, new DefaultExceptionHandler(logger));
		
		final Thread threads[] = new Thread[2];
		final Executor executors[] = new Executor[threads.length];
		
		for (int i = threads.length; i-->0; ) {
			executors[i] = new Executor(monitored);
			threads[i] = new Thread(executors[i]);
		}
		for (Thread thread : threads) {
			thread.start();
		}
		int i = 0;
		for (i = 0; i < 10; i++) {
			try {
				Thread.sleep(500L);
			} catch (InterruptedException ie) {
				i--;
			}
			// try to corrupt the internal state of the list by concurrently adding to it
			// while other threads iterate over it
			monitored.add(Integer.toString(i));
			int count = 0;
			for (Thread thread : threads) {
				if (thread.isAlive()) {
					count++;
				}
			}
			if (count < threads.length) {
				break;
			}
		}
		assertTrue(i < 10);
		for (Executor e : executors) {
			e.cancel();
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		final String log = logger.toString();
		assertTrue(log.contains(".add(Unknown Source)"));
		assertTrue(log.contains("ConcurrentModificationException"));
		
	}

	private static class Executor implements Runnable {
		
		private volatile boolean canceled;
		
		private final List<String> list;
		
		public Executor(final List<String> aList) {
			list = aList;
		}

		@Override
		public void run() {
			while (!canceled) {
				int temp = 0;
				for (String str : list) {
					temp += (str==null)?0:str.length();
				}
			}
		}
		
		public void cancel() {
			canceled = true;
		}
				
	}
	
}
