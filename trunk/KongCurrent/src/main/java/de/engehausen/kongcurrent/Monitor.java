package de.engehausen.kongcurrent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.engehausen.kongcurrent.cglib.DescriptionCglib;
import de.engehausen.kongcurrent.cglib.MonitorCglib;
import de.engehausen.kongcurrent.helper.DefaultComparators;
import de.engehausen.kongcurrent.helper.DefaultDescriptions;
import de.engehausen.kongcurrent.helper.DefaultExceptionHandler;

/**
 * Helper to monitor parallel method invocations for exceptions.
 * <p><img src="doc-files/Monitor-1.png">
 * <p>The monitor creates a proxied version of an object implementing some
 * interface; the proxy can track invocations of the objects' methods and
 * can report on potential concurrent access on the object. This can be
 * used to help find out code paths that concurrently access the object
 * (through the interface methods).
 * <p>Here is a simple example code snippet that will be adapted to use the monitor:
 * <pre>List&lt;String&gt; myList = new ArrayList&lt;String&gt;();
 *processing(myList);</pre>
 * Now you want to know what happens to the list when it is used in the
 * <code>processing(List&lt;String&gt;)</code> method. Instead of the
 * original list you simply pass the monitored version to the processing
 * method:
 * <pre>List&lt;String&gt; myList = new ArrayList&lt;String&gt;();
 *List&lt;String&gt; monitoredList = Monitor.monitor(myList, DefaultDescriptions.&lt;String&gt;listDescription(), new DefaultExceptionHandler());
 *processing(monitoredList);</pre>
 * This code will create a monitored list based on the default description
 * for {@link List} objects; the default logger passed in will output to
 * {@link java.lang.System.out}. The {@link DefaultExceptionHandler} will record the
 * stack traces of all callers and output these in case an exception occurs
 * during method invocation of a monitored method. <b>Important:</b> Keeping
 * track of this information is <i>costly</i>. The methods of the monitored
 * instance can be considerably slowed down; this can directly affect the
 * situation you try to analyze, up to the point where the situation you
 * try to understand does not happen any more. Please keep this in mind.
 * <p>In case of a problem, e.g. a {@link ConcurrentModificationException} the
 * logger would output something similar to this:
 * <pre>exception occurred:
 *Thread[Thread-1,5,main] - java.util.ConcurrentModificationException
 *	at java.util.AbstractList$Itr.checkForComodification(Unknown Source)
 *	at java.util.AbstractList$Itr.next(Unknown Source)
 *	at sun.reflect.GeneratedMethodAccessor2.invoke(Unknown Source)
 *	at sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)
 *	at java.lang.reflect.Method.invoke(Unknown Source)
 *	at de.engehausen.kongcurrent.Monitor$MonitorHandler.invoke(Monitor.java:124)
 *	at $Proxy1.next(Unknown Source)
 *	at Test.run(Unknown Source)
 *	at java.lang.Thread.run(Unknown Source) 
 *
 *the following threads were recently operating on the object:
 *Thread[Thread-1,5,main] - java.lang.Exception: caller...
 *	at $Proxy1.next(Unknown Source)
 *	at Test.run(Unknown Source)
 *	at java.lang.Thread.run(Unknown Source)
 *Thread[main,5,main] - java.lang.Exception: caller...
 *	at $Proxy0.add(Unknown Source)
 *	at Test.run(Unknown Source)
 *	at java.lang.Thread.run(Unknown Source)</pre>
 * The output informs you that a <code>ConcurrentModificationException</code>
 * occurred in thread "Thread-1,5,main" while iterating the list. It also shows
 * you the stack traces of "recent" calls to the object. In there you find a thread
 * which added to the list (thread "main,5,main"), while the other one was iterating
 * the list - which explains why the exception occurred.
 * <p>All this can be adapted to your needs. If you have a custom interface
 * to monitor, you will likely need to provide a {@link Description} for the
 * interface to monitor, which includes methods which may return "dependant"
 * objects (i.e. objects that somehow are backed by the monitored object).
 * If the object to monitor has special semantics you need to provide an
 * appropriate {@link Comparator}. For the Java collection objects default
 * descriptions and comparators exist (see {@link DefaultDescriptions} and
 * {@link DefaultComparators}).
 */
public class Monitor {
	
	protected Monitor() {		
	}

