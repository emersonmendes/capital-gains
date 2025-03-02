package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.ShareOperation;

import java.math.BigDecimal;

import static java.math.RoundingMode.UP;

public class SellShareOperationProcessor implements ShareOperationProcessor {

    private final BigDecimal taxExemptThreshold;
    private final BigDecimal taxRate;

    public SellShareOperationProcessor(ConfigLoader config){
        taxExemptThreshold = config.getBigDecimalProp("tax.exempt.threshold");
        taxRate = config.getBigDecimalProp("tax.rate");
    }

    @Override
    public OperationTax process(ShareOperation operation, ShareOperationContext context) {

        BigDecimal weightedAvgCost = context.getWeightedAvgCost();
        BigDecimal newShares = BigDecimal.valueOf(operation.quantity());

        final BigDecimal totalCost = newShares.multiply(operation.unitCost());

        context.updateTotalShares(context.getTotalShares().subtract(newShares));

        BigDecimal capitalGain = totalCost
            .subtract(weightedAvgCost.multiply(newShares))
            .subtract(context.getLoss());

        context.clearLoss();

        if(isNegative(capitalGain)){
            context.updateLoss(capitalGain.negate());
            capitalGain = BigDecimal.ZERO;
        }

        if(isTaxableSale(operation, weightedAvgCost)){
            return applyTax(capitalGain);
        }

        return OperationTax.ofZero();

    }

    private OperationTax applyTax(BigDecimal capitalGain) {
        BigDecimal tax = capitalGain.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, UP);
        return OperationTax.of(tax);
    }

    private static boolean isNegative(BigDecimal value) {
        return value.signum() < 0;
    }

    private boolean isTaxableSale(ShareOperation operation, BigDecimal weightedAvgCost) {

        BigDecimal unitCost = operation.unitCost();
        BigDecimal newShares = BigDecimal.valueOf(operation.quantity());
        BigDecimal totalCost = newShares.multiply(unitCost);

        if(isGreaterThanOrEqual(taxExemptThreshold, totalCost)){
            return false;
        }

        return isGreaterThanOrEqual(unitCost, weightedAvgCost);

    }

    private boolean isGreaterThanOrEqual(BigDecimal firstValue, BigDecimal secondValue) {
        return firstValue.compareTo(secondValue) >= 0;
    }

}
