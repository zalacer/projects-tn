package ex25;

import static v.ArrayUtils.*;

import java.util.Arrays;

/* p356
  2.5.21 Multidimensional sort. Write a  Vector data type for use in having 
  the sorting methods sort multidimensional vectors of d integers, putting 
  the vectors in order by first component, those with equal first component 
  in order by second component, those with equal first and second components
  in order by third component, and so forth.
  
  Since java vectors and array lists are array based their sizes are integers 
  and they're multidimensionially limited in the sense of this exercise, but
  Integer.MAX_VALUE is enough dimensions for most purposes. On the hand there
  have already been enough exercises that required a comparator structurally 
  similar to the one needed for this exercise.
   
 */

public class Ex2521MultidimensionalSort {
  
  public static class Vec<V extends Comparable<? super V>> implements Comparable<Vec<V>> {
    private V[] vec;
    private final int dim; // length of vec == # dimensions
    
    public Vec(V[] a) { 
      if (a == null) throw new IllegalArgumentException(
          "Vec: array arg must be nonnull");
      // disallowing nulls for compareTo() to always work
      if (hasNull(a)) throw new IllegalArgumentException(
          "Vec: array arg must not contain a null");
      this.vec = a; dim = a.length;
    }
    
    public V[] getArray() { return vec; }
    
    public int getDim() { return dim; }
 
    @Override 
    public int compareTo(Vec<V> w) {
      // same pattern as Domain.compareTo()
      V[] wec = w.getArray();
      int eim = w.getDim();
      for (int i = 0; i < Math.min(dim, eim); i++) {
        int d = vec[i].compareTo(wec[i]);
        if (d < 0) return -1;
        if (d > 0) return +1;
      }
      return dim - eim;
    }    
  
    @Override
    public String toString() {
      if (dim == 0) return "()";
      StringBuilder sb = new StringBuilder(); sb.append("(");
      for (int i = 0; i < dim-1; i++) sb.append(vec[i]+",");
      sb.append(vec[dim-1]+")");
      return sb.toString();
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + dim;
      result = prime * result + Arrays.hashCode(vec);
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
      @SuppressWarnings("rawtypes")
      Vec other = (Vec) obj;
      if (dim != other.dim)
        return false;
      if (!Arrays.equals(vec, other.vec))
        return false;
      return true;
    }
  }

  public static void main(String[] args) {
    
    Integer[] a = {1,2,3,4,5}; 
    Vec<Integer> x = new Vec<>(a); 
    
    Integer[] b = {1,2,3,5,5};
    Vec<Integer> y = new Vec<>(b); 
    
    System.out.println(x.compareTo(y)); // -1
    
 
  }

}


