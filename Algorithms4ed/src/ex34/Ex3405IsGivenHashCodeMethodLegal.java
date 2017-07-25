package ex34;


/* p480
  3.4.5  Is the following implementation of hashCode() legal?
    public int hashCode() { return 17; }
  If so, describe the effect of using it. If not, explain why.
  
  It's legal in the sense  it won't cause compiler errors or warnings. 
  However it makes sense only for a class that's a singleton such
  as an Enum with just one instance.  Otherwise it will probabily be 
  incompatible with equals() and cause odd errors, other unfortunate
  behaviour or invalid resutls when the class containing it is used 
  especially when used in a Map or HashSet.
  
 */             

public class Ex3405IsGivenHashCodeMethodLegal {

  public static void main(String[] args) {
      
  }

}

