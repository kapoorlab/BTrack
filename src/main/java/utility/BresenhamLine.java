package utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;

/**
 * n-Dimension Bresenham line (<a href=
 * "http://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm">http://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm"</a>)
 * : an iterator that iterates all the pixels between two points.
 * <p>
 * This implementation uses eager initialization: all the coordinates along the
 * line are calculated at once when the {@link #reset(Localizable, Localizable)}
 * method is called.
 * </p>
 * <p>
 * Adapted from the <code>Bresenham3D</code> class in VIB-lib by Johannes
 * Schindelin.
 * 
 * @author Jean-Yves Tinevez Apr, 2012
 * @param <t>
 */
public class BresenhamLine<T> implements Cursor<T> {

	private Localizable P1;

	private Localizable P2;

	private Iterator<long[]> iterator;

	private long[] current;

	private final RandomAccess<T> ra;

	/*
	 * CONSTRUCTOR
	 */

	public BresenhamLine(final RandomAccessible<T> source) {
		this.ra = source.randomAccess();
	}

	public BresenhamLine(final RandomAccessible<T> source, final Localizable P1, final Localizable P2) {
		this(source.randomAccess(), P1, P2);
	}

	public BresenhamLine(RandomAccess<T> ra, Localizable P1, Localizable P2) {
		this.ra = ra;
		this.P1 = P1;
		this.P2 = P2;
		reset(P1, P2);
	}

	/*
	 * METHODS
	 */

	public void reset(final Localizable P1, Localizable P2) {
		this.iterator = generateCoords(P1, P2).iterator();
	}

	@Override
	public T get() {
		return ra.get();
	}

	@Override
	public void fwd() {
		current = iterator.next();
		ra.setPosition(current);
	}

	@Override
	public void reset() {
		reset(P1, P2);
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public void localize(long[] position) {
		for (int d = 0; d < current.length; d++) {
			position[d] = current[d];
		}
	}

	@Override
	public long getLongPosition(int d) {
		return current[d];
	}

	@Override
	public BresenhamLine<T> copy() {
		return new BresenhamLine<T>(ra, P1, P2);
	}

	@Override
	public BresenhamLine<T> copyCursor() {
		return copy();
	}

	@Override
	public void localize(float[] position) {
		for (int d = 0; d < current.length; d++) {
			position[d] = current[d];
		}
	}

	@Override
	public void localize(double[] position) {
		for (int d = 0; d < current.length; d++) {
			position[d] = current[d];
		}
	}

	@Override
	public float getFloatPosition(int d) {
		return current[d];
	}

	@Override
	public double getDoublePosition(int d) {
		return current[d];
	}

	@Override
	public int numDimensions() {
		return current.length;
	}

	@Override
	public void jumpFwd(long steps) {
		for (int i = 0; i < steps; i++) {
			current = iterator.next();
		}
		ra.setPosition(current);
	}

	@Override
	public T next() {
		fwd();
		return get();
	}

	@Override
	public void remove() {
	}

	@Override
	public void localize(int[] position) {
		for (int d = 0; d < current.length; d++) {
			position[d] = (int) current[d];
		}
	}

	@Override
	public int getIntPosition(int d) {
		return (int) current[d];
	}

	public static final ArrayList<long[]> generateCoords(final Localizable start, final Localizable end) {

		final int nd = start.numDimensions();
		long[] dxs = new long[nd];
		long[] as = new long[nd];
		float[] signs = new float[nd];

		for (int d = 0; d < nd; d++) {
			dxs[d] = end.getLongPosition(d) - start.getLongPosition(d);
			as[d] = Math.abs(dxs[d]) << 1;
			signs[d] = Math.signum(dxs[d]);
		}

		// Find dominant direction & max ∆x
		int dm = -1;
		long am = -1;
		long maxDelta = -1;
		for (int d = 0; d < nd; d++) {
			if (as[d] >= am) {
				dm = d;
				am = as[d];
			}

			if (Math.abs(dxs[d]) > maxDelta) {
				maxDelta = Math.abs(dxs[d]);
			}
		}

		long[] xd = new long[nd];
		for (int d = 0; d < nd; d++) {
			xd[d] = as[d] - (as[dm] >> 1);
		}

		long[] x = new long[nd];
		start.localize(x);

		final ArrayList<long[]> coords = new ArrayList<long[]>((int) maxDelta);
		while (true) {

			coords.add(x);
			x = Arrays.copyOf(x, x.length);

			if (x[dm] == end.getLongPosition(dm)) {
				return coords;
			}

			for (int d = 0; d < nd; d++) {
				if (d == dm) {
					continue;
				}

				if (xd[d] >= 0) {
					x[d] += signs[d];
					xd[d] -= as[dm];
				}

			}

			x[dm] += signs[dm];

			for (int d = 0; d < nd; d++) {
				if (d == dm) {
					continue;
				}

				xd[d] += as[d];
			}

		}

	}

}
