package st;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/34hash/OneWay.java
 *  http://algs4.cs.princeton.edu/34hash/OneWay.java.html
 *  Compilation:  javac OneWay.java
 *  Execution:    java OneWay password
 *  Dependencies: System.out.java
 *
 *  Comptue the SHA1 of the command line argument.
 *
 *
 * % java OneWay "The quick brown fox jumps over the lazy dog"
 * 2fd4e1c6 7a2d28fc ed849ee1 bb76e739 1b93eb12
 *
 *  % java OneWay test
 *  a94a8fe5 ccb19ba6 1c4c0873 d391e987 982fbbd3
 *
 *  % java OneWay ""
 *  da39a3ee 5e6b4b0d 3255bfef 95601890 afd80709
 *
 ******************************************************************************/

import java.security.MessageDigest;

public final class OneWay {

    public static void main(String[] args) {
        String password = args[0];

        // get SHA1 algorithm object and compute SHA1 of password
        MessageDigest sha1;
        byte[] bytes;
        try {
            sha1 = MessageDigest.getInstance("SHA1");
            bytes = sha1.digest(password.getBytes("ISO-8859-1"));
        }
        catch (java.security.NoSuchAlgorithmException e) {
            System.out.println(e);
            return;
        }
        catch (java.io.UnsupportedEncodingException e) {
            System.out.println(e);
            return;
        }

        // convert bytes to hex, careful to handle leading 0s and 2s complement
        String hex = "0123456789abcdef";
        for (int i = 0; i < bytes.length; i++) {
            if (i % 4 == 0) System.out.print(" ");
            System.out.print(hex.charAt((bytes[i] & 0xF0) >> 4));
            System.out.print(hex.charAt((bytes[i] & 0x0F) >> 0));
        }
        System.out.println();
    }


}

