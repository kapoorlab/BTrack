package pluginTools;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import budDetector.BCellobject;
import budDetector.Budobject;
import budDetector.Budpointobject;
import budDetector.Budregionobject;
import budDetector.Cellobject;
import budDetector.Distance;
import kalmanGUI.CovistoKalmanPanel;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.neighborhood.DiamondShape;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import skeleton.*;
import utility.GetNearest;
import utility.SavePink;
import displayBud.DisplayListOverlay;
import ij.gui.OvalRoi;

public class TrackEachBud {


	final InteractiveBud parent;
	final int t;
	final int maxlabel;
	int percent;
	final ArrayList<Budobject> Budlist;
	final ArrayList<Budpointobject> Budpointlist;
	final ArrayList<BCellobject> Budcelllist;
    ArrayList<Cellobject> celllist = new ArrayList<Cellobject>();
	public TrackEachBud(final InteractiveBud parent, 
			ArrayList<Budobject> Budlist, ArrayList<Budpointobject> Budpointlist, final int t, final int maxlabel,
			final int percent) {

		this.parent = parent;
		this.t = t;
		this.maxlabel = maxlabel;
		this.percent = percent;
		this.Budlist = Budlist;
		this.Budpointlist = Budpointlist;
        this.Budcelllist = null;
	}
	
	public TrackEachBud(final InteractiveBud parent, 
			ArrayList<Budobject> Budlist, ArrayList<Budpointobject> Budpointlist, ArrayList<BCellobject> Budcelllist, final int t, final int maxlabel,
			final int percent) {

		this.parent = parent;
		this.t = t;
		this.maxlabel = maxlabel;
		this.percent = percent;
		this.Budlist = Budlist;
		this.Budpointlist = Budpointlist;
        this.Budcelllist = Budcelllist;
	}


	

	
	
	public ArrayList<Budobject> returnBudlist(){
		
		
		return Budlist;
	}
	
    public ArrayList<Budpointobject> returnBudpointlist(){
		
		
		return Budpointlist;
	}
	
    public ArrayList<BCellobject> returnBCellobjectlist(){
		
		
		return Budcelllist;
	}
	
	
	public void displayBuds() {

		
		
		
		 ArrayList<Pair<Color,OvalRoi>> Allrois = new ArrayList<Pair<Color,OvalRoi>>();
		String uniqueID = Integer.toString(parent.thirdDimension);
		Iterator<Integer> setiter = parent.pixellist.iterator();
		parent.overlay.clear();
		while (setiter.hasNext()) {

			percent++;
			int label = setiter.next();

			if (label > 0) {

				
				
				// Input the integer image of bud with the label and output the binary border
				// for that label
				Budregionobject PairCurrentViewBit = BudCurrentLabelBinaryImage(
						parent.CurrentViewInt, label);

				// For each bud get the list of points
				List<RealLocalizable> truths = DisplayListOverlay.GetCoordinatesBit(PairCurrentViewBit.Boundaryimage);

				// Get the center point of each bud
				RealLocalizable currentpoint = budDetector.Listordering.getMeanCord(truths);

					
					if (parent.jpb != null)
						utility.BudProgressBar.SetProgressBar(parent.jpb,
								100 * (percent) / (parent.thirdDimensionSize + parent.pixellist.size()),
								"Computing Skeletons = " + t + "/" + parent.thirdDimensionSize + " Total Buddies = "
										+ (parent.pixellist.size() ));
					Allrois = Common(PairCurrentViewBit, truths,  currentpoint, uniqueID, label);


					}

      		}
	
			parent.BudOvalRois.put(uniqueID, Allrois);

	
	}
	
	

	
	

