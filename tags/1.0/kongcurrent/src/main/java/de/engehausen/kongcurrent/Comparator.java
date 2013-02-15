package de.engehausen.kongcurrent;

/**
 * Comparator interface for objects. An externalized comparison
 * may be required for handling special semantic requirements that
 * need to be fulfilled also by the monitored (proxied) object.
 * 
 * @param <T> the type of objects to compare
 */
public interface Comparator<T> {

	/**
	 * Compares two objects.
	 * <b>It is not legal to invoke <code>one.equals(two)</code> in any implementation</code>.</b>
	 * @param one the first object, must not be <code>null</code>
	 * @param two the second object, may be <code>null</code>
	 * @return <code>true</code> if the objects are equal, <code>false</code> otherwise.
	 * @see <a href="http://download.oracle.com/javase/6/docs/api/java/lang/Object.html#equals%28java.lang.Object%29">object equality in the JDK</a>
	 */
	boolean equals(T one, T two);

}
