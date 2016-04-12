package ch04.inheritance.reflection;

// 6.0 Suppose that in Section 4.2.2, “The equals Method,” on p. 140, the
// Item.equals method uses an instanceof test. Implement
// DiscountedItem.equals so that it compares only the superclass if
// otherObject is an Item, but also includes the discount if it is a
// DiscountedItem. Show that this method preserves symmetry but fails to be
// transitive—that is, find a combination of items and discounted items so that
// x.equals(y) and y.equals(z), but not x.equals(z).

//Classes Item and DiscountedItem are defined separately in this package.

public class Ch0406Equals {

  public static void main(String[] args) {

    DiscountedItem x = new DiscountedItem("beans", 1.09, 0.1);
    Item y = new Item("beans", 1.09);
    DiscountedItem z = new DiscountedItem("beans", 1.09, 0.2);

    assert x.equals(y) : "!x.equals(y)";  // ok
    assert y.equals(x) : "!y.equals(x)";  // ok
    assert y.equals(z) : "!y.equals(z)";  // ok   
    assert z.equals(y) : "!z.equals(y)";  // ok     
    assert x.equals(z) : "!x.equals(z)";  // AssertionError: !x.equals(z) (shows intransitivity)
    assert z.equals(x) : "!z.equals(x)";  // AssertionError: !z.equals(x) (shows intransitivity) 



    //        Item cherries = new Item("cherries", 5.20);
    //        if (cherries.getClass() != beans.getClass()) {
    //            System.out.println("cherries class not same as beans class");
    //        }
    //        // cherries class not same as beans class
    //        
    //        System.out.println(cherries.getClass()); // class ch04.Item
    //        
    //        Class<?> cl = null;
    //        try {
    //            cl = Class.forName("ch04.Item");
    //            System.out.println(cl); // class ch04.Item
    //        } catch (ClassNotFoundException e) {
    //            e.printStackTrace();
    //        }



  }

}
