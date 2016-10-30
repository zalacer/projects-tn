package ex14;

import static analysis.Log.lg;
import static java.lang.Math.abs;
import static v.ArrayUtils.range;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import analysis.Timer;
import v.Tuple2;
import v.Tuple3;

//  p212
//  1.4.34 Hot or cold. Your goal is to guess a secret integer between 1 and N. You 
//  repeatedly guess integers between 1 and N. After each guess you learn if your guess
//  equals the secret integer (and the game stops). Otherwise, you learn if the guess is
//  hotter (closer to) or colder (farther from) the secret number than your previous 
//  guess. Design an algorithm that finds the secret number in at most ~2 lg N guesses. 
//  Then design an algorithm that finds the secret number in at most ~ 1 lg N guesses.

@SuppressWarnings("unused")
public class Ex1434HotOrColdBinarySearch {
  
  public static int key = 5;
  
  public static BiFunction<Integer,Integer,String> learn = (a,b) -> {
    // functional simulator for learning the outcome of a guess.
    // plugs into guess1LgN and guess2LgN as a command line argument.
    int g = b.intValue(); // current guess
    int p = a.intValue(); // previous guess
    if (g == key)  return "true";
    int d1 = g - key > 0 ? g - key : key - g;
    int d2 = p - key > 0 ? p - key : key - p;
    return d1 == d2 ? "no change" : d1 < d2 ? "hotter" : "cooler";
  };
  
  public static double[] guess1LgN(int N, BiFunction<Integer,Integer,String> learn) {
    // searches for key in [1,N] in ~1lg guesses. this is closely patterned on
    // binary search to the extent that its max metric for all N's up to 100000 is 
    // exactly the same as for binSearch. binSearch, some of this data and methods
    // to reproduce and generate more of it are provided below. this method shows 
    // that this exercise is essentially binary search on range(1,N+1) = int[1,2,3,...,N]
    // without the actual array. it also shows there is no need to greatly extend N, 
    // but it was useful to extend it by one element in each direction so in practice
    // a virtual array range(0, N+2) is used. its metric is the numberOfGuesses/lgN).
    // this method returns a double[] containing key, numberOfGuesses and metric. 
    int min = 0;
    int max = N+1;
    int mid = 1; // the guess
    String r;  // return value of learn
    int t = 0; // number of executions of learn.apply
    
    while (min <= max) {
      mid = min + (max - min) / 2;
      r = learn.apply(mid-1, mid);
      t++;
      if (r.equals("true")) { // mid == key
        return new double[]{key, t,  (1.*t)/lg(N)};
      } else if (r.equals("no change") ) {
        // this is never entered in practice.
        // key cannot be the current min or max since p != g.
        min++; max--;
        System.out.println("no change mid="+mid);
      } else if (r.equals("hotter")) {
        min = mid++;
      } else if (r.equals("cooler")) {
        max = mid--;
      } 
    }
    return new double[]{-1, t,  1.*t/lg(N)};
  }
 
  public static double[] guess2LgN(int N, BiFunction<Integer,Integer,String> learn) {
    // searches for key in [1,N] in ~2lg guesses. engineered so that the max ratio
    // of guesses to 2lg is 1 and it tends to decrease with increasing N (for N=10
    // its max is 0.903090 while for N=10000000 it's 0.752575, see table below. the
    // metric for this method is numberOfGuesses/(2*lg(N). this method returns a
    // double[] containing key, numberOfGuesses and metric.
    int min = 0; 
    int min2 = 0; // previous min
    int max = isExactPowerOf2(N+1) ? N+3 : N+1; //N+3 is hack for N=31 key=12 
    int max2 = 0; // previous max
    int g = 0; // the guess
    int p = 0; // previous geuss
    String r;  // return value of learn
    int t = 0; // number of executions of learn.apply
      
    while(true) {
      g = min + (max-min)/2;
      p = min + (g-min)/2;
      if (g == p)
        while (g == p) {
          min++; max--;
          g = min + (max-min)/2;
          p = min + (g-min)/2;
        }              
      r =  learn.apply(p, g);
      t++;
      if (r.equals("true")) { // g == key
        return new double[]{key, t,  1.*t/(2*lg(N))};
      } else if (r.equals("no change")) {
        // key cannot be the current min or max since p != g
        min++; max--;
        continue;
      } else if (r.equals("hotter")) {
        min2 = p + (g-p)/2;
        if (min == min2 && g-p<=1 && g > min) {
          min = g++;
        } else min = min2;
        continue;
      } else if (r.equals("cooler")) {
        max2 = p + (g-p)/2;
        if (max == max2 && g-p<=1 && g < max) {
          max = g--;
        } else max = max2;
        continue;
      }
    }
  }
  
