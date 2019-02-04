package com.capitalone.dashboard.api.domain;

public interface Versionable<T> {

    T getOldVersion();
    void setOldVersion(T oldVersion);

    int getVersion();
}
