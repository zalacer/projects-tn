package ch10.concurrency;

import java.util.concurrent.ConcurrentHashMap;

// 7. In a ConcurrentHashMap<String, Long>, find the key with
//  maximum value (breaking ties arbitrarily). Hint: reduceEntries.

public class Ch1007ConcurrentHashMapMaxKey {

  private static String findKeyWithMaxValueUsingReduceEntries(
      ConcurrentHashMap<String,Long> map) {
    return map.reduceEntries(3, (e1, e2) -> 
    e1.getValue() >= e2.getValue() ? e1 : e2).getKey();  
  }

  public static void main(String[] args) {

    ConcurrentHashMap<String, Long> chm = new ConcurrentHashMap<>();

    chm.put("e", 5L); chm.put("a", 1L); chm.put("d", 3L); 
    chm.put("c", 9L); chm.put("b", 7L);

    System.out.println(findKeyWithMaxValueUsingReduceEntries(chm)); 
    // c


  }

}
