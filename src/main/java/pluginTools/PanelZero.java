package pluginTools;

import javax.swing.JFrame;

import ij.ImagePlus;
import ij.io.Opener;

import net.imagej.ImageJ;

public class PanelZero {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("");

		
	

	    new ImageJ();
	
		ImagePlus impA = new Opener()
				.openImage("/Users/aimachine/Documents/Claudia/DualBud/SegmentedTilesTransmittedLightA.tif");
		impA.show();
		
		ImagePlus impB = new Opener()
				.openImage("/Users/aimachine/Documents/Claudia/DualBud/YellowSeg.tif");
		impB.show();
	
	
		
		ImagePlus impC = new Opener()
				.openImage("/Users/aimachine/Documents/Claudia/DualBud/TransmittedLightA.tif");
		impC.show();

		ImagePlus impD = new Opener()
				.openImage("/Users/aimachine/Documents/Claudia/DualBud/SegmentedTilesTransmittedLightA.tif");
		impD.show();

		BudFileChooser panel = new BudFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
		
	}
	
	
}
