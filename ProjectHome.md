![http://kongcurrent.googlecode.com/svn/trunk/kongcurrent/doc/de/engehausen/kongcurrent/doc-files/Monitor-1.png](http://kongcurrent.googlecode.com/svn/trunk/kongcurrent/doc/de/engehausen/kongcurrent/doc-files/Monitor-1.png)

This project provides an easy-to use helper to assist debugging issues which cause exceptions under concurrent access.

This helper was inspired by a problem where a non-thread-safe map object held by a third party framework accidentally was shared between threads causing a `ConcurrentModificationException`. The helper - a "monitor" - creates a proxied version of an object implementing some interface; the proxy can track invocations of the objects' methods and can report on potential concurrent access on the object. This can be used to help find out code paths that concurrently access the object through its interface methods. By design the helper is extensible and can be adapted to more specific needs. Experimentally, the current release supports proxying [non-interface objects using cglib](http://kongcurrent.googlecode.com/svn/trunk/kongcurrent/doc/de/engehausen/kongcurrent/cglib/MonitorCglib.html), I am interested in your feedback on this.

Here is a simple example code snippet that will be adapted to use the monitor:

```
List<String> myList = new ArrayList<String>();
processing(myList);
```

Now you want to know what happens to the list when it is used in the `processing(List<String>)` method. Instead of the original list you simply pass the monitored version to the processing method:

```
List<String> myList = new ArrayList<String>();
List<String> monitoredList = Monitor.monitor(myList,
                                             DefaultDescriptions.<String>listDescription(), 
                                             new DefaultExceptionHandler());
processing(monitoredList);
```

This code will create a monitored list based on the default "description" for List objects; a description tells the monitor what interface to proxy, how to deal with return values which may depend on the originally monitored object (e.g. iterators backing the original collection) and how object equality is defined. The default logger passed in will output to `java.lang.System.out`. The `DefaultExceptionHandler` will record the stack traces of all callers and output these in case an exception occurs during method invocation of a monitored method. Important: Keeping track of this information is costly. The methods of the monitored instance can be considerably slowed down; this can directly affect the situation you try to analyze, up to the point where this situation does not happen any more due to the changed run-time behaviour. Please keep this in mind.

In case of a problem, e.g. when a `ConcurrentModificationException` occurs the logger would output something similar to this:

```
exception occurred:
Thread[Thread-1,5,main] - java.util.ConcurrentModificationException
	at java.util.AbstractList$Itr.checkForComodification(Unknown Source)
	at java.util.AbstractList$Itr.next(Unknown Source)
	at sun.reflect.GeneratedMethodAccessor2.invoke(Unknown Source)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)
	at java.lang.reflect.Method.invoke(Unknown Source)
	at de.engehausen.kongcurrent.Monitor$MonitorHandler.invoke(Monitor.java:124)
	at $Proxy1.next(Unknown Source)
	at Test.run(Unknown Source)
	at java.lang.Thread.run(Unknown Source) 

the following threads were recently operating on the object:
Thread[Thread-1,5,main] - java.lang.Exception: caller...
	at $Proxy1.next(Unknown Source)
	at Test.run(Unknown Source)
	at java.lang.Thread.run(Unknown Source)
Thread[main,5,main] - java.lang.Exception: caller...
	at $Proxy0.add(Unknown Source)
	at Test.run(Unknown Source)
	at java.lang.Thread.run(Unknown Source)
```

The output informs you that a `ConcurrentModificationException` occurred in thread "Thread-1,5,main" while iterating the list. It also shows the stack traces of "recent" calls to the object. In there you find a thread which added to the list (thread "main,5,main"), while the other one was iterating the list - which explains why the exception occurred. What is "recent"? The `DefaultExceptionHandler` will keep a weak reference of the stack trace for each thread accessing the monitored object. You may want to change what exactly is kept track of, when and for how long - therefore: _All this can be adapted to your needs._

If you have a custom interface to monitor, you will likely need to provide a `Description` for the interface to monitor, which includes methods which may return "dependant" objects (i.e. objects that somehow are backed by the monitored object). If the object to monitor has special semantics you need to provide an appropriate `Comparator`. For the Java collection objects default descriptions and comparators exist (see `DefaultDescriptions` and `DefaultComparators`).

One final note: This helper is a debugging tool and as such **should not be used in "production environments"**.

Now why don't you try it out by downloading the helper, or browse the [JavaDoc](http://kongcurrent.googlecode.com/svn/trunk/kongcurrent/doc/index.html)?