package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.StockOperation;

import java.math.BigDecimal;

import static java.math.RoundingMode.UP;

public class BuyStockOperationProcessor implements StockOperationProcessor{

    @Override
    public OperationTax proccess(StockOperation operation, StockOperationContext context) {

        BigDecimal currentStocks = context.getCurrentStocks();
        BigDecimal stocks = BigDecimal.valueOf(operation.quantity());

        BigDecimal wap = currentStocks.multiply(context.getCurrentWap())
            .add(stocks.multiply(operation.unitCost()))
            .divide(currentStocks.add(stocks), UP);

        context.updateCurrentWap(wap);

        return OperationTax.ofZero();

    }

}
