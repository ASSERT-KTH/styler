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

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcElementFixedValueOverriddenSimpleException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcElementUnexpectedChildInNilledElementException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcUnexpectedTextInEmptyContentException;
import org.genxdm.processor.w3c.xs.exception.sm.SmExceptionSupplier;
import org.genxdm.processor.w3c.xs.validation.api.VxMapping;
import org.genxdm.processor.w3c.xs.validation.api.VxOutputHandler;
import org.genxdm.processor.w3c.xs.validation.api.VxPSVI;
import org.genxdm.processor.w3c.xs.validation.api.VxSchemaDocumentLocationStrategy;
import org.genxdm.processor.w3c.xs.validation.api.VxValidator;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.constraints.ValueConstraint;
import org.genxdm.xs.enums.ProcessContentsMode;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.exceptions.SimpleTypeException;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ContentType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.SimpleUrType;
import org.genxdm.xs.types.Type;

/**
 * The workhorse of validation is this kernel. Currently there is an explicit coupling to the WXS schema model. However,
 * this class is package protected so overall, the validation API is schema model independent. In future, we may try to
 * create a more abstract kernel.
 */
final class ValidationKernel<A> implements VxValidator<A>, SmExceptionSupplier
{
	// Set by reset method. Preconditions guarantee that it is never null.
	// private final ParticleTerm STRICT_WILDCARD = new StrictWildcard<A>();

	private static boolean isWhiteSpace(final String strval)
	{
		if (null != strval)
		{
			final int n = strval.length();

			for (int i = 0; i < n; i++)
			{
				final char ch = strval.charAt(i);

				// The follwing pattern is denormalized for speed.
				if ((ch == 0x20) || (ch == 0x09) || (ch == 0xD) || (ch == 0xA))
				{
					// Try the next one, all must be whiteSpace.
				}
				else
				{
					return false;
				}
			}
		}

		return true;
	}

	public ValidationKernel(final AtomBridge<A> atomBridge, final VxSchemaDocumentLocationStrategy sdl)
	{
		m_atomBridge = PreCondition.assertNotNull(atomBridge);
		NameSource names = NameSource.SINGLETON;
		m_namespaces = new ValidationPrefixResolver(names);
		m_currentItem = m_documentItem = new ValidationItem();
		// A strict start is necessary to ensure that the root element has a declaration.
		// However, the specification does not seem very clear on what should be the starting mode.
		this.sdl = sdl;
	}

	public void characters(final char[] ch, final int start, final int length)
	{
		m_text.append(ch, start, length);
	}

	private void checkValueConstraintForElement(final ElementDefinition elementDeclaration, final SimpleType simpleType, final List<? extends A> actualValue) throws AbortException
	{
		final ValueConstraint valueConstraint = elementDeclaration.getValueConstraint();
		if (null != valueConstraint)
		{
			switch (valueConstraint.getVariety())
			{
				case Fixed:
				{
					final List<A> initialFixed = valueConstraint.getValue(m_atomBridge);

					try
					{
						final List<A> actualFixed = simpleType.validate(initialFixed, m_atomBridge);
						if (!ValidationSupport.equalValues(actualFixed, actualValue))
						{
							final String fixedC14N = m_atomBridge.getC14NString(actualFixed);
							final String actualC14N = m_atomBridge.getC14NString(actualValue);
							m_errors.error(new CvcElementFixedValueOverriddenSimpleException(elementDeclaration, fixedC14N, actualC14N, m_currentItem.getLocation()));
						}
					}
					catch (final DatatypeException e)
					{
						final String lexicalValue = m_atomBridge.getC14NString(initialFixed);
						m_errors.error(new SimpleTypeException(lexicalValue, simpleType, e));
					}
				}
				break;
				case Default:
				{
					// No problem.
				}
				break;
				default:
				{
					throw new AssertionError(valueConstraint.getVariety());
				}
			}
		}
	}

	public void endDocument() throws IOException, AbortException
	{
		m_mac.endDocument();

		// Check for dangling IDREFs here.
		m_idm.reportDanglingIdRefs(m_errors);

		if (null != m_downstream)
		{
			m_downstream.endDocument();
		}
	}

