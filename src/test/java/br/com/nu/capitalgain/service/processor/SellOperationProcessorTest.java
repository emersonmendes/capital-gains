package br.com.nu.capitalgain.service.processor;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.OperationType;
import br.com.nu.capitalgain.dto.Operation;
import br.com.nu.capitalgain.service.processor.OperationContext;
import br.com.nu.capitalgain.service.processor.SellOperationProcessor;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SellOperationProcessorTest {

    private ConfigLoader configMock;

    @Test
    public void shouldReturnZeroOperationTax() {

        // Arrange
        configMock = mock(ConfigLoader.class);
        when(configMock.getBigDecimalProp(eq("tax.exempt.threshold"))).thenReturn(BigDecimal.valueOf(20));
        when(configMock.getBigDecimalProp(eq("tax.rate"))).thenReturn(BigDecimal.valueOf(20));

        final var processor = new SellOperationProcessor(configMock);
        final var operation = Operation.operation(OperationType.SELL).unitCost(BigDecimal.valueOf(10000)).quantity(1000);

        // Act
        final var operationTax = processor.process(operation, new OperationContext(operation));

        // Assert
        Assertions.assertThat(operationTax).isNotNull();
        Assertions.assertThat(operationTax).isEqualTo(OperationTax.ofZero());

    }

}