package fiji.plugin.btrackmate.gui.wizard;

import static fiji.plugin.btrackmate.gui.Icons.SPOT_TABLE_ICON;
import static fiji.plugin.btrackmate.gui.Icons.TRACK_SCHEME_ICON_16x16;
import static fiji.plugin.btrackmate.gui.Icons.TRACK_TABLES_ICON;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import fiji.plugin.btrackmate.Logger;
import fiji.plugin.btrackmate.Model;
import fiji.plugin.btrackmate.SelectionModel;
import fiji.plugin.btrackmate.Settings;
import fiji.plugin.btrackmate.Spot;
import fiji.plugin.btrackmate.SpotCollection;
import fiji.plugin.btrackmate.TrackMate;
import fiji.plugin.btrackmate.action.AbstractTMAction;
import fiji.plugin.btrackmate.action.ExportAllSpotsStatsAction;
import fiji.plugin.btrackmate.action.ExportStatsTablesAction;
import fiji.plugin.btrackmate.detection.ManualDetectorFactory;
import fiji.plugin.btrackmate.detection.SpotDetectorFactoryBase;
import fiji.plugin.btrackmate.features.FeatureFilter;
import fiji.plugin.btrackmate.gui.components.ConfigurationPanel;
import fiji.plugin.btrackmate.gui.components.FeatureDisplaySelector;
import fiji.plugin.btrackmate.gui.components.LogPanel;
import fiji.plugin.btrackmate.gui.displaysettings.DisplaySettings;
import fiji.plugin.btrackmate.gui.wizard.descriptors.ActionChooserDescriptor;
import fiji.plugin.btrackmate.gui.wizard.descriptors.ChooseDetectorDescriptor;
import fiji.plugin.btrackmate.gui.wizard.descriptors.ChooseTrackerDescriptor;
import fiji.plugin.btrackmate.gui.wizard.descriptors.ConfigureViewsDescriptor;
import fiji.plugin.btrackmate.gui.wizard.descriptors.ExecuteDetectionDescriptor;
import fiji.plugin.btrackmate.gui.wizard.descriptors.ExecuteTrackingDescriptor;
import fiji.plugin.btrackmate.gui.wizard.descriptors.GrapherDescriptor;
import fiji.plugin.btrackmate.gui.wizard.descriptors.InitFilterDescriptor;
import fiji.plugin.btrackmate.gui.wizard.descriptors.LogPanelDescriptor2;
import fiji.plugin.btrackmate.gui.wizard.descriptors.SaveDescriptor;
import fiji.plugin.btrackmate.gui.wizard.descriptors.SpotDetectorDescriptor;
import fiji.plugin.btrackmate.gui.wizard.descriptors.SpotFilterDescriptor;
import fiji.plugin.btrackmate.gui.wizard.descriptors.SpotTrackerDescriptor;
import fiji.plugin.btrackmate.gui.wizard.descriptors.StartDialogDescriptor;
import fiji.plugin.btrackmate.gui.wizard.descriptors.TrackFilterDescriptor;
import fiji.plugin.btrackmate.providers.ActionProvider;
import fiji.plugin.btrackmate.providers.DetectorProvider;
import fiji.plugin.btrackmate.providers.TrackerProvider;
import fiji.plugin.btrackmate.tracking.ManualTrackerFactory;
import fiji.plugin.btrackmate.tracking.SpotTrackerFactory;
import fiji.plugin.btrackmate.visualization.TrackMateModelView;
import fiji.plugin.btrackmate.visualization.hyperstack.HyperStackDisplayer;
import fiji.plugin.btrackmate.visualization.trackscheme.SpotImageUpdater;
import fiji.plugin.btrackmate.visualization.trackscheme.TrackScheme;

public class BTrackMateWizardSequence implements WizardSequence {

	private final TrackMate btrackmate;

	public final boolean secondrun;

	private final SelectionModel selectionModel;

