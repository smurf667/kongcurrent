package de.engehausen.kongcurrent.helper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import de.engehausen.kongcurrent.Comparator;

public class DefaultComparatorsTest {

	@Test
	public void testObjectComparator() {
		final Comparator<Object> comp = DefaultComparators.<Object>objectComparator();
		final Object one = new Object();
		final Object two = new Object();
		
		basicComparison(comp, one);
		assertFalse(comp.equals(one, two));
		assertFalse(comp.equals(two, one));
	}
	
	@Test
	public void testListComparator() {
		final Comparator<List<Integer>> comp = DefaultComparators.<Integer>listComparator();
		final List<Integer> list1 = Arrays.asList(Integer.valueOf(1), Integer.valueOf(2));
		final List<Integer> list2 = Arrays.asList(Integer.valueOf(1), Integer.valueOf(2));
		final List<Integer> list3 = Arrays.asList(Integer.valueOf(1), Integer.valueOf(2));
		final List<Integer> list4 = Arrays.asList(Integer.valueOf(1), null);
		basicComparison(comp, list1);
		assertTrue(comp.equals(list1, list2));
		assertTrue(comp.equals(list1, list3));
		assertTrue(comp.equals(list2, list3));
		assertTrue(comp.equals(list3, list2));
		assertFalse(comp.equals(list1, list4));
		assertFalse(comp.equals(list4, list1));
	}

	@Test
	public void testSetComparator() {
		final Comparator<Set<Integer>> comp = DefaultComparators.<Integer>setComparator();
		final Set<Integer> list1 = new HashSet<Integer>(Arrays.asList(Integer.valueOf(1), Integer.valueOf(2)));
		final Set<Integer> list2 = new HashSet<Integer>(Arrays.asList(Integer.valueOf(1), Integer.valueOf(2)));
		final Set<Integer> list3 = new HashSet<Integer>(Arrays.asList(Integer.valueOf(1), Integer.valueOf(2)));
		final Set<Integer> list4 = new HashSet<Integer>(Arrays.asList(Integer.valueOf(1), null));
		basicComparison(comp, list1);
		assertTrue(comp.equals(list1, list2));
		assertTrue(comp.equals(list1, list3));
		assertTrue(comp.equals(list2, list3));
		assertTrue(comp.equals(list3, list2));
		assertFalse(comp.equals(list1, list4));
		assertFalse(comp.equals(list4, list1));
	}
	
	@Test
	public void testMapComparator() {
		final Comparator<Map<Integer, Integer>> comp = DefaultComparators.<Integer, Integer>mapComparator();
		final Map<Integer, Integer> map1 = createIntMap(0, 2, 4);
		final Map<Integer, Integer> map2 = createIntMap(0, 2, 4);
		final Map<Integer, Integer> map3 = createIntMap(0, 2, 4);
		final Map<Integer, Integer> map4 = createIntMap(0, 2, 4, 6);
		basicComparison(comp, map1);
		assertTrue(comp.equals(map1, map2));
		assertTrue(comp.equals(map1, map3));
		assertTrue(comp.equals(map2, map3));
		assertTrue(comp.equals(map3, map2));
		assertFalse(comp.equals(map1, map4));
		assertFalse(comp.equals(map4, map1));
	}

	@SuppressWarnings("unchecked")
	protected void basicComparison(final Comparator comp, final Object one) {
		assertTrue(comp.equals(one, one));
		assertFalse(comp.equals(one, null));
		// testing behavior when the first argument is null
		// is not necessary, since this is not allow (see interface description)
	}
	
	private Map<Integer, Integer> createIntMap(final int... keys) {
		final Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (int key : keys) {
			result.put(Integer.valueOf(key), Integer.valueOf(key+1));
		}
		return result;
	}
	
}
