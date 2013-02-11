/**
 * A helper to monitor objects for (concurrent) access paths.
 * This package provides a small helper which allows monitoring an object
 * implementing a specific interface. The goal is to record the stack traces
 * of callers to the object in order to handle cases in which exceptions
 * are cause by concurrent access.
 * <p>This helper was inspired by a problem where a non-threadsafe {@link java.util.Map}
 * object held by a third party framework accidently was shared between threads
 * and caused {@link java.util.ConcurrentModificationException}s. The easy-to-use
 * helper can proxy an interface like <code>Map</code> and monitor the object
 * implementing the interface. By keeping track of the callers to the instance
 * and dumping these in the error case the problem could be identified (i.e. the
 * unintended sharing of the instance between threads, and specifically what kind
 * of threads).
 * <p>To monitor an instance, simply proxy it using {@link de.engehausen.kongcurrent.Monitor#monitor(Object, Description, ExceptionHandler)}.
 */
package de.engehausen.kongcurrent;