package Buddy.plugin.trackmate.visualization.trackscheme;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

import greenDetector.Greenobject;


public class GreenTrackSchemeActions {

	/**
	 * When panning with the keyboard, by how much pixels to move.
	 */
	private static final int PAN_AMOUNT = 100;

	private static final ImageIcon RESET_ZOOM_ICON = new ImageIcon(("../resources/zoom.png"));

	private static final ImageIcon ZOOM_IN_ICON = new ImageIcon(("../resources/zoom_in.png"));

	private static final ImageIcon ZOOM_OUT_ICON = new ImageIcon(("../resources/zoom_out.png"));

	private static final ImageIcon EDIT_ICON = new ImageIcon(("../resources/tag_blue_edit.png"));

	private static final ImageIcon HOME_ICON = new ImageIcon(("../resources/control_start.png"));

	private static final ImageIcon END_ICON = new ImageIcon(("../resources/control_end.png"));

	private static final ImageIcon ARROW_UP_ICON = new ImageIcon(("../resources/arrow_up.png"));

	private static final ImageIcon ARROW_DOWN_ICON = new ImageIcon(("../resources/arrow_down.png"));

	private static final ImageIcon ARROW_LEFT_ICON = new ImageIcon(("../resources/arrow_left.png"));

	private static final ImageIcon ARROW_RIGHT_ICON = new ImageIcon(("../resources/arrow_right.png"));

	private static final ImageIcon ARROW_UPLEFT_ICON = new ImageIcon(("../resources/arrow_nw.png"));

	private static final ImageIcon ARROW_DOWNLEFT_ICON = new ImageIcon(("../resources/arrow_sw.png"));

	private static final ImageIcon ARROW_UPRIGHT_ICON = new ImageIcon(("../resources/arrow_ne.png"));

	private static final ImageIcon ARROW_DOWNRIGHT_ICON = new ImageIcon(("../resources/arrow_se.png"));

	private static Action zoomInAction;

	static {
		zoomInAction = mxGraphActions.getZoomInAction();
		zoomInAction.putValue(Action.SMALL_ICON, ZOOM_IN_ICON);
	}

	private static Action zoomOutAction;

	static {
		zoomOutAction = mxGraphActions.getZoomOutAction();
		zoomOutAction.putValue(Action.SMALL_ICON, ZOOM_OUT_ICON);
	}

	private GreenTrackSchemeActions() {
	}

	public static Action getEditAction(final GreenTrackSchemeGraphComponent graphComponent) {
		return new EditAction("edit", EDIT_ICON, graphComponent);
	}

	public static Action getHomeAction(final GreenTrackSchemeGraphComponent graphComponent) {
		return new HomeAction("home", HOME_ICON, graphComponent);
	}

	public static Action getEndAction(final GreenTrackSchemeGraphComponent graphComponent) {
		return new EndAction("end", END_ICON, graphComponent);
	}

	public static Action getResetZoomAction(final GreenTrackSchemeGraphComponent graphComponent) {
		return new ResetZoomAction("resetZoom", RESET_ZOOM_ICON, graphComponent);
	}

