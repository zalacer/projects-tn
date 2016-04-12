package ch13.internationalization;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// 2. Which of the locales in your JVM don’t use Western digits for formatting numbers?

// locale                                                representation of 1234567890
// ===================================================== ============================
// hi_IN (Hindi India)                                   १,२३४,५६७,८९०
// th_TH_TH_#u-nu-thai (Thai Thailand with Thai numbers) ๑,๒๓๔,๕๖๗,๘๙๐
    
public class Ch1302NumberFormats {
  
  public static void printLocalesNotUsingWesternDigits4FormattingNumbers() {
    long n = 1234567890; // using this tests all digits
    for (Locale locale : NumberFormat.getAvailableLocales()) {
      NumberFormat formatter = NumberFormat.getNumberInstance(locale);
      String res = formatter.format(n);
      // allowing number formats to insert ",", ".", "'" and "\h+" (horizontal whitespace)
      String resmod = res.replaceAll(",", "").replaceAll("\\.", "")
                         .replaceAll("'", "").replaceAll("\\h+", "");
      if (!resmod.matches(""+n)) System.out.println(locale);
    }
  }
  
  public static List<Locale> getLocalesNotUsingWesternDigits4FormattingNumbers() {
    List<Locale> locs = new ArrayList<>();
    long n = 1234567890;
    for (Locale locale : NumberFormat.getAvailableLocales()) {
      NumberFormat formatter = NumberFormat.getNumberInstance(locale);
      String res = formatter.format(n);
      // allowing number formats to insert ",", ".", "'" and horizontal whitespace
      String resmod = res.replaceAll(",", "").replaceAll("\\.", "")
          .replaceAll("'", "").replaceAll("\\h+", "");
      if (!resmod.matches(""+n)) locs.add(locale);
    }
    return locs;
  }
  
  public static <N extends Number> String formatNumberInLocale(Locale loc, N n) {
    NumberFormat formatter = NumberFormat.getNumberInstance(loc);
    return formatter.format(n);
  }

  public static void main(String[] args) {

    printLocalesNotUsingWesternDigits4FormattingNumbers();
//    hi_IN
//    th_TH_TH_#u-nu-thai
    
    List<Locale> locs = getLocalesNotUsingWesternDigits4FormattingNumbers();
    long n = 1234567890;
    for (Locale loc : locs) 
      System.out.println(loc+" : "+formatNumberInLocale(loc, n));
//    hi_IN : १,२३४,५६७,८९०  // Hindi India
//    th_TH_TH_#u-nu-thai : ๑,๒๓๔,๕๖๗,๘๙๐ // Thai Thailand with Thai numbers

    
  }

}
