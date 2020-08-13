package listeners;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import fileListeners.ChooseBudOrigMap;
import fileListeners.ChooseBudSecOrigMap;
import fileListeners.ChooseBudSegAMap;
import fileListeners.ChooseBudSegBMap;
import fileListeners.ChooseCellSegAMap;
import fileListeners.ChooseCellSegBMap;
import loadfile.CovistoOneChFileLoader;
import loadfile.CovistoTwoChForceFileLoader;
import pluginTools.BudFileChooser;
import pluginTools.TwoDTimeCellFileChooser;


public class BTrackGoMaskFLListener implements ItemListener {

	public final TwoDTimeCellFileChooser parent;
	
	public BTrackGoMaskFLListener( final TwoDTimeCellFileChooser parent) {
		
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			
			parent.panelFirst.remove(parent.Panelfile);
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
			
			
			
			CovistoTwoChForceFileLoader segmentation = new CovistoTwoChForceFileLoader(parent.chooseMaskSegstring, parent.blankimageNames);
			parent.Panelfile = segmentation.TwoChannelOption();
			
			
			parent.panelFirst.add(parent.Panelfile, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			segmentation.ChooseImage.addActionListener(new ChooseCellSegAMap(parent, segmentation.ChooseImage));
			segmentation.ChoosesecImage.addActionListener(new ChooseCellSegBMap(parent, segmentation.ChoosesecImage));
			parent.DoMask = true;
			parent.NoMask = false;
		parent.Panelfile.validate();
		parent.Panelfile.repaint();
		
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
		parent.Cardframe.pack();
		}
		
		else if (e.getStateChange() == ItemEvent.DESELECTED) {
			

			
			
		}
		
		
		
		
	}

}
