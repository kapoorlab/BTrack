package tracker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import budDetector.Budpointobject;
import budDetector.Distance;
import ij.gui.ImageCanvas;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveBud;
import utility.BudChartMaker;
import zGUI.CovistoZselectPanel;

public class BUDDYDisplaySelectedTrack {
	
	
	
	public static void Select(final InteractiveBud parent,HashMap<Integer, HashMap<Integer,Double>>  VelocityMap) {
		
		

		if (parent.mvl != null)
			parent.imp.getCanvas().removeMouseListener(parent.mvl);
		parent.imp.getCanvas().addMouseListener(parent.mvl = new MouseListener() {

			final ImageCanvas canvas = parent.imp.getWindow().getCanvas();

			@Override
			public void mouseClicked(MouseEvent e) {

			

			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());
				parent.Clickedpoints[0] = x;
				parent.Clickedpoints[1] = y;

				if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()) {
				
					
					//Still to write this function
					displayclicked(parent, parent.rowchoice);
					
					if (!parent.jFreeChartFrameRate.isVisible())
						parent.jFreeChartFrameRate = utility.BudChartMaker.display(parent.chartVelocity, new Dimension(500, 500));
				
					
					
				}
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});

	}
		
       public static void Mark(final InteractiveBud parent, HashMap<Integer, HashMap<Integer,Double>>  VelocityMap) {

    	   
			parent.Drawcolor = Color.ORANGE;

    	   
		parent.table.addMouseListener(parent.tvl = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent event) {
				
				
                Roi[] Roipoints = parent.imp.getOverlay().toArray();
				
				for(Roi point:Roipoints) {
				if(Roi.getColor() == parent.Drawcolor) {
					
					parent.imp.getOverlay().remove(point);
					
				}
				
				}
					parent.imp.updateAndDraw();
				
				
				    Point point = event.getPoint();
			        int row  = parent.table.rowAtPoint(point);
			
			        parent.rowchoice = row;
			        String ID = (String) parent.table.getValueAt(row, 0); 
			        String CordX = (String) parent.table.getValueAt(row, 1);
					String CordY = (String) parent.table.getValueAt(row, 2);
			        
					for (ValuePair<String, Budpointobject> Track: parent.Tracklist) {
						
						String ImageID = Track.getA();
						
						
						if(ID.equals(ImageID)) {
							
							
							OvalRoi points =  new OvalRoi((int) Integer.parseInt(CordX), (int) Integer.parseInt(CordY),
									25, 25);
							parent.imp.getOverlay().add(points);
							points.setStrokeColor(parent.Drawcolor);
							points.setStrokeWidth(40);
							parent.imp.updateAndDraw();
						}
						
						
						
					}
			   			parent.imp.updateAndDraw();
			        displayclicked(parent, row);
			    	if (!parent.jFreeChartFrameRate.isVisible())
						parent.jFreeChartFrameRate = utility.BudChartMaker.display(parent.chartVelocity, new Dimension(500, 500));
			    	parent.table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
						@Override
						public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
								boolean hasFocus, int row, int col) {

							super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
							if (row == parent.rowchoice) {
								setBackground(Color.green);

							} else {
								setBackground(Color.white);
							}
							return this;
						}
					});

					parent.table.validate();
					parent.scrollPane.validate();
					parent.panelFirst.repaint();
					parent.panelFirst.validate();
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
			
			
		});
    	   
    	   
		if (parent.ml != null)
			parent.imp.getCanvas().removeMouseMotionListener(parent.ml);
		parent.imp.getCanvas().addMouseMotionListener(parent.ml = new MouseMotionListener() {

			final ImageCanvas canvas = parent.imp.getWindow().getCanvas();

			@Override
			public void mouseMoved(MouseEvent e) {

				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());

				final HashMap<Integer, double[]> loc = new HashMap<Integer, double[]>();

				loc.put(0, new double[] { x, y });

				double distmin = Double.MAX_VALUE;
				if (parent.tablesize > 0 && parent.table.getRowCount() > 0) {
					NumberFormat f = NumberFormat.getInstance();
					for (int row = 0; row < parent.tablesize; ++row) {
						String CordX = (String) parent.table.getValueAt(row, 1);
						String CordY = (String) parent.table.getValueAt(row, 2);


						double dCordX = 0, dCordZ = 0, dCordY = 0;
						try {
							dCordX = f.parse(CordX).doubleValue();

							dCordY = f.parse(CordY).doubleValue();
						} catch (ParseException e1) {

						}
						double dist = Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y });
						if (Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y }) < distmin
								&& parent.ndims > 3) {

							parent.rowchoice = row;
							distmin = dist;

						}
						if (Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y }) < distmin
								&& parent.ndims <= 3) {

							parent.rowchoice = row;
							distmin = dist;

						}

					}

					parent.table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
						@Override
						public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
								boolean hasFocus, int row, int col) {

							super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
							if (row == parent.rowchoice) {
								setBackground(Color.green);

							} else {
								setBackground(Color.white);
							}
							return this;
						}
					});

					parent.table.validate();
					parent.scrollPane.validate();
					parent.panelFirst.repaint();
					parent.panelFirst.validate();

				}

			}

			@Override
			public void mouseDragged(MouseEvent e) {

			}

		});

	}
       
       public static void displayclicked(InteractiveBud parent,  int trackindex) {

   		// Make something happen
   		parent.row = trackindex;
   		String ID = (String) parent.table.getValueAt(trackindex, 0);
   		if(parent.resultimp!=null)
			parent.resultimp.close();
   		BUDDYDisplayTrack display = new BUDDYDisplayTrack(parent, parent.Globalmodel);
		display.getImp();
		
   	
   		
		HashMap<Integer, Double> VelocityID = parent.BudVelocityMap.get(Integer.parseInt(ID));
   		ArrayList<double[]> Trackinfo = new ArrayList<double[]>();	
   		
   		for (Pair<String, Budpointobject> Track: parent.Tracklist) {
			
			if(Track.getA().equals(ID)) {
			
				
			double time = Track.getB().t * parent.timecal;
			double LocationX = Track.getB().Location[0] * parent.calibrationX;
			double LocationY = Track.getB().Location[1] * parent.calibrationY;
			double Velocity = 0;
			if(VelocityID.get(Track.getB().t)!=null)
			 Velocity = VelocityID.get(Track.getB().t);
			Trackinfo.add(new double[] {time, LocationX, LocationY, Velocity});
			
		
			}
		
		
	
	}
		
		
		
	
		
		
   	
   		

   		if (parent.imp != null) {
   			parent.imp.setOverlay(parent.overlay);
   			parent.imp.updateAndDraw();
   		}

   		
   		if(parent.Velocitydataset!=null)
   	   		parent.Velocitydataset.removeAllSeries();
   	   		parent.Velocitydataset.addSeries(BudChartMaker.drawVelocity(Trackinfo, "Track Velocity"));

   	   		parent.chartVelocity = utility.BudChartMaker.makeChart(parent.Velocitydataset, "Bud Velocity (um/min)", "Time", "Velocity");
   	   		
   	   	
   	}

		
		
	}