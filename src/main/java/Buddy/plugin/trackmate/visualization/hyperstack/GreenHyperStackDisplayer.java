package Buddy.plugin.trackmate.visualization.hyperstack;

import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenModelChangeEvent;
import Buddy.plugin.trackmate.GreenSelectionChangeEvent;
import Buddy.plugin.trackmate.GreenSelectionModel;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.ModelChangeEvent;
import Buddy.plugin.trackmate.SelectionChangeEvent;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.visualization.AbstractTrackMateModelView;
import Buddy.plugin.trackmate.visualization.GreenAbstractTrackMateModelView;
import Buddy.plugin.trackmate.visualization.GreenTrackMateModelView;
import Buddy.plugin.trackmate.visualization.TrackColorGenerator;
import Buddy.plugin.trackmate.visualization.TrackMateModelView;
import Buddy.plugin.trackmate.visualization.ViewUtils;
import greenDetector.Greenobject;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;

public class GreenHyperStackDisplayer extends GreenAbstractTrackMateModelView {

	private static final boolean DEBUG = false;

	protected final ImagePlus imp;

	protected GreenobjectOverlay GreenobjectOverlay;

	protected GreenTrackOverlay trackOverlay;

	private GreenobjectEditTool editTool;

	private Roi initialROI;

	public static final String KEY = "HYPERSTACKDISPLAYER";

	/*
	 * CONSTRUCTORS
	 */

	public GreenHyperStackDisplayer(final GreenModel model, final GreenSelectionModel selectionModel, final ImagePlus imp) {
		super(model, selectionModel);
		if (null != imp) {
			this.imp = imp;
		} else {
			this.imp = ViewUtils.makeEmpytImagePlus(model);
		}
		this.GreenobjectOverlay = createGreenobjectOverlay();
		this.trackOverlay = createTrackOverlay();
	}

	public GreenHyperStackDisplayer(final GreenModel model, final GreenSelectionModel selectionModel) {
		this(model, selectionModel, null);
	}

	/*
	 * PROTECTED METHODS
	 */

	/**
	 * Hook for subclassers. Instantiate here the overlay you want to use for the
	 * Greenobjects.
	 *
	 * @return the Greenobject overlay
	 */
	protected GreenobjectOverlay createGreenobjectOverlay() {
		return new GreenobjectOverlay(model, imp, displaySettings);
	}

	/**
	 * Hook for subclassers. Instantiate here the overlay you want to use for the
	 * Greenobjects.
	 *
	 * @return the track overlay
	 */
	protected GreenTrackOverlay createTrackOverlay() {
		final GreenTrackOverlay to = new GreenTrackOverlay(model, imp, displaySettings);
		final TrackColorGenerator colorGenerator = (TrackColorGenerator) displaySettings.get(KEY_TRACK_COLORING);
		to.setTrackColorGenerator(colorGenerator);
		return to;
	}

	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Exposes the {@link ImagePlus} on which the model is drawn by this view.
	 *
	 * @return the ImagePlus used in this view.
	 */
	public ImagePlus getImp() {
		return imp;
	}

	@Override
	public void modelChanged(final GreenModelChangeEvent event) {
		if (DEBUG)
			System.out.println("[HyperStackDisplayer] Received model changed event ID: " + event.getEventID() + " from "
					+ event.getSource());
		boolean redoOverlay = false;

		switch (event.getEventID()) {

		case GreenModelChangeEvent.MODEL_MODIFIED:
			// Rebuild track overlay only if edges were added or removed, or if
			// at least one Greenobject was removed.
			final Set<DefaultWeightedEdge> edges = event.getEdges();
			if (edges != null && edges.size() > 0) {
				redoOverlay = true;
			}
			break;

		case GreenModelChangeEvent.Greenobject_FILTERED:
			redoOverlay = true;
			break;

		case GreenModelChangeEvent.Greenobject_COMPUTED:
			redoOverlay = true;
			break;

		case GreenModelChangeEvent.TRACKS_VISIBILITY_CHANGED:
		case GreenModelChangeEvent.TRACKS_COMPUTED:
			redoOverlay = true;
			break;
		}

		if (redoOverlay)
			refresh();
	}

	@Override
	public void selectionChanged(final GreenSelectionChangeEvent event) {
		// Highlight selection
		trackOverlay.setHighlight(selectionModel.getEdgeSelection());
		GreenobjectOverlay.setGreenobjectSelection(selectionModel.getGreenobjectSelection());
		// Center on last Greenobject
		super.selectionChanged(event);
		// Redraw
		imp.updateAndDraw();
	}

	@Override
	public void centerViewOn(final Greenobject Greenobject) {
		final int frame = Greenobject.getFeature(Greenobject.POSITION_T).intValue();
		final double dz = imp.getCalibration().pixelDepth;
		imp.setPosition(imp.getC(), 0, frame + 1);
	}

	@Override
	public void render() {
		initialROI = imp.getRoi();
		if (initialROI != null) {
			imp.killRoi();
		}

		clear();
		imp.setOpenAsHyperStack(true);
		if (!imp.isVisible()) {
			imp.show();
		}

		addOverlay(GreenobjectOverlay);
		addOverlay(trackOverlay);
		imp.updateAndDraw();
		registerEditTool();
	}

	@Override
	public void refresh() {
		if (null != imp) {
			imp.updateAndDraw();
		}
	}

	@Override
	public void clear() {
		Overlay overlay = imp.getOverlay();
		if (overlay == null) {
			overlay = new Overlay();
			imp.setOverlay(overlay);
		}
		overlay.clear();
		if (initialROI != null) {
			imp.getOverlay().add(initialROI);
		}
		refresh();
	}

	public void addOverlay(final Roi overlay) {
		imp.getOverlay().add(overlay);
	}

	public GreenSelectionModel getSelectionModel() {
		return selectionModel;
	}

	/*
	 * PRIVATE METHODS
	 */

	private void registerEditTool() {
		editTool = GreenobjectEditTool.getInstance();
		if (!GreenobjectEditTool.isLaunched()) {
			editTool.run("");
		}
		editTool.register(imp, this);
	}

	@Override
	public void setDisplaySettings(final String key, final Object value) {
		boolean dorefresh = false;

		if (key == GreenTrackMateModelView.KEY_Greenobject_COLORING || key == GreenTrackMateModelView.KEY_LIMIT_DRAWING_DEPTH
				|| key == KEY_DRAWING_DEPTH) {
			dorefresh = true;

		} else if (key == TrackMateModelView.KEY_TRACK_COLORING) {
			// pass the new one to the track overlay - we ignore its Greenobject
			// coloring and keep the Greenobject coloring
			final TrackColorGenerator colorGenerator = (TrackColorGenerator) value;
			trackOverlay.setTrackColorGenerator(colorGenerator);
			dorefresh = true;
		}

		super.setDisplaySettings(key, value);
		if (dorefresh) {
			refresh();
		}
	}

	@Override
	public String getKey() {
		return KEY;
	}

}