	public static Action getZoomInAction(final GreenTrackSchemeGraphComponent graphComponent) {
		return new AbstractAction("zoomIn", ZOOM_IN_ICON) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				graphComponent.zoomIn();
			}
		};
	}

	public static Action getZoomOutAction(final GreenTrackSchemeGraphComponent graphComponent) {
		return new AbstractAction("zoomOut", ZOOM_OUT_ICON) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				graphComponent.zoomOut();
			}
		};
	}

	public static Action getSelectNoneAction() {
		return mxGraphActions.getSelectNoneAction();
	}

	public static Action getSelectAllAction() {
		return mxGraphActions.getSelectAllAction();
	}

	public static Action getPanDownAction(final GreenTrackSchemeGraphComponent graphComponent) {
		return new PanAction("panDown", ARROW_DOWN_ICON, graphComponent, 0, PAN_AMOUNT);
	}

	public static Action getPanLeftAction(final GreenTrackSchemeGraphComponent graphComponent) {
		return new PanAction("panLeft", ARROW_LEFT_ICON, graphComponent, -PAN_AMOUNT, 0);
	}

	public static Action getPanRightAction(final GreenTrackSchemeGraphComponent graphComponent) {
		return new PanAction("panRight", ARROW_RIGHT_ICON, graphComponent, PAN_AMOUNT, 0);
	}

	public static Action getPanUpAction(final GreenTrackSchemeGraphComponent graphComponent) {
		return new PanAction("panUp", ARROW_UP_ICON, graphComponent, 0, -PAN_AMOUNT);
	}

	public static Action getPanDownLeftAction(final GreenTrackSchemeGraphComponent graphComponent) {
		return new PanAction("panDownLeft", ARROW_DOWNLEFT_ICON, graphComponent, -PAN_AMOUNT, PAN_AMOUNT);
	}

	public static Action getPanDownRightAction(final GreenTrackSchemeGraphComponent graphComponent) {
		return new PanAction("panDownRight", ARROW_DOWNRIGHT_ICON, graphComponent, PAN_AMOUNT, PAN_AMOUNT);
	}

	public static Action getPanUpLeftAction(final GreenTrackSchemeGraphComponent graphComponent) {
		return new PanAction("panUpLeft", ARROW_UPLEFT_ICON, graphComponent, -PAN_AMOUNT, -PAN_AMOUNT);
	}

	public static Action getPanUpRightAction(final GreenTrackSchemeGraphComponent graphComponent) {
		return new PanAction("panUpRight", ARROW_UPRIGHT_ICON, graphComponent, PAN_AMOUNT, -PAN_AMOUNT);
	}

	/*
	 * ACTION CLASSES
	 */

	private static class PanAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private final int amountx;

		private final int amounty;

		private final GreenTrackSchemeGraphComponent graphComponent;

		public PanAction(final String name, final Icon icon, final GreenTrackSchemeGraphComponent graphComponent,
				final int amountx, final int amounty) {
			super(name, icon);
			this.graphComponent = graphComponent;
			this.amountx = amountx;
			this.amounty = amounty;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Rectangle r = graphComponent.getViewport().getViewRect();
			final int right = r.x + ((amountx < 0) ? 0 : r.width) + amountx;
			final int bottom = r.y + ((amounty < 0) ? 0 : r.height) + amounty;
			graphComponent.getGraphControl().scrollRectToVisible(new Rectangle(right, bottom, 0, 0));
		}
	}

	private static class ResetZoomAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private final GreenTrackSchemeGraphComponent graphComponent;

		public ResetZoomAction(final String name, final Icon icon, final GreenTrackSchemeGraphComponent graphComponent) {
			super(name, icon);
			this.graphComponent = graphComponent;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			graphComponent.zoomTo(1.0, false);
		}

	}

	/**
	 * Centers the view to the first cell in selection.
	 *
	 * @author Jean-Yves Tinevez &lt;jeanyves.tinevez@gmail.com&gt; Sep 12, 2013
	 *
	 */
	private static class HomeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private final GreenTrackSchemeGraphComponent graphComponent;

		public HomeAction(final String name, final Icon icon, final GreenTrackSchemeGraphComponent graphComponent) {
			super(name, icon);
			this.graphComponent = graphComponent;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {

			mxCell cell = null;
			final GreenJGraphXAdapter graph = graphComponent.getGraph();
			final List<mxCell> vertices = getSelectionVertices(graph);
			if (!vertices.isEmpty()) {
				int minFrame = Integer.MAX_VALUE;
				for (final mxCell mxCell : vertices) {
					final int frame = graph.getGreenobjectFor(mxCell).getFeature(Greenobject.POSITION_T).intValue();
					if (frame < minFrame) {
						minFrame = frame;
						cell = mxCell;
					}
				}
			} else {
				final List<mxCell> edges = getSelectionEdges(graph);
				if (!edges.isEmpty()) {
					int minFrame = Integer.MAX_VALUE;
					for (final mxCell mxCell : edges) {
						final mxICell target = mxCell.getTarget();
						final int frame = graph.getGreenobjectFor(target).getFeature(Greenobject.POSITION_T).intValue();
						if (frame < minFrame) {
							minFrame = frame;
							cell = mxCell;
						}
					}
					cell = edges.get(edges.size() - 1);
				} else {
					return;
				}
			}
			graphComponent.scrollCellToVisible(cell, true);
		}
	}

	/**
	 * Centers the view to the last cell in selection, sorted by frame number.
	 *
	 * @author Jean-Yves Tinevez &lt;jeanyves.tinevez@gmail.com&gt; Sep 12, 2013
	 *
	 */
	private static class EndAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private final GreenTrackSchemeGraphComponent graphComponent;

		public EndAction(final String name, final Icon icon, final GreenTrackSchemeGraphComponent graphComponent) {
			super(name, icon);
			this.graphComponent = graphComponent;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			mxCell cell = null;
			final GreenJGraphXAdapter graph = graphComponent.getGraph();
			final List<mxCell> vertices = getSelectionVertices(graph);

			if (!vertices.isEmpty()) {
				int maxFrame = Integer.MIN_VALUE;
				for (final mxCell mxCell : vertices) {
					final int frame = graph.getGreenobjectFor(mxCell).getFeature(Greenobject.POSITION_T).intValue();
					if (frame > maxFrame) {
						maxFrame = frame;
						cell = mxCell;
					}
				}
			} else {
				final List<mxCell> edges = getSelectionEdges(graph);
				if (!edges.isEmpty()) {
					int maxFrame = Integer.MIN_VALUE;
					for (final mxCell mxCell : edges) {
						final mxICell target = mxCell.getTarget();
						final int frame = graph.getGreenobjectFor(target).getFeature(Greenobject.POSITION_T).intValue();
						if (frame > maxFrame) {
							maxFrame = frame;
							cell = mxCell;
						}
					}
					cell = edges.get(edges.size() - 1);
				} else {
					return;
				}
			}
			graphComponent.scrollCellToVisible(cell, true);
		}
	}

	public static class EditAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private final GreenTrackSchemeGraphComponent graphComponent;

		public EditAction(final String name, final Icon icon, final GreenTrackSchemeGraphComponent graphComponent) {
			super(name, icon);
			this.graphComponent = graphComponent;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			multiEditGreenobjectName(graphComponent, e);
		}

		private void multiEditGreenobjectName(final GreenTrackSchemeGraphComponent lGraphComponent,
				final ActionEvent triggerEvent) {
			/*
			 * We want to display the editing window in the cell is the closer to where the
			 * user clicked. That is not perfect, because we can imagine the click is made
			 * for from the selected cells, and that the editing window will not even be
			 * displayed on the screen. No idea for that yet, because JGraphX is expecting
			 * to receive a cell as location for the editing window.
			 */
			final GreenJGraphXAdapter graph = lGraphComponent.getGraph();
			final List<mxCell> vertices = getSelectionVertices(graph);
			if (vertices.isEmpty()) {
				return;
			}

			final Point mousePosition = lGraphComponent.getMousePosition();
			final mxCell tc;
			if (null != mousePosition)
				tc = getClosestCell(vertices, mousePosition);
			else
				tc = vertices.get(0);
			vertices.remove(tc);

			lGraphComponent.startEditingAtCell(tc, triggerEvent);
			lGraphComponent.addListener(mxEvent.LABEL_CHANGED, new mxIEventListener() {

				@Override
				public void invoke(final Object sender, final mxEventObject evt) {
					for (final mxCell cell : vertices) {
						cell.setValue(tc.getValue());
						graph.getGreenobjectFor(cell).setName(tc.getValue().toString());
					}
					lGraphComponent.refresh();
					lGraphComponent.removeListener(this);
				}
			});
		}

		/**
		 * Return, from the given list of cell, the one which is the closer to the point
		 * of this instance.
		 *
		 * @param point
		 */
		private mxCell getClosestCell(final Iterable<mxCell> vertices, final Point2D point) {
			double min_dist = Double.POSITIVE_INFINITY;
			mxCell target_cell = null;
			for (final mxCell cell : vertices) {
				final Point location = cell.getGeometry().getPoint();
				final double dist = location.distanceSq(point);
				if (dist < min_dist) {
					min_dist = dist;
					target_cell = cell;
				}
			}
			return target_cell;
		}
	}

	/*
	 * PRIVATE STATIC METHODS
	 */

	private static List<mxCell> getSelectionVertices(final mxGraph graph) {
		// Build selection categories
		final Object[] selection = graph.getSelectionCells();
		final ArrayList<mxCell> vertices = new ArrayList<>();
		for (final Object obj : selection) {
			final mxCell cell = (mxCell) obj;
			if (cell.isVertex())
				vertices.add(cell);
		}
		return vertices;
	}

	private static List<mxCell> getSelectionEdges(final mxGraph graph) {
		// Build selection categories
		final Object[] selection = graph.getSelectionCells();
		final ArrayList<mxCell> edges = new ArrayList<>();
		for (final Object obj : selection) {
			final mxCell cell = (mxCell) obj;
			if (cell.isEdge())
				edges.add(cell);
		}
		return edges;
	}
}