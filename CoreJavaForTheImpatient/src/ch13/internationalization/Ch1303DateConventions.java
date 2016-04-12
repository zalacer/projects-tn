package ch13.internationalization;

import static utils.MapUtils.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.TreeMap;

//  3. Which of the locales in your JVM use the same date convention (month/day/year)
//  as the United States?

// en, en_US, en_PH, es_PA, es_US, hi   
    
public class Ch1303DateConventions {
  
  public static void printLocalesUsingUSConvention() {
    TreeMap<String, String> ts = new TreeMap<>();
    FormatStyle style = FormatStyle.SHORT;
    DateTimeFormatter formatter = null;
    LocalDate date = LocalDate.of(1963, 5, 8); // in en_US SHORT format is 5/8/63
    String formatted = "";
    for (Locale loc : Locale.getAvailableLocales()) {
      if (loc.toString().equals("")) continue; // omit system default locale
      formatter = DateTimeFormatter.ofLocalizedDate(style).withLocale(loc);
      formatted = formatter.format(date);
      // allow prepending a 0 to 1 digit day and month and 2 digit century to a 2 digit year
      if (formatted.matches(".*(0)?5/(0)?8/(19)?63.*")) 
        ts.put(loc.toString(), formatted);
    }
    printMap(ts);
  }
  public static void main(String[] args) {

    printLocalesUsingUSConvention();
//    locale  date
//    en    : 5/8/63    // English (language)
//    en_PH : 5/8/63    // English Phillipines
//    en_US : 5/8/63    // English US (system default)
//    es_PA : 05/08/63  // Spanish Panama
//    es_US : 5/8/63    // Spanish US
//    hi    : 5/8/63    // Hindi (language)
    
  }

}
