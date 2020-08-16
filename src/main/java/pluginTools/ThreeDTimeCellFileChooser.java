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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JOptionPane;

import fileListeners.ChooseCellSegAMap;
import fileListeners.ChooseGreenOrigMap;
import fileListeners.ChooseGreenSegMap;
import ij.ImagePlus;
import ij.WindowManager;
import io.scif.img.ImgIOException;
import listeners.BTrackGo3DMaskFLListener;
import listeners.BTrackGoFreeFlListener;
import listeners.BTrackGoGreenFLListener;
import listeners.BTrackGoMaskFLListener;
import listeners.BTrackGoRedFLListener;
import listeners.BTrackGoYellowFLListener;
import listeners.ThreeDCellGoFreeFLListener;
import listeners.TwoDCellGoFreeFLListener;
import loadfile.CovistoOneChFileLoader;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import fileListeners.SimplifiedIO;

public class ThreeDTimeCellFileChooser extends JPanel {

	  /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		  public JFrame Cardframe = new JFrame("Red and Green Cell Tracker");
		  public JPanel panelCont = new JPanel();
		  public ImagePlus  impOrigGreen, impSegGreen,  impMask;
		  public File impOrigGreenfile;
		  public JPanel panelFirst = new JPanel();
		  public JPanel Panelfile = new JPanel();
		  public JPanel Panelsuperfile = new JPanel();
		  public JPanel Panelfileoriginal = new JPanel();
		  public JPanel Panelfilemask = new JPanel();
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
		  public JButton Done =  new JButton("Collect Cells and Start Tracker");
		
		   public String chooseCellSegstring = "3D + time Segmentation Image for Cells";
			public Border chooseCellSeg = new CompoundBorder(new TitledBorder(chooseCellSegstring), new EmptyBorder(c.insets));

			public String chooseMaskSegstring = "3D + time Segmentation Image for Cells and 2D + time Mask";
			public Border chooseMaskSeg = new CompoundBorder(new TitledBorder(chooseMaskSegstring),
					new EmptyBorder(c.insets));
			public JProgressBar jpb = new JProgressBar();

			

			public String chooseoriginalCellfilestring = "Cells are tracked inside a region";
			public Border chooseoriginalCellfile = new CompoundBorder(new TitledBorder(chooseoriginalCellfilestring),
					new EmptyBorder(c.insets));

			public String donestring = "Done Selection";
		  public Border LoadBtrack = new CompoundBorder(new TitledBorder(donestring),
					new EmptyBorder(c.insets));
		
		  public Label inputLabelcalX, inputLabelcalY, inputLabelcalZ, inputLabelcalT;
		  public double calibrationX, calibrationY, calibrationZ, FrameInterval;

		  public TextField inputFieldcalX, inputFieldcalY, inputFieldcalZ, FieldinputLabelcalT;
		  public Border microborder = new CompoundBorder(new TitledBorder("Microscope parameters"), new EmptyBorder(c.insets));
			public boolean DoMask = false;
			public boolean NoMask = true;

