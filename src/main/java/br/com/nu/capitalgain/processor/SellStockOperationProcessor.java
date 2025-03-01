package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.StockOperation;

import java.math.BigDecimal;

import static java.math.RoundingMode.UP;

public class SellStockOperationProcessor implements StockOperationProcessor {

    private final BigDecimal taxExemptAmountMax;
    private final BigDecimal taxPaidPercentage;

    public SellStockOperationProcessor(ConfigLoader config){
        taxExemptAmountMax = config.getBigDecimalProp("tax.exempt.amount.max");
        taxPaidPercentage = config.getBigDecimalProp("tax.paid.percentage");
    }

    @Override
    public OperationTax proccess(StockOperation operation, StockOperationContext context) {

        BigDecimal currentWap = context.getCurrentWap();
        BigDecimal sharesTraded = BigDecimal.valueOf(operation.quantity());

        final BigDecimal totalCost = sharesTraded.multiply(operation.unitCost());

        context.setCurrentStocks(context.getCurrentStocks().subtract(sharesTraded));

        BigDecimal profit = totalCost
            .subtract(currentWap.multiply(sharesTraded))
            .subtract(context.getLoss());

        context.clearLoss();

        if(isNegative(profit)){
            context.setLoss(profit.negate());
            profit = BigDecimal.ZERO;
        }

        if(hasTax(operation, currentWap)){
            return calcTax(profit);
        }

        return OperationTax.ofZero();

    }

    private OperationTax calcTax(BigDecimal profit) {
        BigDecimal tax = profit.multiply(taxPaidPercentage).divide(BigDecimal.valueOf(100), 2, UP);
        return OperationTax.of(tax);
    }


    private static boolean isNegative(BigDecimal value) {
        return value.signum() < 0;
    }

    private boolean hasTax(StockOperation operation, BigDecimal currentWap) {

        BigDecimal unitCost = operation.unitCost();
        BigDecimal stocks = BigDecimal.valueOf(operation.quantity());
        BigDecimal totalCost = stocks.multiply(unitCost);

        if(isGreaterThan(taxExemptAmountMax, totalCost)){
            return false;
        }

        return isGreaterThan(unitCost, currentWap);

    }

    private boolean isGreaterThan(BigDecimal fisrtValue, BigDecimal secondValue) {
        return fisrtValue.compareTo(secondValue) >= 0;
    }

}
