package pluginTools;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import budDetector.Budobject;
import budDetector.Budpointobject;
import budDetector.Distance;
import ij.ImagePlus;
import listeners.BudSelectBudsListener;
import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import skeleton.*;
import displayBud.DisplayListOverlay;

public class TrackEachBud {
	
	
	
	final InteractiveBud parent;
	final RandomAccessibleInterval<IntType> CurrentViewInt;
	final int t;
	final int maxlabel;
	int percent;
	
	
	public TrackEachBud(final InteractiveBud parent, final RandomAccessibleInterval<IntType> CurrentViewInt, final int t, final int maxlabel, final int percent ) {
		
		
		this.parent = parent;
		this.CurrentViewInt = CurrentViewInt;
		this.t = t;
		this.maxlabel = maxlabel;
		this.percent = percent;
		
		
	}
	
	public void displayBuds() {
		
		
		int nThreads = Runtime.getRuntime().availableProcessors();
		
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		Iterator<Integer> setiter = parent.pixellist.iterator();
		
		
		
		while (setiter.hasNext()) {
			
			
			int label = setiter.next();
			
			
			if (label > 0) {
				
				String uniqueID = Integer.toString(parent.thirdDimension) + Integer.toString(label);
				
			// Input the integer image of bud with the label and output the binary border for that label
			Pair<RandomAccessibleInterval<BitType>, RandomAccessibleInterval<BitType> > PairCurrentViewBit = CurrentLabelBinaryImage(CurrentViewInt, label);
			
			// For each bud get the list of points
			List<RealLocalizable> truths =  DisplayListOverlay.GetCoordinatesBit(PairCurrentViewBit.getA());
			
			// Get the center point of each bud
			RealLocalizable centerpoint = budDetector.Listordering.getMeanCord(truths);
			
			parent.AllBudcenter.add(centerpoint);
			
			parent.Refcord = centerpoint;
			

			parent.AllRefcords.put(uniqueID, parent.Refcord);
			
			}
			
		}
		
		Iterator<Integer> setitersecond = parent.pixellist.iterator();
		while (setitersecond.hasNext()) {
			
			percent++;
			
			int label = setitersecond.next();
			
			
			if (label > 0) {
			
				String uniqueID = Integer.toString(parent.thirdDimension) + Integer.toString(label);
				
				// Input the integer image of bud with the label and output the binary border for that label
				Pair<RandomAccessibleInterval<BitType>, RandomAccessibleInterval<BitType> > PairCurrentViewBit = CurrentLabelBinaryImage(CurrentViewInt, label);
				
				// For each bud get the list of points
				List<RealLocalizable> truths =  DisplayListOverlay.GetCoordinatesBit(PairCurrentViewBit.getA());
				
				// Get the center point of each bud
				RealLocalizable centerpoint = budDetector.Listordering.getMeanCord(truths);
			if(parent.thirdDimension > 1 && parent.ChosenBudcenter.size() > 0) {
			
				parent.ChosenBudcenter.clear();
				BudSelectBudsListener.removebuds(parent, centerpoint);
			
			
			}
			
			
			parent.overlay.clear();
			if(Contains(parent.ChosenBudcenter, centerpoint) && parent.thirdDimension!=1) {
				if(parent.jpb!=null )
					utility.BudProgressBar.SetProgressBar(parent.jpb, 100 * percent / (parent.thirdDimensionSize +  parent.pixellist.size()-1),
							"No buddies here! What are you doing?");
			
				continue;
				
				
			}
					
				else {
			
				if(parent.jpb!=null )
					utility.BudProgressBar.SetProgressBar(parent.jpb, 100 * percent / (parent.thirdDimensionSize +  parent.pixellist.size()-1),
							"Computing Skeletons = " + t + "/" + parent.thirdDimensionSize + " Total Buddies = " 
									+ (parent.pixellist.size()-1));
			// Skeletonize Bud
			OpService ops = parent.ij.op();
			
			SkeletonCreator<BitType> skelmake = new SkeletonCreator<BitType>(PairCurrentViewBit.getB(), ops);
			skelmake.setClosingRadius(1);
			skelmake.run();
			ArrayList<RandomAccessibleInterval<BitType>> Allskeletons = skelmake.getSkeletons();
			
			
			
			List<RealLocalizable> skeletonEndPoints = AnalyzeSkeleton( Allskeletons , ops);
			
			ArrayList<Budpointobject> Budpointlist = new ArrayList<Budpointobject>();
			
			for(RealLocalizable budpoints:skeletonEndPoints) {
				
				
				Budpointobject Budpoint = new Budpointobject(centerpoint, truths, skeletonEndPoints, truths.size() * parent.calibration, label, new double[] {budpoints.getDoublePosition(0),  budpoints.getDoublePosition(1)}, parent.thirdDimension, 0);
				
				Budpointlist.add(Budpoint);
				
			}
			
			
			
			parent.AllBudpoints.put(uniqueID, Budpointlist);
			
			DisplayListOverlay.ArrowDisplay(parent, new ValuePair<RealLocalizable, List<RealLocalizable>>(centerpoint, truths),skeletonEndPoints, uniqueID);
			
			
			//Allow the user to choose or deselect buds
			
			if(parent.thirdDimension == 1) {
			BudSelectBudsListener.markbuds(parent);
			
			
			}
			
			
				}
			
			}
		}
		
	}
	
