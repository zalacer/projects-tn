package ex34;

import static v.ArrayUtils.*;

import java.security.SecureRandom;

import st.SeparateChainingHashSTX;

/* p482
  3.4.18  Add a constructor to SeparateChainingHashST that gives the 
  client the ability to specify the average number of probes to be 
  tolerated for searches. Use array resizing to keep the average list 
  size less than the specified value, and use the technique described
  on page 478 to ensure that the modulus for hash() is prime.s.

  This is done in SeparateChainingHashSTX by adding a constructor takeing
  a string argument that if parseable as a double sets the tolerance field
  to that double which must be greater than 1, if only marginally, and less
  than or equal to Integer.MAX_VALUE. 

  The lowest tolerance I tested is 1.01, and any requested tolerance below 
  that is raised to it. As the tolerance approaches 1, it's increasingly 
  difficult to shrink M with a simple formula but I think that's ok since 
  the main point is to keep the average probes below the tolerance but not 
  by too much. For example if the tolerance is 1.01 and the avgProbes = 
  1.0095367431640625 when N = 10K and M = 1048576, then after removing 7.5K 
  keys avgProbes lowers by about .007 to  1.0023841857910156 and M remains 
  the same. At this point I can't say that 1.0023841857910156 is excessively 
  lower than 1.01 since it's only about 7.5/100 lower and I'd like it to be 
  1/4 - 1/2 lower to justify recreating a new batch of SequentialSearchSTs 
  and and rehashing all the remaining keys into them. I did some experimen-
  tation with resizing adjustment formulas and found it's difficult keep
  the number of probes within a tight range of the tolerance for a reaonsable
  range of possible tolerances for get(), put() and delete(). It's easier to
  keep the number of probes in line after a large number of keys have been
  added compared to right after initial table construction. 

  Additionally I added the primes array from p478 and added the missing first
  five elements to it, added the hash method from the same page after convert-
  ing it to a private Function<Key,Integer> and modified put() and delete() to
  determine if resize should run using the default or new formula depending 
  on if the tolerance field is 0 or not. 

 */             

public class Ex3418AddConstructor2SeparateChainingHashSTToControlAverageNumberOfProbes {

