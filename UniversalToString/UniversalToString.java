package uts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


// A static universal toString method for Java is included below and has signature
//   universalToString(Object obj, boolean oneLine, boolean simpleName).
// This is a wrapper for _universalToString that in turn calls specialized methods to 
// handle arrays, collections and maps. Handling of enums and other object types is built 
// into _universalToString. universalToString prints objects with cyclic references in 
// closed form, follows the formatting convention suggested in texts of Cay S. Horstmann  
// for subclass toString inclusion and uses traditional toString style for collections 
// and maps, but with expanded object descriptions instead of just a hashcode, while it   
// greatly enhances toString for enums by including all instance variables instead of only  
// the name. With this universal toString method toString methods of all Java classes can  
// simply return universalToString(this, oneLine, simpleName) for some choice of the last
// two boolean arguments depending on preferences.

public class UniversalToString {

  public static class Pair<U, V> {

    private U count;
    private V name;

    public Pair() {
    };

    public Pair(U count, V name) {
      this.count = count;
      this.name = name;
    }

    public U getCount() {
      return count;
    }

    public void setCount(U count) {
      this.count = count;
    }

    public V getName() {
      return name;
    }

    public void setName(V name) {
      this.name = name;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((count == null) ? 0 : count.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      @SuppressWarnings("rawtypes")
      Pair other = (Pair) obj;
      if (count == null) {
        if (other.count != null)
          return false;
      } else if (!count.equals(other.count))
        return false;
      if (name == null) {
        if (other.name != null)
          return false;
      } else if (!name.equals(other.name))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }

  }
  
  public static final String repeat(String c, int length) {
    char[] data = new char[length];
    Arrays.fill(data, c.toCharArray()[0]);
    return new String(data);
  }
  
  public static String tlf(String s) {
    return s.replaceAll("[\n\r\\f\\h\\s]+$", "").replaceAll("^[\n\r\\f\\h\\s]+", "");
  }

  public static boolean isBoxed(Class<? extends Object> c) {
    if (c.getSimpleName().matches("Integer|Long|Double|Byte|Character|Boolean|Short|Float|String"))
      return true;
    return false;
  }

  public static boolean isBoxed(Object o) {
    if (o.getClass().getSimpleName().matches("Integer|Long|Double|Byte|Character|Boolean|Short|Float|String"))
      return true;
    return false;
  }

  public static String universalToString4Map(Object o, boolean oneLine, boolean simpleName) {
    // If oneLine is false each element of o is printed on a separate line.

    if (o == null)
      return "";
    if (!(o instanceof Map))
      throw new IllegalArgumentException("arg o is not a Map");

    String pre, post, sep, indent, k, v;
    Object vo;
    StringBuilder sb = new StringBuilder();

    try {
      @SuppressWarnings("unchecked")
      Map<Object, Object> co = (Map<Object, Object>) o;
      Object[] oa = co.keySet().toArray();
      Class<? extends Object> c = o.getClass();
      String name = simpleName ? c.getSimpleName() : c.getName();
      if (oa.length == 0)
        return name + "{}";
      if (sb.length() > 0)
        sb.delete(0, sb.length() - 1);
      pre = name + "{";
      sb.append(pre);
      post = "}";
      sep = oneLine ? "," : ",\n";
      indent = sep.equals(",") ? "" : repeat(" ", pre.length());
      for (int j = 0; j < oa.length - 1; j++) {
        if (j > 0)
          sb.append(indent);
        if (isBoxed(oa[j])) {
          sb.append(oa[j].toString() + "=");
        } else {
          k = _universalToString(oa[j], true, simpleName, false);
          if (k.contains("] ["))
            k = "[" + k + "]";
          sb.append(k + "=");
        }
        vo = co.get(oa[j]);
        if (isBoxed(vo)) {
          sb.append(vo.toString() + sep);
        } else {
          v = _universalToString(vo, true, simpleName, false);
          if (v.contains("] ["))
            v = "[" + v + "]";
          sb.append(v + sep);
        }
      }
      if (oa.length > 1)
        sb.append(indent);
      if (isBoxed(oa[oa.length - 1])) {
        sb.append(oa[oa.length - 1].toString() + "=");
      } else {
        k = _universalToString(oa[oa.length - 1], true, simpleName, false);
        if (k.contains("] ["))
          k = "[" + k + "]";
        sb.append(k + "=");
      }
      vo = co.get(oa[oa.length - 1]);
      if (isBoxed(vo)) {
        sb.append(vo.toString() + post);
      } else {
        v = _universalToString(vo, true, simpleName, false);
        if (v.contains("] ["))
          v = "[" + v + "]";
        sb.append(v + post);
      }
      return sb.toString();
    } catch (ClassCastException e) {
      e.printStackTrace();
    }
    return "";
  }

