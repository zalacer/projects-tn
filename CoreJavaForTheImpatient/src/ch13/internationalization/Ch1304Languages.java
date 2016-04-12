package ch13.internationalization;

import java.text.Collator;
import java.util.Locale;
import java.util.TreeSet;

// 4. Write a program that prints the names of all languages of locales in 
// your JVM in all available languages. Collate them and suppress duplicates.

public class Ch1304Languages {
  
  public static void printLanguageNames() {
    Collator enUScol = Collator.getInstance(new Locale("en","US"));
    enUScol.setStrength(Collator.CANONICAL_DECOMPOSITION);

    TreeSet<String> languages = new TreeSet<>(enUScol);
    for (Locale l : Locale.getAvailableLocales())
      for (Locale l2 : Locale.getAvailableLocales()) {
        String dl = l.getDisplayLanguage(l2);
        if (dl.length() > 0) 
          languages.add(l.getDisplayLanguage(l2));
      }
    
    System.out.println(languages.size());
    for (String s : languages) System.out.println(s);
  }

  public static void main(String[] args) {
 
    printLanguageNames();

  }

}
