package Buddy.plugin.trackmate.gui;

import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.DEFAULT_HIGHLIGHT_COLOR;
import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.DEFAULT_Greenobject_COLOR;
import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.DEFAULT_TRACK_DISPLAY_DEPTH;
import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.DEFAULT_TRACK_DISPLAY_MODE;
import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.KEY_COLOR;
import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.KEY_COLORMAP;
import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.KEY_DISPLAY_Greenobject_NAMES;
import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.KEY_HIGHLIGHT_COLOR;
import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.KEY_GreenobjectS_VISIBLE;
import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.KEY_Greenobject_COLORING;
import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.KEY_Greenobject_RADIUS_RATIO;
import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.KEY_TRACKS_VISIBLE;
import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.KEY_TRACK_COLORING;
import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.KEY_TRACK_DISPLAY_DEPTH;
import static Buddy.plugin.trackmate.visualization.GreenTrackMateModelView.KEY_TRACK_DISPLAY_MODE;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenSelectionModel;
import Buddy.plugin.trackmate.GreenTrackMate;
import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.TrackMateOptionUtils;
import Buddy.plugin.trackmate.action.AbstractTMAction;
import Buddy.plugin.trackmate.action.ExportAllGreenobjectsStatsAction;
import Buddy.plugin.trackmate.action.ExportStatsToIJAction;
import Buddy.plugin.trackmate.action.GreenAbstractTMAction;
import Buddy.plugin.trackmate.action.GreenExportStatsToIJAction;
import Buddy.plugin.trackmate.features.GreenModelFeatureUpdater;
import Buddy.plugin.trackmate.features.ModelFeatureUpdater;
import Buddy.plugin.trackmate.features.edges.EdgeVelocityAnalyzer;
import Buddy.plugin.trackmate.features.edges.GreenEdgeVelocityAnalyzer;
import Buddy.plugin.trackmate.features.track.TrackIndexAnalyzer;
import Buddy.plugin.trackmate.gui.descriptors.ActionChooserDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.ConfigureViewsDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.GrapherDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.GreenActionChooserDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.GreenConfigureViewsDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.GreenGrapherDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.GreenLoadDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.GreenLogPanelDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.GreenSaveDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.GreenStartDialogDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.GreenTrackFilterDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.GreenTrackerChoiceDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.GreenTrackerConfigurationDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.GreenTrackingDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.GreenViewChoiceDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.GreenWizardPanelDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.LoadDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.LogPanelDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.SaveDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.SomeDialogDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.StartDialogDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.TrackFilterDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.TrackerChoiceDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.TrackerConfigurationDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.TrackingDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.ViewChoiceDescriptor;
import Buddy.plugin.trackmate.gui.descriptors.WizardPanelDescriptor;
import Buddy.plugin.trackmate.gui.panels.tracker.JPanelTrackerSettingsMain;
import Buddy.plugin.trackmate.gui.panels.components.ColorByFeatureGUIPanel;
import Buddy.plugin.trackmate.providers.EdgeAnalyzerProvider;
import Buddy.plugin.trackmate.providers.GreenActionProvider;
import Buddy.plugin.trackmate.providers.GreenEdgeAnalyzerProvider;
import Buddy.plugin.trackmate.providers.GreenTrackAnalyzerProvider;
import Buddy.plugin.trackmate.providers.GreenTrackerProvider;
import Buddy.plugin.trackmate.providers.GreenViewProvider;
import Buddy.plugin.trackmate.providers.TrackAnalyzerProvider;
import Buddy.plugin.trackmate.providers.TrackerProvider;
import Buddy.plugin.trackmate.providers.GreenobjectAnalyzerProvider;
import Buddy.plugin.trackmate.tracking.ManualTrackerFactory;
import Buddy.plugin.trackmate.util.TMUtils;
import Buddy.plugin.trackmate.visualization.FeatureColorGenerator;
import Buddy.plugin.trackmate.visualization.GreenPerEdgeFeatureColorGenerator;
import Buddy.plugin.trackmate.visualization.GreenPerTrackFeatureColorGenerator;
import Buddy.plugin.trackmate.visualization.ManualEdgeColorGenerator;
import Buddy.plugin.trackmate.visualization.ManualGreenobjectColorGenerator;
import Buddy.plugin.trackmate.visualization.PerEdgeFeatureColorGenerator;
import Buddy.plugin.trackmate.visualization.PerTrackFeatureColorGenerator;
import Buddy.plugin.trackmate.visualization.GreenTrackMateModelView;
import Buddy.plugin.trackmate.visualization.trackscheme.GreenTrackScheme;
import Buddy.plugin.trackmate.visualization.trackscheme.GreenobjectImageUpdater;
import Buddy.plugin.trackmate.visualization.trackscheme.TrackScheme;
import Buddy.plugin.trackmate.visualization.GreenobjectColorGenerator;
import Buddy.plugin.trackmate.visualization.GreenobjectColorGeneratorPerTrackFeature;
import greenDetector.Greenobject;
import ij.IJ;
import ij.Prefs;
import pluginTools.InteractiveBud;
import pluginTools.InteractiveGreen;

