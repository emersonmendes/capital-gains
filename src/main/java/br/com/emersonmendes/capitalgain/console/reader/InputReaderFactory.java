package br.com.emersonmendes.capitalgain.console.reader;

import br.com.emersonmendes.capitalgain.console.OperationConsole;

public class InputReaderFactory {

    public InputReader createReader(OperationConsole console, String... args) {
        if(args.length > 0){
            return new ArgsInputReader(console, args);
        }
        return new StdinReader(console);
    }

}