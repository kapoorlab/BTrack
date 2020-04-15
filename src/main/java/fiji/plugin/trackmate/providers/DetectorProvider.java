package fiji.plugin.trackmate.providers;

import fiji.plugin.trackmate.detection.BCellobjectDetectorFactory;

@SuppressWarnings( "rawtypes" )
public class DetectorProvider extends AbstractProvider< BCellobjectDetectorFactory >
{

	public DetectorProvider()
	{
		super( BCellobjectDetectorFactory.class );
	}

	public static void main( final String[] args )
	{
		final DetectorProvider provider = new DetectorProvider();
		System.out.println( provider.echo() );
	}

}
