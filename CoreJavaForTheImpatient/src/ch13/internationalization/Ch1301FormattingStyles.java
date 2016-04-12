package ch13.internationalization;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import  java.time.format.FormatStyle;
import static java.time.format.FormatStyle.*;
import java.util.Locale;
import java.time.ZoneId;

//  1. Write a program that demonstrates the date and time formatting styles in France,
//  China, and Thailand (with Thai digits).

public class Ch1301FormattingStyles {
  
  public static void printStyledDateTimes(Locale loc) {
    FormatStyle[] fs = new FormatStyle[]{SHORT, MEDIUM, LONG, FULL}; 
    String[] fss = new String[]{"SHORT", "MEDIUM", "LONG", "FULL"};
    ZonedDateTime now = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Africa/Dakar"));
    DateTimeFormatter formatter;
    String formatted;
    System.out.println(loc.getDisplayName(Locale.getDefault())
        +" ("+loc+") format styles:");
    for (int i = 0; i < fs.length; i++) {
      formatter =  DateTimeFormatter.ofLocalizedDateTime(fs[i], fs[i]).withLocale(loc)
          .withDecimalStyle(DecimalStyle.of(loc));
      formatted = formatter.format(now);
      System.out.printf("%-6s : %s\n", fss[i], formatted);
    }
    System.out.println();
  }

  public static void main(String[] args) {

    printStyledDateTimes(Locale.FRANCE);
    printStyledDateTimes(Locale.CHINA);
    printStyledDateTimes(new Locale("th", "TH", "TH"));

    //  French (France) (fr_FR) format styles:
    //    SHORT  : 11/04/16 21:32
    //    MEDIUM : 11 avr. 2016 21:32:38
    //    LONG   : 11 avril 2016 21:32:38 GMT
    //    FULL   : lundi 11 avril 2016 21 h 32 GMT
    //
    //    Chinese (China) (zh_CN) format styles:
    //    SHORT  : 16-4-11 下午9:32
    //    MEDIUM : 2016-4-11 21:32:38
    //    LONG   : 2016年4月11日 下午09时32分38秒
    //    FULL   : 2016年4月11日 星期一 下午09时32分38秒 GMT
    //
    //    Thai (Thailand,TH) (th_TH_TH_#u-nu-thai) format styles:
    //    SHORT  : ๑๑/๔/๒๐๑๖, ๒๑:๓๒ น.
    //    MEDIUM : ๑๑ เม.ย. ๒๐๑๖, ๒๑:๓๒:๓๘
    //    LONG   : ๑๑ เมษายน ๒๐๑๖, ๒๑ นาฬิกา ๓๒ นาที
    //    FULL   : วันจันทร์ที่ ๑๑ เมษายน ค.ศ. ๒๐๑๖, ๒๑ นาฬิกา ๓๒ นาที ๓๘ วินาที



  }

}
