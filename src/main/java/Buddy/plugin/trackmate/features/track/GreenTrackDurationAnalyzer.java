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
@Plugin(type = TrackAnalyzer.class)
public class GreenTrackDurationAnalyzer implements GreenTrackAnalyzer {

	public static final String KEY = "Track duration";

	public static final String TRACK_DURATION = "TRACK_DURATION";

	public static final String TRACK_START = "TRACK_START";

	public static final String TRACK_STOP = "TRACK_STOP";

	public static final String TRACK_DISPLACEMENT = "TRACK_DISPLACEMENT";

	public static final List<String> FEATURES = new ArrayList<>(4);

	public static final Map<String, String> FEATURE_NAMES = new HashMap<>(4);

	public static final Map<String, String> FEATURE_SHORT_NAMES = new HashMap<>(4);

	public static final Map<String, GREENDimension> FEATURE_DIMENSIONS = new HashMap<>(4);

	public static final Map<String, Boolean> IS_INT = new HashMap<>(4);

	static {
		FEATURES.add(TRACK_DURATION);
		FEATURES.add(TRACK_START);
		FEATURES.add(TRACK_STOP);
		FEATURES.add(TRACK_DISPLACEMENT);

		FEATURE_NAMES.put(TRACK_DURATION, "Duration of track");
		FEATURE_NAMES.put(TRACK_START, "Track start");
		FEATURE_NAMES.put(TRACK_STOP, "Track stop");
		FEATURE_NAMES.put(TRACK_DISPLACEMENT, "Track displacement");

		FEATURE_SHORT_NAMES.put(TRACK_DURATION, "Duration");
		FEATURE_SHORT_NAMES.put(TRACK_START, "T start");
		FEATURE_SHORT_NAMES.put(TRACK_STOP, "T stop");
		FEATURE_SHORT_NAMES.put(TRACK_DISPLACEMENT, "Displacement");

		FEATURE_DIMENSIONS.put(TRACK_DURATION, GREENDimension.TIME);
		FEATURE_DIMENSIONS.put(TRACK_START, GREENDimension.TIME);
		FEATURE_DIMENSIONS.put(TRACK_STOP, GREENDimension.TIME);
		FEATURE_DIMENSIONS.put(TRACK_DISPLACEMENT, GREENDimension.LENGTH);

		IS_INT.put(TRACK_DURATION, Boolean.FALSE);
		IS_INT.put(TRACK_START, Boolean.FALSE);
		IS_INT.put(TRACK_STOP, Boolean.FALSE);
		IS_INT.put(TRACK_DISPLACEMENT, Boolean.FALSE);
	}

	private int numThreads;

	private long processingTime;

	public GreenTrackDurationAnalyzer() {
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
			threads[i] = new Thread("TrackDurationAnalyzer thread " + i) {
				@Override
				public void run() {
					Integer trackID;
					while ((trackID = queue.poll()) != null) {

						// I love brute force.
						final Set<Greenobject> track = model.getTrackModel().trackGreenobjects(trackID);
						double minT = Double.POSITIVE_INFINITY;
						double maxT = Double.NEGATIVE_INFINITY;
						Double t;
						Greenobject startGreenobject = null;
						Greenobject endGreenobject = null;
						for (final Greenobject Greenobject : track) {
							t = Greenobject.getFeature(Greenobject.POSITION_T);
							if (t < minT) {
								minT = t;
								startGreenobject = Greenobject;
							}
							if (t > maxT) {
								maxT = t;
								endGreenobject = Greenobject;
							}
						}
						if (null == startGreenobject || null == endGreenobject)
							continue;

						fm.putTrackFeature(trackID, TRACK_DURATION, (maxT - minT));
						fm.putTrackFeature(trackID, TRACK_START, minT);
						fm.putTrackFeature(trackID, TRACK_STOP, maxT);
						fm.putTrackFeature(trackID, TRACK_DISPLACEMENT,
								Math.sqrt(startGreenobject.squareDistanceTo(endGreenobject)));

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
	public String getKey() {
		return KEY;
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
