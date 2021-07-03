/*
QName * Copyright (c) 2009-2010 TIBCO Software Inc.
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
package org.genxdm.processor.w3c.xs.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.misc.Pair;
import org.genxdm.bridgekit.xs.ComponentBagImpl;
import org.genxdm.bridgekit.xs.ForeignAttributesSink;
import org.genxdm.bridgekit.xs.complex.AttributeDeclTypeImpl;
import org.genxdm.bridgekit.xs.complex.AttributeGroupImpl;
import org.genxdm.bridgekit.xs.complex.ComplexTypeImpl;
import org.genxdm.bridgekit.xs.complex.ContentTypeImpl;
import org.genxdm.bridgekit.xs.complex.ElementDeclTypeImpl;
import org.genxdm.bridgekit.xs.complex.ListTypeImpl;
import org.genxdm.bridgekit.xs.complex.ModelGroupImpl;
import org.genxdm.bridgekit.xs.complex.ParticleWithElementTerm;
import org.genxdm.bridgekit.xs.complex.ParticleWithModelGroupTerm;
import org.genxdm.bridgekit.xs.complex.ParticleWithWildcardTerm;
import org.genxdm.bridgekit.xs.complex.UnionTypeImpl;
import org.genxdm.bridgekit.xs.complex.WildcardImpl;
import org.genxdm.bridgekit.xs.constraint.AttributeUseImpl;
import org.genxdm.bridgekit.xs.constraint.FacetEnumerationImpl;
import org.genxdm.bridgekit.xs.constraint.FacetFractionDigitsImpl;
import org.genxdm.bridgekit.xs.constraint.FacetImpl;
import org.genxdm.bridgekit.xs.constraint.FacetLengthImpl;
import org.genxdm.bridgekit.xs.constraint.FacetMaxLengthImpl;
import org.genxdm.bridgekit.xs.constraint.FacetMinLengthImpl;
import org.genxdm.bridgekit.xs.constraint.FacetPatternImpl;
import org.genxdm.bridgekit.xs.constraint.FacetTotalDigitsImpl;
import org.genxdm.bridgekit.xs.constraint.FacetValueCompImpl;
import org.genxdm.bridgekit.xs.constraint.IdentityConstraintImpl;
import org.genxdm.bridgekit.xs.simple.AtomicTypeImpl;
import org.genxdm.bridgekit.xs.simple.NotationImpl;
import org.genxdm.bridgekit.xs.simple.SimpleTypeImpl;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.PrefixResolver;
import org.genxdm.processor.w3c.xs.exception.SicOversizedIntegerException;
import org.genxdm.processor.w3c.xs.exception.scc.SccAttributeDeclarationSimpleTypeException;
import org.genxdm.processor.w3c.xs.exception.scc.SccAttributeGroupMemberNamesException;
import org.genxdm.processor.w3c.xs.exception.scc.SccBaseTypeMustBeSimpleTypeException;
import org.genxdm.processor.w3c.xs.exception.scc.SccCyclicAttributeException;
import org.genxdm.processor.w3c.xs.exception.scc.SccCyclicAttributeGroupException;
import org.genxdm.processor.w3c.xs.exception.scc.SccCyclicElementException;
import org.genxdm.processor.w3c.xs.exception.scc.SccCyclicIdentityConstraintException;
import org.genxdm.processor.w3c.xs.exception.scc.SccCyclicModelGroupException;
import org.genxdm.processor.w3c.xs.exception.scc.SccItemTypeMustBeAtomicOrUnionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMemberTypeMustBeAtomicOrListException;
import org.genxdm.processor.w3c.xs.exception.sm.SmAttributeUseException;
import org.genxdm.processor.w3c.xs.exception.sm.SmCyclicTypeException;
import org.genxdm.processor.w3c.xs.exception.sm.SmUndeclaredReferenceException;
import org.genxdm.processor.w3c.xs.exception.src.SrcBaseContentTypeCannotBeSimpleException;
import org.genxdm.processor.w3c.xs.exception.src.SrcBaseMustHaveSimpleOrMixedContentTypeComplexTypeException;
import org.genxdm.processor.w3c.xs.exception.src.SrcBaseTypeMustBeComplexTypeException;
import org.genxdm.processor.w3c.xs.exception.src.SrcSimpleTypeAmongChildrenOfRestrictionException;
import org.genxdm.processor.w3c.xs.xmlrep.XMLAttributeUse;
import org.genxdm.processor.w3c.xs.xmlrep.XMLRepresentation;
import org.genxdm.processor.w3c.xs.xmlrep.XMLSchemaCache;
import org.genxdm.processor.w3c.xs.xmlrep.XMLScope;
import org.genxdm.processor.w3c.xs.xmlrep.XMLTypeRef;
import org.genxdm.processor.w3c.xs.xmlrep.XMLValueConstraint;
import org.genxdm.processor.w3c.xs.xmlrep.components.XMLAttribute;
import org.genxdm.processor.w3c.xs.xmlrep.components.XMLAttributeGroup;
import org.genxdm.processor.w3c.xs.xmlrep.components.XMLElement;
import org.genxdm.processor.w3c.xs.xmlrep.components.XMLIdentityConstraint;
import org.genxdm.processor.w3c.xs.xmlrep.components.XMLModelGroup;
import org.genxdm.processor.w3c.xs.xmlrep.components.XMLNotation;
import org.genxdm.processor.w3c.xs.xmlrep.components.XMLType;
import org.genxdm.processor.w3c.xs.xmlrep.facets.XMLEnumeration;
import org.genxdm.processor.w3c.xs.xmlrep.facets.XMLFractionDigitsFacet;
import org.genxdm.processor.w3c.xs.xmlrep.facets.XMLLength;
import org.genxdm.processor.w3c.xs.xmlrep.facets.XMLMinMaxFacet;
import org.genxdm.processor.w3c.xs.xmlrep.facets.XMLPatternFacet;
import org.genxdm.processor.w3c.xs.xmlrep.facets.XMLTotalDigitsFacet;
import org.genxdm.processor.w3c.xs.xmlrep.particles.XMLParticle;
import org.genxdm.processor.w3c.xs.xmlrep.particles.XMLParticleWithElementTerm;
import org.genxdm.processor.w3c.xs.xmlrep.particles.XMLParticleWithModelGroupTerm;
import org.genxdm.processor.w3c.xs.xmlrep.particles.XMLParticleWithWildcardTerm;
import org.genxdm.processor.w3c.xs.xmlrep.particles.XMLWildcard;
import org.genxdm.processor.w3c.xs.xmlrep.util.FAMap;
import org.genxdm.processor.w3c.xs.xmlrep.util.SrcFrozenLocation;
import org.genxdm.processor.w3c.xs.xmlrep.util.XMLComponentLocator;
import org.genxdm.processor.w3c.xs.xmlrep.util.XMLCycles;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.EnumerationDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.components.SchemaWildcard;
import org.genxdm.xs.constraints.AttributeUse;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.constraints.ModelGroupUse;
import org.genxdm.xs.constraints.NamespaceConstraint;
import org.genxdm.xs.constraints.ValueConstraint;
import org.genxdm.xs.constraints.WildcardUse;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ProcessContentsMode;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.exceptions.SchemaException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.exceptions.SchemaRegExCompileException;
import org.genxdm.xs.exceptions.SimpleTypeException;
import org.genxdm.xs.facets.Facet;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.Limit;
import org.genxdm.xs.facets.Pattern;
import org.genxdm.xs.facets.RegExPattern;
import org.genxdm.xs.facets.SchemaRegExCompiler;
import org.genxdm.xs.types.AtomicType;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ComplexUrType;
import org.genxdm.xs.types.ContentType;
import org.genxdm.xs.types.ListSimpleType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.SimpleUrType;
import org.genxdm.xs.types.Type;
import org.genxdm.xs.types.UnionSimpleType;


/**
 * The main purpose of this conversion class is to build the schema from the cache. <br/>
 * Note: Using a dedicated class allows us to make the plumbing instance members so that we don't have to pass
 * distracting arguments to methods. The use of a static entry point and a private initializer protects against multiple
 * invocations.
 */
public final class XMLSchemaConverter
{
    private XMLSchemaConverter(final SchemaRegExCompiler regexc, final ComponentProvider outCache, final XMLSchemaCache inCache, final ComponentBagImpl schema, final XMLComponentLocator locations, final SchemaExceptionHandler errors, final boolean lastInWins)
    {
        this.regexc = regexc;
        this.m_existingCache = outCache;
        this.m_atoms = new CanonicalAtomBridge(outCache);
        this.m_inCache = inCache;
        this.m_outBag = schema;
        this.m_locations = locations;
        this.m_errors = errors;
        this.m_cycles = new XMLCycles();
        this.m_lastInWins = lastInWins;
    }

    private SchemaWildcard attributeWildcard(final Type baseType)
    {
        if (baseType instanceof ComplexType)
        {
            final ComplexType complexBase = (ComplexType)baseType;
            final SchemaWildcard attributeWildcard = complexBase.getAttributeWildcard();
            if (null != attributeWildcard)
            {
                return attributeWildcard;
            }
            else
            {
                return null;
            }
        }
        else if (baseType instanceof SimpleType)
        {
            return null;
        }
        else
        {
            throw new AssertionError(baseType);
        }
    }

    private SchemaWildcard attributeWildcard(final XMLType complexType) throws AbortException, SchemaException
    {
        final XMLWildcard localWildcard = complexType.attributeWildcard;

        final DerivationMethod derivation = complexType.getDerivationMethod();
        switch (derivation)
        {
            case Restriction:
            {
                return completeWildcard(complexType.getAttributeGroups(), localWildcard);
            }
            case Extension:
            {
                final SchemaWildcard baseWildcard = attributeWildcard(complexType.getBaseRef());
                if (null != baseWildcard)
                {
                    final SchemaWildcard completeWildcard = completeWildcard(complexType.getAttributeGroups(), localWildcard);
                    if (null == completeWildcard)
                    {
                        return baseWildcard;
                    }
                    else
                    {
                        // {process contents} and {annotation} from complete
                        // wildcard.
                        // {namespace constraint} is union of the complete and
                        // base wildcard.
                        return new WildcardImpl(completeWildcard.getProcessContents(), completeWildcard.getNamespaceConstraint().union(baseWildcard.getNamespaceConstraint()));
                    }
                }
                else
                {
                    return completeWildcard(complexType.getAttributeGroups(), localWildcard);
                }
            }
            default:
            {
                // Complex type must be derived by restriction or extension.
                throw new AssertionError(derivation);
            }
        }
    }

    private SchemaWildcard attributeWildcard(final XMLTypeRef typeRef) throws AbortException, SchemaException
    {
        final Type type = convertType(typeRef);
        return attributeWildcard(type);
    }

