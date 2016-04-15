package testclasses;

import static utils.UniversalToString.*;

public class Cir5c {
  public Cir5a a = null;
  public Cir5b b = null;
  public Cir5c c = null;
  public Cir5d d = null;
  public Cir5e e = null;
  public Cir5c(){}
  public Cir5c(Cir5a a, Cir5b b, Cir5c c, Cir5d d, Cir5e e) {
    this.a=a; this.b=b; this.c=c; this.d=d.d; this.e=e;
  }
  
  @Override
  public String toString() {
    return universalToString(this);
  }
}
