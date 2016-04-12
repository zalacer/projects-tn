package ch08.streams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.stream.Collectors;

// 9. Given a finite stream of strings, find the average string length.

public class Ch0809AvgLength {

   // isolating strings by splitting on whitespace
  public static double findAverageWordLength(String fileName) throws IOException {
    DoubleSummaryStatistics summary = Files.newBufferedReader(Paths.get(fileName)).lines()
        .map(l -> l.split("\\s+")).flatMap(Arrays::stream)
        .collect(Collectors.summarizingDouble(String::length));
    return summary.getAverage();
  }

  public static void main(String[] args) throws Exception {

    System.out.println("average = "+findAverageWordLength("words"));
    // average = 9.323884133722638

  }

}