	public VxPSVI endElement() throws IOException, AbortException
	{
		if (m_text.length() > 0)
		{
			try
			{
				handleText(m_text.toString());
			}
			finally
			{
				m_text.setLength(0);
			}
		}

		try
		{
			if (!m_currentItem.m_detectedText)
			{
				handleNoTextCalls();
			}

			final VxPSVI psvi = m_mac.endElement();

			m_icm.endElement(m_currentPSVI, m_currentItem);

			return psvi;
		}
		finally
		{
			m_currentItem = m_currentItem.pop();
			m_currentPSVI = m_currentPSVI.getParent();

			// Maintain prefix mapping information.
			m_namespaces.popContext();

			if (null != m_downstream)
			{
				m_downstream.endElement();
			}
		}
	}

	private void handleNoTextCalls() throws IOException, AbortException
	{
		final Type elementType = m_currentPSVI.getType();
		if (null != elementType)
		{
			if (elementType instanceof SimpleType)
			{
				handleNoTextCallsForSimpleContentModel((SimpleType)elementType);
			}
			else
			{
				final ComplexType complexType = (ComplexType)elementType;
				final ContentType contentType = complexType.getContentType();
				switch (contentType.getKind())
				{
					case Simple:
					{
						handleNoTextCallsForSimpleContentModel(contentType.getSimpleType());
					}
					break;
					case Empty:
					case ElementOnly:
					case Mixed:
					{
						// Do nothing
					}
					break;
					default:
					{
						throw new AssertionError(contentType.getKind());
					}
				}
			}
		}
	}

	private void handleNoTextCallsForSimpleContentModel(final SimpleType simpleType) throws IOException, AbortException
	{
		if (m_currentPSVI.isNilled())
		{
			// OK
		}
		else
		{
			// any default or fixed values.
			final ElementDefinition declaration = m_currentPSVI.getDeclaration();
			final ValueConstraint valueConstraint = (null != declaration) ? declaration.getValueConstraint() : null;
			if (null != valueConstraint)
			{
				switch (valueConstraint.getVariety())
				{
					case Fixed:
					case Default:
					{
						final List<A> initialValue = valueConstraint.getValue(m_atomBridge);
						try
						{
							final List<A> actualValue = simpleType.validate(initialValue, m_atomBridge);

							m_idm.text(actualValue, simpleType, m_currentItem, m_errors, m_atomBridge);
							m_icm.text(actualValue, simpleType, m_currentItem, m_nodeIndex, m_atomBridge);

							if (null != m_downstream)
							{
								m_downstream.text(actualValue);
							}
						}
						catch (final DatatypeException e)
						{
							final String lexicalValue = m_atomBridge.getC14NString(initialValue);
							m_errors.error(new SimpleTypeException(lexicalValue, simpleType, e));

							m_idm.text(initialValue, simpleType, m_currentItem, m_errors, m_atomBridge);
							m_icm.text(initialValue, simpleType, m_currentItem, m_nodeIndex, m_atomBridge);

							if (null != m_downstream)
							{
								m_downstream.text(initialValue);
							}
						}
					}
					break;
					default:
					{
						throw new AssertionError(valueConstraint.getVariety());
					}
				}
			}
			else
			{
				// If no value given above then call the validator with an empty string
				// which will throw an exception if having no text is a problem.
				try
				{
					final List<A> actualValue = simpleType.validate("", m_atomBridge);

					m_idm.text(actualValue, simpleType, m_currentItem, m_errors, m_atomBridge);
					m_icm.text(actualValue, simpleType, m_currentItem, m_nodeIndex, m_atomBridge);

					if (null != m_downstream)
					{
						m_downstream.text(actualValue);
					}
				}
				catch (final DatatypeException e)
				{
					m_errors.error(new SimpleTypeException("", simpleType, e));
				}
			}
		}
	}

