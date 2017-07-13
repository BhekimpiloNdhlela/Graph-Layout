package org.graph.layout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 * @author   Bhekimpilo Ndhlela
 * @author   18998712
 * @since    Friday - 17 - 03 - 2017
 * @version  1.0
 */
public class GraphInitializer{
  private final String graphInputFile;  // name of the input text.
  private int graphOrder = 0;           // number of vertex.
  private int graphSize  = 0;           // number of edges.
  private int [] vertexSet;             // the vertex set of the graph.
  private Digraph diGraph;              // the diGraph object of input.
  private HashMap <String, Integer> frequencyOfEdges;
 
 /**
  * Constructor that takes the input file and computes it so as to achieve the basic step
  * of the program of which is to initialize the application by creating the DiGraph and
  * confirming if the DiGraph is directed or it does not contain any loops or self edges.
  * @param graphInputFile    text file of the input data file
  */
  public GraphInitializer(String graphInputFile){
    this.graphInputFile = graphInputFile;
  }

 /**
  * Method for returning the size of the graph, in Operation Research
  * the size of a graph is the cardinality of the edge set.
  * @param graphSize    this is the number of edges in the graph
  */
  private void setGraphSize(int graphSize){
    this.graphSize = graphSize;
  }

 /**
  * Method that set the order of the graph the order of a graph is the
  * operation research term meaning the number of vertices in graph.
  * @param vertices    set for extracting without duplicate vertices
  */
  private void setGraphOrder(Set<String> vertices){
    graphOrder = vertices.size();
  }

 /**
  * Method that returns the number of vertices within the graph that we are
  * using for the algorithm.
  * @return graphOrder    the number of vertices or nodes in the diGraph.
  */
  public int getGraphOrder(){
    return graphOrder;
  }

 /**
  * Method for parsing the Input text file name This is the path of
  * the file that was used to construct the Graph.
  * @return graphInputTxtFileName    this is the name of the txtFile.
  */
  public String getInputFilePath(){
    return graphInputFile;
  }

 /**
  * Method that returns the DiGraph representation of the graph or the input
  * data that is being used.
  * @return diGraph    diGraph representation of graph
  */
  public Digraph getDigraph(){
    return diGraph;
  }

  /**
   * Method that return a map of haw many times individual edges occur in the
   * text file when reading the text file, this map contains the number of
   * occurance of each edge in the input text file of the directed grap.
   * @return frequencyOfEdges    the map of how many times an edge occurs
   */
  public HashMap<String, Integer> getEdgeCountMap(){
    return frequencyOfEdges;
  }

 /**
  * Method that creates the node set or the vertex set of the diGraph, By putting it into
  * and Integer array.
  * @param vertices    set for extracting without duplicate vertices
  */
  private void makeVertexSet(Set<String> vertices){
    vertexSet = new int[getGraphOrder()];
    int i = 0;
    for(String vertex : vertices){
      vertexSet[i] = Integer.parseInt(vertex);
      i++;
    }
  }

 /**
  * Method for checking whether a Graph or a vertex has a loop, The program terminates
  * if any self edges or loop is detected or identified in the diGrap input.
  * @param vertex0    vertex to be check if it has a loop
  * @param vertex1    vertex used to check for the loop
  */
  private void isLoop(String vertex0, String vertex1){
    if(vertex0.equals(vertex1)){
      System.out.println("A Loop or a Self-Edge was identified in the Graph.");
      System.out.println("Program Terminating...");
      System.exit(0);
    }
  }

 /**
  * Method for checking if the DiGraph is cyclic, if the DiGraph is cyclic the the program
  * or the application terminate, it is dependent on DirectedCycle data type.
  * @param digraph    the diGraph that is being used for the program Layout.
  */
  public void isCyclic(Digraph digraph){
    DirectedCycle finder = new DirectedCycle(diGraph);
    if(finder.hasCycle()) {
      System.out.println("Directed cycle Was identified in the Graph.");
      System.out.println("Program Terminating...");
      System.exit(0);
    }
  }

