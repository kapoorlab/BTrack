package Buddy.plugin.trackmate.gui.panels.components;

import static Buddy.plugin.trackmate.gui.TrackMateWizard.BIG_FONT;
import static Buddy.plugin.trackmate.gui.TrackMateWizard.SMALL_FONT;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.features.FeatureFilter;
import Buddy.plugin.trackmate.gui.TrackMateWizard;
import Buddy.plugin.trackmate.gui.panels.ActionListenablePanel;
import Buddy.plugin.trackmate.gui.panels.components.ColorByFeatureGUIPanel.Category;
import Buddy.plugin.trackmate.util.OnRequestUpdater;

public class FilterGuiPanel extends ActionListenablePanel implements ChangeListener {

	private static final boolean DEBUG = false;

	private static final long serialVersionUID = -1L;

	private static final String ADD_ICON = "images/add.png";

	private static final String REMOVE_ICON = "images/delete.png";

	private final ChangeEvent CHANGE_EVENT = new ChangeEvent(this);

	public ActionEvent COLOR_FEATURE_CHANGED = null;

	private JPanel jPanelBottom;

	private ColorByFeatureGUIPanel jPanelColorByFeatureGUI;

	private JScrollPane jScrollPaneThresholds;

	private JPanel jPanelAllThresholds;

	private JPanel jPanelButtons;

	private JButton jButtonRemoveThreshold;

	private JButton jButtonAddThreshold;

	private JLabel jLabelInfo;

	private JLabel jTopLabel;

	private final OnRequestUpdater updater;

	private final Stack<Component> struts = new Stack<>();

	private int newFeatureIndex;

	private List<FeatureFilter> featureFilters = new ArrayList<>();

	private final ArrayList<ChangeListener> changeListeners = new ArrayList<>();

	private final Map<String, String> featureNames;

	private final List<Category> categories;

	private final Model model;

	private final List<String> features;

	/**
	 * Holds the map of feature values. Is made final so that the instance can be
	 * shared with the components of this panel.
	 */
	private final Map<String, double[]> featureValues;

	/*
	 * CONSTRUCTOR
	 */

	public FilterGuiPanel(final Model model, final List<Category> categories) {
		this.model = model;
		this.categories = categories;

		this.features = new ArrayList<>();
		this.featureNames = new HashMap<>();
		for (final Category category : categories) {
			switch (category) {

			case EDGES:
				features.addAll(model.getFeatureModel().getEdgeFeatures());
				featureNames.putAll(model.getFeatureModel().getEdgeFeatureNames());
				break;
			case TRACKS:
				features.addAll(model.getFeatureModel().getTrackFeatures());
				featureNames.putAll(model.getFeatureModel().getTrackFeatureNames());
				break;
			case DEFAULT:
				// We do not want to allow filtering on manual or default
				// categories.
				// features.add( ColorByFeatureGUIPanel.UNIFORM_KEY );
				featureNames.put(ColorByFeatureGUIPanel.UNIFORM_KEY, ColorByFeatureGUIPanel.UNIFORM_NAME);
				break;
			default:
				throw new IllegalArgumentException("Unkown category: " + category);
			}
		}

		this.updater = new OnRequestUpdater(new OnRequestUpdater.Refreshable() {
			@Override
			public void refresh() {
				FilterGuiPanel.this.refresh();
			}
		});

		this.featureValues = new HashMap<>();
		refreshDisplayedFeatureValues();
		initGUI();
	}

	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Calls the re-calculation of the feature values displayed in the filter
	 * panels.
	 */
	public void refreshDisplayedFeatureValues() {
		featureValues.clear();
		for (final Category category : categories) {
			switch (category) {

			case TRACKS:
				featureValues.putAll(model.getFeatureModel().getTrackFeatureValues());
				break;
			case DEFAULT:
				break;
			case EDGES:
				throw new IllegalArgumentException("Edge filtering is not implemented.");
			default:
				throw new IllegalArgumentException("Don't know what to do with category: " + category);
			}
		}
	}

	/**
	 * Called when one of the {@link FilterPanel} is changed by the user.
	 */
	@Override
	public void stateChanged(final ChangeEvent e) {
		updater.doUpdate();
	}

