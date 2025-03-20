    package br.com.emersonmendes.capitalgain.console.reader;

    import br.com.emersonmendes.capitalgain.dto.Operation;
    import br.com.emersonmendes.capitalgain.dto.OperationTax;
    import br.com.emersonmendes.capitalgain.service.OperationService;
    import br.com.emersonmendes.capitalgain.utils.JsonUtils;
    import com.fasterxml.jackson.core.type.TypeReference;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.util.List;
    import java.util.function.Consumer;

    import static java.nio.charset.StandardCharsets.UTF_8;
    import static java.util.concurrent.CompletableFuture.runAsync;
    import static java.util.concurrent.CompletableFuture.supplyAsync;
    import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;

    public class StdinReader implements InputReader {

        private final OperationService operationService;

        public StdinReader(OperationService operationService) {
            this.operationService = operationService;
        }

        @Override
        public void read(Consumer<List<OperationTax>> consumer) {
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
                        List<Operation> operations = parseOperations(json);
                        List<OperationTax> taxes = supplyAsync(() -> operationService.calculate(operations), executor).join();
                        consumer.accept(taxes);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading input", e);
            }
        }

        private static List<Operation> parseOperations(String json) {
            return JsonUtils.readList(json, new TypeReference<>() {});
        }

    }
