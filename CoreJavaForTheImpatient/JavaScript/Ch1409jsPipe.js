
function pipe() {
  var r = '';
  for (i = 0; i < arguments.length; i++) {
    r = $EXEC(arguments[i], r);
  }
  return r;
}


var f = "C:/java/test";
var find = "C:/Rtools/bin/find ";
var grep = "C:/Rtools/bin/grep ";
var gpat = ".class$"
var sort = "C:/Rtools/bin/sort";
var sed =  "C:/Rtools/bin/sed " ;
var sedopt = '-e \"s/^\\\\.\\\\///\" ';

$ENV.PWD = f;

var r = pipe(find, grep+gpat, sort, sed+sedopt);
print(r);

