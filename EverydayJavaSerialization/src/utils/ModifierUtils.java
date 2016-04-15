package utils;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.INTERFACE;
import static java.lang.reflect.Modifier.NATIVE;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.ABSTRACT;
import static java.lang.reflect.Modifier.PROTECTED;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;
import static java.lang.reflect.Modifier.STRICT;
import static java.lang.reflect.Modifier.SYNCHRONIZED;
import static java.lang.reflect.Modifier.TRANSIENT;
import static java.lang.reflect.Modifier.VOLATILE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class ModifierUtils {

  public static final List<Integer> modifiers = new ArrayList<>(Arrays.asList(
      ABSTRACT,       // 1024
      FINAL,          // 16
      INTERFACE,      // 512
      NATIVE,         // 256
      PRIVATE,        // 2
      PROTECTED,      // 4
      PUBLIC,         // 1
      STATIC,         // 8
      STRICT,         // 2048
      SYNCHRONIZED,   // 32
      TRANSIENT,      // 128
      VOLATILE));     // 64

  public static final Map<Integer, String> modMap = new HashMap<Integer, String>() {
    private static final long serialVersionUID = 1L;
    { put(1024, "abstract");   put(16, "final");      put(512, "interface"); 
      put(256, "native");      put(2, "private");     put(4, "protected"); 
      put(1, "public");        put(8, "static");      put(2048, "strictfp"); 
      put(32, "synchronized"); put(128, "transient"); put(64, "volatile"); 
    } 
  };

  public static final Map<String, Integer> order = new HashMap<String, Integer>() {
    private static final long serialVersionUID = 1L;
    { put("abstract", 4);       put("final", 7);        put("interface", 5); 
      put("native", 11);        put("private", 3);      put("protected", 2); 
      put("public", 1);         put("static", 6);       put("strictfp", 12); 
      put("synchronized", 10);  put("transient", 8);    put("volatile", 9); 
    } 
  };

  public static void findModifiers(int m) {
    for (Integer i : modifiers) {
      if ((i.intValue() & m) == i.intValue()) System.out.println(modMap.get(i));
    }
  }

  public static String listModifiers(int m) {
    Map<String, Integer> map = new HashMap<>();
    String name = "";
    for (Integer i : modifiers) {
      if ((i.intValue() & m) == i.intValue()) {
        name = modMap.get(i);
        map.put(name, order.get(name));
      }
    }
    if (map.isEmpty()) return "noModifiers";
    if (map.size() == 1) return removeBrackets(map.keySet().toString());
    map = sortByValue(map);
    return removeBrackets("("+map.keySet().toString()+")");

  }

  public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
    Map<K, V> result = new LinkedHashMap<>();
    Stream<Entry<K, V>> st = map.entrySet().stream();
    st.sorted(Comparator.comparing(e -> e.getValue()))
    .forEach(e -> result.put(e.getKey(), e.getValue()));
    return result;
  }

  public static boolean hasAbstract(int m) {
    if ((ABSTRACT & m) == ABSTRACT) return true;
    return false;
  }

  public static boolean hasFinal(int m) {
    if ((FINAL & m) == FINAL) return true;
    return false;
  }

  public static boolean hasInterface(int m) {
    if ((INTERFACE & m) == INTERFACE) return true;
    return false;
  }

  public static boolean hasNative(int m) {
    if ((NATIVE & m) == NATIVE) return true;
    return false;
  }

  public static boolean hasPrivate(int m) {
    if ((PRIVATE & m) == PRIVATE) return true;
    return false;
  }

  public static boolean hasProtected(int m) {
    if ((PROTECTED & m) == PROTECTED) return true;
    return false;
  }

  public static boolean hasPublic(int m) {
    if ((PUBLIC & m) == PUBLIC) return true;
    return false;
  }

  public static boolean hasStatic(int m) {
    if ((STATIC & m) == STATIC) return true;
    return false;
  }

  public static boolean hasStrict(int m) {
    if ((STRICT & m) == STRICT) return true;
    return false;
  }

  public static boolean hasSynchronized(int m) {
    if ((SYNCHRONIZED & m) == SYNCHRONIZED) return true;
    return false;
  }

  public static boolean hasTransient(int m) {
    if ((TRANSIENT & m) == TRANSIENT) return true;
    return false;
  }

  public static boolean hasVolatile(int m) {
    if ((VOLATILE & m) == VOLATILE) return true;
    return false;
  }

  public static String removeBrackets(Object s) {
    return s.toString().replaceAll("\\[", "").replaceAll("\\]", "");
  }

}
