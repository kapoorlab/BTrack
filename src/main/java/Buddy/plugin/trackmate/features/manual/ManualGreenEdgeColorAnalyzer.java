package Buddy.plugin.trackmate.features.manual;

import Buddy.plugin.trackmate.Dimension;
import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.features.edges.EdgeAnalyzer;
import Buddy.plugin.trackmate.features.edges.GreenEdgeAnalyzer;
import Buddy.plugin.trackmate.visualization.TrackMateModelView;
import tracker.GREENDimension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.scijava.plugin.Plugin;

@Plugin(type = EdgeAnalyzer.class)
public class ManualGreenEdgeColorAnalyzer implements GreenEdgeAnalyzer {

	public static final String FEATURE = "MANUAL_COLOR";

	public static final String KEY = "MANUAL_EDGE_COLOR_ANALYZER";

	static final List<String> FEATURES = new ArrayList<>(1);

	static final Map<String, String> FEATURE_SHORT_NAMES = new HashMap<>(1);

	static final Map<String, String> FEATURE_NAMES = new HashMap<>(1);

	static final Map<String, GREENDimension> FEATURE_DIMENSIONS = new HashMap<>(1);

	static final Map<String, Boolean> IS_INT = new HashMap<>(1);

	static final String INFO_TEXT = "<html>A dummy analyzer for the feature that stores the color manually assigned to each edge.</html>";

	static final String NAME = "Manual edge color analyzer";

	private static final Double DEFAULT_COLOR_VALUE = Double
			.valueOf(TrackMateModelView.DEFAULT_UNASSIGNED_FEATURE_COLOR.getRGB());

	static {
		FEATURES.add(FEATURE);
		FEATURE_SHORT_NAMES.put(FEATURE, "Edge color");
		FEATURE_NAMES.put(FEATURE, "Manual edge color");
		FEATURE_DIMENSIONS.put(FEATURE, GREENDimension.NONE);
		IS_INT.put(FEATURE, Boolean.TRUE);
	}

	private long processingTime;

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
	public Map<String, Boolean> getIsIntFeature() {
		return IS_INT;
	}

	@Override
	public void setNumThreads() {
	}

	@Override
	public void setNumThreads(final int numThreads) {
	}

	@Override
	public int getNumThreads() {
		return 1;
	}

	@Override
	public String getInfoText() {
		return INFO_TEXT;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void process(final Collection<DefaultWeightedEdge> edges, final GreenModel model) {
		final long start = System.currentTimeMillis();
		for (final DefaultWeightedEdge edge : edges) {
			if (null == model.getFeatureModel().getEdgeFeature(edge, FEATURE)) {
				model.getFeatureModel().putEdgeFeature(edge, FEATURE, DEFAULT_COLOR_VALUE);
			}
		}
		final long end = System.currentTimeMillis();
		processingTime = end - start;
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public boolean isManualFeature() {
		return true;
	}

	@Override
	public ImageIcon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}
}
