package tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import budDetector.Budobject;
import budDetector.Budpointobject;
import kalmanGUI.CovistoKalmanPanel;
import pluginTools.InteractiveBud;

public class BUDDYTrackingFunctions {

	
	final InteractiveBud parent;
	
	public BUDDYTrackingFunctions(final InteractiveBud parent) {
		
		this.parent = parent;
		
	}
	
	
	
public SimpleWeightedGraph<Budobject, DefaultWeightedEdge> BudTrackfunction() {
		
		parent.BudUserchosenCostFunction = new ForBudTrackCostFunction();
		

		ArrayList<ArrayList<Budobject>> colllist = new ArrayList<ArrayList<Budobject>>();
	
		
		for (Map.Entry<String, ArrayList<Budobject>> entry : parent.AllBuds.entrySet()) {

			ArrayList<Budobject> bloblist = entry.getValue();
			if(bloblist.size() > 0) {
			colllist.add(bloblist);
			
			
			
			}
			
			
		

		}

		for(ArrayList<Budobject> point:colllist) {
			
			for(Budobject curr: point) {
				
				
				System.out.println(" Bud Point" +curr.t + " " + curr.Budcenter.getDoublePosition(0) + " " + curr.Budcenter.getDoublePosition(1) );
				
			}
			
			
		}
		ForBudKFsearch Tsearch = new ForBudKFsearch(colllist, parent.BudUserchosenCostFunction,  CovistoKalmanPanel.initialSearchradius/parent.calibration  ,
				CovistoKalmanPanel.initialSearchradius/parent.calibration , 
				CovistoKalmanPanel.maxframegap, parent.AccountedT, parent.jpb);
		Tsearch.process();
		SimpleWeightedGraph<Budobject, DefaultWeightedEdge> simplegraph = Tsearch.getResult();

		return simplegraph;
		
		
	}
	public SimpleWeightedGraph<Budpointobject, DefaultWeightedEdge> Trackfunction() {
		
		parent.UserchosenCostFunction = new BudPointTrackCostFunction();
		

		ArrayList<ArrayList<Budpointobject>> colllist = new ArrayList<ArrayList<Budpointobject>>();
	
		
		for (Map.Entry<String, ArrayList<Budpointobject>> entry : parent.AllBudpoints.entrySet()) {

			ArrayList<Budpointobject> bloblist = entry.getValue();
			System.out.println(entry.getKey() + " " + entry.getValue().size() + "Hash map");
			if(bloblist.size() > 0) {
			colllist.add(bloblist);
			}
			
			

		}

		
		BUDDYKFsearch Tsearch = new BUDDYKFsearch(colllist, parent.UserchosenCostFunction,   0.5 * CovistoKalmanPanel.initialSearchradius/parent.calibration ,
				CovistoKalmanPanel.initialSearchradius/parent.calibration, 
				CovistoKalmanPanel.maxframegap, parent.AccountedT, parent.jpb);
		Tsearch.process();
		SimpleWeightedGraph<Budpointobject, DefaultWeightedEdge> simplegraph = Tsearch.getResult();

		return simplegraph;
		
		
	}
	
	/**
	 * Sort Z or T hashmap by comparing the order in Z or T
	 * 
	 * @param map
	 * @return
	 */
	public static HashMap<String, Integer> sortByValues(HashMap<String, Integer> map) {
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap<String, Integer> sortedHashMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

	
	public static HashMap<String, ArrayList<Budpointobject>> sortByIntegerInter(HashMap<String, ArrayList<Budpointobject>> map) {
		List<Entry<String, ArrayList<Budpointobject>>> list = new LinkedList<Entry<String, ArrayList<Budpointobject>>>(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<String, ArrayList<Budpointobject>>>() {

			@Override
			public int compare(Entry<String, ArrayList<Budpointobject>> o1, Entry<String, ArrayList<Budpointobject>> o2) {
				
				return Integer.parseInt(o1.getKey()) - Integer.parseInt(o2.getKey());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap<String, ArrayList<Budpointobject>> sortedHashMap = new LinkedHashMap<String, ArrayList<Budpointobject>>();
		for (Iterator<Entry<String, ArrayList<Budpointobject>>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, ArrayList<Budpointobject>> entry = (Map.Entry<String, ArrayList<Budpointobject>>) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}
	
}
	
	