package ch11.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

//5. Define an annotation @Todo that contains a message describing whatever it is that
//needs to be done. Define an annotation processor that produces a reminder list from
//a source file. Include a description of the annotated item and the todo message.

// This solution includes an inner Todo annotation and example of processing it on an
// inner test class with annotations for type, constructor, field, method, parameter
// and local variable. Annotation location processing is mostly implemented with 
// reflection. The exception is for local variables that reflection can't access and 
// for which JavaParser was used. The annotation messages identify the annotation 
// location and do not represent usual todo content, although in fact I used them as 
// such for debugging. The purpose is to demonstrate the capability of instrumenting
// annotations for producing messages for all class components which may need fixing
// or enhancment or for other purposes such as doing this exercise.

public class Ch1105ToDoAnnotation {

  @Target({TYPE,
    CONSTRUCTOR,
    FIELD,
    METHOD,
    PARAMETER,
    LOCAL_VARIABLE})
  @Retention(RUNTIME)
  public @interface Todo {
    String msg() default "default";
  }

  @Todo(msg = "D1 type message") public static class D1 {
    boolean done = false;
    @Todo(msg = "D1 field message") int count = 0;

    public D1() {
      super();
    }

    @Todo(msg = "D1 constructor message") 
    public D1(@Todo(msg = "D1 constructor parameter message") boolean done, int count) {
      this.done = done;
      this.count = count;
    }

    @Todo(msg = "D1 method message") public int factor(int a, int b, 
        @Todo(msg = "D1 method parameter message") int c) {
      @Todo(msg = "D1 local variable message") int j = 19;
      return count * j * a * b * c;
    }       
  }

  private static class VariableVisitor extends VoidVisitorAdapter<Object> {
    List<String> r = new ArrayList<>();
    String[] result = null;
    @Override
    public void visit(VariableDeclarationExpr n, Object arg) {
      if (n.getAnnotations() != null) {
        for (AnnotationExpr ae : n.getAnnotations()) {
          if (ae.getName().toString().equals("Todo")) {
            // for a Todo annotation ae.toString() is of the form
            // @Todo(msg = "message contents")
            // using String.replaceFirst to extract message contents
            String msg = ae.toString().replaceFirst(".*?\"", "")
                .replaceFirst("\".*$", "");
            List<VariableDeclarator> vars = n.getVars();
            for (VariableDeclarator v : vars) {                            
              r.add(v.getId().getName()+": "+msg);
            }
          }
        }
      }
      super.visit(n, arg);
      result = r.toArray(new String[0]);
    }
  }

  public static String[] inspectJavaFile(File pFile) 
      throws FileNotFoundException, ParseException, IOException {
    CompilationUnit cu;
    FileInputStream in = new FileInputStream(pFile);
    try {
      cu = JavaParser.parse(in);
    } finally {
      in.close();
    }
    VariableVisitor vv = new VariableVisitor();
    vv.visit(cu, null);
    return vv.result;
  }

  public static String generateTodoList(Class<?> c) throws Exception {
    Class<Todo> anno =  Todo.class;
    String cname = c.getSimpleName();
    StringBuilder b = new StringBuilder();
    Todo todo = null;
    // class annotation
    todo = c.getAnnotation(anno);
    if (todo != null) {
      b.append(cname+": "+todo.msg());
    }
    // constructor annotations
    Constructor<?>[] constructors = c.getConstructors();
    for (Constructor<?> con : constructors) {
      todo = con.getAnnotation(anno);
      if (todo != null) {
        b.append("\n"+cname+" constructor: "+todo.msg());
      }
      Parameter[] params = con.getParameters();
      for (Parameter p : params) {
        todo = p.getAnnotation(anno);
        if (todo != null) {
          b.append("\n"+cname+" constructor parameter "+p.getName()+": "+todo.msg());
        }
      }
    }
    // field annotations
    Field[] fields = c.getDeclaredFields();
    for (Field f : fields) {
      todo = f.getAnnotation(anno);
      if (todo != null) {
        b.append("\n"+cname+"."+f.getName()+": "+todo.msg());
      }
    }
    // method annotations
    Method[] methods = c.getDeclaredMethods();
    for (Method m : methods) {
      todo = m.getAnnotation(anno);
      if (todo != null) {
        b.append("\n"+cname+"."+m.getName()+": "+todo.msg());
      }
      Parameter[] params = m.getParameters();
      for (Parameter p : params) {
        todo = p.getAnnotation(anno);
        if (todo != null) {
          b.append("\n"+cname+"."+m.getName()+"."+p.getName()+": "+todo.msg());
        }
      }
    }
    // local variable annotations
    // customize the value of f with the pathname of this java source file on your system
    File f = new File("C:\\java\\eclipsemars1\\CJFTI\\src\\ch11\\annotations\\Ch1105ToDoAnnotation.java");
    String[] localVars = inspectJavaFile(f);
    for (String localVarInfo  : localVars) 
      b.append("\n"+cname+" local variable "+localVarInfo);
    
    return b.toString();
  }

  public static void main(String[] args) throws Exception {

    System.out.println(generateTodoList(D1.class));
    //  D1: D1 type message
    //  D1 constructor: D1 constructor message
    //  D1 constructor parameter done: D1 constructor parameter message
    //  D1.count: D1 field message
    //  D1.factor: D1 method message
    //  D1.factor.c: D1 method parameter message
    //  D1 local variable j: D1 local variable message

  }

}

