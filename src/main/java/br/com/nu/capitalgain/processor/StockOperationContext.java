package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.dto.StockOperation;

import java.math.BigDecimal;

public class StockOperationContext {

    private BigDecimal currentWap;
    private BigDecimal currentStocks;
    private BigDecimal loss;

    public StockOperationContext(final StockOperation firstOperation) {
        this.currentWap = firstOperation.unitCost();
        this.currentStocks = BigDecimal.valueOf(firstOperation.quantity());
        this.loss = BigDecimal.ZERO;
    }

    public BigDecimal getCurrentWap() {
        return currentWap;
    }

    public void setCurrentWap(BigDecimal currentWap) {
        this.currentWap = currentWap;
    }

    public BigDecimal getCurrentStocks() {
        return currentStocks;
    }

    public void setCurrentStocks(BigDecimal currentStocks) {
        this.currentStocks = currentStocks;
    }

    public BigDecimal getLoss() {
        return loss;
    }

    public void setLoss(BigDecimal loss) {
        this.loss = loss;
    }

    public void clearLoss() {
        setLoss(BigDecimal.ZERO);
    }

}
