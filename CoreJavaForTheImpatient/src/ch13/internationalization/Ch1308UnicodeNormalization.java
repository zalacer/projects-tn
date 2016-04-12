package ch13.internationalization;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.LinkedHashMap;

import utils.Pair;

// 8. Write a program that lists all Unicode characters that are expanded
// to two or more ASCII characters in normalization form KC or KD.

public class Ch1308UnicodeNormalization {
  
  public static void listUnicodeCharsThatExpand2MoreThanOneASCIIcharsUsingKCorKDnormalization() {
    LinkedHashMap<String, Pair<Integer>> u = new LinkedHashMap<>();
    int ulimit = 1114111; // 10FFFF, Unicode max codepoint
    for (int i = 0; i < ulimit+1; i++) {
      int[] ca = new int[]{i};
      String cs = new String(ca, 0, 1);
      
      // nfkc
      String nfkc = Normalizer.normalize(cs, Normalizer.Form.NFKC);
      int asciikc = 0;
      int nonasciikc = 0;
      int[] cpkc = nfkc.codePoints().toArray();
      for (int j = 0; j < cpkc.length; j++) {
        if (cpkc[j] <= 127) {
          asciikc++; 
        } else {
          nonasciikc++;
        }
      }
      
      // nfkd
      String nfkd = Normalizer.normalize(cs, Normalizer.Form.NFKD);
      int asciikd = 0;
      int nonasciikd = 0;
      int[] cpkd = nfkd.codePoints().toArray();
      for (int j = 0; j < cpkd.length; j++) {
        if (cpkd[j] <= 127) {
          asciikd++; 
        } else {
          nonasciikd++;
        }
      }
      
      // the lack of execution of this proves that for cases when either NFKC or NFKD 
      // normalization results in more than one ASCII char and no non-ASCII chars, they 
      // produce identical normalizations
      if ((asciikc > 1 && nonasciikc == 0) || (asciikd > 1 && nonasciikd == 0)) {
        if (!Arrays.equals(cpkc, cpkd)) {
          System.out.println("NFKC and NFKD normalizations give different results for \""
              +cs+"\" (codepoint "+i+")");
          System.out.println("NFKC normalization of \""+cs+"\" (codepoint "+i+"):");
          System.out.println("  \""+nfkc+"\" ("+Arrays.toString(cpkc));
          System.out.println("NFKD normalization of \""+cs+"\" (codepoint "+i+"):");
          System.out.println("  \""+nfkd+"\" ("+Arrays.toString(cpkd));
        }
      }
      
      if (asciikc > 1 && nonasciikc == 0) {
        u.put(cs, (new Pair<Integer>(i, asciikc)));
      } else if (asciikd > 1 && nonasciikd == 0) {
        u.put(cs, (new Pair<Integer>(i, asciikd)));
        // for futher proof this never executes
        System.out.println("using NFKD for \""+cs+"\" (codepoint i)");
      }
    }
    
//    System.out.println(u.size()); //NFKC + NFKD both 307 (> 1 ascii only)
    System.out.println("char   codepoint  #ASCII chars in decomposition");
    System.out.println("=====  =========  =============================");
    for(String s : u.keySet())
      System.out.printf(" %s \t%-4s\t              %s\n",
          s, u.get(s).getFirst(), u.get(s).getSecond());
  }

  public static void main(String[] args) {
  
    listUnicodeCharsThatExpand2MoreThanOneASCIIcharsUsingKCorKDnormalization();

    // for output see Ch1308UnicodeNormalization.pdf and Ch1308UnicodeNormalization.txt
    // in /Internationalization, or run the program and see output in console that
    // should be configured with fixed width font and 8 space tabs which is Eclipse
    // default
    
  }
}
