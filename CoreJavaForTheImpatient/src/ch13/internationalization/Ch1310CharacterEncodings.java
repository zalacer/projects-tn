package ch13.internationalization;

import java.util.LinkedHashMap;
import java.util.Locale;

// 10. Provide a mechanism for showing available character encodings with a human-
// readable description, like in your web browser. The language names should be
// localized. (Use the translations for locale languages.)

// Unfortunately there is no intrinsic connection between locale and charset, 
// however some charsets are designed for particular languages, which currently has 
// to be sorted out by inspection of the output of Charset.availableCharsets() 
// combined with some documentation such as that available at:
//   https://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html,
// that says the situation is confounded in Java by different charset names for
// the same charset depending on API (java.nio or java.io and java.lang).
// Some other useful documents are:
//   http://www.oracle.com/technetwork/java/javase/java8locales-2095355.html
//      (see the Supported Writing Systems table in particular, which shows how
//       how supported encodings depend on the platform such as Windows, Linux, etc.)
//   http://www.science.co.il/Language/Locale-codes.asp
// The result of the suggested analysis could be a drop-down menu of languages to 
// charsets as is available in many applications including Firefox and Notepad++. 
// (I don't find these implementations as useful as they could be, however, since they
// often omit the canonical names of charsets, so one often never is informed of 
// exactly which one is selected, and sometimes they provide no charset name at all.)
// A step towards a solution is a mechanism to lookup a charset given a language,
// namely a map, as follows.

public class Ch1310CharacterEncodings {
  
  private static LinkedHashMap<String,String> locale2Charset = 
      new LinkedHashMap<String,String>() {
    public static final long serialVersionUID = 1L;

    { put("albanian",    "ISO-8859-2");
      put("shqip",       "ISO-8859-2");
      put("sq",          "ISO-8859-2");
      
      put("arabic",      "ISO-8859-6");
      put("العربية",        "ISO-8859-6");
      put("ar",          "ISO-8859-6");
      
      put("belarusian",  "ISO-8859-5");
      put("беларускі",   "ISO-8859-5");
      put("be",          "ISO-8859-5");
      
      put("bulgarian",   "ISO-8859-5");
      put("български",   "ISO-8859-5");
      put("bg",          "ISO-8859-5");      
      
      put("catalan",     "ISO-8859-1");
      put("català",      "ISO-8859-1");
      put("ca",          "ISO-8859-1");
      
      put("chinese",     "GB2312");
      put("中文",        "GB2312");
      put("zh",          "GB2312");

      put("chinesehk",   "Big5");
      put("zh_hk",       "Big5");

      put("chinesetw",   "Big5");
      put("zh_tw",       "Big5"); 
      
      put("croatian",    "ISO-8859-2");
      put("hrvatski",    "ISO-8859-2");
      put("hr",          "ISO-8859-2");
      
      put("czech",       "ISO-8859-2");
      put("čeština",     "ISO-8859-2");
      put("cs",          "ISO-8859-2");

      put("danish",      "ISO-8859-1");
      put("dansk",       "ISO-8859-1");
      put("da",          "ISO-8859-1");
      
      put("dutch",       "ISO-8859-1");
      put("nederlands",  "ISO-8859-1");
      put("nl",          "ISO-8859-1");

      put("english",     "ISO-8859-1");
      put("en",          "ISO-8859-1");
      
      put("estonian",    "ISO-8859-1");
      put("eesti",       "ISO-8859-1");
      put("et",          "ISO-8859-1");

      put("finnish",     "ISO-8859-1");
      put("suomi",       "ISO-8859-1");
      put("fi",          "ISO-8859-1");

      put("french",      "ISO-8859-1");
      put("français",    "ISO-8859-1");
      put("fr",          "ISO-8859-1");
      
      put("german",      "ISO-8859-1");
      put("deutsch",     "ISO-8859-1");
      put("de",          "ISO-8859-1");

      put("greek",       "ISO-8859-7");
      put("ελληνικά",    "ISO-8859-7");
      put("el",          "ISO-8859-7");

      put("hebrew",      "ISO-8859-8");
      put("עברית",       "ISO-8859-8");
      put("iw",          "ISO-8859-8");

      put("hungarian",   "ISO-8859-2");
      put("magyar",      "ISO-8859-2");
      put("hu",          "ISO-8859-2");

      put("icelandic",   "ISO-8859-1");
      put("íslenska",    "ISO-8859-1");
      put("is",          "ISO-8859-1");

      put("italian",     "ISO-8859-1");
      put("italiano",    "ISO-8859-1");
      put("it",          "ISO-8859-1");

      put("japanese",    "Shift_JIS");
      put("日本語",      "Shift_JIS");
      put("ja",          "Shift_JIS");

      put("korean",      "EUC-KR"); 
      put("한국어",     "EUC-KR"); 
      put("ko",          "EUC-KR"); 

      put("latvian",     "ISO-8859-2");
      put("latviešu",    "ISO-8859-2");
      put("lv",          "ISO-8859-2");

      put("lithuanian",  "ISO-8859-2");
      put("lietuvių",    "ISO-8859-2");
      put("lt",          "ISO-8859-2");

      put("macedonian",  "ISO-8859-5");
      put("македонски",  "ISO-8859-5");
      put("mk",          "ISO-8859-5");

      put("norwegian",   "ISO-8859-1");
      put("norsk",       "ISO-8859-1");
      put("no",          "ISO-8859-1");

      put("polish",      "ISO-8859-2");
      put("polski",      "ISO-8859-2");
      put("pl",          "ISO-8859-2");

      put("portuguese",  "ISO-8859-1");
      put("português",   "ISO-8859-1");
      put("pt",          "ISO-8859-1");

      put("romanian",    "ISO-8859-2");
      put("română",      "ISO-8859-2");
      put("ro",          "ISO-8859-2");

      put("russian",     "ISO-8859-5");
      put("русский",     "ISO-8859-5");
      put("ru",          "ISO-8859-5");
 
      put("slovak",      "ISO-8859-2");
      put("slovenčina",  "ISO-8859-2");
      put("sk",          "ISO-8859-2");

      put("slovenian",   "ISO-8859-2");
      put("slovenščina", "ISO-8859-2");
      put("sl",          "ISO-8859-2");
     
      put("serbian",     "ISO-8859-5");
      put("cрпски",      "ISO-8859-5");
      put("sr",          "ISO-8859-5");

      put("spanish",     "ISO-8859-1");
      put("español",     "ISO-8859-1");
      put("es",          "ISO-8859-1");

      put("swedish",     "ISO-8859-1");
      put("svenska",     "ISO-8859-1");
      put("sv",          "ISO-8859-1");

      put("thai",        "TIS-620");
      put("ไทย",         "TIS-620");
      put("th",          "TIS-620");

      put("turkish",     "ISO-8859-9");
      put("türkçe",      "ISO-8859-9");
      put("tr",          "ISO-8859-9");

      put("ukrainian",   "ISO-8859-5");
      put("українська",  "ISO-8859-5");
      put("uk",          "ISO-8859-5");
      
    }};
    
