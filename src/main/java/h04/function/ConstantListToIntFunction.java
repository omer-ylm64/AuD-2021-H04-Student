package h04.function;

import java.util.List;

public class ConstantListToIntFunction<T> implements ListToIntFunction<T> {

  private final int index;

  public ConstantListToIntFunction(int index){
    this.index = index;
  }

  @Override
  public int apply(List<T> list) throws NullPointerException {
    if(list == null){
      throw new NullPointerException();
    }
    return index;
  }
}
