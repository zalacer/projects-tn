package ex14;

import static java.util.Arrays.copyOf;
import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import analysis.Timer;

//  p209
//  1.4.12  Write a program that, given two sorted arrays of N  int values, prints all ele-
//  ments that appear in both arrays, in sorted order. The running time of your program
//  should be proportional to N in the worst case.

public class Ex1412SortedListOfElementsIn2Arrays {

  public static void printMultisetIntersectionSameLengths(int[] a, int[] b) {
    // print the multiset intersection of a and b if possible
    // assumes a and b are sorted in increasing order
    // O(a.length) worst case running time where a.length == b.length
    if (a == null || a.length == 0 || b == null || b.length == 0) 
      System.out.println("multiset intersection is empty");
    if (a.length != b.length) throw new IllegalArgumentException(
        "printMultisetIntersection: the input arrays must have the same length");
    int i = 0; //counter for a
    int j = 0; //counter for b
    boolean someout = false;
    LOOP:
      while( i < a.length-1 && j < a.length-1) {
        if (a[i] == b[j]) {
          System.out.print(a[i]+" ");
          someout = true;
          i++; j++;
          continue;
        } else if (a[i] < b[j]) {
          while(i < a.length-1) {
            if (a[++i] == b[j]) {
              System.out.print(a[i]+" ");
              someout = true;
              i++; j++;
              continue LOOP;
            } else if (a[i] > b[j]) {
              j++;
              continue LOOP;
            }
          }
        } else if (b[j] < a[i]) {
          while(j < a.length-1) {
            if (b[++j] == a[i]) {
              System.out.print(b[j]+" ");
              someout = true;
              i++; j++;
              continue LOOP;
            } else if (b[j] > a[i]) {
              i++;
              continue LOOP;
            }
          }
        }
      }
    if (i == a.length-1) {
      for (; j < b.length; j++)
        if (b[j] == a[i]) {
          System.out.print(a[i]+" ");
          break;
        }
    } else if (j == b.length-1) {
      for (; i < a.length; i++)
        if (a[i] == b[j]) {
          System.out.print(b[j]+" ");
          break;
        }
    }
    if (someout) {
      System.out.println();
    } else {
      System.out.println("multiset intersection is empty");
    }
  }

  // multisetIntersect uses the same algorithm as printMultisetIntersectionSameLengths 
  // with the addition of sorting the arrays, which may be of different lengths, and
  // it returns an array instead of printing the output.
  public static int[] multisetIntersect(int[] a, int[] b) {
    // return an array containing the elements of the multiset intersection
    // of a and b, which are not assumed to be sorted.
    // O(N log N) worst case running time due to sorting the arrays
    if (a == null || a.length == 0 || b == null || b.length == 0) return new int[0];
    Arrays.sort(a);
    Arrays.sort(b);
    int i = 0; //counter for a
    int j = 0; //counter for b
    int[] c = a.length <= b.length ? new int[a.length] : new int[b.length];
    int k = 0; //counter for c
    //    if (b.length > a.length) {int[] t = a; a = b; b = t;}
    LOOP:
      while(i < a.length -1 && j < b.length-1) {
        if (a[i] == b[j]) {
          c[k++] = a[i];
          i++; j++;
          continue;
        } else if (a[i] < b[j]) {
          while(i < a.length-1) {
            if (a[++i] == b[j]) {
              c[k++] = a[i];
              i++; j++;
              continue LOOP;
            } else if (a[i] > b[j]) {
              j++;
              continue LOOP;
            }
          }
        } else if (b[j] < a[i]) {
          while(j < b.length-1) {
            if (b[++j] == a[i]) {
              c[k++] = b[j];
              i++; j++;
              continue LOOP;
            } else if (b[j] > a[i]) {
              i++;
              continue LOOP;
            }
          }
        }
      }
    if (i == a.length-1) {
      for (; j < b.length; j++)
        if (b[j] == a[i]) {
          c[k++] = a[i];
          break;
        }
    } else if (j == b.length-1) {
      for (; i < a.length; i++)
        if (a[i] == b[j]) {
          c[k++] = b[j];
          break;
        }
    }
    return take(c, k);
  }
  
