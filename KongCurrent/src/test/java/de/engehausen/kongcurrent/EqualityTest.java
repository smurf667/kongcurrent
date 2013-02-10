package de.engehausen.kongcurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.engehausen.kongcurrent.helper.DefaultComparators;
import de.engehausen.kongcurrent.helper.DefaultExceptionHandler;
import de.engehausen.kongcurrent.testhelper.Demo;
import de.engehausen.kongcurrent.testhelper.Demo2;
import de.engehausen.kongcurrent.testhelper.Demo2Impl;
import de.engehausen.kongcurrent.testhelper.DemoImpl;

public class EqualityTest extends AbstractMonitorTest {
	
	private static final String HELLO = "hello";
	private static final String WORLD = "world";

	/**
	 * Tests equality rules on a basic demo interface.
	 * @throws Exception in case of error
	 */
	public void testObjectEquality1() throws Exception {
		final Demo o1 = new DemoImpl(4711);
		final Demo p1 = Monitor.monitor(o1, new Description<Demo>(Demo.class), new DefaultExceptionHandler(logger));

		// different instances (also proxy - NOT equal to original)
		assertFalse(new DemoImpl(4711).equals(o1));
		assertFalse(o1.equals(p1));
		assertFalse(p1.equals(o1));

		// reflexive
		assertEquals(o1, o1);
		assertEquals(p1, p1);
		
		// consistent (repeat, same result)
		assertEquals(o1, o1);
		assertEquals(p1, p1);

		// null value
		assertFalse(o1.equals(null));
		assertFalse(p1.equals(null));		
	}


	/**
	 * Test of equality rules with objects that have special semantics
	 * on equals (int value identical means object is equal).
	 * @throws Exception in case of error
	 */
	public void testObjectEquality2() throws Exception {
		final Description<Demo2> desc = new Description<Demo2>(Demo2.class, Demo2Impl.COMPARATOR);
		final Demo2 o1 = new Demo2Impl(4711);
		final Demo2 o2 = new Demo2Impl(4711);
		final Demo2 o3 = new Demo2Impl(4711);
		final Demo2 o4 = new Demo2Impl(0x815);
		final Demo2 p1 = Monitor.monitor(o1, desc, new DefaultExceptionHandler(logger));
		final Demo2 p2 = Monitor.monitor(o2, desc, new DefaultExceptionHandler(logger));
		final Demo2 p3 = Monitor.monitor(o3, desc, new DefaultExceptionHandler(logger));
		final Demo2 p4 = Monitor.monitor(o4, desc, new DefaultExceptionHandler(logger));
		
		fullEqualityRules(o1, o2, o3, o4, p1, p2, p3, p4);
		
		assertTrue(p1.hashCode()!= p4.hashCode());
	}
	
	/**
	 * Test of equality rules of {@link List} objects.
	 * @throws Exception in case of error
	 */
	public void testObjectEquality3() throws Exception {
		final Description<List<String>> desc = new Description<List<String>>(List.class, DefaultComparators.<String>listComparator());
		final List<String> o1 = buildList(HELLO, WORLD);
		final List<String> o2 = buildList(HELLO, WORLD);
		final List<String> o3 = new LinkedList<String>(); // use a different list implementation
		buildList(o3, HELLO, WORLD);
		final List<String> o4 = buildList(HELLO);
		final List<String> p1 = Monitor.monitor(o1, desc, new DefaultExceptionHandler(logger));
		final List<String> p2 = Monitor.monitor(o2, desc, new DefaultExceptionHandler(logger));
		final List<String> p3 = Monitor.monitor(o3, desc, new DefaultExceptionHandler(logger));
		final List<String> p4 = Monitor.monitor(o4, desc, new DefaultExceptionHandler(logger));
		fullEqualityRules(o1, o2, o3, o4, p1, p2, p3, p4);
	}