  public static String universalToString4Collection(Object o, boolean oneLine, boolean simpleName) {
    // If oneLine is false each element of o is printed on a separate line.

    if (o == null)
      return "";
    if (!(o instanceof Collection))
      throw new IllegalArgumentException("arg o is not a Collection");

    String pre, post, sep, indent, uts;
    StringBuilder sb = new StringBuilder();

    try {
      @SuppressWarnings("unchecked")
      Collection<Object> co = (Collection<Object>) o;
      Object[] oa = co.toArray();
      Class<? extends Object> c = o.getClass();
      String name = simpleName ? c.getSimpleName() : c.getName();
      if (oa.length == 0)
        return name + "[]";
      if (sb.length() > 0)
        sb.delete(0, sb.length() - 1);
      pre = name + "[";
      sb.append(pre);
      post = "]";
      sep = oneLine ? "," : ",\n";
      indent = sep.equals(",") ? "" : repeat(" ", pre.length());
      for (int j = 0; j < oa.length - 1; j++) {
        if (j > 0)
          sb.append(indent);
        uts = _universalToString(oa[j], true, simpleName, false);
        if (uts.contains("] ["))
          uts = "[" + uts + "]";
        sb.append(uts + sep);
      }
      if (oa.length > 1)
        sb.append(indent);
      uts = _universalToString(oa[oa.length - 1], true, simpleName, false);
      if (uts.contains("] ["))
        uts = "[" + uts + "]";
      sb.append(uts + post);
      return sb.toString();
    } catch (ClassCastException e) {
      e.printStackTrace();
    }
    return "";
  }

