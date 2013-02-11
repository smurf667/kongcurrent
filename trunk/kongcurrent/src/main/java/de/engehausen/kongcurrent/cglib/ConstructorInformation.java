package de.engehausen.kongcurrent.cglib;

public abstract class ConstructorInformation {
	
	private final Class<?>[] ctorTypes;
	
	public ConstructorInformation(final Class<?>[] constructorArgTypes) {
		ctorTypes = constructorArgTypes;
	}
	
	public Class<?>[] getArgTypes() {
		return ctorTypes;
	}
	
	public abstract Object[] getArgValues();

}
