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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.NodeKind;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcElementAbstractException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcElementDeclarationAndTypeException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcElementFixedAndNilledException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcElementInEmptyContentException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcElementInSimpleContentTypeException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcElementInSimpleTypeException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcElementLocalTypeDerivationException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcElementNotNillableException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcElementUnexpectedChildInNilledElementException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcSubstitutionBlockedByHeadDeclarationException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcSubstitutionBlockedByHeadTypeException;
import org.genxdm.processor.w3c.xs.exception.scc.SccComplexTypeBaseComplexDerivationException;
import org.genxdm.processor.w3c.xs.exception.scc.SccComplexTypeBaseSimpleDerivationException;
import org.genxdm.processor.w3c.xs.exception.scc.SccComplexTypeBaseUrTypeException;
import org.genxdm.processor.w3c.xs.exception.scc.SccComplexTypeDerivationHierarchyException;
import org.genxdm.processor.w3c.xs.exception.scc.SccComplexTypeDerivationMethodException;
import org.genxdm.processor.w3c.xs.exception.scc.SccSimpleTypeDerivationException;
import org.genxdm.processor.w3c.xs.exception.scc.SccSimpleTypeDerivationRestrictionException;
import org.genxdm.processor.w3c.xs.exception.sm.SmUnexpectedElementException;
import org.genxdm.processor.w3c.xs.exception.sm.SmUnexpectedEndException;
import org.genxdm.processor.w3c.xs.validation.api.VxPSVI;
import org.genxdm.processor.w3c.xs.xmlrep.SrcFrozenLocation;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.SchemaWildcard;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.constraints.ValueConstraint;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ProcessContentsMode;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.ComponentConstraintException;
import org.genxdm.xs.exceptions.SchemaException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.resolve.LocationInSchema;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ContentTypeKind;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;
import org.genxdm.xs.types.UnionSimpleType;


/**
 * Keeps track of state for the current element as they are pushed on and popped off the stack with startElement and
 * endElement. There is not a stack object per-se.. These are state objects that are linked together with parent and
 * child pointers and supply push and pop methods. Also note that the objects are recycled by the push method using the
 * child pointer.
 */
final class ModelPSVI implements VxPSVI, Locatable
{
	private ModelPSVI m_parentItem;

	public ModelPSVI getParent()
	{
		return m_parentItem;
	}

	private final ValidationCache cache;
	private final ComponentProvider metaBridge;

	private ModelPSVI m_childItem; // for recycling

	private final NodeKind m_nodeKind;

	// The name of the element information item.
	private QName m_elementName;

	public QName getName()
	{
		return m_elementName;
	}

	private int m_lineNumber;
	private int m_columnNumber;
	private int m_characterOffset;
	private String m_publicId;
	private String m_systemId;

	public LocationInSchema getLocation()
	{
		return new SrcFrozenLocation(m_lineNumber, m_columnNumber, m_characterOffset, m_publicId, m_systemId);
	}

	private Type m_type;
	private SmContentFiniteStateMachine m_machine;

	private ProcessContentsMode m_processContents;

	// The XML Schema specification does not specify what a validating processor should do after
	// it encounters an error. It is not obliged to report more than the first error.
	private boolean m_suspendChecking;

	private ElementDefinition m_elementDecl;

	// Did the instance use xsi:nil="true"?
	private boolean m_nilled;

	/**
	 * Identity scopes may exist for an element information item.
	 */
	public final ArrayList<IdentityScope> m_identityScopes = new ArrayList<IdentityScope>();
	public final HashMap<IdentityConstraint, IdentityScope> m_keyScopes = new HashMap<IdentityConstraint, IdentityScope>();

	private ModelPSVI(final ModelPSVI parent, final NodeKind nodeKind, final ProcessContentsMode processContents, final ComponentProvider metaBridge, final ValidationCache cache)
	{
		this.m_parentItem = parent;
		this.m_nodeKind = PreCondition.assertArgumentNotNull(nodeKind, "nodeKind");
		this.metaBridge = PreCondition.assertArgumentNotNull(metaBridge, "metaBridge");
		this.cache = PreCondition.assertArgumentNotNull(cache, "cache");
		reset(processContents);
	}

	public ModelPSVI(final ProcessContentsMode processContents, final ComponentProvider metaBridge, final ValidationCache cache)
	{
		this(null, NodeKind.DOCUMENT, processContents, metaBridge, cache);
	}

