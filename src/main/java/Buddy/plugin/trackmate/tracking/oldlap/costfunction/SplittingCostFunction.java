package Buddy.plugin.trackmate.tracking.oldlap.costfunction;

import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_ALLOW_TRACK_SPLITTING;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_BLOCKING_VALUE;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_SPLITTING_FEATURE_PENALTIES;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_SPLITTING_MAX_DISTANCE;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

import Jama.Matrix;
import budDetector.BCellobject;
import Buddy.plugin.trackmate.tracking.LAPUtils;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.multithreading.SimpleMultiThreading;

/**
 * <p>
 * Splitting cost function used with
 * {@link Buddy.plugin.trackmate.tracking.oldlap.LAPTracker}.
 * 
 * <p>
 * The <b>cost function</b> is determined by the default equation in the
 * TrackMate trackmate, see below.
 * <p>
 * It slightly differs from the Jaqaman article, see equation (5) and (6) in the
 * paper.
 * <p>
 * The <b>thresholds</b> used are:
 * <ul>
 * <li>Must be within a certain number of frames.</li>
 * <li>Must be within a certain distance.</li>
 * </ul>
 * 
 * @see LAPUtils#computeLinkingCostFor(BCellobject, BCellobject, double, double, Map)
 * @author Nicholas Perry
 * @author Jean-Yves Tinevez
 *
 */
@SuppressWarnings( "deprecation" )
public class SplittingCostFunction extends MultiThreadedBenchmarkAlgorithm implements OutputAlgorithm< Matrix >
{

	private static final boolean DEBUG = false;

	/** The distance threshold. */
	protected final double maxDist;

	/** The value used to block an assignment in the cost matrix. */
	protected final double blockingValue;

	/** Thresholds for the feature ratios. */
	protected final Map< String, Double > featurePenalties;

	private final boolean allowSplitting;

	protected final List< SortedSet< BCellobject > > trackSegments;

	protected final List< BCellobject > middlePoints;

	protected Matrix m;

	/*
	 * CONSTRUCTOR
	 */

	@SuppressWarnings( "unchecked" )
	public SplittingCostFunction( final Map< String, Object > settings, final List< SortedSet< BCellobject > > trackSegments, final List< BCellobject > middlePoints )
	{
		this.maxDist = ( Double ) settings.get( KEY_SPLITTING_MAX_DISTANCE );
		this.blockingValue = ( Double ) settings.get( KEY_BLOCKING_VALUE );
		this.featurePenalties = ( Map< String, Double > ) settings.get( KEY_SPLITTING_FEATURE_PENALTIES );
		this.allowSplitting = ( Boolean ) settings.get( KEY_ALLOW_TRACK_SPLITTING );
		this.trackSegments = trackSegments;
		this.middlePoints = middlePoints;
	}

	/*
	 * METHODS
	 */

	@Override
	public boolean process()
	{
		final long start = System.currentTimeMillis();
		if ( DEBUG )
			System.out.println( "-- DEBUG information from SplittingCostFunction --" );

		if ( !allowSplitting )
		{

			m = new Matrix( middlePoints.size(), trackSegments.size(), blockingValue );

		}
		else
		{

			// Prepare threads
			final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );

			m = new Matrix( middlePoints.size(), trackSegments.size() );

			// Prepare the thread array
			final AtomicInteger ai = new AtomicInteger( 0 );
			for ( int ithread = 0; ithread < threads.length; ithread++ )
			{

				threads[ ithread ] = new Thread( "LAPTracker splitting cost thread " + ( 1 + ithread ) + "/" + threads.length )
				{

					@Override
					public void run()
					{

						for ( int i = ai.getAndIncrement(); i < middlePoints.size(); i = ai.getAndIncrement() )
						{

							final BCellobject middle = middlePoints.get( i );

							for ( int j = 0; j < trackSegments.size(); j++ )
							{

								final SortedSet< BCellobject > track = trackSegments.get( j );
								final BCellobject lStart = track.first();

								if ( DEBUG )
									System.out.println( "Segment " + j );
								if ( track.contains( middle ) )
								{
									m.set( i, j, blockingValue );
									continue;
								}

								// Frame threshold - middle BCellobject must be one
								// frame behind of the start BCellobject
								final int startFrame = lStart.getFeature( BCellobject.POSITION_T ).intValue();
								final int middleFrame = middle.getFeature( BCellobject.POSITION_T ).intValue();
								if ( startFrame - middleFrame != 1 )
								{
									m.set( i, j, blockingValue );
									continue;
								}

								final double cost = LAPUtils.computeLinkingCostFor( lStart, middle, maxDist, blockingValue, featurePenalties );
								m.set( i, j, cost );
							}
						}
					}
				};
			}
			SimpleMultiThreading.startAndJoin( threads );
		}

		final long end = System.currentTimeMillis();
		processingTime = end - start;
		return true;
	}

	@Override
	public boolean checkInput()
	{
		return true;
	}

	@Override
	public Matrix getResult()
	{
		return m;
	}
}
