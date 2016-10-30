package ex24;

import static v.ArrayUtils.arrayToString;
import static v.ArrayUtils.rangeInteger;

import pq.Median;

/* p332
  2.4.30 Dynamic median-finding. Design a data type that supports insert in 
  logarithmic time, find the median in constant time, and delete the median 
  in logarithmic time. Hint: Use a min-heap and a max-heap.

  A two heap solution is implemented in pq.Median. Since it's generic and it
  may be used with non-numeric types it doesn't compute accurate medians for
  even numbers of data, since there no standard process for averaging the middle
  two keys, and instead shows the lesser of them. In such cases it may also
  happen that it would be impossible to delete "the" median since it may not
  exist in the data and could only be expressed with a different type.

  A demo is provided below where results are shown only when the data has an odd 
  number of keys for accuracy.

 */

public class Ex2430DynamicMedianFinding {

  public static void main(String[] args) {

    Median<Integer> m = new Median<>();
    Integer[] a = rangeInteger(1,20);
    System.out.println("testing find median for odd numbered datasets:");
    System.out.println("median  toSortedArray() after new Key added ");
    for (int i = 0; i < a.length; i++) {
      m.insert(a[i]);
      if (m.size() % 2 == 1)
        System.out.printf("%2s      %1s\n", ""+m.median(), 
            arrayToString(m.toSortedArray(),80,1,1));
    }
    System.out.println("\ntesting median deletion for odd numbered datasets:");
    System.out.println("median");
    System.out.println("deleted toSortedArray() after median deleted");
    Integer median; int c = 0;
    while (!m.isEmpty()) {
      if (++c%2==1) {
        median = m.delMedian();
        System.out.printf("%2s      %1s\n", ""+median, 
            arrayToString(m.toSortedArray(),80,1,1));
      }
      else m.delMedian();
    }

  }

}