	private final DisplaySettings displaySettings;

	private WizardPanelDescriptor current;

	private final StartDialogDescriptor startDialogDescriptor;

	private final Map<WizardPanelDescriptor, WizardPanelDescriptor> next;

	private final Map<WizardPanelDescriptor, WizardPanelDescriptor> previous;

	private final LogPanelDescriptor2 logDescriptor;

	private final ChooseDetectorDescriptor chooseDetectorDescriptor;

	private final ExecuteDetectionDescriptor executeDetectionDescriptor;

	private final InitFilterDescriptor initFilterDescriptor;

	private final SpotFilterDescriptor spotFilterDescriptor;

	private final ChooseTrackerDescriptor chooseTrackerDescriptor;

	private final ExecuteTrackingDescriptor executeTrackingDescriptor;

	private final TrackFilterDescriptor trackFilterDescriptor;

	private final ConfigureViewsDescriptor configureViewsDescriptor;

	private final GrapherDescriptor grapherDescriptor;

	private final ActionChooserDescriptor actionChooserDescriptor;

	private final SaveDescriptor saveDescriptor;

	public BTrackMateWizardSequence(final TrackMate btrackmate, final SelectionModel selectionModel,
			final DisplaySettings displaySettings, final Boolean secondrun) {
		this.btrackmate = btrackmate;
		this.selectionModel = selectionModel;
		this.displaySettings = displaySettings;
		this.secondrun = secondrun;
		final Settings settings = btrackmate.getSettings();
		final Model model = btrackmate.getModel();

		final LogPanel logPanel = new LogPanel();
		final Logger logger = logPanel.getLogger();
		
		model.setLogger(logger);
		
		final FeatureDisplaySelector featureSelector = new FeatureDisplaySelector(model, settings, displaySettings);
		final FeatureFilter initialFilter = new FeatureFilter(Spot.QUALITY,
				settings.initialSpotFilterValue.doubleValue(), true);
		final List<FeatureFilter> spotFilters = settings.getSpotFilters();
		final List<FeatureFilter> trackFilters = settings.getTrackFilters();

		logDescriptor = new LogPanelDescriptor2(logPanel);
		startDialogDescriptor = new StartDialogDescriptor(model, settings, logger, secondrun);
		chooseDetectorDescriptor = new ChooseDetectorDescriptor(new DetectorProvider(), btrackmate);
		executeDetectionDescriptor = new ExecuteDetectionDescriptor(btrackmate, logPanel);
		initFilterDescriptor = new InitFilterDescriptor(btrackmate, initialFilter);
		spotFilterDescriptor = new SpotFilterDescriptor(btrackmate, spotFilters, featureSelector);
		chooseTrackerDescriptor = new ChooseTrackerDescriptor(new TrackerProvider(), btrackmate);
		executeTrackingDescriptor = new ExecuteTrackingDescriptor(btrackmate, logPanel);
		trackFilterDescriptor = new TrackFilterDescriptor(btrackmate, trackFilters, featureSelector);
		configureViewsDescriptor = new ConfigureViewsDescriptor(displaySettings, featureSelector,
				new LaunchTrackSchemeAction(), new ShowTrackTablesAction(), new ShowSpotTableAction(),
				model.getSpaceUnits());
		grapherDescriptor = new GrapherDescriptor(btrackmate, displaySettings);
		actionChooserDescriptor = new ActionChooserDescriptor(new ActionProvider(), btrackmate, selectionModel,
				displaySettings);
		saveDescriptor = new SaveDescriptor(btrackmate, displaySettings, this);

		this.next = getForwardSequence();
		this.previous = getBackwardSequence();
		current = startDialogDescriptor;
	}

	@Override
	public WizardPanelDescriptor next() {
		if (current == chooseDetectorDescriptor)
			getDetectorConfigDescriptor();

		if (current == chooseTrackerDescriptor)
			getTrackerConfigDescriptor();

		current = next.get(current);

		return current;
	}

