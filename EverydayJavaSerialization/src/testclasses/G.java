package testclasses;

import java.util.HashMap;
import java.util.Map;

public class G {
  public Map<String,Integer> map1 = new HashMap<String,Integer>(){ 
    private static final long serialVersionUID = 1L; { 
      put("one", 1); put("two", 2); put("three", 3);}};
      
  G(){}
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((map1 == null) ? 0 : map1.hashCode());
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
    G other = (G) obj;
    if (map1 == null) {
      if (other.map1 != null)
        return false;
    } else if (!map1.equals(other.map1))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("G [map1=");
    builder.append(map1);
    builder.append("]");
    return builder.toString();
  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
