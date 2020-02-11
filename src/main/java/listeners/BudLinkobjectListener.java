package listeners;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import pluginTools.InteractiveBud;
import tracker.TrackResult;


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
		
		TrackResult track = new TrackResult(parent);
		track.execute();
		
		

	}

}