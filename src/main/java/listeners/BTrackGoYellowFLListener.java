package listeners;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import fiji.plugin.btrack.gui.components.LoadDualImage;
import fiji.plugin.btrack.gui.descriptors.BTStartDialogDescriptor;
import fileListeners.ChooseBudSegAMap;
import fileListeners.ChooseBudSegBMap;


public class BTrackGoYellowFLListener implements ItemListener {

	public final BTStartDialogDescriptor parent;
	
	public BTrackGoYellowFLListener( final BTStartDialogDescriptor parent) {
		
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			
			parent.panelFirst.remove(parent.Panelfile);
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
			
			
			
			LoadDualImage segmentation = new LoadDualImage(parent.chooseYellowSegstring, parent.blankimageNames);
			parent.Panelfile = segmentation.TwoChannelOption();
			
			
			parent.panelFirst.add(parent.Panelfile, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			segmentation.ChooseImage.addActionListener(new ChooseBudSegAMap(parent, segmentation.ChooseImage));
			segmentation.ChoosesecImage.addActionListener(new ChooseBudSegBMap(parent, segmentation.ChoosesecImage));
			parent.DoYellow = true;
			parent.DoGreen = false;
			parent.DoRed = false;
			parent.NoChannel = false;
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