  public static void main(String[] args) {
    
    SecureRandom r = new SecureRandom();

    int N = 10000;
    Integer[] a = rangeInteger(0, N), b = rangeInteger(0, 3*N/4), 
        c = rangeInteger(20000,30001);
    
    shuffle(a, r);
    shuffle(b, r);
    shuffle(c, r);

    SeparateChainingHashSTX<Integer,Integer> h;

    System.out.println("tolerance of -1 means there is no control of tolerance.");
    
    System.out.println("actual probes is computed by adding probes from relevant "
        + "SequentialSearchSTXs.");

    System.out.println("\ntesting tolerance for search for initial population "
        + "right after construction:");
    System.out.println("=============================================================================");
    System.out.println("\nusing the default resizing protocol:"); 
    h = new SeparateChainingHashSTX<>();
    for (int i : a) h.put(i,i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nafter removing 3/4 of the keys:");
    h.zeroProbes();
    for (int i = 0; i < b.length; i++) h.delete(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+3.*h.probes()/(4.*a.length));
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing the default resizing protocol with capacity 2*N:"); 
    h = new SeparateChainingHashSTX<>(2*N);
    for (int i : a) h.put(i,i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nafter removing 3/4 of the keys:");
    h.zeroProbes();
    for (int i = 0; i < b.length; i++) h.delete(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+3.*h.probes()/(4.*a.length));
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing the default resizing protocol with capacity 4*N:"); 
    h = new SeparateChainingHashSTX<>(4*N);
    for (int i : a) h.put(i,i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nafter removing 3/4 of the keys:");
    h.zeroProbes();
    for (int i = 0; i < b.length; i++) h.delete(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+3.*h.probes()/(4.*a.length));
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == Integer.MAX_VALUE:"); 
    h = new SeparateChainingHashSTX<>(""+Integer.MAX_VALUE);
    for (int i : a) h.put(i,i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nafter removing 3/4 of the keys:");
    h.zeroProbes();
    for (int i = 0; i < b.length; i++) h.delete(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+3.*h.probes()/(4.*a.length));
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 10:"); 
    h = new SeparateChainingHashSTX<>("10");
    h.zeroProbes();
    for (int i : a) h.put(i,i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nafter removing 3/4 of the keys:");
    h.zeroProbes();
    for (int i = 0; i < b.length; i++) h.delete(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+3.*h.probes()/(4.*a.length));
    System.out.println("tolerance             = "+h.getTolerance());
    
    System.out.println("\nusing avg probes tolerance == 7:"); 
    h = new SeparateChainingHashSTX<>("7");
    h.zeroProbes();
    for (int i : a) h.put(i,i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nafter removing 3/4 of the keys:");
    h.zeroProbes();
    for (int i = 0; i < b.length; i++) h.delete(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+3.*h.probes()/(4.*a.length));
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 5:"); 
    h = new SeparateChainingHashSTX<>("5");
    for (int i : a) h.put(i,i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nafter removing 3/4 of the keys:");
    h.zeroProbes();
    for (int i = 0; i < b.length; i++) h.delete(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+3.*h.probes()/(4.*a.length));
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 3:"); 
    h = new SeparateChainingHashSTX<>("3");
    for (int i : a) h.put(i,i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nafter removing 3/4 of the keys:");
    h.zeroProbes();
    for (int i = 0; i < b.length; i++) h.delete(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+3.*h.probes()/(4.*a.length));
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 2:"); 
    h = new SeparateChainingHashSTX<>("2");
    for (int i : a) h.put(i,i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nafter removing 3/4 of the keys:");
    h.zeroProbes();
    for (int i = 0; i < b.length; i++) h.delete(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+3.*h.probes()/(4.*a.length));
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 1.5:"); 
    h = new SeparateChainingHashSTX<>("1.5");
    for (int i : a) h.put(i,i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nafter removing 3/4 of the keys:");
    h.zeroProbes();
    for (int i = 0; i < b.length; i++) h.delete(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+3.*h.probes()/(4.*a.length));
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 1.2:"); 
    h = new SeparateChainingHashSTX<>("1.2");
    for (int i : a) h.put(i,i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nafter removing 3/4 of the keys:");
    h.zeroProbes();
    for (int i = 0; i < b.length; i++) h.delete(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+3.*h.probes()/(4.*a.length));
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 1.1:"); 
    h = new SeparateChainingHashSTX<>("1.1");
    for (int i : a) h.put(i,i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nafter removing 3/4 of the keys:");
    h.zeroProbes();
    for (int i = 0; i < b.length; i++) h.delete(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+3.*h.probes()/(4.*a.length));
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 1.05:"); 
    h = new SeparateChainingHashSTX<>("1.05");
    for (int i : a) h.put(i,i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nafter removing 3/4 of the keys:");
    h.zeroProbes();
    for (int i = 0; i < b.length; i++) h.delete(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+3.*h.probes()/(4.*a.length));
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 1.01:"); 
    h = new SeparateChainingHashSTX<>("1.01");
    for (int i : a) h.put(i,i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nafter removing 3/4 of the keys:");
    h.zeroProbes();
    for (int i = 0; i < b.length; i++) h.delete(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+3.*h.probes()/(4.*a.length));
    System.out.println("tolerance = "+h.getTolerance());

    System.out.println("\ntesting tolerance for search hits:");
    System.out.println("==================================");
  //===========================================================================  
    System.out.println("\nusing the default resizing protocol:"); 
    h = new SeparateChainingHashSTX<>();
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : a) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing the default resizing protocol with capacity 2*N:"); 
    h = new SeparateChainingHashSTX<>(2*N);
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : a) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing the default resizing protocol with capacity 4*N:"); 
    h = new SeparateChainingHashSTX<>(4*N);
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : a) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == Integer.MAX_VALUE:"); 
    h = new SeparateChainingHashSTX<>(""+Integer.MAX_VALUE);
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : a) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 10:"); 
    h = new SeparateChainingHashSTX<>("10");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : a) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());
    
    System.out.println("\nusing avg probes tolerance == 7:"); 
    h = new SeparateChainingHashSTX<>("7");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : a) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 5:"); 
    h = new SeparateChainingHashSTX<>("5");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : a) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 3:"); 
    h = new SeparateChainingHashSTX<>("3");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : a) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 2:"); 
    h = new SeparateChainingHashSTX<>("2");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : a) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 1.5:"); 
    h = new SeparateChainingHashSTX<>("1.5");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : a) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 1.2:"); 
    h = new SeparateChainingHashSTX<>("1.2");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : a) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 1.1:"); 
    h = new SeparateChainingHashSTX<>("1.1");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : a) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 1.05:"); 
    h = new SeparateChainingHashSTX<>("1.05");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : a) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 1.01:"); 
    h = new SeparateChainingHashSTX<>("1.01");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : a) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/a.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\ntesting tolerance for search misses:");
    System.out.println("====================================");
  //===========================================================================  
    System.out.println("\nusing the default resizing protocol:"); 
    h = new SeparateChainingHashSTX<>();
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : c) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/c.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing the default resizing protocol with capacity 2*N:");
    
    h = new SeparateChainingHashSTX<>(2*N);
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : c) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/c.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing the default resizing protocol with capacity 4*N:"); 
    h = new SeparateChainingHashSTX<>(4*N);
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : c) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/c.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == Integer.MAX_VALUE:"); 
    h = new SeparateChainingHashSTX<>(""+Integer.MAX_VALUE);
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : c) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/c.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 10:"); 
    h = new SeparateChainingHashSTX<>("10");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : c) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/c.length);
    System.out.println("tolerance             = "+h.getTolerance());
    
    System.out.println("\nusing avg probes tolerance == 7:"); 
    h = new SeparateChainingHashSTX<>("7");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : c) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/c.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 5:"); 
    h = new SeparateChainingHashSTX<>("5");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : c) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/c.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 3:"); 
    h = new SeparateChainingHashSTX<>("3");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : c) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/c.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 2:"); 
    h = new SeparateChainingHashSTX<>("2");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : c) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/c.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 1.5:"); 
    h = new SeparateChainingHashSTX<>("1.5");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : c) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/c.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 1.2:"); 
    h = new SeparateChainingHashSTX<>("1.2");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : c) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/c.length);
    System.out.println("tolerance             = "+h.getTolerance());
    System.out.println("\nusing avg probes tolerance == 1.1:"); 
    h = new SeparateChainingHashSTX<>("1.1");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : c) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/c.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 1.05:"); 
    h = new SeparateChainingHashSTX<>("1.05");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : c) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/c.length);
    System.out.println("tolerance             = "+h.getTolerance());

    System.out.println("\nusing avg probes tolerance == 1.01:"); 
    h = new SeparateChainingHashSTX<>("1.01");
    for (int i : a) h.put(i,i);
    h.zeroProbes();
    for (int i : c) h.get(i);
    System.out.println("theoretical avgProbes = "+h.avgProbes());
    System.out.println("actual avgProbes      = "+1.*h.probes()/c.length);
    System.out.println("tolerance             = "+h.getTolerance());