	public  ArrayList<Pair<Color,OvalRoi>> Common(Budregionobject  PairCurrentViewBit,
			List<RealLocalizable> truths, RealLocalizable centerpoint, String uniqueID,
			int label) {

		// Skeletonize Bud
		OpService ops = parent.ij.op();
	
		List<RealLocalizable> skeletonEndPoints = new ArrayList<RealLocalizable>();
		if(parent.BudOvalRois.get(Integer.toString(parent.thirdDimension))==null) {
			
		SkeletonCreator<BitType> skelmake = new SkeletonCreator<BitType>((RandomAccessibleInterval<BitType>) ops.morphology().dilate(PairCurrentViewBit.Interiorimage, new DiamondShape(4)), ops);
		skelmake.setClosingRadius(0);
		skelmake.run();
		ArrayList<RandomAccessibleInterval<BitType>> Allskeletons = skelmake.getSkeletons();

		skeletonEndPoints = AnalyzeSkeleton(Allskeletons,truths, ops);
		
		}
		
		else {
			
		ArrayList<Pair<Color, OvalRoi>> rois = 	parent.BudOvalRois.get(Integer.toString(parent.thirdDimension));
		
		for (Pair<Color, OvalRoi> currentroi: rois) {
			
			if(currentroi.getA() == parent.BudColor) {
			RealPoint point = new RealPoint(currentroi.getB().getContourCentroid());
			
			skeletonEndPoints.add(point);
			
		}
		}
			
		}
		
		
		
		for (RealLocalizable budpoints : skeletonEndPoints) {

			Budpointobject Budpoint = new Budpointobject(centerpoint, truths, skeletonEndPoints,
					truths.size() * parent.calibrationX, label,
					new double[] { budpoints.getDoublePosition(0), budpoints.getDoublePosition(1) },
					parent.thirdDimension, 0);

			Budpointlist.add(Budpoint);

		}
		Budobject Curreentbud = new Budobject(centerpoint, truths, skeletonEndPoints, t, label,
				truths.size() * parent.calibrationX);
		Budlist.add(Curreentbud);
		if (parent.SegYelloworiginalimg != null) {
	          celllist = GetNearest.getAllInteriorCells(parent, parent.CurrentViewInt, parent.CurrentViewYellowInt);

	          // check over this point later
		//ArrayList<Cellobject> budcelllist = GetNearest.getLabelInteriorCells(parent, CurrentViewInt, celllist, Curreentbud, label);
		for(Cellobject currentbudcell:celllist) {
			
			Localizable centercell = currentbudcell.Location;
			// For each cell get nearest bud growth point
			RealLocalizable closestdynamicskel = GetNearest.getNearestskelPoint(skeletonEndPoints, centercell);
			// Get distance between the center of cell and bud growth point
			double closestGrowthPoint = Distance.DistanceSqrt(centercell, closestdynamicskel);
			// For each cell get nearest bud point
			RealLocalizable closestskel = GetNearest.getNearestskelPoint(truths, centercell);
			// and the distance
			double closestBudPoint = Distance.DistanceSqrt(centercell, closestskel);
			
			// Make the bud n cell object, each cell has all information about the bud n itself 
			BCellobject budncell = new BCellobject(Curreentbud, Budpointlist, currentbudcell, closestGrowthPoint, closestBudPoint, t);
            parent.budcells.add(budncell, t);  
		}
		
		
		}
		
		


		 ArrayList<Pair<Color,OvalRoi>> Totalrois  = DisplayListOverlay.ArrowDisplay(parent,
				new ValuePair<RealLocalizable, List<RealLocalizable>>(centerpoint, truths), skeletonEndPoints,
				uniqueID);
		
		
		   return Totalrois;

	}



	public static ArrayList<RealLocalizable> AnalyzeSkeleton(ArrayList<RandomAccessibleInterval<BitType>> Allskeletons, List<RealLocalizable> truths,
			OpService ops) {

		ArrayList<RealLocalizable> endPoints = new ArrayList<RealLocalizable>();

		for (RandomAccessibleInterval<BitType> skeleton : Allskeletons) {

			SkeletonAnalyzer<BitType> skelanalyze = new SkeletonAnalyzer<BitType>(skeleton, ops);
			RandomAccessibleInterval<BitType> Ends = skelanalyze.getEndpoints();

			Cursor<BitType> skelcursor = Views.iterable(Ends).localizingCursor();

			while (skelcursor.hasNext()) {

				skelcursor.next();

				RealPoint addPoint = new RealPoint(skelcursor);
				if (skelcursor.get().getInteger() > 0) {
					
					//RealLocalizable nearest = 	GetNearest.getNearestskelPoint(truths, addPoint);
					
					endPoints.add(addPoint);

				}

			}
			
			
			
			

		}
		return endPoints;

	}

	public static RandomAccessibleInterval<BitType> GradientmagnitudeImage(RandomAccessibleInterval<BitType> inputimg) {

		RandomAccessibleInterval<BitType> gradientimg = new ArrayImgFactory<BitType>().create(inputimg, new BitType());
		Cursor<BitType> cursor = Views.iterable(gradientimg).localizingCursor();
		RandomAccessible<BitType> view = Views.extendBorder(inputimg);
		RandomAccess<BitType> randomAccess = view.randomAccess();

		// iterate over all pixels
		while (cursor.hasNext()) {
			// move the cursor to the next pixel
			cursor.fwd();

			// compute gradient and its direction in each dimension
			double gradient = 0;

			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				// set the randomaccess to the location of the cursor
				randomAccess.setPosition(cursor);

				// move one pixel back in dimension d
				randomAccess.bck(d);

				// get the value
				double Back = randomAccess.get().getRealDouble();

				// move twice forward in dimension d, i.e.
				// one pixel above the location of the cursor
				randomAccess.fwd(d);
				randomAccess.fwd(d);

				// get the value
				double Fwd = randomAccess.get().getRealDouble();

				gradient += ((Fwd - Back) * (Fwd - Back)) / 4;

			}

			cursor.get().setReal(Math.sqrt(gradient));

		}

