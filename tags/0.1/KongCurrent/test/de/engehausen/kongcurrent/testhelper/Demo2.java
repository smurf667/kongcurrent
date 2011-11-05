package de.engehausen.kongcurrent.testhelper;

/**
 * Demo interface. Exposes a constant value on its sole method;
 * two instances of this interface are equal when the same value
 * is exposed.
 */
public interface Demo2 {

	int getInt();

	/**
	 * Corresponds to the value returned by {@link #getInt()}.
	 * @return the value returned by {@link #getInt()}.
	 */
	int hashCode();
	
	/**
	 * Equal to any other <code>Demo2</code> implementation if
	 * they both expose the same value on {@link #getInt()}.
	 * @param o object to compare against
	 * @return <code>true</code> if <code>o</code> is a <code>Demo2</code>
	 * implementation returning the same integer.
	 */
	boolean equals(Object o);
	
}
