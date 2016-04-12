package utils;

//  for ch06.Ch0601StackTest

public class Stack2<E> extends ArrayList<E> {
  private static final long serialVersionUID = -7697535938318984731L;

  public boolean push(E e) {
    return add(e);
  }

  public E pop() {
    return remove(size() - 1);
  }

}
