package Buddy.plugin.trackmate.gui.wizard.descriptors;

import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.components.ActionChooserPanel;
import Buddy.plugin.trackmate.gui.displaysettings.DisplaySettings;
import Buddy.plugin.trackmate.gui.wizard.WizardPanelDescriptor;
import Buddy.plugin.trackmate.providers.ActionProvider;

public class ActionChooserDescriptor extends WizardPanelDescriptor
{

	private static final String KEY = "Actions";

	public ActionChooserDescriptor( final ActionProvider actionProvider, final TrackMate trackmate, final SelectionModel selectionModel, final DisplaySettings displaySettings )
	{
		super( KEY );
		this.targetPanel = new ActionChooserPanel( actionProvider, trackmate, selectionModel, displaySettings );
	}
}