    public static String getCharset(Locale locale) {
      String loc = locale.toString().toLowerCase();
      if (locale2Charset.containsKey(loc)) {
        return locale2Charset.get(loc);
      } else if (locale2Charset.containsKey(
          locale.getDisplayLanguage(locale).toLowerCase())) {
        return locale2Charset.get(locale.getDisplayLanguage(locale).toLowerCase());
      } else if (locale2Charset.containsKey(
          locale.getDisplayLanguage().toLowerCase())) {
        return locale2Charset.get(locale.getDisplayLanguage().toLowerCase());
      } else if (locale2Charset.containsKey(
          locale.getLanguage().toLowerCase())) {
        return locale2Charset.get(locale.getLanguage().toLowerCase());
      } else {
        return "UTF-8";
      }
    }

    public static String getCharset(String l) {
      String loc = l.toLowerCase();
      Locale locale = new Locale(l);
      if (locale2Charset.containsKey(loc)) {
        return locale2Charset.get(loc);
      } else if (locale2Charset.containsKey(
          locale.getDisplayLanguage().toLowerCase())) {
        return locale2Charset.get(locale.getDisplayLanguage(locale).toLowerCase());
      } else if (locale2Charset.containsKey(
         locale.getDisplayLanguage().toLowerCase())) {
        return locale2Charset.get(locale.getDisplayLanguage().toLowerCase());
      } else if (locale2Charset.containsKey(
          locale.getLanguage().toLowerCase())) {
        return locale2Charset.get(locale.getLanguage().toLowerCase());
      } else {
        return "UTF-8";
      }
    }
    
  public static void main(String[] args) {
   
    System.out.println(getCharset(Locale.ITALY)); // ISO-8859-1
    System.out.println(getCharset(new Locale("zh", "TW"))); // Big5
    System.out.println(getCharset("한국어")); // EUC-KR
    System.out.println(getCharset("中文")); // GB2312
    System.out.println(getCharset("עברית")); // ISO-8859-8
    System.out.println(getCharset("العربية")); // ISO-8859-6
    System.out.println(getCharset("cрпски")); // ISO-8859-5
    System.out.println(getCharset("ไทย")); // TIS-620
    System.out.println(getCharset("日本語")); // Shift_JIS
    System.out.println(getCharset("čeština")); // ISO-8859-2
    System.out.println(getCharset("ελληνικά")); // ISO-8859-7
    System.out.println(getCharset("Esperanto")); // UTF-8, default

    
  }

}


//for (String s : locale2Charset.keySet()) {
//Locale l = new Locale(s);
//System.out.println(l+" : "+l.getDisplayLanguage()+" : "+l.getDisplayLanguage(l));
//}