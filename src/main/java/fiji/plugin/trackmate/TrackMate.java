package Buddy.plugin.trackmate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.scijava.util.VersionUtils;

import budDetector.Spot;
import Buddy.plugin.trackmate.features.SpotFeatureCalculator;
import Buddy.plugin.trackmate.features.EdgeFeatureCalculator;
import Buddy.plugin.trackmate.features.FeatureFilter;
import Buddy.plugin.trackmate.features.TrackFeatureCalculator;
import Buddy.plugin.trackmate.tracking.SpotTracker;
import Buddy.plugin.trackmate.util.TMUtils;
import ij.gui.ShapeRoi;
import net.imagej.ImgPlus;
import net.imglib2.Interval;
import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.multithreading.SimpleMultiThreading;
import pluginTools.InteractiveBud;

/**
 * <p>
 * The TrackMate_ class runs on the currently active time-lapse image (2D or 3D)
 * and both identifies and tracks bright Spots over time.
 * </p>
 *
 * <p>
 * <b>Required input:</b> A 2D or 3D time-lapse image with bright blobs.
 * </p>
 *
 * @author Nicholas Perry
 * @author Johannes Schindelin
 * @author Jean-Yves Tinevez - Institut Pasteur - July 2010 - 2018
 */
@SuppressWarnings("deprecation")
public class TrackMate implements Benchmark, MultiThreaded, Algorithm {

	public static final String PLUGIN_NAME_STR = "BTrackMate";

	public static final String PLUGIN_NAME_VERSION = VersionUtils.getVersion(TrackMate.class);

	/**
	 * The model this trackmate will shape.
	 */

	protected InteractiveBud parent;

	
	protected final Model model;
	

	protected final Settings settings;
	

	protected long processingTime;

	protected String errorMessage;

	protected int numThreads = Runtime.getRuntime().availableProcessors();

	/*
	 * CONSTRUCTORS
	 */

	public TrackMate(final InteractiveBud parent, final Model model, final Settings settings) {

		this.parent = parent;

		System.out.println("Created BTrackmate Model");
		
		this.model = model;
		this.settings = settings;
		
	}
	
	

	
	

	/*
	 * PROTECTED METHODS
	 */

	/**
	 * This method exists for the following reason:
	 * <p>
	 * The detector receives at each frame a cropped image to operate on, depending
	 * on the user specifying a ROI. It therefore returns Spots whose
	 * coordinates are with respect to the top-left corner of the ROI, not of the
	 * original image.
	 * <p>
	 * This method modifies the given Spots to put them back in the image
	 * coordinate system. Additionally, is a non-square ROI was specified (e.g. a
	 * polygon), it prunes the Spots that are not within the polygon of the
	 * ROI.
	 *
	 * @param SpotsThisFrame
	 *            the Spot list to inspect
	 * @param lSettings
	 *            the {@link Settings} object that will be used to retrieve the
	 *            image ROI and cropping information
	 * @return a list of Spot. Depending on the presence of a polygon ROI, it
	 *         might be a new, pruned list. Or not.
	 */
	protected List<Spot> translateAndPruneSpots(final InteractiveBud parent, final List<Spot> SpotsThisFrame,
			final Settings lSettings) {

		// Put them back in the right referential
		TMUtils.translateSpots(SpotsThisFrame, lSettings.xstart ,
				lSettings.ystart , lSettings.zstart );
		List<Spot> prunedSpots;
		// Prune if outside of ROI
		if (lSettings.roi instanceof ShapeRoi) {
			prunedSpots = new ArrayList<>();
			for (final Spot Spot : SpotsThisFrame) {
				if (lSettings.roi.contains(
						(int) Math.round(Spot.getFeature(Spot.POSITION_X) ),
						(int) Math.round(Spot.getFeature(Spot.POSITION_Y))))
					prunedSpots.add(Spot);
			}
		} else if (null != lSettings.polygon) {
			prunedSpots = new ArrayList<>();
			for (final Spot Spot : SpotsThisFrame) {
				if (lSettings.polygon.contains(Spot.getFeature(Spot.POSITION_X) ,
						Spot.getFeature(Spot.POSITION_Y) ))
					prunedSpots.add(Spot);
			}
		} else {
			prunedSpots = SpotsThisFrame;
		}
		return prunedSpots;
	}
	
	
	
	/*
	 * METHODS
	 */

	public Model getModel() {
		return model;
	}


	public Settings getSettings() {
		return settings;
	}
	
	
	public InteractiveBud getParent() {
		
		return parent;
	}
	

