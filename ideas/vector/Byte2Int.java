import jdk.incubator.vector.*;

public class Byte2Int {

  public static void main(String[] args) {
    final var species = ByteVector.SPECIES_PREFERRED;
    final int byteSize = species.vectorByteSize();
    System.out.println(
      "Size: " + byteSize + " (" + species.vectorBitSize() + ")");
    final byte[] bs = new byte[byteSize];
    for (int i = 0; i < byteSize; i++) bs[i] = (byte) (255-byteSize+i);
    ByteVector v0 = ByteVector.fromArray(species, bs, 0);
    System.out.println("Byte vector: " + v0);
    final VectorMask<Byte> shift = VectorMask.fromLong(
      ByteVector.SPECIES_PREFERRED, (~0L) << (byteSize/2));
    System.out.println(shift);
    final ShortVector unsigned = ShortVector.broadcast(
      ShortVector.SPECIES_PREFERRED, (short) 0xFF);
    final ShortVector scale = ShortVector
      .fromArray(
        ShortVector.SPECIES_PREFERRED,
        new short[] {
          16, 15, 14, 13,  12, 11, 10, 9,
          8,  7,  6,  5,   4,  3,  2, 1},
        0);
    for (int i = 0; i < 2; i++) {
      ShortVector v1 = (ShortVector) v0.convert(VectorOperators.B2S, 0);
      v1 = v1.and(unsigned);
      short sum = v1.reduceLanes(VectorOperators.ADD);
      System.out.println("Short vector: " + v1 + " = " + (sum & 0xFFFF));
      System.out.println("Scale vector: " + scale);
      ShortVector v1s = v1.mul(scale);
      short ssum = v1s.reduceLanes(VectorOperators.ADD);
      System.out.println("Scaled vector: " + v1s + " = " + (ssum & 0xFFFF));
      v0 = v0.compress(shift);
    }
  }

}
