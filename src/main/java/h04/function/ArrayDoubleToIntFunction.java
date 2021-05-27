package h04.function;

import h04.function.DoubleToIntFunction;

public class ArrayDoubleToIntFunction implements DoubleToIntFunction {

  private final int[] arr;

  public ArrayDoubleToIntFunction(int[] a) {
    arr = new int[a.length];
    for (int i = 0; i < a.length; i++) {
      arr[i] = a[i];
    }
  }


  @Override
  public int apply(double x) throws IllegalArgumentException {
    if (x < 0.0 || x > 1.0) {
      throw new IllegalArgumentException();
    }
    System.out.println("x:" + x);
    System.out.println("n:" + arr.length);

    int n = arr.length;
    double res = x * (n - 1);
    int ganz = (int) Math.round(res);

    if (ganz > res && ganz - res < Math.pow(10, -6)) {

      return arr[ganz];
    }
    if (ganz < res && res - ganz < Math.pow(10, -6)) {

      return arr[ganz];
    }
    if (ganz == res) {
      return arr[ganz];
    }
    else{
        int i1 = (int) Math.floor(x * (n - 1));
        int i2 = (int) Math.ceil(x * (n - 1));
        ////y=y1+((y2-y1)/(x2-x1))*(x-x1)
        double li = arr[i1] + ((arr[i2] - arr[i1]) / (i2 - i1)) * ((x * (n - 1)) - i1);
        return (int) Math.round(li);
      }
    }
  }

