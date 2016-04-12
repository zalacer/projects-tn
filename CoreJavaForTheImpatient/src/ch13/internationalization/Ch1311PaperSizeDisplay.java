package ch13.internationalization;

import static utils.StringUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

// 11. Provide a class for locale-dependent display of paper sizes, using the preferred
// dimensional unit and default paper size in the given locale. (Everyone on the planet,
// with the exception of the United States and Canada, uses ISO 216 paper sizes. Only
// three countries in the world have not yet officially adopted the metric system:
// Liberia, Myanmar (Burma), and the United States.)

// references used for paper size data:
// ISO 216 2ed 2007-09-15 (Reference number ISO 216:2007(E)) from http://www.iso.org 
//    a copy of which is in the internationalization folder in this project
// https://en.wikipedia.org/wiki/ISO_216
// https://en.wikipedia.org/wiki/Paper_size
// http://www.cl.cam.ac.uk/~mgk25/iso-paper.html

// For comparison see java.awt.PageAttributes.MediaType. In contrast I chose to use 
// enum maximally and wanted to make it easy for a user to view all available paper 
// sizes, select any by name and find those closest to given a width and height.

public class Ch1311PaperSizeDisplay {
 
  public static class Psd {
    
    private PaperSize defaultPaperSize;
    
    public Psd() {
      super();
      setDefaultPaperSize(getDefaultPaperSize(Locale.getDefault()));
    }

    private enum PaperSize {
      ISO_4A0 ("ISO 216 4A0", "mm", 1632, 2378),
      ISO_2A0 ("ISO 216 2A0", "mm", 1189, 1682),
      ISO_A0 ("ISO 216 A0", "mm", 841, 1189),
      ISO_A1 ("ISO 216 A1", "mm", 594, 841),
      ISO_A2 ("ISO 216 A2", "mm", 420, 594),
      ISO_A3 ("ISO 216 A3", "mm", 297, 420),
      ISO_A4 ("ISO 216 A4", "mm", 210, 297),
      ISO_A5 ("ISO 216 A5", "mm", 148, 210),
      ISO_A6 ("ISO 216 A6", "mm", 105, 148),
      ISO_A7 ("ISO 216 A7", "mm", 74, 105),
      ISO_A8 ("ISO 216 A8", "mm", 52, 74),
      ISO_A9 ("ISO 216 A9", "mm", 37, 52),
      ISO_A10 ("ISO 216 A10", "mm", 26, 37),
      ISO_B0 ("ISO 216 B0", "mm", 1000, 1414),
      ISO_B1 ("ISO 216 B1", "mm", 707, 1000),
      ISO_B2 ("ISO 216 B2", "mm", 500, 707),
      ISO_B3 ("ISO 216 B3", "mm", 353, 500),
      ISO_B4 ("ISO 216 A4", "mm", 250, 353),
      ISO_B5 ("ISO 216 A5", "mm", 176, 250),
      ISO_B6 ("ISO 216 A6", "mm", 125, 176),
      ISO_B7 ("ISO 216 A7", "mm", 88, 125),
      ISO_B8 ("ISO 216 A8", "mm", 62, 88),
      ISO_B9 ("ISO 216 A9", "mm", 44, 62),
      ISO_B10 ("ISO 216 B10", "mm", 31, 44),
      ISO_C0 ("ISO 269 C0", "mm", 917, 1297),
      ISO_C1 ("ISO 269 C1", "mm", 648, 917),
      ISO_C2 ("ISO 269 C2", "mm", 458, 648),
      ISO_C3 ("ISO 269 C3", "mm", 324, 458),
      ISO_C4 ("ISO 269 C4", "mm", 229, 324),
      ISO_C5 ("ISO 269 C5", "mm", 162, 229),
      ISO_C6 ("ISO 269 C6", "mm", 114, 162),
      ISO_C7_6 ("ISO 269 C7/6", "mm", 81, 162),
      ISO_C7 ("ISO 269 C7", "mm", 81, 114),
      ISO_C8 ("ISO 269 C8", "mm", 57, 81),
      ISO_C9 ("ISO 269 C9", "mm", 40, 57),
      ISO_C10 ("ISO 269 C10", "mm", 28, 40),
      ISO_DL ("ISO 269 DL", "mm", 110, 220),
      CAN_P1 ("Canadian 2-9.60M P1", "mm", 560, 860),
      CAN_P2 ("Canadian 2-9.60M P2", "mm", 430, 560),
      CAN_P3 ("Canadian 2-9.60M P3", "mm", 280, 430),
      CAN_P4 ("Canadian 2-9.60M P4", "mm", 215, 280),
      CAN_P5 ("Canadian 2-9.60M P5", "mm", 140, 215),
      CAN_P6 ("Canadian 2-9.60M P6", "mm", 107, 140),
      NA_LETTER ("North American Letter", "in", 8.5, 11),
      NA_GOVERNMENT_LETTER ("North American Government-Letter", "in", 8, 10.5),
      NA_LEGAL ("North American Legal", "in", 8.5, 14),
      NA_JUNIOR_LEGAL ("North American Junior Legal", "in", 8, 5),
      NA_LEDGER ("North American Ledger", "in", 17, 11),
      NA_TABLOID ("North American Tabloid", "in", 11, 17),
      ANSI_A ("ANSI A", "in", 8.5, 11),
      ANSI_B ("ANSI B", "in", 11, 17),
      ANSI_C ("ANSI C", "in", 17, 22),
      ANSI_D ("ANSI D", "in", 22, 34),
      ANSI_E ("ANSI E", "in", 34, 44);

