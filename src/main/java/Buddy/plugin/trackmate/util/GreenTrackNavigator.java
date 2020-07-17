package Buddy.plugin.trackmate.util;

import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenSelectionModel;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.graph.GreenTimeDirectedNeighborIndex;
import Buddy.plugin.trackmate.graph.TimeDirectedNeighborIndex;
import greenDetector.Greenobject;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.graph.DefaultWeightedEdge;


public class GreenTrackNavigator {

	private final GreenModel model;
	private final GreenSelectionModel selectionModel;
	private final GreenTimeDirectedNeighborIndex neighborIndex;

	public GreenTrackNavigator(final GreenModel model, final GreenSelectionModel selectionModel) {
		this.model = model;
		this.selectionModel = selectionModel;
		this.neighborIndex = model.getTrackModel().getDirectedNeighborIndex();
	}

	public synchronized void nextTrack() {
		final Greenobject Greenobject = getAGreenobject();
		if (null == Greenobject) {
			return;
		}

		final Set<Integer> trackIDs = model.getTrackModel().trackIDs(true); // if only it was navigable...
		if (trackIDs.isEmpty()) {
			return;
		}

		Integer trackID = model.getTrackModel().trackIDOf(Greenobject);
		if (null == trackID) {
			// No track? Then move to the first one.
			trackID = model.getTrackModel().trackIDs(true).iterator().next();
		}

		final Iterator<Integer> it = trackIDs.iterator();
		Integer nextTrackID = null;
		while (it.hasNext()) {
			final Integer id = it.next();
			if (id.equals(trackID)) {
				if (it.hasNext()) {
					nextTrackID = it.next();
					break;
				}
				nextTrackID = trackIDs.iterator().next(); // loop
			}
		}

		final Set<Greenobject> Greenobjects = model.getTrackModel().trackGreenobjects(nextTrackID);
		final TreeSet<Greenobject> ring = new TreeSet<>(Greenobject.frameComparator);
		ring.addAll(Greenobjects);
		Greenobject target = ring.ceiling(Greenobject);
		if (null == target) {
			target = ring.floor(Greenobject);
		}

		selectionModel.clearSelection();
		selectionModel.addGreenobjectToSelection(target);
	}

	public synchronized void previousTrack() {
		final Greenobject Greenobject = getAGreenobject();
		if (null == Greenobject) {
			return;
		}

		Integer trackID = model.getTrackModel().trackIDOf(Greenobject);
		final Set<Integer> trackIDs = model.getTrackModel().trackIDs(true); // if only it was navigable...
		if (trackIDs.isEmpty()) {
			return;
		}

		Integer lastID = null;
		for (final Integer id : trackIDs) {
			lastID = id;
		}

		if (null == trackID) {
			// No track? Then take the last one.
			trackID = lastID;
		}

		final Iterator<Integer> it = trackIDs.iterator();
		Integer previousTrackID = null;
		while (it.hasNext()) {
			final Integer id = it.next();
			if (id.equals(trackID)) {
				if (previousTrackID != null) {
					break;
				}
				previousTrackID = lastID;
				break;
			}
			previousTrackID = id;
		}

		final Set<Greenobject> Greenobjects = model.getTrackModel().trackGreenobjects(previousTrackID);
		final TreeSet<Greenobject> ring = new TreeSet<>(Greenobject.frameComparator);
		ring.addAll(Greenobjects);
		Greenobject target = ring.ceiling(Greenobject);
		if (null == target) {
			target = ring.floor(Greenobject);
		}

		selectionModel.clearSelection();
		selectionModel.addGreenobjectToSelection(target);
	}

	public synchronized void nextSibling() {
		final Greenobject Greenobject = getAGreenobject();
		if (null == Greenobject) {
			return;
		}

		final Integer trackID = model.getTrackModel().trackIDOf(Greenobject);
		if (null == trackID) {
			return;
		}

		final int frame = Greenobject.getFeature(Greenobject.POSITION_T).intValue();
		final TreeSet<Greenobject> ring = new TreeSet<>(Greenobject.nameComparator);

		final Set<Greenobject> Greenobjects = model.getTrackModel().trackGreenobjects(trackID);
		for (final Greenobject s : Greenobjects) {
			final int fs = s.getFeature(Greenobject.POSITION_T).intValue();
			if (frame == fs && s != Greenobject) {
				ring.add(s);
			}
		}

		if (!ring.isEmpty()) {
			Greenobject nextSibling = ring.ceiling(Greenobject);
			if (null == nextSibling) {
				nextSibling = ring.first(); // loop
			}
			selectionModel.clearSelection();
			selectionModel.addGreenobjectToSelection(nextSibling);
		}
	}

