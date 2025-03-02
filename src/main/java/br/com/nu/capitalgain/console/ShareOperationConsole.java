package br.com.nu.capitalgain.console;

import br.com.nu.capitalgain.dto.ShareOperation;
import br.com.nu.capitalgain.processor.ShareOperationContext;
import br.com.nu.capitalgain.service.ShareOperationService;
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

public class ShareOperationConsole {

    private final ShareOperationService shareOperationService;
    private final ObjectMapper objectMapper;

    public ShareOperationConsole(
        ShareOperationService shareOperationService,
        ObjectMapper objectMapper
    ) {
        this.shareOperationService = shareOperationService;
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

    private String calculate(String input) {
        try {
            List<ShareOperation> operations = objectMapper.readValue(input, new TypeReference<>() {});
            final var context = new ShareOperationContext(operations.getFirst());
            final var taxes = shareOperationService.calculate(operations, context);
            return objectMapper.writeValueAsString(taxes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not process json", e);
        }
    }

}
