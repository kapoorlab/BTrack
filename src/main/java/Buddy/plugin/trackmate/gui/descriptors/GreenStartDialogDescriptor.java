package Buddy.plugin.trackmate.gui.descriptors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenSelectionModel;
import Buddy.plugin.trackmate.GreenSettings;
import Buddy.plugin.trackmate.GreenTrackMate;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.Settings;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.features.edges.EdgeAnalyzer;
import Buddy.plugin.trackmate.features.edges.GreenEdgeAnalyzer;
import Buddy.plugin.trackmate.features.spot.GreenobjectAnalyzerFactory;
import Buddy.plugin.trackmate.features.spot.GreenobjectAnalyzerFactory;
import Buddy.plugin.trackmate.features.track.GreenTrackAnalyzer;
import Buddy.plugin.trackmate.features.track.TrackAnalyzer;
import Buddy.plugin.trackmate.gui.GreenTrackMateGUIController;
import Buddy.plugin.trackmate.gui.TrackMateGUIController;
import Buddy.plugin.trackmate.gui.panels.StartDialogPanel;
import Buddy.plugin.trackmate.providers.GreenobjectAnalyzerProvider;
import Buddy.plugin.trackmate.providers.EdgeAnalyzerProvider;
import Buddy.plugin.trackmate.providers.GreenEdgeAnalyzerProvider;
import Buddy.plugin.trackmate.providers.GreenTrackAnalyzerProvider;
import Buddy.plugin.trackmate.providers.GreenobjectAnalyzerProvider;
import Buddy.plugin.trackmate.providers.TrackAnalyzerProvider;
import Buddy.plugin.trackmate.visualization.hyperstack.GreenHyperStackDisplayer;
import Buddy.plugin.trackmate.visualization.hyperstack.HyperStackDisplayer;
import ij.ImagePlus;
import ij.WindowManager;
import net.imglib2.img.display.imagej.ImageJFunctions;
import pluginTools.InteractiveBud;
import pluginTools.InteractiveGreen;

public class GreenStartDialogDescriptor implements GreenWizardPanelDescriptor {

	private static final String KEY = "Start";

	private final StartDialogPanel panel;

	private final ArrayList<ActionListener> actionListeners = new ArrayList<>();

	private final GreenTrackMateGUIController controller;
	
	private final GreenTrackMateGUIController greencontroller;
	
	/**
	 * The view that is launched immediately when this descriptor leaves. It will be
	 * used as a central view.
	 */
	private GreenHyperStackDisplayer mainView;

