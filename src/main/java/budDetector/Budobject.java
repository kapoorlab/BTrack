package budDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;

public class Budobject extends AbstractEuclideanSpace implements RealLocalizable, Comparable<Budobject> {

	
	public final RealLocalizable Budcenter;
	public final List<RealLocalizable> linelist;
	public final List<RealLocalizable> dynamiclinelist;
	public final int t;
	private String name;
	public final int ID;
	private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();
	public final double perimeter;
	
	
	
	public Budobject(final RealLocalizable Budcenter, final List<RealLocalizable> linelist, final List<RealLocalizable> dynamiclinelist, final int t, final int ID, final double perimeter) {
		
		super(3);
		
		this.Budcenter = Budcenter;
		
		this.linelist = linelist;
		
		this.dynamiclinelist = dynamiclinelist;
		
		this.t = t;
		
		this.perimeter = perimeter;
		
		this.ID = ID;
		
	}
	
	
	
	public static final String XPOSITION = "XPOSITION";
	public static final String YPOSITION = "XPOSITION";
	public static final String TIME = "TIME";
	
	
	public void setName(final String name) {
		
		this.name = name;
	}
	
	public int ID() {
		
		return ID;
	}
	
	public final Double getFeature(final String feature) {
		
		return features.get(feature);
	}
	
	@Override
	public int compareTo(Budobject o) {
		return hashCode() - o.hashCode();
	}

	@Override
	public void localize(float[] position) {
		int n = position.length;
		for (int d = 0; d < n; ++d)
			position[d] = getFloatPosition(d);
		
	}

	@Override
	public void localize(double[] position) {
		int n = position.length;
		for (int d = 0; d < n; ++d)
			position[d] = getFloatPosition(d);
		
	}

	@Override
	public float getFloatPosition(int d) {
		return (float) Budcenter.getFloatPosition(d);
	}

	@Override
	public double getDoublePosition(int d) {
		return Budcenter.getDoublePosition(d);
	}

}
