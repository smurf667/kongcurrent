package de.engehausen.kongcurrent.helper;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import de.engehausen.kongcurrent.AbstractMonitorTest;

public class DefaultExceptionHandlerTest extends AbstractMonitorTest {

	@Test
	public void testInvocation() throws NoSuchMethodException {
		new DefaultExceptionHandler(); // test constructor...
		
		final List<String> list = Collections.emptyList();
		final Method method = List.class.getDeclaredMethod("contains", new Class<?>[] { Object.class });
		final Object[] args = new Object[] { "hello" };

		final DefaultExceptionHandler handler = new DefaultExceptionHandler(logger);
		
		handler.preInvoke(list, method, args);
		handler.handle(new Exception("123"), list, method, args);
		handler.postInvoke(list, method, args);
		
		final String result = logger.toString();
		assertTrue("log does not contain expected string", result.contains("123"));
		assertTrue("log does not contain expected string", result.contains("de.engehausen.kongcurrent.helper.DefaultExceptionHandlerTest.testInvocation"));
	}
	
}
