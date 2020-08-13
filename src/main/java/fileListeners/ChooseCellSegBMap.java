package fileListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import ij.WindowManager;
import pluginTools.TwoDTimeCellFileChooser;

public class ChooseCellSegBMap implements ActionListener {
	
	
	final TwoDTimeCellFileChooser parent;
	final JComboBox<String> choice;
	
	
	public ChooseCellSegBMap(final TwoDTimeCellFileChooser parent, final JComboBox<String> choice ) {
		
		
		this.parent = parent;
		this.choice = choice;
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		String imagename = (String) choice.getSelectedItem();
		
    	parent.impSegB = WindowManager.getImage(imagename);
    	

		
	}
	

}