/*
    tolerance of -1 means there is no control of tolerance.
    actual probes is computed by adding probes from relevant SequentialSearchSTXs.
    
    testing tolerance for search for initial population right after construction:
    =============================================================================
    
    using the default resizing protocol:
    theoretical avgProbes = 10.0
    actual avgProbes      = 13.909
    tolerance             = -1.0
    
    after removing 3/4 of the keys:
    theoretical avgProbes = 3.0
    actual avgProbes      = 4.31505
    tolerance             = -1.0
    
    using the default resizing protocol with capacity 2*N:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.0
    tolerance             = -1.0
    
    after removing 3/4 of the keys:
    theoretical avgProbes = 5.0
    actual avgProbes      = 3.3738
    tolerance             = -1.0
    
    using the default resizing protocol with capacity 4*N:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.0
    tolerance             = -1.0
    
    after removing 3/4 of the keys:
    theoretical avgProbes = 5.0
    actual avgProbes      = 3.37335
    tolerance             = -1.0
    
    using avg probes tolerance == Integer.MAX_VALUE:
    theoretical avgProbes = 2501.0
    actual avgProbes      = 2499.4642
    tolerance             = 2.147483647E9
    
    after removing 3/4 of the keys:
    theoretical avgProbes = 626.0
    actual avgProbes      = 869.78085
    tolerance             = 2.147483647E9
    
    using avg probes tolerance == 10:
    theoretical avgProbes = 9.0
    actual avgProbes      = 8.88
    tolerance             = 10.0
    
    after removing 3/4 of the keys:
    theoretical avgProbes = 3.0
    actual avgProbes      = 3.77565
    tolerance             = 10.0
    
    using avg probes tolerance == 7:
    theoretical avgProbes = 3.0
    actual avgProbes      = 4.1424
    tolerance             = 7.0
    
    after removing 3/4 of the keys:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.7373
    tolerance             = 7.0
    
    using avg probes tolerance == 5:
    theoretical avgProbes = 2.0
    actual avgProbes      = 3.2596
    tolerance             = 5.0
    
    after removing 3/4 of the keys:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.21965
    tolerance             = 5.0
    
    using avg probes tolerance == 3:
    theoretical avgProbes = 2.0
    actual avgProbes      = 1.957
    tolerance             = 3.0
    
    after removing 3/4 of the keys:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.2603
    tolerance             = 3.0
    
    using avg probes tolerance == 2:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.6384
    tolerance             = 2.0
    
    after removing 3/4 of the keys:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.125
    tolerance             = 2.0
    
    using avg probes tolerance == 1.5:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.6384
    tolerance             = 1.5
    
    after removing 3/4 of the keys:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.125
    tolerance             = 1.5
    
    using avg probes tolerance == 1.2:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.6384
    tolerance             = 1.2
    
    after removing 3/4 of the keys:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.125
    tolerance             = 1.2
    
    using avg probes tolerance == 1.1:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.6384
    tolerance             = 1.1
    
    after removing 3/4 of the keys:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.125
    tolerance             = 1.1
    
    using avg probes tolerance == 1.05:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.6384
    tolerance             = 1.05
    
    after removing 3/4 of the keys:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.125
    tolerance             = 1.05
    
    using avg probes tolerance == 1.01:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.6384
    tolerance             = 1.01
    
    after removing 3/4 of the keys:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.125
    tolerance = 1.01
    
    testing tolerance for search hits:
    ==================================
    
    using the default resizing protocol:
    theoretical avgProbes = 10.0
    actual avgProbes      = 5.392
    tolerance             = -1.0
    
    using the default resizing protocol with capacity 2*N:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.0
    tolerance             = -1.0
    
    using the default resizing protocol with capacity 4*N:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.0
    tolerance             = -1.0
    
    using avg probes tolerance == Integer.MAX_VALUE:
    theoretical avgProbes = 2501.0
    actual avgProbes      = 1250.7321
    tolerance             = 2.147483647E9
    
    using avg probes tolerance == 10:
    theoretical avgProbes = 9.0
    actual avgProbes      = 4.7196
    tolerance             = 10.0
    
    using avg probes tolerance == 7:
    theoretical avgProbes = 3.0
    actual avgProbes      = 1.8276
    tolerance             = 7.0
    
    using avg probes tolerance == 5:
    theoretical avgProbes = 2.0
    actual avgProbes      = 1.1181
    tolerance             = 5.0
    
    using avg probes tolerance == 3:
    theoretical avgProbes = 2.0
    actual avgProbes      = 1.1808
    tolerance             = 3.0
    
    using avg probes tolerance == 2:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.0
    tolerance             = 2.0
    
    using avg probes tolerance == 1.5:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.0
    tolerance             = 1.5
    
    using avg probes tolerance == 1.2:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.0
    tolerance             = 1.2
    
    using avg probes tolerance == 1.1:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.0
    tolerance             = 1.1
    
    using avg probes tolerance == 1.05:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.0
    tolerance             = 1.05
    
    using avg probes tolerance == 1.01:
    theoretical avgProbes = 1.0
    actual avgProbes      = 1.0
    tolerance             = 1.01
    
    testing tolerance for search misses:
    ====================================
    
    using the default resizing protocol:
    theoretical avgProbes = 10.0
    actual avgProbes      = 9.76002399760024
    tolerance             = -1.0
    
    using the default resizing protocol with capacity 2*N:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.9999000099990001
    tolerance             = -1.0
    
    using the default resizing protocol with capacity 4*N:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.0
    tolerance             = -1.0
    
    using avg probes tolerance == Integer.MAX_VALUE:
    theoretical avgProbes = 2501.0
    actual avgProbes      = 2500.4660533946603
    tolerance             = 2.147483647E9
    
    using avg probes tolerance == 10:
    theoretical avgProbes = 9.0
    actual avgProbes      = 8.417958204179582
    tolerance             = 10.0
    
    using avg probes tolerance == 7:
    theoretical avgProbes = 3.0
    actual avgProbes      = 2.609139086091391
    tolerance             = 7.0
    
    using avg probes tolerance == 5:
    theoretical avgProbes = 2.0
    actual avgProbes      = 1.118088191180882
    tolerance             = 5.0
    
    using avg probes tolerance == 3:
    theoretical avgProbes = 2.0
    actual avgProbes      = 1.1807819218078193
    tolerance             = 3.0
    
    using avg probes tolerance == 2:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.6383361663833617
    tolerance             = 2.0
    
    using avg probes tolerance == 1.5:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.6383361663833617
    tolerance             = 1.5
    
    using avg probes tolerance == 1.2:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.6383361663833617
    tolerance             = 1.2
    
    using avg probes tolerance == 1.1:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.6383361663833617
    tolerance             = 1.1
    
    using avg probes tolerance == 1.05:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.6383361663833617
    tolerance             = 1.05
    
    using avg probes tolerance == 1.01:
    theoretical avgProbes = 1.0
    actual avgProbes      = 0.6383361663833617
    tolerance             = 1.01

*/    
    
    
  }

}

