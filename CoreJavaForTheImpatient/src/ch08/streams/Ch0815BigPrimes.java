package ch08.streams;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.Stream;

// 15. Find 500 prime numbers with 50 decimal digits, using a parallel stream of
// BigInteger and the BigInter.isProbablePrime method. Is it any faster
// than using a serial stream?

// With Stream.generate, parallel was slightly faster than serial and both were 
// much faster than using Stream.iterate, especially parallel Stream.iterate
// which was much slower than serial Stream.iterate.

public class Ch0815BigPrimes {

  public static void main(String[] args) {

    BigInteger b = new BigInteger("977");
    Random r = new Random();
    long start, stop, elapsed;

    start = System.currentTimeMillis();
    Stream.iterate(b, x -> BigInteger.probablePrime(164, r))
    .filter(x -> x.toString().length() == 50)
    .filter(x -> x.isProbablePrime(10)) // probability is prime = 0.9990234375
    .limit(500).count();
    stop = System.currentTimeMillis();
    elapsed = stop - start;
    System.out.println("iterator serial time="+elapsed);
    // iterator serial time=1785,1686,1792

    start = System.currentTimeMillis();
    Stream.iterate(b, x -> BigInteger.probablePrime(164, r))
    .parallel()
    .filter(x -> x.toString().length() == 50)
    .filter(x -> x.isProbablePrime(10)) // probability is prime = 0.9990234375
    .limit(500).count();
    stop = System.currentTimeMillis();
    elapsed = stop - start;
    System.out.println("\niterator parallel time="+elapsed);
    // iterator parallel time=6794,6604,6608

    start = System.currentTimeMillis();
    Stream.generate(() -> BigInteger.probablePrime(164, r))
    .filter(x -> x.toString().length() == 50)
    .filter(x -> x.isProbablePrime(10)) // probability is prime = 0.9990234375
    .limit(500).count();
    stop = System.currentTimeMillis();
    elapsed = stop - start;
    System.out.println("\ngenerator serial time="+elapsed);
    // generator serial time=1791,1331,1319

    start = System.currentTimeMillis();
    Stream.generate(() -> BigInteger.probablePrime(164, r))
    .parallel()
    .filter(x -> x.toString().length() == 50)
    .filter(x -> x.isProbablePrime(10)) // probability is prime = 0.9990234375
    .limit(500).count();
    stop = System.currentTimeMillis();
    elapsed = stop - start;
    System.out.println("\ngenerator parallel time="+elapsed);
    // generator parallel time=1676,1183,1191


  }

}

//Results of running all tests in the same session:
//    
//    iterator serial time=1809
//
//    iterator parallel time=6544
//
//    generator serial time=1308
//
//    generator parallel time=1161



//for(BigInteger q : primes)
//System.out.println(q);
