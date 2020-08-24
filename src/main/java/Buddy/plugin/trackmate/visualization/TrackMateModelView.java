package Buddy.plugin.trackmate.visualization;

import java.awt.Color;
import java.util.Map;

import org.jfree.chart.renderer.InterpolatePaintScale;

import Buddy.plugin.trackmate.Model;
import budDetector.BCellobject;

public interface TrackMateModelView
{

	/*
	 * KEY-VALUE CONSTANTS FOR LOOK & FEEL CUSTOMIZATION
	 */

	/*
	 * KEYS
	 */

	/**
	 * Defines the key for the main color. Accepted values are color.
	 */
	public static final String KEY_COLOR = "Color";

	/**
	 * Defines the key for the highlight color, used to paint selection.
	 * Accepted values are color.
	 */
	public static final String KEY_HIGHLIGHT_COLOR = "HighlightColor";

	/**
	 * Defines the key for the track display mode. Possible values are
	 * {@link #TRACK_DISPLAY_MODE_WHOLE}, {@link #TRACK_DISPLAY_MODE_LOCAL},
	 * {@link #TRACK_DISPLAY_MODE_LOCAL_BACKWARD},
	 * {@value #TRACK_DISPLAY_MODE_LOCAL_FORWARD},
	 * {@value #TRACK_DISPLAY_MODE_SELECTION_ONLY}.
	 */
	public static final String KEY_TRACK_DISPLAY_MODE = "TrackDisplaymode";

	/**
	 * Defines the key for the track display depth. Values are integer, and they
	 * defines how many frames away the track can be seen from the current
	 * time-point.
	 */
	public static final String KEY_TRACK_DISPLAY_DEPTH = "TrackDisplayDepth";

	/**
	 * Defines the key for the track visibility. Values are boolean. If
	 * <code>false</code>, tracks are not visible.
	 */
	public static final String KEY_TRACKS_VISIBLE = "TracksVisible";

	/**
	 * Defines the key for the track coloring method. Values are concrete
	 * implementations of {@link TrackColorGenerator}.
	 */
	public static final String KEY_TRACK_COLORING = "TrackColoring";

	/**
	 * Defines the key for the BCellobject visibility. Values are boolean. If
	 * <code>false</code>, BCellobjects are not visible.
	 */
	public static final String KEY_BCellobjectS_VISIBLE = "BCellobjectsVisible";

	/**
	 * Defines the key for the BCellobject name display. Values are boolean. If
	 * <code>false</code>, BCellobject names are not visible.
	 */
	public static final String KEY_DISPLAY_BCellobject_NAMES = "DisplayBCellobjectNames";

	/**
	 * Defines the key for the BCellobject radius ratio. Value should be a positive
	 * {@link Double} object. BCellobjects will be rendered with a radius equals to
	 * their actual radius multiplied by this ratio.
	 */
	public static final String KEY_BCellobject_RADIUS_RATIO = "BCellobjectRadiusRatio";

	/**
	 * Defines the key for the BCellobject coloring method. Accepted values are
	 * implementation of {@link FeatureColorGenerator}
	 */
	public static final String KEY_BCellobject_COLORING = "BCellobjectColoring";

	/**
	 * Defines the key for the color map to use for painting overlay. Acceptable
	 * values are {@link InterpolatePaintScale}s, the default is
	 * {@link InterpolatePaintScale#Jet}.
	 */
	public static final String KEY_COLORMAP = "ColorMap";

	/**
	 * Defines the key for the drawing depth, for views that can use this
	 * settings. The draw depth is the max distance between the view plane and
	 * any object for this object to be drawn. Accepted values are
	 * <code>double</code>. The values must be specified in image units.
	 */
	public static final String KEY_DRAWING_DEPTH = "DrawingDepth";

	/**
	 * Defines the key for limiting the drawing depth. Values are boolean. If
	 * <code>true</code>, drawing depth will be limited by the values specified
	 * by {@link #KEY_DRAWING_DEPTH}.
	 */
	public static final String KEY_LIMIT_DRAWING_DEPTH = "LimitDrawingDepth";

	/*
	 * VALUES
	 */

	/**
	 * Track display mode where the whole tracks are drawn, ignoring the value
	 * of {@link #KEY_TRACK_DISPLAY_DEPTH}.
	 */
	public static final int TRACK_DISPLAY_MODE_WHOLE = 0;

	/**
	 * Track display mode where the only part of the tracks close to the current
	 * time-point are drawn backward and forward. Edges away from current time
	 * point are faded in the background. How much can be seen is defined by the
	 * value of {@link #KEY_TRACK_DISPLAY_DEPTH}.
	 */
	public static final int TRACK_DISPLAY_MODE_LOCAL = 1;

	/**
	 * Track display mode where the only part of the tracks close to the current
	 * time-point are drawn, backward only. How much can be seen is defined by
	 * the value of {@link #KEY_TRACK_DISPLAY_DEPTH}.
	 */
	public static final int TRACK_DISPLAY_MODE_LOCAL_BACKWARD = 2;

