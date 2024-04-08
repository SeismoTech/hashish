Hashish: a psychedelic approach to hashing
======================================================================

This package contains implementation of several hashing algorithms
sharing a common interface.

The criteria to add a hashing algorithm is
(1) to be a fast algorithm,
and (2a) to be useful for data structures or algorithms based on hashing,
or, at least, (2b) as an error detection checksum.

Typical algorithms matching (1) + (2a) are SipHash, MurmurHash, XXHash, ...;
matching (1) + (2b) are Adler32, ...

The implementation must directly cope with all the usual primitive data in Java:
primitive types, `byte[]` and `String`.
There must be also a way to hash structured data.

The implementation heavily leverages scalar-replacement
to get both an abstract and reusable design,
and a great performance.


Other libraries
----------------------------------------------------------------------

[Apache Common Codec](
https://commons.apache.org/proper/commons-codec/apidocs/index.html)
has an `org.apache.commons.codec.digest` package with several
hashing (or message digest) algorithms.
Particularly, there is an implementation of
Murmur3\_32, 
Murmur3\_x64\_128,
and XXHash32.

[Guava](https://guava.dev/)
has a package [`com.google.common.hash`](
https://guava.dev/releases/snapshot-jre/api/docs/com/google/common/hash/Hashing.html)
with 
Murmur3\_32,
Murmur3\_x64\_128,
and SipHash-2-4.
