package br.com.nu.capitalgain.service;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.ShareOperation;
import br.com.nu.capitalgain.processor.ShareOperationContext;
import br.com.nu.capitalgain.processor.ShareOperationProcessorFactory;

import java.util.List;
import java.util.Objects;

public class ShareOperationService {

    private final ShareOperationProcessorFactory shareOperationProcessorFactory;

    public ShareOperationService(ShareOperationProcessorFactory shareOperationProcessorFactory) {
        this.shareOperationProcessorFactory = shareOperationProcessorFactory;
    }

    public List<OperationTax> calculate(List<ShareOperation> operations, ShareOperationContext context) {

        Objects.requireNonNull(operations, "Operations list cannot be null");
        Objects.requireNonNull(context, "ShareOperationContext cannot be null");

        return operations.stream()
            .map(operation -> shareOperationProcessorFactory.getInstance(operation.type())
            .process(operation, context))
            .toList();

    }

}
