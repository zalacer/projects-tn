package ex34;

/* p482
  3.4.19 Implement keys() for SeparateChainingHashST and LinearProbingHashST.

  keys() is already in st.SeparateChainingHashST, st.SeparateChainingHashSTX,
  st.LinearProbingHashST and st.LinearProbingHashSTX.

  For st.LinearProbingHashSTX I changed keys() to:

    public Iterable<Key> keys() {
      if (keys == null) new Queue<Key>();
      return new Queue<Key>(toKeyArray());
    }

  where toKeyArray() is :

    public Key[] toKeyArray() {
      if (keys == null) return null;
      Key[] ka = ofDim(kclass, m);
      int c = 0;
      for (Key k : keys) if (k != null) ka[c++] = k;
      ka = take(ka,c);
      Arrays.sort(ka); 
      return ka;
    }

  Because for testing it's nice to have the keys sorted to more easily inspect
  them and it's useful to be able to produce an array with just the keys. I
  haven't got around to doing that yet for st.SeparateChainingHashSTX but
  have already equipped that with toKeyArray() and toValArray() for one of
  the previous exercises.

  Since the only reason to use keys is for its iterator, that could be done more 
  directly with Arrays.stream(T[]).iterator() using v.ArrayUtils.iterator(T[]). 
  In other words:

    public Iterator<Keys> keysIterator() {
      return Arrays.stream(toKeyArray()).iterator()
    }
    
  or
    
    public static <T> Iterator<T> keysIterator() {
      return Arrays.stream(keys).filter((x)->{return x==null?false:true;}).iterator();
    }
    

 */             

public class Ex3419ImplementTheKeysMethod4SeparateChainingHashSTandLinearProbingHashST {


  public static void main(String[] args) {

  }

}

