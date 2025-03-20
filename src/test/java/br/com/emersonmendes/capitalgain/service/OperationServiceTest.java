package br.com.emersonmendes.capitalgain.service;


import br.com.emersonmendes.capitalgain.config.ConfigLoader;
import br.com.emersonmendes.capitalgain.dto.OperationTax;
import br.com.emersonmendes.capitalgain.dto.Operation;
import br.com.emersonmendes.capitalgain.service.processor.OperationContext;
import br.com.emersonmendes.capitalgain.service.processor.OperationProcessorFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static br.com.emersonmendes.capitalgain.dto.OperationType.BUY;
import static br.com.emersonmendes.capitalgain.dto.OperationType.SELL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OperationServiceTest {

    private ConfigLoader configMock;
    private OperationProcessorFactory operationProcessorFactory;

    @Before
    public void setUp(){

        this.configMock = mock(ConfigLoader.class);
        when(configMock.getBigDecimalProp(eq("tax.exempt.threshold"))).thenReturn(BigDecimal.valueOf(20000));
        when(configMock.getBigDecimalProp(eq("tax.rate"))).thenReturn(BigDecimal.valueOf(20));

        this.operationProcessorFactory = new OperationProcessorFactory(this.configMock);

    }

    @Test // Caso #1
    public void shouldNotPayTaxesForSellOperationsBelowThreshold() {

        // Arrange
        var operationService = new OperationService(this.operationProcessorFactory);

        var operations = List.of(
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(100),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(100),
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(15.00)).quantity(100)
        );

        // Act
        var taxes = operationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSameSizeAs(operations);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO)
        );

    }

    @Test // Caso #2
    public void shouldApplyTaxWhenSellingAboveWeightedAverageCost() {

        // Arrange
        var operationService = new OperationService(operationProcessorFactory);

        var operations = List.of(
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(5000),
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(5.00)).quantity(5000)
        );

        // Act
        var taxes = operationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSameSizeAs(operations);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(10_000.00)),
            OperationTax.of(BigDecimal.ZERO)
        );

    }

    @Test // Caso #3
    public void shouldDeductAccumulatedLossBeforeApplyingTax() {

        // Arrange
        var operationService = new OperationService(operationProcessorFactory);

        var operations = List.of(
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(5.00)).quantity(5000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(3000)
        );

        // Act
        var taxes = operationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSameSizeAs(operations);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(1_000.00))
        );

    }

    @Test // Caso #4
    public void shouldProcessOperationsWithoutApplyingTaxWhenNoLossOrGain() {

        // Arrange
        var operationService = new OperationService(operationProcessorFactory);

        var operations = List.of(
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(25.00)).quantity(5000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(10000)
        );

        // Act
        var taxes = operationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSameSizeAs(operations);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO)
        );Assertions.assertThat(taxes).hasSize(3);

    }

    @Test // Caso #5
    public void shouldPayTaxOnlyAfterCapitalGainWhenNoLossOrPreviousGainRemains() {

        // Arrange
        var operationService = new OperationService(operationProcessorFactory);

        var operations = List.of(
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(25.00)).quantity(5000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(10000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(5000)
        );

        // Act
        var taxes = operationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSameSizeAs(operations);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(10_000.00))
        );

    }

    @Test // Caso #6
    public void shouldDeductLossesBeforeApplyingTaxOnCapitalGains() {

        // Arrange
        var operationService = new OperationService(operationProcessorFactory);

        var operations = List.of(
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(2.00)).quantity(5000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(1000)
        );

        // Act
        var taxes = operationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSameSizeAs(operations);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(3_000.00))
        );

    }

    @Test // Caso #7
    public void shouldRecalculateWeightedAverageConsideringAllPreviousBuysUntilCurrentSale() {

        // Arrange
        var operationService = new OperationService(operationProcessorFactory);

        var operations = List.of(
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(2.00)).quantity(5000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(1000),
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(20.00)).quantity(10000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(5000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(30.00)).quantity(4350),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(30.00)).quantity(650)
        );

        // Act
        var taxes = operationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSameSizeAs(operations);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(3_000.00)),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(3_700.00)),
            OperationTax.of(BigDecimal.ZERO)
        );
    }

    @Test // Caso #8
    public void shouldApplyCapitalGainsTaxForMultipleBuyAndSellOperations() {

        // Arrange
        var operationService = new OperationService(operationProcessorFactory);

        var operations = List.of(
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(50.00)).quantity(10000),
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(20.00)).quantity(10000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(50.00)).quantity(10000)
        );

        // Act
        var taxes = operationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSameSizeAs(operations);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(8_0000.00)),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(6_0000.00))
        );

    }

    @Test
    public void shouldApply20PercentTaxOnOperationCapitalGain() {

        // Arrange
        var operationService = new OperationService(operationProcessorFactory);
        when(configMock.getBigDecimalProp(eq("tax.rate"))).thenReturn(BigDecimal.valueOf(20));

        var operations = List.of(
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(100.00)).quantity(1000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(200.00)).quantity(1000)
        );

        // Act
        var taxes = operationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSameSizeAs(operations);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(20_000.00))
        );

    }

    @Test
    public void shouldRecalculateWeightedAveragePriceAfterBuyAndSellOperations() {

        // Arrange
        var operationService = new OperationService(operationProcessorFactory);
        var operations = List.of(
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(20.00)).quantity(10),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(5),
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(5)
        );

        // Act
        List<OperationTax> taxes = operationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSameSizeAs(operations);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO)
        );

    }

    @Test
    public void shouldOffsetAccumulatedLossAgainstMultipleFutureCapitalGainsUntilFullyDeducted() {

        // Arrange
        final var operationService = new OperationService(operationProcessorFactory);
        final var operations = List.of(
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(05.00)).quantity(5000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(1500),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(1000)
        );

        // Act
        List<OperationTax> taxes = operationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSameSizeAs(operations);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO)
        );

    }

}