package Buddy.plugin.trackmate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.scijava.util.VersionUtils;

import greenDetector.Greenobject;
import Buddy.plugin.trackmate.features.GreenobjectFeatureCalculator;
import Buddy.plugin.trackmate.features.EdgeFeatureCalculator;
import Buddy.plugin.trackmate.features.FeatureFilter;
import Buddy.plugin.trackmate.features.GreenEdgeFeatureCalculator;
import Buddy.plugin.trackmate.features.GreenTrackFeatureCalculator;
import Buddy.plugin.trackmate.features.GreenobjectFeatureCalculator;
import Buddy.plugin.trackmate.features.TrackFeatureCalculator;
import Buddy.plugin.trackmate.tracking.GreenobjectTracker;
import Buddy.plugin.trackmate.util.GreenTMUtils;
import Buddy.plugin.trackmate.util.TMUtils;
import ij.gui.ShapeRoi;
import net.imagej.ImgPlus;
import net.imglib2.Interval;
import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.multithreading.SimpleMultiThreading;
import pluginTools.InteractiveBud;
import pluginTools.InteractiveGreen;

/**
 * <p>
 * The TrackMate_ class runs on the currently active time-lapse image (2D or 3D)
 * and both identifies and tracks bright Greenobjects over time.
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
public class GreenTrackMate implements Benchmark, MultiThreaded, Algorithm {

	public static final String PLUGIN_NAME_STR = "BTrackMate";

	public static final String PLUGIN_NAME_VERSION = VersionUtils.getVersion(GreenTrackMate.class);

	/**
	 * The model this trackmate will shape.
	 */

	protected InteractiveGreen parent;

	
	protected final GreenModel model;
	

	protected final GreenSettings settings;
	

	protected long processingTime;

	protected String errorMessage;

	protected int numThreads = Runtime.getRuntime().availableProcessors();

	/*
	 * CONSTRUCTORS
	 */

	public GreenTrackMate(final InteractiveGreen parent, final GreenSettings settings) {

		this.parent = parent;
		final GreenModel model = new GreenModel();
		
		//model.setGreenobjects(parent.Greencells, true);

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
	 * on the user specifying a ROI. It therefore returns Greenobjects whose
	 * coordinates are with respect to the top-left corner of the ROI, not of the
	 * original image.
	 * <p>
	 * This method modifies the given Greenobjects to put them back in the image
	 * coordinate system. Additionally, is a non-square ROI was specified (e.g. a
	 * polygon), it prunes the Greenobjects that are not within the polygon of the
	 * ROI.
	 *
	 * @param GreenobjectsThisFrame
	 *            the Greenobject list to inspect
	 * @param lSettings
	 *            the {@link Settings} object that will be used to retrieve the
	 *            image ROI and cropping information
	 * @return a list of Greenobject. Depending on the presence of a polygon ROI, it
	 *         might be a new, pruned list. Or not.
	 */
	protected List<Greenobject> translateAndPruneGreenobjects(final List<Greenobject> GreenobjectsThisFrame,
			final GreenSettings lSettings) {

		// Put them back in the right referential
		final double[] calibration = TMUtils.getSpatialCalibration(lSettings.imp);
		GreenTMUtils.translateGreenobjects(GreenobjectsThisFrame, lSettings.xstart ,
				lSettings.ystart , lSettings.zstart );
		List<Greenobject> prunedGreenobjects;
		// Prune if outside of ROI
		if (lSettings.roi instanceof ShapeRoi) {
			prunedGreenobjects = new ArrayList<>();
			for (final Greenobject Greenobject : GreenobjectsThisFrame) {
				if (lSettings.roi.contains(
						(int) Math.round(Greenobject.getFeature(Greenobject.POSITION_X) ),
						(int) Math.round(Greenobject.getFeature(Greenobject.POSITION_Y))))
					prunedGreenobjects.add(Greenobject);
			}
		} else if (null != lSettings.polygon) {
			prunedGreenobjects = new ArrayList<>();
			for (final Greenobject Greenobject : GreenobjectsThisFrame) {
				if (lSettings.polygon.contains(Greenobject.getFeature(Greenobject.POSITION_X) ,
						Greenobject.getFeature(Greenobject.POSITION_Y) ))
					prunedGreenobjects.add(Greenobject);
			}
		} else {
			prunedGreenobjects = GreenobjectsThisFrame;
		}
		return prunedGreenobjects;
	}
	
	
	protected List<Greenobject> translateAndPruneGreenobjects(final List<Greenobject> GreenobjectsThisFrame,
			final Settings lSettings) {

		// Put them back in the right referential
		final double[] calibration = GreenTMUtils.getSpatialCalibration(lSettings.imp);
		GreenTMUtils.translateGreenobjects(GreenobjectsThisFrame, lSettings.xstart ,
				lSettings.ystart , lSettings.zstart );
		List<Greenobject> prunedGreenobjects;
		// Prune if outside of ROI
		if (lSettings.roi instanceof ShapeRoi) {
			prunedGreenobjects = new ArrayList<>();
			for (final Greenobject Greenobject : GreenobjectsThisFrame) {
				if (lSettings.roi.contains(
						(int) Math.round(Greenobject.getFeature(Greenobject.POSITION_X) ),
						(int) Math.round(Greenobject.getFeature(Greenobject.POSITION_Y))))
					prunedGreenobjects.add(Greenobject);
			}
		} else if (null != lSettings.polygon) {
			prunedGreenobjects = new ArrayList<>();
			for (final Greenobject Greenobject : GreenobjectsThisFrame) {
				if (lSettings.polygon.contains(Greenobject.getFeature(Greenobject.POSITION_X) ,
						Greenobject.getFeature(Greenobject.POSITION_Y) ))
					prunedGreenobjects.add(Greenobject);
			}
		} else {
			prunedGreenobjects = GreenobjectsThisFrame;
		}
		return prunedGreenobjects;
	}

	/*
	 * METHODS
	 */

	public GreenModel getGreenModel() {
		return model;
	}


	public GreenSettings getGreenSettings() {
		return settings;
	}
	
	

	/*
	 * PROCESSES
	 */

	/**
	 * Calculate all features for all detected Greenobjects.
	 * <p>
	 * Features are calculated for each Greenobject, using their location, and the
	 * raw image. Features to be calculated and analyzers are taken from the
	 * settings field of this object.
	 *
	 * @param doLogIt
	 *            if <code>true</code>, the {@link Logger} of the model will be
	 *            notified.
	 * @return <code>true</code> if the calculation was performed successfully,
	 *         <code>false</code> otherwise.
	 */
	public boolean computeGreenobjectFeatures(final boolean doLogIt) {
		final Logger logger = model.getLogger();
		logger.log("Computing Greenobject features.\n");
		final GreenobjectFeatureCalculator calculator = new GreenobjectFeatureCalculator(model, settings);
		calculator.setNumThreads(numThreads);
		if (calculator.checkInput() && calculator.process()) {
			if (doLogIt)
				logger.log("Computation done in " + calculator.getProcessingTime() + " ms.\n");
			return true;
		}

		errorMessage = "Greenobject features calculation failed:\n" + calculator.getErrorMessage();
		return false;
	}

	

	
	/**
	 * Calculate all features for all detected Greenobjects.
	 * <p>
	 * Features are calculated for each Greenobject, using their location, and the
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
		final GreenEdgeFeatureCalculator calculator = new GreenEdgeFeatureCalculator(model, settings);
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
		final GreenTrackFeatureCalculator calculator = new GreenTrackFeatureCalculator(model, settings);
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
	 * This method links all the selected Greenobjects from the thresholding part
	 * using the selected tracking algorithm. This tracking process will generate a
	 * graph (more precisely a {@link org.jgrapht.graph.SimpleWeightedGraph}) made
	 * of the Greenobject election for its vertices, and edges representing the
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
		final GreenobjectTracker tracker = settings.trackerFactory.create(parent, settings.trackerSettings);
		tracker.setNumThreads(numThreads);
		tracker.setLogger(logger);
		if (tracker.checkInput() && tracker.process()) {
			model.setTracks(tracker.getResult(), true);
			return true;
		}
		System.out.println(parent.Greencells.keySet().size() + " Parent and Green " + model.Greenobjects.keySet().size());
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

		//model.setGreenobjects(parent.Greencells, true);

		return true;
	}

	/**
	 * Execute the initial Greenobject filtering part.
	 * <p>
	 * Because of the presence of noise, it is possible that some of the regional
	 * maxima found in the detection step have identified noise, rather than objects
	 * of interest. This can generates a very high number of Greenobjects, which is
	 * inconvenient to deal with when it comes to computing their features, or
	 * displaying them.
	 * <p>
	 * Any {@link GreenobjectDetector} is expected to at least compute the
	 * {@link Greenobject#QUALITY} value for each Greenobject it creates, so it is
	 * possible to set up an initial filtering on this feature, prior to any other
	 * operation.
	 * <p>
	 * This method simply takes all the detected Greenobjects, and discard those
	 * whose quality value is below the threshold set by
	 * {@link Settings#initialGreenobjectFilterValue}. The Greenobject field is
	 * overwritten, and discarded Greenobjects can't be recalled.
	 * <p>
	 * The {@link ModelChangeListener}s of this model will be notified with a
	 * {@link ModelChangeEvent#GreenobjectS_COMPUTED} event.
	 *
	 * @return <code>true</code> if the computation completed without errors.
	 */
	public boolean execInitialGreenobjectFiltering() {
		final Logger logger = model.getLogger();
		logger.log("Starting initial filtering process.\n");

		GreenobjectCollection Greenobjects = model.getGreenobjects();
		Greenobjects = Greenobjects.crop();

		model.setGreenobjects(Greenobjects, true); // Forget about the previous one
		return true;
	}

	/**
	 * Execute the Greenobject feature filtering part.
	 * <p>
	 * Because of the presence of noise, it is possible that some of the regional
	 * maxima found in the detection step have identified noise, rather than objects
	 * of interest. A filtering operation based on the calculated features in this
	 * step should allow to rule them out.
	 * <p>
	 * This method simply takes all the detected Greenobjects, and mark as visible
	 * the Greenobjects whose features satisfy all of the filters in the
	 * {@link Settings} object.
	 * <p>
	 * The {@link ModelChangeListener}s of this model will be notified with a
	 * {@link ModelChangeEvent#GreenobjectS_FILTERED} event.
	 *
	 * @param doLogIt
	 *            if <code>true</code>, will send a message to the model logger.
	 * @return <code>true</code> if the computation completed without errors.
	 */
	public boolean execGreenobjectFiltering(final boolean doLogIt) {
		if (doLogIt) {
			final Logger logger = model.getLogger();
			logger.log("Starting Greenobject filtering process.\n");
		}
		model.filterGreenobjects(settings.getGreenobjectFilters(), true);
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

		if (!execInitialGreenobjectFiltering()) {
			return false;
		}

		if (!computeGreenobjectFeatures(true)) {
			return false;
		}

		if (!execGreenobjectFiltering(true)) {
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
