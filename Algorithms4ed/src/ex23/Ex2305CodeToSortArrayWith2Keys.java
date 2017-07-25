package ex23;

import static v.ArrayUtils.par;

import java.util.function.BiConsumer;

public class Ex2305CodeToSortArrayWith2Keys {

  /* p303  
  2.3.5  Give a code fragment that sorts an array that is known to consist 
  of items having just two distinct keys.
  */
 
  public static void main(String[] args) {
    
    String[] w = "ababababababababababba".split("");
    BiConsumer<Integer,Integer> swap;
    
    if (w == null) System.exit(0);
    else if (w.length < 2) { par(w); par(w); System.exit(0); }
    
    String[] z = w.clone(); 
    swap = (e,f) -> { String s = z[e]; z[e] = z[f]; z[f] = s; };
    
    // fragment 1
    int left = 0, right = z.length - 1;
    int i = 1;
    while (i <= right) {
      int c = z[i].compareTo(z[left]);
      if (c == 0) i++;
      else if (c < 0 ) swap.accept(left++, i++); 
      else swap.accept(i, right--); 
    }
    par(z);
     
    String[] y = w.clone();
    swap = (e,f) -> { String s = y[e]; y[e] = y[f]; y[f] = s; };
    
    // fragment 2
    // set the pivot p to the first smallest value and set its index to 0 if the array
    // began with the largest value after swapping it with the first smallest value.
    String p = null; left = 0; right = y.length-2; 
    for (; left <= right; left++) {
      int c = y[left].compareTo(y[left+1]);
      if (c == 0) { if (left == right) return; else continue; }
      else if (c < 0) { p = y[left]; left++; break; }
      else { swap.accept(0,left+1); p = y[0]; left = 1; break; }
    }
    right++;
    // move upwards from the index after that of the pivot and swap larger
    // values with smaller values from the end down and adjust c.
    for (; left <= right; left++)
      if (y[left].compareTo(p) > 0)
        for (int j = right; j > left; j--)
          if (y[j].equals(p)) { swap.accept(left,j); right = --j; break; }
    par(y);
   
  }
                      
}

