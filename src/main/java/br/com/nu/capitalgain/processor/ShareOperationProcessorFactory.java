package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationType;

import java.util.HashMap;
import java.util.Map;

public class ShareOperationProcessorFactory {

    private static final Map<OperationType, ShareOperationProcessor> processors = new HashMap<>();

    public ShareOperationProcessorFactory(ConfigLoader config){
        processors.putIfAbsent(OperationType.BUY, new BuyShareOperationProcessor());
        processors.putIfAbsent(OperationType.SELL, new SellShareOperationProcessor(config));
    }

    public ShareOperationProcessor getInstance(OperationType type){
        var processor = processors.get(type);
        if(processor == null){
            throw new IllegalArgumentException("Type not found for operation: " + type);
        }
        return processor;
    }

}
