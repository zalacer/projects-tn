package ch03.interfaces.lambdas;

// 5. Implement a static constant method of the IntSequence class that yields an
// infinite constant sequence. For example, IntSequence.constant(1) yields
// values 1 1 1 … , ad infinitum. Extra credit if you do this with a lambda
// expression.

public class Ch0305IntSequenceConstant {

  public interface IntSequence {
    boolean hasNext();
    int next();
    static IntSequence constant(int i) {
      return new IntSequence() {
        public int next() {
          return i;
        }
        public boolean hasNext() {
          return true;
        } 
      };           
    }
  }

  public interface IntSequenceLambda {
    default boolean hasNext() {return true;}
    int next();
    static IntSequenceLambda constant(int i) {
      return () -> i;          
    }
  }

  public static void main(String[] args) {

    IntSequence κ = IntSequence.constant(1);
    for(int i = 0; i < 15; i++)
      System.out.print(κ.next()+", "); 
    // 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
    
    System.out.println();

    IntSequenceLambda λ = IntSequenceLambda.constant(5);
    for(int i = 0; i < 19; i++)
      System.out.print(λ.next()+", "); 
    // 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 

  }

}
