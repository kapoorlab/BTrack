package listeners;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.SwingUtilities;

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


	public static void removebuds(final InteractiveBud parent, final RealLocalizable ceneterpoint) {
		
		
		
		OvalRoi Closestroi = GetNearest.getNearestRois(parent.BudOvalRois, new double[] {ceneterpoint.getDoublePosition(0),ceneterpoint.getDoublePosition(1)});
		
		parent.AllBudcenter.remove(ceneterpoint);
		RealLocalizable Closestpoint = GetNearest.getNearestPoint(parent, ceneterpoint);
		parent.ChosenBudcenter.add(Closestpoint);
		Closestroi.setStrokeColor(Color.RED);
		 parent.imp.updateAndDraw();
		
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

				
				if (SwingUtilities.isRightMouseButton(e)) {
					
					// Select buds for removing
					
					 OvalRoi Closestroi = GetNearest.getNearestRois(parent.BudOvalRois, new double[] {x,y});
					 
					 if(Closestroi.getStrokeColor() == Color.GREEN) {
					 
				     RealLocalizable Closestpoint = GetNearest.getNearestPoint(parent, new RealPoint(new double[] {x,y}));
					 parent.ChosenBudcenter.add(Closestpoint);
					 
					 Closestroi.setStrokeColor(Color.RED);
					 parent.imp.updateAndDraw();
					 
					 }
				}
				
				if (SwingUtilities.isRightMouseButton(e) && e.isShiftDown()) {
				
					// Re-add removed buds
					
					OvalRoi Closestroi = GetNearest.getNearestRois(parent.BudOvalRois, new double[] {x,y});
					
					if(Closestroi.getStrokeColor() == Color.RED) {
						
					
					RealLocalizable Closestpoint = GetNearest.getNearestPoint(parent, new RealPoint(new double[] {x,y}));
					 if(parent.ChosenBudcenter.contains(Closestpoint))
					 parent.ChosenBudcenter.remove(Closestpoint);
					
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
