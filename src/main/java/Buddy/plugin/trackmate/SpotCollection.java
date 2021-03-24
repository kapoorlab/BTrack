package Buddy.plugin.trackmate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import budDetector.Spot;
import Buddy.plugin.trackmate.features.FeatureFilter;
import net.imglib2.algorithm.MultiThreaded;

/**
 * A utility class that wrap the {@link java.util.SortedMap} we use to store the
 * Spots contained in each frame with a few utility methods.
 * <p>
 * Internally we rely on ConcurrentSkipListMap to allow concurrent access
 * without clashes.
 * <p>
 * This class is {@link MultiThreaded}. There are a few processes that can
 * benefit from multithreaded computation ({@link #filter(Collection)},
 * {@link #filter(FeatureFilter)}
 *
 * @author VK, Jean-Yves Tinevez &lt;jeanyves.tinevez@gmail.com&gt; - Feb 2011 -
 *         2013
 *
 */
public class SpotCollection implements MultiThreaded {

	public static final Double ZERO = Double.valueOf(0d);

	public static final Double ONE = Double.valueOf(1d);
	
	public static final String VISIBLITY = "VISIBILITY";

	/**
	 * Time units for filtering and cropping operation timeouts. Filtering should
	 * not take more than 1 minute.
	 */
	private static final TimeUnit TIME_OUT_UNITS = TimeUnit.MINUTES;

	/**
	 * Time for filtering and cropping operation timeouts. Filtering should not take
	 * more than 1 minute.
	 */
	private static final long TIME_OUT_DELAY = 1;

	/** The frame by frame list of Spot this object wrap. */
	private ConcurrentSkipListMap<Integer, Set<Spot>> content = new ConcurrentSkipListMap<>();

	private int numThreads;

	/*
	 * CONSTRUCTORS
	 */

	/**
	 * Construct a new empty Spot collection.
	 */
	public SpotCollection() {
		setNumThreads();
	}

	/*
	 * METHODS
	 */

	/**
	 * Retrieves and returns the {@link Spot} object in this collection with
	 * the specified ID. Returns <code>null</code> if the Spot cannot be
	 * found. All Spots, visible or not, are searched for.
	 *
	 * @param ID
	 *            the ID to look for.
	 * @return the Spot with the specified ID or <code>null</code> if this
	 *         Spot does not exist or does not belong to this collection.
	 */
	public Spot search(final int ID) {
		Spot Spot = null;
		for (final Spot s : iterable(false)) {
			if (s.ID() == ID) {
				Spot = s;
				break;
			}
		}
		return Spot;
	}

	@Override
	public String toString() {
		String str = super.toString();
		str += ": contains " + getNSpots() + " Spots total in " + keySet().size()
				+ " different frames, over which " + getNSpots() + " are visible:\n";
		for (final int key : content.keySet()) {
			str += "\tframe " + key + ": " + getNSpots(key) + " Spots total, " + getNSpots(key)
					+ " visible.\n";
		}
		return str;
	}

	/**
	 * Adds the given Spot to this collection, at the specified frame, and
	 * mark it as visible.
	 * <p>
	 * If the frame does not exist yet in the collection, it is created and added.
	 * Upon adding, the added Spot has its feature {@link Spot#FRAME}
	 * updated with the passed frame value.
	 * 
	 * @param Spot
	 *            the Spot to add.
	 * @param frame
	 *            the frame to add it to.
	 */
	public void add(final Spot Spot, final Integer frame) {
		Set<Spot> Spots = content.get(frame);
		if (null == Spots) {
			Spots = new HashSet<>();
			content.put(frame, Spots);
		}
		Spots.add(Spot);
		Spot.time = frame;
	}

	/**
	 * Removes the given Spot from this collection, at the specified frame.
	 * <p>
	 * If the Spot frame collection does not exist yet, nothing is done and
	 * <code>false</code> is returned. If the Spot cannot be found in the
	 * frame content, nothing is done and <code>false</code> is returned.
	 * 
	 * @param Spot
	 *            the Spot to remove.
	 * @param frame
	 *            the frame to remove it from.
	 * @return <code>true</code> if the Spot was succesfully removed.
	 */
	public boolean remove(final Spot Spot, final Integer frame) {
		final Set<Spot> Spots = content.get(frame);
		if (null == Spots) {
			return false;
		}
		return Spots.remove(Spot);
	}
	
