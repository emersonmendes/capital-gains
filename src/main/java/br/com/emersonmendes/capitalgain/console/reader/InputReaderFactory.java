package br.com.emersonmendes.capitalgain.console.reader;

import br.com.emersonmendes.capitalgain.service.OperationService;

public class InputReaderFactory {

    private final OperationService operationService;

    public InputReaderFactory(OperationService operationService) {
        this.operationService = operationService;
    }

    public InputReader createReader(String... args) {
        if(args.length > 0){
            return new ArgsInputReader(operationService, args);
        }
        return new StdinReader(operationService);
    }

}