package listeners;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.jgrapht.graph.DefaultWeightedEdge;

import budDetector.Budobject;
import budDetector.Budpointobject;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.TextRoi;
import ij.io.FileSaver;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveBud;
import tracker.BUDDYTrackModel;
import utility.BresenhamLine;

public class BudSaveAllListener implements ActionListener {
	
	
	
	final InteractiveBud parent;
	
	
	public BudSaveAllListener(final InteractiveBud parent) {
		
		this.parent = parent;
		
	}
	
	


	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		
		
	
		parent.saveFile.mkdir();
				

			
					
					try {
						
	            File budfile = new File(parent.saveFile + "//" + "AllBudInformation" + parent.addToName + "BudIDs"  + ".txt");
    				
    				
    				FileWriter fwbud = new FileWriter(budfile);
    				BufferedWriter bwbud = new BufferedWriter(fwbud);
    				   
    				bwbud.write(
    						"TrackLabel, Time, LocationX , LocationY , Perimeter \n");
                for (ValuePair<String, Budobject> Track: parent.BudTracklist) {
                	
                	String TrackLabel = Track.getA();
                	
             
					
						
						
							
							double time = Track.getB().t * parent.timecal;
							double LocationX = Track.getB().Budcenter.getDoublePosition(0) * parent.calibrationX;
							double LocationY = Track.getB().Budcenter.getDoublePosition(1) * parent.calibrationY;
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
				
				
				for(int tablepos = 0; tablepos< parent.table.getRowCount(); ++tablepos) {
					
					
					
					String ID = (String) parent.table.getValueAt(tablepos, 0);
							if(ID!=null) {
				
				
								HashMap<Integer, Double> VelocityID = parent.BudVelocityMap.get(Integer.parseInt(ID));
				double maxRate = parent.TrackMaxVelocitylist.get(ID);
				double meanRate = parent.TrackMeanVelocitylist.get(ID);
				ArrayList<double[]> Trackinfo = new ArrayList<double[]>();
				for (Pair<String, Budpointobject> Track: parent.Tracklist) {
					
					if(Track.getA().equals(ID)) {
					
						
					double time = Track.getB().t * parent.timecal;
					double LocationX = Track.getB().Location[0] * parent.calibrationX;
					double LocationY = Track.getB().Location[1] * parent.calibrationY;
					double Velocity = 0;
					if(VelocityID.get(Track.getB().t)!=null)
					 Velocity = VelocityID.get(Track.getB().t);
					Trackinfo.add(new double[] {time, LocationX, LocationY, Velocity, meanRate, maxRate});
					
				
					}
				
				
			
			}
				
				
				try {
					
					
					File fichier = new File(
							 parent.saveFile + "//" + "BudGrowth" + parent.addToName + "TrackID" +ID + ".txt");

					FileWriter fw = new FileWriter(fichier);
					BufferedWriter bw = new BufferedWriter(fw);
					
					bw.write(
							" Time, LocationX , LocationY , Velocity, Mean Growth Rate, Max Growth Rate \n");
					
					for(int i = 0; i< Trackinfo.size(); ++i) {
						
						double[] current = Trackinfo.get(i);
						 double time = current[0];
						    double LocationX = current[1];
						    double LocationY = current[2];
						    double Velocity = current[3];
						    
						    bw.write((int)time + "," 
									+ parent.nf.format(LocationX) + "," 
									+ parent.nf.format(LocationY) + "," 
									+ parent.nf.format(Velocity) + "," 
									+ parent.nf.format(meanRate) + "," 
									+ parent.nf.format(maxRate) +
									"\n");
						
					}
			
				
				bw.close();
				fw.close();
			}
			catch (IOException te) {
			}
		
			
			saveTrackMovie(parent, ID);
			}
		}
}
		
		




	public static void saveTrackMovie(InteractiveBud parent, String ID) {
		
		RandomAccessibleInterval<FloatType> TrackMovie = new ArrayImgFactory().create(new long[] {parent.originalimg.dimension(0), parent.originalimg.dimension(1)}, new FloatType());
		
		
		// Get the corresponding set for each id
		Integer id = Integer.parseInt(ID);
		BUDDYTrackModel model = parent.Globalmodel;
		final HashSet<Budpointobject> Snakeset = model.trackBudpointobjects(id);
		ArrayList<Budpointobject> list = new ArrayList<Budpointobject>();

		Comparator<Budpointobject> ThirdDimcomparison = new Comparator<Budpointobject>() {

			@Override
			public int compare(final Budpointobject A, final Budpointobject B) {

				return A.t - B.t;

			}

		};

		

		Iterator<Budpointobject> Snakeiter = Snakeset.iterator();
		while (Snakeiter.hasNext()) {

			Budpointobject currentsnake = Snakeiter.next();

					list.add(currentsnake);

		}
		Collections.sort(list, ThirdDimcomparison);

		
		RandomAccess<FloatType> ranac = TrackMovie.randomAccess();
		
		for (DefaultWeightedEdge e : model.edgeSet()) {

			Budpointobject Spotbase = model.getEdgeSource(e);
			Budpointobject Spottarget = model.getEdgeTarget(e);

			final long[] startedge = new long[] {(long)Spotbase.Location[0],(long)Spotbase.Location[1] };
			final long[] targetedge = new long[] {(long) Spottarget.Location[0], (long)Spottarget.Location[1] }; 
		
			
			if(model.trackIDOf(Spotbase) == id) {

				 BresenhamLine<FloatType> newline = new BresenhamLine<FloatType>(ranac, new Point(startedge), new Point(targetedge));
				  Cursor<FloatType> cursor = newline.copyCursor();
				  
				  
				  while (cursor.hasNext()) {
				  
				  cursor.fwd();
				  
				  cursor.get().setReal(1.0);
				  
				  }
				

			}
		
		
		
		
	}
		
		ImagePlus Trackimp = ImageJFunctions.wrapFloat(TrackMovie, ID);

		FileSaver DistfsC = new FileSaver(Trackimp);

		DistfsC.saveAsTiff(parent.saveFile + "//"  + parent.inputstring.replaceFirst("[.][^.]+$", "")
		+ parent.addToName + "TrackID" + Integer.parseInt(ID) + ".tif");
		
	}

}
