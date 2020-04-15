package fiji.plugin.trackmate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.scijava.util.VersionUtils;

import budDetector.BCellobject;
import fiji.plugin.trackmate.detection.BCellobjectDetector;
import fiji.plugin.trackmate.detection.BCellobjectDetectorFactory;
import fiji.plugin.trackmate.detection.ManualDetectorFactory;
import fiji.plugin.trackmate.features.BCellobjectFeatureCalculator;
import fiji.plugin.trackmate.features.EdgeFeatureCalculator;
import fiji.plugin.trackmate.features.FeatureFilter;
import fiji.plugin.trackmate.features.TrackFeatureCalculator;
import fiji.plugin.trackmate.tracking.BCellobjectTracker;
import fiji.plugin.trackmate.util.TMUtils;
import ij.gui.ShapeRoi;
import net.imagej.ImgPlus;
import net.imglib2.Interval;
import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.multithreading.SimpleMultiThreading;

/**
 * <p>
 * The TrackMate_ class runs on the currently active time-lapse image (2D or 3D)
 * and both identifies and tracks bright BCellobjects over time.
 * </p>
 *
 * <p>
 * <b>Required input:</b> A 2D or 3D time-lapse image with bright blobs.
 * </p>
 *
 * @author Nicholas Perry
 * @author Johannes Schindelin
 * @author Jean-Yves Tinevez - Institut Pasteur - July 2010 - 2018
 */
@SuppressWarnings( "deprecation" )
public class TrackMate implements Benchmark, MultiThreaded, Algorithm
{

	public static final String PLUGIN_NAME_STR = "TrackMate";

	public static final String PLUGIN_NAME_VERSION = VersionUtils.getVersion( TrackMate.class );

	/**
	 * The model this trackmate will shape.
	 */
	protected final Model model;

	protected final Settings settings;

	protected long processingTime;

	protected String errorMessage;

	protected int numThreads = Runtime.getRuntime().availableProcessors();

	/*
	 * CONSTRUCTORS
	 */

	public TrackMate( final Settings settings )
	{
		this( new Model(), settings );
	}

	public TrackMate( final Model model, final Settings settings )
	{
		this.model = model;
		this.settings = settings;
	}

	public TrackMate()
	{
		this( new Model(), new Settings() );
	}

	/*
	 * PROTECTED METHODS
	 */

	/**
	 * This method exists for the following reason:
	 * <p>
	 * The detector receives at each frame a cropped image to operate on,
	 * depending on the user specifying a ROI. It therefore returns BCellobjects whose
	 * coordinates are with respect to the top-left corner of the ROI, not of
	 * the original image.
	 * <p>
	 * This method modifies the given BCellobjects to put them back in the image
	 * coordinate system. Additionally, is a non-square ROI was specified (e.g.
	 * a polygon), it prunes the BCellobjects that are not within the polygon of the
	 * ROI.
	 *
	 * @param BCellobjectsThisFrame
	 *            the BCellobject list to inspect
	 * @param lSettings
	 *            the {@link Settings} object that will be used to retrieve the
	 *            image ROI and cropping information
	 * @return a list of BCellobject. Depending on the presence of a polygon ROI, it
	 *         might be a new, pruned list. Or not.
	 */
	protected List< BCellobject > translateAndPruneBCellobjects( final List< BCellobject > BCellobjectsThisFrame, final Settings lSettings )
	{

		// Put them back in the right referential
		final double[] calibration = TMUtils.getSpatialCalibration( lSettings.imp );
		TMUtils.translateBCellobjects( BCellobjectsThisFrame, lSettings.xstart * calibration[ 0 ], lSettings.ystart * calibration[ 1 ], lSettings.zstart * calibration[ 2 ] );
		List< BCellobject > prunedBCellobjects;
		// Prune if outside of ROI
		if ( lSettings.roi instanceof ShapeRoi )
		{
			prunedBCellobjects = new ArrayList<>();
			for ( final BCellobject BCellobject : BCellobjectsThisFrame )
			{
				if ( lSettings.roi.contains( (int) Math.round( BCellobject.getFeature( BCellobject.POSITION_X ) / calibration[ 0 ] ), (int) Math.round( BCellobject.getFeature( BCellobject.POSITION_Y ) / calibration[ 1 ] ) ) )
					prunedBCellobjects.add( BCellobject );
			}
		}
		else if ( null != lSettings.polygon )
		{
			prunedBCellobjects = new ArrayList<>();
			for ( final BCellobject BCellobject : BCellobjectsThisFrame )
			{
				if ( lSettings.polygon.contains( BCellobject.getFeature( BCellobject.POSITION_X ) / calibration[ 0 ], BCellobject.getFeature( BCellobject.POSITION_Y ) / calibration[ 1 ] ) )
					prunedBCellobjects.add( BCellobject );
			}
		}
		else
		{
			prunedBCellobjects = BCellobjectsThisFrame;
		}
		return prunedBCellobjects;
	}

