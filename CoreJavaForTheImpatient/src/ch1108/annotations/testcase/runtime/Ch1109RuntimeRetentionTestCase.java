package ch1108.annotations.testcase.runtime;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Ch1109RuntimeRetentionTestCase {

  public static void runTestCases(Class<?> cl) throws Exception {
    Class<TestCase> tc = TestCase.class;
    Method[] methods = cl.getDeclaredMethods();
    for (Method m : methods) {
      String mname = m.getName();
      Class<?>[] paramTypes = m.getParameterTypes();
      String[] paramTypeNames = new String[paramTypes.length];
      for (int i = 0; i < paramTypes.length; i++)
        paramTypeNames[i] = paramTypes[i].toString();
      Class<?> ret = m.getReturnType();
      TestCase[] annos = m.getAnnotationsByType(tc);
      Object obj = cl.newInstance(); // in case m isn't static
      Object[] fparams = new Object[paramTypes.length];
      for (TestCase a: annos) {
        String[] aparams = a.params().split(",");
        if (aparams.length != fparams.length) {
          System.err.println(testCase2String(a)
              +" doesn't have the required number of parameters to execute method "
              +mname);
          System.err.println("skipping processing of "+testCase2String(a));
          continue;
        }
        Object fret = null;
        String which = "";
        // convert a.expected() to type ret = m.getReturnType() and store as fret
        try {
          switch (ret.toString()) {
          case "String"    :  fret = a.expected();
                              which = "String";
                              break;
          case "int"       :  fret = Integer.parseInt(a.expected()); 
                              which = "int";
                              break;        
          case "long"      :  fret = Long.parseLong(a.expected());
                              which = "long";
                              break;
          case "double"    :  fret = Double.parseDouble(a.expected());  
                              which = "double";
                              break;
          case "float"     :  fret = Float.parseFloat(a.expected());
                              which = "float";
                              break;            
          case "byte"      :  fret = Byte.parseByte(a.expected());
                              which = "byte";
                              break;          
          case "short"     :  fret = Short.parseShort(a.expected());
                              which = "short";
                              break;
          case "boolean"   :  fret = Boolean.parseBoolean(a.expected());
                              which = "boolean";
                              break;
          case "char"      :  fret = Character.toChars(a.expected().codePointAt(0))[0];
                              which = "char";
                              break;
          case "Integer"   :  fret = new Integer(a.expected());
                              which = "Integer";
                              break;
          case "Long"      :  fret = new Long(a.expected());
                              which = "Long";
                              break;
          case "Double "   :  fret = new Double(a.expected());
                              which = "Double";
                              break;
          case "Float"     :  fret = new Float(a.expected());
                              which = "Float";
                              break;
          case "Byte"      :  fret = new Byte(a.expected());
                              which = "Byte";
                              break;
          case "Short"     :  fret = new Short(a.expected());
                              which = "Short";
                              break;
          case "Boolean"   :  fret = new Boolean(a.expected());
                              which = "Boolean";
                              break;
          case "Character" :  fret = new Character(Character.toChars(a.expected().codePointAt(0))[0]);
                              which = "Character";
                              break;
          default          :  fret = a.expected();
                              which = "default";
                              break;                                 
          }
        } catch (Exception e) {
          System.out.println("exception "+e.getClass().getName()
              +" while converting expected value "+a.expected()+" to "+which);
          System.out.println("skipping processing of annotation "+testCase2String(a));
          continue;
        }
        
        // convert each String parameter in aparams = a.params().split(",")
        // to its type in paramTypes = m.getParameterTypes() and save
        // converted parameters in fparams
        for (int i = 0; i < paramTypes.length; i++) {
          try {
            which = "";
            switch (paramTypeNames[i]) {
            case "String"    :  fparams[i] = aparams[i]; 
                                which = "String";
                                break;
            case "int"       :  fparams[i] = Integer.parseInt(aparams[i]); 
                                which = "int";
                                break;        
            case "long"      :  fparams[i] = Long.parseLong(aparams[i]);
                                which = "long";
                                break;
            case "double"    :  fparams[i] = Double.parseDouble(aparams[i]);  
                                which = "double";
                                break;
            case "float"     :  fparams[i] = Float.parseFloat(aparams[i]);
                                which = "float";
                                break;            
            case "byte"      :  fparams[i] = Byte.parseByte(aparams[i]);
                                which = "byte";
                                break;          
            case "short"     :  fparams[i] = Short.parseShort(aparams[i]);
                                which = "short";
                                break;
            case "boolean"   :  fparams[i] = Boolean.parseBoolean(aparams[i]);
                                which = "boolean";
                                break;
            case "char"      :  fparams[i] = Character.toChars(aparams[i].codePointAt(0))[0];
                                which = "char";
                                break;
            case "Integer"   :  fparams[i] = new Integer(aparams[i]);
                                which = "Integer";
                                break;
            case "Long"      :  fparams[i] = new Long(aparams[i]);
                                which = "Long";
                                break;
            case "Double "   :  fparams[i] = new Double(aparams[i]);
                                which = "Double";
                                break;
            case "Float"     :  fparams[i] = new Float(aparams[i]);
                                which = "Float";
                                break;
            case "Byte"      :  fparams[i] = new Byte(aparams[i]);
                                which = "Byte";
                                break;
            case "Short"     :  fparams[i] = new Short(aparams[i]);
                                which = "Short";
                                break;
            case "Boolean"   :  fparams[i] = new Boolean(aparams[i]);
                                which = "Boolean";
                                break;
            case "Character" :  fparams[i] = new Character(Character.toChars(aparams[i].codePointAt(0))[0]);
                                which = "Character";
                                break;
            default          :  fparams[i] = aparams[i];
                                which = "default";
                                break;                                 
            }
          } catch (Exception e) {
            System.out.println("exception "+e.getClass().getName()
                +" while converting parameter"+fparams[i]+" to "+which);
            System.out.println("skipping processing of annotation "+testCase2String(a));
            continue;
          }
        }
        
        try { // execute the assertion derived from the annotation and invoking m
          switch (ret.toString()) {
          case "String"    :  assert((String)(m.invoke(obj, fparams)) == (String) fret);
                              break;
          case "int"       :  assert((int)(m.invoke(obj, fparams)) == (int) fret);
                              break;        
          case "long"      :  assert((long)(m.invoke(obj, fparams)) == (long) fret);
//                              long r2 = (long) m.invoke(obj, fparams);
//                              System.out.println("r2 :"+r2);
                              break;
          case "double"    :  assert((double)(m.invoke(obj, fparams)) == (double) fret); 
                              break;
          case "float"     :  assert((float)(m.invoke(obj, fparams)) == (float) fret);
                              break;            
          case "byte"      :  assert((byte)(m.invoke(obj, fparams)) == (byte) fret);
                              break;          
          case "short"     :  assert((short)(m.invoke(obj, fparams)) == (short) fret);
                              break;
          case "boolean"   :  assert((boolean)(m.invoke(obj, fparams)) == (boolean) fret);
                              break;
          case "char"      :  assert((char)(m.invoke(obj, fparams)) == (char) fret);
                              break;
          case "Integer"   :  assert((Integer)(m.invoke(obj, fparams)) == (Integer) fret);
                              break;
          case "Long"      :  assert((Long)(m.invoke(obj, fparams)) == (Long) fret);
                              break;
          case "Double "   :  assert((Double)(m.invoke(obj, fparams)) == (Double) fret);
                              break;
          case "Float"     :  assert((Float)(m.invoke(obj, fparams)) == (Float) fret);
                              break;
          case "Byte"      :  assert((Byte)(m.invoke(obj, fparams)) == (Byte) fret);
                              break;
          case "Short"     :  assert((Short)(m.invoke(obj, fparams)) == (Short) fret);
                              break;
          case "Boolean"   :  assert((Boolean)(m.invoke(obj, fparams)) == (Boolean) fret);
                              break;
          case "Character" :  assert((Character)(m.invoke(obj, fparams)) == (Character) fret);
                              break;
          default          :  assert((String)(m.invoke(obj, fparams)) == (String) fret);
                              break;                                 
          }
        } catch (AssertionError e) {
          System.out.println("assertion failed with params="+Arrays.toString(fparams)
          +" and expected="+fret);
        } catch (Exception e1) {
          System.out.println("exception "+e1.getClass().getName()
              +" while invoking method "+mname+" with parameters "
              +  Arrays.toString(fparams));
          e1.printStackTrace();
        }
      }
    }
  }
    
  public static String testCase2String(TestCase tc) {
    return tc.toString().replaceFirst(".*\\.", "");
  }
  
  public static void main(String[] args) throws Exception {

    // this produces no output when all tests pass
    runTestCases(MyMath.class);

  }

}