	/**
	 * Track display mode where the only part of the tracks close to the current
	 * time-point are drawn, forward only. How much can be seen is defined by
	 * the value of {@link #KEY_TRACK_DISPLAY_DEPTH}.
	 */
	public static final int TRACK_DISPLAY_MODE_LOCAL_FORWARD = 3;

	/**
	 * Track display mode similar to {@link #TRACK_DISPLAY_MODE_LOCAL}, except
	 * that for the sake of speed, edges are not faded.
	 */
	public static final int TRACK_DISPLAY_MODE_LOCAL_QUICK = 4;

	/**
	 * Track display mode similar to {@link #TRACK_DISPLAY_MODE_LOCAL_BACKWARD},
	 * except that for the sake of speed, edges are not faded.
	 */
	public static final int TRACK_DISPLAY_MODE_LOCAL_BACKWARD_QUICK = 5;

	/**
	 * Track display mode similar to {@link #TRACK_DISPLAY_MODE_LOCAL_FORWARD},
	 * except that for the sake of speed, edges are not faded.
	 */
	public static final int TRACK_DISPLAY_MODE_LOCAL_FORWARD_QUICK = 6;

	/**
	 * Track display mode where only the content of the current selection is
	 * displayed.
	 */
	public static final int TRACK_DISPLAY_MODE_SELECTION_ONLY = 7;

	/*
	 * DESCRIPTIONS
	 */

	/**
	 * String that describe the corresponding track display mode.
	 */
	public static final String[] TRACK_DISPLAY_MODE_DESCRIPTION = new String[] {
			"Show all entire tracks",
			"Show local tracks",
			"Show local tracks, backward",
			"Show local tracks, forward",
			"Local tracks (fast)",
			"Local tracks, backward (fast)",
			"Local tracks, forward (fast)",
			"Show selection only"
	};

	/*
	 * DEFAULTS
	 */

	/**
	 * The default color for BCellobjects.
	 */
	public static final Color DEFAULT_BCellobject_COLOR = new Color( 1f, 0, 1f );

	/**
	 * The default track color.
	 */
	public static final Color DEFAULT_TRACK_COLOR = new Color( 250, 250, 0 );

	/**
	 * The color to use to paint objects for which a feature is undefined.
	 * <i>E.g.</i> the numerical feature for this object calculated
	 * automatically, but its returned value is {@link Double#NaN}.
	 */
	public static final Color DEFAULT_UNDEFINED_FEATURE_COLOR = Color.BLACK;

	/**
	 * The color to use to paint objects with a manual feature that has not been
	 * assigned yet. <i>E.g</i> the coloring uses a manual feature, but this
	 * object did not receive a value yet.
	 */
	public static final Color DEFAULT_UNASSIGNED_FEATURE_COLOR = Color.GRAY.darker();

	/**
	 * The default color for highlighting.
	 */
	public static final Color DEFAULT_HIGHLIGHT_COLOR = new Color( 0, 1f, 0 );

	/**
	 * The default track display mode.
	 */
	public static final int DEFAULT_TRACK_DISPLAY_MODE = TRACK_DISPLAY_MODE_WHOLE;

	/**
	 * The default track display mode.
	 */
	public static final int DEFAULT_TRACK_DISPLAY_DEPTH = 10;

	/**
	 * The default color map.
	 *
	 * @deprecated replaced by configurable {@code InterpolatePaintScale}
	 *             provided by {@code TrackMateOptions}
	 */
	@Deprecated
	public static final InterpolatePaintScale DEFAULT_COLOR_MAP = InterpolatePaintScale.Viridis;

	/**
	 * The default drawing depth, in image units.
	 */
	public static final double DEFAULT_DRAWING_DEPTH = 10d;

	/**
	 * The default drawing depth limitation mode..
	 */
	public static final boolean DEFAULT_LIMIT_DRAWING_DEPTH = false;

	/*
	 * INTERFACE METHODS
	 */

	/**
	 * Initializes this displayer and render it according to its concrete
	 * implementation.
	 */
	public void render();

	/**
	 * Refreshes the displayer display with current model. If the underlying
	 * model was modified, or the display settings were changed, calling this
	 * method should be enough to update the display with changes.
	 *
	 * @see #setDisplaySettings(String, Object)
	 */
	public void refresh();

	/**
	 * Removes any overlay (for BCellobjects or tracks) from this displayer.
	 */
	public void clear();

	/**
	 * Centers the view on the given BCellobject.
	 */
	public void centerViewOn( final BCellobject BCellobject );

	/**
	 * Returns the current display settings map.
	 */
	public Map< String, Object > getDisplaySettings();

	/**
	 * Set a display parameter.
	 *
	 * @param key
	 *            the key of the parameter to change.
	 * @param value
	 *            the value for the display parameter
	 */
	public void setDisplaySettings( final String key, final Object value );

	/**
	 * Returns the value of a specific display parameter.
	 */
	public Object getDisplaySettings( final String key );

	/**
	 * Returns the model displayed in this view.
	 */
	public Model getModel();

	/**
	 * Returns the unique key that identifies this view.
	 * <p>
	 * Careful: this key <b>must</b> be the same that for the
	 * {@link ViewFactory} that can instantiates this view. This is to
	 * facilitate saving/loading.
	 *
	 * @return the key, as a String.
	 */
	public String getKey();

}
