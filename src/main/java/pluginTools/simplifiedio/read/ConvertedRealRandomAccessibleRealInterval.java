/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */


package pluginTools.simplifiedio.read;

import net.imglib2.AbstractWrappedRealInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.View;
import net.imglib2.converter.Converter;
import net.imglib2.type.Type;

/**
 * TODO
 * 
 */
public class ConvertedRealRandomAccessibleRealInterval< A, B extends Type< B > > extends AbstractWrappedRealInterval< RealRandomAccessibleRealInterval< A > > implements RealRandomAccessibleRealInterval< B >, View
{
	final protected Converter< ? super A, ? super B > converter;

	final protected B converted;

	public ConvertedRealRandomAccessibleRealInterval( final RealRandomAccessibleRealInterval< A > source, final Converter< ? super A, ? super B > converter, final B b )
	{
		super( source );
		this.converter = converter;
		this.converted = b.copy();
	}

	@Override
	public ConvertedRealRandomAccess< A, B > realRandomAccess()
	{
		return new ConvertedRealRandomAccess< A, B >( sourceInterval.realRandomAccess(), converter, converted );
	}

	@Override
	public ConvertedRealRandomAccess< A, B > realRandomAccess( final RealInterval interval )
	{
		return new ConvertedRealRandomAccess< A, B >( sourceInterval.realRandomAccess( interval ), converter, converted );
	}

	/**
	 * @return an instance of the destination {@link Type}.
	 */
	public B getDestinationType()
	{
		return converted.copy();
	}

	public Converter< ? super A, ? super B > getConverter()
	{
		return converter;
	}
}
