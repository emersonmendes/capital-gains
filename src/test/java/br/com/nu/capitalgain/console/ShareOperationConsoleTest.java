package br.com.nu.capitalgain.console;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.service.ShareOperationService;
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

public class ShareOperationConsoleTest {

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
        var shareOperationServiceMock =  mock(ShareOperationService.class);
        var shareOperationConsole = new ShareOperationConsole(shareOperationServiceMock, new JsonMapper());

        when(shareOperationServiceMock.calculate(anyList(), any())).thenReturn(List.of(
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
        shareOperationConsole.start(line1, line2);

        // Assert
        Assertions.assertThat(outContent.toString()).isEqualTo("""
        [{"tax":0.00},{"tax":10000.00}]
        [{"tax":0.00},{"tax":10000.00}]
        """);

    }

    @Test
    public void shouldProcessMultiLinesViaStdin() {

        // Arrange
        var shareOperationServiceMock = mock(ShareOperationService.class);

        when(shareOperationServiceMock.calculate(anyList(), any())).thenReturn(List.of(
            OperationTax.ofZero(), OperationTax.of(BigDecimal.valueOf(10_000.00))
        ));

        var shareOperationConsole = new ShareOperationConsole(shareOperationServiceMock, new JsonMapper());

        String line = """
            [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},
            {"operation":"sell", "unit-cost":20.00, "quantity": 5000}]
            [{"operation":"buy", "unit-cost":20.00, "quantity": 10000},
            {"operation":"sell", "unit-cost":10.00, "quantity": 5000}]
        """;

        System.setIn(new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8)));

        // Act
        shareOperationConsole.start();

        // Assert
        Assertions.assertThat(outContent.toString()).isEqualTo("""
        [{"tax":0.00},{"tax":10000.00}]
        [{"tax":0.00},{"tax":10000.00}]
        """);

    }

}