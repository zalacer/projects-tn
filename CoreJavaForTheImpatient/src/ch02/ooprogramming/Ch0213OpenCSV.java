package ch02.ooprogramming;

import java.io.FileReader;

import com.opencsv.CSVReader;

// 13. Download the JAR file for OpenCSV from
// http://opencsv.sourceforge.net. Write a class with a main method that
// reads a CSV file of your choice and prints some of the content. There is sample code
// on the OpenCSV web site. You haven’t yet learned to deal with exceptions. Just use
// the following header for the main method:
//   public static void main(String[] args) throws Exception
// The point of this exercise is not to do anything useful with CSV files, but to practice
// using a library that is delivered as a JAR file.

public class Ch0213OpenCSV {

  public static void main(String[] args) throws Exception {

    CSVReader reader = new CSVReader(new FileReader("csvtest.txt"));
    String [] nextLine;
    while ((nextLine = reader.readNext()) != null) {
      // nextLine[] is an array of values from the line
      for(String e: nextLine) System.out.print(e+", ");
      System.out.println();
    }
    //        one, two, three, 
    //        four, five, six, 
    //        hello, world, 
    reader.close();
  }

}
