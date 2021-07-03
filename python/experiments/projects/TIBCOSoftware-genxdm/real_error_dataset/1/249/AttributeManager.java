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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.PrefixResolver;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcAttributeFixedValueOverriddenException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcAttributeNormalizedValueException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcAttributeOnSimpleTypeException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcElementUnresolvedLocalTypeException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcMissingAttributeDeclarationException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcUnexpectedAttributeException;
import org.genxdm.processor.w3c.xs.exception.sm.SmAttributeUseException;
import org.genxdm.processor.w3c.xs.exception.sm.SmMissingAttributeException;
import org.genxdm.processor.w3c.xs.validation.api.VxMapping;
import org.genxdm.processor.w3c.xs.validation.api.VxOutputHandler;
import org.genxdm.processor.w3c.xs.validation.api.VxSchemaDocumentLocationStrategy;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.SchemaWildcard;
import org.genxdm.xs.constraints.AttributeUse;
import org.genxdm.xs.constraints.NamespaceConstraint;
import org.genxdm.xs.constraints.ValueConstraint;
import org.genxdm.xs.enums.ProcessContentsMode;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.exceptions.SimpleTypeException;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;


/**
 * Performs activities related to attribute validation.
 */
final class AttributeManager<A>
{
	private final AtomBridge<A> atomBridge;
	// Keep track of the attributes that we have seen in order to report missing attributes.
	private final Set<QName> m_attributes = new HashSet<QName>();
	private Boolean m_localNil = null;
	private Type m_localType = null;
	private final HashMap<QName, Pair<A, SimpleType>> m_xsiAtoms = new HashMap<QName, Pair<A, SimpleType>>();
	private final HashMap<QName, Pair<List<? extends A>, SimpleType>> m_xsiLists = new HashMap<QName, Pair<List<? extends A>, SimpleType>>();
	private final ComponentProvider metaBridge;
	private final String W3C_XML_SCHEMA_INSTANCE_NS_URI;
	private final String XSI_NIL;
	private final String XSI_NO_NAMESPACE_SCHEMA_LOCATION;
	private final String XSI_SCHEMA_LOCATION;
	private final String XSI_TYPE;

	AttributeManager(final ComponentProvider metaBridge, final AtomBridge<A> atomBridge)
	{
		this.metaBridge = PreCondition.assertArgumentNotNull(metaBridge, "metaBridge");
		this.atomBridge = PreCondition.assertArgumentNotNull(atomBridge, "atomBridge");
		this.W3C_XML_SCHEMA_INSTANCE_NS_URI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
		this.XSI_NIL = "nil";
		this.XSI_NO_NAMESPACE_SCHEMA_LOCATION = "noNamespaceSchemaLocation";
		this.XSI_SCHEMA_LOCATION = "schemaLocation";
		this.XSI_TYPE = "type";
	}

