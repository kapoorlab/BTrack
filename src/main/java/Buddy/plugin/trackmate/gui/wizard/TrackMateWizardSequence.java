package Buddy.plugin.trackmate.gui.wizard;

import static Buddy.plugin.trackmate.gui.Icons.BCellobject_TABLE_ICON;
import static Buddy.plugin.trackmate.gui.Icons.TRACK_SCHEME_ICON_16x16;
import static Buddy.plugin.trackmate.gui.Icons.TRACK_TABLES_ICON;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;

import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.Settings;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.action.AbstractTMAction;
import Buddy.plugin.trackmate.action.ExportAllBCellobjectsStatsAction;
import Buddy.plugin.trackmate.action.ExportStatsTablesAction;
import Buddy.plugin.trackmate.features.FeatureFilter;
import Buddy.plugin.trackmate.gui.components.ConfigurationPanel;
import Buddy.plugin.trackmate.gui.components.FeatureDisplaySelector;
import Buddy.plugin.trackmate.gui.components.LogPanel;
import Buddy.plugin.trackmate.gui.displaysettings.DisplaySettings;
import Buddy.plugin.trackmate.gui.wizard.descriptors.ActionChooserDescriptor;
import Buddy.plugin.trackmate.gui.wizard.descriptors.ChooseTrackerDescriptor;
import Buddy.plugin.trackmate.gui.wizard.descriptors.ConfigureViewsDescriptor;
import Buddy.plugin.trackmate.gui.wizard.descriptors.ExecuteDetectionDescriptor;
import Buddy.plugin.trackmate.gui.wizard.descriptors.ExecuteTrackingDescriptor;
import Buddy.plugin.trackmate.gui.wizard.descriptors.GrapherDescriptor;
import Buddy.plugin.trackmate.gui.wizard.descriptors.SaveDescriptor;
import Buddy.plugin.trackmate.gui.wizard.descriptors.BCellobjectTrackerDescriptor;
import Buddy.plugin.trackmate.gui.wizard.descriptors.StartDialogDescriptor;
import Buddy.plugin.trackmate.gui.wizard.descriptors.TrackFilterDescriptor;
import Buddy.plugin.trackmate.providers.ActionProvider;
import Buddy.plugin.trackmate.providers.TrackerProvider;
import Buddy.plugin.trackmate.tracking.ManualTrackerFactory;
import Buddy.plugin.trackmate.tracking.BCellobjectTrackerFactory;
import Buddy.plugin.trackmate.visualization.trackscheme.BCellobjectImageUpdater;
import Buddy.plugin.trackmate.visualization.trackscheme.TrackScheme;

public class TrackMateWizardSequence implements WizardSequence
{

	private final TrackMate trackmate;

	private final SelectionModel selectionModel;

	private final DisplaySettings displaySettings;

	private WizardPanelDescriptor current;

	private final StartDialogDescriptor startDialogDescriptor;

	private final Map< WizardPanelDescriptor, WizardPanelDescriptor > next;

	private final Map< WizardPanelDescriptor, WizardPanelDescriptor > previous;



	private final ExecuteDetectionDescriptor executeDetectionDescriptor;


	private final ChooseTrackerDescriptor chooseTrackerDescriptor;

	private final ExecuteTrackingDescriptor executeTrackingDescriptor;

	private final TrackFilterDescriptor trackFilterDescriptor;

	private final ConfigureViewsDescriptor configureViewsDescriptor;

	private final GrapherDescriptor grapherDescriptor;

	private final ActionChooserDescriptor actionChooserDescriptor;

	private final SaveDescriptor saveDescriptor;

	public TrackMateWizardSequence( final TrackMate trackmate, final SelectionModel selectionModel, final DisplaySettings displaySettings )
	{
		this.trackmate = trackmate;
		this.selectionModel = selectionModel;
		this.displaySettings = displaySettings;
		final Settings settings = trackmate.getSettings();
		final Model model = trackmate.getModel();

		final LogPanel logPanel = new LogPanel();
		final Logger logger = logPanel.getLogger();
		model.setLogger( logger );

		final FeatureDisplaySelector featureSelector = new FeatureDisplaySelector( model, settings, displaySettings );
		final FeatureFilter initialFilter = new FeatureFilter( BCellobject.QUALITY, settings.initialBCellobjectFilterValue.doubleValue(), true );
		final List< FeatureFilter > BCellobjectFilters = settings.getBCellobjectFilters();
		final List< FeatureFilter > trackFilters = settings.getTrackFilters();

		startDialogDescriptor = new StartDialogDescriptor( settings, logger );
		executeDetectionDescriptor = new ExecuteDetectionDescriptor( trackmate, logPanel );
		chooseTrackerDescriptor = new ChooseTrackerDescriptor( new TrackerProvider(), trackmate );
		executeTrackingDescriptor = new ExecuteTrackingDescriptor( trackmate, logPanel );
		trackFilterDescriptor = new TrackFilterDescriptor( trackmate, trackFilters, featureSelector );
		configureViewsDescriptor = new ConfigureViewsDescriptor( displaySettings, featureSelector, new LaunchTrackSchemeAction(), new ShowTrackTablesAction(), new ShowBCellobjectTableAction(), model.getSpaceUnits() );
		grapherDescriptor = new GrapherDescriptor( trackmate, displaySettings );
		actionChooserDescriptor = new ActionChooserDescriptor( new ActionProvider(), trackmate, selectionModel, displaySettings );
		saveDescriptor = new SaveDescriptor( trackmate, displaySettings, this );

		this.next = getForwardSequence();
		this.previous = getBackwardSequence();
		current = startDialogDescriptor;
	}

