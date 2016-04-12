package ch14.compiling.scripting;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

//  10. The solution of the preceding exercise is not quite as good as a Unix pipe 
//  because the second command only starts when the first one has finished. Remedy
//   that by using the ProcessBuilder class.

// In order to do this I developed a java function to create and run a pipeline of
// commands linking the stdout of one command to the stdin of the next and printing
// the stdout of only the last command. This function is named pipe(). It is included
// below with a demo of it launched from main. Then to use it in JavaScript I exported
// this class as a jar from Eclipse to a location in my Windows CLASSPATH and accessed
// its static  paths() function in the Ch1410jsProcessBuilderEnhancement.js script as 
// Packages.ch14.compiling.scripting.Ch1410jsProcessBuilderEnhancement.pipe(cmd, dir);
// The content of this script is given below in a comment in main():

public class Ch1410jsProcessBuilderEnhancement {
  
  // run a pipline of processes with currrent working directory set only
  // for the first and final output printed only by the last
  public static void pipe(List<List<String>> c, String directory) {
    Process[] p = new Process[c.size()];
    InputStream[] is = new InputStream[c.size()];
    OutputStream[] os = new OutputStream[c.size()];
    BufferedWriter[] bw = new BufferedWriter[c.size()];
    Scanner[] sc = new Scanner[c.size()];
    
    try {
      for (int i = 0; i < c.size(); i++) {
        if (i == 0) {
          p[i] = new ProcessBuilder(c.get(i)).directory(Paths.get(directory).toFile()).start();
          is[i] = p[i].getInputStream();    
        } else {
          p[i] = new ProcessBuilder(c.get(i)).start();
          is[i] = p[i].getInputStream();
          os[i] = p[i].getOutputStream();
          bw[i] = new BufferedWriter(new OutputStreamWriter(os[i]));
          
          sc[i-1] = new Scanner(is[i-1]);
          while (sc[i-1].hasNextLine()) {
              bw[i].write(sc[i-1].nextLine()+System.lineSeparator());
          }
          
          sc[i-1].close();
          bw[i].flush();
          bw[i].close();
          os[i].close();
          is[i-1].close();
        }  
      }

      sc[c.size()-1] = new Scanner(is[c.size()-1]);
      while (sc[c.size()-1].hasNextLine())
        System.out.println(sc[c.size()-1].nextLine());
      
      sc[c.size()-1].close();
      sc[c.size()-2].close();
      is[c.size()-2].close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
  public static void main(String[] args) {
    
   // 4 stage find|grep|sort|sed pipeline
    
    List<String> cmd1 = new ArrayList<>(Arrays.asList("C:/Rtools/bin/find"));
    List<String> cmd2  = new ArrayList<>(Arrays.asList("C:/Rtools/bin/grep","\".class$\""));
    List<String> cmd3  = new ArrayList<>(Arrays.asList("C:/Rtools/bin/sort"));
    List<String> cmd4  = new ArrayList<>(Arrays.asList("C:/Rtools/bin/sed","-e \"s/^\\\\.\\\\///\""));
    List<List<String>> cmd = new ArrayList<List<String>>(Arrays.asList(cmd1,cmd2,cmd3,cmd4));
    String dir = "C:\\java\\test";
    pipe(cmd, dir);
    
    // output:
//  Ch0106BigIntFact.class
//  HelloWorld.class
//  Misc.class
//  Network$Member.class
//  Network.class
//  ch01/sec01/HelloWorld.class
//  ch1108/annotations/testcase/MyMath.class
//  ch1108/annotations/testcase/MyMathTest.class
//  ch1108/annotations/testcase/TestCase.class
//  ch1108/annotations/testcase/TestCaseAnnotationProcessor.class
//  ch1108/annotations/testcase/TestCases.class
    
//  paths() can be similarly run from Nashorn with the following script that
//  accesses ch14.compiling.scripting.Ch1410jsProcessBuilderEnhancement from
//  the system CLASSPATH:
    
    // JavaScript/Ch1410jsProcessBuilderEnhancement.js:
//  var cmd1 = java.util.Arrays.asList("C:/Rtools/bin/find");
//  var cmd2 = java.util.Arrays.asList("C:/Rtools/bin/grep","\".class$\"");
//  var cmd3 = java.util.Arrays.asList("C:/Rtools/bin/sort");
//  var cmd4 = java.util.Arrays.asList("C:/Rtools/bin/sed","-e \"s/^\\\\.\\\\///\"");
//  var cmd =  java.util.Arrays.asList(cmd1,cmd2,cmd3,cmd4);
//  var dir = "C:/java/test";
//  Packages.ch14.compiling.scripting.Ch1410jsProcessBuilderEnhancement.pipe(cmd, dir);

  }

}
