package ch14.compiling.scripting;

//  5. Pick some part of the Java API that you want to explore—for example, the
//  ZonedDateTime class. Run some experiments in jjs: construct objects, call
//  methods, and observe the returned values. Did you find it easier than writing test
//  programs in Java?

// I find easier and more useful to write test programs in Java using Eclipse, and 
// do it frequently. An advantage of that is it puts me closer to the finish line of
// a completed program than by using JavaScript. I have also found the Scala REPL 
// to be useful for Java API twiddling in more interesting ways.
    
public class Ch1405jjsHackJavaAPI {

  public static void main(String[] args) {
    
//  JavaScript transcript:
//  jjs> var zdt = java.time.ZonedDateTime
//  jjs> var now = zdt.now()
//  jjs> now
//  2016-01-13T20:28:56.869-05:00[America/New_York]
//  jjs> var nowPlusOneHour = now.plusHours(1)
//  jjs> nowPlusOneHour
//  2016-01-13T21:28:56.869-05:00[America/New_York]


  }

}
