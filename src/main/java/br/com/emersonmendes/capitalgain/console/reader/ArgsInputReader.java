package br.com.emersonmendes.capitalgain.console.reader;

import br.com.emersonmendes.capitalgain.dto.Operation;
import br.com.emersonmendes.capitalgain.dto.OperationTax;
import br.com.emersonmendes.capitalgain.service.OperationService;
import br.com.emersonmendes.capitalgain.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.emersonmendes.capitalgain.utils.JsonUtils.splitJsonArrays;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;

public class ArgsInputReader implements InputReader {

    private final OperationService operationService;
    private final String[] args;

    public ArgsInputReader(OperationService operationService, String[] args) {
        this.operationService = operationService;
        this.args = args;
    }

    @Override
    public void read(Consumer<List<OperationTax>> consumer) {
        try (var executor = newVirtualThreadPerTaskExecutor()) {
            Stream.of(args)
                .flatMap(arg -> Arrays.stream(splitJsonArrays(arg)))
                .map(ArgsInputReader::parseOperations)
                .map(operations -> supplyAsync(() -> operationService.calculate(operations), executor))
                .forEach( taxes -> consumer.accept(taxes.join()));
        }
    }

    private static List<Operation> parseOperations(String json) {
        return JsonUtils.readList(json, new TypeReference<>() {});
    }

}
