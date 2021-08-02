/**
 * 
 */
package fiji.plugin.btrackmate.graph;

import fiji.plugin.btrackmate.Spot;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

public class TimeDirectedDepthFirstIterator extends SortedDepthFirstIterator<Spot, DefaultWeightedEdge> {

	public TimeDirectedDepthFirstIterator(Graph<Spot, DefaultWeightedEdge> g, Spot startVertex) {
		super(g, startVertex, null);
	}

	@Override
	protected void addUnseenChildrenOf(Spot vertex) {

		int ts = vertex.getFeature(Spot.FRAME).intValue();
		for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
			if (nListeners != 0) {
				fireEdgeTraversed(createEdgeTraversalEvent(edge));
			}

			Spot oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
			int tt = oppositeV.getFeature(Spot.FRAME).intValue();
			if (tt <= ts) {
				continue;
			}

			if (seen.containsKey(oppositeV)) {
				encounterVertexAgain(oppositeV, edge);
			} else {
				encounterVertex(oppositeV, edge);
			}
		}
	}

}
