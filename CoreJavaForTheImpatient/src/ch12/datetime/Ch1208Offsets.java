package ch12.datetime;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//  8. Obtain the offsets of today’s date in all supported time zones 
//  for the current time instant, turning ZoneId.getAvailableZoneIds 
//  into a stream and using stream operations.

public class Ch1208Offsets {

  public static List<ZoneOffset> getAllZoneOffsets() {
    ZonedDateTime now = ZonedDateTime.now();
    return ZoneId.getAvailableZoneIds().stream()
        .map(z -> now.withZoneSameInstant(ZoneId.of(z)).getOffset())
        .collect(Collectors.toList());
  }

  public static Set<ZoneOffset> getUniqueAllZoneOffsets() {
    ZonedDateTime now = ZonedDateTime.now();
    return ZoneId.getAvailableZoneIds().stream()
        .map(z -> now.withZoneSameInstant(ZoneId.of(z)).getOffset())
        .collect(Collectors.toSet());
  }

  public static <T> void printCollection(Collection<T> c) {
    for (T t : c) System.out.println(t);
  }

  public static void main(String[] args) {

    List<ZoneOffset> allZoneOffsets = getAllZoneOffsets();
    System.out.println("allZoneOffsets.size: "+allZoneOffsets.size());  //589
    System.out.println("allZoneOffsets:");
    printCollection(allZoneOffsets);

    Set<ZoneOffset> uniqueZoneOffsets = getUniqueAllZoneOffsets();
    System.out.println("\nuniqueZoneOffsets.size: "+uniqueZoneOffsets.size()); //41
    System.out.println("uniqueZoneOffsets: ");
    printCollection(uniqueZoneOffsets);

  }

}

