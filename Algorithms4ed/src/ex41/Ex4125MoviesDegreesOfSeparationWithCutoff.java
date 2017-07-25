package ex41;

import graph.DegreesOfSeparationWithCutoff;

/* p560
  4.1.25  Modify  DegreesOfSeparation to take an  int value y as a command-
  line argument and ignore movies that are more than  y years old.
  
  This is implemented in graph.DegreesOfSeparationWithCutoff that uses a new 
  constructor in SymbolGraphX that takes y as an argument and skips movies 
  older than y iff filename.equals("movies.txt"). Technically this was done 
  using regular expression pattern matching. DegreesOfSeparationWithCutoff is 
  demonstrated below.
  
 */

public class Ex4125MoviesDegreesOfSeparationWithCutoff {

  public static void main(String[] args) {

    String[] vargs = {"Bacon, Kevin", "19"};
    // int 2017 movies older than 1998 will be omitted
    DegreesOfSeparationWithCutoff.main(vargs);
    // notice that some movies have changed compared to the output in
    // exercise 4.1.22 (ex41.Ex4122RunSymbolGraphWithMovies)
 
/*
    Done reading movies.txt
    
    Portman, Natalie
       Bacon, Kevin
       Novocaine (2001)
       David, Keith (I)
       Where the Heart Is (2000)
       Portman, Natalie
       
    Huppert, Isabelle
       Bacon, Kevin
       Stir of Echoes (1999)
       Dunn, Kevin (I)
       I Heart Huckabees (2004)
       Huppert, Isabelle
       
    Mortensen, Viggo
       Bacon, Kevin
       Trapped (2002)
       Becker, Gerry
       Perfect Murder, A (1998)
       Mortensen, Viggo
       
    Affleck, Casey
       Bacon, Kevin
       Woodsman, The (2004)
       Roehm Sr., David C.
       Ocean's Eleven (2001)
       Affleck, Casey
       
*/

  }

}



