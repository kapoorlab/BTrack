package fiji.plugin.trackmate.action;

import java.util.Set;

import javax.swing.ImageIcon;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.scijava.plugin.Plugin;

import budDetector.BCellobject;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.TrackModel;
import fiji.plugin.trackmate.gui.TrackMateGUIController;
import fiji.plugin.trackmate.gui.TrackMateWizard;
import net.imglib2.RealPoint;

/**
 * This action allows to close gaps in tracks by creating new intermediate BCellobjects
 * which are located at interpolated positions. This is useful if you want to
 * measure signal intensity changing during time, even if the BCellobject is not
 * visible. Thus, trackmate is utilisable for Fluorescence Recovery after
 * Photobleaching (FRAP) analysis.
 *
 * Author: Robert Haase, Scientific Computing Facility, MPI-CBG,
 * rhaase@mpi-cbg.de
 *
 * Date: June 2016
 *
 */
public class CloseGapsByLinearInterpolationAction extends AbstractTMAction
{

	public static final ImageIcon ICON = new ImageIcon( TrackMateWizard.class.getResource( "images/BCellobject_icon.png" ) );

	public static final String NAME = "Close gaps by introducing new BCellobjects";

	public static final String KEY = "CLOSE_GAPS_BY_LINEAR_INPERPOLATION";

	public static final String INFO_TEXT = "<html>" 
			+ "This action closes gaps in tracks by introducing new BCellobjects. "
			+ "The BCellobjects positions and size are calculated "
			+ "using linear interpolation." 
			+ "</html>";

	@Override
	public void execute( final TrackMate trackmate )
	{
		final Model model = trackmate.getModel();

		final TrackModel trackModel = model.getTrackModel();

		boolean changed = true;

		while ( changed )
		{
			changed = false;

			// Got through all edges, check if the LOCATION_T distance between BCellobjects
			// is larger than 1
			final Set< DefaultWeightedEdge > edges = model.getTrackModel().edgeSet();
			for ( final DefaultWeightedEdge edge : edges )
			{
				final BCellobject currentBCellobject = trackModel.getEdgeSource( edge );
				final BCellobject nextBCellobject = trackModel.getEdgeTarget( edge );

				final int currentLOCATION_T = currentBCellobject.getFeature( BCellobject.POSITION_T ).intValue();
				final int nextLOCATION_T = nextBCellobject.getFeature( BCellobject.POSITION_T ).intValue();

				if ( Math.abs( nextLOCATION_T - currentLOCATION_T ) > 1 )
				{
					final int presign = nextLOCATION_T > currentLOCATION_T ? 1 : -1;

					model.beginUpdate();

					final double[] currentPosition = new double[ 3 ];
					final double[] nextPosition = new double[ 3 ];

					nextBCellobject.localize( nextPosition );
					currentBCellobject.localize( currentPosition );

					model.removeEdge( currentBCellobject, nextBCellobject );

					// create new BCellobjects in between; interpolate coordinates and
					// some features
					BCellobject formerBCellobject = currentBCellobject;
					for ( int f = currentLOCATION_T + presign; ( f < nextLOCATION_T && presign == 1 ) 
							|| ( f > nextLOCATION_T && presign == -1 ); f += presign )
					{
						final double weight = ( double ) ( nextLOCATION_T - f ) / ( nextLOCATION_T - currentLOCATION_T );

						final double[] position = new double[ 3 ];
						for ( int d = 0; d < currentBCellobject.numDimensions(); d++ )
						{
							position[ d ] = weight * currentPosition[ d ] + ( 1.0 - weight ) * nextPosition[ d ];
						}

						final RealPoint rp = new RealPoint( position );


					}
					model.addEdge( formerBCellobject, nextBCellobject, 1.0 );
					model.endUpdate();

					// Restart search to prevent ConcurrentModificationException
					changed = true;
					break;
				}
			}
		}
	}

	private void interpolateFeature( final BCellobject targetBCellobject, final BCellobject BCellobject1, final BCellobject BCellobject2, final double weight, final String feature )
	{
		if ( targetBCellobject.getFeatures().containsKey( feature ) )
		{
			targetBCellobject.getFeatures().remove( feature );
		}

		targetBCellobject.getFeatures().put( feature, 
				weight * BCellobject1.getFeature( feature ) + ( 1.0 - weight ) * BCellobject2.getFeature( feature ) );
	}

	@Plugin( type = TrackMateActionFactory.class )
	public static class Factory implements TrackMateActionFactory
	{

		@Override
		public String getInfoText()
		{
			return INFO_TEXT;
		}

		@Override
		public String getName()
		{
			return NAME;
		}

		@Override
		public String getKey()
		{
			return KEY;
		}



		@Override
		public TrackMateAction create( final TrackMateGUIController controller )
		{
			return new CloseGapsByLinearInterpolationAction();
		}

		@Override
		public ImageIcon getIcon() {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
