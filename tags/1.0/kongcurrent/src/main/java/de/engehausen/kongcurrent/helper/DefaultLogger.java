package de.engehausen.kongcurrent.helper;

import java.io.PrintStream;

import de.engehausen.kongcurrent.Logger;

/**
 * A logger implementation which outputs strings
 * to a print stream, e.g. to <code>System.out</code>.
 */
public class DefaultLogger implements Logger {
	
	protected final PrintStream printStream;

	/**
	 * Creates a logger which uses {@link System#out}.
	 */
	public DefaultLogger() {
		this(System.out);
	}

	/**
	 * Creates a logger which outputs the strings to log
	 * to the given print stream.
	 * @param stream the stream to write to.
	 */
	public DefaultLogger(final PrintStream stream) {
		printStream = stream;
	}

	@Override
	public void log(final String message) {
		printStream.println(message);
	}

}
