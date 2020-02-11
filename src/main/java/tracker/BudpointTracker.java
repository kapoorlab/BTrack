package tracker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import budDetector.Budpointobject;
import net.imglib2.algorithm.OutputAlgorithm;

public interface BudpointTracker extends OutputAlgorithm< SimpleWeightedGraph< Budpointobject, DefaultWeightedEdge >> {
	
	
		
		

}