	public GreenStartDialogDescriptor(final GreenTrackMateGUIController controller) {
		this.greencontroller = null;
		this.controller = controller;
		this.panel = new StartDialogPanel();
		panel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				fireAction(event);
			}
		});
	}
	

	/*
	 * METHODS
	 */

	/**
	 * Returns <code>true</code> if the {@link ImagePlus} selected is valid and can
	 * be processed.
	 *
	 * @return a boolean flag.
	 */
	public boolean isImpValid() {
		return panel.isImpValid();
	}

	/*
	 * WIZARDPANELDESCRIPTOR METHODS
	 */

	@Override
	public StartDialogPanel getComponent() {
		return panel;
	}

	@Override
	public void aboutToDisplayPanel() {
		if (controller!=null)
		controller.getGUI().setDisplayConfigButtonEnabled(false);
		else
			greencontroller.getGUI().setDisplayConfigButtonEnabled(false);
	
	}

	@Override
	public void displayingPanel(InteractiveGreen parent) {
		ImagePlus imp;
		final GreenTrackMate trackmate = controller.getPlugin();
		if (null == trackmate.getGreenSettings().imp) {
			imp = ImageJFunctions.show(parent.originalimg);
		} else {
			panel.echoSettings(trackmate.getGreenModel(), trackmate.getGreenSettings());
			imp = trackmate.getGreenSettings().imp;
		}
		panel.getFrom(imp);
	}



	
	@Override
	public void aboutToHidePanel() {
		
		GreenTrackMate trackmate = null;
		 trackmate = controller.getPlugin();
		
		
		final GreenModel model = trackmate.getGreenModel();
		
		

			
			final GreenSettings settings = trackmate.getGreenSettings();
		/*
		 * Get settings and pass them to the trackmate managed by the wizard
		 */

		panel.updateTo(model, settings);
		trackmate.getGreenModel().getLogger().log(settings.toStringImageInfo());

		/*
		 * Configure settings object with Greenobject, edge and track analyzers as
		 * specified in the providers.
		 */

		settings.clearGreenobjectAnalyzerFactories();
		final GreenobjectAnalyzerProvider GreenobjectAnalyzerProvider = controller.getGreenobjectAnalyzerProvider();
		final List<String> GreenobjectAnalyzerKeys = GreenobjectAnalyzerProvider.getKeys();
		for (final String key : GreenobjectAnalyzerKeys) {
			final GreenobjectAnalyzerFactory<?> GreenobjectFeatureAnalyzer = GreenobjectAnalyzerProvider
					.getFactory(key);
			settings.addGreenobjectAnalyzerFactory(GreenobjectFeatureAnalyzer);
		}

		settings.clearEdgeAnalyzers();
		final GreenEdgeAnalyzerProvider edgeAnalyzerProvider = controller.getEdgeAnalyzerProvider();
		final List<String> edgeAnalyzerKeys = edgeAnalyzerProvider.getKeys();
		for (final String key : edgeAnalyzerKeys) {
			final GreenEdgeAnalyzer edgeAnalyzer = edgeAnalyzerProvider.getFactory(key);
			settings.addEdgeAnalyzer(edgeAnalyzer);
		}

		settings.clearTrackAnalyzers();
		final GreenTrackAnalyzerProvider trackAnalyzerProvider = controller.getTrackAnalyzerProvider();
		final List<String> trackAnalyzerKeys = trackAnalyzerProvider.getKeys();
		for (final String key : trackAnalyzerKeys) {
			final GreenTrackAnalyzer trackAnalyzer = trackAnalyzerProvider.getFactory(key);
			settings.addTrackAnalyzer(trackAnalyzer);
		}

		trackmate.getGreenModel().getLogger().log(settings.toStringFeatureAnalyzersInfo());
		trackmate.computeGreenobjectFeatures(true);
		trackmate.computeEdgeFeatures(true);
		trackmate.computeTrackFeatures(true);

		/*
		 * Launch the ImagePlus view now.
		 */

		// De-register old one, if any.
		if (mainView != null) {
			mainView.clear();
			model.removeModelChangeListener(mainView);
		}

		final GreenSelectionModel selectionModel = controller.getSelectionModel();
		mainView = new GreenHyperStackDisplayer(model, selectionModel, settings.imp);
		controller.getGuimodel().addView(mainView);
		final Map<String, Object> displaySettings = controller.getGuimodel().getDisplaySettings();
		for (final String key : displaySettings.keySet()) {
			mainView.setDisplaySettings(key, displaySettings.get(key));
		}
		mainView.render();
		controller.getGUI().setDisplayConfigButtonEnabled(true);
		
		}
		
		
		
	

	@Override
	public void comingBackToPanel() {
	}

	@Override
	public String getKey() {
		return KEY;
	}

	/*
	 * LISTERNER METHODS
	 */

	/**
	 * Adds an {@link ActionListener} to this panel. These listeners will be
	 * notified when a button is pushed or when the feature to color is changed.
	 */
	public void addActionListener(final ActionListener listener) {
		actionListeners.add(listener);
	}

	/**
	 * Removes an ActionListener from this panel.
	 *
	 * @return true if the listener was in the ActionListener collection of this
	 *         instance.
	 */
	public boolean removeActionListener(final ActionListener listener) {
		return actionListeners.remove(listener);
	}

	public Collection<ActionListener> getActionListeners() {
		return actionListeners;
	}

	/**
	 * Forward the given {@link ActionEvent} to all the {@link ActionListener} of
	 * this panel.
	 */
	private void fireAction(final ActionEvent e) {
		for (final ActionListener l : actionListeners) {
			l.actionPerformed(e);
		}
	}



}
