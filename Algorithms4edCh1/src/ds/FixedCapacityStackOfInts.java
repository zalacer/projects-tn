package ds;

import java.util.Arrays;
import java.util.NoSuchElementException;

// for Ex1437AutoboxingPerformancePenalty p214
  
  public class FixedCapacityStackOfInts {
    private int[] a;
    private int N;
    
    public FixedCapacityStackOfInts(int cap) { 
      this.a = new int[cap]; 
    }
    
    @SafeVarargs
    public FixedCapacityStackOfInts(int cap, int...items) { 
      this.a = items; 
    }
    
    public boolean isEmpty() { return N == 0; }
    
    public int size() { return N; }
    
    public void push(int item) {
      if (N == a.length) throw new StackOverflowError();
      a[N++] = item; 
    }
    
    public int pop() {
      if (isEmpty()) throw new NoSuchElementException("Stack underflow");
      return a[--N]; 
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + N;
      result = prime * result + Arrays.hashCode(a);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      FixedCapacityStackOfInts other = (FixedCapacityStackOfInts) obj;
      if (N != other.N)
        return false;
      if (!Arrays.equals(a, other.a))
        return false;
      return true;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("FixedCapacityStack(");
      for (int i : a) sb.append(""+i+",");
      return sb.substring(0, sb.length()-1)+")";
    }

  public static void main(String[] args) {

  }

}
