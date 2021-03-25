package loadfile;

import java.awt.Checkbox;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class CovistoTwoChForceFileLoader {

	final String bordertitle;
	final String[] blankimageNames;
	
	public CovistoTwoChForceFileLoader(final String bordertitle, final String[] blankimageNames) {
		
		this.bordertitle = bordertitle;
		this.blankimageNames = blankimageNames;
	}
	
	public  JPanel panelTwoChannel = new JPanel();
	
	public  JComboBox<String> ChooseImage;
	public  JComboBox<String> ChoosesecImage;
	public  JPanel TwoChannelOption() {
		
		
		
		layoutManager.Setlayout.LayoutSetter(panelTwoChannel);
		ChooseImage = new JComboBox<String>(blankimageNames);
		ChoosesecImage = new JComboBox<String>(blankimageNames);
		 Border chooseoriginalfile = new CompoundBorder(new TitledBorder(bordertitle),
				new EmptyBorder(layoutManager.Setlayout.c.insets));
		 
			
		
		 panelTwoChannel.add(ChooseImage, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		 panelTwoChannel.add(ChoosesecImage,  new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		 panelTwoChannel.setBorder(chooseoriginalfile);
		 
		 
		 
		 return panelTwoChannel;
		
	}
	
}