	/**
	 * Creates a monitored version of the given target.
	 * @param target the instance to monitor, must not be <code>null</code>.
	 * @param description a description of the interface, must not be <code>null</code>.
	 * @param handler an exception handler that keeps track of invocations on the
	 * proxied object; a single exception handler instance should be used per monitored
	 * instance, or the handler must be capable of tracking different objects at the
	 * same time.
	 * @return a monitored version of the object to monitor, never <code>null</code>.
	 * @param <T> the type of object to monitor
	 */
	public static <T> T monitor(final T target, final Description<T> description, final ExceptionHandler handler) {
		return (T) monitorGeneric(target, description, handler);
	}

	@SuppressWarnings("unchecked")
	protected static <T> T monitorGeneric(final T target, final Description description, final ExceptionHandler handler) {
		return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(), new Class<?>[] { description.getInterface() }, new MonitorHandler(target, description, handler));
	}

	/**
	 * The invocation handler.
	 * @param <T>
	 */
	private static class MonitorHandler<T> implements InvocationHandler {

		protected static final Map<Method, Method> DIRECT_METHODS;
		
		static {
			final Map<Method, Method> temp = new HashMap<Method, Method>();
			Method method = getMethod(Object.class, "toString", (Class<?>[]) null);
			temp.put(method, method);
			method = getMethod(Object.class, "hashCode", (Class<?>[]) null);
			temp.put(method, method);
			method = getMethod(Object.class, "equals", new Class<?>[] { Object.class });
			temp.put(method, getMethod(MonitorHandler.class, "equals", new Class<?>[] { Object.class, Object.class }));
			DIRECT_METHODS = Collections.unmodifiableMap(temp);
		}
		
		private static Method getMethod(final Class<?> clazz, final String name, final Class<?>... argTypes) {
			try {
				return clazz.getDeclaredMethod(name, argTypes);
			} catch (SecurityException e) {
				throw new IllegalStateException(e);
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException(e);
			}
			
		}

		protected final Object target;
		protected final Description<Object> description;
		protected final ExceptionHandler handler;
		
		public MonitorHandler(final Object aTarget, final Description<Object> aDescription, final ExceptionHandler aHandler) {
			target = aTarget;
			description = aDescription;
			handler = aHandler;
		}

		@Override
		public Object invoke(final Object obj, final Method method, final Object[] args) throws Throwable {
			Object result;
			final Method directMethod = DIRECT_METHODS.get(method);
			if (directMethod == null) {
				try {
					handler.preInvoke(target, method, args);
					result = method.invoke(target, args);					
				} catch (InvocationTargetException e) {
					handler.handle(e.getCause(), target, method, args);
					throw e.getCause();
				} catch (Throwable t) {
					handler.handle(t, target, method, args);
					throw t;
				} finally {
					handler.postInvoke(target, method, args);
				}
				final Description<?> desc = description.getDescription(method);
				if (desc instanceof DescriptionCglib<?>) {
					// TODO runtime dependency to cglib??? will it fail without cglib?
					result = MonitorCglib.monitor(result, (DescriptionCglib) description, handler);
				} else if (desc != null) {
					result = monitorGeneric(result, desc, handler);					
				}
			} else {
				// one of mine
				final Object[] arguments;
				final Class<?> clz = getClass();
				if (clz.equals(directMethod.getDeclaringClass())) { // a "standard" method that needs an extra argument
					if (args != null && args.length>0) {
						// specific monitor handler method, add invocation target
						arguments = new Object[args.length+1];
						System.arraycopy(args, 0, arguments, 0, args.length);
						arguments[args.length] = obj;
					} else {
						arguments = new Object[] { obj };
					}
				} else {
					arguments = args;
				}
				result = directMethod.invoke(this, arguments);
			}
			return result;
		}

		public String toString() {
			final String contents = target.toString();
			final StringBuilder sb = new StringBuilder(contents.length()+12);
			sb.append("{monitored:").append(contents).append('}');
			return sb.toString();
		}
		
		public int hashCode() {
			return target.hashCode();
		}

		/**
		 * Extended equals method which provides the correct "this" in form of
		 * the invocation target.
		 * @param other the object to compare against, may be <code>null</code>
		 * @param invocationTarget the proxy around the handler, never <code>null</code>
		 * @return
		 */
		@SuppressWarnings("unused") // called via reflection from invoke()
		public boolean equals(final Object other, final Object invocationTarget) { 
			if (other == null) {
				return false;
			} else if (other == this || other == invocationTarget) {
				return true;
			} else {
				final Class<?> clz = description.getInterface();
				// other must implement the same interface as us
				if (clz.isAssignableFrom(other.getClass())) {
					// to compare, custom logic may be required (e.g. collection, list and set)
					return description.getComparator().equals(invocationTarget, other);					
				} else {
					return false;
				}
			}
		}
		
	}
		
}
