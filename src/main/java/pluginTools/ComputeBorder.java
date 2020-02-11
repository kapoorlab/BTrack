package pluginTools;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class ComputeBorder extends SwingWorker<Void, Void> {
	
	
	final InteractiveBud parent;
	final JProgressBar jpb;
	
	
	public ComputeBorder(final InteractiveBud parent, final JProgressBar jpb) {
		
		
		this.parent = parent;
		this.jpb = jpb;
		
	}

	
	public Void doInBackground() throws Exception {
		
		BoundaryTrack newtrack = new BoundaryTrack(parent, jpb);
		newtrack.ShowBoundary();
		
		
		return null;
		
	}
	@Override
	protected void done() {
		
		parent.jpb.setIndeterminate(false);
		if(parent.jpb!=null )
			utility.BudProgressBar.SetProgressBar(parent.jpb, 100 ,
					"Bud endpoints computed for all buds present at timepoint " + parent.thirdDimension);
		
	}
	
}
