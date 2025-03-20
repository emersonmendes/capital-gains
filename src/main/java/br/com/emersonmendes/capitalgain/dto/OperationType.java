package br.com.emersonmendes.capitalgain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OperationType {

    BUY("buy"),
    SELL("sell");

    private final String key;

    OperationType(String key) {
        this.key = key;
    }

    @JsonCreator
    public static OperationType fromString(String value) {
        for (var type : OperationType.values()) {
            if (type.key.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid operation type: " + value);
    }

}
