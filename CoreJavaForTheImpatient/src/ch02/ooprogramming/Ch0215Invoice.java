package ch02.ooprogramming;

import utils.Invoice;

// 15. Fully implement the Invoice class in Section 2.6.1, “Static Nested Classes,” 
// on p.79. Provide a method that prints the invoice and a demo program that constructs 
// and prints a sample invoice.

public class Ch0215Invoice {

  public static void main(String[] args) {

    Invoice invoice = new Invoice();
    invoice.addItem("3M 9005NA 9-Inch by 11-Inch Aluminum Oxide Sandpaper, Assorted",
        3, 2.97);
    invoice.addItem("Rust-Oleum 215215 Rust Reformer 10.25-Ounce Spray-Color Black",
        7, 5.19);
    invoice.addItem("Allied Tools 2 PC. Stainless Steel Wire Brush Set",
        1, 5.42);
    invoice.print();

    //        3 x 3M 9005NA 9-Inch by 11-Inch Aluminum Oxide Sandpaper, Assorted @ $2.97 each
    //        7 x Rust-Oleum 215215 Rust Reformer 10.25-Ounce Spray-Color Black @ $5.19 each
    //        1 x Allied Tools 2 PC. Stainless Steel Wire Brush Set @ $5.42 each
    //        Total Price: 50.66

  }

}
