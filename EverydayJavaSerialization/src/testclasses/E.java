package testclasses;

public class E {
  public PaperSize enum1 = PaperSize.ISO_2A0;
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((enum1 == null) ? 0 : enum1.hashCode());
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
    E other = (E) obj;
    if (enum1 != other.enum1)
      return false;
    return true;
  }
  
    @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("E(enum1=");
    builder.append(enum1);
    builder.append(")");
    return builder.toString();
  }

  public static void main(String[] args) {

  }

}
