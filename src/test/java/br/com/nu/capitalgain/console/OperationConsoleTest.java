package br.com.nu.capitalgain.console;

import br.com.nu.capitalgain.console.reader.InputReaderFactory;
import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.service.OperationService;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OperationConsoleTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void shouldProcessMultiLinesViaArguments() {

        // Arrange
        var operationServiceMock =  mock(OperationService.class);
        var inputReaderFactory = new InputReaderFactory();
        var operationConsole = new OperationConsole(operationServiceMock, inputReaderFactory);

        when(operationServiceMock.calculate(anyList(), any())).thenReturn(List.of(
            OperationTax.ofZero(), OperationTax.of(BigDecimal.valueOf(10_000.00))
        ));

        String line1 = """
            [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},
            {"operation":"sell", "unit-cost":20.00, "quantity": 5000}]
        """;

        String line2 = """
            [{"operation":"buy", "unit-cost":20.00, "quantity": 10000},
            {"operation":"sell", "unit-cost":10.00, "quantity": 5000}]
        """;

        // Act
        operationConsole.start(line1, line2);

        // Assert
        Assertions.assertThat(outContent.toString()).isEqualTo("""
        [{"tax":0.00},{"tax":10000.00}]
        [{"tax":0.00},{"tax":10000.00}]
        """);

    }

    @Test
    public void shouldProcessMultiLinesViaStdin() {

        // Arrange
        var operationServiceMock = mock(OperationService.class);

        when(operationServiceMock.calculate(anyList(), any())).thenReturn(List.of(
            OperationTax.ofZero(), OperationTax.of(BigDecimal.valueOf(10_000.00))
        ));

        var inputReaderFactory = new InputReaderFactory();
        var operationConsole = new OperationConsole(operationServiceMock, inputReaderFactory);

        String line = """
            [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},
            {"operation":"sell", "unit-cost":20.00, "quantity": 5000}]
            [{"operation":"buy", "unit-cost":20.00, "quantity": 10000},
            {"operation":"sell", "unit-cost":10.00, "quantity": 5000}]
        """;

        System.setIn(new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8)));

        // Act
        operationConsole.start();

        // Assert
        Assertions.assertThat(outContent.toString()).isEqualTo("""
        [{"tax":0.00},{"tax":10000.00}]
        [{"tax":0.00},{"tax":10000.00}]
        """);

    }

}