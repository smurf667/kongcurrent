package de.engehausen.kongcurrent.cglib;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

import de.engehausen.kongcurrent.testhelper.cglib.ArrayListConstructorInformation;

public class DescriptionCglibTest {

	@Test
	public void testSimpleEquality() {
		final DescriptionCglib<Object> desc1 = new DescriptionCglib<Object>(Object.class);
		final DescriptionCglib<Object> desc2 = new DescriptionCglib<Object>(Object.class);
		Assert.assertEquals(desc1, desc1);
		Assert.assertEquals(desc1, desc2);
		Assert.assertEquals(desc2, desc1);
		Assert.assertFalse(desc1.equals(new DescriptionCglib<String>(String.class)));
		Assert.assertFalse(desc1.equals(null));
		Assert.assertTrue(desc1.hashCode() == desc2.hashCode());
	}
	
	@Test
	public void testWithCtorInfo() {
		final ArrayList<String> orig = new ArrayList<String>(0);
		final DescriptionCglib<ArrayList<String>> desc1 = new DescriptionCglib<ArrayList<String>>(ArrayList.class, new ArrayListConstructorInformation(orig));
		final DescriptionCglib<ArrayList<String>> desc2 = new DescriptionCglib<ArrayList<String>>(ArrayList.class, new ArrayListConstructorInformation(orig));
		Assert.assertEquals(desc1, desc2);
		Assert.assertEquals(desc2, desc1);
		Assert.assertFalse(desc1.equals(new DescriptionCglib<ArrayList<String>>(ArrayList.class)));
		Assert.assertFalse(desc1.equals(null));		
	}
	
}
