package org.mastodon.revised.trackscheme.display;

import org.mastodon.model.FocusListener;
import org.mastodon.model.FocusModel;
import org.mastodon.revised.trackscheme.LineageTreeLayout;
import org.mastodon.revised.trackscheme.ScreenTransform;
import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.util.Listeners;

import net.imglib2.RealPoint;
import net.imglib2.ui.TransformListener;

/**
 * A {@code FocusModel} for TrackScheme that automatically focuses a vertex near
 * the center of the window if none is focused (on {@code getFocusedVertex()}).
 */
public class TrackSchemeAutoFocus implements FocusModel< TrackSchemeVertex, TrackSchemeEdge >, TransformListener< ScreenTransform >
{
	private final LineageTreeLayout layout;

	private final FocusModel< TrackSchemeVertex, TrackSchemeEdge > focus;

	private final ScreenTransform screenTransform = new ScreenTransform();

	private final RealPoint centerPos = new RealPoint( 2 );

	private double ratioXtoY = 1;

	public TrackSchemeAutoFocus(
			final LineageTreeLayout layout,
			final FocusModel< TrackSchemeVertex, TrackSchemeEdge > focus )
	{
		this.layout = layout;
		this.focus = focus;
	}

	@Override
	public void focusVertex( final TrackSchemeVertex vertex )
	{
		focus.focusVertex( vertex );
	}

	@Override
	public TrackSchemeVertex getFocusedVertex( final TrackSchemeVertex ref )
	{
		TrackSchemeVertex vertex = focus.getFocusedVertex( ref );
		if ( vertex != null )
			return vertex;

		vertex = layout.getClosestActiveVertex( centerPos, ratioXtoY, ref );
		if ( vertex != null )
			focus.focusVertex( vertex );

		return vertex;
	}

	@Override
	public Listeners< FocusListener > listeners()
	{
		return focus.listeners();
	}

	@Override
	public void transformChanged( final ScreenTransform transform )
	{
		synchronized ( screenTransform )
		{
			screenTransform.set( transform );
			centerPos.setPosition( ( transform.getMaxX() + transform.getMinX() ) / 2., 0 );
			centerPos.setPosition( ( transform.getMaxY() + transform.getMinY() ) / 2., 1 );
			ratioXtoY = transform.getXtoYRatio();
		}
	}
}
