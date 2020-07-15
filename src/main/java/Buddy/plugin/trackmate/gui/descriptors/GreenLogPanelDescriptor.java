package Buddy.plugin.trackmate.gui.descriptors;

import Buddy.plugin.trackmate.gui.LogPanel;
import pluginTools.InteractiveBud;
import pluginTools.InteractiveGreen;

import java.awt.Component;

public class GreenLogPanelDescriptor implements GreenWizardPanelDescriptor {

	public static final String KEY = "LogPanel";

	private final LogPanel logPanel;

	public GreenLogPanelDescriptor(final LogPanel logPanel) {
		this.logPanel = logPanel;
	}

	@Override
	public Component getComponent() {
		return logPanel;
	}

	@Override
	public void aboutToDisplayPanel() {
	}

	@Override
	public void displayingPanel(InteractiveGreen parent) {
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
