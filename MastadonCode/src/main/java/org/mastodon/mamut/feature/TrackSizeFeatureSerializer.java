package org.mastodon.mamut.feature;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.mastodon.collection.RefCollection;
import org.mastodon.feature.io.FeatureSerializer;
import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.io.ObjectToFileIdMap;
import org.mastodon.io.properties.IntPropertyMapSerializer;
import org.mastodon.mamut.feature.TrackSizeFeature.Spec;
import org.mastodon.properties.IntPropertyMap;
import org.mastodon.revised.model.mamut.Spot;
import org.scijava.plugin.Plugin;

@Plugin( type = TrackSizeFeatureSerializer.class )
public class TrackSizeFeatureSerializer implements FeatureSerializer< TrackSizeFeature, Spot >
{

	@Override
	public Spec getFeatureSpec()
	{
		return TrackSizeFeature.SPEC;
	}

	@Override
	public void serialize( final TrackSizeFeature feature, final ObjectToFileIdMap< Spot > idmap, final ObjectOutputStream oos ) throws IOException
	{
		final IntPropertyMapSerializer< Spot > propertyMapSerializer = new IntPropertyMapSerializer<>( feature.map );
		propertyMapSerializer.writePropertyMap( idmap, oos );
	}

	@Override
	public TrackSizeFeature deserialize( final FileIdToObjectMap< Spot > idmap, final RefCollection< Spot > pool, final ObjectInputStream ois ) throws IOException, ClassNotFoundException
	{
		final IntPropertyMap< Spot > map = new IntPropertyMap<>( pool, -1 );
		final IntPropertyMapSerializer< Spot > propertyMapSerializer = new IntPropertyMapSerializer<>( map );
		propertyMapSerializer.readPropertyMap( idmap, ois );
		return new TrackSizeFeature( map );
	}
}
