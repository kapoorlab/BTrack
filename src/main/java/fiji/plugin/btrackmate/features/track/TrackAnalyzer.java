package fiji.plugin.btrackmate.features.track;

import java.util.Collection;

import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import fiji.plugin.btrackmate.Model;
import fiji.plugin.btrackmate.features.FeatureAnalyzer;

/**
 * Mother interface for the classes that can compute the feature of tracks.
 * Target tracks are given through a {@link Model} and their IDs. Therefore and
 * ideally, concrete implementations can be made stateless.
 * <p>
 * Note: ideally concrete implementation should work in a multi-threaded fashion
 * for performance reason, when possible.
 * <p>
 * For {@link TrackAnalyzer}s, there is a mechanism intended to maintain the
 * model integrity against manual, small changes. Something as simple as
 * removing a spot in the middle of a track will generate two new tracks, which
 * will invalidate all feature values for the old track. Analyzers are notified
 * of such events, so that they can recompute track features after the change.
 * <p>
 * A simple way would be to recompute all track features at once, but this might
 * be too long and overkill for changes that do not affect all tracks
 * (<i>e.g.</i> adding a lonely spot, or a new track is likely not to affect all
 * tracks in some case).
 * <p>
 * So the {@link #process(Collection, Model)} will be called selectively on new
 * or modified tracks every time a change happens. It will be called from the
 * {@link Model} after a {@link Model#endUpdate()}, before any listener gets
 * notified.
 *
 * @author Jean-Yves Tinevez
 */
public interface TrackAnalyzer extends Benchmark, FeatureAnalyzer, MultiThreaded {

	/**
	 * Compute the features of the track whose ID is given.
	 *
	 * @param trackIDs the IDs of the track whose features are to be calculated.
	 * @param model    the {@link Model} from which actual tracks are to be
	 *                 retrieved.
	 */
	public void process(final Collection<Integer> trackIDs, final Model model);

	/**
	 * Returns <code>true</code> if this analyzer is a local analyzer. That is: a
	 * modification that affects only one track requires the track features to be
	 * re-calculated only for this track. If <code>false</code>, any model
	 * modification involving edges will trigger a recalculation over all the
	 * visible tracks of the model.
	 * <p>
	 * Example of a local track feature: the number of spots in a track. It does not
	 * depend on the number of spots in other tracks.
	 * <p>
	 * Example of a non-local track feature: the rank of the track sorted by its
	 * number of spots, compared to other tracks.
	 */
	public boolean isLocal();

}
