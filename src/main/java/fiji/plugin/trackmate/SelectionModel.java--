package Buddy.plugin.trackmate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.GraphIterator;

import budDetector.Spot;

/**
 * A component of {@link Model} that handles Spot and edges selection.
 * 
 * @author Jean-Yves Tinevez
 */
public class SelectionModel {

	private static final boolean DEBUG = false;

	/** The Spot current selection. */
	private Set<Spot> SpotSelection = new HashSet<>();
	/** The edge current selection. */
	private Set<DefaultWeightedEdge> edgeSelection = new HashSet<>();
	/** The list of listener listening to change in selection. */
	private List<SelectionChangeListener> selectionChangeListeners = new ArrayList<>();

	private final Model model;

	/*
	 * DEFAULT VISIBILITY CONSTRUCTOR
	 */

	public SelectionModel(Model parent) {
		this.model = parent;
	}

	/*
	 * DEAL WITH SELECTION CHANGE LISTENER
	 */

	public boolean addSelectionChangeListener(SelectionChangeListener listener) {
		return selectionChangeListeners.add(listener);
	}

	public boolean removeSelectionChangeListener(SelectionChangeListener listener) {
		return selectionChangeListeners.remove(listener);
	}

	public List<SelectionChangeListener> getSelectionChangeListener() {
		return selectionChangeListeners;
	}

	/*
	 * SELECTION CHANGES
	 */

