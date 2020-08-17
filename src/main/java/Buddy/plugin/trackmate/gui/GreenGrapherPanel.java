package Buddy.plugin.trackmate.gui;

import Buddy.plugin.trackmate.GreenTrackMate;
import Buddy.plugin.trackmate.Spot;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.features.GreenobjectFeatureGrapher;
import Buddy.plugin.trackmate.features.EdgeFeatureGrapher;
import Buddy.plugin.trackmate.features.GreenEdgeFeatureGrapher;
import Buddy.plugin.trackmate.features.GreenTrackFeatureGrapher;
import Buddy.plugin.trackmate.features.TrackFeatureGrapher;
import Buddy.plugin.trackmate.features.edges.EdgeTimeLocationAnalyzer;
import Buddy.plugin.trackmate.features.track.TrackIndexAnalyzer;
import Buddy.plugin.trackmate.gui.panels.ActionListenablePanel;
import Buddy.plugin.trackmate.gui.panels.components.FeaturePlotSelectionPanel;
import greenDetector.Greenobject;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.jgrapht.graph.DefaultWeightedEdge;


public class GreenGrapherPanel extends ActionListenablePanel {

	private static final ImageIcon SPOT_ICON = new ImageIcon(
			GreenGrapherPanel.class.getResource("images/Icon1_print_transparency.png"));

	private static final ImageIcon EDGE_ICON = new ImageIcon(
			GreenGrapherPanel.class.getResource("images/Icon2_print_transparency.png"));

	private static final ImageIcon TRACK_ICON = new ImageIcon(
			GreenGrapherPanel.class.getResource("images/Icon3b_print_transparency.png"));

	public static final ImageIcon SPOT_ICON_64x64;

	public static final ImageIcon EDGE_ICON_64x64;

	public static final ImageIcon TRACK_ICON_64x64;

	static {
		final Image image1 = SPOT_ICON.getImage();
		final Image newimg1 = image1.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
		SPOT_ICON_64x64 = new ImageIcon(newimg1);

		final Image image2 = EDGE_ICON.getImage();
		final Image newimg2 = image2.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
		EDGE_ICON_64x64 = new ImageIcon(newimg2);

		final Image image3 = TRACK_ICON.getImage();
		final Image newimg3 = image3.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
		TRACK_ICON_64x64 = new ImageIcon(newimg3);
	}

	private static final long serialVersionUID = 1L;

	private final GreenTrackMate trackmate;

	private final JPanel panelSpot;

	private final JPanel panelEdges;

	private final JPanel panelTracks;

	private FeaturePlotSelectionPanel spotFeatureSelectionPanel;

	private FeaturePlotSelectionPanel edgeFeatureSelectionPanel;

	private FeaturePlotSelectionPanel trackFeatureSelectionPanel;

	/*
	 * CONSTRUCTOR
	 */

	public GreenGrapherPanel(final GreenTrackMate trackmate) {
		this.trackmate = trackmate;

		setLayout(new BorderLayout(0, 0));

		final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		add(tabbedPane, BorderLayout.CENTER);

		panelSpot = new JPanel();
		tabbedPane.addTab("Spots", SPOT_ICON_64x64, panelSpot, null);
		panelSpot.setLayout(new BorderLayout(0, 0));

		panelEdges = new JPanel();
		tabbedPane.addTab("Links", EDGE_ICON_64x64, panelEdges, null);
		panelEdges.setLayout(new BorderLayout(0, 0));

		panelTracks = new JPanel();
		tabbedPane.addTab("Tracks", TRACK_ICON_64x64, panelTracks, null);
		panelTracks.setLayout(new BorderLayout(0, 0));

		refresh();
	}

