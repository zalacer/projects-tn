package ch11.annotations;

import static ch04.inheritance.reflection.Ch0409UniversalToString.*;

public class Cir9f {
  public Cir9g g = null;
  public Cir9f(){}
  
  @Override
  public String toString() {
    return universalToString(this);
  }
  

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}