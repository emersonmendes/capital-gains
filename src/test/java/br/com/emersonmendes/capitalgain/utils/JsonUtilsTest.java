package br.com.emersonmendes.capitalgain.utils;

import br.com.emersonmendes.capitalgain.dto.Operation;
import br.com.emersonmendes.capitalgain.dto.OperationTax;
import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;

import static br.com.emersonmendes.capitalgain.dto.OperationType.BUY;
import static br.com.emersonmendes.capitalgain.dto.OperationType.SELL;
import static java.math.RoundingMode.*;

public class JsonUtilsTest {

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
    public void shouldReadList() {

        // Arrange
        var list = """
        [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},
        {"operation":"sell", "unit-cost":5.00, "quantity": 5000}]
        """;

        // Act
        var operations = JsonUtils.readList(list, new TypeReference<List<Operation>>() {});

        // Assert
        Assertions.assertThat(operations).containsAll(List.of(
            Operation.operation(BUY).unitCost(BigDecimal.valueOf(10.00).setScale(2, UP)).quantity(10000),
            Operation.operation(SELL).unitCost(BigDecimal.valueOf(5.00).setScale(2, UP)).quantity(5000)
        ));

    }

    @Test
    public void testWriteValue() {

        // Arrange
        var taxes = List.of(
             OperationTax.ofZero(),
             OperationTax.of(BigDecimal.valueOf(10.00).setScale(2, UP))
        );

        // Act
        JsonUtils.writeValue(System.out, taxes);

        // Assert
        Assertions.assertThat(outContent.toString()).isEqualTo("""
        [{"tax":0.00},{"tax":10.00}]""");

    }

    @Test
    public void testSplitJsonArrays() {

        // Arrange
        var jsonArraysStr = "[{}][{}][{}]";

        // Act
        var jsons = JsonUtils.splitJsonArrays(jsonArraysStr);

        // Assert
        Assertions.assertThat(jsons).isEqualTo(new String[]{"[{}]","[{}]","[{}]"});

    }

}