	@Override
	public WizardPanelDescriptor previous() {
		if (current == trackFilterDescriptor)
			getTrackerConfigDescriptor();

		if (current == spotFilterDescriptor)
			getDetectorConfigDescriptor();

		current = previous.get(current);
		return current;
	}

	@Override
	public boolean hasNext() {
		return current != actionChooserDescriptor;
	}

	@Override
	public WizardPanelDescriptor current() {
		return current;
	}

	@Override
	public WizardPanelDescriptor logDescriptor() {
		return logDescriptor;
	}

	@Override
	public WizardPanelDescriptor configDescriptor() {
		return configureViewsDescriptor;
	}

	@Override
	public WizardPanelDescriptor save() {
		return saveDescriptor;
	}

	@Override
	public boolean hasPrevious() {
		return current != startDialogDescriptor;
	}

	private Map<WizardPanelDescriptor, WizardPanelDescriptor> getBackwardSequence() {
		final Map<WizardPanelDescriptor, WizardPanelDescriptor> map = new HashMap<>();
		map.put(startDialogDescriptor, null);
		map.put(chooseDetectorDescriptor, startDialogDescriptor);
		map.put(chooseTrackerDescriptor, spotFilterDescriptor);
		map.put(configureViewsDescriptor, trackFilterDescriptor);
		map.put(grapherDescriptor, configureViewsDescriptor);
		map.put(actionChooserDescriptor, grapherDescriptor);
		return map;
	}

	private Map<WizardPanelDescriptor, WizardPanelDescriptor> getForwardSequence() {
		final Map<WizardPanelDescriptor, WizardPanelDescriptor> map = new HashMap<>();
		map.put(startDialogDescriptor, chooseDetectorDescriptor);
		map.put(executeDetectionDescriptor, initFilterDescriptor);
		map.put(initFilterDescriptor, spotFilterDescriptor);
		map.put(spotFilterDescriptor, chooseTrackerDescriptor);
		map.put(executeTrackingDescriptor, trackFilterDescriptor);
		map.put(trackFilterDescriptor, configureViewsDescriptor);
		map.put(configureViewsDescriptor, grapherDescriptor);
		map.put(grapherDescriptor, actionChooserDescriptor);
		return map;
	}

	@Override
	public void setCurrent(final String panelIdentifier) {
		if (panelIdentifier.equals(SpotDetectorDescriptor.KEY)) {
			current = getDetectorConfigDescriptor();
			return;
		}

		if (panelIdentifier.equals(SpotTrackerDescriptor.KEY)) {
			current = getTrackerConfigDescriptor();
			return;
		}

		if (panelIdentifier.equals(InitFilterDescriptor.KEY)) {
			getDetectorConfigDescriptor();
			current = initFilterDescriptor;
			return;
		}

		final List<WizardPanelDescriptor> descriptors = Arrays.asList(new WizardPanelDescriptor[] { logDescriptor,
				chooseDetectorDescriptor, executeDetectionDescriptor, initFilterDescriptor, spotFilterDescriptor,
				chooseTrackerDescriptor, executeTrackingDescriptor, trackFilterDescriptor, configureViewsDescriptor,
				grapherDescriptor, actionChooserDescriptor, saveDescriptor });
		for (final WizardPanelDescriptor w : descriptors) {
			if (w.getPanelDescriptorIdentifier().equals(panelIdentifier)) {
				current = w;
				break;
			}
		}
	}

