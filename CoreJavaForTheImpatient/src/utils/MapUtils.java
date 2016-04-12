package utils;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class MapUtils {

  public static <K,V> void printMap(Map<K,V> map) {
    int max = 0;
    for (K k : map.keySet())
      if (k.toString().length() > max) max = k.toString().length();  
    for (Map.Entry<K, V> e : map.entrySet())
      System.out.printf("%-"+max+"s : %s\n", e.getKey(), e.getValue());
  }

  public static <K,V> void printMapWithArrows(Map<K,V> map) {
    int max = 0;
    for (K k : map.keySet())
      if (k.toString().length() > max) max = k.toString().length();  
    for (Map.Entry<K, V> e : map.entrySet())
      System.out.printf("%-"+max+"s -> %s\n", e.getKey(), e.getValue());
  }

  public static <K,L,V> void printMapOfMapsWithArrows(Map<K,Map<L,V>> map) {
    int max = 0;
    for (K k : map.keySet())
      if (k.toString().length() > max) max = k.toString().length();  
    for (Map.Entry<K, Map<L,V>> e : map.entrySet())
      System.out.printf("%-"+max+"s -> %s\n", e.getKey(), mapWithArrows1Line2String(e.getValue()));
  }


  @SuppressWarnings("unchecked")
  public static <K,V> void printMapWithArrows1Line(Map<K,V> map) {
    int size =  map.keySet().size();
    Object[] keys =  map.keySet().toArray(new Object[size]);
    for (int i = 0; i < size - 1; i++) {
      System.out.printf(((K)keys[i])+" -> "+map.get(((K)keys[i])+", "));
    }
    System.out.println(((K)keys[size - 1])+" -> "+map.get(((K)keys[size - 1])));
  }

  @SuppressWarnings("unchecked")
  public static <K,V> String mapWithArrows1Line2String(Map<K,V> map) {
    StringBuilder sb = new StringBuilder();
    int size =  map.keySet().size();
    Object[] keys =  map.keySet().toArray(new Object[size]);
    for (int i = 0; i < size - 1; i++) {
      sb.append(((K)keys[i])+" -> "+map.get(((K)keys[i])+", "));
    }
    sb.append(((K)keys[size - 1])+" -> "+map.get(((K)keys[size - 1])));
    return sb.toString();
  }

  public static <K,V> void printMap(TreeMap<K,V> map) {
    int max = 0;
    for (K k : map.keySet())
      if (k.toString().length() > max) max = k.toString().length();  
    for (Map.Entry<K, V> e : map.entrySet())
      System.out.printf("%-"+max+"s : %s\n", e.getKey(), e.getValue());
  }

  public static <K,V> void printMapWithArrows(TreeMap<K,V> map) {
    int max = 0;
    for (K k : map.keySet())
      if (k.toString().length() > max) max = k.toString().length();  
    for (Map.Entry<K, V> e : map.entrySet())
      System.out.printf("%-"+max+"s -> %s\n", e.getKey(), e.getValue());
  }

  @SuppressWarnings("unchecked")
  public static <K,V> void printMapWithArrows1Line(TreeMap<K,V> map) {
    int size =  map.keySet().size();
    Object[] keys =  map.keySet().toArray(new Object[size]);
    for (int i = 0; i < size - 1; i++) {
      System.out.printf(((K)keys[i])+" -> "+map.get(((K)keys[i])+", "));
    }
    System.out.println(((K)keys[size - 1])+" -> "+map.get(((K)keys[size - 1])));
  }

  @SuppressWarnings("unchecked")
  public static <K,V> String mapWithArrows1Line2String(TreeMap<K,V> map) {
    StringBuilder sb = new StringBuilder();
    int size =  map.keySet().size();
    Object[] keys =  map.keySet().toArray(new Object[size]);
    for (int i = 0; i < size - 1; i++) {
      sb.append(((K)keys[i])+" -> "+map.get(((K)keys[i]))+", ");
    }
    sb.append(((K)keys[size - 1])+" -> "+map.get(((K)keys[size - 1])));
    return sb.toString();
  }

  public static <K,L,V> void printMapOfMapsWithArrows(TreeMap<K,TreeMap<L,V>> map) {
    int max = 0;
    for (K k : map.keySet())
      if (k.toString().length() > max) max = k.toString().length();  
    for (Map.Entry<K, TreeMap<L,V>> e : map.entrySet())
      System.out.println(e.getKey()+" -> {"+mapWithArrows1Line2String(e.getValue())+"}");
  }

  //Ch0807Tokens2
  
  public static boolean isWord(String s) {
    if (s.length() == 0) return false;
    return s.codePoints().allMatch(Character::isAlphabetic);
  }

  public static void printTopN(String fileName, int n) {
    Comparator<Entry<String, Long>> comp = (e1, e2) -> {
      int c2 = e2.getValue().compareTo(e1.getValue());
      int c1 = e1.getKey().compareTo(e1.getKey());
      return c2 == 0 ? c1 : c2;
    };
    try {
      Files.newBufferedReader(Paths.get(fileName)).lines()
      .map(l -> l.split("\\s+")).flatMap(Arrays::stream).filter(w -> isWord(w))
      .map(String::toLowerCase).collect(groupingBy(identity(), counting()))
      .entrySet().stream().sorted(comp).limit(n).forEach(System.out::println);
    } catch (IOException e) {
      e.printStackTrace();
    }  
  }

  // http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
  public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ) {
    Map<K,V> result = new LinkedHashMap<>();
    Stream <Entry<K,V>> st = map.entrySet().stream();
    st.sorted(Comparator.comparing(e -> e.getValue()))
    .forEachOrdered(e ->result.put(e.getKey(),e.getValue()));
    return result;
  }

  // http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
  public static <K, V extends Comparable<? super V>> NavigableMap<K, V> reverseSortByValue( Map<K, V> map ) {
    TreeMap<K,V> result = new TreeMap<>();
    Stream <Entry<K,V>> st = map.entrySet().stream();
    st.sorted(Comparator.comparing(e -> e.getValue()))
    .forEachOrdered(e ->result.put(e.getKey(),e.getValue()));
    return result.descendingMap();
  }

  // http://www.programcreek.com/2013/03/java-sort-map-by-value/
  public static <K,V extends Comparable<V>> Map<K,V> sortByValue2(Map<K,V> unsortedMap) {
    Map<K,V> sortedMap = new TreeMap<K,V>(new ValueComparator<K,V>(unsortedMap));
    sortedMap.putAll(unsortedMap);
    return sortedMap;
  }

  // http://www.programcreek.com/2013/03/java-sort-map-by-value/
  public static class ValueComparator<K,V extends Comparable<V>> implements Comparator<K> {      
    Map<K,V> map;

    public ValueComparator(Map<K,V> map) {
      this.map = map;
    }

    public int compare(K keyA, K keyB) {
      V valueA = map.get(keyA);
      V valueB = map.get(keyB);
      return valueB.compareTo(valueA);
    }
  }

  public static void main(String[] args) {
    
    String s1 = "2,1,4,1,5,3,7,1,8,3,9,6,11,1,12,3,13,6,14,10,16,1,17,3,18,6,19,10,20,15,21,15,22,10,23,15,24,6,25,10,26,15";
    String[] sa1 = s1.split(",");
    System.out.println("sa1.length="+sa1.length); //42
    String s2 = "2,1,4,1,5,3,7,1,8,3,9,6,11,1,12,3,13,6,14,10,16,1,17,3,18,6,19,10,20,15,21,15,22,10,23,15,24,6,25,10,26,15,28,3";
    String[] sa2 = s2.split(","); //44
    System.out.println("sa2.length="+sa2.length);
    
    String s3 = "2,1,4,1,5,3,7,1,8,3,9,6,11,1,12,3,13,6,14,10,16,1,17,3,18,6,19,10,20,15,21,15,22,10,23,15,24,6,25,10,26,15,27,6,28,152,1,4,1,5,3,7,1,8,3,9,6,11,1,12,3,13,6,14,10,16,1,17,3,18,6,19,10,20,15,21,15,22,10,23,15,24,6,25,10,26,15,27,6,28,15,30,3,31,10,32,1";
    String[] sa = s3.split(","); 
    System.out.println(sa.length); //97
    System.out.println(Arrays.toString(sa));
    
//    TreeMap<String,Integer> tmap1 = new TreeMap<>();
//    //    Map<String,Integer> tmap1 = new HashMap<>();
//    tmap1.put("one", 1); tmap1.put("two", 2); tmap1.put("three", 3);
//    String tmapString = mapWithArrows1Line2String(tmap1);
//    System.out.println(tmapString);
//    System.out.println(tmap1.getClass().getSimpleName()); // TreeMap, HashMap
//
//    System.out.println(tmap1 instanceof Map);
  }

}
