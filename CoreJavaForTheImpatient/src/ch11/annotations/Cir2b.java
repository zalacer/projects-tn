package ch11.annotations;

import static ch04.inheritance.reflection.Ch0409UniversalToString.*;

public class Cir2b {
  public Cir2a a = null;
  public Cir2b(){}
  
  @Override
  public String toString() {
    return universalToString(this);
  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
