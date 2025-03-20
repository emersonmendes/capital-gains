package br.com.emersonmendes.capitalgain.service.processor;

import br.com.emersonmendes.capitalgain.dto.OperationTax;
import br.com.emersonmendes.capitalgain.dto.Operation;

public interface OperationProcessor {

    OperationTax process(Operation operation, OperationContext context);

}
