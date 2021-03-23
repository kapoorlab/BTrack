package fileListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JComboBox;

import ij.IJ;
import ij.WindowManager;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
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
    		parent.imageOrigGreen = SimplifiedIO.openImage(
    				parent.impOrigGreen.getOriginalFileInfo().directory + parent.impOrigGreen.getOriginalFileInfo().fileName,
    				new FloatType());
    		
    			
			parent.TimeTotal = parent.imageOrigGreen.dimension(3);
			
			
			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
			otherSymbols.setDecimalSeparator('.');
			otherSymbols.setGroupingSeparator(','); 
			DecimalFormat df = new DecimalFormat(("#.###"), otherSymbols);
			parent.inputFieldT.setText(String.valueOf(df.format(parent.TimeTotal)));
	}
 
		
	}
	

}
