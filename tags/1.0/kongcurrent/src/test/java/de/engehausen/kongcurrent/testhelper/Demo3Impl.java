package de.engehausen.kongcurrent.testhelper;

/**
 * Test class. You wouldn't code this stateful of course, but this is to test
 * "illegal" concurrent access...
 */
public class Demo3Impl {
	
	private volatile int modCount;
	private volatile int currentMax;

	public Demo3Impl() {
		modCount = Integer.MIN_VALUE;
	}
	
	public int getMax() {
		return currentMax;
	}
	
	protected void setMax(final int m) {
		modCount++;
		currentMax = m;
	}

	public void determineMax(final int[] list) {
		final int now = modCount;
		int max = list[0];
		for (int i = list.length; i-->1; ) {
			if (list[i] > max) {
				max = list[i];
				Thread.yield();
			}
		}
		if (now != modCount) {
			throw new IllegalStateException("object state changed while processing");
		} else {
			setMax(max);
		}
	}
	
}
