package br.com.emersonmendes.capitalgain.service.processor;

import br.com.emersonmendes.capitalgain.config.ConfigLoader;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OperationProcessorFactoryTest {

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenProcessorDoesNotExist() {

        // Arrange
        final var configMock = mock(ConfigLoader.class);
        when(configMock.getBigDecimalProp(eq("tax.exempt.threshold"))).thenReturn(BigDecimal.valueOf(20000));
        when(configMock.getBigDecimalProp(eq("tax.rate"))).thenReturn(BigDecimal.valueOf(20));

        final var operationProcessorFactory = new OperationProcessorFactory(configMock);

        // Act
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            operationProcessorFactory.createProcessor(null);
        });

        // Assert
        Assertions.assertThat(exception.getMessage()).isEqualTo("Type not found for operation: null");

    }

}