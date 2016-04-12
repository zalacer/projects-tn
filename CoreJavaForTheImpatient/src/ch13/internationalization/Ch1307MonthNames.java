package ch13.internationalization;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.TreeMap;

import utils.Pair;

// 7. Write a program that lists the display and standalone month names in all locales
// in which they differ, excepting those where the standalone names consist of digits.

public class Ch1307MonthNames {
  
  public static void listMonthNames() {
    LocalDate[] dates = new LocalDate[12];
    for (int i = 0; i < dates.length; i++) 
      dates[i]  = LocalDate.of(1958, i+1, 7);

    DateTimeFormatter stamatter = null;
    DateTimeFormatter dismatter = null;
    String stamatted = "";  String dismatted = "";
    boolean same = true;
    TreeMap<String, Pair<String[]>> r = new TreeMap<String, Pair<String[]>>();
    
    LOOP:
    for (Locale loc : Locale.getAvailableLocales()) {
      String[] stalone = new String[12];
      String[] display = new String[12];
      if (loc.toString().equals("")) continue; //omit system default locale
      stamatter = DateTimeFormatter.ofPattern("LLLL").withLocale(loc);//full standalone
      dismatter = DateTimeFormatter.ofPattern("MMMM").withLocale(loc);//full display
      same = true;
      for (int i = 0; i < dates.length; i++) {
        stamatted = stamatter.format(dates[i]);
        if (stamatted.matches("1")) continue LOOP; //omit locale with standalone digits
        dismatted = dismatter.format(dates[i]);
        if (! stamatted.equals(dismatted)) same = false;
        stalone[i] = stamatted;
        display[i] = dismatted;
      }
      if (same) continue LOOP;
      r.put(loc.getDisplayName(), new Pair<String[]>(display, stalone));
    }
    for (String s : r.keySet()) {
      System.out.println(s+" month names from Jan2Dec:");
      System.out.println("display:    "+Arrays.toString(r.get(s).getFirst()));
      System.out.println("standalone: "+Arrays.toString(r.get(s).getSecond())); 
      System.out.println();
    }
  }

