package br.com.emersonmendes.capitalgain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public record Operation(
    @JsonProperty("operation")
    OperationType type,
    @JsonProperty("unit-cost")
    BigDecimal unitCost,
    long quantity
) {

    public Operation {
        Objects.requireNonNull(type, "Operation is required!");
        Objects.requireNonNull(unitCost, "Unit cost is required!");
    }

    public static OperationStep operation(OperationType type) {
        return new Builder(type);
    }

    public interface OperationStep {
        UnitCostStep unitCost(BigDecimal unitCost);
    }

    public interface UnitCostStep {
        Operation quantity(long quantity);
    }

    private static class Builder implements OperationStep, UnitCostStep {

        private final OperationType type;
        private BigDecimal unitCost;

        public Builder(OperationType type) {
            this.type = type;
        }

        @Override
        public UnitCostStep unitCost(BigDecimal unitCost) {
            this.unitCost = unitCost;
            return this;
        }

        @Override
        public Operation quantity(long quantity) {
            return new Operation(this.type, this.unitCost, quantity);
        }

    }

}
