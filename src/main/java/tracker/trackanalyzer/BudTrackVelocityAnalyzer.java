package tracker.trackanalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.ImageIcon;

import org.jgrapht.graph.DefaultWeightedEdge;

import budDetector.Budpointobject;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.util.Pair;
import net.imglib2.util.Util;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveBud;
import tracker.BUDDYDimension;
import tracker.BUDDYFeatureModel;
import tracker.BUDDYModel;

public class BudTrackVelocityAnalyzer implements BudTrackAnalyzer {

	/*
	 * CONSTANTS
	 */
	public static final String KEY = "Velocity";

	public static final String TRACK_MEAN_SPEED = "TRACK_MEAN_SPEED";

	public static final String TRACK_MAX_SPEED = "TRACK_MAX_SPEED";

	public static final String TRACK_MIN_SPEED = "TRACK_MIN_SPEED";

	public static final String TRACK_MEDIAN_SPEED = "TRACK_MEDIAN_SPEED";

	public static final String TRACK_STD_SPEED = "TRACK_STD_SPEED";

	// public static final String TRACK_SPEED_KURTOSIS = "TRACK_SPEED_KURTOSIS";
	// public static final String TRACK_SPEED_SKEWNESS = "TRACK_SPEED_SKEWNESS";

	public static final List<String> FEATURES = new ArrayList<>(5);

	public static final Map<String, String> FEATURE_NAMES = new HashMap<>(5);

	public static final Map<String, String> FEATURE_SHORT_NAMES = new HashMap<>(5);

	public static final Map<String, BUDDYDimension> FEATURE_DIMENSIONS = new HashMap<>(5);

	public static final Map<String, Boolean> IS_INT = new HashMap<>(5);

	static {
		FEATURES.add(TRACK_MEAN_SPEED);
		FEATURES.add(TRACK_MAX_SPEED);
		FEATURES.add(TRACK_MIN_SPEED);
		FEATURES.add(TRACK_MEDIAN_SPEED);
		FEATURES.add(TRACK_STD_SPEED);
		// FEATURES.add(TRACK_SPEED_KURTOSIS);
		// FEATURES.add(TRACK_SPEED_SKEWNESS);

		FEATURE_NAMES.put(TRACK_MEAN_SPEED, "Mean velocity");
		FEATURE_NAMES.put(TRACK_MAX_SPEED, "Maximal velocity");
		FEATURE_NAMES.put(TRACK_MIN_SPEED, "Minimal velocity");
		FEATURE_NAMES.put(TRACK_MEDIAN_SPEED, "Median velocity");
		FEATURE_NAMES.put(TRACK_STD_SPEED, "Velocity standard deviation");
		// FEATURE_NAMES.put(TRACK_SPEED_KURTOSIS, "Velocity kurtosis");
		// FEATURE_NAMES.put(TRACK_SPEED_SKEWNESS, "Velocity skewness");

		FEATURE_SHORT_NAMES.put(TRACK_MEAN_SPEED, "Mean V");
		FEATURE_SHORT_NAMES.put(TRACK_MAX_SPEED, "Max V");
		FEATURE_SHORT_NAMES.put(TRACK_MIN_SPEED, "Min V");
		FEATURE_SHORT_NAMES.put(TRACK_MEDIAN_SPEED, "Median V");
		FEATURE_SHORT_NAMES.put(TRACK_STD_SPEED, "V std");
		// FEATURE_SHORT_NAMES.put(TRACK_SPEED_KURTOSIS, "V kurtosis");
		// FEATURE_SHORT_NAMES.put(TRACK_SPEED_SKEWNESS, "V skewness");

		FEATURE_DIMENSIONS.put(TRACK_MEAN_SPEED, BUDDYDimension.VELOCITY);
		FEATURE_DIMENSIONS.put(TRACK_MAX_SPEED, BUDDYDimension.VELOCITY);
		FEATURE_DIMENSIONS.put(TRACK_MIN_SPEED, BUDDYDimension.VELOCITY);
		FEATURE_DIMENSIONS.put(TRACK_MEDIAN_SPEED, BUDDYDimension.VELOCITY);
		FEATURE_DIMENSIONS.put(TRACK_STD_SPEED, BUDDYDimension.VELOCITY);
		// FEATURE_DIMENSIONS.put(TRACK_SPEED_KURTOSIS, Dimension.NONE);
		// FEATURE_DIMENSIONS.put(TRACK_SPEED_SKEWNESS, Dimension.NONE);

		IS_INT.put(TRACK_MEAN_SPEED, Boolean.FALSE);
		IS_INT.put(TRACK_MAX_SPEED, Boolean.FALSE);
		IS_INT.put(TRACK_MIN_SPEED, Boolean.FALSE);
		IS_INT.put(TRACK_MEDIAN_SPEED, Boolean.FALSE);
		IS_INT.put(TRACK_STD_SPEED, Boolean.FALSE);
		// IS_INT.put(TRACK_SPEED_KURTOSIS, Boolean.FALSE);
		// IS_INT.put(TRACK_SPEED_SKEWNESS, Boolean.FALSE);
	}