  public static void main(String[] args) {
        
    listMonthNames();
    
//  Catalan month names from Jan2Dec:
//  display:    [de gener, de febrer, de març, d’abril, de maig, de juny, de juliol, d’agost, de setembre, d’octubre, de novembre, de desembre]
//  standalone: [gener, febrer, març, abril, maig, juny, juliol, agost, setembre, octubre, novembre, desembre]
//
//  Catalan (Spain) month names from Jan2Dec:
//  display:    [de gener, de febrer, de març, d’abril, de maig, de juny, de juliol, d’agost, de setembre, d’octubre, de novembre, de desembre]
//  standalone: [gener, febrer, març, abril, maig, juny, juliol, agost, setembre, octubre, novembre, desembre]
//
//  Croatian month names from Jan2Dec:
//  display:    [siječnja, veljače, ožujka, travnja, svibnja, lipnja, srpnja, kolovoza, rujna, listopada, studenoga, prosinca]
//  standalone: [siječanj, veljača, ožujak, travanj, svibanj, lipanj, srpanj, kolovoz, rujan, listopad, studeni, prosinac]
//
//  Croatian (Croatia) month names from Jan2Dec:
//  display:    [siječnja, veljače, ožujka, travnja, svibnja, lipnja, srpnja, kolovoza, rujna, listopada, studenoga, prosinca]
//  standalone: [siječanj, veljača, ožujak, travanj, svibanj, lipanj, srpanj, kolovoz, rujan, listopad, studeni, prosinac]
//
//  Czech month names from Jan2Dec:
//  display:    [ledna, února, března, dubna, května, června, července, srpna, září, října, listopadu, prosince]
//  standalone: [leden, únor, březen, duben, květen, červen, červenec, srpen, září, říjen, listopad, prosinec]
//
//  Czech (Czech Republic) month names from Jan2Dec:
//  display:    [ledna, února, března, dubna, května, června, července, srpna, září, října, listopadu, prosince]
//  standalone: [leden, únor, březen, duben, květen, červen, červenec, srpen, září, říjen, listopad, prosinec]
//
//  Finnish month names from Jan2Dec:
//  display:    [tammikuuta, helmikuuta, maaliskuuta, huhtikuuta, toukokuuta, kesäkuuta, heinäkuuta, elokuuta, syyskuuta, lokakuuta, marraskuuta, joulukuuta]
//  standalone: [tammikuu, helmikuu, maaliskuu, huhtikuu, toukokuu, kesäkuu, heinäkuu, elokuu, syyskuu, lokakuu, marraskuu, joulukuu]
//
//  Finnish (Finland) month names from Jan2Dec:
//  display:    [tammikuuta, helmikuuta, maaliskuuta, huhtikuuta, toukokuuta, kesäkuuta, heinäkuuta, elokuuta, syyskuuta, lokakuuta, marraskuuta, joulukuuta]
//  standalone: [tammikuu, helmikuu, maaliskuu, huhtikuu, toukokuu, kesäkuu, heinäkuu, elokuu, syyskuu, lokakuu, marraskuu, joulukuu]
//
//  Greek month names from Jan2Dec:
//  display:    [Ιανουαρίου, Φεβρουαρίου, Μαρτίου, Απριλίου, Μαΐου, Ιουνίου, Ιουλίου, Αυγούστου, Σεπτεμβρίου, Οκτωβρίου, Νοεμβρίου, Δεκεμβρίου]
//  standalone: [Ιανουάριος, Φεβρουάριος, Μάρτιος, Απρίλιος, Μάϊος, Ιούνιος, Ιούλιος, Αύγουστος, Σεπτέμβριος, Οκτώβριος, Νοέμβριος, Δεκέμβριος]
//
//  Greek (Cyprus) month names from Jan2Dec:
//  display:    [Ιανουάριος, Φεβρουάριος, Μάρτιος, Απρίλιος, Μάιος, Ιούνιος, Ιούλιος, Αύγουστος, Σεπτέμβριος, Οκτώβριος, Νοέμβριος, Δεκέμβριος]
//  standalone: [Ιανουάριος, Φεβρουάριος, Μάρτιος, Απρίλιος, Μάϊος, Ιούνιος, Ιούλιος, Αύγουστος, Σεπτέμβριος, Οκτώβριος, Νοέμβριος, Δεκέμβριος]
//
//  Greek (Greece) month names from Jan2Dec:
//  display:    [Ιανουαρίου, Φεβρουαρίου, Μαρτίου, Απριλίου, Μαΐου, Ιουνίου, Ιουλίου, Αυγούστου, Σεπτεμβρίου, Οκτωβρίου, Νοεμβρίου, Δεκεμβρίου]
//  standalone: [Ιανουάριος, Φεβρουάριος, Μάρτιος, Απρίλιος, Μάϊος, Ιούνιος, Ιούλιος, Αύγουστος, Σεπτέμβριος, Οκτώβριος, Νοέμβριος, Δεκέμβριος]
//
//  Italian month names from Jan2Dec:
//  display:    [gennaio, febbraio, marzo, aprile, maggio, giugno, luglio, agosto, settembre, ottobre, novembre, dicembre]
//  standalone: [Gennaio, Febbraio, Marzo, Aprile, Maggio, Giugno, Luglio, Agosto, Settembre, Ottobre, Novembre, Dicembre]
//
//  Italian (Italy) month names from Jan2Dec:
//  display:    [gennaio, febbraio, marzo, aprile, maggio, giugno, luglio, agosto, settembre, ottobre, novembre, dicembre]
//  standalone: [Gennaio, Febbraio, Marzo, Aprile, Maggio, Giugno, Luglio, Agosto, Settembre, Ottobre, Novembre, Dicembre]
//
//  Italian (Switzerland) month names from Jan2Dec:
//  display:    [gennaio, febbraio, marzo, aprile, maggio, giugno, luglio, agosto, settembre, ottobre, novembre, dicembre]
//  standalone: [Gennaio, Febbraio, Marzo, Aprile, Maggio, Giugno, Luglio, Agosto, Settembre, Ottobre, Novembre, Dicembre]
//
//  Lithuanian month names from Jan2Dec:
//  display:    [sausio, vasaris, kovas, balandis, gegužė, birželis, liepa, rugpjūtis, rugsėjis, spalis, lapkritis, gruodis]
//  standalone: [Sausio, Vasario, Kovo, Balandžio, Gegužės, Birželio, Liepos, Rugpjūčio, Rugsėjo, Spalio, Lapkričio, Gruodžio]
//
//  Lithuanian (Lithuania) month names from Jan2Dec:
//  display:    [sausio, vasaris, kovas, balandis, gegužė, birželis, liepa, rugpjūtis, rugsėjis, spalis, lapkritis, gruodis]
//  standalone: [Sausio, Vasario, Kovo, Balandžio, Gegužės, Birželio, Liepos, Rugpjūčio, Rugsėjo, Spalio, Lapkričio, Gruodžio]
//
//  Polish month names from Jan2Dec:
//  display:    [stycznia, lutego, marca, kwietnia, maja, czerwca, lipca, sierpnia, września, października, listopada, grudnia]
//  standalone: [styczeń, luty, marzec, kwiecień, maj, czerwiec, lipiec, sierpień, wrzesień, październik, listopad, grudzień]
//
//  Polish (Poland) month names from Jan2Dec:
//  display:    [stycznia, lutego, marca, kwietnia, maja, czerwca, lipca, sierpnia, września, października, listopada, grudnia]
//  standalone: [styczeń, luty, marzec, kwiecień, maj, czerwiec, lipiec, sierpień, wrzesień, październik, listopad, grudzień]
//
//  Russian month names from Jan2Dec:
//  display:    [января, февраля, марта, апреля, мая, июня, июля, августа, сентября, октября, ноября, декабря]
//  standalone: [Январь, Февраль, Март, Апрель, Май, Июнь, Июль, Август, Сентябрь, Октябрь, Ноябрь, Декабрь]
//
//  Russian (Russia) month names from Jan2Dec:
//  display:    [января, февраля, марта, апреля, мая, июня, июля, августа, сентября, октября, ноября, декабря]
//  standalone: [Январь, Февраль, Март, Апрель, Май, Июнь, Июль, Август, Сентябрь, Октябрь, Ноябрь, Декабрь]
//
//  Slovak month names from Jan2Dec:
//  display:    [januára, februára, marca, apríla, mája, júna, júla, augusta, septembra, októbra, novembra, decembra]
//  standalone: [január, február, marec, apríl, máj, jún, júl, august, september, október, november, december]
//
//  Slovak (Slovakia) month names from Jan2Dec:
//  display:    [januára, februára, marca, apríla, mája, júna, júla, augusta, septembra, októbra, novembra, decembra]
//  standalone: [január, február, marec, apríl, máj, jún, júl, august, september, október, november, december]
//
//  Ukrainian month names from Jan2Dec:
//  display:    [січня, лютого, березня, квітня, травня, червня, липня, серпня, вересня, жовтня, листопада, грудня]
//  standalone: [Січень, Лютий, Березень, Квітень, Травень, Червень, Липень, Серпень, Вересень, Жовтень, Листопад, Грудень]
//
//  Ukrainian (Ukraine) month names from Jan2Dec:
//  display:    [січня, лютого, березня, квітня, травня, червня, липня, серпня, вересня, жовтня, листопада, грудня]
//  standalone: [Січень, Лютий, Березень, Квітень, Травень, Червень, Липень, Серпень, Вересень, Жовтень, Листопад, Грудень]

  }

}
