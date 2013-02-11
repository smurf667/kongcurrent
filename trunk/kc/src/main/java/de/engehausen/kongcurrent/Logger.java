package de.engehausen.kongcurrent;

/**
 * Simple logger interface to wrap around a variety of other loggers.
 */
public interface Logger {

	/**
	 * Sends a string to the log.
	 * @param message the string to write to the log
	 */
	void log(String message);

}
