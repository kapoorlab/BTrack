package Buddy.plugin.trackmate.gui.descriptors;

import greenDetector.Greenobject;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.GreenTrackMateGUIController;
import Buddy.plugin.trackmate.gui.TrackMateGUIController;
import Buddy.plugin.trackmate.gui.panels.ConfigureViewsPanel;
import Buddy.plugin.trackmate.visualization.FeatureColorGenerator;
import Buddy.plugin.trackmate.visualization.ManualEdgeColorGenerator;
import Buddy.plugin.trackmate.visualization.ManualGreenobjectColorGenerator;
import Buddy.plugin.trackmate.visualization.ManualGreenobjectColorGenerator;
import Buddy.plugin.trackmate.visualization.PerEdgeFeatureColorGenerator;
import Buddy.plugin.trackmate.visualization.PerTrackFeatureColorGenerator;
import pluginTools.InteractiveBud;
import pluginTools.InteractiveGreen;

public class GreenConfigureViewsDescriptor implements GreenWizardPanelDescriptor {

	public static final String KEY = "ConfigureViews";

	private final ConfigureViewsPanel panel;

	private final GreenTrackMateGUIController controller;
	

	public GreenConfigureViewsDescriptor(final TrackMate trackmate,
			final FeatureColorGenerator<Greenobject> GreenobjectColorGenerator,
			final PerEdgeFeatureColorGenerator edgeColorGenerator,
			final PerTrackFeatureColorGenerator trackColorGenerator,
			final FeatureColorGenerator<Greenobject> GreenobjectColorGeneratorPerTrackFeature,
			final ManualGreenobjectColorGenerator manualGreenobjectColorGenerator,
			final ManualEdgeColorGenerator manualEdgeColorGenerator, final GreenTrackMateGUIController controller) {
		this.controller = controller;
		this.panel = new ConfigureViewsPanel(trackmate.getModel());
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
		controller.getGUI().setNextButtonEnabled(true);
	}

	@Override
	public void displayingPanel(InteractiveGreen parent) {
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
