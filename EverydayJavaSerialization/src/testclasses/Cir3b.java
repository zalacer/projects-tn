package testclasses;

import static utils.UniversalToString.*;

public class Cir3b {
  public Cir3a a = null;
  public Cir3b b = null;
  public Cir3c c = null;
  public Cir3b(){}
  
  @Override
  public String toString() {
    return universalToString(this);
  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}