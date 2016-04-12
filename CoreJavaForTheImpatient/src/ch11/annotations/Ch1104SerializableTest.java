package ch11.annotations;

import static java.lang.System.identityHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.SynchronousQueue;
import java.util.jar.Attributes;

public class Ch1104SerializableTest {
  
  public static final Ch1104Serializable sp4 = new Ch1104Serializable();
  
  public static final void setUseAnnotation(boolean b) {
    sp4.setUseAnnotation(b);
  }
  
  public static final String ser(Object o) {
    return sp4.ser(o);
  }
  
  public static final Object des (String s) {
    return sp4.des(s);
  }
  
  public static final Object serdes(Object o) {
    return sp4.serdes(o);
  }
  
  public final static void copyString2File(String s, String pathName) {
    Ch1104Serializable.copyString2File(s, pathName);
  }
  
  public static final String copyFile2String(String pathName) {
    return Ch1104Serializable.copyFile2String(pathName);
  }
  
  public static final void gzipString2File(String s, String pathName) {
    Ch1104Serializable.gzipString2File(s, pathName);
  }
  
  public static final String gunzipFile2String(String pathName) {
    return Ch1104Serializable.gunzipFile2String(pathName);
  }
  
  public static final void encryptString2File(String data, String pathName, String pwd) {
    Ch1104Serializable.encryptString2File(data, pathName, pwd);
  }
  
  public static final String decryptFile2String(String path, String...keyPath) {
    return Ch1104Serializable.decryptFile2String(path, keyPath);
  }
  
  public static final <K, V> HashMap<V, K> invert(Map<K, V> map) {
    return sp4.invert(map);
  }
  
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    // This is far from an exhaustive suite of tests
    // but increases credibility of correctness.
 
    // Disable use of @Serializable and assume serializability for the following tests.
    // Serializability can be determined by using the public isSerializable(),  
    // isSerializableArray(), isSerializableClass(), isSerializableCollection() and 
    // isSerializableMap() methods, but they aren't used to determine serializability
    // automatically prior to serialization.
    setUseAnnotation(true);
    
    // Annotated is annotated with @Serializable.
    Annotated annotated = new Annotated();
    assert annotated.equals(serdes(annotated));
        
     //// NotAnnotated is not annotated with @Serializable.
     //NotAnnotated notAnnotated = new NotAnnotated();
     //NotAnnotated notAnnotatedSerdes = (NotAnnotated) serdes(notAnnotated);
     //// produces IllegalArgumentException: serialize: cannot serialize o
    
    // Rely on isSerializable() to determine serializability by
    // real time inspection without involving @Serializable.
    setUseAnnotation(false);
    
    // If all tests below pass the output will be only "all tests passed".
    
    assert null == serdes(null);
        
    NotAnnotated notAnnotated2 = new NotAnnotated();
    assert(notAnnotated2.equals(serdes(notAnnotated2)));
    
    Integer one = new Integer(1);
    assert 1 == (int) serdes(one);   
    assert one.equals(serdes(one));
    
    assert "hello".equals(serdes("hello"));
    
    // Numbers is an Enum.
    assert (Numbers.FIVE) == serdes(Numbers.FIVE);
    
    int[] inta = new int[]{1,2,3};
    assert Arrays.equals(inta, (int[]) serdes(inta));
    
    Integer[] integera = new Integer[]{1,2,3};
    assert Arrays.equals(integera, (Integer[]) serdes(integera));
    
    // test array with duplicate elements
    Integer nine = new Integer(9);
    Integer[] oa = new Integer[5];
    oa[0] = new Integer(1); oa[1] = nine;
    oa[2] = new Integer(3); oa[3] = nine;
    oa[4] = new Integer(5);
    Integer[] oaserdes = (Integer[]) serdes(oa);
    assert identityHashCode(oaserdes[1]) == identityHashCode(oaserdes[3]);
    
