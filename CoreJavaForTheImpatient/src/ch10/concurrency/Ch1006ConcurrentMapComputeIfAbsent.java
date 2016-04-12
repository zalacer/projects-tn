package ch10.concurrency;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

//6. Repeat the preceding exercise, but use computeIfAbsent instead. 
// What is the advantage of this approach?

// ComputeIfAbsent is easier to understand and code than merge, but I didn't 
// really have a use case for it in this exercise since there was no need for 
// a Function<? super K,? extends V> because the result I needed did not depend
// explicitly on the key. Also the call to computeIfAbsent had to be followed
// with code to add the current file to a set in case the latter already existed.
// It seems to me that putIfAbsent would have worked as well. 

public class Ch1006ConcurrentMapComputeIfAbsent {

  public static Map<String, TreeSet<File>> mapWordsInFiles(String dir) {
    Path p = Paths.get(dir);
    ConcurrentHashMap<String, TreeSet<File>> chm = 
        new ConcurrentHashMap<String, TreeSet<File>>();
    try (Stream<Path> fw = Files.walk(p)) {
      fw.parallel().filter(f-> Files.isRegularFile(f)).forEach(f -> {
        try (Stream<String> ss = Files.lines(f)) {
          ss.map(l -> l.toLowerCase().split("\\s+")).flatMap(Arrays::stream)
          .filter(w -> isWord(w)).distinct()
          .forEach(w -> {
            File file = f.toFile();
            chm.computeIfAbsent(w, a -> new TreeSet<File>(Arrays.asList(file)));
            // in case the set value was not absent, add file to it
            chm.get(w).add(file); //could try chm.contains(w) first but why bother 
          });
        } catch (IOException e) {
          e.printStackTrace();
        }                
      });
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    return new TreeMap<String, TreeSet<File>>(chm);
  }

  public static boolean isWord(String s) {
    if (s.length() == 0) return false;
    return s.codePoints().allMatch(Character::isAlphabetic);
  }

  public static void main(String[] args) {

    String s = "dirtest";
    Map<String, TreeSet<File>> o = mapWordsInFiles(s);
    for (String k : o.keySet()) {
      System.out.println(k+":");
      for (File p : o.get(k))
        System.out.println("   "+p);
    }

    //  output: 
    //  alpha:
    //    dirtest\.hiddendir1\dir5\file8
    //  beta:
    //    dirtest\.hiddendir1\dir5\file8
    //  gamma:
    //    dirtest\.hiddendir1\dir5\file8
    //  gekko:
    //    dirtest\.hiddendir1\dir5\file8
    //  hello:
    //    dirtest\.hiddendir1\dir5\file8
    //    dirtest\dir1\file3
    //    dirtest\dir2\file5
    //    dirtest\file1
    //    dirtest\file2
    //  is:
    //    dirtest\dir1\dir3\file7.java
    //  one:
    //    dirtest\dir2\file5
    //  skskd:
    //    dirtest\.hiddendir1\dir5\file8
    //  this:
    //    dirtest\dir1\dir3\file7.java
    //  three:
    //    dirtest\dir2\file5
    //  two:
    //    dirtest\dir2\file5
    
  }

}