  public static String universalToString4Array(Object o, boolean oneLine, boolean simpleName) {
    // If oneLine is false each element of o is printed on a separate line.

    if (o == null)
      return "";

    if (!o.getClass().isArray())
      throw new IllegalArgumentException("arg o is not an array");

    String pre, post, sep, indent, uts;
    StringBuilder sb = new StringBuilder();

    while (true) {
      try {
        int[] oa = (int[]) o;
        if (oa.length == 0)
          return "int[]";
        if (sb.length() > 0)
          sb.delete(0, sb.length() - 1);
        pre = "int[";
        sb.append(pre);
        sep = oneLine ? "," : ",\n";
        indent = sep.equals(",") ? "" : repeat(" ", pre.length());
        for (int j = 0; j < oa.length - 1; j++) {
          if (j > 0)
            sb.append(indent);
          sb.append(oa[j] + sep);
        }
        if (oa.length > 1)
          sb.append(indent);
        sb.append(oa[oa.length - 1] + "]");
        return sb.toString();
      } catch (ClassCastException e) {
      }

      try {
        long[] oa = (long[]) o;
        if (oa.length == 0)
          return "long[]";
        if (sb.length() > 0)
          sb.delete(0, sb.length() - 1);
        pre = "long[";
        sb.append(pre);
        sep = oneLine ? "," : ",\n";
        indent = sep.equals(",") ? "" : repeat(" ", pre.length());
        for (int j = 0; j < oa.length - 1; j++) {
          if (j > 0)
            sb.append(indent);
          sb.append(oa[j] + sep);
        }
        if (oa.length > 1)
          sb.append(indent);
        sb.append(oa[oa.length - 1] + "]");
        return sb.toString();
      } catch (ClassCastException e) {
      }

      try {
        double[] oa = (double[]) o;
        if (oa.length == 0)
          return "double[]";
        if (sb.length() > 0)
          sb.delete(0, sb.length() - 1);
        pre = "double[";
        sb.append(pre);
        sep = oneLine ? "," : ",\n";
        indent = sep.equals(",") ? "" : repeat(" ", pre.length());
        for (int j = 0; j < oa.length - 1; j++) {
          if (j > 0)
            sb.append(indent);
          sb.append(oa[j] + sep);
        }
        if (oa.length > 1)
          sb.append(indent);
        sb.append(oa[oa.length - 1] + "]");
        return sb.toString();
      } catch (ClassCastException e) {
      }

      try {
        byte[] oa = (byte[]) o;
        if (oa.length == 0)
          return "byte[]";
        if (sb.length() > 0)
          sb.delete(0, sb.length() - 1);
        pre = "byte[";
        sb.append(pre);
        sep = oneLine ? "," : ",\n";
        indent = sep.equals(",") ? "" : repeat(" ", pre.length());
        for (int j = 0; j < oa.length - 1; j++) {
          if (j > 0)
            sb.append(indent);
          sb.append(oa[j] + sep);
        }
        if (oa.length > 1)
          sb.append(indent);
        sb.append(oa[oa.length - 1] + "]");
        return sb.toString();
      } catch (ClassCastException e) {
      }

      try {
        char[] oa = (char[]) o;
        if (oa.length == 0)
          return "char[]";
        if (sb.length() > 0)
          sb.delete(0, sb.length() - 1);
        pre = "char[";
        sb.append(pre);
        sep = oneLine ? "," : ",\n";
        indent = sep.equals(",") ? "" : repeat(" ", pre.length());
        for (int j = 0; j < oa.length - 1; j++) {
          if (j > 0)
            sb.append(indent);
          sb.append(oa[j] + sep);
        }
        if (oa.length > 1)
          sb.append(indent);
        sb.append(oa[oa.length - 1] + "]");
        return sb.toString();
      } catch (ClassCastException e) {
      }

      try {
        boolean[] oa = (boolean[]) o;
        if (oa.length == 0)
          return "boolean[]";
        if (sb.length() > 0)
          sb.delete(0, sb.length() - 1);
        pre = "boolean[";
        sb.append(pre);
        sep = oneLine ? "," : ",\n";
        indent = sep.equals(",") ? "" : repeat(" ", pre.length());
        for (int j = 0; j < oa.length - 1; j++) {
          if (j > 0)
            sb.append(indent);
          sb.append(oa[j] + sep);
        }
        if (oa.length > 1)
          sb.append(indent);
        sb.append(oa[oa.length - 1] + "]");
        return sb.toString();
      } catch (ClassCastException e) {
      }

      try {
        float[] oa = (float[]) o;
        if (oa.length == 0)
          return "float[]";
        if (sb.length() > 0)
          sb.delete(0, sb.length() - 1);
        pre = "float[";
        sb.append(pre);
        sep = oneLine ? "," : ",\n";
        indent = sep.equals(",") ? "" : repeat(" ", pre.length());
        for (int j = 0; j < oa.length - 1; j++) {
          if (j > 0)
            sb.append(indent);
          sb.append(oa[j] + sep);
        }
        if (oa.length > 1)
          sb.append(indent);
        sb.append(oa[oa.length - 1] + "]");
        return sb.toString();
      } catch (ClassCastException e) {
      }

      try {
        short[] oa = (short[]) o;
        if (oa.length == 0)
          return "short[]";
        if (sb.length() > 0)
          sb.delete(0, sb.length() - 1);
        pre = "short[";
        sb.append(pre);
        sep = oneLine ? "," : ",\n";
        indent = sep.equals(",") ? "" : repeat(" ", pre.length());
        for (int j = 0; j < oa.length - 1; j++) {
          if (j > 0)
            sb.append(indent);
          sb.append(oa[j] + sep);
        }
        if (oa.length > 1)
          sb.append(indent);
        sb.append(oa[oa.length - 1] + "]");
        return sb.toString();
      } catch (ClassCastException e) {
      }

      try {
        Object[] oa = (Object[]) o;
        String name = simpleName ? o.getClass().getComponentType().getSimpleName()
            : o.getClass().getComponentType().getName();
        Class<?> sc = o.getClass().getComponentType();
        boolean p = false;
        if (isBoxed(sc))
          p = true;
        if (oa.length == 0)
          return name + "[]";
        if (sb.length() > 0)
          sb.delete(0, sb.length() - 1);
        pre = name + "[";
        sb.append(pre);
        post = "]";
        sep = oneLine ? "," : ",\n";
        indent = sep.equals(",") ? "" : repeat(" ", pre.length());
        for (int j = 0; j < oa.length - 1; j++) {
          if (j > 0)
            sb.append(indent);
          if (p) {
            uts = oa[j].toString();
          } else {
            uts = _universalToString(oa[j], true, simpleName, false);
            if (uts.contains("] ["))
              uts = "[" + uts + "]";
            sb.append("[");
          }
          sb.append(uts + sep);
        }
        if (oa.length > 1)
          sb.append(indent);
        if (p) {
          uts = oa[oa.length - 1].toString();
        } else {
          uts = _universalToString(oa[oa.length - 1], true, simpleName, false);
          if (uts.contains("] ["))
            uts = "[" + uts + "]";
          sb.append("[");
        }
        sb.append(uts + post);
        return sb.toString();
      } catch (ClassCastException e) {
        e.printStackTrace();
      }
      break;
    }
    return "";
  }

  // wrapper for _universalToString
  public static String universalToString(Object obj, boolean oneLine, boolean simpleName) {
    return _universalToString(obj, oneLine, simpleName, false);
  }

