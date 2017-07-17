package org.graph.layout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author    Bhekimpilo Ndhlela
 * @author    18998712
 * @since     Friday - 17 - 03 - 2017
 * @version   1.0
 */
public class GraphRanking {
  private Digraph diGraph;
  private HashMap<Integer, Integer[]> graphMap;
  private Integer[] sMin, sMax;

  /**
   * constructor of the data type/ object of the graph ranking step, this data type put all
   * the nodes in they respective ranks from the sMinimum set to sMaximum set.
   * @param diGraph    Directed graph object of the Graph.
   */
  public GraphRanking(Digraph diGraph) {
    this.diGraph    = diGraph;
    this.graphMap   = new HashMap<Integer, Integer[]>();
  }

  /**
   * set the maximum set of the directed graph.
   * @param sMax    Vertices in the graph sMaximum rank.
   */
  private void setsMax(Integer[] sMax) {
    this.sMax = sMax;
  }

  /**
   * set the minimum set of the directed graph.
   * @param sMin    Vertices in the graph sMinimum rank.
   */
  private void setsMin(Integer[] sMin) {
    this.sMin = sMin;
  }

  /**
   * Initialize the directed graph ranking procedure of the application, I adopted the convention
   * of putting the minimum rank in the first index of the map and the maximum rank on the last
   * index of the graph map.
   * @param graphOrder    the order of the graph is the number of vertices in the graph.
   */
  public void rankVertices(int graphOrder){
    setGraphMap(graphOrder);
  }

  /**
   * get the map of the directed graph, this returns all the ranks in the map ranked from the minimum
   * rank set to the maximum rank set of the directed graph.
   * @return graphMap    the rank of the graph (RankMap)
   */
  public HashMap<Integer, Integer[]> getGraphMap() {
    return graphMap;
  }

  /**
   * this method compaqres the sMax and sMin length and and return an integer which is the biggest between
   * the two ranks.
   * @return currentMaxRank    the maximum numbr of ranks between sMin and sMax
   */
  public int getCurrantMaxRankLength(){
    if             (sMin.length > sMax.length)  { return sMin.length; }
    else if        (sMin.length < sMax.length)  { return sMax.length; }
    else                                        { return sMin.length; }
  }

  /**
   * check if the directed graph given by the user is valid, is there at least one node in both the
   * minimum set and or maximum set.
   * @param sMaxLength    the number of vertices in the maximum set of the directed graph.
   * @param sMinLength    the number of vertices in the minimum set of the directed graph.
   */
  private void isValid(int sMaxLength, int sMinLength) {
    if((sMinLength == 0) || (sMaxLength == 0)){
      System.out.println("The input data does not have at least one edge in Smax and/or one edge in Smin.");
      System.out.println("Program Terminating...");
      System.exit(0);
    }
  }

  /**
   * Set the graph put the nodes with no incoming nodes into the maximum set queue and the nodes
   * that have no out going edges in the minimum set queue and then the rest put them in the middle
   * rank set queue, they will be separated in step two of this procedure.
   * @param order    the size of the directed graph/ the number of vertices or nodes of the graph.
   */
  private void setGraphMap(Integer order){
    Queue<Integer> rankSetMax = new Queue<Integer>();
    Queue<Integer> rankSetMin = new Queue<Integer>();
    Queue<Integer> rankSetMid = new Queue<Integer>();

    for(Integer i = 0; i < order; i++){
      if       (diGraph.outdegree(i) == 0) { rankSetMin.enqueue(i); }
      else if  (diGraph.indegree(i) == 0)  { rankSetMax.enqueue(i); }
      else                                 { rankSetMid.enqueue(i); }
    }
    setGraphMap(rankSetMax, rankSetMin, rankSetMid);
  }

