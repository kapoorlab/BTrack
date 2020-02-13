package listeners;

import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

import pluginTools.BoundaryTrack;
import pluginTools.InteractiveBud;
import pluginTools.InteractiveBud.ValueChange;
import skeleton.SkeletonizeBuds;
import tracker.TrackResult;
import utility.BudShowView;

public class BudSkeletonListener implements ActionListener {
	
	final InteractiveBud parent;
	
	public BudSkeletonListener(final InteractiveBud parent) {
		
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

		SkeletonizeBuds skeleton = new SkeletonizeBuds(parent);
		skeleton.execute();
		
		

	}
}