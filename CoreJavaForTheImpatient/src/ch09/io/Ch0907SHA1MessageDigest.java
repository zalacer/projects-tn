package ch09.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

// 7. Look up the API documentation for the MessageDigest class and write a
// program that computes the SHA-1 digest of a file. Feed blocks of bytes to the
// MessageDigest object with the update method, then display the result of
// calling digest. Verify that your program produces the same result as the
// sha1sum utility.

public class Ch0907SHA1MessageDigest {

  public static String computeSHA1(String fileName) {
    MessageDigest d2 = null;
    try {
      d2 = MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    byte[] b = null;
    try {
      b = Files.readAllBytes(Paths.get(fileName));
    } catch (IOException e) {
      e.printStackTrace();
    }
    d2.update(b);
    byte[] h2 = d2.digest();
    return DatatypeConverter.printHexBinary(h2).toLowerCase();
  }

  public static void main(String[] args) {

    String sha1 = computeSHA1("books/AlicesAdventuresInWonderland3339.txt");

    // using sha1sum, the SHA-1 of books/AlicesAdventuresInWonderland3339.txt is:
    //   8b21521b3ae1416a177835f2200fc50f87f29b24
    String sha1sum = "8b21521b3ae1416a177835f2200fc50f87f29b24";
    assert sha1sum.equals(sha1);

  }

}
