package ch14.compiling.scripting;

//  12. Write a script nextYear.js that obtains the age of the user and then prints Next
//  year, you will be..., adding 1 to the input. The age can be specified on the
//  command line or in the AGE environment variable. If neither are present, prompt the
//  user.

// The script is JavaScript/Ch1412ScriptPrintAgeNextYear.js and it contents are included
// below as well.

public class Ch1412ScriptPrintAgeNextYear {

  public static void main(String[] args) {

    // contents of  JavaScript/Ch1412ScriptPrintAgeNextYear.js
    //  // with an arg such as 19 run as 
    //  //  jjs -scripting Ch1412ScriptPrintAgeNextYear.js -- 19
    //  if (arguments.length > 0 && arguments[0].match(/^\d+$/)) {
    //   print(Number(arguments[0]) + 1);
    //  } else {
    //   var System = Java.type("java.lang.System");
    //   if (System.env.containsKey("AGE") && System.env.get("AGE").match(/^\d+$/)) {
    //     print(Number(System.env.get("AGE")) + 1);
    //   } else {
    //     var age = readLine("What is your age? ");
    //     while (true) {
    //       if (age.match(/^\d+$/)) {
    //         print(Number(age) + 1);
    //         break;
    //       } else {
    //         age = readLine("Try again. What is your age? (enter a number): ");
    //       }
    //       
    //     }
    //   }
    //  }
    //  exit();

  }

}
