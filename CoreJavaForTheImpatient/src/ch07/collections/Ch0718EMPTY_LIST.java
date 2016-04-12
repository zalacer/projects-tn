package ch07.collections;

import java.util.Collections;
import java.util.List;

// 18. The Collections class has static variables EMPTY_LIST, EMPTY_MAP, and
// EMPTY_SET. Why are they not as useful as the emptyList, emptyMap, and
// emptySet methods?

// EMPTY_LIST is raw (untyped) and generates a rawtypes warning.  It can be cast 
// to a typed List but that's extra work and generates an unchecked warning.
// On the other hand the result of emptyList() can be set directly to a type list
// variable with no warnings, plus its low cost and the list it generates can
// be reused. Same for map and set.


public class Ch0718EMPTY_LIST {

  public static void main(String[] args) {

    @SuppressWarnings("rawtypes")
    List e = Collections.EMPTY_LIST;
    @SuppressWarnings({ "unchecked", "unused" })
    List<String> s = (List<String>) e;

    @SuppressWarnings("unused")
    List<String> g = Collections.emptyList();

  }

}