    private SchemaWildcard completeWildcard(final Iterable<XMLAttributeGroup> attributeGroups, final XMLWildcard localWildcard) throws AbortException, SchemaException
    {
        NamespaceConstraint constraint = null;

        // Remember the first {process contents} within the
        // <attributeGroup>[children].
        ProcessContentsMode processContents = null;
        if (null != attributeGroups)
        {
            for (final XMLAttributeGroup xmlAttributeGroup : attributeGroups)
            {
                final AttributeGroupDefinition attributeGroup = convertAttributeGroup(xmlAttributeGroup);
                final SchemaWildcard groupWildcard = attributeGroup.getWildcard();
                if (null != groupWildcard)
                {
                    if (null == constraint)
                    {
                        constraint = groupWildcard.getNamespaceConstraint();
                        processContents = groupWildcard.getProcessContents();
                    }
                    else
                    {
                        constraint = constraint.intersection(groupWildcard.getNamespaceConstraint());
                    }
                }
            }
        }

        if (null == constraint)
        {
            // If nothing is found in the <attributeGroup>[children]...
            if (null != localWildcard)
            {
                return new WildcardImpl(localWildcard.getProcessContents(), convert(localWildcard.getNamespaceConstraint()));
            }
            else
            {
                return null;
            }
        }
        else
        {
            if (null != localWildcard)
            {
                // {process contents} and {annotation} are those of the local
                // wildcard.
                // {namespace constraint} defined by Attribute Wildcard
                // Intersection.
                return new WildcardImpl(localWildcard.getProcessContents(), convert(localWildcard.getNamespaceConstraint()).intersection(constraint));
            }
            else
            {
                // {process contents} from first <attributeGroup>[children]
                // {namespace constraint} from the <attributeGroup>[children]
                // {annotation} is absent.
                return new WildcardImpl(processContents, constraint);
            }
        }
    }

    /**
     * Expand temporary variables used to hold syntactic constructs for attribute uses and wildcards.
     */
    private Map<QName, AttributeUse> computeAttributeUses(final XMLType complexType, final Map<QName, AttributeUse> attributeUses) throws AbortException, SchemaException
    {
        for (final XMLAttributeUse attributeUse : complexType.getAttributeUses())
        {
            final QName attributeName = attributeUse.getDeclaration().getName();
            try
            {
                if (!attributeUses.containsKey(attributeName))
                {
                    attributeUses.put(attributeName, convertAttributeUse(attributeUse));
                }
                else
                {
                    m_errors.error(new SccAttributeGroupMemberNamesException());
                }
            }
            catch (final SchemaException e)
            {
                m_errors.error(e);
            }
        }

        for (final XMLAttributeGroup xmlAttributeGroup : complexType.getAttributeGroups())
        {
            final AttributeGroupDefinition attributeGroup = convertAttributeGroup(xmlAttributeGroup);
            if (attributeGroup.hasAttributeUses())
            {
                for (final AttributeUse attributeUse : attributeGroup.getAttributeUses())
                {
                    final AttributeDefinition attribute = attributeUse.getAttribute();
                    final QName attributeName = attribute.getName();
                    if (!attributeUses.containsKey(attributeName))
                    {
                        attributeUses.put(attributeName, attributeUse);
                    }
                    else
                    {
                        m_errors.error(new SccAttributeGroupMemberNamesException());
                    }
                }
            }
        }

        switch (complexType.getDerivationMethod())
        {
            case Restriction:
            {
                final Type typeB = convertType(complexType.getBaseRef());
                if (typeB instanceof ComplexType)
                {
                    final ComplexType complexTypeB = (ComplexType)typeB;
                    for (final AttributeUse attributeUse : complexTypeB.getAttributeUses().values())
                    {
                        final QName attributeName = attributeUse.getAttribute().getName();
                        if (!complexType.prohibited.contains(attributeName))
                        {
                            if (attributeUses.containsKey(attributeName))
                            {
                                // Obviously can't add it because that would
                                // cause a non-unique name.
                                // This collision will be analyzed during
                                // schema constraint checking.
                            }
                            else
                            {
                                attributeUses.put(attributeName, attributeUse);
                            }
                        }
                    }
                }
            }
            break;
            case Extension:
            {
                final Type typeB = convertType(complexType.getBaseRef());
                if (typeB instanceof ComplexType)
                {
                    final ComplexType complexTypeB = (ComplexType)typeB;
                    for (final AttributeUse attributeUse : complexTypeB.getAttributeUses().values())
                    {
                        final QName attributeName = attributeUse.getAttribute().getName();
                        attributeUses.put(attributeName, attributeUse);
                    }
                }
            }
            break;
            default:
            {
                throw new RuntimeException(complexType.getDerivationMethod().name());
            }
        }

        return attributeUses;
    }

    /**
     * Compile the enumeration facets for this type. <br/>
     * Enumeration facets are not inherited during compilation, but must be subsets of base types.
     */
    private void computeEnumerations(final SimpleType baseType, final XMLType type, final SimpleTypeImpl target) throws AbortException
    {
        if (type.getEnumerations().size() > 0)
        {
            for (final XMLEnumeration pattern : type.getEnumerations())
            {
                try
                {
                    target.addEnumeration(enumeration(target, baseType, pattern));
                }
                catch (final SmAttributeUseException e)
                {
                    m_errors.error(e);
                }
            }
        }
    }

    private void computeFacets(final SimpleType baseType, final XMLType type, final SimpleTypeImpl target) throws AbortException, SchemaException
    {
        for (final XMLTotalDigitsFacet xmlFacet : type.getTotalDigitsFacets())
        {
            target.addFacet(totalDigits(xmlFacet));
        }
        for (final XMLFractionDigitsFacet xmlFacet : type.getFractionDigitsFacets())
        {
            target.addFacet(fractionDigits(xmlFacet));
        }
        // Note that the length, minLength and maxLength facets are deprecated
        // for types derived from QName or NOTATION.
        if (!subtype(target, m_existingCache.getAtomicType(NativeType.QNAME)) && !subtype(target, m_existingCache.getAtomicType(NativeType.NOTATION)))
        {
            for (final XMLLength xmlFacet : type.getLengthFacets())
            {
                target.addFacet(length(xmlFacet));
            }
        }
        for (final XMLMinMaxFacet xmlFacet : type.getMinMaxFacets())
        {
            if (baseType.isAtomicType())
            {
                try
                {
                    target.addFacet(minmax(xmlFacet, (SimpleType)baseType));
                }
                catch (final SchemaException e)
                {
                    m_errors.error(e);
                }
            }
        }
    }

    private ContentType computeLocallyEmptyContent(final XMLType complexType) throws SchemaException, AbortException
    {
        final DerivationMethod derivation = complexType.getDerivationMethod();
        switch (derivation)
        {
            case Restriction:
            {
                return EMPTY_CONTENT;
            }
            case Extension:
            {
                final Type baseType = convertType(complexType.getBaseRef());
                if (baseType instanceof ComplexType)
                {
                    final ComplexType complexBase = (ComplexType)baseType;
                    return complexBase.getContentType();
                }
                else if (baseType instanceof SimpleType)
                {
                    final SimpleType simpleBase = (SimpleType)baseType;
                    return new ContentTypeImpl(simpleBase);
                }
                else
                {
                    throw new AssertionError(derivation);
                }
            }
            default:
            {
                throw new AssertionError(derivation);
            }
        }
    }

    /**
     * Compile the pattern facets for this type. <br/>
     * Pattern facets are not inherited during compilation.
     */
    private void computePatterns(final LinkedList<XMLPatternFacet> xmlFacets, final SimpleTypeImpl target) throws AbortException
    {
        if (xmlFacets.size() > 0)
        {
            for (final XMLPatternFacet pattern : xmlFacets)
            {
                try
                {
                    target.addPattern(pattern(pattern));
                }
                catch (final SmAttributeUseException e)
                {
                    m_errors.error(e);
                }
            }
        }
    }

    private Set<String> convert(final Iterable<String> strings)
    {
        final Set<String> result = new HashSet<String>();
        for (final String member : strings)
        {
            result.add(member);
        }
        return result;
    }

    private NamespaceConstraint convert(final NamespaceConstraint input)
    {
        switch (input.getMode())
        {
            case Any:
            {
                return NamespaceConstraint.Any();
            }
            case Include:
            {
                return NamespaceConstraint.include(convert(input.getNamespaces()));
            }
            case Exclude:
            {
                // This approach is a bit long-winded but it generalizes better
                // to multiple exclusions.
                final Iterator<String> namespaces = convert(input.getNamespaces()).iterator();
                if (namespaces.hasNext())
                {
                    return NamespaceConstraint.exclude(namespaces.next());
                }
                else
                {
                    throw new AssertionError();
                }
            }
            default:
            {
                throw new AssertionError(input.getMode());
            }
        }
    }

    private AttributeDefinition convertAttribute(final XMLAttribute xmlAttribute) throws AbortException, SchemaException
    {
        final QName name = xmlAttribute.getName();
        final ScopeExtent scope = convertScope(xmlAttribute.getScope());
        if (scope == ScopeExtent.Global)
        {
            if (m_outBag.hasAttribute(name))
            {
                return m_outBag.getAttribute(name);
            }
            if(m_existingCache.hasAttribute(name))
            {
            	if(!m_lastInWins)
            	{
            		// We are not allowing this schema parse to create new elements.  
                    m_inCache.m_attributesUnresolved.remove(name);
                    return m_existingCache.getAttributeDeclaration(name);
            	}
            	else if(m_inCache.m_attributesUnresolved.containsKey(name) || m_attributesResolvedFromExistingCache.containsKey(name))
            	{
            		// This component is a reference which refers to an imported component; otherwise, its name would not be 
            		// in the m_inCache.m_XxxUnresolved.  When XMLSchemaCache.registerXxx is called, it removes name from m_XxxUnresolved,
            		// and registerXxxt is called when XMLSchemaParser encounters a global component, the only components that can be referenced.
            		// So, we're going to remove its name from the list, NOT convert it, and return the existing component.
                    m_inCache.m_attributesUnresolved.remove(name);
            		AttributeDefinition existing = m_existingCache.getAttributeDeclaration(name);
            		m_attributesResolvedFromExistingCache.put(name, existing);
                    return existing;
            	}
            }
            if (m_cycles.attributes.contains(xmlAttribute))
            {
                throw new SccCyclicAttributeException(name);
            }
            else
            {
                m_cycles.attributes.push(xmlAttribute);
            }
        }
        final AttributeDeclTypeImpl attribute;
        try
        {
            attribute = new AttributeDeclTypeImpl(name, scope, m_existingCache.getSimpleUrType());
            if (scope == ScopeExtent.Global)
            {
                m_outBag.add(attribute);
            }
            m_locations.m_attributeLocations.put(attribute, xmlAttribute.getLocation());
        }
        finally
        {
            if (scope == ScopeExtent.Global)
            {
                m_cycles.attributes.pop();
            }
        }
        final Type attributeType = convertType(xmlAttribute.typeRef);
        if (attributeType instanceof SimpleType)
        {
            attribute.setType((SimpleType)attributeType);
        }
        else
        {
            m_errors.error(new SccAttributeDeclarationSimpleTypeException(name));
        }
        if (null != xmlAttribute.m_valueConstraint)
        {
            try
            {
                attribute.setValueConstraint(convertValueConstraint(XMLRepresentation.LN_ATTRIBUTE, xmlAttribute.m_valueConstraint, (SimpleType)attribute.getType()));
            }
            catch (final SchemaException e)
            {
                m_errors.error(e);
            }
        }
        copyForeignAttributes(xmlAttribute.foreignAttributes, attribute);
        return attribute;
    }