	/**
	 * Test of equality rules of {@link Set} objects.
	 * @throws Exception in case of error
	 */
	public void testObjectEquality4() throws Exception {
		final Description<Set<String>> desc = new Description<Set<String>>(Set.class, DefaultComparators.<String>setComparator());
		final Set<String> o1 = buildSet(HELLO, WORLD);
		final Set<String> o2 = buildSet(HELLO, WORLD);
		final Set<String> o3 = new TreeSet<String>(); // use a different set implementation
		buildSet(o3, HELLO, WORLD);
		final Set<String> o4 = Collections.singleton(HELLO);
		final Set<String> p1 = Monitor.monitor(o1, desc, new DefaultExceptionHandler(logger));
		final Set<String> p2 = Monitor.monitor(o2, desc, new DefaultExceptionHandler(logger));
		final Set<String> p3 = Monitor.monitor(o3, desc, new DefaultExceptionHandler(logger));
		final Set<String> p4 = Monitor.monitor(o4, desc, new DefaultExceptionHandler(logger));
		fullEqualityRules(o1, o2, o3, o4, p1, p2, p3, p4);
	}

	/**
	 * Create a list object filled with the given values.
	 * @param values the values to fill in, must not be <code>null</code>
	 * @return a list containing the values
	 */
	protected List<String> buildList(final String... values) {
		final List<String> result = new ArrayList<String>(values.length);
		for (String value : values) {
			result.add(value);
		}
		return result;
	}

	/**
	 * Adds the given values to the list.
	 * @param list the list to add to, must not be <code>null</code>.
	 * @param values the values to add, must not be <code>null</code>.
	 */
	protected void buildList(final List<String> list, final String... values) {
		for (String value : values) {
			list.add(value);
		}
	}

	/**
	 * Create a set object filled with the given values.
	 * @param values the values to fill in, must not be <code>null</code>
	 * @return a list containing the values
	 */
	protected Set<String> buildSet(final String... values) {
		final Set<String> result = new HashSet<String>();
		for (String value : values) {
			result.add(value);
		}
		return result;
	}

	/**
	 * Adds the given values to the set.
	 * @param set the set to add to, must not be <code>null</code>.
	 * @param values the values to add, must not be <code>null</code>.
	 */
	protected void buildSet(final Set<String> set, final String... values) {
		for (String value : values) {
			set.add(value);
		}
	}

	/**
	 * Test behavior of equality for objects.
	 * @param o1 object instance, not <code>null</code>
	 * @param o2 object instance, not <code>null</code>, different from <code>o1</code>, but equal
	 * @param o3 object instance, not <code>null</code>, different from <code>o2</code>, but equal
	 * @param o4 object instance, not <code>null</code>, different from <code>o1</code>, and not equal
	 * @param p1 proxied version of <code>o1</code>
	 * @param p2 proxied version of <code>o2</code>
	 * @param p3 proxied version of <code>o3</code>
	 * @param p4 proxied version of <code>o4</code>
	 * @throws Exception in case of error
	 */
	protected void fullEqualityRules(final Object o1, final Object o2, final Object o3, final Object o4,
			                         final Object p1, final Object p2, final Object p3, final Object p4) throws Exception {
		// (in-)equality
		assertFalse(o1.equals(o4));
		assertFalse(o2.equals(o4));
		assertFalse(o3.equals(o4));
		assertFalse(p1.equals(o4));
		assertFalse(p2.equals(o4));
		assertFalse(p3.equals(o4));

		// different classes
		assertFalse(o1.equals(Integer.valueOf(0)));
		assertFalse(p1.equals(Integer.valueOf(0)));

		// reflexive
		assertEquals(o1, o1);
		assertEquals(p1, p1);
		assertEquals(o4, o4);
		assertEquals(p4, p4);
		
		// symmetric
		assertEquals(o1, o2);
		assertEquals(o2, o1);
		assertEquals(p1, p2);
		assertEquals(p2, p1);
		assertEquals(p1, o1);
		assertEquals(o1, p1);
		
		// transitive
		assertEquals(o1, o2);
		assertEquals(o2, o3);
		assertEquals(o1, o3);
		assertEquals(p1, p2);
		assertEquals(p2, p3);
		assertEquals(p1, p3);
		
		// consistent (repeat, same result)
		assertEquals(o1, o1);
		assertEquals(p1, p1);

		// null value
		assertFalse(o1.equals(null));
		assertFalse(p1.equals(null));		
	}
}