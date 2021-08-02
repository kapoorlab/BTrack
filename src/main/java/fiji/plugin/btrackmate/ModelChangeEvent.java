package fiji.plugin.btrackmate;

import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

public class ModelChangeEvent extends EventObject {

	private static final long serialVersionUID = -1L;
	/** Indicate that a spot was added to the model. */
	public static final int FLAG_SPOT_ADDED = 0;
	/** Indicate that a spot was removed from the model. */
	public static final int FLAG_SPOT_REMOVED = 1;
	/**
	 * Indicate a modification of the features of a spot. It may have changed of
	 * position and feature, but not of frame.
	 */
	public static final int FLAG_SPOT_MODIFIED = 2;
	/**
	 * Indicate that a spot has changed of frame, and possible of position,
	 * features, etc.. .
	 */
	public static final int FLAG_SPOT_FRAME_CHANGED = 3;
	/** Indicate that an edge was added to the model. */
	public static final int FLAG_EDGE_ADDED = 4;
	/** Indicate that an edge was removed from the model. */
	public static final int FLAG_EDGE_REMOVED = 5;
	/**
	 * Indicate that an edge has been modified. Edge modifications occur when the
	 * target or source spots are modified, or when the weight of the edge has been
	 * modified.
	 */
	public static final int FLAG_EDGE_MODIFIED = 6;

	public static final Map<Integer, String> flagsToString = new HashMap<>(7);
	static {
		flagsToString.put(FLAG_SPOT_ADDED, "Spot added");
		flagsToString.put(FLAG_SPOT_FRAME_CHANGED, "Spot frame changed");
		flagsToString.put(FLAG_SPOT_MODIFIED, "Spot modified");
		flagsToString.put(FLAG_SPOT_REMOVED, "Spot removed");
		flagsToString.put(FLAG_EDGE_ADDED, "Edge added");
		flagsToString.put(FLAG_EDGE_MODIFIED, "Edge modified");
		flagsToString.put(FLAG_EDGE_REMOVED, "Edge removed");
	}

	/**
	 * Event type indicating that the spots of the model were computed, and are now
	 * accessible through {@link Model#getSpots()}.
	 */
	public static final int SPOTS_COMPUTED = 4;
	/**
	 * Event type indicating that the spots of the model were filtered.
	 */
	public static final int SPOTS_FILTERED = 5;
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
	 * changing the feature of some spots, and/or adding or removing edges in the
	 * tracks. Content of the modification can be accessed by {@link #getSpots()},
	 * {@link #getSpotFlag(Spot)}, {@link #getFromFrame(Spot)} and
	 * {@link #getToFrame(Spot)}, and for the tracks: {@link #getEdges()} and
	 * {@link #getEdgeFlag(DefaultWeightedEdge)} .
	 */
	public static final int MODEL_MODIFIED = 8;

	/**
	 * Event type indicated that the feature values for the objects in this model
	 * have been computed.
	 */
	public static final int FEATURES_COMPUTED = 9;

	/** Spots affected by this event. */
	private final HashSet<Spot> spots = new HashSet<>();
	/** Edges affected by this event. */
	private final HashSet<DefaultWeightedEdge> edges = new HashSet<>();
	/** For spots removed or moved: frame from which they were removed or moved. */
	private final HashMap<Spot, Integer> fromFrame = new HashMap<>();
	/** For spots removed or added: frame to which they were added or moved. */
	private final HashMap<Spot, Integer> toFrame = new HashMap<>();
	/** Modification flag for spots affected by this event. */
	private final HashMap<Spot, Integer> spotFlags = new HashMap<>();
	/** Modification flag for edges affected by this event. */
	private final HashMap<DefaultWeightedEdge, Integer> edgeFlags = new HashMap<>();
	/** The event type for this instance. */
	private final int eventID;
	private Set<Integer> trackUpdated;

	/**
	 * Create a new event, reflecting a change in a {@link Model}.
	 *
	 * @param source  the object source of this event.
	 * @param eventID the evend ID to use for this event.
	 */
	public ModelChangeEvent(final Object source, final int eventID) {
		super(source);
		this.eventID = eventID;
	}

	public int getEventID() {
		return this.eventID;
	}

	public boolean addAllSpots(final Collection<Spot> lSpots) {
		return this.spots.addAll(lSpots);
	}

	public boolean addSpot(final Spot spot) {
		return this.spots.add(spot);
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

	public Integer putSpotFlag(final Spot spot, final Integer flag) {
		return spotFlags.put(spot, flag);
	}

	public Integer putFromFrame(final Spot spot, final Integer lFromFrame) {
		return this.fromFrame.put(spot, lFromFrame);
	}

	public Integer putToFrame(final Spot spot, final Integer lToFrame) {
		return this.toFrame.put(spot, lToFrame);
	}

	/**
	 * @return the set of spot that are affected by this event. Is empty if no spot
	 *         is affected by this event.
	 */
	public Set<Spot> getSpots() {
		return spots;
	}

	/**
	 * @return the set of edges that are affected by this event. Is empty if no edge
	 *         is affected by this event.
	 */
	public Set<DefaultWeightedEdge> getEdges() {
		return edges;
	}

	/**
	 * Returns the modification flag for the given spot affected by this event.
	 * 
	 * @param spot the spot to query.
	 * @return the modification flag.
	 * @see #FLAG_SPOT_ADDED
	 * @see #FLAG_SPOT_MODIFIED
	 * @see #FLAG_SPOT_REMOVED
	 */
	public Integer getSpotFlag(final Spot spot) {
		return spotFlags.get(spot);
	}

	/**
	 * Returns the modification flag for the given edge affected by this event.
	 * 
	 * @param edge the edge to query.
	 * @return the modification flag.
	 * @see #FLAG_EDGE_ADDED
	 * @see #FLAG_EDGE_REMOVED
	 */
	public Integer getEdgeFlag(final DefaultWeightedEdge edge) {
		return edgeFlags.get(edge);
	}

	public Integer getToFrame(final Spot spot) {
		return toFrame.get(spot);
	}

	public Integer getFromFrame(final Spot spot) {
		return fromFrame.get(spot);
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
		case SPOTS_COMPUTED:
			str.append("Spots computed\n");
			break;
		case SPOTS_FILTERED:
			str.append("Spots filtered\n");
			break;
		case TRACKS_COMPUTED:
			str.append("Tracks computed\n");
			break;
		case TRACKS_VISIBILITY_CHANGED:
			str.append("Track visibility changed\n");
			break;
		case MODEL_MODIFIED:
			str.append("Model modified, with:\n");
			str.append("\t- spots modified: " + (spots != null ? spots.size() : 0) + "\n");
			for (final Spot spot : spots) {
				str.append("\t\t" + spot + ": " + flagsToString.get(spotFlags.get(spot)) + "\n");
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