    private AttributeGroupDefinition convertAttributeGroup(final XMLAttributeGroup xmlAttributeGroup) throws AbortException, SchemaException
    {
        final QName agName = PreCondition.assertArgumentNotNull(xmlAttributeGroup.getName(), "name");
        final ScopeExtent scope = convertScope(xmlAttributeGroup.getScope());
        if (scope == ScopeExtent.Global)
        {
            if (m_outBag.hasAttributeGroup(agName))
            {
                return m_outBag.getAttributeGroup(agName);
            }
            if(m_existingCache.hasAttributeGroup(agName))
            {
            	if(!m_lastInWins)
            	{
            		// We are not allowing this schema parse to create new elements.  
                    m_inCache.m_attributeGroupsUnresolved.remove(agName);
                    return m_existingCache.getAttributeGroup(agName);
            	}
            	else if(m_inCache.m_attributeGroupsUnresolved.containsKey(agName) || m_attributeGroupsResolvedFromExistingCache.containsKey(agName))
            	{
            		// This component is a reference which refers to an imported component; otherwise, its name would not be 
            		// in the m_inCache.m_XxxUnresolved.  When XMLSchemaCache.registerXxx is called, it removes name from m_XxxUnresolved,
            		// and registerXxxt is called when XMLSchemaParser encounters a global component, the only components that can be referenced.
            		// So, we're going to remove its name from the list, NOT convert it, and return the existing component.
                    m_inCache.m_attributeGroupsUnresolved.remove(agName);
            		AttributeGroupDefinition existing = m_existingCache.getAttributeGroup(agName);
            		m_attributeGroupsResolvedFromExistingCache.put(agName, existing);
                    return existing;
            	}
            }
            if (m_cycles.attributeGroups.contains(xmlAttributeGroup))
            {
                throw new SccCyclicAttributeGroupException(xmlAttributeGroup.getName());
            }
            else
            {
                m_cycles.attributeGroups.push(xmlAttributeGroup);
            }
        }
        try
        {
            final HashMap<QName, AttributeUse> attributeUses = new HashMap<QName, AttributeUse>();
            for (final XMLAttributeGroup group : xmlAttributeGroup.getGroups())
            {
                final AttributeGroupDefinition attributeGroup = convertAttributeGroup(group);
                if (attributeGroup.hasAttributeUses())
                {
                    for (final AttributeUse attributeUse : attributeGroup.getAttributeUses())
                    {
                        attributeUses.put(attributeUse.getAttribute().getName(), attributeUse);
                    }
                }
            }
            for (final XMLAttributeUse attributeUse : xmlAttributeGroup.getAttributeUses())
            {
                final QName name = attributeUse.getDeclaration().getName();
                try
                {
                    attributeUses.put(name, convertAttributeUse(attributeUse));
                }
                catch (final SchemaException e)
                {
                    m_errors.error(e);
                }
            }
            final SchemaWildcard completeWildcard = completeWildcard(xmlAttributeGroup.getGroups(), xmlAttributeGroup.wildcard);
            final AttributeGroupDefinition attributeGroup;
            attributeGroup = new AttributeGroupImpl(agName, scope, attributeUses.values(), completeWildcard);

            if (attributeGroup.getScopeExtent() == ScopeExtent.Global)
            {
                m_outBag.add(attributeGroup);
            }
            m_locations.m_attributeGroupLocations.put(attributeGroup, xmlAttributeGroup.getLocation());
            copyForeignAttributes(xmlAttributeGroup.foreignAttributes, (AttributeGroupImpl)attributeGroup);
            return attributeGroup;
        }
        finally
        {
            if (scope == ScopeExtent.Global)
            {
                m_cycles.attributeGroups.pop();
            }
        }
    }

    private void convertAttributeGroups() throws AbortException
    {
        for (final XMLAttributeGroup source : m_inCache.m_attributeGroups.values())
        {
            try
            {
                QName name = source.getName();
                if(!m_lastInWins && m_existingCache.getAttributeGroup(name) != null)
                {
                    m_inCache.m_attributeGroupsUnresolved.remove(name);
                }
                convertAttributeGroup(source);
            }
            catch (final SchemaException e)
            {
                m_errors.error(e);
            }
        }
    }

    private void convertAttributes() throws AbortException
    {
        for (final XMLAttribute source : m_inCache.m_attributes.values())
        {
            try
            {
                QName name = source.getName();
                
                if(!m_lastInWins && m_existingCache.hasAttribute(name))
                {
                    m_inCache.m_attributesUnresolved.remove(name);
                }
                convertAttribute(source);
            }
            catch (final SchemaException e)
            {
                m_errors.error(e);
            }
        }
    }

    private AttributeUse convertAttributeUse(final XMLAttributeUse xmlAttributeUse) throws AbortException, SchemaException
    {
        final AttributeDefinition attribute = convertAttribute(xmlAttributeUse.getDeclaration());
        final AttributeUseImpl attributeUse = new AttributeUseImpl(xmlAttributeUse.isRequired(), attribute);
        if (null != xmlAttributeUse.getValueConstraint())
        {
            final Type attributeType = attribute.getType();
            if (attributeType instanceof SimpleType)
            {
                final SimpleType simpleType = (SimpleType)attributeType;
                try
                {
                    attributeUse.setValueConstraint(convertValueConstraint(XMLRepresentation.LN_ATTRIBUTE, xmlAttributeUse.getValueConstraint(), simpleType));
                }
                catch (final SchemaException e)
                {
                    m_errors.error(e);
                }
            }
            else if (attributeType instanceof SimpleUrType)
            {
                // TODO: Do we set the value constraint with xs:untypedAtomic?
            }
            else
            {
                throw new AssertionError(attributeType);
            }
        }
        return attributeUse;
    }

    private ComplexType convertComplexType(final QName outName, final boolean isAnonymous, final XMLType xmlComplexType) throws AbortException, SchemaException
    {
        final ScopeExtent scope = convertScope(xmlComplexType.getScope());
        if (scope == ScopeExtent.Global)
        {
            if (m_outBag.hasComplexType(outName))
            {
                return m_outBag.getComplexType(outName);
            }
            if(m_existingCache.hasComplexType(outName))
            {
            	if(!m_lastInWins)
            	{
            		// We are not allowing this schema parse to create new elements.  
                    m_inCache.m_typesUnresolved.remove(outName);
                    return m_existingCache.getComplexType(outName);
            	}
            	else if(m_inCache.m_typesUnresolved.containsKey(outName) || m_typesResolvedFromExistingCache.containsKey(outName))
            	{
            		// This component is a reference which refers to an imported component; otherwise, its name would not be 
            		// in the m_inCache.m_XxxUnresolved.  When XMLSchemaCache.registerXxx is called, it removes name from m_XxxUnresolved,
            		// and registerXxxt is called when XMLSchemaParser encounters a global component, the only components that can be referenced.
            		// So, we're going to remove its name from the list, NOT convert it, and return the existing component.
                    m_inCache.m_typesUnresolved.remove(outName);
            		ComplexType existing = m_existingCache.getComplexType(outName);
            		m_typesResolvedFromExistingCache.put(outName, existing);
                    return existing;
            	}
            }
            if (m_cycles.types.contains(xmlComplexType))
            {
                throw new SmCyclicTypeException(outName);
            }

            m_cycles.types.push(xmlComplexType);
            m_complexTypeNameCycles.push(xmlComplexType.getName());
        }
        try
        {

            final Map<QName, AttributeUse> attributeUses = new HashMap<QName,AttributeUse>();

            // Constructing and registering the complex type allows it to be
            // referenced in the {content type} property.
            final ComplexTypeImpl complexType;
            complexType = new ComplexTypeImpl(outName, false, isAnonymous, scope, null, xmlComplexType.getDerivationMethod(), attributeUses, EMPTY_CONTENT, xmlComplexType.getBlock(), m_existingCache.getAtomicType(NativeType.UNTYPED_ATOMIC));
            
            m_outBag.add(complexType);
            m_locations.m_complexTypeLocations.put(complexType, xmlComplexType.getLocation());
            
            final Type baseType = convertType(xmlComplexType.getBaseRef());
            complexType.setBaseType(baseType);
            
            computeAttributeUses(xmlComplexType, attributeUses);
            complexType.setContentType(convertContentType(xmlComplexType));

            complexType.setAbstract(xmlComplexType.isAbstract());
            complexType.setAttributeWildcard(attributeWildcard(xmlComplexType));

            for (final DerivationMethod derivation : xmlComplexType.getBlock())
            {
                complexType.setBlock(derivation, true);
            }

            for (final DerivationMethod derivation : xmlComplexType.getFinal())
            {
                if (derivation.isExtension() || derivation.isRestriction())
                {
                    complexType.setFinal(derivation, true);
                }
                else
                {
                    throw new AssertionError(derivation);
                }
            }
            copyForeignAttributes(xmlComplexType.foreignAttributes, complexType);
            return complexType;
        }
        finally
        {
            if (scope == ScopeExtent.Global)
            {
                m_cycles.types.pop();
                final QName name = m_complexTypeNameCycles.pop();

                // If we have any late resolutions to do, make sure we're back at the point of the
                // stack where the late resolutions needs to begin; that ensures that the necessary base 
                // type(s) have been resolved.  (See GXML-45 for relevant use cases.)
                if(!m_lateTypeResolutionNameList.isEmpty() &&  name.equals(m_lateTypeResolutionNameList.get(0)))
                {
                	while(!m_lateTypeResolutionNameList.isEmpty())
                	{
                		lateResolveType(m_lateTypeResolutionNameList.get(0));
                	}
                	if(!m_lateTypeResolutionMap.isEmpty())
                	{
                		throw new IllegalStateException("Late type resolution map should be empty, but it is not.");
                	}
                	// Element type resolution was delayed for all types whose resolution was delayed.
                	// Those types have been resolved, so now we can resolve the elements.
                	ArrayList<LateResolveElement> list = m_lateElementResolutionMap.get(name);
                	if(list != null && !list.isEmpty())
                	{
                    	for(LateResolveElement lre : list) {
                    		convertElementTypeRef(lre.mi_xmlElement, lre.mi_elementDecl, lre.mi_subHead);
                    	}
                	}
                }
            }
        }
    }
    /**
     * Retrieves a list of the names of complex types needing late resolution; then, retrieves the 
     * corresponding ComplexType object & builds it content model.
     * 
     * (See GXML-45 for use case.)
     *    
     * @param typeName name of the now-resolved type which has dependent types which were awaiting its
     * resolution
     * @throws SchemaException 
     * @throws AbortException 
     * @throws SmAbortException
     * @throws SmException
     */
    private void lateResolveType(final QName typeName) throws AbortException, SchemaException
    {
    	// Get the list of type names which were depending on the incoming type name's resolution.
    	final List<XMLType> list = m_lateTypeResolutionMap.get(typeName);
    	m_lateTypeResolutionMap.remove(typeName);
    	m_lateTypeResolutionNameList.remove(typeName);
    	if(list != null)
    	{
    		// Iterate of the list of XMLType objects
    		for(final XMLType xmlType : list)
    		{
    			final QName lateResolveTypeName = xmlType.getName();
//System.out.println("   late resolution: " + lateResolveTypeName + " because of " + typeName);
    			final ComplexTypeImpl complexType = (ComplexTypeImpl)m_outBag.getComplexType(lateResolveTypeName);
    			complexType.setContentType(convertContentType(xmlType));
    			lateResolveType(lateResolveTypeName);
    		}
    	}
    }

