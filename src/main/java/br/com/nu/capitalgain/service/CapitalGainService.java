package br.com.nu.capitalgain.service;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.StockMarketOperation;
import br.com.nu.capitalgain.dto.enumeration.Operation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.RoundingMode.UP;

public class CapitalGainService {

    private final BigDecimal taxExemptAmountMax;
    private final BigDecimal taxPaidPercentage;

    public CapitalGainService(ConfigLoader config) {
        taxExemptAmountMax = config.getBigDecimalProp("tax.exempt.amount.max");
        taxPaidPercentage = config.getBigDecimalProp("tax.paid.percentage");
    }

    public List<OperationTax> calculate(List<StockMarketOperation> operations) {

        final StockMarketOperation firstOperation = operations.getFirst();

        BigDecimal currentWap = firstOperation.unitCost();
        BigDecimal currentStocks = BigDecimal.valueOf(firstOperation.quantity());

        final List<OperationTax> taxes = new ArrayList<>();
        BigDecimal loss = BigDecimal.ZERO;

        for (StockMarketOperation stockMarketOperation : operations) {
            BigDecimal stocks = BigDecimal.valueOf(stockMarketOperation.quantity());
            BigDecimal unitCost = stockMarketOperation.unitCost();

            Operation operation = stockMarketOperation.operation();
            final BigDecimal totalCost = stocks.multiply(unitCost);

            if(operation.equals(Operation.BUY)){
                currentWap = currentStocks.multiply(currentWap)
                    .add(totalCost)
                    .divide(currentStocks.add(stocks), UP);
                taxes.add(OperationTax.ofZero());
            } else {

                currentStocks = currentStocks.subtract(stocks);

                BigDecimal profit = totalCost.subtract(currentWap.multiply(stocks));

                profit = profit.subtract(loss);
                loss = BigDecimal.ZERO;

                if(isNegative(profit)){
                    loss = profit.negate();
                    profit = BigDecimal.ZERO;
                }

                // TODO: MELHORAR TEM TAX
                if(hasTax(stocks, unitCost, currentWap)){

                    final OperationTax operationTax = OperationTax.of(profit.multiply(taxPaidPercentage).divide(BigDecimal.valueOf(100), 2 , UP));
                    taxes.add(operationTax);

                } else {
                    taxes.add(OperationTax.ofZero());
                }

            }

        }

        return taxes;

    }

    private static boolean isNegative(BigDecimal profit) {
        return profit.signum() == -1;
    }

    private boolean hasTax(BigDecimal stocks, BigDecimal unitCost, BigDecimal currentWap) {

        // TODO: VERIFICAR MELHOR NOME
        if(taxExemptAmountMax.compareTo(stocks.multiply(unitCost)) >= 0){
            return false;
        }
        //TODO:  FAZER ALGUMA COISA GREATER THAN?
        return unitCost.compareTo(currentWap) >= 0;
    }


}
