package Buddy.plugin.trackmate.gui.panels;

import static Buddy.plugin.trackmate.gui.TrackMateWizard.BIG_FONT;
import static Buddy.plugin.trackmate.gui.TrackMateWizard.FONT;
import static Buddy.plugin.trackmate.gui.TrackMateWizard.SMALL_FONT;
import static Buddy.plugin.trackmate.visualization.TrackMateModelView.KEY_DISPLAY_Greenobject_NAMES;
import static Buddy.plugin.trackmate.visualization.TrackMateModelView.KEY_DRAWING_DEPTH;
import static Buddy.plugin.trackmate.visualization.TrackMateModelView.KEY_LIMIT_DRAWING_DEPTH;
import static Buddy.plugin.trackmate.visualization.TrackMateModelView.KEY_GreenobjectS_VISIBLE;
import static Buddy.plugin.trackmate.visualization.TrackMateModelView.KEY_Greenobject_COLORING;
import static Buddy.plugin.trackmate.visualization.TrackMateModelView.KEY_Greenobject_RADIUS_RATIO;
import static Buddy.plugin.trackmate.visualization.TrackMateModelView.KEY_TRACKS_VISIBLE;
import static Buddy.plugin.trackmate.visualization.TrackMateModelView.KEY_TRACK_COLORING;
import static Buddy.plugin.trackmate.visualization.TrackMateModelView.KEY_TRACK_DISPLAY_DEPTH;
import static Buddy.plugin.trackmate.visualization.TrackMateModelView.KEY_TRACK_DISPLAY_MODE;
//import static Buddy.plugin.trackmate.visualization.trackscheme.TrackScheme.TRACK_SCHEME_ICON_16x16;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.jgrapht.graph.DefaultWeightedEdge;

import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.gui.DisplaySettingsEvent;
import Buddy.plugin.trackmate.gui.DisplaySettingsListener;
import Buddy.plugin.trackmate.gui.TrackMateWizard;
import Buddy.plugin.trackmate.gui.panels.components.GreenColorByFeatureGUIPanel;
import Buddy.plugin.trackmate.gui.panels.components.GreenColorByFeatureGUIPanel.Category;
import Buddy.plugin.trackmate.gui.panels.components.JNumericTextField;
import Buddy.plugin.trackmate.gui.panels.components.SetColorScaleDialog;
import Buddy.plugin.trackmate.visualization.FeatureColorGenerator;
import Buddy.plugin.trackmate.visualization.ManualGreenobjectColorGenerator;
import Buddy.plugin.trackmate.visualization.ManualEdgeColorGenerator;
import Buddy.plugin.trackmate.visualization.ManualGreenobjectColorGenerator;
import Buddy.plugin.trackmate.visualization.PerEdgeFeatureColorGenerator;
import Buddy.plugin.trackmate.visualization.PerTrackFeatureColorGenerator;
import Buddy.plugin.trackmate.visualization.GreenobjectColorGenerator;
import Buddy.plugin.trackmate.visualization.GreenobjectColorGeneratorPerTrackFeature;
import Buddy.plugin.trackmate.visualization.TrackColorGenerator;
import Buddy.plugin.trackmate.visualization.TrackMateModelView;
import fiji.util.NumberParser;
import greenDetector.Greenobject;

/**
 * A configuration panel used to tune the aspect of Greenobjects and tracks in
 * multiple
 * {@link Buddy.plugin.trackmate.visualization.AbstractTrackMateModelView}. This
 * GUI takes the role of a controller.
 *
 * @author Jean-Yves Tinevez &lt;tinevez@pasteur.fr&gt; - 2010 - 2011
 */
public class GreenConfigureViewsPanel extends ActionListenablePanel {

	private static final long serialVersionUID = 1L;

	private static final Icon DO_ANALYSIS_ICON = new ImageIcon(
			TrackMateWizard.class.getResource("images/calculator.png"));

	public ActionEvent TRACK_SCHEME_BUTTON_PRESSED = new ActionEvent(this, 0, "TrackSchemeButtonPushed");

	public ActionEvent DO_ANALYSIS_BUTTON_PRESSED = new ActionEvent(this, 1, "DoAnalysisButtonPushed");

	public ActionEvent DO_ANALYSIS_BUTTON_WITH_SHIFT_PRESSED = new ActionEvent(this, 2,
			"DoAnalysisButtonWithShiftPushed");

	private static final String ANALYSIS_BUTTON_TOOLTIP = "<html>"
			+ "Export the features of all tracks, edges and all <br>"
			+ "Greenobjects belonging to a track to ImageJ tables. <br>"
			+ "With <code>shift</code> pressed, the features <br>" + "of all Greenobject are exported.</html>";

	private static final String TRACKSCHEME_BUTTON_TOOLTIP = "<html>" + "Launch a new instance of TrackScheme.</html>";

	/**
	 * A map of String/Object that configures the look and feel of the views.
	 */
	protected Map<String, Object> displaySettings = new HashMap<>();

	protected JButton jButtonShowTrackScheme;

	protected JButton jButtonDoAnalysis;

