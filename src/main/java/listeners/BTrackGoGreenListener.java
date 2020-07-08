package listeners;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import fileListeners.ChooseBudOrigMap;
import fileListeners.ChooseBudSegAMap;
import fileListeners.ChooseGreenOrigMap;
import fileListeners.ChooseGreenSegMap;
import fileListeners.ChooseMaskSegMap;
import fileListeners.ChooseRedOrigMap;
import fileListeners.ChooseRedSegMap;
import loadfile.CovistoOneChFileLoader;
import loadfile.CovistoTwoChForceFileLoader;
import pluginTools.RedGreenFileChooser;


public class BTrackGoGreenListener implements ItemListener {

	public final RedGreenFileChooser parent;
	
	public BTrackGoGreenListener( final RedGreenFileChooser parent) {
		
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			
			parent.panelFirst.remove(parent.Panelfileoriginal);
			parent.panelFirst.remove(parent.Panelfile);
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
			
			CovistoOneChFileLoader original = new CovistoOneChFileLoader(parent.chooseoriginalGreenfilestring, parent.blankimageNames);
			
			parent.Panelfileoriginal = original.SingleChannelOption();
			
			parent.panelFirst.add(parent.Panelfileoriginal, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			
			
			CovistoOneChFileLoader segmentation = new CovistoOneChFileLoader(parent.chooseGreenSegstring, parent.blankimageNames);
			parent.Panelfile = segmentation.SingleChannelOption();
			
			
			parent.panelFirst.add(parent.Panelfile, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
	
			original.ChooseImage.addActionListener(new ChooseGreenOrigMap(parent, original.ChooseImage));
			segmentation.ChooseImage.addActionListener(new ChooseGreenSegMap(parent, segmentation.ChooseImage));
			
			
			CovistoOneChFileLoader mask = new CovistoOneChFileLoader(parent.chooseMaskstring, parent.blankimageNames);
			parent.Panelfilemask = mask.SingleChannelOption();
			
			
			parent.panelFirst.add(parent.Panelfilemask, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			
			parent.OnlyGreen = true;
			parent.RedandGreen = false;
			
			
		parent.Panelfileoriginal.validate();
		parent.Panelfileoriginal.repaint();
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
		
		}
		
		else if (e.getStateChange() == ItemEvent.DESELECTED) {
			
			parent.panelFirst.remove(parent.Panelfileoriginal);
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
			
			parent.OnlyGreen = false;
			parent.RedandGreen = true;
			
			
			CovistoTwoChForceFileLoader dualoriginal = new CovistoTwoChForceFileLoader(parent.chooseoriginalRedfilestring, parent.blankimageNames);
			
			parent.Panelfileoriginal = dualoriginal.TwoChannelOption();
			
			
			dualoriginal.ChooseImage.addActionListener(new ChooseGreenOrigMap(parent, dualoriginal.ChooseImage));
			dualoriginal.ChoosesecImage.addActionListener(new ChooseRedOrigMap(parent, dualoriginal.ChoosesecImage));
			
			parent.panelFirst.add(parent.Panelfileoriginal, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			
			
			CovistoTwoChForceFileLoader dualsegmentation = new CovistoTwoChForceFileLoader(parent.chooseRedSegstring, parent.blankimageNames);
			
			parent.Panelfile = dualsegmentation.TwoChannelOption();
			
			parent.panelFirst.add(parent.Panelfile, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
	
			
			dualsegmentation.ChooseImage.addActionListener(new ChooseGreenSegMap(parent, dualsegmentation.ChooseImage));
			dualsegmentation.ChoosesecImage.addActionListener(new ChooseRedSegMap(parent, dualsegmentation.ChoosesecImage));
			
			
			
			CovistoOneChFileLoader mask = new CovistoOneChFileLoader(parent.chooseMaskstring, parent.blankimageNames);
			parent.Panelfilemask = mask.SingleChannelOption();
			
			mask.ChooseImage.addActionListener(new ChooseMaskSegMap(parent, mask.ChooseImage));
			
			parent.panelFirst.add(parent.Panelfilemask, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			
			
			
			parent.Panelfileoriginal.validate();
			parent.Panelfileoriginal.repaint();
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
			
			
		}
		
		
		
		
	}

}
