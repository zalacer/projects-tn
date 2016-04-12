package ch13.internationalization;

import static java.time.format.FormatStyle.FULL;
import static utils.StringUtils.roundBrackets;

import java.text.ChoiceFormat;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

// 9. Take one of your programs and internationalize all messages,
//  using resource bundles in at least two languages.

// This class has an internationalized message demonstrating MessageFormat,
// ChoiceFormat and ZonedDateTime localization with examples in four languages 
// using both ResourceBundle properties files and classes, which are located in 
// package i18n.

public class Ch1309ResourceBundles {

  // for demo, normally locale and zone would be the system defaults
  // see production version below
  public static void printLocalizedMessageUsingBundleClassesDemo(
      Locale locale, int primes, String ps) {
    ZonedDateTime now = ZonedDateTime.now();
    // assuming certain zones based on locales for demo
    if (locale.equals(Locale.US)) {
      now = now.withZoneSameInstant(ZoneId.of("America/New_York"));
    } else if (locale.equals(Locale.FRANCE)) {
      now = now.withZoneSameInstant(ZoneId.of("Europe/Paris"));
    } else if (locale.equals(Locale.GERMANY)) {
      now = now.withZoneSameInstant(ZoneId.of("Europe/Berlin"));
    } else if (locale.equals(new Locale("da", "DK"))) {
      now = now.withZoneSameInstant(ZoneId.of("Europe/Copenhagen"));
    }
    System.out.print("locale: "+locale.getDisplayName()+" ("+locale+")"); 
    System.out.println(" [demo using bundle classes]");
    ResourceBundle bundle = ResourceBundle.getBundle("i18n.MessagesBundleClass", locale);
    MessageFormat primeMform = (MessageFormat) bundle.getObject("primeMform");
    double[] limits = {0,1,2};
    String[] primeChoices = (String[]) bundle.getObject("primeChoices");
    ChoiceFormat cform = new ChoiceFormat(limits, primeChoices);
    primeMform.setFormatByArgumentIndex(2, cform);
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FULL)
        .withLocale(locale).withDecimalStyle(DecimalStyle.of(locale));
    String formattedTime = timeFormatter.format(now);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FULL)
        .withLocale(locale).withDecimalStyle(DecimalStyle.of(locale));
    String FormattedDate = dateFormatter.format(now);
    Object[] oa = {formattedTime, FormattedDate, new Integer(primes), ps};
    System.out.println(primeMform.format(oa));
  }

  // production version using bundle classes
  public static void printLocalizedMessage(int primes, String ps) {
    Locale locale = Locale.getDefault();
    ZonedDateTime now = ZonedDateTime.now();
    System.out.println("locale: "+locale.getDisplayName()+" ("+locale+")"); 
    ResourceBundle bundle = ResourceBundle.getBundle("i18n.MessagesBundleClass", locale);
    MessageFormat primeMform = (MessageFormat) bundle.getObject("primeMform");
    double[] limits = {0,1,2};
    String[] primeChoices = (String[]) bundle.getObject("primeChoices");
    ChoiceFormat cform = new ChoiceFormat(limits, primeChoices);
    primeMform.setFormatByArgumentIndex(2, cform);
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FULL)
        .withLocale(locale).withDecimalStyle(DecimalStyle.of(locale));
    String formattedTime = timeFormatter.format(now);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FULL)
        .withLocale(locale).withDecimalStyle(DecimalStyle.of(locale));
    String FormattedDate = dateFormatter.format(now);
    Object[] oa = {formattedTime, FormattedDate, new Integer(primes), ps};
    System.out.println(primeMform.format(oa));
  }
  
  public static void printLocalizedMessageUsingBundlePropertiesFilesDemo(
      Locale locale, int primes, String ps) {
    ZonedDateTime now = ZonedDateTime.now();
    // assuming certain zones based on locales for demo
    if (locale.equals(Locale.US)) {
      now = now.withZoneSameInstant(ZoneId.of("America/New_York"));
    } else if (locale.equals(Locale.FRANCE)) {
      now = now.withZoneSameInstant(ZoneId.of("Europe/Paris"));
    } else if (locale.equals(Locale.GERMANY)) {
      now = now.withZoneSameInstant(ZoneId.of("Europe/Berlin"));
    } else if (locale.equals(new Locale("da", "DK"))) {
      now = now.withZoneSameInstant(ZoneId.of("Europe/Copenhagen"));
    }
    System.out.print("locale: "+locale.getDisplayName()+" ("+locale+")"); 
    System.out.println(" [demo using bundle properties files]");
    ResourceBundle bundle = ResourceBundle.getBundle("i18n.MessagesBundle", locale);
    MessageFormat primeMform =  new MessageFormat((String)bundle.getObject("primeMform"));
    double[] limits = {0,1,2};
    String[] primeChoices = (String[]) ((String)bundle.getObject("primeChoices")).split("#");
    ChoiceFormat cform = new ChoiceFormat(limits, primeChoices);
    primeMform.setFormatByArgumentIndex(2, cform);
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FULL)
        .withLocale(locale).withDecimalStyle(DecimalStyle.of(locale));
    String formattedTime = timeFormatter.format(now);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FULL)
        .withLocale(locale).withDecimalStyle(DecimalStyle.of(locale));
    String FormattedDate = dateFormatter.format(now);
    Object[] oa = {formattedTime, FormattedDate, new Integer(primes), ps};
    System.out.println(primeMform.format(oa));
  }

  //production version using bundle properties files 
  public static void printLocalizedMessageUsingBundlePropertiesFiles(int primes, String ps) {
    Locale locale = Locale.getDefault();
    ZonedDateTime now = ZonedDateTime.now();
    System.out.println("locale: "+locale.getDisplayName()+" ("+locale+")"); 
    ResourceBundle bundle = ResourceBundle.getBundle("i18n.MessagesBundle", locale);
    MessageFormat primeMform =  new MessageFormat((String)bundle.getObject("primeMform"));
    double[] limits = {0,1,2};
    String[] primeChoices = (String[]) ((String)bundle.getObject("primeChoices")).split("#");
    ChoiceFormat cform = new ChoiceFormat(limits, primeChoices);
    primeMform.setFormatByArgumentIndex(2, cform);
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FULL)
        .withLocale(locale).withDecimalStyle(DecimalStyle.of(locale));
    String formattedTime = timeFormatter.format(now);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FULL)
        .withLocale(locale).withDecimalStyle(DecimalStyle.of(locale));
    String FormattedDate = dateFormatter.format(now);
    Object[] oa = {formattedTime, FormattedDate, new Integer(primes), ps};
    System.out.println(primeMform.format(oa));
  }

  public static boolean prime(long a){
    if (a == 2) return true;
    if (a <= 1 || a % 2 == 0) return false;
    long max = (long) Math.sqrt(a);
    for (long n= 3; n <= max; n+= 2)
      if (a % n == 0) return false;
    return true;
  }

  public static void main(String[] args) {

    int[] nums = new int[10];
    Random rnd = new Random();

    for (int i=0; i<nums.length; i++) {
      nums[i] = rnd.nextInt(100);
    }

    int primes = 0;
    ArrayList<Integer> p = new ArrayList<>();

    for (int num : nums) {
      if (prime(num)) {
        primes++;
        p.add(num);
      }
    }
 
    String ps = "";
    if (p.size() > 0) {
      Collections.sort(p);
      ps = roundBrackets(p)+"\n";
    }
    
    printLocalizedMessageUsingBundleClassesDemo(Locale.US, primes, ps);
    printLocalizedMessageUsingBundleClassesDemo(Locale.FRANCE, primes, ps);
    printLocalizedMessageUsingBundleClassesDemo(Locale.GERMANY, primes, ps);
    printLocalizedMessageUsingBundleClassesDemo(new Locale("da", "DK"), primes, ps);

//  locale: English (United States) (en_US) [demo using bundle classes]
//  At 10:36:49 PM EDT on Monday, April 11, 2016, we found 4 prime numbers (2, 5, 13, 23)
//
//  locale: French (France) (fr_FR) [demo using bundle classes]
//  À 04 h 36 CEST le mardi 12 avril 2016, nous avons trouvé 4 nombres premiers (2, 5, 13, 23)
//
//  locale: German (Germany) (de_DE) [demo using bundle classes]
//  Bei 04:36 Uhr MESZ Dienstag, 12. April 2016, fanden wir 4 Primzahlen (2, 5, 13, 23)
//
//  locale: Danish (Denmark) (da_DK) [demo using bundle classes]
//  04:36:50 CEST den 12. april 2016, fandt vi 4 primtal (2, 5, 13, 23)
        
    printLocalizedMessageUsingBundlePropertiesFilesDemo(Locale.US, primes, ps);
    printLocalizedMessageUsingBundlePropertiesFilesDemo(Locale.FRANCE, primes, ps);
    printLocalizedMessageUsingBundlePropertiesFilesDemo(Locale.GERMANY, primes, ps);
    printLocalizedMessageUsingBundlePropertiesFilesDemo(new Locale("da", "DK"), primes, ps);
    
//  locale: English (United States) (en_US) [demo using bundle properties files]
//  At 10:36:50 PM EDT on Monday, April 11, 2016, we found 4 prime numbers (2, 5, 13, 23)
//
//  locale: French (France) (fr_FR) [demo using bundle properties files]
//  À 04 h 36 CEST le mardi 12 avril 2016, nous avons trouvé 4 nombres premiers (2, 5, 13, 23)
//
//  locale: German (Germany) (de_DE) [demo using bundle properties files]
//  Bei 04:36 Uhr MESZ Dienstag, 12. April 2016, fanden wir 4 Primzahlen (2, 5, 13, 23)
//
//  locale: Danish (Denmark) (da_DK) [demo using bundle properties files]
//  04:36:50 CEST den 12. april 2016, fandt vi 4 primtal (2, 5, 13, 23)
  
    printLocalizedMessage(primes, ps);
    
//  locale: English (United States) (en_US)
//  At 10:36:50 PM EDT on Monday, April 11, 2016, we found 4 prime numbers (2, 5, 13, 23)
    
    printLocalizedMessageUsingBundlePropertiesFiles(primes, ps);
//  locale: English (United States) (en_US)
//  At 10:36:50 PM EDT on Monday, April 11, 2016, we found 4 prime numbers (2, 5, 13, 23)
  
  }

}

