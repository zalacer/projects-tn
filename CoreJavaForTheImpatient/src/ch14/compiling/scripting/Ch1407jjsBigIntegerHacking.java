package ch14.compiling.scripting;

//  7. Run jjs. Call
//    var b = new java.math.BigInteger(‘1234567890987654321’)
//  Then display b (simply by typing b and Enter). What do you get? What is the value
//  of b.mod(java.math.BigInteger.TEN)? Why is b displayed so strangely?
//  How can you display the actual value of b?

// The display problem is due to Nashorn interpreting BigInteger numbers as JavaScript 
// numbers can be fixed by using java.util.Objects.toString() to stringify the BigInteger 
// before printing it with JavaScript.
    
public class Ch1407jjsBigIntegerHacking {

  public static void main(String[] args) {
    
//  JavaScript transcript:
//  jjs> var b = new java.math.BigInteger('1234567890987654321')
//  jjs> b
//  1234567890987654400
//  jjs> b.mod(java.math.BigInteger.TEN)
//  1
//  jjs> function javaToString(obj) {return java.util.Objects.toString(obj);}
//  jjs> print(javaToString(new java.math.BigInteger('1234567890987654321')))
//  1234567890987654321
  
  
  }

}
