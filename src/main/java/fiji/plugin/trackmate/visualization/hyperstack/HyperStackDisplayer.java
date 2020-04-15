package fiji.plugin.trackmate.visualization.hyperstack;

import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import budDetector.BCellobject;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.ModelChangeEvent;
import fiji.plugin.trackmate.SelectionChangeEvent;
import fiji.plugin.trackmate.SelectionModel;
import fiji.plugin.trackmate.visualization.AbstractTrackMateModelView;
import fiji.plugin.trackmate.visualization.TrackColorGenerator;
import fiji.plugin.trackmate.visualization.TrackMateModelView;
import fiji.plugin.trackmate.visualization.ViewUtils;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;

public class HyperStackDisplayer extends AbstractTrackMateModelView
{

	private static final boolean DEBUG = false;

	protected final ImagePlus imp;

	protected BCellobjectOverlay BCellobjectOverlay;

	protected TrackOverlay trackOverlay;

	private BCellobjectEditTool editTool;

	private Roi initialROI;

	public static final String KEY = "HYPERSTACKDISPLAYER";

	/*
	 * CONSTRUCTORS
	 */

	public HyperStackDisplayer( final Model model, final SelectionModel selectionModel, final ImagePlus imp )
	{
		super( model, selectionModel );
		if ( null != imp )
		{
			this.imp = imp;
		}
		else
		{
			this.imp = ViewUtils.makeEmpytImagePlus( model );
		}
		this.BCellobjectOverlay = createBCellobjectOverlay();
		this.trackOverlay = createTrackOverlay();
	}

	public HyperStackDisplayer( final Model model, final SelectionModel selectionModel )
	{
		this( model, selectionModel, null );
	}

	/*
	 * PROTECTED METHODS
	 */

	/**
	 * Hook for subclassers. Instantiate here the overlay you want to use for
	 * the BCellobjects.
	 *
	 * @return the BCellobject overlay
	 */
	protected BCellobjectOverlay createBCellobjectOverlay()
	{
		return new BCellobjectOverlay( model, imp, displaySettings );
	}

	/**
	 * Hook for subclassers. Instantiate here the overlay you want to use for
	 * the BCellobjects.
	 *
	 * @return the track overlay
	 */
	protected TrackOverlay createTrackOverlay()
	{
		final TrackOverlay to = new TrackOverlay( model, imp, displaySettings );
		final TrackColorGenerator colorGenerator = ( TrackColorGenerator ) displaySettings.get( KEY_TRACK_COLORING );
		to.setTrackColorGenerator( colorGenerator );
		return to;
	}

	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Exposes the {@link ImagePlus} on which the model is drawn by this view.
	 *
	 * @return the ImagePlus used in this view.
	 */
	public ImagePlus getImp()
	{
		return imp;
	}

	@Override
	public void modelChanged( final ModelChangeEvent event )
	{
		if ( DEBUG )
			System.out.println( "[HyperStackDisplayer] Received model changed event ID: " + event.getEventID() + " from " + event.getSource() );
		boolean redoOverlay = false;

		switch ( event.getEventID() )
		{

		case ModelChangeEvent.MODEL_MODIFIED:
			// Rebuild track overlay only if edges were added or removed, or if
			// at least one BCellobject was removed.
			final Set< DefaultWeightedEdge > edges = event.getEdges();
			if ( edges != null && edges.size() > 0 )
			{
				redoOverlay = true;
			}
			break;

		case ModelChangeEvent.BCellobject_FILTERED:
			redoOverlay = true;
			break;

		case ModelChangeEvent.BCellobject_COMPUTED:
			redoOverlay = true;
			break;

		case ModelChangeEvent.TRACKS_VISIBILITY_CHANGED:
		case ModelChangeEvent.TRACKS_COMPUTED:
			redoOverlay = true;
			break;
		}

		if ( redoOverlay )
			refresh();
	}

	@Override
	public void selectionChanged( final SelectionChangeEvent event )
	{
		// Highlight selection
		trackOverlay.setHighlight( selectionModel.getEdgeSelection() );
		BCellobjectOverlay.setBCellobjectSelection( selectionModel.getBCellobjectSelection() );
		// Center on last BCellobject
		super.selectionChanged( event );
		// Redraw
		imp.updateAndDraw();
	}

	@Override
	public void centerViewOn( final BCellobject BCellobject )
	{
		final int frame = BCellobject.getFeature( BCellobject.POSITION_T ).intValue();
		final double dz = imp.getCalibration().pixelDepth;
		imp.setPosition( imp.getC(),0, frame + 1 );
	}

	@Override
	public void render()
	{
		initialROI = imp.getRoi();
		if ( initialROI != null )
		{
			imp.killRoi();
		}

		clear();
		imp.setOpenAsHyperStack( true );
		if ( !imp.isVisible() )
		{
			imp.show();
		}

		addOverlay( BCellobjectOverlay );
		addOverlay( trackOverlay );
		imp.updateAndDraw();
		registerEditTool();
	}

	@Override
	public void refresh()
	{
		if ( null != imp )
		{
			imp.updateAndDraw();
		}
	}

	@Override
	public void clear()
	{
		Overlay overlay = imp.getOverlay();
		if ( overlay == null )
		{
			overlay = new Overlay();
			imp.setOverlay( overlay );
		}
		overlay.clear();
		if ( initialROI != null )
		{
			imp.getOverlay().add( initialROI );
		}
		refresh();
	}

	public void addOverlay( final Roi overlay )
	{
		imp.getOverlay().add( overlay );
	}

	public SelectionModel getSelectionModel()
	{
		return selectionModel;
	}

	/*
	 * PRIVATE METHODS
	 */

	private void registerEditTool()
	{
		editTool = BCellobjectEditTool.getInstance();
		if ( !BCellobjectEditTool.isLaunched() )
		{
			editTool.run( "" );
		}
		editTool.register( imp, this );
	}

	@Override
	public void setDisplaySettings( final String key, final Object value )
	{
		boolean dorefresh = false;

		if ( key == TrackMateModelView.KEY_BCellobject_COLORING || key == TrackMateModelView.KEY_LIMIT_DRAWING_DEPTH || key == KEY_DRAWING_DEPTH )
		{
			dorefresh = true;

		}
		else if ( key == TrackMateModelView.KEY_TRACK_COLORING )
		{
			// pass the new one to the track overlay - we ignore its BCellobject
			// coloring and keep the BCellobject coloring
			final TrackColorGenerator colorGenerator = ( TrackColorGenerator ) value;
			trackOverlay.setTrackColorGenerator( colorGenerator );
			dorefresh = true;
		}

		super.setDisplaySettings( key, value );
		if ( dorefresh )
		{
			refresh();
		}
	}

	@Override
	public String getKey()
	{
		return KEY;
	}


}
