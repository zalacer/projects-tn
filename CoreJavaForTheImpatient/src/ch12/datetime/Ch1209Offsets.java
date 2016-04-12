package ch12.datetime;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

// 9. Again using stream operations, find all time zones whose offsets aren’t full hours.

public class Ch1209Offsets {

  public static List<String> getZoneIdsWithoutFullHourOffsetsAsList() {
    ZonedDateTime now = ZonedDateTime.now();
    return ZoneId.getAvailableZoneIds().stream().filter(z -> {
      ZoneOffset zo = now.withZoneSameInstant(ZoneId.of(z)).getOffset();
      if (zo.toString().matches(".*?:\\d\\d") && (!zo.toString().matches(".*?:00"))) 
        return true;
      return false;
    }).sorted().collect(Collectors.toList());
  }

  public static Map<String,String> getZoneIdsWithoutFullHourOffsetsAsMap() {
    TreeMap<String,String> zones = new TreeMap<>();
    ZonedDateTime now = ZonedDateTime.now();
    ZoneId.getAvailableZoneIds().stream().filter(z -> {
      ZoneOffset zo = now.withZoneSameInstant(ZoneId.of(z)).getOffset();
      if (zo.toString().matches(".*?:\\d\\d") && (!zo.toString().matches(".*?:00"))) 
        return true;
      return false;
    }).forEach(z -> zones.put(z.toString(),now.withZoneSameInstant(ZoneId.of(z))
          .getOffset().toString()));
    return zones;
  }

  public static <T> void printCollection(Collection<T> c) {
    for (T t : c) System.out.println(t);
  }

  public static <K,V> void printMap(Map<K,V> map) {
    int max = 0;
    for (K k : map.keySet())
      if (k.toString().length() > max) max = k.toString().length();  
    for (Map.Entry<K, V> e : map.entrySet())
      System.out.printf("%-"+max+"s : %s\n", e.getKey(), e.getValue());
  }

  public static void main(String[] args) {

    List<String> zoneIdsWithoutFullHourOffsetsList = getZoneIdsWithoutFullHourOffsetsAsList();
    System.out.println("zoneIdsWithoutFullHourOffsetsList.size: "
        +zoneIdsWithoutFullHourOffsetsList.size()); 
    //zoneIdsWithoutFullHourOffsetsList.size: 26
    System.out.println("zoneIdsWithoutFullHourOffsetsList:");
    printCollection(zoneIdsWithoutFullHourOffsetsList);
    // zoneIdsWithoutFullHourOffsetsList:
    //  America/Caracas
    //  America/St_Johns
    //  Asia/Calcutta
    //  Asia/Colombo
    //  Asia/Kabul
    //  Asia/Kathmandu
    //  Asia/Katmandu
    //  Asia/Kolkata
    //  Asia/Pyongyang
    //  Asia/Rangoon
    //  Asia/Tehran
    //  Australia/Adelaide
    //  Australia/Broken_Hill
    //  Australia/Darwin
    //  Australia/Eucla
    //  Australia/LHI
    //  Australia/Lord_Howe
    //  Australia/North
    //  Australia/South
    //  Australia/Yancowinna
    //  Canada/Newfoundland
    //  Indian/Cocos
    //  Iran
    //  NZ-CHAT
    //  Pacific/Chatham
    //  Pacific/Marquesas

    Map<String, String> zoneIdsWithoutFullHourOffsetsMap = 
        getZoneIdsWithoutFullHourOffsetsAsMap();
    System.out.println("\nzoneIdsWithoutFullHourOffsetsMap.size: "
        +zoneIdsWithoutFullHourOffsetsMap.size());
    //  zoneIdsWithoutFullHourOffsetsMap.size: 26
    System.out.println("zoneIdsWithoutFullHourOffsetsMap:");
    printMap(zoneIdsWithoutFullHourOffsetsMap);
    //  zoneIdsWithoutFullHourOffsets:
    //  America/Caracas       : -04:30
    //  America/St_Johns      : -03:30
    //  Asia/Calcutta         : +05:30
    //  Asia/Colombo          : +05:30
    //  Asia/Kabul            : +04:30
    //  Asia/Kathmandu        : +05:45
    //  Asia/Katmandu         : +05:45
    //  Asia/Kolkata          : +05:30
    //  Asia/Pyongyang        : +08:30
    //  Asia/Rangoon          : +06:30
    //  Asia/Tehran           : +03:30
    //  Australia/Adelaide    : +10:30
    //  Australia/Broken_Hill : +10:30
    //  Australia/Darwin      : +09:30
    //  Australia/Eucla       : +08:45
    //  Australia/North       : +09:30
    //  Australia/South       : +10:30
    //  Australia/Yancowinna  : +10:30
    //  Canada/Newfoundland   : -03:30
    //  Indian/Cocos          : +06:30
    //  Iran                  : +03:30
    //  NZ-CHAT               : +13:45
    //  Pacific/Chatham       : +13:45
    //  Pacific/Marquesas     : -09:30
    //  Pacific/Norfolk       : +11:30

  }

}
