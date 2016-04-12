package ch10.concurrency;

import static java.util.logging.Level.FINEST;
import static utils.ConsoleLoggerSetup.setupConsoleLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 3. Implement a method yielding a task that reads through all words 
// in a file, trying to find a given word. The task should finish 
// immediately (with a debug message) when it is interrupted. For all 
// files in a directory, schedule one task for each file. Interrupt all 
// others when one of them has succeeded.

// Throwing a new InterruptedException() when Thread.interrupted() seems 
// to work better for stopping processing but the error and log messages 
// are often not printed to eclipse console

public class Ch1003Interruption {

  private static final Logger log = Logger.getLogger("ThreadLogger");

  public static String findWord(Path f, String word, Thread[] threads) {
    if (!Files.exists(f))
      return "";
    if (!Files.isRegularFile(f))
      return "";
    boolean r = false;
    try (Stream<String> ss = Files.lines(f)) {
      if (Thread.interrupted()) throw new InterruptedException();
      List<String> list = ss.collect(Collectors.toList());
      int count = 0;
      String wordLC = word.toLowerCase();
      for(String line : list) {
        count++;
        if (count % 100 == 0 && Thread.interrupted()) throw new InterruptedException();             
        String[] words = line.trim().toLowerCase().split("\\s+"); 
        String msg;
        for(String drow : words) {
          if (drow.matches(wordLC)) {
            for (Thread thread : threads) {
              thread.interrupt();
            }
            msg ="found "+word+" in "+f.toString();
            return (Thread.currentThread().getName()+":"+msg);
          }
        }
      }
    } catch (InterruptedException e1) {
      System.err.println(Thread.currentThread().getName()+":interrupted");
      // log.finest(Thread.currentThread().getName()+":interrupted");
      Thread.currentThread().interrupt();
    } catch (Exception e) {}

    if (r) return f.toString();
    else return "";
  }

  public static Runnable yeildRunnable(Path f, String word, Thread[] threads) {
    return () -> {String r = findWord(f, word, threads); 
    if (r.length() > 0) System.out.println(r);};    
  }

  public static Set<Path> getFilePaths(String dname) {
    Set<Path> set = Collections.emptySet(); 
    Path d = Paths.get(dname);
    if (!Files.isDirectory(d))
      return set;
    try (Stream<Path> sp = Files.walk(d)) {
      set = sp.filter(Files::isRegularFile).collect(Collectors.toSet());
    } catch (IOException e) {
    }
    return set;
  }

  public static void startThreads(Thread[] threads) {
    for (Thread t : threads) {
      t.start(); 
    }
  }

  public static void joinThreads(Thread[] threads) {
    for (Thread t : threads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    setupConsoleLogger(FINEST);
    log.setLevel(Level.ALL);

    String word = "christmas";
    Object[] paths = getFilePaths("books").toArray();
    Thread[] threads = new Thread[paths.length];

    for (int i = 0; i < paths.length; i++) {
      Path p = (Path) paths[i];
      threads[i] = new Thread(yeildRunnable(p, word, threads), "thread-"+i);
    }

    startThreads(threads);
    joinThreads(threads);

    // example output:
    // thread-2:found christmas in books\Metamorphosis1956.txt
    // thread-0:interrupted
    // thread-5:interrupted
    // thread-3:interrupted

  }

}
