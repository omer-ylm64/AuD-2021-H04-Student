package h04.provider;

import h04.Utils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RatioOfInversionsProvider implements ArgumentsProvider {

    private static final int MAX_STREAM_SIZE = 5;
    private static final int MAX_NUMBER_OF_INVERSIONS = 45;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.generate(() -> {
            int numberOfInversions = 0;
            List<Integer> sequence = IntStream.range(0, MAX_STREAM_SIZE)
                                              .boxed()
                                              .flatMap(integer -> Stream.concat(
                                                      Stream.of(0),
                                                      Utils.RANDOM.ints(1, 1, 10).boxed()))
                                              .collect(Collectors.toList());

            for (int i = 0; i < sequence.size(); i += 2) {
                int a = sequence.get(i), b = sequence.get(i + 1);

                if (Utils.RANDOM.nextBoolean()) {
                    sequence.set(i, b);
                    sequence.set(i + 1, a);

                    numberOfInversions++;
                }
            }

            return Arguments.of(sequence, numberOfInversions, MAX_NUMBER_OF_INVERSIONS);
        }).limit(MAX_STREAM_SIZE);
    }
}
