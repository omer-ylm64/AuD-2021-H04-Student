package h04.function;

import java.util.Comparator;
import java.util.List;

public class FunctionOnRatioOfRuns<T> extends FunctionOnDegreeOfDisorder<T> {

  private final DoubleToIntFunction dt;

  public FunctionOnRatioOfRuns(DoubleToIntFunction dt, Comparator<? super T> x) {
    super(x);
    this.dt = dt;
  }

  @Override
  public int apply(List<T> list) throws NullPointerException {

    double runs = 1;
    for (int i = 0; i < list.size(); i++) {
      if (i+1 != list.size()) {
        if (x.compare(list.get(i),list.get(i+1)) <= 0)
         continue;
        if (x.compare(list.get(i),list.get(i+1)) == 1) {
          runs++;
          continue;
        }
      }
    }
    System.out.println("Runs: " + runs);


    double size = (double) list.size();

    double quotient = (double ) runs / size;
    System.out.println("size: "+size);
    System.out.println("Quotient: " + quotient);
    //dt.apply(quotient);
    return dt.apply(quotient);

  }
}
