package br.com.nu.capitalgain.service;


import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.StockMarketOperation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static br.com.nu.capitalgain.dto.enumeration.Operation.BUY;
import static br.com.nu.capitalgain.dto.enumeration.Operation.SELL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CapitalGainServiceTest {

    private ConfigLoader configMock;

    @Before
    public void setUp(){
        configMock = mock(ConfigLoader.class);
        when(configMock.getBigDecimalProp(eq("tax.exempt.amount.max"))).thenReturn(BigDecimal.valueOf(20000));
        when(configMock.getBigDecimalProp(eq("tax.paid.percentage"))).thenReturn(BigDecimal.valueOf(20));
    }

    @Test // Caso #1
    public void shouldNotPayTaxesWhenTotalOperationLessThan20000Reais() throws JsonProcessingException {

        var capitalGainService = new CapitalGainService(configMock);

        List<OperationTax> taxes = capitalGainService.calculate(List.of(
            StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(100),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(100),
            StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(15.00)).quantity(100)
        ));

        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo("""
        [{"tax":0.00},{"tax":0.00},{"tax":0.00}]""");

    }

    @Test // Caso #2
    public void shouldPayTaxesWhenSellingPriceIsGreaterThanWAP() throws JsonProcessingException {

        var capitalGainService = new CapitalGainService(configMock);

        List<OperationTax> taxes = capitalGainService.calculate(List.of(
            StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(5000),
            StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(5.00)).quantity(5000)
        ));

        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo("""
        [{"tax":0.00},{"tax":10000.00},{"tax":0.00}]""");

    }

    // Case #1 + Case #2 // TODO: integration test

    @Test // Caso #3
    public void shouldPayTaxesAndDeductTheLoss() throws JsonProcessingException {

        var capitalGainService = new CapitalGainService(configMock);

        List<OperationTax> taxes = capitalGainService.calculate(List.of(
            StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(5.00)).quantity(5000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(3000)
        ));

        String result = """
            [{"tax":0.00},{"tax":0.00},{"tax":1000.00}]
        """.trim();
        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

    @Test // Caso #4
    public void shouldOperateWithNoLossAndNoProfit() throws JsonProcessingException {

        var capitalGainService = new CapitalGainService(configMock);

        List<OperationTax> taxes = capitalGainService.calculate(List.of(
                StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
                StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(25.00)).quantity(5000),
                StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(10000)
        ));

        String result = """
            [{"tax":0.00},{"tax":0.00},{"tax":0.00}]
        """.trim();
        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

    @Test // Caso #5
    public void shouldOperateWithNoLossAndNoProfitxx() throws JsonProcessingException {

        var capitalGainService = new CapitalGainService(configMock);

        List<OperationTax> taxes = capitalGainService.calculate(List.of(
            StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(25.00)).quantity(5000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(10000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(5000)
        ));

        String result = """
            [{"tax":0.00},{"tax":0.00},{"tax":0.00},{"tax":10000.00}]
        """.trim();
        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

    @Test // Caso #5
    public void shouldPayTaxAfterGetProfitAndAfterNoLossAndNoProfit() throws JsonProcessingException {

        var capitalGainService = new CapitalGainService(configMock);

        List<OperationTax> taxes = capitalGainService.calculate(List.of(
            StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(25.00)).quantity(5000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(10000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(5000)
        ));

        String result = """
            [{"tax":0.00},{"tax":0.00},{"tax":0.00},{"tax":10000.00}]
        """.trim();

        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

    @Test // Caso #6 // TODO: UM NOME MELHOR
    public void shouldDAR_UM_NOM_MELHOR_PRA_ESSE() throws JsonProcessingException {

        var capitalGainService = new CapitalGainService(configMock);

        List<OperationTax> taxes = capitalGainService.calculate(List.of(
            StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(2)).quantity(5000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(1000)
        ));

        String result = """
            [{"tax":0.00},{"tax":0.00},{"tax":0.00},{"tax":0.00},{"tax":3000.00}]
        """.trim();

        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

    @Test // Caso #7
    public void shouldDAR_UM_NOM_MELHOR_PRA_ESSE2() throws JsonProcessingException {

        var capitalGainService = new CapitalGainService(configMock);

        List<OperationTax> taxes = capitalGainService.calculate(List.of(
            StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(2.00)).quantity(5000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(20.00)).quantity(2000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(25.00)).quantity(1000),
            StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(20.00)).quantity(10000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(15.00)).quantity(5000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(30.00)).quantity(4350),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(30.00)).quantity(650)
        ));

        //todo: DEVE PODER ACEITAR JSON COM ESPAÇOS TBM
        String result = """
            [{"tax":0.00},{"tax":0.00},{"tax":0.00},{"tax":0.00},{"tax":3000.00},{"tax":0.00},{"tax":0.00},{"tax":3700.00},{"tax":0.00}]
        """.trim();

        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

    @Test // Caso #8
    public void shouldDAR_UM_NOM_MELHOR_PRA_ESSE3() throws JsonProcessingException {

        var capitalGainService = new CapitalGainService(configMock);

        List<OperationTax> taxes = capitalGainService.calculate(List.of(
            StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(10.00)).quantity(10000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(50.00)).quantity(10000),
            StockMarketOperation.operation(BUY).unitCost(BigDecimal.valueOf(20.00)).quantity(10000),
            StockMarketOperation.operation(SELL).unitCost(BigDecimal.valueOf(50.00)).quantity(10000)
        ));

        //todo: DEVE PODER ACEITAR JSON COM ESPAÇOS TBM
        String result = """
            [{"tax":0.00},{"tax":80000.00},{"tax":0.00},{"tax":60000.00}]
        """.trim();

        Assertions.assertThat(new ObjectMapper().writeValueAsString(taxes)).isEqualTo(result);

    }

}