	public void refresh() {
		// regen spot features
		panelSpot.removeAll();
		final Collection<String> spotFeatures = trackmate.getGreenModel().getFeatureModel().getGreenobjectFeatures();
		final Map<String, String> spotFeatureNames = trackmate.getGreenModel().getFeatureModel()
				.getGreenobjectFeatureNames();
		spotFeatureSelectionPanel = new FeaturePlotSelectionPanel(Spot.POSITION_T, spotFeatures, spotFeatureNames);
		panelSpot.add(spotFeatureSelectionPanel);
		spotFeatureSelectionPanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				spotFeatureSelectionPanel.setEnabled(false);
				new Thread("TrackMate plot spot features thread") {
					@Override
					public void run() {
						plotSpotFeatures();
						spotFeatureSelectionPanel.setEnabled(true);
					}
				}.start();
			}
		});

		// regen edge features
		panelEdges.removeAll();
		final Collection<String> edgeFeatures = trackmate.getGreenModel().getFeatureModel().getEdgeFeatures();
		final Map<String, String> edgeFeatureNames = trackmate.getGreenModel().getFeatureModel().getEdgeFeatureNames();
		edgeFeatureSelectionPanel = new FeaturePlotSelectionPanel(EdgeTimeLocationAnalyzer.TIME, edgeFeatures,
				edgeFeatureNames);
		panelEdges.add(edgeFeatureSelectionPanel);
		edgeFeatureSelectionPanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				edgeFeatureSelectionPanel.setEnabled(false);
				new Thread("TrackMate plot edge features thread") {
					@Override
					public void run() {
						plotEdgeFeatures();
						edgeFeatureSelectionPanel.setEnabled(true);
					}
				}.start();
			}
		});

		// regen trak features
		panelTracks.removeAll();
		final Collection<String> trackFeatures = trackmate.getGreenModel().getFeatureModel().getTrackFeatures();
		final Map<String, String> trackFeatureNames = trackmate.getGreenModel().getFeatureModel().getTrackFeatureNames();
		trackFeatureSelectionPanel = new FeaturePlotSelectionPanel(TrackIndexAnalyzer.TRACK_INDEX, trackFeatures,
				trackFeatureNames);
		panelTracks.add(trackFeatureSelectionPanel);
		trackFeatureSelectionPanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				trackFeatureSelectionPanel.setEnabled(false);
				new Thread("TrackMate plot track features thread") {
					@Override
					public void run() {
						plotTrackFeatures();
						trackFeatureSelectionPanel.setEnabled(true);
					}
				}.start();
			}
		});
	}

	private void plotSpotFeatures() {
		final String xFeature = spotFeatureSelectionPanel.getXKey();
		final Set<String> yFeatures = spotFeatureSelectionPanel.getYKeys();
		// Collect only the spots that are in tracks
		final List<Greenobject> spots = new ArrayList<>(trackmate.getGreenModel().getGreenobjects().getNGreenobjects());
		for (final Integer trackID : trackmate.getGreenModel().getTrackModel().trackIDs(true)) {
			spots.addAll(trackmate.getGreenModel().getTrackModel().trackGreenobjects(trackID));
		}
		final GreenobjectFeatureGrapher grapher = new GreenobjectFeatureGrapher(xFeature, yFeatures, spots,
				trackmate.getGreenModel());
		grapher.render();
	}

	private void plotEdgeFeatures() {
		// Collect edges in filtered tracks
		final List<DefaultWeightedEdge> edges = new ArrayList<>();
		for (final Integer trackID : trackmate.getGreenModel().getTrackModel().trackIDs(true)) {
			edges.addAll(trackmate.getGreenModel().getTrackModel().trackEdges(trackID));
		}
		// Prepare grapher
		final String xFeature = edgeFeatureSelectionPanel.getXKey();
		final Set<String> yFeatures = edgeFeatureSelectionPanel.getYKeys();
		final GreenEdgeFeatureGrapher grapher = new GreenEdgeFeatureGrapher(xFeature, yFeatures, edges, trackmate.getGreenModel());
		grapher.render();
	}

	private void plotTrackFeatures() {
		// Prepare grapher
		final String xFeature = trackFeatureSelectionPanel.getXKey();
		final Set<String> yFeatures = trackFeatureSelectionPanel.getYKeys();
		final GreenTrackFeatureGrapher grapher = new GreenTrackFeatureGrapher(xFeature, yFeatures, trackmate.getGreenModel());
		grapher.render();
	}

}
