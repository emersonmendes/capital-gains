package br.com.emersonmendes.capitalgain.console;

import br.com.emersonmendes.capitalgain.console.reader.InputReaderFactory;
import br.com.emersonmendes.capitalgain.dto.Operation;
import br.com.emersonmendes.capitalgain.dto.OperationTax;
import br.com.emersonmendes.capitalgain.service.OperationService;
import br.com.emersonmendes.capitalgain.utils.JsonUtils;

import java.util.List;
import java.util.concurrent.Executor;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;

public class OperationConsole {

    private final InputReaderFactory inputReaderFactory;
    private final OperationService operationService;

    public OperationConsole(
        OperationService operationService,
        InputReaderFactory inputReaderFactory
    ) {
        this.operationService = operationService;
        this.inputReaderFactory = inputReaderFactory;
    }

    public void start(String... args) {
        final var reader = inputReaderFactory.createReader(args);
        try (var executor = newVirtualThreadPerTaskExecutor()) {
            reader.read(operations -> calculate(operations, executor));
        }
    }

    private void calculate(List<Operation> operations, Executor executor) {
        supplyAsync(() -> operationService.calculate(operations), executor)
            .thenAcceptAsync(OperationConsole::printTaxes, executor);
    }

    private static void printTaxes(List<OperationTax> taxes) {
        synchronized (System.out) {
            JsonUtils.writeValue(System.out, taxes);
            System.out.println();
        }
    }

}
