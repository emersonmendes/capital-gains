package br.com.emersonmendes;

import br.com.emersonmendes.capitalgain.config.ConfigLoader;
import br.com.emersonmendes.capitalgain.console.reader.InputReaderFactory;
import br.com.emersonmendes.capitalgain.console.OperationConsole;
import br.com.emersonmendes.capitalgain.service.processor.OperationProcessorFactory;
import br.com.emersonmendes.capitalgain.service.OperationService;

public class CapitalGainApplication {

    public static void main(String[] args) {
        final var config = new ConfigLoader("config");
        final var operationProcessorFactory = new OperationProcessorFactory(config);
        final var operationService = new OperationService(operationProcessorFactory);
        final var inputReaderFactory = new InputReaderFactory();
        final var operationConsole = new OperationConsole(operationService, inputReaderFactory);
        operationConsole.start(args);
    }

}