    private ContentType convertContentType(final XMLType xmlComplexType) throws AbortException, SchemaException
    {
        final DerivationMethod derivation = xmlComplexType.getDerivationMethod();

        if (xmlComplexType.m_contentKind.isComplex())
        {
            final boolean mixed = xmlComplexType.m_contentKind.isMixed();
            final ModelGroupUse effectiveContent = effectiveContent(mixed, xmlComplexType.m_contentModel);
            if (derivation.isRestriction())
            {
                if (null == effectiveContent)
                {
                    return EMPTY_CONTENT;
                }
                else
                {
                    if (mixed)
                    {
                        return new ContentTypeImpl(mixed, effectiveContent);
                    }
                    else
                    {
                        if (effectiveContent.getTerm() == null || effectiveContent.getTerm().getParticles().isEmpty())
                        {
                            return EMPTY_CONTENT;
                        }
                        else
                        {
                            return new ContentTypeImpl(mixed, effectiveContent);
                        }
                    }
                }
            }
            else if (derivation.isExtension())
            {
            	// Is the typeRef resolved, yet?  If not, postpone resolution of this type's content.				
            	final QName typeRefName =  xmlComplexType.getBaseRef().getName();
            	if(m_complexTypeNameCycles.contains(typeRefName) || m_lateTypeResolutionMap.containsKey(typeRefName))
            	{
            		//System.out.println("      cycle detected for " + xmlComplexType.getName().getC14NForm());
            		ArrayList<XMLType> list = m_lateTypeResolutionMap.get(typeRefName);
            		if(list == null)
            		{
            			list = new ArrayList<XMLType>();
            			m_lateTypeResolutionMap.put(typeRefName, list);
//System.out.println("Add "+typeRefName+" to lateTypeResolutionNameList");
            			m_lateTypeResolutionNameList.add(typeRefName);
            		}
            		list.add(xmlComplexType);
            		// Also, add the type to be late resolved as key to late resolution map.  Any components
            		// depending on its content model must also wait for resolution.
            		if(false == m_lateTypeResolutionMap.containsKey(xmlComplexType.getName()))
            		{
            			m_lateTypeResolutionMap.put(xmlComplexType.getName(), null);
//System.out.println("Add "+xmlComplexType.getName()+" to lateTypeResolutionNameList");
            			m_lateTypeResolutionNameList.add(xmlComplexType.getName());
            		}
            		return EMPTY_CONTENT; // actual content to be determined later
            	}
                final Type typeB = convertType(xmlComplexType.getBaseRef());
                if (typeB instanceof ComplexType)
                {
                    final ComplexType complexTypeB = (ComplexType)typeB;
                    final ContentType contentTypeB = complexTypeB.getContentType();
                    if (null == effectiveContent)
                    {
                        return contentTypeB;
                    }
                    else if (contentTypeB.isEmpty())
                    {
                        return new ContentTypeImpl(mixed, effectiveContent);
                    }
                    else if (contentTypeB.isSimple())
                    {
                        throw new SrcBaseContentTypeCannotBeSimpleException(xmlComplexType.getName(), complexTypeB.getName(), xmlComplexType.getLocation());
                    }
                    else if (contentTypeB.isComplex())
                    {
                        final LinkedList<ModelGroupUse> particles = new LinkedList<ModelGroupUse>();
                        particles.add(contentTypeB.getContentModel());
                        particles.add(effectiveContent);
                        final ModelGroup modelGroup = new ModelGroupImpl(ModelGroup.SmCompositor.Sequence, particles, null, true, ScopeExtent.Local);
                        final ModelGroupUse particle = new ParticleWithModelGroupTerm(1, 1, modelGroup);
                        return new ContentTypeImpl(mixed, particle);
                    }
                    else
                    {
                        throw new AssertionError(contentTypeB.getKind());
                    }
                }
                else
                {
                    throw new SrcBaseTypeMustBeComplexTypeException(xmlComplexType.getLocation());
                }
            }
            else
            {
                throw new AssertionError(derivation);
            }
        }
        else if (xmlComplexType.m_contentKind.isSimple())
        {
            final Type typeB = convertType(xmlComplexType.getBaseRef());
            if (typeB instanceof ComplexType)
            {
                final ComplexType complexTypeB = (ComplexType)typeB;
                final ContentType contentTypeB = complexTypeB.getContentType();
                if (contentTypeB.isSimple())
                {
                    if (derivation.isRestriction())
                    {
                        return simpleContent(xmlComplexType.simpleType, contentTypeB.getSimpleType());
                    }
                    else if (derivation.isExtension())
                    {
                        return contentTypeB;
                    }
                    else
                    {
                        throw new AssertionError(derivation);
                    }
                }
                else
                {
                    if (derivation.isRestriction())
                    {
                        if (contentTypeB.isMixed())
                        {
                            final ModelGroupUse contentModelB = contentTypeB.getContentModel();
                            if (contentModelB.isEmptiable())
                            {
                                final XMLTypeRef simpleType = xmlComplexType.simpleType.getBaseRef();
                                if (null != simpleType)
                                {
                                    final SimpleType simpleBaseType = extractSimpleType(simpleType);
                                    return simpleContent(xmlComplexType.simpleType, simpleBaseType);
                                }
                                else
                                {
                                    throw new SrcSimpleTypeAmongChildrenOfRestrictionException(xmlComplexType.getLocation());
                                }
                            }
                            else
                            {
                                throw new SrcBaseMustHaveSimpleOrMixedContentTypeComplexTypeException(xmlComplexType.getLocation());
                            }
                        }
                        else
                        {
                            throw new SrcBaseMustHaveSimpleOrMixedContentTypeComplexTypeException(xmlComplexType.getLocation());
                        }
                    }
                    else if (derivation.isExtension())
                    {
                        throw new SrcBaseMustHaveSimpleOrMixedContentTypeComplexTypeException(xmlComplexType.getLocation());
                    }
                    else
                    {
                        throw new AssertionError(derivation);
                    }
                }
            }
            else if (typeB instanceof SimpleType)
            {
                final SimpleType simpleTypeB = (SimpleType)typeB;
                if (derivation.isExtension())
                {
                    return new ContentTypeImpl(simpleTypeB);
                }
                else if (derivation.isRestriction())
                {
                    return new ContentTypeImpl(simpleTypeB);
                }
                else
                {
                    throw new AssertionError(derivation);
                }
            }
            else
            {
                throw new AssertionError(typeB);
            }
        }
        else
        {
            return computeLocallyEmptyContent(xmlComplexType);
        }
    }

    private ElementDefinition convertElement(final XMLElement xmlElement) throws SchemaException, AbortException
    {
        final QName name = PreCondition.assertArgumentNotNull(xmlElement.getName(), "name");
        final ScopeExtent scope = convertScope(xmlElement.getScope());
        if (scope == ScopeExtent.Global)
        {
            if (m_outBag.hasElement(name))
            {
                return m_outBag.getElement(name);
            }
            if(m_existingCache.hasElement(name))
            {
            	if(!m_lastInWins)
            	{
            		// We are not allowing this schema parse to create new elements.  
                    m_inCache.m_elementsUnresolved.remove(name);
                    return m_existingCache.getElementDeclaration(name);
            	}
            	else if(m_inCache.m_elementsUnresolved.containsKey(name) || m_elementsResolvedFromExistingCache.containsKey(name))
            	{
            		// This element is a reference which refers to an imported element; otherwise, its name would not be 
            		// in the m_inCache.m_elementsUnresolved.  When XMLSchemaCache.registerELement is called, it removes name from m_elementsUnresolved,
            		// and registerElement is called when XMLSchemaParser encounters a global element, the only elements that can be referenced.
            		// So, we're going to remove its name from the list and NOT convert it -- it's just a reference
                    m_inCache.m_elementsUnresolved.remove(name);
            		ElementDefinition existing = m_existingCache.getElementDeclaration(name);
            		m_elementsResolvedFromExistingCache.put(name, existing);
                    return existing;
            	}
            }
            if (m_cycles.elements.contains(xmlElement))
            {
                throw new SccCyclicElementException(name);
            }
            else
            {
                m_cycles.elements.push(xmlElement);
            }
        }
        final ElementDeclTypeImpl element;
        ElementDeclTypeImpl substitutionGroupHead = null;            
        try
        {
            PreCondition.assertArgumentNotNull(xmlElement.typeRef, "{type definition} of " + name);

            // The element {type definition} defaults to xs:anyType because
            // there may be circularities.
            // {name}, {target namespace} and {scope} are set here. We set the
            // {type definition} and other
            // properties outside of the scope for checking cycles.
            final ComplexUrType anyType = m_existingCache.getComplexUrType();
            element = new ElementDeclTypeImpl(name, scope, anyType);

            // {substitution group affiliation}
            if (null != xmlElement.substitutionGroup)
            {
                // TODO: Would be nice to avoid this downcast. Maybe by using name for group head?
                substitutionGroupHead = (ElementDeclTypeImpl)convertElement(xmlElement.substitutionGroup);
                element.setSubstitutionGroup(substitutionGroupHead);
                substitutionGroupHead.addSubstitutionGroupMember(element);
            }

            // {identity-constraint definitions}
            for (final XMLIdentityConstraint constraint : xmlElement.getIdentityConstraints())
            {
                element.addIdentityConstraint(convertIdentityConstraint(constraint));
            }
        }
        finally
        {
            if (scope == ScopeExtent.Global)
            {
                m_cycles.elements.pop();
            }
        }

        if (element.getScopeExtent() == ScopeExtent.Global)
        {
            m_outBag.add(element);
        }
        m_locations.m_elementLocations.put(element, xmlElement.getLocation());

		// {type definition}
		convertElementTypeRef(xmlElement, element, substitutionGroupHead);

		// {nillable}
		element.setNillable(xmlElement.isNillable());

		// {disallowed substitutions}
		for (final DerivationMethod derivation : xmlElement.getBlock())
		{
			element.setBlock(derivation, true);
		}

		// {substitution group exclusions}
		for (final DerivationMethod derivation : xmlElement.getFinal())
		{
			element.setFinal(derivation, true);
		}

		// {abstract}
		element.setAbstract(xmlElement.isAbstract());

		// {annotation} we don't care about.

		// foreign attributes
		copyForeignAttributes(xmlElement.foreignAttributes, element);
		// We're done!
		return element;
    }
    private void convertElementTypeRef(final XMLElement xmlElement, final ElementDeclTypeImpl element, ElementDeclTypeImpl substitutionGroupHead) throws AbortException, SchemaException
    {
    	// {type definition}
    	// Is the typeRef resolved, yet?  If not, postpone resolution of this type's content.				
    	if(xmlElement.typeRef.isGlobal())
    	{
    		final QName typeRefName = xmlElement.typeRef.getName();
    		if(m_complexTypeNameCycles.contains(typeRefName))
    		{
    			//System.out.println("      cycle detected for element type ref " + xmlElement.getName().getC14NForm());
    			ArrayList<LateResolveElement> list = m_lateElementResolutionMap.get(typeRefName);
    			if(list == null) {
    				list = new ArrayList<LateResolveElement>();
    				m_lateElementResolutionMap.put(typeRefName, list);
    			}
    			list.add(new LateResolveElement(xmlElement, element, substitutionGroupHead));
    			return;
    		}
    		else
    		{
    			//System.out.println("      NO cycle detected for element type ref " + xmlElement.getName().getC14NForm());
    		}
    	}

    	Type typeFromTypeRef = convertType(xmlElement.typeRef);
    	// If the typeFromTypeRef is complexUrType, then it was not set, probably because
    	// the element did not have a type attribute.  So, use the type from the substitutionGroup
    	// head, if possible.
    	if(substitutionGroupHead != null && typeFromTypeRef.isComplexUrType()) {
    		typeFromTypeRef = substitutionGroupHead.getType();
    	}
    	element.setType(typeFromTypeRef);

    	// {value constraint}
    	if (null != xmlElement.m_valueConstraint)
    	{
            if (element.getType() instanceof SimpleType)
            {
                final SimpleType elementType = (SimpleType)element.getType();
                try
                {
                    element.setValueConstraint(convertValueConstraint(XMLRepresentation.LN_ELEMENT, xmlElement.m_valueConstraint, elementType));
                }
                catch (final SchemaException e)
                {
                    m_errors.error(e);
                }
            }
            else if (element.getType() instanceof ComplexType)
            {
                final ComplexType elementType = (ComplexType)element.getType();
                final ContentType contentType = elementType.getContentType();
                if (contentType.isSimple())
                {
                    final SimpleType simpleType = contentType.getSimpleType();
                    try
                    {
                        element.setValueConstraint(convertValueConstraint(XMLRepresentation.LN_ELEMENT, xmlElement.m_valueConstraint, simpleType));
                    }
                    catch (final SchemaException e)
                    {
                        m_errors.error(e);
                    }
                }
                else
                {
                  final String initialValue = xmlElement.m_valueConstraint.getValue();
                  final SimpleType simpleType = m_existingCache.getSimpleType(NativeType.UNTYPED_ATOMIC);
                  element.setValueConstraint(new ValueConstraint(xmlElement.m_valueConstraint.kind, simpleType, initialValue));
                }
            }
            else
            {
                throw new AssertionError(element.getType());
            }
		}
	}

