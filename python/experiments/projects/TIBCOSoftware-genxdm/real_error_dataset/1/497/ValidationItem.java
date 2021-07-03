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
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.genxdm.processor.w3c.xs.xmlrep.util.SrcFrozenLocation;
import org.genxdm.xs.resolve.LocationInSchema;


/**
 * Keeps track of state for the current element as they are pushed on and popped off the stack with startElement and
 * endElement. There is not a stack object per-se.. These are state objects that are linked together with parent and
 * child pointers and supply push and pop methods. Also note that the objects are recycled by the push method using the
 * child pointer.
 */
final class ValidationItem implements Locatable
{
	private ValidationItem m_parentItem;

	public ValidationItem getParentItem()
	{
		return m_parentItem;
	}

	private ValidationItem m_childItem; // for recycling

	// The index or the element information item in document order.
	private int m_elementIndex;

	public int getElementIndex()
	{
		return m_elementIndex;
	}

	private int m_lineNumber;
	private int m_columnNumber;
	private int m_characterOffset;
	private String m_publicId;
	private String m_systemId;

	/**
	 * @return An opaque frozen location.
	 */
	public LocationInSchema getLocation()
	{
		return new SrcFrozenLocation(m_lineNumber, m_columnNumber, m_characterOffset, m_publicId, m_systemId);
	}

	// The XML Schema specification does not specify what a validating processor
	// should do after
	// it encounters an error. It is not obliged to report more than the first
	// error.
	private boolean m_suspendChecking;

	// Keep track of whether we have seen text nodes as children of this node.
	public boolean m_detectedText;

	/**
	 * Identity scopes may exist for an element information item.
	 */
	public final ArrayList<IdentityScope> m_identityScopes = new ArrayList<IdentityScope>();

	/**
	 * Track the xs:key scopes by name so that we can associate them with xs:keyref scopes.
	 */
	public final HashMap<QName, IdentityScopeKey> m_keyScopes = new HashMap<QName, IdentityScopeKey>();
	/**
	 * The keyref scopes where the name is the key name.
	 */
	public final HashMap<QName, ArrayList<IdentityScopeRef>> m_refScopes = new HashMap<QName, ArrayList<IdentityScopeRef>>();

	private ValidationItem(final ValidationItem parent)
	{
		m_parentItem = parent;
	}

	public ValidationItem()
	{
		this(null);
	}

	public ValidationItem push(final int elementIndex)
	{
		if (m_childItem == null)
		{
			m_childItem = new ValidationItem(this);
		}
		else
		{
			m_childItem.reset();
		}
		m_childItem.m_elementIndex = elementIndex;

		// TODO: Need to get the element location.
		m_childItem.m_lineNumber = -1;
		m_childItem.m_columnNumber = -1;
		m_childItem.m_characterOffset = -1;
		m_childItem.m_publicId = null;
		m_childItem.m_systemId = null;

		m_childItem.m_detectedText = false;

		return m_childItem;
	}

	public ValidationItem pop()
	{
		return m_parentItem;
	}

	private void reset()
	{
		m_suspendChecking = (null != m_parentItem) && m_parentItem.m_suspendChecking;

		m_detectedText = false;
	}

	public boolean getSuspendChecking()
	{
		return m_suspendChecking;
	}

	/**
	 * Returns a key scope for the specified name by searching up the chain of information items.
	 * 
	 * @param name
	 *            The name of the key scope.
	 * @return The key scope or <code>null</code>.
	 */
	public static  IdentityScopeKey getKeyIdentityScope(final ValidationItem origin, final QName name)
	{
		final ValidationItem item = findItemWithKeyConstraint(origin, name);
		if (null != item)
		{
			final IdentityScopeKey keyScope = item.m_keyScopes.get(name);
			if (null != keyScope)
			{
				return keyScope;
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

	public static  ValidationItem findItemWithKeyConstraint(final ValidationItem origin, final QName name)
	{
		// Try locally first.
		if (null != origin.m_keyScopes)
		{
			final IdentityScopeKey keyScope = origin.m_keyScopes.get(name);
			if (null != keyScope)
			{
				return origin;
			}
		}
		// Try the parent information item.
		if (null != origin.m_parentItem)
		{
			return findItemWithKeyConstraint(origin.m_parentItem, name);
		}
		else
		{
			return null;
		}
	}
}
