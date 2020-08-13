package Buddy.plugin.trackmate.visualization.threedviewer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.scijava.java3d.BadTransformException;
import org.scijava.vecmath.Color3f;
import org.scijava.vecmath.Color4f;
import org.scijava.vecmath.Point4d;

import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenModelChangeEvent;
import Buddy.plugin.trackmate.GreenSelectionChangeEvent;
import Buddy.plugin.trackmate.GreenSelectionModel;
import Buddy.plugin.trackmate.GreenobjectCollection;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.ModelChangeEvent;
import Buddy.plugin.trackmate.SelectionChangeEvent;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.util.GreenTMUtils;
import Buddy.plugin.trackmate.util.TMUtils;
import Buddy.plugin.trackmate.visualization.AbstractTrackMateModelView;
import Buddy.plugin.trackmate.visualization.FeatureColorGenerator;
import Buddy.plugin.trackmate.visualization.GreenAbstractTrackMateModelView;
import Buddy.plugin.trackmate.visualization.TrackColorGenerator;
import Buddy.plugin.trackmate.visualization.TrackMateModelView;
import greenDetector.Greenobject;
import ij3d.Content;
import ij3d.ContentInstant;
import ij3d.Image3DUniverse;

public class GreenobjectDisplayer3D extends GreenAbstractTrackMateModelView {

	static final String KEY = "3DVIEWER";

	public static final int DEFAULT_RESAMPLING_FACTOR = 4;

	// public static final int DEFAULT_THRESHOLD = 50;

	private static final boolean DEBUG = false;

	private static final String TRACK_CONTENT_NAME = "Tracks";

	private static final String Greenobject_CONTENT_NAME = "Greenobjects";

	private TreeMap<Integer, GreenobjectGroupNode<Greenobject>> blobs;

	private GreenTrackDisplayNode trackNode;

	private Content GreenobjectContent;

	private Content trackContent;

	private final Image3DUniverse universe;

	// For highlighting
	private ArrayList<Greenobject> previousGreenobjectHighlight;

	private HashMap<Greenobject, Color3f> previousColorHighlight;

	private HashMap<Greenobject, Integer> previousFrameHighlight;

	private TreeMap<Integer, ContentInstant> contentAllFrames;

	public GreenobjectDisplayer3D(final GreenModel model, final GreenSelectionModel selectionModel,
			final Image3DUniverse universe) {
		super(model, selectionModel);
		this.universe = universe;
		setModel(model);
	}

	/*
	 * OVERRIDDEN METHODS
	 */

	@Override
	public void modelChanged(final GreenModelChangeEvent event) {
		if (DEBUG) {
			System.out.println("[GreenobjectDisplayer3D: modelChanged() called with event ID: " + event.getEventID());
			System.out.println(event);
		}

		switch (event.getEventID()) {

		case GreenModelChangeEvent.Greenobject_COMPUTED:
			makeGreenobjectContent();
			break;

		case GreenModelChangeEvent.Greenobject_FILTERED:
			for (final int frame : blobs.keySet()) {
				final GreenobjectGroupNode<Greenobject> frameBlobs = blobs.get(frame);
				for (final Iterator<Greenobject> it = model.getGreenobjects().iterator(frame); it.hasNext();) {
					final Greenobject Greenobject = it.next();
					final boolean visible = true;
					frameBlobs.setVisible(Greenobject, visible);
				}
			}
			break;

		case GreenModelChangeEvent.TRACKS_COMPUTED:
			trackContent = makeTrackContent();
			universe.removeContent(TRACK_CONTENT_NAME);
			universe.addContent(trackContent);
			break;

		case GreenModelChangeEvent.TRACKS_VISIBILITY_CHANGED:
			updateTrackColors();
			trackNode.setTrackVisible(model.getTrackModel().trackIDs(true));
			break;

		case GreenModelChangeEvent.MODEL_MODIFIED: {
			/*
			 * We do not do anything. I could not find a good way to dynamically change the
			 * content of a 3D viewer content. So the 3D viewer just shows a snapshot of the
			 * TrackMate model when it was launched, and is not kept in sync with
			 * modifications afterwards.
			 */
			break;
		}

		default: {
			System.err.println("[GreenobjectDisplayer3D] Unknown event ID: " + event.getEventID());
		}
		}
	}

