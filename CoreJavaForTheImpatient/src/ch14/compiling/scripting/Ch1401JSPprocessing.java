package ch14.compiling.scripting;

import static utils.StringUtils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

//  1. In the JavaServer Pages technology, a web page is a mixture of HTML and Java, 
//  for example:
//    <ul>
//    <% for (int i = 10; i >= 0; i--) { %>
//      <li><%= i %></li>
//    <% } %>
//    <p>Liftoff!</p>
//  Everything outside <%...%> and <%=...%> is printed as is. Code inside is
//  evaluated. If the starting delimiter is <%=, the result is added to the printout.
//  Implement a program that reads such a page, turns it into a Java method, executes it,
//  and yields the resulting page.

// references for translating JSP to Java servlet
// http://cs.au.dk/~amoeller/WWW/jsp/translation.html
// http://www.informit.com/articles/article.aspx?p=130980&seqNum=5

public class Ch1401JSPprocessing {
  
  // write the output of jsp2java() to a file and load it with an URLClassLoader
  public static String createAndExecuteMethod(String name, String jspFile) {
    // write output of jsp2java to a .java file
    String cname = title(name);
    Path tdir = null;
    Path javaFilePath = null;
    try {
      tdir = Files.createTempDirectory("zzz.");
      javaFilePath = Files.createFile(Paths.get(tdir.toString()+"\\"+cname+".java"));
      Files.write(javaFilePath, jsp2java(cname, jspFile).getBytes("UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    // compile the Java file
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    StandardJavaFileManager fileManager = 
        compiler.getStandardFileManager(diagnostics, null, null);
    Iterable<? extends JavaFileObject> compilationUnits = 
        fileManager.getJavaFileObjectsFromStrings(Arrays.asList(javaFilePath.toString()));
    JavaCompiler.CompilationTask task = 
        compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
    boolean success = task.call();
    if (success == false) return "";
 
    String webPage = ""; 
    
    // load the class resulting from compilation and use reflection to 
    // run its only method returning the string it returns.
    try {
      fileManager.close();
      URL url = tdir.toUri().toURL();          
      URL[] urls = new URL[]{url};
      URLClassLoader cl = new URLClassLoader(urls);
      Class<?> cls = cl.loadClass(cname);
      Method method = cls.getMethod("createWebPage");
      webPage = (String) method.invoke(cls);
      cl.close();
    } catch (IOException | ClassNotFoundException | NoSuchMethodException 
        | SecurityException |IllegalAccessException  | IllegalArgumentException 
        | InvocationTargetException e) {
      e.printStackTrace();
    } 
    return webPage;
  }
  
  // write the output of jsp2java() to a string and compile it in and 
  // load the resulting class file from memory with StringSource, 
  // ByteArrayClass and ByteArrayClassLoader using a modification of 
  // Dr. Horstmann's CompilerDemo.java, described in Chapter 14.1 of the 
  // text and provided in the sources accompanying it, applied to the 
  // case of a class with only one method and no fields.
  public static String createAndExecuteMethod2(String name, String jspFile) {
    String cname = title(name);
    String cmethod = "createWebPage";
    String code = jsp2java(cname, jspFile);
//    System.out.println(cname+".java code:\n"+code+"\n");
    
    List<StringSource> sources = Arrays.asList(
            new StringSource(cname, code));
    
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
    
    List<ByteArrayClass> classes = new ArrayList<>();
    
    StandardJavaFileManager stdFileManager = compiler
            .getStandardFileManager(null, null, null);
    
    JavaFileManager fileManager = new ForwardingJavaFileManager<JavaFileManager>(
            stdFileManager) {
        @Override
        public JavaFileObject getJavaFileForOutput(Location location,
                String className, Kind kind, FileObject sibling)
                throws IOException {
            if (kind == Kind.CLASS) {
                ByteArrayClass outfile = new ByteArrayClass(className);
                classes.add(outfile);
                return outfile;
            } else
                return super.getJavaFileForOutput(location, className,
                        kind, sibling);
        }
    };
    
    JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
            collector, null, null, sources);
    task.call();
//    Boolean result = task.call();
    for (Diagnostic<? extends JavaFileObject> d : collector.getDiagnostics()) {
        System.out.println(d);
    }
//    System.out.println("task result: "+result);
    ByteArrayClassLoader loader = new ByteArrayClassLoader(classes);
    Class<?> cl = null;
    try {
      cl = Class.forName(cname, true, loader);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
//    System.out.println(Arrays.toString(cl.getDeclaredFields()));
//    System.out.println(Arrays.toString(cl.getDeclaredMethods()));
    Method method = null;
    String webPage = null;
    try {
      method = cl.getMethod(cmethod);
      webPage = (String) method.invoke(cl);
    } catch (NoSuchMethodException | SecurityException |IllegalAccessException 
        | IllegalArgumentException | InvocationTargetException  e) {
      e.printStackTrace();
    }  
    return webPage;
  }
  
  public static String jsp2java(String name, String file) {
    String r1 = "<%(.+)%>";
    Pattern p1 = Pattern.compile(r1);
    String r2 = "(<[^>]*>)\\s*<%=([^%>]*)%>\\s*(.+)";
    Pattern p2 = Pattern.compile(r2);
    Matcher m;
    
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("public class %s {\n", name));
    sb.append("    public static String createWebPage() {\n");
    sb.append("        StringBuilder sb = new StringBuilder();\n");
    sb.append("        sb.append(\"<!DOCTYPE html PUBLIC \\\"-//W3C//DTD XHTML 1.0 Strict//EN\\\" \\\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\\\">\\n\");\n");
    sb.append("        sb.append(\"<html>\\n\");\n");
    sb.append("        sb.append(\"<head><title>"+name+"</title></head>\\n\");\n");
    sb.append("        sb.append(\"<body>\\n\");\n");
    sb.append("        sb.append(\"<style>\\n\");\n");
    sb.append("        sb.append(\"ul {display:table; margin:0 auto;}\\n\");\n");
    sb.append("        sb.append(\"</style>\\n\");\n");

    Scanner s = null;
    try {
      s = new Scanner(Paths.get(file));
    } catch (IOException e) {
      e.printStackTrace();
    }
    while(s.hasNextLine()) {
      String line = s.nextLine().trim();
      m = p1.matcher(line); 
      if (m.matches()) {
        sb.append("        "+m.group(1).trim()+"\n");
      } else {
        m = p2.matcher(line);
        if (m.matches()) {
          sb.append("            sb.append(\""
                +m.group(1)+"\" + "+m.group(2)+" + \""+m.group(3)+"\\n\");\n");
        } else {
          sb.append("        sb.append(\""+line+"\\n\");\n");
        }
      }     
    }
    sb.append("        sb.append(\"</body>\\n\");\n");
    sb.append("        sb.append(\"</html>\\n\");\n");
    sb.append("        return sb.toString();\n");
    sb.append("    }\n");
    sb.append("}\n");
    return sb.toString();
  }
  
  public static void main(String[] args) throws IOException {
    
   // Ch1401JSPprocessing.txt is the JSP input file
   String webPage = createAndExecuteMethod("Liftoff", "Ch1401JSPprocessing.txt");
   System.out.println(webPage);
// <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
// <html>
// <head><title>Liftoff</title></head>
// <body>
// <style>
// ul {display:table; margin:0 auto;}
// </style>
// <ul>
// <li>10</li>
// <li>9</li>
// <li>8</li>
// <li>7</li>
// <li>6</li>
// <li>5</li>
// <li>4</li>
// <li>3</li>
// <li>2</li>
// <li>1</li>
// <li>0</li>
// <p>Liftoff!</p>
// </body>
// </html>

  // Ch1401JSPprocessing.txt is the JSP input file
  String webPage2 = createAndExecuteMethod2("Liftoff", "Ch1401JSPprocessing.txt");
  System.out.println(webPage2);
//  <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
//  <html>
//  <head><title>Liftoff</title></head>
//  <body>
//  <style>
//  ul {display:table; margin:0 auto;}
//  </style>
//  <ul>
//  <li>10</li>
//  <li>9</li>
//  <li>8</li>
//  <li>7</li>
//  <li>6</li>
//  <li>5</li>
//  <li>4</li>
//  <li>3</li>
//  <li>2</li>
//  <li>1</li>
//  <li>0</li>
//  <p>Liftoff!</p>
//  </body>
//  </html>
    
//  Liftoff.java code: (located in the temp directory zzz.[0-9]* folder on your platform,
//    on Windows 7 this is in C:/Users/userName/AppData/Local/Temp/
//    public class Liftoff {
//        public static String createWebPage() {
//            StringBuilder sb = new StringBuilder();
//            sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
//            sb.append("<html>\n");
//            sb.append("<head><title>Liftoff</title></head>\n");
//            sb.append("<body>\n");
//            sb.append("<style>\n");
//            sb.append("ul {display:table; margin:0 auto;}\n");
//            sb.append("</style>\n");
//            sb.append("<ul>\n");
//            for (int i = 10; i >= 0; i--) {
//                sb.append("<li>" +  i  + "</li>\n");
//            }
//            sb.append("<p>Liftoff!</p>\n");
//            sb.append("</body>\n");
//            sb.append("</html>\n");
//            return sb.toString();
//        }
//    }


  }

}
