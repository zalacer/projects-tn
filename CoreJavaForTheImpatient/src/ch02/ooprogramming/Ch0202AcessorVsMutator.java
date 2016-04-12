package ch02.ooprogramming;

// 2. Consider the nextInt method of the Scanner class. Is it an accessor or mutator?
// Why? What about the nextInt method of the Random class?
// p73: We say that a method is a mutator if it changes the object on which it was invoked. 

// Scanner.nextInt() is a mutator because it changes the state of the Scanner to produce 
// a new value or a failure when nextInt() is called again.
// Random.nextInt() probably is also a mutator since if it uses a linear congruential generator the
// next nextInt() depends on the current one. However this depends on implementation and if it uses
// a true, external random source, nextInt() could be considered to be an accessor of that source
// which is supposed to be independent of program state.

public class Ch0202AcessorVsMutator {

  //    public static void main(String[] args) {
  //
  //    }

}
