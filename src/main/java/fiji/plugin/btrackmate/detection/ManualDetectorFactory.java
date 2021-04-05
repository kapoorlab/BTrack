package fiji.plugin.btrackmate.detection;

import static fiji.plugin.btrackmate.detection.DetectorKeys.DEFAULT_RADIUS;
import static fiji.plugin.btrackmate.detection.DetectorKeys.KEY_RADIUS;
import static fiji.plugin.btrackmate.io.IOUtils.readDoubleAttribute;
import static fiji.plugin.btrackmate.io.IOUtils.writeRadius;
import static fiji.plugin.btrackmate.util.TMUtils.checkMapKeys;
import static fiji.plugin.btrackmate.util.TMUtils.checkParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.jdom2.Element;
import org.scijava.plugin.Plugin;

import fiji.plugin.btrackmate.Model;
import fiji.plugin.btrackmate.Settings;
import fiji.plugin.btrackmate.Spot;
import fiji.plugin.btrackmate.gui.components.ConfigurationPanel;
import fiji.plugin.btrackmate.gui.components.detector.ManualDetectorConfigurationPanel;
import net.imagej.ImgPlus;
import net.imglib2.Interval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

@Plugin( type = SpotDetectorFactory.class )
public class ManualDetectorFactory< T extends RealType< T > & NativeType< T > > implements SpotDetectorFactory< T >
{

	public static final String DETECTOR_KEY = "MANUAL_DETECTOR";

	public static final String NAME = "Manual annotation";

	public static final String INFO_TEXT = "<html>"
			+ "Selecting this will skip the automatic detection phase, and jump directly <br>"
			+ "to manual segmentation. A default spot size will be asked for. "
			+ "</html>";

	protected String errorMessage;

	protected Map< String, Object > settings;

	@Override
	public boolean has2Dsegmentation()
	{
		return true;
	}

	@Override
	public SpotDetector< T > getDetector( final Interval interval, final int frame )
	{
		return new SpotDetector< T >()
		{

			@Override
			public List< Spot > getResult()
			{
				return Collections.emptyList();
			}

			@Override
			public boolean checkInput()
			{
				return true;
			}

			@Override
			public boolean process()
			{
				return true;
			}

			@Override
			public String getErrorMessage()
			{
				return null;
			}

			@Override
			public long getProcessingTime()
			{
				return 0;
			}
		};
	}

	@Override
	public String getKey()
	{
		return DETECTOR_KEY;
	}

	@Override
	public String toString()
	{
		return NAME;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

	@Override
	public boolean setTarget( final ImgPlus< T > img, final Map< String, Object > settings )
	{
		this.settings = settings;
		return checkSettings( settings );
	}

	@Override
	public boolean checkSettings( final Map< String, Object > lSettings )
	{
		final StringBuilder errorHolder = new StringBuilder();
		boolean ok = true;
		ok = ok & checkParameter( lSettings, KEY_RADIUS, Double.class, errorHolder );
		final List< String > mandatoryKeys = new ArrayList<>();
		mandatoryKeys.add( KEY_RADIUS );
		ok = ok & checkMapKeys( lSettings, mandatoryKeys, null, errorHolder );
		if ( !ok )
			errorMessage = errorHolder.toString();

		return ok;
	}

	@Override
	public boolean marshall( final Map< String, Object > lSettings, final Element element )
	{
		final StringBuilder errorHolder = new StringBuilder();
		final boolean ok = writeRadius( lSettings, element, errorHolder );
		if ( !ok )
			errorMessage = errorHolder.toString();
		return ok;
	}

	@Override
	public boolean unmarshall( final Element element, final Map< String, Object > lSettings )
	{
		lSettings.clear();
		final StringBuilder errorHolder = new StringBuilder();
		final boolean ok = readDoubleAttribute( element, lSettings, KEY_RADIUS, errorHolder );
		if ( !ok )
		{
			errorMessage = errorHolder.toString();
			return false;
		}
		return checkSettings( lSettings );
	}

	@Override
	public ConfigurationPanel getDetectorConfigurationPanel( final Settings settings, final Model model )
	{
		return new ManualDetectorConfigurationPanel( INFO_TEXT, NAME );
	}

	@Override
	public String getInfoText()
	{
		return INFO_TEXT;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public Map< String, Object > getDefaultSettings()
	{
		final Map< String, Object > lSettings = new HashMap<>();
		lSettings.put( KEY_RADIUS, DEFAULT_RADIUS );
		return lSettings;
	}

	@Override
	public ImageIcon getIcon()
	{
		return null;
	}
}
