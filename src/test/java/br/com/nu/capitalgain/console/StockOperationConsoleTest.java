package br.com.nu.capitalgain.console;

import br.com.nu.capitalgain.dto.OperationTax;
import br.com.nu.capitalgain.service.StockOperationService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class StockOperationConsoleTest {

    @Test // TODO: improve it
    public void shouldTestConsole() {

        var stockOperationServiceMock =  mock(StockOperationService.class);

        when(stockOperationServiceMock.calculate(any())).thenReturn(List.of(
             OperationTax.ofZero(), OperationTax.ofZero(), OperationTax.ofZero()
        ));

        var stockOperationConsole = new StockOperationConsole(stockOperationServiceMock, new ObjectMapper());

        String stdout = stockOperationConsole.start(new String[]{"""
            [{"operation":"buy", "unit-cost":10.00, "quantity": 100},
            {"operation":"sell", "unit-cost":15.00, "quantity": 50},
            {"operation":"sell", "unit-cost":15.00, "quantity": 50}]
        """});

        String result = """
        [{"tax":0.00},{"tax":0.00},{"tax":0.00}]
        """;

        Assertions.assertThat(stdout).isEqualTo(result.trim());

    }


    // TODO: TESTAR EXCEPTIONS

}