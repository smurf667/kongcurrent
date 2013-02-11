package de.engehausen.kongcurrent;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import de.engehausen.kongcurrent.testhelper.Recorder;

public abstract class AbstractMonitorTest {

	protected Recorder logger;

	@Before
	public void setup() throws Exception {
		logger = new Recorder();
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
