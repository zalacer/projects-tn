package ex31;

/* p391
  3.1.25 Software caching. Since the default implementation of contains() 
  calls get(), the inner loop of  FrequencyCounter
    if (!st.contains(word)) st.put(word, 1);
    else st.put(word, st.get(word) + 1);
  leads to two or three searches for the same key. To enable clear client 
  code like this without sacrificing efficiency, we can use a technique 
  known as software caching, where we save the location of the most 
  recently accessed key in an instance variable. Modify SequentialSearchST 
  and BinarySearchST to take advantage of this idea.
  
  This is done in st.SequentialSearchSTex3125 and BinarySearchSTex3125.
  In the former a private Node recent = null field was added and in the latter
  a private Tuple2<Key,Integer> recent = null field was added. In both, get(),
  put() and delete() were modified.
  
*/

public class Ex3125SoftwareCachingForMostRecentlyAccessedKey {
  
  public static void main(String[] args) {
 
  }
}
