package ch13.internationalization;

import static utils.CollectionUtils.printfCollection;
import static utils.MapUtils.printMapOfMapsWithArrows;
import static utils.StringUtils.repeat;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

// 6. Write a program that lists all currencies that have
// different symbols in at least two locales.

public class Ch1306CurrencySymbols {
  
  public static TreeMap<String, TreeMap<String, String>> getCurrenciesByLocaleMappedToSymbol() {
    TreeMap<String, TreeMap<String, String>> cblm2s  =  new TreeMap<>();
    for (Locale locale : NumberFormat.getAvailableLocales()) {
      NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
      String ccode = nf.getCurrency().getCurrencyCode();
      if (ccode.equals("XXX")) continue;
      String sym = nf.getCurrency().getSymbol(locale);
      cblm2s.putIfAbsent(ccode, new TreeMap<String, String>());
      cblm2s.get(ccode).put(locale.toString(), sym);
    }
    return cblm2s;
  }
  
  public static void listCurrenciesWithMultipleSymbols() {
    TreeMap<String, TreeMap<String, String>> cblm2s = getCurrenciesByLocaleMappedToSymbol();
    if (cblm2s.size() < 1) return;
    int c = 0;
    List<String> locales = null;
    for (String s : cblm2s.keySet()) {
      TreeMap<String, String> ccode = cblm2s.get(s);
      if ((new HashSet<String>(ccode.values()).size()) > 1) {
        if (c == 0) {
          System.out.println("the following currencies have multiple symbols:");
          locales = new ArrayList<>();
          c++;
        }
        locales.add(s);
      }
    }
    int lsize = locales == null ? -1 : locales.size();
    if (lsize > 0) {
      System.out.print("    ");
      printfCollection(locales);
    }
      
    System.out.println("\nin more detail:\n");   

    c = 0;
    for (String s : cblm2s.keySet()) {
      TreeMap<String, String> ccode = cblm2s.get(s);
      if ((new HashSet<String>(ccode.values()).size()) > 1) {      
        if (c == 0) {
          System.out.println("currency  locale       symbol");
          System.out.println("========  ===========  ======");
          c++;
        }
        int d = 0;
        for (Map.Entry<String, String> e : ccode.entrySet()) {
          if (d == 0) {
            System.out.printf("%-9s %-12s %s\n", s, e.getKey(), e.getValue());
            d++;
          } else {
            System.out.print(repeat(' ', 10));
            System.out.printf("%-12s %s\n", e.getKey(), e.getValue());
          }
        }
        System.out.println();
      }
    }
  }
  
  public static void printCurrenciesByLocaleMappedToSymbol() {
    TreeMap<String, TreeMap<String, String>> cblm2s = getCurrenciesByLocaleMappedToSymbol();
    if (cblm2s.size() < 1) return;
    System.out.println("CurrenciesByLocaleMappedToSymbol");
    System.out.println(repeat('=', "CurrenciesByLocaleMappedToSymbol".length()));
    System.out.println("(format: currency -> {locale -> symbol, ...})");
    printMapOfMapsWithArrows(cblm2s);
  }

