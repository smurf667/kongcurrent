package de.engehausen.kongcurrent.testhelper;

public class DemoImpl implements Demo {

	protected final int number;
	
	public DemoImpl() {
		number = 0;
	}
	
	public DemoImpl(final int n) {
		number = n;
	}

	@Override
	public int getInt() {
		return number;
	}

}