			public CheckboxGroup cellmode = new CheckboxGroup();
			public Checkbox FreeMode = new Checkbox("No Mask", NoMask, cellmode);
			public Checkbox MaskMode = new Checkbox("With Mask", DoMask, cellmode);
		  
		  
		  public ThreeDTimeCellFileChooser() {
			
			  
				
			   inputLabelcalX = new Label("Pixel calibration in X(um)");
		       inputFieldcalX = new TextField(5);
			   inputFieldcalX.setText("1");
			   

			   inputLabelcalY = new Label("Pixel calibration in Y(um)");
		       inputFieldcalY = new TextField(5);
			   inputFieldcalY.setText("1");
			   
			   inputLabelcalZ = new Label("Pixel calibration in Z(um)");
		       inputFieldcalZ = new TextField(5);
			   inputFieldcalZ.setText("1");
				
			   inputLabelcalT = new Label("Pixel calibration in T (min)");
			   FieldinputLabelcalT = new TextField(5);
			   FieldinputLabelcalT.setText("1");
			   panelFirst.setLayout(layout);
			   
			   Paneldone.setLayout(layout);
			   Microscope.setLayout(layout);
		       CardLayout cl = new CardLayout();
		       calibrationX = Float.parseFloat(inputFieldcalX.getText());
		       calibrationY = Float.parseFloat(inputFieldcalY.getText());
		       calibrationZ = Float.parseFloat(inputFieldcalZ.getText());
		       
		       FrameInterval = Float.parseFloat(FieldinputLabelcalT.getText());
				
				panelCont.setLayout(cl);
				panelCont.add(panelFirst, "1");
				imageNames = WindowManager.getImageTitles();
				blankimageNames = new String[imageNames.length + 1];
				blankimageNames[0] = " " ;
				
				for(int i = 0; i < imageNames.length; ++i)
					blankimageNames[i + 1] = imageNames[i];
				
				ChooseImage = new JComboBox<String>(blankimageNames);
				ChooseoriginalImage = new JComboBox<String>(blankimageNames);
				ChoosesuperImage = new JComboBox<String>(blankimageNames);
				
				
				
				
				CovistoOneChFileLoader original = new CovistoOneChFileLoader(chooseoriginalCellfilestring, blankimageNames);
				
				Panelfileoriginal = original.SingleChannelOption();
				
				
				panelFirst.add(Panelfileoriginal, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
				original.ChooseImage.addActionListener(new ChooseGreenOrigMap(this, original.ChooseImage));
				
				CovistoOneChFileLoader segmentation = new CovistoOneChFileLoader(chooseCellSegstring, blankimageNames);
				Panelfile = segmentation.SingleChannelOption();
				
				
				panelFirst.add(Panelfile, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
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
				
				
				Microscope.add(inputLabelcalY, new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));

				Microscope.add(inputFieldcalY, new GridBagConstraints(1, 1, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.RELATIVE, insets, 0, 0));
				
				Microscope.add(inputLabelcalT, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
				Microscope.add(FieldinputLabelcalT, new GridBagConstraints(0, 3, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.RELATIVE, insets, 0, 0));
				
		
				
				Microscope.setBorder(microborder);
				panelFirst.add(Microscope, new GridBagConstraints(0, 8, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				
				// Listeneres

				FreeMode.addItemListener(new ThreeDCellGoFreeFLListener(this));
				MaskMode.addItemListener(new BTrackGo3DMaskFLListener(this));
				
				
				segmentation.ChooseImage.addActionListener(new ChooseGreenSegMap(this, segmentation.ChooseImage));
				
				inputFieldcalX.addTextListener(new CalXListener());
				inputFieldcalY.addTextListener(new CalYListener());
				FieldinputLabelcalT.addTextListener(new CalTListener());
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
					calibrationX = Double.parseDouble(s);
				}
				
		  }
		  
		  public class CalYListener implements TextListener {

				
				
				
				@Override
				public void textValueChanged(TextEvent e) {
					final TextComponent tc = (TextComponent)e.getSource();
				    String s = tc.getText();
				   
				    if (s.length() > 0)
					calibrationY = Double.parseDouble(s);
				}
				
		  }
		  
		  public class CalZListener implements TextListener {

				
				
				
				@Override
				public void textValueChanged(TextEvent e) {
					final TextComponent tc = (TextComponent)e.getSource();
				    String s = tc.getText();
				   
				    if (s.length() > 0)
					calibrationZ = Double.parseDouble(s);
				}
				
		  }
		  
		  public class CalTListener implements TextListener {

				
				
				
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
				
				WindowManager.closeAllWindows();
					// Tracking and Measurement is done with imageA

					Done.setEnabled(false);
					RandomAccessibleInterval<FloatType> imageOrigGreen = SimplifiedIO.openImage(
							impOrigGreen.getOriginalFileInfo().directory + impOrigGreen.getOriginalFileInfo().fileName, new FloatType());

					// Segmentation image for green cells
					RandomAccessibleInterval<IntType> imageSegA = SimplifiedIO.openImage(impSegGreen.getOriginalFileInfo().directory + impSegGreen.getOriginalFileInfo().fileName , new IntType());


					

					String name = impOrigGreen.getOriginalFileInfo().fileName;
					WindowManager.closeAllWindows();
					// Image -> Mask -> Cell Mask
					Cardframe.remove(jpb);
					if(DoMask) {
						
						
						RandomAccessibleInterval<IntType> imageMask = SimplifiedIO.openImage(impMask.getOriginalFileInfo().directory + impMask.getOriginalFileInfo().fileName , new IntType());

						
						assert (imageOrigGreen.numDimensions() == imageSegA.numDimensions());
						InteractiveGreen CellCollection = new InteractiveGreen( imageOrigGreen, imageSegA, imageMask, impOrigGreen.getOriginalFileInfo().fileName, calibrationX, calibrationY, calibrationZ, FrameInterval,name );
						
						CellCollection.run(null);
						jpb = CellCollection.jpb;
					}
					
					
					if(NoMask) {
						
						RandomAccessibleInterval<IntType> imageMask = CreateBorderMask(imageOrigGreen);
						InteractiveGreen CellCollection = new InteractiveGreen( imageOrigGreen, imageSegA, imageMask, impOrigGreen.getOriginalFileInfo().fileName, calibrationX, calibrationY, calibrationZ, FrameInterval,name );

						
						CellCollection.run(null);
						
						jpb = CellCollection.jpb;
						
					}
					
					Cardframe.add(jpb, "Last");
					Cardframe.repaint();
					Cardframe.validate();
					
					
					
					
					
					calibrationX = Float.parseFloat(inputFieldcalX.getText());
					calibrationY = Float.parseFloat(inputFieldcalY.getText());
					calibrationZ = Float.parseFloat(inputFieldcalZ.getText());
					FrameInterval = Float.parseFloat(FieldinputLabelcalT.getText());
					
				
				
			}
		  protected final void close(final Frame parent) {
				if (parent != null)
					parent.dispose();

				
			}
		  
		  
		  @SuppressWarnings("deprecation")
			protected RandomAccessibleInterval<IntType> CreateBorderMask(RandomAccessibleInterval<FloatType> imageOrig) {
				
				RandomAccessibleInterval<IntType> imageSegB = new ArrayImgFactory<IntType>().create(imageOrig, new IntType());
				
				
				RandomAccess<IntType> ranac = imageSegB.randomAccess(); 
				
				Cursor<FloatType> cur = Views.iterable(imageOrig).localizingCursor();
				
				while(cur.hasNext()) {
					
					
					cur.fwd();
					
					if(cur.getDoublePosition(0)<= imageOrig.dimension(0) - 1 && cur.getDoublePosition(0) > 1) {
						
						
						ranac.setPosition(cur);
						
						ranac.get().setOne();
					}
					
		           if(cur.getDoublePosition(1) <= imageOrig.dimension(1) - 1 && cur.getDoublePosition(1) > 1) {
						
						
						ranac.setPosition(cur);
						
						ranac.get().setOne();
					}
						
		           if(cur.getDoublePosition(3) <= imageOrig.dimension(3) - 1 && cur.getDoublePosition(3) > 1) {
						
						
						ranac.setPosition(cur);
						
						ranac.get().setOne();
					}
		           
					
				}
				
				return imageSegB;
				
			}

}