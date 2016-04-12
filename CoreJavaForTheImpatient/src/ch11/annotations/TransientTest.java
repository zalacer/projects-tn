package ch11.annotations;

//import static ch04.inheritance.reflection.Ch0409UniversalToString.*;
//import static java.lang.System.identityHashCode;

public class TransientTest {
  
  private int a = 1;
  private int b = 2;
  private @Transient int c = 3; 
  public @Transient boolean d = true; 
  private @Transient double e = 5.7; 
  private @Transient float f = 19.9f;  
  private @Transient char g = 'q';
  private @Transient String h = "zzz";
  private Double i = new Double(2.3); 
  
  public TransientTest(){}

  public int getA() {
    return a;
  }

  public void setA(int a) {
    this.a = a;
  }

  public int getB() {
    return b;
  }

  public void setB(int b) {
    this.b = b;
  }

  public int getC() {
    return c;
  }

  public void setC(int c) {
    this.c = c;
  }

  public boolean getD() {
    return d;
  }

  public void setD(boolean d) {
    this.d = d;
  }

  public double getE() {
    return e;
  }

  public void setE(double e) {
    this.e = e;
  }

  public float getF() {
    return f;
  }

  public void setF(float f) {
    this.f = f;
  }

  public char getG() {
    return g;
  }

  public void setG(char g) {
    this.g = g;
  }

  public String getH() {
    return h;
  }

  public void setH(String h) {
    this.h = h;
  }

  public Double getI() {
    return i;
  }

  public void setI(Double i) {
    this.i = i;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + a;
    result = prime * result + b;
    result = prime * result + ((i == null) ? 0 : i.hashCode());
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
    TransientTest other = (TransientTest) obj;
    if (a != other.a)
      return false;
    if (b != other.b)
      return false;
    if (i == null) {
      if (other.i != null)
        return false;
    } else if (!i.equals(other.i))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("TransientTest(a=");
    builder.append(a);
    builder.append(", b=");
    builder.append(b);
    builder.append(", c=");
    builder.append(c);
    builder.append(", d=");
    builder.append(d);
    builder.append(", e=");
    builder.append(e);
    builder.append(", f=");
    builder.append(f);
    builder.append(", g=");
    builder.append(g);
    builder.append(", h=");
    builder.append(h);
    builder.append(", i=");
    builder.append(i);
    builder.append(")");
    return builder.toString();
  }


  
  
 

 
}
