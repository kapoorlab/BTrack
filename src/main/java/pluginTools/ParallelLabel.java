package pluginTools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import budDetector.Budobject;
import budDetector.Budpointobject;
import budDetector.Budregionobject;
import budDetector.Cellobject;
import budDetector.Distance;
import fiji.plugin.trackmate.Spot;
import net.imagej.ops.OpService;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import utility.GetNearest;
import pluginTools.TrackEach3DCell;

public class ParallelLabel implements Runnable {

	final InteractiveBud parent;
	ArrayList<Cellobject> Greencelllist;
	final String uniqueID;

	
	public ParallelLabel(InteractiveBud parent,ArrayList<Cellobject> Greencelllist , String uniqueID) {
		
		
		this.Greencelllist = Greencelllist;
		this.parent = parent;
		this.uniqueID = uniqueID;
		
		
	}
	
	
	@Override
	public void run() {
	
		  
	    Greencelllist  = GetNearest.getAllInterior3DCells(parent, parent.CurrentViewYellowInt);
	      
     	for(Cellobject currentbudcell:Greencelllist) {
			
			// Make TM spot
     		
			double size = 0;
			// Get bounding box region in XYZ
			for(int i = 0; i <currentbudcell.extents.length; ++i )		
			size = size * currentbudcell.extents[i];
			//Create radius of spot
			size = size/8;
			double radius = Math.sqrt(size);
			double quality = currentbudcell.cellVolume;
			
			// Make the Spot
			Spot budncell = new Spot(currentbudcell.Location,radius,quality);
			
            parent.budcells.add(budncell, parent.fourthDimension);  
            
		
	}

	
	
	}
	
}
