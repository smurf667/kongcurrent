/**
 * 
 */
package de.engehausen.kongcurrent.testhelper;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class FilteredClassloaderTestRunner extends BlockJUnit4ClassRunner {
	
	public FilteredClassloaderTestRunner(final Class<?> clazz) throws InitializationError {
	    super(fromClass(clazz));
	}

	private static Class<?> fromClass(final Class<?> clazz) throws InitializationError {
	    try {
	        final Class<?> result = Class.forName(clazz.getName(), true, new FilteredClassLoader("de.engehausen.", "cglib"));
	        return result;
	    } catch (final ClassNotFoundException e) {
	        throw new InitializationError(Collections.<Throwable>singletonList(e));
	    }
	}

	private static class FilteredClassLoader extends URLClassLoader {
		
		private final String banned;
		private final String prefix;
		
	    public FilteredClassLoader(final String classPrefix, final String substr) {
	    	super(filter(((URLClassLoader) getSystemClassLoader()).getURLs(), substr));
	    	banned = substr;
	    	prefix = classPrefix;
	    }
	    	    
	    public Class<?> loadClass(final String name) throws ClassNotFoundException {
			if (name.startsWith(prefix)) {
				return super.findClass(name);
			} else if (name.contains(banned)) {
				throw new ClassNotFoundException("banned class: "+name);
			}
			return super.loadClass(name);
		}

		private static URL[] filter(final URL[] urls, final String substr) {
			final List<URL> result = new ArrayList<URL>(urls.length);
			for (int i = urls.length; i-->0; ) {
				if (!urls[i].toExternalForm().contains(substr)) {
					result.add(urls[i]);
				}
			}
			return result.toArray(new URL[result.size()]);
		}

		@Override
		public URL getResource(String s) {
			if (s.contains(banned)) {
				// banned resource
				return null;
			} else {
				return super.getResource(s);
			}
		}

	}
}