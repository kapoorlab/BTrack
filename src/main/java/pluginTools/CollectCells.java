package pluginTools;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;


public class CollectCells extends SwingWorker<Void, Void> {
	
	
	final InteractiveBud parent;
	final JProgressBar jpb;
	
	
	public CollectCells(final InteractiveBud parent, final JProgressBar jpb) {
		
		
		this.parent = parent;
		this.jpb = jpb;
		
	}

	
	public Void doInBackground() throws Exception {
		
		
		parent.BudOvalRois.clear();
		parent.ChosenBudcenter.clear();
		
	
		
		CellTrack newtrack = new CellTrack(parent, jpb);
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
