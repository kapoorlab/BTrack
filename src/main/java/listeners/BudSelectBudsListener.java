package listeners;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.SwingUtilities;

import budDetector.Distance;
import displayBud.DisplayListOverlay;
import ij.gui.ImageCanvas;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveBud;
import utility.GetNearest;

public class BudSelectBudsListener {


	public static double maxdist = 50;
	public static void choosebuds(final InteractiveBud parent, final RealLocalizable ceneterpoint) {
		
		
		
		if(parent.ChosenBudcenter.size() > 0	) {
		RealLocalizable Closestpoint = GetNearest.getNearestBudcenter(parent, ceneterpoint);
		double dist = Distance.DistanceSqrt(ceneterpoint, Closestpoint);
		System.out.println(dist + " " + ceneterpoint + " " + Closestpoint );
		if(dist <= maxdist) {
		parent.ChosenBudcenter.remove(Closestpoint);
		parent.ChosenBudcenter.add(ceneterpoint);
		}
		}
		
	}
	
	public static void markbuds(final InteractiveBud parent) {
		
		if (parent.mvl != null)
			parent.imp.getCanvas().removeMouseListener(parent.mvl);
		parent.imp.getCanvas().addMouseListener(parent.mvl = new MouseListener() {

			final ImageCanvas canvas = parent.imp.getWindow().getCanvas();

			@Override
			public void mouseClicked(MouseEvent e) {

			

			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());
				parent.Clickedpoints[0] = x;
				parent.Clickedpoints[1] = y;

				
				if (SwingUtilities.isLeftMouseButton(e)) {
					
					// Select buds for tracking Red wont be tracked
					
					 OvalRoi Closestroi = GetNearest.getNearestRois(parent.BudOvalRois, new double[] {x,y});
					 
					 // Check if already selected
					 RealLocalizable Closestpoint = GetNearest.getNearestPoint(parent, new RealPoint(new double[] {x,y}));
					 if(parent.ChosenBudcenter.contains(Closestpoint)) {
						 
						 parent.ChosenBudcenter.remove(Closestpoint);
							
						 Closestroi.setStrokeColor(Color.RED);
						 parent.imp.updateAndDraw();
						 
					 }
					 else {
						 
						 parent.ChosenBudcenter.add(Closestpoint);
							
						 Closestroi.setStrokeColor(Color.GREEN);
						 parent.imp.updateAndDraw();
						 
					 }
					 
				}
				
			
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});

	}
		
		
	
	
	
}
