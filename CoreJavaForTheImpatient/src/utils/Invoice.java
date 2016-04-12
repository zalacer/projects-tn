package utils;

import java.util.ArrayList;

// Ch02.15. Fully implement the Invoice class in Section 2.6.1, “Static Nested Classes,” 
// on p.79. Provide a method that prints the invoice and a demo program that constructs 
// and prints a sample invoice.

public class Invoice {
  private static class Item {
    String description;
    int quantity;
    double unitPrice;

    double price() { return quantity * unitPrice; }
    public String toString() { 
      return quantity + " x " + description + " @ $" + unitPrice + " each";
    }
  }

  private ArrayList<Item> items = new ArrayList<>();

  public void addItem(String description, int quantity, double unitPrice) {
    Item newItem = new Item();
    newItem.description = description;
    newItem.quantity = quantity;
    newItem.unitPrice = unitPrice;
    items.add(newItem);
  }

  public void print() {
    double total = 0;
    for (Item item : items) {
      System.out.println(item);
      total += item.price();
    }
    System.out.printf("Total Price: %.2f", total);
  }
}