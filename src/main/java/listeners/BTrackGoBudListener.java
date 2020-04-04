package listeners;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import fileListeners.ChooseBudOrigMap;
import fileListeners.ChooseBudSegAMap;
import loadfile.CovistoOneChFileLoader;
import loadfile.CovistoTwoChForceFileLoader;
import pluginTools.BudFileChooser;


public class BTrackGoBudListener implements ItemListener {

	public final BudFileChooser parent;
	
	public BTrackGoBudListener( final BudFileChooser parent) {
		
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			
			parent.panelFirst.remove(parent.Panelfileoriginal);
			parent.panelFirst.remove(parent.Panelfile);
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
			
			CovistoOneChFileLoader original = new CovistoOneChFileLoader(parent.chooseoriginalbudfilestring, parent.blankimageNames);
			
			parent.Panelfileoriginal = original.SingleChannelOption();
			
			
			CovistoOneChFileLoader segmentation = new CovistoOneChFileLoader(parent.chooseBudSegstring, parent.blankimageNames);
			parent.Panelfile = segmentation.SingleChannelOption();
			
			
			parent.panelFirst.add(parent.Panelfile, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			
			
			parent.panelFirst.add(parent.Panelfileoriginal, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
	
			original.ChooseImage.addActionListener(new ChooseBudOrigMap(parent, original.ChooseImage));
			segmentation.ChooseImage.addActionListener(new ChooseBudSegAMap(parent, segmentation.ChooseImage));
			parent.budonly = true;
		parent.Panelfileoriginal.validate();
		parent.Panelfileoriginal.repaint();
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
		
		}
		
		else if (e.getStateChange() == ItemEvent.DESELECTED) {
			
			parent.budonly = false;
			parent.Panelfileoriginal.validate();
			parent.Panelfileoriginal.repaint();
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
			
			
		}
		
		
		
		
	}

}
