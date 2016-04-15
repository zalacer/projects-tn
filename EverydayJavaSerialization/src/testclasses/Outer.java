package testclasses;

public class Outer {
  int outerint = 9;
  Outer(){}
  
  public class Inner1 {
    int inner1int = 1;
    Inner1(){}
 
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + inner1int;
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
      Inner1 other = (Inner1) obj;
      if (!getOuterType().equals(other.getOuterType()))
        return false;
      if (inner1int != other.inner1int)
        return false;
      return true;
    }

    public class Inner2 {
      int inner2int = 2;
      Inner2(){}
  
      @Override
      public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getOuterType().hashCode();
        result = prime * result + inner2int;
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
        Inner2 other = (Inner2) obj;
        if (!getOuterType().equals(other.getOuterType()))
          return false;
        if (inner2int != other.inner2int)
          return false;
        return true;
      }

      public class Inner3 {
        int inner3 = 3;
        Inner3(){}
        @Override
        public int hashCode() {
          final int prime = 31;
          int result = 1;
          result = prime * result + getOuterType().hashCode();
          result = prime * result + inner3;
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
          Inner3 other = (Inner3) obj;
          if (!getOuterType().equals(other.getOuterType()))
            return false;
          if (inner3 != other.inner3)
            return false;
          return true;
        }
        private Inner2 getOuterType() {
          return Inner2.this;
        }
      }

      private Inner1 getOuterType() {
        return Inner1.this;
      }
    }

    private Outer getOuterType() {
      return Outer.this;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + outerint;
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
    Outer other = (Outer) obj;
    if (outerint != other.outerint)
      return false;
    return true;
  }
  
}
