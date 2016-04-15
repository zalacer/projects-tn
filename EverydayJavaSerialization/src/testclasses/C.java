package testclasses;

public class C {
  private String c = "c string";
  String x = "x string";
  
  public C(){}
//  C(String c) { this.c = c; }

  public String getC() {
    return c;
  }
  public void setC(String c) {
    this.c = c;
  }
 
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((c == null) ? 0 : c.hashCode());
    result = prime * result + ((x == null) ? 0 : x.hashCode());
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
    C other = (C) obj;
    if (c == null) {
      if (other.c != null)
        return false;
    } else if (!c.equals(other.c))
      return false;
    if (x == null) {
      if (other.x != null)
        return false;
    } else if (!x.equals(other.x))
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("C(c=");
    builder.append(c);
    builder.append(", x=");
    builder.append(x);
    builder.append(")");
    return builder.toString();
  }

  public static void main(String[] args) {
    
  }

}
