package ex22;

import static sort.BlockMerge.isSorted;
import static sort.BlockMerge.sort;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;

import java.util.Random;

public class Ex2212MergeSortSublinearExtraSpace {

/* p285
  2.2.12 Sublinear extra space. Develop a merge implementation that reduces the extra
  space requirement to max(M, N/M), based on the following idea: Divide the array into
  N/M blocks of size M (for simplicity in this description, assume that N is a multiple
  of M). Then, (i) considering the blocks as items with their first key as the sort key, 
  sort them using selection sort; and (ii) run through the array merging the first block 
  with the second, then the second block with the third, and so forth.
  
  This is done in sort.BlockMerge with demo below. 
*/ 

  public static void main(String[] args) {

    Random r; Integer[] w;
    
    for (int i = 2; i < 1002; i++) {
      w = rangeInteger(1, i, 1);
      r = new Random(System.currentTimeMillis());
      shuffle(w, r); 
      sort(w);
      assert isSorted(w);
    }

  }

}
