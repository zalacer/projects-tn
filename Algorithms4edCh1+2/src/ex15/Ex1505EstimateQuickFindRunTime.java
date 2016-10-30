package ex15;

/*
  p235
  1.5.5  Estimate the minimum amount of time (in days) that would be required for
  quick-find to solve a dynamic connectivity problem with 10**9 sites and 10**6 input pairs,
  on a computer capable of executing 10**9 instructions per second. Assume that each itera-
  tion of the inner for loop requires 10 machine instructions.
  
  Assuming a program of the form solves the problem and it must run to completion;
  
  public static run2Completion(DataSource ds) {
    QuickFindUF uf = new QuickFindUF(1000000000);
    int p; int q;
    // read all data and fully initialize uf with it
    for (int i = 0; i < 1000000; ++) {
      p = readData2Int(); // is this io bound? what's the delay?
      q = readData2Int(); // ditto
      // leaving out connected since it's built into union
      // if (uf.connected(p, q)) continue; //constant time c
      uf.union(p, q); // linear in N
      // Do you want the inner loop to run here? Why and for how many iterations?
    }
    // now do some  processing on uf
    while (what?) {
      runInnerLoopIteration(); // 10 machine instructions 
      // assume inner loop never breaks outer loop
    }
  }
  
  Overall the significant factor is the time taken running union assuming it will be called for 
  most pairs. Each execution of it does 2 array accesses if p and q are already in the same 
  component and 2 + N==10**9 array accesses if not. Assuming each array access takes at least one 
  machine instruction and 70% of the time p and q are not already in the same component, then 
  overall it takes (.7*(2+10**9) + .3*2)*10**6/10**9 sec ~= 7*10**5 sec ~= 486 days. If p and q
  are in the same component just 10% of the time, it takes ~= 10**5 sec ~= 69 days, and if they 
  are in the same component only 1% of the time, it still takes ~= 7 days.
  
  (PS: if the inner loop runs 10**9 times that adds only 10 sec to the runtime which is neglible.)
  
  
   
   
//   Now considering the time taken for connected it 
//  sums to ~ Sigma(lg1+lg2+lg3+...lg(10**) = lg(10**6!) ~ (10**6)*lg(10**6) = 19931569. Supposing
//  each execution of connected takes 10 machine instructions then that comes to 19931568.569324173
//  sec = 
//  Let me know what's the inner loop to figure it in.
                              
*/

public class Ex1505EstimateQuickFindRunTime {
  
  public static void main(String[] args) {
    
  }

}