	public ModelPSVI push(final QName elementName)
	{
		if (m_childItem == null)
		{
			m_childItem = new ModelPSVI(this, NodeKind.ELEMENT, getProcessContents(), this.metaBridge, this.cache);
		}
		else
		{
			m_childItem.reset(getProcessContents());
		}
		m_childItem.m_elementName = PreCondition.assertArgumentNotNull(elementName, "elementName");

		// TODO: Need to get the element location.
		m_childItem.m_lineNumber = -1;
		m_childItem.m_columnNumber = -1;
		m_childItem.m_characterOffset = -1;
		m_childItem.m_publicId = null;
		m_childItem.m_systemId = null;

		return m_childItem;
	}

	public boolean declExists()
	{
		return (null != m_elementDecl);
	}

	/**
	 * Computes the {nilled} property for the element information item by checking the interaction of the xsi:nil and
	 * the {nillable} property of the element declaration.
	 */
	public boolean computeNilled(final Boolean explicitNil, final SchemaExceptionHandler errors) throws AbortException
	{
		if (null != explicitNil)
		{
			if (null != m_elementDecl)
			{
				if (m_elementDecl.isNillable())
				{
					if (explicitNil)
					{
						final ValueConstraint valueConstraint = m_elementDecl.getValueConstraint();
						if (null != valueConstraint && valueConstraint.getVariety().isFixed())
						{
							errors.error(new CvcElementFixedAndNilledException(m_elementDecl, getLocation()));
						}
					}
				}
				else
				{
					errors.error(new CvcElementNotNillableException(m_elementDecl, getLocation()));
				}
			}
			return explicitNil;
		}
		else
		{
			return false;
		}
	}

	public boolean step(final QName childName, final Locatable childLocatable, final SchemaExceptionHandler errors) throws AbortException
	{
		final Type elementType = getType();
		if (null == elementType)
		{
			// TODO: We should really see if locally valid wrt to type has been flagged.
			// Do nothing if there is no type annotation.
			return false;
		}

		if (elementType instanceof SimpleType)
		{
			// TODO: Do we include the xs:anySimpleType (simple ur-type)?
			errors.error(new CvcElementInSimpleTypeException(getName(), getLocation(), childName, childLocatable.getLocation()));
			return false;
		}
		else if (elementType instanceof ComplexType)
		{
			final ComplexType complexType = (ComplexType)elementType;
			if (m_suspendChecking)
			{
				return false;
			}

			switch (getProcessContents())
			{
				case Lax:
				{
					// Fall through
				}
				break;
				case Skip:
				{
					return false;
				}
				case Strict:
				{
					// Fall through
				}
				break;
				default:
				{
					throw new AssertionError(getProcessContents().name());
				}
			}
			if (m_nilled && (null != m_elementDecl))
			{
				errors.error(new CvcElementUnexpectedChildInNilledElementException(m_elementDecl, getLocation()));
				return false;
			}
			switch (complexType.getContentType().getKind())
			{
				case Empty:
				{
					errors.error(new CvcElementInEmptyContentException(getName(), getLocation(), childName, childLocatable.getLocation()));
					return false;
				}
				case Simple:
				{
					errors.error(new CvcElementInSimpleContentTypeException(getName(), getLocation(), childName, childLocatable.getLocation()));
					return false;
				}
				case ElementOnly:
				case Mixed:
				{
					switch (m_nodeKind)
					{
						case ELEMENT:
						{
							if (null != m_machine)
							{
								if (m_machine.step(childName))
								{
									return true;
								}
								else
								{
									errors.error(new SmUnexpectedElementException(getName(), getLocation(), childName, childLocatable.getLocation()));
									m_suspendChecking = true;
									return false;
								}
							}
							else
							{
								switch (getProcessContents())
								{
									case Strict:
									{
										errors.error(new SmUnexpectedElementException(getName(), getLocation(), childName, childLocatable.getLocation()));
										m_suspendChecking = true;
									}
									default:
									{
									}
								}
								return false;
							}
						}
						case DOCUMENT:
						{
							return false;
						}
						default:
						{
							throw new AssertionError(m_nodeKind);
						}
					}
				}
				default:
				{
					throw new AssertionError(complexType.getContentType().getKind());
				}
			}
		}
		else
		{
			throw new AssertionError(elementType);
		}
	}