	/*
	 * METHODS
	 */

	public Model getModel()
	{
		return model;
	}

	public Settings getSettings()
	{
		return settings;
	}

	/*
	 * PROCESSES
	 */

	/**
	 * Calculate all features for all detected BCellobjects.
	 * <p>
	 * Features are calculated for each BCellobject, using their location, and the raw
	 * image. Features to be calculated and analyzers are taken from the
	 * settings field of this object.
	 *
	 * @param doLogIt
	 *            if <code>true</code>, the {@link Logger} of the model will be
	 *            notified.
	 * @return <code>true</code> if the calculation was performed successfully,
	 *         <code>false</code> otherwise.
	 */
	public boolean computeBCellobjectFeatures( final boolean doLogIt )
	{
		final Logger logger = model.getLogger();
		logger.log( "Computing BCellobject features.\n" );
		final BCellobjectFeatureCalculator calculator = new BCellobjectFeatureCalculator( model, settings );
		calculator.setNumThreads( numThreads );
		if ( calculator.checkInput() && calculator.process() )
		{
			if ( doLogIt )
				logger.log( "Computation done in " + calculator.getProcessingTime() + " ms.\n" );
			return true;
		}

		errorMessage = "BCellobject features calculation failed:\n" + calculator.getErrorMessage();
		return false;
	}

	/**
	 * Calculate all features for all detected BCellobjects.
	 * <p>
	 * Features are calculated for each BCellobject, using their location, and the raw
	 * image. Features to be calculated and analyzers are taken from the
	 * settings field of this object.
	 *
	 * @param doLogIt
	 *            if <code>true</code>, the {@link Logger} of the model will be
	 *            notified.
	 * @return <code>true</code> if the calculation was performed successfuly,
	 *         <code>false</code> otherwise.
	 */
	public boolean computeEdgeFeatures( final boolean doLogIt )
	{
		final Logger logger = model.getLogger();
		final EdgeFeatureCalculator calculator = new EdgeFeatureCalculator( model, settings );
		calculator.setNumThreads( numThreads );
		if ( !calculator.checkInput() || !calculator.process() )
		{
			errorMessage = "Edge features calculation failed:\n" + calculator.getErrorMessage();
			return false;
		}
		if ( doLogIt )
		{
			logger.log( "Computation done in " + calculator.getProcessingTime() + " ms.\n" );
		}
		return true;
	}

	/**
	 * Calculate all features for all tracks.
	 *
	 * @param doLogIt
	 *            if <code>true</code>, messages will be sent to the logger.
	 * @return <code>true</code> if the computation completed without errors.
	 */
	public boolean computeTrackFeatures( final boolean doLogIt )
	{
		final Logger logger = model.getLogger();
		final TrackFeatureCalculator calculator = new TrackFeatureCalculator( model, settings );
		calculator.setNumThreads( numThreads );
		if ( calculator.checkInput() && calculator.process() )
		{
			if ( doLogIt )
				logger.log( "Computation done in " + calculator.getProcessingTime() + " ms.\n" );
			return true;
		}

		errorMessage = "Track features calculation failed:\n" + calculator.getErrorMessage();
		return false;
	}

	/**
	 * Execute the tracking part.
	 * <p>
	 * This method links all the selected BCellobjects from the thresholding part using
	 * the selected tracking algorithm. This tracking process will generate a
	 * graph (more precisely a {@link org.jgrapht.graph.SimpleWeightedGraph})
	 * made of the BCellobject election for its vertices, and edges representing the
	 * links.
	 * <p>
	 * The {@link ModelChangeListener}s of the model will be notified when the
	 * successful process is over.
	 *
	 * @return <code>true</code> if the computation completed without errors.
	 */
	public boolean execTracking()
	{
		final Logger logger = model.getLogger();
		logger.log( "Starting tracking process.\n" );
		final BCellobjectTracker tracker = settings.trackerFactory.create( model.getBCellobjects(), settings.trackerSettings );
		tracker.setNumThreads( numThreads );
		tracker.setLogger( logger );
		if ( tracker.checkInput() && tracker.process() )
		{
			model.setTracks( tracker.getResult(), true );
			return true;
		}

		errorMessage = "Tracking process failed:\n" + tracker.getErrorMessage();
		return false;
	}

