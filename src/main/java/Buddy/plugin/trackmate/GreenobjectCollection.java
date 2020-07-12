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

import Buddy.plugin.trackmate.features.FeatureFilter;
import greenDetector.Greenobject;
import net.imglib2.algorithm.MultiThreaded;

/**
 * A utility class that wrap the {@link java.util.SortedMap} we use to store the
 * Greenobjects contained in each frame with a few utility methods.
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
public class GreenobjectCollection implements MultiThreaded {

	public static final Double ZERO = Double.valueOf(0d);

	public static final Double ONE = Double.valueOf(1d);

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

	/** The frame by frame list of Greenobject this object wrap. */
	private ConcurrentSkipListMap<Integer, Set<Greenobject>> content = new ConcurrentSkipListMap<>();

	private int numThreads;

	/*
	 * CONSTRUCTORS
	 */

	/**
	 * Construct a new empty Greenobject collection.
	 */
	public GreenobjectCollection() {
		setNumThreads();
	}

	/*
	 * METHODS
	 */

	/**
	 * Retrieves and returns the {@link Greenobject} object in this collection with
	 * the specified ID. Returns <code>null</code> if the Greenobject cannot be
	 * found. All Greenobjects, visible or not, are searched for.
	 *
	 * @param ID
	 *            the ID to look for.
	 * @return the Greenobject with the specified ID or <code>null</code> if this
	 *         Greenobject does not exist or does not belong to this collection.
	 */
	public Greenobject search(final int ID) {
		Greenobject Greenobject = null;
		for (final Greenobject s : iterable(false)) {
			if (s.ID() == ID) {
				Greenobject = s;
				break;
			}
		}
		return Greenobject;
	}

	@Override
	public String toString() {
		String str = super.toString();
		str += ": contains " + getNGreenobjects() + " Greenobjects total in " + keySet().size()
				+ " different frames, over which " + getNGreenobjects() + " are visible:\n";
		for (final int key : content.keySet()) {
			str += "\tframe " + key + ": " + getNGreenobjects(key) + " Greenobjects total, " + getNGreenobjects(key)
					+ " visible.\n";
		}
		return str;
	}

	/**
	 * Adds the given Greenobject to this collection, at the specified frame, and
	 * mark it as visible.
	 * <p>
	 * If the frame does not exist yet in the collection, it is created and added.
	 * Upon adding, the added Greenobject has its feature {@link Greenobject#FRAME}
	 * updated with the passed frame value.
	 * 
	 * @param Greenobject
	 *            the Greenobject to add.
	 * @param frame
	 *            the frame to add it to.
	 */
	public void add(final Greenobject Greenobject, final Integer frame) {
		Set<Greenobject> Greenobjects = content.get(frame);
		if (null == Greenobjects) {
			Greenobjects = new HashSet<>();
			content.put(frame, Greenobjects);
		}
		Greenobjects.add(Greenobject);
		Greenobject.time = frame;
	}

	/**
	 * Removes the given Greenobject from this collection, at the specified frame.
	 * <p>
	 * If the Greenobject frame collection does not exist yet, nothing is done and
	 * <code>false</code> is returned. If the Greenobject cannot be found in the
	 * frame content, nothing is done and <code>false</code> is returned.
	 * 
	 * @param Greenobject
	 *            the Greenobject to remove.
	 * @param frame
	 *            the frame to remove it from.
	 * @return <code>true</code> if the Greenobject was succesfully removed.
	 */
	public boolean remove(final Greenobject Greenobject, final Integer frame) {
		final Set<Greenobject> Greenobjects = content.get(frame);
		if (null == Greenobjects) {
			return false;
		}
		return Greenobjects.remove(Greenobject);
	}

	/**
	 * Filters out the content of this collection using the specified
	 * {@link FeatureFilter} collection. Greenobjects that are filtered out are
	 * marked as invisible, and visible otherwise. To be marked as visible, a
	 * Greenobject must pass <b>all</b> of the specified filters (AND chaining).
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
					final Set<Greenobject> Greenobjects = content.get(frame);

					Double val, tval;
					boolean isAbove;
					for (final Greenobject Greenobject : Greenobjects) {

						for (final FeatureFilter featureFilter : filters) {

							val = Greenobject.getFeature(featureFilter.feature);
							tval = featureFilter.value;
							isAbove = featureFilter.isAbove;

							if (isAbove && val.compareTo(tval) < 0 || !isAbove && val.compareTo(tval) > 0) {
								break;
							}
						} // loop over filters

					} // loop over Greenobjects

				}

			};
			executors.execute(command);
		}

		executors.shutdown();
		try {
			final boolean ok = executors.awaitTermination(TIME_OUT_DELAY, TIME_OUT_UNITS);
			if (!ok) {
				System.err.println("[GreenobjectCollection.filter()] Timeout of " + TIME_OUT_DELAY + " "
						+ TIME_OUT_UNITS + " reached while filtering.");
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the closest {@link Greenobject} to the given location (encoded as a
	 * Greenobject), contained in the frame <code>frame</code>. If the frame has no
	 * Greenobject, return <code>null</code>.
	 *
	 * @param location
	 *            the location to search for.
	 * @param frame
	 *            the frame to inspect.
	 * @param visibleGreenobjectsOnly
	 *            if true, will only search though visible Greenobjects. If false,
	 *            will search through all Greenobjects.
	 * @return the closest Greenobject to the specified location, member of this
	 *         collection.
	 */
	public final Greenobject getClosestGreenobject(final Greenobject location, final int frame,
			final boolean visibleGreenobjectsOnly) {
		final Set<Greenobject> Greenobjects = content.get(frame);
		if (null == Greenobjects)
			return null;
		double d2;
		double minDist = Double.POSITIVE_INFINITY;
		Greenobject target = null;
		for (final Greenobject s : Greenobjects) {

			d2 = s.squareDistanceTo(location);
			if (d2 < minDist) {
				minDist = d2;
				target = s;
			}

		}
		return target;
	}

	/**
	 * Returns the {@link Greenobject} at the given location (encoded as a
	 * Greenobject), contained in the frame <code>frame</code>. A Greenobject is
	 * returned <b>only</b> if there exists a Greenobject such that the given
	 * location is within the Greenobject radius. Otherwise <code>null</code> is
	 * returned.
	 *
	 * @param location
	 *            the location to search for.
	 * @param frame
	 *            the frame to inspect.
	 * @param visibleGreenobjectsOnly
	 *            if true, will only search though visible Greenobjects. If false,
	 *            will search through all Greenobjects.
	 * @return the closest Greenobject such that the specified location is within
	 *         its radius, member of this collection, or <code>null</code> is such a
	 *         Greenobjects cannot be found.
	 */
	public final Greenobject getGreenobjectAt(final Greenobject location, final int frame,
			final boolean visibleGreenobjectsOnly) {
		final Set<Greenobject> Greenobjects = content.get(frame);
		if (null == Greenobjects || Greenobjects.isEmpty()) {
			return null;
		}

		final TreeMap<Double, Greenobject> distanceToGreenobject = new TreeMap<>();
		double d2;
		for (final Greenobject s : Greenobjects) {

			d2 = s.squareDistanceTo(location);
			if (d2 < s.getFeature(Greenobject.RADIUS) * s.getFeature(Greenobject.RADIUS))
				distanceToGreenobject.put(d2, s);
		}
		if (distanceToGreenobject.isEmpty())
			return null;

		return distanceToGreenobject.firstEntry().getValue();
	}

	/**
	 * Returns the <code>n</code> closest {@link Greenobject} to the given location
	 * (encoded as a Greenobject), contained in the frame <code>frame</code>. If the
	 * number of Greenobjects in the frame is exhausted, a shorter list is returned.
	 * <p>
	 * The list is ordered by increasing distance to the given location.
	 *
	 * @param location
	 *            the location to search for.
	 * @param frame
	 *            the frame to inspect.
	 * @param n
	 *            the number of Greenobjects to search for.
	 * @param visibleGreenobjectsOnly
	 *            if true, will only search though visible Greenobjects. If false,
	 *            will search through all Greenobjects.
	 * @return a new list, with of at most <code>n</code> Greenobjects, ordered by
	 *         increasing distance from the specified location.
	 */
	public final List<Greenobject> getNClosestGreenobjects(final Greenobject location, final int frame, int n,
			final boolean visibleGreenobjectsOnly) {
		final Set<Greenobject> Greenobjects = content.get(frame);
		final TreeMap<Double, Greenobject> distanceToGreenobject = new TreeMap<>();

		double d2;
		for (final Greenobject s : Greenobjects) {

			d2 = s.squareDistanceTo(location);
			distanceToGreenobject.put(d2, s);
		}

		final List<Greenobject> selectedGreenobjects = new ArrayList<>(n);
		final Iterator<Double> it = distanceToGreenobject.keySet().iterator();
		while (n > 0 && it.hasNext()) {
			selectedGreenobjects.add(distanceToGreenobject.get(it.next()));
			n--;
		}
		return selectedGreenobjects;
	}

	/**
	 * Returns the total number of Greenobjects in this collection, over all frames.
	 *
	 * @param visibleGreenobjectsOnly
	 *            if true, will only count visible Greenobjects. If false count all
	 *            Greenobjects.
	 * @return the total number of Greenobjects in this collection.
	 */
	public final int getNGreenobjects() {
		int nGreenobjects = 0;

		final Iterator<Greenobject> it = iterator(true);
		while (it.hasNext()) {
			it.next();
			nGreenobjects++;
		}

		return nGreenobjects;
	}

	/**
	 * Returns the number of Greenobjects at the given frame.
	 *
	 * @param frame
	 *            the frame.
	 * @param visibleGreenobjectsOnly
	 *            if true, will only count visible Greenobjects. If false count all
	 *            Greenobjects.
	 * @return the number of Greenobjects at the given frame.
	 */
	public int getNGreenobjects(final int frame) {

		final Iterator<Greenobject> it = iterator(frame);
		int nGreenobjects = 0;
		while (it.hasNext()) {
			it.next();
			nGreenobjects++;
		}
		return nGreenobjects;

	}

	/*
	 * FEATURES
	 */

	/**
	 * Builds and returns a new map of feature values for this Greenobject
	 * collection. Each feature maps a double array, with 1 element per
	 * {@link Greenobject}, all pooled together.
	 *
	 * @param features
	 *            the features to collect
	 * @param visibleOnly
	 *            if <code>true</code>, only the visible Greenobject values will be
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
				System.err.println("[GreenobjectCollection.collectValues()] Timeout of " + TIME_OUT_DELAY + " "
						+ TIME_OUT_UNITS + " reached while filtering.");
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		return featureValues;
	}

	/**
	 * Returns the feature values of this Greenobject collection as a new double
	 * array.
	 * <p>
	 * If some Greenobjects do not have the interrogated feature set (stored value
	 * is <code>null</code>) or if the value is {@link Double#NaN}, they are
	 * skipped. The returned array might be therefore of smaller size than the
	 * number of Greenobjects interrogated.
	 *
	 * @param feature
	 *            the feature to collect.
	 * @param visibleOnly
	 *            if <code>true</code>, only the visible Greenobject values will be
	 *            collected.
	 * @return a new <code>double</code> array.
	 */
	public final double[] collectValues(final String feature, final boolean visibleOnly) {
		final double[] values = new double[getNGreenobjects()];
		int index = 0;
		for (final Greenobject Greenobject : iterable(visibleOnly)) {
			final Double feat = Greenobject.getFeature(feature);
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
	 * Return an iterator that iterates over all the Greenobjects contained in this
	 * collection.
	 *
	 * @param visibleGreenobjectsOnly
	 *            if true, the returned iterator will only iterate through visible
	 *            Greenobjects. If false, it will iterate over all Greenobjects.
	 * @return an iterator that iterates over this collection.
	 */
	public Iterator<Greenobject> iterator(final boolean visibleGreenobjectsOnly) {
		if (visibleGreenobjectsOnly)
			return new VisibleGreenobjectsIterator();

		return new AllGreenobjectsIterator();
	}

	/**
	 * Return an iterator that iterates over the Greenobjects in the specified
	 * frame.
	 *
	 * @param visibleGreenobjectsOnly
	 *            if true, the returned iterator will only iterate through visible
	 *            Greenobjects. If false, it will iterate over all Greenobjects.
	 * @param frame
	 *            the frame to iterate over.
	 * @return an iterator that iterates over the content of a frame of this
	 *         collection.
	 */
	public Iterator<Greenobject> iterator(final Integer frame) {
		final Set<Greenobject> frameContent = content.get(frame);
		if (null == frameContent) {
			return EMPTY_ITERATOR;
		}

		return frameContent.iterator();
	}

	/**
	 * A convenience methods that returns an {@link Iterable} wrapper for this
	 * collection as a whole.
	 *
	 * @param visibleGreenobjectsOnly
	 *            if true, the iterable will contains only visible Greenobjects.
	 *            Otherwise, it will contain all the Greenobjects.
	 * @return an iterable view of this Greenobject collection.
	 */
	public Iterable<Greenobject> iterable(final boolean visibleGreenobjectsOnly) {
		return new WholeCollectionIterable(visibleGreenobjectsOnly);
	}

	/**
	 * A convenience methods that returns an {@link Iterable} wrapper for a specific
	 * frame of this Greenobject collection. The iterable is backed-up by the actual
	 * collection content, so modifying it can have unexpected results.
	 *
	 * @param visibleGreenobjectsOnly
	 *            if true, the iterable will contains only visible Greenobjects of
	 *            the specified frame. Otherwise, it will contain all the
	 *            Greenobjects of the specified frame.
	 * @param frame
	 *            the frame of the content the returned iterable will wrap.
	 * @return an iterable view of the content of a single frame of this Greenobject
	 *         collection.
	 */
	public Iterable<Greenobject> iterable(final int frame, final boolean visibleGreenobjectsOnly) {
		if (visibleGreenobjectsOnly)
			return new FrameVisibleIterable(frame);

		return content.get(frame);
	}

	/*
	 * SORTEDMAP
	 */

	/**
	 * Stores the specified Greenobjects as the content of the specified frame. The
	 * added Greenobjects are all marked as not visible. Their
	 * {@link Greenobject#FRAME} is updated to be the specified frame.
	 *
	 * @param frame
	 *            the frame to store these Greenobjects at. The specified
	 *            Greenobjects replace the previous content of this frame, if any.
	 * @param Greenobjects
	 *            the Greenobjects to store.
	 */
	public void put(final int frame, final Collection<Greenobject> Greenobjects) {
		final Set<Greenobject> value = new HashSet<>(Greenobjects);
		for (final Greenobject Greenobject : value) {
			Greenobject.putFeature(Greenobject.POSITION_T, Double.valueOf(frame));

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

	private class AllGreenobjectsIterator implements Iterator<Greenobject> {

		private boolean hasNext = true;

		private final Iterator<Integer> frameIterator;

		private Iterator<Greenobject> contentIterator;

		private Greenobject next = null;

		public AllGreenobjectsIterator() {
			this.frameIterator = content.keySet().iterator();
			if (!frameIterator.hasNext()) {
				hasNext = false;
				return;
			}
			final Set<Greenobject> currentFrameContent = content.get(frameIterator.next());
			contentIterator = currentFrameContent.iterator();
			iterate();
		}

		private void iterate() {
			while (true) {

				// Is there still Greenobjects in current content?
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
		public Greenobject next() {
			final Greenobject toReturn = next;
			iterate();
			return toReturn;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"Remove operation is not supported for GreenobjectCollection iterators.");
		}

	}

	private class VisibleGreenobjectsIterator implements Iterator<Greenobject> {

		private boolean hasNext = true;

		private final Iterator<Integer> frameIterator;

		private Iterator<Greenobject> contentIterator;

		private Greenobject next = null;

		private Set<Greenobject> currentFrameContent;

		public VisibleGreenobjectsIterator() {
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
				// Is there still Greenobjects in current content?
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
		public Greenobject next() {
			final Greenobject toReturn = next;
			iterate();
			return toReturn;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"Remove operation is not supported for GreenobjectCollection iterators.");
		}

	}

	private class VisibleGreenobjectsFrameIterator implements Iterator<Greenobject> {

		private boolean hasNext = true;

		private Greenobject next = null;

		private final Iterator<Greenobject> contentIterator;

		public VisibleGreenobjectsFrameIterator(final Set<Greenobject> frameContent) {
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
		public Greenobject next() {
			final Greenobject toReturn = next;
			iterate();
			return toReturn;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"Remove operation is not supported for GreenobjectCollection iterators.");
		}

	}

	/**
	 * Returns a new {@link GreenobjectCollection}, made of only the Greenobjects
	 * marked as visible. All the Greenobjects will then be marked as not-visible.
	 *
	 * @return a new Greenobject collection, made of only the Greenobjects marked as
	 *         visible.
	 */
	public GreenobjectCollection crop() {
		final GreenobjectCollection ns = new GreenobjectCollection();
		ns.setNumThreads(numThreads);

		final Collection<Integer> frames = content.keySet();
		final ExecutorService executors = Executors.newFixedThreadPool(numThreads);
		for (final Integer frame : frames) {

			final Runnable command = new Runnable() {
				@Override
				public void run() {
					final Set<Greenobject> fc = content.get(frame);
					final Set<Greenobject> nfc = new HashSet<>(getNGreenobjects(frame));

					for (final Greenobject Greenobject : fc) {

						nfc.add(Greenobject);
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
				System.err.println("[GreenobjectCollection.crop()] Timeout of " + TIME_OUT_DELAY + " " + TIME_OUT_UNITS
						+ " reached while cropping.");
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		return ns;
	}

	/**
	 * A convenience wrapper that implements {@link Iterable} for this Greenobject
	 * collection.
	 */
	private final class WholeCollectionIterable implements Iterable<Greenobject> {

		private final boolean visibleGreenobjectsOnly;

		public WholeCollectionIterable(final boolean visibleGreenobjectsOnly) {
			this.visibleGreenobjectsOnly = visibleGreenobjectsOnly;
		}

		@Override
		public Iterator<Greenobject> iterator() {
			if (visibleGreenobjectsOnly)
				return new VisibleGreenobjectsIterator();

			return new AllGreenobjectsIterator();
		}
	}

	/**
	 * A convenience wrapper that implements {@link Iterable} for this Greenobject
	 * collection.
	 */
	private final class FrameVisibleIterable implements Iterable<Greenobject> {

		private final int frame;

		public FrameVisibleIterable(final int frame) {
			this.frame = frame;
		}

		@Override
		public Iterator<Greenobject> iterator() {
			return new VisibleGreenobjectsFrameIterator(content.get(frame));
		}
	}

	private static final Iterator<Greenobject> EMPTY_ITERATOR = new Iterator<Greenobject>() {

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Greenobject next() {
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
	 * Creates a new {@link GreenobjectCollection} containing only the specified
	 * Greenobjects. Their frame origin is retrieved from their
	 * {@link Greenobject#FRAME} feature, so it must be set properly for all
	 * Greenobjects. All the Greenobjects of the new collection have the same
	 * visibility that the one they carry.
	 *
	 * @param Greenobjects
	 *            the Greenobject collection to build from.
	 * @return a new {@link GreenobjectCollection} instance.
	 */
	public static GreenobjectCollection fromCollection(final Iterable<Greenobject> Greenobjects) {
		final GreenobjectCollection sc = new GreenobjectCollection();
		for (final Greenobject Greenobject : Greenobjects) {
			final int frame = Greenobject.getFeature(Greenobject.POSITION_T).intValue();
			Set<Greenobject> fc = sc.content.get(frame);
			if (null == fc) {
				fc = new HashSet<>();
				sc.content.put(frame, fc);
			}
			fc.add(Greenobject);
		}
		return sc;
	}

	/**
	 * Creates a new {@link GreenobjectCollection} from a copy of the specified map
	 * of sets. The Greenobjects added this way are completely untouched. In
	 * particular, their {@link #VISIBLITY} feature is left untouched, which makes
	 * this method suitable to de-serialize a {@link GreenobjectCollection}.
	 *
	 * @param source
	 *            the map to buidl the Greenobject collection from.
	 * @return a new GreenobjectCollection.
	 */
	public static GreenobjectCollection fromMap(final Map<Integer, Set<Greenobject>> source) {
		final GreenobjectCollection sc = new GreenobjectCollection();
		sc.content = new ConcurrentSkipListMap<>(source);
		return sc;
	}
}
