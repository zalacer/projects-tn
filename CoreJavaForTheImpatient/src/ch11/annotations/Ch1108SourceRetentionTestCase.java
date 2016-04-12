package ch11.annotations;

//8. Implement the @TestCase annotation, generating a source file whose name is the
//name of the class in which the annotation occurs, followed by Test. For example,
//if MyMath.java contains
//  @TestCase(params=“4”, expected=“24”)
//  @TestCase(params=“0”, expected=“1”)
//  public static long factorial(int n) { … }
//then generate a file MyMathTest.java with statements
//  assert(MyMath.factorial(4) == 24);
//  assert(MyMath.factorial(0) == 1);
//You may assume that the test methods are static, and that params contains a
//comma-separated list of parameters of the correct type.

// Solution annotation definitions, annotations processor and its output is
// in package ch1108.annotations.testcase.

public class Ch1108SourceRetentionTestCase {

  public static void main(String[] args) {
    
    // the following prints no output if all the tests work
    // (only failures are printed)
    ch1108.annotations.testcase.MyMathTest.main(args);
    ch1108.annotations.testcase.FactorialTest.main(args);
    
    
  }

}