	private static boolean Contains(ArrayList<RealLocalizable> Buds, RealLocalizable currentbud) {
		
		boolean contains = false;
		
		for(RealLocalizable bud: Buds) {
			
			
			double dist = Distance.DistanceSqrt(bud, currentbud);
			
			if(dist<=1)
				contains = true;
			
		}
		
		
		return contains;
		
	}
	
	
	
	public static ArrayList<RealLocalizable>  AnalyzeSkeleton(ArrayList<RandomAccessibleInterval<BitType>> Allskeletons, OpService ops ) {
	
		 ArrayList<RealLocalizable> endPoints = new ArrayList<RealLocalizable>();
		
		for(RandomAccessibleInterval<BitType> skeleton : Allskeletons) {
		
			
			SkeletonAnalyzer<BitType> skelanalyze = new SkeletonAnalyzer<BitType>(skeleton, ops);
		    RandomAccessibleInterval<BitType> Ends = skelanalyze.getEndpoints();
		    
		    Cursor<BitType> skelcursor = Views.iterable(Ends).localizingCursor();
		    
		    while(skelcursor.hasNext()) {
		    	
		    	
		    	skelcursor.next();
		    	
		    	if(skelcursor.get().getInteger() > 0) {
		    		endPoints.add(new RealPoint(skelcursor.getDoublePosition(0) ,skelcursor.getDoublePosition(1) ));
		    		
		    	}
		    	
		    	
		    }
				
		}
		return endPoints;
		
	}
	
	public static  RandomAccessibleInterval<BitType> GradientmagnitudeImage(
			RandomAccessibleInterval<BitType> inputimg) {
		
		
		RandomAccessibleInterval<BitType> gradientimg = new ArrayImgFactory<BitType>().create(inputimg,
				new BitType());
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
	
	public static Pair<RandomAccessibleInterval<BitType>,RandomAccessibleInterval<BitType>>  CurrentLabelBinaryImage(RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
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
		RandomAccessibleInterval<BitType> outsmooth = new ArrayImgFactory<BitType>().create(outimg, new BitType());
		try {

			net.imglib2.algorithm.gauss3.Gauss3.gauss(10, Views.extendBorder(outimg), outsmooth);

		} catch (IncompatibleTypeException es) {

			es.printStackTrace();
		}
		
		RandomAccessibleInterval<BitType> gradimg = GradientmagnitudeImage(outsmooth);


		return new ValuePair<RandomAccessibleInterval<BitType>,RandomAccessibleInterval<BitType>>(gradimg, outsmooth);

	}
	

}