//  output:
//  allZoneOffsets.size: 589
//  allZoneOffsets:
//  +03:00
//  -03:00
//  -09:00
//  -08:00
//  +03:00
//  -04:00
//  +05:00
//  +12:00
//  -06:00
//  +07:00
//  +02:00
//  -11:00
//  +02:00
//  +08:00
//  -10:00
//  -10:00
//  -06:00
//  +11:00
//  Z
//  -06:00
//  -05:00
//  +08:00
//  -06:00
//  -05:00
//  +04:00
//  +01:00
//  Z
//  +01:00
//  -06:00
//  +06:00
//  -03:00
//  +10:00
//  +01:00
//  +01:00
//  Z
//  Z
//  -06:00
//  +05:00
//  +02:00
//  -05:00
//  +04:00
//  -03:00
//  +03:00
//  +01:00
//  +02:00
//  +09:00
//  -03:00
//  +08:00
//  +07:00
//  +06:00
//  +02:00
//  -08:00
//  -04:00
//  Z
//  +11:00
//  +02:00
//  Z
//  -04:00
//  -08:00
//  +05:00
//  -03:00
//  +07:00
//  +02:00
//  +06:00
//  -09:00
//  +04:00
//  Z
//  -03:00
//  -05:00
//  +06:00
//  -03:00
//  -03:00
//  +01:00
//  -02:00
//  -01:00
//  +13:00
//  +02:00
//  Z
//  +02:00
//  Z
//  -07:00
//  +03:00
//  -06:00
//  -05:00
//  -04:00
//  +10:00
//  -07:00
//  +03:00
//  +04:00
//  +09:00
//  +02:00
//  -03:00
//  +02:00
//  +05:00
//  +03:00
//  +08:00
//  -02:00
//  Z
//  +08:00
//  -05:00
//  +07:00
//  +13:00
//  +08:00
//  -04:00
//  Z
//  +02:00
//  -03:00
//  +08:00
//  +01:00
//  -07:00
//  -10:00
//  +03:00
//  +01:00
//  -03:00
//  +01:00
//  +01:00
//  -03:00
//  -03:00
//  +01:00
//  +11:00
//  Z
//  +01:00
//  -03:00
//  +01:00
//  +06:00
//  +04:00
//  -11:00
//  -06:00
//  -05:00
//  -07:00
//  +01:00
//  +02:00
//  -03:30
//  -04:00
//  +01:00
//  -04:00
//  +09:00
//  -07:00
//  Z
//  Z
//  -06:00
//  +12:00
//  +01:00
//  Z
//  +02:00
//  +02:00
//  -05:00
//  +13:45
//  +08:00
//  -05:00
//  +06:00
//  +01:00
//  -05:00
//  +06:00
//  -04:00
//  +02:00
//  -01:00
//  +05:00
//  -05:00
//  -10:00
//  +13:45
//  +01:00
//  -02:00
//  +09:00
//  -04:00
//  +05:00
//  -04:00
//  -05:00
//  -04:00
//  Z
//  +01:00
//  +03:00
//  +02:00
//  -04:00
//  -06:00
//  +11:00
//  +01:00
//  +01:00
//  -03:00
//  +08:45
//  +08:00
//  Z
//  +01:00
//  -04:00
//  +02:00
//  +02:00
//  +02:00
//  +11:00
//  -06:00
//  Z
//  +06:00
//  -03:00
//  +09:00
//  -06:00
//  +09:30
//  +01:00
//  +01:00
//  +13:00
//  -06:00
//  +05:00
//  +10:30
//  -04:00
//  +05:00
//  -07:00
//  +13:00
//  -09:00
//  +11:00
//  -05:00
//  +05:00
//  +07:00
//  +01:00
//  -03:00
//  -03:00
//  -02:00
//  -06:00
//  +10:30
//  -08:00
//  +03:00
//  -03:00
//  +03:00
//  +02:00
//  Z
//  +01:00
//  -05:00
//  +05:00
//  -04:00
//  Z
//  +04:00
//  +07:00
//  -04:00
//  -09:00
//  -05:00
//  -07:00
//  -05:00
//  +12:00
//  Z
//  +11:00
//  -08:00
//  -04:00
//  +02:00
//  +10:00
//  +08:30
//  -04:00
//  +02:00
//  Z
//  -08:00
//  Z
//  +06:00
//  -03:30
//  -05:00
//  -09:00
//  +07:00
//  +08:00
//  +01:00
//  +03:00
//  -01:00
//  +01:00
//  +09:00
//  -08:00
//  -07:00
//  +10:00
//  +12:00
//  +01:00
//  +05:30
//  -07:00
//  +11:00
//  +01:00
//  +10:30
//  +02:00
//  -04:00
//  Z
//  -03:00
//  -03:00
//  +12:00
//  -03:00
//  +08:00
//  +13:00
//  -07:00
//  +11:00
//  -05:00
//  +10:00
//  +12:00
//  -04:00
//  -06:00
//  -08:00
//  +08:00
//  Z
//  +01:00
//  -06:00
//  -06:00
//  Z
//  Z
//  -08:00
//  -03:00
//  Z
//  +14:00
//  Z
//  -10:00
//  -11:00
//  -06:00
//  +11:00
//  Z
//  +10:00
//  -07:00
//  -06:00
//  -05:00
//  -05:00
//  +06:00
//  -05:00
//  -03:00
//  +11:00
//  -10:00
//  -06:00
//  +04:00
//  +05:00
//  -09:00
//  +10:00
//  +01:00
//  +02:00
//  Z
//  -05:00
//  Z
//  Z
//  -05:00
//  -09:30
//  -04:00
//  -04:00
//  -06:00
//  -05:00
//  +08:00
//  +03:00
//  +01:00
//  -06:00
//  -08:00
//  +10:00
//  -09:00
//  +01:00
//  -07:00
//  -07:00
//  +07:00
//  -09:00
//  -05:00
//  -04:00
//  -03:00
//  -05:00
//  -07:00
//  +02:00
//  -03:00
//  -03:00
//  +04:00
//  -03:00
//  Z
//  +01:00
//  +01:00
//  -04:00
//  +10:00
//  +08:00
//  +13:00
//  +08:00
//  +07:00
//  +03:00
//  +01:00
//  Z
//  -03:00
//  -05:00
//  +03:00
//  +02:00
//  -02:00
//  +05:00
//  Z
//  -05:00
//  -07:00
//  -06:00
//  +06:00
//  +02:00
//  +01:00
//  -11:00
//  +08:00
//  +07:00
//  +14:00
//  -06:00
//  -04:00
//  +03:00
//  Z
//  +11:00
//  +08:00
//  -02:00
//  Z
//  +08:00
//  +01:00
//  +07:00
//  -06:00
//  Z
//  -06:00
//  +03:00
//  -10:00
//  +11:00
//  Z
//  -05:00
//  -06:00
//  -10:00
//  +11:30
//  -06:00
//  +12:00
//  +03:00
//  +07:00
//  Z
//  -05:00
//  -07:00
//  +01:00
//  +10:30
//  -06:00
//  +02:00
//  -04:00
//  -05:00
//  +03:00
//  +13:00
//  Z
//  -04:00
//  -05:00
//  +03:00
//  +02:00
//  +08:00
//  +10:00
//  -08:00
//  -05:00
//  Z
//  -04:00
//  +05:00
//  +01:00
//  -05:00
//  +01:00
//  -05:00
//  +04:00
//  +03:00
//  +02:00
//  -03:00
//  Z
//  -05:00
//  -07:00
//  -05:00
//  +02:00
//  +11:00
//  +01:00
//  +05:45
//  +09:00
//  +11:00
//  -05:00
//  +11:00
//  -04:00
//  +01:00
//  -06:00
//  -04:00
//  +08:00
//  -08:00
//  -07:00
//  -07:00
//  +05:30
//  +08:00
//  +03:00
//  +10:00
//  +03:00
//  -06:00
//  +06:00
//  -10:00
//  +03:00
//  -04:00
//  -08:00
//  +02:00
//  -05:00
//  Z
//  -05:00
//  -04:00
//  +02:00
//  +08:00
//  +03:30
//  -03:00
//  Z
//  -06:00
//  -04:00
//  -04:00
//  +10:00
//  -10:00
//  -01:00
//  +12:00
//  +06:00
//  +02:00
//  -12:00
//  -11:00
//  -07:00
//  -09:00
//  +02:00
//  +02:00
//  +03:00
//  +02:00
//  +01:00
//  +01:00
//  -05:00
//  Z
//  +03:30
//  Z
//  +03:00
//  -03:00
//  -03:00
//  Z
//  +07:00
//  -08:00
//  -11:00
//  -03:00
//  Z
//  -03:00
//  -04:00
//  -04:00
//  -05:00
//  +05:45
//  -07:00
//  +01:00
//  -08:00
//  +11:00
//  +04:00
//  -05:00
//  +08:00
//  -03:00
//  +08:00
//  Z
//  +02:00
//  -05:00
//  -07:00
//  -03:00
//  -04:00
//  -04:00
//  +09:30
//  +09:00
//  +08:00
//  +06:00
//  +06:30
//  +01:00
//  +05:30
//  -03:00
//  +04:30
//  +06:30
//  +09:00
//  +13:00
//  -05:00
//  +12:00
//  +11:00
//  +10:00
//  -09:00
//  +14:00
//  +13:00
//  +03:00
//  -06:00
//  +02:00
//  -03:00
//  -06:00
//  -04:00
//  +01:00
//  -04:30
//  -04:00
//  +02:00
//  +05:00
//  -08:00
//  Z
//  +10:00
//  +02:00
//  +11:00
//  -06:00
//  +12:00
//  +11:00
//  +08:00
//  -07:00
//  -06:00
//  +01:00
//  +02:00
//  Z
//  -04:00
//  +09:00
//  -05:00
//  +08:00
//  +10:00
//  -08:00
//  -05:00
//  +12:00
//  -03:00
//  +02:00
//  +11:00
//  +02:00
//  -08:00
//  +01:00
//  
//  uniqueZoneOffsets.size: 41
//  uniqueZoneOffsets: 
//  +12:00
//  +08:00
//  Z
//  +04:00
//  -04:30
//  +08:30
//  +04:30
//  +08:45
//  -09:00
//  -05:00
//  -01:00
//  +05:00
//  +01:00
//  +09:00
//  +13:00
//  -09:30
//  +09:30
//  +05:30
//  +13:45
//  +05:45
//  -06:00
//  -10:00
//  -02:00
//  +02:00
//  +06:00
//  +10:00
//  +14:00
//  +10:30
//  +06:30
//  -03:00
//  -11:00
//  -07:00
//  +03:00
//  +07:00
//  +11:00
//  -03:30
//  +11:30
//  +03:30
//  -08:00
//  -04:00
//  -12:00
