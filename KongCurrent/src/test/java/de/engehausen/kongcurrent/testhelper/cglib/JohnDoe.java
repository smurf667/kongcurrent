package de.engehausen.kongcurrent.testhelper.cglib;

public class JohnDoe {
	
	private String info;
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public boolean fail(final boolean withException) {
		if (withException) {
			throw new IllegalStateException();
		} else {
			return withException;
		}
	}

}
