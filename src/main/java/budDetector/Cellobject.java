package budDetector;


import java.util.List;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;

public class Cellobject {

	
	public final double cellVolume;
	
	public final double cellPerimeter;
	
	public final Localizable Location;
	
	public final double[] extents;
	
	public final double totalIntensity;
	
	public final List<RealLocalizable> boundarylist;
	
	public final List<RealLocalizable> interiorlist;
	
	
	
	public Cellobject(List<RealLocalizable> interiorlist, List<RealLocalizable> boundarylist, Localizable location, double totalIntensity, double cellVolume, double cellPerimeter, double[] extents ) {
		
		this.interiorlist = interiorlist;
		
		this.boundarylist = boundarylist;
		
		this.Location = location;
		
		this.cellVolume = cellVolume;
		
		this.cellPerimeter = cellPerimeter;
		
		this.extents = extents;
		
		this.totalIntensity = totalIntensity;
		
	}
	
}
