package v;

//import static v.ArrayUtils.*;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import com.google.common.*;
//import com.google.common.hash.Hasher;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

//import java.util.Objects;

public final class Tuple2<K,V> implements Cloneable {
//  private HashFunction hf = Hashing.murmur3_32();
  public K _1; 
  public V _2; 
    
  public Tuple2(){}
  
  public Tuple2(K k, V v) {
    this._1 = k; this._2 = v;
  }
  
  public Tuple2(Tuple2<K,V> t) {
    this._1 = t._1; this._2 = t._2;
  }
  
  public Tuple2<V,K> swap(Tuple2<K,V> t) {
    return new Tuple2<V,K>(t._2,t._1);
  }
  
  @SuppressWarnings("unchecked")
  public Tuple2<K,V> clone() {
    try {
      return (Tuple2<K,V>) super.clone();
    } catch (CloneNotSupportedException e) {
      return new Tuple2<K,V>(_1, _2);
    }    
  }
  
//  @Override
//  public int hashCode() {
//    return Objects.hash(_1, _2);
//  }
//  

  @Override
  public int hashCode() {
    final int prime1 = 397;
    int result = 1;
    result = prime1 * result + ((_1 == null) ? 0 : _1.hashCode());
    result = prime1 * result + ((_2 == null) ? 0 : _2.hashCode());
    return result & 0x7fffffff;
  }
    
//  public int h() {
//    return hf.newHasher().putInt(_1.hashCode()).putInt(_2.hashCode()).hash().asInt();
//  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    @SuppressWarnings("rawtypes")
    Tuple2 other = (Tuple2) obj;
    if (_1 == null) {
      if (other._1 != null)
        return false;
    } else if (!_1.equals(other._1))
      return false;
    if (_2 == null) {
      if (other._2 != null)
        return false;
    } else if (!_2.equals(other._2))
      return false;
    return true;
  }

  @Override public String toString() {
    return "("+_1+","+_2+")";
  }
  
  public static void main(String[] args) throws CloneNotSupportedException {
    
    Tuple2<Double,Integer> t = new Tuple2<Double,Integer>(5.6, 7);
    Tuple2<Double,Integer> cl = t.clone();
    System.out.println(t);
    System.out.println(cl);
    
//    HashFunction hf1 = Hashing.murmur3_128();
//    HashCode hc1 = hf1.newHasher().putDouble(14.).putDouble(14.).hash();
//    System.out.println(hc1.asLong());
//    
//    HashFunction hf2 = Hashing.murmur3_128();
//    HashCode hc2 = hf2.newHasher().putDouble(14.).putDouble(14.).hash();
//    System.out.println(hc2);
//    
//    HashFunction hf3 = Hashing.murmur3_128();
//    HashCode hc3 = hf3.newHasher().putDouble(14.).hash();
//    System.out.println(hc3.asLong());
//    
//    HashFunction hf = Hashing.goodFastHash(31);
//    HashFunction hf = Hashing.sipHash24();
//    
//    HashFunction hf4 = Hashing.murmur3_32();
//    HashCode hc4 = hf4.newHasher().putDouble(14.).putDouble(27.).hash();
//    System.out.println(hc4.asInt());
//    
//    HashFunction hf5 = Hashing.murmur3_32(17);
//    HashCode hc5 = hf5.newHasher().putDouble(14.).putDouble(27.).hash();
//    System.out.println(hc5.asInt());
//    
//    System.exit(0);
    
//   System.out.println(hc.asInt() & 0x7fffffff);/
   
//    HashFunction hf = Hashing.murmur3_32();
//    HashCode hc = hf.newHasher().putDouble(14.).putDouble(27.).hash();
   
//    Double[] a = rangeDouble(0.,1000.);
//    Set<Integer> set = new HashSet<>();
//    List<Integer> list = new ArrayList<>();
//    for (Double d1: a) {
//      for (Double d2: a) {
//        HashCode hc = hf.newHasher().putInt(d1.hashCode()).putInt(d2.hashCode()).hash();
//        int h = Math.abs(hc.asInt()); // 999773/1000000
////        Tuple2<Double,Double> t = new Tuple2<>(d1,d2);
////        int h = t.hashCode(); // 574238/1000000
////        if (h == 1073899433) System.out.println(d1+" "+d2);
////        if (h == 115173289) System.out.println(d1+" "+d2);
////        if (set.contains(h)) System.out.println(h);
////      int h = d1.hashCode();
//        set.add(h);
//        list.add(h);
////        System.out.println(h);
//      }
//    }
//    System.out.println(list.size());
//    System.out.println(set.size());
    
    HashFunction hf4 = Hashing.murmur3_32();
    HashCode hc4 = hf4.newHasher()
        .putDouble(Double.NEGATIVE_INFINITY).putDouble(Double.POSITIVE_INFINITY).hash();
    System.out.println(-hc4.asInt());
    
    
  }
}