	private JLabel jLabelTrackDisplayMode;

	private JComboBox<String> jComboBoxDisplayMode;

	private JLabel jLabelDisplayOptions;

	private JPanel jPanelGreenobjectOptions;

	private JCheckBox jCheckBoxDisplayGreenobjects;

	private JPanel jPanelTrackOptions;

	private JCheckBox jCheckBoxDisplayTracks;

	private JCheckBox jCheckBoxLimitDepth;

	private JTextField jTextFieldFrameDepth;

	private JLabel jLabelFrameDepth;


	
	private GreenColorByFeatureGUIPanel jPanelGreenobjectColor;

	private JNumericTextField jTextFieldGreenobjectRadius;
	
	

	private JCheckBox jCheckBoxDisplayNames;

	private GreenColorByFeatureGUIPanel trackColorGUI;

	private final Collection<DisplaySettingsListener> listeners = new HashSet<>();

	
	private final GreenModel model;

	private PerTrackFeatureColorGenerator trackColorGenerator;

	private PerEdgeFeatureColorGenerator edgeColorGenerator;

	private FeatureColorGenerator<Greenobject> GreenobjectColorGenerator;
	
	private ManualGreenobjectColorGenerator manualGreenobjectColorGenerator;

	private ManualEdgeColorGenerator manualEdgeColorGenerator;

	private FeatureColorGenerator<Greenobject> GreenobjectColorGeneratorPerTrackFeature;
	
	private JNumericTextField textFieldDrawingDepth;

	private JPanel jpanelDrawingDepth;

	private JLabel lblDrawingDepthUnits;

	private JLabel jLabelGreenobjectRadius;

	protected JPanel jPanelButtons;

	/*
	 * CONSTRUCTOR
	 */

