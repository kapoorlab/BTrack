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
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import fileListeners.ChooseGreenOrigMap;
import fileListeners.ChooseGreenSegAMap;
import ij.ImagePlus;
import ij.WindowManager;
import io.scif.img.ImgIOException;
import listeners.BTrackGoGreenListener;
import listeners.BTrackGoFreeFlListener;
import listeners.BTrackGoGreenFLListener;
import listeners.BTrackGoRedFLListener;
import listeners.BTrackGoYellowFLListener;
import loadfile.CovistoOneChFileLoader;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.GreenFileChooser.GreenDoneListener;
import pluginTools.GreenFileChooser.CalXListener;
import pluginTools.GreenFileChooser.WaveListener;
import pluginTools.simplifiedio.SimplifiedIO;

public class RedGreenFileChooser extends JPanel {

	  /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		  public JFrame Cardframe = new JFrame("Red and Green Cell Tracker");
		  public JPanel panelCont = new JPanel();
		  public ImagePlus impOrigRed, impOrigGreen, impSegGreen, impSegRed;
		  public File impOrigRedfile, impOrigGreenfile;
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
		
		  public boolean twochannel = false;
		  

		  
		  
		  public String chooseGreenSegstring = "Segmentation Image for Green"; 
		  public Border chooseGreenSeg = new CompoundBorder(new TitledBorder(chooseGreenSegstring),
					new EmptyBorder(c.insets));
		  
		  
		  
		  public String chooseRedSegstring = "Segmentation Image for Red"; 
		  public Border chooseRedSeg = new CompoundBorder(new TitledBorder(chooseRedSegstring),
					new EmptyBorder(c.insets));
		  
		  public String chooseoriginalGreenfilestring = "We analyze only Greens";
		  public Border chooseoriginalGreenfile = new CompoundBorder(new TitledBorder(chooseoriginalGreenfilestring),
					new EmptyBorder(c.insets));
		  
		  public String chooseoriginalRedfilestring = "We analyze Red and Greens";
		  public Border chooseoriginalRedfile = new CompoundBorder(new TitledBorder(chooseoriginalRedfilestring),
					new EmptyBorder(c.insets));
		  
		  
		  
		  public String donestring = "Done Selection";
		  public Border LoadBtrack = new CompoundBorder(new TitledBorder(donestring),
					new EmptyBorder(c.insets));
		
		  public Label inputLabelcalX, wavesize;
		  public double calibration, FrameInterval;

