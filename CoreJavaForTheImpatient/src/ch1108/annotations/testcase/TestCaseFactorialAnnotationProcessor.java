package ch1108.annotations.testcase;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes({"ch1108.annotations.testcase.TestCase","ch1108.annotations.testcase.TestCases"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TestCaseFactorialAnnotationProcessor extends AbstractProcessor {

  private String sourceFQClass = ""; 
  private String sourcePkg = "";
  private String targetFQClass = ""; 
  private String targetClass = "";
  private String funName = "";
 
  @Override
  public boolean process(Set<? extends TypeElement> annotations,
      RoundEnvironment currentRound) {
    if (annotations.size() == 0) return true;

    for (Element e : currentRound.getElementsAnnotatedWith(TestCases.class)) {
      funName = e.getSimpleName().toString();
      sourceFQClass = e.getEnclosingElement().toString(); 
      break;
    }
    
    if (funName.equals(""))
      for (Element e : currentRound.getElementsAnnotatedWith(TestCase.class)) {
        funName = e.getSimpleName().toString();
        sourceFQClass = e.getEnclosingElement().toString();
        break;
      }
 
    sourcePkg = sourceFQClass.replaceFirst("\\.[^.]*$", "");
    targetFQClass = sourceFQClass + "Test";
    targetClass = targetFQClass.replaceFirst(".*\\.", "");
   
    try {
      JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(targetFQClass);

      try (PrintWriter out = new PrintWriter(sourceFile.openWriter())) {
        out.println("// Automatically generated by ch1108.annotations.testcase.TestCaseAnnotationProcessor");
        out.println("package "+sourcePkg+";");
        out.println("\r\n  public class "+targetClass+"  {");
        out.println("\r\n    public static void main(String[] args) {");
              
        for (Element el : currentRound.getElementsAnnotatedWith(TestCase.class)) {
          TestCase[] annos = el.getAnnotationsByType(TestCase.class);
          for (TestCase tc : annos) processElement(el, tc, out);
        }
        
        for (Element el : currentRound.getElementsAnnotatedWith(TestCases.class)) {
          TestCase[] annos = el.getAnnotationsByType(TestCase.class);
          for (TestCase tc : annos) processElement(el, tc, out);
        }
        
        out.println("    }");
        out.println("\r\n  }");
        out.close();
      }
    } catch (IOException ex) {
      processingEnv.getMessager().printMessage(Kind.ERROR, ex.getMessage());
    }        
    return true;
  }
  
  private void processElement(Element el, TestCase anno, PrintWriter out) {
    String[] pa = anno.params().split(",");
    if (pa.length == 0) return;
    for (String s : pa) {
      if (! s.matches("[0-9]+")) {
        System.err.println("an element in params is not an int");
        return;
      }
    }
    int[] p = new int[pa.length];
    for (int i = 0; i < pa.length; i++) p[i] = Integer.parseInt(pa[i]);

    // process expected
    String[] ea = anno.expected().split(",");
    for (String s : ea) {
      if (! s.matches("[0-9]+")) {
        System.err.println("an element in expected is not an int");
        return;
      }
    }
    int[] e = new int[ea.length];
    for (int i = 0; i < ea.length; i++) e[i] = Integer.parseInt(ea[i]);

    // analyse data
    int min = 0;
    if (e.length == p.length) {
      min = e.length;
    } else {
      System.err.println("the number of params is not the same as the number of expected");
      min = Math.min(e.length, p.length);
      System.err.println("processing only "+min+" elements");
    } 

    // output
    for (int i = 0; i < min; i ++) {
      out.println("      assert("+sourceFQClass+"."+funName+"("+p[i]+") == "+e[i]+");");
    }    
  }   
}
