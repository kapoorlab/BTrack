package fiji.plugin.trackmate.action;

import javax.swing.ImageIcon;

import fiji.plugin.trackmate.TrackMateModule;
import fiji.plugin.trackmate.gui.TrackMateGUIController;

public interface TrackMateActionFactory extends TrackMateModule
{
	public TrackMateAction create( TrackMateGUIController controller );

	
}