	/**
	 * Execute the detection part.
	 * <p>
	 * This method configure the chosen {@link Settings#detectorFactory} with
	 * the source image and the detectr settings and execute the detection
	 * process for all the frames set in the {@link Settings} object of the
	 * target model.
	 *
	 * @return true if the whole detection step has executed correctly.
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public boolean execDetection()
	{
		final Logger logger = model.getLogger();
		logger.log( "Starting detection process using "
				+ ( ( numThreads > 1 ) ? ( numThreads + " threads" ) : "1 thread" )
				+ ".\n" );

		final BCellobjectDetectorFactory< ? > factory = settings.detectorFactory;
		if ( null == factory )
		{
			errorMessage = "Detector factory is null.\n";
			return false;
		}
		if ( null == settings.detectorSettings )
		{
			errorMessage = "Detector settings is null.\n";
			return false;
		}
		if (factory instanceof ManualDetectorFactory)
		{
			// Skip detection (don't delete anything) if we received this factory.
			return true;
		}

		/*
		 * Prepare interval
		 */
		final ImgPlus img = TMUtils.rawWraps( settings.imp );
		final Interval interval = TMUtils.getInterval( img, settings );
		final int zindex = TMUtils.findZAxisIndex( img );

		factory.setTarget( img, settings.detectorSettings );

		final int numFrames = settings.tend - settings.tstart + 1;
		// Final results holder, for all frames
		final BCellobjectCollection BCellobjects = new BCellobjectCollection();
		BCellobjects.setNumThreads( numThreads );
		// To report progress
		final AtomicInteger BCellobjectFound = new AtomicInteger( 0 );
		final AtomicInteger progress = new AtomicInteger( 0 );
		// To translate BCellobjects, later
		final double[] calibration = TMUtils.getSpatialCalibration( settings.imp );

		/*
		 * Fine tune multi-threading: If we have 10 threads and 15 frames to
		 * process, we process 10 frames at once, and allocate 1 thread per
		 * frame. But if we have 10 threads and 2 frames, we process the 2
		 * frames at once, and allocate 5 threads per frame if we can.
		 */
		final int nSimultaneousFrames = Math.min( numThreads, numFrames );
		final int threadsPerFrame = Math.max( 1, numThreads / nSimultaneousFrames );

		logger.log( "Detection processes "
				+ ( ( nSimultaneousFrames > 1 ) ? ( nSimultaneousFrames + " frames" ) : "1 frame" )
				+ " simultaneously and allocates "
				+ ( ( threadsPerFrame > 1 ) ? ( threadsPerFrame + " threads" ) : "1 thread" )
				+ " per frame.\n" );

		final Thread[] threads = SimpleMultiThreading.newThreads( nSimultaneousFrames );
		final AtomicBoolean ok = new AtomicBoolean( true );

