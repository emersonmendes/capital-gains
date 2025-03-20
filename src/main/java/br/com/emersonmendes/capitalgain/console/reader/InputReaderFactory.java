package br.com.emersonmendes.capitalgain.console.reader;

public class InputReaderFactory {

    public InputReader createReader(String... args) {
        if(args.length > 0){
            return new ArgsInputReader(args);
        }
        return new StdinReader();
    }

}