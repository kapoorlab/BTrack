package fileListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import ij.WindowManager;
import pluginTools.TwoDTimeCellFileChooser;

public class ChooseCellSegAMap implements ActionListener {
	
	
	final TwoDTimeCellFileChooser parent;
	final JComboBox<String> choice;
	
	
	public ChooseCellSegAMap(final TwoDTimeCellFileChooser parent, final JComboBox<String> choice ) {
		
		
		this.parent = parent;
		this.choice = choice;
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		String imagename = (String) choice.getSelectedItem();
		
		
		
    	parent.impSegA = WindowManager.getImage(imagename);
    	

		
	}
	

}