package br.com.nu.capitalgain.service.processor;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationType;

import java.util.HashMap;
import java.util.Map;

public class OperationProcessorFactory {

    private static final Map<OperationType, OperationProcessor> processors = new HashMap<>();

    public OperationProcessorFactory(ConfigLoader config){
        processors.putIfAbsent(OperationType.BUY, new BuyOperationProcessor());
        processors.putIfAbsent(OperationType.SELL, new SellOperationProcessor(config));
    }

    public OperationProcessor createProcessor(OperationType type){
        final var processor = processors.get(type);
        if(processor == null){
            throw new IllegalArgumentException("Type not found for operation: " + type);
        }
        return processor;
    }

}
