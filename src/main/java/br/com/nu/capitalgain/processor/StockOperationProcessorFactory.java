package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.enumeration.OperationType;

import java.util.HashMap;
import java.util.Map;

public class StockOperationProcessorFactory {

    private static final Map<OperationType, StockOperationProcessor> processors = new HashMap<>();

    public StockOperationProcessorFactory(ConfigLoader config){
        processors.putIfAbsent(OperationType.BUY, new BuyStockOperationProcessor());
        processors.putIfAbsent(OperationType.SELL, new SellStockOperationProcessor(config));
    }

    public StockOperationProcessor getInstance(OperationType type){
        StockOperationProcessor processor = processors.get(type);
        if(processor == null){
            throw new IllegalArgumentException("Type not found for operation: " + type);
        }
        return processor;
    }

}