		return gradientimg;
	}

	public static Budregionobject BudCurrentLabelBinaryImage(
			RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
		int n = Intimg.numDimensions();
		long[] position = new long[n];
		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

		RandomAccessibleInterval<BitType> outimg = new ArrayImgFactory<BitType>().create(Intimg, new BitType());
		RandomAccess<BitType> imageRA = outimg.randomAccess();

		// Go through the whole image and add every pixel, that belongs to
		// the currently processed label
		long[] minVal = { Intimg.max(0), Intimg.max(1) };
		long[] maxVal = { Intimg.min(0), Intimg.min(1) };

		while (intCursor.hasNext()) {
			intCursor.fwd();
			imageRA.setPosition(intCursor);
			int i = intCursor.get().get();
			if (i == currentLabel) {

				intCursor.localize(position);
				for (int d = 0; d < n; ++d) {
					if (position[d] < minVal[d]) {
						minVal[d] = position[d];
					}
					if (position[d] > maxVal[d]) {
						maxVal[d] = position[d];
					}

				}

				imageRA.get().setOne();
			} else
				imageRA.get().setZero();

		}
		
		double size = Math.sqrt(Distance.DistanceSq(minVal, maxVal));

	
		Point min = new Point(minVal.length);
		// Gradient image gives us the bondary points
		RandomAccessibleInterval<BitType> gradimg = GradientmagnitudeImage(outimg);
		
		Budregionobject region = new Budregionobject(gradimg, outimg, min,  size);
		return region;

	}
	
	public static Budregionobject BudCurrentLabelBinaryImage3D(
			RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
		int n = Intimg.numDimensions();
		long[] position = new long[n];
		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

		RandomAccessibleInterval<BitType> outimg = new ArrayImgFactory<BitType>().create(Intimg, new BitType());
		RandomAccess<BitType> imageRA = outimg.randomAccess();

		// Go through the whole image and add every pixel, that belongs to
		// the currently processed label
		long[] minVal = { Intimg.max(0), Intimg.max(1), Intimg.max(2) };
		long[] maxVal = { Intimg.min(0), Intimg.min(1),Intimg.min(2) };

		while (intCursor.hasNext()) {
			intCursor.fwd();
			imageRA.setPosition(intCursor);
			int i = intCursor.get().get();
			if (i == currentLabel) {

				intCursor.localize(position);
				for (int d = 0; d < n; ++d) {
					if (position[d] < minVal[d]) {
						minVal[d] = position[d];
					}
					if (position[d] > maxVal[d]) {
						maxVal[d] = position[d];
					}

				}

				imageRA.get().setOne();
			} else
				imageRA.get().setZero();

		}
		
		double size = Math.sqrt(Distance.DistanceSq(minVal, maxVal));

	
		Point min = new Point(minVal.length);
		// Gradient image gives us the bondary points
		RandomAccessibleInterval<BitType> gradimg = GradientmagnitudeImage(outimg);
		
		Budregionobject region = new Budregionobject(gradimg, outimg, min,  size);
		return region;

	}
	public static Budregionobject YellowCurrentLabelBinaryImage(
			RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
		int n = Intimg.numDimensions();
		long[] position = new long[n];
		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

		RandomAccessibleInterval<BitType> outimg = new ArrayImgFactory<BitType>().create(Intimg, new BitType());
		RandomAccess<BitType> imageRA = outimg.randomAccess();

		// Go through the whole image and add every pixel, that belongs to
		// the currently processed label
		long[] minVal = { Intimg.max(0), Intimg.max(1) };
		long[] maxVal = { Intimg.min(0), Intimg.min(1) };

		while (intCursor.hasNext()) {
			intCursor.fwd();
			imageRA.setPosition(intCursor);
			int i = intCursor.get().get();
			if (i == currentLabel) {

				intCursor.localize(position);
				for (int d = 0; d < n; ++d) {
					if (position[d] < minVal[d]) {
						minVal[d] = position[d];
					}
					if (position[d] > maxVal[d]) {
						maxVal[d] = position[d];
					}

				}

				imageRA.get().setOne();
			} else
				imageRA.get().setZero();

		}
		
		double size = Math.sqrt(Distance.DistanceSq(minVal, maxVal));
		for (int d = 0; d < n; ++d) {
			
			minVal[d] = minVal[d] - 10;
			maxVal[d] = maxVal[d] + 10;
			
		}
	
		Point min = new Point(minVal);
		outimg = Views.offsetInterval(outimg, minVal, maxVal);
		// Gradient image gives us the bondary points
		RandomAccessibleInterval<BitType> gradimg = GradientmagnitudeImage(outimg);
		
		Budregionobject region = new Budregionobject(gradimg, outimg, min,  size);
		return region;

	}


}