    private void convertElements() throws AbortException
    {
        for (final XMLElement source : m_inCache.m_elements.values())
        {
            try
            {
                QName name = source.getName();
                if(!m_lastInWins && m_existingCache.getElementDeclaration(name) != null)
                {
                    m_inCache.m_elementsUnresolved.remove(name);
                }
                convertElement(source);
            }
            catch (final SchemaException e)
            {
                m_errors.error(e);
            }
        }
        // Ensure that all elements have their type refs resolved.
        // Element type resolution was delayed for all types whose resolution was delayed.
        // Those types have been resolved, so now we can resolve the elements.
        for(QName typeName : m_lateElementResolutionMap.keySet()) {
        	ArrayList<LateResolveElement> list = m_lateElementResolutionMap.get(typeName);
        	for(LateResolveElement lre : list) {
                try {
                	convertElementTypeRef(lre.mi_xmlElement, lre.mi_elementDecl, lre.mi_subHead);
                } catch (SchemaException e) {
                	m_errors.error(e);
                }
        	}
        }
        m_lateElementResolutionMap.clear();
    }

    private SchemaParticle convertElementUse(final XMLParticleWithElementTerm particle) throws SchemaException, AbortException
    {
        final XMLElement xmlElement = particle.getTerm();
        final ElementDefinition element = convertElement(xmlElement);

        final ParticleWithElementTerm elementUse;
        if (isMaxOccursUnbounded(particle.getMaxOccurs()))
        {
            final int minOccurs = minOccurs(particle.getMinOccurs());
            elementUse = new ParticleWithElementTerm(minOccurs, element);
        }
        else
        {
            final int minOccurs = minOccurs(particle.getMinOccurs());
            final int maxOccurs = maxOccurs(particle.getMaxOccurs());
            elementUse = new ParticleWithElementTerm(minOccurs, maxOccurs, element);
        }
        m_locations.m_particleLocations.put(elementUse, particle.getLocation());
        if (null != particle.valueConstraint)
        {
            final ValueConstraint valueConstraint = convertElementValueConstraint(particle.valueConstraint, element.getType());
            elementUse.setValueConstraint(valueConstraint);
        }
        return elementUse;
    }

    private ValueConstraint convertElementValueConstraint(final XMLValueConstraint xmlValueConstraint, final Type type) throws SchemaException
    {
        if (xmlValueConstraint != null)
        {
            if (type instanceof SimpleType)
            {
                return convertValueConstraint(XMLRepresentation.LN_ELEMENT, xmlValueConstraint, (SimpleType)type);
            }
            else if (type instanceof ComplexType)
            {
                final ContentType contentType = ((ComplexType)type).getContentType();
                if (contentType.isSimple())
                {
                    return convertValueConstraint(XMLRepresentation.LN_ELEMENT, xmlValueConstraint, contentType.getSimpleType());
                }
                else
                {
                    final String initialValue = xmlValueConstraint.getValue();
                    return new ValueConstraint(xmlValueConstraint.kind, m_existingCache.getSimpleType(NativeType.UNTYPED_ATOMIC), initialValue);
                }
            }
            throw new AssertionError(type); // neither simple nor complex; should not happen
        }
        return null;
    }

    private IdentityConstraint convertIdentityConstraint(final XMLIdentityConstraint xmlConstraint) throws SchemaException
    {
        final QName name = xmlConstraint.getName();
        if (m_outBag.hasIdentityConstraint(name))
        {
            return m_outBag.getIdentityConstraint(name);
        }
        if(m_existingCache.hasIdentityConstraint(name))
        {
        	if(!m_lastInWins)
        	{
        		// We are not allowing this schema parse to create new elements.  
                m_inCache.m_constraintsUnresolved.remove(name);
                return m_existingCache.getIdentityConstraint(name);
        	}
        	else if(m_inCache.m_constraintsUnresolved.containsKey(name) || m_constraintsResolvedFromExistingCache.containsKey(name))
        	{
        		// This component is a reference which refers to an imported component; otherwise, its name would not be 
        		// in the m_inCache.m_XxxUnresolved.  When XMLSchemaCache.registerXxx is called, it removes name from m_XxxUnresolved,
        		// and registerXxxt is called when XMLSchemaParser encounters a global component, the only components that can be referenced.
        		// So, we're going to remove its name from the list, NOT convert it, and return the existing component.
                m_inCache.m_constraintsUnresolved.remove(name);
        		IdentityConstraint existing = m_existingCache.getIdentityConstraint(name);
        		m_constraintsResolvedFromExistingCache.put(name, existing);
                return existing;
        	}
        }
        if (m_cycles.constraints.contains(xmlConstraint))
        {
            throw new SccCyclicIdentityConstraintException(name);
        }
        m_cycles.constraints.push(xmlConstraint);
        try
        {
            if (null == xmlConstraint.keyConstraint)
            {
                final IdentityConstraint constraint = new IdentityConstraintImpl(name, xmlConstraint.category, xmlConstraint.selector, xmlConstraint.fields, null);
                m_outBag.add(constraint);
                m_locations.m_constraintLocations.put(constraint, xmlConstraint.getLocation());
                return constraint;
            }
            else
            {
                final IdentityConstraint keyConstraint = convertIdentityConstraint(xmlConstraint.keyConstraint);
                final IdentityConstraint constraint = new IdentityConstraintImpl(name, xmlConstraint.category, xmlConstraint.selector, xmlConstraint.fields, keyConstraint);
                m_outBag.add(constraint);
                m_locations.m_constraintLocations.put(constraint, xmlConstraint.getLocation());
                return constraint;
            }
        }
        finally
        {
            m_cycles.constraints.pop();
        }
    }

    private void convertIdentityConstraints() throws AbortException
    {
        for (final XMLIdentityConstraint source : m_inCache.m_constraints.values())
        {
            try
            {
                QName name = source.getName();
                if(!m_lastInWins && m_existingCache.getIdentityConstraint(name) != null)
                {
                    m_inCache.m_constraintsUnresolved.remove(name);
                }
                convertIdentityConstraint(source);
            }
            catch (final SchemaException e)
            {
                m_errors.error(e);
            }
        }
    }

    private SimpleType convertItemType(final QName simpleType, final XMLTypeRef typeRef) throws AbortException, SchemaException
    {
        final Type itemType = convertType(typeRef);
        if (itemType.isAtomicType())
        {
            return (SimpleType)itemType;
        }
        else if (itemType instanceof UnionSimpleType)
        {
            return (UnionSimpleType)itemType;
        }
        else
        {
            throw new SccItemTypeMustBeAtomicOrUnionException(simpleType);
        }
    }

    private SimpleType convertMemberType(final QName simpleType, final XMLTypeRef typeRef) throws AbortException, SchemaException
    {
        final Type memberType = convertType(typeRef);
        if (memberType.isAtomicType())
        {
            return (SimpleType)memberType;
        }
        else if (memberType instanceof ListSimpleType)
        {
            return (ListSimpleType)memberType;
        }
        else if (memberType instanceof UnionSimpleType)
        {
        	return (UnionSimpleType)memberType;
        }
        else if (memberType instanceof SimpleType)
        {
            if (memberType.isSimpleUrType())
            {
                return (SimpleType)memberType;
            }
        }
        throw new SccMemberTypeMustBeAtomicOrListException(simpleType);
    }

    private ModelGroup convertModelGroup(final XMLModelGroup xmlModelGroup) throws AbortException, SchemaException
    {
        final ScopeExtent scope = convertScope(xmlModelGroup.getScope());
        final QName name;
        final boolean isAnonymous;
        
        if (scope == ScopeExtent.Global)
        {
            name = xmlModelGroup.getName();
            isAnonymous = false;
            if (m_outBag.hasModelGroup(name))
            {
                return m_outBag.getModelGroup(name);
            }
            if(m_existingCache.hasModelGroup(name))
            {
            	if(!m_lastInWins)
            	{
            		// We are not allowing this schema parse to create new elements.  
                    m_inCache.m_modelGroupsUnresolved.remove(name);
                    return m_existingCache.getModelGroup(name);
            	}
            	else if(m_inCache.m_modelGroupsUnresolved.containsKey(name) || m_modelGroupsResolvedFromExistingCache.containsKey(name))
            	{
            		// This component is a reference which refers to an imported component; otherwise, its name would not be 
            		// in the m_inCache.m_XxxUnresolved.  When XMLSchemaCache.registerXxx is called, it removes name from m_XxxUnresolved,
            		// and registerXxxt is called when XMLSchemaParser encounters a global component, the only components that can be referenced.
            		// So, we're going to remove its name from the list, NOT convert it, and return the existing component.
                    m_inCache.m_modelGroupsUnresolved.remove(name);
            		ModelGroup existing = m_existingCache.getModelGroup(name);
            		m_modelGroupsResolvedFromExistingCache.put(name, existing);
                    return existing;
            	}
            }
            if (m_cycles.groups.contains(xmlModelGroup))
            {
                throw new SccCyclicModelGroupException(name, xmlModelGroup.getLocation());
            }
            else
            {
                m_cycles.groups.push(xmlModelGroup);
            }
        }
        else
        {
            name = null;
            isAnonymous = true;
        }
        // Create the model group and add it to m_outBag <em>prior</em> to processing the particles.  This way,
        // we can exclude the contents of element particles from our modelGroup cycle check.
        final ModelGroup.SmCompositor compositor = xmlModelGroup.getCompositor();
        final LinkedList<SchemaParticle> particles = new LinkedList<SchemaParticle>();
        ModelGroup modelGroup = new ModelGroupImpl(compositor, particles, name, isAnonymous, scope);
        copyForeignAttributes(xmlModelGroup.foreignAttributes, (ModelGroupImpl)modelGroup);
        if (modelGroup.getScopeExtent() == ScopeExtent.Global)
        {
            m_outBag.add(modelGroup);
        }
        m_locations.m_modelGroupLocations.put(modelGroup, xmlModelGroup.getLocation());

        try
        {
            
            for (final XMLParticle xmlParticle : xmlModelGroup.getParticles())
            {
                try
                {
                    if (xmlParticle instanceof XMLParticleWithModelGroupTerm)
                    {
                        particles.add(convertModelGroupUse((XMLParticleWithModelGroupTerm)xmlParticle));
                    }
                    else if (xmlParticle instanceof XMLParticleWithElementTerm)
                    {
                    	// We must prevent the contents of element particle from becoming part of our invalid cycles check.
                    	// So, we're going to clear the cycles for groups, and then restore it after we finish processing the 
                    	// element.  
                        Stack<XMLModelGroup> tempGroups = null;
                        if(!m_cycles.groups.isEmpty())
                        {
                        	tempGroups = new Stack<XMLModelGroup>();
                        	for(XMLModelGroup group : m_cycles.groups)
                        	{
                        		tempGroups.add(group);
                        	}
                            m_cycles.groups.clear();
                        }
                        particles.add(convertElementUse((XMLParticleWithElementTerm)xmlParticle));
                        if(tempGroups != null)
                        {
                        	m_cycles.groups.clear(); // should be clear, already; so, this line is probably unnecessary
                        	for(XMLModelGroup group : tempGroups)
                        	{
                        		m_cycles.groups.add(group);
                        	}
                        }
                    }
                    else if (xmlParticle instanceof XMLParticleWithWildcardTerm)
                    {
                        particles.add(convertWildcardUse((XMLParticleWithWildcardTerm)xmlParticle));
                    }
                    else
                    {
                        throw new AssertionError(xmlParticle);
                    }

                }
                catch (final SchemaException e)
                {
                    m_errors.error(e);
                }
            }
            return modelGroup;
        }
        finally
        {
            if (scope == ScopeExtent.Global)
            {
                m_cycles.groups.pop();
            }
        }
    }

