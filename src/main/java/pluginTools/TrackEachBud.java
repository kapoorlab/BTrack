package pluginTools;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import budDetector.BCellobject;
import budDetector.Budobject;
import budDetector.Budpointobject;
import budDetector.Budregionobject;
import budDetector.Cellobject;
import budDetector.Distance;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import kalmanGUI.CovistoKalmanPanel;
import listeners.BudSelectBudsListener;
import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Localizable;
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
import utility.GetNearest;
import displayBud.DisplayListOverlay;
import fiji.plugin.trackmate.BCellobjectCollection;

public class TrackEachBud {

	final InteractiveBud parent;
	final RandomAccessibleInterval<IntType> CurrentViewInt;
	final RandomAccessibleInterval<IntType> CurrentViewYellowInt;
	final int t;
	final int maxlabel;
	int percent;
	final ArrayList<Budobject> Budlist;
	final ArrayList<Budpointobject> Budpointlist;
	final ArrayList<BCellobject> Budcelllist;
    ArrayList<Cellobject> celllist = new ArrayList<Cellobject>();
    
	public TrackEachBud(final InteractiveBud parent, final RandomAccessibleInterval<IntType> CurrentViewInt,
			ArrayList<Budobject> Budlist, ArrayList<Budpointobject> Budpointlist, final int t, final int maxlabel,
			final int percent) {

		this.parent = parent;
		this.CurrentViewInt = CurrentViewInt;
		this.CurrentViewYellowInt = null;
		this.t = t;
		this.maxlabel = maxlabel;
		this.percent = percent;
		this.Budlist = Budlist;
		this.Budpointlist = Budpointlist;
        this.Budcelllist = null;
	}

	public TrackEachBud(final InteractiveBud parent, final RandomAccessibleInterval<IntType> CurrentViewInt,
			final int t, final int maxlabel, final int percent) {

		this.parent = parent;
		this.CurrentViewInt = CurrentViewInt;
		this.CurrentViewYellowInt = null;
		this.t = t;
		this.maxlabel = maxlabel;
		this.percent = percent;
		this.Budlist = null;
		this.Budpointlist = null;
        this.Budcelllist = null;

	}
	
	public TrackEachBud(final InteractiveBud parent, final RandomAccessibleInterval<IntType> CurrentViewInt, final RandomAccessibleInterval<IntType> CurrentViewYellowInt,
			final int t, final int maxlabel, final int percent) {

		this.parent = parent;
		this.CurrentViewInt = CurrentViewInt;
		this.CurrentViewYellowInt = CurrentViewYellowInt;
		this.t = t;
		this.maxlabel = maxlabel;
		this.percent = percent;
		this.Budlist = null;
		this.Budpointlist = null;
        this.Budcelllist = null;

	}
	
	
	public TrackEachBud(final InteractiveBud parent, final RandomAccessibleInterval<IntType> CurrentViewInt, final RandomAccessibleInterval<IntType> CurrentViewYellowInt,
			ArrayList<Budobject> Budlist, ArrayList<Budpointobject> Budpointlist,ArrayList<BCellobject> Budcelllist, final int t, final int maxlabel,
			final int percent) {

		this.parent = parent;
		this.CurrentViewInt = CurrentViewInt;
		this.CurrentViewYellowInt = CurrentViewYellowInt;
		this.t = t;
		this.maxlabel = maxlabel;
		this.percent = percent;
		this.Budlist = Budlist;
		this.Budpointlist = Budpointlist;
		this.Budcelllist = Budcelllist;

	}

	public ArrayList<Budobject> returnBudlist() {

		return Budlist;
	}

	public ArrayList<Budpointobject> returnBudpointlist() {

		return Budpointlist;
	}
	
	public ArrayList<BCellobject> returnBudcelllist() {

		return Budcelllist;
	}


	

