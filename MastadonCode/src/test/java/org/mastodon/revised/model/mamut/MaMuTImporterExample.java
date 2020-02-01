package org.mastodon.revised.model.mamut;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.mastodon.project.MamutProjectIO;
import org.mastodon.revised.mamut.WindowManager;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.ModelUtils;
import org.mastodon.revised.model.mamut.trackmate.TrackMateImporter;
import org.scijava.Context;

import mpicbg.spim.data.SpimDataException;

public class MaMuTImporterExample
{

	public static void main( final String[] args ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		Locale.setDefault( Locale.ROOT );
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

		final File mamutFile = new File( "/Users/tinevez/Projects/JYTinevez/MaMuT/Mastodon-dataset/MaMuT_Parhyale_demo-mamut.xml" );
		final File targetMastodonFile = new File("samples/trackmateimported.mastodon");

		importFromMaMuTAndSave( mamutFile, targetMastodonFile );

		reloadAfterSave( targetMastodonFile );
	}

	private static void importFromMaMuTAndSave(final File mamutFile, final File targetMastodonFile)
	{
		final WindowManager windowManager = new WindowManager( new Context() );
		try
		{
			final TrackMateImporter importer = new TrackMateImporter( mamutFile );
			windowManager.getProjectManager().open( importer.createProject() );
			importer.readModel( windowManager.getAppModel().getModel(), windowManager.getFeatureSpecsService() );
		}
		catch ( final IOException | SpimDataException e )
		{
			e.printStackTrace();
		}

		try
		{
			windowManager.getProjectManager().saveProject( targetMastodonFile );
		}
		catch ( final IOException e )
		{
			e.printStackTrace();
		}
	}


	private static void reloadAfterSave( final File targetMastodonFile )
	{
		final WindowManager windowManager = new WindowManager( new Context() );
		try
		{
			windowManager.getProjectManager().open( new MamutProjectIO().load( targetMastodonFile.getAbsolutePath() ) );
			final Model model = windowManager.getAppModel().getModel();
			System.out.println( "After reloading the saved MaMuT import:" );
			System.out.println( ModelUtils.dump( model, 5 ) );
		}
		catch ( final IOException | SpimDataException e )
		{
			e.printStackTrace();
		}
	}
}
