package org.graph.layout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.Toolkit;

/**
 * @author   Bhekimpilo Ndhlela
 * @author   18998712
 * @since    SATURDAY - 25 - 03 - 2017
 * @version  1.0
 */
public class GraphOutput {
  private StringBuilder sBuilder;
  private HashMap <String, Integer> edgeOccurrences;
  private final HashMap<Integer, Integer[]> aEdgeMap; // adjacent edge map of the graph with dummy edges.
  private final HashMap<Integer, Integer[]> graphMap; // graph map that has been ordered dummy nodes included.
  private final HashMap<String, Integer> edgeCount;
  private final int graphInitOrder;                   // order of the graph before placing any dummy nodes.
  private final int graphFinalOrder;
  private final int rankMaxSize;
  private final int numberOfRanks;
  private int yScale = 0;
  private int xScale = 0;
  private double[][] nodeProperties;

  /**
   * Constructor of the object that Draws the image of the directed graph, writes the final coordinates of both
   * nodes and dummy nodes of the directed graph.
   * @param  graphInitOrder  order of the graph before placing dummy nodes(This is for tracing dummy nodes).
   * @param  graphMap        the map of the graph that contains the new graph properties including dummy nodes.
   */
  public GraphOutput(GraphNodeOrdering graphNodeOrder, int graphInitOrder, HashMap<String, Integer> edgeCount){
    this.graphInitOrder    = graphInitOrder;
    this.graphMap          = graphNodeOrder.getOrderedRanks();
    this.aEdgeMap          = graphNodeOrder.getAdjEdgesMap();
    this.graphFinalOrder   = aEdgeMap.size();
    this.edgeCount         = edgeCount;
    this.rankMaxSize       = graphNodeOrder.getMaxRankSize();
    this.numberOfRanks     = graphNodeOrder.getNumberOfRanks();
    setGraphOutputScale(rankMaxSize, numberOfRanks);
  }

  /**
   * Helper method for seting the graph layout canvas scale, at this point i still use the xScale and yScale for
   * the width and hieght of my canvas, This method crashes because i reelised StdDraw doesnt support large integers
   * and this hence causes the graph to crash, This can however be corrected by keeping track of the numbers and
   * the multiplier factor.
   * @param numberOfRanks  the integer representing the number of ranks in the graph.
   * @param rankMaxSize    the integer representing the rank with the most nodes/ vertices
   */
  private void setGraphOutputScale(int rankMaxSize, int numberOfRanks){
    int xMultiplyFactor = 0;                        
    int yMultiplyFactor = 0;
    
    xMultiplyFactor = (rankMaxSize   > 1000) ? 10 : 100;
    yMultiplyFactor = (numberOfRanks > 1000) ? 10 : 100;

    if(rankMaxSize > 2000)   {
      rankMaxSize      = rankMaxSize/2;
      xMultiplyFactor  = 1;
    }
    xScale = rankMaxSize * xMultiplyFactor;
    yScale = numberOfRanks * yMultiplyFactor;
  }

  /**
   * This method is the heart of the graph output, it has a sequence of steps that are used when the graph output is expected
   * it is the method that calls for the derivation of coordinates and it is also the method that invoke the coordinate text
   * file writer and it also triggers the method that draws vertices/ nodes, a method that draws edges and a method that finally
   * draws the names of the vertices of the graph and lastly it also saves the image.
   * @throws  FileNotFoundException      if file DNE [it is however, pointless because the file is initialized internally]
   */
  public void graphOutputProcedure() throws FileNotFoundException{
    nodeProperties = deriveCordinates();
    writeFinalCoordFile(nodeProperties, graphFinalOrder);
    drawEdges(nodeProperties, getTopOccuringEdges());
    drawVertices(nodeProperties);
  }

  /**
   * This method derives the coordinates of the individual nodes or vertices by trying to achieve equal
   * spacing on both the nodes in individual ranks and in between ranks themselves, this method uses a way
   * of dividing the length of both the width and height of the canvas by the nodes per rank plus 1 and
   * by the number of ranks respectively, and storing the coordinates as integers but this is however not
   * the best approximation of the spacing because of the Integer property of division of rounding down.
   * @throws  FileNotFoundException    if the text is not found
   */
  private double[][] deriveCordinates() throws FileNotFoundException{
    int numberOfRanks           = graphMap.size();
    double [][]nodeProperties   = new double[4][graphFinalOrder];
    int counter                 = 0;

    for(int i = 1; i < numberOfRanks ; ++i){
      Integer[] rank = graphMap.get(i);
      for(int j = 0; j < rank.length; ++j){
        double xSpacing             = (xScale * 1.0) / ((rank.length + 1) * 1.0);
        double ySpacing             = (yScale * 1.0) / ((numberOfRanks) * 1.0);
        nodeProperties [0][counter] = rank[j] * 1.0;
        nodeProperties [1][counter] = i * 1.0;
        nodeProperties [2][counter] = (xSpacing * (j + 1));
        nodeProperties [3][counter] = (ySpacing * i);
        counter++;
      }
    } return nodeProperties;
  }

