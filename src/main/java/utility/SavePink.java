package utility;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import ij.gui.OvalRoi;
import net.imglib2.util.Pair;
import pluginTools.InteractiveBud;

public class SavePink {

	public InteractiveBud parent;
	
	public SavePink(final InteractiveBud parent) {
		
		this.parent = parent;
		
	}
	
	
	
	public void Saver() {
		
		
		try {
		    File budfile = new File(parent.defaultDirectory + "//" + "RestartTrack" + ".csv");
				
				
				
				FileWriter fwbud = new FileWriter(budfile);
				BufferedWriter bwbud = new BufferedWriter(fwbud);
				bwbud.write(
						"Time, LocationX , LocationY \n");
               for (Map.Entry<String, ArrayList<Pair<Color, OvalRoi>>> timeroi: parent.BudOvalRois.entrySet()) {
            	   
						String time =   timeroi.getKey();
						
						ArrayList<Pair<Color, OvalRoi>> Totalrois = timeroi.getValue();
						for(int i = 0; i < Totalrois.size(); ++i) {
							
							Pair<Color, OvalRoi> roi = Totalrois.get(i);
							
							if(roi.getA() == parent.BudColor) {
						double LocationX = roi.getB().getContourCentroid()[0];
						double LocationY = roi.getB().getContourCentroid()[1];
					    
						bwbud.write(time + "," 
								+ parent.nf.format(LocationX) + "," 
								+ parent.nf.format(LocationY) +  
								"\n");
					
						}
						
						}
		    
               }
				
		  
		  bwbud.close();
			fwbud.close();
			
		}
		catch (IOException te) {
		}
			}
	
	
}
