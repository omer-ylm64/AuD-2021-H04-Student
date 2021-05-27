package h04.function;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FunctionOnRatioOfInversions<T> extends FunctionOnDegreeOfDisorder<T> {
  private final DoubleToIntFunction dt;

  public FunctionOnRatioOfInversions(DoubleToIntFunction dt, Comparator<? super T> x) {
    super(x);
    this.dt = dt;
  }

  @Override
  public int apply(List<T> list) throws NullPointerException {

    double max = binomial(list.size(), 2);

    double invers = 0;
    List<T> tmp = new ArrayList<>();
    int count = 0;
    int same = 0;
    for (int i = 0; i < list.size(); i++) {
      if (i+1 != list.size()){
        if (x.compare(list.get(i), list.get(i+1))  >= 0){
          tmp.add(list.get(i));
        }
        if (x.compare(list.get(i), list.get(i+1))  == -1){
          for (int i1 = 0; i1 < tmp.size(); i1++) {
            if (x.compare(tmp.get(i1), tmp.get(i+1))  ==  1){
              count++;
            }
            if (x.compare(tmp.get(i1), tmp.get(i+1))  == 0){
              count++;
              same++;
            }
          }
          for (int i1 = 0; i1 < tmp.size(); i1++) {
            list.remove(i1);
          }
          invers += binomial((count-same),2);
        }
      }
    }
    System.out.println("Inverse: " + invers);
    double quotient = (double) invers/max;


    return dt.apply(quotient);
  }
  public static double binomial(int n,int k) {
    if (n<k) return 0;
    if (n<2*k) k = n-k;
    if (k==1) return n;
    if (k==0) return 1;
    else return binomial(n-1,k-1) + binomial(n-1,k);
  }
}
