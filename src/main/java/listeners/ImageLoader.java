package listeners;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import fileListeners.ChooseGreenSegMap;
import loadfile.CovistoOneChFileLoader;
import pluginTools.ThreeDTimeCellFileChooser;


public class ImageLoader implements ItemListener {

	public final ThreeDTimeCellFileChooser parent;
	
	
	
	public ImageLoader( final ThreeDTimeCellFileChooser parent) {
		
		this.parent = parent;
	}
	
	
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			
			parent.panelFirst.remove(parent.Panelfile);
			parent.Panelfile.removeAll();
			parent.Panelfile.validate();
			parent.Panelfile.repaint();
			parent.Paneldone.remove(parent.Checkpointbutton);
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
			
			parent.panelFirst.add(parent.FreeMode, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			parent.panelFirst.add(parent.MaskMode, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));


			
			// Listeneres
			CovistoOneChFileLoader segmentation = new CovistoOneChFileLoader(parent.chooseCellSegstring, parent.blankimageNames);
			parent.Panelfile = segmentation.SingleChannelOption();
			segmentation.ChooseImage.addActionListener(new ChooseGreenSegMap(parent, segmentation.ChooseImage));
			
         

			
			parent.panelFirst.add(parent.Panelfile, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			

		parent.Panelfile.validate();
		parent.Panelfile.repaint();
		
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
		parent.Cardframe.pack();
		}
		
		else if (e.getStateChange() == ItemEvent.DESELECTED) {
			
			parent.panelFirst.remove(parent.Panelfile);
			parent.Panelfile.removeAll();
			parent.Panelfile.validate();
			parent.Panelfile.repaint();
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
			
			
			parent.Cardframe.pack();
		}
		
		
		
		
	}

}
