package ch08.streams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

// 10. Given a finite stream of strings, find all strings of maximum length.

public class Ch0810MaxStrings {

  public static Object[] findWordsWithMaxLength(String fileName) throws IOException {
    int max = Files.newBufferedReader(Paths.get(fileName)).lines()
        .map(l -> l.split("\\s+")).flatMap(Arrays::stream)
        .collect(Collectors.summarizingInt(String::length)).getMax();
    return Files.newBufferedReader(Paths.get(fileName)).lines()
        .map(l -> l.split("\\s+")).flatMap(Arrays::stream)
        .filter(w -> w.length() == max).toArray();
  }

  public static void main(String[] args) throws Exception {

    Object[] oa = findWordsWithMaxLength("words");
    for (Object o : oa) System.out.println(o+" : length = "+((String)o).length());
    // pneumonoultramicroscopicsilicovolcanoconiosis : length = 45

  }

}
