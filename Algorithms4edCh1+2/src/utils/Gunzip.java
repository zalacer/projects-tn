package utils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;
import static java.util.zip.GZIPInputStream.GZIP_MAGIC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import io.ByteArrayInputStream;
import io.ByteArrayOutputStream;

public class Gunzip {
  
  private static boolean isGzip(byte[] bytes) {
    // https://www.javacodegeeks.com/2015/01/working-with-gzip-and-compressed-data.html
    return bytes[0] == (byte) GZIP_MAGIC && bytes[1] == (byte) (GZIP_MAGIC >>> 8);
  }
  
  private static byte[] toByteArray(String s) {
    return getDecoder().decode(s);
  }
  
  private static String toB64String(byte[] b) {
    return getEncoder().withoutPadding().encodeToString(b);
  }
  
  public static final String gzip(String data) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
    try (GZIPOutputStream gzipos = new GZIPOutputStream(bos)) {
      gzipos.write(data.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return toB64String(bos.toByteArray());
  }

  public static void gzipString2File(String s, String pathName) {
    Path p = Paths.get(pathName);
    if (Files.exists(p)) {
      System.out.println("gzipString2File: overwriting " + pathName);
    } else {
      try {
        Files.createFile(p);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    String gzipped = gzip(s);

    try {
      Files.write(p, gzipped.getBytes(UTF_8));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static final String gunzip(String compressed) {

    byte[] ba = toByteArray(compressed);

    if (!isGzip(ba)) return compressed;

    ByteArrayInputStream bis = new ByteArrayInputStream(ba);
    StringBuilder sb = new StringBuilder();
    String line = null;
    try (GZIPInputStream gis = new GZIPInputStream(bis);
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"))) {
      while ((line = br.readLine()) != null)
        sb.append(line);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }
  

  public static String gunzipFile2String(String pathName) {
    Path p = Paths.get(pathName);
    if (!Files.exists(p)) {
      System.out.println("gunzipFile2String: " + pathName + " does not exist");
      return "";
    }

    String output = null;
    try {
      output = new String(Files.readAllBytes(p), UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (Objects.nonNull(output))
      output = gunzip(output);

    return Objects.isNull(output) ? "" : output;
  }


  public static void main(String[] args) {

  }

}
