package ch11.annotations;

import static ch04.inheritance.reflection.Ch0409UniversalToString.*;

public class Cir9g {
  public Cir9h h = null;
  public Cir9g(){}
  
  @Override
  public String toString() {
    return universalToString(this);
  }
  

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
