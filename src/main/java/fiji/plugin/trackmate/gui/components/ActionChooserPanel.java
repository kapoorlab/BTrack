package Buddy.plugin.trackmate.gui.components;

import static Buddy.plugin.trackmate.gui.Fonts.FONT;
import static Buddy.plugin.trackmate.gui.Icons.EXECUTE_ICON;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.action.CaptureOverlayAction;
import Buddy.plugin.trackmate.action.TrackMateAction;
import Buddy.plugin.trackmate.action.TrackMateActionFactory;
import Buddy.plugin.trackmate.gui.displaysettings.DisplaySettings;
import Buddy.plugin.trackmate.providers.ActionProvider;

public class ActionChooserPanel extends ModuleChooserPanel< TrackMateActionFactory >
{

	private static final long serialVersionUID = 1L;

	public ActionChooserPanel( final ActionProvider actionProvider, final TrackMate trackmate, final SelectionModel selectionModel, final DisplaySettings displaySettings )
	{
		super( actionProvider, "action", CaptureOverlayAction.KEY );

		final LogPanel logPanel = new LogPanel();
		final GridBagConstraints gbcLogPanel = new GridBagConstraints();
		gbcLogPanel.insets = new Insets( 5, 5, 5, 5 );
		gbcLogPanel.fill = GridBagConstraints.BOTH;
		gbcLogPanel.gridx = 0;
		gbcLogPanel.gridy = 3;
		this.add( logPanel, gbcLogPanel );

		final JButton executeButton = new JButton( "Execute", EXECUTE_ICON );
		executeButton.setFont( FONT );
		final GridBagConstraints gbcExecBtn = new GridBagConstraints();
		gbcExecBtn.insets = new Insets( 5, 5, 5, 5 );
		gbcExecBtn.fill = GridBagConstraints.NONE;
		gbcExecBtn.anchor = GridBagConstraints.EAST;
		gbcExecBtn.gridx = 0;
		gbcExecBtn.gridy = 4;
		this.add( executeButton, gbcExecBtn );

		final Logger logger = logPanel.getLogger();
		executeButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				new Thread( "TrackMate action thread" )
				{
					@Override
					public void run()
					{
						try
						{
							executeButton.setEnabled( false );
							final String actionKey = ActionChooserPanel.this.getSelectedModuleKey();
							final TrackMateAction action = actionProvider.getFactory( actionKey ).create();
							if ( null == action )
							{
								logger.error( "Unknown action: " + actionKey + ".\n" );
							}
							else
							{
								action.setLogger( logger );
								action.execute(
										trackmate,
										selectionModel,
										displaySettings,
										( JFrame ) SwingUtilities.getWindowAncestor( ActionChooserPanel.this ) );
							}
						}
						finally
						{
							executeButton.setEnabled( true );
						}
					}
				}.start();
			}
		} );
	}
}
