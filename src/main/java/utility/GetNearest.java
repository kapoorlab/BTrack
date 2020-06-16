package utility;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import budDetector.Budobject;
import budDetector.Budregionobject;
import budDetector.Cellobject;
import budDetector.Distance;
import displayBud.DisplayListOverlay;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import net.imglib2.Cursor;
import net.imglib2.KDTree;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;
import pluginTools.InteractiveBud;
import pluginTools.TrackEachBud;

public class GetNearest {
	public static ArrayList<Cellobject> getLabelInteriorCells(InteractiveBud parent,final RandomAccessibleInterval<IntType> CurrentViewInt,
			ArrayList<Cellobject> InteriorCells,Budobject Currentbud, int currentlabel) {
		
		ArrayList<Cellobject> AllLabelcells = new ArrayList<Cellobject>();
		
		
		
		
		RandomAccess<IntType> intranac = CurrentViewInt.randomAccess();
		
		for(Cellobject currentcell:InteriorCells) {
			
			Localizable cellcenter = currentcell.Location;
			intranac.setPosition(cellcenter);
			int label = intranac.get().get();
			
			if(label == currentlabel) {
				
				
				AllLabelcells.add(currentcell);
			}
			
			
		}
		
		
		return AllLabelcells;
		
	}
	
	
	public static boolean isInterior(List<RealLocalizable> skelpoints, RealLocalizable centerpoint, RealLocalizable targetpoint) {
		
		
		double distbudcenter = Distance.DistanceSqrt(centerpoint, targetpoint);
		
		RealLocalizable skelpoint = getNearestskelPoint(skelpoints, targetpoint);
		
		double distcenterskel = Distance.DistanceSqrt(centerpoint, skelpoint);
		
        boolean isInterior = (distbudcenter < distcenterskel)?true:false; 		
		
		return isInterior;
	}
public static RealLocalizable getNearestskelPoint(final List<RealLocalizable> skelPoints, RealLocalizable ClickedPoint) {
		
		
		RealLocalizable KDtreeroi = null;
		
		
        List<RealLocalizable> Allrois = skelPoints;
		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<RealLocalizable>> targetNodes = new ArrayList<FlagNode<RealLocalizable>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			RealLocalizable r = Allrois.get(index);

			targetCoords.add(new RealPoint(r));

			targetNodes.add(new FlagNode<RealLocalizable>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<RealLocalizable>> Tree = new KDTree<FlagNode<RealLocalizable>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<RealLocalizable> Search = new NNFlagsearchKDtree<RealLocalizable>(Tree);

			final RealLocalizable source = ClickedPoint;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
				
			final FlagNode<RealLocalizable> targetNode = Search.getSampler().get();

			KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
		
	}
	public static ArrayList<Cellobject> getAllInteriorCells(InteractiveBud parent, final RandomAccessibleInterval<IntType> CurrentViewInt, final RandomAccessibleInterval<IntType> CurrentViewYellowInt) {
		
		Cursor<IntType> intcursor = Views.iterable(CurrentViewYellowInt).localizingCursor();
		ArrayList<Cellobject> Allcells = new ArrayList<Cellobject>();
		HashMap<Integer, Boolean> InsideCellList = new HashMap<Integer, Boolean>();
		RandomAccess<IntType> budintran = CurrentViewInt.randomAccess();
		// Select all yellow cells
		
		while (intcursor.hasNext()) {

			intcursor.fwd();
			budintran.setPosition(intcursor);
			int labelyellow = intcursor.get().get();
			int label = budintran.get().get();
			InsideCellList.put(labelyellow, false);
			if(label > 0)
				InsideCellList.put(labelyellow, true);
			
			
			
		}
	
					
					

					for (Integer labelyellow : InsideCellList.keySet()) {
						   Boolean isInterior = InsideCellList.get(labelyellow);
						    if(isInterior) {
							Budregionobject PairCurrentViewBit = TrackEachBud
									.BudCurrentLabelBinaryImage(CurrentViewYellowInt, labelyellow);

							// For each bud get the list of points
							List<RealLocalizable> bordercelltruths = DisplayListOverlay.GetCoordinatesBit(PairCurrentViewBit.Boundaryimage);
							List<RealLocalizable> interiorcelltruths = DisplayListOverlay.GetCoordinatesBit(PairCurrentViewBit.Interiorimage);
							double cellArea = interiorcelltruths.size() * parent.calibration;
							Localizable cellcenterpoint = budDetector.Listordering.getIntMeanCord(bordercelltruths);
							
							
							Cellobject insidecells = new Cellobject(interiorcelltruths, bordercelltruths, cellcenterpoint, cellArea,PairCurrentViewBit.size );
							Allcells.add(insidecells);
							for (RealLocalizable insidetruth : bordercelltruths) {

								Integer xPts = (int) insidetruth.getFloatPosition(0);
								Integer yPts = (int) insidetruth.getFloatPosition(1);
								OvalRoi points = new OvalRoi(xPts, yPts, 2, 2);
								points.setStrokeColor(Color.RED);
								points.setStrokeWidth(2);
								parent.overlay.add(points);
							}
						    }
					}
					
					parent.imp.updateAndDraw();
		return Allcells;
		
	}
	
	public static OvalRoi getNearestRois(ArrayList<OvalRoi> Allrois, double[] Clickedpoint) {

		OvalRoi KDtreeroi = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<OvalRoi>> targetNodes = new ArrayList<FlagNode<OvalRoi>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			Roi r = Allrois.get(index);
			Rectangle rect = r.getBounds();

			targetCoords.add(new RealPoint(rect.x + rect.width / 2.0, rect.y + rect.height / 2.0));

			targetNodes.add(new FlagNode<OvalRoi>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<OvalRoi>> Tree = new KDTree<FlagNode<OvalRoi>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<OvalRoi> Search = new NNFlagsearchKDtree<OvalRoi>(Tree);

			final double[] source = Clickedpoint;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
			final FlagNode<OvalRoi> targetNode = Search.getSampler().get();

			KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
	}
	public static RealLocalizable getNearestPoint(final InteractiveBud parent, RealLocalizable ClickedPoint) {
		
		
		RealLocalizable KDtreeroi = null;
		
		
        ArrayList<RealLocalizable> Allrois = parent.AllBudcenter;
		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<RealLocalizable>> targetNodes = new ArrayList<FlagNode<RealLocalizable>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			RealLocalizable r = Allrois.get(index);

			targetCoords.add(new RealPoint(r));

			targetNodes.add(new FlagNode<RealLocalizable>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<RealLocalizable>> Tree = new KDTree<FlagNode<RealLocalizable>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<RealLocalizable> Search = new NNFlagsearchKDtree<RealLocalizable>(Tree);

			final RealLocalizable source = ClickedPoint;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
			final FlagNode<RealLocalizable> targetNode = Search.getSampler().get();

			KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
		
	}
	public static RealLocalizable getNearestBudcenter(final InteractiveBud parent, RealLocalizable ClickedPoint) {
		
		
		RealLocalizable KDtreeroi = null;
		
		
        ArrayList<RealLocalizable> Allrois = parent.ChosenBudcenter;
		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<RealLocalizable>> targetNodes = new ArrayList<FlagNode<RealLocalizable>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			RealLocalizable r = Allrois.get(index);

			targetCoords.add(new RealPoint(r));

			targetNodes.add(new FlagNode<RealLocalizable>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<RealLocalizable>> Tree = new KDTree<FlagNode<RealLocalizable>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<RealLocalizable> Search = new NNFlagsearchKDtree<RealLocalizable>(Tree);

			final RealLocalizable source = ClickedPoint;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
			final FlagNode<RealLocalizable> targetNode = Search.getSampler().get();

			KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
		
	}

}