		// Prepare the thread array
		final AtomicInteger ai = new AtomicInteger( settings.tstart );
		for ( int ithread = 0; ithread < threads.length; ithread++ )
		{

			threads[ ithread ] = new Thread( "TrackMate BCellobject detection thread " + ( 1 + ithread ) + "/" + threads.length )
			{
				private boolean wasInterrupted()
				{
					try
					{
						if ( isInterrupted() )
							return true;
						sleep( 0 );
						return false;
					}
					catch ( final InterruptedException e )
					{
						return true;
					}
				}

				@Override
				public void run()
				{

					for ( int frame = ai.getAndIncrement(); frame <= settings.tend; frame = ai.getAndIncrement() )
						try
						{
							// Yield detector for target frame
							final BCellobjectDetector< ? > detector = factory.getDetector( interval, frame );
							if ( detector instanceof MultiThreaded )
							{
								final MultiThreaded md = ( MultiThreaded ) detector;
								md.setNumThreads( threadsPerFrame );
							}

							if ( wasInterrupted() )
								return;

							// Execute detection
							if ( ok.get() && detector.checkInput() && detector.process() )
							{
								// On success, get results.
								final List< BCellobject > BCellobjectsThisFrame = detector.getResult();

								/*
								 * Special case: if we have a single column
								 * image, then the detectors internally dealt
								 * with a single line image. We need to permute
								 * back the X & Y coordinates if it's the case.
								 */
								if ( img.dimension( 0 ) < 2 && zindex < 0 )
								{
									for ( final BCellobject BCellobject : BCellobjectsThisFrame )
									{
										BCellobject.putFeature( BCellobject.POSITION_Y, BCellobject.getDoublePosition( 0 ) );
										BCellobject.putFeature( BCellobject.POSITION_X, 0d );
									}
								}

								List< BCellobject > prunedBCellobjects;
								if ( settings.roi instanceof ShapeRoi )
								{
									prunedBCellobjects = new ArrayList<>();
									for ( final BCellobject BCellobject : BCellobjectsThisFrame )
									{
										if ( settings.roi.contains( (int) Math.round( BCellobject.getFeature( BCellobject.POSITION_X ) / calibration[ 0 ] ), (int) Math.round( BCellobject.getFeature( BCellobject.POSITION_Y ) / calibration[ 1 ] ) ) )
											prunedBCellobjects.add( BCellobject );
									}
								}
								else if ( null != settings.polygon )
								{
									prunedBCellobjects = new ArrayList< >();
									for ( final BCellobject BCellobject : BCellobjectsThisFrame )
									{
										if ( settings.polygon.contains( BCellobject.getFeature( BCellobject.POSITION_X ) / calibration[ 0 ], BCellobject.getFeature( BCellobject.POSITION_Y ) / calibration[ 1 ] ) )
											prunedBCellobjects.add( BCellobject );
									}
								}
								else
								{
									prunedBCellobjects = BCellobjectsThisFrame;
								}
								// Add detection feature other than position
								for ( final BCellobject BCellobject : prunedBCellobjects )
								{
									// FRAME will be set upon adding to
									// BCellobjectCollection.
									BCellobject.putFeature( BCellobject.POSITION_T, frame * settings.dt );
								}
								// Store final results for this frame
								BCellobjects.put( frame, prunedBCellobjects );
								// Report
								BCellobjectFound.addAndGet( prunedBCellobjects.size() );
								logger.setProgress( progress.incrementAndGet() / ( double ) numFrames );

							}
							else
							{
								// Fail: exit and report error.
								ok.set( false );
								errorMessage = detector.getErrorMessage();
								return;
							}

						}
						catch ( final RuntimeException e )
						{
							final Throwable cause = e.getCause();
							if ( cause != null && cause instanceof InterruptedException ) { return; }
							throw e;
						}
				}
			};
		}

		logger.setStatus( "Detection..." );
		logger.setProgress( 0 );

		try
		{
			SimpleMultiThreading.startAndJoin( threads );
		}
		catch ( final RuntimeException e )
		{
			ok.set( false );
			if ( e.getCause() != null && e.getCause() instanceof InterruptedException )
			{
				errorMessage = "Detection workers interrupted.\n";
				for ( final Thread thread : threads )
					thread.interrupt();
				for ( final Thread thread : threads )
				{
					if ( thread.isAlive() )
						try
						{
							thread.join();
						}
						catch ( final InterruptedException e2 )
						{
							// ignore
						}
				}
			}
			else
			{
				throw e;
			}
		}
		model.setBCellobjects( BCellobjects, true );