	private int numThreads;

	private long processingTime;

	HashMap<Integer, HashMap<Integer, Double>> VelocityMap;

	public InteractiveBud parent;

	public BudTrackVelocityAnalyzer(final InteractiveBud parent) {
		this.parent = parent;
		setNumThreads();
	}

	/*
	 * METHODS
	 */

	@Override
	public boolean isLocal() {
		return true;
	}

	public HashMap<Integer, HashMap<Integer, Double>> getVelocityMap() {

		return VelocityMap;
	}

	@Override
	public void process(final Collection<Integer> trackIDs, final BUDDYModel model) {

		VelocityMap = new HashMap<Integer, HashMap<Integer, Double>>();

		if (trackIDs.isEmpty()) {
			return;
		}

		final ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(trackIDs.size(), false, trackIDs);
		final BUDDYFeatureModel fm = model.getFeatureModel();

		final Thread[] threads = SimpleMultiThreading.newThreads(numThreads);
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread("TrackSpeedStatisticsAnalyzer thread " + i) {

				@Override
				public void run() {
					Integer trackID;
					while ((trackID = queue.poll()) != null) {
						HashMap<Integer, Double> VelocityList = new HashMap<Integer, Double>();
						final Set<DefaultWeightedEdge> track = model.getTrackModel().trackEdges(trackID);

						double sum = 0;
						double mean = 0;
						double M2 = 0;
						// double M3 = 0;
						// double M4 = 0;
						double delta, delta_n;
						// double delta_n2;
						double term1;
						int n1;

						// Others
						Double val;
						final double[] velocities = new double[track.size()];
						int n = 0;

						for (final DefaultWeightedEdge edge : track) {
							final Budpointobject source = model.getTrackModel().getEdgeSource(edge);
							final Budpointobject target = model.getTrackModel().getEdgeTarget(edge);

							// Edge velocity
							final double d2 = source.squareDistanceTo(target);
							final double dt = source.diffTo(target, Budpointobject.POSITION_T);
							val = Math.sqrt(d2) / Math.abs(dt);
							if (parent.timecal == 0)
								parent.timecal = 1;
							VelocityList.put((int) source.t, val * (parent.calibrationX / parent.timecal));

							// For median, min and max
							velocities[n] = val;
							// For variance and mean
							sum += val;

							// For kurtosis
							n1 = n;
							n++;
							delta = val - mean;
							delta_n = delta / n;
							// delta_n2 = delta_n * delta_n;
							term1 = delta * delta_n * n1;
							mean = mean + delta_n;
							// M4 = M4 + term1 * delta_n2 * (n*n - 3*n + 3) + 6
							// * delta_n2 * M2 - 4 * delta_n * M3;
							// M3 = M3 + term1 * delta_n * (n - 2) - 3 * delta_n
							// * M2;
							M2 = M2 + term1;
						}

						Util.quicksort(velocities, 0, track.size() - 1);
						final double median = velocities[track.size() / 2];
						final double min = velocities[0];
						final double max = velocities[track.size() - 1];
						mean = sum / Math.max(track.size(), 1);
						final double variance = M2 / (track.size() - 1);
						// double kurtosis = (n*M4) / (M2*M2) - 3;
						// double skewness = Math.sqrt(n) * M3 / Math.pow(M2,
						// 3/2.0) ;

						fm.putTrackFeature(trackID, TRACK_MEDIAN_SPEED, median);
						fm.putTrackFeature(trackID, TRACK_MIN_SPEED, min);
						fm.putTrackFeature(trackID, TRACK_MAX_SPEED, max);
						fm.putTrackFeature(trackID, TRACK_MEAN_SPEED, mean);
						fm.putTrackFeature(trackID, TRACK_STD_SPEED, Math.sqrt(variance));
						// fm.putTrackFeature(index, TRACK_SPEED_KURTOSIS,
						// kurtosis);
						// fm.putTrackFeature(index, TRACK_SPEED_SKEWNESS,
						// skewness);

						VelocityMap.put(trackID, VelocityList);

					}

				}
			};
		}

		final long start = System.currentTimeMillis();
		SimpleMultiThreading.startAndJoin(threads);
		final long end = System.currentTimeMillis();
		processingTime = end - start;
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

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public List<String> getFeatures() {
		return FEATURES;
	}

	@Override
	public Map<String, String> getFeatureShortNames() {
		return FEATURE_SHORT_NAMES;
	}

	@Override
	public Map<String, String> getFeatureNames() {
		return FEATURE_NAMES;
	}

	@Override
	public Map<String, BUDDYDimension> getFeatureDimensions() {
		return FEATURE_DIMENSIONS;
	}

	@Override
	public String getInfoText() {
		return null;
	}

	@Override
	public ImageIcon getIcon() {
		return null;
	}

	@Override
	public String getName() {
		return KEY;
	}

	@Override
	public Map<String, Boolean> getIsIntFeature() {
		return IS_INT;
	}

	@Override
	public boolean isManualFeature() {
		return false;
	}

}
