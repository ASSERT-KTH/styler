/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.processor.w3c.xs.validation.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.xml.XMLConstants;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.names.PrefixResolver;

final class ValidationPrefixResolver implements PrefixResolver
{
	private static final Iterable<String> UNARY_DEFAULT_NS_PREFIX_ITERABLE = new UnaryIterable<String>(XMLConstants.DEFAULT_NS_PREFIX);
	private static final Iterable<String> UNARY_XML_NS_PREFIX_ITERABLE = new UnaryIterable<String>(XMLConstants.XML_NS_PREFIX);
	private static final Iterable<String> UNARY_XMLNS_ATTRIBUTE_ITERABLE = new UnaryIterable<String>(XMLConstants.XMLNS_ATTRIBUTE);

	ValidationPrefixResolver(final NameSource nameBridge)
	{
		this.m_prefixes = new String[8];
		this.m_uris = new String [8];
		reset();
	}

	/**
	 * The index of the currently active context. The first context has index zero.
	 */
	private int m_idxContext = -1;

	/**
	 * Parallel array of prefixes and uris and a count of the number used.
	 */
	private String[] m_prefixes;
	private String[] m_uris;
	private int m_numMappings = 0;

	/**
	 * Index into m_prefixes or m_uris of the start of a context.
	 */
	private int[] m_idxStartContext = new int[8];

	/**
	 * Reset this NamespaceSupport for object reuse.
	 */
	public void reset()
	{
		m_idxContext = -1;
		m_numMappings = 0;

		pushContext();
	}

	public void pushContext()
	{
		m_idxContext++;

		// Grow the local arrays if required.
		if (m_idxContext >= m_idxStartContext.length)
		{
			int[] s = m_idxStartContext;
			m_idxStartContext = new int[m_idxStartContext.length * 2];
			System.arraycopy(s, 0, m_idxStartContext, 0, s.length);
		}

		m_idxStartContext[m_idxContext] = m_numMappings;
	}

	public void popContext()
	{
		m_numMappings = m_idxStartContext[m_idxContext];

		m_idxContext--;
	}

