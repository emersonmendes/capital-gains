package br.com.emersonmendes.capitalgain.service;

import br.com.emersonmendes.capitalgain.config.ConfigLoader;
import br.com.emersonmendes.capitalgain.dto.OperationTax;
import br.com.emersonmendes.capitalgain.dto.StockMarketOperation;
import br.com.emersonmendes.capitalgain.dto.enumeration.Operation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.math.RoundingMode.HALF_UP;

public class CapitalGainService {

    private final ConfigLoader config;

    public CapitalGainService(ConfigLoader config) {
        this.config = config;
    }


    public List<OperationTax> calculate(List<StockMarketOperation> operations) {

        final StockMarketOperation firstOperation = operations.getFirst();
        // Weighted Average Price
        BigDecimal currentWap = firstOperation.unitCost();

        BigDecimal currentStocks = BigDecimal.valueOf(firstOperation.quantity());

        final List<OperationTax> taxes = new ArrayList<>();
        final List<BigDecimal> losses = new ArrayList<>();

        // TODO: index só pra teste
        int index = 0;

        for (StockMarketOperation stockMarketOperation : operations) {
            index++;

            BigDecimal stocks = BigDecimal.valueOf(stockMarketOperation.quantity());
            BigDecimal unitCost = stockMarketOperation.unitCost();

            Operation operation = stockMarketOperation.operation();
            final BigDecimal totalCost = stocks.multiply(unitCost);

            if(operation.equals(Operation.BUY)){
                currentWap = currentStocks.multiply(currentWap)
                    .add(totalCost)
                    .divide(currentStocks.add(stocks), HALF_UP); // pq HALF_UP ?????
                taxes.add(OperationTax.ofZero());
            } else {

                currentStocks = currentStocks.subtract(stocks);

                BigDecimal profit = totalCost.subtract(currentWap.multiply(stocks));

                // todo: tem mais de 1 loss???
                for (BigDecimal loss : losses) {
                    profit = profit.subtract(loss);
                }

                losses.clear();

                if(isNegative(profit)){
                    losses.add(profit.negate());
                    profit = BigDecimal.ZERO;
                }

                // TODO: MELHORAR TEM TAX
                if(hasTax(stocks, unitCost, currentWap)){

                    final BigDecimal percentTax = BigDecimal.valueOf(20);

                    System.out.println(index);
                    final OperationTax operationTax = OperationTax.of(profit.multiply(percentTax).divide(BigDecimal.valueOf(100), HALF_UP));
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
        BigDecimal taxExemptAmountMax = this.config.getBigDecimalProp("tax.exempt.amount.max");
        if(taxExemptAmountMax.compareTo(stocks.multiply(unitCost)) >= 0){
            return false;
        }
        //TODO:  FAZER ALGUMA COISA GREATER THAN?
        return unitCost.compareTo(currentWap) >= 0;
    }


}
