package utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import budDetector.Cellobject;
import budDetector.Roiobject;
import pluginTools.InteractiveBud;

public class SaveGreen {

	
	public InteractiveBud parent;
	
	
	public SaveGreen(InteractiveBud parent) {
		
		this.parent = parent;
		
	}
	
	
	
	public void Saver() {
		
		
		try {
		    File budfile = new File(parent.defaultDirectory + "//" + parent.NameA.replaceFirst("[.][^.]+$", "") + "Restart3DcellTrack"+ ".csv");
				
				if(budfile.exists())
				budfile.delete();
				FileWriter fwbud = new FileWriter(budfile);
				BufferedWriter bwbud = new BufferedWriter(fwbud);
				bwbud.write(
						"T, X , Y, Z, Label,  \n");
               for (Map.Entry<Integer, ArrayList<Cellobject>> timeroi: parent.CSVGreen.entrySet()) {
            	   
						Integer time =   timeroi.getKey();
						
						ArrayList<Cellobject> Totalrois = timeroi.getValue();
						for(int i = 0; i < Totalrois.size(); ++i) {
						Cellobject roi = Totalrois.get(i);
						double LocationX = roi.Location.getDoublePosition(0);
						double LocationY = roi.Location.getDoublePosition(1);
						double LocationZ = roi.Location.getDoublePosition(2);
						int Label = roi.label;
						bwbud.write(time + "," 
								+ parent.nf.format(LocationX) + "," 
								+ parent.nf.format(LocationY) +  "," 
								+ parent.nf.format(LocationZ) + ","
								+ parent.nf.format(Label) + "," + 
								"\n");
						}
						}
		  
		  bwbud.close();
			fwbud.close();
			
		}
		catch (IOException te) {
		}
	
}
}
