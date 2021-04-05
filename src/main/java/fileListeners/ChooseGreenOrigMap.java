package fileListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JComboBox;

import fiji.plugin.btrack.gui.descriptors.BTMStartDialogDescriptor;
import ij.IJ;
import ij.WindowManager;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;

public class ChooseGreenOrigMap implements ActionListener {
	
	
	final BTMStartDialogDescriptor parent;
	final JComboBox<String> choice;
	
	
	public ChooseGreenOrigMap(final BTMStartDialogDescriptor parent, final JComboBox<String> choice ) {
		
		
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