	@Override
	public WizardPanelDescriptor next()
	{
		if ( current == chooseDetectorDescriptor )
			getDetectorConfigDescriptor();

		if ( current == chooseTrackerDescriptor )
			getTrackerConfigDescriptor();

		current = next.get( current );
		return current;
	}


	@Override
	public WizardPanelDescriptor previous()
	{
		if ( current == trackFilterDescriptor )
			getTrackerConfigDescriptor();

		if ( current == BCellobjectFilterDescriptor )
			getDetectorConfigDescriptor();

		current = previous.get( current );
		return current;
	}

	@Override
	public boolean hasNext()
	{
		return current != actionChooserDescriptor;
	}

	@Override
	public WizardPanelDescriptor current()
	{
		return current;
	}


	@Override
	public WizardPanelDescriptor logDescriptor()
	{
		return logDescriptor;
	}

	@Override
	public WizardPanelDescriptor configDescriptor()
	{
		return configureViewsDescriptor;
	}

	@Override
	public WizardPanelDescriptor save()
	{
		return saveDescriptor;
	}

	@Override
	public boolean hasPrevious()
	{
		return current != startDialogDescriptor;
	}

	private Map< WizardPanelDescriptor, WizardPanelDescriptor > getBackwardSequence()
	{
		final Map< WizardPanelDescriptor, WizardPanelDescriptor > map = new HashMap<>();
		map.put( startDialogDescriptor, null );
		map.put( chooseDetectorDescriptor, startDialogDescriptor );
		map.put( chooseTrackerDescriptor, BCellobjectFilterDescriptor );
		map.put( configureViewsDescriptor, trackFilterDescriptor );
		map.put( grapherDescriptor, configureViewsDescriptor );
		map.put( actionChooserDescriptor, grapherDescriptor );
		return map;
	}

	private Map< WizardPanelDescriptor, WizardPanelDescriptor > getForwardSequence()
	{
		final Map< WizardPanelDescriptor, WizardPanelDescriptor > map = new HashMap<>();
		map.put( startDialogDescriptor, chooseDetectorDescriptor );
		map.put( executeDetectionDescriptor, initFilterDescriptor );
		map.put( initFilterDescriptor, BCellobjectFilterDescriptor );
		map.put( BCellobjectFilterDescriptor, chooseTrackerDescriptor );
		map.put( executeTrackingDescriptor, trackFilterDescriptor );
		map.put( trackFilterDescriptor, configureViewsDescriptor );
		map.put( configureViewsDescriptor, grapherDescriptor );
		map.put( grapherDescriptor, actionChooserDescriptor );
		return map;
	}

	@Override
	public void setCurrent( final String panelIdentifier )
	{
		if ( panelIdentifier.equals( BCellobjectDetectorDescriptor.KEY ) )
		{
			current = getDetectorConfigDescriptor();
			return;
		}

		if ( panelIdentifier.equals( BCellobjectTrackerDescriptor.KEY ) )
		{
			current = getTrackerConfigDescriptor();
			return;
		}

		if ( panelIdentifier.equals( InitFilterDescriptor.KEY ) )
		{
			getDetectorConfigDescriptor();
			current = initFilterDescriptor;
			return;
		}

		final List< WizardPanelDescriptor > descriptors = Arrays.asList( new WizardPanelDescriptor[] {
				logDescriptor,
				chooseDetectorDescriptor,
				executeDetectionDescriptor,
				initFilterDescriptor,
				BCellobjectFilterDescriptor,
				chooseTrackerDescriptor,
				executeTrackingDescriptor,
				trackFilterDescriptor,
				configureViewsDescriptor,
				grapherDescriptor,
				actionChooserDescriptor,
				saveDescriptor
		} );
		for ( final WizardPanelDescriptor w : descriptors )
		{
			if ( w.getPanelDescriptorIdentifier().equals( panelIdentifier ) )
			{
				current = w;
				break;
			}
		}
	}