	private void handleText(final String initialValue) throws IOException, AbortException
	{
		m_nodeIndex++;

		// Remember that we got a text node.
		m_currentItem.m_detectedText = true;

		if (m_currentItem.getSuspendChecking())
		{
			if (null != m_downstream)
			{
				m_downstream.text(initialValue);
			}
			return;
		}

		switch (m_currentPSVI.getProcessContents())
		{
			case Strict:
			case Lax:
			{
				final ElementDefinition declaration = m_currentPSVI.getDeclaration();
				if (m_currentPSVI.isNilled() && (null != declaration))
				{
					m_errors.error(new CvcElementUnexpectedChildInNilledElementException(declaration, m_currentItem.getLocation()));
				}

				final Type elementType = m_currentPSVI.getType();
				if (null != elementType)
				{
					if (elementType instanceof SimpleType)
					{
					    if (elementType instanceof SimpleUrType)
                        {
                            if (null != m_downstream)
                            {
                                m_downstream.text(initialValue);
                            }
                        }
					    else if (elementType instanceof SimpleType)
						{
							final SimpleType simpleType = (SimpleType)elementType;
							try
							{
								final List<A> actualValue = simpleType.validate(initialValue, m_atomBridge);

								if (null != declaration)
								{
									checkValueConstraintForElement(declaration, simpleType, actualValue);
								}

								m_idm.text(actualValue, simpleType, m_currentItem, m_errors, m_atomBridge);
								m_icm.text(actualValue, simpleType, m_currentItem, m_nodeIndex, m_atomBridge);

								if (null != m_downstream)
								{
									m_downstream.text(actualValue);
								}
							}
							catch (final DatatypeException e)
							{
								m_errors.error(new SimpleTypeException(initialValue, simpleType, e));
								if (null != m_downstream)
								{
									m_downstream.text(initialValue);
								}
							}
						}
						else
						{
							throw new AssertionError(elementType);
						}
					}
					else if (elementType instanceof ComplexType)
					{
						final ComplexType complexType = (ComplexType)elementType;
						final ContentType contentType = complexType.getContentType();
						switch (contentType.getKind())
						{
							case Simple:
							{
								final SimpleType simpleType = contentType.getSimpleType();
								try
								{
									final List<A> actualValue = simpleType.validate(initialValue, m_atomBridge);

									if (null != declaration)
									{
										checkValueConstraintForElement(declaration, simpleType, actualValue);
									}

									m_idm.text(actualValue, simpleType, m_currentItem, m_errors, m_atomBridge);
									m_icm.text(actualValue, simpleType, m_currentItem, m_nodeIndex, m_atomBridge);

									if (null != m_downstream)
									{
										m_downstream.text(actualValue);
									}
								}
								catch (final DatatypeException e)
								{
									m_errors.error(new SimpleTypeException(initialValue, simpleType, e));
									if (null != m_downstream)
									{
										m_downstream.text(initialValue);
									}
								}
							}
							break;
							case ElementOnly:
							{
								if (!isWhiteSpace(initialValue))
								{
									m_errors.error(new CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException(m_currentPSVI.getName(), initialValue, m_currentItem.getLocation()));
								}
							}
							break;
							case Mixed:
							{
								if (null != declaration)
								{
									ValidationRules.checkValueConstraintForMixedContent(declaration, initialValue, m_currentItem, m_errors, m_atomBridge);
								}

								if (null != m_downstream)
								{
									m_downstream.text(initialValue);
								}
							}
							break;
							case Empty:
							{
								m_errors.error(new CvcUnexpectedTextInEmptyContentException(m_currentPSVI.getName(), initialValue, m_currentItem.getLocation()));
								if (null != m_downstream)
								{
									m_downstream.text(initialValue);
								}
							}
							break;
							default:
							{
								throw new AssertionError(contentType.getKind());
							}
						}
					}
					else
					{
						throw new AssertionError(elementType);
					}
				}
				else
				{
					if (null != m_downstream)
					{
						m_downstream.text(initialValue);
					}
				}
			}
			break;
			case Skip:
			{
				if (null != m_downstream)
				{
					m_downstream.text(initialValue);
				}
			}
			break;
			default:
			{
				throw new AssertionError(m_currentPSVI.getProcessContents());
			}
		}
	}

