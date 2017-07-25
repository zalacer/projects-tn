package u;

public class HiddenFeatures {

  //http://stackoverflow.com/questions/15496/hidden-features-of-java/42686#42686
  
  public interface Foo {}
  public interface Bar {}
  public class Baz<T extends Foo & Bar> {}  // union type
  
  @SuppressWarnings("unused")
  public static void main(String[] args) {

//    new Object() {
//      void foo(String s) {
//        System.out.println(s);
//      }
//    }.foo("Hello");
    
    // JavaPuzzlers p75 Puzzle 22: Dupe of URL
    System.out.println("Hello World!");
    http://www.google.com;

    System.exit(0);

  }

}
