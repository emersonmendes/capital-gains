package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.ShareOperation;
import br.com.nu.capitalgain.dto.enumeration.OperationType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class BuyShareOperationProcessorTest {

    @Test
    void shouldReturnZeroTaxWhenDoABuyShareOperation() {

        // Arrange
        BuyShareOperationProcessor processor = new BuyShareOperationProcessor();
        ShareOperation operation = ShareOperation.operation(OperationType.BUY).unitCost(BigDecimal.ONE).quantity(1000);

        // Act
        OperationTax operationTax = processor.process(operation, new ShareOperationContext(operation));

        // Assert
        Assertions.assertThat(operationTax).isNotNull();
        Assertions.assertThat(operationTax).isEqualTo(OperationTax.ofZero());

    }


    @Test
    public void shouldRecalculateWeightedAveragePriceWhenBuyingShares() {

    }

}