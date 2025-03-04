package br.com.nu.capitalgain.console;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static br.com.nu.capitalgain.utils.JsonUtils.splitJsonArrays;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;

public class ArgsInputReader implements InputReader {

    private final OperationConsole console;
    private final String[] args;

    public ArgsInputReader(OperationConsole console, String[] args) {
        this.console = console;
        this.args = args;
    }

    @Override
    public void read() {
        try (var executor = newVirtualThreadPerTaskExecutor()) {
            Stream.of(args)
                .flatMap(arg -> Arrays.stream(splitJsonArrays(arg)))
                .map(json -> runAsync(() -> console.processJson(json), executor))
                .forEach(CompletableFuture::join);
        }
    }

}
