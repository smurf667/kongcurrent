package de.engehausen.kongcurrent.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import de.engehausen.kongcurrent.Description;
import de.engehausen.kongcurrent.ExceptionHandler;
import de.engehausen.kongcurrent.Monitor;

/**
 */
public class MonitorCglib {
	
	protected MonitorCglib() {		
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
	public static <T> T monitor(final T target, final DescriptionCglib<T> description, final ExceptionHandler handler) {
		return (T) monitorGeneric(target, description, handler);
	}

	@SuppressWarnings("unchecked")
	protected static <T> T monitorGeneric(final T target, final DescriptionCglib description, final ExceptionHandler handler) {
		if (description.getInterface().isInterface()) {
			return (T) Monitor.monitor(target, description, handler);
		} else {
			final MonitorHandler monitorHandler = new MonitorHandler(target, description, handler);
			final ConstructorInformation ctorInfo = description.getConstructorInformation();
			if (ctorInfo == null) {
				// expecting default constructor
				return (T) Enhancer.create(target.getClass(), monitorHandler);
			} else {
				final Enhancer e = new Enhancer();
				e.setSuperclass(target.getClass());
				e.setCallback(monitorHandler);
				return (T) e.create(ctorInfo.getArgTypes(), ctorInfo.getArgValues());
			}
		}
	}

	/**
	 * The invocation handler.
	 * @param <T>
	 */
	private static class MonitorHandler<T> implements MethodInterceptor {

		protected final Object target;
		protected final DescriptionCglib<Object> description;
		protected final ExceptionHandler handler;
		
		public MonitorHandler(final Object aTarget, final DescriptionCglib<Object> aDescription, final ExceptionHandler aHandler) {
			target = aTarget;
			description = aDescription;
			handler = aHandler;
		}

		@Override
		public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
			Object result;
			try {
				handler.preInvoke(target, method, args);
				result = proxy.invokeSuper(obj, args);
			} catch (Throwable t) {
				handler.handle(t, target, method, args);
				throw t;
			} finally {
				handler.postInvoke(target, method, args);
			}
			final Description<?> desc = description.getDescription(method);
			if (desc instanceof DescriptionCglib<?>) {
				result = monitorGeneric(result, (DescriptionCglib<?>) desc, handler);
			} else if (desc != null) {
				result = Monitor.monitor(result, description, handler);
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
