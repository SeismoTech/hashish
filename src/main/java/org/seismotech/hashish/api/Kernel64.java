package org.seismotech.hashish.api;

/**
 * A <i>64 bits hashing kernel</i> is a {@link Hash} that consumes data in
 * 64 bits blocks.
 * Call method {@link #block} to provide a full block.
 * Call method {@link #tail} to provide the last data
 * and to finish the hash computation.
 * Data provided to {@link #tail} can never be a full block;
 * therefore, {@code taillen} should be in the range [0,7] (both extremes
 * included).
 *
 * Data should be given little endian.
 * On full blocks, has little significance, except for compliance with other
 * implementations.
 * But for tail data, method {@link #tail} should peek the data from the
 * less significant <i>8*{@code taillen}</i> bits in {@code tail}.
 */
public interface Kernel64 extends Hash {
  Kernel64 clone();
  void block(long block);
  void tail(long tail, int taillen, long totallen);
}
