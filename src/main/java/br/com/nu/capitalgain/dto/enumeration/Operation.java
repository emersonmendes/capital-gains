package br.com.nu.capitalgain.dto.enumeration;

public enum Operation {

    BUY("BUY"),
    SELL("sell");

    private final String key;

    Operation(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
