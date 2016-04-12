package ch14.compiling.scripting;

// 11. Write a script that prints the values of all environment variables.

public class Ch1411ScriptPrintEnvVars {

  public static void main(String[] args) {
    
    // JavaScript script to print all environment variables and their values
    // script pathname: JavaScript/Ch1411ScriptPrintEnvVars.js
//  for (entry in $ENV) {print(entry + " -> " + $ENV[entry]);}
    
    // using Java explicitly in JavaScript
//  var System  = Java.type("java.lang.System");
//  for each (e in System.env.entrySet()) { print(e.key, " -> ", e.value) };
    
    // by calling an external Windows command in JavaScript
//  $EXEC("cmd.exe /C set")
    

  }

}