	@Override
	public void selectionChanged(final GreenSelectionChangeEvent event) {
		// Highlight edges.
		trackNode.setSelection(selectionModel.getEdgeSelection());
		trackNode.refresh();
		// Highlight Greenobjects.
		displayGreenobjectSelection((Integer) displaySettings
				.get(KEY_TRACK_DISPLAY_MODE) == TrackMateModelView.TRACK_DISPLAY_MODE_SELECTION_ONLY);
		// Center on last Greenobject
		super.selectionChanged(event);
	}

	@Override
	public void centerViewOn(final Greenobject Greenobject) {
		final int frame = Greenobject.getFeature(Greenobject.POSITION_T).intValue();
		universe.showTimepoint(frame);
	}

	@Override
	public void refresh() {
		if (null != trackNode)
			trackNode.refresh();
	}

	@Override
	public void render() {
		if (DEBUG)
			System.out.println("[GreenobjectDisplayer3D] Call to render().");

		updateRadiuses();
		updateGreenobjectColors();
		GreenobjectContent.setVisible((Boolean) displaySettings.get(KEY_GreenobjectS_VISIBLE));
		if (null != trackContent) {
			trackContent.setVisible((Boolean) displaySettings.get(KEY_TRACKS_VISIBLE));
			trackNode.setTrackDisplayMode((Integer) displaySettings.get(KEY_TRACK_DISPLAY_MODE));
			trackNode.setTrackDisplayDepth((Integer) displaySettings.get(KEY_TRACK_DISPLAY_DEPTH));
			updateTrackColors();
			trackNode.refresh();
			universe.updateStartAndEndTime(blobs.firstKey(), blobs.lastKey());
			universe.updateTimelineGUI();
		}
	}

	@Override
	public void setDisplaySettings(final String key, final Object value) {
		super.setDisplaySettings(key, value);
		// Treat change of radius
		if (key == KEY_Greenobject_RADIUS_RATIO) {
			updateRadiuses();
		} else if (key == KEY_Greenobject_COLORING) {
			updateGreenobjectColors();
		} else if (key == KEY_TRACK_COLORING) {
			updateTrackColors();
		} else if (key == KEY_DISPLAY_Greenobject_NAMES) {
			for (final int frame : blobs.keySet()) {
				blobs.get(frame).setShowLabels((Boolean) value);
			}
		} else if (key == KEY_GreenobjectS_VISIBLE) {
			GreenobjectContent.setVisible((Boolean) value);
		} else if (key == KEY_TRACKS_VISIBLE && null != trackContent) {
			trackContent.setVisible((Boolean) value);
		} else if (key == KEY_TRACK_DISPLAY_MODE && null != trackNode) {
			trackNode.setTrackDisplayMode((Integer) value);
			displayGreenobjectSelection((Integer) value == TrackMateModelView.TRACK_DISPLAY_MODE_SELECTION_ONLY);
		} else if (key == KEY_TRACK_DISPLAY_DEPTH && null != trackNode) {
			trackNode.setTrackDisplayDepth((Integer) value);
		}
	}

	@Override
	public void clear() {
		universe.removeContent(Greenobject_CONTENT_NAME);
		universe.removeContent(TRACK_CONTENT_NAME);
	}

	/*
	 * PRIVATE METHODS
	 */

	private void setModel(final GreenModel model) {
		if (model.getGreenobjects() != null) {
			makeGreenobjectContent();
		}
		if (model.getTrackModel().nTracks(true) > 0) {
			trackContent = makeTrackContent();
			universe.removeContent(TRACK_CONTENT_NAME);
			universe.addContentLater(trackContent);
		}
	}

	private Content makeTrackContent() {
		// Prepare tracks instant
		trackNode = new GreenTrackDisplayNode(model);
		universe.addTimelapseListener(trackNode);

		// Pass tracks instant to all instants
		final TreeMap<Integer, ContentInstant> instants = new TreeMap<>();
		final ContentInstant trackCI = new ContentInstant("Tracks_all_frames");
		trackCI.display(trackNode);
		instants.put(0, trackCI);
		final Content tc = new Content(TRACK_CONTENT_NAME, instants);
		tc.setShowAllTimepoints(true);
		tc.showCoordinateSystem(false);
		return tc;
	}

