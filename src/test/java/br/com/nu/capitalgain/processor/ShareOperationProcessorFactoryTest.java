package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.config.ConfigLoader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShareOperationProcessorFactoryTest {

    @Test
    void shouldThrowIllegalArgumentExceptionWhenProcessorDoesNotExist() {

        // Arrange
        ConfigLoader configMock = mock(ConfigLoader.class);
        when(configMock.getBigDecimalProp(eq("tax.exempt.threshold"))).thenReturn(BigDecimal.valueOf(20000));
        when(configMock.getBigDecimalProp(eq("tax.rate"))).thenReturn(BigDecimal.valueOf(20));

        var shareOperationProcessorFactory = new ShareOperationProcessorFactory(configMock);

        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            shareOperationProcessorFactory.getInstance(null);
        });

        // Assert
        Assertions.assertThat(exception.getMessage()).isEqualTo("Type not found for operation: null");

    }

}