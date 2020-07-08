package fileListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JComboBox;

import ij.IJ;
import ij.WindowManager;
import pluginTools.BudFileChooser;
import pluginTools.RedGreenFileChooser;

public class ChooseRedOrigMap implements ActionListener {
	
	
	final RedGreenFileChooser parent;
	final JComboBox<String> choice;
	
	
	public ChooseRedOrigMap(final RedGreenFileChooser parent, final JComboBox<String> choice ) {
		
		
		this.parent = parent;
		this.choice = choice;
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		String imagename = (String) choice.getSelectedItem();
		
    	parent.impOrigRed = WindowManager.getImage(imagename);
   
    	if(parent.impOrigRed!=null) {
			parent.calibration = parent.impOrigRed.getCalibration().pixelWidth;
			parent.FrameInterval = parent.impOrigRed.getCalibration().frameInterval;
			if (parent.FrameInterval == 0)
				parent.FrameInterval = 1;
			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
			otherSymbols.setDecimalSeparator('.');
			otherSymbols.setGroupingSeparator(','); 
			DecimalFormat df = new DecimalFormat(("#.###"), otherSymbols);
			parent.Fieldwavesize.setText(String.valueOf(df.format(parent.FrameInterval))); 
			parent.inputFieldcalX.setText(String.valueOf(df.format(parent.calibration)));
	}
 
		
	}
	

}
