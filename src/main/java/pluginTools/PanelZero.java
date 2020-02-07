package pluginTools;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

public class PanelZero {

	public static void main(String[] args) {
		
		new ImageJ();
		JFrame frame = new JFrame("");

	

		ImagePlus impA = new Opener()
				.openImage("/Users/aimachine/Documents/Claudia/ClaudiaProb.tif");
		impA.show();
		
		ImagePlus impC = new Opener()
				.openImage("/Users/aimachine/Documents/Claudia/BudOriginal.tif");
		impC.show();

		ImagePlus impD = new Opener()
				.openImage("/Users/aimachine/Documents/Claudia/BudSegment.tif");
		impD.show();

		BudFileChooser panel = new BudFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
		
	}
	
	
}
