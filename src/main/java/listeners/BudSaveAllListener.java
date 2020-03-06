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
import java.util.HashSet;
import java.util.Iterator;

import org.jgrapht.graph.DefaultWeightedEdge;

import budDetector.Budpointobject;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.TextRoi;
import ij.io.FileSaver;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveBud;
import tracker.TrackModel;

public class BudSaveAllListener implements ActionListener {
	
	
	
	final InteractiveBud parent;
	
	public BudSaveAllListener(final InteractiveBud parent) {
		
		this.parent = parent;
		
	}
	
	


	@Override
	public void actionPerformed(ActionEvent e) {
		
		
	
for(int tablepos = 0; tablepos< parent.table.getRowCount(); ++tablepos) {
			
			
			
	String ID = (String) parent.table.getValueAt(tablepos, 0);
			if(ID!=null) {
			try {
				File fichier = new File(
						 parent.saveFile + "//" + "BudGrowth" + parent.addToName + "TrackID" +ID + ".txt");

				FileWriter fw = new FileWriter(fichier);
				BufferedWriter bw = new BufferedWriter(fw);
				
				bw.write(
						" Time, LocationX , LocationY , Velocity \n");
				
				
				for (ValuePair<String, Budpointobject> Track: parent.Tracklist) {
					
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
				
					}
				
				
			
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
		
		RandomAccessibleInterval<FloatType> TrackMovie = new ArrayImgFactory().create(parent.originalimg, new FloatType());
		
		
		// Get the corresponding set for each id
		Integer id = Integer.parseInt(ID);
		TrackModel model = parent.Globalmodel;
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

			final double[] startedge = Spotbase.Location;
			final double[] targetedge = Spottarget.Location; 
		
			
			if(model.trackIDOf(Spotbase) == id) {

				ranac.setPosition(new int[] { (int) startedge[0],  (int) startedge[1],Spotbase.t});
				ranac.get().setOne();
				
				ranac.setPosition(new int[] {(int) targetedge[0], (int) targetedge[1],Spottarget.t});
				ranac.get().setOne();
				

			}
		
		
		
		
	}
		
		ImagePlus Trackimp = ImageJFunctions.wrapFloat(TrackMovie, ID);

		FileSaver DistfsC = new FileSaver(Trackimp);

		DistfsC.saveAsTiff(parent.saveFile + "//"  + parent.inputstring.replaceFirst("[.][^.]+$", "")
		+ parent.addToName + "TrackID" + Integer.parseInt(ID) + ".tif");
		
	}

}
