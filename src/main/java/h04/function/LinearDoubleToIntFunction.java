package h04.function;

public class LinearDoubleToIntFunction implements DoubleToIntFunction{

  private final double a;
  private final double b;

  public LinearDoubleToIntFunction(double a, double b){
    this.a = a;
    this.b = b;
  }

  @Override
  public int apply(double x) throws IllegalArgumentException {
    if (x < 0 || x > 1){
      throw new IllegalArgumentException();
    }
    return (int) Math.round(a*x+b);
  }
}