public class GreenTrackMateGUIController implements ActionListener {

	/*
	 * FIELDS
	 */

	private static final boolean DEBUG = false;

	protected final Logger logger;

	/** The trackmate piloted here. */
	protected final GreenTrackMate trackmate;

	protected final InteractiveGreen parent;

	/** The GUI controlled by this controller. */
	protected final GreenTrackMateWizard gui;

	protected final GreenTrackMateGUIModel guimodel;

	
	protected GreenobjectAnalyzerProvider GreenobjectAnalyzerProvider;

	protected GreenEdgeAnalyzerProvider edgeAnalyzerProvider;

	protected GreenTrackAnalyzerProvider trackAnalyzerProvider;

	protected GreenViewProvider viewProvider;

	protected GreenTrackerProvider trackerProvider;

	protected GreenActionProvider actionProvider;

	protected GreenStartDialogDescriptor startDialoDescriptor;

	protected GreenViewChoiceDescriptor viewChoiceDescriptor;

	protected GreenTrackerChoiceDescriptor trackerChoiceDescriptor;

	protected GreenTrackerConfigurationDescriptor trackerConfigurationDescriptor;

	protected GreenTrackingDescriptor trackingDescriptor;

	protected GreenGrapherDescriptor grapherDescriptor;

	protected GreenTrackFilterDescriptor trackFilterDescriptor;

	protected GreenConfigureViewsDescriptor configureViewsDescriptor;

	protected GreenActionChooserDescriptor actionChooserDescriptor;

	protected GreenLogPanelDescriptor logPanelDescriptor;

	protected GreenSaveDescriptor saveDescriptor;

	protected GreenLoadDescriptor loadDescriptor;

	protected Collection<GreenWizardPanelDescriptor> registeredDescriptors;

	protected GreenSelectionModel selectionModel;

	protected GreenPerTrackFeatureColorGenerator trackColorGenerator;

	protected GreenPerEdgeFeatureColorGenerator edgeColorGenerator;

	protected FeatureColorGenerator<Greenobject> GreenobjectColorGenerator;

	protected ManualGreenobjectColorGenerator manualGreenobjectColorGenerator;
	protected ManualEdgeColorGenerator manualEdgeColorGenerator;
	protected FeatureColorGenerator<Greenobject> GreenobjectColorGeneratorPerTrackFeature;

	/**
	 * The listener in charge of listening to display settings changes and
	 * forwarding them to the views registered in the {@link #guimodel}.
	 */
	protected DisplaySettingsListener displaySettingsListener;

	/*
	 * CONSTRUCTOR
	 */

	public GreenTrackMateGUIController(final InteractiveGreen parent, final GreenTrackMate trackmate) {

		// I can't stand the metal look. If this is a problem, contact me
		// (jeanyves.tinevez@gmail.com)
		if (IJ.isMacOSX() || IJ.isWindows()) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (final ClassNotFoundException e) {
				e.printStackTrace();
			} catch (final InstantiationException e) {
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			} catch (final UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		}

		this.trackmate = trackmate;
		this.parent = parent;
		trackmate.setNumThreads(Prefs.getThreads());

		/*
		 * Instantiate GUI
		 */

		this.gui = new GreenTrackMateWizard(this);
		this.logger = gui.getLogger();

		// Feature updater
		final GreenModelFeatureUpdater modelFeatureUpdater = new GreenModelFeatureUpdater(trackmate.getGreenModel(),
				trackmate.getGreenSettings());
		modelFeatureUpdater.setNumThreads(trackmate.getNumThreads());

		// Feature colorers
		this.GreenobjectColorGenerator = createGreenobjectColorGenerator();
		this.edgeColorGenerator = createEdgeColorGenerator();
		this.trackColorGenerator = createTrackColorGenerator();
		this.manualEdgeColorGenerator = createManualEdgeColorGenerator();
		this.manualGreenobjectColorGenerator = createManualGreenobjectColorGenerator();
		this.GreenobjectColorGeneratorPerTrackFeature = createGreenobjectColorGeneratorPerTrackFeature();

		// 0.
		this.guimodel = new GreenTrackMateGUIModel();
		this.guimodel.setDisplaySettings(createDisplaySettings(trackmate.getGreenModel()));
		this.displaySettingsListener = new DisplaySettingsListener() {
			@Override
			public void displaySettingsChanged(final DisplaySettingsEvent event) {
				guimodel.getDisplaySettings().put(event.getKey(), event.getNewValue());
				for (final GreenTrackMateModelView view : guimodel.views) {
					view.setDisplaySettings(event.getKey(), event.getNewValue());
					view.refresh();
				}
			}
		};

		// 1.
		createSelectionModel();
		// 2.
		createProviders();
		// 3.
		registeredDescriptors = createDescriptors();

		trackmate.getGreenModel().setLogger(logger);
		gui.setVisible(true);
		gui.addActionListener(this);

		init(parent);
	}

	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Creates a new {@link TrackMateGUIController} instance, set to operate on the
	 * specified {@link TrackMate} instance.
	 * <p>
	 * Subclassers want to override this method to return the correct type.
	 *
	 * @param lTrackmate
	 *            the instance that will be piloted by the new controller.
	 * @return a new instance of the controller.
	 */
	public GreenTrackMateGUIController createOn(final InteractiveGreen parent, final GreenTrackMate lTrackmate) {
		return new GreenTrackMateGUIController(parent, lTrackmate);
	}

