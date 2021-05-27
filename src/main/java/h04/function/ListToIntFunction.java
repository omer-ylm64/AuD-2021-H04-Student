package h04.function;

import java.util.List;

public interface ListToIntFunction<T> {

  int apply(List<T> list) throws NullPointerException;

}
