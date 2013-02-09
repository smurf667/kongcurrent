package de.engehausen.kongcurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.engehausen.kongcurrent.helper.DefaultDescriptions;
import de.engehausen.kongcurrent.helper.DefaultExceptionHandler;

public class SimpleTest extends AbstractMonitorTest {
	
	/**
	 * Test proxying a collection.
	 * @throws Exception in case of error
	 */
	public void testProxying1() throws Exception {
		final Collection<String> collection = new ArrayList<String>(1);
		final String helloWorld = "hello world";
		collection.add(helloWorld);
		final Collection<String> monitored = Monitor.monitor(collection, new Description<Collection<String>>(Collection.class), new DefaultExceptionHandler(logger));
		assertNotNull(monitored);
		assertTrue(monitored instanceof Collection<?>);
		assertTrue(Monitor.monitor(new ArrayList<String>(), new Description<List<String>>(List.class), new DefaultExceptionHandler(logger)) instanceof Collection<?>);
		assertEquals(collection.hashCode(), monitored.hashCode());
		assertSame(collection.iterator().next(), monitored.iterator().next());
		assertEquals(collection.isEmpty(), monitored.isEmpty());
		assertEquals(collection.contains(helloWorld), collection.contains(helloWorld));
		final String nonExistent = "ne";
		assertEquals(collection.contains(nonExistent), collection.contains(nonExistent));
		assertFalse(monitored.toString().equals(collection.toString()));
		assertTrue(monitored.toString().contains(collection.toString()));
		monitored.clear();
		assertEquals(collection.size(), monitored.size());
		assertEquals(collection.isEmpty(), monitored.isEmpty());
		monitored.add(helloWorld);
		assertEquals(1, collection.size());
		assertEquals(collection.size(), monitored.size());		
	}
	
	public void testProxying2() throws Exception {
		final Description<List<String>> desc = DefaultDescriptions.<String>listDescription();

		final List<String> list = buildList("one", "two", null, "three");
		final List<String> monitored = Monitor.monitor(list, desc, new DefaultExceptionHandler(logger));
		assertNotNull(monitored);
		assertTrue(monitored instanceof Collection<?>);
		assertTrue(monitored instanceof List<?>);
		assertEquals(list, monitored);
		assertEquals(monitored, monitored);
		assertEquals(monitored, list);
		assertEquals(4, monitored.size());
		assertEquals(4, list.size());
		final Iterator<String> iterator = monitored.iterator();
		assertNotNull(iterator);
		assertTrue(iterator.getClass().getName().contains("Proxy"));
		assertTrue(iterator.hasNext());
		assertEquals("one", iterator.next());
		iterator.remove();
		assertFalse(monitored.contains("one"));
		assertFalse(list.contains("one"));
		assertTrue(monitored.contains("two"));
		assertTrue(list.contains("two"));
		assertEquals(3, monitored.size());
		assertEquals(3, list.size());

		// some exception tests, suppress logging
		final List<String> readOnly = Monitor.monitor(Collections.singletonList("one"), desc, new DefaultExceptionHandler(new Logger() {			
			@Override
			public void log(final String message) {
				// ignore
			}
		}));
		try {
			readOnly.set(0, null);
			fail("operation unexpectedly succeeded");
		} catch (UnsupportedOperationException e) {
			assertTrue(true);
		}
		try {
			readOnly.iterator().remove();
			fail("operation unexpectedly succeeded");
		} catch (IllegalStateException e) {
			assertTrue(true);
		}
		try {
			final Iterator<String> i = readOnly.iterator();
			i.next();
			i.remove();
			fail("operation unexpectedly succeeded");
		} catch (UnsupportedOperationException e) {
			assertTrue(true);
		}
	}
	
	public void testProxying3() throws Exception {
		final Description<Set<Integer>> desc = DefaultDescriptions.<Integer>setDescription();
		final Set<Integer> original = new HashSet<Integer>();
		original.add(Integer.valueOf(1));
		original.add(Integer.valueOf(2));
		final Set<Integer> monitored = Monitor.monitor(original, desc, new DefaultExceptionHandler(logger));
		try {
			// cause a concurrent modification exception
			for (Integer integer : monitored) {
				monitored.remove(integer);
			}
			fail("operation unexpectedly succeeded");
		} catch (ConcurrentModificationException e) {
			assertTrue(logger.toString().indexOf("java.lang.Exception: caller...") > 100);
		}
	}
	
	/**
	 * Tests performance with no contention taking place.
	 * @throws Exception in case of error
	 */
	public void testPerformance() throws Exception {
		final Description<List<String>> desc = DefaultDescriptions.<String>listDescription();

		final List<String> list = buildList("one", "two", null, "three");
		final List<String> monitored = Monitor.monitor(list, desc, new DefaultExceptionHandler(logger));

		final int max = 50000;
		final double noproxy = time(list, max);
		final double proxy = time(monitored, max);
		// proxy must not be 200x slower than original
		assertTrue(proxy/noproxy < 200);
	}
	
	protected double time(final List<String> list, final long max) {
		final long begin = System.nanoTime();
		for (int i = 0; i < max; i++) {
			assertEquals(4, list.size());
			String temp = null;
			for (Iterator<String> iterator = list.iterator(); iterator.hasNext(); ) {
				temp = iterator.next();
			}
			assertEquals("three", temp);
		}
		return (System.nanoTime() - begin)/1000000d;
	}

}
