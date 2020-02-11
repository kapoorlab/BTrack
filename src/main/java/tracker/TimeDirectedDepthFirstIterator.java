package tracker;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import budDetector.Budpointobject;




public class TimeDirectedDepthFirstIterator extends SortedDepthFirstIterator<Budpointobject, DefaultWeightedEdge> {

	public TimeDirectedDepthFirstIterator(Graph<Budpointobject, DefaultWeightedEdge> g, Budpointobject startVertex) {
		super(g, startVertex, null);
	}
	
	
	
    protected void addUnseenChildrenOf(Budpointobject vertex) {
    	
    	int ts = vertex.getFeature(Budpointobject.TIME).intValue();
        for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(edge));
            }

            Budpointobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            int tt = oppositeV.getFeature(Budpointobject.TIME).intValue();
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
