Hashish: a psychedelic approach to hashing
======================================================================

This package contains implementation of several hashing algorithms
sharing a common interface.

The criteria to add a hashing algorithm is
(1) to be a fast algorithm,
and (2a) to be useful for data structures or algorithms based on hashing,
or, at least, (2b) as an error detection checksum.

Typical algorithms matching (1) + (2a) are 
  SipHash, MurmurHash, XXHash, wyHash, ...;
matching (1) + (2b) are 
  Adler32, ...

The implementation heavily leverages scalar-replacement
to get both an abstract and reusable design,
and a great performance.

API
----------------------------------------------------------------------

The API has the following structure.

- There is a `org.seismotech.hashish.api` package with all the public
interfaces and no implementation.

- There is a `org.seismotech.hashish.impl` package with general
or partial implementations of some interfaces.

- There is a `org.seismotech.hashish.impl.X` subpackage for each
algorithm family.
The `X` is the name of the hashing family with any `hash` substring removed.
For instance, the subpackage for MurmurHash3 32 bits algorithm is
`org.seismotech.hashish.impl.murmur`.

- There are 3 main interfaces;
2 high level (`Hashing` and `Hasher`)
and 1 low level (`Kernel` family).
There is a `Hash` auxiliary interface.

- `Hash` is a abstract hahttps://en.wikipedia.org/wiki/Locality-sensitive_hashingsh value.
It hash methods to retrive a 32, 64 or 128 bits hash value.
This library tries to avoid allocation of `Hash` instances to return
hash values.
When using a functional interface, a `long` is returned (see bellow).
When using stateful objects, those object are instances of `Hash`,
and therefore it is possible to retrieve the current hash value.

- `Hashing` is the functional, side-effect free view of a hashing algorithm.
It can have some immutable state, generally seeds or other initialization
stuff.
It has a bunch of functional, thread-safe methods to compute the hash code
of typical basic Java values:
primitives, arrays of primitives, String, CharSequence and Buffers.
All those methods return the hash code as a `long`,
regardless of the algorithm hash size;
for algorithms computing a 32 bits hash, it is 0 padded to a `long`;
for algorithms computing more that 64 bits,
they are combined to produce a `long`.
And there is a method to build a `Hasher` sharing the same initialization
state.

- `Hasher` is a mutable object to incrementally compute a hash.
It has similar methods to `Hashing`, 
but they update the state and are fluent,
instead of returning a final hash value.
`Hasher` is a `Hash`; therefore, it is possible to get the full hash value.

- The Kernel family is a set of interfaces containing the minimal set of
methods to implement the core of a hashing algorithm.
Most hashing algorithms are *left to right*, *block by block* scanners.
Usually they have special code to process the tail (the last partial block),
and to finalize the computation.
Thus, a kernel is comprised mainly of 2 methods, 
a `block` method to process the next full block,
and a `tail` method to process the last *partial* (posible empty) block.
It is very important that `tail` doesn't allow a full block;
it must be partial, 
and it can be empty (when the data length is multiple of the block size).
After calling `tail`, there should be no other call to `block` or `tail`.

    Kernels implement `Hash`, but their methods should be used only after
calling `tail`.
To support multiple partial hash values on a data stream,
Kernels has a `clone` method, that produce a new instance,
with a cloned state.
After cloning, it is possible to use `tail` on one of the clones to get
the partial hash value.

There is a different Kernel interface for each block width.
For the implemented algorithms, we needed
`Kernel8`, `Kernel32`, `Kernel64`, `Kernel128` and `Kernel256`.
As the block size grows, Kernels methods gets more and more parameters
and the implementation gets dirtier.
Java limitations on memory management leaves little room to do things
better.
Unfortunatelly, there are algorithms with upto 1024 block sizes!

- The main entry points are the implementations of `Hashing` interface.
Although other libraries tend to hide those implementations and offer
static constructors in a kind of factory class or interface,
we prefer to expose the classes implementing `Hashing`.
There is no need to directly instantiate a `Hasher`;
the recommended style is to get a `Hasher` with `Hashing.hasher()`.
It is debatable if there is a real need to use Kernels directly.
Currently they are accesible, just in case there is some basic data structure
not supported in `Hashing` that could be hashed with better performance 
directly using a Kernel.

- The main goal of this approach is to be able to implement all the methods
in `Hashing` without handling all the gory details of each hashing algorithm
in each method.
A kernel is a simple but powerful interface that modern JVM are able to
inline and to stack allocate (Scalar replace).
Java will do all the specialization on each data type,
using inlining, scalar replacement, contant propagation, dead code elimination,
and many other standard optimizations.

