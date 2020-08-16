package Buddy.plugin.trackmate;

	import Buddy.plugin.trackmate.gui.GreenTrackMateGUIController;
import Buddy.plugin.trackmate.gui.GuiUtils;
	import Buddy.plugin.trackmate.gui.TrackMateGUIController;
	import ij.IJ;
	import ij.ImageJ;
	import ij.ImagePlus;
	import ij.WindowManager;
	import ij.plugin.PlugIn;
	import net.imglib2.img.display.imagej.ImageJFunctions;
import pluginTools.InteractiveGreen;
public class GreenTrackMatePlugin_ implements PlugIn {

		protected TrackMate trackmate;

		protected InteractiveGreen parent;

		protected GreenSettings settings;

		protected GreenModel model;

		public GreenTrackMatePlugin_(final InteractiveGreen parent) {

			this.parent = parent;
		}
		
		

		/**
		 * Runs the TrackMate GUI plugin.
		 *
		 * @param imagePath
		 *            a path to an image that can be read by ImageJ. If set, the image
		 *            will be opened and TrackMate will be started set to operate on it.
		 *            If <code>null</code> or 0-length, TrackMate will be set to operate
		 *            on the image currently opened in ImageJ.
		 */
		@Override
		public void run(final String imagePath) {
			final ImagePlus imp = ImageJFunctions.show(parent.originalimg);
			int channels;
			int frames;
			if (imp.getNChannels() > imp.getNFrames()) {
				channels = imp.getNFrames();
				frames = imp.getNChannels();

			}

			else {

				channels = imp.getNChannels();
				frames = imp.getNFrames();

			}
			imp.setC(channels);
			imp.setT(frames);
			final int[] dims = imp.getDimensions();
			imp.setDimensions(channels, dims[4], dims[3]);
			GuiUtils.userCheckImpDimensions(imp);

			settings = createSettings(imp);
			model = createModel();
			model.setGreenobjects(parent.Greencells, true);
			trackmate = createTrackMate();

			/*
			 * Launch GUI.
			 */

			final GreenTrackMateGUIController controller = new GreenTrackMateGUIController(parent, trackmate);
			GuiUtils.positionWindow(controller.getGUI(), imp.getWindow());
		}

		/*
		 * HOOKS
		 */

		/**
		 * Hook for subclassers: <br>
		 * Creates the {@link Model} instance that will be used to store data in the
		 * {@link TrackMate} instance.
		 *
		 * @return a new {@link Model} instance.
		 */

		public InteractiveGreen inputparent() {

			return parent;
		}

		protected GreenModel createModel() {
			return new GreenModel();
		}

		/**
		 * Hook for subclassers: <br>
		 * Creates the {@link Settings} instance that will be used to tune the
		 * {@link TrackMate} instance. It is initialized by default with values taken
		 * from the current {@link ImagePlus}.
		 * 
		 * @param imp
		 *            the {@link ImagePlus} to operate on.
		 * @return a new {@link Settings} instance.
		 */
		protected GreenSettings createSettings(final ImagePlus imp) {
			final GreenSettings lSettings = new GreenSettings();
			lSettings.setFrom(imp);
			return lSettings;
		}

		/**
		 * Hook for subclassers: <br>
		 * Creates the TrackMate instance that will be controlled in the GUI.
		 *
		 * @return a new {@link TrackMate} instance.
		 */
		protected TrackMate createTrackMate() {
			/*
			 * Since we are now sure that we will be working on this model with this
			 * settings, we need to pass to the model the units from the settings.
			 */
			final String spaceUnits = settings.imp.getCalibration().getXUnit();
			final String timeUnits = settings.imp.getCalibration().getTimeUnit();

			model.setPhysicalUnits(spaceUnits, timeUnits);

			System.out.println("Starting trackmate");
			return new TrackMate(parent, settings);
		}

		/*
		 * MAIN METHOD
		 */

	}

	