	private void attribute(final ModelPSVI elementPSVI, final ValidationItem elementItem, final QName attributeName, final int attributeIndex, final String initialValue, final VxOutputHandler<A> downstream, final SchemaExceptionHandler errors, final IdManager idm, final IdentityConstraintManager icm) throws IOException, AbortException
	{
		final Type elementType = elementPSVI.getType();
		if (null == elementType)
		{
			// Short-circuit if we could not assign a type.
			if (null != downstream)
			{
				downstream.attribute(attributeName, initialValue);
			}
		}
		else
		{
			if (elementType instanceof ComplexType)
			{
				final ComplexType complexType = (ComplexType)elementType;
				final AttributeUse attributeUse = complexType.getAttributeUses().get(attributeName);
				if (null != attributeUse)
				{
					final ValueConstraint valueConstraint = attributeUse.getEffectiveValueConstraint();
					final AttributeDefinition attribute = attributeUse.getAttribute();

					final Type attributeType = attribute.getType();
					if (attributeType instanceof SimpleType)
					{
						final SimpleType simpleType = (SimpleType)attributeType;
						final List<A> actualValue = validateAttributeWrtAttributeUse(elementItem, valueConstraint, attributeName, simpleType, attributeIndex, initialValue, idm, icm, errors, atomBridge);
						if (null != downstream)
						{
							downstream.attribute(attributeName, actualValue, simpleType);
						}
					}
					else
					{
						if (null != downstream)
						{
							downstream.attribute(attributeName, initialValue);
						}
					}
				}
				else
				{
					final SchemaWildcard attributeWildcard = complexType.getAttributeWildcard();
					if (null != attributeWildcard)
					{
						// If we end up here then the attribute was not recognized by {attribute uses}.
						final NamespaceConstraint wildcardNs = attributeWildcard.getNamespaceConstraint();
						if (wildcardNs == null)
						{
							errors.error(new CvcUnexpectedAttributeException(elementPSVI.getName(), attributeName, elementItem.getLocation()));
						}
						else
						{
							if (wildcardNs.allowsNamespaceName(attributeName.getNamespaceURI()))
							{
								final ProcessContentsMode processContents = attributeWildcard.getProcessContents();
								switch (processContents)
								{
									case Skip:
									{
										// Do nothing.
										if (null != downstream)
										{
											downstream.attribute(attributeName, initialValue);
										}
									}
									break;
									case Lax:
									{
										final AttributeDefinition attribute = metaBridge.getAttributeDeclaration(attributeName);
										if (null != attribute)
										{
											final Type attributeType = attribute.getType();
											if (attributeType instanceof SimpleType)
											{
												final SimpleType simpleType = (SimpleType)attributeType;
												final ValueConstraint valueConstraint = attribute.getValueConstraint();
												final List<A> actualValue = validateAttributeWrtDeclaration(elementItem, valueConstraint, attributeName, simpleType, initialValue, errors, atomBridge);

												idm.attribute(actualValue, simpleType, elementItem, errors, atomBridge);
												icm.attribute(actualValue, simpleType, elementItem, attributeName, attributeIndex, atomBridge);

												if (null != downstream)
												{
													downstream.attribute(attributeName, actualValue, simpleType);
												}
											}
											else
											{
												if (null != downstream)
												{
													downstream.attribute(attributeName, initialValue);
												}
											}
										}
										else
										{
											// In Lax mode it's not an error if a global attribute declaration cannot be
											// resolved.
											if (null != downstream)
											{
												downstream.attribute(attributeName, initialValue);
											}
										}
									}
									break;
									case Strict:
									{
										final AttributeDefinition attribute = metaBridge.getAttributeDeclaration(attributeName);
										if (null != attribute)
										{
											final Type attributeType = attribute.getType();
											if (attributeType instanceof SimpleType)
											{
												final SimpleType simpleType = (SimpleType)attributeType;
												final ValueConstraint valueConstraint = attribute.getValueConstraint();

												final List<A> actualValue = validateAttributeWrtDeclaration(elementItem, valueConstraint, attributeName, simpleType, initialValue, errors, atomBridge);

												idm.attribute(actualValue, simpleType, elementItem, errors, atomBridge);
												icm.attribute(actualValue, simpleType, elementItem, attributeName, attributeIndex, atomBridge);

												if (null != downstream)
												{
													downstream.attribute(attributeName, actualValue, simpleType);
												}
											}
											else
											{
												if (null != downstream)
												{
													downstream.attribute(attributeName, initialValue);
												}
											}
										}
										else
										{
											// In Strict mode it's an error if a global attribute declaration cannot be
											// resolved.
											errors.error(new CvcMissingAttributeDeclarationException(elementPSVI.getName(), attributeName, elementItem.getLocation()));
											if (null != downstream)
											{
												downstream.attribute(attributeName, initialValue);
											}
										}
									}
									break;
									default:
									{
										throw new AssertionError(processContents);
									}
								}
							}
							else
							{
								errors.error(new CvcUnexpectedAttributeException(elementPSVI.getName(), attributeName, elementItem.getLocation()));
							}
						}
					}
					else
					{
						errors.error(new CvcUnexpectedAttributeException(elementPSVI.getName(), attributeName, elementItem.getLocation()));
					}
				}
			}
			else if (elementType instanceof SimpleType)
			{
				final SimpleType simpleType = (SimpleType)elementType;
				errors.error(new CvcAttributeOnSimpleTypeException(elementPSVI.getName(), attributeName, simpleType, elementItem.getLocation()));
			}
			else
			{
				throw new AssertionError("Are we handling all cases?");
			}
		}
	}