    private void convertModelGroups() throws AbortException
    {
        for (final XMLModelGroup source : m_inCache.m_modelGroups.values())
        {
            try
            {
                QName name = source.getName();
                if(!m_lastInWins && m_existingCache.getModelGroup(name) != null)
                {
                    m_inCache.m_modelGroupsUnresolved.remove(name);
                }
                convertModelGroup(source);
            }
            catch (final SchemaException e)
            {
                m_errors.error(e);
            }
        }
    }

    private ModelGroupUse convertModelGroupUse(final XMLParticleWithModelGroupTerm particle) throws AbortException, SchemaException
    {
        final ModelGroup modelGroup = convertModelGroup(particle.getTerm());

        final ModelGroupUse modelGroupUse;
        if (isMaxOccursUnbounded(particle.getMaxOccurs()))
        {
            final int minOccurs = minOccurs(particle.getMinOccurs());
            modelGroupUse = new ParticleWithModelGroupTerm(minOccurs, modelGroup);
        }
        else
        {
            final int minOccurs = minOccurs(particle.getMinOccurs());
            final int maxOccurs = maxOccurs(particle.getMaxOccurs());
            modelGroupUse = new ParticleWithModelGroupTerm(minOccurs, maxOccurs, modelGroup);
        }
        m_locations.m_particleLocations.put(modelGroupUse, particle.getLocation());
        return modelGroupUse;
    }

    private NotationDefinition convertNotation(final XMLNotation xmlNotation)
    {
        final NotationDefinition notation = new NotationImpl(xmlNotation.getName(), xmlNotation.getPublicId(), xmlNotation.getSystemId());
        copyForeignAttributes(xmlNotation.foreignAttributes, (NotationImpl)notation);
        m_outBag.add(notation);
        m_locations.m_notationLocations.put(notation, xmlNotation.getLocation());
        return notation;
    }

    private void convertNotations()
    {
        for (final XMLNotation source : m_inCache.m_notations.values())
        {
            QName name = source.getName();
            if(!m_lastInWins && m_existingCache.getNotationDeclaration(name) != null)
            {
                m_inCache.m_notationsUnresolved.remove(name);
            }
            convertNotation(source);
        }
    }

    private ScopeExtent convertScope(final XMLScope scope)
    {
        PreCondition.assertArgumentNotNull(scope, "scope");

        return scope.isGlobal() ? ScopeExtent.Global : ScopeExtent.Local;
    }

    /**
     * Applies the Schema Component Constraints to this Simple Type.
     */
    private SimpleType convertSimpleType(final QName name, final boolean isAnonymous, final XMLType xmlSimpleType) throws AbortException, SchemaException
    {
        PreCondition.assertTrue(xmlSimpleType.isSimple(), "expecting a simple type for " + name);

        final ScopeExtent scope = convertScope(xmlSimpleType.getScope());
        if (scope == ScopeExtent.Global)
        {
            if (m_outBag.hasSimpleType(name))
            {
                return m_outBag.getSimpleType(name);
            }
            if(m_existingCache.hasSimpleType(name))
            {
            	if(!m_lastInWins)
            	{
            		// We are not allowing this schema parse to create new elements.  
                    m_inCache.m_typesUnresolved.remove(name);
                    return m_existingCache.getSimpleType(name);
            	}
            	else if(m_inCache.m_typesUnresolved.containsKey(name) || m_typesResolvedFromExistingCache.containsKey(name))
            	{
            		// This component is a reference which refers to an imported component; otherwise, its name would not be 
            		// in the m_inCache.m_XxxUnresolved.  When XMLSchemaCache.registerXxx is called, it removes name from m_XxxUnresolved,
            		// and registerXxxt is called when XMLSchemaParser encounters a global component, the only components that can be referenced.
            		// So, we're going to remove its name from the list, NOT convert it, and return the existing component.
                    m_inCache.m_typesUnresolved.remove(name);
            		SimpleType existing = m_existingCache.getSimpleType(name);
            		m_typesResolvedFromExistingCache.put(name, existing);
                    return existing;
            	}
            }
            if (m_cycles.types.contains(xmlSimpleType))
            {
                throw new SmCyclicTypeException(name);
            }

            m_cycles.types.push(xmlSimpleType);
        }
        try
        {
            final SimpleType simpleBaseType;
            if (null != xmlSimpleType.getBaseRef())
            {
                simpleBaseType = convertSimpleTypeBase(name, xmlSimpleType.getBaseRef());
            }
            else
            {
                simpleBaseType = convertSimpleTypeBase(name, xmlSimpleType.getScope().getType().getBaseRef());
            }

            final SimpleTypeImpl simpleType;
            final DerivationMethod derivation = PreCondition.assertNotNull(xmlSimpleType.getDerivationMethod(), "{type definition} with base " + simpleBaseType.getName());
            final WhiteSpacePolicy whiteSpace = xmlSimpleType.getWhiteSpacePolicy();
            if (derivation.isUnion())
            {
                final LinkedList<SimpleType> memberTypes = new LinkedList<SimpleType>();
                for (final XMLTypeRef memberRef : xmlSimpleType.memberRefs)
                {
                    final SimpleType memberType = convertMemberType(name, memberRef);
                    memberTypes.add(memberType);
                }
                simpleType = new UnionTypeImpl(name, isAnonymous, scope, simpleBaseType, memberTypes, whiteSpace);
                m_outBag.add(simpleType);
                m_locations.m_simpleTypeLocations.put(simpleType, xmlSimpleType.getLocation());
            }
            else if (derivation.isList())
            {
                final SimpleType itemType = convertItemType(name, xmlSimpleType.itemRef);
                simpleType = new ListTypeImpl(name, isAnonymous, scope, itemType, simpleBaseType, whiteSpace);
                m_outBag.add(simpleType);
                m_locations.m_simpleTypeLocations.put(simpleType, xmlSimpleType.getLocation());
            }
            else if (derivation.isRestriction())
            {
                simpleType = deriveSimpleType(name, isAnonymous, scope, simpleBaseType, whiteSpace, xmlSimpleType.getLocation());
            }
            else
            {
                throw new AssertionError(derivation.name());
            }
            computePatterns(xmlSimpleType.getPatternFacets(), simpleType);
            computeFacets(simpleBaseType, xmlSimpleType, simpleType);
            computeEnumerations(simpleBaseType, xmlSimpleType, simpleType);
            copyForeignAttributes(xmlSimpleType.foreignAttributes, simpleType);
            return simpleType;
        }
        finally
        {
            if (scope == ScopeExtent.Global)
            {
                m_cycles.types.pop();
            }
        }
    }

    private SimpleType convertSimpleTypeBase(final QName simpleType, final XMLTypeRef baseRef) throws AbortException, SchemaException
    {
        final Type baseType = convertType(baseRef);
        if (baseType instanceof SimpleType)
        {
            return (SimpleType)baseType;
        }
        else
        {
            throw new SccBaseTypeMustBeSimpleTypeException(simpleType);
        }
    }

    private Type convertType(final QName name, final boolean isAnonymous) throws SchemaException, AbortException
    {
        // note: as of 2014-2-18, only ever called from convertType(XMLTypeRef) (q.v.)
        // first, check our incrementally collected results, in m_outBag.
        // if we have something there, return it.
        // should we insure that it's not in the unresolved list, in that case?
        if (m_outBag.hasSimpleType(name))
            return m_outBag.getSimpleType(name);
        else if (m_outBag.hasComplexType(name))
            return m_outBag.getComplexType(name);
        else
        {
            // now, we look in the collection of xmlrep components. is it there?
            if (m_inCache.m_globalTypes.containsKey(name))
            {
                // okay. if it's simple or complex, convert it and return
                final XMLType type = m_inCache.m_globalTypes.get(name);
                if (type.isSimple())
                    return convertSimpleType(name, isAnonymous, type);
                else if (type.isComplex())
                    return convertComplexType(name, isAnonymous, type);
                // if not, then it's an unresolved reference. so ... don't ever
                // call this method if there's more parsing to be done, eh?
                if(m_lastInWins) 
                {
                    // if operating w/lastInWins true, reference could still be in existing cache
                    // and we wouldn't have resolved it, yet.  check now.)
                    if (m_existingCache.hasSimpleType(name))
                    {
                        m_inCache.m_typesUnresolved.remove(name);
                        return m_existingCache.getSimpleType(name);
                    }
                    else if (m_existingCache.hasComplexType(name))
                    {
                        m_inCache.m_typesUnresolved.remove(name);
                        return m_existingCache.getComplexType(name);
                    }
                }
                throw new SmUndeclaredReferenceException(name, m_inCache.m_typesUnresolved.get(name));
            }
            // it's not in the collection of unresolved components
            // is it already in our component provider?
            else if (m_existingCache.hasSimpleType(name))
            {
                m_inCache.m_typesUnresolved.remove(name);
                return m_existingCache.getSimpleType(name);
            }
            else if (m_existingCache.hasComplexType(name))
            {
                m_inCache.m_typesUnresolved.remove(name);
                return m_existingCache.getComplexType(name);
            }
            // not there, either. oops. die in flames.
            throw new SmUndeclaredReferenceException(name, m_inCache.m_typesUnresolved.get(name));
        }
    }

