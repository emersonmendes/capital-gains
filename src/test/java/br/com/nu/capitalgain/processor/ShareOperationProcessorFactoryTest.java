package br.com.nu.capitalgain.processor;

import br.com.nu.capitalgain.config.ConfigLoader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

public class ShareOperationProcessorFactoryTest {

    @Test
    void shouldThrowIllegalArgumentExceptionWhenProcessorDoesNotExist() {

        // Arrange
        ConfigLoader configMock = mock(ConfigLoader.class);
        var shareOperationProcessorFactory = new ShareOperationProcessorFactory(configMock);

        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            shareOperationProcessorFactory.getInstance(null);
        });

        // Assert
        Assertions.assertThat(exception.getMessage()).isEqualTo("Type not found for operation: null");

    }

}