      final String info;
      final String unit;
      final double width;
      final double height;

      PaperSize(String info, String unit, double width, double height) {
        this.info = info;
        this.unit = unit;
        this.width = width;
        this.height = height;
      }

      public String getInfo() { return info; }
      public String getUnit() { return unit; }
      public double getWidth() { return width; }
      public double getHeight() { return height; }
      
      public double mmwidth() {
        if (unit.equals("mm")) return width;
        return in2mm(width);
      }
      
      public double mmheight() {
        if (unit.equals("mm")) return height;
        return in2mm(height);
      }
      
      public double mmarea() {
        return mmwidth() * mmheight();
      }
      
      public double inwidth() {
        if (unit.equals("in")) return width;
        return mm2in(width);
      }
      
      public double inheight() {
        if (unit.equals("in")) return height;
        return mm2in(height);
      }
      
      public double inarea() {
        return inwidth() * inheight();
      }
      
      public String mmdims() {
        return ""+mmwidth()+" x "+mmheight();
      }
      
      public String indims() {
       return ""+inwidth()+" x "+inheight();
      }
      
      @Override
      public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name()+"[");
        builder.append("unit=");
        builder.append(unit);
        builder.append(", mmdims=");
        builder.append(mmdims());
        builder.append(", indims=");
        builder.append(indims());
        builder.append("]");
        return builder.toString();
      }
      