	private void makeGreenobjectContent() {

		blobs = new TreeMap<>();
		contentAllFrames = new TreeMap<>();
		final double radiusRatio = (Double) displaySettings.get(KEY_Greenobject_RADIUS_RATIO);
		final GreenobjectCollection Greenobjects = model.getGreenobjects();
		@SuppressWarnings("unchecked")
		final FeatureColorGenerator<Greenobject> GreenobjectColorGenerator = (FeatureColorGenerator<Greenobject>) displaySettings
				.get(KEY_Greenobject_COLORING);

		for (final int frame : Greenobjects.keySet()) {
			if (Greenobjects.getNGreenobjects(frame) == 0) {
				continue; // Do not create content for empty frames
			}
			buildFrameContent(Greenobjects, frame, radiusRatio, GreenobjectColorGenerator);
		}

		GreenobjectContent = new Content(Greenobject_CONTENT_NAME, contentAllFrames);
		GreenobjectContent.showCoordinateSystem(false);
		universe.removeContent(Greenobject_CONTENT_NAME);
		universe.addContentLater(GreenobjectContent);
	}

	private void buildFrameContent(final GreenobjectCollection Greenobjects, final Integer frame,
			final double radiusRatio, final FeatureColorGenerator<Greenobject> GreenobjectColorGenerator) {
		final Map<Greenobject, Point4d> centers = new HashMap<>(Greenobjects.getNGreenobjects(frame));
		final Map<Greenobject, Color4f> colors = new HashMap<>(Greenobjects.getNGreenobjects(frame));
		final double[] coords = new double[3];

		for (final Iterator<Greenobject> it = Greenobjects.iterator(frame); it.hasNext();) {
			final Greenobject Greenobject = it.next();
			GreenTMUtils.localize(Greenobject, coords);
			final Double radius = Greenobject.getFeature(Greenobject.RADIUS);
			final double[] pos = new double[] { coords[0], coords[1], coords[2], radius * radiusRatio };
			centers.put(Greenobject, new Point4d(pos));
			final Color4f col = new Color4f(GreenobjectColorGenerator.color(Greenobject));
			col.w = 0f;
			colors.put(Greenobject, col);
		}
		final GreenobjectGroupNode<Greenobject> blobGroup = new GreenobjectGroupNode<>(centers, colors);
		final ContentInstant contentThisFrame = new ContentInstant("Greenobjects_frame_" + frame);

		try {
			contentThisFrame.display(blobGroup);
		} catch (final BadTransformException bte) {
			System.err.println("Bad content for frame " + frame + ". Generated an exception:\n"
					+ bte.getLocalizedMessage() + "\nContent was:\n" + blobGroup.toString());
		}

		// Set visibility:
		if (Greenobjects.getNGreenobjects(frame) > 0) {
			blobGroup.setVisible(Greenobjects.iterable(frame, true));
		}

		contentAllFrames.put(frame, contentThisFrame);
		blobs.put(frame, blobGroup);
	}

	private void updateRadiuses() {
		final double radiusRatio = (Double) displaySettings.get(KEY_Greenobject_RADIUS_RATIO);

		for (final int frame : blobs.keySet()) {
			final GreenobjectGroupNode<Greenobject> GreenobjectGroup = blobs.get(frame);
			for (final Iterator<Greenobject> iterator = model.getGreenobjects().iterator(frame); iterator.hasNext();) {
				final Greenobject Greenobject = iterator.next();
				GreenobjectGroup.setRadius(Greenobject, radiusRatio * Greenobject.getFeature(Greenobject.RADIUS));
			}
		}
	}

	private void updateGreenobjectColors() {
		@SuppressWarnings("unchecked")
		final FeatureColorGenerator<Greenobject> GreenobjectColorGenerator = (FeatureColorGenerator<Greenobject>) displaySettings
				.get(KEY_Greenobject_COLORING);

		for (final int frame : blobs.keySet()) {
			final GreenobjectGroupNode<Greenobject> GreenobjectGroup = blobs.get(frame);
			for (final Iterator<Greenobject> iterator = model.getGreenobjects().iterator(frame); iterator.hasNext();) {
				final Greenobject Greenobject = iterator.next();
				GreenobjectGroup.setColor(Greenobject, new Color3f(GreenobjectColorGenerator.color(Greenobject)));
			}
		}
	}

	private void updateTrackColors() {
		final TrackColorGenerator colorGenerator = (TrackColorGenerator) displaySettings.get(KEY_TRACK_COLORING);

		for (final Integer trackID : model.getTrackModel().trackIDs(true)) {
			colorGenerator.setCurrentTrackID(trackID);
			for (final DefaultWeightedEdge edge : model.getTrackModel().trackEdges(trackID)) {
				final Color color = colorGenerator.color(edge);
				trackNode.setColor(edge, color);
			}
		}
	}

