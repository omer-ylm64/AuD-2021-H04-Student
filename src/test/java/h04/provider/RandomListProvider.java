package h04.provider;

import h04.Utils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomListProvider implements ArgumentsProvider {

    private static final int MAX_STREAM_SIZE = 5;
    private static final int LIST_SIZE = 10;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.generate(() ->
                Arguments.of(Utils.RANDOM.ints(LIST_SIZE, 0, 10)
                                         .boxed()
                                         .collect(Collectors.toList()))
        ).limit(MAX_STREAM_SIZE);
    }
}
