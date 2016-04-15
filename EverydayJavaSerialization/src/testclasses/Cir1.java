package testclasses;

import static utils.UniversalToString.*;

import java.util.Objects;

public class Cir1 {
  public Cir1 c = this;
//  public IdentityHashMap<Object,Integer> cmap = new IdentityHashMap<>();
  public Cir1(){}

  @Override
  public String toString() {
    return universalToString(this);
  }

  public static boolean hasNoArgConstructor(Class<?> c) {
    if (Objects.isNull(c))
      throw new IllegalArgumentException("hasNoArgConstructor: c is null");
    try {
      return Objects.nonNull(c.getDeclaredConstructor());
    } catch (NoSuchMethodException | SecurityException e) {
      try {
        return Objects.nonNull(c.getConstructor());
      } catch (NoSuchMethodException | SecurityException e1) {
        e1.printStackTrace();
      }
    }
    return false;
  }


//  @SuppressWarnings("rawtypes")
  public static void main(String[] args) {
    
    int a = 1;
    int b = a;
    a = 2;
    System.out.println(b);
    
//    String s1 = "serialization.Circular2a^30303^b:serialization.Circular2b@C^map^java.util.HashMap@array^java.lang.Object^03032@array^java.lang.Object^03030"; 
//    String[] sa1 = s1.split("@C\\^");
//    for (String s : sa1) System.out.println(s);
    //  serialization.Circular2a^30303^b:serialization.Circular2b
    //  map^java.util.HashMap@array^java.lang.Object^03032@array^java.lang.Object^03030
    
//    Circular1 circular1b = new Circular1();
//    System.out.println(circular1b);
//    
//    System.out.println(hasNoArgConstructor(Circular1.class));
//    Constructor[] cons = Circular1.class.getConstructors();
//    for (Constructor con : cons) System.out.println(con);
    //public serialization.Circular1()
    
//    IdentityHashMap<Object, Integer> imap = new IdentityHashMap<>();
//    A a = new A();
//    imap.put(a, 1);
//    B b  = new B();
//    imap.put(b, 1);
//    imap.put(b, 1);
//    Circular1 circular1 = new Circular1();
//    imap.put(circular1, 1);
//    Class<?> stringClass = java.lang.String.class;
//    imap.put(stringClass, 1);
//    for (Object o : imap.keySet()) System.out.println(o);
//    
//    Map[] maps = new Map[3];
//    maps[0] = new IdentityHashMap<Object,Integer>();
//    maps[1] = new HashMap<String,String>();
//    System.out.println(Arrays.toString(maps));
   
//    IdentityHashMap<Object,Integer> imap = new IdentityHashMap<>();
//    Integer c = 0;
//    imap.put(new Object(), ++c);
//    System.out.println(c);
    
    
    
    
  }

}
