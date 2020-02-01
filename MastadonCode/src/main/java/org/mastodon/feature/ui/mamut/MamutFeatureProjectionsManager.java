package org.mastodon.feature.ui.mamut;

import static org.mastodon.feature.ui.AvailableFeatureProjectionsImp.createAvailableFeatureProjections;

import org.mastodon.feature.FeatureModel;
import org.mastodon.feature.FeatureSpecsService;
import org.mastodon.feature.ui.AvailableFeatureProjections;
import org.mastodon.feature.ui.FeatureProjectionsManager;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.revised.ui.coloring.feature.DefaultFeatureRangeCalculator;
import org.mastodon.revised.ui.coloring.feature.FeatureColorModeManager;
import org.mastodon.revised.ui.coloring.feature.FeatureProjectionId;
import org.mastodon.revised.ui.coloring.feature.FeatureRangeCalculator;
import org.mastodon.revised.ui.coloring.feature.Projections;
import org.mastodon.revised.ui.coloring.feature.ProjectionsFromFeatureModel;
import org.mastodon.revised.ui.coloring.feature.TargetType;
import org.mastodon.util.Listeners;

/**
 * Provides and up-to-date set of feature projections, as well as
 * {@code FeatureRangeCalculator}s for vertices and edges.
 * <p>
 * Used for FeatureColorModes.
 * <p>
 * This implementation feeds from a {@link Model}: It provides
 * {@code FeatureRangeCalculator} on the {@code Model}s vertices and edges. It
 * listens to changes in the {@code Model}s {@link FeatureModel} to update the
 * available feature projections.
 *
 * @author Tobias Pietzsch
 */
public class MamutFeatureProjectionsManager implements FeatureProjectionsManager
{
	private final FeatureSpecsService featureSpecsService;

	private final FeatureColorModeManager featureColorModeManager;

	private final AggregateFeatureRangeCalculator featureRangeCalculator;

	private final Listeners.List< AvailableFeatureProjectionsListener > listeners;

	private Model model;

	private int numSources = 1;

	public MamutFeatureProjectionsManager(
			final FeatureSpecsService featureSpecsService,
			final FeatureColorModeManager featureColorModeManager )
	{
		this.featureSpecsService = featureSpecsService;
		this.featureColorModeManager = featureColorModeManager;
		this.featureRangeCalculator = new AggregateFeatureRangeCalculator();
		this.listeners = new Listeners.List<>();
	}

	/**
	 * Sets the current {@code Model}. This will update the available
	 * projections and listen to the model's {@code FeatureModel}.
	 *
	 * @param model
	 *            the current {@code Model} (or {@code null}).
	 * @param numSources
	 *            the number of sources in the image data.
	 */
	public void setModel( final Model model, final int numSources )
	{
		this.model = model;
		this.numSources = Math.max( 1, numSources );

		if ( model != null )
		{
			final FeatureModel featureModel = model.getFeatureModel();
			final Projections projections = new ProjectionsFromFeatureModel( featureModel );
			featureRangeCalculator.vertexCalculator = new DefaultFeatureRangeCalculator<>( model.getGraph().vertices(), projections );
			featureRangeCalculator.edgeCalculator = new DefaultFeatureRangeCalculator<>( model.getGraph().edges(), projections );
			featureModel.listeners().add( this::notifyAvailableFeatureProjectionsChanged );
		}
		else
		{
			featureRangeCalculator.vertexCalculator = null;
			featureRangeCalculator.edgeCalculator = null;
		}

		notifyAvailableFeatureProjectionsChanged();
	}

	/**
	 * Exposes the list of listeners that are notified when a change happens to
	 */
	@Override
	public Listeners< AvailableFeatureProjectionsListener > listeners()
	{
		return listeners;
	}

	@Override
	public AvailableFeatureProjections getAvailableFeatureProjections()
	{
		final FeatureModel featureModel = ( model != null ) ? model.getFeatureModel() : null;
		return createAvailableFeatureProjections(
				featureSpecsService,
				numSources,
				featureModel,
				featureColorModeManager,
				Spot.class,
				Link.class );
	}

	private void notifyAvailableFeatureProjectionsChanged()
	{
		listeners.list.forEach( AvailableFeatureProjectionsListener::availableFeatureProjectionsChanged );
	}

	private static class AggregateFeatureRangeCalculator implements FeatureRangeCalculator
	{
		FeatureRangeCalculator vertexCalculator;

		FeatureRangeCalculator edgeCalculator;

		@Override
		public double[] computeMinMax( final FeatureProjectionId projection )
		{
			if ( projection == null )
				return null;

			if ( projection.getTargetType() == TargetType.VERTEX )
			{
				return vertexCalculator == null
						? null
						: vertexCalculator.computeMinMax( projection );
			}
			else // if ( projection.getTargetType() == TargetType.EDGE )
			{
				return edgeCalculator == null
						? null
						: edgeCalculator.computeMinMax( projection );
			}
		}
	};

	@Override
	public FeatureRangeCalculator getFeatureRangeCalculator()
	{
		return featureRangeCalculator;
	}
}