	public void reset()
	{
	    if (m_namespaces != null)
	        m_namespaces.reset();
	    if (m_attributes != null)
	        m_attributes.reset();
		m_nodeIndex = -1;
		if (m_icm != null)
		    m_icm.reset();
	}
	
	public void setComponentProvider(ComponentProvider provider)
	{
	    ValidationCache cache = new ValidationCache();
        m_attributes = new AttributeManager<A>(provider, m_atomBridge);
        m_currentPSVI = m_documentPSVI = new ModelPSVI(ProcessContentsMode.Strict, provider, cache);

        m_mac = new ModelAnalyzerImpl(provider, cache);
        if (m_errors != null)
            m_mac.setExceptionHandler(m_errors);
	}
	
	public void setExceptionHandler(final SchemaExceptionHandler handler)
	{
		m_errors = PreCondition.assertArgumentNotNull(handler, "handler");
		if (m_mac != null)
		    m_mac.setExceptionHandler(handler);
	}

	public void setOutputHandler(final VxOutputHandler<A> handler)
	{
		m_downstream = PreCondition.assertArgumentNotNull(handler, "handler");
	}

	public void startDocument(final URI documentURI) throws IOException
	{
		this.documentURI = documentURI;

		m_currentPSVI = m_documentPSVI;
		m_currentItem = m_documentItem;
		m_mac.startDocument();

		m_nodeIndex = 0; // The document node gets to be the zeroth index.

		m_idm.reset();

		if (null != m_downstream)
		{
			m_downstream.startDocument();
		}
	}

	public void startElement(final QName elementName, final LinkedList<VxMapping<String, String>> namespaces, final LinkedList<VxMapping<QName, String>> attributes) throws IOException, AbortException
	{
		m_text.setLength(0);

		final ValidationItem parentItem = m_currentItem;
		// TODO: Supply a location?
		m_currentItem = parentItem.push(++m_nodeIndex);

		// Maintain prefix mapping information.
		m_namespaces.pushContext();
		if (namespaces.size() > 0) // Optimization.
		{
			for (final VxMapping<String, String> mapping : namespaces)
			{
				m_namespaces.declarePrefix(mapping.getKey(), mapping.getValue());
			}
		}

		// Digest the attributes from the XMLSchema-instance namespace.
		m_attributes.initialize(elementName, m_currentItem, attributes, m_namespaces, documentURI, m_errors, sdl);
		final Type localType = m_attributes.getLocalType();
		final Boolean explicitNil = m_attributes.getLocalNil();

		m_currentPSVI = m_mac.startElement(elementName, localType, explicitNil);

		m_icm.startElement(m_currentPSVI, m_currentItem, m_errors);

		if (m_downstream != null)
		{
			m_downstream.startElement(elementName, m_currentPSVI.getType());

			for (final VxMapping<String, String> mapping : namespaces)
			{
				m_downstream.namespace(mapping.getKey(), mapping.getValue());
			}
		}

		// The attribute manager validates the attributes and sends them downstream, returning the index of the last
		// attribute.
		m_nodeIndex = m_attributes.attributes(m_currentPSVI, m_currentItem, attributes, m_downstream, m_errors, m_idm, m_icm);
	}