 /**
  * Method that check if whether the input file of the DiGraph is empty this method will print a
  * message warning the user that the input text file of the diGraph is empty before terminating the
  * program.
  * @param inputData    the input data file that contains the graph edges
  */
  public void isInputFileEmpty(Scanner inputData){
    if(!(inputData.hasNext())) {
      System.out.println("The Input text file is empty. Please provide an input file with the graph.");
      System.out.println("Program Terminating...");
      System.exit(0);
    }
  }

 /**
   * This method uses a hash map to count the number of duplicate edges in the directed graph input
   * text file, the map holds the number of of individual edge duplicates.
   * @param edge          the edge reap from the text file
   * @return duplicate    is this edge a duplicate?
   */
  private boolean isEdgeDuplicate(String edge){
    Integer oldTally = frequencyOfEdges.get(edge);
    boolean duplicate = false;

    if(oldTally == null)  oldTally  = 0;
    else                  duplicate = true; 

    frequencyOfEdges.put(edge, oldTally + 1);
    return duplicate;
  }

 /**
  * This method reads the graph input text file and initializes the graph layout program as whole
  * if no errors are encountered in the program, It also creates the edges of the graph.
  * @throws FileNotFoundException    if the input text file is not found
  */
  public void readGraphInput() {
    try {
      int size                = 0;
      frequencyOfEdges        = new HashMap<String, Integer>();
      Scanner inputData       = new Scanner(new File(graphInputFile));
      Set<String> vertexSet   = new HashSet<String>();
      Queue<String[]> edges   = new Queue<String[]>();

      // check if input file is empty terminate if empty.
      isInputFileEmpty(inputData);
      //read the data in the file.
      while(inputData.hasNextLine()){
        String edge = inputData.nextLine();
        if(!isEdgeDuplicate(edge)){
          size++;
          String []vertices = edge.split(" -> ");
          isLoop(vertices[0], vertices[1]);
          edges.enqueue(vertices);
          //add to a set to eliminate duplicates so as to get the number of nodes in
          //the graph, and also for creating the vertex set.
          vertexSet.add(vertices[0]);
          vertexSet.add(vertices[1]);
        }
      }
      inputData.close();              // close scanner(inputData)
      setGraphOrder(vertexSet);       // set the number of nodes in the graph.
      makeVertexSet(vertexSet);       // make the vertex set of the graph.
      setGraphSize(size);             // set the graph size.
      createGraphEdges(edges, size);  // create the edges(arcs) of the diGraph.
    } catch (FileNotFoundException e) {
      System.out.println("File Not Found, program exiting");
      System.exit(0);
    }
  }

 /**
  * Methods that creates the edges of the graph this completes this task by creating both the edges that
  * contain the directedEdges and the other one that is default method in the diGraph class by Princeton
  * University, This method makes use of an auxiliary array that takes in dequeued adjacent edges.
  * @param edges   a queue that has an array String of edges of the graph.
  * @param size    the number of edges connected to nodes/ vertices.
  */
  private void createGraphEdges(Queue<String[]> edges, int size){
    diGraph = new Digraph(getGraphOrder());
    for(int i = 0; i < size; i++){
      String[] auxilary = edges.dequeue();
      int a             = Integer.parseInt(auxilary[0]);
      int b             = Integer.parseInt(auxilary[1]);
      // make edges of the graph
      diGraph.addEdge(a, b);
      // make the directed edges of the graph.
      diGraph.addEdge(new DirectedEdge(a, b, 1));
    }
  }

  /**
   * String Representatiion of the object
   */
  public String toString(){
    StringBuilder sb = new StringBuilder(10000);
    sb.append("inputFile path:\t").append(graphInputFile).append("\n");
    sb.append("Graph size:\t").append(graphSize).append("\n");
    sb.append("Graph order:\t").append(graphOrder).append("\n");
    sb.append("Vertex Set:\n");
    for(int i : vertexSet){
      sb.append(i).append(" ");
    }
    sb.trimToSize();
    return sb.toString();
  }
}
