package pluginTools;

import java.util.ArrayList;
import java.util.List;

import budDetector.BCellobject;
import budDetector.Budobject;
import budDetector.Budpointobject;
import budDetector.Budregionobject;
import budDetector.Cellobject;
import displayBud.DisplayListOverlay;
import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class ObjectMaker implements Runnable {

	public int labelgreen;
	public 	ArrayList<Cellobject> Allcells;
	public final RandomAccessibleInterval<IntType> GreenCellSeg ;
	final InteractiveBud parent;
	
	public ObjectMaker(InteractiveBud parent, RandomAccessibleInterval<IntType> GreenCellSeg, ArrayList<Cellobject> Allcells,int labelgreen ) {
		
		this.parent = parent;
		this.GreenCellSeg = GreenCellSeg;
		this.labelgreen = labelgreen;
         this.Allcells = Allcells;
		
		
	}
	
	@Override
	public void run() {
		Budregionobject PairCurrentViewBit = TrackEachBud
				.BudCurrentLabelBinaryImage3D(GreenCellSeg, labelgreen);
		
		
		// For each bud get the list of points
		List<RealLocalizable> bordercelltruths = DisplayListOverlay.GetCoordinatesBit(PairCurrentViewBit.Boundaryimage);
		List<RealLocalizable> interiorcelltruths = DisplayListOverlay.GetCoordinatesBit(PairCurrentViewBit.Boundaryimage);
		double cellArea = Volume(PairCurrentViewBit.Boundaryimage);
		double cellPerimeter = Volume(PairCurrentViewBit.Boundaryimage);
		Localizable cellcenterpoint = budDetector.Listordering.getIntMean3DCord(bordercelltruths);
		double intensity = getIntensity(parent, PairCurrentViewBit.Boundaryimage);
		double[] Extents = radiusXYZ( PairCurrentViewBit.Boundaryimage);
		Cellobject insideGreencells = new Cellobject(interiorcelltruths, bordercelltruths, cellcenterpoint, intensity, cellArea, cellPerimeter, Extents); 
		Allcells.add(insideGreencells);
		

		for (RealLocalizable insidetruth : bordercelltruths) {

			Integer xPts = (int) insidetruth.getFloatPosition(0);
			Integer yPts = (int) insidetruth.getFloatPosition(1);
			Integer zPts = (int) insidetruth.getFloatPosition(2);
			parent.ZTRois.add(new int[] {xPts, yPts, zPts});
		
		}
		
	}
	public static < T extends RealType< T > > double[] radiusXYZ( final RandomAccessibleInterval< T > img)
	{
	  
	  double radiusX = img.realMax(0) - img.realMin(0);
	  double radiusY = img.realMax(1) - img.realMin(1);
	  double radiusZ = img.realMax(2) - img.realMin(2);
	  
	  return new double[]{ radiusX, radiusY, radiusZ };
	}

	public static < T extends RealType< T > > double[] radiusXY( final RandomAccessibleInterval< T > img)
	{
	  
	  double radiusX = img.realMax(0) - img.realMin(0);
	  double radiusY = img.realMax(1) - img.realMin(1);
	  
	  return new double[]{ radiusX, radiusY, 1 };
	}

	public static < T extends RealType< T > > double Volume( final RandomAccessibleInterval< T > img)
	{
		
	  Cursor<T> cur = Views.iterable(img).localizingCursor();
	  double Vol = 0;
	  
	  while(cur.hasNext()) {
		  
		  cur.fwd();
		  if(cur.get().getRealFloat() > 0)
		       Vol++;
	  }
	  

	  return Vol;
	  
	}


		
		public static double getIntensity(InteractiveBud parent, RandomAccessibleInterval<BitType> Regionimage) {
			
			double intensity = 0;
			
			Cursor<BitType> cursor =  Views.iterable(Regionimage).localizingCursor();
			
			RandomAccess<FloatType> intran = parent.CurrentView.randomAccess();
			
			while(cursor.hasNext()) {
				
				cursor.fwd();
				
				if(cursor.get().getInteger() > 0 ) {
					
					intensity+=intran.get().get();
					
				}
				
			}
			
			
			return intensity;		
			
		}

}