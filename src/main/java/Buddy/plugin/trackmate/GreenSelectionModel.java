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

import greenDetector.Greenobject;


/**
 * A component of {@link Model} that handles Greenobject and edges selection.
 * 
 * @author Jean-Yves Tinevez
 */
public class GreenSelectionModel {

	private static final boolean DEBUG = false;

	/** The Greenobject current selection. */
	private Set<Greenobject> GreenobjectSelection = new HashSet<>();
	/** The edge current selection. */
	private Set<DefaultWeightedEdge> edgeSelection = new HashSet<>();
	/** The list of listener listening to change in selection. */
	private List<GreenSelectionChangeListener> selectionChangeListeners = new ArrayList<>();

	private final GreenModel model;

	/*
	 * DEFAULT VISIBILITY CONSTRUCTOR
	 */

	public GreenSelectionModel(GreenModel parent) {
		this.model = parent;
	}

	/*
	 * DEAL WITH SELECTION CHANGE LISTENER
	 */

	public boolean addSelectionChangeListener(GreenSelectionChangeListener listener) {
		return selectionChangeListeners.add(listener);
	}

	public boolean removeSelectionChangeListener(GreenSelectionChangeListener listener) {
		return selectionChangeListeners.remove(listener);
	}

	public List<GreenSelectionChangeListener> getSelectionChangeListener() {
		return selectionChangeListeners;
	}

	/*
	 * SELECTION CHANGES
	 */