	public void setVisible( final boolean visible )
	{
		final Double val = visible ? ONE : ZERO;
		final Collection< Integer > frames = content.keySet();

		final ExecutorService executors = Executors.newFixedThreadPool( numThreads );
		for ( final Integer frame : frames )
		{

			final Runnable command = new Runnable()
			{
				@Override
				public void run()
				{

					final Set< Spot > spots = content.get( frame );
					for ( final Spot spot : spots )
					{
						spot.putFeature( VISIBLITY, val );
					}

				}
			};
			executors.execute( command );
		}

		executors.shutdown();
		try
		{
			final boolean ok = executors.awaitTermination( TIME_OUT_DELAY, TIME_OUT_UNITS );
			if ( !ok )
			{
				System.err.println( "[SpotCollection.setVisible()] Timeout of " + TIME_OUT_DELAY + " " + TIME_OUT_UNITS + " reached." );
			}
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Filters out the content of this collection using the specified
	 * {@link FeatureFilter} collection. Spots that are filtered out are
	 * marked as invisible, and visible otherwise. To be marked as visible, a
	 * Spot must pass <b>all</b> of the specified filters (AND chaining).
	 *
	 * @param filters
	 *            the filter collection to use.
	 */
	public final void filter(final Collection<FeatureFilter> filters) {

		final Collection<Integer> frames = content.keySet();
		final ExecutorService executors = Executors.newFixedThreadPool(numThreads);

		for (final Integer frame : frames) {
			final Runnable command = new Runnable() {
				@Override
				public void run() {
					final Set<Spot> Spots = content.get(frame);

					Double val, tval;
					boolean isAbove;
					for (final Spot Spot : Spots) {

						for (final FeatureFilter featureFilter : filters) {

							val = Spot.getFeature(featureFilter.feature);
							tval = featureFilter.value;
							isAbove = featureFilter.isAbove;

							if (isAbove && val.compareTo(tval) < 0 || !isAbove && val.compareTo(tval) > 0) {
								break;
							}
						} // loop over filters

					} // loop over Spots

				}

			};
			executors.execute(command);
		}

		executors.shutdown();
		try {
			final boolean ok = executors.awaitTermination(TIME_OUT_DELAY, TIME_OUT_UNITS);
			if (!ok) {
				System.err.println("[SpotCollection.filter()] Timeout of " + TIME_OUT_DELAY + " "
						+ TIME_OUT_UNITS + " reached while filtering.");
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the closest {@link Spot} to the given location (encoded as a
	 * Spot), contained in the frame <code>frame</code>. If the frame has no
	 * Spot, return <code>null</code>.
	 *
	 * @param location
	 *            the location to search for.
	 * @param frame
	 *            the frame to inspect.
	 * @param visibleSpotsOnly
	 *            if true, will only search though visible Spots. If false,
	 *            will search through all Spots.
	 * @return the closest Spot to the specified location, member of this
	 *         collection.
	 */
	public final Spot getClosestSpot(final Spot location, final int frame,
			final boolean visibleSpotsOnly) {
		final Set<Spot> Spots = content.get(frame);
		if (null == Spots)
			return null;
		double d2;
		double minDist = Double.POSITIVE_INFINITY;
		Spot target = null;
		for (final Spot s : Spots) {

			d2 = s.squareDistanceTo(location);
			if (d2 < minDist) {
				minDist = d2;
				target = s;
			}

		}
		return target;
	}

	/**
	 * Returns the {@link Spot} at the given location (encoded as a
	 * Spot), contained in the frame <code>frame</code>. A Spot is
	 * returned <b>only</b> if there exists a Spot such that the given
	 * location is within the Spot radius. Otherwise <code>null</code> is
	 * returned.
	 *
	 * @param location
	 *            the location to search for.
	 * @param frame
	 *            the frame to inspect.
	 * @param visibleSpotsOnly
	 *            if true, will only search though visible Spots. If false,
	 *            will search through all Spots.
	 * @return the closest Spot such that the specified location is within
	 *         its radius, member of this collection, or <code>null</code> is such a
	 *         Spots cannot be found.
	 */
	public final Spot getSpotAt(final Spot location, final int frame,
			final boolean visibleSpotsOnly) {
		final Set<Spot> Spots = content.get(frame);
		if (null == Spots || Spots.isEmpty()) {
			return null;
		}

		final TreeMap<Double, Spot> distanceToSpot = new TreeMap<>();
		double d2;
		for (final Spot s : Spots) {

			d2 = s.squareDistanceTo(location);
			double size = s.getFeature(Spot.RADIUS[0]) * s.getFeature(Spot.RADIUS[1]) * s.getFeature(Spot.RADIUS[2]);
			if (d2 < size)
				distanceToSpot.put(d2, s);
		}
		if (distanceToSpot.isEmpty())
			return null;

		return distanceToSpot.firstEntry().getValue();
	}

	/**
	 * Returns the <code>n</code> closest {@link Spot} to the given location
	 * (encoded as a Spot), contained in the frame <code>frame</code>. If the
	 * number of Spots in the frame is exhausted, a shorter list is returned.
	 * <p>
	 * The list is ordered by increasing distance to the given location.
	 *
	 * @param location
	 *            the location to search for.
	 * @param frame
	 *            the frame to inspect.
	 * @param n
	 *            the number of Spots to search for.
	 * @param visibleSpotsOnly
	 *            if true, will only search though visible Spots. If false,
	 *            will search through all Spots.
	 * @return a new list, with of at most <code>n</code> Spots, ordered by
	 *         increasing distance from the specified location.
	 */
	public final List<Spot> getNClosestSpots(final Spot location, final int frame, int n,
			final boolean visibleSpotsOnly) {
		final Set<Spot> Spots = content.get(frame);
		final TreeMap<Double, Spot> distanceToSpot = new TreeMap<>();

		double d2;
		for (final Spot s : Spots) {

			d2 = s.squareDistanceTo(location);
			distanceToSpot.put(d2, s);
		}

		final List<Spot> selectedSpots = new ArrayList<>(n);
		final Iterator<Double> it = distanceToSpot.keySet().iterator();
		while (n > 0 && it.hasNext()) {
			selectedSpots.add(distanceToSpot.get(it.next()));
			n--;
		}
		return selectedSpots;
	}

	/**
	 * Returns the total number of Spots in this collection, over all frames.
	 *
	 * @param visibleSpotsOnly
	 *            if true, will only count visible Spots. If false count all
	 *            Spots.
	 * @return the total number of Spots in this collection.
	 */
	public final int getNSpots() {
		int nSpots = 0;

		final Iterator<Spot> it = iterator(true);
		while (it.hasNext()) {
			it.next();
			nSpots++;
		}

		return nSpots;
	}

	/**
	 * Returns the number of Spots at the given frame.
	 *
	 * @param frame
	 *            the frame.
	 * @param visibleSpotsOnly
	 *            if true, will only count visible Spots. If false count all
	 *            Spots.
	 * @return the number of Spots at the given frame.
	 */
	public int getNSpots(final int frame) {

		final Iterator<Spot> it = iterator(frame);
		int nSpots = 0;
		while (it.hasNext()) {
			it.next();
			nSpots++;
		}
		return nSpots;

	}

	/*
	 * FEATURES
	 */

	/**
	 * Builds and returns a new map of feature values for this Spot
	 * collection. Each feature maps a double array, with 1 element per
	 * {@link Spot}, all pooled together.
	 *
	 * @param features
	 *            the features to collect
	 * @param visibleOnly
	 *            if <code>true</code>, only the visible Spot values will be
	 *            collected.
	 * @return a new map instance.
	 */
	public Map<String, double[]> collectValues(final Collection<String> features, final boolean visibleOnly) {
		final Map<String, double[]> featureValues = new ConcurrentHashMap<>(features.size());
		final ExecutorService executors = Executors.newFixedThreadPool(numThreads);

		for (final String feature : features) {
			final Runnable command = new Runnable() {
				@Override
				public void run() {
					final double[] values = collectValues(feature, visibleOnly);
					featureValues.put(feature, values);
				}

			};
			executors.execute(command);
		}

		executors.shutdown();
		try {
			final boolean ok = executors.awaitTermination(TIME_OUT_DELAY, TIME_OUT_UNITS);
			if (!ok) {
				System.err.println("[SpotCollection.collectValues()] Timeout of " + TIME_OUT_DELAY + " "
						+ TIME_OUT_UNITS + " reached while filtering.");
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		return featureValues;
	}

	/**
	 * Returns the feature values of this Spot collection as a new double
	 * array.
	 * <p>
	 * If some Spots do not have the interrogated feature set (stored value
	 * is <code>null</code>) or if the value is {@link Double#NaN}, they are
	 * skipped. The returned array might be therefore of smaller size than the
	 * number of Spots interrogated.
	 *
	 * @param feature
	 *            the feature to collect.
	 * @param visibleOnly
	 *            if <code>true</code>, only the visible Spot values will be
	 *            collected.
	 * @return a new <code>double</code> array.
	 */
	public final double[] collectValues(final String feature, final boolean visibleOnly) {
		final double[] values = new double[getNSpots()];
		int index = 0;
		for (final Spot Spot : iterable(visibleOnly)) {
			final Double feat = Spot.getFeature(feature);
			if (null == feat) {
				continue;
			}
			final double val = feat.doubleValue();
			if (Double.isNaN(val)) {
				continue;
			}
			values[index] = val;
			index++;
		}
		return values;
	}

	/*
	 * ITERABLE & co
	 */

	/**
	 * Return an iterator that iterates over all the Spots contained in this
	 * collection.
	 *
	 * @param visibleSpotsOnly
	 *            if true, the returned iterator will only iterate through visible
	 *            Spots. If false, it will iterate over all Spots.
	 * @return an iterator that iterates over this collection.
	 */
	public Iterator<Spot> iterator(final boolean visibleSpotsOnly) {
		if (visibleSpotsOnly)
			return new VisibleSpotsIterator();

		return new AllSpotsIterator();
	}

	/**
	 * Return an iterator that iterates over the Spots in the specified
	 * frame.
	 *
	 * @param visibleSpotsOnly
	 *            if true, the returned iterator will only iterate through visible
	 *            Spots. If false, it will iterate over all Spots.
	 * @param frame
	 *            the frame to iterate over.
	 * @return an iterator that iterates over the content of a frame of this
	 *         collection.
	 */
	public Iterator<Spot> iterator(final Integer frame) {
		final Set<Spot> frameContent = content.get(frame);
		if (null == frameContent) {
			return EMPTY_ITERATOR;
		}

		return frameContent.iterator();
	}

	/**
	 * A convenience methods that returns an {@link Iterable} wrapper for this
	 * collection as a whole.
	 *
	 * @param visibleSpotsOnly
	 *            if true, the iterable will contains only visible Spots.
	 *            Otherwise, it will contain all the Spots.
	 * @return an iterable view of this Spot collection.
	 */
	public Iterable<Spot> iterable(final boolean visibleSpotsOnly) {
		return new WholeCollectionIterable(visibleSpotsOnly);
	}

	/**
	 * A convenience methods that returns an {@link Iterable} wrapper for a specific
	 * frame of this Spot collection. The iterable is backed-up by the actual
	 * collection content, so modifying it can have unexpected results.
	 *
	 * @param visibleSpotsOnly
	 *            if true, the iterable will contains only visible Spots of
	 *            the specified frame. Otherwise, it will contain all the
	 *            Spots of the specified frame.
	 * @param frame
	 *            the frame of the content the returned iterable will wrap.
	 * @return an iterable view of the content of a single frame of this Spot
	 *         collection.
	 */
	public Iterable<Spot> iterable(final int frame, final boolean visibleSpotsOnly) {
		if (visibleSpotsOnly)
			return new FrameVisibleIterable(frame);

		return content.get(frame);
	}

	/*
	 * SORTEDMAP
	 */

	/**
	 * Stores the specified Spots as the content of the specified frame. The
	 * added Spots are all marked as not visible. Their
	 * {@link Spot#FRAME} is updated to be the specified frame.
	 *
	 * @param frame
	 *            the frame to store these Spots at. The specified
	 *            Spots replace the previous content of this frame, if any.
	 * @param Spots
	 *            the Spots to store.
	 */
	public void put(final int frame, final Collection<Spot> Spots) {
		final Set<Spot> value = new HashSet<>(Spots);
		for (final Spot Spot : value) {
			Spot.putFeature(Spot.POSITION_T, Double.valueOf(frame));

		}
		content.put(frame, value);
	}

	/**
	 * Returns the first (lowest) frame currently in this collection.
	 *
	 * @return the first (lowest) frame currently in this collection.
	 */
	public Integer firstKey() {
		if (content.isEmpty()) {
			return 0;
		}
		return content.firstKey();
	}

	/**
	 * Returns the last (highest) frame currently in this collection.
	 *
	 * @return the last (highest) frame currently in this collection.
	 */
	public Integer lastKey() {
		if (content.isEmpty()) {
			return 0;
		}
		return content.lastKey();
	}

	/**
	 * Returns a NavigableSet view of the frames contained in this collection. The
	 * set's iterator returns the keys in ascending order. The set is backed by the
	 * map, so changes to the map are reflected in the set, and vice-versa. The set
	 * supports element removal, which removes the corresponding mapping from the
	 * map, via the Iterator.remove, Set.remove, removeAll, retainAll, and clear
	 * operations. It does not support the add or addAll operations.
	 * <p>
	 * The view's iterator is a "weakly consistent" iterator that will never throw
	 * ConcurrentModificationException, and guarantees to traverse elements as they
	 * existed upon construction of the iterator, and may (but is not guaranteed to)
	 * reflect any modifications subsequent to construction.
	 *
	 * @return a navigable set view of the frames in this collection.
	 */
	public NavigableSet<Integer> keySet() {
		return content.keySet();
	}

	/**
	 * Removes all the content from this collection.
	 */
	public void clear() {
		content.clear();
	}

	/*
	 * MULTITHREADING
	 */

	@Override
	public void setNumThreads() {
		this.numThreads = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public void setNumThreads(final int numThreads) {
		this.numThreads = numThreads;
	}

	@Override
	public int getNumThreads() {
		return numThreads;
	}

	/*
	 * PRIVATE CLASSES
	 */

	private class AllSpotsIterator implements Iterator<Spot> {

		private boolean hasNext = true;

		private final Iterator<Integer> frameIterator;

		private Iterator<Spot> contentIterator;

		private Spot next = null;

		public AllSpotsIterator() {
			this.frameIterator = content.keySet().iterator();
			if (!frameIterator.hasNext()) {
				hasNext = false;
				return;
			}
			final Set<Spot> currentFrameContent = content.get(frameIterator.next());
			contentIterator = currentFrameContent.iterator();
			iterate();
		}

		private void iterate() {
			while (true) {

				// Is there still Spots in current content?
				if (!contentIterator.hasNext()) {
					// No. Then move to next frame.
					// Is there still frames to iterate over?
					if (!frameIterator.hasNext()) {
						// No. Then we are done
						hasNext = false;
						next = null;
						return;
					}

					contentIterator = content.get(frameIterator.next()).iterator();
					continue;
				}
				next = contentIterator.next();
				return;
			}
		}

		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public Spot next() {
			final Spot toReturn = next;
			iterate();
			return toReturn;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"Remove operation is not supported for SpotCollection iterators.");
		}

	}

	private class VisibleSpotsIterator implements Iterator<Spot> {

		private boolean hasNext = true;

		private final Iterator<Integer> frameIterator;

		private Iterator<Spot> contentIterator;

		private Spot next = null;

		private Set<Spot> currentFrameContent;

		public VisibleSpotsIterator() {
			this.frameIterator = content.keySet().iterator();
			if (!frameIterator.hasNext()) {
				hasNext = false;
				return;
			}
			currentFrameContent = content.get(frameIterator.next());
			contentIterator = currentFrameContent.iterator();
			iterate();
		}

		private void iterate() {

			while (true) {
				// Is there still Spots in current content?
				if (!contentIterator.hasNext()) {
					// No. Then move to next frame.
					// Is there still frames to iterate over?
					if (!frameIterator.hasNext()) {
						// No. Then we are done
						hasNext = false;
						next = null;
						return;
					}

					// Yes. Then start iterating over the next frame.
					currentFrameContent = content.get(frameIterator.next());
					contentIterator = currentFrameContent.iterator();
					continue;
				}
				next = contentIterator.next();
				// Is it visible?

			}
		}

		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public Spot next() {
			final Spot toReturn = next;
			iterate();
			return toReturn;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"Remove operation is not supported for SpotCollection iterators.");
		}

	}

	private class VisibleSpotsFrameIterator implements Iterator<Spot> {

		private boolean hasNext = true;

		private Spot next = null;

		private final Iterator<Spot> contentIterator;

		public VisibleSpotsFrameIterator(final Set<Spot> frameContent) {
			if (null == frameContent) {
				this.contentIterator = EMPTY_ITERATOR;
			} else {
				this.contentIterator = frameContent.iterator();
			}
			iterate();
		}

		private void iterate() {
			while (true) {
				if (!contentIterator.hasNext()) {
					// No. Then we are done
					hasNext = false;
					next = null;
					return;
				}
				next = contentIterator.next();
				// Is it visible?

			}
		}

		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public Spot next() {
			final Spot toReturn = next;
			iterate();
			return toReturn;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"Remove operation is not supported for SpotCollection iterators.");
		}

	}

	/**
	 * Returns a new {@link SpotCollection}, made of only the Spots
	 * marked as visible. All the Spots will then be marked as not-visible.
	 *
	 * @return a new Spot collection, made of only the Spots marked as
	 *         visible.
	 */
	public SpotCollection crop() {
		final SpotCollection ns = new SpotCollection();
		ns.setNumThreads(numThreads);

		final Collection<Integer> frames = content.keySet();
		final ExecutorService executors = Executors.newFixedThreadPool(numThreads);
		for (final Integer frame : frames) {

			final Runnable command = new Runnable() {
				@Override
				public void run() {
					final Set<Spot> fc = content.get(frame);
					final Set<Spot> nfc = new HashSet<>(getNSpots(frame));

					for (final Spot Spot : fc) {

						nfc.add(Spot);
					}
					ns.content.put(frame, nfc);
				}
			};
			executors.execute(command);
		}

		executors.shutdown();
		try {
			final boolean ok = executors.awaitTermination(TIME_OUT_DELAY, TIME_OUT_UNITS);
			if (!ok) {
				System.err.println("[SpotCollection.crop()] Timeout of " + TIME_OUT_DELAY + " " + TIME_OUT_UNITS
						+ " reached while cropping.");
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		return ns;
	}

	/**
	 * A convenience wrapper that implements {@link Iterable} for this Spot
	 * collection.
	 */
	private final class WholeCollectionIterable implements Iterable<Spot> {

		private final boolean visibleSpotsOnly;

		public WholeCollectionIterable(final boolean visibleSpotsOnly) {
			this.visibleSpotsOnly = visibleSpotsOnly;
		}

		@Override
		public Iterator<Spot> iterator() {
			if (visibleSpotsOnly)
				return new VisibleSpotsIterator();

			return new AllSpotsIterator();
		}
	}

	/**
	 * A convenience wrapper that implements {@link Iterable} for this Spot
	 * collection.
	 */
	private final class FrameVisibleIterable implements Iterable<Spot> {

		private final int frame;

		public FrameVisibleIterable(final int frame) {
			this.frame = frame;
		}

		@Override
		public Iterator<Spot> iterator() {
			return new VisibleSpotsFrameIterator(content.get(frame));
		}
	}

	private static final Iterator<Spot> EMPTY_ITERATOR = new Iterator<Spot>() {

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Spot next() {
			return null;
		}

		@Override
		public void remove() {
		}
	};

	/*
	 * STATIC METHODS
	 */

	/**
	 * Creates a new {@link SpotCollection} containing only the specified
	 * Spots. Their frame origin is retrieved from their
	 * {@link Spot#FRAME} feature, so it must be set properly for all
	 * Spots. All the Spots of the new collection have the same
	 * visibility that the one they carry.
	 *
	 * @param Spots
	 *            the Spot collection to build from.
	 * @return a new {@link SpotCollection} instance.
	 */
	public static SpotCollection fromCollection(final Iterable<Spot> Spots) {
		final SpotCollection sc = new SpotCollection();
		for (final Spot Spot : Spots) {
			final int frame = Spot.getFeature(Spot.POSITION_T).intValue();
			Set<Spot> fc = sc.content.get(frame);
			if (null == fc) {
				fc = new HashSet<>();
				sc.content.put(frame, fc);
			}
			fc.add(Spot);
		}
		return sc;
	}

	/**
	 * Creates a new {@link SpotCollection} from a copy of the specified map
	 * of sets. The Spots added this way are completely untouched. In
	 * particular, their {@link #VISIBLITY} feature is left untouched, which makes
	 * this method suitable to de-serialize a {@link SpotCollection}.
	 *
	 * @param source
	 *            the map to buidl the Spot collection from.
	 * @return a new SpotCollection.
	 */
	public static SpotCollection fromMap(final Map<Integer, Set<Spot>> source) {
		final SpotCollection sc = new SpotCollection();
		sc.content = new ConcurrentSkipListMap<>(source);
		return sc;
	}
}
