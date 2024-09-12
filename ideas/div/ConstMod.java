public class ConstMod {

  static final int BASE = 65521;

  public static void main(String[] args) {
    System.err.println(sumMod(Integer.parseInt(args[0])));
  }

  static long sumMod(int n) {
    long total = 0;
    for (int i = 0; i < n; i++) {
      total += modBase(i);
    }
    return total;
  }

  static int modBase(int i) {
    //return i % BASE;
    return Integer.remainderUnsigned(i, BASE);
  }
}