    private Type convertType(final QName name, final boolean isAnonymous, final XMLType type) throws AbortException, SchemaException
    {
        if (type.isSimple())
            return convertSimpleType(name, isAnonymous, type);
        else if (type.isComplex())
            return convertComplexType(name, isAnonymous, type);
        // if it's neither simple nor complex, it's a type reference without a referent
        throw new SmUndeclaredReferenceException(name, m_inCache.m_typesUnresolved.get(name));
    }

    private Type convertType(final XMLTypeRef typeRef) throws AbortException, SchemaException
    {
        if (typeRef.isGlobal())
        {
            QName name = typeRef.getName();
            // normal case: lastInWins is not true.
            // check the cache.
            if (!m_lastInWins)
            {
                if (m_existingCache.hasComplexType(name))
                {
                    m_inCache.m_typesUnresolved.remove(name);
                    return m_existingCache.getComplexType(name);
                }
                if (m_existingCache.hasSimpleType(name))
                {
                    m_inCache.m_typesUnresolved.remove(name);
                    return m_existingCache.getSimpleType(name);
                }
            }
            
            // either it wasn't in the cache (an error), or
            // we're doing lastInWins (it may be in the outbag)
            // so call convertType, so that we check outbag
            // before checking cache--inverting the order, in effect.
            return convertType(typeRef.getName(), false);
        }
        else
        {
            return convertType(m_existingCache.generateUniqueName(), true, typeRef.getLocal());
        }
    }

    private void convertTypes() throws AbortException
    {
        // we're iterating over all of the global types that haven't been
        // converted yet. local types can't ever be unresolved, of course,
        // and should be handled in the context of whatever contains them.
        for (final XMLType sourceType : m_inCache.m_globalTypes.values())
        {
            // {name} is known because the type is global.
            final QName name = sourceType.getName();
            // and therefore not anonymous
            final boolean isAnonymous = false;
            try
            {
                // if this one exists already, do special processing in case lastInWins
            	if (m_existingCache.getComplexType(name) != null || m_existingCache.getSimpleType(name) != null)
            	{
            	    if (!m_lastInWins)
            	        m_inCache.m_typesUnresolved.remove(name);
            	    else
            	    {
            	        // if it's a real type to replace the existing, replace.
            	        if (sourceType.isComplex())
            	            convertComplexType(name, isAnonymous, sourceType);
                        else if (sourceType.isSimple())
                            convertSimpleType(name, isAnonymous, sourceType);
            	        // if neither complex nor simple, then it's a ref; remove from unresolved
                        else
                            m_inCache.m_typesUnresolved.remove(name);
            	    }
            	}
            	// otherwise, it's not in the existing cache, so convert
                else if (sourceType.isComplex())
                    convertComplexType(name, isAnonymous, sourceType);
                else if (sourceType.isSimple())
                    convertSimpleType(name, isAnonymous, sourceType);
                else // this isn't going to work--it's an incomplete type/type reference without a referent
                    convertType(name, isAnonymous, sourceType); // let this method throw.
            }
            catch (final SchemaException e)
            {
                m_errors.error(e);
            }
        }
    }

    private ValueConstraint convertValueConstraint(final String elementName, final XMLValueConstraint xmlValueConstraint, final SimpleType simpleType) 
        throws SchemaException
    {
        if (xmlValueConstraint != null)
        {
            final String initialValue = xmlValueConstraint.getValue();
            try
            {
                List<XmlAtom> val = simpleType.validate(initialValue, m_atoms);
                if (val.size() > 0)
                    return new ValueConstraint(xmlValueConstraint.kind, simpleType, m_atoms.getC14NForm(val.get(0)));
                // TODO: throw a better exception
                throw new AssertionError(); // no value in the value constraint
            }
            catch (DatatypeException dte)
            {
                final SimpleTypeException ste = new SimpleTypeException(initialValue, simpleType, dte);
                throw new SmAttributeUseException(new QName(elementName), xmlValueConstraint.getAttributeName(), xmlValueConstraint.getLocation(), ste);
            }
            
        }
        return null;
    }

    private SchemaWildcard convertWildcard(final XMLWildcard wildcard)
    {
        if (null != wildcard)
        {
            return new WildcardImpl(wildcard.getProcessContents(), convert(wildcard.getNamespaceConstraint()));
        }
        else
        {
            return null;
        }
    }

    private SchemaParticle convertWildcardUse(final XMLParticleWithWildcardTerm particle) throws SicOversizedIntegerException
    {
        final SchemaWildcard wildcard = convertWildcard(particle.getTerm());

        final WildcardUse wildcardUse;
        if (isMaxOccursUnbounded(particle.getMaxOccurs()))
        {
            final int minOccurs = minOccurs(particle.getMinOccurs());
            wildcardUse = new ParticleWithWildcardTerm(minOccurs, wildcard);
        }
        else
        {
            final int minOccurs = minOccurs(particle.getMinOccurs());
            final int maxOccurs = maxOccurs(particle.getMaxOccurs());
            wildcardUse = new ParticleWithWildcardTerm(minOccurs, maxOccurs, wildcard);
        }
        m_locations.m_particleLocations.put(wildcardUse, particle.getLocation());
        return wildcardUse;
    }

    private SimpleTypeImpl deriveSimpleType(final QName name, final boolean isAnonymous, final ScopeExtent scope, final SimpleType simpleBaseType, final WhiteSpacePolicy whiteSpace, final SrcFrozenLocation location) throws SchemaException
    {
        final SimpleTypeImpl simpleType;
        if (simpleBaseType.isAtomicType())
        {
            final AtomicType atomicBaseType = (AtomicType)simpleBaseType;
            simpleType = new AtomicTypeImpl(name, isAnonymous, scope, atomicBaseType, whiteSpace);
            m_outBag.add(simpleType);
            m_locations.m_simpleTypeLocations.put(simpleType, location);
        }
        else if (simpleBaseType instanceof ListSimpleType)
        {
            final ListSimpleType listBaseListType = (ListSimpleType)simpleBaseType;
            simpleType = new ListTypeImpl(name, isAnonymous, scope, listBaseListType.getItemType(), simpleBaseType, whiteSpace);
            m_outBag.add(simpleType);
            m_locations.m_simpleTypeLocations.put(simpleType, location);
        }
        else if (simpleBaseType instanceof UnionSimpleType)
        {
            final UnionSimpleType unionBaseType = (UnionSimpleType)simpleBaseType;
            simpleType = new UnionTypeImpl(name, isAnonymous, scope, simpleBaseType, unionBaseType.getMemberTypes(), whiteSpace);
            m_outBag.add(simpleType);
            m_locations.m_simpleTypeLocations.put(simpleType, location);
        }
        else if (simpleBaseType.isSimpleUrType())
        {
            throw new SccBaseTypeMustBeSimpleTypeException(name);
        }
        else
        {
            throw new AssertionError(simpleBaseType.getClass());
        }
        return simpleType;
    }

    private ModelGroupUse effectiveContent(final boolean mixed, final XMLParticleWithModelGroupTerm contentModel) throws AbortException, SchemaException
    {
        if (null == contentModel)
        {
            if (mixed)
            {
                final List<SchemaParticle> particles = Collections.emptyList();
                final ModelGroup modelGroup = new ModelGroupImpl(ModelGroup.SmCompositor.Sequence, particles, null, true, ScopeExtent.Local);
                return new ParticleWithModelGroupTerm(1, 1, modelGroup);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return convertModelGroupUse(contentModel);
        }
    }

    private EnumerationDefinition enumeration(final SimpleType type, final SimpleType baseType, final XMLEnumeration sourceEnum) throws SmAttributeUseException
    {
        try
        {
            final SimpleType notationType = m_existingCache.getAtomicType(NativeType.NOTATION);
            final FacetEnumerationImpl impl;
            if (baseType.getName().equals(notationType.getName()) || baseType.derivedFromType(notationType, EnumSet.of(DerivationMethod.Restriction)))
            {
                final PrefixResolver resolver = sourceEnum.getPrefixResolver();
                baseType.validate(sourceEnum.getValue(), resolver, m_atoms);
                impl = new FacetEnumerationImpl(sourceEnum.getValue(), baseType, resolver);
            }
            else
            {
                baseType.validate(sourceEnum.getValue(), m_atoms);
                impl = new FacetEnumerationImpl(sourceEnum.getValue(), baseType, null);
            }
            // we've removed foreign attributes, on the theory that the api for enumeration represents
            // all the possible values, not just one of them. if that's wrong, restore this.
//            copyForeignAttributes(sourceEnum.foreignAttributes, impl);
            return impl;
        }
        catch (final DatatypeException dte)
        {
            final SimpleTypeException ste = new SimpleTypeException(sourceEnum.getValue(), baseType, dte);
            final QName elementName = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, XMLRepresentation.LN_ENUMERATION);
            final QName attributeName = new QName(XMLRepresentation.LN_VALUE);
            throw new SmAttributeUseException(elementName, attributeName, sourceEnum.getLocation(), ste);
        }
    }

    private SimpleType extractSimpleType(final XMLTypeRef typeRef) throws AbortException, SchemaException
    {
        final Type type = convertType(typeRef);
        if (type instanceof SimpleType)
        {
            return (SimpleType)type;
        }
        else if (type instanceof ComplexType)
        {
            final ComplexType complexType = (ComplexType)type;
            final ContentType contentType = complexType.getContentType();
            if (contentType.isSimple())
            {
                return contentType.getSimpleType();
            }
            else if (contentType.isMixed())
            {
                return contentType.getSimpleType();
            }
            else
            {
                throw new AssertionError(contentType.getKind());
            }
        }
        else
        {
            throw new AssertionError(type);
        }
    }

    private Facet fractionDigits(final XMLFractionDigitsFacet xmlFacet) throws SicOversizedIntegerException
    {
        final FacetFractionDigitsImpl impl = new FacetFractionDigitsImpl(getIntValue(xmlFacet.value), xmlFacet.fixed);
        copyForeignAttributes(xmlFacet.foreignAttributes, impl);
        return impl;
    }
    
    /**
     * Converts a BigInteger value to an int value
     * 
     * @param value
     *            the BigInteger to convert
     * @return the int value equivalent of the incoming BigInteger value
     * @throws SicOversizedIntegerException
     *             if value is larger than Integer.MAX_VALUE
     */
    private int getIntValue(final BigInteger value) throws SicOversizedIntegerException
    {
        PreCondition.assertArgumentNotNull(value, "value");
        if (value.compareTo(MAX_INT_SIZE) <= 0)
        {
            return value.intValue();
        }
        else
        {
            throw new SicOversizedIntegerException(value);
        }
    }

    private Facet length(final XMLLength xmlFacet) throws SicOversizedIntegerException
    {
        final FacetImpl impl;
        if (xmlFacet.minLength != null)
        {
            if (xmlFacet.maxLength != null)
            {
                if (xmlFacet.minLength.equals(xmlFacet.maxLength))
                {
                    impl = new FacetLengthImpl(getIntValue(xmlFacet.minLength), xmlFacet.fixed);
                }
                else
                {
                    throw new AssertionError();
                }
            }
            else
            {
                impl = new FacetMinLengthImpl(getIntValue(xmlFacet.minLength), xmlFacet.fixed);
            }
        }
        else
        {
            if (xmlFacet.maxLength != null)
            {
                impl = new FacetMaxLengthImpl(getIntValue(xmlFacet.maxLength), xmlFacet.fixed);
            }
            else
            {
                throw new AssertionError();
            }
        }
        copyForeignAttributes(xmlFacet.foreignAttributes, impl);
        return impl;
    }