	public GreenConfigureViewsPanel(final GreenModel model) {
		this.model = model;
		initGUI();
		refreshGUI();
		resizeButtons();
	}
	


	
	
	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Adds the specified {@link DisplaySettingsListener} to the collection of
	 * listeners that will be notified when a display settings change is made on
	 * this GUI.
	 *
	 * @param listener
	 *            the listener to add.
	 */
	public void addDisplaySettingsChangeListener(final DisplaySettingsListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the specified {@link DisplaySettingsListener} from the collection of
	 * listeners of this GUI.
	 *
	 * @param listener
	 *            the listener to remove.
	 * @return <code>true</code> if the listener belonged to the list of registered
	 *         listener and was successfully removed.
	 */
	public boolean removeDisplaySettingsChangeListener(final DisplaySettingsListener listener) {
		return listeners.remove(listener);
	}

	/**
	 * Exposes the {@link JButton} that should trigger the launch of TrackScheme.
	 *
	 * @return the TrackScheme {@link JButton}.
	 */
	public JButton getTrackSchemeButton() {
		return jButtonShowTrackScheme;
	}

	/**
	 * Exposes the {@link JButton} that should trigger the launch of analysis.
	 *
	 * @return the analysis {@link JButton}.
	 */
	public JButton getDoAnalysisButton() {
		return jButtonDoAnalysis;
	}

	public JLabel getTitleJLabel() {
		return jLabelDisplayOptions;
	}

	/**
	 * Overrides the track color generator configured in this panel, allowing to
	 * share instances.
	 *
	 * @param trackColorGenerator
	 *            the new color generator. The previous one will be terminated.
	 */
	public void setTrackColorGenerator(final PerTrackFeatureColorGenerator trackColorGenerator) {
		if (null != this.trackColorGenerator) {
			this.trackColorGenerator.terminate();
		}
		this.trackColorGenerator = trackColorGenerator;
	}

	/**
	 * Overrides the edge color generator configured in this panel, allowing to
	 * share instances.
	 *
	 * @param edgeColorGenerator
	 *            the new color generator. The previous one will be terminated.
	 */
	public void setEdgeColorGenerator(final PerEdgeFeatureColorGenerator edgeColorGenerator) {
		if (null != this.edgeColorGenerator) {
			this.edgeColorGenerator.terminate();
		}
		this.edgeColorGenerator = edgeColorGenerator;
	}

	/**
	 * Overrides the Greenobject color generator configured in this panel, allowing
	 * to share instances.
	 *
	 * @param GreenobjectColorGenerator
	 *            the new color generator.
	 */
	public void setGreenobjectColorGenerator(final FeatureColorGenerator<Greenobject> GreenobjectColorGenerator) {
		if (null != this.GreenobjectColorGenerator) {
			this.GreenobjectColorGenerator.terminate();
		}
		this.GreenobjectColorGenerator = GreenobjectColorGenerator;
	}

	public void setGreenobjectColorGeneratorPerTrackFeature(
			final FeatureColorGenerator<Greenobject> GreenobjectColorGeneratorPerTrackFeature) {
		if (null != this.GreenobjectColorGeneratorPerTrackFeature) {
			this.GreenobjectColorGeneratorPerTrackFeature.terminate();
		}
		this.GreenobjectColorGeneratorPerTrackFeature = GreenobjectColorGeneratorPerTrackFeature;
	}
	

	

	
	

	public void refreshColorFeatures() {
		if ((displaySettings.get(KEY_Greenobject_COLORING) instanceof GreenobjectColorGenerator)) {
			jPanelGreenobjectColor.setColorFeature(GreenobjectColorGenerator.getFeature());
		} else if ((displaySettings.get(KEY_Greenobject_COLORING) instanceof ManualGreenobjectColorGenerator)) {
			jPanelGreenobjectColor.setColorFeature(GreenColorByFeatureGUIPanel.MANUAL_KEY);
		} else if (((displaySettings
				.get(KEY_Greenobject_COLORING) instanceof GreenobjectColorGeneratorPerTrackFeature))) {
			jPanelGreenobjectColor.setColorFeature(GreenobjectColorGeneratorPerTrackFeature.getFeature());
		}

		if (!(displaySettings.get(KEY_TRACK_COLORING) instanceof ManualEdgeColorGenerator)) {
			trackColorGUI.setColorFeature(trackColorGenerator.getFeature());
		}
	}
	



	
	public void setManualGreenobjectColorGenerator(
			final ManualGreenobjectColorGenerator manualGreenobjectColorGenerator) {
		if (null != this.manualGreenobjectColorGenerator) {
			this.manualGreenobjectColorGenerator.terminate();
		}
		this.manualGreenobjectColorGenerator = manualGreenobjectColorGenerator;
	}


	public void setManualEdgeColorGenerator(final ManualEdgeColorGenerator manualEdgeColorGenerator) {
		if (null != this.manualEdgeColorGenerator) {
			this.manualEdgeColorGenerator.terminate();
		}
		this.manualEdgeColorGenerator = manualEdgeColorGenerator;
	}

	/**
	 * Refreshes some components of this GUI with current values of the model.
	 */
	public void refreshGUI() {

		/*
		 * Greenobject coloring
		 */

		if (null != jPanelGreenobjectColor) {
			jPanelGreenobjectOptions.remove(jPanelGreenobjectColor);
		}
		jPanelGreenobjectColor = new GreenColorByFeatureGUIPanel(model,
				Arrays.asList(new Category[] { Category.Greenobject, Category.DEFAULT, Category.TRACKS }));

		jPanelGreenobjectColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					final FeatureColorGenerator<Greenobject> colorGenerator;
					final Category category = jPanelGreenobjectColor.getColorGeneratorCategory();
					switch (category) {
					case TRACKS:
						colorGenerator = GreenobjectColorGeneratorPerTrackFeature;
						break;

					default:
						colorGenerator = GreenobjectColorGenerator;
						break;
					}

					final JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(GreenConfigureViewsPanel.this);
					final SetColorScaleDialog dialog = new SetColorScaleDialog(topFrame,
							"Set color scale for Greenobjects", colorGenerator);
					dialog.setVisible(true);
					if (!dialog.hasUserPressedOK()) {
						return;
					}

					if (dialog.isAutoMinMaxMode()) {
						colorGenerator.autoMinMax();
					}
					jPanelGreenobjectColor.setFrom(dialog);
					jPanelGreenobjectColor.autoMinMax();

					final DisplaySettingsEvent event = new DisplaySettingsEvent(GreenConfigureViewsPanel.this,
							KEY_Greenobject_COLORING, colorGenerator, colorGenerator);
					fireDisplaySettingsChange(event);
				}

			}
		});

		jPanelGreenobjectColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				@SuppressWarnings("unchecked")
				final FeatureColorGenerator<Greenobject> oldValue = (FeatureColorGenerator<Greenobject>) displaySettings
						.get(KEY_Greenobject_COLORING);
				final FeatureColorGenerator<Greenobject> newValue;
				final Category category = jPanelGreenobjectColor.getColorGeneratorCategory();
				switch (category) {
				case Greenobject:
					if (null == GreenobjectColorGenerator) {
						return;
					}
					GreenobjectColorGenerator.setFeature(jPanelGreenobjectColor.getColorFeature());
					newValue = GreenobjectColorGenerator;
					break;
				case TRACKS:
					newValue = GreenobjectColorGeneratorPerTrackFeature;
					GreenobjectColorGeneratorPerTrackFeature.setFeature(jPanelGreenobjectColor.getColorFeature());
					break;
				case DEFAULT:
					if (jPanelGreenobjectColor.getColorFeature().equals(GreenColorByFeatureGUIPanel.UNIFORM_KEY)) {
						GreenobjectColorGenerator.setFeature(null);
						newValue = GreenobjectColorGenerator;
					} else {
						newValue = manualGreenobjectColorGenerator;
					}
					break;
				default:
					throw new IllegalArgumentException("Unknow Greenobject color generator category: " + category);
				}
				displaySettings.put(KEY_Greenobject_COLORING, newValue);
				final DisplaySettingsEvent event = new DisplaySettingsEvent(GreenConfigureViewsPanel.this,
						KEY_Greenobject_COLORING, newValue, oldValue);
				fireDisplaySettingsChange(event);
			}
		});
		jPanelGreenobjectColor.autoMinMax();
		final GroupLayout gl_jPanelGreenobjectOptions = new GroupLayout(jPanelGreenobjectOptions);
		gl_jPanelGreenobjectOptions.setHorizontalGroup(gl_jPanelGreenobjectOptions
				.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_jPanelGreenobjectOptions.createSequentialGroup().addGap(5)
						.addGroup(gl_jPanelGreenobjectOptions.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_jPanelGreenobjectOptions.createSequentialGroup()
										.addComponent(jCheckBoxDisplayNames, GroupLayout.DEFAULT_SIZE, 267,
												Short.MAX_VALUE)
										.addContainerGap())
								.addGroup(gl_jPanelGreenobjectOptions.createSequentialGroup()
										.addComponent(jLabelGreenobjectRadius, GroupLayout.DEFAULT_SIZE, 120,
												Short.MAX_VALUE)
										.addGap(5)
										.addComponent(jTextFieldGreenobjectRadius, GroupLayout.DEFAULT_SIZE, 45,
												Short.MAX_VALUE)
										.addGap(103))))
				.addGroup(gl_jPanelGreenobjectOptions.createSequentialGroup().addContainerGap()
						.addComponent(jPanelGreenobjectColor, GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
						.addContainerGap()));
		gl_jPanelGreenobjectOptions.setVerticalGroup(gl_jPanelGreenobjectOptions.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_jPanelGreenobjectOptions.createSequentialGroup()
						.addGroup(gl_jPanelGreenobjectOptions.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_jPanelGreenobjectOptions.createSequentialGroup().addGap(8)
										.addComponent(jLabelGreenobjectRadius))
								.addGroup(gl_jPanelGreenobjectOptions.createSequentialGroup().addGap(5).addComponent(
										jTextFieldGreenobjectRadius, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(jCheckBoxDisplayNames)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(jPanelGreenobjectColor, GroupLayout.PREFERRED_SIZE, 51, Short.MAX_VALUE)
						.addContainerGap()));
		jPanelGreenobjectOptions.setLayout(gl_jPanelGreenobjectOptions);

		/*
		 * Track coloring
		 */

		if (null != trackColorGUI) {
			jPanelTrackOptions.remove(trackColorGUI);
		}

		trackColorGUI = new GreenColorByFeatureGUIPanel(model,
				Arrays.asList(new Category[] { Category.TRACKS, Category.EDGES, Category.DEFAULT }));
		// trackColorGUI.setPreferredSize(new java.awt.Dimension(265, 45));

		trackColorGUI.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					final FeatureColorGenerator<DefaultWeightedEdge> colorGenerator;
					final String str;
					final Category category = trackColorGUI.getColorGeneratorCategory();
					switch (category) {
					case TRACKS:
						colorGenerator = trackColorGenerator;
						str = "tracks";
						break;

					default:
						colorGenerator = edgeColorGenerator;
						str = "edges";
						break;
					}

					final JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(GreenConfigureViewsPanel.this);
					final SetColorScaleDialog dialog = new SetColorScaleDialog(topFrame, "Set color scale for " + str,
							colorGenerator);
					dialog.setVisible(true);
					if (!dialog.hasUserPressedOK()) {
						return;
					}

					if (dialog.isAutoMinMaxMode()) {
						colorGenerator.autoMinMax();
					}
					trackColorGUI.setFrom(dialog);
					trackColorGUI.autoMinMax();
					final DisplaySettingsEvent event = new DisplaySettingsEvent(GreenConfigureViewsPanel.this,
							KEY_TRACK_COLORING, colorGenerator, colorGenerator);
					fireDisplaySettingsChange(event);
				}

			}
		});

		trackColorGUI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final TrackColorGenerator oldValue = (TrackColorGenerator) displaySettings.get(KEY_TRACK_COLORING);
				TrackColorGenerator newValue;
				final Category category = trackColorGUI.getColorGeneratorCategory();
				switch (category) {
				case TRACKS:
					newValue = trackColorGenerator;
					newValue.setFeature(trackColorGUI.getColorFeature());
					break;
				case EDGES:
					newValue = edgeColorGenerator;
					newValue.setFeature(trackColorGUI.getColorFeature());
					break;
				case DEFAULT:
					if (trackColorGUI.getColorFeature().equals(GreenColorByFeatureGUIPanel.MANUAL_KEY)) {
						newValue = manualEdgeColorGenerator;
					} else {
						newValue = trackColorGenerator;
						newValue.setFeature(null);
					}
					break;
				default:
					throw new IllegalArgumentException("Unknow track color generator category: " + category);
				}
				displaySettings.put(KEY_TRACK_COLORING, newValue);
				// new value vs old value does not really hold.
				final DisplaySettingsEvent event = new DisplaySettingsEvent(trackColorGUI, KEY_TRACK_COLORING, newValue,
						oldValue);
				fireDisplaySettingsChange(event);
			}
		});
		final GroupLayout gl_jPanelTrackOptions = new GroupLayout(jPanelTrackOptions);
		gl_jPanelTrackOptions.setHorizontalGroup(gl_jPanelTrackOptions.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_jPanelTrackOptions.createSequentialGroup().addGap(5)
						.addGroup(gl_jPanelTrackOptions.createParallelGroup(Alignment.TRAILING)
								.addComponent(jComboBoxDisplayMode, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(gl_jPanelTrackOptions.createSequentialGroup()
										.addComponent(jCheckBoxLimitDepth, GroupLayout.DEFAULT_SIZE,
												GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addGap(6))
								.addGroup(gl_jPanelTrackOptions.createSequentialGroup()
										.addComponent(jLabelFrameDepth, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
										.addGap(5)
										.addComponent(jTextFieldFrameDepth, GroupLayout.DEFAULT_SIZE, 49,
												Short.MAX_VALUE)
										.addGap(108))
								.addComponent(jLabelTrackDisplayMode, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE))
						.addGap(8))
				.addGroup(
						gl_jPanelTrackOptions
								.createSequentialGroup().addContainerGap().addComponent(trackColorGUI,
										GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addContainerGap()));
		gl_jPanelTrackOptions.setVerticalGroup(gl_jPanelTrackOptions.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_jPanelTrackOptions.createSequentialGroup().addGap(5)
						.addComponent(jLabelTrackDisplayMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addGap(5)
						.addComponent(jComboBoxDisplayMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addGap(5)
						.addComponent(jCheckBoxLimitDepth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_jPanelTrackOptions.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_jPanelTrackOptions.createSequentialGroup().addGap(8).addComponent(
										jLabelFrameDepth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_jPanelTrackOptions.createSequentialGroup().addGap(5).addComponent(
										jTextFieldFrameDepth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(trackColorGUI, GroupLayout.PREFERRED_SIZE, 49, Short.MAX_VALUE).addGap(9)));
		jPanelTrackOptions.setLayout(gl_jPanelTrackOptions);

		if (GreenobjectColorGenerator != null) {
			jPanelGreenobjectColor.setColorFeature(GreenobjectColorGenerator.getFeature());
		}
		if (trackColorGenerator != null) {
			trackColorGUI.setColorFeature(trackColorGenerator.getFeature());
		}

		/*
		 * Units
		 */

		lblDrawingDepthUnits.setText(model.getSpaceUnits());

	}

	/*
	 * PRIVATE METHODS
	 */

	private void fireDisplaySettingsChange(final DisplaySettingsEvent event) {
		for (final DisplaySettingsListener listener : listeners) {
			listener.displaySettingsChanged(event);
		}
	}

	private void initGUI() {
		try {
			this.setPreferredSize(new Dimension(300, 521));
			this.setSize(300, 500);
			{
				jPanelTrackOptions = new JPanel() {
					private static final long serialVersionUID = -1805693239189343720L;

					@Override
					public void setEnabled(final boolean enabled) {
						for (final Component c : getComponents())
							c.setEnabled(enabled);
					}
				};
				jPanelTrackOptions.setBorder(new LineBorder(new java.awt.Color(192, 192, 192), 1, true));
				{
					jLabelTrackDisplayMode = new JLabel();
					jLabelTrackDisplayMode.setText("  Track display mode:");
					jLabelTrackDisplayMode.setBounds(10, 163, 268, 15);
					jLabelTrackDisplayMode.setFont(FONT);
					jLabelTrackDisplayMode.setPreferredSize(new java.awt.Dimension(261, 14));
				}
				{
					final String[] keyNames = TrackMateModelView.TRACK_DISPLAY_MODE_DESCRIPTION;
					final ComboBoxModel<String> jComboBoxDisplayModeModel = new DefaultComboBoxModel<>(keyNames);
					jComboBoxDisplayMode = new JComboBox<>();
					jComboBoxDisplayMode.setModel(jComboBoxDisplayModeModel);
					jComboBoxDisplayMode.setSelectedIndex(0);
					jComboBoxDisplayMode.setFont(SMALL_FONT);
					jComboBoxDisplayMode.setPreferredSize(new java.awt.Dimension(265, 27));
					jComboBoxDisplayMode.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							final Integer oldValue = (Integer) displaySettings.get(KEY_TRACK_DISPLAY_MODE);
							final Integer newValue = jComboBoxDisplayMode.getSelectedIndex();
							displaySettings.put(KEY_TRACK_DISPLAY_MODE, newValue);

							final DisplaySettingsEvent event = new DisplaySettingsEvent(GreenConfigureViewsPanel.this,
									KEY_TRACK_DISPLAY_MODE, newValue, oldValue);
							fireDisplaySettingsChange(event);
						}
					});
				}
				{
					jCheckBoxLimitDepth = new JCheckBox();
					jCheckBoxLimitDepth.setText("Limit frame depth");
					jCheckBoxLimitDepth.setBounds(6, 216, 272, 23);
					jCheckBoxLimitDepth.setFont(FONT);
					jCheckBoxLimitDepth.setSelected(true);
					jCheckBoxLimitDepth.setPreferredSize(new java.awt.Dimension(259, 23));
					jCheckBoxLimitDepth.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							Integer depth;
							if (jCheckBoxLimitDepth.isSelected()) {
								depth = NumberParser.parseInteger(jTextFieldFrameDepth.getText());
							} else {
								depth = (int) 1e9;
							}
							final Integer oldValue = (Integer) displaySettings.get(KEY_TRACK_DISPLAY_DEPTH);
							displaySettings.put(KEY_TRACK_DISPLAY_DEPTH, depth);

							final DisplaySettingsEvent event = new DisplaySettingsEvent(GreenConfigureViewsPanel.this,
									KEY_TRACK_DISPLAY_DEPTH, depth, oldValue);
							fireDisplaySettingsChange(event);
						}
					});
				}
				{
					jLabelFrameDepth = new JLabel();
					jLabelFrameDepth.setText("  Frame depth:");
					jLabelFrameDepth.setFont(SMALL_FONT);
					jLabelFrameDepth.setPreferredSize(new java.awt.Dimension(103, 14));
				}
				{
					displaySettings.put(KEY_TRACK_DISPLAY_DEPTH,
							Integer.valueOf(TrackMateModelView.DEFAULT_TRACK_DISPLAY_DEPTH));

					jTextFieldFrameDepth = new JTextField();
					jTextFieldFrameDepth.setHorizontalAlignment(SwingConstants.CENTER);
					jTextFieldFrameDepth.setFont(SMALL_FONT);
					jTextFieldFrameDepth.setText("" + TrackMateModelView.DEFAULT_TRACK_DISPLAY_DEPTH);
					jTextFieldFrameDepth.setPreferredSize(new java.awt.Dimension(34, 20));
					jTextFieldFrameDepth.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							final Integer oldValue = (Integer) displaySettings.get(KEY_TRACK_DISPLAY_DEPTH);
							try {
								final Integer depth = NumberParser.parseInteger(jTextFieldFrameDepth.getText());
								displaySettings.put(KEY_TRACK_DISPLAY_DEPTH, depth);

								final DisplaySettingsEvent event = new DisplaySettingsEvent(GreenConfigureViewsPanel.this,
										KEY_TRACK_DISPLAY_DEPTH, depth, oldValue);
								fireDisplaySettingsChange(event);
							} catch (final NumberFormatException nfe) {
								jTextFieldFrameDepth.setText("" + oldValue);
							}
						}
					});
				}
			}
			{
				jCheckBoxDisplayTracks = new JCheckBox();
				jCheckBoxDisplayTracks.setText("Display tracks");
				jCheckBoxDisplayTracks.setFont(FONT);
				jCheckBoxDisplayTracks.setSelected(true);
				jCheckBoxDisplayTracks.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						final Boolean oldValue = (Boolean) displaySettings.get(KEY_TRACKS_VISIBLE);
						final Boolean newValue = jCheckBoxDisplayTracks.isSelected();
						displaySettings.put(KEY_TRACKS_VISIBLE, newValue);

						final DisplaySettingsEvent event = new DisplaySettingsEvent(GreenConfigureViewsPanel.this,
								KEY_TRACKS_VISIBLE, newValue, oldValue);
						fireDisplaySettingsChange(event);
					}
				});
			}
			{
				jCheckBoxDisplayGreenobjects = new JCheckBox();
				jCheckBoxDisplayGreenobjects.setText("Display Greenobjects");
				jCheckBoxDisplayGreenobjects.setFont(FONT);
				jCheckBoxDisplayGreenobjects.setSelected(true);
				jCheckBoxDisplayGreenobjects.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						final Boolean oldValue = (Boolean) displaySettings.get(KEY_GreenobjectS_VISIBLE);
						final Boolean newValue = jCheckBoxDisplayGreenobjects.isSelected();
						displaySettings.put(KEY_GreenobjectS_VISIBLE, newValue);

						final DisplaySettingsEvent event = new DisplaySettingsEvent(GreenConfigureViewsPanel.this,
								KEY_GreenobjectS_VISIBLE, newValue, oldValue);
						fireDisplaySettingsChange(event);
					}
				});
			}
			{
				jPanelGreenobjectOptions = new JPanel() {
					private static final long serialVersionUID = 1L;

					@Override
					public void setEnabled(final boolean enabled) {
						for (final Component c : getComponents())
							c.setEnabled(enabled);
					}
				};
				jPanelGreenobjectOptions.setBorder(new LineBorder(new java.awt.Color(192, 192, 192), 1, true));
				{
					jLabelGreenobjectRadius = new JLabel();
					jLabelGreenobjectRadius.setText("  Greenobject display radius ratio:");
					jLabelGreenobjectRadius.setFont(SMALL_FONT);

					jTextFieldGreenobjectRadius = new JNumericTextField("1");
					jTextFieldGreenobjectRadius.setHorizontalAlignment(SwingConstants.CENTER);
					jTextFieldGreenobjectRadius.setPreferredSize(new java.awt.Dimension(34, 20));
					jTextFieldGreenobjectRadius.setFont(SMALL_FONT);
					jTextFieldGreenobjectRadius.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							final Double oldValue = (Double) displaySettings.get(KEY_Greenobject_RADIUS_RATIO);
							final Double newValue = (double) jTextFieldGreenobjectRadius.getValue();
							displaySettings.put(KEY_Greenobject_RADIUS_RATIO, newValue);

							final DisplaySettingsEvent event = new DisplaySettingsEvent(GreenConfigureViewsPanel.this,
									KEY_Greenobject_RADIUS_RATIO, newValue, oldValue);
							fireDisplaySettingsChange(event);
						}
					});
					jTextFieldGreenobjectRadius.addFocusListener(new FocusListener() {
						@Override
						public void focusLost(final FocusEvent e) {
							final Double oldValue = (Double) displaySettings.get(KEY_Greenobject_RADIUS_RATIO);
							final Double newValue = (double) jTextFieldGreenobjectRadius.getValue();
							displaySettings.put(KEY_Greenobject_RADIUS_RATIO, newValue);

							final DisplaySettingsEvent event = new DisplaySettingsEvent(GreenConfigureViewsPanel.this,
									KEY_Greenobject_RADIUS_RATIO, newValue, oldValue);
							fireDisplaySettingsChange(event);
						}

						@Override
						public void focusGained(final FocusEvent e) {
						}
					});
				}
				{
					jCheckBoxDisplayNames = new JCheckBox();
					jCheckBoxDisplayNames.setText("Display Greenobject names");
					jCheckBoxDisplayNames.setFont(SMALL_FONT);
					jCheckBoxDisplayNames.setSelected(false);
					jCheckBoxDisplayNames.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							final Boolean oldValue = (Boolean) displaySettings.get(KEY_DISPLAY_Greenobject_NAMES);
							final Boolean newValue = jCheckBoxDisplayNames.isSelected();
							displaySettings.put(KEY_DISPLAY_Greenobject_NAMES, newValue);

							final DisplaySettingsEvent event = new DisplaySettingsEvent(GreenConfigureViewsPanel.this,
									KEY_DISPLAY_Greenobject_NAMES, newValue, oldValue);
							fireDisplaySettingsChange(event);
						}
					});
				}
			}
			{
				/*
				 * DRAWING DEPTH
				 */

				jpanelDrawingDepth = new JPanel() {
					private static final long serialVersionUID = 1L;

					@Override
					public void setEnabled(final boolean enabled) {
						for (final Component c : getComponents())
							c.setEnabled(enabled);
					}
				};
				final FlowLayout flowLayout = (FlowLayout) jpanelDrawingDepth.getLayout();
				flowLayout.setAlignment(FlowLayout.LEFT);
				jpanelDrawingDepth.setBorder(new LineBorder(new java.awt.Color(192, 192, 192), 1, true));

				final JCheckBox chckbxDrawingDepth = new JCheckBox("Limit drawing depth");
				chckbxDrawingDepth.setFont(SMALL_FONT);
				chckbxDrawingDepth.setSelected(TrackMateModelView.DEFAULT_LIMIT_DRAWING_DEPTH);
				jpanelDrawingDepth.add(chckbxDrawingDepth);
				chckbxDrawingDepth.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						final Boolean oldValue = (Boolean) displaySettings.get(KEY_LIMIT_DRAWING_DEPTH);
						final Boolean newValue = chckbxDrawingDepth.isSelected();

						textFieldDrawingDepth.setEnabled(newValue);
						lblDrawingDepthUnits.setEnabled(newValue);

						displaySettings.put(KEY_LIMIT_DRAWING_DEPTH, newValue);

						final DisplaySettingsEvent event = new DisplaySettingsEvent(GreenConfigureViewsPanel.this,
								KEY_LIMIT_DRAWING_DEPTH, newValue, oldValue);
						fireDisplaySettingsChange(event);
					}
				});

				textFieldDrawingDepth = new JNumericTextField(TrackMateModelView.DEFAULT_DRAWING_DEPTH);
				textFieldDrawingDepth.setFormat("%.1f");
				textFieldDrawingDepth.setHorizontalAlignment(SwingConstants.CENTER);
				textFieldDrawingDepth.setFont(SMALL_FONT);
				jpanelDrawingDepth.add(textFieldDrawingDepth);
				textFieldDrawingDepth.setColumns(7);
				textFieldDrawingDepth.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						final Double oldValue = (Double) displaySettings.get(KEY_DRAWING_DEPTH);
						final Double newValue = textFieldDrawingDepth.getValue();
						displaySettings.put(KEY_DRAWING_DEPTH, newValue);

						final DisplaySettingsEvent event = new DisplaySettingsEvent(GreenConfigureViewsPanel.this,
								KEY_DRAWING_DEPTH, newValue, oldValue);
						fireDisplaySettingsChange(event);
					}
				});
				textFieldDrawingDepth.addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent e) {
						final Double oldValue = (Double) displaySettings.get(KEY_Greenobject_RADIUS_RATIO);
						final Double newValue = textFieldDrawingDepth.getValue();
						displaySettings.put(KEY_DRAWING_DEPTH, newValue);

						final DisplaySettingsEvent event = new DisplaySettingsEvent(GreenConfigureViewsPanel.this,
								KEY_DRAWING_DEPTH, newValue, oldValue);
						fireDisplaySettingsChange(event);
					}

					@Override
					public void focusGained(final FocusEvent e) {
					}
				});

				lblDrawingDepthUnits = new JLabel(model.getSpaceUnits());
				lblDrawingDepthUnits.setFont(SMALL_FONT);
				jpanelDrawingDepth.add(lblDrawingDepthUnits);

				textFieldDrawingDepth.setEnabled(chckbxDrawingDepth.isSelected());
				lblDrawingDepthUnits.setEnabled(chckbxDrawingDepth.isSelected());
			}
			{
				jLabelDisplayOptions = new JLabel();
				jLabelDisplayOptions.setText("Display options");
				jLabelDisplayOptions.setFont(BIG_FONT);
				jLabelDisplayOptions.setHorizontalAlignment(SwingConstants.LEFT);
			}

			jPanelButtons = new JPanel();
			jPanelButtons.setLayout(new WrapLayout());
			{
				jButtonShowTrackScheme = new JButton();
				jPanelButtons.add(jButtonShowTrackScheme);
				jButtonShowTrackScheme.setText("TrackScheme");
				// jButtonShowTrackScheme.setIcon( TRACK_SCHEME_ICON_16x16 );
				jButtonShowTrackScheme.setFont(FONT);
				jButtonShowTrackScheme.setToolTipText(TRACKSCHEME_BUTTON_TOOLTIP);
				{
					jButtonDoAnalysis = new JButton("Analysis");
					jPanelButtons.add(jButtonDoAnalysis);
					jButtonDoAnalysis.setFont(FONT);
					jButtonDoAnalysis.setIcon(DO_ANALYSIS_ICON);
					jButtonDoAnalysis.setToolTipText(ANALYSIS_BUTTON_TOOLTIP);
					jButtonDoAnalysis.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent event) {
							if ((event.getModifiers() & ActionEvent.SHIFT_MASK) != 0)
								fireAction(DO_ANALYSIS_BUTTON_WITH_SHIFT_PRESSED);
							else
								fireAction(DO_ANALYSIS_BUTTON_PRESSED);
						}
					});
				}
				jButtonShowTrackScheme.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						fireAction(TRACK_SCHEME_BUTTON_PRESSED);
					}
				});
			}

			final GroupLayout groupLayout = new GroupLayout(this);
			groupLayout
					.setHorizontalGroup(
							groupLayout.createParallelGroup(Alignment.TRAILING)
									.addGroup(groupLayout.createSequentialGroup().addGap(14)
											.addComponent(jLabelDisplayOptions, GroupLayout.DEFAULT_SIZE, 280,
													Short.MAX_VALUE)
											.addGap(6))
									.addGroup(groupLayout.createSequentialGroup().addGap(10)
											.addComponent(jCheckBoxDisplayGreenobjects, GroupLayout.DEFAULT_SIZE, 280,
													Short.MAX_VALUE)
											.addGap(10))
									.addGroup(groupLayout.createSequentialGroup().addGap(10)
											.addComponent(jPanelGreenobjectOptions, GroupLayout.PREFERRED_SIZE, 280,
													Short.MAX_VALUE)
											.addGap(10))
									.addGroup(groupLayout.createSequentialGroup().addGap(10)
											.addComponent(jCheckBoxDisplayTracks, GroupLayout.DEFAULT_SIZE, 284,
													Short.MAX_VALUE)
											.addContainerGap())
									.addGroup(groupLayout
											.createSequentialGroup().addGap(10)
											.addComponent(jPanelTrackOptions, GroupLayout.PREFERRED_SIZE, 280,
													Short.MAX_VALUE)
											.addGap(10))
									.addGroup(groupLayout.createSequentialGroup().addGap(10)
											.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
													.addComponent(jPanelButtons, Alignment.LEADING,
															GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
													.addComponent(jpanelDrawingDepth, Alignment.LEADING,
															GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
											.addGap(10)));
			groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
					.createSequentialGroup().addGap(6)
					.addComponent(jLabelDisplayOptions, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addGap(4).addComponent(jCheckBoxDisplayGreenobjects).addGap(2)
					.addComponent(jPanelGreenobjectOptions, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
					.addGap(4).addComponent(jCheckBoxDisplayTracks).addGap(1)
					.addComponent(jPanelTrackOptions, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(jpanelDrawingDepth, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(jPanelButtons, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE).addContainerGap()));

			setLayout(groupLayout);

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	protected void resizeButtons() {
		final Component[] buttons = jPanelButtons.getComponents();
		int maxWidth = -1;
		int maxHeight = -1;
		for (final Component button : buttons) {
			final Dimension btd = button.getPreferredSize();
			if (btd.width > maxWidth) {
				maxWidth = btd.width;
			}
			if (btd.height > maxHeight) {
				maxHeight = btd.height;
			}
		}
		final Dimension size = new Dimension(maxWidth, maxHeight);
		for (final Component button : buttons) {
			button.setPreferredSize(size);
		}
	}

	
}
