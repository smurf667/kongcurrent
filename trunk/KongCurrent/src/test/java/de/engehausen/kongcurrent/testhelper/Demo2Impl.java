package de.engehausen.kongcurrent.testhelper;

import de.engehausen.kongcurrent.Comparator;

public class Demo2Impl implements Demo2 {

	public static final Comparator<Demo2> COMPARATOR = new Comparator<Demo2>() {
		@Override
		public boolean equals(final Demo2 one, final Demo2 two) {
			if (two == null) {
				return false;
			} else {
				// one cannot be null by interface contract
				return one.getInt() == two.getInt();
			}
		}
	};
	
	protected final int number;
	
	public Demo2Impl(final int n) {
		number = n;
	}

	@Override
	public int getInt() {
		return number;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof Demo2) {
			return number == ((Demo2) obj).getInt();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getInt();
	}

}
