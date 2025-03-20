package br.com.emersonmendes.capitalgain.console.reader;

import br.com.emersonmendes.capitalgain.dto.Operation;
import br.com.emersonmendes.capitalgain.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static br.com.emersonmendes.capitalgain.utils.JsonUtils.splitJsonArrays;

public class ArgsInputReader implements InputReader {

    private final String[] args;

    public ArgsInputReader(String[] args) {
        this.args = args;
    }

    @Override
    public void read(Consumer<List<Operation>> consumer) {
        Stream.of(args)
            .flatMap(arg -> Arrays.stream(splitJsonArrays(arg)))
            .map(ArgsInputReader::parseOperations)
            .forEach(consumer);
    }

    private static List<Operation> parseOperations(String json) {
        return JsonUtils.readList(json, new TypeReference<>() {});
    }

}
