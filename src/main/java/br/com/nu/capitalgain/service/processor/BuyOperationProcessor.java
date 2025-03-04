package br.com.nu.capitalgain.service.processor;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.Operation;

import java.math.BigDecimal;

import static java.math.RoundingMode.UP;

public class BuyOperationProcessor implements OperationProcessor {

    @Override
    public OperationTax process(Operation operation, OperationContext context) {

        final var totalShares = context.getTotalShares();
        final var newShares = BigDecimal.valueOf(operation.quantity());

        var newWeightedAvgCost = totalShares
            .multiply(context.getWeightedAvgCost())
            .add(newShares.multiply(operation.unitCost()))
            .divide(totalShares.add(newShares), UP);

        context.updateWeightedAvgCost(newWeightedAvgCost);

        return OperationTax.ofZero();

    }

}
