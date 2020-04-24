package Buddy.plugin.trackmate.tracking.kdtree;

import static Buddy.plugin.trackmate.io.IOUtils.readDoubleAttribute;
import static Buddy.plugin.trackmate.io.IOUtils.writeAttribute;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.DEFAULT_LINKING_MAX_DISTANCE;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_LINKING_MAX_DISTANCE;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.BCellobjectCollection;
import Buddy.plugin.trackmate.gui.ConfigurationPanel;
import Buddy.plugin.trackmate.gui.panels.tracker.NearestNeighborTrackerSettingsPanel;
import Buddy.plugin.trackmate.tracking.BCellobjectTracker;
import Buddy.plugin.trackmate.tracking.BCellobjectTrackerFactory;
import pluginTools.InteractiveBud;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.jdom2.Element;
import org.scijava.plugin.Plugin;

@Plugin(type = BCellobjectTrackerFactory.class, priority = 1d)
public class NearestNeighborTrackerFactory implements BCellobjectTrackerFactory {
	public static final String TRACKER_KEY = "NEAREST_NEIGHBOR_TRACKER";

	public static final String NAME = "Nearest neighbor search";

	public static final String INFO_TEXT = "<html>"
			+ "This tracker is the most simple one, and is based on nearest neighbor <br>"
			+ "search. The BCellobjects in the target frame are searched for the nearest neighbor <br> "
			+ "of each BCellobject in the source frame. If the BCellobjects found are closer than the <br>"
			+ "maximal allowed distance, a link between the two is created. <br>" + "<p>"
			+ "The nearest neighbor search relies upon the KD-tree technique implemented <br>"
			+ "in imglib by Johannes Schindelin and friends. This ensure a very efficient "
			+ "tracking and makes this tracker suitable for situation where a huge number <br>"
			+ "of particles are to be tracked over a very large number of frames. However, <br>"
			+ "because of the naiveness of its principles, it can result in pathological <br>"
			+ "tracks. It can only do frame-to-frame linking; there cannot be any track <br>"
			+ "merging or splitting, and gaps will not be closed. Also, the end results are non-" + "deterministic."
			+ " </html>";

	private String errorMessage;

	@Override
	public String getInfoText() {
		return INFO_TEXT;
	}

	@Override
	public ImageIcon getIcon() {
		return null;
	}

	@Override
	public String getKey() {
		return TRACKER_KEY;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public BCellobjectTracker create(final InteractiveBud parent, final Map<String, Object> settings) {
		return new NearestNeighborTracker(parent, settings);
	}

	@Override
	public ConfigurationPanel getTrackerConfigurationPanel(final Model model) {
		final String spaceUnits = model.getSpaceUnits();
		return new NearestNeighborTrackerSettingsPanel(NAME, INFO_TEXT, spaceUnits);
	}

	@Override
	public boolean marshall(final Map<String, Object> settings, final Element element) {
		final StringBuilder str = new StringBuilder();
		final boolean ok = writeAttribute(settings, element, KEY_LINKING_MAX_DISTANCE, Double.class, str);
		if (!ok) {
			errorMessage = str.toString();
		}
		return ok;
	}

	@Override
	public boolean unmarshall(final Element element, final Map<String, Object> settings) {
		settings.clear();
		final StringBuilder errorHolder = new StringBuilder();
		final boolean ok = readDoubleAttribute(element, settings, KEY_LINKING_MAX_DISTANCE, errorHolder);
		if (!ok) {
			errorMessage = errorHolder.toString();
		}
		return ok;
	}

	@Override
	public String toString(final Map<String, Object> sm) {
		return String.format("  Max distance: %.1f\n", (Double) sm.get(KEY_LINKING_MAX_DISTANCE));
	}

	@Override
	public Map<String, Object> getDefaultSettings() {
		final Map<String, Object> settings = new HashMap<>();
		settings.put(KEY_LINKING_MAX_DISTANCE, DEFAULT_LINKING_MAX_DISTANCE);
		return settings;
	}

	@Override
	public boolean checkSettingsValidity(final Map<String, Object> settings) {
		final StringBuilder str = new StringBuilder();
		final boolean ok = NearestNeighborTracker.checkInput(settings, str);
		if (!ok) {
			errorMessage = str.toString();
		}
		return ok;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

}
