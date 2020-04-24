package Buddy.plugin.trackmate.visualization.trackscheme;

import static Buddy.plugin.trackmate.visualization.trackscheme.TrackScheme.DEFAULT_CELL_HEIGHT;
import static Buddy.plugin.trackmate.visualization.trackscheme.TrackScheme.DEFAULT_CELL_WIDTH;
import static Buddy.plugin.trackmate.visualization.trackscheme.TrackScheme.X_COLUMN_SIZE;
import static Buddy.plugin.trackmate.visualization.trackscheme.TrackScheme.Y_COLUMN_SIZE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

import budDetector.BCellobject;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.graph.ConvexBranchesDecomposition;
import Buddy.plugin.trackmate.graph.ConvexBranchesDecomposition.TrackBranchDecomposition;
import Buddy.plugin.trackmate.graph.GraphUtils;
import Buddy.plugin.trackmate.graph.SortedDepthFirstIterator;
import Buddy.plugin.trackmate.graph.TimeDirectedNeighborIndex;
import net.imglib2.algorithm.Benchmark;

/**
 * This {@link mxGraphLayout} arranges cells on a graph in lanes corresponding
 * to tracks. It also sets the style of each cell so that they have a coloring
 * depending on the lane they belong to. Each lane's width and color is
 * available to other classes for further exploitation.
 *
 * @author Jean-Yves Tinevez &lt;jeanyves.tinevez@gmail.com&gt; - Mar 2011 -
 *         2012 - 2014
 *
 */
public class TrackSchemeGraphLayout extends mxGraphLayout implements Benchmark {

	private static final int START_COLUMN = 1;

	/** The target model to draw BCellobject from. */
	private final Model model;

	private final JGraphXAdapter graphAdapter;

	private final TrackSchemeGraphComponent component;

	/**
	 * Hold the current row length for each frame. That is, for frame
	 * <code>i</code>, the number of cells on the row corresponding to frame
	 * <code>i</code> is <code>rowLength.get(i)</code>. This field is regenerated
	 * after each call to {@link #execute(Object)}.
	 */
	private Map<Integer, Integer> rowLengths;

	private long processingTime;

	/*
	 * CONSTRUCTOR
	 */

	public TrackSchemeGraphLayout(final JGraphXAdapter graph, final Model model,
			final TrackSchemeGraphComponent component) {
		super(graph);
		this.graphAdapter = graph;
		this.model = model;
		this.component = component;
	}

	/*
	 * PUBLIC METHODS
	 */

