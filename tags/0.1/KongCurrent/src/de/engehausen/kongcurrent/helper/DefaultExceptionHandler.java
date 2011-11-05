package de.engehausen.kongcurrent.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import de.engehausen.kongcurrent.ExceptionHandler;
import de.engehausen.kongcurrent.Logger;

/**
 * A handler implementation for tracking invocations to a monitored
 * object. In case of an exception information is written to the
 * provided log instance which shows the potentially involved threads
 * when the invocation failed.<br>
 * This handler is written to be used <i>per monitored instance</i>, i.e.
 * it must not be shared between more than one monitored instance. Keep
 * in mind that this implementation may slow down performance of the
 * monitored object considerably; this may even affect the concurrency 
 * situation that is to be analyzed through the handler.
 */
public class DefaultExceptionHandler implements ExceptionHandler {
	
	protected final Logger logger;
	protected final Map<Thread, Exception> callers;
	
	/**
	 * Creates the exception handler using the {@link DefaultLogger}.
	 */
	public DefaultExceptionHandler() {
		this(new DefaultLogger());
	}
	
	/**
	 * Creates the exception handler using the given logger.
	 * @param aLogger the logger to use, must not be <code>null</code>.
	 */
	public DefaultExceptionHandler(final Logger aLogger) {
		logger = aLogger;
		// tracks invokers of the monitored object via exceptions
		// and by thread. the thread is held weakly; if the thread
		// goes, so will the map entry
		callers = new WeakHashMap<Thread, Exception>();
	}

	/**
	 * Captures the current thread and store it and the calling stack
	 * for analysis if {@link #handle(Throwable, Object, Method, Object[])} is called.
	 * @param target the monitored object
	 * @param method the method to be invoked on that object
	 * @param args the arguments to the method
	 */
	public void preInvoke(final Object target, final Method method, final Object[] args) {
		final Thread currentThread = Thread.currentThread();
		final Exception e = new Exception("caller...");
		synchronized (callers) {
			callers.put(currentThread, e); 			
		}
	}

	/**
	 * Does nothing.
	 * @param target the monitored object
	 * @param method the method invoked on that object
	 * @param args the arguments to the method
	 */
	public void postInvoke(final Object target, final Method method, final Object[] args) {
		// does nothing; removing the calling information right now may hide
		// the fact that it was "recently" invoked. no memory leak will occur
		// through the callers map, since it weakly holds the keys - in worst
		// case a stack is remembered in the map that has not been called in a
		// while
	}

	/**
	 * Handles an exception which occurred while executing a method
	 * of the monitored object.
	 * @param throwable the exception which occurred while executing a method
	 * @param target the monitored object
	 * @param method the method invoked on that object
	 * @param args the arguments to the method
	 */
	public void handle(final Throwable throwable, final Object target, final Method method, final Object[] args) {
		final StringWriter sw = new StringWriter(4096);
		final PrintWriter pw = new PrintWriter(sw);
		sw.write("exception occurred:\n");
		showStack(pw, Thread.currentThread(), throwable);
		sw.append("\nthe following threads were recently operating on the object:\n");
		synchronized (callers) {
			for (Map.Entry<Thread, Exception> entry : callers.entrySet()) {
				final Exception e = entry.getValue();
				fixStack(e, 2); // suppress the pre-invoke from the stack trace..
				showStack(pw, entry.getKey(), e);
			}
		}
		logger.log(sw.toString());
	}
	
	protected void fixStack(final Exception exception, final int cut) {
		final StackTraceElement[] elements = exception.getStackTrace();
		final int max = elements.length - cut;
		if (max > 0) {
			final StackTraceElement[] shortened = new StackTraceElement[max];
			System.arraycopy(elements, cut, shortened, 0, max);
			exception.setStackTrace(shortened);
		}
	}
	
	protected void showStack(final PrintWriter pw, final Thread thread, final Throwable throwable) {
		pw.append(thread.toString()).append(" - ");
		throwable.printStackTrace(pw);
	}

}
