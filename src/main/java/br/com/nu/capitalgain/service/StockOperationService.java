package br.com.nu.capitalgain.service;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.StockOperation;
import br.com.nu.capitalgain.processor.StockOperationContext;
import br.com.nu.capitalgain.processor.StockOperationProcessorFactory;

import java.util.List;

public class StockOperationService {

    private final StockOperationProcessorFactory stockOperationProcessorFactory;

    public StockOperationService(ConfigLoader config) {
        stockOperationProcessorFactory = new StockOperationProcessorFactory(config);
    }

    public List<OperationTax> calculate(List<StockOperation> operations) {
        final StockOperationContext context = new StockOperationContext(operations.getFirst());
        return operations.stream()
            .map(operation -> stockOperationProcessorFactory.getInstance(operation.type())
            .process(operation, context))
            .toList();
    }

}
