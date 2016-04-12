package ch07.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

// 2. In an array list of strings, make each string uppercase. Do this with 
// (a) aniterator, (b) a loop over the index values, and (c) the replaceAll method.

public class Ch0702ArrayList {

  public static void main(String[] args) {

    ArrayList<String> a;
    
    // using an iterator
    a = new ArrayList<>(Arrays.asList("one", "two", "three"));       
    Iterator<String> it = a.iterator();
    int c = 0;
    while (it.hasNext()) {            
      a.set(c, it.next().toUpperCase());
      c++;
    }
    System.out.println(a);
    // [ONE, TWO, THREE]

    // looping over the index values
    a = new ArrayList<>(Arrays.asList("one", "two", "three"));
    for (int i = 0; i < a.size(); i++) 
      a.set(i, a.get(i).toUpperCase());
    System.out.println(a);
    // [ONE, TWO, THREE]

    // using index values with enhanced for
    a = new ArrayList<>(Arrays.asList("one", "two", "three"));
    int index = 0;
    for (String s : a) {
      a.set(index, s.toUpperCase());
      index++;
    }
    System.out.println(a);
    // [ONE, TWO, THREE]

    // using replaceAll()
    a = new ArrayList<>(Arrays.asList("one", "two", "three"));
    a.replaceAll(x -> x.toUpperCase());
    System.out.println(a);
    // [ONE, TWO, THREE]








  }

}

//String f = "alice.txt";
//Scanner s = new Scanner(Paths.get(f));
//s.useDelimiter("\n");
//List<String> a = Arrays.asList(s.next().split("\\s+")); 
//s.close();
