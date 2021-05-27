package h04.function;

import java.util.Comparator;

public abstract class FunctionOnDegreeOfDisorder<T> implements ListToIntFunction<T> {
  protected final Comparator<? super T> x;

  public FunctionOnDegreeOfDisorder(Comparator<? super T> x){
    this.x = x;
  }
}