	public void declarePrefix(final String prefix, final String uri)
	{
		// Do not remove! This prefix check is an invariant of the API.
		PreCondition.assertArgumentNotNull(prefix, "prefix");
		// Do not remove! This uri check is an invariant of the API.
		PreCondition.assertArgumentNotNull(uri, "uri");

		// Search for a mapping from the top of the array, but limited to the
		// current context.
		for (int i = m_numMappings - 1; i >= m_idxStartContext[m_idxContext]; i--)
		{
			if (equals(prefix, m_prefixes[i]))
			{
				m_uris[i] = uri;
				return;
			}
		}

		final boolean isXmlNamespacePrefix = equals(XMLConstants.XML_NS_PREFIX, prefix);
		final boolean isXmlNamespaceURI = (XMLConstants.XML_NS_URI == uri);

		if (isXmlNamespacePrefix && !isXmlNamespaceURI)
		{
			throw new IllegalArgumentException("The prefix '" + XMLConstants.XML_NS_PREFIX + "' can only be bound to '" + XMLConstants.XML_NS_URI + ".");
		}

		if (isXmlNamespaceURI && !isXmlNamespacePrefix)
		{
			throw new IllegalArgumentException("The namespace '" + XMLConstants.XML_NS_URI + "' can only have the prefix '" + XMLConstants.XML_NS_PREFIX + ".");
		}

		if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix) || XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(uri))
		{
			throw new IllegalArgumentException("Neither the prefix '" + XMLConstants.XMLNS_ATTRIBUTE + "' nor the URI '" + XMLConstants.XMLNS_ATTRIBUTE_NS_URI + "' can be bound.");
		}

		// if (!"".equals(prefix) && !XML11Char.isXML11ValidNCName(prefix))
		// {
		// throw new IllegalArgumentException("Prefix '" + prefix +
		// "' is not a valid XML 1.1 NCName");
		// }

		// Grow the arrays if required.
		if (m_numMappings >= m_prefixes.length)
		{
			String[] p = m_prefixes;
			m_prefixes = new String[m_prefixes.length * 2];
			System.arraycopy(p, 0, m_prefixes, 0, p.length);

			String[] u = m_uris;
			m_uris = new String[m_uris.length * 2];
			System.arraycopy(u, 0, m_uris, 0, u.length);
		}

		// Define the new mapping.
		m_prefixes[m_numMappings] = prefix;
		m_uris[m_numMappings] = uri;

		// Keep track of the total number of mappings.
		m_numMappings++;
	}

	public String getNamespace(final String prefix)
	{
		PreCondition.assertArgumentNotNull(prefix);

		// Search for a mapping from the top of the array.
		for (int i = m_numMappings - 1; i >= 0; i--)
		{
			if (equals(prefix, m_prefixes[i]))
			{
				return m_uris[i];
			}
		}

		final int length = prefix.length();

		if (3 == length)
		{
			if (equals(XMLConstants.XML_NS_PREFIX, prefix))
			{
				return XMLConstants.XML_NS_URI;
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * Given a namespace-namespaceURI, get the corresponding prefix. In the case of ambiguity, the contract for this
	 * interface is to return a prefix that does not correspond to the prefix mapping for the default namespace.
	 * 
	 * @param namespaceURI
	 *            The namespace URI to look up.
	 * @return The associated prefix.
	 */
	public Iterable<String> getPrefixes(final String namespaceURI)
	{
		ArrayList<String> prefixes = null;

		// Search for a mapping from the top of the array.
		for (int i = m_numMappings - 1; i >= 0; i--)
		{
			if (namespaceURI.equals(m_uris[i]))
			{
				if (null == prefixes)
				{
					prefixes = new ArrayList<String>();
				}
				prefixes.add(m_prefixes[i]);
			}
		}

		if (null != prefixes)
		{
			return prefixes;
		}
		else
		{
			if (namespaceURI.equals(XMLConstants.NULL_NS_URI))
			{
				return UNARY_DEFAULT_NS_PREFIX_ITERABLE;
			}
			if (namespaceURI.equals(XMLConstants.XML_NS_URI))
			{
				return UNARY_XML_NS_PREFIX_ITERABLE;
			}
			else if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI))
			{
				return UNARY_XMLNS_ATTRIBUTE_ITERABLE;
			}
			else
			{
				return Collections.emptyList();
			}
		}
	}

	/**
	 * Returns a prefix that is associated with the specified namespace-uri. If there are no viable prefix mappings then
	 * <code>null</code> is returned.
	 * 
	 * <p>
	 * If there is ambiguity in the mappings available, a prefix hint can be used to suggest the most appropriate
	 * alternative. The prefix hint may be supplied as <code>null</code> to indicate no preference. The prefix hint will
	 * normally be supplied from the original parsed document. This practice can be used to make an output document
	 * similar to an input document.
	 * </p>
	 * <p>
	 * A mapping for the default (zero-length) prefix will only be considered if <code>mayUseDefaultMapping</code> is
	 * set to <code>true</code>. When determining the prefix for element names, the setting should be <code>true</code>.
	 * For attribute names, the setting should be <code>false</code>.
	 * </p>
	 * 
	 * @param namespaceURI
	 *            The namespace-uri for which the prefix is required. May not be <code>null</code>.
	 * @param prefixHint
	 *            A prefix hint for selecting from alternative mappings. May be <code>null</code> to indicate no
	 *            preference.
	 * @param mayUseDefaultMapping
	 *            Determines whether the default namespace prefix is acceptable.
	 */
	public String getPrefix(final String namespaceURI, final String prefixHint, final boolean mayUseDefaultMapping)
	{
		PreCondition.assertArgumentNotNull(namespaceURI, "namespaceURI");

		String bestPrefix = null;

		// Search for a mapping from the top of the array.
		for (int i = m_numMappings - 1; i >= 0; i--)
		{
			if (namespaceURI.equals(m_uris[i]))
			{
				final String currentPrefix = m_prefixes[i];

				if ((currentPrefix.length() > 0) || mayUseDefaultMapping)
				{
					if (null != prefixHint)
					{
						if (equals(currentPrefix, prefixHint))
						{
							return currentPrefix;
						}
					}

					if (null == bestPrefix)
					{
						bestPrefix = currentPrefix;
					}
				}
			}
		}

		if (null != bestPrefix)
		{
			return bestPrefix;
		}
		else
		{
			if (namespaceURI.equals(XMLConstants.NULL_NS_URI))
			{
				if (mayUseDefaultMapping)
				{
					return XMLConstants.DEFAULT_NS_PREFIX;
				}
				else
				{
					return null;
				}
			}
			else if (namespaceURI.equals(XMLConstants.XML_NS_URI))
			{
				return XMLConstants.XML_NS_PREFIX;
			}
			else if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI))
			{
				return XMLConstants.XMLNS_ATTRIBUTE;
			}
			else
			{
				return null;
			}
		}
	}

	public Enumeration<String> getDeclaredPrefixes()
	{
		return new DeclaredPrefixEnumeration();
	}

	public boolean anyDeclaredPrefixes()
	{
		// There is no danger of an ArrayIndexOutOfBoundsException
		// because the context at the bottom of the stack is initialized
		// to contain the reserved "xml" prefix.
		return m_idxStartContext[m_idxContext] < m_numMappings;
	}

	class DeclaredPrefixEnumeration implements Enumeration<String>
	{
		/**
		 * Index into m_prefixes. We'll start it at the bottom of the context to preserve arrival sequence and
		 * increment.
		 */
		private int m_idxMapping;

		public DeclaredPrefixEnumeration()
		{
			m_idxMapping = m_idxStartContext[m_idxContext];
		}

		public boolean hasMoreElements()
		{
			return m_idxMapping < m_numMappings;
		}

		public String nextElement()
		{
			return m_prefixes[m_idxMapping++];
		}
	}

	/**
	 * Type-safe function, non-virtual S/B minimal overhead.
	 */
	private static boolean equals(final String lhs, final String rhs)
	{
		return lhs.equals(rhs);
	}
}
