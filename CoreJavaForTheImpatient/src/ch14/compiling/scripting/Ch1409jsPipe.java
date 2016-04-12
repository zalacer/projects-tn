package ch14.compiling.scripting;

//  9. Write a JavaScript function pipe that takes a sequence of shell commands and
//  pipes the output of one to the input of the next, returning the final command’s
//  output. For example, pipe('find .', 'grep -v class', 'sort').
//  Simply call $EXEC repeatedly.

//  On Windows using Rtools utilities and Python scripts in separate examples in place
//  of shell commands.

public class Ch1409jsPipe {

  public static void main(String[] args) {
    
//  // JavaScript function - requires jjs -scripting to use $EXEC
//  function pipe() {
//    var r = '';
//    for (i = 0; i < arguments.length; i++) {
//      r = $EXEC(arguments[i], r);
//    }
//    return r;
//  }
    
// here's an example using pipe() for a find|grep|sort|sed
// pipeline on Windows using Rtools utilities
//  run as: jjs -scripting Ch1409jsPipe.js

//  // /JavaScript/Ch1409jsPipe.js:
//    
//  function pipe() {
//    var r = '';
//    for (i = 0; i < arguments.length; i++) {
//      r = $EXEC(arguments[i], r);
//    }
//    return r;
//  }
//
//  var f = "C:/java/test";
//  var find = "C:/Rtools/bin/find ";
//  var grep = "C:/Rtools/bin/grep ";
//  var gpat = ".class$"
//  var sort = "C:/Rtools/bin/sort";
//  var sed =  "C:/Rtools/bin/sed " ;
//  var sedopt = '-e \"s/^\\\\.\\\\///\" ';
//
//  $ENV.PWD = f;
//
//  var r = pipe(find, grep+gpat, sort, sed+sedopt);
//  print(r);

//  here is another example of using pipe() for a pipeline of
//  simple Python scripts whose cumulative output results in
//  "one and one is two"
    
    // Python start.py script (in JavaScript/start.py)
//  print("one")
    
    // Python relay.py script (in JavaSccript/relay.py)
//  import sys
//
//  l = ''
//  for line in sys.stdin:
//    l = line.strip()
//    
//  x = l.split()
//  if (len(x)==0): print(l + ' ' + 'none')
//  if (len(x)==1): print(l + ' ' + 'and')
//  if (len(x)==2): print(l + ' ' + 'one')
//  if (len(x)==3): print(l + ' ' + 'is')
//  if (len(x)==4): print(l + ' ' + 'two')
    
    // JavaScript pipeline
//  pipe('python start.py', 'python relay.py', 'python relay.py', 'python relay.py', 'python relay.py')
    
    // JavaScript transcript (jjs -scripting (interactive shell))
//  jjs> function pipe() { var r = ''; for (i = 0; i < arguments.length; i++) { r = $EXEC(arguments[i], r); } return r; }
//  jjs> pipe('python start.py', 'python relay.py',  'python relay.py', 'python relay.py', 'python relay.py')
//  (output)
//  one and one is two

  }

}
