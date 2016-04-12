package ch09.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.ByteBuffer;
import static java.nio.charset.StandardCharsets.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.mozilla.universalchardet.UniversalDetector;

// 3. Write a program that reads a file containing text and, assuming that most 
// words are English, guesses whether the encoding is ASCII, ISO 8859-1, UTF-8,
//  or UTF-16, and if the latter, which byte ordering is used.

// I found it easier to detect encoding more accurately with non-Enlish languages
// in which non-ASCII characters are frequently found, such as French and Italian.

public class Ch0903GuessEncoding {

  final static String lineSep = System.getProperty("line.separator");
  final static Charset UTF_32BE = Charset.forName("UTF-32BE");
  final static Charset UTF_32LE = Charset.forName("UTF-32LE");
  final static Charset WINDOWS_1252 = Charset.forName("windows-1252");

  final static HashMap<Charset, Byte[]> bom = new HashMap<Charset, Byte[]>(){
    private static final long serialVersionUID = 1L;
    {   put(UTF_16BE, new Byte[]{(byte)0xFE,(byte)0xFF});
    put(UTF_16LE, new Byte[]{(byte)0xFF,(byte)0xFE});
    put(UTF_8,    new Byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});
    put(UTF_32BE, new Byte[]{(byte)0x00,(byte)0x00,(byte)0xFE,(byte)0xFF});
    put(UTF_32LE, new Byte[]{(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x00});
    }      
  };

  public static boolean isContinuationChar(byte b) {
    return -128 <= b && b <= -65;
  }

  private static boolean isTwoBytesSequence(byte b) {
    return -64 <= b && b <= -33;
  }

  private static boolean isThreeBytesSequence(byte b) {
    return -32 <= b && b <= -17;
  }

  private static boolean isFourBytesSequence(byte b) {
    return -16 <= b && b <= -9;
  }

  private static boolean isFiveBytesSequence(byte b) {
    return -8 <= b && b <= -5;
  }

  private static boolean isSixBytesSequence(byte b) {
    return -4 <= b && b <= -3;
  }

  public static boolean isASCII(byte b) {
    return 0 <= b && b <= 127;
  }

  // http://www.i18nqa.com/debug/table-iso8859-1-vs-windows-1252.html
  public static boolean isNotISO88591(byte b) {
    return 128 <= b && b <= 159;
  }

