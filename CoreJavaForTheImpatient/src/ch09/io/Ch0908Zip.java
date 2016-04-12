package ch09.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//8. Write a utility method for producing a ZIP file containing 
// all files from a directory and its descendants.

public class Ch0908Zip {

  public static int getMinEnclosingSubPathIndex(Path a, Path b, int min) {
    Path na = a.normalize();
    Path nb = b.normalize();
    if (na.equals(nb)) return 0;
    int ca = na.getNameCount();
    int cb = nb.getNameCount();
    if (ca <= 1 && ca == cb) return 0;
    if (min <= 0) min = ca <= cb ? ca : cb;
    int index = 0;
    for(int i = 0; i < min; i++)
      if (na.subpath(i, i+1).equals(nb.subpath(i, i+1))) {
        index++;
      } else break;
    //return index > 0 ? na.subpath(0, common) : Paths.get("");
    return index > 0 ? index - 1 : 0;
  }

  public static int getMinEnclosingSubPathIndex(Path...paths) {
    if (paths.length <= 1) return 0;
    if (paths.length == 2)
      return getMinEnclosingSubPathIndex(paths[0], paths[1], 0);
    int min = Integer.MAX_VALUE;
    Path[] npaths = new Path[paths.length];
    Path tp; int nc;
    for(int i = 0; i < paths.length; i++) {
      tp = paths[i].normalize();
      nc = tp.getNameCount();
      if (nc < min) min = nc;
      npaths[i] = tp;  
    }
    int index = Integer.MAX_VALUE;
    int tmp = 0;
    for(int i = 0; i < npaths.length - 1; i++) {
      tmp = getMinEnclosingSubPathIndex(paths[i], paths[i+1], min);
      if (tmp == 0) return 0;
      if (tmp  < index) index = tmp;
    }

    return index;
  }

  public static void createZip(String zipFile, String sourceDir) {
    // Create a hierarchical zip containing all of sourceDir and
    // relativized to include only sourceDir and its contents
    // to see what this means compare output with that of 
    // createZipHierarchicalNotRelativized()
    File f = new File(sourceDir);
    if (!( f.isDirectory() || f.isFile()))
      throw new IllegalArgumentException("sourceDir must resolve to file or dir");
    Set<Path> paths = new HashSet<>();
    try {
      if (f.isFile()) { 
        paths.add(Paths.get(sourceDir));
      } else {
        Files.walk(Paths.get(sourceDir))
        .filter(p -> p.toFile().isFile())
        .forEach(p -> paths.add(p));
      }
      int relativisor = getMinEnclosingSubPathIndex(paths.toArray(new Path[paths.size()]));
      Path zipPath = Paths.get(zipFile);
      URI uri = new URI("jar", zipPath.toUri().toString(), null);
      Map<String, String> csm = Collections.singletonMap("create", "true");
      try (FileSystem zipfs = FileSystems.newFileSystem(uri,csm)) {
        Path t;
        for(Path p : paths) {
          Path prel = p.subpath(relativisor, p.getNameCount());
          t = zipfs.getPath("/").resolve(prel.toString());
          Files.createDirectories(t.getParent());
          Files.copy(p, t);
        }
      }
    } catch (Exception e) {
      e.printStackTrace(); 
    } 
  }

  public static void createZipHierarchicalNotRelativized(String zipFile, String sourceDir) {
    // Create a hierarchical zip containing all of sourceDir but not relativized
    // to sourceDir, i.e. contains paths up to filesystem root.
    File f = new File(sourceDir);
    if (!( f.isDirectory() || f.isFile()))
      throw new IllegalArgumentException("sourceDir must resolve to file or dir");
    Set<Path> paths = new HashSet<>();  
    try {
      if (f.isFile()) { 
        paths.add(Paths.get(sourceDir));
      } else {
        Files.walk(Paths.get(sourceDir))
        .filter(p -> p.toFile().isFile())
        .forEach(p -> paths.add(p));
      }

      Path zipPath = Paths.get(zipFile);
      URI uri = new URI("jar", zipPath.toUri().toString(), null);
      Map<String, String> csm = Collections.singletonMap("create", "true");
      try (FileSystem zipfs = FileSystems.newFileSystem(uri,csm)) {
        Path t;
        for(Path p : paths) {
          t = zipfs.getPath("/").resolve(p.toString());
          Files.createDirectories(t.getParent());
          Files.copy(p, t);
        }
      }
    } catch (Exception e) {
      e.printStackTrace(); 
    } 
  }

  public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

    String dir = "C:/java/ziptestdir";

    createZip("CH0908Zip0.zip",dir); 
    Thread.sleep(3000); // avoid collision of temporary files
    createZipHierarchicalNotRelativized("CH0908ZipHnotR.zip", dir);


  }

}