	/*
	 * PROCESSES
	 */

	/**
	 * Calculate all features for all detected Spots.
	 * <p>
	 * Features are calculated for each Spot, using their location, and the
	 * raw image. Features to be calculated and analyzers are taken from the
	 * settings field of this object.
	 *
	 * @param doLogIt
	 *            if <code>true</code>, the {@link Logger} of the model will be
	 *            notified.
	 * @return <code>true</code> if the calculation was performed successfully,
	 *         <code>false</code> otherwise.
	 */
	public boolean computeSpotFeatures(final boolean doLogIt) {
		final Logger logger = model.getLogger();
		logger.log("Computing Spot features.\n");
		final SpotFeatureCalculator calculator = new SpotFeatureCalculator(model, settings);
		calculator.setNumThreads(numThreads);
		if (calculator.checkInput() && calculator.process()) {
			if (doLogIt)
				logger.log("Computation done in " + calculator.getProcessingTime() + " ms.\n");
			return true;
		}

		errorMessage = "Spot features calculation failed:\n" + calculator.getErrorMessage();
		return false;
	}

	

	
	/**
	 * Calculate all features for all detected Spots.
	 * <p>
	 * Features are calculated for each Spot, using their location, and the
	 * raw image. Features to be calculated and analyzers are taken from the
	 * settings field of this object.
	 *
	 * @param doLogIt
	 *            if <code>true</code>, the {@link Logger} of the model will be
	 *            notified.
	 * @return <code>true</code> if the calculation was performed successfuly,
	 *         <code>false</code> otherwise.
	 */
	public boolean computeEdgeFeatures(final boolean doLogIt) {
		final Logger logger = model.getLogger();
		final EdgeFeatureCalculator calculator = new EdgeFeatureCalculator(model, settings);
		calculator.setNumThreads(numThreads);
		if (!calculator.checkInput() || !calculator.process()) {
			errorMessage = "Edge features calculation failed:\n" + calculator.getErrorMessage();
			return false;
		}
		if (doLogIt) {
			logger.log("Computation done in " + calculator.getProcessingTime() + " ms.\n");
		}
		return true;
	}

	/**
	 * Calculate all features for all tracks.
	 *
	 * @param doLogIt
	 *            if <code>true</code>, messages will be sent to the logger.
	 * @return <code>true</code> if the computation completed without errors.
	 */
	public boolean computeTrackFeatures(final boolean doLogIt) {
		final Logger logger = model.getLogger();
		final TrackFeatureCalculator calculator = new TrackFeatureCalculator(model, settings);
		calculator.setNumThreads(numThreads);
		if (calculator.checkInput() && calculator.process()) {
			if (doLogIt)
				logger.log("Computation done in " + calculator.getProcessingTime() + " ms.\n");
			return true;
		}

		errorMessage = "Track features calculation failed:\n" + calculator.getErrorMessage();
		return false;
	}

	/**
	 * Execute the tracking part.
	 * <p>
	 * This method links all the selected Spots from the thresholding part
	 * using the selected tracking algorithm. This tracking process will generate a
	 * graph (more precisely a {@link org.jgrapht.graph.SimpleWeightedGraph}) made
	 * of the Spot election for its vertices, and edges representing the
	 * links.
	 * <p>
	 * The {@link ModelChangeListener}s of the model will be notified when the
	 * successful process is over.
	 *
	 * @return <code>true</code> if the computation completed without errors.
	 */
	public boolean execTracking() {
		final Logger logger = model.getLogger();
		logger.log("Starting tracking process.\n");
		final SpotTracker tracker = settings.trackerFactory.create(parent.budcells, settings.trackerSettings);
		tracker.setNumThreads(numThreads);
		tracker.setLogger(logger);
		if (tracker.checkInput() && tracker.process()) {
			model.setTracks(tracker.getResult(), true);
			return true;
		}
		System.out.println(parent.budcells.keySet().size() + " Parent and Bud " + model.Spots.keySet().size());
		errorMessage = "Tracking process failed:\n" + tracker.getErrorMessage();
		return false;
	}

