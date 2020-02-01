package org.mastodon.revised.mamut;

import org.mastodon.app.MastodonAppModel;
import org.mastodon.plugin.MastodonPlugins;
import org.mastodon.revised.bdv.SharedBigDataViewerData;
import org.mastodon.revised.bdv.overlay.ui.RenderSettingsManager;
import org.mastodon.revised.model.mamut.BoundingSphereRadiusStatistics;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyleManager;
import org.mastodon.revised.ui.coloring.feature.FeatureColorModeManager;
import org.mastodon.revised.ui.keymap.KeymapManager;
import org.scijava.ui.behaviour.KeyPressedManager;
import org.scijava.ui.behaviour.util.Actions;

/**
 * Data class that stores the data model and the application model of the MaMuT
 * application.
 *
 * @author Jean-Yves Tinevez
 */
public class MamutAppModel extends MastodonAppModel< Model, Spot, Link >
{
	private static final int NUM_GROUPS = 3;

	private final BoundingSphereRadiusStatistics radiusStats;

	private final SharedBigDataViewerData sharedBdvData;

	private final TrackSchemeStyleManager trackSchemeStyleManager;

	private final RenderSettingsManager renderSettingsManager;

	private final FeatureColorModeManager featureColorModeManager;

	private final int minTimepoint;

	private final int maxTimepoint;

	public MamutAppModel(
			final Model model,
			final SharedBigDataViewerData sharedBdvData,
			final KeyPressedManager keyPressedManager,
			final TrackSchemeStyleManager trackSchemeStyleManager,
			final RenderSettingsManager renderSettingsManager,
			final FeatureColorModeManager featureColorModeManager,
			final KeymapManager keymapManager,
			final MastodonPlugins plugins,
			final Actions globalActions )
	{
		super( NUM_GROUPS, model, keyPressedManager, keymapManager, plugins, globalActions, new String[] { KeyConfigContexts.MASTODON } );

		this.radiusStats = new BoundingSphereRadiusStatistics( model );
		this.sharedBdvData = sharedBdvData;
		this.trackSchemeStyleManager = trackSchemeStyleManager;
		this.renderSettingsManager = renderSettingsManager;
		this.featureColorModeManager = featureColorModeManager;
		this.minTimepoint = 0;
		this.maxTimepoint = sharedBdvData.getNumTimepoints() - 1;
		/*
		 * TODO: (?) For now, we use timepoint indices in MaMuT model, instead
		 * of IDs/names. This is because BDV also displays timepoint index, and
		 * it would be confusing to have different labels in TrackScheme. If
		 * this is changed in the future, then probably only in the model files.
		 */
	}

	public TrackSchemeStyleManager getTrackSchemeStyleManager()
	{
		return trackSchemeStyleManager;
	}

	public RenderSettingsManager getRenderSettingsManager()
	{
		return renderSettingsManager;
	}

	public FeatureColorModeManager getFeatureColorModeManager()
	{
		return featureColorModeManager;
	}

	public BoundingSphereRadiusStatistics getRadiusStats()
	{
		return radiusStats;
	}

	public SharedBigDataViewerData getSharedBdvData()
	{
		return sharedBdvData;
	}

	public int getMinTimepoint()
	{
		return minTimepoint;
	}

	public int getMaxTimepoint()
	{
		return maxTimepoint;
	}
}
