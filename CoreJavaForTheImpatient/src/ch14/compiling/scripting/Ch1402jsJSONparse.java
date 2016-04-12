package ch14.compiling.scripting;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

//  2. From a Java program, call the JavaScript JSON.parse method to turn a JSON-
//  formatted string into a JavaScript object, then turn it back into a string.
//  Do this (a) with eval, (b) with invokeMethod, (c) by a Java method call through
//  the interface
//    public interface JSON {
//      Object parse(String str);
//      String stringify(Object obj);
//    }

public class Ch1402jsJSONparse {
  
  public interface JSON {
    Object parse(String str);
    String stringify(Object obj);
  }    

  public static void main(String[] args) throws ScriptException, NoSuchMethodException {
    
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("nashorn");
    Invocable invocable = (Invocable) engine;
    
    String jstr = "{\"@type\":\"Employee\",\"firstName\":\"margie\",\"lastName\":\"fessler\"}";    
    
    // using engine.eval but no explicit invokeMethod or Java interface
    ScriptObjectMirror json1 = (ScriptObjectMirror) engine.eval("JSON");
    Object result = json1.callMember("parse", jstr); //[object Object]
    System.out.println(json1.callMember("stringify", result));
    //{"@type":"Employee","firstName":"margie","lastName":"fessler"}
    
    // using invokeMethod 
    Object json2 = engine.eval("JSON");
    result  = invocable.invokeMethod(json2, "parse", jstr); //[object Object]
    System.out.println(invocable.invokeMethod(json2, "stringify", result));
    //{"@type":"Employee","firstName":"margie","lastName":"fessler"}

    // using  Java interface
    JSON json3 = invocable.getInterface(engine.eval("JSON"), JSON.class);
    result = json3.parse(jstr); //[object Object]
    System.out.println(json3.stringify(result)); 
    //{"@type":"Employee","firstName":"margie","lastName":"fessler"}
    

  }

}
