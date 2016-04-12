package ch08.streams;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.stream.Stream;
import java.util.stream.LongStream;

//4. Using Stream.iterate, make an infinite stream of random numbers—not by
// calling Math.random but by directly implementing a linear congruential
// generator. In such a generator, you start with x[0] = seed and then produce 
// x[n  + 1] = (a*x[n] + c) % m, for appropriate values of a, c, and m. You 
// should implement a method with parameters a, c, m, and seed that yields a 
// Stream<Long>. Try out a = 25214903917, c = 11, and m = 2**48 .

public class Ch0804LinearCongruentialGenerator {

  public static LongStream genLcg(long seed, long a, long c, double m) {
    return  LongStream.iterate(seed, x -> ((long) ((a * x + c) % m)));
  }

  public static Stream<BigDecimal> bigLcg(long seed, long a, long c, double m) {
    MathContext mc = MathContext.UNLIMITED;
    BigDecimal seedb = new BigDecimal(""+seed, mc);
    BigDecimal ab = new BigDecimal(""+a, mc);
    BigDecimal cb = new BigDecimal(""+c, mc);
    BigDecimal mb = new BigDecimal(""+m, mc);
    return Stream.iterate(seedb, x -> ab.multiply(x).add(cb).divideAndRemainder(mb, mc)[1]);
  }

  public static LongStream bigLcg2Long(long seed, long a, long c, double m) {
    MathContext mc = MathContext.UNLIMITED;
    BigDecimal ab = new BigDecimal(""+a, mc);
    BigDecimal cb = new BigDecimal(""+c, mc);
    BigDecimal mb = new BigDecimal(""+m, mc);
    return LongStream.iterate(seed, x -> (ab.multiply(new BigDecimal(""+x))
        .add(cb).divideAndRemainder(mb, mc)[1].longValue()));
  }

  public static void main(String[] args) {

    long seed = 7901L;
    long a = 25214903917L;
    long c = 11;
    double m = Math.pow(2,48); // 281,474,976,710,656

    // ok but don't want negatives
    LongStream lcg = genLcg(seed, a, c, m);
    System.out.println("genLcg("+seed+", "+a+", "+c+", "+m+").limit(9):");
    lcg.limit(9).forEach(System.out::println);
    //        7901 // exclude this
    //        199222955848228
    //        106238370066432
    //        -157650485022720
    //        44382654476288
    //        -24452511502336
    //        -109307117817856
    //        -251214736494592
    //        -169755070320640

    System.out.println();

    // using BigDecimal seeems to eliminate negatives
    Stream<BigDecimal> bigStream = bigLcg(seed, a, c, m);
    System.out.println("bigLcg("+seed+", "+a+", "+c+", "+m+").limit(30):");
    bigStream.limit(30).forEach(System.out::println);
    //        7901 // exclude this
    //        199222955848228
    //        106238370066783
    //        132674922962814
    //        205013959236785
    //        125164923583848
    //        170041205937491
    //        83798838077026
    //        69346822661061
    //        138659016554732
    //        226794375824519
    //        185737046882182
    //        69555843254297
    //        22456785250480
    //        94958806659835
    //        256875888277226
    //        163956860313005
    //        112902478876852
    //        272689881912495
    //        49574011439246
    //        60111118448769
    //        59922069484792
    //        230247547706787
    //        167930413813874
    //        267435216268437
    //        215456566647164
    //        80211286475223
    //        214954879333014
    //        113900759272937
    //        172150779675200

    System.out.println();

    // uses BigDecimal for calculations and converts results to long
    LongStream longStream = bigLcg2Long(seed, a, c, m);
    System.out.println("bigLcg2Long("+seed+", "+a+", "+c+", "+m+").limit(30):");
    longStream.limit(30).forEach(System.out::println);
    //        7901 // exclude this
    //        199222955848228
    //        106238370066783
    //        132674922962814
    //        205013959236785
    //        125164923583848
    //        170041205937491
    //        83798838077026
    //        69346822661061
    //        138659016554732
    //        226794375824519
    //        185737046882182
    //        69555843254297
    //        22456785250480
    //        94958806659835
    //        256875888277226
    //        163956860313005
    //        112902478876852
    //        272689881912495
    //        49574011439246
    //        60111118448769
    //        59922069484792
    //        230247547706787
    //        167930413813874
    //        267435216268437
    //        215456566647164
    //        80211286475223
    //        214954879333014
    //        113900759272937
    //        172150779675200

    System.out.println();

    // print pseudo random numbers from 0 to 9    
    System.out.println("bigLcg2Long("+seed+", "+a+", "+c+", "+m+").forEach(x -> {"
        + "\n     System.out.println((int) Math.floor(x * 10 / "+m+"));}):");
    bigLcg2Long(seed, a, c, m).limit(30).forEach(x -> {
      System.out.println((int) Math.floor(x * 10 / m));});
    //        0 // exclude this
    //        7
    //        3
    //        4
    //        7
    //        4
    //        6
    //        2
    //        2
    //        4
    //        8
    //        6
    //        2
    //        0
    //        3
    //        9
    //        5
    //        4
    //        9
    //        1
    //        2
    //        2
    //        8
    //        5
    //        9
    //        7
    //        2
    //        7
    //        4
    //        6



  }

}