  public static double[] binSearch(int[] a, int key) {
    // this is regular binary search modified only to return a double[]
    // containing key, numberOfGuesses, metric where the latter is 
    // numberOfGuesses/lg(a.length).
    int lo = 0;
    int hi = a.length - 1;
    int t = 0; // number of trials
    while (lo <= hi) { 
      t++;
      int mid = lo + (hi - lo) / 2;
      if      (key < a[mid]) hi = mid - 1;
      else if (key > a[mid]) lo = mid + 1;
      else return new double[]{mid, t, 1.*t/lg(a.length)};
    }
    return new double[]{-1, t, 1.*t/lg(a.length)};
  }
  
  public static boolean isExactPowerOf2(int x) {
    // return true if x is an exact power of 2 else return false
    return (x & (x-1)) == 0;
  }
  
  public static <K> Map<K, Double> reverseSortByValue( Map<K, Double> input ) {
    // return a new map with the values of input reverse sorted.
    Map<K,Double> result = new LinkedHashMap<>();
    input.entrySet().stream().sorted(Comparator.comparing(e -> -e.getValue())) 
      .forEachOrdered(e -> result.put(e.getKey(),e.getValue()));
    return result;
  }
  
  public static Tuple3<Double,Integer,Integer> findMaxGuess2LgNMetric(int n) {
    // return a Tuple3(metric,N,key) for the max metric for N in the range
    // [1,n] for guess2LgNMetric over all keys in the range [1-N].
    double max = Double.NEGATIVE_INFINITY;
    int k = 0; // value of key associated with max
    int N = 0; // value of N associated with max
    for (int i = 1; i < n+1; i++)
      for (int j = 1; j < i+1; j++) {
        key = j;
        double[] da = guess2LgN(i, learn);
        if (da[2] == Double.POSITIVE_INFINITY) continue;
        if (da[2] > max) {
          max = da[1]; N = i; k = j;
        }
      }
    return new Tuple3<Double,Integer,Integer>(max,N,k);
  }
  
  public static Tuple3<Double,Integer,Integer> findMaxGuess1LgNMetric(int n) {
    // return a Tuple3(metric,N,key) for the max metric for N in the range
    // [1,n] for guess1LgNMetric over all keys in the range [1-N].
    double max = Double.NEGATIVE_INFINITY;
    int k = 0; // value of key associated with max
    int N = 0; // value of N associated with max
    for (int i = 1; i < n+1; i++)
      for (int j = 1; j < i+1; j++) {
        key = j;
        double[] da = guess1LgN(i, learn);
        if (da[2] == Double.POSITIVE_INFINITY) continue;
        if (da[2] > max) {
          max = da[1]; N = i; k = j;
        }
      }
    return new Tuple3<Double,Integer,Integer>(max,N,k);
  }
  
  public static Tuple3<Double,Integer,Integer> findMaxBinSearchMetric(int n) {
    // return a Tuple3(metric,N,key) for the max metric for N in the range
    // [1,n] for guess1LgNMetric over all keys in the range [1-N].
    double max = Double.NEGATIVE_INFINITY;
    int k = 0; // value of key associated with max
    int N = 0; // value of N associated with max
    for (int i = 1; i < n+1; i++)
      for (int j = 1; j < i+1; j++) {
        key = j;
        double[] da = binSearch(range(1, i+1), j);
        if (da[2] == Double.POSITIVE_INFINITY) continue;
        if (da[2] > max) {
          max = da[1]; N = i; k = j;
        }
      }
    return new Tuple3<Double,Integer,Integer>(max,N,k);
  }

