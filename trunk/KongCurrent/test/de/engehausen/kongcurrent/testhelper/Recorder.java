package de.engehausen.kongcurrent.testhelper;

import de.engehausen.kongcurrent.Logger;

public class Recorder implements Logger {
	
	protected final StringBuilder buffer;
	
	public Recorder() {
		buffer = new StringBuilder(256);
	}

	@Override
	public void log(final String message) {
		buffer.append(message).append("\n");
	}
	
	public String toString() {
		return buffer.toString();
	}

}
