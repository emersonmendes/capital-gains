package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.StockOperation;

public interface StockOperationProcessor {

    OperationTax proccess(StockOperation operation, StockOperationContext context);

}
