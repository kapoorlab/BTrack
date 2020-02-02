package fiji.plugin.trackmate.features.spot;

import static fiji.plugin.trackmate.features.spot.SpotContrastAnalyzerFactory.KEY;

import java.util.Iterator;

import net.imagej.ImgPlus;
import net.imglib2.type.numeric.RealType;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.util.SpotNeighborhood;
import fiji.plugin.trackmate.util.SpotNeighborhoodCursor;

public class SpotContrastAnalyzer< T extends RealType< T >> extends IndependentSpotFeatureAnalyzer< T >
{

	protected static final double RAD_PERCENTAGE = .5f;

	public SpotContrastAnalyzer( final ImgPlus< T > img, final Iterator< Spot > spots )
	{
		super( img, spots );
	}

	@Override
	public final void process( final Spot spot )
	{
		final double contrast = getContrast( spot );
		spot.putFeature( KEY, Math.abs( contrast ) );
	}

	/**
	 * Compute the contrast for the given spot.
	 * 
	 * @param spot the spot to measure.
	 * @return the contrast.
	 */
	private final double getContrast( final Spot spot )
	{

		final SpotNeighborhood< T > neighborhood = new SpotNeighborhood<>( spot, img );

		final double radius = spot.getFeature( Spot.RADIUS );
		long innerRingVolume = 0;
		long outerRingVolume = 0;
		final double radius2 = radius * radius;
		final double innerRadius2 = radius2 * ( 1 - RAD_PERCENTAGE ) * ( 1 - RAD_PERCENTAGE );
		double innerTotalIntensity = 0;
		double outerTotalIntensity = 0;
		double dist2;

		final SpotNeighborhoodCursor< T > cursor = neighborhood.cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			dist2 = cursor.getDistanceSquared();
			if ( dist2 > radius2 )
			{
				outerRingVolume++;
				outerTotalIntensity += cursor.get().getRealDouble();
			}
			else if ( dist2 > innerRadius2 )
			{
				innerRingVolume++;
				innerTotalIntensity += cursor.get().getRealDouble();
			}
		}

		final double innerMeanIntensity = innerTotalIntensity / innerRingVolume;
		final double outerMeanIntensity = outerTotalIntensity / outerRingVolume;
		return innerMeanIntensity - outerMeanIntensity;
	}
}
