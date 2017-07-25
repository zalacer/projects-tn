package u;

//http://joelgrus.com/2016/05/23/fizz-buzz-in-tensorflow/
//interviewer: OK, so I need you to print the numbers from 1 to 100, 
//except that if the number is divisible by 3 print "fizz", 
//if it's divisible by 5 print "buzz", 
//and if it's divisible by 15 print "fizzbuzz".
// see also:
//http://codereview.stackexchange.com/questions/11489/fizzbuzz-implementation

public class FizzBuzz {
  
  public static void fizzBuzz() {
    for (int i = 1; i < 101; i++) 
      if (i%15==0) {
        System.out.println(i+"   fizzbuzz");
      } else if(i%5==0) {
        System.out.println(i+"   buzz");
      } else if(i%3 ==0) {
        System.out.println(i+"   fizz");
      } else System.out.println(i);
  }

  public static void main(String[] args) {
    
    fizzBuzz();

  }

}
