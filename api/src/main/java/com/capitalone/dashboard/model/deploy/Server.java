package com.capitalone.dashboard.model.deploy;

public class Server {
    private final String name;
    private final boolean online;

    public Server(String name, boolean online) {
        this.name = name;
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return online;
    }
}
