package Buddy.plugin.trackmate.action;


import Buddy.plugin.trackmate.TrackMateModule;
import Buddy.plugin.trackmate.gui.GreenTrackMateGUIController;

public interface GreenTrackMateActionFactory extends TrackMateModule {
	public GreenTrackMateAction create(GreenTrackMateGUIController controller);

}
