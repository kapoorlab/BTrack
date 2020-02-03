package fileListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import ij.WindowManager;
import pluginTools.BudFileChooser;

public class ChooseSecOrigMap implements ActionListener {
	
	
	final BudFileChooser parent;
	final JComboBox<String> choice;
	
	
	public ChooseSecOrigMap(final BudFileChooser parent, final JComboBox<String> choice ) {
		
		
		this.parent = parent;
		this.choice = choice;
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		String imagename = (String) choice.getSelectedItem();
		
		
		
    	parent.impOrigSec = WindowManager.getImage(imagename);
    	

		
	}
	

}
