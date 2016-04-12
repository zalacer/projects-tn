package ch11.annotations;

import java.util.Objects;

public class Empty {
  
  Empty(){}
  
  @Override
  public int hashCode()  {
    return Objects.hashCode(this);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    return true;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Empty []");
    return builder.toString();
  }



  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