	public void clearSelection() {
		if (DEBUG)
			System.out.println("[SelectionModel] Clearing selection");
		// Prepare event
		Map<Greenobject, Boolean> GreenobjectMap = new HashMap<>(GreenobjectSelection.size());
		for (Greenobject Greenobject : GreenobjectSelection)
			GreenobjectMap.put(Greenobject, false);
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(edgeSelection.size());
		for (DefaultWeightedEdge edge : edgeSelection)
			edgeMap.put(edge, false);
		GreenSelectionChangeEvent event = new GreenSelectionChangeEvent(this, GreenobjectMap, edgeMap);
		// Clear fields
		clearGreenobjectSelection();
		clearEdgeSelection();
		// Fire event
		for (GreenSelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void clearGreenobjectSelection() {
		if (DEBUG)
			System.out.println("[SelectionModel] Clearing Greenobject selection");
		// Prepare event
		Map<Greenobject, Boolean> GreenobjectMap = new HashMap<>(GreenobjectSelection.size());
		for (Greenobject Greenobject : GreenobjectSelection)
			GreenobjectMap.put(Greenobject, false);
		GreenSelectionChangeEvent event = new GreenSelectionChangeEvent(this, GreenobjectMap, null);
		// Clear field
		GreenobjectSelection.clear();
		// Fire event
		for (GreenSelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void clearEdgeSelection() {
		if (DEBUG)
			System.out.println("[SelectionModel] Clearing edge selection");
		// Prepare event
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(edgeSelection.size());
		for (DefaultWeightedEdge edge : edgeSelection)
			edgeMap.put(edge, false);
		GreenSelectionChangeEvent event = new GreenSelectionChangeEvent(this, null, edgeMap);
		// Clear field
		edgeSelection.clear();
		// Fire event
		for (GreenSelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void addGreenobjectToSelection(final Greenobject Greenobject) {
		if (!GreenobjectSelection.add(Greenobject))
			return; // Do nothing if already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Adding Greenobject " + Greenobject + " to selection");
		Map<Greenobject, Boolean> GreenobjectMap = new HashMap<>(1);
		GreenobjectMap.put(Greenobject, true);
		if (DEBUG)
			System.out.println("[SelectionModel] Seding event to listeners: " + selectionChangeListeners);
		GreenSelectionChangeEvent event = new GreenSelectionChangeEvent(this, GreenobjectMap, null);
		for (GreenSelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void removeGreenobjectFromSelection(final Greenobject Greenobject) {
		if (!GreenobjectSelection.remove(Greenobject))
			return; // Do nothing was not already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Removing Greenobject " + Greenobject + " from selection");
		Map<Greenobject, Boolean> GreenobjectMap = new HashMap<>(1);
		GreenobjectMap.put(Greenobject, false);
		GreenSelectionChangeEvent event = new GreenSelectionChangeEvent(this, GreenobjectMap, null);
		for (GreenSelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void addGreenobjectToSelection(final Collection<Greenobject> Greenobjects) {
		Map<Greenobject, Boolean> GreenobjectMap = new HashMap<>(Greenobjects.size());
		for (Greenobject Greenobject : Greenobjects) {
			if (GreenobjectSelection.add(Greenobject)) {
				GreenobjectMap.put(Greenobject, true);
				if (DEBUG)
					System.out.println("[SelectionModel] Adding Greenobject " + Greenobject + " to selection");
			}
		}
		GreenSelectionChangeEvent event = new GreenSelectionChangeEvent(this, GreenobjectMap, null);
		if (DEBUG)
			System.out.println("[SelectionModel] Seding event " + event.hashCode() + " to "
					+ selectionChangeListeners.size() + " listeners: " + selectionChangeListeners);
		for (GreenSelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void removeGreenobjectFromSelection(final Collection<Greenobject> Greenobjects) {
		Map<Greenobject, Boolean> GreenobjectMap = new HashMap<>(Greenobjects.size());
		for (Greenobject Greenobject : Greenobjects) {
			if (GreenobjectSelection.remove(Greenobject)) {
				GreenobjectMap.put(Greenobject, false);
				if (DEBUG)
					System.out.println("[SelectionModel] Removing Greenobject " + Greenobject + " from selection");
			}
		}
		GreenSelectionChangeEvent event = new GreenSelectionChangeEvent(this, GreenobjectMap, null);
		for (GreenSelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void addEdgeToSelection(final DefaultWeightedEdge edge) {
		if (!edgeSelection.add(edge))
			return; // Do nothing if already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Adding edge " + edge + " to selection");
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(1);
		edgeMap.put(edge, true);
		GreenSelectionChangeEvent event = new GreenSelectionChangeEvent(this, null, edgeMap);
		for (GreenSelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);

	}

	public void removeEdgeFromSelection(final DefaultWeightedEdge edge) {
		if (!edgeSelection.remove(edge))
			return; // Do nothing if already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Removing edge " + edge + " from selection");
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(1);
		edgeMap.put(edge, false);
		GreenSelectionChangeEvent event = new GreenSelectionChangeEvent(this, null, edgeMap);
		for (GreenSelectionChangeListener listener : selectionChangeListeners)
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
		GreenSelectionChangeEvent event = new GreenSelectionChangeEvent(this, null, edgeMap);
		for (GreenSelectionChangeListener listener : selectionChangeListeners)
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
		GreenSelectionChangeEvent event = new GreenSelectionChangeEvent(this, null, edgeMap);
		for (GreenSelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public Set<Greenobject> getGreenobjectSelection() {
		return GreenobjectSelection;
	}

	public Set<DefaultWeightedEdge> getEdgeSelection() {
		return edgeSelection;
	}

	/*
	 * SPECIAL METHODS
	 */

	/**
	 * Search and add all Greenobjects and links belonging to the same track(s) that
	 * of given <code>Greenobjects</code> and <code>edges</code> to current
	 * selection. A <code>direction</code> parameter allow specifying whether we
	 * should include only parts upwards in time, downwards in time or all the way
	 * through.
	 * 
	 * @param Greenobjects
	 *            the Greenobjects to include in search
	 * @param edges
	 *            the edges to include in search
	 * @param direction
	 *            the direction to go when searching. Positive integers will result
	 *            in searching upwards in time, negative integers downwards in time
	 *            and 0 all the way through.
	 */
	public void selectTrack(final Collection<Greenobject> Greenobjects, final Collection<DefaultWeightedEdge> edges,
			final int direction) {

		HashSet<Greenobject> inspectionGreenobjects = new HashSet<>(Greenobjects);

		for (DefaultWeightedEdge edge : edges) {
			// We add connected Greenobjects to the list of Greenobjects to inspect
			inspectionGreenobjects.add(model.getTrackModel().getEdgeSource(edge));
			inspectionGreenobjects.add(model.getTrackModel().getEdgeTarget(edge));
		}

		// Walk across tracks to build selection
		final HashSet<Greenobject> lGreenobjectSelection = new HashSet<>();
		final HashSet<DefaultWeightedEdge> lEdgeSelection = new HashSet<>();

		if (direction == 0) { // Unconditionally
			for (Greenobject Greenobject : inspectionGreenobjects) {
				lGreenobjectSelection.add(Greenobject);
				GraphIterator<Greenobject, DefaultWeightedEdge> walker = model.getTrackModel()
						.getDepthFirstIterator(Greenobject, false);
				while (walker.hasNext()) {
					Greenobject target = walker.next();
					lGreenobjectSelection.add(target);
					// Deal with edges
					Set<DefaultWeightedEdge> targetEdges = model.getTrackModel().edgesOf(target);
					for (DefaultWeightedEdge targetEdge : targetEdges) {
						lEdgeSelection.add(targetEdge);
					}
				}
			}

		} else { // Only upward or backward in time
			for (Greenobject Greenobject : inspectionGreenobjects) {
				lGreenobjectSelection.add(Greenobject);

				// A bit more complicated: we want to walk in only one direction,
				// when branching is occurring, we do not want to get back in time.
				Stack<Greenobject> stack = new Stack<>();
				stack.add(Greenobject);
				while (!stack.isEmpty()) {
					Greenobject inspected = stack.pop();
					Set<DefaultWeightedEdge> targetEdges = model.getTrackModel().edgesOf(inspected);
					for (DefaultWeightedEdge targetEdge : targetEdges) {
						Greenobject other;
						if (direction > 0) {
							// Upward in time: we just have to search through edges using their source
							// Greenobjects
							other = model.getTrackModel().getEdgeSource(targetEdge);
						} else {
							other = model.getTrackModel().getEdgeTarget(targetEdge);
						}

						if (other != inspected) {
							lGreenobjectSelection.add(other);
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
			Greenobject source = model.getTrackModel().getEdgeSource(edge);
			Greenobject target = model.getTrackModel().getEdgeTarget(edge);
			if (!(lGreenobjectSelection.contains(source) && lGreenobjectSelection.contains(target))) {
				edgesToRemove.add(edge);
			}
		}
		lEdgeSelection.removeAll(edgesToRemove);

		// Set selection
		addGreenobjectToSelection(lGreenobjectSelection);
		addEdgeToSelection(lEdgeSelection);
	}

}
