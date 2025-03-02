package br.com.nu.capitalgain.console;

import br.com.nu.capitalgain.dto.StockOperation;
import br.com.nu.capitalgain.service.StockOperationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StockOperationConsole {

    private final StockOperationService stockOperationService;
    private final ObjectMapper objectMapper;

    public StockOperationConsole(
        StockOperationService stockOperationService,
        ObjectMapper objectMapper
    ) {
        this.stockOperationService = stockOperationService;
        this.objectMapper = objectMapper;
    }

    public String start(String... args) {
        if(args.length > 0) {
            return processLines(args);
        } else {
            return processStdin();
        }
    }

    private String processStdin() {
        InputStream inputStream = System.in;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            String input = scanner.hasNext() ? scanner.useDelimiter("\\A").next() : "";
            String[] lines = input.split("(?<=])\\s*(?=\\[)", -1);
            return processLines(lines);
        }
    }

    private String processLines(String[] lines) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()){
            List<CompletableFuture<String>> futures = Stream.of(lines)
                .map(line -> CompletableFuture.supplyAsync(() -> calculate(line), executor))
                .toList();
            return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.joining("\n"));
        }
    }

    private String calculate(String jsonInput) {
        try {
            List<StockOperation> operations  = objectMapper.readValue(jsonInput, new TypeReference<>() {});
            var taxes = stockOperationService.calculate(operations);
            return objectMapper.writeValueAsString(taxes);
        } catch (JsonProcessingException e) {
            //TODO: improve Exception feedback
            throw new RuntimeException("Could not process json", e);
        }
    }

}
