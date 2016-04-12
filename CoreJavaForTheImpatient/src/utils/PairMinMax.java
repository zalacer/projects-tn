package utils;

// for Ch0608PairMinMax
// 7. Implement a class Pair<E> that stores a pair of elements of type E. Provide
// accessors to get the first and second element.
// 8. Modify the class of the preceding exercise by adding methods max and min,
// getting the larger or smaller of the two elements. Supply an appropriate type bound
// for E.

public class PairMinMax<E extends Comparable<? super E>> {

  private E first;
  private E second;

  public PairMinMax(){};

  public PairMinMax(E first, E second) {
    this.first = first;
    this.second = second;
  }

  public E getFirst() {
    return first;
  }

  public void setFirst(E first) {
    this.first = first;
  }

  public E getSecond() {
    return second;
  }

  public void setSecond(E second) {
    this.second = second;
  }

  public E max() {
    if (first == null && second == null) {
      return null;
    } else if (first == null) {
      return second;
    } else if (second == null) {
      return first;
    } else {
      return first.compareTo(second) >= 0 ? first : second;
    }
  }

  public E min() {
    if (first == null && second == null) {
      return null;
    } else if (first == null) {
      return second;
    } else if (second == null) {
      return first;
    } else {
      return first.compareTo(second) < 0 ? first : second;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((first == null) ? 0 : first.hashCode());
    result = prime * result + ((second == null) ? 0 : second.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    @SuppressWarnings("rawtypes")
    PairMinMax other = (PairMinMax) obj;
    if (first == null) {
      if (other.first != null)
        return false;
    } else if (!first.equals(other.first))
      return false;
    if (second == null) {
      if (other.second != null)
        return false;
    } else if (!second.equals(other.second))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Pair [first=");
    builder.append(first);
    builder.append(", second=");
    builder.append(second);
    builder.append("]");
    return builder.toString();
  }



}
