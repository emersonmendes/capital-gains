package br.com.emersonmendes.capitalgain.service.processor;

import br.com.emersonmendes.capitalgain.dto.Operation;

import java.math.BigDecimal;

public class OperationContext {

    private BigDecimal weightedAvgCost;
    private BigDecimal totalShares;
    private BigDecimal loss;

    public OperationContext(Operation firstOperation) {
        this.weightedAvgCost = firstOperation.unitCost();
        this.totalShares = BigDecimal.valueOf(firstOperation.quantity());
        this.loss = BigDecimal.ZERO;
    }

    public BigDecimal getWeightedAvgCost() {
        return weightedAvgCost;
    }

    public void updateWeightedAvgCost(BigDecimal newWeightedAvgCost) {
        this.weightedAvgCost = newWeightedAvgCost;
    }

    public BigDecimal getTotalShares() {
        return totalShares;
    }

    public void updateTotalShares(BigDecimal totalShares) {
        this.totalShares = totalShares;
    }

    public BigDecimal getLoss() {
        return loss;
    }

    public void updateLoss(BigDecimal loss) {
        this.loss = loss;
    }

    public void clearLoss() {
        updateLoss(BigDecimal.ZERO);
    }

}
