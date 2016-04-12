package ch06.generics;

import utils.PairMinMax;

// 8. Modify the class of the preceding exercise by adding methods max and min,
// getting the larger or smaller of the two elements. Supply an appropriate type
//  bound for E.

public class Ch0608PairMinMax {

  public static void main(String[] args) {

    PairMinMax<Double> p1 = new PairMinMax<>(1.3, 2.9);
    System.out.println(p1.max()); // 2.9
    System.out.println(p1.min()); // 1.3

    PairMinMax<Double> p2 = new PairMinMax<>(5.0, 5.0);
    System.out.println(p2.max()); // 5.0
    System.out.println(p2.min()); // 5.0

    PairMinMax<Double> p3 = new PairMinMax<>(5.0, null);
    System.out.println(p3.max()); // 5.0
    System.out.println(p3.min()); // 5.0

    PairMinMax<Double> p4 = new PairMinMax<>(null, 5.0);
    System.out.println(p4.max()); // 5.0
    System.out.println(p4.min()); // 5.0

    PairMinMax<Double> p5 = new PairMinMax<>(null, null);
    System.out.println(p5.max()); // null
    System.out.println(p5.min()); // null

  }

}
