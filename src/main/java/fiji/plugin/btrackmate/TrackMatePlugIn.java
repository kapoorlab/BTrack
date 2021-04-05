package fiji.plugin.btrackmate;

import static fiji.plugin.btrackmate.gui.Icons.TRACKMATE_ICON;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fiji.plugin.btrackmate.gui.GuiUtils;
import fiji.plugin.btrackmate.gui.displaysettings.DisplaySettings;
import fiji.plugin.btrackmate.gui.displaysettings.DisplaySettingsIO;
import fiji.plugin.btrackmate.gui.wizard.TrackMateWizardSequence;
import fiji.plugin.btrackmate.gui.wizard.WizardSequence;
import fiji.plugin.btrackmate.visualization.TrackMateModelView;
import fiji.plugin.btrackmate.visualization.hyperstack.HyperStackDisplayer;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.InteractiveBud;

public class TrackMatePlugIn implements PlugIn {

	protected InteractiveBud parent;

	public TrackMatePlugIn(final InteractiveBud parent) {

		this.parent = parent;
	}

	@Override
	public void run(final String imagePath) {
		GuiUtils.setSystemLookAndFeel();

		ImagePlus imp = null;
		imp = Reshape3D(parent.originalimg, "");
		// Main objects.
		final Settings settings = createSettings(imp);
		final Model model = createModel();
		model.setSpots(parent.budcells, true);
		final TrackMate btrackmate = createTrackMate(model, settings);
		final SelectionModel selectionModel = new SelectionModel(model);
		final DisplaySettings displaySettings = createDisplaySettings();

		// Main view.
		final TrackMateModelView displayer = new HyperStackDisplayer(model, selectionModel, imp, displaySettings);
		displayer.render();

		// Wizard.
		final WizardSequence sequence = createSequence(btrackmate, selectionModel, displaySettings);
		final JFrame frame = sequence.run("BTrackMate" + imp.getShortTitle());
		frame.setIconImage(TRACKMATE_ICON.getImage());
		GuiUtils.positionWindow(frame, imp.getWindow());
		frame.setVisible(true);
	}

	/**
	 * Hook for subclassers: <br>
	 * Will create and position the sequence that will be played by the wizard
	 * launched by this plugin.
	 * 
	 * @param btrackmate
	 * @param selectionModel
	 * @param displaySettings
	 * @return
	 */
	protected WizardSequence createSequence(final TrackMate btrackmate, final SelectionModel selectionModel,
			final DisplaySettings displaySettings) {
		return new TrackMateWizardSequence(btrackmate, selectionModel, displaySettings);
	}

	public static ImagePlus Reshape3D(RandomAccessibleInterval<FloatType> image, String title) {

		int channels, frames;

		ImagePlus imp = ImageJFunctions.wrapFloat(image, title);
		if (imp.getNChannels() > imp.getNFrames()) {
			channels = imp.getNFrames();
			frames = imp.getNChannels();

		}

		else {

			channels = imp.getNChannels();
			frames = imp.getNFrames();

		}

		imp.setDimensions(channels, frames, imp.getNSlices());
		imp.show();

		return imp;

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
	protected Model createModel() {
		return new Model();
	}

	/**
	 * Hook for subclassers: <br>
	 * Creates the {@link Settings} instance that will be used to tune the
	 * {@link TrackMate} instance. It is initialized by default with values taken
	 * from the current {@link ImagePlus}.
	 *
	 * @param imp the {@link ImagePlus} to operate on.
	 * @return a new {@link Settings} instance.
	 */
	protected Settings createSettings(final ImagePlus imp) {
		final Settings ls = new Settings();
		ls.setFrom(imp);
		ls.addAllAnalyzers();
		return ls;
	}

	/**
	 * Hook for subclassers: <br>
	 * Creates the TrackMate instance that will be controlled in the GUI.
	 *
	 * @return a new {@link TrackMate} instance.
	 */
	protected TrackMate createTrackMate(final Model model, final Settings settings) {
		/*
		 * Since we are now sure that we will be working on this model with this
		 * settings, we need to pass to the model the units from the settings.
		 */
		final String spaceUnits = settings.imp.getCalibration().getXUnit();
		final String timeUnits = settings.imp.getCalibration().getTimeUnit();
		model.setPhysicalUnits(spaceUnits, timeUnits);

		return new TrackMate(model, settings);
	}

	protected DisplaySettings createDisplaySettings() {
		return DisplaySettingsIO.readUserDefault().copy("CurrentDisplaySettings");
	}

	public static void main(final String[] args) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		ImageJ.main(args);

	}
}
