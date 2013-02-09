/**
 * 
 */
package de.engehausen.kongcurrent.testhelper.cglib;

import java.util.Arrays;
import java.util.Iterator;

public class JaneDoe {
	
	private final int id;
	private final JohnDoe partner;
	
	public JaneDoe(int x) {
		this(x, null);
	}
	
	public JaneDoe(final int x, final JohnDoe p) {
		super();
		id = x;
		partner = p;
	}
	
	public JohnDoe getPartner() {
		return partner;
	}
	
	public Iterator<Integer> iterator() {
		final Integer[] data = new Integer[id];
		for (int i = id; i-->0; ) {
			data[i] = Integer.valueOf(i);
		}
		return Arrays.asList(data).iterator();
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "berta"+id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof JaneDoe) {
			return id == ((JaneDoe) obj).id;
		} else {
			return false;
		}
	}
}