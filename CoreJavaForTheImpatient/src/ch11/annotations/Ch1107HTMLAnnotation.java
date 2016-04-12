package ch11.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static utils.StringUtils.replaceLast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;

import utils.CharsetToolkit;

//7. If annotations had existed in early versions of Java, they might have taken the role
//of Javadoc. Define annotations @Param, @Return, and so on, and produce a basic
//HTML document from them with an annotation processor.

// I implemented 5 annotations two of which are repeating for a total of 7.
// They are all defined in this class together with an assortment of methods
// that are annotated with them and I used this class for testing.
// All the annotations have runtime retention so its easy to test them 
// interactively, and they all target METHOD only. The method that processes 
// them is called docMethod2HTML and is just before main near the bottom. In
// the documents it produces, all the annotations used to enable this are
// omitted from method signatures. It works well and picks up generic types, 
// however reflection doesn't detect annotations with source retention. That 
// could be remedied by scanning the source with an AST parser such as JavaParse. 
// Example output is located at the bottom after class Ch1107HTMLAnnotation and 
// in the folder named Ch1107HTMLAnnotation at the top level in this project.

// Breifly the annotations and their usage is as follows:
//  1. @Doc - is required for any of the other annotations to be processed
//     by docMethod2HTML and it takes a string parameter named info which
//     is meant to briefly describe a method in one line
//  2. @Param is a repeated annotation to invoked for each method parameter.
//     It takes an String parameter named info which should be in
//     "parameterName : parameterDescription" format.  If a parameter x is
//     not annotated with @Params then docMethod2HTML prints 
//     "x - the nth parameter of givenMethod method"
//     for the appropriate integer n according to the method's signature
//  3. @Params simply encapsulates a Param[]
//  4. @Return enables documentation of a customized comment for a method.
//     If it's missing docMethod2HTML prints only the return type.
//  5. @Xception is a repeated annotation for documenting declared method
//     exceptions with customized comments. It has a String info parameter that
//     should be in the format "fully.qualified.exception.name : comment".
//  6. @Xceptions simply encapsulates an @Xception[];
//  7. @See allows adding one piece of reference info. If its a url beginning
//     with "http://" or "https://" it's documented with a clickable href. This 
//     could be repeating so multiple references could be added.
//  Example usage of these annotations is shown below.

public class Ch1107HTMLAnnotation {
    
    private static int count = 1;
    final static String lineSep = System.getProperty("line.separator");
    
    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface Doc {
        String info() default "";
    }
        
    @Target(METHOD)
    @Retention(RUNTIME)
    @Repeatable(Params.class)
    public @interface Param {
        String info() default "";
    }

    @Target(METHOD)
    @Retention(RUNTIME)
    @interface Params {
        Param[] value();
    }
    
    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface Return {
        String info() default "";
    }
    
    @Target(METHOD)
    @Retention(RUNTIME)
    @Repeatable(Xceptions.class)
    public @interface Xception {
        String info() default "";
    }
    
    @Target(METHOD)
    @Retention(RUNTIME)
    @interface Xceptions {
        Xception[] value();
    }
    
    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface See {
        String info() default "";
    }
    
    @Doc
    @See(info = "https://docs.oracle.com/javase/8/docs/api/java/lang/InterruptedException.html")
    public static int factor(int a, final int b, int c) throws InterruptedException {
        int j = 19;
        return count * j * a * b / c;
    }   
    
    @Doc(info = "this method calculates force based on Newton's formula")
    @Param(info = "m : represents mass")
    @Param(info = "a : represents acceleration")
    @Return(info = "the force")
    public static double force(double m, double a) {
        return m * a;
    }
    
