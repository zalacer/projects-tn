package ch04.inheritance.reflection;

import java.util.Objects;

//6.0 Suppose that in Section 4.2.2, “The equals Method,” on p. 140, the
//Item.equals method uses an instanceof test. Implement
//DiscountedItem.equals so that it compares only the superclass if
//otherObject is an Item, but also includes the discount if it is a
//DiscountedItem. Show that this method preserves symmetry but fails to be
//transitive—that is, find a combination of items and discounted items so that
//x.equals(y) and y.equals(z), but not x.equals(z).

public class Item {
  private String description;
  private double price;

  public Item(String description, double price) {
    this.description = description;
    this.price = price;
  }

  public boolean equals(Object otherObject) {
    // A quick test to see if the objects are identical
    if (this == otherObject) return true;
    // Must return false if the explicit parameter is null
    if (otherObject == null) return false;
    // Check that otherObject is a Item
    if (!(otherObject instanceof Item)) return false;
    // if (getClass() != otherObject.getClass()) return false;
    // Test whether the instance variables have identical values
    Item other = (Item) otherObject;
    return Objects.equals(description, other.description)
        && price == other.price;
  }

  public int hashCode() {
    return Objects.hash(description, price);
  }
}
