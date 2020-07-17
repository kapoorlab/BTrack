package Buddy.plugin.trackmate.visualization.trackscheme;

import java.util.HashMap;
import java.util.Set;

import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

import greenDetector.Greenobject;
import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.Model;

public class GreenJGraphXAdapter extends mxGraph implements GraphListener<Greenobject, DefaultWeightedEdge> {

	private final HashMap<Greenobject, mxCell> vertexToCellMap = new HashMap<>();

	private final HashMap<DefaultWeightedEdge, mxCell> edgeToCellMap = new HashMap<>();

	private final HashMap<mxCell, Greenobject> cellToVertexMap = new HashMap<>();

	private final HashMap<mxCell, DefaultWeightedEdge> cellToEdgeMap = new HashMap<>();

	private final GreenModel tmm;

	/*
	 * CONSTRUCTOR
	 */

	public GreenJGraphXAdapter(final GreenModel tmm) {
		super();
		this.tmm = tmm;
		insertTrackCollection(tmm);
	}

	/*
	 * METHODS
	 */

	/**
	 * Overridden method so that when a label is changed, we change the target
	 * Greenobject's name.
	 */
	@Override
	public void cellLabelChanged(final Object cell, final Object value, final boolean autoSize) {
		model.beginUpdate();
		try {
			final Greenobject Greenobject = cellToVertexMap.get(cell);
			if (null == Greenobject)
				return;
			final String str = (String) value;
			Greenobject.setName(str);
			getModel().setValue(cell, str);

			if (autoSize) {
				cellSizeUpdated(cell, false);
			}
		} finally {
			model.endUpdate();
		}
	}

	public mxCell addJGraphTVertex(final Greenobject vertex) {
		if (vertexToCellMap.containsKey(vertex)) {
			// cell for Greenobject already existed, skip creation and return original
			// cell.
			return vertexToCellMap.get(vertex);
		}
		mxCell cell = null;
		getModel().beginUpdate();
		try {
			cell = new mxCell(vertex, new mxGeometry(), "");
			cell.setVertex(true);
			cell.setId(null);
			cell.setValue(vertex.getName());
			addCell(cell, defaultParent);
			vertexToCellMap.put(vertex, cell);
			cellToVertexMap.put(cell, vertex);
		} finally {
			getModel().endUpdate();
		}
		return cell;
	}

	public mxCell addJGraphTEdge(final DefaultWeightedEdge edge) {
		if (edgeToCellMap.containsKey(edge)) {
			// cell for edge already existed, skip creation and return original
			// cell.
			return edgeToCellMap.get(edge);
		}
		mxCell cell = null;
		getModel().beginUpdate();
		try {
			final Greenobject source = tmm.getTrackModel().getEdgeSource(edge);
			final Greenobject target = tmm.getTrackModel().getEdgeTarget(edge);
			cell = new mxCell(edge);
			cell.setEdge(true);
			cell.setId(null);
			cell.setValue(String.format("%.1f", tmm.getTrackModel().getEdgeWeight(edge)));
			cell.setGeometry(new mxGeometry());
			cell.getGeometry().setRelative(true);
			addEdge(cell, defaultParent, vertexToCellMap.get(source), vertexToCellMap.get(target), null);
			edgeToCellMap.put(edge, cell);
			cellToEdgeMap.put(cell, edge);
		} finally {
			getModel().endUpdate();
		}
		return cell;
	}

	public void mapEdgeToCell(final DefaultWeightedEdge edge, final mxCell cell) {
		cellToEdgeMap.put(cell, edge);
		edgeToCellMap.put(edge, cell);
	}

	public Greenobject getGreenobjectFor(final mxICell cell) {
		return cellToVertexMap.get(cell);
	}

	public DefaultWeightedEdge getEdgeFor(final mxICell cell) {
		return cellToEdgeMap.get(cell);
	}

	public mxCell getCellFor(final Greenobject Greenobject) {
		return vertexToCellMap.get(Greenobject);
	}

	public mxCell getCellFor(final DefaultWeightedEdge edge) {
		return edgeToCellMap.get(edge);
	}

	public Set<mxCell> getVertexCells() {
		return cellToVertexMap.keySet();
	}

	public Set<mxCell> getEdgeCells() {
		return cellToEdgeMap.keySet();
	}

	public void removeMapping(final Greenobject Greenobject) {
		final mxICell cell = vertexToCellMap.remove(Greenobject);
		cellToVertexMap.remove(cell);
	}

	public void removeMapping(final DefaultWeightedEdge edge) {
		final mxICell cell = edgeToCellMap.remove(edge);
		cellToEdgeMap.remove(cell);
	}

	/*
	 * GRAPH LISTENER
	 */

	@Override
	public void vertexAdded(final GraphVertexChangeEvent<Greenobject> e) {
		addJGraphTVertex(e.getVertex());
	}

	@Override
	public void vertexRemoved(final GraphVertexChangeEvent<Greenobject> e) {
		final mxCell cell = vertexToCellMap.remove(e.getVertex());
		removeCells(new Object[] { cell });
	}

	@Override
	public void edgeAdded(final GraphEdgeChangeEvent<Greenobject, DefaultWeightedEdge> e) {
		addJGraphTEdge(e.getEdge());
	}

	@Override
	public void edgeRemoved(final GraphEdgeChangeEvent<Greenobject, DefaultWeightedEdge> e) {
		final mxICell cell = edgeToCellMap.remove(e.getEdge());
		removeCells(new Object[] { cell });
	}

	/*
	 * PRIVATE METHODS
	 */

	/**
	 * Only insert Greenobject and edges belonging to visible tracks. Any other
	 * Greenobject or edges will be ignored by the whole trackscheme framework, and
	 * if they are needed, they will have to be imported "by hand".
	 */
	private void insertTrackCollection(final GreenModel lTmm) {
		model.beginUpdate();
		try {
			for (final Integer trackID : lTmm.getTrackModel().trackIDs(true)) {
				for (final Greenobject vertex : lTmm.getTrackModel().trackGreenobjects(trackID))
					addJGraphTVertex(vertex);

				for (final DefaultWeightedEdge edge : lTmm.getTrackModel().trackEdges(trackID))
					addJGraphTEdge(edge);
			}
		} finally {
			model.endUpdate();
		}

	}

}