	/**
	 * Check that all expected attributes are present and generate any that are specified as defaults.
	 * 
	 * @param errors
	 *            The exception handler for reporting a missing attribute or a datatype error.
	 * @param downstream
	 *            The handler for downstream events.
	 */
	private int checkForMissingAttributes(final ModelPSVI elementPSVI, final ValidationItem elementItem, final int lastIndex, final SchemaExceptionHandler errors, final VxOutputHandler<A> downstream, final IdManager idm, final IdentityConstraintManager icm) throws IOException, AbortException
	{
		int attributeIndex = lastIndex;
		final Type elementType = elementPSVI.getType();
		if (elementType instanceof ComplexType)
		{
			final ComplexType complexType = (ComplexType)elementType;
			for (final AttributeUse attributeUse : complexType.getAttributeUses().values())
			{
				final AttributeDefinition attribute = attributeUse.getAttribute();
				final QName attributeName = attribute.getName();
				if (!m_attributes.contains(attributeName))
				{

					final ValueConstraint valueConstraint = attributeUse.getEffectiveValueConstraint();
					if (null != valueConstraint)
					{
						switch (valueConstraint.getVariety())
						{
							case Default:
							{
								attributeIndex++;
								// Create attribute with the value - a nasty W3C XML Schema side effect!
								// TODO: I think we need to re-validate here to get the actual type?
								// TODO: This would then apply to the managers and the downstream.
								final Type attributeType = attribute.getType();
								final List<A> actualValue = valueConstraint.getValue(atomBridge);

								if (attributeType instanceof SimpleType)
								{
									final SimpleType simpleType = (SimpleType)attributeType;
									idm.attribute(actualValue, simpleType, elementItem, errors, atomBridge);
									icm.attribute(actualValue, simpleType, elementItem, attributeName, attributeIndex, atomBridge);

									if (null != downstream)
									{
										downstream.attribute(attributeName, actualValue, simpleType);
									}
								}
								else
								{
									if (null != downstream)
									{
										downstream.attribute(attributeName, atomBridge.getC14NString(actualValue));
									}
								}
							}
							break;
							case Fixed:
							{
								if (attributeUse.isRequired())
								{
									errors.error(new SmMissingAttributeException(elementPSVI.getName(), attributeName, elementItem.getLocation()));
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
						if (attributeUse.isRequired())
						{
							errors.error(new SmMissingAttributeException(elementPSVI.getName(), attributeName, elementItem.getLocation()));
						}
					}
				}
			}
		}
		return attributeIndex;
	}

	private void checkValueAgainstValueConstraint(final ValueConstraint valueConstraint, final QName attributeName, final List<? extends A> actualValue, final Locatable locatable, final SchemaExceptionHandler errors, final AtomBridge<A> atomBridge) throws AbortException
	{
		if (null != valueConstraint)
		{
			switch (valueConstraint.getVariety())
			{
				case Fixed:
				{
					final List<A> fixed = valueConstraint.getValue(atomBridge);

					if (!ValidationSupport.equalValues(fixed, actualValue))
					{
						final String fixedC14N = atomBridge.getC14NString(fixed);
						final String actualC14N = atomBridge.getC14NString(actualValue);
						errors.error(new CvcAttributeFixedValueOverriddenException(attributeName, fixedC14N, actualC14N, locatable.getLocation()));
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
					throw new RuntimeException(valueConstraint.getVariety().name());
				}
			}
		}
	}

	public Boolean getLocalNil()
	{
		return m_localNil;
	}

	public Type getLocalType()
	{
		return m_localType;
	}

	/**
	 * Initialize this manager by examining those attributes in the W3C XML Schema-instance namespace.
	 * 
	 * @param elementName
	 *            The element information item name.
	 * @param attributes
	 *            The list of attributes.
	 * @param errors
	 *            The handler for exceptions.
	 * @param p2n
	 *            A prefix resolver.
	 */
	public void initialize(final QName elementName, final Locatable locatable, final LinkedList<VxMapping<QName, String>> attributes, final PrefixResolver p2n, final URI baseURI, final SchemaExceptionHandler errors, final VxSchemaDocumentLocationStrategy schemaDocumentLocationStrategy) throws IOException, AbortException
	{
		reset();

		if (attributes.size() > 0) // Optimization.
		{
			for (final VxMapping<QName, String> mapping : attributes)
			{
				final QName attributeName = mapping.getKey();
				final String data = mapping.getValue();

				if (W3C_XML_SCHEMA_INSTANCE_NS_URI == attributeName.getNamespaceURI())
				{
					final String localName = attributeName.getLocalPart();
					if (XSI_TYPE == localName)
					{
						final AttributeDefinition attribute = metaBridge.getAttributeDeclaration(attributeName);
						final SimpleType attributeType = (SimpleType)attribute.getType();
						try
						{
							final A actualValue = attributeType.validate(data, atomBridge).get(0);
							final QName typeName = resolveXsiType(atomBridge.getLocalNameFromQName(actualValue), atomBridge.getPrefixFromQName(actualValue), p2n);
							m_localType = metaBridge.getTypeDefinition(typeName);
							if (null != m_localType)
							{
								m_xsiAtoms.put(attributeName, new Pair<A, SimpleType>(actualValue, attributeType));
							}
							else
							{
								errors.error(new CvcElementUnresolvedLocalTypeException(typeName, elementName, locatable.getLocation()));
							}
						}
						catch (final DatatypeException dte)
						{
							final SimpleTypeException ste = new SimpleTypeException(data, attributeType, dte);
							errors.error(new SmAttributeUseException(elementName, attributeName, locatable.getLocation(), ste));
						}
					}
					else if (XSI_NIL == localName)
					{
						final AttributeDefinition attribute = metaBridge.getAttributeDeclaration(attributeName);
						final SimpleType attributeType = (SimpleType)attribute.getType();
						try
						{
							final A actualValue = attributeType.validate(data, atomBridge).get(0);
							m_localNil = atomBridge.getBoolean(actualValue);
							m_xsiAtoms.put(attributeName, new Pair<A, SimpleType>(actualValue, attributeType));
						}
						catch (final DatatypeException dte)
						{
							final SimpleTypeException ste = new SimpleTypeException(data, attributeType, dte);
							errors.error(new SmAttributeUseException(elementName, attributeName, locatable.getLocation(), ste));
						}
					}
					else if (XSI_SCHEMA_LOCATION == localName)
					{
						final AttributeDefinition attribute = metaBridge.getAttributeDeclaration(attributeName);
						final SimpleType attributeType = (SimpleType)attribute.getType();
						try
						{
							final List<A> actualValue = attributeType.validate(data, atomBridge);
							if (null != schemaDocumentLocationStrategy)
							{
								final Iterator<A> atoms = actualValue.iterator();
								while (atoms.hasNext())
								{
									final URI namespace = atomBridge.getURI(atoms.next());
									if (atoms.hasNext())
									{
										final URI schemaLocation = atomBridge.getURI(atoms.next());
										try
										{
											schemaDocumentLocationStrategy.schemaLocation(baseURI, namespace, schemaLocation);
										}
										catch (final Exception e)
										{
											// Ignore
										}
									}
								}
							}
							m_xsiLists.put(attributeName, new Pair<List<? extends A>, SimpleType>(actualValue, attributeType));
						}
						catch (final DatatypeException dte)
						{
							final SimpleTypeException ste = new SimpleTypeException(data, attributeType, dte);
							errors.error(new SmAttributeUseException(elementName, attributeName, locatable.getLocation(), ste));
						}
					}
					else if (XSI_NO_NAMESPACE_SCHEMA_LOCATION == localName)
					{
						final AttributeDefinition attribute = metaBridge.getAttributeDeclaration(attributeName);
						final SimpleType attributeType = (SimpleType)attribute.getType();
						try
						{
							final A actualValue = attributeType.validate(data, atomBridge).get(0);
							if (null != schemaDocumentLocationStrategy)
							{
								final URI schemaLocation = atomBridge.getURI(actualValue);
								schemaDocumentLocationStrategy.noNamespaceSchemaLocation(baseURI, schemaLocation);
							}
							m_xsiAtoms.put(attributeName, new Pair<A, SimpleType>(actualValue, attributeType));
						}
						catch (final DatatypeException dte)
						{
							final SimpleTypeException ste = new SimpleTypeException(data, attributeType, dte);
							errors.error(new SmAttributeUseException(elementName, attributeName, locatable.getLocation(), ste));
						}
					}
					else
					{
						// Ignore the attribute, it gets handled along with all the others
					}
				}
			}
		}
	}

	public void reset()
	{
		m_attributes.clear();
		m_xsiAtoms.clear();
		m_xsiLists.clear();
		m_localType = null;
		m_localNil = null;
	}

	private QName resolveXsiType(final String localName, final String prefix, final PrefixResolver p2n)
	{
		if (prefix.length() > 0)
		{
			final String namespaceURI = p2n.getNamespace(prefix);
			if (null != namespaceURI)
			{
				return new QName(namespaceURI, localName, prefix);
			}
			else
			{
				throw new AssertionError("Unable to resolve prefix: '" + prefix + "'");
			}
		}
		else
		{
			final String namespaceURI = p2n.getNamespace(prefix);
			if (null != namespaceURI)
			{
				return new QName(namespaceURI, localName, prefix);
			}
			else
			{
				return new QName("", localName, prefix);
			}
		}
	}

	/**
	 * Validates the collection of attributes for the element information item, augmented using defaults, and sent
	 * downstream.
	 * 
	 * @param elementItem
	 *            The element information item.
	 * @param attributes
	 *            The list of attributes.
	 * @param downstream
	 *            The handler for downstream events.
	 * @param errors
	 *            The handler for exceptions.
	 * @param icm
	 *            The identity constraint manager.
	 * @param p2n
	 *            A prefix resolver.
	 * 
	 * @return The index of the last attribute.
	 */
	public int attributes(final ModelPSVI elementPSVI, final ValidationItem elementItem, final List<VxMapping<QName, String>> attributes, final VxOutputHandler<A> downstream, final SchemaExceptionHandler errors, final IdManager idm, final IdentityConstraintManager icm) throws IOException, AbortException
	{
		int attributeIndex = elementItem.getElementIndex();
		for (final VxMapping<QName, String> mapping : attributes)
		{
			attributeIndex++;
			final QName attributeName = mapping.getKey();
			if (m_xsiAtoms.containsKey(attributeName))
			{
				if (null != downstream)
				{
					downstream.attribute(attributeName, atomBridge.wrapAtom(m_xsiAtoms.get(attributeName).getFirst()), m_xsiAtoms.get(attributeName).getSecond());
				}
			}
			else if (m_xsiLists.containsKey(attributeName))
			{
				if (null != downstream)
				{
					downstream.attribute(attributeName, m_xsiLists.get(attributeName).getFirst(), m_xsiLists.get(attributeName).getSecond());
				}
			}
			else
			{
				// Make a note that we have seen this attribute.
				m_attributes.add(attributeName);
				attribute(elementPSVI, elementItem, attributeName, attributeIndex, mapping.getValue(), downstream, errors, idm, icm);
			}
		}
		return checkForMissingAttributes(elementPSVI, elementItem, attributeIndex, errors, downstream, idm, icm);
	}

	private List<A> validateAttributeWrtAttributeUse(final ValidationItem elementItem, final ValueConstraint valueConstraint, final QName attributeName, final SimpleType attributeType, final int attributeIndex, final String initialValue, final IdManager idm, final IdentityConstraintManager icm, final SchemaExceptionHandler errors, final AtomBridge<A> atomBridge) throws AbortException
	{
		final List<A> actualValue = validateAttributeWrtDeclaration(elementItem, valueConstraint, attributeName, attributeType, initialValue, errors, atomBridge);

		idm.attribute(actualValue, attributeType, elementItem, errors, atomBridge);
		icm.attribute(actualValue, attributeType, elementItem, attributeName, attributeIndex, atomBridge);

		checkValueAgainstValueConstraint(valueConstraint, attributeName, actualValue, elementItem, errors, atomBridge);

		return actualValue;
	}

	private List<A> validateAttributeWrtDeclaration(final Locatable location, final ValueConstraint valueConstraint, final QName attributeName, final SimpleType attributeType, final String initialValue, final SchemaExceptionHandler errors, final AtomBridge<A> atomBridge) throws AbortException
	{
		final List<A> actualValue = validateWrtType(attributeName, attributeType, initialValue, location, errors, atomBridge);

		checkValueAgainstValueConstraint(valueConstraint, attributeName, actualValue, location, errors, atomBridge);

		return actualValue;
	}

	private List<A> validateWrtType(final QName attributeName, final SimpleType attributeType, final String initialValue, final Locatable locatable, final SchemaExceptionHandler errors, final AtomBridge<A> atomBridge) throws AbortException
	{
		PreCondition.assertArgumentNotNull(attributeName, "attributeName");
		PreCondition.assertArgumentNotNull(attributeType);
		try
		{
			return attributeType.validate(initialValue, atomBridge);
		}
		catch (final DatatypeException dte)
		{
			final SimpleTypeException ste = new SimpleTypeException(initialValue, attributeType, dte);
			errors.error(new CvcAttributeNormalizedValueException(attributeName, initialValue, locatable.getLocation(), ste));
			return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(initialValue));
		}
	}
}
