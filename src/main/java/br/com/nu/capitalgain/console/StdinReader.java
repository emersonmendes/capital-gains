    package br.com.nu.capitalgain.console;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;

    import static java.nio.charset.StandardCharsets.UTF_8;
    import static java.util.concurrent.CompletableFuture.runAsync;
    import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;

    public class StdinReader implements InputReader {

        private final OperationConsole console;

        public StdinReader(OperationConsole console) {
            this.console = console;
        }

        @Override
        public void read() {
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
                        runAsync(() -> console.processJson(json), executor);
                        jsonBuffer.setLength(0);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading input", e);
            }
        }

    }
