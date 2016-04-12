package ch08.streams;

import java.util.stream.Stream;

import utils.Averager;

// 14. Write a call to reduce that can be used to compute the average of a
// Stream<Double>. Why can’t you simply compute the sum and divide by
// count()?

// You can if you can figure out how to get them both in one pass. One way
// to do this is to accumulate the sum in an external array element and 
// terminate the stream with count(). Another way is to accumulate the count
// in an external array element and terminate the stream with reduce to 
// accumulate the sum. There is also the method of using a custom class
// to keep track of the sum and the count with reduce.

public class Ch0814AverageReduce {

  // using a custom class to keep track of the sum 
  // and the count with reduce
  public static double streamAverage(Stream<Double> stream) {
    return stream.reduce(new Averager(), Averager::accept, Averager::combine).average();
  }

  // accumulating sum in an external array element 
  // and terminating the stream with count
  public static double streamAverage2(Stream<Double> s) {
    double[] d = new double[1];
    long count = s.filter(x->{d[0]+=x;return true;}).count();
    return d[0]/count;
  }

  // accumulating the count in an external array element 
  // and terminating the stream with reduction to sum
  public static double streamAverage3(Stream<Double> s) {
    long[] l = new long[1];
    Double sum = s.filter(x->{l[0]++;return true;}).reduce(0.,Double::sum);
    return sum/l[0];
  }

  public static void main(String[] args) {

    Stream<Double> s = Stream.of(1.,2.,3.,4.,5.,6.,7.,8.,9.);
    System.out.println(streamAverage(s));  // 5.0

    Stream<Double> t = Stream.of(1.,2.,3.,4.,5.,6.,7.,8.,9.);
    System.out.println(streamAverage2(t)); // 5.0

    Stream<Double> u = Stream.of(1.,2.,3.,4.,5.,6.,7.,8.,9.);
    System.out.println(streamAverage3(u)); // 5.0

  }

}