    Attributes.Name an = new Attributes.Name("hello");
    assert an.equals(serdes(an));
    
    ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1,2,3));
    assert list.equals(serdes(list));
    
    // test list with duplicate elements
    Integer eleven = new Integer(1);
    Integer five = new Integer(5); 
    ArrayList<Integer> a1 = new ArrayList<>(Arrays.asList(eleven, five, five, eleven));
    ArrayList<Integer> a1serdes = (ArrayList<Integer>) serdes(a1);
    assert identityHashCode(a1serdes.get(0)) == identityHashCode(a1serdes.get(3));
    assert identityHashCode(a1serdes.get(1)) == identityHashCode(a1serdes.get(2));
    
    LinkedList<Float> ll = new LinkedList<>(Arrays.asList(1.1F, 2.2F, 3.3F));
    assert ll.equals(serdes(ll));
    
    ArrayBlockingQueue<Integer> abq = new ArrayBlockingQueue<>(3);
    try {
      abq.put(1); abq.put(2); abq.put(3);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // Multiple simultaneous ArrayBlockingQueue instances have
    // different lock objects, however abq and serdes(abq) are 
    // essentially identical.
    assert Arrays.equals(abq.toArray(),
        ((ArrayBlockingQueue<Integer>) serdes(abq)).toArray());
    
    // It's impossible to put elements into a SynchronousQueue 
    // without an already initiated a remove operation.
    SynchronousQueue<Integer> sq = new SynchronousQueue<>();
    // This shows deserialization created a new SynchronousQueue instance.
    assert Arrays.equals(sq.toArray(),
        ((SynchronousQueue<Integer>) serdes(sq)).toArray());
    
    HashSet<Double> set = new HashSet<>(Arrays.asList(1.1, 2.2, 3.3));
    assert set.equals(serdes(set));
    
    EnumSet<Numbers> enumSet = EnumSet.allOf(Numbers.class);
    assert enumSet.equals(serdes(enumSet));
    
    TreeSet<Short> ts = new TreeSet<>(Arrays.asList((short) 1, (short) 2, (short) 3));
    assert ts.equals(serdes(ts));
    
    ConcurrentSkipListSet<Byte> cset = 
        new ConcurrentSkipListSet<>(Arrays.asList((byte) 1, (byte) 7, (byte) 15));
    assert cset.equals(serdes(cset));
    
    Stack<Integer> stack = new Stack<>();
    stack.push(1); stack.push(2); stack.push(3);
    assert stack.equals(serdes(stack));
    
    Vector<Integer> v = new Vector<>(Arrays.asList(1, 2, 3));
    assert v.equals(serdes(v));
 
    HashMap<String,Integer> map = new HashMap<>();
    map.put("one", 1); map.put("two", 2); map.put("three", 3); 
    assert map.equals(serdes(map));
    
    // test map with duplicate values
    HashMap<String,Integer> map2 = new HashMap<>();
    map2.put("one", five); map2.put("two", 2); map2.put("three", five); 
    assert map2.equals(serdes(map2));
    assert identityHashCode(map2.get("one")) == identityHashCode(map2.get("three"));
    
    Attributes attributes = new Attributes();
    Attributes.Name atOne = new Attributes.Name("one");
    Attributes.Name atTwo = new Attributes.Name("two");
    Attributes.Name atThree = new Attributes.Name("three");
    attributes.put(atOne, atOne.toString());
    attributes.put(atTwo, atTwo.toString());
    attributes.put(atThree, atThree.toString());
    assert attributes.equals(serdes(attributes));
    
    ConcurrentHashMap<String, Integer> chm = new ConcurrentHashMap<>();
    chm.put("one", 1); chm.put("two", 2); chm.put("three", 3); 
    assert chm.equals(serdes(chm));
    
    ConcurrentSkipListMap<String, Integer> cslm = new ConcurrentSkipListMap<>();
    cslm.put("one", 1); cslm.put("two", 2); cslm.put("three", 3); 
    assert cslm.equals(serdes(cslm));
    
    EnumMap <Numbers, Integer> em = new EnumMap<Numbers, Integer>(Numbers.class);
    em.put(Numbers.ONE, 1); em.put(Numbers.TWO, 2); em.put(Numbers.THREE, 3);
    assert em.equals(serdes(em));
    
    Hashtable<String,Integer> table = new Hashtable<>();
    table.put("one", 1); table.put("two", 2); table.put("three", 3); 
    assert table.equals(serdes(table));
    
    LinkedHashMap<String,Integer> lhm = new LinkedHashMap<>();
    lhm.put("one", 1); lhm.put("two", 2); lhm.put("three", 3); 
    assert lhm.equals(serdes(lhm));
    
    TreeMap<String,Integer> tmap = new TreeMap<>();
    tmap.put("one", 1); tmap.put("two", 2); tmap.put("three", 3); 
    assert tmap.equals(serdes(tmap));
    
    WeakHashMap<String,Integer> wmap = new WeakHashMap<>();
    wmap.put("one", 1); wmap.put("two", 2); wmap.put("three", 3); 
    assert wmap.equals(serdes(wmap));
    
    BitSet bset = new BitSet(); bset.set(1); bset.set(101); bset.set(1001);
    assert bset.equals(serdes(bset));
    
    // Empty is a class with no fields.
    Empty empty = new Empty();
    assert empty.equals(serdes(empty));
    
    // A is a class with one field.
    A a = new A(); a.setA(null);
    assert a.equals(serdes(a));
    a.setA("a string");
    assert a.equals(serdes(a));
    
    // B is a class with primitive, array, Set, List, Map and other object fields.
    B b = new B();
    
    //System.out.println("b="+b);
    ////  b=B(z=1, d=2.2, bool=true, s=what, pstr=protected string, ia=[1, 2, 3], 
    ////    sa=[one, two, three], ca=[a, null, c], set1=[1.1, 2.2, 3.3], 
    ////    list1=[true, false, true], map1={one=1, two=2, three=3}, 
    ////    a=A [a=hello a], c=C(c=c string, x=x string))
    
    assert b.equals(serdes(b));
    
    // Circularity tests: 
    // (Using universalToString representations in assertions.)
    
    Cir1 cir1 = new Cir1();
    assert cir1.toString().equals(serdes(cir1).toString());

    Cir2a cir2a = new Cir2a();
    Cir2b cir2b = new Cir2b();
    cir2a.b = cir2b;
    cir2b.a = cir2a;
    
    // System.out.println(cir2a);
    //// Cir2a(b=Cir2b(a=Cir2a(b)))
    // System.out.println(cir2b);
    //// Cir2b(a=Cir2a(b=Cir2b(a))) 
    
    Cir2a cir2aserdes = (Cir2a) serdes(cir2a);
    Cir2b cir2bserdes = (Cir2b) serdes(cir2b);
    
    assert cir2a.toString().equals(cir2aserdes.toString());
    assert cir2b.toString().equals(cir2bserdes.toString());
    
    // Since they are independently serialized, cir2aserdes.b is not identical
    // to cir2bserdes and cir2bserdes.a is not identical to cir2aserdes, i.e.: 
    assert identityHashCode(cir2aserdes.b) != identityHashCode(cir2bserdes);
    assert identityHashCode(cir2bserdes.a) != identityHashCode(cir2aserdes);
    
    // However, when objects are serialized as components or fields of an object,
    // identity hashcode relationships are preserved, e.g.:
    Object[] cir2Ar = new Object[]{cir2a, cir2b};
    Object[] cir2Arserdes = (Object[]) serdes(cir2Ar);
    Cir2a cir2a0 = (Cir2a) cir2Arserdes[0];
    Cir2b cir2b1 = (Cir2b) cir2Arserdes[1];   
    assert identityHashCode(cir2a0.b) == identityHashCode(cir2b1);
    assert identityHashCode(cir2b1.a) == identityHashCode(cir2a0);
    
    LinkedHashMap<Object,Object> cir2map = new LinkedHashMap<>();
    cir2map.put(cir2a, cir2b); cir2map.put(cir2b, cir2a);
    LinkedHashMap<Object,Object> cir2mapserdes = 
    (LinkedHashMap<Object,Object>) serdes(cir2map);

    ArrayList<Map.Entry<Object,Object>> cir2serdeslist = 
        new ArrayList<>(cir2mapserdes.entrySet());

          cir2a0 = (Cir2a) cir2serdeslist.get(0).getKey();
    Cir2b cir2b0 = (Cir2b) cir2serdeslist.get(0).getValue();
          cir2b1 = (Cir2b) cir2serdeslist.get(1).getKey();
    Cir2a cir2a1 = (Cir2a) cir2serdeslist.get(1).getValue();

    assert identityHashCode(cir2a0) == identityHashCode(cir2a1);
    assert identityHashCode(cir2b0) == identityHashCode(cir2b1);

    Cir3a cir3a = new Cir3a();
    Cir3b cir3b = new Cir3b();
    Cir3c cir3c = new Cir3c();
    cir3a.a = cir3a;
    cir3a.b = cir3b;
    cir3a.c = cir3c;
    cir3b.a = cir3a;
    cir3b.b = cir3b;
    cir3b.c = cir3c;
    cir3c.a = cir3a;
    cir3c.b = cir3b;
    cir3c.c = cir3c;
    
    // System.out.println(cir3a);
    //// Cir3a(a=Cir3a(a,b=Cir3b(a,b,c=Cir3c(a,b,c)),c),b,c)
    // System.out.println(cir3b);
    //// Cir3b(a=Cir3a(a,b=Cir3b(a,b,c=Cir3c(a,b,c)),c),b,c)
    // System.out.println(cir3c);
    //// Cir3c(a=Cir3a(a,b=Cir3b(a,b,c=Cir3c(a,b,c)),c),b,c)
    
    assert cir3a.toString().equals(serdes(cir3a).toString());
    assert cir3b.toString().equals(serdes(cir3b).toString());
    assert cir3c.toString().equals(serdes(cir3c).toString());
    
    LinkedHashMap<Object,Object> cir3map = new LinkedHashMap<>();
    cir3map.put(cir3a, cir3b); cir3map.put(cir3b, cir3a);

    LinkedHashMap<Object,Object> cir3mapserdes = 
        (LinkedHashMap<Object,Object>) serdes(cir3map);
    ArrayList<Map.Entry<Object,Object>> cir3serdeslist = 
        new ArrayList<>(cir3mapserdes.entrySet());

    Cir3a cir3a0 = (Cir3a) cir3serdeslist.get(0).getKey();
    Cir3b cir3b0 = (Cir3b) cir3serdeslist.get(0).getValue();
    Cir3b cir3b1 = (Cir3b) cir3serdeslist.get(1).getKey();
    Cir3a cir3a1 = (Cir3a) cir3serdeslist.get(1).getValue();

    assert identityHashCode(cir3a0) == identityHashCode(cir3a1);
    assert identityHashCode(cir3b0) == identityHashCode(cir3b1);
    
    Cir5a cir5a = new Cir5a();
    Cir5b cir5b = new Cir5b();
    Cir5c cir5c = new Cir5c();
    Cir5d cir5d = new Cir5d();
    Cir5e cir5e = new Cir5e();
    
    cir5a.a = cir5a;
    cir5a.b = cir5b;
    cir5a.c = cir5c;
    cir5a.d = cir5d;
    cir5a.e = cir5e;
    
    cir5b.a = cir5a;
    cir5b.b = cir5b;
    cir5b.c = cir5c;
    cir5b.d = cir5d;
    cir5b.e = cir5e;
    
    cir5c.a = cir5a;
    cir5c.b = cir5b;
    cir5c.c = cir5c;
    cir5c.d = cir5d;
    cir5c.e = cir5e;
    
    cir5d.a = cir5a;
    cir5d.b = cir5b;
    cir5d.c = cir5c;
    cir5d.d = cir5d;
    cir5d.e = cir5e;
    
    cir5e.a = cir5a;
    cir5e.b = cir5b;
    cir5e.c = cir5c;
    cir5e.d = cir5d;
    cir5e.e = cir5e;
    
    // System.out.println("cir5a.toString():\n"+cir5a.toString().replaceAll("\n|\\s+",""));
    //// cir5a.toString():
    //// Cir5a(a=Cir5a(a,b=Cir5b(a,b,c=Cir5c(a,b,c,d=Cir5d(a,b,c,d,e=Cir5e(a,b,c,d,e)),e),d,e),c,d,e),b,c,d,e)

    assert cir5a.toString().equals(serdes(cir5a).toString());
    assert cir5b.toString().equals(serdes(cir5b).toString());
    assert cir5c.toString().equals(serdes(cir5c).toString());
    assert cir5d.toString().equals(serdes(cir5d).toString());
    assert cir5e.toString().equals(serdes(cir5e).toString());
    
    Object[] cir5Ar = new Object[]{cir5a,cir5b,cir5c,cir5d,cir5e};
    Object[] cir5Arserdes = (Object[]) serdes(cir5Ar);
    
    Cir5a cir5a0 = (Cir5a) cir5Arserdes[0];
    Cir5b cir5b1 = (Cir5b) cir5Arserdes[1];
    Cir5c cir5c2 = (Cir5c) cir5Arserdes[2];
    Cir5d cir5d3 = (Cir5d) cir5Arserdes[3];
    Cir5e cir5e4 = (Cir5e) cir5Arserdes[4];

    assert identityHashCode(cir5a0) == identityHashCode(cir5a0.a);
    assert identityHashCode(cir5a0) == identityHashCode(cir5b1.a);
    assert identityHashCode(cir5a0) == identityHashCode(cir5c2.a);
    assert identityHashCode(cir5a0) == identityHashCode(cir5d3.a);
    assert identityHashCode(cir5a0) == identityHashCode(cir5d3.a);
    
    assert identityHashCode(cir5b1) == identityHashCode(cir5a0.b);
    assert identityHashCode(cir5b1) == identityHashCode(cir5b1.b);
    assert identityHashCode(cir5b1) == identityHashCode(cir5c2.b);
    assert identityHashCode(cir5b1) == identityHashCode(cir5d3.b);
    assert identityHashCode(cir5b1) == identityHashCode(cir5d3.b);
    
    assert identityHashCode(cir5c2) == identityHashCode(cir5a0.c);
    assert identityHashCode(cir5c2) == identityHashCode(cir5b1.c);
    assert identityHashCode(cir5c2) == identityHashCode(cir5c2.c);
    assert identityHashCode(cir5c2) == identityHashCode(cir5d3.c);
    assert identityHashCode(cir5c2) == identityHashCode(cir5d3.c);
    
    assert identityHashCode(cir5d3) == identityHashCode(cir5a0.d);
    assert identityHashCode(cir5d3) == identityHashCode(cir5b1.d);
    assert identityHashCode(cir5d3) == identityHashCode(cir5c2.d);
    assert identityHashCode(cir5d3) == identityHashCode(cir5d3.d);
    assert identityHashCode(cir5d3) == identityHashCode(cir5e4.d);
    
    assert identityHashCode(cir5e4) == identityHashCode(cir5a0.e);
    assert identityHashCode(cir5e4) == identityHashCode(cir5b1.e);
    assert identityHashCode(cir5e4) == identityHashCode(cir5c2.e);
    assert identityHashCode(cir5e4) == identityHashCode(cir5d3.e);
    assert identityHashCode(cir5e4) == identityHashCode(cir5e4.e);
    
    ArrayList<Object> cir5Al = new ArrayList<>(Arrays.asList(cir5a,cir5b,cir5c,cir5d,cir5e));
    ArrayList<Object> cir5Alserdes = (ArrayList<Object>) serdes(cir5Al);
    
    cir5a0 = (Cir5a) cir5Alserdes.get(0);
    cir5b1 = (Cir5b) cir5Alserdes.get(1);
    cir5c2 = (Cir5c) cir5Alserdes.get(2);
    cir5d3 = (Cir5d) cir5Alserdes.get(3);
    cir5e4 = (Cir5e) cir5Alserdes.get(4);
    
    assert identityHashCode(cir5a0) == identityHashCode(cir5a0.a);
    assert identityHashCode(cir5a0) == identityHashCode(cir5b1.a);
    assert identityHashCode(cir5a0) == identityHashCode(cir5c2.a);
    assert identityHashCode(cir5a0) == identityHashCode(cir5d3.a);
    assert identityHashCode(cir5a0) == identityHashCode(cir5e4.a);
    
    assert identityHashCode(cir5b1) == identityHashCode(cir5a0.b);
    assert identityHashCode(cir5b1) == identityHashCode(cir5b1.b);
    assert identityHashCode(cir5b1) == identityHashCode(cir5c2.b);
    assert identityHashCode(cir5b1) == identityHashCode(cir5d3.b);
    assert identityHashCode(cir5b1) == identityHashCode(cir5e4.b);
    
    assert identityHashCode(cir5c2) == identityHashCode(cir5a0.c);
    assert identityHashCode(cir5c2) == identityHashCode(cir5b1.c);
    assert identityHashCode(cir5c2) == identityHashCode(cir5c2.c);
    assert identityHashCode(cir5c2) == identityHashCode(cir5d3.c);
    assert identityHashCode(cir5c2) == identityHashCode(cir5e4.c);
    
    assert identityHashCode(cir5d3) == identityHashCode(cir5a0.d);
    assert identityHashCode(cir5d3) == identityHashCode(cir5b1.d);
    assert identityHashCode(cir5d3) == identityHashCode(cir5c2.d);
    assert identityHashCode(cir5d3) == identityHashCode(cir5d3.d);
    assert identityHashCode(cir5d3) == identityHashCode(cir5e4.d);
    
    assert identityHashCode(cir5e4) == identityHashCode(cir5a0.e);
    assert identityHashCode(cir5e4) == identityHashCode(cir5b1.e);
    assert identityHashCode(cir5e4) == identityHashCode(cir5c2.e);
    assert identityHashCode(cir5e4) == identityHashCode(cir5d3.e);
    assert identityHashCode(cir5e4) == identityHashCode(cir5e4.e);
   
    Cir9a cir9a = new Cir9a();    
    Cir9b cir9b = new Cir9b(); 
    Cir9c cir9c = new Cir9c(); 
    Cir9d cir9d = new Cir9d(); 
    Cir9e cir9e = new Cir9e(); 
    Cir9f cir9f = new Cir9f(); 
    Cir9g cir9g = new Cir9g(); 
    Cir9h cir9h = new Cir9h(); 
    Cir9i cir9i = new Cir9i(); 
    Cir9j cir9j = new Cir9j(); 
    Cir9k cir9k = new Cir9k(); 
    
    cir9a.b = cir9b;
    cir9b.c = cir9c;
    cir9c.d = cir9d;
    cir9d.e = cir9e;
    cir9e.f = cir9f;
    cir9f.g = cir9g;
    cir9g.h = cir9h;
    cir9h.i = cir9i;
    cir9i.j = cir9j;
    cir9j.k = cir9k;
    cir9k.a = cir9a;
    
    // System.out.println(cir9a);
    ////  Cir9a(
    ////      b=Cir9b(
    ////        c=Cir9c(
    ////          d=Cir9d(
    ////            e=Cir9e(
    ////              f=Cir9f(
    ////                g=Cir9g(
    ////                  h=Cir9h(
    ////                    i=Cir9i(
    ////                      j=Cir9j(
    ////                        k=Cir9k(
    ////                          a=Cir9a(
    ////                            b))))))))))))
    
    assert cir9a.toString().equals(serdes(cir9a).toString());
    assert cir9b.toString().equals(serdes(cir9b).toString());
    assert cir9c.toString().equals(serdes(cir9c).toString());
    assert cir9d.toString().equals(serdes(cir9d).toString());
    assert cir9e.toString().equals(serdes(cir9e).toString());
    assert cir9f.toString().equals(serdes(cir9f).toString());
    assert cir9g.toString().equals(serdes(cir9g).toString());
    assert cir9h.toString().equals(serdes(cir9h).toString());
    assert cir9i.toString().equals(serdes(cir9i).toString());
    assert cir9j.toString().equals(serdes(cir9j).toString());
    assert cir9k.toString().equals(serdes(cir9k).toString());
    
    Map<Object, Object> cir9map = new LinkedHashMap<>();
    cir9map.put(cir9a, cir9b);  cir9map.put(cir9b, cir9c);
    cir9map.put(cir9c, cir9d);  cir9map.put(cir9d, cir9e);
    cir9map.put(cir9e, cir9f);  cir9map.put(cir9f, cir9g);
    cir9map.put(cir9g, cir9h);  cir9map.put(cir9h, cir9i);
    cir9map.put(cir9i, cir9j);  cir9map.put(cir9j, cir9k);
    
    Map<Object, Object> cir9mapserdes = (LinkedHashMap<Object, Object>) serdes(cir9map);
    ArrayList<Map.Entry<Object,Object>> cir9serdeslist = 
        new ArrayList<>(cir9mapserdes.entrySet());
    
    Cir9a cir9a0 = (Cir9a) cir9serdeslist.get(0).getKey();
    Cir9b cir9b0 = (Cir9b) cir9serdeslist.get(0).getValue();
    Cir9b cir9b1 = (Cir9b) cir9serdeslist.get(1).getKey();
    Cir9c cir9c1 = (Cir9c) cir9serdeslist.get(1).getValue();
    Cir9c cir9c2 = (Cir9c) cir9serdeslist.get(2).getKey();
    Cir9d cir9d2 = (Cir9d) cir9serdeslist.get(2).getValue();
    Cir9d cir9d3 = (Cir9d) cir9serdeslist.get(3).getKey();
    Cir9e cir9e3 = (Cir9e) cir9serdeslist.get(3).getValue();
    Cir9e cir9e4 = (Cir9e) cir9serdeslist.get(4).getKey();
    Cir9f cir9f4 = (Cir9f) cir9serdeslist.get(4).getValue();
    Cir9f cir9f5 = (Cir9f) cir9serdeslist.get(5).getKey();
    Cir9g cir9g5 = (Cir9g) cir9serdeslist.get(5).getValue();
    Cir9g cir9g6 = (Cir9g) cir9serdeslist.get(6).getKey();
    Cir9h cir9h6 = (Cir9h) cir9serdeslist.get(6).getValue();
    Cir9h cir9h7 = (Cir9h) cir9serdeslist.get(7).getKey();
    Cir9i cir9i7 = (Cir9i) cir9serdeslist.get(7).getValue();
    Cir9i cir9i8 = (Cir9i) cir9serdeslist.get(8).getKey();
    Cir9j cir9j8 = (Cir9j) cir9serdeslist.get(8).getValue();
    Cir9j cir9j9 = (Cir9j) cir9serdeslist.get(9).getKey();
    Cir9k cir9k9 = (Cir9k) cir9serdeslist.get(9).getValue();
    
    assert identityHashCode(cir9a0.b) == identityHashCode(cir9b0);
    assert identityHashCode(cir9b0) == identityHashCode(cir9b1);
    
    assert identityHashCode(cir9b1.c) == identityHashCode(cir9c1);
    assert identityHashCode(cir9c1) == identityHashCode(cir9c2);
    
    assert identityHashCode(cir9c2.d) == identityHashCode(cir9d2);
    assert identityHashCode(cir9d2) == identityHashCode(cir9d3);
  
    assert identityHashCode(cir9d3.e) == identityHashCode(cir9e3);
    assert identityHashCode(cir9e3) == identityHashCode(cir9e4);
    
    assert identityHashCode(cir9e4.f) == identityHashCode(cir9f4);
    assert identityHashCode(cir9f4) == identityHashCode(cir9f5);
    
    assert identityHashCode(cir9f5.g) == identityHashCode(cir9g5);
    assert identityHashCode(cir9g5) == identityHashCode(cir9g6);
    
    assert identityHashCode(cir9g6.h) == identityHashCode(cir9h6);
    assert identityHashCode(cir9h6) == identityHashCode(cir9h7);
    
    assert identityHashCode(cir9h7.i) == identityHashCode(cir9i7);
    assert identityHashCode(cir9i7) == identityHashCode(cir9i8);
    
    assert identityHashCode(cir9i8.j) == identityHashCode(cir9j8);
    assert identityHashCode(cir9j8) == identityHashCode(cir9j9);
    
    assert identityHashCode(cir9j9.k) == identityHashCode(cir9k9);
    assert identityHashCode(cir9k9.a) == identityHashCode(cir9a0);
    
    // test of array with elements that reference it
    Object[] ob = new Object[5];
    ob[0] = new Integer(1); ob[1] = ob;
    ob[2] = new Integer(2); ob[3] = ob;
    ob[4] = new Integer(3);
    Object[] obserdes = (Object[]) serdes(ob);
    assert identityHashCode(obserdes) == identityHashCode(obserdes[1]);
    assert identityHashCode(obserdes) == identityHashCode(obserdes[3]);
    
    // test of list with elements that reference it
    List<Object> ol = new ArrayList<>();
    ol.add(1); ol.add(ol); ol.add(2); ol.add(ol); ol.add(3);
    List<Object> olserdes = (List<Object>) serdes(ol);
    assert identityHashCode(olserdes) == identityHashCode(olserdes.get(1));
    assert identityHashCode(olserdes) == identityHashCode(olserdes.get(3));
    
    // test ArrayBlockingQueue with element that references it
    ArrayBlockingQueue<Object> abq2 = new ArrayBlockingQueue<>(3);
    try {
      abq2.put(1); abq2.put(abq2); abq2.put(3);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    ArrayBlockingQueue<Object> abq2serdes = (ArrayBlockingQueue<Object>) serdes(abq2);
    Object[] abq2serdesa = abq2serdes.toArray();
    assert identityHashCode(abq2serdes) == identityHashCode(abq2serdesa[1]);

    // test map with a value that references it
    HashMap<Integer,Object> mapX = new HashMap<>();
    mapX.put(1, "one"); mapX.put(2, mapX); mapX.put(3, "three"); 
    HashMap<Integer,Object> mapXserdes = (HashMap<Integer,Object>) serdes(mapX);
    assert identityHashCode(mapXserdes) == identityHashCode(mapXserdes.get(2));
    
    // test map with a key that references it
    HashMap<Object, Integer> mapY = new HashMap<Object, Integer>(){
      private static final long serialVersionUID = 1L;
      // the default AbstractMap hashCode() method causes 
      // a stack overflow when a key references the map
      @Override
      public int hashCode() {
        return identityHashCode(this);
      }
    };
    mapY.put("one", 1); mapY.put(mapY, 2); mapY.put("three", 3); 
    HashMap<Object, Integer> mapYserdes = (HashMap<Object, Integer>) serdes(mapY);
    assert(identityHashCode(mapYserdes) == identityHashCode(invert(mapYserdes).get(2)));
    
    // test @Transient annotation
    // In TransientTest, fields c, d, e, f, g and h are annotated with @Transient
    // and serialization sets their values to their defaults, while fields a, b and
    // i are not annotated with @Transient and their values are serialized as is.
    TransientTest t = new TransientTest();
    //System.out.println(t);
    ////TransientTest(a=1, b=2, c=3, d=true, e=5.7, f=19.9, g=q, h=zzz, i=2.3)
    TransientTest tserdes = (TransientTest) serdes(t);
    //System.out.println(tserdes);
    ////TransientTest(a=1, b=2, c=0, d=false, e=0.0, f=0.0, g= , h=null, i=2.3)
    assert tserdes.getC() == 0;
    assert tserdes.getD() == false;
    assert tserdes.getE() == 0;
    assert tserdes.getF() == 0;
    assert tserdes.getG() == 0;
    assert tserdes.getH() == null;
    
    // Demos of saving and recovering a serialization string to and from a file:
    
    // copyString2File(ser(b), "ser2/B.ser");
    //// ser2/B.ser contains 3,295 bytes
    // B bserdes = (B) des(copyFile2String("ser2/B.ser"));
    // assert b.equals(bserdes);
  
    // With gzip.
    // gzipString2File(ser(b), "ser2/B.ser.gz");
    //// ser2/B.ser.gz contains 1,252 bytes
    // B bserdes2 = (B) des(gunzipFile2String("ser2/B.ser.gz"));
    // assert b.equals(bserdes2);
  
    // With AES encryption over gzip.
    // encryptString2File(ser(b), "ser2/B.aes", "password");
    // // ser2/B.aes contains 1,859 bytes
    // // keys/B.key contains 43 bytes
    // B bserdes3 = (B) des(decryptFile2String("ser2/B.aes"));
    // assert b.equals(bserdes3);

    System.out.println("all tests passed");

  }

}
