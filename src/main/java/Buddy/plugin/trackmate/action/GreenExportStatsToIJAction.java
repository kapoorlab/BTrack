package Buddy.plugin.trackmate.action;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.scijava.plugin.Plugin;

import Buddy.plugin.trackmate.FeatureModel;
import Buddy.plugin.trackmate.GreenFeatureModel;
import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenSelectionModel;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.features.edges.EdgeTargetAnalyzer;
import Buddy.plugin.trackmate.features.edges.EdgeTimeLocationAnalyzer;
import Buddy.plugin.trackmate.features.edges.GreenEdgeTargetAnalyzer;
import Buddy.plugin.trackmate.features.edges.GreenEdgeTimeLocationAnalyzer;
import Buddy.plugin.trackmate.gui.GreenTrackMateGUIController;
import Buddy.plugin.trackmate.gui.TrackMateGUIController;
import Buddy.plugin.trackmate.gui.TrackMateWizard;
import Buddy.plugin.trackmate.util.GreenModelTools;
import Buddy.plugin.trackmate.util.ModelTools;
import greenDetector.Greenobject;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.text.TextPanel;
import ij.text.TextWindow;

public class GreenExportStatsToIJAction extends AbstractTMAction {

	public static final String NAME = "Export statistics to tables";

	public static final String KEY = "EXPORT_STATS_TO_IJ";

	public static final String INFO_TEXT = "<html>" + "Compute and export all statistics to 3 ImageJ results table. "
			+ "Statistisc are separated in features computed for: " + "<ol> "
			+ "	<li> Greenobjects in filtered tracks; " + "	<li> links between those Greenobjects; "
			+ "	<li> filtered tracks. " + "</ol> "
			+ "For tracks and links, they are recalculated prior to exporting. Note "
			+ "that Greenobjects and links that are not in a filtered tracks are not part " + "of this export."
			+ "</html>";

	private static final String Greenobject_TABLE_NAME = "Greenobjects in tracks statistics";

	private static final String EDGE_TABLE_NAME = "Links in tracks statistics";

	private static final String TRACK_TABLE_NAME = "Track statistics";

	private static final String ID_COLUMN = "ID";

	private static final String TRACK_ID_COLUMN = "TRACK_ID";

	private ResultsTable GreenobjectTable;

	private ResultsTable edgeTable;

	private ResultsTable trackTable;

	private final GreenSelectionModel selectionModel;

	public GreenExportStatsToIJAction(final GreenSelectionModel selectionModel) {
		this.selectionModel = selectionModel;
	}

