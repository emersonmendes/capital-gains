package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.ShareOperation;

import java.math.BigDecimal;

import static java.math.RoundingMode.UP;

public class BuyShareOperationProcessor implements ShareOperationProcessor {

    @Override
    public OperationTax process(ShareOperation operation, ShareOperationContext context) {

        var totalShares = context.getTotalShares();
        var newShares = BigDecimal.valueOf(operation.quantity());

        var newWeightedAvgCost = totalShares
            .multiply(context.getWeightedAvgCost())
            .add(newShares.multiply(operation.unitCost()))
            .divide(totalShares.add(newShares), UP);

        context.updateWeightedAvgCost(newWeightedAvgCost);

        return OperationTax.ofZero();

    }

}