  public static int[] intersectMultisetMapped(int[] a, int[] b) {
    // return an int[] containing the elements of the multiset
    // intersection of a and b. that means if a given value
    // occurs n times in b then just first n occurrences of it 
    // in a will be in the multiset intersection.
    if (a == null || b == null || a.length == 0 || b.length == 0)
      return new int[] {};
    Map<Integer, Integer> mapa = new HashMap<>();
    for (int i = 0; i < a.length; i++)
      mapa.merge(a[i], 1, Integer::sum);
    Map<Integer, Integer> mapb = new HashMap<>();
    for (int i = 0; i < b.length; i++)
      mapb.merge(b[i], 1, Integer::sum);
    int len = a.length >= b.length ? a.length : b.length;
    int[] c = new int[len];
    int cindex = 0;
    Integer ia;
    Integer ib;
    int count;
    for (Integer e : mapa.keySet()) {
      if (mapb.containsKey(e)) {
        ia = mapa.get(e);
        ib = mapb.get(e);
        count = ia <= ib ? ia : ib;
        for (int i = 0; i < count; i++)
          c[cindex++] = e;
      }
    }
    int[] d = take(c, cindex);
    Arrays.sort(d);
    return d;
  }

  // intersectMultiset is a generic version of multisetIntersect. the main issue
  // introduced is handling of nulls which are chosen to be ignored, that is nulls
  // are not candidates for inclusion in the multiset intersection of two arrays.
  public static <T extends Comparable<? super T>> T[] intersectMultiset(T[] a, T[] b) {
    // return an array containing the elements of the multiset intersection
    // of a and b, exluding nulls. a and b are not assumed to be sorted.
    // O(N log N) worst case running time due to sorting the arrays
    if (a == null || b == null) throw new IllegalArgumentException("intersectMultiset: "
        + "neither array argument can be null");
    if (a.length == 0 || b.length == 0) return copyOf(a, 0);
    // to allow inclusion of nulls it's necessary to use a null friendly Comparator
    // i.e. one that doesn't throw a NullPointerException from Comparator.compare
    Arrays.sort(a, Comparator.nullsLast((x,y) -> x.compareTo(y)));
    Arrays.sort(b, Comparator.nullsLast((x,y) -> x.compareTo(y)));
    int i = 0; //counter for a
    int j = 0; //counter for b
    T[] c = a.length <= b.length ? ofDim(a.getClass().getComponentType(), a.length) 
        : ofDim(b.getClass().getComponentType(), b.length);
    //    if (a.length > b.length) {T[] t = a; a = b; b = t;}
    int k = 0; //counter for c
    LOOP:
      while(i < a.length-1 && j < b.length-1) {
        //      if (a[i] == null || b[j] == null) break;
        // skip nulls in both arrays until a[i] != null && b[j] != null
        if (a[i] == null && b[j] == null) {
          i++; j++; continue;
        } else if (a[i] == null) {
          i++; continue;
        } else if (b[j] == null ) {
          j++; continue;
        }
        // compare non null elements 
        if (a[i].compareTo(b[j]) == 0) {
          c[k++] = a[i];
          i++; j++;
          continue;
        } else if (a[i].compareTo(b[j]) < 0) {
          while(i < a.length-1) {
            if (a[++i] == null) continue LOOP;
            if (a[i].compareTo(b[j]) == 0) {
              c[k++] = a[i];
              i++; j++;
              continue LOOP;
            } else if (a[i].compareTo(b[j]) > 0) {
              j++;
              continue LOOP;
            }
          }
        } else if (b[j].compareTo(a[i]) < 0) {
          while(j < b.length-1) {
            if (b[++j] == null) continue LOOP;
            if (b[j].compareTo(a[i]) == 0) {
              c[k++] = b[j];
              i++; j++;
              continue LOOP;
            } else if (b[j].compareTo(a[i]) > 0) {
              i++;
              continue LOOP;
            }
          }
        }
      }
    // finish up last element comparison
    if (i == a.length-1 && a[i] != null) {
      for (; j < b.length; j++) {
        if (b[j] == null) continue;
        if (b[j] == a[i]) {
          c[k++] = a[i];
          break;
        }
      }
    } else if (j == b.length-1 && b[j] != null) {
      for (; i < a.length; i++) {
        if (a[i] == null) continue;
        if (a[i] == b[j]) {
          c[k++] = b[j];
          break;
        }
      }
    }
    return take(c, k);
  }

