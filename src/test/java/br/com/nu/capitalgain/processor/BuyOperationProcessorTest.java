package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.OperationType;
import br.com.nu.capitalgain.dto.Operation;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.math.BigDecimal;

public class BuyOperationProcessorTest {

    @Test
    public void shouldReturnZeroTaxWhenDoABuyOperation() {

        // Arrange
        var processor = new BuyOperationProcessor();
        var operation = Operation.operation(OperationType.BUY).unitCost(BigDecimal.ONE).quantity(1000);

        // Act
        var operationTax = processor.process(operation, new OperationContext(operation));

        // Assert
        Assertions.assertThat(operationTax).isNotNull();
        Assertions.assertThat(operationTax).isEqualTo(OperationTax.ofZero());

    }


    @Test
    public void shouldRecalculateWeightedAveragePriceWhenDoBuyOperation() {

        // Arrange
        var operation1 = Operation.operation(OperationType.BUY).unitCost(BigDecimal.valueOf(70)).quantity(7000);
        var operation2 = Operation.operation(OperationType.BUY).unitCost(BigDecimal.valueOf(140)).quantity(2000);

        var processor = new BuyOperationProcessor();
        var operationContext = new OperationContext(operation1);

        // Act
        processor.process(operation1, operationContext);

        // Assert
        Assertions.assertThat(operationContext.getWeightedAvgCost()).isEqualTo(BigDecimal.valueOf(70));

        // Act
        processor.process(operation2, operationContext);

        // Assert
        Assertions.assertThat(operationContext.getWeightedAvgCost()).isEqualTo(BigDecimal.valueOf(86));

    }

}