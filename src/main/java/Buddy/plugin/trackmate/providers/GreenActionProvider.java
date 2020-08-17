package Buddy.plugin.trackmate.providers;

import Buddy.plugin.trackmate.action.GreenTrackMateActionFactory;

public class GreenActionProvider extends AbstractProvider<GreenTrackMateActionFactory> {

	public GreenActionProvider() {
		super(GreenTrackMateActionFactory.class);
	}

	public static void main(final String[] args) {
		final GreenActionProvider provider = new GreenActionProvider();
		System.out.println(provider.echo());
	}

}