  /**
   * Method to initialize the graph drawing canvas, this method set the minimum and maximum of both the horizontal
   * (x) and vertical (y) axis and it also set the dimension of the canvas size and lastly it set the pen radius that
   * will be used for drawing the nodes, the node's name and the edges of the diGraph.
   */
  public void graphCanvasInit()throws FileNotFoundException{
    StdDraw.setCanvasSize(xScale,yScale);
    StdDraw.setXscale(0, xScale);
    StdDraw.setYscale(0, yScale);
    StdDraw.clear(new Color(0, 119, 95));
    StdDraw.setPenRadius(0.005);

    JFrame frame           = new JFrame();
    JScrollPane scrollPane = new JScrollPane(StdDraw.getFrame().getContentPane());

    scrollPane.getViewport().setBackground(new Color (39, 85, 99));
    graphOutputProcedure();
    StdDraw.draw();
    frame.add(scrollPane);

    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(new Dimension(900 ,700));
    frame.setTitle(" GRAPH OUT PUT CANVAS");
    frame.setVisible(true);
  }

  /**
   * This method draws the edges of the diGraph it draws the adjacent edges of the diGraph after drawing
   * the nodes or the vertices so as to not disturb the names of the edges because of color interference.
   * @param nodeProperties      an Integer array containing the node: coordinates, ranks and names.
   */
  private void drawEdges(double [][]nodeProperties, Integer[] topOccuringEdges) {
    Color RED      = new Color(205, 0, 0);    //color of edge that occurs more frequent.
    Color ORANGE   = new Color(255, 102, 0);  //color of edge thath occurs 2nd frequent.
    Color YELLOW   = new Color(255, 255, 0);  //color of edge that occurs 3rd frequent.
    Color BLACK    = new Color(0, 0, 0);      //color of the rest of the edges.

    for(int i = 0; i < this.graphFinalOrder; i++) {
     Integer[]  aEdges = aEdgeMap.get((int)nodeProperties[0][i]);
      for(int j = 0; j < aEdges.length; ++j) {
        for(int k = 0; k < this.graphFinalOrder; ++k) {
          if((int)nodeProperties[0][k] == (aEdges[j])) {
            Integer edgeOccurence = edgeOccurrences.get((int)nodeProperties[0][i] + " -> " + (int)nodeProperties[0][k]);
            //Set Color equals to Red for edges that have the most occurrence.
            if(edgeOccurence.equals(topOccuringEdges[0]) && !topOccuringEdges[0].equals(1)) {
              StdDraw.setPenColor(RED);
            //set Color equals to Orange for edges that occur 2nd most.
            }else if(edgeOccurence.equals(topOccuringEdges[1]) && !topOccuringEdges[1].equals(0) && !topOccuringEdges[1].equals(1)) {
              StdDraw.setPenColor(ORANGE);
            //set color equals to yellow for edges that occur 3rd most.
            }else if(edgeOccurence.equals(topOccuringEdges[2]) && !topOccuringEdges[2].equals(0) && !topOccuringEdges[2].equals(1)) {
              StdDraw.setPenColor(YELLOW);
            //set the color to black for ever edge that does not occur most, 2nd most or 3rd most.
            }else { StdDraw.setPenColor(BLACK); }
            //draw the directed edge of the graph.
            StdDraw.line(nodeProperties[2][i], nodeProperties[3][i], nodeProperties[2][k], nodeProperties[3][k]);
          }
        }
      }
    }
  }

  /**
   * Find and store the edge that occurs the most in the directed graph.
   * @return topOccuringEdges    the top 3 occurring edges
   */
  private Integer[] getTopOccuringEdges() {
    Integer sigmaEdge        = 0;
    edgeOccurrences          = new HashMap<String, Integer>();
    HashSet<Integer> tempSet = new HashSet<Integer>();

    //separate the (string) edges with dummy nodes.
    for(String temp: edgeCount.keySet()) {
      String []currentEdge = temp.split(" -> ");
      Integer edgeOccurance = edgeCount.get(temp);
      sigmaEdge += edgeOccurance;
      for(int i = 1; i < currentEdge.length; ++i) {
        edgeOccurrences.put((currentEdge[i - 1] + " -> " + currentEdge[i]), edgeCount.get(temp));
        tempSet.add(edgeCount.get(temp));
      }
    }
    edgeCount.clear();  //avoid loitering of the elements in this data structure(map).
    return getTopOccuringEdges(tempSet.toArray(new Integer[tempSet.size()]));
  }

