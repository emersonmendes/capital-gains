package br.com.nu.capitalgain.service;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.Operation;
import br.com.nu.capitalgain.service.processor.OperationContext;
import br.com.nu.capitalgain.service.processor.OperationProcessorFactory;

import java.util.List;
import java.util.Objects;

public class OperationService {

    private final OperationProcessorFactory operationProcessorFactory;

    public OperationService(OperationProcessorFactory operationProcessorFactory) {
        this.operationProcessorFactory = operationProcessorFactory;
    }

    public List<OperationTax> calculate(List<Operation> operations, OperationContext context) {

        Objects.requireNonNull(operations, "Operations list cannot be null");
        Objects.requireNonNull(context, "OperationContext cannot be null");

        return operations.stream()
            .map(operation -> operationProcessorFactory.createProcessor(operation.type())
            .process(operation, context))
            .toList();

    }

}
