package de.engehausen.kongcurrent.helper;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import de.engehausen.kongcurrent.Description;

public class DefaultDescriptionsTest extends TestCase {
	
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

	@Override
	protected void setUp() throws Exception {
		alreadyHandled = new HashSet<Description<?>>();
	}

	@Override
	protected void tearDown() throws Exception {
		alreadyHandled = null;
	}

	public void testCollection() throws Exception {
		verifyInterfacesCovered(DefaultDescriptions.collectionDescription(), alreadyHandled);
	}

	public void testList() throws Exception {
		verifyInterfacesCovered(DefaultDescriptions.listDescription(), alreadyHandled);
	}

	public void testSet() throws Exception {
		verifyInterfacesCovered(DefaultDescriptions.setDescription(), alreadyHandled);
	}

	public void testMap() throws Exception {
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
