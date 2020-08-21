package Buddy.plugin.trackmate.action;

import javax.swing.ImageIcon;

import Buddy.plugin.trackmate.TrackMateModule;
import Buddy.plugin.trackmate.gui.TrackMateGUIController;
import pluginTools.InteractiveBud;

public interface TrackMateActionFactory extends TrackMateModule {
	public TrackMateAction create(InteractiveBud parent, TrackMateGUIController controller);

	

}