	/**
	 * Returns the thresholds currently set by this GUI.
	 */
	public List<FeatureFilter> getFeatureFilters() {
		return featureFilters;
	}

	/**
	 * Returns the feature selected in the "color by feature" comb-box.
	 */
	public String getColorFeature() {
		return jPanelColorByFeatureGUI.getColorFeature();
	}

	public Category getColorCategory() {
		return jPanelColorByFeatureGUI.getColorGeneratorCategory();
	}

	public void setColorFeature(final String feature) {
		jPanelColorByFeatureGUI.setColorFeature(feature);
	}

	/**
	 * Add an {@link ChangeListener} to this panel. The {@link ChangeListener} will
	 * be notified when a change happens to the thresholds displayed by this panel,
	 * whether due to the slider being move, the auto-threshold button being
	 * pressed, or the combo-box selection being changed.
	 */
	public void addChangeListener(final ChangeListener listener) {
		changeListeners.add(listener);
	}

	/**
	 * Remove a ChangeListener from this panel.
	 *
	 * @return true if the listener was in listener collection of this instance.
	 */
	public boolean removeChangeListener(final ChangeListener listener) {
		return changeListeners.remove(listener);
	}

	public Collection<ChangeListener> getChangeListeners() {
		return changeListeners;
	}

	/*
	 * PRIVATE METHODS
	 */

	private void fireThresholdChanged(final ChangeEvent e) {
		for (final ChangeListener cl : changeListeners) {
			cl.stateChanged(e);
		}
	}

	public void addFilterPanel() {
		addFilterPanel(features.get(newFeatureIndex));
	}

	public void addFilterPanel(final FeatureFilter filter) {
		if (null == filter)
			return;

		final int filterIndex = features.indexOf(filter.feature);
		newFeatureIndex++;
		if (newFeatureIndex >= features.size())
			newFeatureIndex = 0;
		final Component strut = Box.createVerticalStrut(5);
		struts.push(strut);
		jPanelAllThresholds.add(strut);
		jPanelAllThresholds.revalidate();
		stateChanged(CHANGE_EVENT);
	}

	public void addFilterPanel(final String feature) {
		if (null == featureValues)
			return;
		newFeatureIndex++;
		if (newFeatureIndex >= features.size())
			newFeatureIndex = 0;
		final Component strut = Box.createVerticalStrut(5);
		struts.push(strut);
		jPanelAllThresholds.add(strut);
		jPanelAllThresholds.revalidate();
		stateChanged(CHANGE_EVENT);
	}

	private void removeThresholdPanel() {
		try {
			final Component strut = struts.pop();
			jPanelAllThresholds.remove(strut);
			jPanelAllThresholds.repaint();
			stateChanged(CHANGE_EVENT);
		} catch (final EmptyStackException ese) {
		}
	}

	private void refresh() {
		if (DEBUG) {
			System.out.println("[FilterGuiPanel] #refresh()");
		}

		fireThresholdChanged(null);
		updateInfoText();
	}

	private void updateInfoText() {
		String info = "";
		int nobjects = 0;

		for (final double[] values : featureValues.values()) { // bulletproof against unspecified features, which are
																// signaled by
																// empty arrays
			if (values.length > 0) {
				nobjects = values.length;
				break;
			}
		}

		if (nobjects == 0) {
			info = "No objects.";
		} else if (featureFilters == null || featureFilters.isEmpty()) {
			info = "Keep all " + nobjects + " " + categories.get(0) + ".";
		} else {
			int nselected = 0;
			double val;
			for (int i = 0; i < nobjects; i++) {
				boolean ok = true;
				for (final FeatureFilter filter : featureFilters) {
					final double[] values = featureValues.get(filter.feature);
					if (i >= values.length || values.length == 0) { // bulletproof
						continue;
					}
					val = values[i];
					if (filter.isAbove) {
						if (val < filter.value) {
							ok = false;
							break;
						}
					} else {
						if (val > filter.value) {
							ok = false;
							break;
						}
					}
				}
				if (ok)
					nselected++;
			}
			info = "Keep " + nselected + " " + categories.get(0) + " out of  " + nobjects + ".";
		}
		jLabelInfo.setText(info);

	}

