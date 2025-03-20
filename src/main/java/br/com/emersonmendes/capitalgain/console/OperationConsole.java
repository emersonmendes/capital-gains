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

    private final InputReaderFactory inputReaderFactory;

    public OperationConsole(InputReaderFactory inputReaderFactory) {
        this.inputReaderFactory = inputReaderFactory;
    }

    public void start(String... args) {
        final var reader = inputReaderFactory.createReader(args);
        reader.read(OperationConsole::printTaxes);
    }

    private static void printTaxes(List<OperationTax> taxes) {
        synchronized (System.out) {
            JsonUtils.writeValue(System.out, taxes);
            System.out.println();
        }
    }

}