		if ( ok.get() )
		{
			logger.log( "Found " + BCellobjectFound.get() + " BCellobjects.\n" );
		}
		else
		{
			logger.error( "Detection failed after " + progress.get() + " frames:\n" + errorMessage );
			logger.log( "Found " + BCellobjectFound.get() + " BCellobjects prior failure.\n" );
		}
		logger.setProgress( 1 );
		logger.setStatus( "" );
		return ok.get();
	}

	/**
	 * Execute the initial BCellobject filtering part.
	 * <p>
	 * Because of the presence of noise, it is possible that some of the
	 * regional maxima found in the detection step have identified noise, rather
	 * than objects of interest. This can generates a very high number of BCellobjects,
	 * which is inconvenient to deal with when it comes to computing their
	 * features, or displaying them.
	 * <p>
	 * Any {@link BCellobjectDetector} is expected to at least compute the
	 * {@link BCellobject#QUALITY} value for each BCellobject it creates, so it is possible to
	 * set up an initial filtering on this feature, prior to any other
	 * operation.
	 * <p>
	 * This method simply takes all the detected BCellobjects, and discard those whose
	 * quality value is below the threshold set by
	 * {@link Settings#initialBCellobjectFilterValue}. The BCellobject field is overwritten,
	 * and discarded BCellobjects can't be recalled.
	 * <p>
	 * The {@link ModelChangeListener}s of this model will be notified with a
	 * {@link ModelChangeEvent#BCellobjectS_COMPUTED} event.
	 *
	 * @return <code>true</code> if the computation completed without errors.
	 */
	public boolean execInitialBCellobjectFiltering()
	{
		final Logger logger = model.getLogger();
		logger.log( "Starting initial filtering process.\n" );

		final Double initialBCellobjectFilterValue = settings.initialBCellobjectFilterValue;

		BCellobjectCollection BCellobjects = model.getBCellobjects();
		BCellobjects = BCellobjects.crop();

		model.setBCellobjects( BCellobjects, true ); // Forget about the previous one
		return true;
	}

	/**
	 * Execute the BCellobject feature filtering part.
	 * <p>
	 * Because of the presence of noise, it is possible that some of the
	 * regional maxima found in the detection step have identified noise, rather
	 * than objects of interest. A filtering operation based on the calculated
	 * features in this step should allow to rule them out.
	 * <p>
	 * This method simply takes all the detected BCellobjects, and mark as visible the
	 * BCellobjects whose features satisfy all of the filters in the {@link Settings}
	 * object.
	 * <p>
	 * The {@link ModelChangeListener}s of this model will be notified with a
	 * {@link ModelChangeEvent#BCellobjectS_FILTERED} event.
	 *
	 * @param doLogIt
	 *            if <code>true</code>, will send a message to the model logger.
	 * @return <code>true</code> if the computation completed without errors.
	 */
	public boolean execBCellobjectFiltering( final boolean doLogIt )
	{
		if ( doLogIt )
		{
			final Logger logger = model.getLogger();
			logger.log( "Starting BCellobject filtering process.\n" );
		}
		model.filterBCellobjects( settings.getBCellobjectFilters(), true );
		return true;
	}

	public boolean execTrackFiltering( final boolean doLogIt )
	{
		if ( doLogIt )
		{
			final Logger logger = model.getLogger();
			logger.log( "Starting track filtering process.\n" );
		}

		model.beginUpdate();
		try
		{
			for ( final Integer trackID : model.getTrackModel().trackIDs( false ) )
			{
				boolean trackIsOk = true;
				for ( final FeatureFilter filter : settings.getTrackFilters() )
				{
					final Double tval = filter.value;
					final Double val = model.getFeatureModel().getTrackFeature( trackID, filter.feature );
					if ( null == val )
						continue;

					if ( filter.isAbove )
					{
						if ( val < tval )
						{
							trackIsOk = false;
							break;
						}
					}
					else
					{
						if ( val > tval )
						{
							trackIsOk = false;
							break;
						}
					}
				}
				model.setTrackVisibility( trackID, trackIsOk );
			}
		}
		finally
		{
			model.endUpdate();
		}
		return true;
	}

	@Override
	public String toString()
	{
		return PLUGIN_NAME_STR + "v" + PLUGIN_NAME_VERSION;
	}

	/*
	 * ALGORITHM METHODS
	 */

	@Override
	public boolean checkInput()
	{
		if ( null == model )
		{
			errorMessage = "The model is null.\n";
			return false;
		}
		if ( null == settings )
		{
			errorMessage = "Settings are null";
			return false;
		}
		if ( !settings.checkValidity() )
		{
			errorMessage = settings.getErrorMessage();
			return false;
		}
		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

	@Override
	public boolean process()
	{
		if ( !execDetection() ) { return false; }

		if ( !execInitialBCellobjectFiltering() ) { return false; }

		if ( !computeBCellobjectFeatures( true ) ) { return false; }

		if ( !execBCellobjectFiltering( true ) ) { return false; }

		if ( !execTracking() ) { return false; }

		if ( !computeTrackFeatures( true ) ) { return false; }

		if ( !execTrackFiltering( true ) ) { return false; }

		if ( !computeEdgeFeatures( true ) ) { return false; }

		return true;
	}

	@Override
	public int getNumThreads()
	{
		return numThreads;
	}

	@Override
	public void setNumThreads()
	{
		this.numThreads = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public void setNumThreads( final int numThreads )
	{
		this.numThreads = numThreads;
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}
}
