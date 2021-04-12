package fiji.plugin.btrackmate.gui.wizard.descriptors;

import org.scijava.Cancelable;

import fiji.plugin.btrackmate.TrackMate;
import fiji.plugin.btrackmate.gui.components.LogPanel;
import fiji.plugin.btrackmate.gui.wizard.WizardPanelDescriptor;

public class ExecuteDetectionDescriptor extends WizardPanelDescriptor
{

	public static final String KEY = "ExecuteDetection";

	private final TrackMate btrackmate;
	

	public ExecuteDetectionDescriptor( final TrackMate btrackmate, final LogPanel logPanel )
	{
		super( KEY );
		this.btrackmate = btrackmate;
		
		this.targetPanel = logPanel;
		if(btrackmate.getSettings().impSeg==null)
			this.targetPanel.setEnabled(false);
		
	}

	@Override
	public Runnable getForwardRunnable()
	{
		if(btrackmate.getSettings().impSeg!=null) {
		return () -> {
			final long start = System.currentTimeMillis();
			btrackmate.execDetection(btrackmate.getSettings());
			final long end = System.currentTimeMillis();
			btrackmate.getModel().getLogger().log( String.format( "Detection done in %.1f s.\n", ( end - start ) / 1e3f ) );
		};
		}
		
		else 
		 return null;
	}

	@Override
	public Cancelable getCancelable()
	{
		return btrackmate;
	}
}