	/**
	 * Determines and registers the descriptor used to configure the detector chosen
	 * in the {@link ChooseDetectorDescriptor}.
	 *
	 * @return a suitable {@link SpotDetectorDescriptor}.
	 */
	private SpotDetectorDescriptor getDetectorConfigDescriptor() {
		final SpotDetectorFactoryBase<?> detectorFactory = btrackmate.getSettings().detectorFactory;

		/*
		 * Special case: are we dealing with the manual detector? If yes, no config, no
		 * detection.
		 */
		if (detectorFactory.getKey().equals(ManualDetectorFactory.DETECTOR_KEY)) {
			// Position sequence next and previous.
			next.put(chooseDetectorDescriptor, spotFilterDescriptor);
			previous.put(spotFilterDescriptor, chooseDetectorDescriptor);
			previous.put(executeDetectionDescriptor, chooseDetectorDescriptor);
			previous.put(initFilterDescriptor, chooseDetectorDescriptor);
			return null;
		}

		/*
		 * Copy as much settings as we can to the potentially new config descriptor.
		 */
		// From settings.
		final Map<String, Object> oldSettings1 = new HashMap<>(btrackmate.getSettings().detectorSettings);
		// From previous panel.
		final Map<String, Object> oldSettings2 = new HashMap<>();
		final WizardPanelDescriptor previousDescriptor = next.get(chooseDetectorDescriptor);
		if (previousDescriptor != null && previousDescriptor instanceof SpotDetectorDescriptor) {
			final SpotDetectorDescriptor previousSpotDetectorDescriptor = (SpotDetectorDescriptor) previousDescriptor;
			final ConfigurationPanel detectorConfigPanel = (ConfigurationPanel) previousSpotDetectorDescriptor.targetPanel;
			oldSettings2.putAll(detectorConfigPanel.getSettings());
		}

		final Map<String, Object> defaultSettings = detectorFactory.getDefaultSettings();
		for (final String skey : defaultSettings.keySet()) {
			Object previousValue = oldSettings2.get(skey);
			if (previousValue == null)
				previousValue = oldSettings1.get(skey);

			defaultSettings.put(skey, previousValue);
		}

		final ConfigurationPanel detectorConfigurationPanel = detectorFactory
				.getDetectorConfigurationPanel(btrackmate.getSettings(), btrackmate.getModel());
		detectorConfigurationPanel.setSettings(defaultSettings);
		btrackmate.getSettings().detectorSettings = defaultSettings;
		final SpotDetectorDescriptor configDescriptor = new SpotDetectorDescriptor(btrackmate.getSettings(),
				detectorConfigurationPanel, btrackmate.getModel().getLogger());

		// Position sequence next and previous.
		next.put(chooseDetectorDescriptor, configDescriptor);
		next.put(configDescriptor, executeDetectionDescriptor);
		previous.put(configDescriptor, chooseDetectorDescriptor);
		previous.put(executeDetectionDescriptor, configDescriptor);
		previous.put(initFilterDescriptor, configDescriptor);
		previous.put(spotFilterDescriptor, configDescriptor);

		return configDescriptor;
	}