      public String toStringWithInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append(name()+"[");
        builder.append("info=");
        builder.append(info);
        builder.append(", unit=");
        builder.append(unit);
        builder.append(", mmdims=");
        builder.append(mmdims());
        builder.append(", indims=");
        builder.append(indims());
        builder.append("]");
        return builder.toString();
      }
      
    }
    
    public void setDefaultPaperSize(PaperSize ps) {
      if (ps == null) {
        throw new IllegalArgumentException("null not a valid paper size");
      }
      defaultPaperSize = ps;
    }

    public PaperSize getDefaultPaperSize() {
      return defaultPaperSize;
    }
    
    public PaperSize getDefaultPaperSize(Locale locale) {
      String defaultCountry = locale.getCountry();
      if (defaultCountry != null &&
          (defaultCountry.equals(Locale.US.getCountry()) ||
              defaultCountry.equals(Locale.CANADA.getCountry()))) {
        return PaperSize.NA_LETTER;
      } else {
        return PaperSize.ISO_A4;
      }
    }
    
    public String getInfo(PaperSize p) {
      return p.getInfo();
    }

    public String getUnit(PaperSize p) {
      return p.getUnit();
    }
    
    public Double getWidth(PaperSize p) {
      return p.getWidth();
    }
    
    public Double getHeight(PaperSize p) {
      return p.getHeight();
    }
    
    public String getDefaultUnit() {
      return getDefaultPaperSize().getUnit();
    }
    
    public String getDefaultUnit(Locale locale) {
      return getDefaultPaperSize(locale).getUnit();
    }
    
    public static PaperSize[] getPaperSizes() {
      return PaperSize.values();
    }
    
    public static List<PaperSize> getPaperSizeList() {
      return Arrays.asList(PaperSize.values());
    }
         
    public static List<String> getPaperSizeNames() {
      List<String> values = new ArrayList<String>();
      for (PaperSize ps : PaperSize.values())
          values.add(ps.name());
      return values;
    }
    
    public static void printPaperSizeNames() {
      System.out.println("names of all available paper sizes:");
      for (PaperSize ps : PaperSize.values()) 
        System.out.println(ps.name());
      System.out.println();
    }
    
    public static void printPaperSizes() {
      System.out.println("all available paper sizes:");
      for (PaperSize ps : PaperSize.values()) 
        System.out.println(ps.toString());
      System.out.println();
    }
    
    public static void printPaperSizesWithInfo() {
      System.out.println("all available paper sizes with info:");
      for (PaperSize ps : PaperSize.values()) 
        System.out.println(ps.toStringWithInfo());
      System.out.println();
    }
    
    public static void printPaperSizesSortedBySize() {
      Map<PaperSize, Double> m = new HashMap<>();
      for (PaperSize p : PaperSize.values()) {
        m.put(p, p.mmarea());
      }
      
      int maxmmdims = 0;
      int maxindims = 0;
      for (PaperSize p : PaperSize.values()) {
        if (p.mmdims().length() > maxmmdims) 
          maxmmdims = p.mmdims().length();
        if (p.indims().length() > maxindims) 
          maxindims = p.indims().length();
      }
      
      Map<PaperSize, Double> r = sortByValue(m);
      System.out.println("all available paper sizes sorted by increasing area:");
      System.out.println("name                 area (mm²) mm w x h        in w x h");
      System.out.println(repeat('=', 20)+" "+repeat('=', 10)+" "
          +repeat('=', maxmmdims)+" "+repeat('=', maxindims));
      
      for (PaperSize p : r.keySet())
        System.out.printf("%-20s %-10.2f %-"+maxmmdims+"s %-"+maxindims+"s\n",
            p.name(), r.get(p), p.mmdims(), p.indims());
      System.out.println();
    }
    
    public static void findClosestPaperSizes(
        String unit, double width, double height, int numberOfmatches) {
      if (!(unit.toLowerCase().matches("mm|in"))) {
          System.out.println("recognized units are mm and in");
          System.out.println("unit "+unit+" is not recognized");
          System.out.println("using mm as the default unit2use");
          unit = "mm";
      }
      if (width < 0 || height < 0) {
        System.out.println("width and height must both be > 0");
        return;
      }
      if (numberOfmatches == 0) {
        System.out.println("returning no matches as requested");
        return;
      }
      if (numberOfmatches < 0) {
        System.out.println("can't return a negative number of matches");
        System.out.println("will return "+(-numberOfmatches));
      }
      if (numberOfmatches >= PaperSize.values().length) {
        System.out.println("the number of requested matches is greater or "
            + "\n  equal to the number of available paper sizes");
        System.out.println("run printPaperSizesSortedBySize() to see all "
            + "\n  available paper sizes");
        return;
      }
            
      double target = width * height;
      
      Map<PaperSize, Double> m = new HashMap<>();
      
      // using difference of areas as metric
      if (unit.equals("mm")) {
        for (PaperSize p : PaperSize.values()) {
          m.put(p, Math.abs(target - p.mmarea()));
        }        
      } else if (unit.equals("in")){
        for (PaperSize p : PaperSize.values()) {
          m.put(p, Math.abs(target - p.inarea()));
        }
      }
     
//      Euclidian distance metric factoring in aspect ratio and area
//      did not appear to produce better results
//      if (unit.equals("mm")) {
//        for (PaperSize p : PaperSize.values()) {
//          double na = Math.abs(target - p.mmarea()) / target;
//          double nr = Math.abs(width/height - p.width/p.height) / (width/height);
//          double d = sqrt(pow(na, 2) - pow(nr, 2));
//          m.put(p, d);
//        }
//      } else if (unit.equals("in")) {
//        for (PaperSize p : PaperSize.values()) {
//          double na = Math.abs(target - p.inarea()) / target;
//          double nr = Math.abs(width/height - p.width/p.height) / (width/height);
//          double d = sqrt(pow(na, 2) - pow(nr, 2));
//          m.put(p, d);
//        }
//      }
      
      Map<PaperSize, Double> r = sortByValue(m);
      int count = 0;
      
      int maxmmdims = 0;
      int maxindims = 0;
      for (PaperSize p : PaperSize.values()) {
        if (p.mmdims().length() > maxmmdims) 
          maxmmdims = p.mmdims().length();
        if (p.indims().length() > maxindims) 
          maxindims = p.indims().length();
      }    
      
      String squnit = unit+"²";
      String psqunit = "("+squnit+")";
      System.out.println("target area = "+width+" * "+height+" = "+target+" "+squnit);
      System.out.println("top "+numberOfmatches+" best matches in descending order by "
          + "increasing area difference:");
      System.out.println("                     area diff");
      System.out.println("name                 "+psqunit+"        mm w x h        in w x h");

      System.out.println(repeat('=', 20)+" "+repeat('=', 12)+" "
          +repeat('=', maxmmdims)+" "+repeat('=', maxindims));
      
      for (PaperSize p : r.keySet()) {
        System.out.printf("%-20s %-12.2f %-"+maxmmdims+"s %-"+maxindims+"s\n",
            p.name(), r.get(p), p.mmdims(), p.indims());
        count++;
        if (count == numberOfmatches) break;
      }
      System.out.println();
    }
    
    public static boolean hasPaperSize(String paperSize) {
      try {
        PaperSize.valueOf(paperSize);  
        return true;
      } catch  (IllegalArgumentException e) {
        return false;
      }
    }
     
    public PaperSize getPaperSize(String paperSize) {
      PaperSize ps;
      try {
        ps = PaperSize.valueOf(paperSize);  
      } catch  (IllegalArgumentException e) {
        return null;
      }
      return ps;
    }
    
    public static double mm2in (double mm) {
      double inches = mm * 0.039370;
      return Double.parseDouble(String.format("%.2f", inches));
    }
    
    public static double in2mm (double inches) {
      double mm = inches / 0.039370;
      return Double.parseDouble(String.format("%.2f", mm));
    }
    
    public static <K, V extends Comparable<? super V>> Map<K, V> 
        sortByValue(Map<K, V> map) {
      Map<K, V> result = new LinkedHashMap<>();
      Stream<Entry<K, V>> st = map.entrySet().stream();
      st.sorted(Comparator.comparing(e -> e.getValue()))
        .forEach(e -> result.put(e.getKey(), e.getValue()));
      return result;
    }

  }

  public static void main(String[] args) {
   
    Psd.findClosestPaperSizes("in", 8, 13, 5);
//  target area = 8.0 * 13.0 = 104.0 in²
//  top 5 best matches in descending order by increasing area difference:
//                       area diff
//  name                 (in²)        mm w x h        in w x h
//  ==================== ============ =============== =============
//  ISO_A4               7.32         210.0 x 297.0   8.27 x 11.69 
//  ANSI_A               10.50        215.9 x 279.4   8.5 x 11.0   
//  NA_LETTER            10.50        215.9 x 279.4   8.5 x 11.0   
//  CAN_P4               10.77        215.0 x 280.0   8.46 x 11.02 
//  ISO_C4               11.10        229.0 x 324.0   9.02 x 12.76 
    
    Psd psd = new Psd();
    
    System.out.println(Locale.getDefault());
//  en_US
     
    System.out.println(psd.getDefaultPaperSize());
//  NA_LETTER[unit=in, mmdims=215.9 x 279.4, indims=8.5 x 11.0]
    
    // The default unit is operationally defined as the unit of the default 
    // PaperSize that in turn is determined by the default locale initially
    // by default. There is actually no defaultUnit parameter pe se.
    
    System.out.println(psd.getDefaultUnit());
//  in
    
    if (Psd.hasPaperSize("CAN_P4")) 
      psd.setDefaultPaperSize(psd.getPaperSize("CAN_P4"));
    
    System.out.println(psd.getDefaultPaperSize());
//  CAN_P4[unit=mm, mmdims=215.0 x 280.0, indims=8.46 x 11.02] 
    
    System.out.println(psd.getDefaultUnit());
//  mm
    
    System.out.println(psd.getDefaultPaperSize(Locale.GERMANY));
//  ISO_A4[unit=mm, mmdims=210.0 x 297.0, indims=8.27 x 11.69]
    
    System.out.println(psd.getDefaultUnit(Locale.GERMANY));
//  mm
    
    Psd.printPaperSizesSortedBySize();
//  all available paper sizes sorted by increasing area:
//  name                 area (mm²) mm w x h        in w x h
//  ==================== ========== =============== =============
//  ISO_A10              962.00     26.0 x 37.0     1.02 x 1.46  
//  ISO_C10              1120.00    28.0 x 40.0     1.1 x 1.57   
//  ISO_B10              1364.00    31.0 x 44.0     1.22 x 1.73  
//  ISO_A9               1924.00    37.0 x 52.0     1.46 x 2.05  
//  ISO_C9               2280.00    40.0 x 57.0     1.57 x 2.24  
//  ISO_B9               2728.00    44.0 x 62.0     1.73 x 2.44  
//  ISO_A8               3848.00    52.0 x 74.0     2.05 x 2.91  
//  ISO_C8               4617.00    57.0 x 81.0     2.24 x 3.19  
//  ISO_B8               5456.00    62.0 x 88.0     2.44 x 3.46  
//  ISO_A7               7770.00    74.0 x 105.0    2.91 x 4.13  
//  ISO_C7               9234.00    81.0 x 114.0    3.19 x 4.49  
//  ISO_B7               11000.00   88.0 x 125.0    3.46 x 4.92  
//  ISO_C7_6             13122.00   81.0 x 162.0    3.19 x 6.38  
//  CAN_P6               14980.00   107.0 x 140.0   4.21 x 5.51  
//  ISO_A6               15540.00   105.0 x 148.0   4.13 x 5.83  
//  ISO_C6               18468.00   114.0 x 162.0   4.49 x 6.38  
//  ISO_B6               22000.00   125.0 x 176.0   4.92 x 6.93  
//  ISO_DL               24200.00   110.0 x 220.0   4.33 x 8.66  
//  NA_JUNIOR_LEGAL      25806.40   203.2 x 127.0   8.0 x 5.0    
//  CAN_P5               30100.00   140.0 x 215.0   5.51 x 8.46  
//  ISO_A5               31080.00   148.0 x 210.0   5.83 x 8.27  
//  ISO_C5               37098.00   162.0 x 229.0   6.38 x 9.02  
//  ISO_B5               44000.00   176.0 x 250.0   6.93 x 9.84  
//  NA_GOVERNMENT_LETTER 54193.44   203.2 x 266.7   8.0 x 10.5   
//  CAN_P4               60200.00   215.0 x 280.0   8.46 x 11.02 
//  ANSI_A               60322.46   215.9 x 279.4   8.5 x 11.0   
//  NA_LETTER            60322.46   215.9 x 279.4   8.5 x 11.0   
//  ISO_A4               62370.00   210.0 x 297.0   8.27 x 11.69 
//  ISO_C4               74196.00   229.0 x 324.0   9.02 x 12.76 
//  NA_LEGAL             76774.04   215.9 x 355.6   8.5 x 14.0   
//  ISO_B4               88250.00   250.0 x 353.0   9.84 x 13.9  
//  CAN_P3               120400.00  280.0 x 430.0   11.02 x 16.93
//  NA_TABLOID           120644.92  279.4 x 431.8   11.0 x 17.0  
//  ANSI_B               120644.92  279.4 x 431.8   11.0 x 17.0  
//  NA_LEDGER            120644.92  431.8 x 279.4   17.0 x 11.0  
//  ISO_A3               124740.00  297.0 x 420.0   11.69 x 16.54
//  ISO_C3               148392.00  324.0 x 458.0   12.76 x 18.03
//  ISO_B3               176500.00  353.0 x 500.0   13.9 x 19.69 
//  CAN_P2               240800.00  430.0 x 560.0   16.93 x 22.05
//  ANSI_C               241289.84  431.8 x 558.8   17.0 x 22.0  
//  ISO_A2               249480.00  420.0 x 594.0   16.54 x 23.39
//  ISO_C2               296784.00  458.0 x 648.0   18.03 x 25.51
//  ISO_B2               353500.00  500.0 x 707.0   19.69 x 27.83
//  CAN_P1               481600.00  560.0 x 860.0   22.05 x 33.86
//  ANSI_D               482579.68  558.8 x 863.6   22.0 x 34.0  
//  ISO_A1               499554.00  594.0 x 841.0   23.39 x 33.11
//  ISO_C1               594216.00  648.0 x 917.0   25.51 x 36.1 
//  ISO_B1               707000.00  707.0 x 1000.0  27.83 x 39.37
//  ANSI_E               965159.36  863.6 x 1117.6  34.0 x 44.0  
//  ISO_A0               999949.00  841.0 x 1189.0  33.11 x 46.81
//  ISO_C0               1189349.00 917.0 x 1297.0  36.1 x 51.06 
//  ISO_B0               1414000.00 1000.0 x 1414.0 39.37 x 55.67

    Psd.printPaperSizesWithInfo();
//  all available paper sizes with info:
//  ISO_4A0[info=ISO 216 4A0, unit=mm, mmdims=1632.0 x 2378.0, indims=64.25 x 93.62]
//  ISO_2A0[info=ISO 216 2A0, unit=mm, mmdims=1189.0 x 1682.0, indims=46.81 x 66.22]
//  ISO_A0[info=ISO 216 A0, unit=mm, mmdims=841.0 x 1189.0, indims=33.11 x 46.81]
//  ISO_A1[info=ISO 216 A1, unit=mm, mmdims=594.0 x 841.0, indims=23.39 x 33.11]
//  ISO_A2[info=ISO 216 A2, unit=mm, mmdims=420.0 x 594.0, indims=16.54 x 23.39]
//  ISO_A3[info=ISO 216 A3, unit=mm, mmdims=297.0 x 420.0, indims=11.69 x 16.54]
//  ISO_A4[info=ISO 216 A4, unit=mm, mmdims=210.0 x 297.0, indims=8.27 x 11.69]
//  ISO_A5[info=ISO 216 A5, unit=mm, mmdims=148.0 x 210.0, indims=5.83 x 8.27]
//  ISO_A6[info=ISO 216 A6, unit=mm, mmdims=105.0 x 148.0, indims=4.13 x 5.83]
//  ISO_A7[info=ISO 216 A7, unit=mm, mmdims=74.0 x 105.0, indims=2.91 x 4.13]
//  ISO_A8[info=ISO 216 A8, unit=mm, mmdims=52.0 x 74.0, indims=2.05 x 2.91]
//  ISO_A9[info=ISO 216 A9, unit=mm, mmdims=37.0 x 52.0, indims=1.46 x 2.05]
//  ISO_A10[info=ISO 216 A10, unit=mm, mmdims=26.0 x 37.0, indims=1.02 x 1.46]
//  ISO_B0[info=ISO 216 B0, unit=mm, mmdims=1000.0 x 1414.0, indims=39.37 x 55.67]
//  ISO_B1[info=ISO 216 B1, unit=mm, mmdims=707.0 x 1000.0, indims=27.83 x 39.37]
//  ISO_B2[info=ISO 216 B2, unit=mm, mmdims=500.0 x 707.0, indims=19.69 x 27.83]
//  ISO_B3[info=ISO 216 B3, unit=mm, mmdims=353.0 x 500.0, indims=13.9 x 19.69]
//  ISO_B4[info=ISO 216 A4, unit=mm, mmdims=250.0 x 353.0, indims=9.84 x 13.9]
//  ISO_B5[info=ISO 216 A5, unit=mm, mmdims=176.0 x 250.0, indims=6.93 x 9.84]
//  ISO_B6[info=ISO 216 A6, unit=mm, mmdims=125.0 x 176.0, indims=4.92 x 6.93]
//  ISO_B7[info=ISO 216 A7, unit=mm, mmdims=88.0 x 125.0, indims=3.46 x 4.92]
//  ISO_B8[info=ISO 216 A8, unit=mm, mmdims=62.0 x 88.0, indims=2.44 x 3.46]
//  ISO_B9[info=ISO 216 A9, unit=mm, mmdims=44.0 x 62.0, indims=1.73 x 2.44]
//  ISO_B10[info=ISO 216 B10, unit=mm, mmdims=31.0 x 44.0, indims=1.22 x 1.73]
//  ISO_C0[info=ISO 269 C0, unit=mm, mmdims=917.0 x 1297.0, indims=36.1 x 51.06]
//  ISO_C1[info=ISO 269 C1, unit=mm, mmdims=648.0 x 917.0, indims=25.51 x 36.1]
//  ISO_C2[info=ISO 269 C2, unit=mm, mmdims=458.0 x 648.0, indims=18.03 x 25.51]
//  ISO_C3[info=ISO 269 C3, unit=mm, mmdims=324.0 x 458.0, indims=12.76 x 18.03]
//  ISO_C4[info=ISO 269 C4, unit=mm, mmdims=229.0 x 324.0, indims=9.02 x 12.76]
//  ISO_C5[info=ISO 269 C5, unit=mm, mmdims=162.0 x 229.0, indims=6.38 x 9.02]
//  ISO_C6[info=ISO 269 C6, unit=mm, mmdims=114.0 x 162.0, indims=4.49 x 6.38]
//  ISO_C7_6[info=ISO 269 C7/6, unit=mm, mmdims=81.0 x 162.0, indims=3.19 x 6.38]
//  ISO_C7[info=ISO 269 C7, unit=mm, mmdims=81.0 x 114.0, indims=3.19 x 4.49]
//  ISO_C8[info=ISO 269 C8, unit=mm, mmdims=57.0 x 81.0, indims=2.24 x 3.19]
//  ISO_C9[info=ISO 269 C9, unit=mm, mmdims=40.0 x 57.0, indims=1.57 x 2.24]
//  ISO_C10[info=ISO 269 C10, unit=mm, mmdims=28.0 x 40.0, indims=1.1 x 1.57]
//  ISO_DL[info=ISO 269 DL, unit=mm, mmdims=110.0 x 220.0, indims=4.33 x 8.66]
//  CAN_P1[info=Canadian 2-9.60M P1, unit=mm, mmdims=560.0 x 860.0, indims=22.05 x 33.86]
//  CAN_P2[info=Canadian 2-9.60M P2, unit=mm, mmdims=430.0 x 560.0, indims=16.93 x 22.05]
//  CAN_P3[info=Canadian 2-9.60M P3, unit=mm, mmdims=280.0 x 430.0, indims=11.02 x 16.93]
//  CAN_P4[info=Canadian 2-9.60M P4, unit=mm, mmdims=215.0 x 280.0, indims=8.46 x 11.02]
//  CAN_P5[info=Canadian 2-9.60M P5, unit=mm, mmdims=140.0 x 215.0, indims=5.51 x 8.46]
//  CAN_P6[info=Canadian 2-9.60M P6, unit=mm, mmdims=107.0 x 140.0, indims=4.21 x 5.51]
//  NA_LETTER[info=North American Letter, unit=in, mmdims=215.9 x 279.4, indims=8.5 x 11.0]
//  NA_GOVERNMENT_LETTER[info=North American Government-Letter, unit=in, mmdims=203.2 x 266.7, indims=8.0 x 10.5]
//  NA_LEGAL[info=North American Legal, unit=in, mmdims=215.9 x 355.6, indims=8.5 x 14.0]
//  NA_JUNIOR_LEGAL[info=North American Junior Legal, unit=in, mmdims=203.2 x 127.0, indims=8.0 x 5.0]
//  NA_LEDGER[info=North American Ledger, unit=in, mmdims=431.8 x 279.4, indims=17.0 x 11.0]
//  NA_TABLOID[info=North American Tabloid, unit=in, mmdims=279.4 x 431.8, indims=11.0 x 17.0]
//  ANSI_A[info=ANSI A, unit=in, mmdims=215.9 x 279.4, indims=8.5 x 11.0]
//  ANSI_B[info=ANSI B, unit=in, mmdims=279.4 x 431.8, indims=11.0 x 17.0]
//  ANSI_C[info=ANSI C, unit=in, mmdims=431.8 x 558.8, indims=17.0 x 22.0]
//  ANSI_D[info=ANSI D, unit=in, mmdims=558.8 x 863.6, indims=22.0 x 34.0]
//  ANSI_E[info=ANSI E, unit=in, mmdims=863.6 x 1117.6, indims=34.0 x 44.0]
  
  }

}
