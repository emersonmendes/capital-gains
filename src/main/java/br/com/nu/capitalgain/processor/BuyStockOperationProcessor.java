package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.StockOperation;

import java.math.BigDecimal;

import static java.math.RoundingMode.UP;

public class BuyStockOperationProcessor implements StockOperationProcessor{

    @Override
    public OperationTax process(StockOperation operation, StockOperationContext context) {

        BigDecimal totalShares = context.getTotalShares();
        BigDecimal newShares = BigDecimal.valueOf(operation.quantity());

        BigDecimal newWeightedAvgCost = totalShares
            .multiply(context.getWeightedAvgCost())
            .add(newShares.multiply(operation.unitCost()))
            .divide(totalShares.add(newShares), UP);

        context.updateWeightedAvgCost(newWeightedAvgCost);

        return OperationTax.ofZero();

    }

}