  public static int[] intersect(int[] a, int[] b) {
    // return an array containing the elements of the intersection
    // of a and b, which are not assumed to be sorted.
    // O(N log N) worst case running time due to sorting the arrays
    if (a == null || a.length == 0 || b == null || b.length == 0) return new int[0];
    Arrays.sort(a);
    Arrays.sort(b);
    int i = 0; //counter for a
    int j = 0; //counter for b
    int[] c = a.length <= b.length ? new int[a.length] : new int[b.length];
    int k = 0; //counter for c
    LOOP:
      while( i < a.length-1 && j < b.length-1) {
        //skip duplicates in a
        while(i < a.length-1) {
          if (a[i] == a[i+1]) {
            i++;
          } else break;
        }
        //skip duplicates in b
        while(j < b.length-1) {
          if (b[j] == b[j+1]) {
            j++;
          } else break;
        }
        if (a[i] == b[j]) {
          c[k++] = a[i];
          i++; j++;
          continue;
        } else if (a[i] < b[j]) {
          while(i < a.length-1) {
            if (a[++i] == b[j]) {
              c[k++] = a[i];
              i++; j++;
              continue LOOP;
            } else if (a[i] > b[j]) {
              j++;
              continue LOOP;
            }
          }
        } else if (b[j] < a[i]) {
          while(j < b.length-1) {
            if (b[++j] == a[i]) {
              c[k++] = b[j];
              i++; j++;
              continue LOOP;
            } else if (b[j] > a[i]) {
              i++;
              continue LOOP;
            }
          }
        }
      }
    // process last element if dangling
    if (i == a.length-1) {
      for (; j < b.length; j++)
        if (b[j] == a[i]) {
          c[k++] = a[i];
          break;
        }
    } else if (j == b.length-1) {
      for (; i < a.length; i++)
        if (a[i] == b[j]) {
          c[k++] = b[j];
          break;
        }
    }
    return take(c, k);
  }
  
  // this is a generic version of intersect. the main issue introduced is handling
  // of nulls which are chosen to be ignored, that is nulls are not candidates for 
  // inclusion in the intersection of two arrays.
  public static <T extends Comparable<? super T>> T[] intersect(T[] a, T[] b) {
    // return an array containing the elements of the intersection
    // of a and b, exluding nulls. a and b are not assumed to be sorted.
    // O(N log N) worst case running time due to sorting the arrays
    if (a == null || b == null) throw new IllegalArgumentException("intersect: "
        + "neither array argument can be null");
    if (a.length == 0 || b.length == 0) return copyOf(a, 0);
    // to allow inclusion of nulls it's necessary to use a null friendly Comparator
    // i.e. one that doesn't throw a NullPointerException from Comparator.compare
    Arrays.sort(a, Comparator.nullsLast((x,y) -> x.compareTo(y)));
    Arrays.sort(b, Comparator.nullsLast((x,y) -> x.compareTo(y)));
    int i = 0; //counter for a
    int j = 0; //counter for b
    int k = 0; //counter for c, the output array
    T[] c = a.length <= b.length ? ofDim(a.getClass().getComponentType(), a.length) 
        : ofDim(b.getClass().getComponentType(), b.length);
    LOOP:
      while(i < a.length-1 && j < b.length-1) {
        // skip nulls and duplicates
        while(i < a.length-1) {
          if (a[i] == null) { i = a.length-1; break LOOP; }
          if (a[i+1] != null && a[i].equals(a[i+1])) i++;
          else break;
        }
        while(j < b.length-1) {
          if (b[j] == null) { j = b.length-1; break LOOP; }
          if (b[j+1] != null && b[j].equals(b[j+1])) j++;
          else break;
        }

        if (a[i].compareTo(b[j]) == 0) {
          c[k++] = a[i];
          i++; j++;
          continue;
        } else if (a[i].compareTo(b[j]) < 0) {
          while(i < a.length-1) {
            if (a[++i] == null) continue LOOP;
            if (a[i].compareTo(b[j]) == 0) {
              c[k++] = a[i];
              i++; j++;
              continue LOOP;
            } else if (a[i].compareTo(b[j]) > 0) {
              j++;
              continue LOOP;
            }
          }
        } else if (b[j].compareTo(a[i]) < 0) {
          while(j < b.length-1) {
            if (b[++j] == null) continue LOOP;
            if (b[j].compareTo(a[i]) == 0) {
              c[k++] = b[j];
              i++; j++;
              continue LOOP;
            } else if (b[j].compareTo(a[i]) > 0) {
              i++;
              continue LOOP;
            }
          }
        }
      }
    // last element comparison in case both arrays have the same distinct last element != null
    if (i == a.length-1 && a[i] != null) {
      for (; j < b.length; j++) {
        if (b[j] == null) continue;
        if (b[j] == a[i]) {
          c[k++] = a[i];
          break;
        }
      }
    } else if (j == b.length-1 && b[j] != null) {
      for (; i < a.length; i++) {
        if (a[i] == null) continue;
        if (a[i] == b[j]) {
          c[k++] = b[j];
          break;
        }
      }
    }
    return take(c, k);
  }

