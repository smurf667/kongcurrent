package de.engehausen.kongcurrent.helper;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.engehausen.kongcurrent.Description;

public class DefaultDescriptionsTest {
	
	private static Set<Class<?>> PROXY_CANDIDATES;
	
	static {
		PROXY_CANDIDATES = new HashSet<Class<?>>();
		PROXY_CANDIDATES.add(Collection.class);
		PROXY_CANDIDATES.add(List.class);
		PROXY_CANDIDATES.add(Set.class);
		PROXY_CANDIDATES.add(Map.class);
		PROXY_CANDIDATES.add(Iterator.class);		
		PROXY_CANDIDATES.add(ListIterator.class);
	}
	
	protected Set<Description<?>> alreadyHandled;

	@Before
	public void setup() {
		alreadyHandled = new HashSet<Description<?>>();
	}

	@Test
	public void testCollection() {
		verifyInterfacesCovered(DefaultDescriptions.collectionDescription(), alreadyHandled);
	}

	@Test
	public void testList() {
		verifyInterfacesCovered(DefaultDescriptions.listDescription(), alreadyHandled);
	}

	@Test
	public void testSet() {
		verifyInterfacesCovered(DefaultDescriptions.setDescription(), alreadyHandled);
	}

	@Test
	public void testMap() {
		assertNotNull(DefaultDescriptions.mapDescription()); // cover a specific code path in the default descriptions
		verifyInterfacesCovered(DefaultDescriptions.mapDescription(), alreadyHandled);
	}

	protected void verifyInterfacesCovered(final Description<?> desc, final Set<Description<?>> handled) {
		if (handled.add(desc)) {
			final Class<?> iface = desc.getInterface();
			assertNotNull(iface);
			for (Method method : iface.getDeclaredMethods()) {
				if (PROXY_CANDIDATES.contains(method.getReturnType())) {
					final Description<?> child = desc.getDescription(method);
					assertNotNull("dependant not covered for "+method, child);
					verifyInterfacesCovered(child, handled);
				}
			}			
		}
	}

}
