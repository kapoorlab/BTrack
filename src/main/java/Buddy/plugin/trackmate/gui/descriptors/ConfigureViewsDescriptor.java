package Buddy.plugin.trackmate.gui.descriptors;

import budDetector.BCellobject;
import greenDetector.Greenobject;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.GreenTrackMateGUIController;
import Buddy.plugin.trackmate.gui.TrackMateGUIController;
import Buddy.plugin.trackmate.gui.panels.ConfigureViewsPanel;
import Buddy.plugin.trackmate.visualization.FeatureColorGenerator;
import Buddy.plugin.trackmate.visualization.ManualEdgeColorGenerator;
import Buddy.plugin.trackmate.visualization.ManualGreenobjectColorGenerator;
import Buddy.plugin.trackmate.visualization.ManualBCellobjectColorGenerator;
import Buddy.plugin.trackmate.visualization.PerEdgeFeatureColorGenerator;
import Buddy.plugin.trackmate.visualization.PerTrackFeatureColorGenerator;
import pluginTools.InteractiveBud;
import pluginTools.InteractiveGreen;

public class ConfigureViewsDescriptor implements WizardPanelDescriptor {

	public static final String KEY = "ConfigureViews";

	private final ConfigureViewsPanel panel;

	private final TrackMateGUIController controller;
	
	private final GreenTrackMateGUIController greencontroller;

	public ConfigureViewsDescriptor(final TrackMate trackmate,
			final FeatureColorGenerator<BCellobject> BCellobjectColorGenerator,
			final PerEdgeFeatureColorGenerator edgeColorGenerator,
			final PerTrackFeatureColorGenerator trackColorGenerator,
			final FeatureColorGenerator<BCellobject> BCellobjectColorGeneratorPerTrackFeature,
			final ManualBCellobjectColorGenerator manualBCellobjectColorGenerator,
			final ManualEdgeColorGenerator manualEdgeColorGenerator, final TrackMateGUIController controller) {
		this.controller = controller;
		this.greencontroller = null;
		this.panel = new ConfigureViewsPanel(trackmate.getModel());
		panel.setBCellobjectColorGenerator(BCellobjectColorGenerator);
		panel.setEdgeColorGenerator(edgeColorGenerator);
		panel.setTrackColorGenerator(trackColorGenerator);
		panel.setManualBCellobjectColorGenerator(manualBCellobjectColorGenerator);
		panel.setManualEdgeColorGenerator(manualEdgeColorGenerator);
		panel.setBCellobjectColorGeneratorPerTrackFeature(BCellobjectColorGeneratorPerTrackFeature);
	}
	
	public ConfigureViewsDescriptor(final TrackMate trackmate,
			final FeatureColorGenerator<Greenobject> GreenobjectColorGenerator,
			final PerEdgeFeatureColorGenerator edgeColorGenerator,
			final PerTrackFeatureColorGenerator trackColorGenerator,
			final FeatureColorGenerator<Greenobject> GreenobjectColorGeneratorPerTrackFeature,
			final ManualGreenobjectColorGenerator manualGreenobjectColorGenerator,
			final ManualEdgeColorGenerator manualEdgeColorGenerator, final GreenTrackMateGUIController greencontroller) {
		this.controller = null;
		this.greencontroller = greencontroller;
		this.panel = new ConfigureViewsPanel(trackmate.getGreenModel());
		panel.setGreenobjectColorGenerator(GreenobjectColorGenerator);
		panel.setEdgeColorGenerator(edgeColorGenerator);
		panel.setTrackColorGenerator(trackColorGenerator);
		panel.setManualGreenobjectColorGenerator(manualGreenobjectColorGenerator);
		panel.setManualEdgeColorGenerator(manualEdgeColorGenerator);
		panel.setGreenobjectColorGeneratorPerTrackFeature(GreenobjectColorGeneratorPerTrackFeature);
	}

	@Override
	public ConfigureViewsPanel getComponent() {
		return panel;
	}

	@Override
	public void aboutToDisplayPanel() {
		panel.refreshGUI();
		if (controller!=null)
		controller.getGUI().setNextButtonEnabled(true);
		else
			greencontroller.getGUI().setNextButtonEnabled(true);		
	}

	@Override
	public void displayingPanel(InteractiveBud parent) {
		panel.refreshColorFeatures();
	}
	@Override
	public void displayingGreenPanel(InteractiveGreen parent) {
		panel.refreshColorFeatures();
	}

	@Override
	public void aboutToHidePanel() {
	}

	@Override
	public void comingBackToPanel() {
		panel.refreshGUI();
		panel.refreshColorFeatures();
	}

	@Override
	public String getKey() {
		return KEY;
	}

}
