package ch13.internationalization;

import java.text.Collator;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.TreeSet;

// 5. Repeat the preceding exercise (4) for currency names.

// Exercise statement: Write a program that prints the names 
// of all currencies in all available languages.

public class Ch1305CurrencyNames {
  
  public static void printCurrencyNames() {
    Collator enUScol = Collator.getInstance(new Locale("en","US"));
    enUScol.setStrength(Collator.CANONICAL_DECOMPOSITION);
    
    TreeSet<String> currencies = new TreeSet<>(enUScol);
    for (Locale l : NumberFormat.getAvailableLocales())
      for (Locale l2 : NumberFormat.getAvailableLocales())
        currencies.add(NumberFormat.getCurrencyInstance(l).getCurrency().getDisplayName(l2));
    
//    System.out.println(currencies.size()); //785
    for (String s : currencies) System.out.println(s);
  }

  public static void main(String[] args) {

    printCurrencyNames();
    
  }

}

