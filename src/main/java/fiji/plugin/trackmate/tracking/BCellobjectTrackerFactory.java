package Buddy.plugin.trackmate.tracking;

import java.util.Map;

import org.jdom2.Element;

import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.BCellobjectCollection;
import Buddy.plugin.trackmate.TrackMateModule;
import Buddy.plugin.trackmate.gui.ConfigurationPanel;

public interface BCellobjectTrackerFactory extends TrackMateModule
{

	/**
	 * Instantiates and returns a new {@link BCellobjectTracker} configured to operate
	 * on the specified {@link BCellobjectCollection}, using the specified settins map.
	 *
	 * @param BCellobjects
	 *            the {@link BCellobjectCollection} containing the BCellobjects to track.
	 * @param settings
	 *            the settings map configuring the tracker.
	 * @return a new {@link BCellobjectTracker} instance.
	 */
	public BCellobjectTracker create( final BCellobjectCollection BCellobjects, final Map< String, Object > settings );

	/**
	 * Returns a new GUI panel able to configure the settings suitable for the
	 * target tracker identified by the key parameter.
	 *
	 * @param model
	 *            the model that will be modified by the target tracker.
	 * @return a new configuration panel.
	 */
	public ConfigurationPanel getTrackerConfigurationPanel( final Model model );

	/**
	 * Marshalls a settings map to a JDom element, ready for saving to XML. The
	 * element is <b>updated</b> with new attributes.
	 * <p>
	 * Only parameters specific to the concrete tracker factory are marshalled.
	 * The element also always receive an attribute named
	 * {@value TrackerKeys#XML_ATTRIBUTE_TRACKER_NAME} that saves the target
	 * {@link BCellobjectTracker} key.
	 *
	 * @return true if marshalling was successful. If not, check
	 *         {@link #getErrorMessage()}
	 */
	public boolean marshall( final Map< String, Object > settings, final Element element );

	/**
	 * Un-marshall a JDom element to update a settings map, and sets the target
	 * tracker of this provider from the element.
	 * <p>
	 * Concretely: the the specific settings map for the targeted tracker is
	 * updated from the element.
	 *
	 * @param element
	 *            the JDom element to read from.
	 * @param settings
	 *            the map to update. Is cleared prior to updating, so that it
	 *            contains only the parameters specific to the target tracker.
	 * @return true if unmarshalling was successful. If not, check
	 *         {@link #getErrorMessage()}
	 */
	public boolean unmarshall( final Element element, final Map< String, Object > settings );

	/**
	 * A utility method that builds a string representation of a settings map
	 * owing to the currently selected tracker in this provider.
	 *
	 * @param sm
	 *            the map to echo.
	 * @return a string representation of the map.
	 */
	public String toString( final Map< String, Object > sm );

	/**
	 * Returns a new default settings map suitable for the tracker Settings are
	 * instantiated with default values. is returned.
	 *
	 * @return a settings map.
	 */
	public Map< String, Object > getDefaultSettings();

	/**
	 * Checks the validity of the given settings map for the tracker. The
	 * validity check is strict: we check that all needed parameters are here
	 * and are of the right class, and that there is no extra unwanted
	 * parameters.
	 *
	 * @return true if the settings map can be used with the target factory. If
	 *         not, check {@link #getErrorMessage()}
	 */
	public boolean checkSettingsValidity( final Map< String, Object > settings );

	/**
	 * Returns a meaningful error message for the last action on this factory.
	 *
	 * @return an error message.
	 * @see #marshall(Map, Element)
	 * @see #unmarshall(Element, Map)
	 * @see #checkSettingsValidity(Map)
	 */
	public String getErrorMessage();
}
