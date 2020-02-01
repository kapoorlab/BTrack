package org.mastodon.revised.bdv.overlay.ui;

import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mastodon.revised.bdv.overlay.RenderSettings;
import org.mastodon.revised.io.yaml.AbstractWorkaroundConstruct;
import org.mastodon.revised.io.yaml.WorkaroundConstructor;
import org.mastodon.revised.io.yaml.WorkaroundRepresent;
import org.mastodon.revised.io.yaml.WorkaroundRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import static org.yaml.snakeyaml.DumperOptions.FlowStyle.FLOW;

/**
 * Facilities to dump / load {@link RenderSettings} to / from a YAML file.
 *
 * @author Jean-Yves Tinevez
 *
 */
public class RenderSettingsIO
{
	private static class RenderSettingsRepresenter extends WorkaroundRepresenter
	{
		public RenderSettingsRepresenter()
		{
			putRepresent( new RepresentBasicStroke( this ) );
			putRepresent( new RepresentRenderSettings( this ) );
		}
	}

	private static class RenderSettingsConstructor extends WorkaroundConstructor
	{
		public RenderSettingsConstructor()
		{
			super( Object.class );
			putConstruct( new ConstructBasicStroke( this ) );
			putConstruct( new ConstructRenderSettings( this ) );
		}
	}

	/**
	 * Returns a YAML instance that can dump / load a collection of
	 * {@link RenderSettings} to / from a .yaml file.
	 *
	 * @return a new YAML instance.
	 */
	static Yaml createYaml()
	{
		final DumperOptions dumperOptions = new DumperOptions();
		final Representer representer = new RenderSettingsRepresenter();
		final Constructor constructor = new RenderSettingsConstructor();
		final Yaml yaml = new Yaml( constructor, representer, dumperOptions );
		return yaml;
	}

	private static final Tag STROKE_TAG = new Tag( "!stroke" );

	private static class RepresentBasicStroke extends WorkaroundRepresent
	{
		public RepresentBasicStroke( final WorkaroundRepresenter r )
		{
			super( r, STROKE_TAG, BasicStroke.class );
		}

		@Override
		public Node representData( final Object data )
		{
			final BasicStroke s = ( BasicStroke ) data;
			final Map< String, Object > mapping = new LinkedHashMap<>();
			mapping.put( "width", s.getLineWidth() );
			mapping.put( "cap", s.getEndCap() );
			mapping.put( "join", s.getLineJoin() );
			mapping.put( "miterlimit", s.getMiterLimit() );
			ArrayList< Float > dash = null;
			final float[] dashArray = s.getDashArray();
			if ( dashArray != null )
			{
				dash = new ArrayList<>();
				for ( final float f : dashArray )
					dash.add( f );
			}
			mapping.put( "dash", dash );
			mapping.put( "dash_phase", s.getDashPhase() );
			final Node node = representMapping( getTag(), mapping, FLOW );
			return node;
		}
	}

	private static class ConstructBasicStroke extends AbstractWorkaroundConstruct
	{
		public ConstructBasicStroke( final WorkaroundConstructor c )
		{
			super( c, STROKE_TAG );
		}

		@Override
		public Object construct( final Node node )
		{
			try
			{
				final Map< Object, Object > mapping = constructMapping( ( MappingNode  ) node );
				final float width = ( ( Double ) mapping.get( "width" ) ).floatValue();
				final int cap = ( Integer ) mapping.get( "cap" );
				final int join = ( Integer ) mapping.get( "join" );
				final float miterlimit = ( ( Double ) mapping.get( "miterlimit" ) ).floatValue();
				@SuppressWarnings( "unchecked" )
				final List< Double > list = ( List< Double > ) mapping.get( "dash" );
				float[] dash = null;
				if ( list != null && !list.isEmpty() )
				{
					dash = new float[ list.size() ];
					int i = 0;
					for ( final double d : list )
						dash[ i++ ] = ( float ) d;
				}
				final float dash_phase = ( ( Double ) mapping.get( "dash_phase" ) ).floatValue();
				return new BasicStroke( width, cap, join, miterlimit, dash, dash_phase );
			}
			catch( final Exception e )
			{
				e.printStackTrace();
			}
			return null;
		}
	}

