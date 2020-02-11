package utility;

import ij.IJ;
import pluginTools.InteractiveBud;

public class BudShowView {

	
	final InteractiveBud parent;
	
	
	public BudShowView(final InteractiveBud parent) {
		
		this.parent = parent;
		
	}
	
	

	
	
	public void shownewT() {

		if (parent.thirdDimension > parent.thirdDimensionSize) {
			IJ.log("Max time point exceeded, moving to last time point instead");
			parent.thirdDimension = parent.thirdDimensionSize;
			
			
			parent.CurrentView = utility.BudSlicer.getCurrentBudView(parent.originalimg,(int) parent.thirdDimension,
					(int)parent.thirdDimensionSize);
			
		} else {

			parent.CurrentView = utility.BudSlicer.getCurrentBudView(parent.originalimg,(int) parent.thirdDimension,
					(int)parent.thirdDimensionSize);
			
		}

		
		
	

		
	}
	
}