  @SafeVarargs
  public static String _universalToString(Object obj, boolean oneLine, boolean simpleName, boolean tmp,
      Map<Integer, Pair<Integer, String>>... h) {

    if (isBoxed(obj))
      return obj.toString();

    if (obj.getClass().isArray()) {
      return universalToString4Array(obj, oneLine, simpleName);
    }

    if (obj instanceof Collection) {
      return universalToString4Collection(obj, oneLine, simpleName);
    }

    boolean isEnum = false;
    if (obj.getClass().isEnum())
      isEnum = true;

    if (obj instanceof Map) {
      return universalToString4Map(obj, oneLine, simpleName);
    }

    List<List<String>> out = new ArrayList<>();
    out.add(new ArrayList<String>());
    out.add(new ArrayList<String>());
    Map<Integer, Pair<Integer, String>> hashes = null;
    if (h.length == 0) {
      hashes = new HashMap<Integer, Pair<Integer, String>>();
    } else {
      hashes = h[0];
    }
    int hash = 0;
    Class<? extends Object> nclass = obj.getClass();
    if (nclass.getName().equals("java.lang.Object"))
      return obj.toString(); // e.g. java.lang.Object@2a139a55
    hash = Objects.hashCode(obj);
    if (hashes.keySet().contains(hash)) {
      hashes.get(hash).setCount(hashes.get(hash).getCount() + 1);
    }
    List<Class<? extends Object>> il = new ArrayList<>();
    if (!nclass.getSuperclass().getName().equals("java.lang.Object")) {
      il.add(nclass.getSuperclass());
    }
    il.add(nclass);
    StringBuilder sb = new StringBuilder();
    int[] indent = new int[] { 0, 0 };

    for (int i = 0; i < il.size(); i++) {
      if (i == 0) {
        if (il.size() == 2) {
          if (simpleName) {
            sb.append(il.get(1).getSimpleName() + "[");
            out.get(0).add(il.get(1).getSimpleName() + "[");
            indent[0] = (il.get(1).getSimpleName() + "[").length();
          } else {
            sb.append(il.get(1).getName() + "[");
            out.get(0).add(il.get(1).getName() + "[");
            indent[0] = (il.get(1).getName() + "[").length();
          }
        } else {
          if (simpleName) {
            sb.append(il.get(0).getSimpleName() + "[");
            out.get(0).add(il.get(0).getSimpleName() + "[");
            indent[0] = (il.get(0).getSimpleName() + "[").length();
          } else {
            sb.append(il.get(0).getName() + "[");
            out.get(0).add(il.get(0).getName() + "[");
            indent[0] = (il.get(0).getName() + "[").length();
          }
        }
      } else {
        if (oneLine) {
          sb.append(" [");
          out.get(1).add("[");
        } else {
          sb.append("[");
          out.get(1).add("[");
          indent[1] = 1;
        }
      }

      Class<? extends Object> tclass = il.get(i);
      int c1 = 0;
      // enum handling setup
      StringBuilder sb1 = null;
      List<String> elist = null;
      boolean enumActive = false;
      if (isEnum) {
        sb1 = new StringBuilder();
        elist = new ArrayList<String>();
        enumActive = true;
      }
      if (enumActive && !isEnum) {
        sb1 = null;
        elist = null;
      }
      // end of enum handling setup
      for (Field f : tclass.getDeclaredFields()) {
        f.setAccessible(true);
        String name = f.getName();
        Class<?> fclass = f.getType();
        // Enum handling of inner Enum references
        if (isEnum) {
          int elen = 0;
          if (c1 == 0)
            sb1.append("values=");
          if (obj.getClass().getName().equals(fclass.getName())) {
            c1++;
            elist.add(name);
            continue;
          }
          if (name.equals("ENUM$VALUES")) {
            try {
              elen = ((Object[]) f.get(obj)).length;
              if (elen > 0 && c1 == elen) {
                sb1.append(elist);
                sb.append(sb1.toString() + ",");
                out.get(i).add(sb1.toString() + ",");
              }
            } catch (IllegalArgumentException | IllegalAccessException e) {
              e.printStackTrace();
            }
            continue;
          }

        } // end of Enum handling
        boolean primitive = fclass.isPrimitive();
        Object value = null;
        try {
          value = f.get(obj);
        } catch (IllegalArgumentException | IllegalAccessException e) {
          System.out.println("exception when referencing parameter " + name + " " + "with value of type "
              + value.getClass().getName() + " in object of " + obj.getClass().getName());
          continue;
        }
        if (value == null || value == "") {
          value = "null";
          primitive = true;
        }
        if (isBoxed(value)) {
          primitive = true;
          value = value.toString();
        }
        if (value.getClass().isArray()) {
          value = universalToString(value, oneLine, simpleName);
          primitive = true;
        }
        if (primitive) {
          sb.append(name + "=" + value + ",");
          out.get(i).add(name + "=" + value + ",");
        } else {
          hash = Objects.hashCode(value);
          if (!hashes.keySet().contains(hash)) {
            for (Integer j : hashes.keySet()) {
              if (hashes.get(j).getName().equals(name) && j.intValue() != hash) {
                name = name + "@" + Integer.toHexString(hash);
                break;
              }
            }
            hashes.put(hash, new Pair<Integer, String>(1, name));
          } else {
            if (Objects.isNull(hashes.get(hash).getName()) || hashes.get(hash).getName().equals("")) {
            } else {
              hashes.get(hash).setCount(hashes.get(hash).getCount() + 1);
            }
          }

          // When a parameter is first encountered its name and value are
          // saved in the map of object hash codes, so the next time that
          // object occurs it's designated in the output with just the
          // the parameter name. This prevents stack overflows and 
          // and fundamentally hinges on Java's capability to create 
          // objects with circular references and assign them unique hash 
          // codes. In some cases, however, a compound object may contain 
          // different other objects of the same or different classes but 
          // with an identically named parameter. In that case all but the 
          // first occurence of such a parameter name is qualified by 
          // appending "@"+Integer.toHexString(hash) to it, where hash is 
          // the int hashcode of the object it references, in order to 
          // render the resulting name unique in the output string.

          if (hashes.get(hash).getCount().intValue() > 2) {
            if (name.equals(hashes.get(hash).getName())) {
              sb.append(name + ","); // simply append the parameter name
              out.get(i).add(name + ",");
            } else {
              sb.append(name + "=" + hashes.get(hash).getName() + ",");
              out.get(i).add(name + "=" + hashes.get(hash).getName() + ",");
            }
          } else {
            String uts = _universalToString(value, oneLine, simpleName, true, hashes);
            sb.append(name + "=" + uts + ",");
            out.get(i).add(name + "=" + uts + ",");
          }
        }
      }
      String sbs = sb.toString();
      if (sbs.matches(".*?,")) {
        sbs = sbs.substring(0, sbs.length() - 1) + "]";
      } else if (sbs.matches(".*?,]")) {
        sbs = sbs.substring(0, sbs.length() - 2) + "]";
      }
      sb.delete(0, sb.length());
      if (i == 1 && (!(sbs.charAt(sbs.length() - 1) == ']')))
        sbs = sbs + "]";
      sb.append(sbs);

      String last = out.get(i).get(out.get(i).size() - 1);
      if (last.matches(".*?,")) {
        last = last.substring(0, last.length() - 1) + "]";
        out.get(i).remove(out.get(i).size() - 1);
        out.get(i).add(last);
      } else if (last.matches(".*?,]")) {
        last = last.substring(0, sbs.length() - 2) + "]";
        out.get(i).remove(out.get(i).size() - 1);
        out.get(i).add(last);
      }
      if (i == 1 && (!(last.charAt(last.length() - 1) == ']')))
        last = last + "]";
      out.get(i).remove(out.get(i).size() - 1);
      out.get(i).add(last);
    }

    StringBuilder sb2 = new StringBuilder();
    if (!tmp) {
      boolean once = false;

      if (out.get(0).size() > 0) {
        for (int j = 0; j < out.get(0).size(); j++) {
          if (j < 2) {
            sb2.append(out.get(0).get(j));
          } else {
            if (!once) {
              sb2.append("\n");
              once = true;
            }
            sb2.append(repeat(" ", indent[0]) + out.get(0).get(j) + "\n");
          }
        }
      }

      once = false;

      if (out.get(1).size() > 0) {
        for (int j = 0; j < out.get(1).size(); j++) {
          if (j < 2) {
            sb2.append(out.get(1).get(j));
          } else {
            if (!once) {
              sb2.append("\n");
              once = true;
            }
            sb2.append(repeat(" ", indent[1]) + out.get(1).get(j) + "\n");
          }
        }
      }
    }

    if (tmp)
      return sb.toString();
    if (oneLine)
      return tlf(sb.toString());
    return tlf(sb2.toString());

  }