	/**
	 * Determines and registers the descriptor used to configure the tracker chosen
	 * in the {@link ChooseTrackerDescriptor}.
	 *
	 * @return a suitable {@link SpotTrackerDescriptor}.
	 */
	private SpotTrackerDescriptor getTrackerConfigDescriptor() {
		final SpotTrackerFactory trackerFactory = btrackmate.getSettings().trackerFactory;

		/*
		 * Special case: are we dealing with the manual tracker? If yes, no config, no
		 * detection.
		 */
		if (trackerFactory.getKey().equals(ManualTrackerFactory.TRACKER_KEY)) {
			// Position sequence next and previous.
			next.put(chooseTrackerDescriptor, trackFilterDescriptor);
			previous.put(executeTrackingDescriptor, chooseTrackerDescriptor);
			previous.put(trackFilterDescriptor, chooseTrackerDescriptor);
			return null;
		}
		/*
		 * Copy as much settings as we can to the potentially new config descriptor.
		 */
		// From settings.
		final Map<String, Object> oldSettings1 = new HashMap<>(btrackmate.getSettings().trackerSettings);
		// From previous panel.
		final Map<String, Object> oldSettings2 = new HashMap<>();
		final WizardPanelDescriptor previousDescriptor = next.get(chooseTrackerDescriptor);
		if (previousDescriptor != null && previousDescriptor instanceof SpotTrackerDescriptor) {
			final SpotTrackerDescriptor previousTrackerDetectorDescriptor = (SpotTrackerDescriptor) previousDescriptor;
			final ConfigurationPanel detectorConfigPanel = (ConfigurationPanel) previousTrackerDetectorDescriptor.targetPanel;
			oldSettings2.putAll(detectorConfigPanel.getSettings());
		}

		final Map<String, Object> defaultSettings = trackerFactory.getDefaultSettings();
		for (final String skey : defaultSettings.keySet()) {
			Object previousValue = oldSettings2.get(skey);
			if (previousValue == null)
				previousValue = oldSettings1.get(skey);

			defaultSettings.put(skey, previousValue);
		}

		final ConfigurationPanel trackerConfigurationPanel = trackerFactory
				.getTrackerConfigurationPanel(btrackmate.getModel());
		trackerConfigurationPanel.setSettings(defaultSettings);
		btrackmate.getSettings().trackerSettings = defaultSettings;
		final SpotTrackerDescriptor configDescriptor = new SpotTrackerDescriptor(btrackmate.getSettings(),
				trackerConfigurationPanel, btrackmate.getModel().getLogger());

		// Position sequence next and previous.
		next.put(chooseTrackerDescriptor, configDescriptor);
		next.put(configDescriptor, executeTrackingDescriptor);
		previous.put(configDescriptor, chooseTrackerDescriptor);
		previous.put(executeTrackingDescriptor, configDescriptor);
		previous.put(trackFilterDescriptor, configDescriptor);

		return configDescriptor;
	}

	private static final String TRACK_TABLES_BUTTON_TOOLTIP = "<html>"
			+ "Export the features of all tracks, edges and all <br>" + "spots belonging to a track to ImageJ tables."
			+ "</html>";

	private static final String SPOT_TABLE_BUTTON_TOOLTIP = "Export the features of all spots to ImageJ tables.";

	private static final String TRACKSCHEME_BUTTON_TOOLTIP = "<html>Launch a new instance of TrackScheme.</html>";

	private class LaunchTrackSchemeAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private LaunchTrackSchemeAction() {
			super("TrackScheme", TRACK_SCHEME_ICON_16x16);
			putValue(SHORT_DESCRIPTION, TRACKSCHEME_BUTTON_TOOLTIP);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			new Thread("Launching TrackScheme thread") {
				@Override
				public void run() {
					final TrackScheme trackscheme = new TrackScheme(btrackmate.getModel(), selectionModel,
							displaySettings);
					final SpotImageUpdater thumbnailUpdater = new SpotImageUpdater(btrackmate.getSettings());
					trackscheme.setSpotImageUpdater(thumbnailUpdater);
					trackscheme.render();
				}
			}.start();
		}
	}

	private class ShowTrackTablesAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private ShowTrackTablesAction() {
			super("Tracks", TRACK_TABLES_ICON);
			putValue(SHORT_DESCRIPTION, TRACK_TABLES_BUTTON_TOOLTIP);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			showTables(false);
		}
	}

	private class ShowSpotTableAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private ShowSpotTableAction() {
			super("Spots", SPOT_TABLE_ICON);
			putValue(SHORT_DESCRIPTION, SPOT_TABLE_BUTTON_TOOLTIP);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			showTables(true);
		}
	}

	private void showTables(final boolean showSpotTable) {
		new Thread("TrackMate table thread.") {
			@Override
			public void run() {
				AbstractTMAction action;
				if (showSpotTable)
					action = new ExportAllSpotsStatsAction();
				else
					action = new ExportStatsTablesAction();

				action.execute(btrackmate, selectionModel, displaySettings, null);
			}
		}.start();
	}
}
