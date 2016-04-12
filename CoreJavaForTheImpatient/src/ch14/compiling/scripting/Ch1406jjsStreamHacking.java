package ch14.compiling.scripting;

//  6. Run jjs and, using the stream library, interactively work out a solution for the
//  following problem: Print all unique long words (> 12 characters) from a file in
//  sorted order. First read the words, then filter the long words, and so on. How does
//  this interactive approach compare to your usual workflow?

// I find using jjs is quite cumbersome particularly regarding Java stream pipelines, 
// since the entire pipeline has to be put on one line, at least with the cmd.exe interface 
// in Windows. Also unfortunately. Java Patterns can't be used as regexes in Nashorn, so 
// expertise with their nuances is wasted when using JavaScript. 
    
public class Ch1406jjsStreamHacking {

  public static void main(String[] args) {
    
//  JavaScript transcript:
//  jjs> var file = 'AlicesAdventuresInWonderland3339.txt'
//  jjs> var path = java.nio.file.Paths.get(file)
//  jjs> var sc = java.util.Scanner
//  jjs> var input = new sc(path)
//  jjs> input.useDelimiter('$')
//  jjs> var contents = input.next()
//  jjs> var words = Java.to(contents.split(/\s+/), Java.type('java.lang.String[]'))
//  jjs> var long = java.util.Arrays.stream(words).map(function(w) w.toLowerCase().replace(/[.,!?:;'"]+/g, '')).distinct().filter(function(w) w.length > 12).toArray()
//  jjs> long.length
//  57 
//  jjs> java.util.Arrays.sort(long)
//  jjs> for each (var e in long) print(e)
//  adventures--beginning
//  affectionately
//  again--before
//  arithmetic--ambition
//  back-somersault
//  beau--ootiful
//  bitter--and--and
//  bread-and-butter
//  bread-and-butter--
//  chrysalis--you
//  circumstances
//  contemptuously
//  conversations
//  croquet-ground
//  cross-examine
//  cucumber-frame
//  cucumber-frames
//  disappointment
//  distance--but
//  drawling--the
//  drawling-master
//  e--e--evening
//  educations--in
//  execution--once
//  extraordinary
//  farm-yard--while
//  first--verdict
//  good-naturedly
//  important--unimportant--unimportant--important--
//  inquisitively
//  jack-in-the-box
//  looking-glass
//  means--to--make--anything--prettier
//  muchness--did
//  muchness--you
//  multiplication
//  northumbria--
//  ointment--one
//  out-of-the-way
//  particular--here
//  queer-looking
//  questions--how
//  rabbit-hole--and
//  seen--everything
//  shingle--will
//  simply--never
//  sisters--they
//  straightening
//  sweet-tempered
//  things--everything
//  treacle-well--eh
//  uncomfortable
//  uncomfortably
//  waistcoat-pocket
//  washing--extra
//  without--maybe
//  writing-desks


    
  }

}
