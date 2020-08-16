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
import pluginTools.ThreeDTimeCellFileChooser;

public class ChooseGreenOrigMap implements ActionListener {
	
	
	final ThreeDTimeCellFileChooser parent;
	final JComboBox<String> choice;
	
	
	public ChooseGreenOrigMap(final ThreeDTimeCellFileChooser parent, final JComboBox<String> choice ) {
		
		
		this.parent = parent;
		this.choice = choice;
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		String imagename = (String) choice.getSelectedItem();
		
		
		
    	parent.impOrigGreen = WindowManager.getImage(imagename);
   
    	if(parent.impOrigGreen!=null) {
			
    		    parent.calibrationX = parent.impOrigGreen.getCalibration().pixelWidth;
			parent.calibrationY = parent.impOrigGreen.getCalibration().pixelHeight;
			parent.calibrationZ = parent.impOrigGreen.getCalibration().pixelDepth;
			parent.FrameInterval = parent.impOrigGreen.getCalibration().frameInterval;
			
			if (parent.FrameInterval == 0)
				parent.FrameInterval = 1;
			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
			otherSymbols.setDecimalSeparator('.');
			otherSymbols.setGroupingSeparator(','); 
			DecimalFormat df = new DecimalFormat(("#.###"), otherSymbols);
			parent.inputLabelcalT.setText(String.valueOf(df.format(parent.FrameInterval))); 
			parent.inputFieldcalX.setText(String.valueOf(df.format(parent.calibrationX)));
			parent.inputFieldcalY.setText(String.valueOf(df.format(parent.calibrationY)));
			parent.inputFieldcalZ.setText(String.valueOf(df.format(parent.calibrationZ)));
	}
 
		
	}
	

}