	public void text(final List<? extends A> initialValue) throws IOException, AbortException
	{
		m_nodeIndex++;

		// Remember that we got a text node.
		m_currentItem.m_detectedText = true;

		if (m_currentItem.getSuspendChecking())
		{
			if (null != m_downstream)
			{
				m_downstream.text(initialValue);
			}
			return;
		}

		switch (m_currentPSVI.getProcessContents())
		{
			case Strict:
			case Lax:
			{
				final ElementDefinition declaration = m_currentPSVI.getDeclaration();
				if (m_currentPSVI.isNilled())
				{
					m_errors.error(new CvcElementUnexpectedChildInNilledElementException(declaration, m_currentItem.getLocation()));
				}

				final Type elementType = m_currentPSVI.getType();
				if (null != elementType)
				{
					if (elementType instanceof SimpleType)
					{
						final SimpleType simpleType = (SimpleType)elementType;
						try
						{
							final List<A> actualValue = simpleType.validate(initialValue, m_atomBridge);

							if (null != declaration)
							{
								checkValueConstraintForElement(declaration, simpleType, actualValue);
							}

							m_idm.text(actualValue, simpleType, m_currentItem, m_errors, m_atomBridge);
							m_icm.text(actualValue, simpleType, m_currentItem, m_nodeIndex, m_atomBridge);

							if (null != m_downstream)
							{
								m_downstream.text(actualValue);
							}
						}
						catch (final DatatypeException e)
						{
							m_errors.error(new SimpleTypeException(m_atomBridge.getC14NString(initialValue), simpleType, e));
							if (null != m_downstream)
							{
								m_downstream.text(initialValue);
							}
						}
					}
					else
					{
						final ComplexType complexType = (ComplexType)elementType;
						final ContentType contentType = complexType.getContentType();
						switch (contentType.getKind())
						{
							case Simple:
							{
								final SimpleType simpleType = contentType.getSimpleType();
								try
								{
									final List<A> actualValue = simpleType.validate(initialValue, m_atomBridge);

									checkValueConstraintForElement(declaration, simpleType, actualValue);

									m_idm.text(actualValue, simpleType, m_currentItem, m_errors, m_atomBridge);
									m_icm.text(actualValue, simpleType, m_currentItem, m_nodeIndex, m_atomBridge);

									if (null != m_downstream)
									{
										m_downstream.text(actualValue);
									}
								}
								catch (final DatatypeException e)
								{
									m_errors.error(new SimpleTypeException(m_atomBridge.getC14NString(initialValue), simpleType, e));
									if (null != m_downstream)
									{
										m_downstream.text(initialValue);
									}
								}
							}
							break;
							case ElementOnly:
							{
								final String strval = m_atomBridge.getC14NString(initialValue);
								if (!isWhiteSpace(strval))
								{
									m_errors.error(new CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException(m_currentPSVI.getName(), strval, m_currentItem.getLocation()));
								}
							}
							break;
							case Mixed:
							{
								ValidationRules.checkValueConstraintForMixedContent(declaration, m_atomBridge.getC14NString(initialValue), m_currentItem, m_errors, m_atomBridge);

								if (null != m_downstream)
								{
									m_downstream.text(initialValue);
								}
							}
							break;
							case Empty:
							{
								m_errors.error(new CvcUnexpectedTextInEmptyContentException(m_currentPSVI.getName(), m_atomBridge.getC14NString(initialValue), m_currentItem.getLocation()));
								if (null != m_downstream)
								{
									m_downstream.text(initialValue);
								}
							}
							break;
							default:
							{
								throw new AssertionError(contentType.getKind());
							}
						}
					}
				}
				else
				{
					if (null != m_downstream)
					{
						m_downstream.text(initialValue);
					}
				}
			}
			break;
			case Skip:
			{
				if (null != m_downstream)
				{
					m_downstream.text(initialValue);
				}
			}
			break;
			default:
			{
				throw new AssertionError(m_currentPSVI.getProcessContents());
			}
		}
	}
    private final AtomBridge<A> m_atomBridge;
    private AttributeManager<A> m_attributes;
    private ValidationItem m_currentItem;
    private ModelPSVI m_currentPSVI;

    private final ValidationItem m_documentItem;
    private ModelPSVI m_documentPSVI;
    // Set by reset method. Preconditions guarantee that it is never null.
    private VxOutputHandler<A> m_downstream;
    // private Location m_location;
    private SchemaExceptionHandler m_errors = SmExceptionThrower.SINGLETON;
    private final VxSchemaDocumentLocationStrategy sdl;

    private final IdentityConstraintManager m_icm = new IdentityConstraintManager();

    private final IdManager m_idm = new IdManager();

    // Maintain state for each element.
    // private URI m_baseURI;
    private ModelAnalyzerImpl m_mac; // Model Analyzer Component
    private final ValidationPrefixResolver m_namespaces;

    // Index of node within document is used to determine node identity.
    private int m_nodeIndex = -1;

    // We must normalize character events into a single text node.
    private final StringBuilder m_text = new StringBuilder();
    private URI documentURI;

}
