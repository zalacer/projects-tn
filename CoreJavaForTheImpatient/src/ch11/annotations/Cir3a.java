package ch11.annotations;

import static ch04.inheritance.reflection.Ch0409UniversalToString.*;

public class Cir3a {
  public Cir3a a = null;
  public Cir3b b = null;
  public Cir3c c = null;
  public Cir3a(){}
  
  @Override
  public String toString() {
    return universalToString(this);
  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
