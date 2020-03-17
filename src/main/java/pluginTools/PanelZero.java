package pluginTools;

import javax.swing.JFrame;

import ij.ImagePlus;
import ij.io.Opener;

import net.imagej.ImageJ;

public class PanelZero {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("");

	

	
		
		ImagePlus impC = new Opener()
				.openImage("/Users/aimachine/Documents/Claudia/DualBud/SuperTest.tif");
		impC.show();

		ImagePlus impD = new Opener()
				.openImage("/Users/aimachine/Documents/Claudia/DualBud/TRResults/SuperSegmentation.tif");
		impD.show();

		BudFileChooser panel = new BudFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
		
	}
	
	
}
