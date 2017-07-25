package st;

import static v.ArrayUtils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

// based on LookupCSV modified for possible multiple values per key for ex3512

import st.RedBlackBSTQ;

public class LookupCSVQ {

  // Do not instantiate.
  private LookupCSVQ() { }

  public static void main(String[] args) throws FileNotFoundException {
    RedBlackBSTQ<String, String> st = new RedBlackBSTQ<>();
    Scanner sc = new Scanner(new File(args[0]));
    int kf = Integer.parseInt(args[1]);
    int vf = Integer.parseInt(args[2]);
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      String[] tokens = line.split(",");
      String k = tokens[kf]; 
      String v = tokens[vf];
      st.put(k, v);
    }
    sc.close();
 
    System.out.println("verifying input from "+args[0]+":");
    System.out.println("st = "+st.tos());
    System.out.println("st.size() = "+st.size());
    Iterator<String> it = st.keys().iterator();
    System.out.println("key  values");
    while (it.hasNext()) {
      String k = it.next(); 
      System.out.print(k+"    "); par(st.getValues(k)); 
    }

    System.out.println("\nscanning System.in for keys:");
    sc = new Scanner(System.in);
    while (sc.hasNext()) {
      String k = sc.next();
      System.out.print("input key = "+k+":  ");
      if (!k.equals("") && st.contains(k)) { 
        System.out.print("values = "); par(st.getValues(k)); }
      else System.out.println("key not found");    
    }
    sc.close();

    //        while (!StdIn.isEmpty()) {
    //            String s = StdIn.readString();
    //            if (st.contains(s)) st.geAtQueue(s).show();
    //            else                System.out.println("Not found");
    //        }
  }

  //*  % java LookupCSV amino.csv 0 3     % java LookupCSV ip.csv 0 1 
  //*  TTA                                www.google.com 
  //*  Leucine                            216.239.41.99 
  //*  ABC                               
  //*  Not found                          % java LookupCSV ip.csv 1 0 
  //*  TCT                                216.239.41.99 
  //*  Serine                             www.google.com 
  //*                                 
  //*  % java LookupCSV amino.csv 3 0     % java LookupCSV DJIA.csv 0 1 
  //*  Glycine                            29-Oct-29 
  //*  GGG                                252.38 
  //*                                     20-Oct-87 
  //*                                     1738.74
}