	@Override
	public void execute(final TrackMate trackmate) {
		logger.log("Exporting statistics.\n");

		// Model
		final GreenModel model = trackmate.getGreenModel();
		final GreenFeatureModel fm = model.getFeatureModel();

		// Export Greenobjects
		logger.log("  - Exporting Greenobject statistics...");
		final Set<Integer> trackIDs = model.getTrackModel().trackIDs(true);
		final Collection<String> GreenobjectFeatures = trackmate.getGreenModel().getFeatureModel().getGreenobjectFeatures();

		this.GreenobjectTable = new ResultsTable();

		// Parse Greenobjects to insert values as objects
		for (final Integer trackID : trackIDs) {
			final Set<Greenobject> track = model.getTrackModel().trackGreenobjects(trackID);
			// Sort by frame
			final List<Greenobject> sortedTrack = new ArrayList<>(track);
			Collections.sort(sortedTrack, Greenobject.frameComparator);

			for (final Greenobject Greenobject : sortedTrack) {
				GreenobjectTable.incrementCounter();
				GreenobjectTable.addLabel(Greenobject.getName());
				GreenobjectTable.addValue(ID_COLUMN, "" + Greenobject.ID());
				GreenobjectTable.addValue("TRACK_ID", "" + trackID.intValue());
				for (final String feature : GreenobjectFeatures) {
					final Double val = Greenobject.getFeature(feature);
					if (null == val) {
						GreenobjectTable.addValue(feature, "None");
					} else {
						if (fm.getGreenobjectFeatureIsInt().get(feature).booleanValue()) {
							GreenobjectTable.addValue(feature, "" + val.intValue());
						} else {
							GreenobjectTable.addValue(feature, val.doubleValue());
						}
					}
				}
			}
		}
		logger.log(" Done.\n");

		// Export edges
		logger.log("  - Exporting links statistics...");
		// Yield available edge feature
		final Collection<String> edgeFeatures = fm.getEdgeFeatures();

		this.edgeTable = new ResultsTable();

		// Sort by track
		for (final Integer trackID : trackIDs) {
			// Comparators
			final Comparator<DefaultWeightedEdge> edgeTimeComparator = GreenModelTools
					.featureEdgeComparator(GreenEdgeTimeLocationAnalyzer.TIME, fm);
			final Comparator<DefaultWeightedEdge> edgeSourceGreenobjectTimeComparator = new EdgeSourceGreenobjectFrameComparator(
					model);

			final Set<DefaultWeightedEdge> track = model.getTrackModel().trackEdges(trackID);
			final List<DefaultWeightedEdge> sortedTrack = new ArrayList<>(track);

			/*
			 * Sort them by frame, if the EdgeTimeLocationAnalyzer feature is declared.
			 */

			if (model.getFeatureModel().getEdgeFeatures().contains(EdgeTimeLocationAnalyzer.KEY))
				Collections.sort(sortedTrack, edgeTimeComparator);
			else
				Collections.sort(sortedTrack, edgeSourceGreenobjectTimeComparator);

			for (final DefaultWeightedEdge edge : sortedTrack) {
				edgeTable.incrementCounter();
				edgeTable.addLabel(edge.toString());
				edgeTable.addValue(TRACK_ID_COLUMN, "" + trackID.intValue());
				for (final String feature : edgeFeatures) {
					final Object o = fm.getEdgeFeature(edge, feature);
					if (o instanceof String) {
						continue;
					}
					final Number d = (Number) o;
					if (d == null) {
						edgeTable.addValue(feature, "None");
					} else {
						if (fm.getEdgeFeatureIsInt().get(feature).booleanValue()) {
							edgeTable.addValue(feature, "" + d.intValue());
						} else {
							edgeTable.addValue(feature, d.doubleValue());
						}

					}
				}

			}
		}
		logger.log(" Done.\n");

		// Export tracks
		logger.log("  - Exporting tracks statistics...");
		// Yield available edge feature
		final Collection<String> trackFeatures = fm.getTrackFeatures();

		this.trackTable = new ResultsTable();

		// Sort by track
		for (final Integer trackID : trackIDs) {
			trackTable.incrementCounter();
			trackTable.addLabel(model.getTrackModel().name(trackID));
			trackTable.addValue(TRACK_ID_COLUMN, "" + trackID.intValue());
			for (final String feature : trackFeatures) {
				final Double val = fm.getTrackFeature(trackID, feature);
				if (null == val) {
					trackTable.addValue(feature, "None");
				} else {
					if (fm.getTrackFeatureIsInt().get(feature).booleanValue()) {
						trackTable.addValue(feature, "" + val.intValue());
					} else {
						trackTable.addValue(feature, val.doubleValue());
					}
				}
			}
		}
		logger.log(" Done.\n");

		// Show tables
		GreenobjectTable.show(Greenobject_TABLE_NAME);
		edgeTable.show(EDGE_TABLE_NAME);
		trackTable.show(TRACK_TABLE_NAME);

		// Hack to make the results tables in sync with selection model.
		if (null != selectionModel) {

			/*
			 * Greenobject table listener.
			 */

			final TextWindow GreenobjectTableWindow = (TextWindow) WindowManager.getWindow(Greenobject_TABLE_NAME);
			final TextPanel GreenobjectTableTextPanel = GreenobjectTableWindow.getTextPanel();
			GreenobjectTableTextPanel.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseReleased(final MouseEvent e) {
					final int selStart = GreenobjectTableTextPanel.getSelectionStart();
					final int selEnd = GreenobjectTableTextPanel.getSelectionEnd();
					if (selStart < 0 || selEnd < 0)
						return;

					final int minLine = Math.min(selStart, selEnd);
					final int maxLine = Math.max(selStart, selEnd);
					final Set<Greenobject> Greenobjects = new HashSet<>();
					for (int row = minLine; row <= maxLine; row++) {
						final int GreenobjectID = Integer
								.parseInt(GreenobjectTableTextPanel.getResultsTable().getStringValue(ID_COLUMN, row));
						final Greenobject Greenobject = model.getGreenobjects().search(GreenobjectID);
						if (null != Greenobject)
							Greenobjects.add(Greenobject);
					}
					selectionModel.clearSelection();
					selectionModel.addGreenobjectToSelection(Greenobjects);
				}
			});

			/*
			 * Edge table listener.
			 */

			/*
			 * We can only retrieve edges if the table contains the source and target ID
			 * columns.
			 */