	public void clearSelection() {
		if (DEBUG)
			System.out.println("[SelectionModel] Clearing selection");
		// Prepare event
		Map<Spot, Boolean> SpotMap = new HashMap<>(SpotSelection.size());
		for (Spot Spot : SpotSelection)
			SpotMap.put(Spot, false);
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(edgeSelection.size());
		for (DefaultWeightedEdge edge : edgeSelection)
			edgeMap.put(edge, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, SpotMap, edgeMap);
		// Clear fields
		clearSpotSelection();
		clearEdgeSelection();
		// Fire event
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void clearSpotSelection() {
		if (DEBUG)
			System.out.println("[SelectionModel] Clearing Spot selection");
		// Prepare event
		Map<Spot, Boolean> SpotMap = new HashMap<>(SpotSelection.size());
		for (Spot Spot : SpotSelection)
			SpotMap.put(Spot, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, SpotMap, null);
		// Clear field
		SpotSelection.clear();
		// Fire event
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void clearEdgeSelection() {
		if (DEBUG)
			System.out.println("[SelectionModel] Clearing edge selection");
		// Prepare event
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(edgeSelection.size());
		for (DefaultWeightedEdge edge : edgeSelection)
			edgeMap.put(edge, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		// Clear field
		edgeSelection.clear();
		// Fire event
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void addSpotToSelection(final Spot Spot) {
		if (!SpotSelection.add(Spot))
			return; // Do nothing if already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Adding Spot " + Spot + " to selection");
		Map<Spot, Boolean> SpotMap = new HashMap<>(1);
		SpotMap.put(Spot, true);
		if (DEBUG)
			System.out.println("[SelectionModel] Seding event to listeners: " + selectionChangeListeners);
		SelectionChangeEvent event = new SelectionChangeEvent(this, SpotMap, null);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void removeSpotFromSelection(final Spot Spot) {
		if (!SpotSelection.remove(Spot))
			return; // Do nothing was not already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Removing Spot " + Spot + " from selection");
		Map<Spot, Boolean> SpotMap = new HashMap<>(1);
		SpotMap.put(Spot, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, SpotMap, null);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void addSpotToSelection(final Collection<Spot> Spots) {
		Map<Spot, Boolean> SpotMap = new HashMap<>(Spots.size());
		for (Spot Spot : Spots) {
			if (SpotSelection.add(Spot)) {
				SpotMap.put(Spot, true);
				if (DEBUG)
					System.out.println("[SelectionModel] Adding Spot " + Spot + " to selection");
			}
		}
		SelectionChangeEvent event = new SelectionChangeEvent(this, SpotMap, null);
		if (DEBUG)
			System.out.println("[SelectionModel] Seding event " + event.hashCode() + " to "
					+ selectionChangeListeners.size() + " listeners: " + selectionChangeListeners);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void removeSpotFromSelection(final Collection<Spot> Spots) {
		Map<Spot, Boolean> SpotMap = new HashMap<>(Spots.size());
		for (Spot Spot : Spots) {
			if (SpotSelection.remove(Spot)) {
				SpotMap.put(Spot, false);
				if (DEBUG)
					System.out.println("[SelectionModel] Removing Spot " + Spot + " from selection");
			}
		}
		SelectionChangeEvent event = new SelectionChangeEvent(this, SpotMap, null);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void addEdgeToSelection(final DefaultWeightedEdge edge) {
		if (!edgeSelection.add(edge))
			return; // Do nothing if already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Adding edge " + edge + " to selection");
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(1);
		edgeMap.put(edge, true);
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);

	}

	public void removeEdgeFromSelection(final DefaultWeightedEdge edge) {
		if (!edgeSelection.remove(edge))
			return; // Do nothing if already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Removing edge " + edge + " from selection");
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(1);
		edgeMap.put(edge, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);

	}

	public void addEdgeToSelection(final Collection<DefaultWeightedEdge> edges) {
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(edges.size());
		for (DefaultWeightedEdge edge : edges) {
			if (edgeSelection.add(edge)) {
				edgeMap.put(edge, true);
				if (DEBUG)
					System.out.println("[SelectionModel] Adding edge " + edge + " to selection");
			}
		}
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void removeEdgeFromSelection(final Collection<DefaultWeightedEdge> edges) {
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(edges.size());
		for (DefaultWeightedEdge edge : edges) {
			if (edgeSelection.remove(edge)) {
				edgeMap.put(edge, false);
				if (DEBUG)
					System.out.println("[SelectionModel] Removing edge " + edge + " from selection");
			}
		}
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public Set<Spot> getSpotSelection() {
		return SpotSelection;
	}

	public Set<DefaultWeightedEdge> getEdgeSelection() {
		return edgeSelection;
	}

	/*
	 * SPECIAL METHODS
	 */

	/**
	 * Search and add all Spots and links belonging to the same track(s) that
	 * of given <code>Spots</code> and <code>edges</code> to current
	 * selection. A <code>direction</code> parameter allow specifying whether we
	 * should include only parts upwards in time, downwards in time or all the way
	 * through.
	 * 
	 * @param Spots
	 *            the Spots to include in search
	 * @param edges
	 *            the edges to include in search
	 * @param direction
	 *            the direction to go when searching. Positive integers will result
	 *            in searching upwards in time, negative integers downwards in time
	 *            and 0 all the way through.
	 */
	public void selectTrack(final Collection<Spot> Spots, final Collection<DefaultWeightedEdge> edges,
			final int direction) {

		HashSet<Spot> inspectionSpots = new HashSet<>(Spots);

		for (DefaultWeightedEdge edge : edges) {
			// We add connected Spots to the list of Spots to inspect
			inspectionSpots.add(model.getTrackModel().getEdgeSource(edge));
			inspectionSpots.add(model.getTrackModel().getEdgeTarget(edge));
		}

		// Walk across tracks to build selection
		final HashSet<Spot> lSpotSelection = new HashSet<>();
		final HashSet<DefaultWeightedEdge> lEdgeSelection = new HashSet<>();

		if (direction == 0) { // Unconditionally
			for (Spot Spot : inspectionSpots) {
				lSpotSelection.add(Spot);
				GraphIterator<Spot, DefaultWeightedEdge> walker = model.getTrackModel()
						.getDepthFirstIterator(Spot, false);
				while (walker.hasNext()) {
					Spot target = walker.next();
					lSpotSelection.add(target);
					// Deal with edges
					Set<DefaultWeightedEdge> targetEdges = model.getTrackModel().edgesOf(target);
					for (DefaultWeightedEdge targetEdge : targetEdges) {
						lEdgeSelection.add(targetEdge);
					}
				}
			}

		} else { // Only upward or backward in time
			for (Spot Spot : inspectionSpots) {
				lSpotSelection.add(Spot);

				// A bit more complicated: we want to walk in only one direction,
				// when branching is occurring, we do not want to get back in time.
				Stack<Spot> stack = new Stack<>();
				stack.add(Spot);
				while (!stack.isEmpty()) {
					Spot inspected = stack.pop();
					Set<DefaultWeightedEdge> targetEdges = model.getTrackModel().edgesOf(inspected);
					for (DefaultWeightedEdge targetEdge : targetEdges) {
						Spot other;
						if (direction > 0) {
							// Upward in time: we just have to search through edges using their source
							// Spots
							other = model.getTrackModel().getEdgeSource(targetEdge);
						} else {
							other = model.getTrackModel().getEdgeTarget(targetEdge);
						}

						if (other != inspected) {
							lSpotSelection.add(other);
							lEdgeSelection.add(targetEdge);
							stack.add(other);
						}
					}
				}
			}
		}

		// Cut "tail": remove the first an last edges in time, so that the selection
		// only has conencted
		// edges in it.
		ArrayList<DefaultWeightedEdge> edgesToRemove = new ArrayList<>();
		for (DefaultWeightedEdge edge : lEdgeSelection) {
			Spot source = model.getTrackModel().getEdgeSource(edge);
			Spot target = model.getTrackModel().getEdgeTarget(edge);
			if (!(lSpotSelection.contains(source) && lSpotSelection.contains(target))) {
				edgesToRemove.add(edge);
			}
		}
		lEdgeSelection.removeAll(edgesToRemove);

		// Set selection
		addSpotToSelection(lSpotSelection);
		addEdgeToSelection(lEdgeSelection);
	}

}
