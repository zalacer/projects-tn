package ch07.collections;

import static utils.StringUtils.repeat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 10. Implement Dijkstra’s algorithm to find the shortest paths in a network of cities,
// some of which are connected by roads. (For a description, check out your favorite
// book on algorithms or the Wikipedia article.) Use a helper class Neighbor that
// stores the name of a neighboring city and the distance. Represent the graph as a map
// from cities to sets of neighbors. Use a PriorityQueue<Neighbor> in the
// algorithm.

public class Ch0710DijkstrasAlgorithm {

  // required in Graph constructor and main()
  final static String lineSep = System.getProperty("line.separator");

  public static class Graph {
    private final Map<String, City> cities;
    private final Map<String, HashSet<Neighbor>> graph;
    private City source;

    Graph(String graphConfig) {
      cities = new HashMap<>();
      graph = new HashMap<>();

      final String regex = "(^\\s*\\p{Alpha}[\\p{Alnum}_]*)\\s*,"
          + "\\s*(\\p{Alpha}[\\p{Alnum}_]*)\\s*,"
          + "\\s*+(\\d+|\\d+\\.\\d*|\\d*\\.\\d+)\\s*\\z"; 

      Matcher matcher = null;
      String city1Name = null;
      String city2Name = null;
      Double d = null;

      for (String s : Arrays.asList(graphConfig.split(lineSep))) {
        matcher = Pattern.compile(regex).matcher(s);
        if (matcher.matches()) {
          city1Name = matcher.group(1);
          city2Name = matcher.group(2);
          d = new Double(matcher.group(3));
          cities.putIfAbsent(city1Name, new City(city1Name));
          cities.putIfAbsent(city2Name, new City(city2Name));
          if (null != graph.putIfAbsent(city1Name, 
              new HashSet<Neighbor>(Arrays.asList(new Neighbor(city2Name, d))))) {
            graph.get(city1Name).add(new Neighbor(city2Name, d));
          }            
        }
      }
    }

    class City implements Comparable<City> {
      public final String name;
      public Double distanceFromSource = Double.POSITIVE_INFINITY;
      public City previousCity = null;

      public City (String name) {
        this.name = name;
      }

      @Override
      public int compareTo(City o) {
        return Double.compare(distanceFromSource, o.distanceFromSource);
      }

      public Double getDistanceFromSource() {
        return distanceFromSource;
      }

      public void setDistanceFromSource(Double distanceFromSource) {
        this.distanceFromSource = distanceFromSource;
      }

      public City getpreviousCity() {
        return previousCity;
      }

      public void setpreviousCity(City previousCity) {
        this.previousCity = previousCity;
      }

      @Override
      public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getOuterType().hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        City other = (City) obj;
        if (!getOuterType().equals(other.getOuterType()))
          return false;
        if (name == null) {
          if (other.name != null)
            return false;
        } else if (!name.equals(other.name))
          return false;
        return true;
      }

      private Graph getOuterType() {
        return Graph.this;
      }

