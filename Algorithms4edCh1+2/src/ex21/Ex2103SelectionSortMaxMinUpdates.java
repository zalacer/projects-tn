package ex21;

/* p264
  2.1.3  Give an example of an array of N items that maximizes the number of 
  times the test a[j] < a[min] fails (and, therefore,  min gets updated) 
  during the operation of selection sort (Algorithm 2.1).
  
  This happens when the array is in reverse sorted order, for example the number 
  of min updates when sorting Integer[7,6,5,4,3,2,1] is 12. For such arrays, if N 
  is even, the formula for the number of min updates is (N/2)** and if it's odd 
  the formula is (N/2)*(N/2+1). Either way its ~ (N/2)**.
   
*/

public class Ex2103SelectionSortMaxMinUpdates {

}
