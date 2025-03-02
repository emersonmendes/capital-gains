package br.com.nu.capitalgain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public record ShareOperation(
    @JsonProperty("operation")
    OperationType type,
    @JsonProperty("unit-cost")
    BigDecimal unitCost,
    long quantity
) {

    public ShareOperation {
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
        ShareOperation quantity(long quantity);
    }

    private static class Builder implements OperationStep, UnitCostStep {

        private final OperationType type;
        private BigDecimal unitCost;
        private long quantity;

        public Builder(OperationType type) {
            this.type = type;
        }

        @Override
        public UnitCostStep unitCost(BigDecimal unitCost) {
            this.unitCost = unitCost;
            return this;
        }

        @Override
        public ShareOperation quantity(long quantity) {
            this.quantity = quantity;
            return new ShareOperation(
                this.type,
                this.unitCost,
                this.quantity
            );
        }

    }

}
