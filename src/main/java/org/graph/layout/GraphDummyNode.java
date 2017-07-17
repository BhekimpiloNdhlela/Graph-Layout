package org.graph.layout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author  Bhekimpilo Ndhlela
 * @author  18998712
 * @since   Tuesday - 20 - 03 - 2017
 * @version 1.0
 */
public class GraphDummyNode {
  private Digraph digraph;
  private Integer numberVertices;
  private HashMap<Integer, Integer[]> aEdgeMap;
  private HashMap<Integer, Integer[]> graphMap;
  private HashMap<String, Integer> edgeCountMap;
  private Queue<String> adjEdgesBefore;
  private Queue<String> adjEdgesAfter;

  /**
   * Data-type that puts the dummy nodes for the edge pline in to both the map of the graph and the adjacency list
   * map.
   * @param digraph     the directed graph object.
   * @param graphMap    the rank map of the graph.
   */
  public GraphDummyNode(Digraph digraph, HashMap<Integer, Integer[]> graphMap) {
    this.graphMap       = graphMap;
    this.digraph        = digraph;
    this.numberVertices = digraph.V();
    this.aEdgeMap       = new HashMap<Integer, Integer[]>();
    this.adjEdgesAfter  = new Queue<String>();
    this.adjEdgesBefore = new Queue<String>();
    setAdjacentVerticesMap();
  }

  /**
   * acquire the new graph rank map after inserting the dummy nodes or vertices to the rank map, if the graph has
   * 10 nodes dummy nodes will be symbolized by 11, 12, 13 etc, depending on the number of dummy nodes the graph
   * requires.
   * @return graphMap    the new graph map that contains the dummy nodes/ vertices
   */
  public HashMap<Integer, Integer[]> getNewGraphMap() {
    return graphMap;
  }

  /**
   * acquire the new map that has the number of occurrences of edges after inserting dummy nodes or vertices
   * to the graph.
   * @return edgeCountMap    the map that has the number of edge occurrences.
   */
  public HashMap<String, Integer> getNewEdgeCountMap() {
    return edgeCountMap;
  }

  /**
   * Acquire the new edge adjacent list that contains the dummy nodes of the directed graph that is being
   * used by the program.
   * @return aEdgeMap    the new edge adjacent list of the graph
   */
  public HashMap<Integer, Integer[]> getNewAdjEdjeMap() {
    return aEdgeMap;
  }

  /**
   * adjacency map set, method for setting the adjacent edge map of the directed graph that is being used.
   */
  private void setAdjacentVerticesMap() {
    ArrayList<Integer> adjEdges = new ArrayList<Integer>();
    for(Integer v = 0; v < digraph.V(); v++) {
      for(Integer w : digraph.getAdj(v)) {
        adjEdges.add(w);
      }
      aEdgeMap.put(v, adjEdges.toArray(new Integer[adjEdges.size()]));
      adjEdges.clear();
    }
  }

  /**
   * method for getting the rank of the current node.
   * @param searchNode      the node to search for.
   * @param currentRank     the rank to search in for the node.
   * @return rankFoundIn    the rank were the node was found in.
   */
  private Integer getRankCurrentNode(Integer searchNode, Integer currentRank) {
    Integer [] searchRank;
    for(Integer i = currentRank; i > 0; i--) {
      searchRank = graphMap.get(i);
      for(int j = 0; j < searchRank.length; j++) {
        if(searchNode.equals(searchRank[j])) {
          return i;
        }
      }
    } return null;
  }

  /**
   * Adds the dummy nodes between the top vertex and the bottom vertex of that subgraph graph, for
   * example: if i have an edge for example 3 -> 4 and they need dummy node then the resulting string
   * will be 3 -> 5 -> 6 -> 4, were 5 & 6 are dummy nodes.
   * @param edgeCountMap   the map that contains the number of edge occurrences
   */
  public void addDummyNodesToMap(HashMap<String, Integer> edgeCountMap) {
    this.edgeCountMap                           = edgeCountMap;
    HashMap<Integer, Integer[]> adjVerticesMap  = aEdgeMap;
    Integer []currentRank;
    Integer [] adjV;
    Integer fRank;

    for(Integer wRank = graphMap.size() - 1 ; wRank > 1; wRank--) {
      currentRank = graphMap.get(wRank);
      for(Integer j = 0; j < currentRank.length; j++) {
        adjV = adjVerticesMap.get(currentRank[j]);
        for(Integer k = 0; k < adjV.length; k++) {
          fRank = getRankCurrentNode(adjV[k], wRank);
          if(fRank != null){
            if((wRank - fRank) > 1) {
              putDummyNode(wRank - 1, fRank, adjV[k], currentRank[j]);
            }
          }
        }
      }
    } updateEdgeCountMap(adjEdgesBefore, adjEdgesAfter);
  }

