import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author  Bhekimpilo Ndhlela
 * @author  18998712
 * @since   Wednesday - 22 - 03 - 2017
 * @version 1.0
 */
public class GraphNodeOrdering {
  private HashMap<Integer, Integer[]> rankMap;     //the map of the Graph ranks.
  private HashMap<Integer, Integer[]> adjEdgeMap;  //the map of the adjacent edges of the graph.
  private int maxRankSize = 0;

  /**
   * This data - type is essential, it is the last stage of the project this ins the data - type that accomplishes the
   * task of node ordering within respective ranks so as to accomplish minimal number of edge crossings at the final image
   * output.
   * @param rankMap      the map that contains the set of vertices in ranks of the graph.
   * @param adjEdgesMap  the map that contains the adjacent edges of the graph.
   */
  public GraphNodeOrdering(HashMap<Integer, Integer[]> rankMap, HashMap<Integer, Integer[]> adjEdgeMap, int maxRankSize) {
    this.rankMap      = rankMap;
    this.adjEdgeMap   = adjEdgeMap;
    this.maxRankSize  = maxRankSize;
  }

  /**
   * This method return the number of the ranks in the map of the directed graph, this method is not that neccessary because
   * the number of ranks can be acquired by calling rankMap.size() and subracting one from the result since the first slot
   * of the map is on purpose nullified.
   * @return numberOfRanks    the number of ranks in the map of the graph
   */
  public int getNumberOfRanks() {
    return rankMap.size() -1;
  }

  /**
   * This method returns the size of the bigest/ longest rank in the rank map of the directed graph, this method or the result
   * of this method is nessesariy in scalling the graph when drawing the output.
   * @return maxRankSize    the size of the longest rank in the map of th graph.
   */
  public int getMaxRankSize() {
    return maxRankSize;
  }

  /**
   * Acquire the rank of the diGraph after having the nodes ranked so as to minimize the edge crossings between
   * edges of different rank.
   * @return rankMap    the map that contains the set of vertices in ranks of the graph.
   */
  public HashMap<Integer, Integer[]> getOrderedRanks() {
    return rankMap;
  }

  /**
   * Acquire the diGraph adjacent list for the edges and are in the diGraph that are adjacent to each other
   * use this method to get the map of the diGraph that contains the adjacent edges.
   * @return adjEdgeMap   the map that contains the adjacent edges of the graph.
   */
  public HashMap<Integer, Integer[]> getAdjEdgesMap() {
    return adjEdgeMap;
  }

  /**
   * Order the rank sets of a diGraph to minimize the number of edge crossings when drawing the picture representation
   * of the diGraph.
   */
  public void orderRankSet() {
    orderRankSet(rankMap, adjEdgeMap);
  }

  /**
   * This is the heart of the data - type, this method accomplishes the task by getting the current rank to order the
   * nodes and ranks them accordingly, when accomplishing the task i start at the 2nd rank of the directed graph because
   * the sMin is accepted as ordered from the start, in other words we assume sMin is ordered, Also the node ordering is
   * done from sMin going up to sMax, well this was part of the spec, This Method is an implemented algorithm for the
   * Barycenter Method so as to minimise edge crossings.
   * @param rankMap     the map that contains the set of vertices in ranks of the graph.
   * @param edgeMap     the map that contains the adjacent edges of the graph.
   */
  private void orderRankSet(HashMap<Integer, Integer[]> rankMap, HashMap<Integer, Integer[]> edgeMap) {
    int numberOfRanks = rankMap.size();
    //do not consider the sMin rank because it is already ordered
    for(int i = 2; i < numberOfRanks; ++i) {
      Integer []orderingRank         = rankMap.get(i);                   //ordering rank set
      Integer []previousRank         = rankMap.get(i - 1);               //previous rank set
      Double  []orderingIndex        = new Double[orderingRank.length];  //the array to order with respect to.
      if(orderingRank.length > maxRankSize) maxRankSize = orderingRank.length;
      if(orderingRank.length == 1 || previousRank.length == 1) continue; //no need to order ranks of this atribute.

      for(int k = 0; k < orderingRank.length; ++k) {
        Integer []aEdgeList          = edgeMap.get(orderingRank[k]);     //edge adjacency list for the ordering element at hand
        Double indexSumOfAdjEdges    = 0.0;
        for(int l = 0; l < aEdgeList.length; ++l){
          //the sum of the index of nodes adjacent to the ordering rank element at hand.
          indexSumOfAdjEdges = indexSumOfAdjEdges + getNodeIndex(previousRank, aEdgeList[l]) + 1.0 ;
        } orderingIndex[k] = indexSumOfAdjEdges / (aEdgeList.length * 1.0); //Barycenter step
      }
      Merge.sort(orderingIndex, orderingRank);   //order the rank set by sorting it with respect to the ordering index.
      rankMap.put(i, orderingRank);              //update the rank map with the ordered rank set.
    }
  }

 /**
  * Brute force method for searching for a node so as to get its index or its possition in an array.
  * @return indexOfNode    the possition or index of the node being searched for.
  * @param  pRank          the previous rank or the rank were the node is being searched from.
  * @param  nodeToFind     the node that is being searched for.
  **/
  private double getNodeIndex(Integer[] pRank, Integer nodeToFind) {
    for(int i = 0; i < pRank.length; i++){
      if(nodeToFind.equals(pRank[i])) { return i; }
    } return -1.0;
  }

  /**
   * String representation of the data type, i am including this for debugging purposes it might be useless after the deploying
   * but i need it during this stage of the development procedure, It is essential.
   * @return toString    the string representation of the object
   */
  public String toString() {
    StringBuilder sb   = new StringBuilder("Ordered Nodes of the Graph : \n");
    int graphMapSize   = rankMap.size();
    Integer[] rankInput;
    for(int w = 1; w < graphMapSize; ++w) {
      rankInput = rankMap.get(w);
      sb.append(w).append("\t");
      for(int j = 0; j < rankInput.length; ++j) {
        sb.append(rankInput[j]).append(" ");
      } sb.append("\n");
    } return sb.toString();
  }
}
