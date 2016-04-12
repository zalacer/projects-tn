package ch09.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import static java.nio.charset.StandardCharsets.*;
import java.util.Base64;

//import org.apache.commons.codec.binary.Base64;

// 9. Using the URLConnection class, read data from a password-protected web page
// with “basic” authentication. Concatenate the user name, a colon, and the password,
// and compute the Base64 encoding:
//   String input = username + “:” + password;
//   String encoding = Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
// Set the HTTP header Authorization to the value "Basic " + encoding.
// Then read and print the page contents.

public class Ch0909URLConnection {

  public static void getProtectedURL(String u, String name, String password) {
    try {
      String input = name + ":" + password;
      String encoding = Base64.getEncoder().encodeToString(input.getBytes(UTF_8));
      URL url = new URL(u);
      URLConnection urlConnection = url.openConnection();
      urlConnection.setRequestProperty("Authorization", "Basic " + encoding);
      InputStream is = urlConnection.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);

      int numCharsRead;
      char[] charArray = new char[1024];
      StringBuffer sb = new StringBuffer();
      while ((numCharsRead = isr.read(charArray)) > 0) {
        sb.append(charArray, 0, numCharsRead);
      }
      String result = sb.toString();

      System.out.println("*** BEGIN ***");
      System.out.println(result);
      System.out.println("*** END ***");
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {

    // update parameters for your context
    getProtectedURL("http://10.176.32.59", "xxx", "yyy");

  }

}
