package fiji.plugin.btrack;

import javax.swing.JFrame;

import ij.ImagePlus;
import ij.io.Opener;
import pluginTools.BudFileChooser;
import ij.ImageJ;

public class LoadBTrackPlugIn {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("");

		
	

	    new ImageJ();
	
		ImagePlus impA = new Opener()
				.openImage("/Users/aimachine/Image0.tif");
		impA.show();
		
	
	
		

		BudFileChooser panel = new BudFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
		
	}
	
	
}
