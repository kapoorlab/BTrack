package pluginTools;

import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ij.process.ImageConverter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;


import budDetector.Roiobject;
import fileListeners.ChooseBudOrigMap;
import fileListeners.ChooseBudSegAMap;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.OvalRoi;
import io.scif.img.ImgIOException;
import listeners.BTrackGoBudListener;
import listeners.BTrackGoFreeFlListener;
import listeners.BTrackGoGreenFLListener;
import listeners.BTrackGoYellowFLListener;
import loadfile.CovistoOneChFileLoader;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import fileListeners.SimplifiedIO;


public class BudFileChooser extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JFrame Cardframe = new JFrame("Bud n Cell tracker");
	public JPanel panelCont = new JPanel();
	public ImagePlus impOrig, impOrigRGB, impSegA, impSegB, impSegC, impSegD;
	public File impOrigfile, impOrigSecfile, impSegAfile, impSegBfile, impSegCfile;
	public JPanel panelFirst = new JPanel();
	public JPanel Panelfile = new JPanel();
	public JPanel Panelcsv = new JPanel();
	public JPanel Panelsuperfile = new JPanel();
	public JPanel Panelfileoriginal = new JPanel();
	public JPanel Paneldone = new JPanel();
	public JPanel Panelrun = new JPanel();
	public JPanel Microscope = new JPanel();
	public final Insets insets = new Insets(10, 10, 0, 10);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();
	public final String[] imageNames, blankimageNames;
	public JComboBox<String> ChooseImage;
	public JComboBox<String> ChoosesuperImage;
	public JComboBox<String> ChooseoriginalImage;
	public JComboBox<String> ChooseRGBImage;
	public JButton Done = new JButton("Finished choosing files, start BTrack");
	public JButton Restart = new JButton("Reload Saved Budpoints");
	public HashMap<String, ArrayList<Roiobject>> BudOvalRois= new HashMap<String, ArrayList<Roiobject>>();
	public boolean simple = false;
	public boolean curvesuper = true;
	public boolean curvesimple = false;
	public boolean twochannel = false;

	public String chooseBudSegstring = "Segmentation Image for buds";
	public Border chooseBudSeg = new CompoundBorder(new TitledBorder(chooseBudSegstring), new EmptyBorder(c.insets));

	public String chooseYellowSegstring = "Segmentation Image for buds and cell Ch 1";
	public Border chooseYellowSeg = new CompoundBorder(new TitledBorder(chooseYellowSegstring),
			new EmptyBorder(c.insets));
	public String chooseGreenSegstring = "Segmentation Image for buds and cell Ch 1,2";
	public Border chooseGreenSeg = new CompoundBorder(new TitledBorder(chooseYellowSegstring),
			new EmptyBorder(c.insets));

	public String chooseRedSegstring = "Segmentation Image for buds and cell Ch 1,2,3";
	public Border chooseRedSeg = new CompoundBorder(new TitledBorder(chooseYellowSegstring), new EmptyBorder(c.insets));

	public String chooseoriginalbudfilestring = "We analyze only Buds";
	public Border chooseoriginalbudfile = new CompoundBorder(new TitledBorder(chooseoriginalbudfilestring),
			new EmptyBorder(c.insets));

	public String donestring = "Done Selection";
	public Border LoadBtrack = new CompoundBorder(new TitledBorder(donestring), new EmptyBorder(c.insets));

	  public Label inputLabelcalX, inputLabelcalY, inputLabelcalZ, inputLabelcalT;
	  public double calibrationX, calibrationY, calibrationZ, FrameInterval;

	  public TextField inputFieldcalX, inputFieldcalY, inputFieldcalZ, FieldinputLabelcalT;
	public Border microborder = new CompoundBorder(new TitledBorder("Microscope parameters"),
			new EmptyBorder(c.insets));
	public CheckboxGroup budmode = new CheckboxGroup();
	public boolean OnlyBud = true;
	public boolean RGBBud = false;
	public Checkbox GoBud = new Checkbox("Bud", OnlyBud, budmode);

	public boolean DoYellow = false;
	public boolean DoGreen = false;
	public boolean DoRed = false;
	public boolean NoChannel = true;

	public CheckboxGroup cellmode = new CheckboxGroup();
	public Checkbox FreeMode = new Checkbox("No Flourescent Channel", NoChannel, cellmode);
	public Checkbox YellowMode = new Checkbox("Flourescent Channel 1", DoYellow, cellmode);


	public BudFileChooser() {

		   inputLabelcalX = new Label("Pixel calibration in X(um)");
	       inputFieldcalX = new TextField(5);
		   inputFieldcalX.setText("1");
		   

		   inputLabelcalY = new Label("Pixel calibration in Y(um)");
	       inputFieldcalY = new TextField(5);
		   inputFieldcalY.setText("1");

		   inputLabelcalT = new Label("Pixel calibration in T (min)");
		   FieldinputLabelcalT = new TextField(5);
		   FieldinputLabelcalT.setText("1");
		panelFirst.setLayout(layout);

		Paneldone.setLayout(layout);
		Microscope.setLayout(layout);
		CardLayout cl = new CardLayout();
		calibrationX = Float.parseFloat(inputFieldcalX.getText());
		calibrationY = Float.parseFloat(inputFieldcalY.getText());
		FrameInterval = Float.parseFloat(FieldinputLabelcalT.getText());

		panelCont.setLayout(cl);
		panelCont.add(panelFirst, "1");
		imageNames = WindowManager.getImageTitles();
		blankimageNames = new String[imageNames.length + 1];
		blankimageNames[0] = " ";

		for (int i = 0; i < imageNames.length; ++i)
			blankimageNames[i + 1] = imageNames[i];

		ChooseImage = new JComboBox<String>(blankimageNames);
		ChooseoriginalImage = new JComboBox<String>(blankimageNames);
		ChooseRGBImage = new JComboBox<String>(blankimageNames);
		ChoosesuperImage = new JComboBox<String>(blankimageNames);


		CovistoOneChFileLoader original = new CovistoOneChFileLoader(chooseoriginalbudfilestring, blankimageNames);

		Panelfileoriginal = original.SingleChannelOption();

		panelFirst.add(Panelfileoriginal, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		original.ChooseImage.addActionListener(new ChooseBudOrigMap(this, original.ChooseImage));

		panelFirst.add(FreeMode, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		panelFirst.add(YellowMode, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		CovistoOneChFileLoader segmentation = new CovistoOneChFileLoader(chooseBudSegstring, blankimageNames);
		Panelfile = segmentation.SingleChannelOption();

		panelFirst.add(Panelfile, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Panelcsv.add(Restart, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		panelFirst.add(Panelcsv, new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
		Paneldone.add(Done, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Paneldone.setBorder(LoadBtrack);
		panelFirst.add(Paneldone, new GridBagConstraints(0, 10, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));


		Microscope.add(inputLabelcalX, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Microscope.add(inputFieldcalX, new GridBagConstraints(0, 1, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));
		
		Microscope.add(inputLabelcalY, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Microscope.add(inputFieldcalY, new GridBagConstraints(0, 3, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));

		Microscope.add(inputLabelcalT, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Microscope.add(FieldinputLabelcalT, new GridBagConstraints(0, 5, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));
		Microscope.setBorder(microborder);
		panelFirst.add(Microscope, new GridBagConstraints(0, 8, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		// Listeneres

		GoBud.addItemListener(new BTrackGoBudListener(this));
		FreeMode.addItemListener(new BTrackGoFreeFlListener(this));
		YellowMode.addItemListener(new BTrackGoYellowFLListener(this));
		segmentation.ChooseImage.addActionListener(new ChooseBudSegAMap(this, segmentation.ChooseImage));

		inputFieldcalX.addTextListener(new CalXListener());
		inputFieldcalY.addTextListener(new CalYListener());
		FieldinputLabelcalT.addTextListener(new CalTListener());
		Done.addActionListener(new BudDoneListener());
		Restart.addActionListener(new ReloadListener());
		panelFirst.setVisible(true);
		cl.show(panelCont, "1");
		Cardframe.add(panelCont, "Center");
		Panelsuperfile.setEnabled(true);
		ChoosesuperImage.setEnabled(true);

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);
	}

	public class CalXListener implements TextListener {

		@Override
		public void textValueChanged(TextEvent e) {
			final TextComponent tc = (TextComponent) e.getSource();
			String s = tc.getText();

			if (s.length() > 0)
				calibrationX = Double.parseDouble(s);
		}

	}

	
	public class CalYListener implements TextListener {

		@Override
		public void textValueChanged(TextEvent e) {
			final TextComponent tc = (TextComponent) e.getSource();
			String s = tc.getText();

			if (s.length() > 0)
				calibrationY = Double.parseDouble(s);
		}

	}
	
	public class CalTListener implements TextListener {

		@Override
		public void textValueChanged(TextEvent e) {
			final TextComponent tc = (TextComponent) e.getSource();
			String s = tc.getText();

			if (s.length() > 0)
				FrameInterval = Double.parseDouble(s);
		}

	}
	

	public class BudDoneListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				DoneCurrBud(Cardframe);
			} catch (ImgIOException e1) {

			}
		}

	}

	public class ReloadListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			JFileChooser csvfile = new JFileChooser();
			FileFilter csvfilter = new FileFilter() 
			{
			      //Override accept method
			      public boolean accept(File file) {
			              
			             //if the file extension is .log return true, else false
			             if (file.getName().endsWith(".csv")) {
			                return true;
			             }
			             return false;
			      }

				@Override
				public String getDescription() {
					
					return null;
				}
			};
	        String line = "";
	        String cvsSplitBy = ",";
			if (impOrig!=null)
			csvfile.setCurrentDirectory(new File(impOrig.getOriginalFileInfo().directory));
			else 
				csvfile.setCurrentDirectory(new java.io.File("."));
			csvfile.setDialogTitle("Pink dot file");
			csvfile.setFileSelectionMode(JFileChooser.FILES_ONLY);
			csvfile.setFileFilter(csvfilter);
			int count = 0;
			if (csvfile.showOpenDialog(Cardframe) == JFileChooser.APPROVE_OPTION) {
				
				File budfile = new File(csvfile.getSelectedFile().getPath());
				ArrayList<Roiobject> Allrois = new ArrayList<Roiobject>();
				
		        try (BufferedReader br = new BufferedReader(new FileReader(budfile))) {

		            while ((line = br.readLine()) != null) {

		                // use comma as separator
		                String[] budpoints = line.split(cvsSplitBy);
                          
		                 if(count > 0) {
		                
                           if(BudOvalRois.get(budpoints[0])==null) {
                        	    Allrois = new ArrayList<Roiobject>();
                        	    BudOvalRois.put(budpoints[0], Allrois);    
                           }
                           else
                        	   BudOvalRois.put(budpoints[0], Allrois);
		                OvalRoi roi = new OvalRoi(Integer.parseInt(budpoints[1]), Integer.parseInt(budpoints[2]), 10, 10);
		                
		                Allrois.add(new Roiobject (Color.PINK, roi, 
		                		new RealPoint(new double[] {Float.parseFloat(budpoints[1]), Float.parseFloat(budpoints[2])}), Integer.parseInt(budpoints[3])));
		         
		            }
		                 count = count +  1;
		            }
		            
		            
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        
		        
			}
			else
				csvfile = null;
			
		
		
	}
		
	}
	

	
	public void DoneCurrBud(Frame parent) throws ImgIOException {

		// Tracking and Measurement is done with imageA

		RandomAccessibleInterval<FloatType> imageOrig = SimplifiedIO.openImage(
				impOrig.getOriginalFileInfo().directory + impOrig.getOriginalFileInfo().fileName, new FloatType());

		RandomAccessibleInterval<IntType> imageSegA = SimplifiedIO.openImage(
				impSegA.getOriginalFileInfo().directory + impSegA.getOriginalFileInfo().fileName, new IntType());

	
		

		String name = impOrig.getOriginalFileInfo().fileName;

		WindowManager.closeAllWindows();
		
		calibrationX = Float.parseFloat(inputFieldcalX.getText());
		calibrationY = Float.parseFloat(inputFieldcalY.getText());
		FrameInterval = Float.parseFloat(FieldinputLabelcalT.getText());
		if (!DoYellow && !DoGreen && !DoRed) {

			InteractiveBud masterparent = new InteractiveBud(imageOrig, imageSegA,BudOvalRois, new File(impOrig.getOriginalFileInfo().directory) , impOrig.getOriginalFileInfo().fileName, calibrationX, calibrationY, FrameInterval,
					name, true);
		
		masterparent.run(null);
		
		}

		if (DoYellow) {

			RandomAccessibleInterval<IntType> imageSegB = SimplifiedIO.openImage(
					impSegB.getOriginalFileInfo().directory + impSegB.getOriginalFileInfo().fileName, new IntType());

			assert (imageOrig.numDimensions() == imageSegA.numDimensions());
			assert (imageOrig.numDimensions() == imageSegB.numDimensions());
			InteractiveBud masterparent =  new InteractiveBud(imageOrig, imageSegA, imageSegB, new File(impOrig.getOriginalFileInfo().directory) ,impOrig.getOriginalFileInfo().fileName, calibrationX, calibrationY,
					FrameInterval, name);
			
			masterparent.run(null);

		}

		

		close(parent);

	}

	protected final void close(final Frame parent) {
		if (parent != null)
			parent.dispose();

	}

}