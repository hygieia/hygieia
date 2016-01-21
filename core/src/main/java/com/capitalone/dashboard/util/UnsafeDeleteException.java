package com.capitalone.dashboard.util;

/**
 * Created by jkc on 1/20/16.
 */
public class UnsafeDeleteException extends RuntimeException {
    public UnsafeDeleteException() {
        super();
    }
    public UnsafeDeleteException(String s) {
        super(s);
    }
    public UnsafeDeleteException(String s, Throwable throwable) {
        super(s, throwable);
    }
    public UnsafeDeleteException(Throwable throwable) {
        super(throwable);
    }

}
