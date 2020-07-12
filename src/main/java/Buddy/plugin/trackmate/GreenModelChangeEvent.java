package Buddy.plugin.trackmate;

import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import greenDetector.Greenobject;

import org.jgrapht.graph.DefaultWeightedEdge;

public class GreenModelChangeEvent extends EventObject {

	private static final long serialVersionUID = -1L;
	/** Indicate that a Greenobject was added to the model. */
	public static final int FLAG_Greenobject_ADDED = 0;
	
	/** Indicate that a Greenobject was removed from the model. */
	public static final int FLAG_Greenobject_REMOVED = 1;
	/**
	 * Indicate a modification of the features of a Greenobject. It may have changed
	 * of position and feature, but not of frame.
	 */
	public static final int FLAG_Greenobject_MODIFIED = 2;
	/**
	 * Indicate that a Greenobject has changed of frame, and possible of position,
	 * features, etc.. .
	 */
	public static final int FLAG_Greenobject_FRAME_CHANGED = 3;
	/** Indicate that an edge was added to the model. */
	public static final int FLAG_EDGE_ADDED = 4;
	/** Indicate that an edge was removed from the model. */
	public static final int FLAG_EDGE_REMOVED = 5;
	/**
	 * Indicate that an edge has been modified. Edge modifications occur when the
	 * target or source Greenobject are modified, or when the weight of the edge has
	 * been modified.
	 */
	public static final int FLAG_EDGE_MODIFIED = 6;

	public static final Map<Integer, String> flagsToString = new HashMap<>(7);
	static {
		flagsToString.put(FLAG_Greenobject_ADDED, "Greenobject added");
		flagsToString.put(FLAG_Greenobject_FRAME_CHANGED, "Greenobject frame changed");
		flagsToString.put(FLAG_Greenobject_MODIFIED, "Greenobject modified");
		flagsToString.put(FLAG_Greenobject_REMOVED, "Greenobject removed");
		flagsToString.put(FLAG_EDGE_ADDED, "Edge added");
		flagsToString.put(FLAG_EDGE_MODIFIED, "Edge modified");
		flagsToString.put(FLAG_EDGE_REMOVED, "Edge removed");
	}

	/**
	 * Event type indicating that the Greenobject of the model were computed, and
	 * are now accessible through {@link Model#getGreenobject()}.
	 */
	public static final int Greenobject_COMPUTED = 4;
	/**
	 * Event type indicating that the Greenobject of the model were filtered.
	 */
	public static final int Greenobject_FILTERED = 5;
	/**
	 * Event type indicating that the tracks of the model were computed.
	 */
	public static final int TRACKS_COMPUTED = 6;
	/**
	 * Event type indicating that the tracks of the model had their visibility
	 * changed.
	 */
	public static final int TRACKS_VISIBILITY_CHANGED = 7;

	/**
	 * Event type indicating that model was modified, by adding, removing or
	 * changing the feature of some Greenobject, and/or adding or removing edges in
	 * the tracks. Content of the modification can be accessed by
	 * {@link #getGreenobject()}, {@link #getGreenobjectFlag(Greenobject)},
	 * {@link #getFromFrame(Greenobject)} and {@link #getToFrame(Greenobject)}, and
	 * for the tracks: {@link #getEdges()} and
	 * {@link #getEdgeFlag(DefaultWeightedEdge)} .
	 */
	public static final int MODEL_MODIFIED = 8;

	/** Greenobject affected by this event. */
	private final HashSet<Greenobject> Greenobject = new HashSet<>();
	/** Edges affected by this event. */
	private final HashSet<DefaultWeightedEdge> edges = new HashSet<>();
	/**
	 * For Greenobject removed or moved: frame from which they were removed or
	 * moved.
	 */
	private final HashMap<Greenobject, Integer> fromFrame = new HashMap<>();
	/**
	 * For Greenobject removed or added: frame to which they were added or moved.
	 */
	private final HashMap<Greenobject, Integer> toFrame = new HashMap<>();
	/** Modification flag for Greenobject affected by this event. */
	private final HashMap<Greenobject, Integer> GreenobjectFlags = new HashMap<>();
	/** Modification flag for edges affected by this event. */
	private final HashMap<DefaultWeightedEdge, Integer> edgeFlags = new HashMap<>();
	/** The event type for this instance. */
	private final int eventID;
	private Set<Integer> trackUpdated;

	/**
	 * Create a new event, reflecting a change in a {@link Model}.
	 *
	 * @param source
	 *            the object source of this event.
	 * @param eventID
	 *            the evend ID to use for this event.
	 */
	public GreenModelChangeEvent(final Object source, final int eventID) {
		super(source);
		this.eventID = eventID;
	}

