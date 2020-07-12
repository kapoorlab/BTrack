package Buddy.plugin.trackmate.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.imagej.ImgPlus;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;
import net.imglib2.multithreading.SimpleMultiThreading;
import tracker.GREENDimension;
import Buddy.plugin.trackmate.Dimension;
import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenSettings;
import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.Settings;
import Buddy.plugin.trackmate.features.spot.GreenobjectAnalyzer;
import Buddy.plugin.trackmate.features.spot.GreenobjectAnalyzerFactory;
import Buddy.plugin.trackmate.features.spot.IndependentGreenobjectFeatureAnalyzer;
import Buddy.plugin.trackmate.GreenobjectCollection;
import Buddy.plugin.trackmate.util.TMUtils;
import greenDetector.Greenobject;

/**
 * A class dedicated to centralizing the calculation of the numerical features
 * of Greenobjects, through {@link GreenobjectAnalyzer}s.
 * 
 * @author Jean-Yves Tinevez - 2013
 * 
 */
@SuppressWarnings("deprecation")
public class GreenobjectFeatureCalculator extends MultiThreadedBenchmarkAlgorithm {

	private static final String BASE_ERROR_MSG = "[GreenobjectFeatureCalculator] ";

	private final GreenSettings settings;

	private final GreenModel model;

	public GreenobjectFeatureCalculator(final GreenModel model, final GreenSettings settings) {
		this.settings = settings;
		this.model = model;
	}

	/*
	 * METHODS
	 */

	@Override
	public boolean checkInput() {
		if (null == model) {
			errorMessage = BASE_ERROR_MSG + "Model object is null.";
			return false;
		}
		if (null == settings) {
			errorMessage = BASE_ERROR_MSG + "Settings object is null.";
			return false;
		}
		return true;
	}

	/**
	 * Calculates the Greenobject features configured in the {@link Settings} for
	 * all the Greenobjects of this model,
	 * <p>
	 * Features are calculated for each Greenobject, using their location, and the
	 * raw image. Since a {@link GreenobjectAnalyzer} can compute more than a
	 * feature at once, Greenobjects might received more data than required.
	 */
	@Override
	public boolean process() {
		final long start = System.currentTimeMillis();

		// Declare what you do.
		for (final GreenobjectAnalyzerFactory<?> factory : settings.getGreenobjectAnalyzerFactories()) {
			final Collection<String> features = factory.getFeatures();
			final Map<String, String> featureNames = factory.getFeatureNames();
			final Map<String, String> featureShortNames = factory.getFeatureShortNames();
			final Map<String, GREENDimension> featureDimensions = factory.getGreenFeatureDimensions();
			final Map<String, Boolean> isIntFeature = factory.getIsIntFeature();
			model.getFeatureModel().declareGreenobjectFeatures(features, featureNames, featureShortNames,
					featureDimensions, isIntFeature);
		}

		// Do it.
		computeGreenobjectFeaturesAgent(model.getGreenobjects(), settings.getGreenobjectAnalyzerFactories(), true);

		final long end = System.currentTimeMillis();
		processingTime = end - start;
		return true;
	}

	/**
	 * Calculates all the Greenobject features configured in the {@link Settings}
	 * object for the specified Greenobject collection. Features are calculated for
	 * each Greenobject, using their location, and the raw image.
	 */
	public void computeGreenobjectFeatures(final GreenobjectCollection toCompute, final boolean doLogIt) {
		final List<GreenobjectAnalyzerFactory<?>> GreenobjectFeatureAnalyzers = settings
				.getGreenobjectAnalyzerFactories();
		computeGreenobjectFeaturesAgent(toCompute, GreenobjectFeatureAnalyzers, doLogIt);
	}

	/**
	 * The method in charge of computing Greenobject features with the given
	 * {@link GreenobjectAnalyzer}s, for the given {@link GreenobjectCollection}.
	 * 
	 * @param toCompute
	 */
	private void computeGreenobjectFeaturesAgent(final GreenobjectCollection toCompute,
			final List<GreenobjectAnalyzerFactory<?>> analyzerFactories, final boolean doLogIt) {

		final Logger logger;
		if (doLogIt) {
			logger = model.getLogger();
		} else {
			logger = Logger.VOID_LOGGER;
		}

		// Can't compute any Greenobject feature without an image to compute on.
		if (settings.imp == null)
			return;

		// Do it.
		final List<Integer> frameSet = new ArrayList<>(toCompute.keySet());
		final int numFrames = frameSet.size();

		final AtomicInteger ai = new AtomicInteger(0);
		final AtomicInteger progress = new AtomicInteger(0);
		final Thread[] threads = SimpleMultiThreading.newThreads(numThreads);

		int tc = 0;
		if (settings != null && settings.detectorSettings != null) {
			// Try to extract it from detector settings target channel
			final Map<String, Object> ds = settings.detectorSettings;

		}
		final int targetChannel = tc;

		@SuppressWarnings("rawtypes")
		final ImgPlus img = TMUtils.rawWraps(settings.imp);

		// Prepare the thread array
		for (int ithread = 0; ithread < threads.length; ithread++) {

			threads[ithread] = new Thread(
					"TrackMate Greenobject feature calculating thread " + (1 + ithread) + "/" + threads.length) {

				@Override
				public void run() {

					for (int index = ai.getAndIncrement(); index < numFrames; index = ai.getAndIncrement()) {

						final int frame = frameSet.get(index);
						for (final GreenobjectAnalyzerFactory<?> factory : analyzerFactories) {
							@SuppressWarnings("unchecked")
							final GreenobjectAnalyzer<?> analyzer = factory.getAnalyzer(model, img, frame,
									targetChannel);
							if (analyzer instanceof IndependentGreenobjectFeatureAnalyzer) {
								// Independent: we can process only the Greenobject to update.
								@SuppressWarnings("rawtypes")
								final IndependentGreenobjectFeatureAnalyzer analyzer2 = (IndependentGreenobjectFeatureAnalyzer) analyzer;
								for (final Greenobject Greenobject : toCompute.iterable(frame, false)) {
									analyzer2.process(Greenobject);
								}
							} else {
								// Process all Greenobjects of the frame at once.
								analyzer.process();
							}

						}

						logger.setProgress(progress.incrementAndGet() / (float) numFrames);
					} // Finished looping over frames
				}
			};
		}
		logger.setStatus("Calculating " + toCompute.getNGreenobjects() + " Greenobjects features...");
		logger.setProgress(0);

		SimpleMultiThreading.startAndJoin(threads);

		logger.setProgress(1);
		logger.setStatus("");
	}

}
