package br.com.nu.capitalgain.dto;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class OperationTypeTest {

    @Test
    public void shouldThrowsIllegalArgumentExceptionWhenTryToGetAnUnknownType(){

        // Act
        final var exception = assertThrows(IllegalArgumentException.class, () -> {
            OperationType.fromString("exchange");
        });

        // Assert
        Assertions.assertThat(exception.getMessage()).isEqualTo("Invalid operation type: exchange");

    }

}