package tracker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import budDetector.Budobject;
import budDetector.Budpointobject;
import budDetector.Distance;
import ij.ImageStack;
import kalmanGUI.CovistoKalmanPanel;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.BoundaryTrack;
import pluginTools.InteractiveBud;
import zGUI.CovistoZselectPanel;

public class TrackResult extends SwingWorker<Void, Void> {

	final InteractiveBud parent;

	public TrackResult(final InteractiveBud parent) {

		this.parent = parent;
	}

	@Override
	protected Void doInBackground() throws Exception {

		parent.jpb.setIndeterminate(false);
		parent.Cardframe.validate();

		parent.prestack = new ImageStack((int) parent.originalimg.dimension(0), (int) parent.originalimg.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());

		
		
		TrackingFunctions track = new TrackingFunctions(parent);
		SimpleWeightedGraph<Budpointobject, DefaultWeightedEdge> simplegraph = track.Trackfunction();
		
		// Seperate graph for buds
		SimpleWeightedGraph<Budobject, DefaultWeightedEdge> Budsimplegraph = track.BudTrackfunction();
		
		
		// Display Graph results, make table etc
		DisplayGraph(simplegraph);
		BudDisplayGraph(Budsimplegraph);
	
		if(parent.jpb!=null )
			utility.BudProgressBar.SetProgressBar(parent.jpb, 100 ,
					"Bud Tracking Done" );
		
		

		return null;
	}

	
	protected void DisplayGraph(SimpleWeightedGraph<Budpointobject, DefaultWeightedEdge> simplegraph) {

		int minid = Integer.MAX_VALUE;
		int maxid = Integer.MIN_VALUE;
		TrackModel model = new TrackModel(simplegraph);

	
		for (final Integer id : model.trackIDs(false)) {
			if (id > maxid)
				maxid = id;

			if (id < minid)
				minid = id;
		}
		if (minid != Integer.MAX_VALUE) {

			for (final Integer id : model.trackIDs(true)) {

				Comparator<Pair<String, Budpointobject>> ThirdDimcomparison = new Comparator<Pair<String, Budpointobject>>() {

					@Override
					public int compare(final Pair<String, Budpointobject> A, final Pair<String, Budpointobject> B) {

						return A.getB().t - B.getB().t;

					}

				};

				String ID = Integer.toString(id);
				model.setName(id, "Track" + id);
				parent.Globalmodel = model;
				final HashSet<Budpointobject> Angleset = model.trackBudpointobjects(id);
				if (Angleset.size() > (CovistoKalmanPanel.trackduration/100.0) * parent.thirdDimensionSize) {
			
				Iterator<Budpointobject> Angleiter = Angleset.iterator();
			
				
				Budpointobject previousbud = null;
				while (Angleiter.hasNext()) {
					
					
                    double velocity = 0; 
					Budpointobject currentbud = Angleiter.next();
					if(previousbud!=null) { 
							velocity = Math.sqrt(Distance.DistanceSq(currentbud.Location, previousbud.Location));
					
					velocity = velocity * (parent.calibration/parent.timecal);
					Budpointobject newbud = new Budpointobject(currentbud.Budcenter, currentbud.linelist, currentbud.dynamiclinelist,currentbud.perimeter, currentbud.label, currentbud.Location, currentbud.t, velocity);
					parent.Tracklist.add(new ValuePair<String, Budpointobject>(ID, newbud));
					}
					previousbud = currentbud;
				}
				
				
				Collections.sort(parent.Tracklist, ThirdDimcomparison);
				

				}
			}

	
			
			
			CreateTableView(parent);
			DisplaySelectedTrack.Select(parent);
			DisplaySelectedTrack.Mark(parent);

		}
	}
	
	
	protected void BudDisplayGraph(SimpleWeightedGraph<Budobject, DefaultWeightedEdge> simplegraph) {

		int minid = Integer.MAX_VALUE;
		int maxid = Integer.MIN_VALUE;
		BudTrackModel model = new BudTrackModel(simplegraph);

	
		for (final Integer id : model.trackIDs(false)) {
			if (id > maxid)
				maxid = id;

			if (id < minid)
				minid = id;
		}
		if (minid != Integer.MAX_VALUE) {

			for (final Integer id : model.trackIDs(true)) {

				Comparator<Pair<String, Budobject>> ThirdDimcomparison = new Comparator<Pair<String, Budobject>>() {

					@Override
					public int compare(final Pair<String, Budobject> A, final Pair<String, Budobject> B) {

						return A.getB().t - B.getB().t;

					}

				};

				String ID = Integer.toString(id);
				model.setName(id, "Track" + id);
				parent.BudGlobalModel = model;
				final HashSet<Budobject> Angleset = model.trackBudobjects(id);
				if (Angleset.size() > (CovistoKalmanPanel.trackduration/100.0) * parent.thirdDimensionSize) {
			
				Iterator<Budobject> Angleiter = Angleset.iterator();
				while (Angleiter.hasNext()) {
					
					
                    double velocity = 0; 
                    Budobject currentbud = Angleiter.next();
					
					velocity = velocity * (parent.calibration/parent.timecal);
					parent.BudTracklist.add(new ValuePair<String, Budobject>(ID, currentbud));
				}
				Collections.sort(parent.BudTracklist, ThirdDimcomparison);
				

				}
			}

	
			
			

		}
	}
	public void CreateTableView(InteractiveBud parent) {



		Object[] colnames = new Object[] { "Track Id", "Location X", "Location Y", "Location T", "Growth Rate" };

		Object[][] rowvalues = new Object[0][colnames.length];

		rowvalues = new Object[parent.Tracklist.size()][colnames.length];

		parent.table = new JTable(rowvalues, colnames);
		parent.row = 0;
		NumberFormat f = NumberFormat.getInstance();
		
		
		HashMap<String, Boolean> LabelCovered = new HashMap<String, Boolean>();
		
for (ValuePair<String, Budpointobject> Track: parent.Tracklist) {
			
			String ID = Track.getA();
		LabelCovered.put(ID, false);
		
}
		
		for (ValuePair<String, Budpointobject> Track: parent.Tracklist) {
			
			String ID = Track.getA();
			
			Budpointobject currentbud = Track.getB();
			
			if(LabelCovered.get(ID)!=null)
				if(!LabelCovered.get(ID))
			if(currentbud.t == parent.thirdDimension) {
				
			parent.table.getModel().setValueAt(ID, parent.row, 0);
			parent.table.getModel().setValueAt(f.format(currentbud.Location[0]), parent.row, 1);
			parent.table.getModel().setValueAt(f.format(currentbud.Location[1]), parent.row, 2);
			parent.table.getModel().setValueAt(f.format(currentbud.t), parent.row, 3);
			parent.table.getModel().setValueAt(f.format(currentbud.velocity), parent.row, 4);
			parent.row++;

			parent.tablesize = parent.row;
			LabelCovered.put(ID, true);
			}
			
			
		}
		

		makeGUI(parent);

	}
	
	
	public static void makeGUI(final InteractiveBud parent) {
		
		
		  parent.PanelSelectFile.removeAll();
			
			parent.table.setFillsViewportHeight(true);

			parent.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		

			int size = 100;
			parent.table.getColumnModel().getColumn(0).setPreferredWidth(size);
			parent.table.getColumnModel().getColumn(1).setPreferredWidth(size);
			parent.table.getColumnModel().getColumn(2).setPreferredWidth(size);
			parent.table.getColumnModel().getColumn(3).setPreferredWidth(size);
			parent.table.getColumnModel().getColumn(4).setPreferredWidth(size);
			parent.table.setPreferredScrollableViewportSize(parent.table.getPreferredSize());
			
			parent.table.setMinimumSize(parent.table.getPreferredSize());

			parent.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			parent.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			parent.scrollPane = new JScrollPane(parent.table);

			parent.scrollPane.getViewport().add(parent.table);
			parent.scrollPane.setAutoscrolls(true);
			
			parent.PanelSelectFile.add(parent.scrollPane, BorderLayout.CENTER);

			parent.PanelSelectFile.setBorder(parent.selectfile);

			
			parent.table.isOpaque();
			parent.scrollPane.setMinimumSize(new Dimension(300, 200));
			parent.scrollPane.setPreferredSize(new Dimension(300, 200));
		
		parent.table.repaint();
		parent.table.validate();
		
		parent.scrollPane.repaint();
		parent.scrollPane.validate();
		parent.PanelSelectFile.repaint();
		parent.PanelSelectFile.validate();
		parent.panelFirst.repaint();
		parent.panelFirst.validate();
		parent.Cardframe.repaint();
		parent.Cardframe.validate();

	}
}