  public static List<Tuple3<Double,Integer,Integer>> findGuess2LgNMetricOver1(int n) {
    // return a list of Tuple3(metric,N,key) for all results returned
    // by guess2LgNMetric for N in the range [1,n] over all keys in the range 
    // [1-N] for which metric > 1.
    List<Tuple3<Double,Integer,Integer>> list = new ArrayList<>();
    int k = 0; // value of key associated with max
    int N = 0; // value of N associated with max
    for (int i = 1; i < n+1; i++)
      for (int j = 1; j < i+1; j++) {
        key = j;
        double[] da = guess2LgN(i, learn);
        if (da[2] == Double.POSITIVE_INFINITY) continue;
        if (da[2] > 1.0) {
          list.add(new Tuple3<Double,Integer,Integer>(da[2],i,j));
        }
      }
    return list;
  }
  
  public static List<Tuple3<Double,Integer,Integer>> findGuess1LgNMetricOver1(int n) {
    // return a list of Tuple3(metric,N,key) for all results returned
    // by guess2LgNMetric for N in the range [1,n] over all keys in the range 
    // [1-N] for which metric > 1.
    List<Tuple3<Double,Integer,Integer>> list = new ArrayList<>();
    int k = 0; // value of key associated with max
    int N = 0; // value of N associated with max
    for (int i = 1; i < n+1; i++)
      for (int j = 1; j < i+1; j++) {
        key = j;
        double[] da = guess1LgN(i, learn);
        if (da[1] == Double.POSITIVE_INFINITY) continue;
        if (da[1] > 1.0) {
          list.add(new Tuple3<Double,Integer,Integer>(da[2],i,j));
        }
      }
    return list;
  }
  
  public static List<Tuple3<Double,Integer,Integer>> findBinSearchMetricOver1(int n) {
    // return a list of Tuple3(metric,N,key) for all results returned
    // by guess2LgNMetric for N in the range [1,n] over all keys in the range 
    // [1-N] for which metric > 1.
    List<Tuple3<Double,Integer,Integer>> list = new ArrayList<>();
    int k = 0; // value of key associated with max
    int N = 0; // value of N associated with max
    for (int i = 1; i < n+1; i++)
      for (int j = 1; j < i+1; j++) {
        key = j;
        double[] da = binSearch(range(1, i+1), j);
        if (da[1] == Double.POSITIVE_INFINITY) continue;
        if (da[1] > 1.0) {
          list.add(new Tuple3<Double,Integer,Integer>(da[2],i,j));
        }
      }
    return list;
  }
  
  public static void printGuess2LgN(int start, int end) {
    // prints N, metric and key from guess2LgN output for all N from 
    // start to end for all keys from 1 to N.
    for (int i = start; i < end+1; i++)
      for (int j = 1; j < i+1; j++) {
        key = j;
        double[] da = guess2LgN(i, learn);
        System.out.printf("N=%02d, metric=%f, key=%d\n", i, da[2], j);
      }
    System.out.println();
  }
  
  public static void printGuess2LgNMaxMetric(int start, int end) {
    // prints the max metric value from guess2LgN output over all N from 
    // start to end for all keys from 1 to N.
    int[] a; double max = Double.NEGATIVE_INFINITY;
    for (int i = start; i < end+1; i++) {
      a = range(start, end+1);
      for (int j = 1; j < i+1; j++) {
        key = j;
        double[] da = guess2LgN(i, learn);
        if (da[2] == Double.POSITIVE_INFINITY) continue;
        if (da[2] > max) max = da[2];
      }
    }
    System.out.println(max);
  }
  
