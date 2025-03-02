package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.StockOperation;
import br.com.nu.capitalgain.dto.enumeration.OperationType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class BuyStockOperationProcessorTest {

    @Test
    void shouldReturnZeroTaxWhenDoABuyStockOperation() {

        // Arrange
        BuyStockOperationProcessor processor = new BuyStockOperationProcessor();
        StockOperation operation = StockOperation.operation(OperationType.BUY).unitCost(BigDecimal.ONE).quantity(1000);

        // Act
        OperationTax operationTax = processor.process(operation, new StockOperationContext(operation));

        // Assert
        Assertions.assertThat(operationTax).isNotNull();
        Assertions.assertThat(operationTax).isEqualTo(OperationTax.ofZero());

    }

}