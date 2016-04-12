package ch11.annotations;

import static ch04.inheritance.reflection.Ch0409UniversalToString.*;

public class Cir2a {
  public Cir2b b = null;
  public Cir2a(){}
  
  @Override
  public String toString() {
    return universalToString(this);
  }
  

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
