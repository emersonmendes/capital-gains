package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.StockOperation;
import br.com.nu.capitalgain.dto.enumeration.OperationType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BuyStockOperationProcessorTest {

    @Test
    void shouldReturnZeroTaxWhenDoABuyStockOperation() {
        BuyStockOperationProcessor processor = new BuyStockOperationProcessor();
        StockOperation operation = StockOperation.operation(OperationType.BUY).unitCost(BigDecimal.ONE).quantity(1000);
        OperationTax operationTax = processor.proccess(operation, new StockOperationContext(operation));
        Assertions.assertThat(operationTax).isNotNull();
        Assertions.assertThat(operationTax.tax()).isEqualTo(OperationTax.ofZero().tax());
    }

}