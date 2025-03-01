package br.com.nu.capitalgain.dto.enumeration;

public enum OperationType {

    BUY("BUY"),
    SELL("sell");

    private final String key;

    OperationType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
