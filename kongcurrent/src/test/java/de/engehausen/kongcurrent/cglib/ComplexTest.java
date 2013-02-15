package de.engehausen.kongcurrent.cglib;

import org.junit.Assert;
import org.junit.Test;

import de.engehausen.kongcurrent.helper.DefaultExceptionHandler;
import de.engehausen.kongcurrent.testhelper.Demo3Impl;
import de.engehausen.kongcurrent.testhelper.Recorder;

public class ComplexTest {
	
	@Test
	public void testHandling() {
		final Recorder logger = new Recorder();
		final Demo3Impl monitored = MonitorCglib.monitor(
				new Demo3Impl(),
				new DescriptionCglib<Demo3Impl>(Demo3Impl.class),
				new DefaultExceptionHandler(logger));
		final Thread[] threads = new Thread[4];
		final Executor[] executors = new Executor[threads.length];
		for (int i = threads.length; i-->0; ) {
			executors[i] = new Executor(monitored);
			threads[i] = new Thread(executors[i]);
		}
		for (int i = threads.length; i-->0; ) {
			threads[i].start();
		}
		boolean foundException = false;
		for (int i = threads.length; i-->0; ) {
			try {
				threads[i].join();
				if (executors[i].getCause() != null) {
					foundException = true;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Assert.assertTrue(foundException);
		final String info = logger.toString();
		Assert.assertTrue(info.contains("object state changed while processing"));
		Assert.assertTrue(info.contains("EnhancerByCGLIB"));
		Assert.assertTrue(info.contains(".doit("));
	}
	
	private static class Executor implements Runnable {

		private final Demo3Impl monitored;
		private Throwable cause;
		
		public Executor(final Demo3Impl impl) {
			monitored = impl;
		}
		
		public Throwable getCause() {
			return cause;
		}
		
		@Override
		public void run() {
			final int[] list = new int[32];
			for (int i = list.length; i-->0; ) {
				list[i] = i;
			}
			try {
				for (int i = 0; i < 4096; i++) {
					doit((int) (5*Math.random()), list);
				}
			} catch (Throwable t) {
				cause = t;
			}
		}
		
		protected void doit(final int count, final int[] list) {
			if (count > 0) {
				doit(count-1, list);
			} else {
				monitored.determineMax(list);
			}
		}
		
	}

}
