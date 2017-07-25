package ex34;

import static v.ArrayUtils.*;

import java.security.SecureRandom;

import st.LinearProbingHashSTX;

/* p481
  3.4.16  Suppose that a linear-probing table of size 10^6 is half full, 
  with occupied positions chosen at random. Estimate the probability that 
  all positions with indices divisible by 100 are occupied. 
  
  The probability that any position is occupied is 1/2 and the probability
  that a position index is divisible by 100 is 1/100 so the probability
  that a position with index divisible by 100 is occupied = 1/200 and the
  probability that ((10^6)/100) such positions are occupied is 
   (1/200)^((10^6)/100) = (1/200)^10000 which is effectively 0.
  
  Experimentally, using 5*10^5 distinct keys about half of the positions with 
  indices divisible by 100 would be occupied so that the probabilty that all 
  such positions would be occupied is zero. The same is true using 5*10^5 
  random keys in the range [0,Integer.MAX_VALUE). The probability decreases 
  with fewer keys and when only keys not divisible by 100 are used and it 
  increases when only keys divisible by 100 are used and the number of keys 
  increases for a fixed table size. Verifying experiments are included below.
 
*/             

public class Ex3416ProbabilityOfCertainPositionsFilledInLinearProbingHashSTX {
  
  public static void main(String[] args) {
    
     int N = 500000;
     Integer[] a = rangeInteger(0,N); int numberFilled, c, next; 
     double frequency; // frequency of positions with index divisible by 100 occupied
     LinearProbingHashSTX<Integer, Integer> h;
     
     System.out.println("using non-random distinct keys:");
     h = new LinearProbingHashSTX<>(2*N);
     for (int i = 0; i < N; i++) h.put(a[i], a[i]);
     numberFilled = h.numberFilled((i)->{return i % 100 == 0;});
     frequency = (1.*numberFilled)/(2*N/100);
     System.out.printf("frequency = %7.5f\n",frequency);
     assert h.getM() == 1000000;
     
     System.out.println("\nusing random keys from [0,Integer.MAX_VALUE):");
     SecureRandom r = new SecureRandom();
     h = new LinearProbingHashSTX<>(2*N);
     for (int i = 0; i < N; i++) r.nextInt(Integer.MAX_VALUE); //prep
     for (int i = 0; i < N; i++) h.put(r.nextInt(Integer.MAX_VALUE), 1);
     numberFilled = h.numberFilled((i)->{return i % 100 == 0;});
     frequency = (1.*numberFilled)/(2*N/100);
     System.out.printf("frequency = %7.5f\n",frequency);
     assert h.getM() == 1000000;
     
     System.out.println("\nusing random keys from [0,1000):");
     h = new LinearProbingHashSTX<>(2*N);
     for (int i = 0; i < N; i++) r.nextInt(1000); //prep
     for (int i = 0; i < N; i++) h.put(r.nextInt(1000), 1);
     numberFilled = h.numberFilled((i)->{return i%100 == 0;});
     frequency = (1.*numberFilled)/(2*N/100);
     System.out.printf("frequency = %7.5f\n",frequency);
     assert h.getM() == 1000000;
     
     System.out.println("\nusing random keys from [0,100):");
     h = new LinearProbingHashSTX<>(2*N);
     for (int i = 0; i < N; i++) r.nextInt(100); //prep
     for (int i = 0; i < N; i++) h.put(r.nextInt(100), 1);
     numberFilled = h.numberFilled((i)->{return i%100 == 0;});
     frequency = (1.*numberFilled)/(2*N/100);
     System.out.printf("frequency = %7.5f\n",frequency);
     assert h.getM() == 1000000;
     
     System.out.println("\nusing random keys from [0,Integer.MAX_VALUE) "
         + "including only those not divisible by 100:");
     h = new LinearProbingHashSTX<>(2*N);
     for (int i = 0; i < N; i++) r.nextInt(Integer.MAX_VALUE); //prep
     c = 0;
     while (c < N) {
       next = r.nextInt(Integer.MAX_VALUE); 
       if (next % 100 == 0) continue;
       else h.put(next, 1);
       c++;
     }
     numberFilled = h.numberFilled((i)->{return i % 100 == 0;});
     frequency = (1.*numberFilled)/(2*N/100);
     System.out.printf("frequency = %7.5f\n",frequency);
     assert h.getM() == 1000000;
     
     System.out.println("\nusing random keys from [0,Integer.MAX_VALUE) "
         + "including only those divisible by 100:");
     h = new LinearProbingHashSTX<>(2*N);
     for (int i = 0; i < N; i++) r.nextInt(Integer.MAX_VALUE); //prep
     c = 0;
     while (c < N) {
       next = r.nextInt(Integer.MAX_VALUE); 
       if (next % 100 != 0) continue;
       else h.put(next, 1);
       c++;
     }
     numberFilled = h.numberFilled((i)->{return i % 100 == 0;});
     frequency = (1.*numberFilled)/(2*N/100);
     System.out.printf("frequency = %7.5f\n",frequency);
     assert h.getM() == 1000000;
     
     System.out.println("\nusing non-random keys divisible by 100:");
     h = new LinearProbingHashSTX<>(2*N);
     c = 0;
     while (c < 2*N) {
       h.put(c, 1);
       c += 100;
     }
     numberFilled = h.numberFilled((i)->{return i % 100 == 0;});
     frequency = (1.*numberFilled)/(2*N/100);
     System.out.printf("frequency = %7.5f\n",frequency);
     assert h.getM() == 1000000;
  }
/*
    using non-random distinct keys:
    frequency = 0.50000
    
    using random keys from [0,Integer.MAX_VALUE):
    frequency = 0.50390
    
    using random keys from [0,1000):
    frequency = 0.00100
    
    using random keys from [0,100):
    frequency = 0.00010
    
    using random keys from [0,Integer.MAX_VALUE) including only those not divisible by 100:
    frequency = 0.18570
    
    using random keys from [0,Integer.MAX_VALUE) including only those divisible by 100:
    frequency = 1.00000
    
    using non-random keys divisible by 100:
    frequency = 1.00000

*/
}