      @Override
      public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("City[name=");
        builder.append(name);
        builder.append(", distanceFromSource=");
        builder.append(distanceFromSource);
        builder.append(", previousCity=");
        if (previousCity == null) {
          builder.append("null"); 
        } else {
          builder.append(previousCity.name);
        }
        builder.append("]");
        return builder.toString();
      }

    }

    class Neighbor {
      public String name;
      public Double distancefromKey; 
      // distancefromKey means distance from the city
      // of which it's a neighbor in the graph map
      // and whose name is a key in that map.
      // see string representation of graph below
      // starting at line 442.

      Neighbor(String name, Double distancefromKey) {
        this.name = name;
        this.distancefromKey = distancefromKey;
      }

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public Double getDistancefromKey() {
        return distancefromKey;
      }

      public void setDistancefromKey(Double distancefromKey) {
        this.distancefromKey = distancefromKey;
      }

      @Override
      public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getOuterType().hashCode();
        result = prime * result + ((distancefromKey == null) ? 0 : distancefromKey.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Neighbor other = (Neighbor) obj;
        if (!getOuterType().equals(other.getOuterType()))
          return false;
        if (distancefromKey == null) {
          if (other.distancefromKey != null)
            return false;
        } else if (!distancefromKey.equals(other.distancefromKey))
          return false;
        if (name == null) {
          if (other.name != null)
            return false;
        } else if (!name.equals(other.name))
          return false;
        return true;
      }

      private Graph getOuterType() {
        return Graph.this;
      }

      @Override
      public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Neighbor[name=");
        builder.append(name);
        builder.append(", distancefromKey=");
        builder.append(distancefromKey);
        builder.append("]");
        return builder.toString();
      }
    }

    public void findMinPaths(String start) {
      if (!graph.containsKey(start)) {
        System.err.printf("Graph doesn't contain start city \"%s\"\n", start);
        return;
      }

      source = cities.get(start);

      PriorityQueue<City> q = new PriorityQueue<>();

      for (City v : cities.values()) {
        v.previousCity = v.equals(source) ? source : null;
        v.distanceFromSource = v.equals(source) ? 0 : Double.POSITIVE_INFINITY;
        q.add(v);
      }

      while (!q.isEmpty()) {
        City u = q.poll(); // city with shortest dist
        // first iteration returns source
        if (u.distanceFromSource == Double.POSITIVE_INFINITY)
          break; // infinity  => unreachable

        //for each neighbor of u examine its distanceFromSource and possibly adjust it
        if (graph.containsKey(u.name)) {
          for(Neighbor n : graph.get(u.name)) {
            City v = cities.get(n.name);
            final double adjustedDist = u.distanceFromSource + n.distancefromKey;
            if (adjustedDist < v.distanceFromSource) {
              q.remove(v);
              v.distanceFromSource = adjustedDist;
              v.previousCity = u;
              q.add(v);                   
            }
          }
        }
      }
    }

    public void printCities() {
      for (String s: cities.keySet()) {
        System.out.println(cities.get(s));
      }
    }

    public void printGraph() {
      for (String s : graph.keySet()) {
        System.out.print(s + " -> ");
        Object o = graph.get(s).toArray();
        int length = ((Object[]) o).length;
        Neighbor[] nn = new Neighbor[length];
        int count = 0;
        for (Neighbor n : graph.get(s).toArray(nn)) {
          if (length == 1) {
            System.out.println("("+n+")");
          } else if (count == 0) {
            System.out.println("("+n+",");
          } else if (count == length - 1) {
            System.out.println("      " + n+")");
          } else {
            System.out.println("      " + n+",");
          }
          count++;
        }
      }
    }

    // this can be implemented as a method in City perhaps more simply
    public void printPath(String endName) {
      if (!cities.containsKey(endName)) {
        System.err.printf("Graph doesn't contain end city \"%s\"\n", endName);
        return;
      }

      Stack<String> stack = new Stack<>();
      City x = cities.get(endName);

      // if only one city in path to itself print it with zero distance and return
      if (x == x.previousCity) {
        System.out.println(x.name+" -> "+x.name+"(0.00)");
        return;
      }

      while (x != x.previousCity) {
        if (x.previousCity == null) {
          stack.push(String.format("%s(unreached)", x.name));
          break;
        } else {
          stack.push(String.format(" -> %s(%.2f)", x.name, x.distanceFromSource));
          x = x.previousCity;
        }
      }

      stack.push(String.format("%s", x.previousCity.name));

      StringBuilder sb = new StringBuilder();

      while (! stack.empty())
        sb.append(stack.pop());

      System.out.println(sb.toString());    
    }

    public void printAllPaths() {
      System.out.println("\nall min paths from "+source.name+":");
      for (City c : cities.values()) {
        printPath(c.name);
      }
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();

      // cities
      builder.append("Graph [cities={");
      Object[] ac = cities.keySet().toArray();
      builder.append(ac[0]+"=");
      builder.append(cities.get(ac[0])+",");
      for (int i = 1; i < cities.size() - 1; i++) {                                
        builder.append("\n"+repeat(' ', 15)+ac[i]+"=");
        builder.append(cities.get(ac[i])+",");
      }                 
      builder.append("\n"+repeat(' ', 15)+ac[cities.size()-1]+"=");
      builder.append(cities.get(ac[cities.size()-1])+"}");

      // graph
      builder.append(",\n"+repeat(' ', 8)+"graph={");
      Object[] ag = graph.keySet().toArray();
      String str0 = (ag[0]+"=[").toString();
      builder.append(str0);
      Object[] ag0 = graph.get(ag[0]).toArray();
      builder.append(ag0[0]+",");
      for (int j = 1; j < ag0.length - 1; j++) {                           
        builder.append("\n"+repeat(' ', str0.length() + 15)+ag0[j]+",");
      }
      builder.append("\n"+repeat(' ', str0.length() + 15)+ag0[ag0.length - 1]+"],");   
      for (int i = 1; i < graph.size() - 1; i++) {                              
        String str1 = repeat(' ', 15)+ag[i]+"=[";
        builder.append("\n"+str1);                
        Object[] agn = graph.get(ag[i]).toArray();
        builder.append(agn[0]+",");
        for (int j = 1; j < agn.length - 1; j++) {
          builder.append("\n"+repeat(' ', str1.length())+agn[j]+",");
        }
        builder.append("\n"+repeat(' ', str1.length())+agn[agn.length - 1]+"],");
      }                 
      builder.append("\n"+repeat(' ', 15)+ag[graph.size()-1]+"=");
      builder.append(graph.get(ac[graph.size()-1])+"}");

      // coda
      builder.append("]");
      return builder.toString();
    }       
  }

  public static void main(String[] args) {

    // The graph constructor takes a string in this format and builds cities and neighbors.
    // For example: "a, b, 7" means for cities named "a" and "b" the distance from the former
    // to the latter is 7 along some road. This causes construction of City("a") and City("b)
    // if not already done, and insertion of a new Neighbor("b", 7) in the set which is the 
    // value of the key "a" in Map<String, HashSet<Neighbor>> graph.
    String graphConfiguration = String.join(lineSep,
        "a, b, 7",
        "a, c, 9",
        "a, f, 14",
        "b, c, 10",
        "b, d, 15",
        "c, d, 11",
        "c, f, 2",
        "d, e, 6",
        "e, f, 9"
        );

    Graph g = new Graph(graphConfiguration);

    // print newly initialized cities
    System.out.println("newly initialized cities:");
    g.printCities();

    // print graph = map of city names to their neighbors including distance to each
    System.out.println("\ngraph:");
    g.printGraph();

    // calculate minimum paths from city named "a" to all other reachable cities
    g.findMinPaths("a");

    // print minimum path from a to e
    System.out.println("\nminimum path from a to e:");
    g.printPath("e");

    // print minimum path from a to f
    System.out.println("\nminimum path from a to f:");
    g.printPath("f");

    // print all minimum paths from a
    g.printAllPaths();

    // print updated cities
    System.out.println("\nupdated cities:");
    g.printCities();

    // print graph again - content unchanged
    System.out.println("\ngraph (content unchanged):");
    g.printGraph();

    // demo of g.toString()
    System.out.println("\ndemo of g.toString():");
    System.out.println(g.toString());

  }

}