  public static void printGuess1LgN(int start, int end) {
    // prints N, metric and key from guess1LgN output for all N from 
    // start to end for all keys from 1 to N.
    for (int i = start; i < end+1; i++)
      for (int j = 1; j < i+1; j++) {
        key = j;
        double[] da = guess1LgN(i, learn);
        System.out.printf("N=%02d, metric=%f, key=%d\n", i, da[2], j);
      }
    System.out.println();
  }
  
  public static void printGuess1LgNMaxMetric(int start, int end) {
    // prints the max metric value from guess1LgN output over all N from 
    // start to end for all keys from 1 to N.
    int[] a; double max = Double.NEGATIVE_INFINITY;
    for (int i = start; i < end+1; i++) {
      a = range(start, end+1);
      for (int j = 1; j < i+1; j++) {
        key = j;
        double[] da = guess1LgN(i, learn);
        if (da[2] == Double.POSITIVE_INFINITY) continue;
        if (da[1] > max) max = da[2];
      }
    }
    System.out.println(max);
  }
  
  public static void printBinSearch(int start, int end) {
    // prints N, metric and key from binSearch output for all N from 
    // start to end for all keys from 1 to N.
    int[] a;
    for (int i = start; i < end+1; i++) {
      a = range(start, end+1);
      for (int j = 1; j < i+1; j++) {
        key = j;
        double[] da = binSearch(a, key);
        System.out.printf("N=%02d, metric=%f, key=%d\n", i, da[2], j);
      }
    }
    System.out.println();
  }
  
  public static void printBinSearchMaxMetric(int start, int end) {
    // prints the max metric value from binSearch output over all N from 
    // start to end for all keys from 1 to N.
    int[] a; double max = Double.NEGATIVE_INFINITY;
    for (int i = start; i < end+1; i++) {
      a = range(start, end+1);
      for (int j = 1; j < i+1; j++) {
        key = j;
        double[] da = binSearch(a, key);
        if (da[1] > max) max = da[2];
      }
    }
    System.out.println(max);
  }
  
  public static void printGuess1LgNMetricAtPowersOf10() { 
    // prints the max metric from guess1LgN output for N equal to powers 
    // of 10 ranging from 10**1 to 10**8 over all keys from 1 to N.
    double[] da;
    HashMap<Double, Double> map = null;
    Map<Double, Double> smap = null;
    int c = 10;

    while (c < 100000000) {
      map = new HashMap<>();
      for (int j = 1; j < c+1; j++) {
        key = j;
        da = guess1LgN(c, learn);
        map.put(1.*c, da[2]);
      }  
      smap = reverseSortByValue(map);
      for (Double d : smap.keySet()) {
        System.out.printf("%s%-8d  %f\n", "N=",c,smap.get(d));
        break;
      }
      c*=10;
    }
  }

  public static void printGuess2LgNMetricAtPowersOf10() {
    // prints the max metric from guess2LgN output for N equal to powers 
    // of 10 ranging from 10**1 to 10**8 over all keys from 1 to N.
    double[] da;
    HashMap<Double, Double> map = null;
    Map<Double, Double> smap = null;
    int c = 10;

    while (c < 100000000) {
      map = new HashMap<>();
      for (int j = 1; j < c+1; j++) {
        key = j;
        da = guess2LgN(c, learn);
        map.put(1.*c, da[2]);
      }  
      smap = reverseSortByValue(map);
      for (Double d : smap.keySet()) {
        System.out.printf("%s%-8d  %f\n", "N=",c,smap.get(d));
        break;
      }
      c*=10;
    }
  }
  
