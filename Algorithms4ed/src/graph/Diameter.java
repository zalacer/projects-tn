//package graph;
//
///******************************************************************************
// *  http://introcs.cs.princeton.edu/java/45graph/Diameter.java
// *  http://introcs.cs.princeton.edu/java/45graph/Diameter.java.html
// *  Compilation:  javac Diameter.java
// *  Dependencies: PathFinder.java
// *  Execution:    java Diameter movies-top-grossing.txt
// *  
// *  Computes the diameter (longest distance between two vertices)
// *  of a connected graph by running breadth first search from
// *  each vertex.
// *
// ******************************************************************************/
//
//public class Diameter {
//
//    public static void main(String[] args) {
//
//        // read in data and initialize graph
//        String filename = args[0];
//        Graph G = new Graph(filename, "/");
//        StdOut.println("Done reading movies and building graph");
//
//        // run breadth first search from each vertex
//        int best = -1;
//        for (String s : G.vertices()) {
//            PathFinder finder = new PathFinder(G, s);
//            for (String v : G.vertices()) {
//                if (finder.hasPathTo(v) && finder.distanceTo(v) > best) {
//                    StdOut.println(finder.pathTo(v));
//                    StdOut.println();
//                    best = finder.distanceTo(v);
//                }
//            }
//        }
//    }
//}
//
