package ch06.generics;

// 23. In the cautionary note at the end of Section 6.6.7, “Exceptions and Generics,” 
// on p.217, the throwAs helper method is used to “cast” ex into a RuntimeException 
// and  rethrow it. Why can’t you use a regular cast, i.e. throw (RuntimeException) ex?

// Work on this was done in horstmann.ch06.sec06.Exceptions.  Basically the example in 
// the text merely propagates the original exception which actually was caught in doWork
// which passes the exception object to throwAs which fails to cast it because the 
// original is not a subclass of the type of cast attempted on it in doWork. 
// Unfortunately, this mismatch does not cause a visible exception but it might cause a 
// hidden one. More deeply no checked exception can be cast to a checked exception by 
// because no checked exception is a child of any unchecked exception.


public class Ch0623RuntimeException {

}