	private static final Tag RENDERSETTINGS_TAG = new Tag( "!bdvrendersettings" );

	private static class RepresentRenderSettings extends WorkaroundRepresent
	{
		public RepresentRenderSettings( final WorkaroundRepresenter r )
		{
			super( r, RENDERSETTINGS_TAG, RenderSettings.class );
		}

		@Override
		public Node representData( final Object data )
		{
			final RenderSettings s = ( RenderSettings ) data;
			final Map< String, Object > mapping = new LinkedHashMap< >();

			mapping.put( "name", s.getName() );

			mapping.put( "antialiasing", s.getUseAntialiasing() );
			mapping.put( "drawLinks", s.getDrawLinks() );
			mapping.put( "timeRangeForLinks", s.getTimeLimit() );
			mapping.put( "gradientForLinks", s.getUseGradient() );
			mapping.put( "drawSpots", s.getDrawSpots() );
			mapping.put( "drawEllipsoidIntersection", s.getDrawEllipsoidSliceIntersection() );
			mapping.put( "drawEllipsoidProjection", s.getDrawEllipsoidSliceProjection() );
			mapping.put( "drawSpotCenters", s.getDrawSpotCenters() );
			mapping.put( "drawSpotCentersForEllipses", s.getDrawSpotCentersForEllipses() );
			mapping.put( "drawSpotLabels", s.getDrawSpotLabels() );
			mapping.put( "focusLimit", s.getFocusLimit() );
			mapping.put( "focusLimitViewRelative", s.getFocusLimitViewRelative() );
			mapping.put( "ellipsoidFadeDepth", s.getEllipsoidFadeDepth() );
			mapping.put( "pointFadeDepth", s.getPointFadeDepth() );

			final Node node = representMapping( getTag(), mapping, getDefaultFlowStyle() );
			return node;
		}
	}

	private static class ConstructRenderSettings extends AbstractWorkaroundConstruct
	{
		public ConstructRenderSettings( final WorkaroundConstructor c )
		{
			super( c, RENDERSETTINGS_TAG );
		}

		@Override
		public Object construct( final Node node )
		{
			try
			{
				final Map< Object, Object > mapping = constructMapping( ( MappingNode  ) node );
				final String name = ( String ) mapping.get( "name" );
				final RenderSettings s = RenderSettings.defaultStyle().copy( name );

				s.setName( ( String ) mapping.get( "name") );

				s.setUseAntialiasing( ( boolean ) mapping.get( "antialiasing" ) );
				s.setDrawLinks( ( boolean ) mapping.get( "drawLinks" ) );
				s.setTimeLimit( ( int ) mapping.get( "timeRangeForLinks" ) );
				s.setUseGradient( ( boolean ) mapping.get( "gradientForLinks" ) );
				s.setDrawSpots( ( boolean ) mapping.get( "drawSpots" ) );
				s.setDrawEllipsoidSliceIntersection( ( boolean ) mapping.get( "drawEllipsoidIntersection" ) );
				s.setDrawEllipsoidSliceProjection( ( boolean ) mapping.get( "drawEllipsoidProjection" ) );
				s.setDrawSpotCenters( ( boolean ) mapping.get( "drawSpotCenters" ) );
				s.setDrawSpotCentersForEllipses( ( boolean ) mapping.get( "drawSpotCentersForEllipses" ) );
				s.setDrawSpotLabels( ( boolean ) mapping.get( "drawSpotLabels" ) );
				s.setFocusLimit( ( double ) mapping.get( "focusLimit" ) );
				s.setFocusLimitViewRelative( ( boolean ) mapping.get( "focusLimitViewRelative" ) );
				s.setEllipsoidFadeDepth( ( double ) mapping.get( "ellipsoidFadeDepth" ) );
				s.setPointFadeDepth( ( double ) mapping.get( "pointFadeDepth" ) );

				return s;
			}
			catch( final Exception e )
			{
				e.printStackTrace();
			}
			return null;
		}
	}
}
