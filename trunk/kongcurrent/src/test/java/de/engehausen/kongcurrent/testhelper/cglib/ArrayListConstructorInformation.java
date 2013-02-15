/**
 * 
 */
package de.engehausen.kongcurrent.testhelper.cglib;

import java.util.ArrayList;
import java.util.Collection;

import de.engehausen.kongcurrent.cglib.ConstructorInformation;

public class ArrayListConstructorInformation extends ConstructorInformation<ArrayList<String>> {		

	private static final Class<?>[] signature = new Class<?>[] { Collection.class };
	private final ArrayList<String> orig;

	public ArrayListConstructorInformation(final ArrayList<String> o) {
		super(signature);
		orig = o;
	}

	@Override
	public Object[] getArgValues() {
		return new Object[] { orig };
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ArrayListConstructorInformation) {
			final ArrayListConstructorInformation info = (ArrayListConstructorInformation) obj;
			return orig.equals(info.orig);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return orig.hashCode();
	}

}