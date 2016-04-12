package ch06.generics;

import utils.Pair;

public class Ch0607Pair {

  public static void main(String[] args) {

    Pair<String> p = new Pair<>("hello", "world");
    System.out.println(p.getFirst()); // hello
    System.out.println(p.getSecond()); // world

  }
}
