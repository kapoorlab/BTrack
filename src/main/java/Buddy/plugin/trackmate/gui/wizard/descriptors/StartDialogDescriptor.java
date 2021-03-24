package Buddy.plugin.trackmate.gui.wizard.descriptors;

import static Buddy.plugin.trackmate.gui.Fonts.BIG_FONT;
import static Buddy.plugin.trackmate.gui.Fonts.SMALL_FONT;
import static Buddy.plugin.trackmate.gui.Fonts.TEXTFIELD_DIMENSION;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.Settings;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.GuiUtils;
import Buddy.plugin.trackmate.gui.wizard.WizardPanelDescriptor;
import Buddy.plugin.trackmate.util.TMUtils;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.Calibration;

public class StartDialogDescriptor extends WizardPanelDescriptor
{

	private static final String KEY = "Start";

	private final Settings settings;

	private final Logger logger;

	public StartDialogDescriptor( final Settings settings, final Logger logger )
	{
		super( KEY );
		this.settings = settings;
		this.logger = logger;
		this.targetPanel = new RoiSettingsPanel( settings.imp );
	}

	@Override
	public void aboutToDisplayPanel()
	{
		final String welcomeMessage = TrackMate.PLUGIN_NAME_STR + " v" + TrackMate.PLUGIN_NAME_VERSION + " started on:\n" + TMUtils.getCurrentTimeString() + '\n';
		// Log GUI processing start
		logger.log( welcomeMessage, Logger.BLUE_COLOR );
		logger.log( "Please note that TrackMate is available through Buddy, and is based on a publication. "
				+ "If you use it successfully for your research please be so kind to cite our work:\n" );
		logger.log( "Tinevez, JY.; Perry, N. & Schindelin, J. et al. (2017), 'TrackMate: An open and extensible platform for single-particle tracking.', "
				+ "Methods 115: 80-90, PMID 27713081.\n", Logger.GREEN_COLOR );
		logger.log( "https://www.ncbi.nlm.nih.gov/pubmed/27713081\n", Logger.BLUE_COLOR );
		logger.log( "https://www.sciencedirect.com/science/article/pii/S1046202316303346\n", Logger.BLUE_COLOR );
		logger.log( "\nNumerical feature analyzers:\n", Logger.BLUE_COLOR );
		logger.log( settings.toStringFeatureAnalyzersInfo() );
	}

	@Override
	public void aboutToHidePanel()
	{
		// Copy the values in the panel to the settings object.
		final RoiSettingsPanel panel = ( RoiSettingsPanel ) targetPanel;
		// Log
		logger.log( "\nImage region of interest:\n", Logger.BLUE_COLOR );
		logger.log( settings.toStringImageInfo() );
	}

	private static class RoiSettingsPanel extends JPanel
	{

		private static final long serialVersionUID = -1L;

		private static final NumberFormat DOUBLE_FORMAT = new DecimalFormat( "#.###" );

		private static final String TOOLTIP = "<html>" +
				"Pressing this button will make the current <br>" +
				"ImagePlus the source for TrackMate. If the <br>" +
				"image has a ROI, it will be used to set the <br>" +
				"crop rectangle as well.</html>";


		public RoiSettingsPanel( final ImagePlus imp )
		{
			this.setPreferredSize( new Dimension( 291, 491 ) );
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
			gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0 };
			setLayout( gridBagLayout );

			final JLabel lblCitation = new JLabel( "<html>"
					+ "BTrackmate is a slim version of Trackmate, "
					+ "to be used when inputting integer segmentations as tif or csv file"
					
					+ "<p>"
					+ " Made for Claudia Carabana Garcia by Varun Kapoor based on "
					+ "<b>Tinevez, JY.; Perry, N. & Schindelin, J. et al. (2017), "
					+ "<i>TrackMate: An open and extensible platform for single-particle "
					+ "tracking.</i></b> Methods 115: 80-90."
					+ "</html>" );
			lblCitation.setFont( SMALL_FONT );

			final GridBagConstraints gbcLblCitation = new GridBagConstraints();
			gbcLblCitation.fill = GridBagConstraints.BOTH;
			gbcLblCitation.insets = new Insets( 5, 5, 5, 5 );
			gbcLblCitation.gridwidth = 4;
			gbcLblCitation.gridx = 0;
			gbcLblCitation.gridy = 0;
			add( lblCitation, gbcLblCitation );

			final JLabel lblLinkPubMed = new JLabel( "<html>"
					+ "<a href=https://www.ncbi.nlm.nih.gov/pubmed/27713081>on PubMed (PMID 27713081)</a></html>" );
			lblLinkPubMed.setFont( SMALL_FONT );
			lblLinkPubMed.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
			lblLinkPubMed.addMouseListener( new MouseAdapter()
			{
				@Override
				public void mouseClicked( final java.awt.event.MouseEvent e )
				{
					try
					{
						Desktop.getDesktop().browse( new URI( "https://www.ncbi.nlm.nih.gov/pubmed/27713081" ) );
					}
					catch ( URISyntaxException | IOException ex )
					{
						ex.printStackTrace();
					}
				}
			} );
			final GridBagConstraints gbcLblLinkPubMed = new GridBagConstraints();
			gbcLblLinkPubMed.anchor = GridBagConstraints.NORTH;
			gbcLblLinkPubMed.fill = GridBagConstraints.HORIZONTAL;
			gbcLblLinkPubMed.gridwidth = 4;
			gbcLblLinkPubMed.insets = new Insets( 0, 10, 5, 5 );
			gbcLblLinkPubMed.gridx = 0;
			gbcLblLinkPubMed.gridy = 1;
			add( lblLinkPubMed, gbcLblLinkPubMed );

			final JLabel lblImageName = new JLabel( "Target image: " + imp.getShortTitle() );
			lblImageName.setFont( BIG_FONT );
			final GridBagConstraints gbcLabelImageName = new GridBagConstraints();
			gbcLabelImageName.anchor = GridBagConstraints.SOUTH;
			gbcLabelImageName.fill = GridBagConstraints.HORIZONTAL;
			gbcLabelImageName.insets = new Insets( 5, 5, 5, 5 );
			gbcLabelImageName.gridwidth = 4;
			gbcLabelImageName.gridx = 0;
			gbcLabelImageName.gridy = 3;
			add( lblImageName, gbcLabelImageName );

			
		}
	}

}
