package br.com.nu;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.console.reader.InputReaderFactory;
import br.com.nu.capitalgain.console.OperationConsole;
import br.com.nu.capitalgain.service.processor.OperationProcessorFactory;
import br.com.nu.capitalgain.service.OperationService;

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