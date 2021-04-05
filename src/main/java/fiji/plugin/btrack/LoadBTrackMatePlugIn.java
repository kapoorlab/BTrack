package fiji.plugin.btrack;

import javax.swing.JFrame;

import fiji.plugin.btrack.gui.descriptors.BTMStartDialogDescriptor;
import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

public class LoadBTrackMatePlugIn {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("");

		
	

	    new ImageJ();
	
		ImagePlus impA = new Opener()
			.openImage("/Users/aimachine/StarDiskMask/Tracking-1.tif");
		impA.show();
		
		
		BTMStartDialogDescriptor panel = new BTMStartDialogDescriptor();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
		
	}
	
	
}
