package listeners;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import net.imglib2.RealLocalizable;
import pluginTools.BoundaryTrack;
import pluginTools.InteractiveBud;
import tracker.BUDDYTrackResult;


public class BudLinkobjectListener implements ActionListener {
	
	final InteractiveBud parent;
	
	public BudLinkobjectListener(final InteractiveBud parent) {
		
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

		
		parent.Tracklist.clear();
		parent.Finalresult.clear();
		parent.table.repaint();
		parent.table.validate();
		parent.panelFirst.repaint();
		parent.panelFirst.validate();
		
		BUDDYTrackResult track = new BUDDYTrackResult(parent);
		track.execute();
		
		parent.Cellbutton.setEnabled(true);

	}

}