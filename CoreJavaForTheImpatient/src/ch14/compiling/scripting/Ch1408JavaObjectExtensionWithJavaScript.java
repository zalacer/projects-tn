package ch14.compiling.scripting;

//  8. At the end of Section 14.3.9, “Extending Java Classes and Implementing Java
//  Interfaces,” on p. 435, you saw how to extend ArrayList so that every call to
//  add is logged. But that only worked for a single object. Write a JavaScript function
//  that is a factory for such objects, so that you can generate any number of logging
//  array lists.

public class Ch1408JavaObjectExtensionWithJavaScript {

  public static void main(String[] args) {
    
//  extended ArrayList Factory function:
//  function createNewArrayListWithAddLogging() {
//    var arr = new (Java.extend(java.util.ArrayList)) {
//      add: function(x) {
//        print('Adding ' + x);
//        return Java.super(arr).add(x)
//      }
//    };
//    return arr;
//  }
    
//  JavaScript transcript:
//  jjs> function getNewArrayListWithAddLogging () { var arr = new (Java.extend(java.util.ArrayList)) { add: function(x) { print('Adding ' + x); return Java.super(arr).add(x) } }; return arr }
//  jjs> var newArrayListWithAddLogging1 = getNewArrayListWithAddLogging()
//  jjs> newArrayListWithAddLogging1.add('hello')
//  Adding hello
//  true
//  jjs> newArrayListWithAddLogging1
//  [hello]
//  jjs> var newArrayListWithAddLogging2 = getNewArrayListWithAddLogging()
//  jjs> newArrayListWithAddLogging2.add('how now brown cow?')
//  Adding how now brown cow?
//  true
//  jjs> newArrayListWithAddLogging2
//  [how now brown cow?]
//  jjs> var newArrayListWithAddLogging3 = getNewArrayListWithAddLogging()
//  jjs> newArrayListWithAddLogging3.add('three')
//  Adding three
//  true
//  jjs> newArrayListWithAddLogging3.add('score')
//  Adding score
//  true
//  jjs> newArrayListWithAddLogging3.add('and')
//  Adding and
//  true
//  jjs> newArrayListWithAddLogging3.add('seven')
//  Adding seven
//  true
//  jjs> newArrayListWithAddLogging3
//  [three, score, and, seven, years, ago]
    
  }

}
