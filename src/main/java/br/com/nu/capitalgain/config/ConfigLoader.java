package br.com.nu.capitalgain.config;

import java.math.BigDecimal;
import java.util.ResourceBundle;

public class ConfigLoader {

    private final ResourceBundle bundle;

    public ConfigLoader(String fileName) {
        bundle = ResourceBundle.getBundle(fileName);
    }

    public String getProp(String key) {
        return bundle.getString(key);
    }

    public BigDecimal getBigDecimalProp(String key) {
        String property = getProp(key);
        return BigDecimal.valueOf(Double.parseDouble(property));
    }

}
