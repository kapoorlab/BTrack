package Buddy.plugin.trackmate.gui.wizard.descriptors;

import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.components.GrapherPanel;
import Buddy.plugin.trackmate.gui.displaysettings.DisplaySettings;
import Buddy.plugin.trackmate.gui.wizard.WizardPanelDescriptor;

public class GrapherDescriptor extends WizardPanelDescriptor
{

	private static final String KEY = "GraphFeatures";

	public GrapherDescriptor( final TrackMate trackmate, final DisplaySettings displaySettings )
	{
		super( KEY );
		this.targetPanel = new GrapherPanel( trackmate, displaySettings );
	}

	@Override
	public void aboutToDisplayPanel()
	{
		final GrapherPanel panel = ( GrapherPanel ) targetPanel;
		panel.refresh();
	}
}
