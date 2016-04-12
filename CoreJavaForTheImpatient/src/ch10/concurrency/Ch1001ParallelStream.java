package ch10.concurrency;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;

// 1. Using parallel streams, find all files in a directory that contain 
// a given word. How do you find just the first one? Are the files actually 
// searched concurrently?

// On a computer with multiple cores it's searched simultaneously in parallel 
// on separate cores as well as possibly concurrently with multiple threads on 
// them. The first word can be found by setting limit(1) just before the
// finalizer of the group of parallel streams, however that's "first" as
// defined by the first match found by the algorithm and which may find 
// different first matches on different runs since the performance of each thread
// on each core depends on its state and that of the JVM which depends on 
// their initial states and may be different from run to run unless strictly
// controlled which isn't easy on platforms for general use. For better determinism,
// an ordered list of files may be searched sequentially.

public class Ch1001ParallelStream {

  public static String[] findFilesWithWord(String dir, String word, long limit) {
    Path p = Paths.get(dir);
    Set<String> f = new ConcurrentSkipListSet<>();
    try (Stream<Path> fw = Files.walk(p)) {
      fw.parallel().filter(x-> {
        if (!Files.isRegularFile(x)) return false;
        long r = 0;
        try {
          r = Files.lines(x).filter(s->s.matches(".*"+word+".*")).limit(1).count();
        } catch (Exception e) {
          e.printStackTrace();
        }
        return r == 1 ? true : false;
      }).limit(limit).forEach(y->f.add(y.toString()));     
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    return f.toArray(new String[f.size()]);
  }   

  public static void main(String[] args) {

    String s = "dirtest";
    String[] r = findFilesWithWord(s,"hello",Long.MAX_VALUE);
    for(String e : r) System.out.println(e);
    //  dirtest\.hiddendir1\dir5\file8
    //  dirtest\dir1\file3
    //  dirtest\dir2\file5
    //  dirtest\file1
    //  dirtest\file2

    String[] r2 = findFilesWithWord(s,"hello",1);
    System.out.println();
    for(String e : r2) System.out.println(e);
    // dirtest\.hiddendir1\dir5\file8

  }

}
