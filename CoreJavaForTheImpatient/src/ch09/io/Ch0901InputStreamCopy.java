package ch09.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

// 1. Write a utility method for copying all of an InputStream to an
// OutputStream, without using any temporary files. Provide another solution,
// without a loop, using operations from the Files class, using a temporary file.

public class Ch0901InputStreamCopy {

  // from text ch09.1.3 p273
  public static void copy(InputStream in, OutputStream out) throws IOException {
    final int BLOCKSIZE = 1024;
    byte[] bytes = new byte[BLOCKSIZE];
    int len;
    while ((len = in.read(bytes)) != -1) out.write(bytes, 0, len);
  }

  public static void copy2(InputStream in, OutputStream out) throws IOException {
    Path f = Files.createTempFile(null, null);
    Files.copy(in, f, StandardCopyOption.REPLACE_EXISTING);
    Files.copy(f, out);
  }

  public static void main(String[] args) throws IOException {

    Path p1 = Paths.get("file1");
    Path p2 = Paths.get("file2");

    InputStream in = Files.newInputStream(p1);
    OutputStream out = Files.newOutputStream(p2, StandardOpenOption.CREATE);
    copy2(in,out);
    String content = new String(Files.readAllBytes(p2), StandardCharsets.UTF_8);
    System.out.println(content);

  }

}
