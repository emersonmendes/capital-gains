package br.com.nu.capitalgain.service;


import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.StockOperation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public void shouldNotPayTaxesForSellOperationsBelowThreshold() throws JsonProcessingException {

        // Arrange
        var stockOperationService = new StockOperationService(configMock);

        StockOperation buyOperation = StockOperation
            .operation(BUY)
            .unitCost(BigDecimal.valueOf(10.00))
            .quantity(100);

        StockOperation sellOperation1 = StockOperation
            .operation(SELL)
            .unitCost(BigDecimal.valueOf(15.00))
            .quantity(100);

        StockOperation sellOperation2 = StockOperation
            .operation(BUY)
            .unitCost(BigDecimal.valueOf(15.00))
            .quantity(100);

        // Act
        List<OperationTax> taxes = stockOperationService.calculate(List.of(buyOperation, sellOperation1, sellOperation2));

        // Assert
        Assertions.assertThat(taxes).hasSize(3);
        Assertions.assertThat(taxes.get(0).tax()).isEqualTo(OperationTax.ofZero().tax());
        Assertions.assertThat(taxes.get(1).tax()).isEqualTo(OperationTax.ofZero().tax());
        Assertions.assertThat(taxes.get(2).tax()).isEqualTo(OperationTax.ofZero().tax());

    }

    @Test // Caso #2
    public void shouldPayTaxesWhenSellingPriceIsGreaterThanWAP() throws JsonProcessingException {

        var stockOperationService = new StockOperationService(configMock);

        List<OperationTax> taxes = stockOperationService.calculate(List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(5000),
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(5.00)).quantity(5000)
        ));

        String result = """
            [{"tax":0.00},{"tax":10000.00},{"tax":0.00}]
        """.trim();

        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

    @Test // Caso #3
    public void shouldPayTaxesAndDeductTheLoss() throws JsonProcessingException {

        var stockOperationService = new StockOperationService(configMock);

        List<OperationTax> taxes = stockOperationService.calculate(List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(5.00)).quantity(5000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(3000)
        ));

        String result = """
            [{"tax":0.00},{"tax":0.00},{"tax":1000.00}]
        """.trim();
        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

    @Test // Caso #4
    public void shouldOperateWithNoLossAndNoProfit() throws JsonProcessingException {

        var stockOperationService = new StockOperationService(configMock);

        List<OperationTax> taxes = stockOperationService.calculate(List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(25.00)).quantity(5000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(10000)
        ));

        String result = """
            [{"tax":0.00},{"tax":0.00},{"tax":0.00}]
        """.trim();
        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

    @Test // Caso #5
    public void shouldOperateWithNoLossAndNoProfitxx() throws JsonProcessingException {

        var stockOperationService = new StockOperationService(configMock);

        List<OperationTax> taxes = stockOperationService.calculate(List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(25.00)).quantity(5000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(5000)
        ));

        String result = """
            [{"tax":0.00},{"tax":0.00},{"tax":0.00},{"tax":10000.00}]
        """.trim();
        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

    @Test // Caso #5
    public void shouldPayTaxAfterGetProfitAndAfterNoLossAndNoProfit() throws JsonProcessingException {

        var stockOperationService = new StockOperationService(configMock);

        List<OperationTax> taxes = stockOperationService.calculate(List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(25.00)).quantity(5000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(5000)
        ));

        String result = """
            [{"tax":0.00},{"tax":0.00},{"tax":0.00},{"tax":10000.00}]
        """.trim();

        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

    @Test // Caso #6 // TODO: UM NOME MELHOR
    public void shouldDAR_UM_NOM_MELHOR_PRA_ESSE() throws JsonProcessingException {

        var stockOperationService = new StockOperationService(configMock);

        List<OperationTax> taxes = stockOperationService.calculate(List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(2)).quantity(5000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(1000)
        ));

        String result = """
            [{"tax":0.00},{"tax":0.00},{"tax":0.00},{"tax":0.00},{"tax":3000.00}]
        """.trim();

        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

    @Test // Caso #7
    public void shouldDAR_UM_NOM_MELHOR_PRA_ESSE2() throws JsonProcessingException {

        var stockOperationService = new StockOperationService(configMock);

        List<OperationTax> taxes = stockOperationService.calculate(List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(2.00)).quantity(5000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(1000),
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(20.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(5000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(30.00)).quantity(4350),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(30.00)).quantity(650)
        ));

        //todo: DEVE PODER ACEITAR JSON COM ESPAÇOS TBM
        String result = """
            [{"tax":0.00},{"tax":0.00},{"tax":0.00},{"tax":0.00},{"tax":3000.00},{"tax":0.00},{"tax":0.00},{"tax":3700.00},{"tax":0.00}]
        """.trim();

        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

    @Test // Caso #8
    public void shouldDAR_UM_NOM_MELHOR_PRA_ESSE3() throws JsonProcessingException {

        var stockOperationService = new StockOperationService(configMock);

        List<OperationTax> taxes = stockOperationService.calculate(List.of(
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(50.00)).quantity(10000),
            StockOperation.operation(BUY).unitCost(BigDecimal.valueOf(20.00)).quantity(10000),
            StockOperation.operation(SELL).unitCost(BigDecimal.valueOf(50.00)).quantity(10000)
        ));

        //todo: DEVE PODER ACEITAR JSON COM ESPAÇOS TBM
        String result = """
            [{"tax":0.00},{"tax":80000.00},{"tax":0.00},{"tax":60000.00}]
        """.trim();

        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

}