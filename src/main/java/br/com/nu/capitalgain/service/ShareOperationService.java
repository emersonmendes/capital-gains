package br.com.nu.capitalgain.service;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.ShareOperation;
import br.com.nu.capitalgain.processor.ShareOperationContext;
import br.com.nu.capitalgain.processor.ShareOperationProcessorFactory;

import java.util.List;

public class ShareOperationService {

    private final ShareOperationProcessorFactory shareOperationProcessorFactory;

    public ShareOperationService(ConfigLoader config) {
        shareOperationProcessorFactory = new ShareOperationProcessorFactory(config);
    }

    public List<OperationTax> calculate(List<ShareOperation> operations) {
        final ShareOperationContext context = new ShareOperationContext(operations.getFirst());
        return operations.stream()
            .map(operation -> shareOperationProcessorFactory.getInstance(operation.type())
            .process(operation, context))
            .toList();
    }

}
