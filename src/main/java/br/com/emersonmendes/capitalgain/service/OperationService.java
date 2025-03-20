package br.com.emersonmendes.capitalgain.service;

import br.com.emersonmendes.capitalgain.dto.OperationTax;
import br.com.emersonmendes.capitalgain.dto.Operation;
import br.com.emersonmendes.capitalgain.service.processor.OperationContext;
import br.com.emersonmendes.capitalgain.service.processor.OperationProcessorFactory;
import br.com.emersonmendes.capitalgain.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Objects;

public class OperationService {

    private final OperationProcessorFactory operationProcessorFactory;

    public OperationService(OperationProcessorFactory operationProcessorFactory) {
        this.operationProcessorFactory = operationProcessorFactory;
    }

    public List<OperationTax> calculate(List<Operation> operations) {

        Objects.requireNonNull(operations, "Operations list cannot be null");

        final var context = new OperationContext(operations.getFirst());

        return operations.stream()
            .map(operation -> operationProcessorFactory.createProcessor(operation.type())
            .process(operation, context))
            .toList();

    }

}
