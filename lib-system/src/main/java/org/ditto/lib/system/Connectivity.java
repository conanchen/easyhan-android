package org.ditto.lib.system;

public class Connectivity {
    public enum Type {
        WIFI, MOBILE
    }

    private Type type;
    private boolean connected;

    public Connectivity(Type type, boolean isConnected) {
        this.type = type;
        this.connected = isConnected;
    }

    public Type getType() {
        return type;
    }

    public boolean isConnected() {
        return connected;
    }
}