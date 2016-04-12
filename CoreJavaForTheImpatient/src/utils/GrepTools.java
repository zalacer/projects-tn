package utils;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

// Ch815Grep
public class GrepTools {
    
    public static List<String> grepFile(String s, String r) {
        List<String> l = new ArrayList<>();
        Path p = Paths.get(s);
        File f = p.toFile();
        if (!(f.isFile() && f.canRead()))
            return l;
        Pattern rc = Pattern.compile(r);

        try (Stream<String> lines = Files.lines(p)) {
            lines.filter(rc.asPredicate()).forEach(x -> l.add(x));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return l;
    }
    
    public static List<String> grepFile(String s, Pattern r) {
        List<String> l = new ArrayList<>();
        Path p = Paths.get(s);
        File f = p.toFile();
        if (!(f.isFile() && f.canRead()))
            return l;

        try (Stream<String> lines = Files.lines(p)) {
            lines.filter(r.asPredicate()).forEach(x -> l.add(x));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return l;
    }

    public static void grep(String d, String r) {
        String outPath = "grep.out";
        //System.out.println("running grep(" + d + ", " + r + ")");
        Path p = Paths.get(d);
        //System.out.println("p = " + p);
        File f = p.toFile();
        if ((f.isFile() && f.canRead())) {
            //System.out.println(f + " is file and readable");
            List<String> o = grepFile(d, r);
            if (o.size() > 0) {
                System.out.println(f + ": ");
                for (String e : o)
                    System.out.println("  " + e);
            }
            return;
        }
//        System.out.println(f + " is not a file");
//        if (!f.isDirectory())
//            System.out.println(f + " is not a directory");
//        if (!f.canRead())
//            System.out.println(f + " is not readable");
        
        if (!(f.isDirectory() && f.canRead())) {
            System.out.println(f + " is not a directory or not readable");
            return;
        }
        
        //System.out.println(f + " is dir and readable");
        
        try {
            Writer w = Files.newBufferedWriter(Paths.get(outPath));
            Pattern rc = Pattern.compile(r);
            Stream<Path> entries = Files.walk(p);
            Files.walk(p).filter(e -> e.toFile().isFile())
                .filter(e -> !e.getFileName().toString().endsWith(".class"))
                .filter(e -> !e.getFileName().toString().endsWith(".jar"))
                .forEach(e -> {
                    // System.out.println("processing "+e);
                    List<String> o = grepFile(e.toString(), rc);
                    if (o.size() > 0) {
                        // System.out.println(e + ": ");
                        String s1 = e.toString() + ": \n";
                        try {
                            //w.write(s1, 0, s1.length());
                            w.write(s1);
                            for (String g : o) {
                                // System.out.println(" " + g);
                                String s2 = "  " + g + "\n";
                                w.write(s2);
                            }
                            //System.out.println();
                            String s3 = "\n";
                            w.write(s3);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                entries.close();
                w.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

}
