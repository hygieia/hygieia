package com.capitalone.dashboard.util;

/**
 * A class that can supply objects of a single type.  Semantically, this could
 * be a factory, generator, builder, closure, or something else entirely. No
 * guarantees are implied by this interface.
 *
 */
public interface Supplier<T> {

    /**
     * Retrieves an instance of the appropriate type. The returned object may or
     * may not be a new instance, depending on the implementation.
     *
     * @return an instance of the appropriate type
     */
    T get();
}
