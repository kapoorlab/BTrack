package fiji.plugin.btrackmate.providers;

import fiji.plugin.btrackmate.tracking.SpotTrackerFactory;

public class TrackerProvider extends AbstractProvider< SpotTrackerFactory >
{


	public TrackerProvider()
	{
		super( SpotTrackerFactory.class );
	}

	public static void main( final String[] args )
	{
		final TrackerProvider provider = new TrackerProvider();
		System.out.println( provider.echo() );
	}
}