	public void displayBuds() {

		int sidecutpixel = 10;
		int nThreads = Runtime.getRuntime().availableProcessors();

		String uniqueID = Integer.toString(parent.thirdDimension);

		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		Iterator<Integer> setiter = parent.pixellist.iterator();
		Set<Integer> copyList = parent.pixellist;
		parent.overlay.clear();

		
		if (parent.SegYelloworiginalimg != null) {
		
          celllist = GetNearest.getAllInteriorCells(parent, CurrentViewInt, CurrentViewYellowInt);

		}
		while (setiter.hasNext()) {

			int label = setiter.next();

			if (label > 0) {

				// Input the integer image of bud with the label and output the binary border
				// for that label, first one is the border n second binary is the filled bud
				Budregionobject PairCurrentViewBit = CurrentLabelBinaryImage(
						CurrentViewInt, label);

				// For each bud get the list of points
				List<RealLocalizable> truths = DisplayListOverlay.GetCoordinatesBit(PairCurrentViewBit.Boundaryimage);

				// Get the center point of each bud
				RealLocalizable centerpoint = budDetector.Listordering.getMeanCord(truths);

				int ndims = centerpoint.numDimensions();
				for (int d = 0; d < ndims; ++d)
					if (centerpoint.getDoublePosition(d) > sidecutpixel
							&& centerpoint.getDoublePosition(d) < CurrentViewInt.dimension(d) - sidecutpixel) {
						parent.AllBudcenter.add(centerpoint);

						parent.Refcord = centerpoint;

						parent.AllRefcords.put(uniqueID, parent.Refcord);

					}
			}

		}
		Iterator<Integer> setitersecond = copyList.iterator();

		HashMap<Integer, Boolean> LabelCovered = new HashMap<Integer, Boolean>();
		for (Integer Track : copyList) {

			LabelCovered.put(Track, false);

		}
		while (setitersecond.hasNext()) {

			percent++;
			int label = setitersecond.next();

			if (label > 0) {

				// Input the integer image of bud with the label and output the binary border
				// for that label
				
				Budregionobject PairCurrentViewBit = CurrentLabelBinaryImage(
						CurrentViewInt, label);

				// For each bud get the list of points
				List<RealLocalizable> truths = DisplayListOverlay.GetCoordinatesBit(PairCurrentViewBit.Boundaryimage);

				// Get the center point of each bud
				RealLocalizable centerpoint = budDetector.Listordering.getMeanCord(truths);

				
				
				if (CovistoKalmanPanel.Skeletontime.isEnabled()) {

					if(label == 1)
						 parent.ChosenBudcenter.add(centerpoint);
					if (parent.jpb != null)
						utility.BudProgressBar.SetProgressBar(parent.jpb,
								100 * (percent) / (parent.thirdDimensionSize + parent.pixellist.size()),
								"Computing Skeletons = " + t + "/" + parent.thirdDimensionSize + " Total Buddies = "
										+ (parent.pixellist.size()));
					Common(PairCurrentViewBit, truths, centerpoint, uniqueID, label);
				}

				int ndims = centerpoint.numDimensions();
				for (int d = 0; d < ndims; ++d)
					if (centerpoint.getDoublePosition(d) > sidecutpixel
							&& centerpoint.getDoublePosition(d) < CurrentViewInt.dimension(d) - sidecutpixel) {

						if (!CovistoKalmanPanel.Skeletontime.isEnabled()) {

							BudSelectBudsListener.choosebuds(parent, centerpoint);

						}

						if (parent.ChosenBudcenter.size() == 0 && parent.thirdDimension > 1
								&& !CovistoKalmanPanel.Skeletontime.isEnabled()) {

							if (parent.jpb != null)
								utility.BudProgressBar.SetProgressBar(parent.jpb,
										100 * (percent) / (parent.thirdDimensionSize + parent.pixellist.size()),
										"No buddies here! What are you doing?");

						}

						if (parent.ChosenBudcenter.size() != 0)
							for (RealLocalizable currentpoint : parent.ChosenBudcenter) {

								RandomAccess<IntType> intranac = CurrentViewInt.randomAccess();
								intranac.setPosition(new long[] { (long) currentpoint.getFloatPosition(0),
										(long) currentpoint.getFloatPosition(1) });
								int Labelchosen = intranac.get().get();
								if (LabelCovered.get(Labelchosen) != null)
									if (!LabelCovered.get(Labelchosen))
										if (label == Labelchosen) {

											LabelCovered.put(label, true);
											PairCurrentViewBit = CurrentLabelBinaryImage(CurrentViewInt, label);
											// For each bud get the list of points
											truths = DisplayListOverlay.GetCoordinatesBit(PairCurrentViewBit.Boundaryimage);
											centerpoint = budDetector.Listordering.getMeanCord(truths);

											if (parent.jpb != null)
												utility.BudProgressBar.SetProgressBar(parent.jpb,
														100 * (percent)
																/ (parent.thirdDimensionSize + parent.pixellist.size()),
														"Computing Skeletons = " + t + "/" + parent.thirdDimensionSize
																+ " Total Buddies = " + (parent.pixellist.size()));

											Common(PairCurrentViewBit, truths, centerpoint, uniqueID, label);

										}

							}

					}

			}

		}

	}

