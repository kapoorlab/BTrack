package budDetector;


import java.util.List;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;

public class Cellobject {

	
	public double cellArea;
	
	public Localizable Location;
	
	public double size;
	
	public List<RealLocalizable> boundarylist;
	
	public List<RealLocalizable> interiorlist;
	
	
	
	public Cellobject(List<RealLocalizable> interiorlist, List<RealLocalizable> boundarylist, Localizable location, double cellArea, double size ) {
		
		this.interiorlist = interiorlist;
		
		this.boundarylist = boundarylist;
		
		this.Location = location;
		
		this.cellArea = cellArea;
		
		this.size = size;
		
		
	}
	
}
