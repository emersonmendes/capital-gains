package br.com.emersonmendes.capitalgain.console;

import br.com.emersonmendes.capitalgain.console.reader.InputReaderFactory;
import br.com.emersonmendes.capitalgain.dto.Operation;
import br.com.emersonmendes.capitalgain.dto.OperationTax;
import br.com.emersonmendes.capitalgain.service.processor.OperationContext;
import br.com.emersonmendes.capitalgain.service.OperationService;
import br.com.emersonmendes.capitalgain.utils.JsonUtils;
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
        final var operations = parseOperations(json);
        final var taxes = calculateTaxes(operations);
        printTaxes(taxes);
    }

    private List<OperationTax> calculateTaxes(List<Operation> operations) {
        final var context = new OperationContext(operations.getFirst());
        return operationService.calculate(operations, context);
    }

    private static void printTaxes(List<OperationTax> taxes) {
        synchronized (System.out) {
            JsonUtils.writeValue(System.out, taxes);
            System.out.println();
        }
    }

    private static List<Operation> parseOperations(String json) {
        return JsonUtils.readList(json, new TypeReference<>() {});
    }

}
