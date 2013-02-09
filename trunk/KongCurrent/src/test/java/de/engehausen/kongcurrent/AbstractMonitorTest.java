package de.engehausen.kongcurrent;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import de.engehausen.kongcurrent.testhelper.Recorder;

public abstract class AbstractMonitorTest extends TestCase {

	protected Recorder logger;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		logger = new Recorder();
	}

	@Override
	protected void tearDown() throws Exception {
		logger = null;
		super.tearDown();
	}

	/**
	 * Creates a modifiable list containing the given elements.
	 * @param <E> type of the elements in the list
	 * @param elements the elements, must not be <code>null</code>
	 * @return a newly built list with the given elements
	 */
	protected <E> List<E> buildList(E... elements) {
		final List<E> result = new ArrayList<E>(elements.length);
		for (E value : elements) {
			result.add(value);
		}
		return result;
	}

}
