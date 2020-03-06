package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import budDetector.Budpointobject;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveBud;

public class BudSaveListener implements ActionListener {
	
	
	
	final InteractiveBud parent;
	
	public BudSaveListener(final InteractiveBud parent) {
		
		this.parent = parent;
		
	}
	
	


	@Override
	public void actionPerformed(ActionEvent e) {
	
		String ID = parent.selectedID;
		if(ID!=null) {
		for (ValuePair<String, Budpointobject> Track: parent.Tracklist) {
			
			try {
				File fichier = new File(
						 parent.saveFile + "//" + "BudGrowth" + parent.addToName + "TrackID" +ID + ".txt");

				FileWriter fw = new FileWriter(fichier);
				BufferedWriter bw = new BufferedWriter(fw);
				
				bw.write(
						" Time, LocationX , LocationY , Velocity \n");
				
				if(Track.getA().equals(ID)) {
				
					
					double time = Track.getB().t * parent.timecal;
					double LocationX = Track.getB().Location[0] * parent.calibration;
					double LocationY = Track.getB().Location[1] * parent.calibration;
					double Velocity = Track.getB().velocity;
				bw.write(time + "," 
						+ parent.nf.format(LocationX) + "," 
						+ parent.nf.format(LocationY) + "," 
						+ parent.nf.format(Velocity) + 
						"\n");
				
				
			bw.close();
			fw.close();
				}
			}
			catch (IOException te) {
			}
			
		}
		
		BudSaveAllListener.saveTrackMovie(parent, ID);
		}
		
	}

}
