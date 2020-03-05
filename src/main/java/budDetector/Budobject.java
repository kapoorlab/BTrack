package budDetector;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;

public class Budobject extends AbstractEuclideanSpace implements RealLocalizable, Comparable<Budobject> {

	
	public final double[] Budcenter;
	public final ArrayList<RealLocalizable> linelist;
	public final ArrayList<RealLocalizable> dynamiclinelist;
	public final int t;
	private String name;
	private final int ID;
	private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();
	public static AtomicInteger IDcounter = new AtomicInteger( -1 );
	public final double perimeter;
	
	
	
	public Budobject(final double[] Budcenter, final ArrayList<RealLocalizable> linelist, final ArrayList<RealLocalizable> dynamiclinelist, final int t, final double perimeter) {
		
		super(3);
		
		this.Budcenter = Budcenter;
		
		this.linelist = linelist;
		
		this.dynamiclinelist = dynamiclinelist;
		
		this.t = t;
		
		this.perimeter = perimeter;
		
		this.ID = IDcounter.incrementAndGet();
		this.name = "ID" + ID;
		
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
		return (float) Budcenter[d];
	}

	@Override
	public double getDoublePosition(int d) {
		return Budcenter[d];
	}

}
