package tracker;

import budDetector.Budobject;
import budDetector.Budpointobject;

public class ForBudTrackCostFunction implements BUDDYCostFunction< Budobject, Budobject >
{

	

		
	@Override
	public double linkingCost( final Budobject source, final Budobject target )
	{
		return source.DistanceTo(target);
	}
		



	}

