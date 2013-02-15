package de.engehausen.kongcurrent.cglib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

import de.engehausen.kongcurrent.AbstractMonitorTest;
import de.engehausen.kongcurrent.helper.DefaultExceptionHandler;
import de.engehausen.kongcurrent.testhelper.cglib.ArrayListConstructorInformation;
import de.engehausen.kongcurrent.testhelper.cglib.JaneDoe;
import de.engehausen.kongcurrent.testhelper.cglib.JaneDoeConstructorInformation;
import de.engehausen.kongcurrent.testhelper.cglib.JohnDoe;
import de.engehausen.kongcurrent.testhelper.cglib.SimpleJaneDoeConstructorInformation;

public class SimpleTest extends AbstractMonitorTest {

	@Test
	public void testCustomClassNonDefaultCtor() {
		final JaneDoe original = new JaneDoe(4);
		final JaneDoe monitored = MonitorCglib.monitor(original, new DescriptionCglib<JaneDoe>(JaneDoe.class, new SimpleJaneDoeConstructorInformation(original)), new DefaultExceptionHandler(logger));
		assertEquals(original.hashCode(), monitored.hashCode());
		assertEquals(original.toString(), monitored.toString());
		assertEquals(original.getId(), monitored.getId());
	}
	
	@Test
	public void testStandardClassDefaultCtor() {
		final String entry = "entry";
		final ArrayList<String> original = new ArrayList<String>(1);
		original.add(entry);
		final ArrayList<String> monitored = MonitorCglib.monitor(original, new DescriptionCglib<ArrayList<String>>(ArrayList.class, new ArrayListConstructorInformation(original)), new DefaultExceptionHandler(logger));
		assertEquals(original.hashCode(), monitored.hashCode());
		assertEquals(original.toString(), monitored.toString());
		assertEquals(original.get(0), monitored.get(0));
		assertEquals(original, monitored);
		assertEquals(monitored, original);
		assertEquals(entry, monitored.remove(0));
		assertFalse(original.equals(monitored));
		assertFalse(monitored.equals(original));
		assertEquals(entry, original.remove(0));
		assertEquals(original, monitored);
		assertTrue(monitored.isEmpty());
		assertTrue(original.isEmpty());
	}
	
	@Test
	public void testDependant() throws NoSuchMethodException {
		final JohnDoe john = new JohnDoe();
		final JaneDoe original = new JaneDoe(4, john);
		final DescriptionCglib<JaneDoe> desc1 = new DescriptionCglib<JaneDoe>(JaneDoe.class, new JaneDoeConstructorInformation(original));
		final DescriptionCglib<JohnDoe> desc2 = new DescriptionCglib<JohnDoe>(JohnDoe.class);
		desc1.addDependant(desc2, "getPartner");
		final JaneDoe monitored = MonitorCglib.monitor(original, desc1, new DefaultExceptionHandler(logger));
		final JohnDoe monitoredJohn = monitored.getPartner();
		try {
			monitoredJohn.fail(true);
			fail();
		} catch (IllegalStateException e) {
			assertTrue(logger.toString().indexOf("java.lang.Exception: caller...") > 100);
		}
	}
	
	@Test
	public void testPlainDescription() {
		final DescriptionCglib<Iterator<String>> desc = new DescriptionCglib<Iterator<String>>(Iterator.class);
		final Iterator<String> monitored = MonitorCglib.monitor(Collections.singleton("hello").iterator(), desc, new DefaultExceptionHandler(logger));
		Assert.assertEquals("hello", monitored.next());
		Assert.assertFalse(monitored.hasNext());
	}

}
