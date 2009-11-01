/*
Copyright 2008 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.graph.dhns.filter;

import java.util.HashMap;
import java.util.Map;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.graph.dhns.DhnsGraphController;
import org.gephi.graph.dhns.core.Dhns;
import org.gephi.graph.dhns.core.EdgeProcessor;
import org.gephi.graph.dhns.core.GraphFactoryImpl;
import org.gephi.graph.dhns.core.GraphStructure;
import org.gephi.graph.dhns.core.IDGen;
import org.gephi.graph.dhns.core.TreeStructure;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.graph.HierarchicalDirectedGraphImpl;
import org.gephi.graph.dhns.node.AbstractNode;
import org.gephi.graph.dhns.views.ViewImpl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class DhnsTestFiltering {

    private Dhns dhnsGlobal;
    private HierarchicalDirectedGraphImpl graphGlobal;
    private Map<String, Node> nodeMap;
    private Map<String, Edge> edgeMap;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        DhnsGraphController controller = new DhnsGraphController();
        dhnsGlobal = new Dhns(controller);
        graphGlobal = new HierarchicalDirectedGraphImpl(dhnsGlobal, dhnsGlobal.getGraphStructure());
        nodeMap = new HashMap<String, Node>();
        edgeMap = new HashMap<String, Edge>();

        TreeStructure treeStructure = dhnsGlobal.getGraphStructure().getStructure();
        GraphFactoryImpl factory = dhnsGlobal.factory();

        //Nodes
        //System.out.println("-----Global-----");
        for (int i = 0; i < 10; i++) {
            Node node = factory.newNode();
            node.getNodeData().setLabel("Node " + i);
            graphGlobal.addNode(node);
            nodeMap.put(node.getNodeData().getLabel(), node);
        //System.out.println("Node " + i + " added. Id = " + node.getId());
        }
        //System.out.println("---End Global---");

        //Edges
        Node node0 = nodeMap.get("Node 0");
        Node node1 = nodeMap.get("Node 1");
        Node node2 = nodeMap.get("Node 2");
        Node node3 = nodeMap.get("Node 3");
        Node node4 = nodeMap.get("Node 4");
        Node node5 = nodeMap.get("Node 5");
        Node node6 = nodeMap.get("Node 6");
        Node node7 = nodeMap.get("Node 7");
        Node node8 = nodeMap.get("Node 8");

        AbstractEdge edge1 = factory.newEdge(node4, node5, 1f, true);
        AbstractEdge edge2 = factory.newEdge(node5, node6, 4f, true);
        AbstractEdge edge3 = factory.newEdge(node6, node5, 3f, true);
        AbstractEdge edge4 = factory.newEdge(node7, node7, 5f, true);
        AbstractEdge edge5 = factory.newEdge(node4, node4, 2f, true);
        AbstractEdge edge6 = factory.newEdge(node2, node1, 1f, true);
        AbstractEdge edge7 = factory.newEdge(node2, node3, 10f, true);
        AbstractEdge edge8 = factory.newEdge(node2, node5, 12f, true);

        graphGlobal.addEdge(edge1);
        graphGlobal.addEdge(edge2);
        graphGlobal.addEdge(edge3);
        graphGlobal.addEdge(edge4);
        graphGlobal.addEdge(edge5);
        graphGlobal.addEdge(edge6);
        graphGlobal.addEdge(edge7);
        graphGlobal.addEdge(edge8);

        edgeMap.put("4-5", edge1);
        edgeMap.put("5-6", edge2);
        edgeMap.put("6-5", edge3);
        edgeMap.put("7-7", edge4);
        edgeMap.put("4-4", edge5);
        edgeMap.put("2-1", edge6);
        edgeMap.put("2-3", edge7);
        edgeMap.put("2-5", edge8);
    }

    @Test
    public void testSetUp() {
        Node[] expected = new Node[10];
        for (int i = 0; i < nodeMap.size(); i++) {
            expected[i] = nodeMap.get("Node " + i);
        }
        Node[] actual = graphGlobal.getNodes().toArray();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testFiltering() {
        //dhnsGlobal.getGraphStructure().getStructure().showTreeAsTable();
        graphGlobal.getView().addPredicate(new DegreePredicate(3, 5));
        Node[] actual = graphGlobal.getNodes().toArray();
        for (int i = 0; i < actual.length; i++) {
            System.out.println(actual[i].getId());
        }
    //((ViewImpl) graphGlobal.getView()).getGraphStructure().getStructure().showTreeAsTable();
    }

    @Test
    public void testMetaEdgesProcessing() {
        /*Dhns dhns = new Dhns(new DhnsGraphController());
        GraphStructure graphStructure = dhns.getGraphStructure();
        TreeStructure treeStructure = graphStructure.getStructure();
        GraphFactoryImpl factoryImpl = new GraphFactoryImpl(new IDGen(), null);
        EdgeProcessor edgeProcessor = new EdgeProcessor(dhns);

        AbstractNode n1 = factoryImpl.newNode();
        AbstractNode n2 = factoryImpl.newNode();
        AbstractNode n3 = factoryImpl.newNode();
        AbstractNode n4 = factoryImpl.newNode();
        AbstractNode n5 = factoryImpl.newNode();
        AbstractNode n6 = factoryImpl.newNode();

        treeStructure.insertAsChild(n1, treeStructure.getRoot());
        treeStructure.insertAsChild(n4, treeStructure.getRoot());
        treeStructure.insertAsChild(n2, n1);
        treeStructure.insertAsChild(n3, n1);
        treeStructure.insertAsChild(n5, n4);
        treeStructure.insertAsChild(n6, n4);

        AbstractEdge e23 = factoryImpl.newEdge(n2, n3);
        AbstractEdge e25 = factoryImpl.newEdge(n2, n5);
        AbstractEdge e52 = factoryImpl.newEdge(n5, n2);
        AbstractEdge e53 = factoryImpl.newEdge(n5, n3);
        AbstractEdge e65 = factoryImpl.newEdge(n6, n5);

        n2.getEdgesOutTree().add(e23);
        n3.getEdgesInTree().add(e23);
        n2.getEdgesOutTree().add(e25);
        n5.getEdgesInTree().add(e25);
        n5.getEdgesOutTree().add(e52);
        n2.getEdgesInTree().add(e52);
        n5.getEdgesOutTree().add(e53);
        n3.getEdgesInTree().add(e53);
        n6.getEdgesOutTree().add(e65);
        n5.getEdgesInTree().add(e65);

        n1.setEnabled(true);
        n4.setEnabled(true);

        edgeProcessor.computeMetaEdges(graphStructure);

        HierarchicalDirectedGraphImpl graph = new HierarchicalDirectedGraphImpl(dhns, graphStructure);
        Edge[] actual = graph.getMetaEdges().toArray();
        assertEquals(2, actual.length);
        MetaEdgeImpl metaEdge14 = (MetaEdgeImpl)actual[0];
        MetaEdgeImpl metaEdge41 = (MetaEdgeImpl)actual[1];
        assertSame(n1, metaEdge14.getSource());
        assertSame(n4, metaEdge14.getTarget());
        assertEquals(1f, metaEdge14.getWeight(),0);
        assertSame(n4, metaEdge41.getSource());
        assertSame(n1, metaEdge41.getTarget());
        assertEquals(2f, metaEdge41.getWeight(),0);
        edgeProcessor.clearAllMetaEdges();

        n1.setEnabled(false);
        n3.setEnabled(true);

        edgeProcessor.computeMetaEdges(graphStructure);

        actual = graph.getMetaEdges().toArray();
        assertEquals(1, actual.length);
        MetaEdgeImpl metaEdge43 = (MetaEdgeImpl)actual[0];
        assertSame(n4, metaEdge43.getSource());
        assertSame(n3, metaEdge43.getTarget());
        edgeProcessor.clearAllMetaEdges();

        n4.setEnabled(false);
        n5.setEnabled(true);

        edgeProcessor.computeMetaEdges(graphStructure);

        actual = graph.getMetaEdges().toArray();
        assertEquals(0, actual.length);

        treeStructure.showTreeAsTable();*/
    }

    @Test
    public void testFilterFlatHierarchy() {
        Dhns dhns = new Dhns(new DhnsGraphController());
        GraphStructure graphStructure = dhns.getGraphStructure();
        TreeStructure treeStructure = graphStructure.getStructure();
        GraphFactoryImpl factoryImpl = new GraphFactoryImpl(new IDGen(), null);
        EdgeProcessor edgeProcessor = new EdgeProcessor(dhns);

        AbstractNode n1 = factoryImpl.newNode();
        AbstractNode n2 = factoryImpl.newNode();
        AbstractNode n3 = factoryImpl.newNode();
        AbstractNode n4 = factoryImpl.newNode();
        AbstractNode n5 = factoryImpl.newNode();
        AbstractNode n6 = factoryImpl.newNode();

        treeStructure.insertAsChild(n1, treeStructure.getRoot());
        treeStructure.insertAsChild(n4, treeStructure.getRoot());
        treeStructure.insertAsChild(n2, n1);
        treeStructure.insertAsChild(n3, n1);
        treeStructure.insertAsChild(n5, n4);
        treeStructure.insertAsChild(n6, n4);

        AbstractEdge e23 = factoryImpl.newEdge(n2, n3);
        AbstractEdge e25 = factoryImpl.newEdge(n2, n5);
        AbstractEdge e52 = factoryImpl.newEdge(n5, n2);
        AbstractEdge e53 = factoryImpl.newEdge(n5, n3);
        AbstractEdge e65 = factoryImpl.newEdge(n6, n5);

        n2.getEdgesOutTree().add(e23);
        n3.getEdgesInTree().add(e23);
        n2.getEdgesOutTree().add(e25);
        n5.getEdgesInTree().add(e25);
        n5.getEdgesOutTree().add(e52);
        n2.getEdgesInTree().add(e52);
        n5.getEdgesOutTree().add(e53);
        n3.getEdgesInTree().add(e53);
        n6.getEdgesOutTree().add(e65);
        n5.getEdgesInTree().add(e65);

        n4.setEnabled(true);
        n2.setEnabled(true);

        //SubGraphManager.filterFlatHierarchy(graphStructure);
    }

    @Test
    public void testMetaEdges() {
        Dhns dhns = new Dhns(new DhnsGraphController());
        GraphStructure graphStructure = dhns.getGraphStructure();
        HierarchicalGraph graph = dhns.getHierarchicalDirectedGraph();
        TreeStructure treeStructure = graphStructure.getStructure();
        GraphFactoryImpl factoryImpl = dhns.factory();

        AbstractNode na = factoryImpl.newNode();
        na.getNodeData().setLabel("na");
        AbstractNode nb = factoryImpl.newNode();
        nb.getNodeData().setLabel("nb");
        AbstractNode nc = factoryImpl.newNode();
        nc.getNodeData().setLabel("nc");
        AbstractNode nd = factoryImpl.newNode();
        nd.getNodeData().setLabel("nd");
        AbstractNode ne = factoryImpl.newNode();
        ne.getNodeData().setLabel("ne");
        AbstractNode nf = factoryImpl.newNode();
        nf.getNodeData().setLabel("nf");
        AbstractNode ng = factoryImpl.newNode();
        ng.getNodeData().setLabel("ng");

        treeStructure.insertAsChild(na, treeStructure.getRoot());
        treeStructure.insertAsChild(nb, na);
        treeStructure.insertAsChild(ne, na);
        treeStructure.insertAsChild(nc, nb);
        treeStructure.insertAsChild(nd, nb);
        treeStructure.insertAsChild(nf, ne);
        treeStructure.insertAsChild(ng, ne);

        nb.setEnabled(true);
        ne.setEnabled(true);

        AbstractEdge ebe = factoryImpl.newEdge(nb, ne);
        AbstractEdge ecd = factoryImpl.newEdge(nc, nd);
        AbstractEdge egb = factoryImpl.newEdge(ng, nb);
        AbstractEdge efa = factoryImpl.newEdge(nf, na);

        graph.addEdge(ebe);
        graph.addEdge(ecd);
        graph.addEdge(egb);
        graph.addEdge(efa);

        treeStructure.showTreeAsTable();
        Edge[] actual = graph.getMetaEdges().toArray();
        for (int i = 0; i < actual.length; i++) {
            System.out.println(actual[i].getSource().getNodeData().getLabel()+"->"+actual[i].getTarget().getNodeData().getLabel());
        }

        graph = dhns.getHierarchicalDirectedGraphVisible();
        ((ViewImpl)graph.getView()).checkUpdate();
        actual = graph.getMetaEdges().toArray();
        for (int i = 0; i < actual.length; i++) {
            System.out.println(actual[i].getSource().getNodeData().getLabel()+"->"+actual[i].getTarget().getNodeData().getLabel());
        }
    }
}