	@Override
	public void execute(final Object lParent) {

		final long start = System.currentTimeMillis();

		/*
		 * To be able to deal with lonely cells later (i.e. cells that are not part of a
		 * track), we retrieve the list of all cells.
		 */
		final Object[] objs = graphAdapter.getChildVertices(graphAdapter.getDefaultParent());
		final ArrayList<mxCell> lonelyCells = new ArrayList<>(objs.length);
		for (final Object obj : objs) {
			lonelyCells.add((mxCell) obj);
		}

		/*
		 * Get a neighbor cache
		 */
		final TimeDirectedNeighborIndex neighborCache = model.getTrackModel().getDirectedNeighborIndex();

		/*
		 * Compute column width from recursive cumsum
		 */
		final Map<BCellobject, Integer> cumulativeBranchWidth = GraphUtils.cumulativeBranchWidth(model.getTrackModel());

		/*
		 * How many rows do we have to parse?
		 */
		final int maxFrame = model.getBCellobjects().lastKey();

		graphAdapter.getModel().beginUpdate();
		try {

			/*
			 * Pass n tracks info on component
			 */
			final int ntracks = model.getTrackModel().nTracks(true);
			component.columnWidths = new int[ntracks];
			component.columnTrackIDs = new Integer[ntracks];

			/*
			 * Initialize the column occupancy array
			 */
			final int[] columns = new int[maxFrame + 1];
			for (int i = 0; i < columns.length; i++) {
				columns[i] = START_COLUMN;
			}

			int trackIndex = 0;
			for (final Integer trackID : model.getTrackModel().trackIDs(true)) { // will be sorted by track name

				// Get Tracks
				final Set<BCellobject> track = model.getTrackModel().trackBCellobjects(trackID);

				// Pass name & trackID to component
				component.columnTrackIDs[trackIndex] = trackID;

				// Get first BCellobject
				final TreeSet<BCellobject> sortedTrack = new TreeSet<>(BCellobject.frameComparator);
				sortedTrack.addAll(track);
				final BCellobject first = sortedTrack.first();

				/*
				 * A special case: our quick layout below fails for graph that are not trees.
				 * That is: if a track has at least a BCellobject that has more than one
				 * predecessor. If we have to deal with such a case, we revert to the old, slow
				 * scheme.
				 */

				final boolean isTree = GraphUtils.isTree(track, neighborCache);

				if (isTree) {

					/*
					 * Quick layout for a tree-like track
					 */

					// First loop: Loop over BCellobjects in good order
					final SortedDepthFirstIterator<BCellobject, DefaultWeightedEdge> iterator = model.getTrackModel()
							.getSortedDepthFirstIterator(first, BCellobject.nameComparator, false);

					while (iterator.hasNext()) {

						final BCellobject BCellobject = iterator.next();

						// Get corresponding JGraphX cell, add it if it does not
						// exist in the JGraphX yet
						final mxICell cell = graphAdapter.getCellFor(BCellobject);

						// This is cell is in a track, remove it from the list
						// of lonely cells
						lonelyCells.remove(cell);

						// Determine in what row to put the BCellobject
						final int frame = BCellobject.getFeature(BCellobject.POSITION_T).intValue();

						// Cell size, position and style
						final int cellPos = columns[frame] + cumulativeBranchWidth.get(BCellobject) / 2;
						setCellGeometry(cell, frame, cellPos);
						columns[frame] += cumulativeBranchWidth.get(BCellobject);

						// If it is a leaf, we fill the remaining row below and
						// above
						if (neighborCache.successorsOf(BCellobject).size() == 0) {
							final int target = columns[frame];
							for (int i = 0; i <= maxFrame; i++) {
								columns[i] = target;
							}
						}

					}

				} else {

					/*
					 * Layout in branches for merging tracks
					 */

					final TrackBranchDecomposition branchDecomposition = ConvexBranchesDecomposition
							.processTrack(trackID, model.getTrackModel(), neighborCache, false, false);
					final SimpleDirectedGraph<List<BCellobject>, DefaultEdge> branchGraph = ConvexBranchesDecomposition
							.buildBranchGraph(branchDecomposition);
					final DepthFirstIterator<List<BCellobject>, DefaultEdge> depthFirstIterator = new DepthFirstIterator<>(
							branchGraph);

					while (depthFirstIterator.hasNext()) {
						final List<BCellobject> branch = depthFirstIterator.next();

						final int firstFrame = branch.get(0).getFeature(BCellobject.POSITION_T).intValue();
						final int lastFrame = branch.get(branch.size() - 1).getFeature(BCellobject.POSITION_T)
								.intValue();

						// Determine target column.
						int targetColumn = columns[firstFrame];
						for (final BCellobject BCellobject : branch) {
							final int sFrame = BCellobject.getFeature(BCellobject.POSITION_T).intValue();
							if (columns[sFrame] > targetColumn) {
								targetColumn = columns[sFrame];
							}
						}

						// Place BCellobjects.
						for (final BCellobject BCellobject : branch) {
							// Get corresponding JGraphX cell, add it if it does
							// not exist in the JGraphX yet
							final mxICell cell = graphAdapter.getCellFor(BCellobject);

							// This is cell is in a track, remove it from the
							// list of lonely cells
							lonelyCells.remove(cell);

							// Determine in what row to put the BCellobject
							final int frame = BCellobject.getFeature(BCellobject.POSITION_T).intValue();

							// Cell position
							setCellGeometry(cell, frame, targetColumn);
						}

						// Update column index.
						for (int frame = firstFrame; frame <= lastFrame; frame++) {
							columns[frame] = targetColumn + 1;
						}
					}
				}

				// When done with a track, move all columns to the next free
				// column
				int maxCol = 0;
				for (int j = 0; j < columns.length; j++) {
					if (columns[j] > maxCol) {
						maxCol = columns[j];
					}
				}
				for (int i = 0; i < columns.length; i++) {
					columns[i] = maxCol + 1;
				}

				// Store column widths for the panel background
				int sumWidth = START_COLUMN;
				for (int i = 0; i < trackIndex; i++) {
					sumWidth += component.columnWidths[i];
				}
				component.columnWidths[trackIndex] = maxCol - sumWidth;

				trackIndex++;
			} // loop over tracks

			// Deal with lonely cells
			for (final mxCell cell : lonelyCells) {
				final BCellobject BCellobject = graphAdapter.getBCellobjectFor(cell);
				final int frame = BCellobject.getFeature(BCellobject.POSITION_T).intValue();
				setCellGeometry(cell, frame, columns[frame]++);
			}

			// Before we leave, we regenerate the row length, for our brothers
			rowLengths = new HashMap<>(columns.length);
			for (int i = 0; i < columns.length; i++) {
				rowLengths.put(i, columns[i]);
			}

			// Move vertices cells to front, to make them easily selectable.
			final Object[] verticesCells = graphAdapter.getVertexCells().toArray();
			graphAdapter.cellsOrdered(verticesCells, false);

		} finally {
			graphAdapter.getModel().endUpdate();
		}

		final long end = System.currentTimeMillis();
		processingTime = end - start;
	}

	private final void setCellGeometry(final mxICell cell, final int row, final int targetColumn) {

		final double x = (targetColumn) * X_COLUMN_SIZE - DEFAULT_CELL_WIDTH / 2;
		final double y = (0.5 + row) * Y_COLUMN_SIZE - DEFAULT_CELL_HEIGHT / 2;
		final mxGeometry geometry = cell.getGeometry();
		geometry.setX(x);
		geometry.setY(y);
	}

	/**
	 * @return the current row length for each frame. That is, for frame
	 *         <code>i</code>, the number of cells on the row corresponding to frame
	 *         <code>i</code> is <code>rowLength.get(i)</code>. This field is
	 *         regenerated after each call to {@link #execute(Object)}.
	 */
	public Map<Integer, Integer> getRowLengths() {
		return rowLengths;
	}

	@Override
	public long getProcessingTime() {
		return processingTime;
	}
}