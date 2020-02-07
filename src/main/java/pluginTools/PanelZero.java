package pluginTools;

import javax.swing.JFrame;

import ij.ImagePlus;
import ij.io.Opener;
import sc.fiji.simplifiedio.SimplifiedIO;

import net.imagej.ImageJ;

public class PanelZero {

	public static void main(String[] args) {
		ImageJ ij = new ImageJ();
		ij.ui().showUI();
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
