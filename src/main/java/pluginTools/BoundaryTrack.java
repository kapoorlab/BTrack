package pluginTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JProgressBar;

import kalmanGUI.CovistoKalmanPanel;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import pluginTools.InteractiveBud.ValueChange;


public class BoundaryTrack {
	
	
	final InteractiveBud parent;
	final JProgressBar jpb;
	
	
	public BoundaryTrack(final InteractiveBud parent, final JProgressBar jpb) {
		
		this.parent = parent;
		this.jpb = jpb;
		
	}
	
	
	public void ShowBoundary() {
		
		int percent = 0;
		RandomAccessibleInterval<IntType> BudSeg = utility.BudSlicer.getCurrentBudView(parent.Segoriginalimg,(int) parent.thirdDimension,
				(int)parent.thirdDimensionSize);
		
		GetPixelList(BudSeg);
		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(BudSeg), min, max);
		
		
		TrackEachBud compute = new TrackEachBud(parent, BudSeg, parent.thirdDimension, max.get(), percent);
		
		compute.displayBuds();
		
	}
	
	
	public void ShowBoundaryTime() {
		
		int percent = 0;
		for(int t = 1; t <= parent.thirdDimensionSize; ++t) {
			
			if (parent.mvl != null) {
				parent.imp.getCanvas().removeMouseListener(parent.mvl);
			    parent.mouseremoved = true;	
			}
			
			if(parent.EscapePressed) {

				if(parent.jpb!=null ) 
					utility.BudProgressBar.SetProgressBar(parent.jpb, 100 ,
							"You pressed Escape to stop calculation, press restart to start again" );
				parent.EscapePressed = false;
				CovistoKalmanPanel.Skeletontime.setEnabled(true);
				break;
			}
			
		parent.thirdDimension = t;
		parent.updatePreview(ValueChange.THIRDDIMmouse);
		
		parent.inputFieldT.setText(Integer.toString((int)parent.thirdDimension));
		
		parent.timeslider.setValue(utility.BudSlicer.computeScrollbarPositionFromValue(parent.thirdDimension, parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));
		parent.timeslider.repaint();
		parent.timeslider.validate();
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
		
		
		RandomAccessibleInterval<IntType> BudSeg = utility.BudSlicer.getCurrentBudView(parent.Segoriginalimg,(int) parent.thirdDimension,
				(int)parent.thirdDimensionSize);
		GetPixelList(BudSeg);
		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(BudSeg), min, max);
		
		
		TrackEachBud compute = new TrackEachBud(parent, BudSeg, parent.thirdDimension, max.get(), percent);
		
		compute.displayBuds();
			
		
		percent++;
		
		}
		
		if(parent.jpb!=null && parent.thirdDimension == parent.thirdDimensionSize) {
			CovistoKalmanPanel.Skeletontime.setEnabled(true);
			CovistoKalmanPanel.Timetrack.setEnabled(true);
			parent.SaveAllbutton.setEnabled(true);
			parent.Savebutton.setEnabled(true);
			utility.BudProgressBar.SetProgressBar(parent.jpb, 100 ,
					"Skeletons Created, Push Track Buddies Button" );
			parent.AllRefcords = new HashMap<String, RealLocalizable>();
			parent.AllBudcenter = new ArrayList<RealLocalizable>();
			parent.ChosenBudcenter = new ArrayList<RealLocalizable>();	
			
		}
	}
	
	public  void GetPixelList(RandomAccessibleInterval<IntType> intimg) {

		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(intimg), min, max);
		Cursor<IntType> intCursor = Views.iterable(intimg).cursor();
		// Neglect the background class label
		int currentLabel = max.get();
		parent.pixellist.clear();
		
		
		while (intCursor.hasNext()) {
			intCursor.fwd();
			int i = intCursor.get().get();
			if (i != currentLabel ) {

				parent.pixellist.add(i);

				currentLabel = i;

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