  public static void main(String[] args) throws InterruptedException {

//    int[] a = {1,2,2,3,7,7,8};
//    int[] b = {2,2,2,4,5,7,9};
//    //    printMultisetIntersectionSameLengths(a,b); // 2 2 7
//    //    printMultisetIntersectionSameLengths(b,a); // 2 2 7
//    //    System.out.println();
//
//    a = new int[]{2,2,2,3,3,7,7,12,12};
//    b = new int[]{1,2,2,7,8,9,12,12,12};
//    //    printMultisetIntersectionSameLengths(a,b); // 2 2 7 12 12 
//    //    printMultisetIntersectionSameLengths(b,a); // 2 2 7 12 12 
//    //    System.out.println();
//
//    pa(multisetIntersect(a,b)); //int[2,2,7,12,12]
//    pa(multisetIntersect(b,a)); //int[2,2,7,12,12]
//    pa(intersectMultiset((Integer[]) box(a), (Integer[]) box(b))); //Integer[2,2,7,12,12]
//    pa(intersectMultiset((Integer[]) box(b), (Integer[]) box(a))); //Integer[2,2,7,12,12]
//    pa(intersect(a,b)); //int[2,7,12]
//    pa(intersect(b,a)); //int[2,7,12]
//    pa(intersect((Integer[]) box(a), (Integer[]) box(b))); //Integer[2,7,12]
//    pa(intersect((Integer[]) box(b), (Integer[]) box(a))); //Integer[2,7,12]
//    System.out.println();
//
//    b = new int[]{0,2,2,2,4,5,6,7,9,10,11,12};
//    pa(multisetIntersect(a,b)); //int[2,2,2,7,12]
//    pa(multisetIntersect(b,a)); //int[2,2,2,7,12]
//    pa(intersectMultiset((Integer[]) box(a), (Integer[]) box(b))); //Integer[2,2,2,7,12]
//    pa(intersectMultiset((Integer[]) box(b), (Integer[]) box(a))); //Integer[2,2,2,7,12]
//    pa(intersect(a,b)); //int[2,7,12]
//    pa(intersect(b,a)); //int[2,7,12]
//    pa(intersect((Integer[]) box(a), (Integer[]) box(b))); //Integer[2,7,12]
//    pa(intersect((Integer[]) box(b), (Integer[]) box(a))); //Integer[2,7,12]
//    System.out.println();
//
//    a = new int[]{1,2,2,3,3,7,7,12,12};
//    b = new int[]{0,2,2,2,4,4,5,6,7,9,9,11,11,12,12,12};
//    pa(multisetIntersect(a,b)); //int[2,2,7,12,12]
//    pa(multisetIntersect(a,b)); //int[2,2,7,12,12]
//    pa(intersectMultiset((Integer[]) box(a), (Integer[]) box(b))); //Integer[2,2,7,12,12]
//    pa(intersectMultiset((Integer[]) box(b), (Integer[]) box(a))); //Integer[2,2,7,12,12]
//    pa(intersect(a,b)); //int[2,7,12]
//    pa(intersect(b,a)); //int[2,7,12]
//    pa(intersect((Integer[]) box(a), (Integer[]) box(b))); //Integer[2,7,12]
//    pa(intersect((Integer[]) box(b), (Integer[]) box(a))); //Integer[2,7,12]
//    System.out.println();
//
//    b = new int[]{0,2,2,2,4,4,12,12,12};
//    pa(multisetIntersect(a,b)); //int[2,2,12,12]
//    pa(multisetIntersect(a,b)); //int[2,2,12,12]
//    pa(intersectMultiset((Integer[]) box(a), (Integer[]) box(b))); //Integer[2,2,12,12]
//    pa(intersectMultiset((Integer[]) box(b), (Integer[]) box(a))); //Integer[2,2,12,12]
//    pa(intersect(a,b)); //int[2,12]
//    pa(intersect(b,a)); //int[2,12]
//    pa(intersect((Integer[]) box(a), (Integer[]) box(b))); //Integer[2,12]
//    pa(intersect((Integer[]) box(b), (Integer[]) box(a))); //Integer[2,12]
//    System.out.println();
//
//    a = new int[]{39,12,1,38,17,2,37,15,2,36,12,9,9,34,1,31,7,29,40,13,1,17};
//    b = new int[]{12,9,1,12,2,9,4,2,12,9,1};
//    pa(multisetIntersect(a,b)); //int[1,1,2,2,9,9,12,12]
//    pa(multisetIntersect(a,b)); //int[1,1,2,2,9,9,12,12]
//    pa(intersectMultiset((Integer[]) box(a), (Integer[]) box(b))); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersectMultiset((Integer[]) box(b), (Integer[]) box(a))); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersect(a,b)); //int[1,2,9,12]
//    pa(intersect(b,a)); //int[1,2,9,12]
//    pa(intersect((Integer[]) box(a), (Integer[]) box(b))); //Integer[1,2,9,12]
//    pa(intersect((Integer[]) box(b), (Integer[]) box(a))); //Integer[1,2,9,12]
//    System.out.println();
//
//    // additional intersect tests
//    a = new int[]{1,2,3,4,5};
//    b = new int[]{5,6,7,8,9};
//    pa(intersect(a,b)); //int[5]
//    pa(intersect(b,a)); //int[5]
//    pa(intersect((Integer[]) box(a), (Integer[]) box(b))); //Integer[5]
//    pa(intersect((Integer[]) box(b), (Integer[]) box(a))); //Integer[5]
//    System.out.println();
//
//    b = new int[]{5,5,6,7,8,9};
//    pa(intersect(a,b)); //int[5]
//    pa(intersect(b,a)); //int[5]
//    pa(intersect((Integer[]) box(a), (Integer[]) box(b))); //Integer[5]
//    pa(intersect((Integer[]) box(b), (Integer[]) box(a))); //Integer[5]
//    System.out.println();
//
//    a = new int[]{1,2,3,4,5,5};
//    b = new int[]{5,6,7,8,9};
//    pa(intersect(a,b)); //int[5]
//    pa(intersect(b,a)); //int[5]
//    pa(intersect((Integer[]) box(a), (Integer[]) box(b))); //Integer[5]
//    pa(intersect((Integer[]) box(b), (Integer[]) box(a))); //Integer[5]
//    System.out.println();
//
//    b = new int[]{5,6,7,8,9,4};
//    pa(intersect(a,b)); //int[4,5]
//    pa(intersect(b,a)); //int[4,5]
//    pa(intersect((Integer[]) box(a), (Integer[]) box(b))); //Integer[4,5]
//    pa(intersect((Integer[]) box(b), (Integer[]) box(a))); //Integer[4,5]
//    System.out.println();
//
//    a = new int[]{1,2,3,4,5};
//    b = new int[]{1,2,3,4,5};
//    pa(multisetIntersect(a,b)); //int[1,2,3,4,5]
//    pa(multisetIntersect(a,b)); //int[1,2,3,4,5]
//    pa(intersectMultiset((Integer[]) box(a), (Integer[]) box(b))); //Integer[1,2,3,4,5]
//    pa(intersectMultiset((Integer[]) box(b), (Integer[]) box(a))); //Integer[1,2,3,4,5]
//    pa(intersect(a,b)); //int[1,2,3,4,5]
//    pa(intersect(b,a)); //int[1,2,3,4,5]
//    pa(intersect((Integer[]) box(a), (Integer[]) box(b))); //Integer[1,2,3,4,5]
//    pa(intersect((Integer[]) box(b), (Integer[]) box(a))); //Integer[1,2,3,4,5]
//    System.out.println();
//
//    a = new int[]{1,1,2,2,3,3,4,4,5,5};
//    b = new int[]{1,1,2,2,3,3,4,4,5,5};
//    pa(multisetIntersect(a,b)); //int[1,1,2,2,3,3,4,4,5,5]
//    pa(multisetIntersect(a,b)); //int[1,1,2,2,3,3,4,4,5,5]
//    pa(intersectMultiset((Integer[]) box(a), (Integer[]) box(b))); //Integer[1,1,2,2,3,3,4,4,5,5]
//    pa(intersectMultiset((Integer[]) box(b), (Integer[]) box(a))); //Integer[1,1,2,2,3,3,4,4,5,5]
//    pa(intersect(a,b)); //int[1,2,3,4,5]
//    pa(intersect(b,a)); //int[1,2,3,4,5]
//    pa(intersect((Integer[]) box(a), (Integer[]) box(b))); //Integer[1,2,3,4,5]
//    pa(intersect((Integer[]) box(b), (Integer[]) box(a))); //Integer[1,2,3,4,5]
//    System.out.println();
//
//    a = new int[]{1,1,1,2,2,2,3,3,3,4,4,4,5,5,5};
//    b = new int[]{1,1,2,2,3,3,4,4,5,5};
//    pa(multisetIntersect(a,b)); //int[1,1,2,2,3,3,4,4,5,5]
//    pa(multisetIntersect(a,b)); //int[1,1,2,2,3,3,4,4,5,5]
//    pa(intersectMultiset((Integer[]) box(a), (Integer[]) box(b))); //Integer[1,1,2,2,3,3,4,4,5,5]
//    pa(intersectMultiset((Integer[]) box(b), (Integer[]) box(a))); //Integer[1,1,2,2,3,3,4,4,5,5]
//    pa(intersect(a,b)); //int[1,2,3,4,5]
//    pa(intersect(b,a)); //int[1,2,3,4,5]
//    pa(intersect((Integer[]) box(a), (Integer[]) box(b))); //Integer[1,2,3,4,5]
//    pa(intersect((Integer[]) box(b), (Integer[]) box(a))); //Integer[1,2,3,4,5]
//    System.out.println();
//
//    a = new int[]{1,1,2,2,3,3,4,4,5,5};
//    b = new int[]{1,1,1,2,2,2,3,3,3,4,4,4,5,5,5};
//    pa(multisetIntersect(a,b)); //int[1,1,2,2,3,3,4,4,5,5]
//    pa(multisetIntersect(a,b)); //int[1,1,2,2,3,3,4,4,5,5]
//    pa(intersectMultiset((Integer[]) box(a), (Integer[]) box(b))); //Integer[1,1,2,2,3,3,4,4,5,5]
//    pa(intersectMultiset((Integer[]) box(b), (Integer[]) box(a))); //Integer[1,1,2,2,3,3,4,4,5,5]
//    pa(intersect(a,b)); //int[1,2,3,4,5]
//    pa(intersect(b,a)); //int[1,2,3,4,5]
//    pa(intersect((Integer[]) box(a), (Integer[]) box(b))); //Integer[1,2,3,4,5]
//    pa(intersect((Integer[]) box(b), (Integer[]) box(a))); //Integer[1,2,3,4,5]
//    System.out.println();
//
//    // additional intersectMultiset and intersect tests
//    // previous tests of intersectMultiset and intersectwere without nulls; 
//    // now trying with several nulls in one of the arrays
//    Integer[] ia = new Integer[]
//        {null,39,12,1,38,17,2,37,15,null,2,36,12,9,9,34,1,31,7,29,40,13,1,17,null};
//    Integer[] ib = new Integer[]{12,9,1,12,2,9,4,2,12,9,1};
//    pa(intersectMultiset(ia,ib)); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersectMultiset(ib,ia)); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersect(ia,ib)); //Integer[1,2,9,12]
//    pa(intersect(ib,ia)); //Integer[1,2,9,12]
//    System.out.println();
//
//    // now trying with the same number (3) of nulls in each array
//    ib = new Integer[]{null,12,9,1,12,2,null,9,4,2,12,9,1,null};
//    pa(intersectMultiset(ia,ib)); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersectMultiset(ib,ia)); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersect(ia,ib)); //Integer[1,2,9,12]
//    pa(intersect(ib,ia)); //Integer[1,2,9,12]
//    System.out.println();
//
//    // now trying with different numbers of nulls (3 vs 5) in the arrays
//    ia = new Integer[]
//        {null,39,12,1,38,null,17,2,37,15,null,2,36,12,9,9,34,1,null,31,7,29,40,13,1,17,null};
//    pa(intersectMultiset(ia,ib)); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersectMultiset(ib,ia)); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersect(ia,ib)); //Integer[1,2,9,12]
//    pa(intersect(ib,ia)); //Integer[1,2,9,12]
//    System.out.println();
//
//    // now trying with and odd difference of null counts (3 vs 6) in the arrays
//    ia = new Integer[]
//        {null,39,12,1,38,null,17,2,37,15,null,2,36,null,12,9,9,34,1,null,31,7,29,40,13,1,17,null};
//    pa(intersectMultiset(ia,ib)); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersectMultiset(ib,ia)); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersect(ia,ib)); //Integer[1,2,9,12]
//    pa(intersect(ib,ia)); //Integer[1,2,9,12]
//    System.out.println();
//
//    // now trying with one null in the shortest array and none in the longest
//    ib = new Integer[]{null,12,9,1,12,2,9,4,2,12,9,1};
//    ia = new Integer[]
//        {39,12,1,38,17,2,37,15,2,36,12,9,9,34,1,31,7,29,40,13,1,17};
//    pa(intersectMultiset(ia,ib)); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersectMultiset(ib,ia)); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersect(ia,ib)); //Integer[1,2,9,12]
//    pa(intersect(ib,ia)); //Integer[1,2,9,12]
//    System.out.println();
//
//    // now trying with two nulls in the shortest array and none in the longest
//    ib = new Integer[]{null,12,9,1,12,2,9,4,2,12,9,1,null};
//    ia = new Integer[]
//        {39,12,1,38,17,2,37,15,2,36,12,9,9,34,1,31,7,29,40,13,1,17};
//    pa(intersectMultiset(ia,ib)); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersectMultiset(ib,ia)); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersect(ia,ib)); //Integer[1,2,9,12]
//    pa(intersect(ib,ia)); //Integer[1,2,9,12]
//    System.out.println();
//
//    // now trying with two nulls in the shortest array and one in the longest
//    ib = new Integer[]{null,12,9,1,12,2,9,4,2,12,9,1,null};
//    ia = new Integer[]
//        {39,12,1,38,17,2,37,15,2,36,12,9,9,34,1,31,7,29,40,13,1,17,null};
//    pa(intersectMultiset(ia,ib)); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersectMultiset(ib,ia)); //Integer[1,1,2,2,9,9,12,12]
//    pa(intersect(ia,ib)); //Integer[1,2,9,12]
//    pa(intersect(ib,ia)); //Integer[1,2,9,12]
//    System.out.println();
//
//    // now trying variations two increasing sequences with various nulls
//    ib = new Integer[]{1,2,3,4,5,null};
//    ia = new Integer[]{1,2,3,4,5};
//    pa(intersectMultiset(ia,ib)); //Integer[1,2,3,4,5]
//    pa(intersectMultiset(ib,ia)); //Integer[1,2,3,4,5]
//    pa(intersect(ia,ib)); //Integer[1,2,3,4,5]
//    pa(intersect(ib,ia)); //Integer[1,2,3,4,5]
//    System.out.println();
//
//    ib = new Integer[]{1,2,3,4,5,6,null};
//    ia = new Integer[]{1,2,3,4,5};
//    pa(intersectMultiset(ia,ib)); //Integer[1,2,3,4,5]
//    pa(intersectMultiset(ib,ia)); //Integer[1,2,3,4,5]
//    pa(intersect(ia,ib)); //Integer[1,2,3,4,5]
//    pa(intersect(ib,ia)); //Integer[1,2,3,4,5]
//    System.out.println();
//
//    ib = new Integer[]{1,2,3,4,5,6,null};
//    ia = new Integer[]{1,2,3,4,5,null};
//    pa(intersectMultiset(ia,ib)); //Integer[1,2,3,4,5]
//    pa(intersectMultiset(ib,ia)); //Integer[1,2,3,4,5]
//    pa(intersect(ia,ib)); //Integer[1,2,3,4,5]
//    pa(intersect(ib,ia)); //Integer[1,2,3,4,5]
//    System.out.println();
//
//    ib = new Integer[]{1,2,3,4,5,6,null};
//    ia = new Integer[]{1,2,3,4,5,null,null};
//    pa(intersectMultiset(ia,ib)); //Integer[1,2,3,4,5]
//    pa(intersectMultiset(ib,ia)); //Integer[1,2,3,4,5]
//    pa(intersect(ia,ib)); //Integer[1,2,3,4,5]
//    pa(intersect(ib,ia)); //Integer[1,2,3,4,5]
//    System.out.println();
//
//    ib = new Integer[]{1,2,3,4,5,null,null};
//    ia = new Integer[]{1,2,3,4,5,null};
//    pa(intersectMultiset(ia,ib)); //Integer[1,2,3,4,5]
//    pa(intersectMultiset(ib,ia)); //Integer[1,2,3,4,5]
//    pa(intersect(ia,ib)); //Integer[1,2,3,4,5]
//    pa(intersect(ib,ia)); //Integer[1,2,3,4,5]
//    System.out.println();
//
//    ib = new Integer[]{1,2,3,4,5,6,null,null,null};
//    ia = new Integer[]{1,2,3,4,5};
//    pa(intersectMultiset(ia,ib)); //Integer[1,2,3,4,5]
//    pa(intersectMultiset(ib,ia)); //Integer[1,2,3,4,5]
//    pa(intersect(ia,ib)); //Integer[1,2,3,4,5]
//    pa(intersect(ib,ia)); //Integer[1,2,3,4,5]
//    System.out.println();

    // performance comparison multisetIntersect vs intersectMultisetMapped
    // intersectMultisetMapped is implemented using two Maps
    // using int versions of both but intersectMultisetMapped only has boxing
    // and unboxing overhead
    
    Random r = new Random(77);
    int[] a = r.ints(10000,1,501).toArray();
    int[] b = r.ints(10000,1,501).toArray();
    Timer t = new Timer();
    int[] d = intersectMultisetMapped(a,b);
    t.stop();
    Thread.sleep(5000);
    t.reset();
    int[] c = multisetIntersect(a,b);
    t.stop();
    System.out.println(Arrays.equals(c,d)); //true for all trials
    
//    times in ms               run first     run last
//    multisetIntersect         27,27,26      8,8,8
//    intersectMultisetMapped   40,42,40      52,53,48
    
    // performance comparison intersect vs intersectSet
    // intersectSet is implemented using two Sets
    // using the generic versions of both
    
    Random r2 = new Random(77);
    int[] a2 = r2.ints(10000,1,501).toArray();
    Integer[] ia = (Integer[]) box(a2);
    int[] b2 = r.ints(10000,1,501).toArray();
    Integer[] ib = (Integer[]) box(b2);
    Timer t2 = new Timer();
    Integer[] c2 = intersect(ia,ib);
    t2.stop();
    Thread.sleep(5000);
    t2.reset();
    Integer[] d2 = intersect(ia,ib);
    t2.stop();
    System.out.println(Arrays.equals(c2,d2)); //true for all trials
//  results show the virtue of hashing for performance  
//  times in ms               run first     run last
//  intersect                 23,26,24      56,68,63
//  intersectSet              7,7,7         31,17,42
    
  }

}
