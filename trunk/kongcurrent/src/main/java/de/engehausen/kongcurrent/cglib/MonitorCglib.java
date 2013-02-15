package de.engehausen.kongcurrent.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import de.engehausen.kongcurrent.Description;
import de.engehausen.kongcurrent.ExceptionHandler;
import de.engehausen.kongcurrent.Monitor;
import de.engehausen.kongcurrent.helper.DefaultExceptionHandler;

/**
 * Experimental helper to monitor parallel method invocations for exceptions on 
 * actual <i>classes</i> (versus interfaces as described in {@link Monitor}).
 * The proxy can track invocations of the objects' methods and
 * can report on potential concurrent access on the object. This can be
 * used to help find out code paths that concurrently access the object.
 * <p>This monitor uses <a href="http://cglib.sourceforge.net/">cglib</a> to build
 * a proxied (actually subclassed) version of the object. The cglib library and its dependencies must be on the classpath.
 * For proxying to work a new object of the class must either be constructable using
 * the default constructor or using a constructor with arguments. Classes with constructors
 * that use arguments need to be described using the {@link ConstructorInformation} in
 * {@link DescriptionCglib}.
 * <p>Here is a simple example code snippet that will be adapted to use the monitor:
 * <pre>MyClass myImpl = new MyClass("default");
 *processing(myList);</pre>
 * Now you want to know what happens to the object when it is used in the
 * <code>processing(MyClass)</code> method. Instead of the
 * original object you simply pass the monitored version to the processing
 * method:
 * <pre> final MyClass myImpl = new MyClass("default");
 * //constructor takes String as argument, value supplied by myImpl instance
 * ConstructorInformation&lt;MyClass&gt; ctorInfo = new ConstructorInformation&lt;MyClass&gt;(String.class) {
 *     public Object[] getArgValues() {
 *         return new Object[] { myImpl.getSetting() }; // getSetting() would return value set in CTOR
 *     }
 * };
 * // description for MyClass, with constructor information
 * DescriptionCglib&lt;MyClass&gt; description = new DescriptionCglib&lt;MyClass&gt;(MyClass.class, ctorInfo);
 * // creates a monitored instance that was created with the same
 * // constructor as the original instance
 * MyClass monitored = MonitorCglib.monitor(myImpl, description, new DefaultExceptionHandler());
 * 
 * processing(monitored);</pre>
 * This code will create a monitored instance based on the description of the
 * original class; the default logger passed in will output to
 * {@link java.lang.System.out}. The {@link DefaultExceptionHandler} will record the
 * stack traces of all callers and output these in case an exception occurs
 * during method invocation of a monitored method. <b>Important:</b> Keeping
 * track of this information is <i>costly</i>. The methods of the monitored
 * instance can be considerably slowed down; this can directly affect the
 * situation you try to analyze, up to the point where the situation you
 * try to understand does not happen any more. Please keep this in mind.
 * <p>In case of a problem, e.g. a {@link IllegalStateException} the
 * logger would output something similar to this:
 * <pre>exception occurred:
 *Thread[Thread-1,5,main] - java.lang.IllegalStateException: object state changed while processing
 *	at de.engehausen.kongcurrent.testhelper.Demo3Impl.determineMax(Demo3Impl.java:35)
 *	at de.engehausen.kongcurrent.testhelper.Demo3Impl$$EnhancerByCGLIB$$cfa20a97.CGLIB$determineMax$2(<generated>)
 *...
 *	at de.engehausen.kongcurrent.cglib.ComplexTest$Executor.run(ComplexTest.java:68)
 *	at java.lang.Thread.run(Unknown Source)
 *
 *the following threads were recently operating on the object:
 *Thread[Thread-1,5,main] - java.lang.Exception: caller...
 *	at de.engehausen.kongcurrent.testhelper.Demo3Impl$$EnhancerByCGLIB$$cfa20a97.determineMax(<generated>)
 *	at de.engehausen.kongcurrent.cglib.ComplexTest$Executor.doit(ComplexTest.java:79)
 *	at de.engehausen.kongcurrent.cglib.ComplexTest$Executor.run(ComplexTest.java:68)
 *	at java.lang.Thread.run(Unknown Source)
 *Thread[Thread-0,5,main] - java.lang.Exception: caller...
 *	at de.engehausen.kongcurrent.testhelper.Demo3Impl$$EnhancerByCGLIB$$cfa20a97.determineMax(<generated>)
 *	at de.engehausen.kongcurrent.cglib.ComplexTest$Executor.doit(ComplexTest.java:79)
 *	at de.engehausen.kongcurrent.cglib.ComplexTest$Executor.doit(ComplexTest.java:77)
 *	at de.engehausen.kongcurrent.cglib.ComplexTest$Executor.doit(ComplexTest.java:77)
 *	at de.engehausen.kongcurrent.cglib.ComplexTest$Executor.run(ComplexTest.java:68)
 *	at java.lang.Thread.run(Unknown Source)</pre>
 * The output informs you that an <code>IllegalStateException</code>
 * occurred in thread "Thread-1,5,main" while working on <code>determineMax()</code>. It also shows
 * you the stack traces of "recent" calls to the object. In there you find another thread
 * which added worked on the monitored object (thread "Thread-0,5,main").
 * <p>All this can be adapted to your needs. You need to provide a {@link DescriptionCglib} for the
 * object to monitor, and likely information on how to construct an equivalent object using the
 * {@link ConstructorInformation}. In case the monitored object returns further objects that somehow
 * depend on the originally monitored object (think e.g. iterator of a list) then monitoring these
 * is possible as well by adding them as "dependant" objects to the description via the appropriate
 * method description. If the return value of the method returning a "dependant" you can also specify
 * a plain {@link Description}, in this case the dynamic proxying approach would kick in.
 */
public class MonitorCglib {
	
	private MonitorCglib() {
		// not to be instantiated
	}

	/**
	 * Creates a monitored version of the given target.
	 * @param target the instance to monitor, must not be <code>null</code>.
	 * @param description a description of the class or interface, must not be <code>null</code>.
	 * If the description is for an interface, then the proxying will be delegated to {@link Monitor}.
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
	 * The invocation handler (cglib method interceptor).
	 * @param <T> the type for the instance to be monitored
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

	}
		
}
