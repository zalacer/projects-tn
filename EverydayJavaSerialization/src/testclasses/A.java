package testclasses;

import annotations.Serializable;

@Serializable public class A {
  private String a = "a string";
  
  public A(){}
  A(String a) { this.a = a; }

  public String getA() {
    return a;
  }
  public void setA(String a) {
    this.a = a;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((a == null) ? 0 : a.hashCode());
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
    A other = (A) obj;
    if (a == null) {
      if (other.a != null)
        return false;
    } else if (!a.equals(other.a))
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("A [a=");
    builder.append(a);
    builder.append("]");
    return builder.toString();
  }
  
  
  
  public static void main(String[] args) {
    
  }

}
