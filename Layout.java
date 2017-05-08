import java.io.FileNotFoundException;

/**
 * Graph Layout project that reads the graph presented by the input text file, This program Ranks
 * the nodes into respective ranks according to the longest path to the sMin nodes in the rank that
 * are ranked first the program also depends on the data type that orders the nodes in respective rank
 * so as to minimise the number of edge crossings.
 * @author   Bhekimpilo Ndhlela
 * @author   18998712
 * @since    Friday - 24 - March - 2017
 * @version  1.0
 */
public class Layout {
  /**
   * PROJECT CLIENT (GRAPH or NETWORK LAYERING CLIENT)
   * This is the client of the project it tases in the args and send it to the GraphInitializer
   * datatype so that the graph layout program can the initiated.
   * @param   args[0](inputFilePath)   this is the input file directory or path
   * @throws  FileNotFoundException    if the tixt file for the input graph is not found
   */
  public static void main(String []args) throws FileNotFoundException{
    //check if the program was provided with the input Graph File if not terminate.:
    if(args.length == 0){
      System.out.println("Provide the Application with the input text file that contains the Graph input");
      System.exit(0);
    }

    //initialise the graph layout Program.
    GraphInitializer graph = new GraphInitializer(args[0]);
    graph.readGraphInput();
    Digraph dGraph         = graph.getDigraph();
    //check if Graph is cyclic terminate if cyclic
    graph.isCyclic(dGraph);

    //initialise the graph ranking data type, this datatype ranks the vertices
    //if and only if there is at least one element in both the sMin and
    //sMax or it terminates the program by pirint the appropriate message
    GraphRanking gR = new GraphRanking(dGraph);
    int graphOrder  = graph.getGraphOrder();
    gR.rankVertices(graphOrder);
    int maxRankL    = gR.getCurrantMaxRankLength();

    //create the dummy nodes and dummy edges and then update the appropriate Maps
    GraphDummyNode dN = new GraphDummyNode(dGraph, gR.getGraphMap());
    dN.addDummyNodesToMap(graph.getEdgeCountMap());

    //order the nodes in individual ranks to minimise the edge crossings.
    GraphNodeOrdering nO = new GraphNodeOrdering(dN.getNewGraphMap(), dN.getNewAdjEdjeMap(), maxRankL);
    nO.orderRankSet();

    //create the graph out put, in this case the coordinate text file and save the image of the graph.
    GraphOutput gO = new GraphOutput(nO, graphOrder, dN.getNewEdgeCountMap());
    gO.graphCanvasInit();
  }
}
