package ex24;

import static analysis.Log.log;

/* p331
  2.4.23 Multiway heaps. Considering the cost of compares only, and assuming
  that it takes t compares to find the largest of t items, find the value of 
  t that minimizes the coefficient of NlgN in the compare count when a t-ary 
  heap is used in heapsort. First, assume a straightforward generalization of
  sink; then assume that Floyd’s method can save one compare in the inner loop.
  
  In other words, find int t>1 that minimizes t*log(N,t) for N>0, where log(N,t)
  is log of N base t.
  
  Using calculus:
  y = x*log(N,x) = ln(N)*x/ln(x);
  dy/dx = ln(N)(ln(x) - 1)/ln(x)^2 
      (using (f/g)' = (f*1/g)' = f'(1/g) + f(-1/g^2) = (f'g - fg')/g^2 )
  at min/max dy/dx = 0 => ln(x) = 1 => x = e = 2.718 ≅ 3
  this is a min - see graph of x/log(x) at https://www.google.com/webhp?gws_rd=ssl#q=x%2Flog%28x%29.
  this approach doesn't work with Floyd's method, i.e. 
  y = (x-1)*log(N,x) = ln(N)*(x-1)/ln(x)
  dy/dx = ln(N)(ln(x)-(x-1)/x)/ln(x)^2  = 0 => xln(x) = x-1 => x=1 but require x>1
  see http://m.wolframalpha.com/input/?i=solve+x+log%28x%29+%3D+x-1&x=6&y=8
      
  Experimentally using findMin():
    Without Floyd's method, when N=1, t=2 and t=3 both give coefficient of zero
    and when N>1, t=3 gives the min coefficient.
  
  Experimentally using findMinWithFloyd():
    With Floyd's method, for N>0, t=2 gives the min coefficient.

  
*/
public class Ex2423MultiwayHeapThatMinimizesCompares {
  
  public static int findMin(int n, int b) {
    //  find t>1 that minimizes tlog(n,t) for n > 0
    double min = Double.POSITIVE_INFINITY, v = 0; int t = 0;
    for (int i = 2; i <= b; i++) {
      v = i*log(n,i); if (v < min) { min = v; t = i; }
    }
    return t;
  }
  
  public static int findMinWithFloyd(int n, int b) {
    //  find t>1 that minimizes tlog(n,t) for n > 0 with Floyd's method
    double min = Double.POSITIVE_INFINITY, v = 0; int t = 0;
    for (int i = 2; i <= b; i++) {
      v = (i-1)*log(n,i); if (v < min) { min = v; t = i; }
    }
    return t;
  }
 
  public static void main(String[] args) {

    for (int i = 1; i < 1000000; i++) {
      int t = findMin(i,100);
      if (t != 3) System.out.println("i="+i+" t="+t);
    }
    // i=1 t=2
    
    System.out.println(2*log(1,2)); //0
    System.out.println(3*log(1,3)); //0

    for (int i = 1; i < 1000000; i++) {
      int t = findMinWithFloyd(i,100);
      if (t != 2) System.out.println("i="+i+" t="+t);
    }
    
  }

}
