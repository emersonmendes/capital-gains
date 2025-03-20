package br.com.emersonmendes.capitalgain.console.reader;

import br.com.emersonmendes.capitalgain.dto.OperationTax;

import java.util.List;
import java.util.function.Consumer;

public interface InputReader {
    void read(Consumer<List<OperationTax>> callback);
}

