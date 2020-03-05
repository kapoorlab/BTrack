package tracker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import net.imglib2.util.Pair;
import pluginTools.InteractiveBud;
import utility.BudChartMaker;
import zGUI.CovistoZselectPanel;

public class DisplaySelectedTrack {
	
	
	
	public static void Select(final InteractiveBud parent) {
		
		

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
		
       public static void Mark(final InteractiveBud parent) {

		
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
								&& CovistoZselectPanel.thirdDimension == (int) dCordZ && parent.ndims > 3) {

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
       
       public static void displayclicked(InteractiveBud parent, int trackindex) {

   		// Make something happen
   		parent.row = trackindex;
   		String ID = (String) parent.table.getValueAt(trackindex, 0);
   		if(parent.resultimp!=null)
			parent.resultimp.close();
   		DisplayTrack display = new DisplayTrack(parent, parent.Globalmodel);
		display.getImp();
		
   	
   		
   		ArrayList<Pair<String, Budpointobject>> currentresultIntA = new ArrayList<Pair<String, Budpointobject>>();

   		for (Pair<String, Budpointobject> currentInt : parent.Tracklist) {

   			if (ID.equals(currentInt.getA())) {

   				currentresultIntA.add(currentInt);

   			}

   		}

   	
   		

   		if (parent.imp != null) {
   			parent.imp.setOverlay(parent.overlay);
   			parent.imp.updateAndDraw();
   		}

   		
   		if(parent.Velocitydataset!=null)
   	   		parent.Velocitydataset.removeAllSeries();
   	   		parent.Velocitydataset.addSeries(BudChartMaker.drawVelocity(currentresultIntA, "Intensity"));

   	   		parent.chartVelocity = utility.BudChartMaker.makeChart(parent.Velocitydataset, "Bud Velocity (um/min)", "Time", "Velocity");
   	   		
   	   	
   	}

		
		
	}