	public int getEventID() {
		return this.eventID;
	}

	public boolean addAllGreenobject(final Collection<Greenobject> lGreenobject) {
		return this.Greenobject.addAll(lGreenobject);
	}

	public boolean addGreenobject(final Greenobject Greenobject) {
		return this.Greenobject.add(Greenobject);
	}

	public boolean addAllEdges(final Collection<DefaultWeightedEdge> lEdges) {
		return this.edges.addAll(lEdges);
	}

	public boolean addEdge(final DefaultWeightedEdge edge) {
		return edges.add(edge);
	}

	public Integer putEdgeFlag(final DefaultWeightedEdge edge, final Integer flag) {
		return edgeFlags.put(edge, flag);
	}

	public Integer putGreenobjectFlag(final Greenobject Greenobject, final Integer flag) {
		return GreenobjectFlags.put(Greenobject, flag);
	}

	public Integer putFromFrame(final Greenobject Greenobject, final Integer lFromFrame) {
		return this.fromFrame.put(Greenobject, lFromFrame);
	}

	public Integer putToFrame(final Greenobject Greenobject, final Integer lToFrame) {
		return this.toFrame.put(Greenobject, lToFrame);
	}

	/**
	 * @return the set of Greenobject that are affected by this event. Is empty if
	 *         no Greenobject is affected by this event.
	 */
	public Set<Greenobject> getGreenobject() {
		return Greenobject;
	}

	/**
	 * @return the set of edges that are affected by this event. Is empty if no edge
	 *         is affected by this event.
	 */
	public Set<DefaultWeightedEdge> getEdges() {
		return edges;
	}

	/**
	 * Returns the modification flag for the given Greenobject affected by this
	 * event.
	 * 
	 * @param Greenobject
	 *            the Greenobject to query.
	 * @return the modification flag.
	 * @see #FLAG_Greenobject_ADDED
	 * @see #FLAG_Greenobject_MODIFIED
	 * @see #FLAG_Greenobject_REMOVED
	 */
	public Integer getGreenobjectFlag(final Greenobject Greenobject) {
		return GreenobjectFlags.get(Greenobject);
	}

	/**
	 * Returns the modification flag for the given edge affected by this event.
	 * 
	 * @param edge
	 *            the edge to query.
	 * @return the modification flag.
	 * @see #FLAG_EDGE_ADDED
	 * @see #FLAG_EDGE_REMOVED
	 */
	public Integer getEdgeFlag(final DefaultWeightedEdge edge) {
		return edgeFlags.get(edge);
	}

	public Integer getToFrame(final Greenobject Greenobject) {
		return toFrame.get(Greenobject);
	}

	public Integer getFromFrame(final Greenobject Greenobject) {
		return fromFrame.get(Greenobject);
	}

	public void setSource(final Object source) {
		this.source = source;
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder("[ModelChangeEvent]:\n");
		str.append(" - source: " + source.getClass() + "_" + source.hashCode() + "\n");
		str.append(" - event type: ");
		switch (eventID) {
		case Greenobject_COMPUTED:
			str.append("Greenobject computed\n");
			break;
		case Greenobject_FILTERED:
			str.append("Greenobject filtered\n");
			break;
		case TRACKS_COMPUTED:
			str.append("Tracks computed\n");
			break;
		case TRACKS_VISIBILITY_CHANGED:
			str.append("Track visibility changed\n");
			break;
		case MODEL_MODIFIED:
			str.append("Model modified, with:\n");
			str.append("\t- Greenobject modified: " + (Greenobject != null ? Greenobject.size() : 0) + "\n");
			for (final Greenobject Greenobject : Greenobject) {
				str.append("\t\t" + Greenobject + ": " + flagsToString.get(GreenobjectFlags.get(Greenobject)) + "\n");
			}
			str.append("\t- edges modified: " + (edges != null ? edges.size() : 0) + "\n");
			for (final DefaultWeightedEdge edge : edges) {
				str.append("\t\t" + edge + ": " + flagsToString.get(edgeFlags.get(edge)) + "\n");
			}
			str.append("\t- tracks to update: " + trackUpdated + "\n");
		}
		return str.toString();
	}

	public void setTracksUpdated(final Set<Integer> tracksToUpdate) {
		this.trackUpdated = tracksToUpdate;
	}

	/**
	 * @return the IDs of track that were modified or created by this event.
	 */
	public Set<Integer> getTrackUpdated() {
		return trackUpdated;
	}


}
