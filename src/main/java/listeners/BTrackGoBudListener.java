package listeners;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import fiji.plugin.btrack.gui.components.LoadSingleImage;
import fiji.plugin.btrack.gui.descriptors.BTStartDialogDescriptor;
import fileListeners.ChooseBudOrigMap;


public class BTrackGoBudListener implements ItemListener {

	public final BTStartDialogDescriptor parent;
	
	public BTrackGoBudListener( final BTStartDialogDescriptor parent) {
		
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			
			parent.panelFirst.remove(parent.Panelfileoriginal);
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
			
			LoadSingleImage original = new LoadSingleImage(parent.chooseoriginalbudfilestring, parent.blankimageNames);
			
			parent.Panelfileoriginal = original.SingleChannelOption();
			
			parent.panelFirst.add(parent.Panelfileoriginal, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
	
			original.ChooseImage.addActionListener(new ChooseBudOrigMap(parent, original.ChooseImage));
		parent.Panelfileoriginal.validate();
		parent.Panelfileoriginal.repaint();
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
		
		}
		
		else if (e.getStateChange() == ItemEvent.DESELECTED) {
			
			parent.Panelfileoriginal.validate();
			parent.Panelfileoriginal.repaint();
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
			
			
		}
		
		
		
		
	}

}