	/**
	 * Execute the detection part.
	 * <p>
	 * This method configure the chosen {@link Settings#detectorFactory} with the
	 * source image and the detectr settings and execute the detection process for
	 * all the frames set in the {@link Settings} object of the target model.
	 *
	 * @return true if the whole detection step has executed correctly.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean execDetection() {

		model.setSpots(parent.budcells, true);

		return true;
	}

	/**
	 * Execute the initial Spot filtering part.
	 * <p>
	 * Because of the presence of noise, it is possible that some of the regional
	 * maxima found in the detection step have identified noise, rather than objects
	 * of interest. This can generates a very high number of Spots, which is
	 * inconvenient to deal with when it comes to computing their features, or
	 * displaying them.
	 * <p>
	 * Any {@link SpotDetector} is expected to at least compute the
	 * {@link Spot#QUALITY} value for each Spot it creates, so it is
	 * possible to set up an initial filtering on this feature, prior to any other
	 * operation.
	 * <p>
	 * This method simply takes all the detected Spots, and discard those
	 * whose quality value is below the threshold set by
	 * {@link Settings#initialSpotFilterValue}. The Spot field is
	 * overwritten, and discarded Spots can't be recalled.
	 * <p>
	 * The {@link ModelChangeListener}s of this model will be notified with a
	 * {@link ModelChangeEvent#SpotS_COMPUTED} event.
	 *
	 * @return <code>true</code> if the computation completed without errors.
	 */
	public boolean execInitialSpotFiltering() {
		final Logger logger = model.getLogger();
		logger.log("Starting initial filtering process.\n");

		SpotCollection Spots = model.getSpots();
		Spots = Spots.crop();

		model.setSpots(Spots, true); // Forget about the previous one
		return true;
	}

	/**
	 * Execute the Spot feature filtering part.
	 * <p>
	 * Because of the presence of noise, it is possible that some of the regional
	 * maxima found in the detection step have identified noise, rather than objects
	 * of interest. A filtering operation based on the calculated features in this
	 * step should allow to rule them out.
	 * <p>
	 * This method simply takes all the detected Spots, and mark as visible
	 * the Spots whose features satisfy all of the filters in the
	 * {@link Settings} object.
	 * <p>
	 * The {@link ModelChangeListener}s of this model will be notified with a
	 * {@link ModelChangeEvent#SpotS_FILTERED} event.
	 *
	 * @param doLogIt
	 *            if <code>true</code>, will send a message to the model logger.
	 * @return <code>true</code> if the computation completed without errors.
	 */
	public boolean execSpotFiltering(final boolean doLogIt) {
		if (doLogIt) {
			final Logger logger = model.getLogger();
			logger.log("Starting Spot filtering process.\n");
		}
		model.filterSpots(settings.getSpotFilters(), true);
		return true;
	}

	public boolean execTrackFiltering(final boolean doLogIt) {
		if (doLogIt) {
			final Logger logger = model.getLogger();
			logger.log("Starting track filtering process.\n");
		}

		model.beginUpdate();
		try {
			for (final Integer trackID : model.getTrackModel().trackIDs(false)) {
				boolean trackIsOk = true;
				for (final FeatureFilter filter : settings.getTrackFilters()) {
					final Double tval = filter.value;
					final Double val = model.getFeatureModel().getTrackFeature(trackID, filter.feature);
					if (null == val)
						continue;

					if (filter.isAbove) {
						if (val < tval) {
							trackIsOk = false;
							break;
						}
					} else {
						if (val > tval) {
							trackIsOk = false;
							break;
						}
					}
				}
				model.setTrackVisibility(trackID, trackIsOk);
			}
		} finally {
			model.endUpdate();
		}
		return true;
	}

	@Override
	public String toString() {
		return PLUGIN_NAME_STR + "v" + PLUGIN_NAME_VERSION;
	}

	/*
	 * ALGORITHM METHODS
	 */

	@Override
	public boolean checkInput() {
		if (null == model) {
			errorMessage = "The model is null.\n";
			return false;
		}
		if (null == settings) {
			errorMessage = "Settings are null";
			return false;
		}
		if (!settings.checkValidity()) {
			errorMessage = settings.getErrorMessage();
			return false;
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public boolean process() {
		if (!execDetection()) {
			return false;
		}

		if (!execInitialSpotFiltering()) {
			return false;
		}

		if (!computeSpotFeatures(true)) {
			return false;
		}

		if (!execSpotFiltering(true)) {
			return false;
		}

		if (!execTracking()) {
			return false;
		}

		if (!computeTrackFeatures(true)) {
			return false;
		}

		if (!execTrackFiltering(true)) {
			return false;
		}

		if (!computeEdgeFeatures(true)) {
			return false;
		}

		return true;
	}

	@Override
	public int getNumThreads() {
		return numThreads;
	}

	@Override
	public void setNumThreads() {
		this.numThreads = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public void setNumThreads(final int numThreads) {
		this.numThreads = numThreads;
	}

	@Override
	public long getProcessingTime() {
		return processingTime;
	}
}
