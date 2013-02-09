/**
 * 
 */
package de.engehausen.kongcurrent.testhelper.cglib;

import de.engehausen.kongcurrent.cglib.ConstructorInformation;

public class SimpleJaneDoeConstructorInformation extends ConstructorInformation {		
	private static final Class<?>[] signature = new Class<?>[] { int.class };
	private final JaneDoe orig;
	public SimpleJaneDoeConstructorInformation(final JaneDoe o) {
		super(signature);
		orig = o;
	}
	@Override
	public Object[] getArgValues() {
		return new Object[] { Integer.valueOf(orig.getId()) };
	}
}