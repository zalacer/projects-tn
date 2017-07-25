package u;

import java.nio.ByteBuffer;
import java.util.Iterator;

//http://stackoverflow.com/questions/18825324/why-bytebuffer-doesnt-provide-method-to-read-write-boolean-data-type 
public class BitBuffer implements Iterator<Boolean>{

  private ByteBuffer buffer;
  private int pos;

  public BitBuffer(ByteBuffer buffer){
    this.buffer = buffer;
  }

  public BitBuffer(int capacity){
    this.buffer =  ByteBuffer.allocate(capacity / 8);
  }

  public void put(boolean b){
    set(pos++,b);
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


}
