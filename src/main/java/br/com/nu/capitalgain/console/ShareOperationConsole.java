package br.com.nu.capitalgain.console;

import br.com.nu.capitalgain.dto.ShareOperation;
import br.com.nu.capitalgain.processor.ShareOperationContext;
import br.com.nu.capitalgain.service.ShareOperationService;
import com.fasterxml.jackson.core.type.TypeReference;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.runAsync;

public class ShareOperationConsole {

    private final ShareOperationService shareOperationService;
    private final JsonMapper jsonMapper;

    public ShareOperationConsole(ShareOperationService shareOperationService, JsonMapper jsonMapper) {
        this.shareOperationService = shareOperationService;
        this.jsonMapper = jsonMapper;
    }

    public void start(String... args) {
        if(args.length > 0) {
            processLines(args);
            return;
        }
        processStdin();
    }

    private void processStdin() {
        try (var scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            var input = scanner.useDelimiter("\\A").next();
            var lines = splitJson(input);
            processLines(lines);
        }
    }

    private void processLines(String[] lines) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()){
            Stream.of(lines)
                .flatMap(line -> Arrays.stream(splitJson(line)))
                .map(json -> runAsync(() -> processJson(json), executor))
                .forEach(CompletableFuture::join);
        }
    }

    private void processJson(String json) {
        List<ShareOperation> operations = jsonMapper.readList(json, new TypeReference<>() {});
        final var context = new ShareOperationContext(operations.getFirst());
        final var taxes = shareOperationService.calculate(operations, context);
        jsonMapper.writeValue(System.out, taxes);
        System.out.println();
    }

    private static String[] splitJson(String input) {
        return input.split("(?<=])\\s*(?=\\[)", -1);
    }

}
