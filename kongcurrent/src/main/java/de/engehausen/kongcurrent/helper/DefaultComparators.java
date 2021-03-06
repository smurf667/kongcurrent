package de.engehausen.kongcurrent.helper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.engehausen.kongcurrent.Comparator;

/**
 * A collection of default comparator implementations. The following
 * comparators are supported:
 * <ul>
 * <li>{@link #objectComparator()} - a comparator that follows the rules
 * defined for <a href="http://download.oracle.com/javase/6/docs/api/java/lang/Object.html#equals%28java.lang.Object%29">Objects</a>
 * <li>{@link #listComparator()} - a comparator that follows the rules
 * defined for <a href="http://download.oracle.com/javase/6/docs/api/java/util/List.html#equals%28java.lang.Object%29">Lists</a>
 * <li>{@link #setComparator()} - a comparator that follows the rules 
 * defined for <a href="http://download.oracle.com/javase/6/docs/api/java/util/Set.html#equals%28java.lang.Object%29">Sets</a>
 * <li>{@link #mapComparator()} - a comparator that follows the rules
 * defined for <a href="http://download.oracle.com/javase/6/docs/api/java/util/Map.html#equals%28java.lang.Object%29">Maps</a>
 * </ul>
 * Custom comparators can be used by implementing {@link Comparator}.
 */
public final class DefaultComparators {

	private static final String UNCHECKED = "unchecked";

	@SuppressWarnings(UNCHECKED)
	private static final Comparator objectComparatorInst = new ObjectComparator();
	@SuppressWarnings(UNCHECKED)
	private static final Comparator listComparatorInst = new ListComparator();
	@SuppressWarnings(UNCHECKED)
	private static final Comparator setComparatorInst = new SetComparator();
	@SuppressWarnings(UNCHECKED)
	private static final Comparator mapComparatorInst = new MapComparator();

	private DefaultComparators() {
		// not to be instantiated
	}
	
	/**
	 * Returns a comparator instance which uses the rules defined
	 * for {@link Object#equals(Object)}.
	 * @param <E> the type the comparator will use
	 * @return a comparator instance, never <code>null</code>
	 */
	@SuppressWarnings(UNCHECKED)
	public static <E> Comparator<E> objectComparator() {
		return (Comparator<E>) objectComparatorInst;
	}

	/**
	 * Returns a comparator instance which uses the rules defined
	 * for {@link List#equals(Object)}.
	 * @param <E> the type the comparator will use
	 * @return a comparator instance, never <code>null</code>
	 */
	@SuppressWarnings(UNCHECKED)
	public static <E> Comparator<List<E>> listComparator() {
		return (Comparator<List<E>>) listComparatorInst;
	}

	/**
	 * Returns a comparator instance which uses the rules defined
	 * for {@link Set#equals(Object)}.
	 * @param <E> the type the comparator will use
	 * @return a comparator instance, never <code>null</code>
	 */
	@SuppressWarnings(UNCHECKED)
	public static <E> Comparator<Set<E>> setComparator() {
		return (Comparator<Set<E>>) setComparatorInst;
	}

	/**
	 * Returns a comparator instance which uses the rules defined
	 * for {@link Map#equals(Object)}.
	 * @param <K> the type the comparator will use for map keys
	 * @param <V> the type the comparator will use for map values
	 * @return a comparator instance, never <code>null</code>
	 */
	@SuppressWarnings(UNCHECKED)
	public static <K, V> Comparator<Map<K, V>> mapComparator() {
		return (Comparator<Map<K, V>>) mapComparatorInst;
	}

	private static class ObjectComparator implements Comparator<Object> {
		@Override
		public boolean equals(final Object one, final Object two) {
			return one==two; //NOPMD this must be the comparison implementation
		}
	}
	
	private static class ListComparator<E> implements Comparator<List<E>> {
		@Override
		public boolean equals(final List<E> list1, final List<E> list2) {
			if (list2 == null) {
				return false;
			} else {
				final int size = list1.size();
				if (size == list2.size()) {
					for (int i = size; i-->0; ) {
						final E item = list1.get(i);
						if (item == null) {
							if (list2.get(i) != null) {
								return false;
							}
						} else if (!item.equals(list2.get(i))) {
							return false;
						}
					}
					return true;
				} else {
					return false;
				}
			}
		}
	}

	private static class SetComparator<E> implements Comparator<Set<E>> {
		@Override
		public boolean equals(final Set<E> set1, final Set<E> set2) {
			if (set2 == null) {
				return false;
			} else {
				if (set1.size() == set2.size()) {
					return set1.containsAll(set2);
				} else {
					return false;
				}
			}
		}		
	}
	
	private static class MapComparator<K, V> implements Comparator<Map<K, V>> {
		@Override
		public boolean equals(final Map<K, V> map1, final Map<K, V> map2) {
			if (map2 == null) {
				return false;
			} else {
				if (map1.size() == map2.size()) {
					return map1.entrySet().equals(map2.entrySet());
				} else {
					return false;
				}
			}
		}		
	}

}
