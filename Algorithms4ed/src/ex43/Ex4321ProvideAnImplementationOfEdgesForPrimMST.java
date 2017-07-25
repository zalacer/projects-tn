package ex43;

/* p632
  4.3.21  Provide an implementation of  edges() for  PrimMST (page 622).
  
  Solution :
    public Iterable<Edge> edges() {
      Bag<Edge> mst = new Bag<Edge>();
      for (int v = 1; v < edgeTo.length; v++)
        mst.add(edgeTo[v]);
      return mst;
    }
 
 */  

public class Ex4321ProvideAnImplementationOfEdgesForPrimMST {
 
  public static void main(String[] args) {
     
  }

}


