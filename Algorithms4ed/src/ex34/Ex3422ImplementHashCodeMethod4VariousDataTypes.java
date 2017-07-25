package ex34;

import static analysis.Log.*;
import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Iterator;

import ds.Queue;
import st.LinearProbingHashSTX;

/* p482
  3.4.22  Implement hashCode() for various types: Point2D, Interval, 
  Interval2D, and Date 
  
  Point2D: (http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/Point2D.java.html)
  this was already implemented as:
    public int hashCode() {
      int hashX = ((Double) x).hashCode();
      int hashY = ((Double) y).hashCode();
      return 31*hashX + hashY;
    }
  
    A Java way for doing this more simply as recommended by Cay S. HorstMann is:
      public int hashCode() { return Objects.hash(x,y); }
  
  Interval: (http://introcs.cs.princeton.edu/java/32class/Interval.java.html)
  this was already implemented as:
    public int hashCode() {
      int hash1 = ((Double) min).hashCode();
      int hash2 = ((Double) max).hashCode();
      return 31*hash1 + hash2;
    }
    
    Alternatively:
      public int hashCode() { return Objects.hash(min,max); }
      
  Interval2D: (http://algs4.cs.princeton.edu/12oop/Interval2D.java.html)
  this was already implemented as:
    public int hashCode() {
      int hash1 = x.hashCode();
      int hash2 = y.hashCode();
      return 31*hash1 + hash2;
    }
    
    Alternatively:
      public int hashCode() { return Objects.hash(x,y); }
    
  Date: (http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/Date.java.html)
  this was already implemented as:
    public int hashCode() {
      int hash = 17;
      hash = 31*hash + month;
      hash = 31*hash + day;
      hash = 31*hash + year;
      return hash;
    }
    
    Alternatively: public int hashCode() { return Objects.hash(month,day,year); }
  
*/



@SuppressWarnings("unused")
public class Ex3422ImplementHashCodeMethod4VariousDataTypes {
  

  public static void main(String[] args) {
    


  }

}

