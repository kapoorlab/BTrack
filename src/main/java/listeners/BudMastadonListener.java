package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fiji.plugin.trackmate.LoadTrackMatePlugIn_;
import pluginTools.InteractiveBud;

public class BudMastadonListener implements ActionListener {

	final InteractiveBud parent;
	
	public BudMastadonListener(InteractiveBud parent) {
		
		
		this.parent = parent;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		LoadTrackMatePlugIn_ plugin = new LoadTrackMatePlugIn_();
		plugin.run(null);
		
		
		
		
	}

}
