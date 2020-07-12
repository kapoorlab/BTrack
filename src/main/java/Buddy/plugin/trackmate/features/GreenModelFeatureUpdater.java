package Buddy.plugin.trackmate.features;

import java.util.ArrayList;

import org.jgrapht.graph.DefaultWeightedEdge;

import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.ModelChangeEvent;
import Buddy.plugin.trackmate.ModelChangeListener;
import Buddy.plugin.trackmate.Settings;
import greenDetector.Greenobject;
import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenModelChangeEvent;
import Buddy.plugin.trackmate.GreenModelChangeListener;
import Buddy.plugin.trackmate.GreenSettings;
import Buddy.plugin.trackmate.GreenobjectCollection;
import net.imglib2.algorithm.MultiThreaded;

/**
 * A utility class that listens to the change occurring in a model, and updates
 * its Greenobject, edge and track features accordingly. Useful to keep the
 * model in sync with manual editing.
 * 
 * @author Jean-Yves Tinevez - 2013
 */
public class GreenModelFeatureUpdater implements GreenModelChangeListener, MultiThreaded {

	private final GreenobjectFeatureCalculator GreenobjectFeatureCalculator;
	private final GreenEdgeFeatureCalculator edgeFeatureCalculator;
	private final GreenTrackFeatureCalculator trackFeatureCalculator;
	private final GreenModel model;

	private int numThreads;

	/**
	 * Constructs and activate a {@link ModelFeatureUpdater}. The new instance is
	 * registered to listen to model changes, and update its feature.
	 * 
	 * @param model
	 *            the model to listen to.
	 * @param settings
	 *            the {@link Settings} the model is built against. Required to
	 *            access the raw data.
	 */
	public GreenModelFeatureUpdater(GreenModel model, GreenSettings settings) {
		this.model = model;
		this.GreenobjectFeatureCalculator = new GreenobjectFeatureCalculator(model, settings);
		this.edgeFeatureCalculator = new GreenEdgeFeatureCalculator(model, settings);
		this.trackFeatureCalculator = new GreenTrackFeatureCalculator(model, settings);
		model.addModelChangeListener(this);
		setNumThreads();
	}

	/**
	 * Updates the model features against the change notified here. If the event is
	 * not a {@link ModelChangeEvent#MODEL_MODIFIED}, does nothing.
	 */
	@Override
	public void modelChanged(GreenModelChangeEvent event) {
		if (event.getEventID() != ModelChangeEvent.MODEL_MODIFIED) {
			return;
		}

		// Build Greenobject list
		ArrayList<Greenobject> Greenobjects = new ArrayList<>(event.getGreenobject().size());
		for (Greenobject Greenobject : event.getGreenobject()) {
			if (event.getGreenobjectFlag(Greenobject) != GreenModelChangeEvent.FLAG_Greenobject_REMOVED) {
				Greenobjects.add(Greenobject);
			}
		}
		GreenobjectCollection sc = GreenobjectCollection.fromCollection(Greenobjects);

		// Build edge list
		ArrayList<DefaultWeightedEdge> edges = new ArrayList<>(event.getEdges().size());
		for (DefaultWeightedEdge edge : event.getEdges()) {
			if (event.getEdgeFlag(edge) != ModelChangeEvent.FLAG_EDGE_REMOVED) {
				edges.add(edge);
			}
		}

		// Update Greenobject features
		GreenobjectFeatureCalculator.computeGreenobjectFeatures(sc, false);

		// Update edge features
		edgeFeatureCalculator.computeEdgesFeatures(edges, false);

		// Update track features
		trackFeatureCalculator.computeTrackFeatures(event.getTrackUpdated(), false);
	}

	/**
	 * Re-registers this instance from the listeners of the model, and stop updating
	 * its features.
	 */
	public void quit() {
		model.removeModelChangeListener(this);
	}

	@Override
	public int getNumThreads() {
		return numThreads;
	}

	@Override
	public void setNumThreads() {
		setNumThreads(Runtime.getRuntime().availableProcessors());
	}

	@Override
	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
		GreenobjectFeatureCalculator.setNumThreads(numThreads);
		edgeFeatureCalculator.setNumThreads(numThreads);
		trackFeatureCalculator.setNumThreads(numThreads);
	}

}
