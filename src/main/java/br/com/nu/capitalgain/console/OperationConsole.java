package br.com.nu.capitalgain.console;

import br.com.nu.capitalgain.console.reader.InputReaderFactory;
import br.com.nu.capitalgain.dto.Operation;
import br.com.nu.capitalgain.service.processor.OperationContext;
import br.com.nu.capitalgain.service.OperationService;
import br.com.nu.capitalgain.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class OperationConsole {

    private final OperationService operationService;
    private final InputReaderFactory inputReaderFactory;

    public OperationConsole(OperationService operationService, InputReaderFactory inputReaderFactory) {
        this.operationService = operationService;
        this.inputReaderFactory = inputReaderFactory;
    }

    public void start(String... args) {
        final var reader = inputReaderFactory.createReader(this, args);
        reader.read();
    }

    public void processJson(String json) {
        final var operations = JsonUtils.readList(json, new TypeReference<List<Operation>>() {});
        final var context = new OperationContext(operations.getFirst());
        final var taxes = operationService.calculate(operations, context);
        synchronized (System.out) {
            JsonUtils.writeValue(System.out, taxes);
            System.out.println();
        }
    }

}