	private void highlightGreenobjects(final Collection<Greenobject> Greenobjects) {
		// Restore previous display settings for previously highlighted Greenobject
		if (null != previousGreenobjectHighlight)
			for (final Greenobject Greenobject : previousGreenobjectHighlight) {
				final Integer frame = previousFrameHighlight.get(Greenobject);
				if (null != frame) {
					final GreenobjectGroupNode<Greenobject> GreenobjectGroupNode = blobs.get(frame);
					if (null != GreenobjectGroupNode) {
						GreenobjectGroupNode.setColor(Greenobject, previousColorHighlight.get(Greenobject));
					}
				}
			}

		/*
		 * Don't color Greenobject selection in the highlight color if we are displaying
		 * selection only.
		 */
		final Integer trackDisplayMode = (Integer) displaySettings.get(KEY_TRACK_DISPLAY_MODE);
		if (trackDisplayMode == TrackMateModelView.TRACK_DISPLAY_MODE_SELECTION_ONLY)
			return;

		/*
		 * Store previous color value and color the Greenobject selection with the
		 * highlight color.
		 */

		previousGreenobjectHighlight = new ArrayList<>(Greenobjects.size());
		previousColorHighlight = new HashMap<>(Greenobjects.size());
		previousFrameHighlight = new HashMap<>(Greenobjects.size());

		final Color3f highlightColor = new Color3f((Color) displaySettings.get(KEY_HIGHLIGHT_COLOR));
		for (final Greenobject Greenobject : Greenobjects) {
			final int frame = Greenobject.getFeature(Greenobject.POSITION_T).intValue();
			// Store current settings
			previousGreenobjectHighlight.add(Greenobject);
			final GreenobjectGroupNode<Greenobject> GreenobjectGroupNode = blobs.get(frame);
			if (null != GreenobjectGroupNode) {
				previousColorHighlight.put(Greenobject, GreenobjectGroupNode.getColor3f(Greenobject));
				previousFrameHighlight.put(Greenobject, frame);
				// Update target Greenobject display
				blobs.get(frame).setColor(Greenobject, highlightColor);
			}
		}
	}

	/**
	 * Changes the visibility of the displayed Greenobject.
	 *
	 * @param onlyGreenobjectSelection
	 *            If <code>true</code>, we display on the Greenobjects in the
	 *            selection. Otherwise we display all Greenobjects marked as
	 *            visible.
	 */
	private void displayGreenobjectSelection(final boolean onlyGreenobjectSelection) {
		final Set<Greenobject> GreenobjectSelection = selectionModel.getGreenobjectSelection();
		if (onlyGreenobjectSelection) {
			if (GreenobjectSelection.isEmpty()) {
				for (final Integer frame : blobs.keySet()) {
					blobs.get(frame).setVisible(false);
				}
				return;
			}

			// Sort Greenobjects in selection per frame.
			final HashMap<Integer, ArrayList<Greenobject>> GreenobjectsPerFrame = new HashMap<>(blobs.size());
			for (final Integer frame : blobs.keySet()) {
				final ArrayList<Greenobject> Greenobjects = new ArrayList<>();
				GreenobjectsPerFrame.put(frame, Greenobjects);
			}

			for (final Greenobject Greenobject : GreenobjectSelection) {
				final int frame = Greenobject.getFeature(Greenobject.POSITION_T).intValue();
				final ArrayList<Greenobject> Greenobjects = GreenobjectsPerFrame.get(Integer.valueOf(frame));
				Greenobjects.add(Greenobject);
			}

			// Mark then as visible, the others as invisible.
			for (final Integer frame : GreenobjectsPerFrame.keySet()) {
				blobs.get(frame).setVisible(GreenobjectsPerFrame.get(frame));
			}

			// Restore proper color.
			updateGreenobjectColors();
			updateTrackColors();
		} else {
			// Make all visible Greenobjects visible here.
			for (final int frame : blobs.keySet()) {
				final Iterable<Greenobject> Greenobjects = model.getGreenobjects().iterable(frame, true);
				blobs.get(frame).setVisible(Greenobjects);
			}
			highlightGreenobjects(GreenobjectSelection);
		}
	}

	@Override
	public String getKey() {
		return KEY;
	}
}
