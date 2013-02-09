/**
 * 
 */
package de.engehausen.kongcurrent.testhelper.cglib;

import java.util.ArrayList;
import java.util.Collection;

import de.engehausen.kongcurrent.cglib.ConstructorInformation;

public class ArrayListConstructorInformation extends ConstructorInformation {		

	private static final Class<?>[] signature = new Class<?>[] { Collection.class };
	private final ArrayList<?> orig;

	public ArrayListConstructorInformation(final ArrayList<?> o) {
		super(signature);
		orig = o;
	}

	@Override
	public Object[] getArgValues() {
		return new Object[] { orig };
	}

}