
// with an arg such as 19 run as 
//   jjs -scripting Ch1412ScriptPrintAgeNextYear.js -- 19
if (arguments.length > 0 && arguments[0].match(/^\d+$/)) {
  print(Number(arguments[0]) + 1);
} else {
  var System = Java.type("java.lang.System");
  if (System.env.containsKey("AGE") && System.env.get("AGE").match(/^\d+$/)) {
    print(Number(System.env.get("AGE")) + 1);
  } else {
    var age = readLine("What is your age? ");
    while (true) {
      if (age.match(/^\d+$/)) {
        print(Number(age) + 1);
        break;
      } else {
        age = readLine("Try again. What is your age? (enter a number): ");
      }
      
    }
  }
}
exit();