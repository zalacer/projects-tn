package sort;

import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.Random;

//http://www.iti.fh-flensburg.de/lang/algorithmen/sortieren/merge/natmerge.htm
//https://www.google.com/translate?hl=en&sl=de&tl=en&u=http%3A%2F%2Fwww.iti.fh-flensburg.de%2Flang%2Falgorithmen%2Fsortieren%2Fmerge%2Fnatmerge.htm&sandbox=1

public class NaturalMergeSorter {
  private int[] a;
  private int[] b;    // aux array
  private int n;

  public void sort(int[] a) {
    this.a=a;
    n=a.length;
    b=new int[n];
    naturalmergesort();
  }

  private boolean mergeruns(int[] a, int[] b) {
    int i=0, k=0, x;
    boolean asc=true;

    while (i<n) {
      k=i;
      do x=a[i++]; while (i<n && x<=a[i]);  // ascending part
      while (i<n && x>=a[i]) x=a[i++];      // descending part
      merge (a, b, k, i-1, asc);
      asc=!asc;
    }
    return k==0;
  }

  private void merge(int[] a, int[] b, int lo, int hi, boolean asc) {
    int k=asc ? lo: hi;
    int c=asc ? 1: -1;
    int i=lo, j=hi;

    // Each copy back next largest element,
    // To i and j cross // located
    // jeweils das nächstgrößte Element zurückkopieren,
    // bis i und j sich überkreuzen
    while (i<=j) {
      if (a[i]<=a[j])
        b[k]=a[i++];
      else
        b[k]=a[j--];
      k+=c;
    }
  }

  private void naturalmergesort() {   
    // From a to b and b merge alternately by a
    // abwechselnd von a nach b und von b nach a verschmelzen
    while (!mergeruns(a, b) & !mergeruns(b, a));
  }


  public static void main(String[] args) {
    
    NaturalMergeSorter nat = new NaturalMergeSorter();
    Random r = new Random(System.currentTimeMillis()); 
    int[] w, x; 
    w = range(0,11); // [0,1,2,3,4,5,6,7,8,9,10]
    x = w.clone();
    
    shuffle(x,r);
    pa(x,-1);
    nat.sort(x);
    pa(x,-1);
    assert Arrays.equals(x,w);
    
//    int[] a = {9, 1, 8, 2, 7, 3, 6, 4, 5};
//    nat.sort(a);
//    pa(a,-1);
  }

}

/*
  analysis from
  http://www.iti.fh-flensburg.de/lang/algorithmen/sortieren/merge/natmerge.htm
  https://www.google.com/translate?hl=en&sl=de&tl=en&u=http%3A%2F%2Fwww.iti.fh-flensburg.de%2Flang%2Falgorithmen%2Fsortieren%2Fmerge%2Fnatmerge.htm&sandbox=1

  The function mergeruns halved in each pass, the number of bitonic runs. Are beginning
  r natural cycles exist, so calls are thus required Θ (log r), until only one is left 
  running. Since mergeruns in time Θ (n) runs, Natural mergesort has a time complexity 
  of Θ (n log r). The worst case occurs when all naturally occurring bitonic runs have 
  the length of 2, for example, as in the sequence 1 0 1 0 1 0 1 0. Then are r = n / 2 
  runs available, and Natural mergesort requires as much time as Mergesort , namely 
  Θ(n log (n)). The best case occurs when the sequence consisting of only a constant 
  number bitonic runs. Then, time is required only Θ (n). This is especially the case 
  when the sequence is already sorted in ascending order, if it is sorted in descending 
  order, if it consists of two assorted cuts, or if a constant number of elements need to 
  be sorted in an ordered sequence. A disadvantage is that Natural mergesort mergesort as 
  an additional memory space of Θ (n) for the temporary array b.
*/