			final int sourceIDColumn = edgeTable.getColumnIndex(GreenEdgeTargetAnalyzer.Greenobject_SOURCE_ID);
			final int targetIDColumn = edgeTable.getColumnIndex(GreenEdgeTargetAnalyzer.Greenobject_TARGET_ID);
			if (sourceIDColumn != ResultsTable.COLUMN_NOT_FOUND && targetIDColumn != ResultsTable.COLUMN_NOT_FOUND) {

				final TextWindow edgeTableWindow = (TextWindow) WindowManager.getWindow(EDGE_TABLE_NAME);
				final TextPanel edgeTableTextPanel = edgeTableWindow.getTextPanel();
				edgeTableTextPanel.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseReleased(final MouseEvent e) {
						final int selStart = edgeTableTextPanel.getSelectionStart();
						final int selEnd = edgeTableTextPanel.getSelectionEnd();
						if (selStart < 0 || selEnd < 0)
							return;

						final int minLine = Math.min(selStart, selEnd);
						final int maxLine = Math.max(selStart, selEnd);
						final Set<DefaultWeightedEdge> edges = new HashSet<>();
						for (int row = minLine; row <= maxLine; row++) {
							final int sourceID = Integer
									.parseInt(edgeTableTextPanel.getResultsTable().getStringValue(sourceIDColumn, row));
							final Greenobject source = model.getGreenobjects().search(sourceID);
							final int targetID = Integer
									.parseInt(edgeTableTextPanel.getResultsTable().getStringValue(targetIDColumn, row));
							final Greenobject target = model.getGreenobjects().search(targetID);
							final DefaultWeightedEdge edge = model.getTrackModel().getEdge(source, target);
							if (null != edge)
								edges.add(edge);
						}
						selectionModel.clearSelection();
						selectionModel.addEdgeToSelection(edges);
					}
				});
			}

			/*
			 * Track table listener.
			 */

			final TextWindow trackTableWindow = (TextWindow) WindowManager.getWindow(TRACK_TABLE_NAME);
			final TextPanel trackTableTextPanel = trackTableWindow.getTextPanel();
			trackTableTextPanel.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseReleased(final MouseEvent e) {
					final int selStart = trackTableTextPanel.getSelectionStart();
					final int selEnd = trackTableTextPanel.getSelectionEnd();
					if (selStart < 0 || selEnd < 0)
						return;

					final int minLine = Math.min(selStart, selEnd);
					final int maxLine = Math.max(selStart, selEnd);
					final Set<DefaultWeightedEdge> edges = new HashSet<>();
					final Set<Greenobject> Greenobjects = new HashSet<>();
					for (int row = minLine; row <= maxLine; row++) {
						final int trackID = Integer
								.parseInt(trackTableTextPanel.getResultsTable().getStringValue(TRACK_ID_COLUMN, row));
						Greenobjects.addAll(model.getTrackModel().trackGreenobjects(trackID));
						edges.addAll(model.getTrackModel().trackEdges(trackID));
					}
					selectionModel.clearSelection();
					selectionModel.addGreenobjectToSelection(Greenobjects);
					selectionModel.addEdgeToSelection(edges);
				}
			});
		}
	}

	/**
	 * Returns the results table containing the Greenobject statistics, or
	 * <code>null</code> if the {@link #execute(TrackMate)} method has not been
	 * called.
	 *
	 * @return the results table containing the Greenobject statistics.
	 */
	public ResultsTable getGreenobjectTable() {
		return GreenobjectTable;
	}

	/**
	 * Returns the results table containing the edge statistics, or
	 * <code>null</code> if the {@link #execute(TrackMate)} method has not been
	 * called.
	 *
	 * @return the results table containing the edge statistics.
	 */
	public ResultsTable getEdgeTable() {
		return edgeTable;
	}

	/**
	 * Returns the results table containing the track statistics, or
	 * <code>null</code> if the {@link #execute(TrackMate)} method has not been
	 * called.
	 *
	 * @return the results table containing the track statistics.
	 */
	public ResultsTable getTrackTable() {
		return trackTable;
	}

	// Invisible because called on the view config panel.
	@Plugin(type = GreenTrackMateActionFactory.class, visible = false)
	public static class Factory implements GreenTrackMateActionFactory {

		@Override
		public String getInfoText() {
			return INFO_TEXT;
		}

		@Override
		public String getKey() {
			return KEY;
		}

		@Override
		public TrackMateAction create(final GreenTrackMateGUIController controller) {
			return new GreenExportStatsToIJAction(controller.getSelectionModel());
		}

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public ImageIcon getIcon() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final class EdgeSourceGreenobjectFrameComparator implements Comparator<DefaultWeightedEdge> {

		private final GreenModel model;

		public EdgeSourceGreenobjectFrameComparator(final GreenModel model) {
			this.model = model;
		}

		@Override
		public int compare(final DefaultWeightedEdge e1, final DefaultWeightedEdge e2) {
			final double t1 = model.getTrackModel().getEdgeSource(e1).getFeature(Greenobject.POSITION_T).doubleValue();
			final double t2 = model.getTrackModel().getEdgeSource(e2).getFeature(Greenobject.POSITION_T).doubleValue();
			if (t1 < t2) {
				return -1;
			}
			if (t1 > t2) {
				return 1;
			}
			return 0;
		}

	}

}
