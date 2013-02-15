package de.engehausen.kongcurrent.cglib;

import de.engehausen.kongcurrent.Description;
import de.engehausen.kongcurrent.Monitor;

/**
 * Description of a <i>class<i> to monitor. This extends the
 * {@link Description} used for <i>interfaces</i> and can be
 * used with classes that will be proxied (actually subclassed) using cglib.
 * Additionally the description provides further descriptions
 * for methods returning objects that somehow depend on the
 * original instance.
 * 
 * @param <T> the type of class the description describes
 */
public class DescriptionCglib<T> extends Description<T> {

	protected final ConstructorInformation<T> ctorInfo;

	/**
	 * Creates a description for the given class. The class is expected
	 * to have a no-arg default constructor.
	 * @param aClass the class to describe, must not be <code>null</code>
	 */
	public DescriptionCglib(final Class<?> aClass) {
		this(aClass, null);
	}

	/**
	 * Creates a description for the given class. 
	 * @param aClass the class to describe, must not be <code>null</code>
	 * @param aConstructorInfo the constructor information to create proxies; may only be <code>null</code> if a no-arg constructor exists for <code>aClass</code>
	 */
	public DescriptionCglib(final Class<?> aClass, final ConstructorInformation<T> aConstructorInfo) {
		super(aClass, null);
		ctorInfo = aConstructorInfo;
	}

	/**
	 * Adds a method as a "dependant" to the description. This means
	 * that if this method is invoked, the result object should be
	 * monitored as well, because it is depending on the parent object
	 * in some way. <i>Once the description object is used through {@link Monitor},
	 * directly or indirectly, this method must not be used any more.</i>
	 * @param description the cglib-based description applicable for the return type of the method, must not be <code>null</code>.
	 * @param methodName the name of the method, must not be <code>null</code>.
	 * @param parameterTypes the types of arguments to the method, may be <code>null</code>.
	 * @param <E> the type the description stands for
	 * @throws SecurityException if a security manager denies access
	 * @throws NoSuchMethodException if a matching method is not found
	 */
	public <E> void addDependant(final DescriptionCglib<E> description, final String methodName, final Class<?>... parameterTypes) throws SecurityException, NoSuchMethodException {
		addDependant(getInterface().getDeclaredMethod(methodName, parameterTypes), description);
	}

	/**
	 * Returns information on the constructor of an object to proxy.
	 * @return information on the constructor of an object to proxy.
	 */
	public ConstructorInformation<T> getConstructorInformation() {
		return ctorInfo;
	}

	/**
	 * Compares the given object to this description.
	 * @return <code>true</code> if the given object is also a
	 * description for the same interface, with the same comparator
	 * and dependants; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null) {
			return false;
		} else {
			if (obj instanceof DescriptionCglib<?>) {
				final DescriptionCglib<?> other = (DescriptionCglib<?>) obj;
				if (ctorInfo == null) {
					return other.ctorInfo == null && proxyInterface.equals(other.proxyInterface) &&
							comparator.equals(other.comparator) &&
							dependants.equals(other.dependants);
				} else {
					return proxyInterface.equals(other.proxyInterface) &&
					comparator.equals(other.comparator) &&
					dependants.equals(other.dependants) &&
					ctorInfo.equals(other.ctorInfo);
				}
			} else {
				return false;				
			}
		}
	}

	/**
	 * Returns the hash code of the description.
	 * @return the hash code of the description.
	 */
	@Override
	public int hashCode() {
		return proxyInterface.hashCode();
	}

}
