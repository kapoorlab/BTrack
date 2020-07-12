package Buddy.plugin.trackmate.features.track;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.ImageIcon;

import net.imglib2.multithreading.SimpleMultiThreading;
import tracker.GREENDimension;

import org.scijava.plugin.Plugin;

import Buddy.plugin.trackmate.Dimension;
import Buddy.plugin.trackmate.FeatureModel;
import Buddy.plugin.trackmate.GreenFeatureModel;
import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.Model;
import greenDetector.Greenobject;

@SuppressWarnings("deprecation")
@Plugin(type = GreenTrackAnalyzer.class)
public class GreenTrackLocationAnalyzer implements GreenTrackAnalyzer {

	/*
	 * FEATURE NAMES
	 */
	public static final String KEY = "Track location";

	public static final String X_LOCATION = "TRACK_X_LOCATION";

	public static final String Y_LOCATION = "TRACK_Y_LOCATION";
	
	public static final String Z_LOCATION = "TRACK_Z_LOCATION";


	public static final List<String> FEATURES = new ArrayList<>(3);

	public static final Map<String, String> FEATURE_NAMES = new HashMap<>(3);

	public static final Map<String, String> FEATURE_SHORT_NAMES = new HashMap<>(3);

	public static final Map<String, GREENDimension> FEATURE_DIMENSIONS = new HashMap<>(3);

	public static final Map<String, Boolean> IS_INT = new HashMap<>(3);

	static {
		FEATURES.add(X_LOCATION);
		FEATURES.add(Y_LOCATION);
		FEATURES.add(Z_LOCATION);
		FEATURE_NAMES.put(X_LOCATION, "X Location (mean)");
		FEATURE_NAMES.put(Y_LOCATION, "Y Location (mean)");
		FEATURE_NAMES.put(Z_LOCATION, "Z Location (mean)");
		FEATURE_SHORT_NAMES.put(X_LOCATION, "X");
		FEATURE_SHORT_NAMES.put(Y_LOCATION, "Y");
		FEATURE_SHORT_NAMES.put(Z_LOCATION, "Z");
		FEATURE_DIMENSIONS.put(X_LOCATION, GREENDimension.POSITION);
		FEATURE_DIMENSIONS.put(Y_LOCATION, GREENDimension.POSITION);
		FEATURE_DIMENSIONS.put(Z_LOCATION, GREENDimension.POSITION);
		IS_INT.put(X_LOCATION, Boolean.FALSE);
		IS_INT.put(Y_LOCATION, Boolean.FALSE);
		IS_INT.put(Z_LOCATION, Boolean.FALSE);
	}

	private int numThreads;

	private long processingTime;

	/*
	 * CONSTRUCTOR
	 */

	public GreenTrackLocationAnalyzer() {
		setNumThreads();
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public void process(final Collection<Integer> trackIDs, final GreenModel model) {

		if (trackIDs.isEmpty()) {
			return;
		}

		final ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(trackIDs.size(), false, trackIDs);
		final GreenFeatureModel fm = model.getFeatureModel();

		final Thread[] threads = SimpleMultiThreading.newThreads(numThreads);
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread("TrackLocationAnalyzer thread " + i) {
				@Override
				public void run() {
					Integer trackID;
					while ((trackID = queue.poll()) != null) {

						final Set<Greenobject> track = model.getTrackModel().trackGreenobjects(trackID);

						double x = 0;
						double y = 0;
						double z = 0;

						for (final Greenobject Greenobject : track) {
							x += Greenobject.getFeature(Greenobject.POSITION_X);
							y += Greenobject.getFeature(Greenobject.POSITION_Y);
							z += Greenobject.getFeature(Greenobject.POSITION_Z);
						}

						final int nGreenobjects = track.size();
						x /= nGreenobjects;
						y /= nGreenobjects;
						z /= nGreenobjects;
						fm.putTrackFeature(trackID, X_LOCATION, x);
						fm.putTrackFeature(trackID, Y_LOCATION, y);
						fm.putTrackFeature(trackID, Z_LOCATION, z);

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
	public Map<String, GREENDimension> getFeatureDimensions() {
		return FEATURE_DIMENSIONS;
	}

	@Override
	public String getInfoText() {
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

	@Override
	public ImageIcon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}
}
