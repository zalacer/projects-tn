package ch11.annotations;

import static ch04.inheritance.reflection.Ch0409UniversalToString.*;

public class Cir9i {
  public Cir9j j = null;
  public Cir9i(){}
  
  @Override
  public String toString() {
    return universalToString(this);
  }
  

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
