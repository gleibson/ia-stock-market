
   GNU Trove: High performance collections for Java.

Objectives

   The GNU Trove library has two objectives:
    1. Provide "free" (as in "free speech" and "free beer"), fast,
       lightweight implementations of the java.util Collections API.
       These implementations are designed to be pluggable replacements
       for their JDK equivalents.
    2. Whenever possible, provide the same collections support for
       primitive types. This gap in the JDK is often addressed by using
       the "wrapper" classes (java.lang.Integer, java.lang.Float, etc.)
       with Object-based collections. For most applications, however,
       collections which store primitives directly will require less
       space and yield significant performance gains.

Hashtable techniques

   The Trove maps/sets use open addressing instead of the chaining
   approach taken by the JDK hashtables. This eliminates the need to
   create Map.Entry wrappper objects for every item in a table and so
   reduces the O (big-oh) in the performance of the hashtable algorithm.
   The size of the tables used in Trove's maps/sets is always a prime
   number, improving the probability of an optimal distribution of
   entries across the table, and so reducing the likelihood of
   performance-degrading collisions. Trove sets are not backed by maps,
   and so using a THashSet does not result in the allocation of an unused
   "values" array.

Hashing strategies

   Trove's maps/sets support the use of custom hashing strategies,
   allowing you to tune collections based on characteristics of the input
   data. This feature also allows you to define hash functions when it is
   not feasible to override Object.hashCode(). For example, the
   java.lang.String class is final, and its implementation of hashCode()
   takes O(n) time to complete. In some applications, however, it may be
   possible for a custom hashing function to save time by skipping
   portions of the string that are invariant.

   Using java.util.HashMap, it is not possible to use Java language
   arrays as keys. For example, this code:
    char[] foo, bar;
    foo = new char[] {'a','b','c'};
    bar = new char[] {'a','b','c'};
    System.out.println(foo.hashCode() == bar.hashCode() ? "equal" : "not equal"
);
    System.out.println(foo.equals(bar) ? "equal" : "not equal");
    

   produces this output:
    not equal
    not equal
    

   And so an entry stored in a java.util.HashMap with foo as a key could
   not be retrieved with bar, since there is no way to override
   hashCode() or equals() on language array objects.

   In a gnu.trove.THashMap, however, you can implement a
   TObjectHashingStrategy to enable hashing on arrays:
    class CharArrayStrategy implements TObjectHashingStrategy {
        public int computeHashCode(Object o) {
            char[] c = (char[])o;
            // use the shift-add-xor class of string hashing functions
            // cf. Ramakrishna and Zobel, "Performance in Practice of String Ha
shing Functions"
            int h = 31; // seed chosen at random
            for (int i = 0; i < c.length; i++) { // could skip invariants
                h = h ^ ((h << 5) + (h >> 2) + c[i]); // L=5, R=2 works well fo
r ASCII input
            }
            return h;
        }

        public boolean equals(Object o1, Object o2) {
            char[] c1 = (char[])o1;
            char[] c2 = (char[])o2;
            if (c1.length != c2.length) { // could drop this check for fixed-le
ngth keys
                return false;
            }
            for (int i = 0, len = c1.length; i < len; i++) { // could skip inva
riants
                if (c1[i] != c2[i]) {
                    return false;
                }
            }
            return true;
        }
    }
    

Iterators in primitive collections

   As of release 0.1.7, Trove's primitive mappings include access through
   Iterators as well as procedures and functions. The API documentation
   on those classes contains several examples showing how these can be
   used effectively and explaining why their semantics differ from those
   of java.util.Iterator.

Miscellaneous

   N.B. using Map.entrySet on a Trove Map is supported, but not
   encouraged. The reason is that this API requires the creation of the
   Map.Entry Objects that all other parts of Trove manage to avoid. An
   alternative is to implement the appropriate Procedure interface and
   use it to invoke the Map's forEachEntry API. Map.keySet and Map.values
   are not similarly encumbered; nevertheless, the forEachKey,
   forEachValue, and transformValues APIs will yield slightly better
   performance at the cost of compatibility with the interface of
   java.util.Map.
     _________________________________________________________________

   Last modified: Mon Sep 23 18:22:39 PDT 2002
