package br.com.nu.capitalgain.console;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.dto.StockOperation;
import br.com.nu.capitalgain.service.StockOperationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

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

    public String start(String[] args) {
        try {

            String jsonInput;

            if(args.length > 0) {
                jsonInput = args[0];
            } else {

                InputStream inputStream = System.in;
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
                jsonInput = scanner.useDelimiter("\\A").next();
                scanner.close();
            }


            List<StockOperation> operations = objectMapper.readValue(jsonInput, new TypeReference<>() {});
            var taxes = stockOperationService.calculate(operations);
            return objectMapper.writeValueAsString(taxes);

        } catch (JsonProcessingException e) {
            // TODO: CRIAR EXCEPTION????
            throw new RuntimeException("Could not parse JSON", e);
        }
    }

}
