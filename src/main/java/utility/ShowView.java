package utility;

import ij.IJ;
import pluginTools.InteractiveBud;

public class ShowView {

	
	final InteractiveBud parent;
	
	
	public ShowView(final InteractiveBud parent) {
		
		this.parent = parent;
		
	}
	
	

	
	
	public void shownewT() {

		if (parent.thirdDimension > parent.thirdDimensionSize) {
			IJ.log("Max time point exceeded, moving to last time point instead");
			parent.thirdDimension = parent.thirdDimensionSize;
			
			
			parent.CurrentView = utility.Slicer.getCurrentView(parent.originalimg,(int) parent.thirdDimension,
					(int)parent.thirdDimensionSize);
			
		} else {

			parent.CurrentView = utility.Slicer.getCurrentView(parent.originalimg,(int) parent.thirdDimension,
					(int)parent.thirdDimensionSize);
			
		}

		
		
	

		
	}
	
}
