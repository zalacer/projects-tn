package utils;

import java.util.ArrayList;
import java.util.Collection;
//import java.util.List;
import java.util.Random;
import java.security.SecureRandom;

public class RandomlySelectableArrayList<E> extends ArrayList<E> {
  private static final long serialVersionUID = 4017723334311641674L;

  public RandomlySelectableArrayList() {
    super();
  }

  public RandomlySelectableArrayList(Collection<? extends E> c) {
    super(c);
  }

  private static Random generator = new SecureRandom(); //new Random();

  public static int nextInt(int low, int high) {
    return low + generator.nextInt(high - low + 1);
  }

  public E randomElement() {
    if (this.size() == 0) return null;
    return this.get(nextInt(0,this.size() - 1));
  }

  public E getRandomElement() {
    if (this.size() == 0) return null;
    return this.get(nextInt(0,this.size() - 1));
  }

  public void setSeed(long seed) {
    generator.setSeed(seed);
  }

}