	public void checkForUnexpectedEndOfContent(final SchemaExceptionHandler errors) throws AbortException
	{
		if (m_suspendChecking)
		{
			return;
		}

		switch (getProcessContents())
		{
			case Lax:
			{
				// Fall through
			}
			break;
			case Skip:
			{
				return;
			}
			case Strict:
			{
				// Fall through
			}
			break;
			default:
			{
				throw new AssertionError(getProcessContents().name());
			}
		}

		if (!m_nilled)
		{
			// We don't want an exception in startElement (causing a null machine) to be masked by a NPE.
			if (null != m_machine)
			{
				if (!m_machine.end())
				{
					errors.error(new SmUnexpectedEndException(getName(), getLocation()));
				}
			}
		}
	}

	public ModelPSVI pop()
	{
		return m_parentItem;
	}

	private void reset(final ProcessContentsMode processContents)
	{
		m_elementDecl = null;
		m_type = null;
		m_machine = null;

		setProcessContents(processContents);
		m_suspendChecking = (null != m_parentItem) && m_parentItem.m_suspendChecking;

		m_nilled = false;
	}

	public void annotate(final Type type)
	{
		m_type = PreCondition.assertArgumentNotNull(type);
		if (type instanceof ComplexType)
		{
			final ComplexType complexType = (ComplexType)type;
			final ContentTypeKind kind = complexType.getContentType().getKind();
			if (kind.isComplex())
			{
				m_machine = cache.getMachine(complexType);
			}
		}
	}

	public ElementDefinition getDeclaration()
	{
		return m_elementDecl;
	}

	public Type getType()
	{
		return m_type;
	}

	public boolean isNilled()
	{
		return m_nilled;
	}

	public void setNilled(boolean nilled)
	{
		m_nilled = nilled;
	}

	public ProcessContentsMode getProcessContents()
	{
		return m_processContents;
	}

	public void setProcessContents(final ProcessContentsMode processContents)
	{
		m_processContents = PreCondition.assertArgumentNotNull(processContents);
	}

	public boolean getSuspendChecking()
	{
		return m_suspendChecking;
	}

	public static  void assignPSVI(final ModelPSVI elementItem, final Type localType, final SchemaExceptionHandler errors) throws AbortException, SchemaException
	{
		final SmContentFiniteStateMachine machine = elementItem.m_parentItem.m_machine;
		if (null != machine)
		{
			if (machine.isElementMatch())
			{
				elementItem.m_elementDecl = machine.getElement();
				checkDeclNotAbstract(elementItem.m_elementDecl, elementItem, errors);
				final Type dynamicType;
				if (null != localType)
				{
					dynamicType = localType;
					checkLocalTypeValidlyDerivedFromElementType(elementItem.m_elementDecl, localType, elementItem);
				}
				else
				{
					dynamicType = elementItem.m_elementDecl.getType();
				}
				final ElementDefinition substitutionGroup = elementItem.m_elementDecl.getSubstitutionGroup();
				if (null != substitutionGroup)
				{
					checkDeclSubstitutionsNotBlocked(elementItem.m_elementDecl, dynamicType, errors, elementItem);
				}
				elementItem.annotate(dynamicType);
			}
			else
			{
				// It must be a wildcard match.
				final SchemaWildcard wildcard = machine.getWildcard();
				elementItem.setProcessContents(wildcard.getProcessContents());

				elementItem.recoverPSVI(localType, errors);
			}
		}
		else
		{
			// TODO: Why don't we annotate with the localType?
		}
	}

	public void recoverPSVI(final Type localType, final SchemaExceptionHandler errors) throws AbortException, SchemaException
	{
		final QName elementName = getName();

		switch (getProcessContents())
		{
			case Strict:
			{
				m_elementDecl = metaBridge.getElementDeclaration(elementName);
				if (null != m_elementDecl)
				{
					checkDeclNotAbstract(m_elementDecl, this, errors);
					if (null != localType)
					{
						checkLocalTypeValidlyDerivedFromElementType(m_elementDecl, localType, this);

						if (m_elementDecl.hasSubstitutionGroup())
						{
							checkDeclSubstitutionsNotBlocked(m_elementDecl, localType, errors, this);
						}
						annotate(localType);
					}
					else
					{
						final Type elementType = m_elementDecl.getType();

						if (m_elementDecl.hasSubstitutionGroup())
						{
							checkDeclSubstitutionsNotBlocked(m_elementDecl, elementType, errors, this);
						}
						annotate(elementType);
					}
				}
				else
				{
					if (null != localType)
					{
						annotate(localType);
					}
					else
					{
						errors.error(new CvcElementDeclarationAndTypeException(elementName, getLocation()));
					}
				}
			}
			break;
			case Lax:
			{
				m_elementDecl = metaBridge.getElementDeclaration(elementName);
				if (null != m_elementDecl)
				{
					checkDeclNotAbstract(m_elementDecl, this, errors);
					if (null != localType)
					{
						checkLocalTypeValidlyDerivedFromElementType(m_elementDecl, localType, this);

						if (m_elementDecl.hasSubstitutionGroup())
						{
							checkDeclSubstitutionsNotBlocked(m_elementDecl, localType, errors, this);
						}
						annotate(localType);
					}
					else
					{
						final Type elementType = m_elementDecl.getType();

						if (m_elementDecl.hasSubstitutionGroup())
						{
							checkDeclSubstitutionsNotBlocked(m_elementDecl, elementType, errors, this);
						}
						annotate(elementType);
					}
				}
				else
				{
					if (null != localType)
					{
						annotate(localType);
					}
				}
			}
			break;
			case Skip:
			{
			}
			break;
			default:
			{
				throw new AssertionError(getProcessContents());
			}
		}
	}

