package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.SwingUtilities;

import budDetector.Budpointobject;
import fileListeners.SimplifiedIO;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import kalmanGUI.CovistoKalmanPanel;
import net.imagej.DatasetService;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.InteractiveBud;
import pluginTools.InteractiveBud.ValueChange;
import skeleton.SkeletonizeBuds;

public class BudModifyListener implements ActionListener {
	
	final InteractiveBud parent;
	static ImgPlus<FloatType> imp;
	
	public BudModifyListener(final InteractiveBud parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				go();

			}

		});

	}
	

	public void go() {

		

		parent.table.removeAll();
		parent.Tracklist.clear();
		parent.table.repaint();
		parent.table.validate();
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
		
		
		for (Map.Entry<String, ArrayList<Budpointobject>> Buds : parent.AllBudpoints.entrySet()) {
			
			
			
			
		}
		
		

	}
	
	
	
	
	
	}