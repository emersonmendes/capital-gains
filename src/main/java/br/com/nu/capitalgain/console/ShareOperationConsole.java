package br.com.nu.capitalgain.console;

import br.com.nu.capitalgain.dto.ShareOperation;
import br.com.nu.capitalgain.processor.ShareOperationContext;
import br.com.nu.capitalgain.service.ShareOperationService;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;

public class ShareOperationConsole {

    private final ShareOperationService shareOperationService;
    private final JsonMapper jsonMapper;

    public ShareOperationConsole(ShareOperationService shareOperationService, JsonMapper jsonMapper) {
        this.shareOperationService = shareOperationService;
        this.jsonMapper = jsonMapper;
    }

    public void start(String... args) {
        if(args.length > 0) {
            processArgs(args);
            return;
        }
        processStdin();
    }

    private void processStdin() {
        try (
            final var reader = new BufferedReader(new InputStreamReader(System.in, UTF_8));
            final var executor = newVirtualThreadPerTaskExecutor();
        ) {
            final var jsonBuffer = new StringBuilder();
            int charCode;

            while ((charCode = reader.read()) != -1) {
                final var currentChar = (char) charCode;
                jsonBuffer.append(currentChar);
                if (currentChar == ']') {
                    final var json = jsonBuffer.toString();
                    jsonBuffer.setLength(0);
                    runAsync(() -> processJson(json), executor);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading input", e);
        }
    }

    private void processArgs(String[] args) {
        try (var executor = newVirtualThreadPerTaskExecutor()){
            Stream.of(args)
                .flatMap(arg -> Arrays.stream(splitJson(arg)))
                .map(json -> runAsync(() -> processJson(json), executor))
                .forEach(CompletableFuture::join);
        }
    }

    private void processJson(String json) {
        final var operations = jsonMapper.readList(json, new TypeReference<List<ShareOperation>>() {});
        final var context = new ShareOperationContext(operations.getFirst());
        final var taxes = shareOperationService.calculate(operations, context);
        synchronized (System.out) {
            jsonMapper.writeValue(System.out, taxes);
            System.out.println();
        }
    }

    private static String[] splitJson(String input) {
        return input.split("(?<=])\\s*(?=\\[)", -1);
    }

}
