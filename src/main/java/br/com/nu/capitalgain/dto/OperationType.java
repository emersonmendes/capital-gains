package br.com.nu.capitalgain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OperationType {

    BUY("buy"),
    SELL("sell");

    private final String key;

    OperationType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @JsonCreator
    public static OperationType fromString(String value) {
        for (OperationType type : OperationType.values()) {
            if (type.key.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid operation type: " + value);
    }

}
