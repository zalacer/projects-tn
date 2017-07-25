package ex14;

//  p209
//  1.4.9  Give a formula to predict the running time of a program for a problem of size N
//  when doubling experiments have shown that the doubling factor is 2**b and the running
//  time for problems of size N0 (N-subscript-0) is T.

// RT ==  running time as a function of N; lg = log base 2; ** is exponentiation
// given RT(2N)/RT(N) == 2**b (for sufficiently large N)
// (lg(RT(2N)) - lg(RT(N)))/(lg(2N) - lg(N)) == b; // b is the slope
// in equation of a line let y be lg(running time) and x be lg(problem size)
// given point (x,y) == (lg(N0), lg(T))
// y = lg(T) + b(x - lg(N0))
// then for x == lg(N) and where TN is the running time for N:
// lg(TN) = lg(T) + b(lg(N) - lg(N0)) = lg(T*(N/N0)**b)
// TN = T*(N/N0)**b

public class Ex1409PredictRunningTime {

  public static void main(String[] args) {

  }

}
