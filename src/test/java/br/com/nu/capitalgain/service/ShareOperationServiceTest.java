package br.com.nu.capitalgain.service;


import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.ShareOperation;
import br.com.nu.capitalgain.processor.ShareOperationContext;
import br.com.nu.capitalgain.processor.ShareOperationProcessorFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static br.com.nu.capitalgain.dto.OperationType.BUY;
import static br.com.nu.capitalgain.dto.OperationType.SELL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShareOperationServiceTest {

    private ConfigLoader configMock;
    private ShareOperationProcessorFactory shareOperationProcessorFactory;

    @Before
    public void setUp(){

        this.configMock = mock(ConfigLoader.class);
        when(configMock.getBigDecimalProp(eq("tax.exempt.threshold"))).thenReturn(BigDecimal.valueOf(20000));
        when(configMock.getBigDecimalProp(eq("tax.rate"))).thenReturn(BigDecimal.valueOf(20));

        this.shareOperationProcessorFactory = new ShareOperationProcessorFactory(this.configMock);

    }

    @Test // Caso #1
    public void shouldNotPayTaxesForSellOperationsBelowThreshold() {

        // Arrange
        var shareOperationService = new ShareOperationService(this.shareOperationProcessorFactory);

        List<ShareOperation> operations = List.of(
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(100),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(100),
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(15.00)).quantity(100)
        );

        var context = new ShareOperationContext(operations.getFirst());

        // Act
        List<OperationTax> taxes = shareOperationService.calculate(operations, context);

        // Assert
        Assertions.assertThat(taxes).hasSameSizeAs(operations);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO)
        );

    }

    @Test // Caso #2
    public void shouldPayTaxesWhenSellingPriceIsGreaterThanWeightedAvgCost() {

        // Arrange
        var shareOperationService = new ShareOperationService(shareOperationProcessorFactory);

        List<ShareOperation> operations = List.of(
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(5000),
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(5.00)).quantity(5000)
        );

        var context = new ShareOperationContext(operations.getFirst());

        // Act
        List<OperationTax> taxes = shareOperationService.calculate(operations, context);

        // Assert
        Assertions.assertThat(taxes).hasSameSizeAs(operations);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(10_000.00)),
            OperationTax.of(BigDecimal.ZERO)
        );

    }

    @Test // Caso #3
    public void shouldApplyTaxesAfterDeductingAccumulatedLoss() {

        // Arrange
        var shareOperationService = new ShareOperationService(shareOperationProcessorFactory);

        List<ShareOperation> operations = List.of(
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(5.00)).quantity(5000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(3000)
        );

        var context = new ShareOperationContext(operations.getFirst());

        // Act
        List<OperationTax> taxes = shareOperationService.calculate(operations, context);

        // Assert
        Assertions.assertThat(taxes).hasSameSizeAs(operations);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(1_000.00))
        );

    }

    @Test // Caso #4
    public void shouldProcessOperationWithNoNetLossOrCapitalGain() {

        // Arrange
        var shareOperationService = new ShareOperationService(shareOperationProcessorFactory);

        List<ShareOperation> operations = List.of(
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(25.00)).quantity(5000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(10000)
        );

        var context = new ShareOperationContext(operations.getFirst());

        // Act
        List<OperationTax> taxes = shareOperationService.calculate(operations, context);

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
        var shareOperationService = new ShareOperationService(shareOperationProcessorFactory);

        List<ShareOperation> operations = List.of(
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(25.00)).quantity(5000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(10000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(5000)
        );

        var context = new ShareOperationContext(operations.getFirst());

        // Act
        List<OperationTax> taxes = shareOperationService.calculate(operations, context);

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
    public void shouldValidateCase6() {

        // Arrange
        var shareOperationService = new ShareOperationService(shareOperationProcessorFactory);

        List<ShareOperation> operations = List.of(
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(2.00)).quantity(5000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(1000)
        );

        var context = new ShareOperationContext(operations.getFirst());

        // Act
        List<OperationTax> taxes = shareOperationService.calculate(operations, context);
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
    public void shouldValidateCase7() {

        // Arrange
        var shareOperationService = new ShareOperationService(shareOperationProcessorFactory);

        List<ShareOperation> operations = List.of(
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(2.00)).quantity(5000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(1000),
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(20.00)).quantity(10000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(5000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(30.00)).quantity(4350),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(30.00)).quantity(650)
        );

        var context = new ShareOperationContext(operations.getFirst());

        // Act
        List<OperationTax> taxes = shareOperationService.calculate(operations, context);

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
    public void shouldValidateCase8() {

        // Arrange
        var shareOperationService = new ShareOperationService(shareOperationProcessorFactory);

        List<ShareOperation> operations = List.of(
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(50.00)).quantity(10000),
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(20.00)).quantity(10000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(50.00)).quantity(10000)
        );

        var context = new ShareOperationContext(operations.getFirst());

        // Act
        List<OperationTax> taxes = shareOperationService.calculate(operations, context);

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
        var shareOperationService = new ShareOperationService(shareOperationProcessorFactory);
        when(configMock.getBigDecimalProp(eq("tax.rate"))).thenReturn(BigDecimal.valueOf(20));

        List<ShareOperation> operations = List.of(
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(100.00)).quantity(1000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(200.00)).quantity(1000)
        );

        var context = new ShareOperationContext(operations.getFirst());

        // Act
        List<OperationTax> taxes = shareOperationService.calculate(operations, context);

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
        var shareOperationService = new ShareOperationService(shareOperationProcessorFactory);
        List<ShareOperation> operations = List.of(
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(20.00)).quantity(10),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(5),
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(5)
        );

        var context = new ShareOperationContext(operations.getFirst());

        // Act
        shareOperationService.calculate(operations, context);

        // Assert
        Assertions.assertThat(context.getWeightedAvgCost()).isEqualTo(BigDecimal.valueOf(15.0));

    }

    @Test
    public void shouldOffsetAccumulatedLossAgainstMultipleFutureCapitalGainsUntilFullyDeducted() {

        // Arrange
        var shareOperationService = new ShareOperationService(shareOperationProcessorFactory);
        List<ShareOperation> operations = List.of(
            ShareOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(05.00)).quantity(5000),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(1500),
            ShareOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(1000)
        );

        var context = new ShareOperationContext(operations.getFirst());

        // Act
        shareOperationService.calculate(operations, context);

        // Assert
        Assertions.assertThat(context.getLoss()).isEqualTo(BigDecimal.ZERO);

    }

}