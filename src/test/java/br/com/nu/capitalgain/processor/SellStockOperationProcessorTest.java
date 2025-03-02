package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.StockOperation;
import br.com.nu.capitalgain.dto.enumeration.OperationType;
import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SellStockOperationProcessorTest {

    private ConfigLoader configMock;


    @Test
    void shouldReturnZeroOperationTax() {

        // Arrange
        configMock = mock(ConfigLoader.class);
        when(configMock.getBigDecimalProp(eq("tax.exempt.amount.max"))).thenReturn(BigDecimal.valueOf(20));
        when(configMock.getBigDecimalProp(eq("tax.paid.percentage"))).thenReturn(BigDecimal.valueOf(20));

        SellStockOperationProcessor processor = new SellStockOperationProcessor(configMock);
        StockOperation operation = StockOperation.operation(OperationType.SELL).unitCost(BigDecimal.valueOf(10000)).quantity(1000);

        // Act
        OperationTax operationTax = processor.proccess(operation, new StockOperationContext(operation));

        // Assert
        Assertions.assertThat(operationTax).isNotNull();
        Assertions.assertThat(operationTax).isEqualTo(OperationTax.ofZero());

    }

}