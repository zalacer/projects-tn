package ex34;

import static java.util.Arrays.sort;
import static utils.RandomUtils.randomString;
import static v.ArrayUtils.arrayToString;
import static v.ArrayUtils.max;
import static v.ArrayUtils.repeat;
import static v.ArrayUtils.unique;

import java.security.SecureRandom;
import java.util.Arrays;


/* p480
  3.4.4  Write a program to find values of a and M, with M as small 
  as possible, such that the hash function (a * k) %  M for trans-
  forming the kth letter of the alphabet into a table index produces 
  distinct values (no collisions) for the keys  S E A R C H X M P L .
  The result is known as a perfect hash function.

  Since there are 10 keys, M >= 10. Since the int value of 'S' is 83 
  that's prime and also the largest prime factor in the keys as ints, 
  M <= 83. Based on that, my approach is to find the smallest number
  for M in (10..83] such that all the hashes are unique by iteratively
  trying each starting with the smallest.
  
  Testing shows this approach works generally for char/Character keys
  as demonstrated below using hashParamMTest(String).
 */             

public class Ex3404PerfectHashFunction {

  public static int hashParamM(String s) {
    // return hash parameter M for characters in s as keys.
    // using a = 1 a priori. if -1 is returned it means that
    // the method failed to find a suitable value for M.
    String ss = s.replaceAll("\\s+","");
    String[] sa = ss.split("");
    sa = unique(sa);
    char[] ca = new char[sa.length];
    for (int i = 0; i < sa.length; i++) ca[i] = sa[i].charAt(0);
    sort(ca);
    int max = (int)max(ca), m = -1;
    int start = ca.length+1;
    int[] ia = new int[ca.length];
    for (int i = start; i <= max; i++) {
      for (int j = 0; j < ca.length; j++) ia[j] = ca[j]  % i;
      if (allUnique(ia)) { m = i; break; }
    }
    return m;
  }
  
  public static void hashParamMTest(String s) {
    System.out.print("Keys = ");
    String ss = s.replaceAll("\\s+","");
    String[] sa = ss.split("");
    sa = unique(sa);
    sort(sa);
    for (int i = 0; i < sa.length; i++) System.out.print(sa[i]+" ");
    System.out.println();

    int M = hashParamM(s);
    if (M == -1) {
      System.out.println("hashParamM failed\n");
      return;
    }
    System.out.println("M = "+M);
    System.out.println("a = "+1+" (assumed)");

    char[] ca = new char[sa.length];
    for (int i = 0; i < sa.length; i++) ca[i] = sa[i].charAt(0);
    sort(ca);
    int[] hashcodes = new int[ca.length];
    for (int i = 0; i < ca.length; i++) hashcodes[i]= ca[i] % M;
    sort(hashcodes);
    System.out.println("hashcodes = "+arrayToString(hashcodes,999,1,1));
    assert Arrays.equals(hashcodes, unique(hashcodes));
    System.out.println("number of unique hashcodes = "+hashcodes.length);
    System.out.println();
  }

  public static boolean allUnique(int[] a) {
    // return true if a has no dups else false
    if (a.length < 2) return true;
    sort(a);
    for (int i = 0; i < a.length-1; i++) 
      if (a[i] == a[i+1]) return false;
    return true;
  }
  
  public static String makeRandomString(int len) {
    SecureRandom r = new SecureRandom();
    r.setSeed(System.currentTimeMillis());
    StringBuilder sb = new StringBuilder();
    while (sb.length() < len) {
      for (int i = 32; i < 178; i++)
        sb.append(repeat((char)i, r.nextInt(11)));
    }
    return sb.substring(0,len);
  }

  public static void main(String[] args) {
      
    hashParamMTest("S E A R C H X M P L");
    hashParamMTest("YHBIZAGTRS");
    hashParamMTest("yhbizagtrs");
    hashParamMTest("AWSDJBEFYKY");
    hashParamMTest("VORMAKCOUQURG");
    hashParamMTest("LJEHQPZVWLDOFMPNPKVWTDAQACRFDGYTIKFMAKZ");
    hashParamMTest("smVBHVUAtYtdBSObeRPmWUDFwDgQAXmiJCibZIm");
    hashParamMTest("V4y2E!6YjRNuiiWx19pKZ0&#@UzX0qbMRtkO-SH");
    hashParamMTest("jZN&G7CI-T#ZbJoosojQZZzUMWeMtac7Vmy*Qpj$W1+*X$*8a-7G&%mlfggkGh9F6bdr+#r=uEuO7Avx6PwH95*=6g_pXiZPmUPWEZylYWTh^ZM@VoY1Qv=gXVTcreUf-4ZrStmq?I%Z9?%&ggm5@WjOJN*%%ehv--YS1S?oM6Q8*c??R^c3xbXKfi5QElmI@L9YTmLQ5#4keGI3Q6Ljc6euTL8r3xjkxXvNPH9YjQVl8#Jwnu^|IwthmB$icGUo");
    hashParamMTest(randomString(9753));
    hashParamMTest(makeRandomString(3579));
    
  }

}

