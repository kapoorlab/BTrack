package pluginTools;

import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
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
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.BasicConfigurator;

import fileListeners.ChooseBudOrigMap;
import fileListeners.ChooseBudSecOrigMap;
import fileListeners.ChooseBudSegAMap;
import fileListeners.ChooseBudSegBMap;
import fileListeners.ChooseBudSegCMap;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import listeners.BTrackGoBudListener;
import listeners.BTrackGoFreeFlListener;
import listeners.BTrackGoRedListener;
import listeners.BTrackGoYellowFLListener;
import loadfile.CovistoOneChFileLoader;
import loadfile.CovistoThreeChForceFileLoader;
import loadfile.CovistoTwoChForceFileLoader;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.simplifiedio.SimplifiedIO;


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
		  public JButton Done =  new JButton("Finished choosing files, start BTrack");
		
		  public boolean simple = false;
		  public boolean curvesuper = true;
		  public boolean curvesimple = false;
		  public boolean twochannel = false;
		  

		  
		  
		  public String chooseBudSegstring = "Segmentation Image for buds"; 
		  public Border chooseBudSeg = new CompoundBorder(new TitledBorder(chooseBudSegstring),
					new EmptyBorder(c.insets));
		  
		  public String chooseYellowSegstring = "Segmentation Image for buds and cell Ch 1"; 
		  public Border chooseYellowSeg = new CompoundBorder(new TitledBorder(chooseYellowSegstring),
					new EmptyBorder(c.insets));
		  
		  public String chooseoriginalbudfilestring = "We analyze only Buds";
		  public Border chooseoriginalbudfile = new CompoundBorder(new TitledBorder(chooseoriginalbudfilestring),
					new EmptyBorder(c.insets));
		  
		  public String chooseoriginalrgbfilestring = "We analyze Buds & cell(s)";
		  public Border chooseoriginalrgbfile = new CompoundBorder(new TitledBorder(chooseoriginalrgbfilestring),
					new EmptyBorder(c.insets));
		  
		  
		  public String donestring = "Done Selection";
		  public Border LoadBtrack = new CompoundBorder(new TitledBorder(donestring),
					new EmptyBorder(c.insets));
		
		  public Label inputLabelcalX, wavesize;
		  public double calibration, Wavesize;

		  public TextField inputFieldcalX, Fieldwavesize;
		  public Border microborder = new CompoundBorder(new TitledBorder("Microscope parameters"), new EmptyBorder(c.insets));
		  public CheckboxGroup budmode = new CheckboxGroup();
		  public boolean OnlyBud = true;
		  public boolean RGBBud = false;
		  public Checkbox GoBud = new Checkbox("Only Bud", OnlyBud, budmode);
		  public Checkbox GoRed = new Checkbox("Input RGB", RGBBud, budmode);
		  
		  public boolean DoYellow = false;
		  public boolean DoGreen = false;
		  public boolean DoRed = false;
		  public boolean NoChannel = true;
		  
		  public CheckboxGroup cellmode = new CheckboxGroup();
		  public Checkbox FreeMode = new Checkbox("No Flourescent Channel", NoChannel, cellmode);
		  public Checkbox YellowMode = new Checkbox("Flourescent Channel 1", DoYellow, cellmode);
		  public Checkbox GreenMode = new Checkbox("Flourescent Channel 2", DoGreen, cellmode);
		  public Checkbox RedMode = new Checkbox("Flourescent Channel 3", DoRed, cellmode);
		  
		  
		  public BudFileChooser() {
			
			
			   inputLabelcalX = new Label("Pixel calibration in X,Y (um)");
		       inputFieldcalX = new TextField(5);
			   inputFieldcalX.setText("1");
				
			   wavesize = new Label("Pixel calibration in T (min)");
			   Fieldwavesize = new TextField(5);
			   Fieldwavesize.setText("1");
			   panelFirst.setLayout(layout);
			   
			   Paneldone.setLayout(layout);
			   Microscope.setLayout(layout);
		       CardLayout cl = new CardLayout();
		       calibration = Float.parseFloat(inputFieldcalX.getText());
				Wavesize = Float.parseFloat(Fieldwavesize.getText());
				
				panelCont.setLayout(cl);
				panelCont.add(panelFirst, "1");
				imageNames = WindowManager.getImageTitles();
				blankimageNames = new String[imageNames.length + 1];
				blankimageNames[0] = " " ;
				
				for(int i = 0; i < imageNames.length; ++i)
					blankimageNames[i + 1] = imageNames[i];
				
				ChooseImage = new JComboBox<String>(blankimageNames);
				ChooseoriginalImage = new JComboBox<String>(blankimageNames);
				ChooseRGBImage = new JComboBox<String>(blankimageNames);
				ChoosesuperImage = new JComboBox<String>(blankimageNames);
				
				
				
				panelFirst.add(GoBud, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				panelFirst.add(GoRed, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
				
				CovistoOneChFileLoader original = new CovistoOneChFileLoader(chooseoriginalbudfilestring, blankimageNames);
				
				Panelfileoriginal = original.SingleChannelOption();
				
				
				panelFirst.add(Panelfileoriginal, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
				original.ChooseImage.addActionListener(new ChooseBudOrigMap(this, original.ChooseImage));
				
				
				panelFirst.add(FreeMode, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				panelFirst.add(YellowMode, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				panelFirst.add(GreenMode, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				panelFirst.add(RedMode, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
				
				CovistoOneChFileLoader segmentation = new CovistoOneChFileLoader(chooseBudSegstring, blankimageNames);
				Panelfile = segmentation.SingleChannelOption();
				
				
				panelFirst.add(Panelfile, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
				Paneldone.add(Done, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				Paneldone.setBorder(LoadBtrack);
				panelFirst.add(Paneldone, new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
				Microscope.add(inputLabelcalX, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
				Microscope.add(inputFieldcalX, new GridBagConstraints(0, 1, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.RELATIVE, insets, 0, 0));
				
				Microscope.add(wavesize, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
				Microscope.add(Fieldwavesize, new GridBagConstraints(0, 3, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.RELATIVE, insets, 0, 0));
				
		
				
				Microscope.setBorder(microborder);
				panelFirst.add(Microscope, new GridBagConstraints(0, 8, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				
				// Listeneres 
				
				GoBud.addItemListener(new BTrackGoBudListener(this));
				GoRed.addItemListener(new BTrackGoRedListener(this));
				FreeMode.addItemListener(new BTrackGoFreeFlListener(this));
				YellowMode.addItemListener(new BTrackGoYellowFLListener(this));
				//GreenMode.addItemListener(new BTrackGoGreenFLListener(this));
				//RedMode.addItemListener(new BTrackGoRedFLListener(this));
				
				segmentation.ChooseImage.addActionListener(new ChooseBudSegAMap(this, segmentation.ChooseImage));
				
				inputFieldcalX.addTextListener(new CalXListener());
				Fieldwavesize.addTextListener(new WaveListener());
				Done.addActionListener(new BudDoneListener());
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
					final TextComponent tc = (TextComponent)e.getSource();
				    String s = tc.getText();
				   
				    if (s.length() > 0)
					calibration = Double.parseDouble(s);
				}
				
		  }
		  
		  public class WaveListener implements TextListener {

				
				
				
				@Override
				public void textValueChanged(TextEvent e) {
					final TextComponent tc = (TextComponent)e.getSource();
				    String s = tc.getText();
				   
				    if (s.length() > 0)
					Wavesize = Float.parseFloat(s);
					
				}
				
		  }
			
		  public class BudDoneListener implements ActionListener{
			  
			  
			  @Override
				public void actionPerformed(ActionEvent e) {
				  
				  
				  try {
					DoneCurrBud(Cardframe);
				} catch (ImgIOException e1) {

					// TODO Auto-generated catch block

				
				}
			  }
			  
			  
			  
		  }
		  
		
		  public void DoneCurrBud(Frame parent) throws ImgIOException{
				
				// Tracking and Measurement is done with imageA 
		        
			    org.apache.log4j.BasicConfigurator.configure();
				
				RandomAccessibleInterval<FloatType> imageOrig =  SimplifiedIO.openImage(impOrig.getOriginalFileInfo().directory + impOrig.getOriginalFileInfo().fileName, new FloatType());
				
				RandomAccessibleInterval<IntType> imageSegA = SimplifiedIO.openImage(impSegA.getOriginalFileInfo().directory + impSegA.getOriginalFileInfo().fileName , new IntType());
				
				String name = impOrig.getOriginalFileInfo().fileName;
				
				WindowManager.closeAllWindows();
				calibration = Float.parseFloat(inputFieldcalX.getText());
				Wavesize = Float.parseFloat(Fieldwavesize.getText());
				System.out.println("CalibrationX:" + calibration);
				System.out.println("Wavesize:" + Wavesize);
				if(OnlyBud || (RGBBud && !DoYellow && !DoGreen && !DoRed))
				
					new InteractiveBud(imageOrig, imageSegA,impOrig.getOriginalFileInfo().fileName, calibration, Wavesize,name    ).run(null);
				
				
				if(RGBBud && DoYellow) {
					
					RandomAccessibleInterval<ARGBType> imageOrigRGB =  SimplifiedIO.openImage(impOrigRGB.getOriginalFileInfo().directory + impOrigRGB.getOriginalFileInfo().fileName, new ARGBType());
					RandomAccessibleInterval<IntType> imageSegB = SimplifiedIO.openImage(impSegB.getOriginalFileInfo().directory + impSegB.getOriginalFileInfo().fileName , new IntType());
					new InteractiveBud(imageOrigRGB, imageSegA, imageSegB, impOrig.getOriginalFileInfo().fileName, calibration, Wavesize,name    ).run(null);
					
				}
				
				if(RGBBud && DoGreen) {
					
					RandomAccessibleInterval<ARGBType> imageOrigRGB =  SimplifiedIO.openImage(impOrigRGB.getOriginalFileInfo().directory + impOrigRGB.getOriginalFileInfo().fileName, new ARGBType());
					RandomAccessibleInterval<IntType> imageSegB = SimplifiedIO.openImage(impSegB.getOriginalFileInfo().directory + impSegB.getOriginalFileInfo().fileName , new IntType());
					RandomAccessibleInterval<IntType> imageSegC = SimplifiedIO.openImage(impSegC.getOriginalFileInfo().directory + impSegC.getOriginalFileInfo().fileName , new IntType());
					new InteractiveBud(imageOrigRGB, imageSegA, imageSegB,imageSegC, impOrig.getOriginalFileInfo().fileName, calibration, Wavesize,name    ).run(null);
					
				}
				
                if(RGBBud && DoRed) {
					
					RandomAccessibleInterval<ARGBType> imageOrigRGB =  SimplifiedIO.openImage(impOrigRGB.getOriginalFileInfo().directory + impOrigRGB.getOriginalFileInfo().fileName, new ARGBType());
					RandomAccessibleInterval<IntType> imageSegB = SimplifiedIO.openImage(impSegB.getOriginalFileInfo().directory + impSegB.getOriginalFileInfo().fileName , new IntType());
					RandomAccessibleInterval<IntType> imageSegC = SimplifiedIO.openImage(impSegC.getOriginalFileInfo().directory + impSegC.getOriginalFileInfo().fileName , new IntType());
					RandomAccessibleInterval<IntType> imageSegD = SimplifiedIO.openImage(impSegD.getOriginalFileInfo().directory + impSegD.getOriginalFileInfo().fileName , new IntType());
					new InteractiveBud(imageOrigRGB, imageSegA, imageSegB,imageSegC, imageSegD, impOrig.getOriginalFileInfo().fileName, calibration, Wavesize,name    ).run(null);
					
				}
				
				
				close(parent);
				
				
			}
		  protected final void close(final Frame parent) {
				if (parent != null)
					parent.dispose();

				
			}

		

}
