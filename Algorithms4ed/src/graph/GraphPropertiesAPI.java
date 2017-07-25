package graph;

public interface GraphPropertiesAPI {
  // GraphProperties(Graph G){} // constructor (exception if  G not connected)
  public int eccentricity(int v); // eccentricity of  v
  public int diameter(); // diameter of  G
  public int radius(); // radius of  G
  public int center(); // a center of  G

}