In fact, we are pretty happy with the project arquitecture.
The Kernel approach is a perfect match for many important algoriths
(MurmurHash, XXHash, SipHash).
For those that cannot be implemented with a Kernel,
`Hashing` and `Hasher` interfaces are perfectly feaseable.
Although the Kernel approach is not feasible for some algorithms,
and the `Hashing` and `Hasher` interfaces can cope with them.

And last, but not least, the performance is great with a limited implementation
effort.
All our code is zero allocation once the JVM discover it is hot
and optimizes it (particularly, applies Scalar Replacement).
As a reference, benchmarking our code against the highly optimized
library 
[Zero-Allocation-Hashing](https://github.com/OpenHFT/Zero-Allocation-Hashing)
shows that we are less that 10% slower.


References
----------------------------------------------------------------------

[Understanding Hash Functions](
https://github.com/google/farmhash/blob/master/Understanding_Hash_Functions)
by Geoff Pike,
from the FarmHash repo.
A short, nice and comprehensible introduction to Hash function design.

[SMHasher](https://github.com/aappleby/smhasher)
The home of MurmurHash and the SMHasher test suite to check the quality of
a hashing algorithm.


Hashing algorithms
----------------------------------------------------------------------

- [xxHash](https://github.com/Cyan4973/xxHash)
It has a [really good specification](
https://github.com/Cyan4973/xxHash/blob/dev/doc/xxhash_spec.md)
C++ production ready implementation.
Variants XXH32 and XXH64 implemented.
Very fast and high quality.
https://chromium.googlesource.com/external/github.com/Cyan4973/xxHash/+/375d401bd4a4eba07ee75d6e627546052cb5b0ec/README.md

- [CityHash](https://github.com/google/cityhash/tree/master)
No specification of the algorithm.
C++ production ready implementation.
Difficult to implement in this project 
because it starts hashing the tail of the data.

- [FarmHash](https://github.com/google/farmhash)
No description of the algorithm.
C++ production ready implementation.

- [wyHash](https://github.com/wangyi-fudan/wyhash)
No description of the algorithm.
C++ production ready implementation.
Several languages use this algorithm to compute hash values: Zig, V, Nim and Go.
It contains also a pseudo-random number generator.

- [MetroHash](https://github.com/jandrewrogers/MetroHash)
Automatically generated to maximize performance while passing a minimum
quality (SMHasher).
Therefore, there cannot be a specification:
the generated code is the specification.
For more info, see
http://www.jandrewrogers.com/2015/05/27/metrohash/

- [AquaHash](https://github.com/jandrewrogers/AquaHash)
Fast Hashing With AES Intrinsics.
No description of the algorithm.
C++ production ready implementation.
It depends on AES operations/intrinsics available in some cpus.
From the guy that implemented MetroHash (J. Andrew Rogers).

- [KomiHash](https://github.com/avaneev/komihash)
No specification, but a readable code.
Only one version: 512 bits blocks, 64 bits hash.
[README.md](https://github.com/avaneev/komihash/blob/main/README.md)
is a nice reading.

- [PolymurHash](https://github.com/orlp/polymur-hash)
A very unusual and interesting hash function.
It is an *universal* hash function.
49 bytes (392 bits) blocks, 64 bits hash.
Difficult to adapt to our hashing design.
It requires a really specific Kernel for 49 bytes receiving 7 partial longs.
The tail processing has forward and backward reading,
making the kernel approach impossible.
There is no specification, only a detailed explanation of the mathematical
founding.
The code is easy to read.


Other libraries
----------------------------------------------------------------------

[Apache Common Codec](
https://commons.apache.org/proper/commons-codec/apidocs/index.html)
has an `org.apache.commons.codec.digest` package with several
hashing (or message digest) algorithms.
Particularly, there is an implementation of
Murmur3\_32, 
Murmur3\_x64\_128,
and xxHash32.

[Guava](https://guava.dev/)
has a package [`com.google.common.hash`](
https://guava.dev/releases/snapshot-jre/api/docs/com/google/common/hash/Hashing.html)
with 
Murmur3\_32,
Murmur3\_x64\_128,
and SipHash-2-4.

[Zero-Allocation-Hashing](
https://github.com/OpenHFT/Zero-Allocation-Hashing/tree/ea)
is a specific project of hashing algorithms
focused on performance (and zero-allocation)
from OpenHFT (Chronicle Software).
It has several hashing algorithms, including Murmur3 and xxHash.
It is a good project to compare with, not only for performance but also to
learn techiques.

[hash4j](https://github.com/dynatrace-oss/hash4j)
a project from Dynatrace
collecting several high-quality, non-cryptographic hashing algoritms.
And also some applications of hashing to solve real world problems:
similarity, distinct count, and big file hashing.
