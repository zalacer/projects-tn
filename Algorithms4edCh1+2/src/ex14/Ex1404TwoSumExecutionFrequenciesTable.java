package ex14;

//  p208
//  1.4.4  Develop a table like the one on page 181 for TwoSum

//    public class TwoSumFast {
//                                                      
//        public static int count(int[] a) {          frequency of execution  
//          -----------------------------------------
//          |-----------------                      |
//     A--->||Arrays.sort(a);|                      |<---1
//          |-----------------                      |
//          |-------------------                    |
//     B--->||int N = a.length;|                    |
//          ||int cnt = 0;     |                    |<---1
//          |-------------------                    |
//          |               ------------            |
//          |for (int i = 0;|i < N; i++|)           |  
//          |----------------          -------------|
//     C--->|| if (BinarySearch.rank(-a[i], a) > i)||<---N
//          || ----------------------------------- ||
//     D--->|| | cnt++;                          | ||<---x
//          || ----------------------------------- ||
//          |---------------------------------------|
//          |return cnt;                            |
//          -----------------------------------------
//        }
//    
//        public static void main(String[] args) {
//          int[] a = In.readInts(args[0]);
//          StdOut.println(count(a));
//        }
//    }

// statement  time(sec)  frequency             total time
//  block
// ------------------------------------------------------
//   D          t0       x (depends on input)  t0x
//   C          t1       N                     t1NlogN
//   B          t2       1                     t2   
//   A          t3       1                     t3~NlogN
//
// C is the inner loop
 
public class Ex1404TwoSumExecutionFrequenciesTable {

  public static void main(String[] args) {

  }

}
