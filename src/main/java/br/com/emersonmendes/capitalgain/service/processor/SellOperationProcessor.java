package br.com.emersonmendes.capitalgain.service.processor;

import br.com.emersonmendes.capitalgain.config.ConfigLoader;
import br.com.emersonmendes.capitalgain.dto.OperationTax;
import br.com.emersonmendes.capitalgain.dto.Operation;

import java.math.BigDecimal;
import java.util.Objects;

import static java.math.RoundingMode.UP;

public class SellOperationProcessor implements OperationProcessor {

    private final BigDecimal taxExemptThreshold;
    private final BigDecimal taxRate;

    public SellOperationProcessor(ConfigLoader config){
        Objects.requireNonNull(config, "ConfigLoader cannot be null");
        taxExemptThreshold = config.getBigDecimalProp("tax.exempt.threshold");
        taxRate = config.getBigDecimalProp("tax.rate");
    }

    @Override
    public OperationTax process(Operation operation, OperationContext context) {

        final var weightedAvgCost = context.getWeightedAvgCost();
        final var newShares = BigDecimal.valueOf(operation.quantity());
        final var totalCost = newShares.multiply(operation.unitCost());

        context.updateTotalShares(context.getTotalShares().subtract(newShares));

        var capitalGain = totalCost
            .subtract(weightedAvgCost.multiply(newShares))
            .subtract(context.getLoss());

        context.clearLoss();

        if(isNegative(capitalGain)){
            context.updateLoss(capitalGain.negate());
            capitalGain = BigDecimal.ZERO;
        }

        if(isTaxable(operation, weightedAvgCost)){
            return applyTax(capitalGain);
        }

        return OperationTax.ofZero();

    }

    private OperationTax applyTax(BigDecimal capitalGain) {
        final var tax = capitalGain.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, UP);
        return OperationTax.of(tax);
    }

    private static boolean isNegative(BigDecimal value) {
        return value.signum() < 0;
    }

    private boolean isTaxable(Operation operation, BigDecimal weightedAvgCost) {

        final var unitCost = operation.unitCost();
        final var newShares = BigDecimal.valueOf(operation.quantity());
        final var totalCost = newShares.multiply(unitCost);

        if(isGreaterThanOrEqual(taxExemptThreshold, totalCost)){
            return false;
        }

        return isGreaterThanOrEqual(unitCost, weightedAvgCost);

    }

    private boolean isGreaterThanOrEqual(BigDecimal firstValue, BigDecimal secondValue) {
        return firstValue.compareTo(secondValue) >= 0;
    }

}