  /**
   * overloaded method for putting the dummy node in to the rank map of the directed graph.
   * @param dummyPosition    the position where to put the dummy node.
   * @param adjV             the bottom vertex or bottom node.
   * @param topV             the top vertex or the top node.
   */
  private void putDummyNode(Integer dummyPosition, Integer fRank, Integer adjV, Integer topV) {
    StringBuilder sbEdges     = new StringBuilder(50);    //for graph edge map
    StringBuilder sbRanks     = new StringBuilder(50);    //for graph rank map
    StringBuilder sbNewEdges  = new StringBuilder(50);    //for edge count map

    sbEdges.append(topV + " ");
    sbRanks.append(dummyPosition + 1 + " ");
    sbNewEdges.append(topV + " -> ");
    //put dummy nodes or vertices by appending.
    for(int i = dummyPosition; i > fRank; i-- ) {
      sbEdges.append(numberVertices++ + " ");
      sbNewEdges.append(numberVertices - 1 + " -> ");
      sbRanks.append(i + " ");
    }

    //put the incidents nodes after putting the dummies.
    sbEdges.append(adjV);
    sbNewEdges.append(adjV);
    //put the first rank.
    sbRanks.append(1);
    //avoid spaces at the end of the string.
    sbEdges.trimToSize();
    sbRanks.trimToSize();

    String []sEdges = sbEdges.toString().split(" ");
    String []sRanks = sbRanks.toString().split(" ");
    putDummyNode(topV, adjV, sEdges, sRanks);

    adjEdgesBefore.enqueue( topV + " -> " + adjV);
    adjEdgesAfter.enqueue(sbNewEdges.toString());
  }

  /**
   * Update the edge count map with edges that contain the dummy edges inbetween the edges.
   * @param  after     the edgeCounter map of the graph after adding dummy nodes
   * @param  before    the edgeCounter map of the graph before adding dummy nodes.
   */
  private void updateEdgeCountMap(Queue<String> before, Queue<String> after) {
    while(!before.isEmpty()){
      edgeCountMap.put(after.dequeue(), edgeCountMap.remove(before.dequeue()));
    }
  }

  /**
   * Put the dummy nodes into the graph rank  map of the directed graph.
   * @param topV      the vertex that is adjacent to the adjV
   * @param adjV      the vertex that is adjacent to top
   * @param sEdges    set of the adjacent edges.
   * @param sRanks    set in the rank.
   */
  private void putDummyNode(Integer topV, Integer adjV, String []sEdges, String []sRanks) {
    HashSet<Integer> rCopy               = new HashSet<Integer>();
    HashMap<Integer, Integer[]> aEdgeMap = this.aEdgeMap;
    Integer []workingE                   = aEdgeMap.get(topV);
    Integer []extEdgemap;

    for(int i = 0; i < workingE.length; i++) {
      rCopy.add(workingE[i]);
    }
    //update the edge adjacent map by adding the dummy edges to the map.
    Integer newIndex;
    rCopy.add(Integer.parseInt(sEdges[1]));
    rCopy.remove(adjV);
    aEdgeMap.put(topV, rCopy.toArray(new Integer[rCopy.size()]));

    for(Integer i = 1; i < sEdges.length - 1 ; i++) {
      extEdgemap    = new Integer[1];
      extEdgemap[0] = Integer.parseInt(sEdges[i + 1]);
      newIndex      = Integer.parseInt(sEdges[i]);
      aEdgeMap.put(newIndex, extEdgemap);
    }
    //update the rank Map of the graph by adding the dummy node to the map.
    Integer[] workingRank;
    Integer[] extRank;

    for(int i = 1; i < sEdges.length - 1 ; i++) {
      workingRank   = graphMap.get(Integer.parseInt(sRanks[i]));
      extRank       = new Integer[workingRank.length + 1];
      for(int j = 0; j < workingRank.length; j++) {
        extRank[j]  = workingRank[j];
      }
      extRank[extRank.length - 1] = Integer.parseInt(sEdges[i]);
      graphMap.put(Integer.parseInt(sRanks[i]), extRank);
    }
  }

  /**
   * String representation of the data type, [for: unit testing and, or debuging purposes]
   */
  public String toString() {
    StringBuilder sB = new StringBuilder(100);
    sB.append("NODE RANK MAP OF THE GIVEN GRAPH AFTER ADDING DUMMY NODES:\n");
    int graphMapSize = graphMap.size();
    int w = 1;
    //traverse the directed graph rank map
    while(w < graphMapSize) {
      Integer[] rankInput = graphMap.get(w);
      sB.append(w + "\t");
      for(int j = 0; j < rankInput.length; j++) {
        sB.append(rankInput[j] + " ");
      }
      w++;
      sB.append("\n");
    }

    sB.append("EDGE ADJACENT MAP OF THE GIVEN GRAPH AFTER ADDING DUMMY NODES:\n");
    HashMap<Integer, Integer[]> aEM = aEdgeMap;
    //traverse the directed graph rank map
    for(int i = 0; i < aEM.size(); i++){
      Integer[] aEdgeInput = aEM.get(i);
      sB.append(i + "\t");
      for(int j = 0; j < aEdgeInput.length; j++){
        sB.append(aEdgeInput[j] + " ");
      } sB.append("\n");
    } return (sB.toString());
  }
}
