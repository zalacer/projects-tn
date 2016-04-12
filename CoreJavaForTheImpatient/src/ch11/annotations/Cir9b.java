package ch11.annotations;

import static ch04.inheritance.reflection.Ch0409UniversalToString.*;

public class Cir9b {
  public Cir9c c = null;
  public Cir9b(){}
  
  @Override
  public String toString() {
    return universalToString(this);
  }
  

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
