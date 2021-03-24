package Buddy.plugin.trackmate;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import budDetector.Spot;
import Buddy.plugin.trackmate.features.FeatureFilter;

/**
 * <h1>The model for the data managed by TrackMate trackmate.</h1>
 * <p>
 * This is a relatively large class, with a lot of public methods. This
 * complexity arose because this class handles data storage and manipulation,
 * through user manual editing and automatic processing. To avoid conflicting
 * accesses to the data, some specialized methods had to be created, hopefully
 * built in coherent sets.
 *
 * @author Jean-Yves Tinevez &lt;tinevez@pasteur.fr&gt; - 2010-2013
 *
 */
public class Model {

	/*
	 * CONSTANTS
	 */

	private static final boolean DEBUG = false;

	/*
	 * FIELDS
	 */

	// FEATURES

	private final FeatureModel featureModel;

	// TRACKS

	private final TrackModel trackModel;

	// SpotS

	/** The Spots managed by this model. */
	protected SpotCollection Spots = new SpotCollection();

	// TRANSACTION MODEL

	/**
	 * Counter for the depth of nested transactions. Each call to beginUpdate
	 * increments this counter and each call to endUpdate decrements it. When the
	 * counter reaches 0, the transaction is closed and the respective events are
	 * fired. Initial value is 0.
	 */
	private int updateLevel = 0;

	private final HashSet<Spot> SpotsAdded = new HashSet<>();

	private final HashSet<Spot> SpotsRemoved = new HashSet<>();

	private final HashSet<Spot> SpotsMoved = new HashSet<>();

	private final HashSet<Spot> SpotsUpdated = new HashSet<>();

	/**
	 * The event cache. During a transaction, some modifications might trigger the
	 * need to fire a model change event. We want to fire these events only when the
	 * transaction closes (when the updateLevel reaches 0), so we store the event ID
	 * in this cache in the meantime. The event cache contains only the int IDs of
	 * the events listed in {@link ModelChangeEvent}, namely
	 * <ul>
	 * <li>{@link ModelChangeEvent#SpotS_COMPUTED}
	 * <li>{@link ModelChangeEvent#TRACKS_COMPUTED}
	 * <li>{@link ModelChangeEvent#TRACKS_VISIBILITY_CHANGED}
	 * </ul>
	 * The {@link ModelChangeEvent#MODEL_MODIFIED} cannot be cached this way, for it
	 * needs to be configured with modification Spot and edge targets, so it
	 * uses a different system (see {@link #flushUpdate()}).
	 */
	private final HashSet<Integer> eventCache = new HashSet<>();

	// OTHERS

	/** The logger to append processes messages. */
	private Logger logger = Logger.DEFAULT_LOGGER;

	private String spaceUnits = "pixels";

	private String timeUnits = "frames";

	// LISTENERS

	/**
	 * The list of listeners listening to model content change.
	 */
	Set<ModelChangeListener> modelChangeListeners = new LinkedHashSet<>();

	/*
	 * CONSTRUCTOR
	 */

	public Model() {
		featureModel = createFeatureModel();
		trackModel = createTrackModel();
	}

	/*
	 * HOOKS
	 */

	/**
	 * Instantiates a blank {@link TrackModel} to use whithin this model.
	 * <p>
	 * Subclassers can override this method to have the model work with their own
	 * subclass of {@link TrackModel}.
	 *
	 * @return a new instance of {@link TrackModel}.
	 */
	protected TrackModel createTrackModel() {
		return new TrackModel();
	}

	/**
	 * Instantiates a blank {@link FeatureModel} to use whithin this model.
	 * <p>
	 * Subclassers can override this method to have the model work with their own
	 * subclass of {@link FeatureModel}.
	 * 
	 * @return a new instance of {@link FeatureModel}.
	 */
	protected FeatureModel createFeatureModel() {
		return new FeatureModel(this);
	}

	/*
	 * UTILS METHODS
	 */

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();

		str.append('\n');
		if (null == Spots || Spots.keySet().size() == 0) {
			str.append("No Spots.\n");
		} else {
			str.append("Contains " + Spots.getNSpots() + " Spots in total.\n");
		}
		if (Spots.getNSpots() == 0) {
			str.append("No filtered Spots.\n");
		} else {
			str.append("Contains " + Spots.getNSpots() + " filtered Spots.\n");
		}

