package pluginTools;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import Buddy.plugin.trackmate.TrackMatePlugIn_;


public class CollectGreenCells extends SwingWorker<Void, Void> {
	
	
	final InteractiveBud parent;
	final JProgressBar jpb;
	
	
	public CollectGreenCells(final InteractiveBud parent, final JProgressBar jpb) {
		
		
		this.parent = parent;
		this.jpb = jpb;
		
	}

	
	public Void doInBackground() throws Exception {
		
		
		
		GreenCellTrack newtrack = new GreenCellTrack(parent, jpb);
		newtrack.ShowCellTime();
		
		
		
		return null;
		
	}
	@Override
	protected void done() {
		
		parent.jpb.setIndeterminate(false);
		if(parent.jpb!=null )
			utility.BudProgressBar.SetProgressBar(parent.jpb, 100 ,
					"Collected all cells, starting TrackMate");
		
		
		TrackMatePlugIn_ plugin = new TrackMatePlugIn_(parent);
		plugin.run("threeD");
		
	}
	
}
