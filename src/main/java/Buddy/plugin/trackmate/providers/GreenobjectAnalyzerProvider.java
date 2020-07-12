package Buddy.plugin.trackmate.providers;

import Buddy.plugin.trackmate.features.spot.GreenobjectAnalyzerFactory;


	
	
	@SuppressWarnings("rawtypes")
	public class GreenobjectAnalyzerProvider extends AbstractProvider<GreenobjectAnalyzerFactory> {

		public GreenobjectAnalyzerProvider() {
			super(GreenobjectAnalyzerFactory.class);
		}

		public static void main(final String[] args) {
			final GreenobjectAnalyzerProvider provider = new GreenobjectAnalyzerProvider();
			System.out.println(provider.echo());
		}
	}

	
	