  /**
   * Find and store the top three edges that occur the most in the directed graph.
   * @param edgeTally            the number of times an edge occur
   * @return topOccuringEdges    the top 3 occurring edges
   */
  private Integer[] getTopOccuringEdges(Integer[] edgeTally) {
    Integer[] topOccurringEdges = { 0, 0, 0};

    for(Integer current : edgeTally) {
      //put 1st top occurring edge.
      if(current > topOccurringEdges[0]) {
        topOccurringEdges[2] = topOccurringEdges[1];
        topOccurringEdges[1] = topOccurringEdges[0];
        topOccurringEdges[0] = current;
      //put 2nd top occurring edge.
      }else if(current > topOccurringEdges[1]) {
        topOccurringEdges[2] = topOccurringEdges[1];
        topOccurringEdges[1] = current;
      //put 3rd top occurring edge.
      }else if(current > topOccurringEdges[2]) { topOccurringEdges[2] = current; }
    } return topOccurringEdges;
  }

  /**
   * This method draws the vertices or the nodes in the diGraph, but it does not draw the dummy
   * nodes in this case since they are only used to structure the diGraph output image.
   * @param nodeProperties    an Integer array containing the node: coordinates, ranks and names.
   */
  private void drawVertices(double [][]nodeProperties) {
    for(int i = 0; i < graphFinalOrder; ++i) {
      if(nodeProperties[0][i] < graphInitOrder) {
        //draw a filled circle representing the node/ vertex.
        StdDraw.setPenColor(new Color(0, 0, 0));
        StdDraw.filledCircle(nodeProperties[2][i], nodeProperties[3][i], 10);
        //draw or write the vertex/ node name
        StdDraw.setPenColor(new Color(8, 173, 154));
        StdDraw.text(nodeProperties[2][i], nodeProperties[3][i], ""+(int)nodeProperties[0][i]);
      }
    }
  }

  /**
   * Method that dumps a text file with the final coordinates for each node and the final spline points
   * for each edge, for automatic marking purposes, This text file also includes the coordinates of Dummy
   * nodes/ vertices.
   * @throws FileNotFoundException  if file DNE [but this is pointless the file is created internally].
   * @param  nodeProperties         an Integer array containing the node: coordinates, ranks and names.
   * @param  newGraphOrder          the new Graph order/ size with dummy nodes included.
   */
  private void writeFinalCoordFile(double [][] nodeProperties, int graphFinalOrder){
    String outPutTextFile       = new String("output.txt");
    try {
      PrintWriter outPutStream  = new PrintWriter(outPutTextFile);
      outPutStream.println("Node Name:\t" + "Rank Of Node:\t\t" + "Coordinates (x, y):");
      //write to the output text file.
      for(int i = 0; i < graphFinalOrder; ++i) {
        int nameNode        = (int)nodeProperties[0][i];
        int rankNode        = (int)nodeProperties[1][i];
        double xCoordinate  = nodeProperties[2][i];
        double yCoordinate  = nodeProperties[3][i];
        outPutStream.println("\t"+nameNode+"\t\t\t["+rankNode+"]\t\t\t("+xCoordinate+" , "+yCoordinate+")");
        outPutStream.flush();
      }
      outPutStream.close();
    } catch (FileNotFoundException e) { /* the file is declared by the program/ created by the program no need to catch */ }
  }

  /**
   * String representation of the object or the Data Type [for: Debuging and or Unit Testing]
   */
  public String toString(){
    StringBuilder sb = new StringBuilder(10000);
    sb.append("Node Name:\tRank Of Node:\t\tCoordinates(x,y):\n");
     
    for(int i = 0; i < graphFinalOrder; ++i){  
      int nameNode        = (int)nodeProperties[0][i];
      int rankNode        = (int)nodeProperties[1][i];
      double xCoordinate  = nodeProperties[2][i];
      double yCoordinate  = nodeProperties[3][i];
      sb.append("\t").append((int)nameNode).append("\t\t\t[").append((int)rankNode).append("]\t\t\t(");
      sb.append((int)xCoordinate).append(" , ").append((int)yCoordinate).append(")\n");
    } return sBuilder.toString();
  }
}