	public void Common(Budregionobject PairCurrentViewBit,
			List<RealLocalizable> truths, RealLocalizable centerpoint, String uniqueID, int label) {

		// Skeletonize Bud
		OpService ops = parent.ij.op();

		SkeletonCreator<BitType> skelmake = new SkeletonCreator<BitType>(PairCurrentViewBit.Interiorimage, ops);
		skelmake.setClosingRadius(2);
		skelmake.run();
		ArrayList<RandomAccessibleInterval<BitType>> Allskeletons = skelmake.getSkeletons();
		List<RealLocalizable> skeletonEndPoints = AnalyzeSkeleton(Allskeletons, ops);

		if (!CovistoKalmanPanel.Skeletontime.isEnabled()) {
			for (RealLocalizable budpoints : skeletonEndPoints) {

				Budpointobject Budpoint = new Budpointobject(centerpoint, truths, skeletonEndPoints,
						truths.size() * parent.calibration, label,
						new double[] { budpoints.getDoublePosition(0), budpoints.getDoublePosition(1) },
						parent.thirdDimension, 0);

				Budpointlist.add(Budpoint);

			}
			Budobject Currentbud = new Budobject(centerpoint, truths, skeletonEndPoints, t, label,
					truths.size() * parent.calibration);
			Budlist.add(Currentbud);
			ArrayList<Cellobject> budcelllist = GetNearest.getLabelInteriorCells(parent, CurrentViewInt, celllist, Currentbud);
			for(Cellobject currentbudcell:budcelllist) {
				
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
				BCellobject budncell = new BCellobject(Currentbud, Budpointlist, currentbudcell, closestGrowthPoint, closestBudPoint, t);
                Budcelllist.add(budncell); 
                parent.budcells.add(budncell, t);  
			}
			

		}

		DisplayListOverlay.ArrowDisplay(parent,
				new ValuePair<RealLocalizable, List<RealLocalizable>>(centerpoint, truths), skeletonEndPoints,
				uniqueID);

		// Allow the user to choose or deselect buds
		if (parent.thirdDimension == 1)
			BudSelectBudsListener.markbuds(parent);

	}

	private static boolean Contains(ArrayList<RealLocalizable> Buds, RealLocalizable currentbud) {

		boolean contains = false;

		for (RealLocalizable bud : Buds) {

			double dist = Distance.DistanceSqrt(bud, currentbud);

			if (dist <= 1)
				contains = true;

		}

		return contains;

	}

	public static ArrayList<RealLocalizable> AnalyzeSkeleton(ArrayList<RandomAccessibleInterval<BitType>> Allskeletons,
			OpService ops) {

		ArrayList<RealLocalizable> endPoints = new ArrayList<RealLocalizable>();

		for (RandomAccessibleInterval<BitType> skeleton : Allskeletons) {

			SkeletonAnalyzer<BitType> skelanalyze = new SkeletonAnalyzer<BitType>(skeleton, ops);
			RandomAccessibleInterval<BitType> Ends = skelanalyze.getEndpoints();

			Cursor<BitType> skelcursor = Views.iterable(Ends).localizingCursor();

			while (skelcursor.hasNext()) {

				skelcursor.next();

				if (skelcursor.get().getInteger() > 0) {
					endPoints.add(new RealPoint(skelcursor.getDoublePosition(0), skelcursor.getDoublePosition(1)));

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

	public static Budregionobject CurrentLabelBinaryImage(
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
		
		// Smooth the interior image a bit to give the interior cell image
		RandomAccessibleInterval<BitType> outsmooth = new ArrayImgFactory<BitType>().create(outimg, new BitType());
		try {

			net.imglib2.algorithm.gauss3.Gauss3.gauss(1, Views.extendBorder(outimg), outsmooth);

		} catch (IncompatibleTypeException es) {

			es.printStackTrace();
		}

		
		// Gradient image gives us the bondary points
		RandomAccessibleInterval<BitType> gradimg = GradientmagnitudeImage(outsmooth);
		
		
		Budregionobject region = new Budregionobject(gradimg, outsmooth, size);
		return region;

	}

}