	/**
	 * Determines and registers the descriptor used to configure the detector
	 * chosen in the {@link ChooseDetectorDescriptor}.
	 *
	 * @return a suitable {@link BCellobjectDetectorDescriptor}.
	 */
	private BCellobjectDetectorDescriptor getDetectorConfigDescriptor()
	{
		final BCellobjectDetectorFactoryBase< ? > detectorFactory = trackmate.getSettings().detectorFactory;

		/*
		 * Special case: are we dealing with the manual detector? If yes, no
		 * config, no detection.
		 */
		if ( detectorFactory.getKey().equals( ManualDetectorFactory.DETECTOR_KEY ) )
		{
			// Position sequence next and previous.
			next.put( chooseDetectorDescriptor, BCellobjectFilterDescriptor );
			previous.put( BCellobjectFilterDescriptor, chooseDetectorDescriptor );
			previous.put( executeDetectionDescriptor, chooseDetectorDescriptor );
			previous.put( initFilterDescriptor, chooseDetectorDescriptor );
			return null;
		}

		/*
		 * Copy as much settings as we can to the potentially new config
		 * descriptor.
		 */
		// From settings.
		final Map< String, Object > oldSettings1 = new HashMap<>( trackmate.getSettings().detectorSettings );
		// From previous panel.
		final Map< String, Object > oldSettings2 = new HashMap<>();
		final WizardPanelDescriptor previousDescriptor = next.get( chooseDetectorDescriptor );
		if ( previousDescriptor != null && previousDescriptor instanceof BCellobjectDetectorDescriptor )
		{
			final BCellobjectDetectorDescriptor previousBCellobjectDetectorDescriptor = ( BCellobjectDetectorDescriptor ) previousDescriptor;
			final ConfigurationPanel detectorConfigPanel = ( ConfigurationPanel ) previousBCellobjectDetectorDescriptor.targetPanel;
			oldSettings2.putAll( detectorConfigPanel.getSettings() );
		}

		final Map< String, Object > defaultSettings = detectorFactory.getDefaultSettings();
		for ( final String skey : defaultSettings.keySet() )
		{
			Object previousValue = oldSettings2.get( skey );
			if ( previousValue == null )
				previousValue = oldSettings1.get( skey );

			defaultSettings.put( skey, previousValue );
		}

		final ConfigurationPanel detectorConfigurationPanel = detectorFactory.getDetectorConfigurationPanel( trackmate.getSettings(), trackmate.getModel() );
		detectorConfigurationPanel.setSettings( defaultSettings );
		trackmate.getSettings().detectorSettings = defaultSettings;
		final BCellobjectDetectorDescriptor configDescriptor = new BCellobjectDetectorDescriptor( trackmate.getSettings(), detectorConfigurationPanel, trackmate.getModel().getLogger() );

		// Position sequence next and previous.
		next.put( chooseDetectorDescriptor, configDescriptor );
		next.put( configDescriptor, executeDetectionDescriptor );
		previous.put( configDescriptor, chooseDetectorDescriptor );
		previous.put( executeDetectionDescriptor, configDescriptor );
		previous.put( initFilterDescriptor, configDescriptor );
		previous.put( BCellobjectFilterDescriptor, configDescriptor );

		return configDescriptor;
	}

