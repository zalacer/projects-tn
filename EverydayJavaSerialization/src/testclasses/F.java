package testclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
//import java.util.List;
//import java.util.Set;

import annotations.Serializable;

@Serializable
public class F {
  int z = 1;
  public double d = 2.2;
  public boolean bool = true;
  private String s = "what";
  protected String pstr = "protected string";
  public static String cstatic = "c string";
  public int[] ia = new int[]{1,2,3};
  public String[]  sa = new String[]{"one","two","three"};
  public Character[] ca = new Character[]{'a',null,'c'};
  public HashSet<Double> set1 = new HashSet<>(Arrays.asList(1.1,2.2,3.3));
  public ArrayList<Boolean> list1 = new ArrayList<>(Arrays.asList(true,false,true));
  public A a = new A("hello a");
  public C c = new C();
 
  public F(){}
  
  public String getS() {
    return s;
  }

  public void setS(String s) {
    this.s = s;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((a == null) ? 0 : a.hashCode());
    result = prime * result + (bool ? 1231 : 1237);
    result = prime * result + ((c == null) ? 0 : c.hashCode());
    result = prime * result + Arrays.hashCode(ca);
    long temp;
    temp = Double.doubleToLongBits(d);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + Arrays.hashCode(ia);
    result = prime * result + ((list1 == null) ? 0 : list1.hashCode());
    result = prime * result + ((pstr == null) ? 0 : pstr.hashCode());
    result = prime * result + ((s == null) ? 0 : s.hashCode());
    result = prime * result + Arrays.hashCode(sa);
    result = prime * result + ((set1 == null) ? 0 : set1.hashCode());
    result = prime * result + z;
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
    F other = (F) obj;
    if (a == null) {
      if (other.a != null)
        return false;
    } else if (!a.equals(other.a))
      return false;
    if (bool != other.bool)
      return false;
    if (c == null) {
      if (other.c != null)
        return false;
    } else if (!c.equals(other.c))
      return false;
    if (!Arrays.equals(ca, other.ca))
      return false;
    if (Double.doubleToLongBits(d) != Double.doubleToLongBits(other.d))
      return false;
    if (!Arrays.equals(ia, other.ia))
      return false;
    if (list1 == null) {
      if (other.list1 != null)
        return false;
    } else if (!list1.equals(other.list1))
      return false;
    if (pstr == null) {
      if (other.pstr != null)
        return false;
    } else if (!pstr.equals(other.pstr))
      return false;
    if (s == null) {
      if (other.s != null)
        return false;
    } else if (!s.equals(other.s))
      return false;
    if (!Arrays.equals(sa, other.sa))
      return false;
    if (set1 == null) {
      if (other.set1 != null)
        return false;
    } else if (!set1.equals(other.set1))
      return false;
    if (z != other.z)
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("F [z=");
    builder.append(z);
    builder.append(", d=");
    builder.append(d);
    builder.append(", bool=");
    builder.append(bool);
    builder.append(", s=");
    builder.append(s);
    builder.append(", pstr=");
    builder.append(pstr);
    builder.append(", ia=");
    builder.append(Arrays.toString(ia));
    builder.append(", sa=");
    builder.append(Arrays.toString(sa));
    builder.append(", ca=");
    builder.append(Arrays.toString(ca));
    builder.append(", set1=");
    builder.append(set1);
    builder.append(", list1=");
    builder.append(list1);
    builder.append(", a=");
    builder.append(a);
    builder.append(", c=");
    builder.append(c);
    builder.append("]");
    return builder.toString();
  }


  
  
  
//  public static void main(String[] args) {
//
//  }

}
