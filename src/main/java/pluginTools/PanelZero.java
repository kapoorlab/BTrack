package pluginTools;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

public class PanelZero {

	public static void main(String[] args) {
		
		new ImageJ();
		JFrame frame = new JFrame("");

		ImagePlus impB = new Opener()
				.openImage("/Users/aimachine/Documents/Dureen/RAW.tif");
		impB.show();

		ImagePlus impA = new Opener()
				.openImage("/Users/aimachine/Documents/Dureen/MASK_EMBRYO.tif");
		impA.show();

		BudFileChooser panel = new BudFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
		
	}
	
	
}
