package displayBud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import ij.IJ;
import ij.gui.Arrow;
import ij.gui.Line;
import ij.gui.OvalRoi;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import pluginTools.InteractiveBud;

public class DisplayListOverlay {

	
	
	// Get all the non-zero co ordinates of a binary image 
	public static ArrayList<RealLocalizable> GetCoordinatesBit(
			RandomAccessibleInterval<BitType> actualRoiimg) {

		ArrayList<RealLocalizable> coordinatelist = new ArrayList<RealLocalizable>();
		int ndims = actualRoiimg.numDimensions();
		


		final Cursor<BitType> center = Views.iterable(actualRoiimg).localizingCursor();

		

		while(center.hasNext()) {
			
			center.fwd();
			
			double[] posf = new double[ndims];
			center.localize(posf);
			final RealPoint rpos = new RealPoint(posf);
			if(center.get().getInteger() > 0) {
				coordinatelist.add(rpos);
			}
			
		}
		
	
	
		
		return coordinatelist;
	}
	
	public static void DisplayList(final InteractiveBud parent, List<RealLocalizable> Skelpoints) {
		
		
		
	}
	
	// Display the found points as arrows on the bud
	public static void ArrowDisplay(final InteractiveBud parent,Pair<RealLocalizable, List<RealLocalizable>> Ordered,List<RealLocalizable> Skelpoints, String uniqueID) {
		
		parent.overlay.clear();
		parent.BudOvalRois.clear();
		
		for (int i = 0; i < Ordered.getB().size() ; i += 1) {

			double X = Ordered.getB().get(i).getDoublePosition(0);
			double Y = Ordered.getB().get(i).getDoublePosition(1);


			OvalRoi points =  new OvalRoi((int) X, (int) Y,
					2, 2);
			
			points.setStrokeColor(Color.RED);
			parent.overlay.add(points);
		
		}

		OvalRoi oval = new OvalRoi((int) Ordered.getA().getDoublePosition(0), (int) Ordered.getA().getDoublePosition(1),
				10, 10);
		oval.setStrokeWidth(10);
		oval.setStrokeColor(Color.GREEN);
		parent.overlay.add(oval);
		parent.BudOvalRois.add(oval);
		
		
        for (int i = 0; i < Skelpoints.size(); i++) {
			
			int X = (int)Skelpoints.get(i).getFloatPosition(0);
			int Y = (int)Skelpoints.get(i).getFloatPosition(1);
			
			OvalRoi points =  new OvalRoi((int) X, (int) Y,
					10, 10);
			points.setStrokeColor(Color.PINK);
			points.setStrokeWidth(10);
			parent.overlay.add(points);
		}
		
		
		parent.imp.updateAndDraw();
		
	}
	
	
	
}
