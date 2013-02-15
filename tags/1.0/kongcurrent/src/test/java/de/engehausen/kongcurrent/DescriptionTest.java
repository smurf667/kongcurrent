package de.engehausen.kongcurrent;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

public class DescriptionTest {

	@Test
	public void testEquality() {
		final Description<List<?>> desc1 = new Description<List<?>>(List.class);
		final Description<List<?>> desc2 = new Description<List<?>>(List.class);
		Assert.assertEquals(desc1, desc1);
		Assert.assertEquals(desc1, desc2);
		Assert.assertEquals(desc2, desc1);
		Assert.assertFalse(desc1.equals(new Description<Map<?, ?>>(Map.class)));
		Assert.assertFalse(desc1.equals(null));
		Assert.assertTrue(desc1.hashCode() == desc2.hashCode());
	}
	
}
