package br.com.emersonmendes.capitalgain.dto;

import br.com.emersonmendes.capitalgain.dto.enumeration.Operation;

import java.math.BigDecimal;
import java.util.Objects;

public record StockMarketOperation(
    Operation operation,
    BigDecimal unitCost,
    long quantity
) {

    public StockMarketOperation {
        Objects.requireNonNull(operation, "Operation is required!");
        Objects.requireNonNull(unitCost, "Unit cost is required!");
    }

    public static OperationStep operation(Operation operation) {
        return new Builder(operation);
    }

    public interface OperationStep {
        UnitCostStep unitCost(BigDecimal unitCost);
    }

    public interface UnitCostStep {
        StockMarketOperation quantity(long quantity);
    }


    private static class Builder implements OperationStep, UnitCostStep {

        private final Operation operation;
        private BigDecimal unitCost;
        private long quantity;

        public Builder(Operation operation) {
            this.operation = operation;
        }

        @Override
        public UnitCostStep unitCost(BigDecimal unitCost) {
            this.unitCost = unitCost;
            return this;
        }

        @Override
        public StockMarketOperation quantity(long quantity) {
            this.quantity = quantity;
            return new StockMarketOperation(
                this.operation,
                this.unitCost,
                this.quantity
            );
        }

    }

}