  public static void printBinSearchMetricAtPowersOf10() {
    // prints the max metric from binSearch output for N equal to powers 
    // of 10 ranging from 10**1 to 10**8 over all keys from 1 to N using
    // int arrays constructed with range(1, N+1) = int[1,2,3,...,N].
    double[] da;
    HashMap<Double, Double> map = null;
    Map<Double, Double> smap = null;
    int c = 10;

    while (c < 100000000) {
      map = new HashMap<>();
      for (int j = 1; j < c+1; j++) {
        key = j;
        da = binSearch(range(1,c+1), j);
        map.put(1.*c, da[2]);
      }  
      smap = reverseSortByValue(map);
      for (Double d : smap.keySet()) {
        System.out.printf("%s%-8d  %f\n", "N=",c,smap.get(d));
        break;
      }
      c*=10;
    }
  }
  
  public static void printGuess1LgNTableOfResults(int start, int end) {
    // prints N, key and metric from guess1LgN output for all N from 
    // start to end for all keys from 1 to N reverse sorted by metric.
    double[] da;
    HashMap<Tuple2<Integer,Integer>, Double> map = new HashMap<>();
    Map<Tuple2<Integer,Integer>, Double> smap = null;

    for (int i = start; i < end+1; i++) {
      for (int j = 1; j < i+1; j++) {
        key = j;
        da = guess1LgN(i, learn);
        map.put(new Tuple2<Integer,Integer>(i,j), da[2]);
      }
    }

    smap = reverseSortByValue(map);
    for (Tuple2<Integer,Integer> d : smap.keySet()) {
      System.out.printf("%-8d%-8d  %f\n", d._1, d._2, smap.get(d));
    }
  }
  
  public static void printGuess2LgNTableOfResults(int start, int end) {
    // prints N, key and metric from guess2LgN output for all N from 
    // start to end for all keys from 1 to N reverse sorted by metric.
    double[] da;
    HashMap<Tuple2<Integer,Integer>, Double> map = new HashMap<>();
    Map<Tuple2<Integer,Integer>, Double> smap = null;

    for (int i = start; i < end+1; i++) {
      for (int j = 1; j < i+1; j++) {
        key = j;
        da = guess2LgN(i, learn);
        map.put(new Tuple2<Integer,Integer>(i,j), da[2]);
      }
    }

    smap = reverseSortByValue(map);
    for (Tuple2<Integer,Integer> d : smap.keySet()) {
      System.out.printf("%-8d%-8d  %f\n", d._1, d._2, smap.get(d));
    }
  }
  
  public static void printBinSearchTableOfResults(int start, int end) {
    // prints N, key and metric from binSearch output for all N from start
    // to end for all keys from 1 to N reverse sorted by metric using int
    // arrays constructed with range(1, N+1) = int[1,2,3,...,N].
    double[] da;
    HashMap<Tuple2<Integer,Integer>, Double> map = new HashMap<>();
    Map<Tuple2<Integer,Integer>, Double> smap = null;

    for (int i = start; i < end+1; i++) {
      for (int j = 1; j < i+1; j++) {
        key = j;
        da = binSearch(range(1, i+1), j);
        map.put(new Tuple2<Integer,Integer>(i,j), da[2]);
      }
    }

    smap = reverseSortByValue(map);
    for (Tuple2<Integer,Integer> d : smap.keySet()) {
      System.out.printf("%-8d%-8d  %f\n", d._1, d._2, smap.get(d));
    }
  }
  