  /**
   * Set the directed graph map by using longest path for the middle set so that they can be ranked this
   * is the second of this procedure.
   * @param rankSetMax    the sMaximum set(rank) of the directed graph the graph.
   * @param rankSetMin    the sMinimum set(rank) of the directed graph the graph.
   * @param rankSetMid    the sMiddle(all sets in the middle sets) of directed graph the graph.
   */
  private void setGraphMap(Queue<Integer> rankSetMax, Queue<Integer> rankSetMin, Queue<Integer> rankSetMid){
    Integer []sMin = new Integer[rankSetMin.size()]; // declaring the sMin set
    Integer []sMax = new Integer[rankSetMax.size()]; // declaring the sMax set
    Integer []sMid = new Integer[rankSetMid.size()]; // declaring the sMid set
    isValid(sMax.length, sMin.length);    // check if this is a valid graph

    // make an array for the middle rank set
    for(int i = 0; i < sMid.length; i++) { sMid[i] = rankSetMid.dequeue();}
    // make an array for the minimum rank set
    for(int i = 0; i < sMin.length; i++) { sMin[i] = rankSetMin.dequeue(); }
    // make an array for the maximum rank set
    for(int i = 0; i < sMax.length; i++) { sMax[i] = rankSetMax.dequeue(); }

    setsMin(sMin);           // place the minimum set into the MAP
    setsMax(sMax);           // place the maximum set into the MAP
    setGraphMap(sMin, sMid); // continue setting the graph MAP
  }

  /**
   * Set the graph map by dividing the nodes in the sMiddle into their respective ranks using the longest
   * path for a certain node to any node in the sMinimum rank set, third step and final step of ranking.
   * @param sMin    the set of all the elements in the sMin set/ rank.
   * @param sMid    all the set of the diGraph in the middle set/ rank.
   */
  private void setGraphMap(Integer []sMin, Integer []sMid) {
    ArrayList<Integer> rankNumber   = new ArrayList<Integer>();
    Set<Integer> graphRanks         = new HashSet<Integer>();

    //check if there is a path if not ignore.
    for(int i = 0; i < sMid.length; i++){
      AcyclicLP acyclicLP = new AcyclicLP(diGraph, sMid[i]);
      int lPath = 0;
      for(int j = 0; j < sMin.length; j++){
        //distance == to NEGATIVE_Infinity there is no path.
        double longDist = acyclicLP.distTo(sMin[j]);
        if(longDist != Double.NEGATIVE_INFINITY){
          //put the longest path distance into the lPath array if there is a path.
          lPath = (longDist > lPath) ? (int)longDist : lPath; 
        }
      }
      int p = lPath + 1;
      rankNumber.add(p);
      graphRanks.add(p);
    } addToMap(rankNumber, sMid, graphRanks);
  }

  /**
   * Add the nodes or vertices in to their respective rank sets, this method works on the middle rank
   * set because already the sMinimum and sMaximum sets are already in their ranks, This method puts the
   * middle set into separate ranks in the Graph rank Map.
   * @param rankNumber   number of all the rank number in the graph.
   * @param sMiddle      all the ranks of the diGraph that are in the middle ranks.
   * @param graphRanks   number of all graph ranks, that are in the graph.
   */
  private void addToMap(ArrayList<Integer> rankNumber, Integer[] sMiddle, Set<Integer> graphRanks){
    Queue<Integer> temp   = new Queue<Integer>();
    int rankSize          = graphRanks.size();
    int []gRanks          = new int[rankSize];
    int count             = 0;
    // copy graph ranks into array/ should have used to array instead.
    for(Integer i : graphRanks){
      gRanks[count] = i;
      count++;
    }
    // put sets into ranks
    for(int i = 0; i < rankSize; i++){
      for(int j = 0; j < rankNumber.size(); j++){
        if(gRanks[i] == (int)rankNumber.get(j)){
          temp.enqueue(sMiddle[j]);
        }
      }
      int subArraySize = temp.size();
      Integer[] subArray = new Integer[subArraySize];
      for(int j = 0; j < subArraySize; j++){
        subArray[j] = temp.dequeue();
      } graphMap.put(gRanks[i], subArray);
    }
    graphMap.put(0, null);                     // entry 0 of the map is marked empty(null).
    graphMap.put(1, sMin);                     // put the sMin set into the map.
    graphMap.put(gRanks[count - 1] + 1, sMax); // put the sMax set into the map.
  }

  /**
   * String representation for the object, This String object prints the ranks of the Directed graph
   * from the Map.
   */
  public String toString(){
    StringBuilder sB = new StringBuilder("Ranks of the map before Ordering:\n");
    int graphMapSize = graphMap.size();
    Integer[] rankInput;
    for(int w = 1; w < graphMapSize; w++){
      rankInput = graphMap.get(w);
      sB.append(w).append("\t");
      for(int j = 0; j < rankInput.length; j++){
        sB.append(rankInput[j]).append(" ");
      }
      sB.append("\n");
    }
    return sB.toString();
  }
}
