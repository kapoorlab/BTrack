package Buddy.plugin.trackmate.action;

import javax.swing.ImageIcon;

import Buddy.plugin.trackmate.TrackMateModule;
import Buddy.plugin.trackmate.gui.GreenTrackMateGUIController;
import Buddy.plugin.trackmate.gui.TrackMateGUIController;

public interface GreenTrackMateActionFactory extends TrackMateModule {
	public TrackMateAction create(GreenTrackMateGUIController controller);

}