	/**
	 * Closes the GUI controlled by this instance.
	 */
	public void quit() {
		gui.dispose();
	}

	/**
	 * Exposes the {@link TrackMateWizard} instance controlled here.
	 */
	public GreenTrackMateWizard getGUI() {
		return gui;
	}

	/**
	 * Exposes the {@link TrackMate} trackmate piloted by the wizard.
	 */
	public GreenTrackMate getPlugin() {
		return trackmate;
	}

	/**
	 * Exposes the {@link SelectionModel} shared amongst all
	 * {@link Buddy.plugin.trackmate.SelectionChangeListener}s controlled by this
	 * instance.
	 *
	 * @return the {@link SelectionModel}.
	 */
	public GreenSelectionModel getSelectionModel() {
		return selectionModel;
	}

	public GreenTrackMateGUIModel getGuimodel() {
		return guimodel;
	}

	/**
	 * Sets the GUI current state via a key string. Registered descriptors are
	 * parsed until one is found that has a matching key (
	 * {@link WizardPanelDescriptor#getKey()}). Then it is displayed. If a matching
	 * key is not found, nothing is done, and an error is logged in the
	 * {@link LogPanel}.
	 * <p>
	 * This method is typically called to restore a saved GUI state.
	 *
	 * @param stateKey
	 *            the target state string.
	 */
	public void setGUIStateString(final String stateKey) {
		for (final GreenWizardPanelDescriptor descriptor : registeredDescriptors) {

			if (stateKey.equals(descriptor.getKey())) {

				guimodel.currentDescriptor = descriptor;
				gui.show(descriptor);
				if (null == nextDescriptor(descriptor)) {
					gui.setNextButtonEnabled(false);
				} else {
					gui.setNextButtonEnabled(true);
				}
				if (null == previousDescriptor(descriptor)) {
					gui.setPreviousButtonEnabled(false);
				} else {
					gui.setPreviousButtonEnabled(true);
				}
				descriptor.displayingPanel(parent);
				return;

			}
		}

		logger.error("Cannot move to state " + stateKey + ". Unknown state.\n");
	}

	/**
	 * Returns the {@link ViewProvider} instance, serving
	 * {@link GreenTrackMateModelView}s to this GUI
	 *
	 * @return the view provider.
	 */
	public GreenViewProvider getViewProvider() {
		return viewProvider;
	}

	/**
	 * Returns the {@link GreenobjectAnalyzerProvider} instance, serving
	 * {@link Buddy.plugin.trackmate.features.Greenobject.GreenobjectAnalyzerFactory}s
	 * to this GUI.
	 *
	 * @return the Greenobject analyzer provider.
	 */
	public GreenobjectAnalyzerProvider getGreenobjectAnalyzerProvider() {
		return GreenobjectAnalyzerProvider;
	}

	/**
	 * Returns the {@link EdgeAnalyzerProvider} instance, serving
	 * {@link Buddy.plugin.trackmate.features.edges.EdgeAnalyzer}s to this GUI.
	 *
	 * @return the edge analyzer provider.
	 */
	public GreenEdgeAnalyzerProvider getEdgeAnalyzerProvider() {
		return edgeAnalyzerProvider;
	}

