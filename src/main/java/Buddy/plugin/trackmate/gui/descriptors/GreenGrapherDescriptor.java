package Buddy.plugin.trackmate.gui.descriptors;

import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.GrapherPanel;
import Buddy.plugin.trackmate.gui.GreenTrackMateGUIController;
import pluginTools.InteractiveGreen;

import java.awt.Component;

public class GreenGrapherDescriptor implements GreenWizardPanelDescriptor {

	private static final String KEY = "GraphFeatures";

	private final GrapherPanel panel;

	private final GreenTrackMateGUIController controller;
	

	public GreenGrapherDescriptor(final TrackMate trackmate, final GreenTrackMateGUIController controller) {
		this.panel = new GrapherPanel(trackmate);
		this.controller = controller;
	}



	@Override
	public Component getComponent() {
		return panel;
	}

	@Override
	public void aboutToDisplayPanel() {
		panel.refresh();
	}

	@Override
	public void displayingPanel(InteractiveGreen parent) {
		controller.getGUI().setNextButtonEnabled(true);
	}


	
	@Override
	public void aboutToHidePanel() {
	}

	@Override
	public void comingBackToPanel() {
	}

	@Override
	public String getKey() {
		return KEY;
	}
}
