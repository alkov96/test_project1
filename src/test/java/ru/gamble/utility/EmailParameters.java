package ru.gamble.utility;

public enum EmailParameters {
    USER("mail.imap.user"),
    HOST("mail.imap.host"),
    PASS("user.password"),
    PORT("mail.imap.port"),
    SSL("mail.imap.ssl.enable"),
    PROTOCOL("mail.store.protocol"),
    DEBUG("mail.debug");

    private final String value;

    EmailParameters(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