	/**
	 * Returns the {@link TrackAnalyzerProvider} instance, serving
	 * {@link Buddy.plugin.trackmate.features.track.TrackAnalyzer}s to this GUI.
	 *
	 * @return the track analyzer provider.
	 */
	public GreenTrackAnalyzerProvider getTrackAnalyzerProvider() {
		return trackAnalyzerProvider;
	}

	/**
	 * Returns the {@link TrackerProvider} instance, serving
	 * {@link Buddy.plugin.trackmate.tracking.GreenobjectTracker}s to this GUI.
	 *
	 * @return the tracker provider.
	 */
	public GreenTrackerProvider getTrackerProvider() {
		return trackerProvider;
	}

	/*
	 * PROTECTED METHODS
	 */

	protected void createSelectionModel() {
		selectionModel = new GreenSelectionModel(trackmate.getGreenModel());
	}

	protected FeatureColorGenerator<Greenobject> createGreenobjectColorGenerator() {
		return new GreenobjectColorGenerator(trackmate.getGreenModel());
	}

	protected GreenPerEdgeFeatureColorGenerator createEdgeColorGenerator() {
		return new GreenPerEdgeFeatureColorGenerator(trackmate.getGreenModel(), GreenEdgeVelocityAnalyzer.VELOCITY);
	}

	protected GreenPerTrackFeatureColorGenerator createTrackColorGenerator() {
		final GreenPerTrackFeatureColorGenerator generator = new GreenPerTrackFeatureColorGenerator(trackmate.getGreenModel(),
				TrackIndexAnalyzer.TRACK_INDEX);
		return generator;
	}

	protected ManualGreenobjectColorGenerator createManualGreenobjectColorGenerator() {
		return new ManualGreenobjectColorGenerator();
	}

	protected ManualEdgeColorGenerator createManualEdgeColorGenerator() {
		return new ManualEdgeColorGenerator(trackmate.getGreenModel());
	}

	protected FeatureColorGenerator<Greenobject> createGreenobjectColorGeneratorPerTrackFeature() {
		final FeatureColorGenerator<Greenobject> generator = new GreenobjectColorGeneratorPerTrackFeature(
				trackmate.getGreenModel(), TrackIndexAnalyzer.TRACK_INDEX);
		return generator;
	}

	protected void createProviders() {
		GreenobjectAnalyzerProvider = new GreenobjectAnalyzerProvider();
		edgeAnalyzerProvider = new GreenEdgeAnalyzerProvider();
		trackAnalyzerProvider = new GreenTrackAnalyzerProvider();
		viewProvider = new GreenViewProvider();
		trackerProvider = new GreenTrackerProvider();
		actionProvider = new GreenActionProvider();
	}

