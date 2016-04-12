package ch04.inheritance.reflection;

import java.util.Objects;

//6.0 Suppose that in Section 4.2.2, “The equals Method,” on p. 140, the
//Item.equals method uses an instanceof test. Implement
//DiscountedItem.equals so that it compares only the superclass if
//otherObject is an Item, but also includes the discount if it is a
//DiscountedItem. Show that this method preserves symmetry but fails to be
//transitive—that is, find a combination of items and discounted items so that
//x.equals(y) and y.equals(z), but not x.equals(z).

public class DiscountedItem extends Item {
  private double discount;

  public DiscountedItem(String description, double price, double discount) {
    super(description, price);
    this.discount = discount;
  }

  public boolean equals(Object otherObject) {
    if (!super.equals(otherObject)) return false;
    if (otherObject.getClass() == Item.class) return true;
    if (otherObject instanceof DiscountedItem) {
      DiscountedItem other = (DiscountedItem) otherObject;
      return discount == other.discount;
    }
    return false;
  }

  public int hashCode() {
    return Objects.hash(super.hashCode(), discount);
  }

}
