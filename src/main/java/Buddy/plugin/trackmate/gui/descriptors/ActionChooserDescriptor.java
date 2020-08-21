package Buddy.plugin.trackmate.gui.descriptors;

import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.TrackMateGUIController;
import Buddy.plugin.trackmate.gui.panels.ActionChooserPanel;
import Buddy.plugin.trackmate.gui.panels.ListChooserPanel;
import Buddy.plugin.trackmate.providers.ActionProvider;
import pluginTools.InteractiveBud;
import pluginTools.InteractiveGreen;

public class ActionChooserDescriptor implements WizardPanelDescriptor {

	private static final String KEY = "Actions";

	private final ActionChooserPanel panel;

	
	public final InteractiveBud parent;
	public ActionChooserDescriptor(final InteractiveBud parent, final ActionProvider actionProvider, final TrackMate trackmate,
			final TrackMateGUIController controller) {
		this.parent = parent;
		this.panel = new ActionChooserPanel(parent, actionProvider, trackmate, controller);
	}


	
	@Override
	public ListChooserPanel getComponent() {
		return panel.getPanel();
	}

	@Override
	public void aboutToDisplayPanel() {
	}

	@Override
	public void displayingPanel(InteractiveBud parent) {
	}

	@Override
	public void aboutToHidePanel(InteractiveBud parent) {
	}

	@Override
	public void comingBackToPanel() {
	}

	@Override
	public String getKey() {
		return KEY;
	}


}
