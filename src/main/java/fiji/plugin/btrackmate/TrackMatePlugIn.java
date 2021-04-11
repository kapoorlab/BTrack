package fiji.plugin.btrackmate;

import static fiji.plugin.btrackmate.gui.Icons.TRACKMATE_ICON;
import static fiji.plugin.btrackmate.gui.Icons.TRACKMATE_ICON;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fiji.plugin.btrackmate.gui.GuiUtils;
import fiji.plugin.btrackmate.gui.displaysettings.DisplaySettings;
import fiji.plugin.btrackmate.gui.displaysettings.DisplaySettingsIO;
import fiji.plugin.btrackmate.gui.wizard.BTrackMateWizardSequence;
import fiji.plugin.btrackmate.gui.wizard.WizardSequence;
import fiji.plugin.btrackmate.visualization.TrackMateModelView;
import fiji.plugin.btrackmate.visualization.hyperstack.HyperStackDisplayer;
import fiji.plugin.btrackmate.Model;
import fiji.plugin.btrackmate.SelectionModel;
import fiji.plugin.btrackmate.Settings;
import fiji.plugin.btrackmate.TrackMate;
import fiji.plugin.btrackmate.TrackMatePlugIn;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;

public class TrackMatePlugIn implements PlugIn
{

	@Override
	public void run( final String imagePath )
	{
		GuiUtils.setSystemLookAndFeel();
		final ImagePlus imp;
		if ( imagePath != null && imagePath.length() > 0 )
		{
			imp = IJ.openImage( imagePath );
			if ( null == imp.getOriginalFileInfo() )
			{
				IJ.error( TrackMate.PLUGIN_NAME_STR + " v" + TrackMate.PLUGIN_NAME_VERSION, "Could not load image with path " + imagePath + "." );
				return;
			}
		}
		else
		{
			imp = WindowManager.getCurrentImage();
			if ( null == imp )
			{
				IJ.error( TrackMate.PLUGIN_NAME_STR + " v" + TrackMate.PLUGIN_NAME_VERSION,
						"Please open an image before running TrackMate." );
				return;
			}
			else if ( imp.getType() == ImagePlus.COLOR_RGB )
			{
				IJ.error( TrackMate.PLUGIN_NAME_STR + " v" + TrackMate.PLUGIN_NAME_VERSION,
						"TrackMate does not work on RGB images." );
				return;
			}
		}

		imp.setOpenAsHyperStack( true );
		imp.setDisplayMode( IJ.COMPOSITE );
		if ( !imp.isVisible() )
			imp.show();

		GuiUtils.userCheckImpDimensions( imp );

		// Main objects.
		final Settings settings = createSettings( imp );
		final Model model = createModel( imp );
		final TrackMate btrackmate = createTrackMate( model, settings );
		final SelectionModel selectionModel = new SelectionModel( model );
		final DisplaySettings displaySettings = createDisplaySettings();

		// Main view.
		

		// Wizard.
		final WizardSequence sequence = createSequence( btrackmate, selectionModel, displaySettings );
		final JFrame frame = sequence.run( "BTrackMate on " + imp.getShortTitle() );
		frame.setIconImage( TRACKMATE_ICON.getImage() );
		GuiUtils.positionWindow( frame, imp.getWindow() );
		frame.setVisible( true );
	}

	/**
	 * Hook for subclassers: <br>
	 * Will create and position the sequence that will be played by the wizard
	 * launched by this plugin.
	 * 
	 * @param trackmate
	 * @param selectionModel
	 * @param displaySettings
	 * @return
	 */
	protected WizardSequence createSequence( final TrackMate btrackmate, final SelectionModel selectionModel, final DisplaySettings displaySettings )
	{
		return new BTrackMateWizardSequence( btrackmate, selectionModel, displaySettings);
	}

	/**
	 * Hook for subclassers: <br>
	 * Creates the {@link Model} instance that will be used to store data in the
	 * {@link TrackMate} instance.
	 * 
	 * @param imp
	 *
	 * @return a new {@link Model} instance.
	 */
	protected Model createModel( final ImagePlus imp )
	{
		final Model model = new Model();
		model.setPhysicalUnits(
				imp.getCalibration().getUnit(),
				imp.getCalibration().getTimeUnit() );
		return model;
	}

	/**
	 * Hook for subclassers: <br>
	 * Creates the {@link Settings} instance that will be used to tune the
	 * {@link TrackMate} instance. It is initialized by default with values
	 * taken from the current {@link ImagePlus}.
	 *
	 * @param imp
	 *            the {@link ImagePlus} to operate on.
	 * @return a new {@link Settings} instance.
	 */
	protected Settings createSettings( final ImagePlus imp )
	{
		final Settings ls = new Settings();
		ls.setFrom( imp );
		ls.addAllAnalyzers();
		return ls;
	}

	/**
	 * Hook for subclassers: <br>
	 * Creates the TrackMate instance that will be controlled in the GUI.
	 *
	 * @return a new {@link TrackMate} instance.
	 */
	protected TrackMate createTrackMate( final Model model, final Settings settings )
	{
		/*
		 * Since we are now sure that we will be working on this model with this
		 * settings, we need to pass to the model the units from the settings.
		 */
		final String spaceUnits = settings.imp.getCalibration().getXUnit();
		final String timeUnits = settings.imp.getCalibration().getTimeUnit();
		model.setPhysicalUnits( spaceUnits, timeUnits );

		return new TrackMate( model, settings );
	}

	protected DisplaySettings createDisplaySettings()
	{
		return DisplaySettingsIO.readUserDefault().copy( "CurrentDisplaySettings" );
	}

	public static void main( final String[] args ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		ImageJ.main( args );
//		new TrackMatePlugIn().run( "samples/Stack.tif" );
//		new TrackMatePlugIn().run( "samples/Merged.tif" );
		new TrackMatePlugIn().run("/Users/aimachine/Downloads/CellTracking/Raw.tif");
//		new TrackMatePlugIn().run( "samples/Mask.tif" );
//		new TrackMatePlugIn().run( "samples/FakeTracks.tif" );
	}
}
