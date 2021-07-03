package org.genxdm.samples.performance.bridges;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.Resolved;
import org.genxdm.io.Resolver;

public final class SampleResolver implements Resolver
{
	final URI baseURI;

	public SampleResolver(final URI baseURI)
	{
		this.baseURI = PreCondition.assertArgumentNotNull(baseURI, "baseURI");
	}

	/**
	 * Convert a URI relative to a base URI into an input source.
	 * <p/>
	 * This default implementation requires that neither parameter be null, and performs the expected action to retrieve
	 * the input source (which may involve network access).
	 * 
	 * @param baseURI
	 *            the base URI against which the target is to be resolved; must not be null
	 * @param location
	 *            the URI to resolve; must not be null
	 * @return a pair of InputStream and resolved URI.
	 */
	public Resolved<InputStream> resolveInputStream(final URI location) throws IOException
	{
		PreCondition.assertArgumentNotNull(location, "uri");
		if (location.isAbsolute())
		{
			return retrieve(location, location);
		}
		else
		{
			PreCondition.assertArgumentNotNull(baseURI, "baseURI");

			final URI base = baseURI.normalize();
			final URI resolved = base.resolve(location);

			return retrieve(location, resolved);
		}
	}

	private Resolved<InputStream> retrieve(final URI location, final URI uri) throws IOException
	{
		PreCondition.assertArgumentNotNull(uri, "uri");

		final URL toRetrieve;

		if (!uri.isAbsolute()) // assume local file
		{
			final File canonFile = new File(uri.toString()).getCanonicalFile();
			toRetrieve = canonFile.toURI().toURL();
		}
		else
		{
			toRetrieve = uri.toURL();
		}

		if (toRetrieve == null)
		{
			throw new FileNotFoundException(uri.toString());
		}

		final InputStream stream = toRetrieve.openStream();
		if (stream == null)
		{
			throw new FileNotFoundException(toRetrieve.toString());
		}
		try
		{
			return new Resolved<InputStream>(location, stream, toRetrieve.toURI());
		}
		catch (final URISyntaxException e)
		{
			throw new AssertionError(e);
		}
	}
	public Resolved<Reader> resolveReader(URI location) throws IOException {
		throw new UnsupportedOperationException("resolverReader");
	}
}
