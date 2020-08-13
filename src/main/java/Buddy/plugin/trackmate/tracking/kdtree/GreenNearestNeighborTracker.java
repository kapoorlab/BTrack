package Buddy.plugin.trackmate.tracking.kdtree;

import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_LINKING_MAX_DISTANCE;
import static Buddy.plugin.trackmate.util.TMUtils.checkMapKeys;
import static Buddy.plugin.trackmate.util.TMUtils.checkParameter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.KDTree;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;
import net.imglib2.multithreading.SimpleMultiThreading;
import pluginTools.InteractiveBud;
import pluginTools.InteractiveGreen;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import greenDetector.Greenobject;
import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.GreenobjectCollection;
import Buddy.plugin.trackmate.tracking.GreenobjectTracker;
import Buddy.plugin.trackmate.tracking.GreenobjectTracker;
import Buddy.plugin.trackmate.util.GreenTMUtils;
import Buddy.plugin.trackmate.util.TMUtils;

@SuppressWarnings("deprecation")
public class GreenNearestNeighborTracker extends MultiThreadedBenchmarkAlgorithm implements GreenobjectTracker {

	/*
	 * FIELDS
	 */
	public final InteractiveGreen parent;

	protected final Map<String, Object> settings;

	protected Logger logger = Logger.VOID_LOGGER;

	protected SimpleWeightedGraph<Greenobject, DefaultWeightedEdge> graph;

	/*
	 * CONSTRUCTOR
	 */

	public GreenNearestNeighborTracker(final InteractiveGreen parent, final Map<String, Object> settings) {
		this.parent = parent;
		this.settings = settings;
	}

	/*
	 * PUBLIC METHODS
	 */

	@Override
	public boolean checkInput() {
		final StringBuilder errrorHolder = new StringBuilder();
		final boolean ok = checkInput(settings, errrorHolder);
		if (!ok) {
			errorMessage = errrorHolder.toString();
		}
		return ok;
	}

	@Override
	public boolean process() {
		final long start = System.currentTimeMillis();

		reset();

		final double maxLinkingDistance = (Double) settings.get(KEY_LINKING_MAX_DISTANCE);
		final double maxDistSquare = maxLinkingDistance * maxLinkingDistance;

		final TreeSet<Integer> frames = new TreeSet<>(parent.Greencells.keySet());
		final Thread[] threads = new Thread[numThreads];

		// Prepare the thread array
		final AtomicInteger ai = new AtomicInteger(frames.first());
		final AtomicInteger progress = new AtomicInteger(0);
		for (int ithread = 0; ithread < threads.length; ithread++) {

			threads[ithread] = new Thread("Nearest neighbor tracker thread " + (1 + ithread) + "/" + threads.length) {

				@Override
				public void run() {

					for (int i = ai.getAndIncrement(); i < frames.last(); i = ai.getAndIncrement()) {

						// Build frame pair
						final int sourceFrame = i;
						final int targetFrame = frames.higher(i);

						final int nTargetGreenobjects = parent.Greencells.getNGreenobjects(targetFrame);
						if (nTargetGreenobjects < 1) {
							continue;
						}

						final List<RealPoint> targetCoords = new ArrayList<>(nTargetGreenobjects);
						final List<FlagNode<Greenobject>> targetNodes = new ArrayList<>(nTargetGreenobjects);
						final Iterator<Greenobject> targetIt = parent.Greencells.iterator(targetFrame);
						while (targetIt.hasNext()) {
							final double[] coords = new double[3];
							final Greenobject Greenobject = targetIt.next();
							GreenTMUtils.localize(Greenobject, coords);
							targetCoords.add(new RealPoint(coords));
							targetNodes.add(new FlagNode<>(Greenobject));
						}

						final KDTree<FlagNode<Greenobject>> tree = new KDTree<>(targetNodes, targetCoords);
						final NearestNeighborFlagSearchOnKDTree<Greenobject> search = new NearestNeighborFlagSearchOnKDTree<>(
								tree);

						// For each Greenobject in the source frame, find its nearest neighbor in the
						// target frame
						final Iterator<Greenobject> sourceIt = parent.Greencells.iterator(sourceFrame);
						while (sourceIt.hasNext()) {
							final Greenobject source = sourceIt.next();
							final double[] coords = new double[3];
							GreenTMUtils.localize(source, coords);
							final RealPoint sourceCoords = new RealPoint(coords);
							search.search(sourceCoords);

							final double squareDist = search.getSquareDistance();
							final FlagNode<Greenobject> targetNode = search.getSampler().get();

							if (squareDist > maxDistSquare) {
								// The closest we could find is too far. We skip this source Greenobject and do
								// not create a link
								continue;
							}

							// Everything is ok. This mode is free and below max dist. We create a link
							// and mark this node as assigned.

							targetNode.setVisited(true);
							synchronized (graph) {
								final DefaultWeightedEdge edge = graph.addEdge(source, targetNode.getValue());
								graph.setEdgeWeight(edge, squareDist);
							}

						}
						logger.setProgress(progress.incrementAndGet() / (float) frames.size());

					}
				}
			};

		}

		logger.setStatus("Tracking...");
		logger.setProgress(0);

		SimpleMultiThreading.startAndJoin(threads);

		logger.setProgress(1);
		logger.setStatus("");

		final long end = System.currentTimeMillis();
		processingTime = end - start;
		return true;
	}

	@Override
	public SimpleWeightedGraph<Greenobject, DefaultWeightedEdge> getResult() {
		return graph;
	}

	public void reset() {
		graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		final Iterator<Greenobject> it = parent.Greencells.iterator(true);
		while (it.hasNext()) {
			graph.addVertex(it.next());
		}
	}

	public static boolean checkInput(final Map<String, Object> settings, final StringBuilder errrorHolder) {
		boolean ok = checkParameter(settings, KEY_LINKING_MAX_DISTANCE, Double.class, errrorHolder);
		final List<String> mandatoryKeys = new ArrayList<>();
		mandatoryKeys.add(KEY_LINKING_MAX_DISTANCE);
		ok = ok & checkMapKeys(settings, mandatoryKeys, null, errrorHolder);
		return ok;
	}

	@Override
	public void setLogger(final Logger logger) {
		this.logger = logger;
	}
}
