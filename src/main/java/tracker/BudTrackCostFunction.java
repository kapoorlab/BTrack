package tracker;

import budDetector.Budpointobject;

public class BudTrackCostFunction implements BUDDYCostFunction< Budpointobject, Budpointobject >
{

	

		
	@Override
	public double linkingCost( final Budpointobject source, final Budpointobject target )
	{
		return source.DistanceTo(target);
	}
		



	}

