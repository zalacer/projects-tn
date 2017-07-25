package ex41;

/* p561
  4.1.28  Two graphs are isomorphic if there is a way to rename the vertices 
  of one to make it identical to the other. Draw all the nonisomorphic graphs 
  with two, three, four, and five vertices.
 
    2 vertices: * *, *-*
    
    3 vertices: * * *, *-* *, *-*-*, *-*
                                     |/
                                     *
    4 vertices: * * * *, *-* * *, *-*-* *, *-*-*-*, *-*-*, *-* *, *-* *, *-*-*, *-*, *-*, *-*, *-*,
                                                      |    |/       |/   |/     | |  |/   |/|  |X| 
                                                      *    *        *    *      *-*  *-*  *-*  *-*
 
    5 vertices * * * * *, *-* * * *, *-*-* * *, *-*-*-* *, *-*-*-*-*, *-*-* *-*, *-*-* * , *-*-*-*,
                                                                                   |         |
                                                                                   *         *
               *-* * *, *-*-* *, *-*-*-*, *-*-*-*, *-* *-*, *-*-* *, *-*-*-*, *-* *, *-*-*, *-*-*,
               |/       |/       |/         |/     |/        \ /      \ /     | |    | |    |   |
               *        *        *          *      *          *        *      *-*    *-*    *---* 
                                                                           
              *-* *, *-*-*, *-* *, *-* *, *-*-*, *-* *, *-* *, *-*-*, *-*-*, *-*-*, *-*-*, *-*-*,
              |/     |/       |/   |/|    |/|      |/|  |X|    |X|    |/  |  |  /|  |\ /|  |\ /|
              *-*    *-*      *-*  *-*    *-*      *-*  *-*    *-*    *---*  | / |  | X |  | X |
              *                                                              |/  |  |/ \|  |/ \|
                                                                             *---*  *   *  *---*
            
              *-*-*,  the rest are too tedious to draw with characters, see G21-G23 at  
                |     http://wiki.smp.uq.edu.au/G-designs/index.php/Graphs_with_five_vertices                                                        
               *-*    or GraphsWith2-6Vertices.pdf in this project                              
 */                                                  

public class Ex4128NonIsomorphicGraphs {

  public static void main(String[] args) {

  }

}



