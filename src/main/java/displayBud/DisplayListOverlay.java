package displayBud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import ij.IJ;
import ij.gui.Arrow;
import ij.gui.OvalRoi;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import pluginTools.InteractiveBud;

public class DisplayListOverlay {

	
	
	// Get all the non-zero co ordinates of a binary image 
	public static ArrayList<RealLocalizable> GetCoordinatesBit(
			RandomAccessibleInterval<FloatType> actualRoiimg) {

		ArrayList<RealLocalizable> coordinatelist = new ArrayList<RealLocalizable>();
		int ndims = actualRoiimg.numDimensions();
		


		final Cursor<FloatType> center = Views.iterable(actualRoiimg).localizingCursor();

		

		while(center.hasNext()) {
			
			center.fwd();
			
			double[] posf = new double[ndims];
			center.localize(posf);
			final RealPoint rpos = new RealPoint(posf);
			if(center.get().get() > 0) {
				coordinatelist.add(rpos);
			}
			
		}
		
	
	
		
		return coordinatelist;
	}
	
	
	
	// Display the found points as arrows on the bud
	public static void ArrowDisplay(final InteractiveBud parent,Pair<RealLocalizable, List<RealLocalizable>> Ordered, String uniqueID) {
		
		for (int i = 0; i < Ordered.getB().size() - 10; i += 10) {

			double X = Ordered.getB().get(i).getDoublePosition(0);
			double Y = Ordered.getB().get(i).getDoublePosition(1);

			double nextX = Ordered.getB().get(i + 10).getDoublePosition(0);
			double nextY = Ordered.getB().get(i + 10).getDoublePosition(1);

			Arrow line = new Arrow(X, Y, nextX, nextY);
			line.setStrokeWidth(0.01);
			parent.overlay.add(line);
		}

		OvalRoi oval = new OvalRoi((int) Ordered.getA().getDoublePosition(0), (int) Ordered.getA().getDoublePosition(1),
				10, 10);
		oval.setStrokeWidth(10);
		oval.setStrokeColor(Color.GREEN);
		parent.overlay.add(oval);
		parent.imp.updateAndDraw();
		parent.Refcord = Ordered.getA();

		parent.AllRefcords.put(uniqueID, parent.Refcord);
	}
	
	

	
	
}
