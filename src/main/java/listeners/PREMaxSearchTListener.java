package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import kalmanGUI.CovistoKalmanPanel;
import nearestNeighbourGUI.CovistoNearestNPanel;
import pluginTools.InteractiveBud;

public class PREMaxSearchTListener implements AdjustmentListener {
	
	final Label label;
	final String string;
	final InteractiveBud parent;
	final float min, max;
	final int scrollbarSize;
	final JScrollBar scrollbar;
	
	
	public PREMaxSearchTListener(final InteractiveBud parent, final Label label, final String string, final float min, final float max, final int scrollbarSize, final JScrollBar scrollbar) {
		
		this.parent = parent;
		this.label = label;
		this.string = string;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;
		this.scrollbar = scrollbar;
		
		scrollbar.setBlockIncrement(utility.Slicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
		scrollbar.setUnitIncrement(utility.Slicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
	}
	
	
	
	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		CovistoKalmanPanel.maxSearchradius = utility.Slicer.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		
			scrollbar.setValue(utility.Slicer.computeScrollbarPositionFromValue(CovistoKalmanPanel.maxSearchradius, min, max, scrollbarSize));

			label.setText(string +  " = "  + parent.nf.format(CovistoKalmanPanel.maxSearchradius));
			
			
	
	}
	

}
