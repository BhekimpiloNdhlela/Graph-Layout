package org.graph.layout;

import static org.junit.Assert.*;
import java.io.FileNotFoundException;

import org.graph.layout.Digraph;
import org.graph.layout.GraphDummyNode;
import org.graph.layout.GraphInitializer;
import org.graph.layout.GraphNodeOrdering;
import org.graph.layout.GraphOutput;
import org.graph.layout.GraphRanking;
import org.junit.Test;

public class LayoutUnitTester {
  private GraphInitializer   iGraph;
  private Digraph            dGraph;
  private GraphRanking       rGraph;	
  private GraphDummyNode     dNGraph;
  private GraphNodeOrdering  noGraph;
  private int                graphOrder;
  private int   			 maxRankSize;
  private GraphOutput        oGraph;
  
  @Test
  public void test() throws FileNotFoundException {
    testGraphInitializer(); 
    testGraphRanking();
    testGraphDummyNode();
    testGraphNodeOrdering();
    testGraphOutPut();
  }
  
  public void testGraphInitializer() {
    iGraph = new GraphInitializer("testingGraph.txt");
    iGraph.readGraphInput();
    dGraph = iGraph.getDigraph(); 
    iGraph.isCyclic(dGraph);

    assertEquals(5, iGraph.getGraphOrder());
    assertEquals("testingGraph.txt", iGraph.getInputFilePath());
    assertEquals("inputFile path:\ttestingGraph.txt\nGraph size:\t7\nGraph order:\t5\nVertex Set:\n0 1 2 3 4 ", this.iGraph.toString());
  }
  
  public void testGraphRanking(){
    rGraph      = new GraphRanking(dGraph);
    graphOrder  = iGraph.getGraphOrder(); 
    rGraph.rankVertices(graphOrder);
    maxRankSize = rGraph.getCurrantMaxRankLength();
    
    assertEquals("Ranks of the map before Ordering:\n1\t4 \n2\t3 \n3\t2 \n4\t1 \n5\t0 \n",this.rGraph.toString());
  }
  
  public void testGraphDummyNode(){
    dNGraph = new GraphDummyNode(dGraph, rGraph.getGraphMap());
    dNGraph.addDummyNodesToMap(iGraph.getEdgeCountMap());
    
    String messageRank   = new String("NODE RANK MAP OF THE GIVEN GRAPH AFTER ADDING DUMMY NODES:\n");
    String messageEdgeL  = new String("EDGE ADJACENT MAP OF THE GIVEN GRAPH AFTER ADDING DUMMY NODES:\n");
    String ranks         = new String("1\t4 \n2\t3 10 \n3\t2 7 9 \n4\t1 5 6 8 \n5\t0 \n");
    String edgeL         = new String("0\t1 5 6 8 \n1\t2 \n2\t3 \n3\t4 \n4\t\n5\t2 \n6\t7 \n7\t3 \n8\t9 \n9\t10 \n10\t4 \n");
    
    assertEquals(messageRank + ranks + messageEdgeL + edgeL, dNGraph.toString());
  }
  
  public void testGraphNodeOrdering() {
    noGraph = new GraphNodeOrdering(dNGraph.getNewGraphMap(), dNGraph.getNewAdjEdjeMap(), maxRankSize);
    noGraph.orderRankSet();
   
    String message       = new String("Ordered Nodes of the Graph : \n"); 
    String ranks         = new String("1\t4 \n2\t3 10 \n3\t2 7 9 \n4\t1 5 6 8 \n5\t0 \n");
    
    assertEquals(message + ranks, noGraph.toString());
  } 
  
  public void testGraphOutPut() {
    oGraph = new GraphOutput(noGraph, graphOrder, dNGraph.getNewEdgeCountMap());
    try {
      oGraph.graphCanvasInit();
      String topLine         = new String("Node Name:\tRank Of Node:\t\tCoordinates(x,y):\n");
      String subSequantLines = new String("\t4\t\t\t[1]\t\t\t(200 , 83)\n"   +
     	                                  "\t3\t\t\t[2]\t\t\t(133 , 166)\n"    +
      	                                  "\t10\t\t\t[2]\t\t\t(266 , 166)\n"  +
      	                                  "\t2\t\t\t[3]\t\t\t(100 , 250)\n"    +
      	                                  "\t7\t\t\t[3]\t\t\t(200 , 250)\n"   +
      	                                  "\t9\t\t\t[3]\t\t\t(300 , 250)\n"   +
      	                                  "\t1\t\t\t[4]\t\t\t(80 , 333)\n"   +
      	                                  "\t5\t\t\t[4]\t\t\t(160 , 333)\n"   +
      	                                  "\t6\t\t\t[4]\t\t\t(240 , 333)\n"  +
      	                                  "\t8\t\t\t[4]\t\t\t(320 , 333)\n"  +
      	                                  "\t0\t\t\t[5]\t\t\t(200 , 416)\n");
      
      assertEquals(topLine + subSequantLines, oGraph.toString());
	  } catch (FileNotFoundException e) { }
  } 
}