		str.append('\n');
		if (trackModel.nTracks(false) == 0) {
			str.append("No tracks.\n");
		} else {
			str.append("Contains " + trackModel.nTracks(false) + " tracks in total.\n");
		}
		if (trackModel.nTracks(true) == 0) {
			str.append("No filtered tracks.\n");
		} else {
			str.append("Contains " + trackModel.nTracks(true) + " filtered tracks.\n");
		}

		str.append('\n');
		str.append("Physical units:\n  space units: " + spaceUnits + "\n  time units: " + timeUnits + '\n');

		str.append('\n');
		str.append(featureModel.toString());

		return str.toString();
	}

	/*
	 * DEAL WITH MODEL CHANGE LISTENER
	 */

	public void addModelChangeListener(final ModelChangeListener listener) {
		modelChangeListeners.add(listener);
	}

	public boolean removeModelChangeListener(final ModelChangeListener listener) {
		return modelChangeListeners.remove(listener);
	}

	public Set<ModelChangeListener> getModelChangeListener() {
		return modelChangeListeners;
	}

	/*
	 * PHYSICAL UNITS
	 */

	/**
	 * Sets the physical units for the quantities stored in this model.
	 *
	 * @param spaceUnits
	 *            the spatial units (e.g. μm).
	 * @param timeUnits
	 *            the time units (e.g. min).
	 */
	public void setPhysicalUnits(final String spaceUnits, final String timeUnits) {
		this.spaceUnits = spaceUnits;
		this.timeUnits = timeUnits;
	}

	/**
	 * Returns the spatial units for the quantities stored in this model.
	 *
	 * @return the spatial units.
	 */
	public String getSpaceUnits() {
		return spaceUnits;
	}

	/**
	 * Returns the time units for the quantities stored in this model.
	 *
	 * @return the time units.
	 */
	public String getTimeUnits() {
		return timeUnits;
	}

	/*
	 * GRAPH MODIFICATION
	 */

	public synchronized void beginUpdate() {
		updateLevel++;
		if (DEBUG)
			System.out.println("[TrackMateModel] #beginUpdate: increasing update level to " + updateLevel + ".");
	}

	public synchronized void endUpdate() {
		updateLevel--;
		if (DEBUG)
			System.out.println("[TrackMateModel] #endUpdate: decreasing update level to " + updateLevel + ".");
		if (updateLevel == 0) {
			if (DEBUG)
				System.out.println("[TrackMateModel] #endUpdate: update level is 0, calling flushUpdate().");
			flushUpdate();
		}
	}

	/*
	 * TRACK METHODS: WE DELEGATE TO THE TRACK GRAPH MODEL
	 */

	/**
	 * Removes all the tracks from this model.
	 *
	 * @param doNotify
	 *            if <code>true</code>, model listeners will be notified with a
	 *            {@link ModelChangeEvent#TRACKS_COMPUTED} event.
	 */
	public void clearTracks(final boolean doNotify) {
		trackModel.clear();
		if (doNotify) {
			final ModelChangeEvent event = new ModelChangeEvent(this, ModelChangeEvent.TRACKS_COMPUTED);
			for (final ModelChangeListener listener : modelChangeListeners)
				listener.modelChanged(event);
		}
	}

	/**
	 * Returns the {@link TrackModel} that manages the tracks for this model.
	 * 
	 * @return the track model.
	 */
	public TrackModel getTrackModel() {
		return trackModel;
	}

	/**
	 * Sets the tracks stored in this model in bulk.
	 * <p>
	 * Clears the tracks of this model and replace it by the tracks found by
	 * inspecting the specified graph. All new tracks found will be made visible and
	 * will be given a default name.
	 * <p>
	 *
	 * @param graph
	 *            the graph to parse for tracks.
	 * @param doNotify
	 *            if <code>true</code>, model listeners will be notified with a
	 *            {@link ModelChangeEvent#TRACKS_COMPUTED} event.
	 */
	public void setTracks(final SimpleWeightedGraph<Spot, DefaultWeightedEdge> graph, final boolean doNotify) {
		trackModel.setGraph(graph);
		if (doNotify) {
			final ModelChangeEvent event = new ModelChangeEvent(this, ModelChangeEvent.TRACKS_COMPUTED);
			for (final ModelChangeListener listener : modelChangeListeners)
				listener.modelChanged(event);
		}
	}

	/*
	 * GETTERS / SETTERS FOR SpotS
	 */

	/**
	 * Returns the Spot collection managed by this model.
	 *
	 * @return the Spot collection managed by this model.
	 */
	public SpotCollection getSpots() {
		return Spots;
	}

	/**
	 * Removes all the Spots from this model.
	 *
	 * @param doNotify
	 *            if <code>true</code>, model listeners will be notified with a
	 *            {@link ModelChangeEvent#SpotS_COMPUTED} event.
	 */
	public void clearSpots(final boolean doNotify) {
		Spots.clear();
		if (doNotify) {
			final ModelChangeEvent event = new ModelChangeEvent(this, ModelChangeEvent.Spot_COMPUTED);
			for (final ModelChangeListener listener : modelChangeListeners)
				listener.modelChanged(event);
		}
	}

	/**
	 * Set the {@link SpotCollection} managed by this model.
	 *
	 * @param doNotify
	 *            if true, will file a
	 *            {@link ModelChangeEvent#SpotS_COMPUTED} event.
	 * @param Spots
	 *            the {@link SpotCollection} to set.
	 */
	public void setSpots(final SpotCollection Spots, final boolean doNotify) {
		this.Spots = Spots;
		if (doNotify) {
			final ModelChangeEvent event = new ModelChangeEvent(this, ModelChangeEvent.Spot_COMPUTED);
			for (final ModelChangeListener listener : modelChangeListeners)
				listener.modelChanged(event);
		}
	}

	/**
	 * Filters the {@link SpotCollection} managed by this model with the
	 * {@link FeatureFilter}s specified.
	 *
	 * @param SpotFilters
	 *            the {@link FeatureFilter} collection to use for filtering.
	 * @param doNotify
	 *            if true, will file a
	 *            {@link ModelChangeEvent#SpotS_FILTERED} event.
	 */
	public void filterSpots(final Collection<FeatureFilter> SpotFilters, final boolean doNotify) {
		Spots.filter(SpotFilters);
		if (doNotify) {
			final ModelChangeEvent event = new ModelChangeEvent(this, ModelChangeEvent.Spot_FILTERED);
			for (final ModelChangeListener listener : modelChangeListeners)
				listener.modelChanged(event);
		}

	}

	/*
	 * LOGGER
	 */

	/**
	 * Set the logger that will receive the messages from the processes occurring
	 * within this trackmate.
	 * 
	 * @param logger
	 *            the {@link Logger} to use.
	 */
	public void setLogger(final Logger logger) {
		this.logger = logger;
	}

	/**
	 * Return the logger currently set for this model.
	 * 
	 * @return the {@link Logger} used.
	 */
	public Logger getLogger() {
		return logger;
	}

	/*
	 * FEATURES
	 */

	public FeatureModel getFeatureModel() {
		return featureModel;
	}

	/*
	 * MODEL CHANGE METHODS
	 */

	/**
	 * Moves a single Spot from a frame to another, make it visible if it was
	 * not, then mark it for feature update. If the source Spot could not be
	 * found in the source frame, nothing is done and <code>null</code> is returned.
	 * <p>
	 * For the model update to happen correctly and listeners to be notified
	 * properly, a call to this method must happen within a transaction, as in:
	 *
	 * <pre>
	 * model.beginUpdate();
	 * try {
	 * 	... // model modifications here
	 * } finally {
	 * 	model.endUpdate();
	 * }
	 * </pre>
	 *
	 * @param SpotToMove
	 *            the Spot to move
	 * @param fromFrame
	 *            the frame the Spot originated from
	 * @param toFrame
	 *            the destination frame
	 * @return the Spot that was moved, or <code>null</code> if it could not
	 *         be found in the source frame
	 */
	public synchronized Spot moveSpotFrom(final Spot SpotToMove, final Integer fromFrame,
			final Integer toFrame) {
		final boolean ok = Spots.remove(SpotToMove, fromFrame);
		if (!ok) {
			if (DEBUG) {
				System.err.println(
						"[TrackMateModel] Could not find Spot " + SpotToMove + " in frame " + fromFrame);
			}
			return null;
		}
		Spots.add(SpotToMove, toFrame);
		if (DEBUG) {
			System.out.println("[TrackMateModel] Moving " + SpotToMove + " from frame " + fromFrame
					+ " to frame " + toFrame);
		}

		// Mark for update Spot and edges
		trackModel.edgesModified.addAll(trackModel.edgesOf(SpotToMove));
		SpotsMoved.add(SpotToMove);
		return SpotToMove;
	}

	/**
	 * Adds a single Spot to the collections managed by this model, mark it
	 * as visible, then update its features.
	 * <p>
	 * For the model update to happen correctly and listeners to be notified
	 * properly, a call to this method must happen within a transaction, as in:
	 *
	 * <pre>
	 * model.beginUpdate();
	 * try {
	 * 	... // model modifications here
	 * } finally {
	 * 	model.endUpdate();
	 * }
	 * </pre>
	 * 
	 * @param SpotToAdd
	 *            the Spot to add.
	 * @param toFrame
	 *            the frame to add it to.
	 *
	 * @return the Spot just added.
	 */
	public synchronized Spot addSpotTo(final Spot SpotToAdd, final Integer toFrame) {
		Spots.add(SpotToAdd, toFrame);
		SpotsAdded.add(SpotToAdd); // TRANSACTION
		if (DEBUG) {
			System.out.println("[TrackMateModel] Adding Spot " + SpotToAdd + " to frame " + toFrame);
		}
		trackModel.addSpot(SpotToAdd);
		return SpotToAdd;
	}

	/**
	 * Removes a single Spot from the collections managed by this model. If
	 * the Spot cannot be found, nothing is done and <code>null</code> is
	 * returned.
	 * <p>
	 * For the model update to happen correctly and listeners to be notified
	 * properly, a call to this method must happen within a transaction, as in:
	 *
	 * <pre>
	 * model.beginUpdate();
	 * try {
	 * 	... // model modifications here
	 * } finally {
	 * 	model.endUpdate();
	 * }
	 * </pre>
	 *
	 * @param SpotToRemove
	 *            the Spot to remove.
	 * @return the Spot removed, or <code>null</code> if it could not be
	 *         found.
	 */
	public synchronized Spot removeSpot(final Spot SpotToRemove) {
		final int fromFrame = SpotToRemove.getFeature(Spot.POSITION_T).intValue();
		if (Spots.remove(SpotToRemove, fromFrame)) {
			SpotsRemoved.add(SpotToRemove); // TRANSACTION
			if (DEBUG)
				System.out.println(
						"[TrackMateModel] Removing Spot " + SpotToRemove + " from frame " + fromFrame);

			trackModel.removeSpot(SpotToRemove);
			// changes to edges will be caught automatically by the TrackGraphModel
			return SpotToRemove;
		}
		if (DEBUG)
			System.err.println("[TrackMateModel] The Spot " + SpotToRemove + " cannot be found in frame "
					+ fromFrame);

		return null;
	}

	/**
	 * Mark the specified Spot for update. At the end of the model
	 * transaction, its features will be recomputed, and other edge and track
	 * features that depends on it will be as well.
	 * <p>
	 * For the model update to happen correctly and listeners to be notified
	 * properly, a call to this method must happen within a transaction, as in:
	 *
	 * <pre>
	 * model.beginUpdate();
	 * try {
	 * 	... // model modifications here
	 * } finally {
	 * 	model.endUpdate();
	 * }
	 * </pre>
	 *
	 * @param SpotToUpdate
	 *            the Spot to mark for update
	 */
	public synchronized void updateFeatures(final Spot SpotToUpdate) {
		SpotsUpdated.add(SpotToUpdate); // Enlist for feature update when
		// transaction is marked as finished
		final Set<DefaultWeightedEdge> touchingEdges = trackModel.edgesOf(SpotToUpdate);
		if (null != touchingEdges) {
			trackModel.edgesModified.addAll(touchingEdges);
		}
	}

	/**
	 * Creates a new edge between two Spots, with the specified weight.
	 * <p>
	 * For the model update to happen correctly and listeners to be notified
	 * properly, a call to this method must happen within a transaction, as in:
	 *
	 * <pre>
	 * model.beginUpdate();
	 * try {
	 * 	... // model modifications here
	 * } finally {
	 * 	model.endUpdate();
	 * }
	 * </pre>
	 *
	 * @param source
	 *            the source Spot.
	 * @param target
	 *            the target Spot.
	 * @param weight
	 *            the weight of the edge.
	 * @return the edge created.
	 */
	public synchronized DefaultWeightedEdge addEdge(final Spot source, final Spot target,
			final double weight) {
		return trackModel.addEdge(source, target, weight);
	}

	/**
	 * Removes an edge between two Spots and returns it. Returns
	 * <code>null</code> and do nothing to the tracks if the edge did not exist.
	 *
	 * @param source
	 *            the source Spot.
	 * @param target
	 *            the target Spot.
	 * @return the edge between the two Spots, if it existed.
	 */
	public synchronized DefaultWeightedEdge removeEdge(final Spot source, final Spot target) {
		return trackModel.removeEdge(source, target);
	}

	/**
	 * Removes an existing edge from the model.
	 * <p>
	 * For the model update to happen correctly and listeners to be notified
	 * properly, a call to this method must happen within a transaction, as in:
	 *
	 * <pre>
	 * model.beginUpdate();
	 * try {
	 * 	... // model modifications here
	 * } finally {
	 * 	model.endUpdate();
	 * }
	 * </pre>
	 *
	 * @param edge
	 *            the edge to remove.
	 * @return <code>true</code> if the edge existed in the model and was
	 *         successfully, <code>false</code> otherwise.
	 */
	public synchronized boolean removeEdge(final DefaultWeightedEdge edge) {
		return trackModel.removeEdge(edge);
	}

	/**
	 * Sets the weight of the specified edge.
	 * <p>
	 * For the model update to happen correctly and listeners to be notified
	 * properly, a call to this method must happen within a transaction, as in:
	 *
	 * <pre>
	 * model.beginUpdate();
	 * try {
	 * 	... // model modifications here
	 * } finally {
	 * 	model.endUpdate();
	 * }
	 * </pre>
	 *
	 * @param edge
	 *            the edge.
	 * @param weight
	 *            the weight to set.
	 */
	public synchronized void setEdgeWeight(final DefaultWeightedEdge edge, final double weight) {
		trackModel.setEdgeWeight(edge, weight);
	}

	/**
	 * Sets the visibility of the specified track. Throws a
	 * {@link NullPointerException} if the track ID is unknown to the model.
	 * <p>
	 * For the model update to happen correctly and listeners to be notified
	 * properly, a call to this method must happen within a transaction, as in:
	 *
	 * <pre>
	 * model.beginUpdate();
	 * try {
	 * 	... // model modifications here
	 * } finally {
	 * 	model.endUpdate();
	 * }
	 * </pre>
	 *
	 * @param trackID
	 *            the track ID.
	 * @param visible
	 *            the desired visibility.
	 * @return the specified track visibility prior to calling this method.
	 */
	public synchronized boolean setTrackVisibility(final Integer trackID, final boolean visible) {
		final boolean oldvis = trackModel.setVisibility(trackID, visible);
		final boolean modified = oldvis != visible;
		if (modified) {
			eventCache.add(ModelChangeEvent.TRACKS_VISIBILITY_CHANGED);
		}
		return oldvis;
	}

	/*
	 * PRIVATE METHODS
	 */

	/**
	 * Fire events. Regenerate fields derived from the filtered graph.
	 */
	private void flushUpdate() {

		if (DEBUG) {
			System.out.println("[TrackMateModel] #flushUpdate().");
			System.out.println("[TrackMateModel] #flushUpdate(): Event cache is :" + eventCache);
			System.out.println("[TrackMateModel] #flushUpdate(): Track content is:\n" + trackModel.echo());
		}

		/*
		 * We recompute tracks only if some edges have been added or removed, (if some
		 * Spots have been removed that causes edges to be removes, we already
		 * know about it). We do NOT recompute tracks if Spots have been added:
		 * they will not result in new tracks made of single Spots.
		 */
		final int nEdgesToSignal = trackModel.edgesAdded.size() + trackModel.edgesRemoved.size()
				+ trackModel.edgesModified.size();

		// Do we have tracks to update?
		final HashSet<Integer> tracksToUpdate = new HashSet<>(trackModel.tracksUpdated);

		// We also want to update the tracks that have edges that were modified
		for (final DefaultWeightedEdge modifiedEdge : trackModel.edgesModified) {
			tracksToUpdate.add(trackModel.trackIDOf(modifiedEdge));
		}

		// Deal with new or moved Spots: we need to update their features.
		final int nSpotsToUpdate = SpotsAdded.size() + SpotsMoved.size()
				+ SpotsUpdated.size();
		if (nSpotsToUpdate > 0) {
			final HashSet<Spot> SpotsToUpdate = new HashSet<>(nSpotsToUpdate);
			SpotsToUpdate.addAll(SpotsAdded);
			SpotsToUpdate.addAll(SpotsMoved);
			SpotsToUpdate.addAll(SpotsUpdated);
		}

		// Initialize event
		final ModelChangeEvent event = new ModelChangeEvent(this, ModelChangeEvent.MODEL_MODIFIED);

		// Configure it with Spots to signal.
		final int nSpotsToSignal = nSpotsToUpdate + SpotsRemoved.size();
		if (nSpotsToSignal > 0) {
			event.addAllSpot(SpotsAdded);
			event.addAllSpot(SpotsRemoved);
			event.addAllSpot(SpotsMoved);
			event.addAllSpot(SpotsUpdated);

			for (final Spot Spot : SpotsAdded) {
				event.putSpotFlag(Spot, ModelChangeEvent.FLAG_Spot_ADDED);
			}
			for (final Spot Spot : SpotsRemoved) {
				event.putSpotFlag(Spot, ModelChangeEvent.FLAG_Spot_REMOVED);
			}
			for (final Spot Spot : SpotsMoved) {
				event.putSpotFlag(Spot, ModelChangeEvent.FLAG_Spot_FRAME_CHANGED);
			}
			for (final Spot Spot : SpotsUpdated) {
				event.putSpotFlag(Spot, ModelChangeEvent.FLAG_Spot_MODIFIED);
			}
		}

		// Configure it with edges to signal.
		if (nEdgesToSignal > 0) {
			event.addAllEdges(trackModel.edgesAdded);
			event.addAllEdges(trackModel.edgesRemoved);
			event.addAllEdges(trackModel.edgesModified);

			for (final DefaultWeightedEdge edge : trackModel.edgesAdded) {
				event.putEdgeFlag(edge, ModelChangeEvent.FLAG_EDGE_ADDED);
			}
			for (final DefaultWeightedEdge edge : trackModel.edgesRemoved) {
				event.putEdgeFlag(edge, ModelChangeEvent.FLAG_EDGE_REMOVED);
			}
			for (final DefaultWeightedEdge edge : trackModel.edgesModified) {
				event.putEdgeFlag(edge, ModelChangeEvent.FLAG_EDGE_MODIFIED);
			}
		}

		// Configure it with the tracks we found need updating
		event.setTracksUpdated(tracksToUpdate);

		try {
			if (nEdgesToSignal + nSpotsToSignal > 0) {
				if (DEBUG) {
					System.out.println("[TrackMateModel] #flushUpdate(): firing model modified event");
					System.out.println("[TrackMateModel] to " + modelChangeListeners);

				}
				for (final ModelChangeListener listener : modelChangeListeners) {
					listener.modelChanged(event);
				}
			}

			// Fire events stored in the event cache
			for (final int eventID : eventCache) {
				if (DEBUG) {
					System.out.println("[TrackMateModel] #flushUpdate(): firing event with ID " + eventID);
				}
				final ModelChangeEvent cachedEvent = new ModelChangeEvent(this, eventID);
				for (final ModelChangeListener listener : modelChangeListeners) {
					listener.modelChanged(cachedEvent);
				}
			}

		} finally {
			SpotsAdded.clear();
			SpotsRemoved.clear();
			SpotsMoved.clear();
			SpotsUpdated.clear();
			trackModel.edgesAdded.clear();
			trackModel.edgesRemoved.clear();
			trackModel.edgesModified.clear();
			trackModel.tracksUpdated.clear();
			eventCache.clear();
		}

	}

}
