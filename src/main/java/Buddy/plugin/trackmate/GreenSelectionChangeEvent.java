package Buddy.plugin.trackmate;

import java.util.EventObject;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;

import greenDetector.Greenobject;


/**
 * An event that characterizes a change in the current selection.
 * {@link Greenobject} selection and {@link DefaultWeightedEdge} selection are
 * dealt with separately, to keep the use of this class general.
 */
public class GreenSelectionChangeEvent extends EventObject {

	private static final long serialVersionUID = -8920831578922412606L;

	/** Changes in {@link DefaultWeightedEdge} selection this event represents. */
	private final Map<DefaultWeightedEdge, Boolean> edges;

	/** Changes in {@link Greenobject} selection this event represents. */
	protected Map<Greenobject, Boolean> Greenobjects;

	/*
	 * CONSTRUCTORS
	 */

	/**
	 * Represents a change in the selection of a displayed TM model.
	 * <p>
	 * Two maps are given. The first one represent changes in the Greenobject
	 * selection. The {@link Boolean} mapped to a {@link Greenobject} key specifies
	 * if the Greenobject was added to the selection (<code>true</code>) or removed
	 * from it (<code>false</code>). The same goes for the
	 * {@link DefaultWeightedEdge} map. <code>null</code>s are accepted for the two
	 * maps, to specify that no changes happened for the corresponding type.
	 * 
	 * @param source
	 *            the source object that fires this event.
	 * @param GreenobjectMap
	 *            the Greenobjects that are added or removed from the selection by
	 *            this event.
	 * @param edges
	 *            the edges that are added or removed from the selection by this
	 *            event.
	 */
	public GreenSelectionChangeEvent(final Object source, final Map<Greenobject, Boolean> GreenobjectMap,
			final Map<DefaultWeightedEdge, Boolean> edges) {
		super(source);
		this.Greenobjects = GreenobjectMap;
		this.edges = edges;
	}

	/*
	 * METHODS
	 */

	/**
	 * Returns the Greenobjects that have been added or removed from the selection.
	 * The {@link Boolean} mapped to a {@link Greenobject} key specifies if the
	 * Greenobject was added to the selection (<code>true</code>) or removed from it
	 * (<code>false</code>).
	 * 
	 * @return added or removed Greenobjects, can be <code>null</code> if no changes
	 *         on Greenobject selection happened.
	 */
	public Map<Greenobject, Boolean> getGreenobjects() {
		return Greenobjects;
	}

	/**
	 * Returns the edges that have been added or removed from the selection. The
	 * {@link Boolean} mapped to a {@link DefaultWeightedEdge} key specifies if the
	 * edge was added to the selection (<code>true</code>) or removed from it
	 * (<code>false</code>).
	 * 
	 * @return added or removed edges, can be <code>null</code> if no changes on
	 *         edge selection happened.
	 */
	public Map<DefaultWeightedEdge, Boolean> getEdges() {
		return edges;
	}

}
