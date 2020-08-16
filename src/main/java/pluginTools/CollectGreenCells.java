package pluginTools;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;


public class CollectGreenCells extends SwingWorker<Void, Void> {
	
	
	final InteractiveGreen parent;
	final JProgressBar jpb;
	
	
	public CollectGreenCells(final InteractiveGreen parent, final JProgressBar jpb) {
		
		
		this.parent = parent;
		this.jpb = jpb;
		
	}

	
	public Void doInBackground() throws Exception {
		
		
		parent.Greencells.clear();
		
	
		
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
		
	}
	
}
