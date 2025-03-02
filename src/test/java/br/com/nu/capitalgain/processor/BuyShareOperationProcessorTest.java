package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.OperationType;
import br.com.nu.capitalgain.dto.ShareOperation;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.math.BigDecimal;

public class BuyShareOperationProcessorTest {

    @Test
    public void shouldReturnZeroTaxWhenDoABuyShareOperation() {

        // Arrange
        var processor = new BuyShareOperationProcessor();
        var operation = ShareOperation.operation(OperationType.BUY).unitCost(BigDecimal.ONE).quantity(1000);

        // Act
        var operationTax = processor.process(operation, new ShareOperationContext(operation));

        // Assert
        Assertions.assertThat(operationTax).isNotNull();
        Assertions.assertThat(operationTax).isEqualTo(OperationTax.ofZero());

    }


    @Test
    public void shouldRecalculateWeightedAveragePriceWhenBuyingShares() {

        // Arrange
        var operation1 = ShareOperation.operation(OperationType.BUY).unitCost(BigDecimal.valueOf(70)).quantity(7000);
        var operation2 = ShareOperation.operation(OperationType.BUY).unitCost(BigDecimal.valueOf(140)).quantity(2000);

        var processor = new BuyShareOperationProcessor();
        var shareOperationContext = new ShareOperationContext(operation1);

        // Act
        processor.process(operation1, shareOperationContext);

        // Assert
        Assertions.assertThat(shareOperationContext.getWeightedAvgCost()).isEqualTo(BigDecimal.valueOf(70));

        // Act
        processor.process(operation2, shareOperationContext);

        // Assert
        Assertions.assertThat(shareOperationContext.getWeightedAvgCost()).isEqualTo(BigDecimal.valueOf(86));

    }

}