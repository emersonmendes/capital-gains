package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.Operation;

public interface OperationProcessor {

    OperationTax process(Operation operation, OperationContext context);

}
