package Buddy.plugin.trackmate.visualization;

import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenModelChangeListener;
import Buddy.plugin.trackmate.GreenSelectionChangeEvent;
import Buddy.plugin.trackmate.GreenSelectionChangeListener;
import Buddy.plugin.trackmate.GreenSelectionModel;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.ModelChangeListener;
import Buddy.plugin.trackmate.SelectionChangeEvent;
import Buddy.plugin.trackmate.SelectionChangeListener;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.TrackMateOptionUtils;
import greenDetector.Greenobject;

import java.util.HashMap;
import java.util.Map;


/**
 * An abstract class for Greenobject displayers, that can overlay detected
 * Greenobjects and tracks on top of the image data.
 * <p>
 *
 * @author Jean-Yves Tinevez &lt;jeanyves.tinevez@gmail.com&gt; Jan 2011
 */
public abstract class GreenAbstractTrackMateModelView
		implements GreenSelectionChangeListener, GreenTrackMateModelView, GreenModelChangeListener {

	/*
	 * FIELDS
	 */

	/**
	 * A map of String/Object that configures the look and feel of the display.
	 */
	protected Map<String, Object> displaySettings;

	/** The model displayed by this class. */
	protected GreenModel model;

	protected final GreenSelectionModel selectionModel;

	/*
	 * PROTECTED CONSTRUCTOR
	 */

	protected GreenAbstractTrackMateModelView(final GreenModel model, final GreenSelectionModel selectionModel) {
		this.selectionModel = selectionModel;
		this.model = model;
		this.displaySettings = initDisplaySettings(model);
		model.addModelChangeListener(this);
		selectionModel.addSelectionChangeListener(this);
	}

	/*
	 * PUBLIC METHODS
	 */

	@Override
	public void setDisplaySettings(final String key, final Object value) {
		displaySettings.put(key, value);
	}

	@Override
	public Object getDisplaySettings(final String key) {
		return displaySettings.get(key);
	}

	@Override
	public Map<String, Object> getDisplaySettings() {
		return displaySettings;
	}

	/**
	 * This needs to be overriden for concrete implementation to display selection.
	 */
	@Override
	public void selectionChanged(final GreenSelectionChangeEvent event) {
		// Center on selection if we added one Greenobject exactly
		final Map<Greenobject, Boolean> GreenobjectsAdded = event.getGreenobjects();
		if (GreenobjectsAdded != null && GreenobjectsAdded.size() == 1) {
			final boolean added = GreenobjectsAdded.values().iterator().next();
			if (added) {
				final Greenobject Greenobject = GreenobjectsAdded.keySet().iterator().next();
				centerViewOn(Greenobject);
			}
		}
	}

	@Override
	public GreenModel getModel() {
		return model;
	}

	/**
	 * Provides default display settings.
	 *
	 * @param lModel
	 *            the model this view operate on. Needed for some display settings.
	 */
	protected Map<String, Object> initDisplaySettings(final GreenModel lModel) {
		final Map<String, Object> lDisplaySettings = new HashMap<>(11);
		lDisplaySettings.put(KEY_COLOR, DEFAULT_Greenobject_COLOR);
		lDisplaySettings.put(KEY_HIGHLIGHT_COLOR, DEFAULT_HIGHLIGHT_COLOR);
		lDisplaySettings.put(KEY_DISPLAY_Greenobject_NAMES, false);
		lDisplaySettings.put(KEY_Greenobject_COLORING, new DummyGreenobjectColorGenerator());
		lDisplaySettings.put(KEY_Greenobject_RADIUS_RATIO, 1.0d);
		lDisplaySettings.put(KEY_TRACKS_VISIBLE, true);
		lDisplaySettings.put(KEY_TRACK_DISPLAY_MODE, DEFAULT_TRACK_DISPLAY_MODE);
		lDisplaySettings.put(KEY_TRACK_DISPLAY_DEPTH, DEFAULT_TRACK_DISPLAY_DEPTH);
		lDisplaySettings.put(KEY_TRACK_COLORING, new DummyTrackColorGenerator());
		lDisplaySettings.put(KEY_COLORMAP, TrackMateOptionUtils.getOptions().getPaintScale());
		lDisplaySettings.put(KEY_LIMIT_DRAWING_DEPTH, DEFAULT_LIMIT_DRAWING_DEPTH);
		lDisplaySettings.put(KEY_DRAWING_DEPTH, DEFAULT_DRAWING_DEPTH);
		return lDisplaySettings;
	}

}
