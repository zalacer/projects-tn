package utils;

//for ch06.Ch0601StackTest from utils.ArrayList

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

// 1. Implement a class Stack<E> that manages an array list of elements of type E.
// Provide methods push, pop, and isEmpty.

// Cloned sources of version 8u40-b25 AbstractList and ArrayList.
// In AbstractList changed modCount on line 612 from protected to public
// and changed removeRange on line 677 from protected to public.
// In ArrayList changed occurrences of "ArrayList" to "Stack", defined
// push(e) to return add(e), defined pop(e) to return remove(size -1), and
// added all necessary imports. isEmpty is already defined in ArrayList.

public class Stack<E> extends AbstractList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
  private static final long serialVersionUID = 515677911107036873L;
  private static final int DEFAULT_CAPACITY = 10;
  private static final Object[] EMPTY_ELEMENTDATA = {};
  private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
  transient Object[] elementData;
  private int size;
  private int modCount;

  public Stack() {
    this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
  }

  public Stack(int initialCapacity) {
    if (initialCapacity > 0) {
      this.elementData = new Object[initialCapacity];
    } else if (initialCapacity == 0) {
      this.elementData = EMPTY_ELEMENTDATA;
    } else {
      throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
    }
  }

  public Stack(Collection<? extends E> c) {
    elementData = c.toArray();
    if ((size = elementData.length) != 0) {
      // c.toArray might (incorrectly) not return Object[] (see 6260652)
      if (elementData.getClass() != Object[].class)
        elementData = Arrays.copyOf(elementData, size, Object[].class);
    } else {
      // replace with empty array.
      this.elementData = EMPTY_ELEMENTDATA;
    }
  }

  public void trimToSize() {
    modCount++;
    if (size < elementData.length) {
      elementData = (size == 0)
          ? EMPTY_ELEMENTDATA
              : Arrays.copyOf(elementData, size);
    }
  }

  public void ensureCapacity(int minCapacity) {
    int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
        // any size if not default element table
        ? 0
            // larger than default for default empty table. It's already
            // supposed to be at default size.
            : DEFAULT_CAPACITY;

    if (minCapacity > minExpand) {
      ensureExplicitCapacity(minCapacity);
    }
  }

  private void ensureCapacityInternal(int minCapacity) {
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
      minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
    }

    ensureExplicitCapacity(minCapacity);
  }

  private void ensureExplicitCapacity(int minCapacity) {
    modCount++;

    // overflow-conscious code
    if (minCapacity - elementData.length > 0)
      grow(minCapacity);
  }

  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

  private void grow(int minCapacity) {
    // overflow-conscious code
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0)
      newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
      newCapacity = hugeCapacity(minCapacity);
    // minCapacity is usually close to size, so this is a win:
    elementData = Arrays.copyOf(elementData, newCapacity);
  }

  private static int hugeCapacity(int minCapacity) {
    if (minCapacity < 0) // overflow
      throw new OutOfMemoryError();
    return (minCapacity > MAX_ARRAY_SIZE) ?
        Integer.MAX_VALUE :
          MAX_ARRAY_SIZE;
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public int indexOf(Object o) {
    if (o == null) {
      for (int i = 0; i < size; i++)
        if (elementData[i]==null)
          return i;
    } else {
      for (int i = 0; i < size; i++)
        if (o.equals(elementData[i]))
          return i;
    }
    return -1;
  }

  public int lastIndexOf(Object o) {
    if (o == null) {
      for (int i = size-1; i >= 0; i--)
        if (elementData[i]==null)
          return i;
    } else {
      for (int i = size-1; i >= 0; i--)
        if (o.equals(elementData[i]))
          return i;
    }
    return -1;
  }

  public Object clone() {
    try {
      Stack<?> v = (Stack<?>) super.clone();
      v.elementData = Arrays.copyOf(elementData, size);
      v.modCount = 0;
      return v;
    } catch (CloneNotSupportedException e) {
      // this shouldn't happen, since we are Cloneable
      throw new InternalError(e);
    }
  }

  public Object[] toArray() {
    return Arrays.copyOf(elementData, size);
  }

  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] a) {
    if (a.length < size)
      // Make a new array of a's runtime type, but my contents:
      return (T[]) Arrays.copyOf(elementData, size, a.getClass());
    System.arraycopy(elementData, 0, a, 0, size);
    if (a.length > size)
      a[size] = null;
    return a;
  }

  @SuppressWarnings("unchecked")
  E elementData(int index) {
    return (E) elementData[index];
  }

  public E get(int index) {
    rangeCheck(index);

    return elementData(index);
  }

  public E set(int index, E element) {
    rangeCheck(index);

    E oldValue = elementData(index);
    elementData[index] = element;
    return oldValue;
  }

  public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    elementData[size++] = e;
    return true;
  }

  public boolean push(E e) {
    return add(e);
  }

  public void add(int index, E element) {
    rangeCheckForAdd(index);

    ensureCapacityInternal(size + 1);  // Increments modCount!!
    System.arraycopy(elementData, index, elementData, index + 1,
        size - index);
    elementData[index] = element;
    size++;
  }

  public E remove(int index) {
    rangeCheck(index);

    modCount++;
    E oldValue = elementData(index);

    int numMoved = size - index - 1;
    if (numMoved > 0)
      System.arraycopy(elementData, index+1, elementData, index,
          numMoved);
    elementData[--size] = null; // clear to let GC do its work

    return oldValue;
  }

  public E pop() {
    return remove(size - 1);
  }

  public boolean remove(Object o) {
    if (o == null) {
      for (int index = 0; index < size; index++)
        if (elementData[index] == null) {
          fastRemove(index);
          return true;
        }
    } else {
      for (int index = 0; index < size; index++)
        if (o.equals(elementData[index])) {
          fastRemove(index);
          return true;
        }
    }
    return false;
  }

  private void fastRemove(int index) {
    modCount++;
    int numMoved = size - index - 1;
    if (numMoved > 0)
      System.arraycopy(elementData, index+1, elementData, index,
          numMoved);
    elementData[--size] = null; // clear to let GC do its work
  }

  public void clear() {
    modCount++;
    for (int i = 0; i < size; i++)
      elementData[i] = null;

    size = 0;
  }

  public boolean addAll(Collection<? extends E> c) {
    Object[] a = c.toArray();
    int numNew = a.length;
    ensureCapacityInternal(size + numNew);  // Increments modCount
    System.arraycopy(a, 0, elementData, size, numNew);
    size += numNew;
    return numNew != 0;
  }

  public boolean addAll(int index, Collection<? extends E> c) {
    rangeCheckForAdd(index);

    Object[] a = c.toArray();
    int numNew = a.length;
    ensureCapacityInternal(size + numNew);  // Increments modCount

    int numMoved = size - index;
    if (numMoved > 0)
      System.arraycopy(elementData, index, elementData, index + numNew,
          numMoved);

    System.arraycopy(a, 0, elementData, index, numNew);
    size += numNew;
    return numNew != 0;
  }

  public void removeRange(int fromIndex, int toIndex) {
    modCount++;
    int numMoved = size - toIndex;
    System.arraycopy(elementData, toIndex, elementData, fromIndex,
        numMoved);

    // clear to let GC do its work
    int newSize = size - (toIndex-fromIndex);
    for (int i = newSize; i < size; i++) {
      elementData[i] = null;
    }
    size = newSize;
  }

  private void rangeCheck(int index) {
    if (index >= size)
      throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
  }

  private void rangeCheckForAdd(int index) {
    if (index > size || index < 0)
      throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
  }

  private String outOfBoundsMsg(int index) {
    return "Index: "+index+", Size: "+size;
  }

  public boolean removeAll(Collection<?> c) {
    Objects.requireNonNull(c);
    return batchRemove(c, false);
  }

  public boolean retainAll(Collection<?> c) {
    Objects.requireNonNull(c);
    return batchRemove(c, true);
  }

  private boolean batchRemove(Collection<?> c, boolean complement) {
    final Object[] elementData = this.elementData;
    int r = 0, w = 0;
    boolean modified = false;
    try {
      for (; r < size; r++)
        if (c.contains(elementData[r]) == complement)
          elementData[w++] = elementData[r];
    } finally {
      // Preserve behavioral compatibility with AbstractCollection,
      // even if c.contains() throws.
      if (r != size) {
        System.arraycopy(elementData, r,
            elementData, w,
            size - r);
        w += size - r;
      }
      if (w != size) {
        // clear to let GC do its work
        for (int i = w; i < size; i++)
          elementData[i] = null;
        modCount += size - w;
        size = w;
        modified = true;
      }
    }
    return modified;
  }

  private void writeObject(java.io.ObjectOutputStream s)
      throws java.io.IOException{
    // Write out element count, and any hidden stuff
    int expectedModCount = modCount;
    s.defaultWriteObject();

    // Write out size as capacity for behavioural compatibility with clone()
    s.writeInt(size);

    // Write out all elements in the proper order.
    for (int i=0; i<size; i++) {
      s.writeObject(elementData[i]);
    }

    if (modCount != expectedModCount) {
      throw new ConcurrentModificationException();
    }
  }

  private void readObject(java.io.ObjectInputStream s)
      throws java.io.IOException, ClassNotFoundException {
    elementData = EMPTY_ELEMENTDATA;

    // Read in size, and any hidden stuff
    s.defaultReadObject();

    // Read in capacity
    s.readInt(); // ignored

    if (size > 0) {
      // be like clone(), allocate array based upon size not capacity
      ensureCapacityInternal(size);

      Object[] a = elementData;
      // Read in all elements in the proper order.
      for (int i=0; i<size; i++) {
        a[i] = s.readObject();
      }
    }
  }

  public ListIterator<E> listIterator(int index) {
    if (index < 0 || index > size)
      throw new IndexOutOfBoundsException("Index: "+index);
    return new ListItr(index);
  }

  public ListIterator<E> listIterator() {
    return new ListItr(0);
  }

  public Iterator<E> iterator() {
    return new Itr();
  }

  private class Itr implements Iterator<E> {
    int cursor;       // index of next element to return
    int lastRet = -1; // index of last element returned; -1 if no such
    int expectedModCount = modCount;

    public boolean hasNext() {
      return cursor != size;
    }

    @SuppressWarnings("unchecked")
    public E next() {
      checkForComodification();
      int i = cursor;
      if (i >= size)
        throw new NoSuchElementException();
      Object[] elementData = Stack.this.elementData;
      if (i >= elementData.length)
        throw new ConcurrentModificationException();
      cursor = i + 1;
      return (E) elementData[lastRet = i];
    }

    public void remove() {
      if (lastRet < 0)
        throw new IllegalStateException();
      checkForComodification();

      try {
        Stack.this.remove(lastRet);
        cursor = lastRet;
        lastRet = -1;
        expectedModCount = modCount;
      } catch (IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void forEachRemaining(Consumer<? super E> consumer) {
      Objects.requireNonNull(consumer);
      final int size = Stack.this.size;
      int i = cursor;
      if (i >= size) {
        return;
      }
      final Object[] elementData = Stack.this.elementData;
      if (i >= elementData.length) {
        throw new ConcurrentModificationException();
      }
      while (i != size && modCount == expectedModCount) {
        consumer.accept((E) elementData[i++]);
      }
      // update once at end of iteration to reduce heap write traffic
      cursor = i;
      lastRet = i - 1;
      checkForComodification();
    }

    final void checkForComodification() {
      if (modCount != expectedModCount)
        throw new ConcurrentModificationException();
    }
  }

  private class ListItr extends Itr implements ListIterator<E> {
    ListItr(int index) {
      super();
      cursor = index;
    }

    public boolean hasPrevious() {
      return cursor != 0;
    }

    public int nextIndex() {
      return cursor;
    }

    public int previousIndex() {
      return cursor - 1;
    }

    @SuppressWarnings("unchecked")
    public E previous() {
      checkForComodification();
      int i = cursor - 1;
      if (i < 0)
        throw new NoSuchElementException();
      Object[] elementData = Stack.this.elementData;
      if (i >= elementData.length)
        throw new ConcurrentModificationException();
      cursor = i;
      return (E) elementData[lastRet = i];
    }

    public void set(E e) {
      if (lastRet < 0)
        throw new IllegalStateException();
      checkForComodification();

      try {
        Stack.this.set(lastRet, e);
      } catch (IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }
    }

    public void add(E e) {
      checkForComodification();

      try {
        int i = cursor;
        Stack.this.add(i, e);
        cursor = i + 1;
        lastRet = -1;
        expectedModCount = modCount;
      } catch (IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }
    }
  }

  public List<E> subList(int fromIndex, int toIndex) {
    subListRangeCheck(fromIndex, toIndex, size);
    return new SubList(this, 0, fromIndex, toIndex);
  }

  static void subListRangeCheck(int fromIndex, int toIndex, int size) {
    if (fromIndex < 0)
      throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
    if (toIndex > size)
      throw new IndexOutOfBoundsException("toIndex = " + toIndex);
    if (fromIndex > toIndex)
      throw new IllegalArgumentException("fromIndex(" + fromIndex +
          ") > toIndex(" + toIndex + ")");
  }

  private class SubList extends AbstractList<E> implements RandomAccess {
    private final AbstractList<E> parent;
    private final int parentOffset;
    private final int offset;
    int size;

    SubList(AbstractList<E> parent,
        int offset, int fromIndex, int toIndex) {
      this.parent = parent;
      this.parentOffset = fromIndex;
      this.offset = offset + fromIndex;
      this.size = toIndex - fromIndex;
      this.modCount = Stack.this.modCount;
    }

    public E set(int index, E e) {
      rangeCheck(index);
      checkForComodification();
      E oldValue = Stack.this.elementData(offset + index);
      Stack.this.elementData[offset + index] = e;
      return oldValue;
    }

    public E get(int index) {
      rangeCheck(index);
      checkForComodification();
      return Stack.this.elementData(offset + index);
    }

    public int size() {
      checkForComodification();
      return this.size;
    }

    public void add(int index, E e) {
      rangeCheckForAdd(index);
      checkForComodification();
      parent.add(parentOffset + index, e);
      this.modCount = parent.modCount;
      this.size++;
    }

    public E remove(int index) {
      rangeCheck(index);
      checkForComodification();
      E result = parent.remove(parentOffset + index);
      this.modCount = parent.modCount;
      this.size--;
      return result;
    }

    public void removeRange(int fromIndex, int toIndex) {
      checkForComodification();
      parent.removeRange(parentOffset + fromIndex,
          parentOffset + toIndex);
      this.modCount = parent.modCount;
      this.size -= toIndex - fromIndex;
    }

    public boolean addAll(Collection<? extends E> c) {
      return addAll(this.size, c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
      rangeCheckForAdd(index);
      int cSize = c.size();
      if (cSize==0)
        return false;

      checkForComodification();
      parent.addAll(parentOffset + index, c);
      this.modCount = parent.modCount;
      this.size += cSize;
      return true;
    }

    public Iterator<E> iterator() {
      return listIterator();
    }

    public ListIterator<E> listIterator(final int index) {
      checkForComodification();
      rangeCheckForAdd(index);
      final int offset = this.offset;

      return new ListIterator<E>() {
        int cursor = index;
        int lastRet = -1;
        int expectedModCount = Stack.this.modCount;

        public boolean hasNext() {
          return cursor != SubList.this.size;
        }

        @SuppressWarnings("unchecked")
        public E next() {
          checkForComodification();
          int i = cursor;
          if (i >= SubList.this.size)
            throw new NoSuchElementException();
          Object[] elementData = Stack.this.elementData;
          if (offset + i >= elementData.length)
            throw new ConcurrentModificationException();
          cursor = i + 1;
          return (E) elementData[offset + (lastRet = i)];
        }

        public boolean hasPrevious() {
          return cursor != 0;
        }

        @SuppressWarnings("unchecked")
        public E previous() {
          checkForComodification();
          int i = cursor - 1;
          if (i < 0)
            throw new NoSuchElementException();
          Object[] elementData = Stack.this.elementData;
          if (offset + i >= elementData.length)
            throw new ConcurrentModificationException();
          cursor = i;
          return (E) elementData[offset + (lastRet = i)];
        }

        @SuppressWarnings("unchecked")
        public void forEachRemaining(Consumer<? super E> consumer) {
          Objects.requireNonNull(consumer);
          final int size = SubList.this.size;
          int i = cursor;
          if (i >= size) {
            return;
          }
          final Object[] elementData = Stack.this.elementData;
          if (offset + i >= elementData.length) {
            throw new ConcurrentModificationException();
          }
          while (i != size && modCount == expectedModCount) {
            consumer.accept((E) elementData[offset + (i++)]);
          }
          // update once at end of iteration to reduce heap write traffic
          lastRet = cursor = i;
          checkForComodification();
        }

        public int nextIndex() {
          return cursor;
        }

        public int previousIndex() {
          return cursor - 1;
        }

        public void remove() {
          if (lastRet < 0)
            throw new IllegalStateException();
          checkForComodification();

          try {
            SubList.this.remove(lastRet);
            cursor = lastRet;
            lastRet = -1;
            expectedModCount = Stack.this.modCount;
          } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
          }
        }

        public void set(E e) {
          if (lastRet < 0)
            throw new IllegalStateException();
          checkForComodification();

          try {
            Stack.this.set(offset + lastRet, e);
          } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
          }
        }

        public void add(E e) {
          checkForComodification();

          try {
            int i = cursor;
            SubList.this.add(i, e);
            cursor = i + 1;
            lastRet = -1;
            expectedModCount = Stack.this.modCount;
          } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
          }
        }

        final void checkForComodification() {
          if (expectedModCount != Stack.this.modCount)
            throw new ConcurrentModificationException();
        }
      };
    }

    public List<E> subList(int fromIndex, int toIndex) {
      subListRangeCheck(fromIndex, toIndex, size);
      return new SubList(this, offset, fromIndex, toIndex);
    }

    private void rangeCheck(int index) {
      if (index < 0 || index >= this.size)
        throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private void rangeCheckForAdd(int index) {
      if (index < 0 || index > this.size)
        throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private String outOfBoundsMsg(int index) {
      return "Index: "+index+", Size: "+this.size;
    }

    private void checkForComodification() {
      if (Stack.this.modCount != this.modCount)
        throw new ConcurrentModificationException();
    }

    public Spliterator<E> spliterator() {
      checkForComodification();
      return new StackSpliterator<E>(Stack.this, offset,
          offset + this.size, this.modCount);
    }
  }

  @Override
  public void forEach(Consumer<? super E> action) {
    Objects.requireNonNull(action);
    final int expectedModCount = modCount;
    @SuppressWarnings("unchecked")
    final E[] elementData = (E[]) this.elementData;
    final int size = this.size;
    for (int i=0; modCount == expectedModCount && i < size; i++) {
      action.accept(elementData[i]);
    }
    if (modCount != expectedModCount) {
      throw new ConcurrentModificationException();
    }
  }

  /**
   * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
   * and <em>fail-fast</em> {@link Spliterator} over the elements in this
   * list.
   *
   * <p>The {@code Spliterator} reports {@link Spliterator#SIZED},
   * {@link Spliterator#SUBSIZED}, and {@link Spliterator#ORDERED}.
   * Overriding implementations should document the reporting of additional
   * characteristic values.
   *
   * @return a {@code Spliterator} over the elements in this list
   * @since 1.8
   */
  @Override
  public Spliterator<E> spliterator() {
    return new StackSpliterator<>(this, 0, -1, 0);
  }

  /** Index-based split-by-two, lazily initialized Spliterator */
  static final class StackSpliterator<E> implements Spliterator<E> {

    /*
     * If ArrayLists were immutable, or structurally immutable (no
     * adds, removes, etc), we could implement their spliterators
     * with Arrays.spliterator. Instead we detect as much
     * interference during traversal as practical without
     * sacrificing much performance. We rely primarily on
     * modCounts. These are not guaranteed to detect concurrency
     * violations, and are sometimes overly conservative about
     * within-thread interference, but detect enough problems to
     * be worthwhile in practice. To carry this out, we (1) lazily
     * initialize fence and expectedModCount until the latest
     * point that we need to commit to the state we are checking
     * against; thus improving precision.  (This doesn't apply to
     * SubLists, that create spliterators with current non-lazy
     * values).  (2) We perform only a single
     * ConcurrentModificationException check at the end of forEach
     * (the most performance-sensitive method). When using forEach
     * (as opposed to iterators), we can normally only detect
     * interference after actions, not before. Further
     * CME-triggering checks apply to all other possible
     * violations of assumptions for example null or too-small
     * elementData array given its size(), that could only have
     * occurred due to interference.  This allows the inner loop
     * of forEach to run without any further checks, and
     * simplifies lambda-resolution. While this does entail a
     * number of checks, note that in the common case of
     * list.stream().forEach(a), no checks or other computation
     * occur anywhere other than inside forEach itself.  The other
     * less-often-used methods cannot take advantage of most of
     * these streamlinings.
     */

    private final Stack<E> list;
    private int index; // current index, modified on advance/split
    private int fence; // -1 until used; then one past last index
    private int expectedModCount; // initialized when fence set

    /** Create new spliterator covering the given  range */
    StackSpliterator(Stack<E> list, int origin, int fence,
        int expectedModCount) {
      this.list = list; // OK if null unless traversed
      this.index = origin;
      this.fence = fence;
      this.expectedModCount = expectedModCount;
    }

    private int getFence() { // initialize fence to size on first use
      int hi; // (a specialized variant appears in method forEach)
      Stack<E> lst;
      if ((hi = fence) < 0) {
        if ((lst = list) == null)
          hi = fence = 0;
        else {
          expectedModCount = lst.modCount;
          hi = fence = lst.size;
        }
      }
      return hi;
    }

    public StackSpliterator<E> trySplit() {
      int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
    return (lo >= mid) ? null : // divide range in half unless too small
      new StackSpliterator<E>(list, lo, index = mid,
      expectedModCount);
    }

    public boolean tryAdvance(Consumer<? super E> action) {
      if (action == null)
        throw new NullPointerException();
      int hi = getFence(), i = index;
      if (i < hi) {
        index = i + 1;
        @SuppressWarnings("unchecked") E e = (E)list.elementData[i];
        action.accept(e);
        if (list.modCount != expectedModCount)
          throw new ConcurrentModificationException();
        return true;
      }
      return false;
    }

    public void forEachRemaining(Consumer<? super E> action) {
      int i, hi, mc; // hoist accesses and checks from loop
      Stack<E> lst; Object[] a;
      if (action == null)
        throw new NullPointerException();
      if ((lst = list) != null && (a = lst.elementData) != null) {
        if ((hi = fence) < 0) {
          mc = lst.modCount;
          hi = lst.size;
        }
        else
          mc = expectedModCount;
        if ((i = index) >= 0 && (index = hi) <= a.length) {
          for (; i < hi; ++i) {
            @SuppressWarnings("unchecked") E e = (E) a[i];
            action.accept(e);
          }
          if (lst.modCount == mc)
            return;
        }
      }
      throw new ConcurrentModificationException();
    }

    public long estimateSize() {
      return (long) (getFence() - index);
    }

    public int characteristics() {
      return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
    }
  }

  @Override
  public boolean removeIf(Predicate<? super E> filter) {
    Objects.requireNonNull(filter);
    // figure out which elements are to be removed
    // any exception thrown from the filter predicate at this stage
    // will leave the collection unmodified
    int removeCount = 0;
    final BitSet removeSet = new BitSet(size);
    final int expectedModCount = modCount;
    final int size = this.size;
    for (int i=0; modCount == expectedModCount && i < size; i++) {
      @SuppressWarnings("unchecked")
      final E element = (E) elementData[i];
      if (filter.test(element)) {
        removeSet.set(i);
        removeCount++;
      }
    }
    if (modCount != expectedModCount) {
      throw new ConcurrentModificationException();
    }

    // shift surviving elements left over the spaces left by removed elements
    final boolean anyToRemove = removeCount > 0;
    if (anyToRemove) {
      final int newSize = size - removeCount;
      for (int i=0, j=0; (i < size) && (j < newSize); i++, j++) {
        i = removeSet.nextClearBit(i);
        elementData[j] = elementData[i];
      }
      for (int k=newSize; k < size; k++) {
        elementData[k] = null;  // Let gc do its work
      }
      this.size = newSize;
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      modCount++;
    }

    return anyToRemove;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void replaceAll(UnaryOperator<E> operator) {
    Objects.requireNonNull(operator);
    final int expectedModCount = modCount;
    final int size = this.size;
    for (int i=0; modCount == expectedModCount && i < size; i++) {
      elementData[i] = operator.apply((E) elementData[i]);
    }
    if (modCount != expectedModCount) {
      throw new ConcurrentModificationException();
    }
    modCount++;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void sort(Comparator<? super E> c) {
    final int expectedModCount = modCount;
    Arrays.sort((E[]) elementData, 0, size, c);
    if (modCount != expectedModCount) {
      throw new ConcurrentModificationException();
    }
    modCount++;
  }
}