  public static void main(String[] args) {
    
    listCurrenciesWithMultipleSymbols();
//  the following currencies have multiple symbols:
//    BAM, INR, RSD, SGD, USD

//  in more detail:
//
//  currency  locale       symbol
//  ========  ===========  ======
//  BAM       sr_BA        КМ.
//            sr_BA_#Latn  KM
//
//  INR       en_IN        Rs.
//            hi_IN        रू
//
//  RSD       sr_RS        дин.
//            sr_RS_#Latn  din.
//
//  SGD       en_SG        $
//            zh_SG        S$
//
//  USD       en_US        $
//            es_EC        $
//            es_PR        $
//            es_US        US$

    printCurrenciesByLocaleMappedToSymbol();
//  CurrenciesByLocaleMappedToSymbol
//  ================================
//  (format: currency -> {locale -> symbol, ...})
//  AED -> {ar_AE -> د.إ.‏}
//  ALL -> {sq_AL -> Lek}
//  ARS -> {es_AR -> $}
//  AUD -> {en_AU -> $}
//  BAM -> {sr_BA -> КМ., sr_BA_#Latn -> KM}
//  BGN -> {bg_BG -> лв.}
//  BHD -> {ar_BH -> د.ب.‏}
//  BOB -> {es_BO -> B$}
//  BRL -> {pt_BR -> R$}
//  BYR -> {be_BY -> Руб}
//  CAD -> {en_CA -> $, fr_CA -> $}
//  CHF -> {de_CH -> SFr., fr_CH -> SFr., it_CH -> SFr.}
//  CLP -> {es_CL -> Ch$}
//  CNY -> {zh_CN -> ￥}
//  COP -> {es_CO -> $}
//  CRC -> {es_CR -> C}
//  CSD -> {sr_CS -> CSD}
//  CUP -> {es_CU -> CU$}
//  CZK -> {cs_CZ -> Kč}
//  DKK -> {da_DK -> kr}
//  DOP -> {es_DO -> RD$}
//  DZD -> {ar_DZ -> د.ج.‏}
//  EGP -> {ar_EG -> ج.م.‏}
//  EUR -> {ca_ES -> €, de_AT -> €, de_DE -> €, de_GR -> €, de_LU -> €, el_CY -> €, el_GR -> €, 
//          en_IE -> €, en_MT -> €, es_ES -> €, et_EE -> €, fi_FI -> €, fr_BE -> €, fr_FR -> €, 
//          fr_LU -> €, ga_IE -> €, it_IT -> €, lt_LT -> €, lv_LV -> €, mt_MT -> €, nl_BE -> €, 
//          nl_NL -> €, pt_PT -> €, sk_SK -> €, sl_SI -> €, sr_ME -> €, sr_ME_#Latn -> €}
//  GBP -> {en_GB -> £}
//  GTQ -> {es_GT -> Q}
//  HKD -> {zh_HK -> HK$}
//  HNL -> {es_HN -> L}
//  HRK -> {hr_HR -> Kn}
//  HUF -> {hu_HU -> Ft}
//  IDR -> {in_ID -> Rp}
//  ILS -> {iw_IL -> ש"ח}
//  INR -> {en_IN -> Rs., hi_IN -> रू}
//  IQD -> {ar_IQ -> د.ع.‏}
//  ISK -> {is_IS -> kr.}
//  JOD -> {ar_JO -> د.أ.‏}
//  JPY -> {ja_JP -> ￥, ja_JP_JP_#u-ca-japanese -> ￥}
//  KRW -> {ko_KR -> ￦}
//  KWD -> {ar_KW -> د.ك.‏}
//  LBP -> {ar_LB -> ل.ل.‏}
//  LYD -> {ar_LY -> د.ل.‏}
//  MAD -> {ar_MA -> د.م.‏}
//  MKD -> {mk_MK -> Den}
//  MXN -> {es_MX -> $}
//  MYR -> {ms_MY -> RM}
//  NIO -> {es_NI -> $C}
//  NOK -> {no_NO -> kr, no_NO_NY -> kr}
//  NZD -> {en_NZ -> $}
//  OMR -> {ar_OM -> ر.ع.‏}
//  PAB -> {es_PA -> B}
//  PEN -> {es_PE -> S/.}
//  PHP -> {en_PH -> Php}
//  PLN -> {pl_PL -> zł}
//  PYG -> {es_PY -> G}
//  QAR -> {ar_QA -> ر.ق.‏}
//  RON -> {ro_RO -> LEI}
//  RSD -> {sr_RS -> дин., sr_RS_#Latn -> din.}
//  RUB -> {ru_RU -> руб.}
//  SAR -> {ar_SA -> ر.س.‏}
//  SDG -> {ar_SD -> ج.س.‏}
//  SEK -> {sv_SE -> kr}
//  SGD -> {en_SG -> $, zh_SG -> S$}
//  SVC -> {es_SV -> C}
//  SYP -> {ar_SY -> ل.س.‏}
//  THB -> {th_TH -> ฿, th_TH_TH_#u-nu-thai -> ฿}
//  TND -> {ar_TN -> د.ت.‏}
//  TRY -> {tr_TR -> TL}
//  TWD -> {zh_TW -> NT$}
//  UAH -> {uk_UA -> грн.}
//  USD -> {en_US -> $, es_EC -> $, es_PR -> $, es_US -> US$}
//  UYU -> {es_UY -> NU$}
//  VEF -> {es_VE -> Bs.F.}
//  VND -> {vi_VN -> đ}
//  YER -> {ar_YE -> ر.ي.‏}
//  ZAR -> {en_ZA -> R}

  }

}
