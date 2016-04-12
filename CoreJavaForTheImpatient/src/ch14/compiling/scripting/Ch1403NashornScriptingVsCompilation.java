package ch14.compiling.scripting;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import javax.script.Compilable;

import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

//  3. Is compiling worthwhile with Nashorn? Write a JavaScript program that sorts an
//  array the dumb way, trying all permutations until it is sorted. Compare the running
//  time of the compiled and interpreted version. Here is a JavaScript function for
//  computing the next permutation:
//    function nextPermutation(a) {
//      // Find the largest nonincreasing suffix starting at a[i]
//      var i = a.length - 1
//      while (i > 0 && a[i - 1] >= a[i]) i—
//      if (i > 0) {
//        // Swap a[i - 1] with the rightmost a[k] > a[i - 1]
//        // Note that a[i] > a[i - 1]
//        var k = a.length - 1
//        while (a[k] <= a[i - 1]) k—
//        swap(a, i - 1, k)
//      } // Otherwise, the suffix is the entire array
//        // Reverse the suffix
//        var j = a.length - 1
//       while (i < j) { swap(a, i, j); i++; j— }
//    }



public class Ch1403NashornScriptingVsCompilation {

  public static void main(String[] args) throws IOException, ScriptException {
    
//  A few tests with a simple sort in JavaScript shows that compilation
//  improves performance by about 1%. This may or may not generalize
//  and these tests do not justify a broad conclusion. Note that each 
//  was done in a separate run of Java main().
    
//    This is the sort.js script used for testing:
//
//    function swap(a,i,j) {
//      tmp = a[i]
//      a[i] = a[j]
//      a[j] = tmp
//    }
//
//    function nextPermutation(a) {
//      // Find the largest nonincreasing suffix starting at a[i]
//      var i = a.length - 1
//      while (i > 0 && a[i - 1] >= a[i]) i--
//      if (i > 0) {
//        // Swap a[i - 1] with the rightmost a[k] > a[i - 1]
//        // Note that a[i] > a[i - 1]
//        var k = a.length - 1
//        while (a[k] <= a[i - 1]) k--
//        swap(a, i - 1, k)
//      } // Otherwise, the suffix is the entire array
//      // Reverse the suffix
//      var j = a.length - 1
//      while (i < j) { swap(a, i, j); i++; j-- }
//      var c = true;
//      for (var i = 0; i < a.length - 1; i++) {
//        if (a[i] > a[i+1]) {
//          c = false;
//          break;
//        }
//      }
//      if (c) {
//        return(c)
//      }
//    }

//    var nums = [47,35,50,5,44,88,97,69,1,9,38,26]
//    print('unsorted array: '+nums)
//    var c = 0;
//
//    while (true) {
//      c++
//      var s = nextPermutation(nums)
//      if (s == true) {
//        break
//      }
//    }
//    
//    print('number of iterations: '+c)
//    print('sorted array: '+nums)  
          
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("nashorn");
 
    // without compilation
    System.out.println("without compilation");
    Instant start = Instant.now();
    engine.eval(Files.newBufferedReader(Paths.get("JavaScript/sort.js")));
    Instant stop = Instant.now();
    Duration timeElapsed = Duration.between(start, stop);
    long millis = timeElapsed.toMillis();
    System.out.println("millis:"+millis);
  
//  trial 1
//  unsorted array: 47,35,50,5,44,88,97,69,1,9,38,26
//  number of iterations: 182826743
//  sorted array: 1,5,9,26,35,38,44,47,50,69,88,97
//  millis:23136
  
//  trial 2
//  unsorted array: 47,35,50,5,44,88,97,69,1,9,38,26
//  number of iterations: 182826743
//  sorted array: 1,5,9,26,35,38,44,47,50,69,88,97
//  millis:22928
    
//  trial 3
//  unsorted array: 47,35,50,5,44,88,97,69,1,9,38,26
//  number of iterations: 182826743
//  sorted array: 1,5,9,26,35,38,44,47,50,69,88,97
//  millis:22983

//  with compilation
    System.out.println("\nwith compilation");
    Reader reader = Files.newBufferedReader(Paths.get("JavaScript/sort.js"));            
    CompiledScript script = ((Compilable) engine).compile(reader);
    start = Instant.now();
    script.eval();
    stop = Instant.now();
    timeElapsed = Duration.between(start, stop);
    millis = timeElapsed.toMillis();
    System.out.println("millis:"+millis);

//  trial 1
//  unsorted array: 47,35,50,5,44,88,97,69,1,9,38,26
//  number of iterations: 182826743
//  sorted array: 1,5,9,26,35,38,44,47,50,69,88,97
//  millis:22773

//  trial 2
//  unsorted array: 47,35,50,5,44,88,97,69,1,9,38,26
//  number of iterations: 182826743
//  sorted array: 1,5,9,26,35,38,44,47,50,69,88,97
//  millis:22902
    
//  trial 3
//  unsorted array: 47,35,50,5,44,88,97,69,1,9,38,26
//  number of iterations: 182826743
//  sorted array: 1,5,9,26,35,38,44,47,50,69,88,97
//  millis:22883
    
//  test of compilation time
//  start = Instant.now();
//  ((Compilable) engine).compile(reader);
//  stop = Instant.now();
//  timeElapsed = Duration.between(start, stop);
//  millis = timeElapsed.toMillis();
//  System.out.println("millis:"+millis);
//  millis:155
//  millis:146
//  millis:157
    
  }

}
