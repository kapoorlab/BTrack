package listeners;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import fileListeners.ChooseBudOrigMap;
import fileListeners.ChooseBudSecOrigMap;
import fileListeners.ChooseBudSegBMap;
import loadfile.CovistoOneChFileLoader;
import loadfile.CovistoTwoChForceFileLoader;
import pluginTools.BudFileChooser;


public class BTrackGoRedListener implements ItemListener {

	public final BudFileChooser parent;
	
	public BTrackGoRedListener( final BudFileChooser parent) {
		
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			
			parent.panelFirst.remove(parent.Panelfileoriginal);
			parent.panelFirst.remove(parent.Panelfile);
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
			CovistoOneChFileLoader original = new CovistoOneChFileLoader(parent.chooseoriginalrgbfilestring, parent.blankimageNames);
			
			parent.Panelfileoriginal = original.SingleChannelOption();
			
			
			
			CovistoTwoChForceFileLoader segmentation = new CovistoTwoChForceFileLoader(parent.chooseRedSegstring, parent.blankimageNames);
			parent.Panelfile = segmentation.TwoChannelOption();
			
			
			parent.panelFirst.add(parent.Panelfile, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			parent.panelFirst.add(parent.Panelfileoriginal, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
	
			original.ChooseImage.addActionListener(new ChooseBudSecOrigMap(parent, original.ChooseImage));
			segmentation.ChoosesecImage.addActionListener(new ChooseBudSegBMap(parent, segmentation.ChoosesecImage));
			parent.budonly = false;
		parent.Panelfileoriginal.validate();
		parent.Panelfileoriginal.repaint();
		
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
		
		}
		
		else if (e.getStateChange() == ItemEvent.DESELECTED) {
			
			parent.budonly = true;
			parent.Panelfileoriginal.validate();
			parent.Panelfileoriginal.repaint();
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
			
			
		}
		
		
		
		
	}

}
