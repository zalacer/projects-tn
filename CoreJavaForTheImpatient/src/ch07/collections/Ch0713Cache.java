package ch07.collections;

import java.util.LinkedHashMap;
import java.util.Map;

//13. The LinkedHashMap calls the method removeEldestEntry whenever a new
//element is inserted. Implement a subclass Cache that limits the map to a given size
//provided in the constructor.

public class Ch0713Cache extends LinkedHashMap<String, Integer>{
  private static final long serialVersionUID = -9093057372588208066L;
  private final int n;

  Ch0713Cache(int n) {
    super(n);
    this.n = n;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
    return size() > n;
  }


  public static void main(String[] args) {

    Ch0713Cache cache = new Ch0713Cache(3);
    cache.put("one", 1);
    cache.put("two", 2);
    cache.put("three", 3);
    cache.put("four", 4);
    System.out.println(cache);
    // {two=2, three=3, four=4}
    cache.put("five", 5);
    // {three=3, four=4, five=5}
    System.out.println(cache);
    cache.put("six", 6);
    System.out.println(cache);
    // {four=4, five=5, six=6}
  }

}
