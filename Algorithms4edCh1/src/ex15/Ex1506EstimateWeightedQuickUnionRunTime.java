package ex15;

/*
  p235
  1.5.6  Repeat Exercise 1.5.5 for weighted quick-union.
  
  (1.5.5  Estimate the minimum amount of time (in days) that would be required for
  quick-find to solve a dynamic connectivity problem with 10**9 sites and 10**6 input pairs,
  on a computer capable of executing 10**9 instructions per second. Assume that each itera-
  tion of the inner for loop requires 10 machine instructions.)
  
  Assuming a program of the form solves the problem and it must run to completion;
  
  public static run2Completion(DataSource ds) {
    QuickFindUF uf = new QuickFindUF(1000000000);
    int p; int q;
    // read all data and fully initialize uf with it
    for (int i = 0; i < 1000000; ++) {
      p = readData2Int(); // is this io bound? what's the delay?
      q = readData2Int(); // ditto
      // leaving out connected since it's built into union
      //if (uf.connected(p, q)) continue; // takes 2lgN time (runs find twice)
      uf.union(p, q); // takes 2lgN time (runs find twice)
      // Do you want the inner loop to run here? Why and for how many iterations?
    }
    // now do some  processing on uf
    while (what?) {
      runInnerLoopIteration(); // 10 machine instructions 
      // assume inner loop never breaks outer loop
    }
  }
  
  Each iteration of find does 2 array accesses and supposing each of those takes at least one
  machine instruction and adding 10 machine instructions for function call overhead including
  a call to validate(), then each execution of it takes (lg(1000000000)*2+10)/10**9 = 
  .0000006979470570797252 sec. Since union runs find twice and supposing the roots of p and q do 
  not match 70% of the time and if so it runs 10 extra machine instructions, then overall it takes
  (2*.0000006979470570797252*10**6 + .7*10**-3 sec which is approximately 1.3965941141594504 sec.
  If the inner loop runs 10**9 times then it takes a total of 10 sec and the grand total runtime 
  is 11.3965941141594504 sec ~= 0.008 days.

*/

public class Ex1506EstimateWeightedQuickUnionRunTime {
  
  public static void main(String[] args) {
    
  }

}
