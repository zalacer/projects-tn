package ex25;

import static java.util.Arrays.sort;
import static v.ArrayUtils.*;

import java.io.File;
import java.util.Comparator;

/* p357
  2.5.29 Sort files by size and date of last modification. Write comparators
  for the type File to order by increasing/decreasing order of file size, 
  ascending/descending order of file name, and ascending/descending order of 
  last modification date. Use these comparators in a program  LS that takes a 
  command-line argument and lists the files in the current directory according 
  to a specified order, e.g.,  "-t" to sort by timestamp. Support multiple flags 
  to break ties. Be sure to use a stable sort.

 */

public class Ex2529SortFilesByDateAndTimeOfLastModification { 

  public static File[] ls(String dir, String...s) {
    // compare files in a directory using size and lastModification attributes
    // and combinations of them to break ties.
    // command line switches:
    // 1. -size : compare using size only.
    // 2. -lastmod : compare using lastModified only.
    // 3. -rsize : compare using reverse size only.
    // 4. -rlastmod : compare using reverse lastModified only.
    // 5. -size -lastmod : compare using size, break ties with lastModified.
    // 6. -lastmod - size : compare using lastModified, break ties with size.
    // 7. -rsize -rlastmod : compare using reverse size, break ties with reverse lastModified.
    // 8. -rlastmod -rsize : compare using reverse lastModified, break ties with rsize.
    // if there is one arg it's used if it matches one of 1-4.
    // if there are two args they're used if they match one of 5-8.
    // if there are more than two args the first two are used if they match one of
    // 5-8 else the first is used if it matches one of 1-4.
    // otherwise IllegalArgumentException.

    if (dir == null) throw new IllegalArgumentException("dir can't be null");
    if (s == null || s.length == 0) throw new IllegalArgumentException("need an option arg");

    // define 8 comparators, but not all combinations
    Comparator<File> sizec = (f1,f2) -> {
      // compare using size
      long f1l = f1.length(); long f2l = f2.length();
      if (f1l < f2l) return -1;
      else if (f1l > f2l) return 1;
      else return 0;
    };
    Comparator<File> rsizec = (f1,f2) -> {
      // compare using reverse size
      long f1l = f1.length(); long f2l = f2.length();
      if (f1l < f2l) return 1;
      else if (f1l > f2l) return -1;
      else return 0;
    };
    Comparator<File> lastmodc = (f1,f2) -> {
      // compare using lastmod
      long f1l = f1.lastModified(); long f2l = f2.lastModified();
      if (f1l < f2l) return -1;
      else if (f1l > f2l) return 1;
      else return 0;
    }; 
    Comparator<File> rlastmodc = (f1,f2) -> {
      // compare using reverse rlastmod
      long f1l = f1.lastModified(); long f2l = f2.lastModified();
      if (f1l < f2l) return 1;
      else if (f1l > f2l) return -1;
      else return 0;
    };
    Comparator<File> sizelmc = (f1,f2) -> {
      // compare using size, break ties using lastmod
      long f1l = f1.length(); long f2l = f2.length();
      if (f1l < f2l) return -1;
      else if (f1l > f2l) return 1;
      else {
        f1l = f1.lastModified(); f2l = f2.lastModified();
        if (f1l < f2l) return 1;
        else if (f1l > f2l) return -1;
        else return 0;    
      }
    };
    Comparator<File> rsizerlmc = (f1,f2) -> {
      //compare using reverse size, break ties using reverse lastmod
      long f1l = f1.length(); long f2l = f2.length();
      if (f1l < f2l) return 1;
      else if (f1l > f2l) return -1;
      else {
        f1l = f1.lastModified(); f2l = f2.lastModified();
        if (f1l < f2l) return -1;
        else if (f1l > f2l) return 1;
        else return 0;    
      }
    };
    Comparator<File> lastmodszc = (f1,f2) -> {
      // compare using lastmod, break ties with size
      long f1l = f1.lastModified(); long f2l = f2.lastModified();
      if (f1l < f2l) return -1;
      else if (f1l > f2l) return 1;
      else {
        f1l = f1.length();  f2l = f2.length();
        if (f1l < f2l) return -1;
        else if (f1l > f2l) return 1;
        else return 0;
      }
    };
    Comparator<File> rlastmodrszc = (f1,f2) -> {
      // compare using reverse rlastmod, break ties with rsize
      long f1l = f1.lastModified(); long f2l = f2.lastModified();
      if (f1l < f2l) return 1;
      else if (f1l > f2l) return -1;
      else {
        f1l = f1.length(); f2l = f2.length();
        if (f1l < f2l) return 1;
        else if (f1l > f2l) return -1;
        else return 0;
      }
    };

    // gather the files into an array
    File x = new File(dir);
    if (!x.exists()) throw new IllegalArgumentException("dir doesn't exist");
    if (!x.isDirectory()) throw new IllegalArgumentException("dir isn't a directory");
    File[] a = x.listFiles();

    // capture args and sort the array
    if (s.length == 1) {
      switch (s[0]) {
      case "-size"     : sort(a,sizec);     break;
      case "-rsize"    : sort(a,rsizec);    break;
      case "-lastmod"  : sort(a,lastmodc);  break;
      case "-rlastmod" : sort(a,rlastmodc); break;
      default: throw new IllegalArgumentException("unrecognized option arg");
      }
    } else {
      switch (s[0]) {
      case "-size"     : 
        if (s[1].equals("-lastmod"))  sort(a,sizelmc);
        else sort(a,sizec);
        break;
      case "-rsize"    : 
        if (s[1].equals("-rlastmod")) sort(a,rsizerlmc);       
        else sort(a,rsizec);
        break;
      case "-lastmod"  : 
        if (s[1].equals("-size")) sort(a,lastmodszc); 
        else sort(a,lastmodc);
        break;
      case "-rlastmod" : 
        if (s[1].equals("-rsize")) sort(a,rlastmodrszc);    
        else sort(a,rlastmodc);
        break;
      default: throw new IllegalArgumentException("unrecognized options args");
      }
    }
    // return the array
    return a;
  }


  public static void main(String[] args) {

    show(ls("C:\\dirtest2", "-rsize", "-rlastmod"));
    // C:\dirtest2\file2.txt C:\dirtest2\file1.txt C:\dirtest2\file3.txt  
    
    show(ls("C:\\dirtest2", "-lastmod", "-size"));
    // C:\dirtest2\file3.txt C:\dirtest2\file2.txt C:\dirtest2\file1.txt 

    show(ls("C:\\dirtest2", "-rlastmod", "-rsize"));
    // C:\dirtest2\file1.txt C:\dirtest2\file2.txt C:\dirtest2\file3.txt
    
  }

}


