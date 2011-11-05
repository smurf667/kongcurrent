package de.engehausen.kongcurrent.helper;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import de.engehausen.kongcurrent.Description;
import de.engehausen.kongcurrent.Monitor;

/**
 * A collection of default description implementations for common
 * collection interfaces. Descriptions are used for monitoring objects
 * through the {@link Monitor}. They define which interfaces and return
 * types to monitor.
 * <br>The following descriptions are supported:
 * <ul>
 * <li>{@link #collectionDescription()} - a description for collections
 * <li>{@link #listDescription()} - a description for lists
 * <li>{@link #setDescription()} - a description for sets
 * <li>{@link #mapDescription()} - a description for maps
 * </ul>
 * Custom descriptions can be built by instantiating and setting up new
 * instances of {@link Description}.
 */
public class DefaultDescriptions {
	
	@SuppressWarnings("unchecked")
	private static Description collectionDescInst;
	@SuppressWarnings("unchecked")
	private static Description setDescInst;
	@SuppressWarnings("unchecked")
	private static Description listDescInst;
	@SuppressWarnings("unchecked")
	private static Description mapDescInst;
	
	protected DefaultDescriptions() {	
	}
	
	/**
	 * A description for collections. The description ensures that
	 * the {@link Iterator} that the collection may return is monitored
	 * as well.
	 * @param <T> the type of objects held in the collection
	 * @return a description instance, never <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public static <T> Description<Collection<T>> collectionDescription() {
		if (collectionDescInst != null) {
			return collectionDescInst;
		} else {
			return collectionDescInst = buildCollectionDescription();
		}
	}
	
	/**
	 * A description for sets. The description ensures that
	 * the {@link Iterator} that the set may return is monitored
	 * as well.
	 * @param <T> the type of objects held in the set
	 * @return a description instance, never <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public static <T> Description<Set<T>> setDescription() {
		if (setDescInst != null) {
			return setDescInst;
		} else {
			return setDescInst = buildSetDescription();
		}		
	}

	/**
	 * A description for lists. The description ensures that
	 * the {@link Iterator}, {@link ListIterator} or sub list the 
	 * list may return is monitored as well.
	 * @param <T> the type of objects held in the set
	 * @return a description instance, never <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public static <T> Description<List<T>> listDescription() {
		if (listDescInst != null) {
			return listDescInst;
		} else {
			return listDescInst = buildListDescription();
		}		
	}

	/**
	 * A description for maps. The description ensures that the
	 * key set, entry set or value collection the map may return is
	 * monitored as well.
	 * @param <K> the type of keys of the map
	 * @param <V> the type of values of the map
	 * @return a description instance, never <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Description<Map<K, V>> mapDescription() {
		if (mapDescInst != null) {
			return mapDescInst;
		} else {
			return mapDescInst = buildMapDescription();
		}		
	}

	private static <T> Description<Collection<T>> buildCollectionDescription() {
		final Description<Collection<T>> result = new Description<Collection<T>>(Collection.class);
		try {
			addMethod(result, Iterator.class, "iterator", (Class<?>[]) null);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
		return result;
	}

	private static <T> Description<Set<T>> buildSetDescription() {
		final Description<Set<T>> result = new Description<Set<T>>(Set.class, DefaultComparators.<T>setComparator());
		try {
			addMethod(result, Iterator.class, "iterator", (Class<?>[]) null);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
		return result;
	}

	private static <T> Description<List<T>> buildListDescription() {
		final Description<List<T>> result = new Description<List<T>>(List.class, DefaultComparators.<T>listComparator());
		try {
			addMethod(result, Iterator.class, "iterator", (Class<?>[]) null);
			addMethod(result, ListIterator.class, "listIterator", (Class<?>[]) null);
			addMethod(result, ListIterator.class, "listIterator", new Class<?>[] { Integer.TYPE });
			result.addDependant(result, "subList", new Class<?>[] { Integer.TYPE, Integer.TYPE });
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
		return result;
	}

	private static <K, V> Description<Map<K, V>> buildMapDescription() {
		final Description<Map<K, V>> result = new Description<Map<K, V>>(Map.class, DefaultComparators.<K, V>mapComparator());
		try {
			result.addDependant(setDescription(), "entrySet", (Class<?>[]) null);
			result.addDependant(setDescription(), "keySet", (Class<?>[]) null);
			result.addDependant(collectionDescription(), "values", (Class<?>[]) null);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
		return result;
	}

	private static <T> Description<T> addMethod(final Description<T> parent, final Class<?> clz, final String methodName, final Class<?>... parameterTypes) throws NoSuchMethodException {
		final Description<T> child = new Description<T>(clz);
		parent.addDependant(child, methodName, parameterTypes);
		return child;
	}

}