  // following are classes for universalToString demos

  public static class A {
    B b;
    int aid = 1;

    public A() {
      super();
    }

    public A(B b) {
      this.b = b;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }
  }

  public static class A2 {
    B2 x;
    int a2id = 2;

    public A2() {
      super();
    }

    public A2(B2 b) {
      this.x = b;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }
  }

  public static class B {
    public A a;
    int bid = 3;

    public B() {
      super();
    }

    public B(A a) {
      this.a = a;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }
  }

  public static class B2 {
    public A2 y;
    int b2id = 4;

    public B2() {
      super();
    }

    public B2(A2 a) {
      this.y = a;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }
  }

  public static class C {
    A a;
    B b;
    int cid = 5;

    public C() {
      super();
    }

    public C(A a, B b) {
      this.a = a;
      this.b = b;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }
  }

  public static class C2 {
    A2 x;
    B2 y;
    int c2id = 6;

    public C2() {
      super();
    }

    public C2(A2 a, B2 b) {
      this.x = a;
      this.y = b;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }
  }

  public static class D {
    A a;
    B b;
    C c;
    int did = 7;

    public D() {
      super();
    }

    public D(A a, B b, C c) {
      this.a = a;
      this.b = b;
      this.c = c;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }
  }

  public static class D2 {
    A2 x;
    B2 y;
    C2 z;
    int d2id = 8;

