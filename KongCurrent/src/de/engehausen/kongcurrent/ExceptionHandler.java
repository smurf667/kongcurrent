package de.engehausen.kongcurrent;

import java.lang.reflect.Method;

/**
 * Handles exceptions occurring on monitored methods.
 * The handler is invoked by the {@link Monitor} before
 * and after invoking a monitored method.
 */
public interface ExceptionHandler {

	/**
	 * Invoked before executing the monitored method.
	 * @param target the object on which the method will be invoked.
	 * @param method the method that will be invoked, never <code>null</code>
	 * @param args the arguments that the method will be invoked with, may be <code>null</code>
	 */
	void preInvoke(Object target, Method method, Object[] args);

	/**
	 * Invoked in case an exception is thrown.
	 * @param throwable the exception that was thrown, never <code>null</code>.
	 * @param target the object on which the method was invoked.
	 * @param method the method that was invoked, never <code>null</code>
	 * @param args the arguments that the method was invoked with, may be <code>null</code>
	 */
	void handle(Throwable throwable, Object target, Method method, Object[] args);
	
	/**
	 * Invoked after the monitored method was invoked.
	 * @param target the object on which the method was invoked.
	 * @param method the method that was invoked, never <code>null</code>
	 * @param args the arguments that the method was invoked with, may be <code>null</code>
	 */
	void postInvoke(Object target, Method method, Object[] args);

}