    @Doc(info = "this method merges the maps in an array of maps")
    @Param(info = "biFun : bifunction for insertion into Map.merge")
    @Param(info = "maps : array of Map<K,V> to merge")
    @Return(info = "the map formed by merging all the maps")
    @SafeVarargs
    public static <K,V> Map<K,V> mapMerge(
            BiFunction<? super V,? super V,? extends V> biFun, Map<K,V>...maps) {
        if (maps.length == 0) return Collections.emptyMap();
        if (maps.length == 1) return maps[0];
        for (int i = 1; i < maps.length; i++)
            for (Entry<K,V> e : maps[i].entrySet())
                maps[0].merge(e.getKey(), e.getValue(), biFun);
        return maps[0];
    }
    
    public static Charset getDefaultSystemCharset() {
        return Charset.forName(System.getProperty("file.encoding"));
    }
    
    @Doc
    @Xception(info = "java.io.FileNotFoundException : file may not exist")
    @Xception(info = "java.io.IOException : file may not be readable")
    public static Charset guessEncoding(File f, int bufferLength) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(f);
        byte[] buffer = new byte[bufferLength];
        fis.read(buffer);
        fis.close();
        CharsetToolkit toolkit = new CharsetToolkit(buffer);
        toolkit.setDefaultCharset(getDefaultSystemCharset());
        return toolkit.guessEncoding();
    }
       
    public static Map<Integer,String> ordinal = new HashMap<Integer,String>() {
        private static final long serialVersionUID = 1L;
        { put(1,"first"); put(2,"second"); put(3,"third");     put(4,"fourth");
          put(5,"fifth"); put(6,"sixth");  put(7,"seventh");   put(8,"eighth");
          put(9,"ninth"); put(10,"tenth"); put(11,"eleventh"); put(12,"twelvth"); 
        }
    };
    
    @Doc
    public static String docMethod2HTML(Class<?> c)  {
        String cname = c.getName();
        Method[] methods = c.getDeclaredMethods();
        
        // output HTML header, styling, etc.
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"+lineSep);
        builder.append("<html lang=\"en\">"+lineSep);
        builder.append("<head>"+lineSep);
        builder.append("<title>Ch1107HTMLAnnotation.docMethod2HTML Demo</title>"+lineSep);
        builder.append("</head>"+lineSep);
        builder.append("<body>"+lineSep);
        builder.append("<style>"+lineSep);
        builder.append("li"+lineSep);
        builder.append("{"+lineSep);
        builder.append("    list-style-type: none;"+lineSep);
        builder.append("}"+lineSep);
        builder.append("h4 {"+lineSep);
        builder.append("   width: 100%;"+lineSep); 
        builder.append("   text-align: left;"+lineSep); 
        builder.append("   border-bottom: 25px solid #F0F0F0;"+lineSep); 
        builder.append("   border-top: 25px solid #F0F0F0;"+lineSep);
        builder.append("   line-height: 0.0em;"+lineSep);
        builder.append("   margin: 10px 0 20px;"+lineSep) ;
        builder.append("}"+lineSep); 
        builder.append("h4 span {"+lineSep);
        builder.append("    background:#fff"+lineSep); 
        builder.append("    padding:0 10px;"+lineSep); 
        builder.append("}"+lineSep);
        builder.append("</style>"+lineSep);
        builder.append("<center><h2>Ch1107HTMLAnnotation.docMethod2HTML Demo</h2></center>"+lineSep);
 
        // loop over the methods Class<?> c and select and process only those
        // with an @Doc annotation
        for (Method m : methods) {
               // Doc annotation and method name
            if (m.isAnnotationPresent(Doc.class)) {
                Doc doc = m.getAnnotation(Doc.class);
                String docString = doc.info();
                String name = m.getName();
                
                // Return type and annotation
                String returnType = m.getGenericReturnType().getTypeName();
                String retString = "";
                if (m.isAnnotationPresent(Return.class)) {
                    Return ret = m.getAnnotation(Return.class);
                    retString = ret.info();
                }
                
                // parameters
                boolean haveParamAnno = false;
                Param[] paramAnnosArray = m.getAnnotationsByType(Param.class);
                if (paramAnnosArray.length > 0) haveParamAnno = true;
                Map<String,String>  paramAnnos = new HashMap<>();
                if (haveParamAnno) {
                    for (Param p : paramAnnosArray) {
                        String[] pa = p.info().split(":");
                        paramAnnos.put(pa[0].trim(), pa[1].trim());
                    }
                }
                boolean haveParam = false;
                Parameter[] params = m.getParameters();
                if (params.length > 0) haveParam = true;
                Set<String> paramNames = new HashSet<>();
                if (params.length > 0) {
                    for (Parameter p : params) paramNames.add(p.getName());
                }
                String paramString = Arrays.toString(params);
                paramString = paramString.replace("[", "(").replace("]", ")");
                
                // method signature
                String sig = m.toGenericString();
                sig = replaceLast(sig, "\\(.*\\)", paramString)
                         .replaceFirst(cname+"\\."+name, name);
                
                // exceptions
                boolean haveXceptionAnno = false;
                Xception[] xceptionAnnosArray = m.getAnnotationsByType(Xception.class);
                if (xceptionAnnosArray.length > 0) haveXceptionAnno = true;
                Map<String,String> xceptionAnnos = new HashMap<>();
                if (haveXceptionAnno) {
                    for (Xception e : xceptionAnnosArray) {
                        String[] ea = e.info().split(":");
                        xceptionAnnos.put(ea[0].trim(), ea[1].trim());
                    }
                }                
                boolean haveException = false;
                Class<?>[] exceptionTypes = m.getExceptionTypes();
                if (exceptionTypes.length > 0) haveException = true;
                Set<String> exceptionNames = new HashSet<>();
                if (exceptionTypes.length > 0) {
                    for (Class<?> cl : exceptionTypes) 
                        exceptionNames.add(cl.getName());       
                }
                
                // See
                boolean haveSeeAnno = false;
                See[] seeAnnosArray = m.getAnnotationsByType(See.class);
                if (seeAnnosArray.length > 0) haveSeeAnno = true;
                
                // other annotations
                Annotation[] annotations = m.getDeclaredAnnotations();
                List<String> annos = new ArrayList<>();
                for (Annotation a : annotations) {
                    if (a.annotationType() != Doc.class 
                            && a.annotationType() != Param.class
                            && a.annotationType() != Params.class
                            && a.annotationType() != Return.class
                            && a.annotationType() != Xception.class
                            && a.annotationType() != Xceptions.class
                            && a.annotationType() != See.class) {                     
                        String astring = "@" + a.toString()
                                            .replace("()", "")
                                            .replaceFirst(".*\\$", "")
                                            .replaceFirst(".*\\.", "");
                        annos.add(astring);
                    }
                }
  
                // output method name
                builder.append("<ul class=\"blockList\">"+lineSep);
                builder.append("<li class=\"blockList\">"+lineSep);
                builder.append("<h4>&nbsp;&nbsp;"+name+"</h4>"+lineSep);
                
                // output method signature
                builder.append("<pre>"+lineSep);
                for (String a : annos) builder.append(a+lineSep);
                builder.append(escapeHtml4(sig)+lineSep);
                builder.append("</pre>"+lineSep);
                
                // output docString - method synopsis
                if (docString.length() > 0) {
                    builder.append("<div class=\"block\">"+lineSep);
                    builder.append(docString+lineSep);
                    builder.append("</div>"+lineSep);
                }
                
                // output parameters
                if (haveParam) {
                    builder.append("<dl>"+lineSep);
                    builder.append("<dt><span class=\"paramLabel\">Parameters:</span></dt>"+lineSep);
                    int d = 1;
                    for (Parameter p : params) {
                        String pname = p.getName();
                        if (haveParamAnno && paramAnnos.containsKey(pname)) {
                            //System.out.println(pname+" - "+paramAnnos.get(pname));
                            builder.append("<dd><code>"+pname+"</code> - "
                                    +paramAnnos.get(pname)+"</dd>"+lineSep);
                        } else {
                            //System.out.println(pname+" is the "+ordinal.get(d)
                            //+" parameter of the "+name+" method");
                            builder.append("<dd><code>"+pname+"</code> - is the "
                                    +ordinal.get(d)+" parameter of the "+name+" method</dd>"+lineSep);
                        }
                        d++;
                    }
                }
                
                // output exceptions
                if (haveException) {
                    builder.append("<dt><span class=\"throwsLabel\">Throws:</span></dt>"+lineSep);
                    for (Class<?> cl : exceptionTypes) {
                        String ename = cl.getName();
                        if (haveXceptionAnno && xceptionAnnos.containsKey(ename)) {
                            //System.out.println(ename+" - "+xceptionAnnos.get(ename));
                            builder.append("<dd><code>"+ename+"</code> - "
                                    +xceptionAnnos.get(ename)+"</dd>"+lineSep);
                        } else {
                            //System.out.println(ename);
                            builder.append("<dd><code>"+ename+"</code>"+"</dd>"+lineSep);
                        }
                    }
                }
                
                // output return info or code
                builder.append("<dt><span class=\"returnLabel\">Returns:</span></dt>"+lineSep);
                if (retString != "") {
                    //System.out.println(retString);
                    builder.append("<dd>"+retString+"</dd>"+lineSep);
                } else {
                    //System.out.println(returnType);
                    builder.append("<dd>"+returnType+"</dd>"+lineSep);
                }
                
                // output see also
                if (haveSeeAnno) {
                    builder.append("<dt><span class=\"seeLabel\">See Also:</span></dt>"+lineSep);
                    for (See see : seeAnnosArray) {
                        String seeInfo = see.info();
                        //System.out.println(seeInfo);
                        if (seeInfo.startsWith("http://") || seeInfo.startsWith("https://")) {
                            String seeURI = seeInfo.replaceFirst(".*//", "");
                            seeURI = seeURI.replaceFirst("\\.[^.]*$", "");
                            builder.append("<dd><a href=\""+seeInfo+"\">"+seeURI+"</a></dd>"+lineSep);
                        } else {
                            builder.append("<dd><code>"+seeInfo+"</code></dd>"+lineSep);
                        }
                    }
                }
                // close list formatting
                builder.append("</dl>"+lineSep);
                builder.append("</li>"+lineSep);
                builder.append("</ul>"+lineSep);
            }
        }
        // close HTML body and html tags
        builder.append("</body>"+lineSep);
        builder.append("</html>"+lineSep);
        
        return builder.toString();
    }
   
    public static void main(String[] args)  {
        String methodsDocs = docMethod2HTML(Ch1107HTMLAnnotation.class);
        System.out.println(methodsDocs);              
    }
}

