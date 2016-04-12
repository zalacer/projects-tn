
// Ch1410jsProcessBuilderEnhancement.js

var cmd1 = java.util.Arrays.asList("C:/Rtools/bin/find");
var cmd2 = java.util.Arrays.asList("C:/Rtools/bin/grep","\".class$\"");
var cmd3 = java.util.Arrays.asList("C:/Rtools/bin/sort");
var cmd4 = java.util.Arrays.asList("C:/Rtools/bin/sed","-e \"s/^\\\\.\\\\///\"");
var cmd =  java.util.Arrays.asList(cmd1,cmd2,cmd3,cmd4);

var dir = "C:/Users/tn/Documents/vulab/java/test";

Packages.ch14.compiling.scripting.Ch1410jsProcessBuilderEnhancement.pipe(cmd, dir);