    public D2() {
      super();
    }

    public D2(A2 a, B2 b, C2 c) {
      this.x = a;
      this.y = b;
      this.z = c;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }
  }

  public static class F {
    G g;
    int fid = 9;

    public F() {
      super();
    }

    public F(G g) {
      this.g = g;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }

  }

  public static class G {
    H h;
    int gid = 10;

    public G() {
      super();
    }

    public G(H h) {
      this.h = h;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }
  }

  public static class H {
    F f;
    int hid = 11;

    public H() {
      super();
    }

    public H(F f) {
      this.f = f;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }
  }

  public static class Employee {
    private String name;
    private double salary;

    public Employee(String name, double salary) {
      this.name = name;
      this.salary = salary;
    }

    public void raiseSalary(double byPercent) {
      double raise = salary * byPercent / 100;
      salary += raise;
    }

    public final String getName() {
      return name;
    }

    public double getSalary() {
      return salary;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }

  }

  public static class Manager extends Employee {
    private double bonus;

    public Manager(String name, double salary) {
      super(name, salary);
      bonus = 0;
    }

    public void setBonus(double bonus) {
      this.bonus = bonus;
    }

    public double getSalary() { // Overrides superclass method
      return super.getSalary() + bonus;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }

  }

  public static class ArrayListSubClass extends ArrayList<Object> {
    private static final long serialVersionUID = 1L;
    int alscid = 9;

    public ArrayListSubClass() {
      super();
    }

    public ArrayListSubClass(Collection<? extends Object> c) {
      super(c);
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }
  }

  public enum PaperSize {
    ISO_4A0("ISO 216 4A0", "mm", 1632, 2378), ISO_2A0("ISO 216 2A0", "mm", 1189, 1682), ISO_A0("ISO 216 A0", "mm", 841,
        1189);
    final String info;
    final String unit;
    final double width;
    final double height;

    PaperSize(String info, String unit, double width, double height) {
      this.info = info;
      this.unit = unit;
      this.width = width;
      this.height = height;
    }

    //    @Override
    //    public String toString() { return universalToString(this, true, true); }
  }

