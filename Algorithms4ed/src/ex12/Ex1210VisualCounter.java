package ex12;

import java.util.Objects;

import edu.princeton.cs.algs4.StdDraw;

//  1.2.10  Develop a class  VisualCounter that allows both increment and decrement
//  operations. Take two arguments  N and  max in the constructor, where  N specifies the
//  maximum number of operations and  max specifies the maximum absolute value for
//  the counter. As a side effect, create a plot showing the value of the counter each time its
//  tally changes.


public class Ex1210VisualCounter {

  public static class VisualCounter {
    private final int N;
    private final int max;
    private int count = 0;
    private int ops = 0;

    public VisualCounter(int N, int max) {
      this.N = N > 0 ? N : 0;
      this.max = abs(max);
      StdDraw.setXscale(0, N+2);
      StdDraw.setYscale(-max-2, max+2);
      StdDraw.setPenRadius(.01);
    }
    
    public int abs(int x) {
      return x >= 0 ? x : -x;
    }

    public void increment() {
      if (ops < N) {
        if (count < max) {
          ops++; count++;
          StdDraw.setPenColor(StdDraw.BLACK);
        } else {
          System.err.println("counter at upper limit: can only decrement it");
          StdDraw.setPenColor(StdDraw.ORANGE);
        }
      } else {
        System.err.println("counter max ops reached; use a new counter");
        StdDraw.setPenColor(StdDraw.RED);
      }    
      StdDraw.point(ops, count);
    }
    
    public void decrement() {
      if (ops < N) {
        if (count > -max) {
          ops++; count--;
          StdDraw.setPenColor(StdDraw.BLACK);
        } else {
          System.err.println("counter at lower limit: can only increment it");
          StdDraw.setPenColor(StdDraw.ORANGE);
        }
      } else {
        System.err.println("counter max ops reached; use a new counter");
        StdDraw.setPenColor(StdDraw.RED);
      }
      StdDraw.point(ops, count);
    }
    
    public int getN() {
      return N;
    }

    public int getMax() {
      return max;
    }

    public int getCount() {
      return count;
    }

    public int getOps() {
      return ops;
    }

    @Override
    public int hashCode() {
      return Objects.hash(N, count, max, ops);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      VisualCounter other = (VisualCounter) obj;
      if (N != other.N)
        return false;
      if (count != other.count)
        return false;
      if (max != other.max)
        return false;
      if (ops != other.ops)
        return false;
      return true;
    }

    public String toString() {
      return "VisualCounter(count="+count+",max="+max+",ops="+ops+",N="+N+")";
    } 
   
  }

  public static class Accumulator {
    private double total;
    private int N;
    public void addDataValue(double val) {
      N++;
      total += val;
    }
    public double mean() { 
      return total/N; 
    }

    public String toString() { return "Mean (" + N + " values): "
        + String.format("%7.5f", mean()); 
    }
  }


  public static void main(String[] args) {

    VisualCounter v = new VisualCounter(10,5);
    v.increment();v.increment();v.increment();v.increment();v.increment();
    v.increment(); //counter at upper limit: can only decrement it
    v.decrement(); v.decrement(); v.decrement(); v.decrement(); v.decrement(); 
    v.decrement(); //counter max ops reached; use a new counter
    System.out.println(v); //VisualCounter(count=0,max=5,ops=10,N=10)
    System.out.println(v.getCount()); //0
    System.out.println(v.getMax()); //5
    System.out.println(v.getOps()); //10
    System.out.println(v.getN()); //10
  }

}
