package de.twometer.fakechats.model;

import java.io.Serializable;

public class ContactData implements Serializable {

    private String name;
    private String lastSeenState;

    public ContactData(String name, String lastSeenState) {
        this.name = name;
        this.lastSeenState = lastSeenState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastSeenState() {
        return lastSeenState;
    }

    public void setLastSeenState(String lastSeenState) {
        this.lastSeenState = lastSeenState;
    }
}
