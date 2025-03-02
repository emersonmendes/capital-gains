package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.ShareOperation;

public interface ShareOperationProcessor {

    OperationTax process(ShareOperation operation, ShareOperationContext context);

}
