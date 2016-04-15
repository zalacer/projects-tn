package testclasses;

public class D  extends C {
//  private String c = "d's c string";
  private String f = "f string";
  public Integer g = 5;
  public D d = null;
  
  public D(){}
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((d == null) ? 0 : d.hashCode());
    result = prime * result + ((f == null) ? 0 : f.hashCode());
    result = prime * result + ((g == null) ? 0 : g.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    D other = (D) obj;
    if (d == null) {
      if (other.d != null)
        return false;
    } else if (!d.equals(other.d))
      return false;
    if (f == null) {
      if (other.f != null)
        return false;
    } else if (!f.equals(other.f))
      return false;
    if (g == null) {
      if (other.g != null)
        return false;
    } else if (!g.equals(other.g))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("D(f=");
    builder.append(f);
    builder.append(", g=");
    builder.append(g);
    builder.append(", d=");
    builder.append(d);
    builder.append(")");
    return builder.toString();
  }

  public static void main(String[] args) {
    
  }

}
