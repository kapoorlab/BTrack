package tracker;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import greenDetector.Greenobject;




public class GREENTimeDirectedDepthFirstIterator extends GREENSortedDepthFirstIterator<Greenobject, DefaultWeightedEdge> {

	public GREENTimeDirectedDepthFirstIterator(Graph<Greenobject, DefaultWeightedEdge> g, Greenobject startVertex) {
		super(g, startVertex, null);
	}
	
	
	
    protected void addUnseenChildrenOf(Greenobject vertex) {
    	
    	int ts = vertex.getFeature(Greenobject.TIME).intValue();
        for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(edge));
            }

            Greenobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            int tt = oppositeV.getFeature(Greenobject.TIME).intValue();
            if (tt <= ts) {
            	continue;
            }

            if ( seen.containsKey(oppositeV)) {
                encounterVertexAgain(oppositeV, edge);
            } else {
                encounterVertex(oppositeV, edge);
            }
        }
    }

	
	
}
