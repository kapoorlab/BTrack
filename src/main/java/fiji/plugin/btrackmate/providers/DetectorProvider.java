package fiji.plugin.btrackmate.providers;

import fiji.plugin.btrackmate.detection.SpotDetectorFactoryBase;

@SuppressWarnings( "rawtypes" )
public class DetectorProvider extends AbstractProvider< SpotDetectorFactoryBase >
{

	public DetectorProvider()
	{
		super( SpotDetectorFactoryBase.class );
	}

	public static void main( final String[] args )
	{
		final DetectorProvider provider = new DetectorProvider();
		System.out.println( provider.echo() );
	}

}