	public synchronized void previousSibling() {
		final Greenobject Greenobject = getAGreenobject();
		if (null == Greenobject) {
			return;
		}

		final Integer trackID = model.getTrackModel().trackIDOf(Greenobject);
		if (null == trackID) {
			return;
		}

		final int frame = Greenobject.getFeature(Greenobject.POSITION_T).intValue();
		final TreeSet<Greenobject> ring = new TreeSet<>(Greenobject.nameComparator);

		final Set<Greenobject> Greenobjects = model.getTrackModel().trackGreenobjects(trackID);
		for (final Greenobject s : Greenobjects) {
			final int fs = s.getFeature(Greenobject.POSITION_T).intValue();
			if (frame == fs && s != Greenobject) {
				ring.add(s);
			}
		}

		if (!ring.isEmpty()) {
			Greenobject previousSibling = ring.floor(Greenobject);
			if (null == previousSibling) {
				previousSibling = ring.last(); // loop
			}
			selectionModel.clearSelection();
			selectionModel.addGreenobjectToSelection(previousSibling);
		}
	}

	public synchronized void previousInTime() {
		final Greenobject Greenobject = getAGreenobject();
		if (null == Greenobject) {
			return;
		}

		final Set<Greenobject> predecessors = neighborIndex.predecessorsOf(Greenobject);
		if (!predecessors.isEmpty()) {
			final Greenobject next = predecessors.iterator().next();
			selectionModel.clearSelection();
			selectionModel.addGreenobjectToSelection(next);
		}
	}

	public synchronized void nextInTime() {
		final Greenobject Greenobject = getAGreenobject();
		if (null == Greenobject) {
			return;
		}

		final Set<Greenobject> successors = neighborIndex.successorsOf(Greenobject);
		if (!successors.isEmpty()) {
			final Greenobject next = successors.iterator().next();
			selectionModel.clearSelection();
			selectionModel.addGreenobjectToSelection(next);
		}
	}

	/*
	 * STATIC METHODS
	 */

	/**
	 * Return a meaningful Greenobject from the current selection, or
	 * <code>null</code> if the selection is empty.
	 */
	private Greenobject getAGreenobject() {
		// Get it from Greenobject selection
		final Set<Greenobject> GreenobjectSelection = selectionModel.getGreenobjectSelection();
		if (!GreenobjectSelection.isEmpty()) {
			final Iterator<Greenobject> it = GreenobjectSelection.iterator();
			Greenobject Greenobject = it.next();
			int minFrame = Greenobject.getFeature(Greenobject.POSITION_T).intValue();
			while (it.hasNext()) {
				final Greenobject s = it.next();
				final int frame = s.getFeature(Greenobject.POSITION_T).intValue();
				if (frame < minFrame) {
					minFrame = frame;
					Greenobject = s;
				}
			}
			return Greenobject;
		}

		// Nope? Then get it from edges
		final Set<DefaultWeightedEdge> edgeSelection = selectionModel.getEdgeSelection();
		if (!edgeSelection.isEmpty()) {
			final Iterator<DefaultWeightedEdge> it = edgeSelection.iterator();
			final DefaultWeightedEdge edge = it.next();
			Greenobject Greenobject = model.getTrackModel().getEdgeSource(edge);
			int minFrame = Greenobject.getFeature(Greenobject.POSITION_T).intValue();
			while (it.hasNext()) {
				final DefaultWeightedEdge e = it.next();
				final Greenobject s = model.getTrackModel().getEdgeSource(e);
				final int frame = s.getFeature(Greenobject.POSITION_T).intValue();
				if (frame < minFrame) {
					minFrame = frame;
					Greenobject = s;
				}
			}
			return Greenobject;
		}

		// Still nothing? Then give up.
		return null;
	}
}
