package br.com.emersonmendes.capitalgain.service.processor;

import br.com.emersonmendes.capitalgain.config.ConfigLoader;
import br.com.emersonmendes.capitalgain.dto.OperationType;

import java.util.EnumMap;
import java.util.Map;

public class OperationProcessorFactory {

    private static final Map<OperationType, OperationProcessor> processors = new EnumMap<>(OperationType.class);

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