	/**
	 * Creates the map of next descriptor for each descriptor.
	 */
	protected Collection<GreenWizardPanelDescriptor> createDescriptors() {

		/*
		 * Logging panel: receive message, share with the TrackMateModel
		 */
		final LogPanel logPanel = gui.getLogPanel();
		logPanelDescriptor = new GreenLogPanelDescriptor(logPanel);

		/*
		 * Start panel
		 */
		startDialoDescriptor = new GreenStartDialogDescriptor(this) {
			@Override
			public void aboutToHidePanel() {
				super.aboutToHidePanel();
				// Reset the default save location.
				SomeDialogDescriptor.file = null;
			}

			@Override
			public void displayingPanel(InteractiveGreen parent) {
				super.displayingPanel(parent);
				if (startDialoDescriptor.isImpValid()) {
					// Ensure we reset default save location
					gui.setNextButtonEnabled(true);
				} else {
					gui.setNextButtonEnabled(false);
				}
			}
		};
		// Listen if the selected imp is valid and toggle next button
		// accordingly.
		startDialoDescriptor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (startDialoDescriptor.isImpValid()) {
					// Ensure we reset default save location
					gui.setNextButtonEnabled(true);
				} else {
					gui.setNextButtonEnabled(false);
				}
			}
		});

		/*
		 * Execute and report detection progress
		 */

		/*
		 * Select and render a view
		 */
		// We need the GUI model to register the created view there.
		viewChoiceDescriptor = new GreenViewChoiceDescriptor(viewProvider, guimodel, this);

		/*
		 * Choose a tracker
		 */
		trackerChoiceDescriptor = new GreenTrackerChoiceDescriptor(trackerProvider, trackmate, this);

		/*
		 * Configure chosen tracker
		 */
		trackerConfigurationDescriptor = new GreenTrackerConfigurationDescriptor(trackerProvider, trackmate, this);

		/*
		 * Execute tracking
		 */
		trackingDescriptor = new GreenTrackingDescriptor(this);

		/*
		 * Track filtering
		 */
		trackFilterDescriptor = new GreenTrackFilterDescriptor(trackmate, trackColorGenerator, this);
		trackFilterDescriptor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				if (trackFilterDescriptor.getComponent().getColorCategory()
						.equals(ColorByFeatureGUIPanel.Category.DEFAULT)) {
					trackColorGenerator.setFeature(null);
				} else {
					trackColorGenerator.setFeature(trackFilterDescriptor.getComponent().getColorFeature());
				}
				for (final GreenTrackMateModelView view : guimodel.views) {
					view.setDisplaySettings(GreenTrackMateModelView.KEY_TRACK_COLORING, trackColorGenerator);
					view.refresh();
				}
			}
		});
		trackFilterDescriptor.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent event) {
				// We set the thresholds field of the model but do not touch its
				// selected Greenobject field yet.
				trackmate.getGreenSettings().setTrackFilters(trackFilterDescriptor.getComponent().getFeatureFilters());
				trackmate.execTrackFiltering(false);
			}
		});

		/*
		 * Finished, let's change the display settings.
		 */
		configureViewsDescriptor = new GreenConfigureViewsDescriptor(trackmate, GreenobjectColorGenerator,
				edgeColorGenerator, trackColorGenerator, GreenobjectColorGeneratorPerTrackFeature,
				manualGreenobjectColorGenerator, manualEdgeColorGenerator, this);
		configureViewsDescriptor.getComponent().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				if (event == configureViewsDescriptor.getComponent().TRACK_SCHEME_BUTTON_PRESSED) {
					launchTrackScheme();

				} else if (event == configureViewsDescriptor.getComponent().DO_ANALYSIS_BUTTON_PRESSED) {
					launchDoAnalysis(false);

				} else if (event == configureViewsDescriptor.getComponent().DO_ANALYSIS_BUTTON_WITH_SHIFT_PRESSED) {
					launchDoAnalysis(true);

				} else {
					System.out.println("[TrackMateGUIController] Caught unknown event: " + event);
				}
			}
		});
		configureViewsDescriptor.getComponent().addDisplaySettingsChangeListener(displaySettingsListener);

		/*
		 * Export and graph features.
		 */
		grapherDescriptor = new GreenGrapherDescriptor(trackmate, this);

		/*
		 * Offer to take some actions on the data.
		 */
		actionChooserDescriptor = new GreenActionChooserDescriptor(actionProvider, trackmate, this);

		/*
		 * Save descriptor
		 */
		saveDescriptor = new GreenSaveDescriptor(this);

		/*
		 * Load descriptor
		 */
		loadDescriptor = new GreenLoadDescriptor(parent, this);

		/*
		 * Store created descriptors
		 */
		final ArrayList<GreenWizardPanelDescriptor> descriptors = new ArrayList<>(16);
		descriptors.add(actionChooserDescriptor);
		descriptors.add(configureViewsDescriptor);
		descriptors.add(grapherDescriptor);
		descriptors.add(loadDescriptor);
		descriptors.add(logPanelDescriptor);
		descriptors.add(saveDescriptor);
		descriptors.add(startDialoDescriptor);
		descriptors.add(trackFilterDescriptor);
		descriptors.add(trackerChoiceDescriptor);
		descriptors.add(trackerConfigurationDescriptor);
		descriptors.add(trackingDescriptor);
		descriptors.add(viewChoiceDescriptor);
		return descriptors;
	}

	protected GreenWizardPanelDescriptor getFirstDescriptor() {
		return startDialoDescriptor;
	}

	protected GreenWizardPanelDescriptor nextDescriptor(final GreenWizardPanelDescriptor currentDescriptor) {

		if (currentDescriptor == startDialoDescriptor) {

			return trackerChoiceDescriptor;

		}

		if (currentDescriptor == trackerChoiceDescriptor) {
			if (null == trackmate.getGreenSettings().trackerFactory
					|| trackmate.getGreenSettings().trackerFactory.getKey().equals(ManualTrackerFactory.TRACKER_KEY))
				return trackFilterDescriptor;

			return trackerConfigurationDescriptor;

		} else if (currentDescriptor == trackerConfigurationDescriptor) {
			return trackingDescriptor;

		} else if (currentDescriptor == trackingDescriptor) {
			return trackFilterDescriptor;

		} else if (currentDescriptor == trackFilterDescriptor) {
			return configureViewsDescriptor;

		} else if (currentDescriptor == configureViewsDescriptor) {
			return grapherDescriptor;

		} else if (currentDescriptor == grapherDescriptor) {
			return actionChooserDescriptor;

		} else if (currentDescriptor == actionChooserDescriptor) {
			return null;

		} else {
			throw new IllegalArgumentException("Next descriptor for " + currentDescriptor + " is unknown.");
		}
	}

	protected GreenWizardPanelDescriptor previousDescriptor(final GreenWizardPanelDescriptor currentDescriptor) {

		if (currentDescriptor == trackerChoiceDescriptor) {
			return startDialoDescriptor;

		}

		if (currentDescriptor == trackerConfigurationDescriptor) {
			return trackerChoiceDescriptor;

		} else if (currentDescriptor == trackingDescriptor) {
			return trackerConfigurationDescriptor;

		} else if (currentDescriptor == trackFilterDescriptor) {
			if (null == trackmate.getGreenSettings().trackerFactory
					|| trackmate.getGreenSettings().trackerFactory.getKey().equals(ManualTrackerFactory.TRACKER_KEY))
				return trackerChoiceDescriptor;

			return trackerConfigurationDescriptor;

		} else if (currentDescriptor == configureViewsDescriptor) {
			return trackFilterDescriptor;

		} else if (currentDescriptor == grapherDescriptor) {
			return configureViewsDescriptor;

		} else if (currentDescriptor == actionChooserDescriptor) {
			return grapherDescriptor;

		} else {
			return startDialoDescriptor;
		}
	}

	/**
	 * Display the first panel
	 */
	protected void init(InteractiveGreen parent) {

		// We need to listen to events happening on the View configuration
		configureViewsDescriptor.getComponent().addActionListener(this);

		// Get start panel id
		gui.setPreviousButtonEnabled(false);
		final GreenWizardPanelDescriptor panelDescriptor = getFirstDescriptor();
		guimodel.currentDescriptor = panelDescriptor;

		final String welcomeMessage = TrackMate.PLUGIN_NAME_STR + " v" + TrackMate.PLUGIN_NAME_VERSION
				+ " started on:\n" + TMUtils.getCurrentTimeString() + '\n';
		// Log GUI processing start
		gui.getLogger().log(welcomeMessage, Logger.BLUE_COLOR);
		gui.getLogger().log(" A Fiji hack to track Buds and cells in BTrack, based on this publication: ");

		gui.getLogger().log("https://scholar.google.com/scholar?cluster=9846627681021220605\n", Logger.BLUE_COLOR);
		// Execute about to be displayed action of the new one
		panelDescriptor.aboutToDisplayPanel();

		// Display matching panel
		gui.show(panelDescriptor);

		// Show the panel in the dialog, and execute action after display
		panelDescriptor.displayingPanel(parent);
	}

	/**
	 * Returns the starting display settings that will be passed to any new view
	 * registered within this GUI.
	 *
	 * @param model
	 *            the model this GUI will configure; might be required by some
	 *            display settings.
	 * @return a map of display settings mappings.
	 */
	protected Map<String, Object> createDisplaySettings(final GreenModel model) {
		final Map<String, Object> displaySettings = new HashMap<>();
		displaySettings.put(KEY_COLOR, DEFAULT_Greenobject_COLOR);
		displaySettings.put(KEY_HIGHLIGHT_COLOR, DEFAULT_HIGHLIGHT_COLOR);
		displaySettings.put(KEY_GreenobjectS_VISIBLE, true);
		displaySettings.put(KEY_DISPLAY_Greenobject_NAMES, false);
		displaySettings.put(KEY_Greenobject_COLORING, GreenobjectColorGenerator);
		displaySettings.put(KEY_Greenobject_RADIUS_RATIO, 1.0d);
		displaySettings.put(KEY_TRACKS_VISIBLE, true);
		displaySettings.put(KEY_TRACK_DISPLAY_MODE, DEFAULT_TRACK_DISPLAY_MODE);
		displaySettings.put(KEY_TRACK_DISPLAY_DEPTH, DEFAULT_TRACK_DISPLAY_DEPTH);
		displaySettings.put(KEY_TRACK_COLORING, trackColorGenerator);
		displaySettings.put(KEY_COLORMAP, TrackMateOptionUtils.getOptions().getPaintScale());
		return displaySettings;
	}

	/*
	 * ACTION LISTENER
	 */

	@Override
	public void actionPerformed(final ActionEvent event) {
		if (DEBUG)
			System.out.println("[TrackMateGUIController] Caught event " + event);

		if (event == gui.NEXT_BUTTON_PRESSED && guimodel.actionFlag) {

			next();

		} else if (event == gui.PREVIOUS_BUTTON_PRESSED && guimodel.actionFlag) {

			previous();

		} else if (event == gui.LOAD_BUTTON_PRESSED && guimodel.actionFlag) {

			/*
			 * TODO: There is actually NO load button anymore. The user load the data
			 * directly through another plugin call. We left this code here intact in case I
			 * change my mind. Removing it will actually trigger an appreciable
			 * simplification of the code, but I let it linger here a bit more. - Sep 2013
			 */

			guimodel.actionFlag = false;
			gui.jButtonNext.setText("Resume");
			disableButtonsAndStoreState();
			load();
			restoreButtonsState();

		} else if (event == gui.SAVE_BUTTON_PRESSED && guimodel.actionFlag) {

			guimodel.actionFlag = false;
			gui.jButtonNext.setText("Resume");
			disableButtonsAndStoreState();
			new Thread("TrackMate saving thread") {
				@Override
				public void run() {
					save();
					gui.jButtonNext.setEnabled(true);
				}
			}.start();

		} else if ((event == gui.NEXT_BUTTON_PRESSED || event == gui.PREVIOUS_BUTTON_PRESSED
				|| event == gui.LOAD_BUTTON_PRESSED || event == gui.SAVE_BUTTON_PRESSED) && !guimodel.actionFlag) {

			// Display previous panel, but do not execute its actions
			guimodel.actionFlag = true;
			gui.show(guimodel.previousDescriptor);

			// Put back buttons
			gui.jButtonNext.setText("Next");
			restoreButtonsState();

		} else if (event == gui.LOG_BUTTON_PRESSED) {

			if (guimodel.displayingLog) {

				restoreButtonsState();
				gui.show(guimodel.previousDescriptor);
				guimodel.displayingLog = false;

			} else {
				disableButtonsAndStoreState();
				guimodel.previousDescriptor = guimodel.currentDescriptor;
				gui.show(logPanelDescriptor);
				gui.setLogButtonEnabled(true);
				guimodel.displayingLog = true;
			}
		} else if (event == gui.DISPLAY_CONFIG_BUTTON_PRESSED) {
			if (guimodel.displayingDisplayConfig) {

				restoreButtonsState();
				gui.show(guimodel.previousDescriptor);
				guimodel.displayingDisplayConfig = false;

			} else {
				disableButtonsAndStoreState();
				guimodel.previousDescriptor = guimodel.currentDescriptor;
				trackmate.computeGreenobjectFeatures(true);
				trackmate.computeEdgeFeatures(true);
				trackmate.computeTrackFeatures(true);
				configureViewsDescriptor.getComponent().refreshGUI();
				configureViewsDescriptor.getComponent().refreshColorFeatures();
				gui.show(configureViewsDescriptor);
				gui.setDisplayConfigButtonEnabled(true);
				guimodel.displayingDisplayConfig = true;
			}
		}
	}

	private void next() {

		gui.setNextButtonEnabled(false);

		// Execute leave action of the old panel
		final GreenWizardPanelDescriptor oldDescriptor = guimodel.currentDescriptor;
		if (oldDescriptor != null) {
			oldDescriptor.aboutToHidePanel();
		}

		// Find and store new one to display
		final GreenWizardPanelDescriptor panelDescriptor = nextDescriptor(oldDescriptor);
		guimodel.currentDescriptor = panelDescriptor;

		// Re-enable the previous button, in case it was disabled
		gui.setPreviousButtonEnabled(true);

		// Execute about to be displayed action of the new one
		panelDescriptor.aboutToDisplayPanel();

		// Display matching panel
		gui.show(panelDescriptor);

		// Show the panel in the dialog, and execute action after display
		panelDescriptor.displayingPanel(parent);
	}

	private void previous() {
		// Move to previous panel, but do not execute its forward-navigation
		// actions.
		final GreenWizardPanelDescriptor olDescriptor = guimodel.currentDescriptor;
		final GreenWizardPanelDescriptor panelDescriptor = previousDescriptor(olDescriptor);
		// Execute its backward-navigation actions.
		panelDescriptor.comingBackToPanel();
		// Do whatever we do when the panel is shown.
		panelDescriptor.displayingPanel(parent);
		gui.show(panelDescriptor);
		guimodel.currentDescriptor = panelDescriptor;

		// Check if the new panel has a next panel. If not, disable the next
		// button
		if (null == previousDescriptor(panelDescriptor)) {
			gui.setPreviousButtonEnabled(false);
		}

		// Re-enable the previous button, in case it was disabled
		gui.setNextButtonEnabled(true);
	}

	private void load() {
		// Store current state
		guimodel.previousDescriptor = guimodel.currentDescriptor;

		// Move to load state and show log panel
		loadDescriptor.aboutToDisplayPanel();
		gui.show(loadDescriptor);

		// Instantiate GuiReader, ask for file, and load it in memory
		loadDescriptor.displayingPanel(parent);
	}

	private void save() {
		// Store current state
		guimodel.previousDescriptor = guimodel.currentDescriptor;

		/*
		 * Special case: if we are currently configuring a detector or a tracker, stores
		 * the settings currently displayed in TrackMate.
		 */

		if (guimodel.currentDescriptor.equals(trackerConfigurationDescriptor)) {
			// This will flush currently displayed settings to TrackMate.
			guimodel.currentDescriptor.aboutToHidePanel();
		}

		// Move to save state and execute
		saveDescriptor.aboutToDisplayPanel();

		gui.show(saveDescriptor);
		gui.getLogger().log(TMUtils.getCurrentTimeString() + '\n', Logger.BLUE_COLOR);
		saveDescriptor.displayingPanel(parent);
	}

	/**
	 * Disable the 4 bottom buttons and memorize their state to that they can be
	 * restored when calling {@link #restoreButtonsState()}.
	 */
	public void disableButtonsAndStoreState() {
		guimodel.loadButtonState = gui.jButtonLoad.isEnabled();
		guimodel.saveButtonState = gui.jButtonSave.isEnabled();
		guimodel.previousButtonState = gui.jButtonPrevious.isEnabled();
		guimodel.nextButtonState = gui.jButtonNext.isEnabled();
		guimodel.displayConfigButtonState = gui.jButtonDisplayConfig.isEnabled();
		guimodel.logButtonState = gui.jButtonLog.isEnabled();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				gui.jButtonLoad.setEnabled(false);
				gui.jButtonNext.setEnabled(false);
				gui.jButtonPrevious.setEnabled(false);
				gui.jButtonSave.setEnabled(false);
				gui.jButtonLog.setEnabled(false);
				gui.jButtonDisplayConfig.setEnabled(false);
			}
		});
	}

	/**
	 * Restore the button state saved when calling
	 * {@link #disableButtonsAndStoreState()}. Do nothing if
	 * {@link #disableButtonsAndStoreState()} was not called before.
	 */
	public void restoreButtonsState() {
		gui.setLoadButtonEnabled(guimodel.loadButtonState);
		gui.setSaveButtonEnabled(guimodel.saveButtonState);
		gui.setPreviousButtonEnabled(guimodel.previousButtonState);
		gui.setNextButtonEnabled(guimodel.nextButtonState);
		gui.setDisplayConfigButtonEnabled(guimodel.displayConfigButtonState);
		gui.setLogButtonEnabled(guimodel.logButtonState);
	}

	private void launchTrackScheme() {
		final JButton button = configureViewsDescriptor.getComponent().getTrackSchemeButton();
		button.setEnabled(false);
		new Thread("Launching TrackScheme thread") {
			@Override
			public void run() {
				final GreenTrackScheme trackscheme = new GreenTrackScheme(trackmate.getGreenModel(), selectionModel);
				final GreenobjectImageUpdater thumbnailUpdater = new GreenobjectImageUpdater(trackmate.getGreenSettings());
				trackscheme.setGreenobjectImageUpdater(thumbnailUpdater);
				for (final String settingKey : guimodel.getDisplaySettings().keySet()) {
					trackscheme.setDisplaySettings(settingKey, guimodel.getDisplaySettings().get(settingKey));
				}
				trackscheme.render();
				guimodel.addView(trackscheme);
				// De-register
				trackscheme.getGUI().addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(final WindowEvent e) {
						guimodel.removeView(trackscheme);
					}
				});

				button.setEnabled(true);
			}
		}.start();
	}

	private void launchDoAnalysis(final boolean showAllGreenobjectStats) {
		final JButton button = configureViewsDescriptor.getComponent().getDoAnalysisButton();
		button.setEnabled(false);
		if (guimodel.displayingLog == false && guimodel.displayingDisplayConfig == false)
			disableButtonsAndStoreState();
		gui.show(logPanelDescriptor);
		new Thread("TrackMate export analysis to IJ thread.") {
			@Override
			public void run() {
				try {
					GreenAbstractTMAction action;
					if (showAllGreenobjectStats)
						action = new ExportAllGreenobjectsStatsAction(selectionModel);
					else
						action = new GreenExportStatsToIJAction(selectionModel);

					action.execute(trackmate);

				} finally {
					gui.show(configureViewsDescriptor);
					button.setEnabled(true);
					if (guimodel.displayingLog == false && guimodel.displayingDisplayConfig == false)
						restoreButtonsState();
				}
			}
		}.start();
	}

}
