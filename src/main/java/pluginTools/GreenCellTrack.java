package pluginTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JProgressBar;

import budDetector.BCellobject;
import kalmanGUI.CovistoKalmanPanel;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import pluginTools.InteractiveBud.ValueChange;

public class GreenCellTrack {

	final InteractiveBud parent;
	final JProgressBar jpb;

	public GreenCellTrack(final InteractiveBud parent, final JProgressBar jpb) {

		this.parent = parent;
		this.jpb = jpb;

	}

	public void ShowCellTime() {

		int percent = 0;

		for (int t = 1; t <= parent.fourthDimensionSize; ++t) {

			parent.fourthDimension = t;

			parent.ZTRois = new ArrayList<int[]>();
			parent.updatePreview(ValueChange.THIRDDIMmouse);

			GetPixelList(parent.CurrentViewInt);

			TrackEach3DCell compute = new TrackEach3DCell(parent, percent);
			compute.displayCells();

			percent++;

		}

	}

	public void GetPixelList(RandomAccessibleInterval<IntType> intimg) {

		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(intimg), min, max);
		Cursor<IntType> intCursor = Views.iterable(intimg).cursor();
		// Neglect the background class label
		parent.pixellist.clear();

		while (intCursor.hasNext()) {
			intCursor.fwd();
			int i = intCursor.get().get();

			if (!parent.pixellist.contains(i) && i > 0) {
				parent.pixellist.add(i);

			}

		}

	}

	public <T extends Comparable<T> & Type<T>> void computeMinMax(final Iterable<T> input, final T min, final T max) {
		// create a cursor for the image (the order does not matter)
		final Iterator<T> iterator = input.iterator();

		// initialize min and max with the first image value
		T type = iterator.next();

		min.set(type);
		max.set(type);

		// loop over the rest of the data and determine min and max value
		while (iterator.hasNext()) {
			// we need this type more than once
			type = iterator.next();

			if (type.compareTo(min) < 0)
				min.set(type);

			if (type.compareTo(max) > 0)
				max.set(type);
		}
	}

}