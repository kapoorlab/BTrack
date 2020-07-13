package Buddy.plugin.trackmate.providers;

import Buddy.plugin.trackmate.features.edges.GreenEdgeAnalyzer;

/**
 * A provider for the edge analyzers provided in the GUI.
 */
public class GreenEdgeAnalyzerProvider extends AbstractProvider<GreenEdgeAnalyzer> {

	public GreenEdgeAnalyzerProvider() {
		super(GreenEdgeAnalyzer.class);
	}

	public static void main(final String[] args) {
		final EdgeAnalyzerProvider provider = new EdgeAnalyzerProvider();
		System.out.println(provider.echo());
	}
}