	/**
	 * Determines and registers the descriptor used to configure the tracker
	 * chosen in the {@link ChooseTrackerDescriptor}.
	 *
	 * @return a suitable {@link BCellobjectTrackerDescriptor}.
	 */
	private BCellobjectTrackerDescriptor getTrackerConfigDescriptor()
	{
		final BCellobjectTrackerFactory trackerFactory = trackmate.getSettings().trackerFactory;

		/*
		 * Special case: are we dealing with the manual tracker? If yes, no
		 * config, no detection.
		 */
		if ( trackerFactory.getKey().equals( ManualTrackerFactory.TRACKER_KEY ) )
		{
			// Position sequence next and previous.
			next.put( chooseTrackerDescriptor, trackFilterDescriptor );
			previous.put( executeTrackingDescriptor, chooseTrackerDescriptor );
			previous.put( trackFilterDescriptor, chooseTrackerDescriptor );
			return null;
		}
		/*
		 * Copy as much settings as we can to the potentially new config
		 * descriptor.
		 */
		// From settings.
		final Map< String, Object > oldSettings1 = new HashMap<>( trackmate.getSettings().trackerSettings );
		// From previous panel.
		final Map< String, Object > oldSettings2 = new HashMap<>();
		final WizardPanelDescriptor previousDescriptor = next.get( chooseTrackerDescriptor );
		if ( previousDescriptor != null && previousDescriptor instanceof BCellobjectTrackerDescriptor )
		{
			final BCellobjectTrackerDescriptor previousTrackerDetectorDescriptor = ( BCellobjectTrackerDescriptor ) previousDescriptor;
			final ConfigurationPanel detectorConfigPanel = ( ConfigurationPanel ) previousTrackerDetectorDescriptor.targetPanel;
			oldSettings2.putAll( detectorConfigPanel.getSettings() );
		}

		final Map< String, Object > defaultSettings = trackerFactory.getDefaultSettings();
		for ( final String skey : defaultSettings.keySet() )
		{
			Object previousValue = oldSettings2.get( skey );
			if ( previousValue == null )
				previousValue = oldSettings1.get( skey );

			defaultSettings.put( skey, previousValue );
		}

		final ConfigurationPanel trackerConfigurationPanel = trackerFactory.getTrackerConfigurationPanel( trackmate.getModel() );
		trackerConfigurationPanel.setSettings( defaultSettings );
		trackmate.getSettings().trackerSettings = defaultSettings;
		final BCellobjectTrackerDescriptor configDescriptor = new BCellobjectTrackerDescriptor( trackmate.getSettings(), trackerConfigurationPanel, trackmate.getModel().getLogger() );

		// Position sequence next and previous.
		next.put( chooseTrackerDescriptor, configDescriptor );
		next.put( configDescriptor, executeTrackingDescriptor );
		previous.put( configDescriptor, chooseTrackerDescriptor );
		previous.put( executeTrackingDescriptor, configDescriptor );
		previous.put( trackFilterDescriptor, configDescriptor );
		
		return configDescriptor;
	}

	private static final String TRACK_TABLES_BUTTON_TOOLTIP = "<html>"
			+ "Export the features of all tracks, edges and all <br>"
			+ "BCellobjects belonging to a track to ImageJ tables."
			+ "</html>";

	private static final String BCellobject_TABLE_BUTTON_TOOLTIP = "Export the features of all BCellobjects to ImageJ tables.";

	private static final String TRACKSCHEME_BUTTON_TOOLTIP = "<html>Launch a new instance of TrackScheme.</html>";

	private class LaunchTrackSchemeAction extends AbstractAction
	{
		private static final long serialVersionUID = 1L;

		private LaunchTrackSchemeAction()
		{
			super( "TrackScheme", TRACK_SCHEME_ICON_16x16 );
			putValue( SHORT_DESCRIPTION, TRACKSCHEME_BUTTON_TOOLTIP );
		}

		@Override
		public void actionPerformed( final ActionEvent e )
		{
			new Thread( "Launching TrackScheme thread" )
			{
				@Override
				public void run()
				{
					final TrackScheme trackscheme = new TrackScheme( trackmate.getModel(), selectionModel, displaySettings );
					final BCellobjectImageUpdater thumbnailUpdater = new BCellobjectImageUpdater( trackmate.getSettings() );
					trackscheme.setBCellobjectImageUpdater( thumbnailUpdater );
					trackscheme.render();
				}
			}.start();
		}
	}

	private class ShowTrackTablesAction extends AbstractAction
	{
		private static final long serialVersionUID = 1L;

		private ShowTrackTablesAction()
		{
			super( "Tracks", TRACK_TABLES_ICON );
			putValue( SHORT_DESCRIPTION, TRACK_TABLES_BUTTON_TOOLTIP );
		}

		@Override
		public void actionPerformed( final ActionEvent e )
		{
			showTables( false );
		}
	}

	private class ShowBCellobjectTableAction extends AbstractAction
	{
		private static final long serialVersionUID = 1L;

		private ShowBCellobjectTableAction()
		{
			super( "BCellobjects", BCellobject_TABLE_ICON );
			putValue( SHORT_DESCRIPTION, BCellobject_TABLE_BUTTON_TOOLTIP );
		}

		@Override
		public void actionPerformed( final ActionEvent e )
		{
			showTables( true );
		}
	}

	private void showTables( final boolean showBCellobjectTable )
	{
		new Thread( "TrackMate table thread." )
		{
			@Override
			public void run()
			{
				AbstractTMAction action;
				if ( showBCellobjectTable )
					action = new ExportAllBCellobjectsStatsAction();
				else
					action = new ExportStatsTablesAction();

				action.execute( trackmate, selectionModel, displaySettings, null );
			}
		}.start();
	}
}
