package de.twometer.fakechats.model;

public enum MessageSender {
    OTHER,
    SELF,
    SYSTEM_DATE,
    SYSTEM_UNKNOWN_NUMBER;

    public MessageSender invert() {
        if (this == OTHER) return SELF;
        else if (this == SELF) return OTHER;
        else return this;
    }
}
