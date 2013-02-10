package de.engehausen.kongcurrent;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.engehausen.kongcurrent.helper.DefaultComparators;

/**
 * Description of an interface to monitor. The description
 * also holds a {@link Comparator} that allows to perform the
 * comparison of two instances implementing this interface.
 * Additionally the description provides further descriptions
 * for methods returning objects that somehow depend on the
 * original instance.
 * 
 * @param <T> the type of interface the description describes
 */
public class Description<T> {

	@SuppressWarnings("unchecked")
	protected static final Map<Method, Description> EMPTY = Collections.emptyMap();
	
	protected final Class<?> proxyInterface;
	protected final Comparator<T> comparator;
	@SuppressWarnings("unchecked")
	protected Map<Method, Description> dependants;

	/**
	 * Creates a description for the given interface. The description
	 * will expose the given comparator.
	 * @param anInterface the interface class, must not be <code>null</code>
	 * @param aComparator a comparator implementation <i>consistent with <code>equals</code></i>; may be <code>null</code>. In this case normal object equality rules are applied.
	 */
	public Description(final Class<?> anInterface, final Comparator<T> aComparator) {
		proxyInterface = anInterface;
		comparator = aComparator==null?DefaultComparators.<T>objectComparator():aComparator;
		dependants = EMPTY;
	}
	
	/**
	 * Creates a description for the given interface. The description
	 * will expose the default object comparator (see <a href="http://download.oracle.com/javase/6/docs/api/java/lang/Object.html">Object</a>).
	 * @param anInterface the interface class, must not be <code>null</code>
	 */
	public Description(final Class<?> anInterface) {
		this(anInterface, null);
	}

	/**
	 * Returns the class object for the interface this description
	 * stands for.
	 * @return the class object for an interface
	 */
	public Class<?> getInterface() {
		return proxyInterface;
	}

	/**
	 * Returns the comparator for instances implementing the type
	 * this description stands for. The comparator is used on instances
	 * that are monitored through the {@link Monitor}.
	 * @return a comparator, never <code>null</code>.
	 */
	public Comparator<T> getComparator() {
		return comparator;
	}

	/**
	 * Returns a description for the given method.
	 * @param method a method of the type this description stands for, must not be <code>null</code>
	 * @param <E> the type the description stands for
	 * @return a description how to proxy the result, or <code>null</code> if no proxying is required.
	 */
	@SuppressWarnings("unchecked")
	public <E> Description<E> getDescription(final Method method) {
		return dependants.get(method);
	}

	/**
	 * Adds a method as a "dependant" to the description. This means
	 * that if this method is invoked, the result object should be
	 * monitored as well, because it is depending on the parent object
	 * in some way. <i>Once the description object is used through {@link Monitor},
	 * directly or indirectly, this method must not be used any more.</i>
	 * @param description the description applicable for the return type of the method, must not be <code>null</code>.
	 * @param methodName the name of the method, must not be <code>null</code>.
	 * @param parameterTypes the types of arguments to the method, may be <code>null</code>.
	 * @param <E> the type the description stands for
	 * @throws SecurityException if a security manager denies access
	 * @throws NoSuchMethodException if a matching method is not found
	 */
	public <E> void addDependant(final Description<E> description, final String methodName, final Class<?>... parameterTypes) throws SecurityException, NoSuchMethodException {
		addDependant(proxyInterface.getDeclaredMethod(methodName, parameterTypes), description);
	}
	
	@SuppressWarnings("unchecked")
	protected <E> void addDependant(final Method method, final Description<E> description) {
		if (dependants == EMPTY) {
			dependants = new HashMap<Method, Description>();
		}
		dependants.put(method, description);
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
			if (obj instanceof Description<?>) {
				final Description<?> other = (Description<?>) obj;
				return proxyInterface.equals(other.proxyInterface) &&
				       comparator.equals(other.comparator) &&
				       dependants.equals(other.dependants);
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
