package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.dto.ShareOperation;

import java.math.BigDecimal;

public class ShareOperationContext {

    private BigDecimal weightedAvgCost;
    private BigDecimal totalShares;
    private BigDecimal loss;

    public ShareOperationContext(ShareOperation firstOperation) {
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
