/**
 * 
 */
package de.engehausen.kongcurrent.testhelper.cglib;

import de.engehausen.kongcurrent.cglib.ConstructorInformation;

public class JaneDoeConstructorInformation extends ConstructorInformation {		
	private static final Class<?>[] signature = new Class<?>[] { int.class, JohnDoe.class };
	private final JaneDoe orig;
	public JaneDoeConstructorInformation(final JaneDoe o) {
		super(signature);
		orig = o;
	}
	@Override
	public Object[] getArgValues() {
		return new Object[] { Integer.valueOf(orig.getId()), orig.getPartner() };
	}
}