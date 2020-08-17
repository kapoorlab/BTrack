package Buddy.plugin.trackmate.gui.descriptors;

import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.GreenTrackMateGUIController;
import Buddy.plugin.trackmate.gui.panels.GreenActionChooserPanel;
import Buddy.plugin.trackmate.gui.panels.ListChooserPanel;
import Buddy.plugin.trackmate.providers.GreenActionProvider;
import pluginTools.InteractiveGreen;

public class GreenActionChooserDescriptor implements GreenWizardPanelDescriptor {

	private static final String KEY = "Actions";

	private final GreenActionChooserPanel panel;

	public GreenActionChooserDescriptor(final GreenActionProvider actionProvider, final TrackMate trackmate,
			final GreenTrackMateGUIController controller) {
		this.panel = new GreenActionChooserPanel(actionProvider, trackmate, controller);
	}


	
	@Override
	public ListChooserPanel getComponent() {
		return panel.getPanel();
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
