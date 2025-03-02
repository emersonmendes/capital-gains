package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.OperationType;
import br.com.nu.capitalgain.dto.ShareOperation;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SellShareOperationProcessorTest {

    private ConfigLoader configMock;

    @Test
    public void shouldReturnZeroOperationTax() {

        // Arrange
        configMock = mock(ConfigLoader.class);
        when(configMock.getBigDecimalProp(eq("tax.exempt.threshold"))).thenReturn(BigDecimal.valueOf(20));
        when(configMock.getBigDecimalProp(eq("tax.rate"))).thenReturn(BigDecimal.valueOf(20));

        SellShareOperationProcessor processor = new SellShareOperationProcessor(configMock);
        ShareOperation operation = ShareOperation.operation(OperationType.SELL).unitCost(BigDecimal.valueOf(10000)).quantity(1000);

        // Act
        OperationTax operationTax = processor.process(operation, new ShareOperationContext(operation));

        // Assert
        Assertions.assertThat(operationTax).isNotNull();
        Assertions.assertThat(operationTax).isEqualTo(OperationTax.ofZero());

    }

}