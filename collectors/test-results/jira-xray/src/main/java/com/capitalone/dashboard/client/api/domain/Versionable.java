package com.capitalone.dashboard.client.api.domain;

public interface Versionable<T> {

    T getOldVersion();
    void setOldVersion(T oldVersion);

    int getVersion();
}
