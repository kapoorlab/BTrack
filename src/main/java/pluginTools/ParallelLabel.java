package pluginTools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import budDetector.BCellobject;
import budDetector.Budobject;
import budDetector.Budpointobject;
import budDetector.Budregionobject;
import budDetector.Cellobject;
import budDetector.Distance;
import net.imagej.ops.OpService;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import utility.GetNearest;
import pluginTools.TrackEach3DCell;

public class ParallelLabel implements Runnable {

	final InteractiveBud parent;
	final Budregionobject  PairCurrentViewBit;
	final List<RealLocalizable> truths;
	ArrayList<Cellobject> Greencelllist;
	final RealLocalizable centerpoint;
	final String uniqueID;
	final int label;

	
	public ParallelLabel(InteractiveBud parent,ArrayList<Cellobject> Greencelllist , Budregionobject  PairCurrentViewBit,
			List<RealLocalizable> truths, RealLocalizable centerpoint, String uniqueID,
			int label) {
		
		
		this.Greencelllist = Greencelllist;
		this.parent = parent;
		this.PairCurrentViewBit = PairCurrentViewBit;
		this.truths = truths;
		this.centerpoint = centerpoint;
		this.uniqueID = uniqueID;
		this.label = label;
		
		
	}
	
	
	
	@Override
	public void run() {
	

		
		// Corner points of region
				OpService ops = parent.ij.op();
				
		List<RealLocalizable> skeletonEndPoints = TrackEach3DCell.GetCorner(PairCurrentViewBit, ops);
		

		Budobject Curreentbud = new Budobject(centerpoint, truths, skeletonEndPoints, parent.fourthDimension, label,
				truths.size() * parent.calibrationX);
	      Greencelllist  = GetNearest.getAllInterior3DCells(parent, parent.CurrentViewInt, parent.CurrentViewYellowInt);

	      
	      
     	for(Cellobject currentbudcell:Greencelllist) {
			
			
			Localizable centercell = currentbudcell.Location;
			
			RealLocalizable closestskel = GetNearest.getNearestskelPoint(truths, centercell);
			// and the distance
			double closestBudPoint = 0;
			if(closestskel!=null)
				closestBudPoint = Distance.DistanceSqrt(centercell, closestskel);
			// Make the bud n cell object, each cell has all information about the bud n itself 
			BCellobject budncell = new BCellobject(Curreentbud, new ArrayList<Budpointobject>(), currentbudcell, closestBudPoint, closestBudPoint, parent.fourthDimension);
            parent.budcells.add(budncell, parent.fourthDimension);  
            
		
	}

	
	
	}
	
}
