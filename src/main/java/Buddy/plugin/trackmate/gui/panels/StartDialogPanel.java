package Buddy.plugin.trackmate.gui.panels;

import static Buddy.plugin.trackmate.gui.TrackMateWizard.BIG_FONT;
import static Buddy.plugin.trackmate.gui.TrackMateWizard.SMALL_FONT;
import static Buddy.plugin.trackmate.gui.TrackMateWizard.TEXTFIELD_DIMENSION;

import java.awt.Cursor;
import java.awt.Desktop;
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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenSettings;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.Settings;
import Buddy.plugin.trackmate.gui.panels.components.JNumericTextField;
import fiji.util.NumberParser;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Roi;
import net.imglib2.img.display.imagej.ImageJFunctions;
import pluginTools.InteractiveBud;

public class StartDialogPanel extends ActionListenablePanel {

	private static final long serialVersionUID = -1L;

	private static final String TOOLTIP = "<html>" + "Pressing this button will make the current <br>"
			+ "ImagePlus the source for TrackMate. If the <br>" + "image has a ROI, it will be used to set the <br>"
			+ "crop rectangle as well.</html>";

	/** ActionEvent fired when the user press the refresh button. */
	private final ActionEvent IMAGEPLUS_REFRESHED = new ActionEvent(this, 0, "ImagePlus refreshed");

	private final JLabel jLabelImageName;


	private final JNumericTextField jTextFieldTEnd;

	private final JNumericTextField jTextFieldTStart;

	private final JButton jButtonRefresh;

	private final JLabel jLabelUnits4;

	private final JLabel jLabelUnits3;

	private final JLabel jLabelUnits2;

	private final JLabel jLabelUnits1;

	private final JNumericTextField jTextFieldPixelWidth;

	private final JNumericTextField jTextFieldVoxelDepth;

	private final JNumericTextField jTextFieldPixelHeight;

	private final JNumericTextField jTextFieldTimeInterval;

	private ImagePlus imp;

	private boolean impValid = false;

	public StartDialogPanel() {
		this.setPreferredSize(new java.awt.Dimension(266, 476));
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
				1.0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0 };
		setLayout(gridBagLayout);


		
		final JLabel lblCitation = new JLabel("<html>" +" BTrack is based on TrackMate which is a published Fiji tool for tracking,"
		+  "<b>Tinevez, JY.; Perry, N. & Schindelin, J. et al. (2017), "
		+ "<i>TrackMate: An open and extensible platform for single-particle "
		+ "tracking.</i></b> Methods 115: 80-90.");
		lblCitation.setFont(SMALL_FONT);
		final GridBagConstraints gbc_lblCitation = new GridBagConstraints();
		gbc_lblCitation.fill = GridBagConstraints.BOTH;
		gbc_lblCitation.insets = new Insets(5, 5, 5, 5);
		gbc_lblCitation.gridwidth = 4;
		gbc_lblCitation.gridx = 0;
		gbc_lblCitation.gridy = 0;
		add(lblCitation, gbc_lblCitation);

