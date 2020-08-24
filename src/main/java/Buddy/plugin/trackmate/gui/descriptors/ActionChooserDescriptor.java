package Buddy.plugin.trackmate.gui.descriptors;

import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.TrackMateGUIController;
import Buddy.plugin.trackmate.gui.panels.ActionChooserPanel;
import Buddy.plugin.trackmate.gui.panels.ListChooserPanel;
import Buddy.plugin.trackmate.providers.ActionProvider;

public class ActionChooserDescriptor implements WizardPanelDescriptor
{

	private static final String KEY = "Actions";

	private final ActionChooserPanel panel;

	public ActionChooserDescriptor( final ActionProvider actionProvider, final TrackMate trackmate, final TrackMateGUIController controller )
	{
		this.panel = new ActionChooserPanel( actionProvider, trackmate, controller );
	}

	@Override
	public ListChooserPanel getComponent()
	{
		return panel.getPanel();
	}

	@Override
	public void aboutToDisplayPanel()
	{}

	@Override
	public void displayingPanel()
	{}

	@Override
	public void aboutToHidePanel()
	{}

	@Override
	public void comingBackToPanel()
	{}

	@Override
	public String getKey()
	{
		return KEY;
	}

}
