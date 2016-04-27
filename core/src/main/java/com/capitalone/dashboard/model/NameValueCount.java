package com.capitalone.dashboard.model;

public class NameValueCount {
    private NameValue keyValue;
    private int count;

    public NameValueCount(NameValue keyValue, int count) {
        this.keyValue = keyValue;
        this.count = count;
    }

    public NameValue getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(NameValue keyValue) {
        this.keyValue = keyValue;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