	private static void checkDeclNotAbstract(final ElementDefinition elementDeclaration, final Locatable locatable, final SchemaExceptionHandler errors) throws AbortException
	{
		// Check that the declaration is not abstract.
		if (elementDeclaration.isAbstract())
		{
			errors.error(new CvcElementAbstractException(elementDeclaration, locatable.getLocation()));
		}
	}

	private static void checkDeclSubstitutionsNotBlocked(final ElementDefinition elementDeclaration, final Type elementType, final SchemaExceptionHandler errors, final Locatable locatable) throws AbortException
	{
		// Note: Substitution can be blocked by extension and restriction as well.
		final ElementDefinition substitutionGroup = elementDeclaration.getSubstitutionGroup();
		final Set<DerivationMethod> block = substitutionGroup.getDisallowedSubtitutions();
		if (block.contains(DerivationMethod.Substitution))
		{
			// Substitutions are blocked outright by the substitution group declaration.
			errors.error(new CvcSubstitutionBlockedByHeadDeclarationException(elementDeclaration, substitutionGroup, locatable.getLocation()));
		}

		final Type headType = substitutionGroup.getType();

		if (block.contains(DerivationMethod.Extension))
		{
			if (elementType.derivedFromType(headType, EnumSet.of(DerivationMethod.Extension)))
			{
				errors.error(new CvcSubstitutionBlockedByHeadDeclarationException(elementDeclaration, substitutionGroup, locatable.getLocation()));
			}
		}

		if (block.contains(DerivationMethod.Restriction))
		{
			if (elementType.derivedFromType(headType, EnumSet.of(DerivationMethod.Restriction)))
			{
				errors.error(new CvcSubstitutionBlockedByHeadDeclarationException(elementDeclaration, substitutionGroup, locatable.getLocation()));
			}
		}

		if (headType instanceof ComplexType)
		{
			final ComplexType complexType = (ComplexType)headType;
			final Set<DerivationMethod> prohibitedSubstitutions = complexType.getProhibitedSubstitutions();
			if (prohibitedSubstitutions.contains(DerivationMethod.Substitution))
			{
				throw new AssertionError("Isn't this dead code?");
				// Substitutions are blocked outright by the substitution group type.
				// errors.error(new CvcSubstitutionBlockedByHeadTypeException(elementDeclaration,
				// locatable.getLocation()));
			}

			if (prohibitedSubstitutions.contains(DerivationMethod.Extension))
			{
				if (elementType.derivedFromType(headType, EnumSet.of(DerivationMethod.Extension)))
				{
					errors.error(new CvcSubstitutionBlockedByHeadTypeException(elementDeclaration, locatable.getLocation()));
				}
			}

			if (prohibitedSubstitutions.contains(DerivationMethod.Restriction))
			{
				if (elementType.derivedFromType(headType, EnumSet.of(DerivationMethod.Restriction)))
				{
					errors.error(new CvcSubstitutionBlockedByHeadTypeException(elementDeclaration, locatable.getLocation()));
				}
			}
		}
	}

