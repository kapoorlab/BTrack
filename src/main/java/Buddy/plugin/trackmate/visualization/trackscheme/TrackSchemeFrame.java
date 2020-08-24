package Buddy.plugin.trackmate.visualization.trackscheme;

import static Buddy.plugin.trackmate.gui.TrackMateWizard.SMALL_FONT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxRubberband;

import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.util.TrackNavigator;

public class TrackSchemeFrame extends JFrame
{

	/*
	 * CONSTANTS
	 */

	private static final long serialVersionUID = 1L;

	/*
	 * FIELDS
	 */

	/** The side pane in which spot selection info will be displayed. */
	private InfoPane infoPane;

	private JGraphXAdapter graph;

	private final TrackScheme trackScheme;

	/** The graph component in charge of painting the graph. */
	TrackSchemeGraphComponent graphComponent;

	/** The {@link Logger} that sends messages to the TrackScheme status bar. */
	final Logger logger;

	/*
	 * CONSTRUCTORS
	 */

	public TrackSchemeFrame( final TrackScheme trackScheme )
	{
		this.trackScheme = trackScheme;
		this.graph = trackScheme.getGraph();

		// Frame look
		setIconImage( TrackScheme.TRACK_SCHEME_ICON.getImage() );

		// Layout
		getContentPane().setLayout( new BorderLayout() );

		// Add a ToolBar
		getContentPane().add( createToolBar(), BorderLayout.NORTH );

		// Add the status bar
		final JPanel statusPanel = new JPanel();
		getContentPane().add( statusPanel, BorderLayout.SOUTH );

		statusPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );

		final JLabel statusLabel = new JLabel( " " );
		statusLabel.setFont( SMALL_FONT );
		statusLabel.setHorizontalAlignment( SwingConstants.RIGHT );
		statusLabel.setPreferredSize( new Dimension( 200, 12 ) );
		statusPanel.add( statusLabel );

		final JProgressBar progressBar = new JProgressBar();
		progressBar.setPreferredSize( new Dimension( 146, 12 ) );
		statusPanel.add( progressBar );

		this.logger = new Logger()
		{
			@Override
			public void log( final String message, final Color color )
			{
				SwingUtilities.invokeLater( new Runnable()
				{
					@Override
					public void run()
					{
						statusLabel.setText( message );
						statusLabel.setForeground( color );
					}
				} );
			}

			@Override
			public void error( final String message )
			{
				log( message, Color.RED );
			}

			@Override
			public void setProgress( final double val )
			{
				SwingUtilities.invokeLater( new Runnable()
				{
					@Override
					public void run()
					{
						progressBar.setValue( ( int ) ( val * 100 ) );
					}
				} );
			}

			@Override
			public void setStatus( final String status )
			{
				log( status, Logger.BLUE_COLOR );
			}
		};
	}

	/*
	 * PUBLIC METHODS
	 */

	public void init( final JGraphXAdapter lGraph )
	{
		this.graph = lGraph;
		// GraphComponent
		graphComponent = createGraphComponent();

		// Add the info pane
		infoPane = new InfoPane( trackScheme.getModel(), trackScheme.getSelectionModel() );

		// Add the graph outline
		final mxGraphOutline graphOutline = new mxGraphOutline( graphComponent );

		final JSplitPane inner = new JSplitPane( JSplitPane.VERTICAL_SPLIT, graphOutline, infoPane );
		inner.setDividerLocation( 120 );
		inner.setMinimumSize( new Dimension( 0, 0 ) );

		final JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, inner, graphComponent );
		splitPane.setDividerLocation( 170 );
		getContentPane().add( splitPane, BorderLayout.CENTER );

		final TrackSchemeKeyboardHandler keyboardHandler = new TrackSchemeKeyboardHandler( graphComponent, new TrackNavigator( trackScheme.getModel(), trackScheme.getSelectionModel() ) );
		keyboardHandler.installKeyboardActions( graphComponent );
		keyboardHandler.installKeyboardActions( infoPane );
	}

	/*
	 * Selection management
	 */

	public void centerViewOn( final mxICell cell )
	{
		graphComponent.scrollCellToVisible( cell, true );
	}

	/**
	 * Instantiate the graph component in charge of painting the graph. Hook for
	 * sub-classers.
	 */
	private TrackSchemeGraphComponent createGraphComponent()
	{
		final TrackSchemeGraphComponent gc = new TrackSchemeGraphComponent( graph, trackScheme );
		gc.getVerticalScrollBar().setUnitIncrement( 16 );
		gc.getHorizontalScrollBar().setUnitIncrement( 16 );

		/*
		 * gc.setExportEnabled(true); Seems to be required to have a preview
		 * when we move cells. Also give the ability to export a cell as an
		 * image clipping
		 */
		gc.getConnectionHandler().setEnabled( TrackScheme.DEFAULT_LINKING_ENABLED );
		/*
		 * By default, can be changed in the track scheme toolbar
		 */

		new mxRubberband( gc );

		// Popup menu
		gc.getGraphControl().addMouseListener( new MouseAdapter()
		{
			@Override
			public void mousePressed( final MouseEvent e )
			{
				if ( e.isPopupTrigger() )
				{
					displayPopupMenu( gc.getCellAt( e.getX(), e.getY(), false ), e.getPoint() );
				}
			}

			@Override
			public void mouseReleased( final MouseEvent e )
			{
				if ( e.isPopupTrigger() )
				{
					displayPopupMenu( gc.getCellAt( e.getX(), e.getY(), false ), e.getPoint() );
				}
			}
		} );

		gc.addMouseWheelListener( new MouseWheelListener()
		{

			@Override
			public void mouseWheelMoved( final MouseWheelEvent e )
			{
				if ( gc.isPanningEvent( e ) )
				{
					final boolean in = e.getWheelRotation() < 0;
					if ( in )
						gc.zoomIn();
					else
						gc.zoomOut();
				}
			}
		} );

		gc.setKeepSelectionVisibleOnZoom( true );
		gc.setPanning( true );
		return gc;
	}

	/**
	 * Instantiate the toolbar of the track scheme.
	 */
	private JToolBar createToolBar()
	{
		return new TrackSchemeToolbar( trackScheme );
	}

	/**
	 * PopupMenu
	 */
	private void displayPopupMenu( final Object cell, final Point point )
	{
		final TrackSchemePopupMenu menu = new TrackSchemePopupMenu( trackScheme, cell, point );
		menu.show( graphComponent.getViewport().getView(), ( int ) point.getX(), ( int ) point.getY() );
	}

}