    private Limit limit(final String value, final SimpleType simpleType, final FacetKind kind, final boolean isFixed)
    {
        PreCondition.assertArgumentNotNull(value, "value");
        PreCondition.assertArgumentNotNull(simpleType, "simpleType");
        PreCondition.assertArgumentNotNull(kind, "kind");

        if (simpleType.isAtomicType())
        {
            return new FacetValueCompImpl(value, kind, simpleType, isFixed);
        }
        else if (simpleType instanceof ListSimpleType)
        {
            final ListSimpleType listType = (ListSimpleType)simpleType;
            final SimpleType itemType = listType.getItemType();
            if (itemType.isAtomicType())
            {
                final SimpleType atomicType = (SimpleType)itemType;
                return new FacetValueCompImpl(value, kind, atomicType, isFixed);
            }
            else if (itemType instanceof UnionSimpleType)
            {
                // is this a TODO? or does the specification forbid lists of unions?
                throw new UnsupportedOperationException();
            }
            else
            {
                // The specification forbids lists of lists.
                throw new UnsupportedOperationException();
            }
        }
        else if (simpleType instanceof UnionSimpleType)
        {
            // TODO: is a limit on a union forbidden?
            throw new UnsupportedOperationException();
        }
        else
        {
            // Simple Ur-Type? TODO: no limits?
            throw new UnsupportedOperationException();
        }
    }

    private Facet minmax(final XMLMinMaxFacet xmlFacet, final SimpleType baseType) throws SchemaException
    {
        // TODO
        final List<XmlAtom> value;
        {
            final String initialValue = xmlFacet.value;
            try
            {
                value = baseType.validate(initialValue, m_atoms);
            }
            catch (final DatatypeException dte)
            {
                final SimpleTypeException ste = new SimpleTypeException(initialValue, baseType, dte);
                final QName elementName = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, xmlFacet.elementName);
                final QName attributeName = new QName(XMLRepresentation.LN_VALUE);
                final SrcFrozenLocation location = xmlFacet.getLocation();
                throw new SmAttributeUseException(elementName, attributeName, location, ste);
            }
        }
        if (value.size() > 0)
        {
            return limit(m_atoms.getC14NForm(value.get(0)), baseType, xmlFacet.getOperator(), xmlFacet.fixed);
        }
        return null;
    }

    private Pattern pattern(final XMLPatternFacet pattern) throws SmAttributeUseException
    {
        try
        {
            final String regex = pattern.value;
            try
            {
                final RegExPattern regexp = regexc.compile(regex);
                final FacetPatternImpl impl = new FacetPatternImpl(regexp, regex);
                copyForeignAttributes(pattern.foreignAttributes, impl);
                return impl;
            }
            catch (final SchemaRegExCompileException e)
            {
                final DatatypeException dte = new DatatypeException(regex, null);
                throw new SimpleTypeException(regex, null, dte);
            }
        }
        catch (final SimpleTypeException ste)
        {
            final QName elementName = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, XMLRepresentation.LN_PATTERN);
            final QName attributeName = new QName(XMLRepresentation.LN_VALUE);
            throw new SmAttributeUseException(elementName, attributeName, pattern.getLocation(), ste);
        }
    }

    private ContentType simpleContent(final XMLType simpleType, final SimpleType simpleBaseType) throws AbortException, SchemaException
    {
        final QName name;
        final boolean isAnonymous;
        final ScopeExtent scope = convertScope(simpleType.getScope());
        if (scope == ScopeExtent.Global)
        {
            name = simpleType.getName();
            isAnonymous = false;
        }
        else
        {
            name = m_existingCache.generateUniqueName();
            isAnonymous = true;
        }
        final WhiteSpacePolicy whiteSpace = simpleType.getWhiteSpacePolicy();
        final SimpleTypeImpl simpleTypeD = deriveSimpleType(name, isAnonymous, scope, simpleBaseType, whiteSpace, simpleType.getLocation());
        computePatterns(simpleType.getPatternFacets(), simpleTypeD);
        computeFacets(simpleBaseType, simpleType, simpleTypeD);
        computeEnumerations(simpleBaseType, simpleType, simpleTypeD);
        return new ContentTypeImpl(simpleTypeD);
    }

    private Facet totalDigits(final XMLTotalDigitsFacet xmlFacet) throws SicOversizedIntegerException
    {
        final FacetTotalDigitsImpl impl = new FacetTotalDigitsImpl(getIntValue(xmlFacet.value), xmlFacet.fixed);
        copyForeignAttributes(xmlFacet.foreignAttributes, impl);
        return impl;
    }
    
    private void copyForeignAttributes(FAMap source, ForeignAttributesSink target)
    {
        for (QName name : source.keySet())
        {
            target.putForeignAttribute(name, source.get(name));
        }
    }

    public static  Pair<ComponentBagImpl, XMLComponentLocator> convert(final SchemaRegExCompiler regexc, final ComponentProvider rtmCache, final XMLSchemaCache xmlCache, final SchemaExceptionHandler errors) throws AbortException
    {
    	return convert(regexc, rtmCache, xmlCache, errors, false);
    }
    public static  Pair<ComponentBagImpl, XMLComponentLocator> convert(final SchemaRegExCompiler regexc, final ComponentProvider rtmCache, final XMLSchemaCache xmlCache, final SchemaExceptionHandler errors, boolean lastInWins) throws AbortException
    {
        final ComponentBagImpl schema = new ComponentBagImpl();
        final XMLComponentLocator locations = new XMLComponentLocator();

        final XMLSchemaConverter converter = new XMLSchemaConverter(regexc, rtmCache, xmlCache, schema, locations, errors, lastInWins);

        xmlCache.computeSubstitutionGroups();

        converter.convertTypes();
        converter.convertAttributes();
        converter.convertElements();
        converter.convertAttributeGroups();
        converter.convertIdentityConstraints();
        converter.convertModelGroups();
        converter.convertNotations();

        return new Pair<ComponentBagImpl, XMLComponentLocator>(schema, locations);
    }

    static boolean isMaxOccursUnbounded(final BigInteger maxOccurs) throws SicOversizedIntegerException
    {
        PreCondition.assertArgumentNotNull(maxOccurs, "maxOccurs");

        if (XMLParticle.UNBOUNDED.equals(maxOccurs))
        {
            return true;
        }
        else
        {
            if (MAX_INT_SIZE.compareTo(maxOccurs) < 0)
            {
                throw new SicOversizedIntegerException(maxOccurs);
            }
            else
            {
                return false;
            }
        }
    }

    static int maxOccurs(final BigInteger maxOccurs) throws SicOversizedIntegerException
    {
        PreCondition.assertArgumentNotNull(maxOccurs, "maxOccurs");

        if (XMLParticle.UNBOUNDED.equals(maxOccurs))
        {
            throw new IllegalStateException("maxOccurs is unbounded");
        }
        else
        {
            if (MAX_INT_SIZE.compareTo(maxOccurs) < 0)
            {
                throw new SicOversizedIntegerException(maxOccurs);
            }
            else
            {
                return maxOccurs.intValue();
            }
        }
    }

    static int minOccurs(final BigInteger minOccurs) throws SicOversizedIntegerException
    {
        PreCondition.assertArgumentNotNull(minOccurs, "minOccurs");
        if (MAX_INT_SIZE.compareTo(minOccurs) < 0)
        {
            throw new SicOversizedIntegerException(minOccurs);
        }
        else
        {
            PreCondition.assertTrue(minOccurs.compareTo(BigInteger.ZERO) >= 0, "minOccurs >= 0");
            return minOccurs.intValue();
        }
    }

    private static  boolean subtype(final Type lhs, final Type rhs)
    {
        PreCondition.assertArgumentNotNull(lhs, "lhs");
        PreCondition.assertArgumentNotNull(rhs, "rhs");
        if (!rhs.isComplexUrType())
        {
            Type currentType = lhs;
            while (true)
            {
            	if(currentType == rhs)
            	{
            		return true;
            	}
            	if(currentType.getName().equals(rhs.getName()))
            	{
            		return true;
            	}
            	if (!currentType.isComplexUrType())
            	{
            		currentType = currentType.getBaseType();
            	}
            	else
            	{
            		return false;
            	}
            }
        }
        else
        {
            // All item types are derived from the Complex Ur-type.
            return true;
        }
    }

    /**
     * Integer.MAX_VALUE as a BigInteger; needed to ensure that we throw an exception rather than attempt to convert
     * BigInteger values larger than Integer.MAX_VALUE.
     */
    private static final BigInteger MAX_INT_SIZE = BigInteger.valueOf(Integer.MAX_VALUE);

    private final ContentType EMPTY_CONTENT = new ContentTypeImpl();
    private final XMLCycles m_cycles;
    
	private final Stack<QName> m_complexTypeNameCycles = new Stack<QName>();
	private final HashMap<QName,ArrayList<XMLType>> m_lateTypeResolutionMap = new HashMap<QName,ArrayList<XMLType>>();
	private final ArrayList<QName> m_lateTypeResolutionNameList = new ArrayList<QName>();
	
	// key = QName of type, value = list of elements to resolve
	private final HashMap<QName, ArrayList<LateResolveElement>> m_lateElementResolutionMap = new HashMap<QName, ArrayList<LateResolveElement>>();
    

    private final SchemaExceptionHandler m_errors;

    // things that have already been pushed into the component provider in this parse group
    private final ComponentProvider m_existingCache;
    
    private final CanonicalAtomBridge m_atoms;

    // this is the collection of components from the xmlrep package.
    // it needs to be empty when we're done.
    private final XMLSchemaCache m_inCache;

    private final XMLComponentLocator m_locations;

    // this is what we'll return
    private final ComponentBagImpl m_outBag;

    private final SchemaRegExCompiler regexc;
    
    private final boolean m_lastInWins;
    
    // Used only when m_lastInWins is true
    public final Map<QName, ElementDefinition> m_elementsResolvedFromExistingCache = new HashMap<QName, ElementDefinition>();
    public final Map<QName, AttributeDefinition> m_attributesResolvedFromExistingCache = new HashMap<QName, AttributeDefinition>();
    public final Map<QName, Type> m_typesResolvedFromExistingCache = new HashMap<QName, Type>();
    public final Map<QName, ModelGroup> m_modelGroupsResolvedFromExistingCache = new HashMap<QName, ModelGroup>();
    public final Map<QName, AttributeGroupDefinition> m_attributeGroupsResolvedFromExistingCache = new HashMap<QName, AttributeGroupDefinition>();
    public final Map<QName, IdentityConstraint> m_constraintsResolvedFromExistingCache = new HashMap<QName, IdentityConstraint>();
    public final Map<QName, NotationDefinition> m_notationsResolvedFromExistingCache = new HashMap<QName, NotationDefinition>();
    
    final class LateResolveElement {
    	public LateResolveElement(final XMLElement xmlElement, final ElementDeclTypeImpl elementDecl, final ElementDeclTypeImpl subHead)
    	{
    		mi_xmlElement = xmlElement;
    		mi_elementDecl = elementDecl;
    		mi_subHead = subHead;
    	}
    	final XMLElement mi_xmlElement;
    	final ElementDeclTypeImpl mi_elementDecl;
    	final ElementDeclTypeImpl mi_subHead;
    }
}
