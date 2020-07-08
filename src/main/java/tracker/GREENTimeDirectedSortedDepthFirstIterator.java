package tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import greenDetector.Greenobject;






public class GREENTimeDirectedSortedDepthFirstIterator extends GREENSortedDepthFirstIterator<Greenobject, DefaultWeightedEdge> {

	public GREENTimeDirectedSortedDepthFirstIterator(final Graph<Greenobject, DefaultWeightedEdge> g, final Greenobject startVertex, final Comparator<Greenobject> comparator) {
		super(g, startVertex, comparator);
	}



    @Override
	protected void addUnseenChildrenOf(final Greenobject vertex) {

		// Retrieve target vertices, and sort them in a list
		final List< Greenobject > sortedChildren = new ArrayList< Greenobject >();
    	// Keep a map of matching edges so that we can retrieve them in the same order
    	final Map<Greenobject, DefaultWeightedEdge> localEdges = new HashMap<Greenobject, DefaultWeightedEdge>();

    	final int ts = vertex.getFeature(Greenobject.TIME).intValue();
        for (final DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {

        	final Greenobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
        	final int tt = oppositeV.getFeature(Greenobject.TIME).intValue();
        	if (tt <= ts) {
        		continue;
        	}

        	if (!seen.containsKey(oppositeV)) {
        		sortedChildren.add(oppositeV);
        	}
        	localEdges.put(oppositeV, edge);
        }

		Collections.sort( sortedChildren, Collections.reverseOrder( comparator ) );
		final Iterator< Greenobject > it = sortedChildren.iterator();
        while (it.hasNext()) {
			final Greenobject child = it.next();

            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(localEdges.get(child)));
            }

            if (seen.containsKey(child)) {
                encounterVertexAgain(child, localEdges.get(child));
            } else {
                encounterVertex(child, localEdges.get(child));
            }
        }
    }



}
