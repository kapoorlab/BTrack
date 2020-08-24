package Buddy.plugin.trackmate.visualization.hyperstack;

import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.Settings;
import Buddy.plugin.trackmate.visualization.TrackMateModelView;
import Buddy.plugin.trackmate.visualization.ViewFactory;
import ij.ImagePlus;
import pluginTools.InteractiveBud;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

@Plugin( type = ViewFactory.class )
public class HyperStackDisplayerFactory implements ViewFactory
{

	private static final String INFO_TEXT = "<html>" + "This displayer overlays the spots and tracks on the current <br>" + "ImageJ hyperstack window. <br>" + "<p> " + "This displayer allows manual editing of spots, thanks to the spot <br> " + "edit tool that appear in ImageJ toolbar." + "<p>" + "Double-clicking in a spot toggles the editing mode: The spot can <br> " + "be moved around in a XY plane by mouse dragging. To move it in Z <br>" + "or in time, simply change the current plane and time-point by <br>" + "using the hyperstack sliders. To change its radius, hold the <br>" + "<tt>alt</tt> key down and rotate the mouse-wheel. Holding the <br>" + "<tt>shift</tt> key on top changes it faster. " + "<p>" + "Alternatively, keyboard can be used to edit spots:<br/>" + " - <b>A</b> creates a new spot under the mouse.<br/>" + " - <b>D</b> deletes the spot under the mouse.<br/>" + " - <b>Q</b> and <b>E</b> decreases and increases the radius of the spot " + "under the mouse (shift to go faster).<br/>" + " - <b>Space</b> + mouse drag moves the spot under the mouse.<br/>" + "<p>" + "To toggle links between two spots, select two spots (Shift+Click), <br>" + "then press <b>L</b>. " + "<p>" + "<b>Shift+L</b> toggle the auto-linking mode on/off. <br>" + "If on, every spot created will be automatically linked with the spot <br>" + "currently selected, if they are in subsequent frames." + "</html>";

	private static final String NAME = "HyperStack Displayer";

	@Override
	public TrackMateModelView create(final InteractiveBud parent, final Model model, final Settings settings, final SelectionModel selectionModel )
	{
		final ImagePlus imp;
		if ( settings == null )
		{
			imp = null;
		}
		else
		{
			imp = settings.imp;
		}
		return new HyperStackDisplayer( parent, model, selectionModel, imp );
	}

	@Override
	public String getInfoText()
	{
		return INFO_TEXT;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public String getKey()
	{
		return HyperStackDisplayer.KEY;
	}

	@Override
	public ImageIcon getIcon()
	{
		return null;
	}

}
