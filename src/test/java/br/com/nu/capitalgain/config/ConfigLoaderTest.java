package br.com.nu.capitalgain.config;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.math.BigDecimal;

public class ConfigLoaderTest {

    @Test
    public void shouldLoadConfiguration() {

        // Arrange
        final var config = new ConfigLoader("config");

        // Act
        var taxExemptThreshold = config.getBigDecimalProp("tax.exempt.threshold");
        var taxRate = config.getProp("tax.rate");

        // Assert
        Assertions.assertThat(taxExemptThreshold).isEqualTo(BigDecimal.valueOf(20000.0));
        Assertions.assertThat(taxRate).isEqualTo("20");

    }

}