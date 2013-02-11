package de.engehausen.kongcurrent.helper;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

public class DefaultLoggerTest {
	
	@Test
	public void testDefaultLogger() throws Exception {
		final DefaultLogger logger1 = new DefaultLogger();
		logger1.log("test123"); // must work
		final ByteArrayOutputStream baos = new ByteArrayOutputStream(32);
		final PrintStream stream = new PrintStream(baos);
		final DefaultLogger logger2 = new DefaultLogger(stream);
		logger2.log("hello");
		final String message = new String(baos.toByteArray());
		Assert.assertEquals("hello\r\n", message);
	}

}
