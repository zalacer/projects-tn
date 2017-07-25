package ex35;

import static v.ArrayUtils.*;

import st.LinearProbingHashSTM;

/* p507
  3.5.8  Modify LinearProbingHashST to keep duplicate keys in the table. 
  Return any value associated with the given key for get(), and remove all 
  items in the table that have keys equal to the given key for delete().
  
  Since any value for a given key can be returned by get(), I interpret the
  exercise statement to be satisfiable by retaining one value for each key,
  and storing a count of the number of times a key has been put(). In other 
  words duplicate keys won't be stored literally but represented by a count. 
  Since it isn't specified what value to return for a key when it has multiple 
  values, the last value assigned to each key will be returned by get(). It
  would be simple to retain all values for each key and have delete remove
  them one at a time in order of insertion or not, but none of that was
  specified.
  
  Maybe this misses the point, but I'm uninterested in storing multiple equal
  keys for no stated purpose and don't appreciate that there's any point, i.e.
  any reason to do it instead of using the approach outlined above.
  
  The class is implemented as st.LinearProbingHashSTM. It uses a private inner 
  class V with fields Value v and int c, where v is the last Value put for the 
  corresponding Key and c is the count of that Key, and vals[] is an array of V 
  instead of Value. There are two versions of put(), one public with the usual 
  signature and the other private with an additional count argument for rehashing 
  in delete() and rebuilding for resize(). Similarly there is a private get() that 
  returns V and is called by the public get() that returns Value and getKeyCount() 
  that returns a Key's count. LinearProbingHashSTM is demonstrated below.
  
*/

public class Ex3508ModifyLinearProbingHashST2KeepDuplicateKeys {
  
  public static void main(String[] args) {
    
    String[] sa = "E A S Y Q U T I O N".split("\\s+");
    Character[] ca = new Character[sa.length];
    for (int i = 0; i < sa.length; i++) ca[i] = sa[i].charAt(0);
    Integer[] ia = rangeInteger(0,sa.length);
    LinearProbingHashSTM<Character, Integer> ht = new LinearProbingHashSTM<>(ca,ia);
    System.out.print("ht = "+ht+"\n");
    System.out.println("get('E') = "+ht.get('E'));
    System.out.println("count of 'E' = "+ht.getKeyCount('E'));
    System.out.println("put('E', 11)"); ht.put('E', 11); 
    System.out.print("ht = "+ht+"\n"); 
    System.out.println("get('E') = "+ht.get('E'));
    System.out.println("count of 'E' = "+ht.getKeyCount('E'));
    System.out.println("put('E', 12)"); ht.put('E', 12);
    System.out.print("ht = "+ht+"\n"); 
    System.out.println("get('E') = "+ht.get('E'));
    System.out.println("count of 'E' = "+ht.getKeyCount('E'));
    System.out.println("ht.delete('E')"); ht.delete('E');
    System.out.print("ht = "+ht+"\n"); 
    System.out.println("get('E') = "+ht.get('E'));
    System.out.println("count of 'E' = "+ht.getKeyCount('E'));
/*
    ht = {Q:(4,1),S:(2,1),T:(6,1),A:(1,1),U:(5,1),Y:(3,1),E:(0,1),I:(7,1),N:(9,1),O:(8,1)}
    get('E') = 0
    count of 'E' = 1
    put('E', 11)
    ht = {Q:(4,1),S:(2,1),T:(6,1),U:(5,1),Y:(3,1),A:(1,1),E:(11,2),I:(7,1),N:(9,1),O:(8,1)}
    get('E') = 11
    count of 'E' = 2
    put('E', 12)
    ht = {Q:(4,1),S:(2,1),T:(6,1),U:(5,1),Y:(3,1),A:(1,1),E:(12,3),I:(7,1),N:(9,1),O:(8,1)}
    get('E') = 12
    count of 'E' = 3
    ht.delete('E')
    ht = {Q:(4,1),S:(2,1),T:(6,1),U:(5,1),Y:(3,1),A:(1,1),I:(7,1),N:(9,1),O:(8,1)}
    get('E') = null
    count of 'E' = 0
*/

  }

}

