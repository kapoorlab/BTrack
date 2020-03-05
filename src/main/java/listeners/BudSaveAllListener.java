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

public class BudSaveAllListener implements ActionListener {
	
	
	
	final InteractiveBud parent;
	
	public BudSaveAllListener(final InteractiveBud parent) {
		
		this.parent = parent;
		
	}
	
	


	@Override
	public void actionPerformed(ActionEvent e) {
	
for(int tablepos = 0; tablepos< parent.table.getRowCount(); ++tablepos) {
			
			String ID = (String) parent.table.getValueAt(tablepos, 0);
		for (ValuePair<String, Budpointobject> Track: parent.Tracklist) {
			
			try {
				File fichier = new File(
						 parent.saveFile + "//" + "BudGrowth" + parent.addToName + "TrackID" +ID + ".txt");

				FileWriter fw = new FileWriter(fichier);
				BufferedWriter bw = new BufferedWriter(fw);
				
				bw.write(
						"\t Time \t LocationX \t LocationY \t Velocity \n");
				
				if(Track.getA().equals(ID)) {
				
					
					double time = Track.getB().t * parent.timecal;
					double LocationX = Track.getB().Location[0] * parent.calibration;
					double LocationY = Track.getB().Location[1] * parent.calibration;
					double Velocity = Track.getB().velocity;
				bw.write("\t" + time + "\t" + "\t"
						+ LocationX + "\t" + "\t"
						+ LocationY + "\t" + "\t"
						+ Velocity + 
						"\n");
				
				
			bw.close();
			fw.close();
				}
			}
			catch (IOException te) {
			}
			
		}
}
		
		
	}

}
