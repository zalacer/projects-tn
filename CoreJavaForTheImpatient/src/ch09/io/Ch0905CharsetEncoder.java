package ch09.io;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.bind.DatatypeConverter;

// 5. When an encoder of a Charset with partial Unicode coverage can’t encode a
// character, it replaces it with a default—usually, but not always, the encoding of "?".
// Find all replacements of all available character sets that support encoding. Use the
// newEncoder method to get an encoder, and call its replacement method to get
// the replacement. For each unique result, report the canonical names of the charsets
// that use it.

public class Ch0905CharsetEncoder {
    
    // http://stackoverflow.com/questions/923863/converting-a-string-to-hexadecimal-in-java
    public static String toHex(String arg) {
        return String.format("%x", new BigInteger(1, arg.getBytes(UTF_8)));
    }
    
    // http://stackoverflow.com/questions/8890174/in-java-how-do-i-convert-a-hex-string-to-a-byte
    public static String toHexString(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    // http://stackoverflow.com/questions/8890174/in-java-how-do-i-convert-a-hex-string-to-a-byte
    public static byte[] toByteArray(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }
    
    public static String byteArray2String(byte[] b) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < b.length - 1; i++) {
            sb.append(String.format("%x,",b[i]));
        }
        sb.append(String.format("%x]",b[b.length - 1]));
        return sb.toString();
    }
    
    // this was used for testing storage of byte[] as String
    // and conversion of String back to byte[]
    //  private static final byte[] uniBytes = {
    //      (byte)0xEF, (byte)0xBF, (byte)0xBD,  // -17, -65, -67 
    //      (byte)0x47, (byte)0x4D // 'G', 'M'
    //  };

    public static void main(String[] args) {

        TreeMap<String,TreeSet<String>> chr = new TreeMap<>();
        
        SortedMap<String, Charset> charsets = Charset.availableCharsets();
        String v;
        for (Entry<String,Charset> e : charsets.entrySet())
            // System.out.println(e.getKey()+" : "+e.getValue());
            if (e.getValue().canEncode()) {
                v = toHexString(e.getValue().newEncoder().replacement());
                if (chr.putIfAbsent(v, new TreeSet<String>()) == null) {
                    chr.get(v).add(e.getKey());
                } else {
                    chr.get(v).add(e.getKey());
                }
            }
        
        for (Entry<String,TreeSet<String>> e : chr.entrySet())
            System.out.printf("%-8s : %s\n",e.getKey(),e.getValue());
        
// Replacement    Charsets 
//    0000FFFD : [UTF-32, UTF-32BE, X-UTF-32BE-BOM]
//    2129     : [ISO-2022-JP, ISO-2022-JP-2, x-JIS0208, x-windows-50220, x-windows-50221, x-windows-iso2022jp]
//    2244     : [JIS_X0212-1990]
//    3F       : [Big5, Big5-HKSCS, CESU-8, EUC-JP, EUC-KR, GB18030, GB2312, GBK, IBM-Thai, IBM00858, IBM01140, IBM01141, IBM01142, IBM01143, IBM01144, IBM01145, IBM01146, IBM01147, IBM01148, IBM01149, IBM037, IBM1026, IBM1047, IBM273, IBM277, IBM278, IBM280, IBM284, IBM285, IBM290, IBM297, IBM420, IBM424, IBM437, IBM500, IBM775, IBM850, IBM852, IBM855, IBM857, IBM860, IBM861, IBM862, IBM863, IBM864, IBM865, IBM866, IBM868, IBM869, IBM870, IBM871, IBM918, ISO-2022-KR, ISO-8859-1, ISO-8859-13, ISO-8859-15, ISO-8859-2, ISO-8859-3, ISO-8859-4, ISO-8859-5, ISO-8859-6, ISO-8859-7, ISO-8859-8, ISO-8859-9, JIS_X0201, KOI8-R, KOI8-U, Shift_JIS, TIS-620, US-ASCII, UTF-8, windows-1250, windows-1251, windows-1252, windows-1253, windows-1254, windows-1255, windows-1256, windows-1257, windows-1258, windows-31j, x-Big5-HKSCS-2001, x-Big5-Solaris, x-EUC-TW, x-IBM1006, x-IBM1025, x-IBM1046, x-IBM1097, x-IBM1098, x-IBM1112, x-IBM1122, x-IBM1123, x-IBM1124, x-IBM1166, x-IBM1381, x-IBM1383, x-IBM33722, x-IBM737, x-IBM833, x-IBM856, x-IBM874, x-IBM875, x-IBM921, x-IBM922, x-IBM942, x-IBM942C, x-IBM943, x-IBM943C, x-IBM948, x-IBM949, x-IBM949C, x-IBM950, x-IBM964, x-IBM970, x-ISCII91, x-ISO-2022-CN-CNS, x-ISO-2022-CN-GB, x-Johab, x-MS932_0213, x-MS950-HKSCS, x-MS950-HKSCS-XP, x-MacArabic, x-MacCentralEurope, x-MacCroatian, x-MacCyrillic, x-MacDingbat, x-MacGreek, x-MacHebrew, x-MacIceland, x-MacRoman, x-MacRomania, x-MacSymbol, x-MacThai, x-MacTurkish, x-MacUkraine, x-PCK, x-SJIS_0213, x-euc-jp-linux, x-eucJP-Open, x-iso-8859-11, x-mswin-936, x-windows-874, x-windows-949, x-windows-950]
//    426F     : [x-IBM300]
//    6F       : [x-IBM1364, x-IBM930, x-IBM933, x-IBM935, x-IBM937, x-IBM939]
//    FDFF     : [UTF-16LE, x-UTF-16LE-BOM]
//    FDFF0000 : [UTF-32LE, X-UTF-32LE-BOM]
//    FEFE     : [x-IBM834]
//    FFFD     : [UTF-16, UTF-16BE]
                        
        // Premilinary testing to determine how to store data for exercise
        // showed byte[] can be stored as String and recovered from it. 
        // Auxiliary methods for this are toHexString and toByteArray using DatatypeConverter
//        byte b = (byte)0xEF;
//        System.out.println(b); //-17
//        byte[] ba = new byte[]{b};
//        System.out.println(toHex(new String(ba)));
//        Byte by = new Byte(b);
//        System.out.println(Integer.toHexString(by.intValue())); //ffffffef
//        System.out.printf("%x\n", b); //ef
//        String bstring = String.format("%x",b);
//        System.out.println(bstring); //ef
//        byte[] bar = new BigInteger(bstring,16).toByteArray();
//        System.out.println(bar.length); // 2
//        System.out.println(bar[0] +", "+bar[1]); // 0, -17
//        System.out.println(new String(uniBytes)); // �GM
//        System.out.println(byteArray2String(uniBytes)); // [ef,bf,bd,47,4d]
//        byte[] bar2 = new BigInteger("47",16).toByteArray();
//        System.out.println(bar2.length); // 1
//        System.out.println(bar2[0]); // 71
//        System.out.println(toHexString(uniBytes)); // EFBFBD474D
//        byte[] newUniBytes = toByteArray(toHexString(uniBytes));
//        System.out.println(byteArray2String(newUniBytes)); // [ef,bf,bd,47,4d]
//        assert Arrays.equals(uniBytes,newUniBytes); 
        
    }

}
