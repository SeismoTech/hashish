/**
 * XXHash has a specification and a reference implementation at
 * https://github.com/Cyan4973/xxHash/tree/dev
 * In fact, the implementation is more than a reference implemention:
 * it is a high performant, production ready implementation.
 * Therefore, it is very difficult to deduce the algorithm from the code.
 * Fortunatelly, the
 * [specification](
 * https://github.com/Cyan4973/xxHash/blob/dev/doc/xxhash_spec.md)
 * is very detailed and well written.
 *
 * <p>XXHash has 3 versions:
 * - XXH32, consuming blocks of 128 bits and computing a 32 bits hash.
 * - XXH64, consuming blocks of 256 bits and computing a 64 bits hash.
 * - XXH3, that has 2 subversion producing 64 and 128 bits hash.
 *
 * <p>Currently XXH32 and XXH64 have been implemented.
 */
package com.seismotech.hashish.impl.xx;
