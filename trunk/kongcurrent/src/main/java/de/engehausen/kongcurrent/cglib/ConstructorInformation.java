package de.engehausen.kongcurrent.cglib;

/**
 * Supplies information on how to create an object of a specific type.
 * This information is required by cglibs' enhancer to build a subclassed
 * version of a class. <code>ConstructorInformation</code> may be used in {@link DescriptionCglib}.
 * @param <T> the type for which this class provides constructor information.
 */
public abstract class ConstructorInformation<T> {
	
	private final Class<?>[] ctorTypes;

	/**
	 * Creates the constructor information with the given argument
	 * types, i.e. the object to describe has a constructor with the
	 * given argument types
	 * @param constructorArgTypes the argument types, must not be <code>null</code>.
	 */
	public ConstructorInformation(final Class<?>... constructorArgTypes) {
		ctorTypes = constructorArgTypes;
	}

	/**
	 * Returns the argument types.
	 * @return the argument types.
	 */
	public Class<?>[] getArgTypes() {
		return ctorTypes;
	}

	/**
	 * Returns concrete values for the constructor of the
	 * type being described by this constructor information.
	 * @return concrete values for constructor invocation;
	 * never <code>null</code>. Array must be of same length
	 * as the one exposed via {@link #getArgTypes()}.
	 */
	public abstract Object[] getArgValues();

}
