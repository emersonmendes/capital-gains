package br.com.nu;

import br.com.nu.capitalgain.config.ConfigLoader;
import br.com.nu.capitalgain.console.InputReaderFactory;
import br.com.nu.capitalgain.console.OperationConsole;
import br.com.nu.capitalgain.processor.OperationProcessorFactory;
import br.com.nu.capitalgain.service.OperationService;

public class CapitalGainApplication {

    public static void main(String[] args) {
        final var config = new ConfigLoader("config");
        final var operationProcessorFactory = new OperationProcessorFactory(config);
        final var operationService = new OperationService(operationProcessorFactory);
        final var inputProcessorFactory = new InputReaderFactory();
        final var operationConsole = new OperationConsole(operationService, inputProcessorFactory);
        operationConsole.start(args);
    }

}