// example output (also in the Ch1107HTMLAnnotation folder at the top level of this project)

//  <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
//  <html lang="en">
//  <head>
//  <title>Ch1107HTMLAnnotation.docMethod2HTML Demo</title>
//  </head>
//  <body>
//  <style>
//  li
//  {
//      list-style-type: none;
//  }
//  h4 {
//     width: 100%;
//     text-align: left;
//     border-bottom: 25px solid #F0F0F0;
//     border-top: 25px solid #F0F0F0;
//     line-height: 0.0em;
//     margin: 10px 0 20px;
//  }
//  h4 span {
//      background:#fff
//      padding:0 10px;
//  }
//  </style>
//  <center><h2>Ch1107HTMLAnnotation.docMethod2HTML Demo</h2></center>
//  <ul class="blockList">
//  <li class="blockList">
//  <h4>&nbsp;&nbsp;force</h4>
//  <pre>
//  public static double force(double m, double a)
//  </pre>
//  <div class="block">
//  this method calculates force based on Newton's formula
//  </div>
//  <dl>
//  <dt><span class="paramLabel">Parameters:</span></dt>
//  <dd><code>m</code> - represents mass</dd>
//  <dd><code>a</code> - represents acceleration</dd>
//  <dt><span class="returnLabel">Returns:</span></dt>
//  <dd>the force</dd>
//  </dl>
//  </li>
//  </ul>
//  <ul class="blockList">
//  <li class="blockList">
//  <h4>&nbsp;&nbsp;factor</h4>
//  <pre>
//  public static int factor(int a, final int b, int c) throws java.lang.InterruptedException
//  </pre>
//  <dl>
//  <dt><span class="paramLabel">Parameters:</span></dt>
//  <dd><code>a</code> - is the first parameter of the factor method</dd>
//  <dd><code>b</code> - is the second parameter of the factor method</dd>
//  <dd><code>c</code> - is the third parameter of the factor method</dd>
//  <dt><span class="throwsLabel">Throws:</span></dt>
//  <dd><code>java.lang.InterruptedException</code></dd>
//  <dt><span class="returnLabel">Returns:</span></dt>
//  <dd>int</dd>
//  <dt><span class="seeLabel">See Also:</span></dt>
//  <dd><a href="https://docs.oracle.com/javase/8/docs/api/java/lang/InterruptedException.html">docs.oracle.com/javase/8/docs/api/java/lang/InterruptedException</a></dd>
//  </dl>
//  </li>
//  </ul>
//  <ul class="blockList">
//  <li class="blockList">
//  <h4>&nbsp;&nbsp;mapMerge</h4>
//  <pre>
//  @SafeVarargs
//  public static &lt;K,V&gt; java.util.Map&lt;K, V&gt; mapMerge(java.util.function.BiFunction&lt;? super V, ? super V, ? extends V&gt; biFun, java.util.Map&lt;K, V&gt;... maps)
//  </pre>
//  <div class="block">
//  this method merges the maps in an array of maps
//  </div>
//  <dl>
//  <dt><span class="paramLabel">Parameters:</span></dt>
//  <dd><code>biFun</code> - bifunction for insertion into Map.merge</dd>
//  <dd><code>maps</code> - array of Map<K,V> to merge</dd>
//  <dt><span class="returnLabel">Returns:</span></dt>
//  <dd>the map formed by merging all the maps</dd>
//  </dl>
//  </li>
//  </ul>
//  <ul class="blockList">
//  <li class="blockList">
//  <h4>&nbsp;&nbsp;guessEncoding</h4>
//  <pre>
//  public static java.nio.charset.Charset guessEncoding(java.io.File f, int bufferLength) throws java.io.FileNotFoundException,java.io.IOException
//  </pre>
//  <dl>
//  <dt><span class="paramLabel">Parameters:</span></dt>
//  <dd><code>f</code> - is the first parameter of the guessEncoding method</dd>
//  <dd><code>bufferLength</code> - is the second parameter of the guessEncoding method</dd>
//  <dt><span class="throwsLabel">Throws:</span></dt>
//  <dd><code>java.io.FileNotFoundException</code> - file may not exist</dd>
//  <dd><code>java.io.IOException</code> - file may not be readable</dd>
//  <dt><span class="returnLabel">Returns:</span></dt>
//  <dd>java.nio.charset.Charset</dd>
//  </dl>
//  </li>
//  </ul>
//  <ul class="blockList">
//  <li class="blockList">
//  <h4>&nbsp;&nbsp;docMethod2HTML</h4>
//  <pre>
//  public static java.lang.String docMethod2HTML(java.lang.Class&lt;?&gt; c)
//  </pre>
//  <dl>
//  <dt><span class="paramLabel">Parameters:</span></dt>
//  <dd><code>c</code> - is the first parameter of the docMethod2HTML method</dd>
//  <dt><span class="returnLabel">Returns:</span></dt>
//  <dd>java.lang.String</dd>
//  </dl>
//  </li>
//  </ul>
//  </body>
//  </html>




