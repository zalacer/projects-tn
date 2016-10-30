package ex14;

//  p209 (cost model intro re ThreeSum p182)
//  1.4.7  Analyze  ThreeSum under a cost model that counts arithmetic operations (and
//  comparisons) involving the input numbers.

//  ThreeSum count method (text p173)
//  public static int count(int[] a) { 
//    // Count triples that sum to 0.
//    int N = a.length;
//    int cnt = 0;
//    for (int i = 0; i < N; i++)        //1
//      for (int j = i+1; j < N; j++)    //~N
//        for (int k = j+1; k < N; k++)  //~(N**2)/2
//          if (a[i] + a[j] + a[k] == 0) //~(N**3)/6
//            cnt++;
//    return cnt;
//  }

//  Proposition Ex1407. For N input numbers the ThreeSum algorithm executes ~(N**3)/2 
//  arithmetic and comparison operations directly involving them.
//  Proof: For each of the ~(N**3)/6 triples, the algorithm does two additions to sum 
//  the three numbers and then compares that sum to zero.
  

public class Ex1407ThreeSumCostModel {

  public static void main(String[] args) {

  }

}