	private static void checkLocalTypeValidlyDerivedFromElementType(final ElementDefinition elementDeclaration, final Type localType, final Locatable locatable) throws CvcElementLocalTypeDerivationException
	{
		final Set<DerivationMethod> block = elementDeclaration.getDisallowedSubtitutions();
		final Type elementType = elementDeclaration.getType();
		if (localType instanceof SimpleType)
		{
			try
			{
				checkTypeDerivationOKSimple((SimpleType)localType, elementType, block);
			}
			catch (final ComponentConstraintException e)
			{
				throw new CvcElementLocalTypeDerivationException(localType, elementDeclaration, e, locatable.getLocation());
			}
		}
		else
		{
			final Set<DerivationMethod> union = new HashSet<DerivationMethod>();

			union.addAll(block);
			if (elementType instanceof ComplexType)
			{
				final ComplexType complexType = (ComplexType)elementType;
				union.addAll(complexType.getProhibitedSubstitutions());
			}

			try
			{
				checkTypeDerivationOKComplex(localType, elementType, union);
			}
			catch (final ComponentConstraintException e)
			{
				throw new CvcElementLocalTypeDerivationException(localType, elementDeclaration, e, locatable.getLocation());
			}
		}
	}

	/**
	 * Type Derivation OK (Simple) (3.14.6)
	 */
	private static void checkTypeDerivationOKSimple(final SimpleType D, final Type B, final Set<DerivationMethod> subset) throws ComponentConstraintException
	{
		if (D.getName().equals(B.getName()))
		{
			// They are the same type definition.
		}
		else
		{
			final Type deesBaseType = D.getBaseType();
			if (subset.contains(DerivationMethod.Restriction) || deesBaseType.getFinal().contains(DerivationMethod.Restriction))
			{
				throw new SccSimpleTypeDerivationRestrictionException(D.getName());
			}

			boolean isOK = false;
			if (deesBaseType.getName().equals(B.getName()))
			{
				isOK = true;
			}
			else if (!deesBaseType.isComplexUrType() && ModelPSVI.isTypeDerivationOK(deesBaseType, B, subset))
			{
				isOK = true;
			}
			else if (!D.isSimpleUrType() && (D.isListType() || D.isUnionType()) && B.isSimpleUrType())
			{
				isOK = true;
			}
			else if (B instanceof UnionSimpleType)
			{
				final UnionSimpleType unionType = (UnionSimpleType)B;
				for (final SimpleType memberType : unionType.getMemberTypes())
				{
					if (isTypeDerivationOK(D, memberType, subset))
					{
						isOK = true;
						break;
					}
				}
			}
			if (!isOK)
			{
				throw new SccSimpleTypeDerivationException(D.getName());
			}
		}
	}

	private static <A, S> void checkTypeDerivationOKComplex(final Type D, final Type B, final Set<DerivationMethod> subset) throws ComponentConstraintException
	{
		if (D.getName().equals(B.getName()))
		{
			// They are the same type definition.
		}
		else if (D.isComplexUrType())
		{
			throw new SccComplexTypeDerivationHierarchyException(D, B, subset);
		}
		else
		{
			if (subset.contains(D.getDerivationMethod()))
			{
				throw new SccComplexTypeDerivationMethodException(D, B, subset);
			}

			final Type deeBaseType = D.getBaseType();
			if (deeBaseType.getName().equals(B.getName()))
			{
				// B is D's {base type definition}
			}
			else
			{
				if (deeBaseType.isComplexUrType())
				{
					throw new SccComplexTypeBaseUrTypeException(D, B, subset);
				}
				else
				{
					if (deeBaseType instanceof ComplexType)
					{
						try
						{
							checkTypeDerivationOK((ComplexType)deeBaseType, B, subset);
						}
						catch (final ComponentConstraintException e)
						{
							throw new SccComplexTypeBaseComplexDerivationException(D, B, subset, e);
						}
					}
					else
					{
						try
						{
							checkTypeDerivationOKSimple((SimpleType)deeBaseType, B, subset);
						}
						catch (final ComponentConstraintException e)
						{
							throw new SccComplexTypeBaseSimpleDerivationException(D, B, subset, e);
						}
					}
				}
			}
		}
	}

	private static void checkTypeDerivationOK(final Type D, final Type B, final Set<DerivationMethod> subset) throws ComponentConstraintException
	{
		if (D instanceof SimpleType)
		{
			checkTypeDerivationOKSimple((SimpleType)D, B, subset);
		}
		else if (D instanceof ComplexType)
		{
			checkTypeDerivationOKComplex(D, B, subset);
		}
		else
		{
			throw new AssertionError(D);
		}
	}

	private static boolean isTypeDerivationOK(final Type D, final Type B, final Set<DerivationMethod> subset)
	{
		try
		{
			checkTypeDerivationOK(D, B, subset);
		}
		catch (final SchemaException e)
		{
			return false;
		}
		return true;
	}
}