	private void initGUI() {
		try {
			final BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			setPreferredSize(new Dimension(270, 500));
			{
				jTopLabel = new JLabel();
				jTopLabel.setText("      Set filters on " + categories.get(0).toString());
				jTopLabel.setFont(BIG_FONT);
				jTopLabel.setPreferredSize(new Dimension(300, 40));
				this.add(jTopLabel, BorderLayout.NORTH);
			}
			{
				jScrollPaneThresholds = new JScrollPane();
				this.add(jScrollPaneThresholds, BorderLayout.CENTER);
				jScrollPaneThresholds.setPreferredSize(new java.awt.Dimension(250, 389));
				jScrollPaneThresholds.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				jScrollPaneThresholds.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				{
					jPanelAllThresholds = new JPanel();
					final BoxLayout jPanelAllThresholdsLayout = new BoxLayout(jPanelAllThresholds, BoxLayout.Y_AXIS);
					jPanelAllThresholds.setLayout(jPanelAllThresholdsLayout);
					jScrollPaneThresholds.setViewportView(jPanelAllThresholds);
				}
			}
			{
				jPanelBottom = new JPanel();
				final BorderLayout jPanelBottomLayout = new BorderLayout();
				jPanelBottom.setLayout(jPanelBottomLayout);
				this.add(jPanelBottom, BorderLayout.SOUTH);
				jPanelBottom.setPreferredSize(new java.awt.Dimension(270, 71));
				{
					jPanelButtons = new JPanel();
					jPanelBottom.add(jPanelButtons, BorderLayout.NORTH);
					final BoxLayout jPanelButtonsLayout = new BoxLayout(jPanelButtons, javax.swing.BoxLayout.X_AXIS);
					jPanelButtons.setLayout(jPanelButtonsLayout);
					jPanelButtons.setPreferredSize(new java.awt.Dimension(270, 22));
					jPanelButtons.setSize(270, 25);
					jPanelButtons.setMaximumSize(new java.awt.Dimension(32767, 25));
					{
						jPanelButtons.add(Box.createHorizontalStrut(5));
						jButtonAddThreshold = new JButton();
						jPanelButtons.add(jButtonAddThreshold);
						jButtonAddThreshold.setIcon(new ImageIcon(TrackMateWizard.class.getResource(ADD_ICON)));
						jButtonAddThreshold.setFont(SMALL_FONT);
						jButtonAddThreshold.setPreferredSize(new java.awt.Dimension(24, 24));
						jButtonAddThreshold.setSize(24, 24);
						jButtonAddThreshold.setMinimumSize(new java.awt.Dimension(24, 24));
						jButtonAddThreshold.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(final ActionEvent e) {
								addFilterPanel();
							}
						});
					}
					{
						jPanelButtons.add(Box.createHorizontalStrut(5));
						jButtonRemoveThreshold = new JButton();
						jPanelButtons.add(jButtonRemoveThreshold);
						jButtonRemoveThreshold.setIcon(new ImageIcon(TrackMateWizard.class.getResource(REMOVE_ICON)));
						jButtonRemoveThreshold.setFont(SMALL_FONT);
						jButtonRemoveThreshold.setPreferredSize(new java.awt.Dimension(24, 24));
						jButtonRemoveThreshold.setSize(24, 24);
						jButtonRemoveThreshold.setMinimumSize(new java.awt.Dimension(24, 24));
						jButtonRemoveThreshold.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(final ActionEvent e) {
								removeThresholdPanel();
							}
						});
						jPanelButtons.add(Box.createHorizontalGlue());
					}
					{
						jPanelButtons.add(Box.createHorizontalStrut(5));
						jLabelInfo = new JLabel();
						jLabelInfo.setFont(SMALL_FONT);
						jPanelButtons.add(jLabelInfo);
					}
				}
				{
					jPanelColorByFeatureGUI = new ColorByFeatureGUIPanel(model, categories);
					COLOR_FEATURE_CHANGED = jPanelColorByFeatureGUI.COLOR_FEATURE_CHANGED;
					jPanelColorByFeatureGUI.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							fireAction(COLOR_FEATURE_CHANGED);
						}
					});
					jPanelBottom.add(jPanelColorByFeatureGUI);
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
