package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import budDetector.BudTrackobject;
import budDetector.Budobject;
import budDetector.Budpointobject;
import net.imglib2.util.Pair;
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

			
		
		
		BudSaveAllListener.saveTrackMovie(parent, ID);
		
		
	
			
			try {
        File budfile = new File(parent.saveFile + "//" + "AllBudInformation" + parent.addToName + "Buds"  + ".txt");
			
			
			FileWriter fwbud = new FileWriter(budfile);
			BufferedWriter bwbud = new BufferedWriter(fwbud);
			bwbud.write(
					"TrackLabel, Time, LocationX , LocationY , Perimeter \n");
			
	
        for (ValuePair<String, Budobject> Track: parent.BudTracklist) {
        	
        	String TrackLabel = Track.getA();
        	
        	
        
			
	
				
					
					double time = Track.getB().t * parent.timecal;
					double LocationX = Track.getB().Budcenter.getDoublePosition(0) * parent.calibration;
					double LocationY = Track.getB().Budcenter.getDoublePosition(1) * parent.calibration;
					double Perimeter = Track.getB().perimeter;
				    
					bwbud.write(TrackLabel + "," + (int)time + "," 
							+ parent.nf.format(LocationX) + "," 
							+ parent.nf.format(LocationY) + "," 
							+ parent.nf.format(Perimeter) + 
							"\n");
				
				
					
				}
				
					
				
        
	
			
      
      bwbud.close();
		fwbud.close();
		
	}
	catch (IOException te) {
	}
		}
		
	
			
		ArrayList<double[]> Trackinfo = new ArrayList<double[]>();
		for (Pair<String, Budpointobject> Track: parent.Tracklist) {
			
			if(Track.getA().equals(ID)) {
			
				
			double time = Track.getB().t * parent.timecal;
			double LocationX = Track.getB().Location[0] * parent.calibration;
			double LocationY = Track.getB().Location[1] * parent.calibration;
			double Velocity = Track.getB().velocity;
			
			Trackinfo.add(new double[] {time, LocationX, LocationY, Velocity});
			
		
			}
		
		
	
	}
		
		int averageframe = 3;
		ArrayList<double[]> AverageTrackinfo = new ArrayList<double[]>();
		for(int i = 0; i< Trackinfo.size() - averageframe; ++i) {
			
			double[] current = Trackinfo.get(i);
    		double[] next = Trackinfo.get(i + 1);
    		double[] secondnext = Trackinfo.get(i + 2);
    		double[] thirdnext = Trackinfo.get(i + 3);
    		double[] fourthnext = Trackinfo.get(i + 4);
    		double[] fifthnext = Trackinfo.get(i + 5);
    		
    	    double currentvelocity = current[3];
    	    double nextvelocity = next[3];
    	    double secondnextvelocity = secondnext[3];
    	    double thirdnextvelocity = thirdnext[3];
    	    double fourthnextvelocity = fourthnext[3];
    	    double fifthnextvelocity = fifthnext[3];
    	    
    	    double averagevelocity = (currentvelocity + nextvelocity + secondnextvelocity + thirdnextvelocity + fourthnextvelocity + fifthnextvelocity )/6.0;
	    double time = current[0];
	    double LocationX = current[1];
	    double LocationY = current[2];
	    AverageTrackinfo.add(new double[] {time, LocationX, LocationY, averagevelocity});
			
		}
		
		try {
			
			
			File fichier = new File(
					 parent.saveFile + "//" + "BudGrowth" + parent.addToName + "TrackID" +ID + ".txt");

			FileWriter fw = new FileWriter(fichier);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(
					" Time, LocationX , LocationY , Velocity \n");
			
			for(int i = 0; i< AverageTrackinfo.size(); ++i) {
				
				double[] current = AverageTrackinfo.get(i);
				 double time = current[0];
				    double LocationX = current[1];
				    double LocationY = current[2];
				    double Velocity = current[3];
				    
				    bw.write((int)time + "," 
							+ parent.nf.format(LocationX) + "," 
							+ parent.nf.format(LocationY) + "," 
							+ parent.nf.format(Velocity) + 
							"\n");
				
			}
	
		
		bw.close();
		fw.close();
	}
	catch (IOException te) {
	}
		
		
		
		}
		
	}