		final JLabel lblLinkPubMed = new JLabel(
				"<html><a href=https://www.ncbi.nlm.nih.gov/pubmed/27713081>on PubMed (PMID 27713081)</a></html>");
		lblLinkPubMed.setFont(SMALL_FONT);
		lblLinkPubMed.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblLinkPubMed.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final java.awt.event.MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://www.ncbi.nlm.nih.gov/pubmed/27713081"));
				} catch (URISyntaxException | IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		final GridBagConstraints gbc_lblLinkPubMed = new GridBagConstraints();
		gbc_lblLinkPubMed.anchor = GridBagConstraints.NORTH;
		gbc_lblLinkPubMed.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblLinkPubMed.gridwidth = 4;
		gbc_lblLinkPubMed.insets = new Insets(0, 10, 5, 5);
		gbc_lblLinkPubMed.gridx = 0;
		gbc_lblLinkPubMed.gridy = 1;
		add(lblLinkPubMed, gbc_lblLinkPubMed);

		jLabelImageName = new JLabel("Select an image, and press refresh.");
		jLabelImageName.setFont(BIG_FONT);
		final GridBagConstraints gbc_jLabelImageName = new GridBagConstraints();
		gbc_jLabelImageName.anchor = GridBagConstraints.SOUTH;
		gbc_jLabelImageName.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelImageName.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelImageName.gridwidth = 4;
		gbc_jLabelImageName.gridx = 0;
		gbc_jLabelImageName.gridy = 3;
		add(jLabelImageName, gbc_jLabelImageName);

		final JLabel jLabelCheckCalibration = new JLabel("Calibration settings:");
		jLabelCheckCalibration.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jLabelCheckCalibration = new GridBagConstraints();
		gbc_jLabelCheckCalibration.anchor = GridBagConstraints.SOUTH;
		gbc_jLabelCheckCalibration.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelCheckCalibration.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelCheckCalibration.gridwidth = 4;
		gbc_jLabelCheckCalibration.gridx = 0;
		gbc_jLabelCheckCalibration.gridy = 4;
		add(jLabelCheckCalibration, gbc_jLabelCheckCalibration);

		final JLabel jLabelPixelWidth = new JLabel("Pixel width:");
		jLabelPixelWidth.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jLabelPixelWidth = new GridBagConstraints();
		gbc_jLabelPixelWidth.anchor = GridBagConstraints.EAST;
		gbc_jLabelPixelWidth.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelPixelWidth.gridwidth = 2;
		gbc_jLabelPixelWidth.gridx = 0;
		gbc_jLabelPixelWidth.gridy = 5;
		add(jLabelPixelWidth, gbc_jLabelPixelWidth);

		jTextFieldPixelWidth = new JNumericTextField();
		jTextFieldPixelWidth.setHorizontalAlignment(SwingConstants.CENTER);
		jTextFieldPixelWidth.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jTextFieldPixelWidth = new GridBagConstraints();
		gbc_jTextFieldPixelWidth.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldPixelWidth.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldPixelWidth.insets = new Insets(5, 5, 5, 5);
		gbc_jTextFieldPixelWidth.gridx = 2;
		gbc_jTextFieldPixelWidth.gridy = 5;
		add(jTextFieldPixelWidth, gbc_jTextFieldPixelWidth);

		jLabelUnits1 = new JLabel();
		jLabelUnits1.setText("units");
		jLabelUnits1.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jLabelUnits1 = new GridBagConstraints();
		gbc_jLabelUnits1.anchor = GridBagConstraints.WEST;
		gbc_jLabelUnits1.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelUnits1.gridx = 3;
		gbc_jLabelUnits1.gridy = 5;
		add(jLabelUnits1, gbc_jLabelUnits1);

		final JLabel jLabelPixelHeight = new JLabel("Pixel height:");
		jLabelPixelHeight.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jLabelPixelHeight = new GridBagConstraints();
		gbc_jLabelPixelHeight.anchor = GridBagConstraints.EAST;
		gbc_jLabelPixelHeight.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelPixelHeight.gridwidth = 2;
		gbc_jLabelPixelHeight.gridx = 0;
		gbc_jLabelPixelHeight.gridy = 6;
		add(jLabelPixelHeight, gbc_jLabelPixelHeight);

		jTextFieldPixelHeight = new JNumericTextField();
		jTextFieldPixelHeight.setHorizontalAlignment(SwingConstants.CENTER);
		jTextFieldPixelHeight.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jTextFieldPixelHeight = new GridBagConstraints();
		gbc_jTextFieldPixelHeight.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldPixelHeight.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldPixelHeight.insets = new Insets(5, 5, 5, 5);
		gbc_jTextFieldPixelHeight.gridx = 2;
		gbc_jTextFieldPixelHeight.gridy = 6;
		add(jTextFieldPixelHeight, gbc_jTextFieldPixelHeight);

		final JLabel jLabelTimeInterval = new JLabel("Time interval:");
		jLabelTimeInterval.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jLabelTimeInterval = new GridBagConstraints();
		gbc_jLabelTimeInterval.anchor = GridBagConstraints.EAST;
		gbc_jLabelTimeInterval.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelTimeInterval.gridwidth = 2;
		gbc_jLabelTimeInterval.gridx = 0;
		gbc_jLabelTimeInterval.gridy = 8;
		add(jLabelTimeInterval, gbc_jLabelTimeInterval);

		jLabelUnits2 = new JLabel();
		jLabelUnits2.setText("units");
		jLabelUnits2.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jLabelUnits2 = new GridBagConstraints();
		gbc_jLabelUnits2.anchor = GridBagConstraints.WEST;
		gbc_jLabelUnits2.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelUnits2.gridx = 3;
		gbc_jLabelUnits2.gridy = 6;
		add(jLabelUnits2, gbc_jLabelUnits2);

		jTextFieldVoxelDepth = new JNumericTextField();
		jTextFieldVoxelDepth.setHorizontalAlignment(SwingConstants.CENTER);
		jTextFieldVoxelDepth.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jTextFieldVoxelDepth = new GridBagConstraints();
		gbc_jTextFieldVoxelDepth.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldVoxelDepth.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldVoxelDepth.insets = new Insets(5, 5, 5, 5);
		gbc_jTextFieldVoxelDepth.gridx = 2;
		gbc_jTextFieldVoxelDepth.gridy = 7;
		add(jTextFieldVoxelDepth, gbc_jTextFieldVoxelDepth);

		final JLabel jLabelVoxelDepth = new JLabel("Voxel depth:");
		jLabelVoxelDepth.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jLabelVoxelDepth = new GridBagConstraints();
		gbc_jLabelVoxelDepth.anchor = GridBagConstraints.EAST;
		gbc_jLabelVoxelDepth.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelVoxelDepth.gridwidth = 2;
		gbc_jLabelVoxelDepth.gridx = 0;
		gbc_jLabelVoxelDepth.gridy = 7;
		add(jLabelVoxelDepth, gbc_jLabelVoxelDepth);

		jLabelUnits3 = new JLabel();
		jLabelUnits3.setText("units");
		jLabelUnits3.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jLabelUnits3 = new GridBagConstraints();
		gbc_jLabelUnits3.anchor = GridBagConstraints.WEST;
		gbc_jLabelUnits3.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelUnits3.gridx = 3;
		gbc_jLabelUnits3.gridy = 7;
		add(jLabelUnits3, gbc_jLabelUnits3);

		jLabelUnits4 = new JLabel();
		jLabelUnits4.setText("units");
		jLabelUnits4.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jLabelUnits4 = new GridBagConstraints();
		gbc_jLabelUnits4.anchor = GridBagConstraints.WEST;
		gbc_jLabelUnits4.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelUnits4.gridx = 3;
		gbc_jLabelUnits4.gridy = 8;
		add(jLabelUnits4, gbc_jLabelUnits4);

		jTextFieldTimeInterval = new JNumericTextField();
		jTextFieldTimeInterval.setHorizontalAlignment(SwingConstants.CENTER);
		jTextFieldTimeInterval.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jTextFieldTimeInterval = new GridBagConstraints();
		gbc_jTextFieldTimeInterval.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldTimeInterval.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldTimeInterval.insets = new Insets(5, 5, 5, 5);
		gbc_jTextFieldTimeInterval.gridx = 2;
		gbc_jTextFieldTimeInterval.gridy = 8;
		add(jTextFieldTimeInterval, gbc_jTextFieldTimeInterval);

		final JLabel jLabelCropSetting = new JLabel("Crop settings (in pixels, 0-based):");
		jLabelCropSetting.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jLabelCropSetting = new GridBagConstraints();
		gbc_jLabelCropSetting.anchor = GridBagConstraints.SOUTH;
		gbc_jLabelCropSetting.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelCropSetting.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelCropSetting.gridwidth = 4;
		gbc_jLabelCropSetting.gridx = 0;
		gbc_jLabelCropSetting.gridy = 9;
		add(jLabelCropSetting, gbc_jLabelCropSetting);

		

		jTextFieldTStart = new JNumericTextField();
		jTextFieldTStart.setFormat("%.0f");
		jTextFieldTStart.setHorizontalAlignment(SwingConstants.CENTER);
		jTextFieldTStart.setPreferredSize(TEXTFIELD_DIMENSION);
		jTextFieldTStart.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jTextFieldTStart = new GridBagConstraints();
		gbc_jTextFieldTStart.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldTStart.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldTStart.insets = new Insets(5, 5, 5, 5);
		gbc_jTextFieldTStart.gridx = 1;
		gbc_jTextFieldTStart.gridy = 13;
		add(jTextFieldTStart, gbc_jTextFieldTStart);

		final JLabel jLabelT = new JLabel("T");
		jLabelT.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jLabelT = new GridBagConstraints();
		gbc_jLabelT.anchor = GridBagConstraints.EAST;
		gbc_jLabelT.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelT.gridx = 0;
		gbc_jLabelT.gridy = 13;
		add(jLabelT, gbc_jLabelT);

		jTextFieldTEnd = new JNumericTextField();
		jTextFieldTEnd.setFormat("%.0f");
		jTextFieldTEnd.setHorizontalAlignment(SwingConstants.CENTER);
		jTextFieldTEnd.setPreferredSize(TEXTFIELD_DIMENSION);
		jTextFieldTEnd.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jTextFieldTEnd = new GridBagConstraints();
		gbc_jTextFieldTEnd.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldTEnd.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldTEnd.insets = new Insets(5, 5, 5, 5);
		gbc_jTextFieldTEnd.gridx = 3;
		gbc_jTextFieldTEnd.gridy = 13;
		add(jTextFieldTEnd, gbc_jTextFieldTEnd);

		final JLabel jLabelTo4 = new JLabel("to");
		jLabelTo4.setFont(SMALL_FONT);
		final GridBagConstraints gbc_jLabelTo4 = new GridBagConstraints();
		gbc_jLabelTo4.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelTo4.gridx = 2;
		gbc_jLabelTo4.gridy = 13;
		add(jLabelTo4, gbc_jLabelTo4);

		jButtonRefresh = new JButton("Refresh source");
		jButtonRefresh.setToolTipText(TOOLTIP);
		jButtonRefresh.setFont(SMALL_FONT);
	}

	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Returns <code>true</code> if the {@link ImagePlus} selected is valid and can
	 * be processed.
	 * 
	 * @return a boolean flag.
	 */
	public boolean isImpValid() {
		return impValid;
	}

	/**
	 * Update the specified settings object, with the parameters set in this panel.
	 * 
	 * @param settings
	 *            the Settings to update. Cannot be <code>null</code>.
	 */
	public void updateTo(final Model model, final Settings settings) {
		settings.imp = imp;
		// Crop cube
		settings.tstart = NumberParser.parseInteger(jTextFieldTStart.getText());
		settings.tend = NumberParser.parseInteger(jTextFieldTEnd.getText());
		// Image info
		settings.dx = NumberParser.parseDouble(jTextFieldPixelWidth.getText());
		settings.dy = NumberParser.parseDouble(jTextFieldPixelHeight.getText());
		settings.dz = NumberParser.parseDouble(jTextFieldVoxelDepth.getText());
		settings.dt = NumberParser.parseDouble(jTextFieldTimeInterval.getText());
		settings.width = imp.getWidth();
		settings.height = imp.getHeight();
		settings.nslices = imp.getNSlices();
		settings.nframes = imp.getNFrames();
		// Units
		model.setPhysicalUnits(jLabelUnits1.getText(), jLabelUnits4.getText());
		// Roi
		final Roi roi = imp.getRoi();
		if (null != roi) {
			settings.roi = roi;
			settings.polygon = roi.getPolygon();
		}
		// File info
		if (null != imp.getOriginalFileInfo()) {
			settings.imageFileName = imp.getOriginalFileInfo().fileName;
			settings.imageFolder = imp.getOriginalFileInfo().directory;
		}

	}
	
	
	public void updateTo(final GreenModel model, final GreenSettings settings) {
		settings.imp = imp;
		// Crop cube
		settings.tstart = NumberParser.parseInteger(jTextFieldTStart.getText());
		settings.tend = NumberParser.parseInteger(jTextFieldTEnd.getText());
		// Image info
		settings.dx = NumberParser.parseDouble(jTextFieldPixelWidth.getText());
		settings.dy = NumberParser.parseDouble(jTextFieldPixelHeight.getText());
		settings.dz = NumberParser.parseDouble(jTextFieldVoxelDepth.getText());
		settings.dt = NumberParser.parseDouble(jTextFieldTimeInterval.getText());
		settings.width = imp.getWidth();
		settings.height = imp.getHeight();
		settings.nslices = imp.getNSlices();
		settings.nframes = imp.getNFrames();
		// Units
		model.setPhysicalUnits(jLabelUnits1.getText(), jLabelUnits4.getText());
		// Roi
		final Roi roi = imp.getRoi();
		if (null != roi) {
			settings.roi = roi;
			settings.polygon = roi.getPolygon();
		}
		// File info
		if (null != imp.getOriginalFileInfo()) {
			settings.imageFileName = imp.getOriginalFileInfo().fileName;
			settings.imageFolder = imp.getOriginalFileInfo().directory;
		}

	}

	/*
	 * PRIVATE METHODS
	 */

	/**
	 * Fill the text fields with the parameters grabbed in the {@link Settings}
	 * argument.
	 */
	public void echoSettings(final Model model, final Settings settings) {
		jLabelImageName.setText(settings.imp.getTitle());
		jTextFieldPixelWidth.setText("" + settings.dx);
		jTextFieldPixelHeight.setText("" + settings.dy);
		jTextFieldVoxelDepth.setText("" + settings.dz);
		jTextFieldTimeInterval.setText("" + settings.dt);
		jLabelUnits1.setText("pixel");
		jLabelUnits2.setText("pixel");
		jLabelUnits3.setText("pixel");
		jLabelUnits4.setText("pixel");
		jTextFieldTStart.setText("" + settings.tstart);
		jTextFieldTEnd.setText("" + settings.tend);
	}
	
	public void echoSettings(final GreenModel model, final Settings settings) {
		jLabelImageName.setText(settings.imp.getTitle());
		jTextFieldPixelWidth.setText("" + settings.dx);
		jTextFieldPixelHeight.setText("" + settings.dy);
		jTextFieldVoxelDepth.setText("" + settings.dz);
		jTextFieldTimeInterval.setText("" + settings.dt);
		jLabelUnits1.setText("pixel");
		jLabelUnits2.setText("pixel");
		jLabelUnits3.setText("pixel");
		jLabelUnits4.setText("pixel");
		jTextFieldTStart.setText("" + settings.tstart);
		jTextFieldTEnd.setText("" + settings.tend);
	}
	

	/**
	 * Fill the text fields with parameters grabbed from specified ImagePlus.
	 */
	public void getFrom(final ImagePlus lImp) {
		this.imp = lImp;

		if (lImp.getType() == ImagePlus.COLOR_RGB) {
			// We do not know how to process RGB images
			jLabelImageName.setText(lImp.getShortTitle() + " is RGB");
			impValid = true;

		}

		jLabelImageName.setText("Target: " + lImp.getShortTitle());
		jTextFieldPixelWidth.setValue(1);
		jTextFieldPixelHeight.setValue(1);
		jTextFieldVoxelDepth.setValue(1);
		if (lImp.getCalibration().frameInterval == 0) {
			jTextFieldTimeInterval.setValue(1);
			jLabelUnits4.setText("frame");
		} else {
			jTextFieldTimeInterval.setValue(1);
			jLabelUnits4.setText(lImp.getCalibration().getTimeUnit());
		}
		jLabelUnits1.setText(lImp.getCalibration().getXUnit());
		jLabelUnits2.setText(lImp.getCalibration().getYUnit());
		jLabelUnits3.setText(lImp.getCalibration().getZUnit());
		Roi roi = lImp.getRoi();
		if (null == roi)
			roi = new Roi(0, 0, lImp.getWidth(), lImp.getHeight());
		final Rectangle boundingRect = roi.getBounds();
		jTextFieldTStart.setText("" + 0);
		jTextFieldTEnd.setText("" + (lImp.getNFrames() - 1));

		impValid = true;
	}

	

	
}
