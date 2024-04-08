import jdk.incubator.vector.*;

public class ByteSpecies {
  public static void main(String[] args) {
    System.err.println(ByteVector.SPECIES_PREFERRED);
    System.err.println(ByteVector.SPECIES_MAX);
    System.err.println(ByteVector.SPECIES_512);
  }
}
