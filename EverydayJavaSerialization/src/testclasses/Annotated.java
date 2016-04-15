package testclasses;

import annotations.Serializable;

@Serializable
public class Annotated {
  String ann = "annotated";
  
  public Annotated(){}
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((ann == null) ? 0 : ann.hashCode());
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
    Annotated other = (Annotated) obj;
    if (ann == null) {
      if (other.ann != null)
        return false;
    } else if (!ann.equals(other.ann))
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Annotated(ann=");
    builder.append(ann);
    builder.append(")");
    return builder.toString();
  }
  
  
}