  public static void main(String[] args) {
    
//    key =7;
//    double[] r = guess1LgN(100, learn);
//    pa(r); 
    
    
//    Tuple3<Double,Integer,Integer> t = findMaxguess2LgNMetric(1000);
//    System.out.println(t); //(1.0092454329104992,31,12)
//    
//    List<Tuple3<Double, Integer, Integer>> list = findguess2LgNMetricOver1(1000);
//    System.out.println(list); //[]    
    
//    Timer timer = new Timer();
//    timer.stop();
//    int x = 10;
//    while(x < 1000000) {
//      timer.reset();
//      printBinSearchMaxMetric(1,x);
//      timer.stop();
//      x*=10;
//    }
    //  1.2041199826559246
    //  47 ms
    //  1.0536049848239342
    //  0 ms
    //  1.0034333188799374
    //  78 ms
    //  1.0536049848239342
    //  4992 ms
    //  1.023501985257536
    //  405196 ms

//    timer = new Timer();
//    timer.stop();
//    int x = 10;
//    while(x < 100000) {
//      timer.reset();
//      printguess2LgNMaxMetric(1,x);
//      timer.stop();
//      x*=10;
//    }
    //  0 ms
    //  1.0
    //  16 ms
    //  1.0
    //  16 ms
    //  1.0
    //  140 ms
    //  1.0
    //  15335 ms
    
//    timer = new Timer();
//    timer.stop();
//    int x = 10;
//    while(x < 1000000) {
//      timer.reset();
//      printguess1LgNMaxMetric(1,x);
//      timer.stop();
//      x*=10;
//    } // this gives exactly the same metric maxes as binSearch
    //  0 ms
    //  1.2041199826559246
    //  31 ms
    //  1.0536049848239342
    //  0 ms
    //  1.0034333188799374
    //  140 ms
    //  1.0536049848239342
    //  13369 ms
    
    //  test results for guess2LgN for from N=1 through N=10
    //  metric = numberOf_learn.apply_Executions/(2*lg(N))
    //  N=01, metric=Infinity, key=1
    //
    //  N=02, metric=0.500000, key=1
    //  N=02, metric=1.000000, key=2
    //
    //  N=03, metric=0.630930, key=1
    //  N=03, metric=0.315465, key=2
    //  N=03, metric=0.946395, key=3
    //
    //  N=04, metric=0.500000, key=1
    //  N=04, metric=0.250000, key=2
    //  N=04, metric=0.500000, key=3
    //  N=04, metric=1.000000, key=4
    //
    //  N=05, metric=0.430677, key=1
    //  N=05, metric=0.646015, key=2
    //  N=05, metric=0.215338, key=3
    //  N=05, metric=0.430677, key=4
    //  N=05, metric=0.861353, key=5
    //
    //  N=06, metric=0.386853, key=1
    //  N=06, metric=0.580279, key=2
    //  N=06, metric=0.193426, key=3
    //  N=06, metric=0.386853, key=4
    //  N=06, metric=0.580279, key=5
    //  N=06, metric=0.967132, key=6
    //
    //  N=07, metric=0.356207, key=1
    //  N=07, metric=0.534311, key=2
    //  N=07, metric=0.712414, key=3
    //  N=07, metric=0.178104, key=4
    //  N=07, metric=0.356207, key=5
    //  N=07, metric=0.534311, key=6
    //  N=07, metric=0.890518, key=7
    //
    //  N=08, metric=0.333333, key=1
    //  N=08, metric=0.500000, key=2
    //  N=08, metric=0.666667, key=3
    //  N=08, metric=0.166667, key=4
    //  N=08, metric=0.666667, key=5
    //  N=08, metric=0.333333, key=6
    //  N=08, metric=0.500000, key=7
    //  N=08, metric=0.833333, key=8
    //
    //  N=09, metric=0.315465, key=1
    //  N=09, metric=0.473197, key=2
    //  N=09, metric=0.630930, key=3
    //  N=09, metric=0.473197, key=4
    //  N=09, metric=0.157732, key=5
    //  N=09, metric=0.315465, key=6
    //  N=09, metric=0.473197, key=7
    //  N=09, metric=0.630930, key=8
    //  N=09, metric=0.946395, key=9
    //
    //  N=10, metric=0.301030, key=1
    //  N=10, metric=0.451545, key=2
    //  N=10, metric=0.602060, key=3
    //  N=10, metric=0.451545, key=4
    //  N=10, metric=0.150515, key=5
    //  N=10, metric=0.752575, key=6
    //  N=10, metric=0.301030, key=7
    //  N=10, metric=0.451545, key=8
    //  N=10, metric=0.602060, key=9
    //  N=10, metric=0.903090, key=10
    
//    double[] da;
//    HashMap<Double, Double> map = null;
//    Map<Double, Double> smap = null;
//    int c = 10;
//    
//    while (c < 100000000) {
//      map = new HashMap<>();
//      for (int j = 1; j < c+1; j++) {
//        key = j;
//        da = guess1LgN(c, learn);
//        map.put(1.*c, da[1]);
//      }  
//      smap = reverseSortByValue(map);
//      for (Double d : smap.keySet()) {
//        System.out.printf("%s%-8d  %f\n", "N=",c,smap.get(d));
//        break;
//      }
//      c*=10;
//    }
        
//  printGuess2LgNMetricAtPowersOf10();
    //  test results for guess2LgN for powers of 10 1-7.
    //  prints highest metric value for N over all keys 1-N.
    //  metric = numberOf_learn.apply_Executions/(lg(N)).
    //  N=10        0.903090
    //  N=100       0.827832
    //  N=1000      0.802747
    //  N=10000     0.790204
    //  N=100000    0.782678
    //  N=1000000   0.752575
    //  N=10000000  0.752575
    
//  printGuess1LgNMetricAtPowersOf10();
    //  test results for guess1LgNA for powers of 10 1-7.
    //  prints highest metric value for N over all keys 1-N.
    //  metric = numberOf_learn.apply_Executions/lg(N).
    //  N=10        1.204120
    //  N=100       1.053605
    //  N=1000      1.003433
    //  N=10000     1.053605
    //  N=100000    1.023502
    //  N=1000000   1.003433
    //  N=10000000  1.032103
    
//  printBinSearchMetricAtPowersOf10(); 
    //  test results for binSearch for powers of 10 1-7.
    //  prints highest metric value for N over all keys 1-N.
    //  metric = numberOfTestsOfMidAgainstKey/lg(N).
    //  identical to printGuess1LgNMetricAtPowersOf10 output.
    //  this ran very slowly compared to printGuess1LgNMetricAtPowersOf10.
    //  N=10        1.204120
    //  N=100       1.053605
    //  N=1000      1.003433
    //  N=10000     1.053605
    //  N=100000    1.023502
    //  N=1000000   1.003433
    //  N=10000000  1.032103

//    printGuess2LgNTableOfResults(1,5);
    // N, key and metric for guess2LgN
    //  N       key       metric
    //  1       1         Infinity
    //  2       2         1.000000 // occurs since lg(1) = 0;
    //  4       4         1.000000
    //  3       2         0.946395
    //  5       5         0.861353
    //  5       2         0.646015
    //  3       1         0.630930
    //  2       1         0.500000
    //  4       3         0.500000
    //  4       1         0.500000
    //  5       4         0.430677
    //  5       1         0.430677
    //  3       3         0.315465
    //  4       2         0.250000
    //  5       3         0.215338
    
//    printGuess1LgNTableOfResults(1,5);
    // N, key and metric for guess1LgN
    //  N       key       metric
    //  1       1         Infinity // occurs since lg(1) = 0;
    //  2       2         2.000000
    //  4       4         1.500000
    //  5       5         1.292030
    //  5       2         1.292030
    //  3       3         1.261860
    //  3       1         1.261860
    //  2       1         1.000000
    //  4       3         1.000000
    //  4       1         1.000000
    //  5       4         0.861353
    //  5       1         0.861353
    //  3       2         0.630930
    //  4       2         0.500000
    //  5       3         0.430677
   
//    printBinSearchTableOfResults(1,5);
    // N, key and metric for guess1LgN
    // identical to printGuess1LgNTableOfResults(1,5) results above.
    //  1       1         Infinity // occurs since lg(1) = 0;
    //  2       2         2.000000
    //  4       4         1.500000
    //  5       5         1.292030
    //  5       2         1.292030
    //  3       3         1.261860
    //  3       1         1.261860
    //  2       1         1.000000
    //  4       3         1.000000
    //  4       1         1.000000
    //  5       4         0.861353
    //  5       1         0.861353
    //  3       2         0.630930
    //  4       2         0.500000
    //  5       3         0.430677

  }

}
