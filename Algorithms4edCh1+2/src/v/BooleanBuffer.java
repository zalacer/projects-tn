package v;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;

// adapted from:
// http://stackoverflow.com/questions/18825324/why-bytebuffer-doesnt-provide-method-to-read-write-boolean-data-type
public class BooleanBuffer implements Iterator<Boolean>{

  private ByteBuffer buffer;
  private int pos;
  private static int realArrayLength=0;

  public BooleanBuffer(ByteBuffer buffer){
    this.buffer = buffer;
  }

  public BooleanBuffer(int capacity){
    if (capacity < 0) throw new IllegalArgumentException("BooleanBuffer constructor: "
        + "capacity can't be < 0");
    this.buffer =  ByteBuffer.allocate(capacity%8 == 0 ? capacity/8 : 1+capacity/8);
  }
  
  public static BooleanBuffer allocate(int length) {
    if (length < 0) throw new IllegalArgumentException("BooleanBuffer.allocate: "
        + "capacity can't be < 0");
    // assuming capacity  is length of an array
    realArrayLength = length;
    return new BooleanBuffer(length);
  }

  public void put(boolean b){
    set(pos++,b);
  }
  
  public BooleanBuffer put(boolean[] a, int offset, int length) {
    if (a == null) throw new IllegalArgumentException("BooleanBuffer.put: "
        + "the array can't be null");
    if (a.length == 0) return this;
    if (offset > a.length-1) throw new IllegalArgumentException("BooleanBuffer.put: "
        + "offset not in bounds for array");  
    int len = offset+length <= a.length ? offset+length : a.length;
    for (int i = offset; i < len; i++) this.put(a[i]);
    return this;
  }

  public Boolean next(){
    return get(pos);
  }

  @Override
  public boolean hasNext() {
    return pos++ < size() -1;
  }

  public void rewind(){
    pos=0;
  }

  public int size(){
    return buffer.capacity() * 8;
  }

  public boolean get(int pos){
    buffer.position(pos / 8);
    byte byt = buffer.get();
    int bitPos = pos % 8;
    return isSet(byt, bitPos);
  }

  public void set(int pos, boolean b){
    int bytePos = pos / 8;
    buffer.position(bytePos);
    byte byt = buffer.get();
    int bitPos = pos % 8;
    if(isSet(byt, bitPos) != b){
      buffer.position(bytePos);
      buffer.put(flipBit(byt, bitPos));
    }
  }

  static byte flipBit(byte b, int pos){ 
    byte mask = (byte) (1 << pos);
    return (byte)(mask ^ b); 
  } 

  static boolean isSet(byte b, int pos){
    return ((b >> pos) & 1) == 1;
  }

  @Override
    public void remove() {
  }
  
  public boolean[] toArray() {
    boolean[] a = new boolean[realArrayLength];
    for (int i = 0; i < realArrayLength; i++) a[i] = get(i);
    return a;
  }
  
  @Override
  public String toString() {
    System.out.println(size());
    return Arrays.toString(toArray());
  }


}