  public static String findEncoding(String fname, int...sampleSize)  {
    // get content from file
    byte[] data = new byte[0]; 
    if (sampleSize.length > 0 && sampleSize[0] > 0) {
      data = new byte[sampleSize[0]];
      FileInputStream fis;
      try {
        fis = new FileInputStream(fname);
        int l = fis.read(data);
        fis.close();
        if (l == -1) return "no input";
      } catch (IOException e1) {
        e1.printStackTrace();
      }

    } else {
      try {
        data = Files.readAllBytes(Paths.get(fname));
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      if (data.length == 0) return "no input";
    }

    // determine if file has BOM and take it as definitive
    // http://www.unicode.org/faq/utf_bom.html#BOM
    if (data.length > 3 && data[0] == bom.get(UTF_32LE)[0]
        && data[1] == bom.get(UTF_32LE)[1]
            && data[2] == bom.get(UTF_32LE)[2]
                && data[3] == bom.get(UTF_32LE)[3])
      return "UTF_32LE";                        

    if (data.length > 3 && data[0] == bom.get(UTF_32BE)[0]
        && data[1] == bom.get(UTF_32BE)[1]
            && data[2] == bom.get(UTF_32BE)[2]
                && data[3] == bom.get(UTF_32BE)[3])
      return "UTF_32BE";        

    if (data.length > 2 && data[0] == bom.get(UTF_8)[0]
        && data[1] == bom.get(UTF_8)[1]
            && data[2] == bom.get(UTF_8)[2])
      return "UTF_8";

    if (data.length > 1 && data[0] == bom.get(UTF_16LE)[0]
        && data[1] == bom.get(UTF_16LE)[1])
      return "UTF_16LE";

    if (data.length > 1 && data[0] == bom.get(UTF_16BE)[0]
        && data[1] == bom.get(UTF_16BE)[1])
      return "UTF_16BE";

    // read thru data testing for ASCII and UTF-8
    // based on CharsetToolkit by Guillaume LAFORGE with mods
    boolean highOrderBit = false;
    boolean validUTF8 = true;
    boolean validASCII = true;
    boolean validISO88591 = true;

    int length = data.length;
    int i = 0; byte b0=0,b1=0,b2=0,b3=0,b4=0,b5=0; 
    while (i < length) {
      b0 = data[i];
      if (!isASCII(b0)) validASCII = false;
      if (isNotISO88591(b0)) validISO88591 = false;
      if (i < length - 1) b1 = data[i + 1];
      if (i < length - 2) b2 = data[i + 2];
      if (i < length - 3) b3 = data[i + 3];
      if (i < length - 4) b4 = data[i + 4];
      if (i < length - 5) b5 = data[i + 5];
      if (b0 < 0) {
        highOrderBit = true;
        if (i < length - 1 && isTwoBytesSequence(b0)) {
          if (!isContinuationChar(b1)) {
            validUTF8 = false;
          } else {
            i++;
          }
        } else if (i < length - 2 && isThreeBytesSequence(b0)) {
          if (!(isContinuationChar(b1) && isContinuationChar(b2))) {
            validUTF8 = false;
          } else {
            i += 2;
          }
        } else if (i < length - 3 && isFourBytesSequence(b0)) {
          if (!(isContinuationChar(b1) && isContinuationChar(b2) && isContinuationChar(b3))) {
            validUTF8 = false;
          } else {
            i += 3;
          }
        }
        else if (i < length - 4 && isFiveBytesSequence(b0)) {
          if (!(isContinuationChar(b1) && isContinuationChar(b2) && isContinuationChar(b3) &&
              isContinuationChar(b4))) {
            validUTF8 = false;
          } else {
            i += 4;
          }
        }
        else if (i < length - 5 && isSixBytesSequence(b0)) {
          if (!(isContinuationChar(b1) && isContinuationChar(b2) && isContinuationChar(b3) &&
              isContinuationChar(b4) && isContinuationChar(b5))) {
            validUTF8 = false;
          } else {
            i += 5;
          }
        } else {
          validUTF8 = false;
        }
      }
      if (!(validUTF8 || validASCII)) {
        break;
      }
      i++;
    }

    if (validASCII && !highOrderBit) return "US_ASCII";
    if (validUTF8) return("UTF_8");

    // reduce futher possibilities with CharsetDecoder decoding tests
    TreeMap<Charset,String> charsets =  new TreeMap<Charset,String>(){
      private static final long serialVersionUID = 1L;
      {   put(ISO_8859_1, "");
      put(UTF_16, "");
      put(UTF_16BE, "");
      put(UTF_16LE, "");
      put(UTF_32BE, "");
      put(UTF_32LE, "");
      put(WINDOWS_1252, "");
      }         
    };

    if (!validISO88591) charsets.remove(ISO_8859_1);

    ByteBuffer byteBuf; CharsetDecoder decoder;
    for (Charset cs : charsets.keySet()) {
      byteBuf = ByteBuffer.wrap(Arrays.copyOf(data, data.length));
      decoder = cs.newDecoder();
      decoder.onMalformedInput(CodingErrorAction.REPORT);
      decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
      try {          
        decoder.decode(byteBuf);
        charsets.put(cs, "true");
      } catch (Exception e) {
        charsets.put(cs, "false");
      }
    }

    // ad hoc pruning

    // For example this prefers ISO_8859_1 if both it and WINDOWS_1252 are detected.
    // That would be more advisable if running on Linux vs Windows.
    if (charsets.containsKey(ISO_8859_1)
        && charsets.get(ISO_8859_1).equals("true") 
        && charsets.containsKey(WINDOWS_1252)
        && charsets.get(WINDOWS_1252).equals("true")) 
      charsets.remove(WINDOWS_1252);

    if (charsets.containsKey(UTF_16BE)
        && charsets.get(UTF_16BE).equals("true") 
        && charsets.containsKey(UTF_16LE)
        && charsets.get(UTF_16LE).equals("true")
        && charsets.containsKey(UTF_16)
        && charsets.get(UTF_16).equals("true")) {
      charsets.remove(UTF_16BE);
      charsets.remove(UTF_16LE);
    }

    if (charsets.containsKey(UTF_16)
        && charsets.get(UTF_16).equals("true") 
        && charsets.containsKey(UTF_16LE)
        && charsets.get(UTF_16LE).equals("true"))
      charsets.remove(UTF_16LE);

    if (charsets.containsKey(UTF_16)
        && charsets.get(UTF_16).equals("true") 
        && charsets.containsKey(UTF_16BE)
        && charsets.get(UTF_16BE).equals("true"))
      charsets.remove(UTF_16BE);

    if (charsets.containsKey(UTF_16)
        && charsets.get(UTF_16).equals("true")
        && charsets.containsKey(ISO_8859_1)
        && charsets.get(ISO_8859_1).equals("true"))
      charsets.remove(ISO_8859_1);

    StringBuffer sb = new StringBuffer();

    for(Entry<Charset,String> e : charsets.entrySet())
      if (e.getValue().equals("true"))
        sb.append(e.getKey()+",");

    return sb.toString().replaceFirst(",$", "");

  }

  public static void universalCharDet(String fileName) throws IOException {
    // http://code.google.com/p/juniversalchardet/
    byte[] buf = new byte[4096];
    java.io.FileInputStream fis = new FileInputStream(fileName);

    // (1)
    UniversalDetector detector = new UniversalDetector(null);

    // (2)
    int nread;
    while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
      detector.handleData(buf, 0, nread);
    }
    // (3)
    detector.dataEnd();
    fis.close();

    // (4)
    String encoding = detector.getDetectedCharset();
    if (encoding != null) {
      System.out.println("Detected encoding = " + encoding);
    } else {
      System.out.println("No encoding detected.");
    }

    // (5)
    detector.reset();
  }

  public static void main(String[] args)  {

    System.out.println("findEncoding results:");
    try (Stream<Path> testFiles = Files.list(Paths.get("i18n/"))) {
      testFiles.forEach(f -> {
        System.out.println("\n"+f.toString().replaceFirst("^i18n\\\\","")+":");
        System.out.println(findEncoding(f.toString()));  
      });    
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Does Google do better?
    System.out.println("\nuniversalCharDet results:");
    try (Stream<Path> testFiles = Files.list(Paths.get("i18n/"))) {
      testFiles.forEach(f -> {
        System.out.println("\n"+f.toString().replaceFirst("^i18n\\\\","")+":");
        try {
          universalCharDet(f.toString());
        } catch (Exception e) {
          e.printStackTrace();
        }  
      });    
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

