package fileListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import fiji.plugin.btrack.gui.descriptors.BTStartDialogDescriptor;
import ij.WindowManager;

public class ChooseBudSegCMap implements ActionListener {
	
	
	final BTStartDialogDescriptor parent;
	final JComboBox<String> choice;
	
	
	public ChooseBudSegCMap(final BTStartDialogDescriptor parent, final JComboBox<String> choice ) {
		
		
		this.parent = parent;
		this.choice = choice;
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		String imagename = (String) choice.getSelectedItem();
		
    	parent.impSegC = WindowManager.getImage(imagename);

		
	}
	

}
