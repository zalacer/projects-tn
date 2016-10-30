package ds;

public class Node<T>{
  T t;
  Node<T> next;
  
  public Node(){};
  
  public Node(T t) {
    this.t = t; this.next = null;
  }
  
  public Node(Node<T> next) {
    this.t = null; this.next = next;
  }
  
  public Node(T t, Node<T> next) {
    this.t = t; this.next = next;
  }

  public T t() {
    return t;
  }
  
  public T getT() {
    return t;
  }
  
  public void setT(T t) {
    this.t = t;
  }
  
  public Node<T> next() {
    return next;
  }
  
  public Node<T> getNext() {
    return next;
  }
  
  public void setNext(Node<T> next) {
    this.next = next;
  }

  @Override
  public String toString() {
    return "Node("+t+")";
  }
  

  public static void main(String[] args) {
   
  }
}