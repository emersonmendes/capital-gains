package br.com.nu.capitalgain.service;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.StockOperation;
import br.com.nu.capitalgain.processor.StockOperationContext;
import br.com.nu.capitalgain.processor.StockOperationProcessor;
import br.com.nu.capitalgain.processor.StockOperationProcessorFactory;

import java.util.ArrayList;
import java.util.List;

public class StockOperationService {

    private final StockOperationProcessorFactory stockOperationProcessorFactory;

    public StockOperationService(ConfigLoader config) {
        stockOperationProcessorFactory = new StockOperationProcessorFactory(config);
    }

    public List<OperationTax> calculate(List<StockOperation> operations) {

        final StockOperationContext context = new StockOperationContext(operations.getFirst());
        final List<OperationTax> taxes = new ArrayList<>();

        for (StockOperation operation : operations) {
            StockOperationProcessor processor = stockOperationProcessorFactory.getInstance(operation.type());
            taxes.add(processor.proccess(operation, context));
        }

        return taxes;

    }

}
