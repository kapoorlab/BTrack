/**
 *
 */
package org.mastodon.revised.trackscheme.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphChangeNotifier;
import org.mastodon.graph.Vertex;
import org.mastodon.revised.mamut.KeyConfigContexts;
import org.mastodon.revised.trackscheme.ScreenTransform;
import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.revised.ui.keymap.CommandDescriptionProvider;
import org.mastodon.revised.ui.keymap.CommandDescriptions;
import org.mastodon.spatial.HasTimepoint;
import org.mastodon.undo.UndoPointMarker;
import org.scijava.plugin.Plugin;
import org.scijava.ui.behaviour.DragBehaviour;
import org.scijava.ui.behaviour.util.AbstractNamedBehaviour;
import org.scijava.ui.behaviour.util.Behaviours;

import net.imglib2.ui.OverlayRenderer;
import net.imglib2.ui.TransformListener;

/**
 * Bahviour for creating / deleting links in TrackScheme views.
 *
 * @param <V>
 *            vertex type.
 * @param <E>
 *            edge type.
 *
 * @author Jean-Yves Tinevez
 * @author Tobias Pietzsch
 */
public class ToggleLinkBehaviour< V extends Vertex< E > & HasTimepoint, E extends Edge< V > >
		extends AbstractNamedBehaviour
		implements DragBehaviour
{
	public static final String TOGGLE_LINK = "toggle link";

	private static final String[] TOGGLE_LINK_KEYS = new String[] { "L" };

	/*
	 * Command descriptions for all provided commands
	 */
	@Plugin( type = Descriptions.class )
	public static class Descriptions extends CommandDescriptionProvider
	{
		public Descriptions()
		{
			super( KeyConfigContexts.TRACKSCHEME );
		}

		@Override
		public void getCommandDescriptions( final CommandDescriptions descriptions )
		{
			descriptions.add( TOGGLE_LINK, TOGGLE_LINK_KEYS, "Toggle a Link by dragging between two spots." );
		}
	}

	private static final Color EDIT_GRAPH_OVERLAY_COLOR = Color.RED.darker();

	private static final BasicStroke EDIT_GRAPH_OVERLAY_STROKE = new BasicStroke( 2f );

	private final TrackSchemeGraph< V, E > graph;

	private final TrackSchemeOverlay renderer;

	private final ReentrantReadWriteLock lock;

	private final GraphChangeNotifier notify;

	private final UndoPointMarker undo;

	private final TrackSchemePanel panel;

	private final EditOverlay overlay;

	private final TrackSchemeVertex startVertex;

	private final TrackSchemeVertex endVertex;

	private boolean editing;

	public static < V extends Vertex< E > & HasTimepoint, E extends Edge< V > > void install(
			final Behaviours behaviours,
			final TrackSchemePanel panel,
			final TrackSchemeGraph< V, E > graph,
			final ReentrantReadWriteLock lock,
			final GraphChangeNotifier notify,
			final UndoPointMarker undo )
	{
		final ToggleLinkBehaviour< V, E > toggleLinkBehaviour = new ToggleLinkBehaviour<>( panel, graph, lock, notify, undo );
		behaviours.namedBehaviour( toggleLinkBehaviour, TOGGLE_LINK_KEYS );
	}

	private ToggleLinkBehaviour(
			final TrackSchemePanel panel,
			final TrackSchemeGraph< V, E > graph,
			final ReentrantReadWriteLock lock,
			final GraphChangeNotifier notify,
			final UndoPointMarker undo )
	{
		super( TOGGLE_LINK );
		this.panel = panel;
		this.graph = graph;
		this.renderer = panel.getGraphOverlay();
		this.lock = lock;
		this.notify = notify;
		this.undo = undo;

		// Create and register overlay.
		overlay = new EditOverlay();
		overlay.transformChanged( panel.getTransformEventHandler().getTransform() );
		// put the overlay first, so that is below the graph rendering.
		renderer.addOverlayRenderer( overlay );
		panel.getDisplay().addTransformListener( overlay );

		startVertex = graph.vertexRef();
		endVertex = graph.vertexRef();
		editing = false;
	}

	@Override
	public void init( final int x, final int y )
	{
		// TODO: should listen to graph and abort behaviour if startVertex is removed. For this TrackSchemeGraph would need to be listenable...

		// Get vertex we clicked inside.
		if ( renderer.getVertexAt( x, y, startVertex ) != null )
		{
			overlay.from[ 0 ] = startVertex.getLayoutX();
			overlay.from[ 1 ] = startVertex.getTimepoint();
			overlay.to[ 0 ] = overlay.from[ 0 ];
			overlay.to[ 1 ] = overlay.to[ 0 ];
			editing = true;
			overlay.paint = true;
		}
	}

	@Override
	public void drag( final int x, final int y )
	{
		if ( editing )
		{
			if ( renderer.getVertexAt( x, y, endVertex ) != null && startVertex.getTimepoint() != endVertex.getTimepoint() )
			{
				overlay.to[ 0 ] = endVertex.getLayoutX();
				overlay.to[ 1 ] = endVertex.getTimepoint();
				overlay.strongEdge = true;
			}
			else
			{
				overlay.vTo[ 0 ] = x - panel.getOffsetHeaders().getWidth();
				overlay.vTo[ 1 ] = y - panel.getOffsetHeaders().getHeight();
				overlay.screenTransform.applyInverse( overlay.to, overlay.vTo );
				overlay.strongEdge = false;
			}
			panel.repaint();
		}
	}

	@Override
	public void end( final int x, final int y )
	{
		if ( editing )
		{
			editing = false;
			overlay.paint = false;

			lock.writeLock().lock();
			try
			{
				if ( renderer.getVertexAt( x, y, endVertex ) != null )
				{
					overlay.to[ 0 ] = endVertex.getLayoutX();
					overlay.to[ 1 ] = endVertex.getTimepoint();

					/*
					 * Prevent the creation of links between vertices in the
					 * same time-point.
					 */
					final int tStart = startVertex.getTimepoint();
					final int tEnd = endVertex.getTimepoint();
					if ( tStart == tEnd )
						return;

					/*
					 * Careful with directed graphs. We always check and create
					 * links forward in time.
					 */
					final TrackSchemeVertex source = tStart > tEnd ? endVertex : startVertex;
					final TrackSchemeVertex target = tStart > tEnd ? startVertex : endVertex;

					final TrackSchemeEdge eref = graph.edgeRef();
					final TrackSchemeEdge edge = graph.getEdge( source, target, eref );
					if ( null == edge )
						graph.addEdge( source, target, eref ).init();
					else
						graph.remove( edge );
					graph.releaseRef( eref );

					undo.setUndoPoint();
					notify.notifyGraphChanged();

				}
			}
			finally
			{
				lock.writeLock().unlock();
			}
		}
	}

	private class EditOverlay implements OverlayRenderer, TransformListener< ScreenTransform >
	{
		public boolean strongEdge;

		/** The global coordinates to paint the link from. */
		private final double[] from;

		/** The global coordinates to paint the link to. */
		private final double[] to;

		/** The viewer coordinates to paint the link from. */
		private final double[] vFrom;

		/** The viewer coordinates to paint the link to. */
		private final double[] vTo;

		private final ScreenTransform screenTransform;

		private boolean paint;

		public EditOverlay()
		{
			from = new double[ 2 ];
			vFrom = new double[ 2 ];
			to = new double[ 2 ];
			vTo = new double[ 2 ];
			screenTransform = new ScreenTransform();
			paint = false;
		}

		@Override
		public void drawOverlays( final Graphics g )
		{
			if ( !paint )
				return;

			final Graphics2D graphics = ( Graphics2D ) g;
			g.setColor( EDIT_GRAPH_OVERLAY_COLOR );
			if ( strongEdge )
				graphics.setStroke( EDIT_GRAPH_OVERLAY_STROKE );
			screenTransform.apply( from, vFrom );
			screenTransform.apply( to, vTo );
			g.drawLine(
					( int ) vFrom[ 0 ] + panel.getOffsetHeaders().getWidth(),
					( int ) vFrom[ 1 ] + panel.getOffsetHeaders().getHeight(),
					( int ) vTo[ 0 ] + panel.getOffsetHeaders().getWidth(),
					( int ) vTo[ 1 ] + panel.getOffsetHeaders().getHeight() );
		}

		@Override
		public void setCanvasSize( final int width, final int height )
		{}

		@Override
		public void transformChanged( final ScreenTransform transform )
		{
			synchronized ( screenTransform )
			{
				screenTransform.set( transform );
			}
		}
	}
}
