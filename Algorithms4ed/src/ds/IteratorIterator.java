package ds;

import static v.ArrayUtils.*;
import java.util.Iterator;
import java.util.Arrays; 

// from https://stackoverflow.com/questions/3610261/is-it-possible-to-merge-iterators-in-java
//   re. answer by Christoffer.

public class IteratorIterator<T> implements Iterator<T> {
    private final Iterator<T> is[];
    private int current = 0;

    @SafeVarargs
    public IteratorIterator(Iterator<T>... iterators) {
            is = iterators;
    }

    public boolean hasNext() {
            while ( current < is.length && !is[current].hasNext() )
                    current++;

            return current < is.length;
    }

    public T next() {
            while ( current < is.length && !is[current].hasNext() )
                    current++;

            return is[current].next();
    }

    public void remove() { /* not implemented */ }

    @SuppressWarnings("unchecked")
    public static <X> Iterator<X> itx(Iterator<X>[] ita) {
      // return an iterator of X spanning multiple iterators of X.
      if (ita == null || ita.length == 0) 
        return (Iterator<X>)Arrays.stream(ofDim(Iterator.class,0)).iterator();
      return new Iterator<X>() {
        private int i = 0;
        public boolean hasNext() {
          while ( i < ita.length && !ita[i].hasNext()) i++;
          return i < ita.length;
        }
        public X next() {
          while ( i < ita.length && !ita[i].hasNext()) i++;
          return ita[i].next();
        }
      };
    }
      

  @SuppressWarnings("unused")
  public static void main(String[] args) {
   
    Iterator<Integer> a = Arrays.asList(1,2,3,4).iterator();
    Iterator<Integer> b = Arrays.asList(10,11,12).iterator();
    Iterator<Integer> c = Arrays.asList(99, 98, 97).iterator();
    Iterator<Integer> d = Arrays.stream(new Integer[]{}).iterator();
    
    Iterator<Integer>[] ita = ofDim(Iterator.class,3);
    ita[0] = a; ita[1] = b; ita[2] = c;
    
    Iterator<Integer>[] itd = ofDim(Iterator.class,1);
    itd[0] = d;
    
    Iterator<Integer> ii = new IteratorIterator<Integer>(a,b,c);
    Iterator<Integer> ii2 = itx(ita);
    Iterator<Integer> ii3 = itx(itd);
    Iterator<Integer> ii4 = itx((Iterator<Integer>[])null);
    
    while (ii4.hasNext()) System.out.println(ii4.next());
  }

}
