package Buddy.plugin.trackmate;

import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import budDetector.Spot;

import org.jgrapht.graph.DefaultWeightedEdge;

public class ModelChangeEvent extends EventObject {

	private static final long serialVersionUID = -1L;
	/** Indicate that a Spot was added to the model. */
	public static final int FLAG_Spot_ADDED = 0;
	
	/** Indicate that a Spot was removed from the model. */
	public static final int FLAG_Spot_REMOVED = 1;
	/**
	 * Indicate a modification of the features of a Spot. It may have changed
	 * of position and feature, but not of frame.
	 */
	public static final int FLAG_Spot_MODIFIED = 2;
	/**
	 * Indicate that a Spot has changed of frame, and possible of position,
	 * features, etc.. .
	 */
	public static final int FLAG_Spot_FRAME_CHANGED = 3;
	/** Indicate that an edge was added to the model. */
	public static final int FLAG_EDGE_ADDED = 4;
	/** Indicate that an edge was removed from the model. */
	public static final int FLAG_EDGE_REMOVED = 5;
	/**
	 * Indicate that an edge has been modified. Edge modifications occur when the
	 * target or source Spot are modified, or when the weight of the edge has
	 * been modified.
	 */
	public static final int FLAG_EDGE_MODIFIED = 6;

	public static final Map<Integer, String> flagsToString = new HashMap<>(7);
	static {
		flagsToString.put(FLAG_Spot_ADDED, "Spot added");
		flagsToString.put(FLAG_Spot_FRAME_CHANGED, "Spot frame changed");
		flagsToString.put(FLAG_Spot_MODIFIED, "Spot modified");
		flagsToString.put(FLAG_Spot_REMOVED, "Spot removed");
		flagsToString.put(FLAG_EDGE_ADDED, "Edge added");
		flagsToString.put(FLAG_EDGE_MODIFIED, "Edge modified");
		flagsToString.put(FLAG_EDGE_REMOVED, "Edge removed");
	}

	/**
	 * Event type indicating that the Spot of the model were computed, and
	 * are now accessible through {@link Model#getSpot()}.
	 */
	public static final int Spot_COMPUTED = 4;
	/**
	 * Event type indicating that the Spot of the model were filtered.
	 */
	public static final int Spot_FILTERED = 5;
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
	 * changing the feature of some Spot, and/or adding or removing edges in
	 * the tracks. Content of the modification can be accessed by
	 * {@link #getSpot()}, {@link #getSpotFlag(Spot)},
	 * {@link #getFromFrame(Spot)} and {@link #getToFrame(Spot)}, and
	 * for the tracks: {@link #getEdges()} and
	 * {@link #getEdgeFlag(DefaultWeightedEdge)} .
	 */
	public static final int MODEL_MODIFIED = 8;

	/** Spot affected by this event. */
	private final HashSet<Spot> Spot = new HashSet<>();
	/** Edges affected by this event. */
	private final HashSet<DefaultWeightedEdge> edges = new HashSet<>();
	/**
	 * For Spot removed or moved: frame from which they were removed or
	 * moved.
	 */
	private final HashMap<Spot, Integer> fromFrame = new HashMap<>();
	/**
	 * For Spot removed or added: frame to which they were added or moved.
	 */
	private final HashMap<Spot, Integer> toFrame = new HashMap<>();
	/** Modification flag for Spot affected by this event. */
	private final HashMap<Spot, Integer> SpotFlags = new HashMap<>();
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
	public ModelChangeEvent(final Object source, final int eventID) {
		super(source);
		this.eventID = eventID;
	}

	public int getEventID() {
		return this.eventID;
	}

	public boolean addAllSpot(final Collection<Spot> lSpot) {
		return this.Spot.addAll(lSpot);
	}

	public boolean addSpot(final Spot Spot) {
		return this.Spot.add(Spot);
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

	public Integer putSpotFlag(final Spot Spot, final Integer flag) {
		return SpotFlags.put(Spot, flag);
	}

	public Integer putFromFrame(final Spot Spot, final Integer lFromFrame) {
		return this.fromFrame.put(Spot, lFromFrame);
	}

	public Integer putToFrame(final Spot Spot, final Integer lToFrame) {
		return this.toFrame.put(Spot, lToFrame);
	}

	/**
	 * @return the set of Spot that are affected by this event. Is empty if
	 *         no Spot is affected by this event.
	 */
	public Set<Spot> getSpot() {
		return Spot;
	}

	/**
	 * @return the set of edges that are affected by this event. Is empty if no edge
	 *         is affected by this event.
	 */
	public Set<DefaultWeightedEdge> getEdges() {
		return edges;
	}

	/**
	 * Returns the modification flag for the given Spot affected by this
	 * event.
	 * 
	 * @param Spot
	 *            the Spot to query.
	 * @return the modification flag.
	 * @see #FLAG_Spot_ADDED
	 * @see #FLAG_Spot_MODIFIED
	 * @see #FLAG_Spot_REMOVED
	 */
	public Integer getSpotFlag(final Spot Spot) {
		return SpotFlags.get(Spot);
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

	public Integer getToFrame(final Spot Spot) {
		return toFrame.get(Spot);
	}

	public Integer getFromFrame(final Spot Spot) {
		return fromFrame.get(Spot);
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
		case Spot_COMPUTED:
			str.append("Spot computed\n");
			break;
		case Spot_FILTERED:
			str.append("Spot filtered\n");
			break;
		case TRACKS_COMPUTED:
			str.append("Tracks computed\n");
			break;
		case TRACKS_VISIBILITY_CHANGED:
			str.append("Track visibility changed\n");
			break;
		case MODEL_MODIFIED:
			str.append("Model modified, with:\n");
			str.append("\t- Spot modified: " + (Spot != null ? Spot.size() : 0) + "\n");
			for (final Spot Spot : Spot) {
				str.append("\t\t" + Spot + ": " + flagsToString.get(SpotFlags.get(Spot)) + "\n");
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
