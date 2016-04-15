package testclasses;

public class NotAnnotated {
  String not = "notAnnotated";
  
  public NotAnnotated(){}
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((not == null) ? 0 : not.hashCode());
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
    NotAnnotated other = (NotAnnotated) obj;
    if (not == null) {
      if (other.not != null)
        return false;
    } else if (!not.equals(other.not))
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("NotAnnotated [not=");
    builder.append(not);
    builder.append("]");
    return builder.toString();
  }
  
}
