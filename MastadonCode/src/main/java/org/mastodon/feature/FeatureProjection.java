package org.mastodon.feature;

/**
 *
 * @param <T>
 *            target the type of the {@link Feature} this feature-projection is
 *            defined for.
 */
public interface FeatureProjection< T >
{

	public FeatureProjectionKey getKey();

	/**
	 * Returns whether the feature value is set for the specified object.
	 *
	 * @param obj
	 *            the object.
	 * @return <code>true</code> if a value is present for the specified object,
	 *         <code>false</code> otherwise.
	 */
	public boolean isSet( T obj );

	/**
	 * Returns the value of this projection for the specified object.
	 *
	 * @param obj
	 *            the object.
	 * @return the feature projection value as a <code>double</code>.
	 */
	public double value( T obj );

	/**
	 * Returns the units of this projection.
	 *
	 * @return the physical units.
	 */
	public String units();

}
