package ex14;

//  p211
//  1.4.23 Binary search for a fraction. Devise a method that uses a logarithmic number of
//  queries of the form Is the number less than x? to find a rational number p/q such that 0
//  < p < q < N. Hint : Two fractions with denominators less than N cannot differ by more
//  than 1/N**2.

// If N is 5, 1/4 and 3/4 satisfy the constraints but 3/4 - 1/4 = 1/2 > 1/25. Perhaps the
// condition meant is that for fractions satisfying the constraints the difference between 
// any two cannot be <= 1/N**2. For example, (N-2/N-1) - (N-3/N-1) = 1/N-1 > 1/N**2 for N > 1 
// since N-1 < N < N**2 and the constraints imply N > 1 since if not there are no integers
// p or q > 0 and < N.

// As a way to approach this I've implemented fuzzy binary search for an array of doubles 
// within a certain tolerance. In other words, return the index of an element e in the array
// if abs(e-key) <= t when searching for key or return -1 if no element satisfies this. 
// Binary search for a rational in an array of rationals represented by doubles can be done 
// using this method as shown in the first example in main.

public class Ex1423FuzzyBinarySearch {
  
  public static int fuzzyIndexOf(double[] z, double key, double t) {
    // return the index of any element of z within t of key or -1 if no such element
    // is found given that z is sorted in ascending order. t may be specified safely 
    // with 10**-10 precision (on my platform but I can't guarantee that on all, however
    // it could be better on some). t is made positive if given negative and accomodates
    // positive and negative variations around key. this algorithm is the same as regular 
    // binary search except when mid has been selected for output it's filtered for 
    // inclusion in [z[mid] - abs(t), z[mid] + abs(t)] or -1 is returned.
    if (z == null || z.length == 0) return -1;
    if (t < 0) t = -t;
    double fudge = 0.00000000001; //fudge factor for double arithmetic errors
    int lo = 0;
    int hi = z.length - 1;
    while (lo <= hi) { 
      int mid = lo + (hi - lo) / 2;
      if (key < z[mid]-t) {
        hi = mid - 1;
      } else if (key > z[mid]+t) {
        lo = mid + 1;
      } else {
        double dif = key - z[mid];
        if (dif < 0) dif = -dif;
        return dif <= t+fudge ? mid : -1;
      }
    }
    return -1;
  }

  public static int rank(double[] a, double key) { 
    // standard binary search.
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) { 
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if (key < a[mid]) {
        hi = mid - 1;
      } else if (key > a[mid]) {
        lo = mid + 1;
      } else {
        return mid;
      }
    }
    return -1;
  }
           
  public static void main(String[] args) {
    
    int n = 7;
    double t = 1./(n*n); 
    System.out.println("t="+t); ////t=0.02040816326530612
    double[] z = {3./5, 2./3, 5./6, 3./4, 4./5,};
    System.out.println(fuzzyIndexOf(z, 100./121, t)); //2
    
    z = new double[]{1,2,2.3,3,3.3,4,4,5,6,7};
    System.out.println("\nrank");
    System.out.println(rank(z, 2.3)); //2
    System.out.println("\nfuzzy");
    System.out.println(fuzzyIndexOf(z, 2.2, .1)); //2
    System.out.println(fuzzyIndexOf(z, 2.1, .1)); //-1
    System.out.println(fuzzyIndexOf(z, 2.4, .1)); //2 fixed floating point error:
                                                  //2.4 - 2.3 =  0.10000000000000009
                                                  //with fudge = 0.00000000001
    z = new double[]{1,2,2.3,3,3.3,4,4,5,6.5,7};
    System.out.println(fuzzyIndexOf(z, 6.5, .002)); //8
    System.out.println(fuzzyIndexOf(z, 6.4, .1)); //8
    System.out.println(fuzzyIndexOf(z, 6.499, .001)); //8
    System.out.println(fuzzyIndexOf(z, 6.498, .001)); //-1
    System.out.println(fuzzyIndexOf(z, 6.498, .002)); //8
    System.out.println(fuzzyIndexOf(z, 6.49999999999, .0000000001)); //8
    System.out.println(fuzzyIndexOf(z, 6.49999999999, .00000000001)); //8
    System.out.println(6.49999999999 - .00000000001); //6.49999999998
    System.out.println(fuzzyIndexOf(z, 6.49999, .00001)); //8
    System.out.println(fuzzyIndexOf(z, 6.49999, .000001)); //-1
    System.out.println(fuzzyIndexOf(z, 6.499999, .000001)); //8
    System.out.println(fuzzyIndexOf(z, 6.499999, .0000001)); //-1
    System.out.println(fuzzyIndexOf(z, 6.4999999, .0000001)); //8
    System.out.println(fuzzyIndexOf(z, 6.4999999, .00000001)); //-1
    System.out.println(fuzzyIndexOf(z, 6.49999999, .00000001)); //8
    System.out.println(fuzzyIndexOf(z, 6.49999999, .000000001)); //-1
    System.out.println(fuzzyIndexOf(z, 6.499999999, .000000001)); //8
    System.out.println(fuzzyIndexOf(z, 6.499999999, .0000000001)); //-1
    System.out.println(fuzzyIndexOf(z, 6.4999999999, .0000000001)); //8
    System.out.println(fuzzyIndexOf(z, 6.4999999999, .00000000001)); //-1
    System.out.println(fuzzyIndexOf(z, 6.49999999999, .00000000001)); //8
    System.out.println(fuzzyIndexOf(z, 6.49999999999, .000000000001)); //-1
    System.out.println(fuzzyIndexOf(z, 6.499999999999, .000000000001)); //8
    System.out.println(fuzzyIndexOf(z, 6.499999999999, .0000000000001)); //-1
    System.out.println(fuzzyIndexOf(z, 6.4999999999999, .0000000000001)); //8
    System.out.println(fuzzyIndexOf(z, 6.4999999999999, .00000000000001)); //-1
    System.out.println(fuzzyIndexOf(z, 6.49999999999999, .00000000000001));  //8
    System.out.println(fuzzyIndexOf(z, 6.49999999999999, .000000000000001)); //-1
    System.out.println(fuzzyIndexOf(z, 6.499999999999999, .000000000000001)); //8
    System.out.println(fuzzyIndexOf(z, 6.499999999999999, .0000000000000001)); //-1
    System.out.println(fuzzyIndexOf(z, 6.4999999999999999, .0000000000000001)); //8
    System.out.println(fuzzyIndexOf(z, 6.4999999999999999, .00000000000000001)); //8
  
  }

}