		  public TextField inputFieldcalX, Fieldwavesize;
		  public Border microborder = new CompoundBorder(new TitledBorder("Microscope parameters"), new EmptyBorder(c.insets));
		  public CheckboxGroup Greenmode = new CheckboxGroup();
		  public boolean OnlyGreen = true;
		  public boolean RedandGreen = false;
		  public Checkbox GoGreen = new Checkbox("Single Flourescent Channel", OnlyGreen, Greenmode);
		  public Checkbox RedGreen = new Checkbox("Double Flourescent Channel", RedandGreen, Greenmode);
		  
		  
		  public RedGreenFileChooser() {
			
			  
				
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
		       FrameInterval = Float.parseFloat(Fieldwavesize.getText());
				
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
				
				
				
				panelFirst.add(GoGreen, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
				
				CovistoOneChFileLoader original = new CovistoOneChFileLoader(chooseoriginalGreenfilestring, blankimageNames);
				
				Panelfileoriginal = original.SingleChannelOption();
				
				
				panelFirst.add(Panelfileoriginal, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
				original.ChooseImage.addActionListener(new ChooseGreenOrigMap(this, original.ChooseImage));
				
				
				panelFirst.add(FreeMode, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				panelFirst.add(YellowMode, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				panelFirst.add(GreenMode, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				panelFirst.add(RedMode, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
				
				CovistoOneChFileLoader segmentation = new CovistoOneChFileLoader(chooseGreenSegstring, blankimageNames);
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
				
				GoGreen.addItemListener(new BTrackGoGreenListener(this));
				FreeMode.addItemListener(new BTrackGoFreeFlListener(this));
				YellowMode.addItemListener(new BTrackGoYellowFLListener(this));
				GreenMode.addItemListener(new BTrackGoGreenFLListener(this));
				RedMode.addItemListener(new BTrackGoRedFLListener(this));
				segmentation.ChooseImage.addActionListener(new ChooseGreenSegAMap(this, segmentation.ChooseImage));
				
				inputFieldcalX.addTextListener(new CalXListener());
				Fieldwavesize.addTextListener(new WaveListener());
				Done.addActionListener(new GreenDoneListener());
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
				    	FrameInterval = Float.parseFloat(s);
					
				}
				
		  }
			
		  public class GreenDoneListener implements ActionListener{
			  
			  
			  @Override
				public void actionPerformed(ActionEvent e) {
				  
				  
				  try {
					DoneCurrGreen(Cardframe);
				} catch (ImgIOException e1) {


				
				}
			  }
			  
			  
			  
		  }
		
			
		
		
		  public void DoneCurrGreen(Frame parent) throws ImgIOException{
				
				// Tracking and Measurement is done with imageA 
			  
			   
			
			    
			  RandomAccessibleInterval<FloatType> imageOrig = SimplifiedIO.openImage(impOrig.getOriginalFileInfo().directory + impOrig.getOriginalFileInfo().fileName, new FloatType());
		
				
				
				
				RandomAccessibleInterval<IntType> imageSegA = SimplifiedIO.openImage(impSegA.getOriginalFileInfo().directory + impSegA.getOriginalFileInfo().fileName , new IntType());
				
				String name = impOrig.getOriginalFileInfo().fileName;
				
				WindowManager.closeAllWindows();
				calibration = Float.parseFloat(inputFieldcalX.getText());
				FrameInterval = Float.parseFloat(Fieldwavesize.getText());
				System.out.println("CalibrationX:" + calibration);
				System.out.println("CalibrationT:" + FrameInterval);
				if(!DoYellow && !DoGreen && !DoRed)
				
					new InteractiveGreen( imageOrig, imageSegA,impOrig.getOriginalFileInfo().fileName, calibration, FrameInterval,name    ).run(null);
				
				
				if(DoYellow) {
					
					
					RandomAccessibleInterval<IntType> imageSegB = SimplifiedIO.openImage(impSegB.getOriginalFileInfo().directory + impSegB.getOriginalFileInfo().fileName , new IntType());
				
					assert (imageOrig.numDimensions() == imageSegA.numDimensions());
					assert (imageOrig.numDimensions() == imageSegB.numDimensions());
					new InteractiveGreen(imageOrig, imageSegA, imageSegB, impOrig.getOriginalFileInfo().fileName, calibration, FrameInterval,name    ).run(null);
					
				}
				
				if(DoGreen) {
					RandomAccessibleInterval<IntType> imageSegB = SimplifiedIO.openImage(impSegB.getOriginalFileInfo().directory + impSegB.getOriginalFileInfo().fileName , new IntType());
					RandomAccessibleInterval<IntType> imageSegC = SimplifiedIO.openImage(impSegC.getOriginalFileInfo().directory + impSegC.getOriginalFileInfo().fileName , new IntType());
				
					
					assert (imageOrig.numDimensions() == imageSegA.numDimensions());
					assert (imageOrig.numDimensions() == imageSegB.numDimensions());
					assert (imageOrig.numDimensions() == imageSegC.numDimensions());
					
					new InteractiveGreen(imageOrig, imageSegA, imageSegB,imageSegC, impOrig.getOriginalFileInfo().fileName, calibration, FrameInterval,name    ).run(null);
					
				}
				
              if(DoRed) {
					
					RandomAccessibleInterval<IntType> imageSegB = SimplifiedIO.openImage(impSegB.getOriginalFileInfo().directory + impSegB.getOriginalFileInfo().fileName , new IntType());
					RandomAccessibleInterval<IntType> imageSegC = SimplifiedIO.openImage(impSegC.getOriginalFileInfo().directory + impSegC.getOriginalFileInfo().fileName , new IntType());
					RandomAccessibleInterval<IntType> imageSegD = SimplifiedIO.openImage(impSegD.getOriginalFileInfo().directory + impSegD.getOriginalFileInfo().fileName , new IntType());
					
					
					
					assert (imageOrig.numDimensions() == imageSegA.numDimensions());
					assert (imageOrig.numDimensions() == imageSegB.numDimensions());
					assert (imageOrig.numDimensions() == imageSegC.numDimensions());
					assert (imageOrig.numDimensions() == imageSegD.numDimensions());
					
					
					new InteractiveGreen(imageOrig, imageSegA, imageSegB,imageSegC, imageSegD, impOrig.getOriginalFileInfo().fileName, calibration, FrameInterval,name    ).run(null);
					
				}
				
				
				close(parent);
				
				
			}
		  protected final void close(final Frame parent) {
				if (parent != null)
					parent.dispose();

				
			}

}