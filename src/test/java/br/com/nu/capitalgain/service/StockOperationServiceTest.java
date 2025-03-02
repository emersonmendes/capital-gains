package br.com.nu.capitalgain.service;


import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.StockOperation;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static br.com.nu.capitalgain.dto.enumeration.OperationType.BUY;
import static br.com.nu.capitalgain.dto.enumeration.OperationType.SELL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StockOperationServiceTest {

    private ConfigLoader configMock;

    @Before
    public void setUp(){
        configMock = mock(ConfigLoader.class);
        when(configMock.getBigDecimalProp(eq("tax.exempt.amount.max"))).thenReturn(BigDecimal.valueOf(20000));
        when(configMock.getBigDecimalProp(eq("tax.paid.percentage"))).thenReturn(BigDecimal.valueOf(20));
    }

    @Test // Caso #1
    public void shouldNotPayTaxesForSellOperationsBelowThreshold() {

        // Arrange
        var stockOperationService = new StockOperationService(configMock);

        List<StockOperation> operations = List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(100),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(100),
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(15.00)).quantity(100)
        );

        // Act
        List<OperationTax> taxes = stockOperationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSize(3);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO)
        );

    }

    @Test // Caso #2
    public void shouldPayTaxesWhenSellingPriceIsGreaterThanWAP() {

        // Arrange
        var stockOperationService = new StockOperationService(configMock);

        List<StockOperation> operations = List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(5000),
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(5.00)).quantity(5000)
        );

        // Act
        List<OperationTax> taxes = stockOperationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSize(3);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(10_000.00)),
            OperationTax.of(BigDecimal.ZERO)
        );

    }

    @Test // Caso #3
    public void shouldPayTaxesAndDeductTheLoss() {

        // Arrange
        var stockOperationService = new StockOperationService(configMock);

        List<StockOperation> operations = List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(5.00)).quantity(5000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(3000)
        );

        // Act
        List<OperationTax> taxes = stockOperationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSize(3);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(1_000.00))
        );

    }

    @Test // Caso #4
    public void shouldOperateWithNoLossAndNoProfit() {

        // Arrange
        var stockOperationService = new StockOperationService(configMock);

        List<StockOperation> operations = List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(25.00)).quantity(5000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(10000)
        );

        // Act
        List<OperationTax> taxes = stockOperationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSize(3);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO)
        );

    }

    @Test // Caso #5
    public void shouldPayTaxAfterGetProfitAndAfterNoLossAndNoProfit() {

        // Arrange
        var stockOperationService = new StockOperationService(configMock);

        List<StockOperation> operations = List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(25.00)).quantity(5000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(5000)
        );

        // Act
        List<OperationTax> taxes = stockOperationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSize(4);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(10_000.00))
        );

    }

    @Test // Caso #6 // TODO: UM NOME MELHOR
    public void shouldDAR_UM_NOM_MELHOR_PRA_ESSE() {

        // Arrange
        var stockOperationService = new StockOperationService(configMock);

        List<StockOperation> operations = List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(2)).quantity(5000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(1000)
        );

        // Act
        List<OperationTax> taxes = stockOperationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSize(5);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(3_000.00))
        );

    }

    @Test // Caso #7
    public void shouldDAR_UM_NOM_MELHOR_PRA_ESSE2() {

        // Arrange
        var stockOperationService = new StockOperationService(configMock);

        List<StockOperation> operations = List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(2.00)).quantity(5000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(1000),
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(20.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(5000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(30.00)).quantity(4350),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(30.00)).quantity(650)
        );

        // Act
        List<OperationTax> taxes = stockOperationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSize(9);
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
    public void shouldDAR_UM_NOM_MELHOR_PRA_ESSE3() {

        // Arrange
        var stockOperationService = new StockOperationService(configMock);

        List<StockOperation> operations = List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(50.00)).quantity(10000),
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(20.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(50.00)).quantity(10000)
        );

        // Act
        List<OperationTax> taxes = stockOperationService.calculate(operations);

        // Assert
        Assertions.assertThat(taxes).hasSize(4);
        Assertions.assertThat(taxes).containsExactly(
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(8_0000.00)),
            OperationTax.of(BigDecimal.ZERO),
            OperationTax.of(BigDecimal.valueOf(6_0000.00))
        );

    }

}