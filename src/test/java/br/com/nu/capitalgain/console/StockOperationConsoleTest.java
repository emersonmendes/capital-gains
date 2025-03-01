package br.com.nu.capitalgain.console;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.service.StockOperationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class StockOperationConsoleTest {

    @Test
    public void shouldProcessMultiLinesViaArguments() {

        var stockOperationServiceMock =  mock(StockOperationService.class);

        when(stockOperationServiceMock.calculate(anyList())).thenReturn(List.of(
            OperationTax.ofZero(), OperationTax.of(BigDecimal.valueOf(10_000.00))
        ));

        var stockOperationConsole = new StockOperationConsole(stockOperationServiceMock, new ObjectMapper());

        String line1 = """
            [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},
            {"operation":"sell", "unit-cost":20.00, "quantity": 5000}]
        """;

        String line2 = """
            [{"operation":"buy", "unit-cost":20.00, "quantity": 10000},
            {"operation":"sell", "unit-cost":10.00, "quantity": 5000}]
        """;

        String stdout = stockOperationConsole.start(new String[]{line1, line2});

        String result = """
        [{"tax":0.00},{"tax":10000.00}]
        [{"tax":0.00},{"tax":10000.00}]""";

        Assertions.assertThat(stdout).isEqualTo(result);

    }

    @Test
    public void shouldProcessMultiLinesViaStdin() {

        var stockOperationServiceMock =  mock(StockOperationService.class);

        when(stockOperationServiceMock.calculate(anyList())).thenReturn(List.of(
            OperationTax.ofZero(), OperationTax.of(BigDecimal.valueOf(10_000.00))
        ));

        var stockOperationConsole = new StockOperationConsole(stockOperationServiceMock, new ObjectMapper());

        String line = """
            [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},{"operation":"sell", "unit-cost":20.00, "quantity": 5000}]
            [{"operation":"buy", "unit-cost":20.00, "quantity": 10000},{"operation":"sell", "unit-cost":10.00, "quantity": 5000}]
        """;

        System.setIn(new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8)));

        String stdout = stockOperationConsole.start();

        String result = """
        [{"tax":0.00},{"tax":10000.00}]
        [{"tax":0.00},{"tax":10000.00}]""";

        Assertions.assertThat(stdout).isEqualTo(result);

    }


    // TODO: TESTAR EXCEPTIONS

}