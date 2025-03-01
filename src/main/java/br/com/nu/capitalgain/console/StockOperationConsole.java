package br.com.nu.capitalgain.console;

import br.com.nu.capitalgain.dto.StockOperation;
import br.com.nu.capitalgain.service.StockOperationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
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
        try {
            if(args.length > 0) {
                return processArgs(args);
            } else {
                return processStdin();
            }
        } catch (Exception e) {
            // TODO: CRIAR EXCEPTION????
            throw new RuntimeException("Could not parse JSON", e);
        }
    }

    private String processArgs(String[] args) {
        return processLines(Stream.of(args));
    }

    private String processStdin() {

        InputStream inputStream = System.in;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            String input = scanner.useDelimiter("\\A").next();
            Stream<String> linesStream = Arrays.stream(input.split("(?<=])\\s*(?=\\[)", -1));
            return processLines(linesStream);

        } catch (Exception e) {
            // TODO: CRIAR EXCEPTION????
            throw new RuntimeException("Could do something", e);
        }
    }

    private String processLines(Stream<String> stream) {
        return stream
            .parallel()
            .map(this::calculate)
            .collect(Collectors.joining("\n"));
    }

    private String calculate(String jsonInput) {
        try {
            List<StockOperation> operations = objectMapper.readValue(jsonInput, new TypeReference<>() {});
            var taxes = stockOperationService.calculate(operations);
            return objectMapper.writeValueAsString(taxes);
        } catch (Exception e) {
            // TODO: CRIAR EXCEPTION????
            throw new RuntimeException("Could not parse JSON", e);
        }
    }

}
