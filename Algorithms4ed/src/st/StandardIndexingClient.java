package st;

import edu.princeton.cs.algs4.StdIn;

// from p370
public class StandardIndexingClient {

  public static void main(String[] args) {
    ST<String, Integer> st;
    st = new ST<String, Integer>();
    for (int i = 0; !StdIn.isEmpty(); i++) {
      String key = StdIn.readString();
      st.put(key, i);
    }
    for (String s : st.keys())
      System.out.println(s + " " + st.get(s));

  }

}
