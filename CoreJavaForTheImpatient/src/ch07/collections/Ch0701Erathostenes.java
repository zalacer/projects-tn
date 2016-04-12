package ch07.collections;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;

// 1. Implement the “Sieve of Erathostenes” algorithm to determine all prime numbers ≤ n. 
// Add all numbers from 2 to n to a set. Then repeatedly find the smallest element s
// in the set, and remove s**2 , s * (s + 1), s · (s + 2), and so on. You are done when s**2 > n.
// Do this with both a HashSet<Integer> and a BitSet.

public class Ch0701Erathostenes {

  public static int[] primesHashSet(int n) {
    n = Math.abs(n);
    if (n == 0)
      return new int[] {};
    if (n == 1)
      return new int[] { 1 };
    if (n == 2)
      return new int[] { 1, 2 };
    if (n == 3 || n == 4)
      return new int[] { 1, 2, 3 };
    if (n == 5 || n == 6)
      return new int[] { 1, 2, 3, 5 };
    HashSet<Integer> hs = new HashSet<>(Arrays.asList(1, 2, 3, 5));
    for (int i = 7; i <= n; i++)
      hs.add(i);
    int c = 0;
    double limit = Math.sqrt(n);
    for (int i = 2; i <= limit; i++) {
      int sq = i * i;
      c = 0;
      while (true) {
        int v = sq + i * c;
        if (v <= n) {
          hs.remove(new Integer(v));
          c++;
        } else {
          break;
        }
      }
    }
    int[] out = new int[hs.size()];
    Iterator<Integer> it = hs.iterator();
    c = 0;
    while (it.hasNext()) {
      out[c] = it.next();
      c++;
    }
    return out;
  }

  public static int[] primesBitSet(int n) {
    n = Math.abs(n);
    if (n == 0)
      return new int[] {};
    if (n == 1)
      return new int[] { 1 };
    if (n == 2)
      return new int[] { 1, 2 };
    if (n == 3 || n == 4)
      return new int[] { 1, 2, 3 };
    if (n == 5 || n == 6)
      return new int[] { 1, 2, 3, 5 };
    BitSet bs = new BitSet(n);
    bs.set(1,4); bs.set(5);
    for (int i = 7; i <= n; i++)
      bs.set(i);
    int c = 0;
    double limit = Math.sqrt(n);
    for (int i = 2; i <= limit; i++) {
      int sq = i * i;
      c = 0;
      while (true) {
        int v = sq + i * c;
        if (v <= n) {
          bs.set(v,false);
          c++;
        } else {
          break;
        }
      }
    }
    return bs.stream().toArray();
  }

  public static void main(String[] args) {

    System.out.println("\nprimesHashSet(30)");
    System.out.println(Arrays.toString(primesHashSet(30)));
    // [1, 2, 3, 5, 7, 11, 13, 17, 19, 23, 29]

    System.out.println("\nprimesBitSet(30)");
    System.out.println(Arrays.toString(primesBitSet(30)));
    // [1, 2, 3, 5, 7, 11, 13, 17, 19, 23, 29]

    System.out.println("\nprimesBitSet(1000)");
    for (int i : primesBitSet(1000)) System.out.println(i);       
    //            1
    //            2
    //            3
    //            5
    //            7
    //            11
    //            13
    //            17
    //            19
    //            23
    //            29
    //            31
    //            37
    //            41
    //            43
    //            47
    //            53
    //            59
    //            61
    //            67
    //            71
    //            73
    //            79
    //            83
    //            89
    //            97
    //            101
    //            103
    //            107
    //            109
    //            113
    //            127
    //            131
    //            137
    //            139
    //            149
    //            151
    //            157
    //            163
    //            167
    //            173
    //            179
    //            181
    //            191
    //            193
    //            197
    //            199
    //            211
    //            223
    //            227
    //            229
    //            233
    //            239
    //            241
    //            251
    //            257
    //            263
    //            269
    //            271
    //            277
    //            281
    //            283
    //            293
    //            307
    //            311
    //            313
    //            317
    //            331
    //            337
    //            347
    //            349
    //            353
    //            359
    //            367
    //            373
    //            379
    //            383
    //            389
    //            397
    //            401
    //            409
    //            419
    //            421
    //            431
    //            433
    //            439
    //            443
    //            449
    //            457
    //            461
    //            463
    //            467
    //            479
    //            487
    //            491
    //            499
    //            503
    //            509
    //            521
    //            523
    //            541
    //            547
    //            557
    //            563
    //            569
    //            571
    //            577
    //            587
    //            593
    //            599
    //            601
    //            607
    //            613
    //            617
    //            619
    //            631
    //            641
    //            643
    //            647
    //            653
    //            659
    //            661
    //            673
    //            677
    //            683
    //            691
    //            701
    //            709
    //            719
    //            727
    //            733
    //            739
    //            743
    //            751
    //            757
    //            761
    //            769
    //            773
    //            787
    //            797
    //            809
    //            811
    //            821
    //            823
    //            827
    //            829
    //            839
    //            853
    //            857
    //            859
    //            863
    //            877
    //            881
    //            883
    //            887
    //            907
    //            911
    //            919
    //            929
    //            937
    //            941
    //            947
    //            953
    //            967
    //            971
    //            977
    //            983
    //            991
    //            997

  }
}