  public static void main(String[] args) {

    Employee e1 = new Employee("Joe", 81000.09);
    Employee e2 = new Employee("Ivy", 91000.02);
    Employee e3 = new Employee("May", 76300.00);
    Manager m1 = new Manager("Jane", 101000.10);
    Manager m2 = new Manager("Zack", 115000.50);
    Manager m3 = new Manager("Elise", 124998.50);
    m1.setBonus(23500.67);
    m2.setBonus(59430.00);
    m3.setBonus(36100.19);
    A a1 = new A();
    B b1 = new B(a1);
    a1.b = b1;
    C c1 = new C(a1, b1);
    A2 a2 = new A2();
    B2 b2 = new B2(a2);
    a2.x = b2;
    C2 c2 = new C2(a2, b2);
    D d1 = new D(a1, b1, c1);
    D2 d2 = new D2(a2, b2, c2);
    F f = new F();
    G g = new G();
    H h = new H();
    f.g = g;
    g.h = h;
    h.f = f;
    ArrayList<String> arrayListStrings = new ArrayList<>(Arrays.asList("one", "two", "three"));
    List<Object> arrayListObjects = new ArrayList<>(Arrays.asList(f, g, h));
    ArrayListSubClass arrayListSubClassObjects = new ArrayListSubClass(Arrays.asList(f, g, h));
    Set<Integer> setIntegers = new HashSet<>(Arrays.asList(1, 2, 3));
    Set<Object> setObjects = new LinkedHashSet<>(Arrays.asList(a1, b1, c1, d1));
    PaperSize ps4a0Enum = PaperSize.ISO_4A0;
    Map<Integer, String> map1 = new HashMap<>();
    map1.put(1, "one");
    map1.put(2, "two");
    map1.put(3, "three");
    LinkedHashMap<Character, Object> map2 = new LinkedHashMap<>();
    map2.put('x', a1);
    map2.put('y', b1);
    map2.put('z', c1);

    System.out.println(universalToString(new int[0], true, true) + "\n");
    //      int[]

    System.out.println(universalToString(new int[0], false, false) + "\n");
    //      int[]

    System.out.println(universalToString(new int[] { 1 }, true, true) + "\n");
    //      int[1]

    System.out.println(universalToString(new int[] { 1 }, false, false) + "\n");
    //      int[1]

    System.out.println(universalToString(new int[] { 1, 2, 3 }, true, true) + "\n");
    //      int[1,2,3] 

    System.out.println(universalToString(new int[] { 1, 2, 3 }, false, false) + "\n");
    //      int[1,
    //          2,
    //          3]

    System.out.println(universalToString(new long[] { 1L, 2L, 3L }, true, true) + "\n");
    //      long[1,2,3]

    System.out.println(universalToString(new double[] { 1., 2., 3. }, true, true) + "\n");
    //      double[1.0,2.0,3.0]

    System.out.println(universalToString(new byte[] { 1, 2, 3 }, true, true) + "\n");
    //      byte[1,2,3]

    System.out.println(universalToString(new char[] { 'a', 'b', 'c' }, true, true) + "\n");
    //      char[a,b,c]

    System.out.println(universalToString(new boolean[] { true, false, true }, true, true) + "\n");
    //      boolean[true,false,true]

    System.out.println(universalToString(new boolean[] { true, false, true }, false, false) + "\n");
    //      boolean[true,
    //              false,
    //              true]

    System.out.println(universalToString(new float[] { 1.e0f, 2.e1f, 3.e2f }, true, true) + "\n");
    //      float[1.0,20.0,300.0]

    System.out.println(universalToString(new short[] { 1, 2, 3 }, true, true) + "\n");
    //      short[1,2,3]

    System.out.println(universalToString(new String[] { "one", "two", "three" }, true, true) + "\n");
    //      String[one,two,three]

    System.out.println(
        universalToString(new Integer[] { new Integer(1), new Integer(2), new Integer(3) }, true, true) + "\n");
    //      Integer[1,2,3]   

    System.out.println(universalToString(e1, true, true) + "\n");
    //      Employee[name=Joe,salary=81000.09]

    System.out.println(universalToString(e1, false, true) + "\n");
    //      Employee[name=Joe,
    //               salary=81000.09]

    System.out.println(universalToString(m1, true, true) + "\n");
    //      Manager[name=Jane,salary=101000.1] [bonus=23500.67]

    System.out.println(universalToString(m1, false, true) + "\n");
    //      Manager[name=Jane,
    //              salary=101000.1]
    //      [bonus=23500.67]

    System.out.println(universalToString(new Employee[] { e1, e2, e3 }, true, true) + "\n");
    //      Employee[[Employee[name=Joe,salary=81000.09],[Employee[name=Ivy,salary=91000.02],[Employee[name=May,salary=76300.0]]

    System.out.println(universalToString(new Employee[] { e1, e2, e3 }, false, true) + "\n");
    //      Employee[[Employee[name=Joe,salary=81000.09],
    //               [Employee[name=Ivy,salary=91000.02],
    //               [Employee[name=May,salary=76300.0]]

    System.out.println(universalToString(new Manager[] { m1, m2, m3 }, true, true) + "\n");
    //      Manager[[[Manager[name=Jane,salary=101000.1] [bonus=23500.67]],[[Manager[name=Zack,salary=115000.5] [bonus=59430.0]],[[Manager[name=Elise,salary=124998.5] [bonus=36100.19]]]

    System.out.println(universalToString(new Manager[] { m1, m2, m3 }, false, true) + "\n");
    //      Manager[[[Manager[name=Jane,salary=101000.1] [bonus=23500.67]],
    //              [[Manager[name=Zack,salary=115000.5] [bonus=59430.0]],
    //              [[Manager[name=Elise,salary=124998.5] [bonus=36100.19]]]

    System.out.println(universalToString(a1, true, true) + "\n");
    //      A[b=B[a=A[b,aid=1],bid=3],aid=1]

    System.out.println(universalToString(a1, false, true) + "\n");
    //      A[b=B[a=A[b,aid=1],bid=3],
    //        aid=1]

    System.out.println(universalToString(b1, true, true) + "\n");
    //      B[a=A[b=B[a,bid=3],aid=1],bid=3]

    System.out.println(universalToString(b1, false, true) + "\n");
    //      B[a=A[b=B[a,bid=3],aid=1],
    //        bid=3]

    System.out.println(universalToString(c1, true, true) + "\n");
    //      C[a=A[b=B[a,bid=3],aid=1],b,cid=5]

    System.out.println(universalToString(c1, false, true) + "\n");
    //      C[a=A[b=B[a,bid=3],aid=1],
    //        b,
    //        cid=5]

    System.out.println(universalToString(d1, true, true) + "\n");
    //      D[a=A[b=B[a,bid=3],aid=1],b,c=C[a,b,cid=5],did=7]

    System.out.println(universalToString(d1, false, true) + "\n");
    //      D[a=A[b=B[a,bid=3],aid=1],
    //        b,
    //        c=C[a,b,cid=5],
    //        did=7]

    System.out.println(universalToString(new Object[] { a1, b1, c1, d1 }, true, true) + "\n");
    //      Object[[A[b=B[a=A[b,aid=1],bid=3],aid=1],[B[a=A[b=B[a,bid=3],aid=1],bid=3],[C[a=A[b=B[a,bid=3],aid=1],b,cid=5],[D[a=A[b=B[a,bid=3],aid=1],b,c=C[a,b,cid=5],did=7]]

    System.out.println(universalToString(new Object[] { a1, b1, c1, d1 }, false, true) + "\n");
    //      Object[[A[b=B[a=A[b,aid=1],bid=3],aid=1],
    //             [B[a=A[b=B[a,bid=3],aid=1],bid=3],
    //             [C[a=A[b=B[a,bid=3],aid=1],b,cid=5],
    //             [D[a=A[b=B[a,bid=3],aid=1],b,c=C[a,b,cid=5],did=7]]

    System.out.println(universalToString(d2, true, true) + "\n");
    //      D2[x=A2[x@1540e19d=B2[y=x,b2id=4],a2id=2],y=x@1540e19d,z=C2[x,y=x@1540e19d,c2id=6],d2id=8]

    System.out.println(universalToString(d2, false, true) + "\n");
    //      D2[x=A2[x@1540e19d=B2[y=x,b2id=4],a2id=2],
    //         y=x@232204a1,
    //         z=C2[x,y=x@232204a1,c2id=6],
    //         d2id=8]

    System.out.println(universalToString(f, true, true) + "\n");
    //      F[g=G[h=H[f=F[g,fid=9],hid=11],gid=10],fid=9]

    System.out.println(universalToString(g, true, true) + "\n");
    //      G[h=H[f=F[g=G[h,gid=10],fid=9],hid=11],gid=10]

    System.out.println(universalToString(h, true, true) + "\n");
    //      H[f=F[g=G[h=H[f,hid=11],gid=10],fid=9],hid=11]

    System.out.println(universalToString(arrayListStrings, true, true) + "\n");
    //      ArrayList[one,two,three]

    System.out.println(universalToString(arrayListObjects, false, true) + "\n");
    //      ArrayList[F[g=G[h=H[f=F[g,fid=9],hid=11],gid=10],fid=9],
    //                G[h=H[f=F[g=G[h,gid=10],fid=9],hid=11],gid=10],
    //                H[f=F[g=G[h=H[f,hid=11],gid=10],fid=9],hid=11]]

    System.out.println(universalToString(arrayListSubClassObjects, false, true) + "\n");
    //      ArrayListSubClass[F[g=G[h=H[f=F[g,fid=9],hid=11],gid=10],fid=9],
    //                        G[h=H[f=F[g=G[h,gid=10],fid=9],hid=11],gid=10],
    //                        H[f=F[g=G[h=H[f,hid=11],gid=10],fid=9],hid=11]]

    System.out.println(universalToString(setIntegers, true, true) + "\n");
    //      HashSet[1,2,3]  

    System.out.println(universalToString(setObjects, false, true) + "\n");
    //      LinkedHashSet[A[b=B[a=A[b,aid=1],bid=3],aid=1],
    //                    B[a=A[b=B[a,bid=3],aid=1],bid=3],
    //                    C[a=A[b=B[a,bid=3],aid=1],b,cid=5],
    //                    D[a=A[b=B[a,bid=3],aid=1],b,c=C[a,b,cid=5],did=7]]

    System.out.println(universalToString(ps4a0Enum, false, true) + "\n");
    //      PaperSize[name=ISO_4A0,
    //                ordinal=0]
    //      [info=ISO 216 4A0,
    //       unit=mm,
    //       width=1632.0,
    //       height=2378.0,
    //       values=[ISO_4A0, ISO_2A0, ISO_A0]]

    System.out.println(universalToString(map1, true, true) + "\n");
    //      HashMap{1=one,2=two,3=three}

    System.out.println(universalToString(map1, false, true) + "\n");
    //      HashMap{1=one,
    //              2=two,
    //              3=three}    

    System.out.println(universalToString(map2, false, true) + "\n");
    //      LinkedHashMap{x=A[b=B[a=A[b,aid=1],bid=3],aid=1],
    //                    y=B[a=A[b=B[a,bid=3],aid=1],bid=3],
    //                    z=C[a=A[b=B[a,bid=3],aid=1],b,cid=5]}

  }

}
