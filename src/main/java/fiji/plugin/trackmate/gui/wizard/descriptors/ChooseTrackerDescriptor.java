package Buddy.plugin.trackmate.gui.wizard.descriptors;

import java.util.Map;

import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.components.ModuleChooserPanel;
import Buddy.plugin.trackmate.gui.wizard.WizardPanelDescriptor;
import Buddy.plugin.trackmate.providers.TrackerProvider;
import Buddy.plugin.trackmate.tracking.BCellobjectTrackerFactory;
import Buddy.plugin.trackmate.tracking.sparselap.SimpleSparseLAPTrackerFactory;

public class ChooseTrackerDescriptor extends WizardPanelDescriptor
{

	private static final String KEY = "ChooseTracker";

	private final TrackMate trackmate;

	private final TrackerProvider trackerProvider;

	public ChooseTrackerDescriptor( final TrackerProvider trackerProvider, final TrackMate trackmate )
	{
		super( KEY );
		this.trackmate = trackmate;
		this.trackerProvider = trackerProvider;

		String selectedTracker = SimpleSparseLAPTrackerFactory.THIS2_TRACKER_KEY; // default
		if ( null != trackmate.getSettings().trackerFactory )
			selectedTracker = trackmate.getSettings().trackerFactory.getKey();

		this.targetPanel = new ModuleChooserPanel<>( trackerProvider, "tracker", selectedTracker );
	}

	private void setCurrentChoiceFromPlugin()
	{
		String key = SimpleSparseLAPTrackerFactory.THIS2_TRACKER_KEY; // default
		if ( null != trackmate.getSettings().trackerFactory )
			key = trackmate.getSettings().trackerFactory.getKey();

		@SuppressWarnings( "unchecked" )
		final ModuleChooserPanel< BCellobjectTrackerFactory > component = (Buddy.plugin.trackmate.gui.components.ModuleChooserPanel< BCellobjectTrackerFactory > ) targetPanel;
		component.setSelectedModuleKey( key );
	}

	@Override
	public void displayingPanel()
	{
		setCurrentChoiceFromPlugin();
	}

	@Override
	public void aboutToHidePanel()
	{
		// Configure the detector provider with choice made in panel
		@SuppressWarnings( "unchecked" )
		final ModuleChooserPanel< BCellobjectTrackerFactory > component = (Buddy.plugin.trackmate.gui.components.ModuleChooserPanel< BCellobjectTrackerFactory > ) targetPanel;
		final String trackerKey = component.getSelectedModuleKey();

		// Configure trackmate settings with selected detector
		final BCellobjectTrackerFactory factory = trackerProvider.getFactory( trackerKey );

		if ( null == factory )
		{
			trackmate.getModel().getLogger().error( "[ChooseTrackerDescriptor] Cannot find tracker named "
					+ trackerKey
					+ " in current TrackMate modules." );
			return;
		}
		trackmate.getSettings().trackerFactory = factory;

		/*
		 * Compare current settings with default ones, and substitute default
		 * ones only if the old ones are absent or not compatible with it.
		 */
		final Map< String, Object > currentSettings = trackmate.getSettings().trackerSettings;
		if ( !factory.checkSettingsValidity( currentSettings ) )
		{
			final Map< String, Object > defaultSettings = factory.getDefaultSettings();
			trackmate.getSettings().trackerSettings = defaultSettings;
		}
	}
}