// output:
//    newly initialized cities:
//    City[name=a, distanceFromSource=Infinity, previousCity=null]
//    City[name=b, distanceFromSource=Infinity, previousCity=null]
//    City[name=c, distanceFromSource=Infinity, previousCity=null]
//    City[name=d, distanceFromSource=Infinity, previousCity=null]
//    City[name=e, distanceFromSource=Infinity, previousCity=null]
//    City[name=f, distanceFromSource=Infinity, previousCity=null]
//    
//    graph:
//    a -> (Neighbor[name=f, distancefromKey=14.0],
//          Neighbor[name=c, distancefromKey=9.0],
//          Neighbor[name=b, distancefromKey=7.0])
//    b -> (Neighbor[name=d, distancefromKey=15.0],
//          Neighbor[name=c, distancefromKey=10.0])
//    c -> (Neighbor[name=d, distancefromKey=11.0],
//          Neighbor[name=f, distancefromKey=2.0])
//    d -> (Neighbor[name=e, distancefromKey=6.0])
//    e -> (Neighbor[name=f, distancefromKey=9.0])
//    
//    minimum path from a to e:
//    a -> c(9.00) -> d(20.00) -> e(26.00)
//    
//    minimum path from a to f:
//    a -> c(9.00) -> f(11.00)
//    
//    all min paths from a:
//    a -> a(0.00)
//    a -> b(7.00)
//    a -> c(9.00)
//    a -> c(9.00) -> d(20.00)
//    a -> c(9.00) -> d(20.00) -> e(26.00)
//    a -> c(9.00) -> f(11.00)
//    
//    updated cities:
//    City[name=a, distanceFromSource=0.0, previousCity=a]
//    City[name=b, distanceFromSource=7.0, previousCity=a]
//    City[name=c, distanceFromSource=9.0, previousCity=a]
//    City[name=d, distanceFromSource=20.0, previousCity=c]
//    City[name=e, distanceFromSource=26.0, previousCity=d]
//    City[name=f, distanceFromSource=11.0, previousCity=c]
//    
//    graph (content unchanged):
//    a -> (Neighbor[name=f, distancefromKey=14.0],
//          Neighbor[name=c, distancefromKey=9.0],
//          Neighbor[name=b, distancefromKey=7.0])
//    b -> (Neighbor[name=d, distancefromKey=15.0],
//          Neighbor[name=c, distancefromKey=10.0])
//    c -> (Neighbor[name=d, distancefromKey=11.0],
//          Neighbor[name=f, distancefromKey=2.0])
//    d -> (Neighbor[name=e, distancefromKey=6.0])
//    e -> (Neighbor[name=f, distancefromKey=9.0])
//
//    demo of g.toString():
//    Graph [cities={a=City[name=a, distanceFromSource=0.0, previousCity=a],
//                   b=City[name=b, distanceFromSource=7.0, previousCity=a],
//                   c=City[name=c, distanceFromSource=9.0, previousCity=a],
//                   d=City[name=d, distanceFromSource=20.0, previousCity=c],
//                   e=City[name=e, distanceFromSource=26.0, previousCity=d],
//                   f=City[name=f, distanceFromSource=11.0, previousCity=c]},
//            graph={a=[Neighbor[name=f, distancefromKey=14.0],
//                      Neighbor[name=c, distancefromKey=9.0],
//                      Neighbor[name=b, distancefromKey=7.0]],
//                   b=[Neighbor[name=d, distancefromKey=15.0],
//                      Neighbor[name=c, distancefromKey=10.0]],
//                   c=[Neighbor[name=d, distancefromKey=11.0],
//                      Neighbor[name=f, distancefromKey=2.0]],
//                   d=[Neighbor[name=e, distancefromKey=6.0],
//                      Neighbor[name=e, distancefromKey=6.0]],
//                   e=[Neighbor[name=f, distancefromKey=9.0]]}]
