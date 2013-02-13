package de.engehausen.kongcurrent.cglib;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.engehausen.kongcurrent.Description;
import de.engehausen.kongcurrent.Monitor;
import de.engehausen.kongcurrent.SimpleTest;
import de.engehausen.kongcurrent.cglib.DescriptionCglib;
import de.engehausen.kongcurrent.helper.DefaultExceptionHandler;
import de.engehausen.kongcurrent.testhelper.Demo;
import de.engehausen.kongcurrent.testhelper.DemoImpl;
import de.engehausen.kongcurrent.testhelper.FilteredClassloaderTestRunner;

/**
 * Tests that using a {@link DescriptionCglib} when <code>cglib</code> is not
 * on the class path causes an error, and that "normal" (Java dynamic proxy)
 * is still possible.
 */
@RunWith(FilteredClassloaderTestRunner.class) // make sure cglib classes are not on the classpath
public class NoCglibUseTest {

	/**
	 * Tests that using a {@link DescriptionCglib} when <code>cglib</code> is not
	 * on the class path causes an error. 
	 * @throws NoSuchMethodException in case of error
	 */
	@Test(expected=IllegalStateException.class)
	public void testNoCglibOnPath() throws NoSuchMethodException {
		final Description<Iterator<DemoImpl>> stdDesc = new Description<Iterator<DemoImpl>>(Iterator.class);
		final DescriptionCglib<DemoImpl> cgDesc = new DescriptionCglib<DemoImpl>(Demo.class);
		stdDesc.addDependant(cgDesc, "next", (Class<?>[]) null);
		final Iterator<DemoImpl> monitoredIterator = Monitor.monitor(Arrays.<DemoImpl>asList(new DemoImpl()).iterator(), stdDesc, new DefaultExceptionHandler());
		monitoredIterator.next().getInt();
	}
	
	@Test
	public void testNormalUse() {
		new SimpleTest().testProxying2();
	}

}
