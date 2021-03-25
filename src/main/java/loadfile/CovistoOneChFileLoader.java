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

public class CovistoOneChFileLoader {

	final String bordertitle;
	final String[] blankimageNames;
	
	public CovistoOneChFileLoader(final String bordertitle, final String[] blankimageNames) {
		
		this.bordertitle = bordertitle;
		this.blankimageNames = blankimageNames;
	}
	
	public  JPanel panelSingleChannel = new JPanel();
	
	public  JComboBox<String> ChooseImage;
	public  JPanel SingleChannelOption() {
		
		
		
		layoutManager.Setlayout.LayoutSetter(panelSingleChannel);
		ChooseImage = new JComboBox<String>(blankimageNames);
		 Border chooseoriginalfile = new CompoundBorder(new TitledBorder(bordertitle),
				new EmptyBorder(layoutManager.Setlayout.c.insets));
		
		 panelSingleChannel.add(ChooseImage, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		 panelSingleChannel.setBorder(chooseoriginalfile);
		 
		 
		 
		 return panelSingleChannel;
		
	}
	
}
