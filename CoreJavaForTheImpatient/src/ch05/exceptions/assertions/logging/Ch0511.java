package ch05.exceptions.assertions.logging;

// 11. Compare the use of Objects.requireNonNull(obj) and 
// assert obj != null. Give a compelling use for each.

// Objects.requireNonNull(obj) is useful for parameter validation and
// throws NullPointerException if obj is null and also as a form of
// inline documentation. I prefer using the 
// requireNonNull(T obj, String message) because it prints a custom 
// message when obj is null that is use to communicate at least the 
// name of the parameter.

// assert obj != null is a quick and reliable way of testing that obj is 
// null or not. assertions are useful to testing and proving program 
// assumptions and for detecting bugs. They have the advantages of producing 
// no output when true, being disabled by default and enabled only when a
// program is run with the -ea or -enableassertions options, can be enabled 
// and disabled for only specific classes or packages and can be configured 
// to print a message on failure for communicating details. They are easy to 
// enable/disable in eclipse and I usually have them globally enabled during
// development.

public class Ch0511 {

  //    public static void main(String[] args) {
  //        
  //    }

}
