/*
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
package org.genxdm.processor.w3c.xs.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.misc.StringToURIParser;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcUnexpectedAttributeException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException;
import org.genxdm.processor.w3c.xs.exception.sm.SmAttributeDefaultAndUseImpliesOptionalException;
import org.genxdm.processor.w3c.xs.exception.sm.SmAttributeRefPresentException;
import org.genxdm.processor.w3c.xs.exception.sm.SmAttributeRefXorNameException;
import org.genxdm.processor.w3c.xs.exception.sm.SmAttributeUseException;
import org.genxdm.processor.w3c.xs.exception.sm.SmComplexTypeException;
import org.genxdm.processor.w3c.xs.exception.sm.SmDuplicateIDException;
import org.genxdm.processor.w3c.xs.exception.sm.SmElementRefPresentException;
import org.genxdm.processor.w3c.xs.exception.sm.SmElementRefXorNameException;
import org.genxdm.processor.w3c.xs.exception.sm.SmElementSimpleTypeXorComplexTypeException;
import org.genxdm.processor.w3c.xs.exception.sm.SmIllegalNamespaceException;
import org.genxdm.processor.w3c.xs.exception.sm.SmImportNotWellFormedException;
import org.genxdm.processor.w3c.xs.exception.sm.SmInclusionNamespaceMismatchException;
import org.genxdm.processor.w3c.xs.exception.sm.SmInclusionNotWellFormedException;
import org.genxdm.processor.w3c.xs.exception.sm.SmMissingAttributeException;
import org.genxdm.processor.w3c.xs.exception.sm.SmRedefineTypeSelfReferenceException;
import org.genxdm.processor.w3c.xs.exception.sm.SmRedefinitionNamespaceMismatchException;
import org.genxdm.processor.w3c.xs.exception.sm.SmRedefinitionNotWellFormedException;
import org.genxdm.processor.w3c.xs.exception.sm.SmSimpleTypeListException;
import org.genxdm.processor.w3c.xs.exception.sm.SmSimpleTypeRestrictionException;
import org.genxdm.processor.w3c.xs.exception.sm.SmSimpleTypeUnionException;
import org.genxdm.processor.w3c.xs.exception.sm.SmSourceAttributeDefaultAndFixedPresentException;
import org.genxdm.processor.w3c.xs.exception.sm.SmTopLevelSchemaNotWellFormedException;
import org.genxdm.processor.w3c.xs.exception.sm.SmUnexpectedElementException;
import org.genxdm.processor.w3c.xs.exception.sm.SmUnexpectedEndException;
import org.genxdm.processor.w3c.xs.exception.src.SrcAttributeTypeAndSimpleTypePresentException;
import org.genxdm.processor.w3c.xs.exception.src.SrcPrefixNotFoundException;
import org.genxdm.processor.w3c.xs.xmlrep.XMLAttributeUse;
import org.genxdm.processor.w3c.xs.xmlrep.XMLCardinality;
import org.genxdm.processor.w3c.xs.xmlrep.XMLContentTypeKind;
import org.genxdm.processor.w3c.xs.xmlrep.XMLRepresentation;
import org.genxdm.processor.w3c.xs.xmlrep.XMLSchemaCache;
import org.genxdm.processor.w3c.xs.xmlrep.XMLSchemaModule;
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
import org.genxdm.processor.w3c.xs.xmlrep.exceptions.XMLAttributeException;
import org.genxdm.processor.w3c.xs.xmlrep.exceptions.XMLAttributeGroupException;
import org.genxdm.processor.w3c.xs.xmlrep.exceptions.XMLComplexTypeException;
import org.genxdm.processor.w3c.xs.xmlrep.exceptions.XMLCompositorOutsideGroupException;
import org.genxdm.processor.w3c.xs.xmlrep.exceptions.XMLElementException;
import org.genxdm.processor.w3c.xs.xmlrep.exceptions.XMLFieldException;
import org.genxdm.processor.w3c.xs.xmlrep.exceptions.XMLIdentityConstraintException;
import org.genxdm.processor.w3c.xs.xmlrep.exceptions.XMLModelGroupException;
import org.genxdm.processor.w3c.xs.xmlrep.exceptions.XMLModelGroupUseException;
import org.genxdm.processor.w3c.xs.xmlrep.exceptions.XMLNotationException;
import org.genxdm.processor.w3c.xs.xmlrep.exceptions.XMLSelectorException;
import org.genxdm.processor.w3c.xs.xmlrep.exceptions.XMLSimpleTypeException;
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
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.constraints.IdentityConstraintKind;
import org.genxdm.xs.constraints.NamespaceConstraint;
import org.genxdm.xs.constraints.RestrictedXPath;
import org.genxdm.xs.constraints.RestrictedXPathParser;
import org.genxdm.xs.constraints.ValueConstraint;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ProcessContentsMode;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.exceptions.SchemaException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.exceptions.SimpleTypeException;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.resolve.CatalogResolver;
import org.genxdm.xs.resolve.SchemaCatalog;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;

/**
 * Implementation Notes:
 * <ul>
 * <li>Distinguish between global declarations and local declarations (the latter are really particles).</li>
 * <li>The processing context is used to generate anonymous type names and to retrieve references to native W3C XML
 * Schema types.</li>
 * <li>Maximise the feedback from parsing by choosing when to throw/catch
 * </ul>
 */
final class XMLSchemaParser extends XMLRepresentation
{
    public XMLSchemaParser(final ComponentProvider bootstrap, final SchemaExceptionHandler errors, final SchemaCatalog catalog, final CatalogResolver resolver, boolean processRepeatedNamespaces)
    {
        this.bootstrap = PreCondition.assertArgumentNotNull(bootstrap, "bootstrap");
        this.atoms = new CanonicalAtomBridge(bootstrap);
        this.m_pms = new PrefixMappingSupport();
        this.m_errors = PreCondition.assertArgumentNotNull(errors, "errors");
        this.m_catalog = catalog;
        this.m_resolver = resolver;
        this.m_processRepeatedNamespaces = processRepeatedNamespaces;
        this.m_xp = new DefaultRestrictedXPathParser(bootstrap);
        ANY_SIMPLE_TYPE = new XMLTypeRef(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anySimpleType"));
        ANY_TYPE = new XMLTypeRef(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anyType"));
    }

    public void parse(final String systemId, final InputStream istream, final XMLSchemaCache cache, final XMLSchemaModule module) throws AbortException
    {
        PreCondition.assertArgumentNotNull(cache, "cache");
        PreCondition.assertArgumentNotNull(module, "module");

        if(!module.isChameleon())
        {
            if (cache.m_seenSystemIds.contains(systemId))
            {
                return;
            }
            else
            {
            	// If this schema doesn't have a targetNamespace, add it the chameleon list so we don't reparse it
            	// into the no namespace.
            	if(XMLConstants.NULL_NS_URI.equals(module.computeTargetNamespace()))
            	{
            		HashSet<String> tnsSet = cache.m_seenChameleonsLocation2Tns.get(systemId);
            		if(tnsSet != null)
            		{
            			tnsSet.add(XMLConstants.NULL_NS_URI);
            		}
            		else
            		{
            			final HashSet<String> newList = new HashSet<String>();
            			newList.add(XMLConstants.NULL_NS_URI);
            			cache.m_seenChameleonsLocation2Tns.put(systemId, newList);
            		}
            	}
            	cache.m_seenSystemIds.add(systemId);
            }
        }
        else
        {
        	// For chameleons, we only want to parse them into a particular namespace once.  Subsequent
        	// request to parse them into that namespace should be ignored (i.e. we're going to return from this code block).
    		String computedTns = module.computeTargetNamespace();
        	HashSet<String> tnsSet = cache.m_seenChameleonsLocation2Tns.get(systemId);
        	if(tnsSet != null)
        	{
        		if(tnsSet.contains(computedTns))
        		{
        			// Stop parsing.  We've already parsed this chameleon into this namespace.
        			return;
        		}
        		else
        		{
        			tnsSet.add(computedTns);
        		}
        	}
        	else
        	{
        		final HashSet<String> newList = new HashSet<String>();
        		newList.add(computedTns);
        		cache.m_seenChameleonsLocation2Tns.put(systemId, newList);
        	}
        }

        final XMLInputFactory factory = XMLInputFactory.newInstance();

        // this pattern of instantiation will catch the idiotic cases in which
        // an implementation of XMLInputFactory attempts to turn a supplied URI
        // into a URL, which is not always possible (for instance, urn-s).
        XMLStreamReader reader = null;
        boolean tryNull = (systemId == null);
        if (systemId != null)
        {
            try
            {
                reader = factory.createXMLStreamReader(systemId.toString(), istream);
            }
            catch (final XMLStreamException xse)
            {
                // ignore it this time around; try again:
                tryNull = true;
            }
        }
        if (tryNull)
        {
            try
            {
                reader = factory.createXMLStreamReader(null, istream);
            }
            catch (final XMLStreamException xse)
            {
                // TODO: figure out what happens.  following comment was in received code.
                // I'm not sure what has happened here, but it doesn't fit into the
                // category of not
                // being well formed XML. Perhaps it's not XML at all. We'll throw
                // this assertion and deal
                // with it more accurately when we know more.
                throw new GenXDMException(xse);
            }
        }
        
        try
        {
            boolean done = false;

            while (!done)
            {
                final int event = reader.next();

                switch (event)
                {
                    case XMLStreamConstants.END_DOCUMENT:
                    {
                        reader.close();
                        done = true;
                    }
                    break;
                    case XMLStreamConstants.START_ELEMENT:
                    {
                        m_pms.pushContext();
                        try
                        {
                            copyNamespaces(reader, m_pms);
                            if (isWXS(reader.getNamespaceURI()))
                            {
                                final String localName = reader.getLocalName();
                                if (LN_SCHEMA.equals(localName))
                                {
                                    schemaTag(reader, cache, module);
                                }
                                else
                                {
                                    reportUnexpectedElementTag("document", reader.getName(), reader.getLocation());
                                    skipTag(reader);
                                }
                            }
                            else
                            {
                                reportUnexpectedElementTag("document", reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        finally
                        {
                            m_pms.popContext();
                        }
                    }
                    break;
                    default:
                    {
                        // ignore
                    }
                }
            }
        }
        catch (final XMLStreamException e)
        {
            if (module.isImport())
            {
                m_errors.error(new SmImportNotWellFormedException(getFrozenLocation(reader.getLocation())));
            }
            else if (module.isInclude())
            {
                m_errors.error(new SmInclusionNotWellFormedException(getFrozenLocation(reader.getLocation())));
            }
            else if (module.isRedefine())
            {
                m_errors.error(new SmRedefinitionNotWellFormedException(getFrozenLocation(reader.getLocation())));
            }
            else
            // must be top level schema
            {
                m_errors.error(new SmTopLevelSchemaNotWellFormedException(getFrozenLocation(reader.getLocation())));
            }
        }
    }

    private void annotationContent(final String contextName, final XMLStreamReader reader, final XMLSchemaModule module) throws XMLStreamException, AbortException
    {
        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, contextName, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                {
                    final String text = reader.getText();
                    if (!isWhiteSpace(text))
                    {
                        reportUnexpectedNonWhiteSpaceTextInElementOnlyContent(contextName, text, reader.getLocation());
                    }
                }
                break;
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    private void annotationTag(final XMLStreamReader reader, final XMLSchemaModule module) throws XMLStreamException, AbortException
    {
        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // ignore foreign attributes?
                // {any attributes with non-schema namespace}
            }
        }

        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if ("documentation".equals(localName))
                            {
                                documentationTag(reader);
                            }
                            else if ("appinfo".equals(localName))
                            {
                                appinfoTag(reader);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_ANNOTATION, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    /**
     * xs:anyAttribute.
     */
    private XMLWildcard anyAttributeTag(final XMLStreamReader reader, final String targetNamespace, final XMLSchemaModule module) throws XMLStreamException, AbortException
    {
        ProcessContentsMode processContents = ProcessContentsMode.Strict;
        NamespaceConstraint namespaceConstraint = NamespaceConstraint.Any();

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if (LN_NAMESPACE.equals(localName))
                {
                    try
                    {
                        namespaceConstraint = namespaces(reader.getAttributeValue(i), targetNamespace);
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_PROCESS_CONTENTS.equals(localName))
                {
                    try
                    {
                        processContents = processContents(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_ANY_ATTRIBUTE, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_ANY_ATTRIBUTE, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        XMLWildcard result = new XMLWildcard(processContents, namespaceConstraint);
        result.foreignAttributes.putAll(foreignAttributes);
        foreignAttributes.clear();
        return result;
    }

    /**
     * xs:any
     */
    private XMLParticle anyElementTag(final XMLStreamReader reader, final String targetNamespace, final XMLSchemaModule module) throws XMLStreamException, AbortException
    {
        ProcessContentsMode processContents = ProcessContentsMode.Strict;
        NamespaceConstraint namespaceConstraint = NamespaceConstraint.Any();

        BigInteger minOccurs = BigInteger.ONE;
        BigInteger maxOccurs = BigInteger.ONE;

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if (LN_MAX_OCCURS.equals(localName))
                {
                    maxOccurs = maxOccurs(reader.getAttributeValue(i), true, reader.getLocation(), reader.getName());
                }
                else if (LN_MIN_OCCURS.equals(localName))
                {
                    minOccurs = minOccurs(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
                }
                else if (LN_NAMESPACE.equals(localName))
                {
                    try
                    {
                        namespaceConstraint = namespaces(reader.getAttributeValue(i), targetNamespace);
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_PROCESS_CONTENTS.equals(localName))
                {
                    try
                    {
                        processContents = processContents(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_ANY, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_ANY, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        final XMLWildcard wildcard = new XMLWildcard(processContents, namespaceConstraint);
        wildcard.foreignAttributes.putAll(foreignAttributes);
        foreignAttributes.clear();
        return new XMLParticleWithWildcardTerm(minOccurs, maxOccurs, wildcard, getFrozenLocation(reader.getLocation()));
    }

    private void appinfoTag(final XMLStreamReader reader) throws XMLStreamException, AbortException
    {
        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_SOURCE.equals(localName))
                {
                    /* final String source = */reader.getAttributeValue(i);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // foreign attributes not allowed on appinfo (I think)
                // {any attributes with non-schema namespace}
            }
        }

        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        skipTag(reader);
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    private void assertRefAbsent(final QName ref, final Location location) throws AbortException
    {
        if (null != ref)
        {
            m_errors.error(new SmElementRefPresentException(getFrozenLocation(location)));
        }
    }

    /**
     * xs:attributeGroup (reference)
     */
    private XMLAttributeGroup attribGroupRefTag(final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final String targetNamespace) throws XMLStreamException, XMLAttributeGroupException, AbortException
    {
        final QName ref;
        try
        {
            ref = requiredQName(LN_REF, module.isChameleon(), targetNamespace, reader);
        }
        catch (final SmComplexTypeException e)
        {
            skipTag(reader);
            throw new XMLAttributeGroupException(e);
        }

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if (LN_REF.equals(localName))
                {
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        final XMLAttributeGroup attributeGroup;
        try
        {
            attributeGroup = cache.dereferenceAttributeGroup(ref, reader.getLocation(), false);
        }
        catch (final SchemaException e)
        {
            skipTag(reader);
            throw new XMLAttributeGroupException(e);
        }
        attributeGroup.foreignAttributes.putAll(foreignAttributes);
        foreignAttributes.clear();
        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_ATTRIBUTE_GROUP, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_ATTRIBUTE_GROUP, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                    // Ignore.
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }

        return attributeGroup;
    }

    /**
     * xs:attributeGroup (global definition)
     */
    private XMLAttributeGroup attribGroupTag(final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace) throws XMLStreamException, XMLAttributeGroupException, AbortException
    {
        final XMLAttributeGroup attributeGroup;
        final LinkedList<XMLAttributeUse> savedLocalAttributes;
        final LinkedList<XMLAttributeGroup> savedReferencedAttributeGroups;
        final XMLWildcard savedWildcard;
        final HashSet<QName> savedProhibited;
        if (!redefine)
        {
            try
            {
                attributeGroup = cache.registerAttributeGroup(requiredNCName(LN_NAME, targetNamespace, reader), getFrozenLocation(reader.getLocation()));
            }
            catch (final SchemaException e)
            {
                skipTag(reader);
                throw new XMLAttributeGroupException(e);
            }
            savedLocalAttributes = null;
            savedReferencedAttributeGroups = null;
            savedWildcard = null;
            savedProhibited = null;
        }
        else
        {
            try
            {
                attributeGroup = cache.dereferenceAttributeGroup(requiredNCName(LN_NAME, targetNamespace, reader), reader.getLocation(), true/*
                                                                                                                                             * must
                                                                                                                                             * exist
                                                                                                                                             */);
            }
            catch (final SchemaException e)
            {
                skipTag(reader);
                throw new XMLAttributeGroupException(e);
            }

            savedLocalAttributes = new LinkedList<XMLAttributeUse>(attributeGroup.getAttributeUses());
            attributeGroup.getAttributeUses().clear();

            savedReferencedAttributeGroups = new LinkedList<XMLAttributeGroup>(attributeGroup.getGroups());
            attributeGroup.getGroups().clear();

            savedProhibited = new HashSet<QName>(attributeGroup.prohibited);
            attributeGroup.prohibited.clear();

            savedWildcard = attributeGroup.wildcard;
            attributeGroup.wildcard = null;
        }

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if (LN_NAME.equals(localName))
                {
                    // Already known.
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                attributeGroup.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_ATTRIBUTE_GROUP, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else if (LN_ATTRIBUTE.equals(localName))
                            {
                                attributeLocalTag(reader, cache, module, redefine, targetNamespace, attributeGroup.getAttributeUses(), attributeGroup.prohibited, new XMLScope(attributeGroup));
                                firstElement = false;
                            }
                            else if (LN_ATTRIBUTE_GROUP.equals(localName))
                            {
                                final XMLAttributeGroup ag = attribGroupRefTag(reader, cache, module, targetNamespace);
                                if (!redefine)
                                {
                                    attributeGroup.getGroups().add(ag);
                                }
                                else
                                {
                                    if (attributeGroup == ag)
                                    {
                                        for (final XMLAttributeUse attributeUse : savedLocalAttributes)
                                        {
                                            attributeGroup.getAttributeUses().add(attributeUse);
                                        }
                                        for (final XMLAttributeGroup reference : savedReferencedAttributeGroups)
                                        {
                                            attributeGroup.getGroups().add(reference);
                                        }
                                        for (final QName name : savedProhibited)
                                        {
                                            attributeGroup.prohibited.add(name);
                                        }
                                        attributeGroup.wildcard = savedWildcard;
                                    }
                                    else
                                    {
                                        attributeGroup.getGroups().add(ag);
                                    }
                                }
                                firstElement = false;
                            }
                            else if (LN_ANY_ATTRIBUTE.equals(localName))
                            {
                                attributeGroup.wildcard = anyAttributeTag(reader, targetNamespace, module);
                                firstElement = false;
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_ATTRIBUTE_GROUP, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                    // Ignore.
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }

        if (!redefine)
        {
            return attributeGroup;
        }
        else
        {
            // This would be a copy of the original.
            return null;
        }
    }

    /**
     * xs:attribute (reference or local definition)
     */
    private void attributeLocalTag(final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace, final LinkedList<XMLAttributeUse> attributeUses, final HashSet<QName> prohibited, final XMLScope scope) throws XMLStreamException, AbortException
    {
        String name = null;
        XMLTypeRef type = null;
        final QName ref = referenceOptional(reader, LN_REF, module, targetNamespace);
        XMLCardinality use = XMLCardinality.OPTIONAL;
        boolean qualified = module.attributeQualified;
        XMLValueConstraint valueConstraint = null;
        String id = null;

        boolean seenForm = false;
        boolean seenDefault = false;

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_DEFAULT.equals(localName))
                {
                    seenDefault = true;
                    if (null == valueConstraint)
                    {
                        valueConstraint = new XMLValueConstraint(ValueConstraint.Kind.Default, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
                    }
                    else
                    {
                        m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
                    }
                }
                else if (LN_FIXED.equals(localName))
                {
                    if (null == valueConstraint)
                    {
                        valueConstraint = new XMLValueConstraint(ValueConstraint.Kind.Fixed, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
                    }
                    else
                    {
                        m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
                    }
                }
                else if (LN_FORM.equals(localName))
                {
                    seenForm = true;
                    try
                    {
                        qualified = qualified(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if (LN_NAME.equals(localName))
                {
                    try
                    {
                        name = name(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_REF.equals(localName))
                {
                }
                else if (LN_TYPE.equals(localName))
                {
                    try
                    {
                        type = typeRef(reader.getAttributeValue(i), LN_TYPE, module.isChameleon(), targetNamespace, reader);
                        ensureReferenceType(type.getName(), reader.getLocation(), false, cache);
                    }
                    catch (final SmComplexTypeException e)
                    {
                        m_errors.error(e);
                    }
                }
                else if (LN_USE.equals(localName))
                {
                    try
                    {
                        use = use(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        // If default and use are both present, use must have the actual value
        // optional.
        if (seenDefault)
        {
            if (use.getMinOccurs() != 0 || use.getMaxOccurs() != 1)
            {
                m_errors.error(new SmAttributeDefaultAndUseImpliesOptionalException(getFrozenLocation(reader.getLocation())));
            }
        }

        // The following test only applies to local attributes.
        if ((null != ref) && (seenForm || (null != type)))
        {
            m_errors.error(new SmAttributeRefPresentException(getFrozenLocation(reader.getLocation())));
        }

        final XMLAttribute attribute;
        try
        {
            attribute = determineLocalAttribute(name, qualified, ref, cache, reader, targetNamespace, scope);
        }
        catch (final SchemaException e)
        {
            m_errors.error(e);
            skipTag(reader);
            return;
        }
        attribute.foreignAttributes.putAll(foreignAttributes);
        foreignAttributes.clear();
        attribute.id = id;
        if (null != type)
        {
            attribute.typeRef = type;
        }

        final ContentModelMachine<String> machine = new ContentModelMachine<String>(attributeTable, EPSILON);
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (!machine.step(localName))
                            {
                                reportUnexpectedElementTag(LN_ATTRIBUTE, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                            else
                            {
                                if (LN_SIMPLE_TYPE.equals(localName))
                                {
                                    if (null != ref)
                                    {
                                        m_errors.error(new SmAttributeRefPresentException(getFrozenLocation(reader.getLocation())));
                                    }
                                    if (null != type)
                                    {
                                        m_errors.error(new SrcAttributeTypeAndSimpleTypePresentException(getFrozenLocation(reader.getLocation())));
                                    }
                                    attribute.typeRef = simpleTypeLocalTag(new XMLScope(attribute), cache, module, reader, redefine, targetNamespace);
                                }
                                else if (LN_ANNOTATION.equals(localName))
                                {
                                    annotationTag(reader, module);
                                }
                                else
                                {
                                    throw new AssertionError(reader.getName());
                                }
                            }
                        }
                        else
                        {
                            skipTag(reader);
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    if (!machine.end())
                    {
                        reportUnexpectedEnd(LN_ATTRIBUTE, reader.getLocation());
                    }
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                    // Ignore.
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }

        final boolean forbidden = (use.getMaxOccurs() > 0);
        if (forbidden)
        {
            final boolean required = (use.getMinOccurs() > 0);
            final XMLAttributeUse attributeUse = new XMLAttributeUse(required, attribute, valueConstraint);

            attributeUses.add(attributeUse);
        }
        else
        {
            // The attribute use is forbidden.
            prohibited.add(attribute.getName());
        }
    }

    /**
     * xs:attribute (global definition).
     */
    private XMLAttribute attributeTag(final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final String targetNamespace) throws XMLStreamException, XMLAttributeException, AbortException
    {
        final XMLAttribute attribute;
        try
        {
            attribute = cache.registerAttribute(requiredNCName(LN_NAME, targetNamespace, reader), getFrozenLocation(reader.getLocation()));
        }
        catch (final SchemaException e)
        {
            skipTag(reader);
            throw new XMLAttributeException(e);
        }

        boolean missingTypeAttribute = true;
        // boolean seenDefault = false;

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_NAME.equals(localName))
                {
                    // Already known.
                }
                else if (LN_TYPE.equals(localName))
                {
                    missingTypeAttribute = false;
                    try
                    {
                        attribute.typeRef = typeRef(reader.getAttributeValue(i), LN_TYPE, module.isChameleon(), targetNamespace, reader);
                        ensureReferenceType(attribute.typeRef.getName(), reader.getLocation(), false, cache);
                    }
                    catch (final SmComplexTypeException e)
                    {
                        m_errors.error(e);
                    }
                }
                else if (LN_DEFAULT.equals(localName))
                {
                    // seenDefault = true;
                    if (null == attribute.m_valueConstraint)
                    {
                        attribute.m_valueConstraint = new XMLValueConstraint(ValueConstraint.Kind.Default, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
                    }
                    else
                    {
                        m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
                    }
                }
                else if (LN_FIXED.equals(localName))
                {
                    if (null == attribute.m_valueConstraint)
                    {
                        attribute.m_valueConstraint = new XMLValueConstraint(ValueConstraint.Kind.Fixed, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
                    }
                    else
                    {
                        m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    attribute.id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                attribute.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_SIMPLE_TYPE.equals(localName))
                            {
                                if (missingTypeAttribute)
                                {
                                    attribute.typeRef = simpleTypeLocalTag(new XMLScope(attribute), cache, module, reader, false, targetNamespace);
                                }
                                else
                                {
                                    m_errors.error(new SrcAttributeTypeAndSimpleTypePresentException(getFrozenLocation(reader.getLocation())));
                                }
                                firstElement = false;
                            }
                            else if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_ATTRIBUTE, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_ATTRIBUTE, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                    // Ignore.
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }

        return attribute;
    }

    /**
     * This function is common to extension and restriction of simple content.
     */
    private QName baseTypeDefinitionInComplexContent(final XMLType complexType, final DerivationMethod derivation, final XMLStreamReader reader, final boolean redefine, final XMLSchemaCache cache, final XMLSchemaModule module, final String targetNamespace) throws SchemaException, AbortException
    {
        final QName baseName = requiredQName(LN_BASE, module.isChameleon(), targetNamespace, reader);
        ensureReferenceType(baseName, reader.getLocation(), redefine, cache);
        // The {base type definition} for the Complex Type is the type
        // definition resolved by the
        // actual value of the base attribute. This could be a forward reference
        // so we use the QName.
        complexType.setBase(new XMLTypeRef(baseName), derivation);
        return baseName;
    }

    /**
     * Used to ensure that a child xs:annotation occurs a maximumn number of once.
     */
    private boolean checkAnnotationMaxOccursUnity(final boolean allowed, final String contextName, final Location location) throws AbortException
    {
        return checkWxsElementMaxOccursUnity(allowed, contextName, LN_ANNOTATION, location);
    }

    /**
     * Validate the xs:ID attribute and return the value as a String. <br/>
     * We assume that the name of the attribute is {@link #LN_ID} for reporting purposes.
     * 
     * @param attributeValue
     *            The value of the xs:ID attribute.
     * @param location
     *            The stream location of the xs:ID attribute.
     * @param elementName
     *            The name of the element bearing the xs:ID attribute.
     * @param module
     *            The module being parsed - used to record the xs:ID values to ensure uniqueness.
     * @return The xs:ID value as a String.
     */
    private String checkID(final String attributeValue, final Location location, final QName elementName, final XMLSchemaModule module) throws AbortException
    {
        try
        {
            return checkIDValue(attributeValue, location, module);
        }
        catch (final SimpleTypeException e)
        {
            reportAttributeUseError(elementName, new QName(LN_ID), location, e);
        }
        catch (final SmDuplicateIDException e)
        {
            m_errors.error(e);
        }
        // It's acceptable to return null because we aren't going to do anything
        // significant with id.
        return null;
    }

    private String checkIDValue(final String strval, final Location location, final XMLSchemaModule module) throws SimpleTypeException, SmDuplicateIDException
    {
        PreCondition.assertArgumentNotNull(strval, LN_ID);

        final SimpleType idType = bootstrap.getAtomicType(NativeType.ID);

        final List<XmlAtom> value;
        try
        {
            value = idType.validate(strval, atoms);
        }
        catch (DatatypeException dte)
        {
            throw new SimpleTypeException(strval, idType, dte);
        }
        if (value.size() > 0)
        {
            final String id = atoms.getString(value.get(0));
            if (module.m_ids.contains(id))
                throw new SmDuplicateIDException(id, new SrcFrozenLocation(location));
            module.m_ids.add(id);
            return id;
        }
        return null;
    }

    private void checkPrefixBound(final String prefix, final String namespaceURI, final String initialValue) throws SimpleTypeException
    {
        if (!isBoundPrefix(prefix, namespaceURI))
        {
            final SrcPrefixNotFoundException cause = new SrcPrefixNotFoundException(prefix);
            final DatatypeException dte = new DatatypeException(initialValue, null, cause);
            throw new SimpleTypeException(initialValue, null, dte);
        }
    }

    /**
     * Used to ensure that a particular child element occurs a maximum number of once.
     */
    private boolean checkWxsElementMaxOccursUnity(final boolean missing, final String contextName, final String unexpectedName, final Location location) throws AbortException
    {
        if (!missing)
        {
            reportUnexpectedElementTag(contextName, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, unexpectedName), location);
        }
        return false;
    }

    /**
     * xs:complexContent <br/>
     * We don't return anything because this affects multiple aspects of the complex type.
     */
    private void complexContentTag(final XMLType complexType, final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace) throws XMLStreamException, AbortException
    {
        PreCondition.assertArgumentNotNull(complexType, LN_COMPLEX_TYPE);

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if (LN_MIXED.equals(localName))
                {
                    try
                    {
                        if (trueOrFalse(reader.getAttributeValue(i)))
                        {
                            complexType.m_contentKind = XMLContentTypeKind.Mixed;
                        }
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // complex content tag disappears on parse, so no foreign attributes remain.
                // {any attributes with non-schema namespace}
            }
        }

        final ContentModelMachine<String> machine = new ContentModelMachine<String>(complexContentTable, EPSILON);
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (!machine.step(localName))
                            {
                                reportUnexpectedElementTag(LN_COMPLEX_CONTENT, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                            else
                            {
                                if (LN_EXTENSION.equals(localName))
                                {
                                    extensionInComplexContentTag(complexType, reader, cache, module, redefine, targetNamespace);
                                }
                                else if (LN_RESTRICTION.equals(localName))
                                {
                                    restrictionInComplexContentTag(complexType, reader, cache, module, redefine, targetNamespace);
                                }
                                else if (LN_ANNOTATION.equals(localName))
                                {
                                    annotationTag(reader, module);
                                }
                                else
                                {
                                    throw new AssertionError(reader.getName());
                                }
                            }
                        }
                        else
                        {
                            skipTag(reader);
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    if (!machine.end())
                    {
                        reportUnexpectedEnd(LN_COMPLEX_CONTENT, reader.getLocation());
                    }
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    /**
     * This does not correspond to a specific tag. <br/>
     * Used to parse the content of xs:complexType (global and local), but not the same as xs:complexContent.
     */
    private void complexTypeContent(final XMLType complexType, final XMLSchemaCache cache, final XMLSchemaModule module, final XMLStreamReader reader, final boolean redefine, final String targetNamespace) throws XMLStreamException, AbortException
    {
        final ContentModelMachine<String> machine = new ContentModelMachine<String>(complexTypeTable, EPSILON);
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (!machine.step(localName))
                            {
                                reportUnexpectedElementTag(LN_COMPLEX_TYPE, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                            else
                            {
                                if (LN_GROUP.equals(localName))
                                {
                                    complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
                                    try
                                    {
                                        complexType.m_contentModel = groupParticleTag(new XMLScope(complexType), reader, cache, module, redefine, targetNamespace);
                                    }
                                    catch (final XMLModelGroupUseException e)
                                    {
                                        m_errors.error(e.getCause());
                                    }
                                }
                                else if (LN_ALL.equals(localName))
                                {
                                    complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
                                    try
                                    {
                                        complexType.m_contentModel = compositorOutsideGroupTag(ModelGroup.SmCompositor.All, new XMLScope(complexType), localName, reader, cache, module, redefine, targetNamespace);
                                    }
                                    catch (final XMLCompositorOutsideGroupException e)
                                    {
                                        m_errors.error(e.getCause());
                                    }
                                }
                                else if (LN_CHOICE.equals(localName))
                                {
                                    complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
                                    try
                                    {
                                        complexType.m_contentModel = compositorOutsideGroupTag(ModelGroup.SmCompositor.Choice, new XMLScope(complexType), localName, reader, cache, module, redefine, targetNamespace);
                                    }
                                    catch (final XMLCompositorOutsideGroupException e)
                                    {
                                        m_errors.error(e.getCause());
                                    }
                                }
                                else if (LN_SEQUENCE.equals(localName))
                                {
                                    complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
                                    try
                                    {
                                        complexType.m_contentModel = compositorOutsideGroupTag(ModelGroup.SmCompositor.Sequence, new XMLScope(complexType), localName, reader, cache, module, redefine, targetNamespace);
                                    }
                                    catch (final XMLCompositorOutsideGroupException e)
                                    {
                                        m_errors.error(e.getCause());
                                    }
                                }
                                else if (LN_ATTRIBUTE.equals(localName))
                                {
                                    attributeLocalTag(reader, cache, module, redefine, targetNamespace, complexType.getAttributeUses(), complexType.prohibited, new XMLScope(complexType));
                                }
                                else if (LN_ATTRIBUTE_GROUP.equals(localName))
                                {
                                    try
                                    {
                                        complexType.getAttributeGroups().add(attribGroupRefTag(reader, cache, module, targetNamespace));
                                    }
                                    catch (final XMLAttributeGroupException e)
                                    {
                                        m_errors.error(e.getCause());
                                    }
                                }
                                else if (LN_ANY_ATTRIBUTE.equals(localName))
                                {
                                    complexType.attributeWildcard = anyAttributeTag(reader, targetNamespace, module);
                                }
                                else if (LN_COMPLEX_CONTENT.equals(localName))
                                {
                                    complexContentTag(complexType, reader, cache, module, redefine, targetNamespace);
                                }
                                else if (LN_SIMPLE_CONTENT.equals(localName))
                                {
                                    simpleContentTag(complexType, reader, cache, module, redefine, targetNamespace);
                                }
                                else if (LN_ANNOTATION.equals(localName))
                                {
                                    annotationTag(reader, module);
                                }
                                else
                                {
                                    throw new AssertionError(reader.getName());
                                }
                            }
                        }
                        else
                        {
                            skipTag(reader);
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    if (!machine.end())
                    {
                        reportUnexpectedEnd(LN_COMPLEX_TYPE, reader.getLocation());
                    }
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                    // Ignore
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    /**
     * xs:complexType (global definition)
     */
    private XMLType complexTypeGlobalTag(final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace) throws XMLStreamException, XMLComplexTypeException, AbortException
    {
        final XMLType complexType;
        if (!redefine)
        {
            try
            {
                complexType = cache.registerType(requiredNCName(LN_NAME, targetNamespace, reader), getFrozenLocation(reader.getLocation()));
            }
            catch (final SchemaException e)
            {
                skipTag(reader);
                throw new XMLComplexTypeException(e);
            }

            complexType.setComplexFlag();
            complexType.setBase(ANY_TYPE, DerivationMethod.Restriction);
            complexType.getBlock().addAll(module.blockDefault);
        }
        else
        {
            try
            {
                complexType = cache.dereferenceType(requiredNCName(LN_NAME, targetNamespace, reader), reader.getLocation(), redefine);
            }
            catch (final SchemaException e)
            {
                skipTag(reader);
                throw new XMLComplexTypeException(e);
            }
        }

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_NAME.equals(localName))
                {
                    // Already known.
                }
                else if (LN_MIXED.equals(localName))
                {
                    try
                    {
                        if (trueOrFalse(reader.getAttributeValue(i)))
                        {
                            complexType.m_contentKind = XMLContentTypeKind.Mixed;
                        }
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_ABSTRACT.equals(localName))
                {
                    try
                    {
                        complexType.setAbstractFlag(trueOrFalse(reader.getAttributeValue(i)));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_BLOCK.equals(localName))
                {
                    try
                    {
                        control(reader.getAttributeValue(i), EnumSet.of(DerivationMethod.Extension, DerivationMethod.Restriction), complexType.getBlock());
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_FINAL.equals(localName))
                {
                    try
                    {
                        control(reader.getAttributeValue(i), EnumSet.of(DerivationMethod.Extension, DerivationMethod.Restriction), complexType.getFinal());
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                complexType.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        complexTypeContent(complexType, cache, module, reader, redefine, targetNamespace);

        if (!redefine)
        {
            return complexType;
        }
        else
        {
            // In theory, we might return a copy of the original.
            return null;
        }
    }

    /**
     * xs:complexType (local definition)
     */
    private XMLTypeRef complexTypeLocalTag(final XMLScope scope, final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace) throws XMLStreamException, AbortException
    {
        final XMLType complexType = cache.registerAnonymousType(scope, getFrozenLocation(reader.getLocation()));
        complexType.setComplexFlag();
        complexType.setBase(ANY_TYPE, DerivationMethod.Restriction);

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_MIXED.equals(localName))
                {
                    try
                    {
                        if (trueOrFalse(reader.getAttributeValue(i)))
                        {
                            complexType.m_contentKind = XMLContentTypeKind.Mixed;
                        }
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                complexType.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        complexTypeContent(complexType, cache, module, reader, redefine, targetNamespace);

        return new XMLTypeRef(complexType);
    }

    /**
     * xs:sequence, xs:choice or xs:all (outside a group)
     */
    private XMLParticleWithModelGroupTerm compositorOutsideGroupTag(final ModelGroup.SmCompositor compositor, final XMLScope compositorScope, final String contextName, final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace) throws XMLStreamException, XMLCompositorOutsideGroupException, AbortException
    {
        final XMLModelGroup group = new XMLModelGroup(compositor, compositorScope, getFrozenLocation(reader.getLocation()));

        BigInteger minOccurs = BigInteger.ONE;
        BigInteger maxOccurs = BigInteger.ONE;

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_MAX_OCCURS.equals(localName))
                {
                    switch (compositor)
                    {
                        case All:
                        {
                            maxOccurs = maxOccurs(reader.getAttributeValue(i), false, reader.getLocation(), reader.getName());
                        }
                        break;
                        case Choice:
                        case Sequence:
                        {
                            maxOccurs = maxOccurs(reader.getAttributeValue(i), true, reader.getLocation(), reader.getName());
                        }
                        break;
                        default:
                        {
                            throw new RuntimeException(compositor.name());
                        }
                    }
                }
                else if (LN_MIN_OCCURS.equals(localName))
                {
                    switch (compositor)
                    {
                        case All:
                        {
                            minOccurs = minOccurs(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
                        }
                        break;
                        case Choice:
                        case Sequence:
                        {
                            minOccurs = minOccurs(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
                        }
                        break;
                        default:
                        {
                            throw new AssertionError(compositor);
                        }
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                group.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ELEMENT.equals(localName))
                            {
                                switch (compositor)
                                {
                                    case All:
                                    {
                                        try
                                        {
                                            group.getParticles().add(PreCondition.assertNotNull(elementWithinAllTag(reader, cache, module, redefine, targetNamespace, new XMLScope(group))));
                                        }
                                        catch (final XMLElementException e)
                                        {
                                            m_errors.error(e.getCause());
                                        }
                                    }
                                    break;
                                    case Sequence:
                                    case Choice:
                                    {
                                        try
                                        {
                                            group.getParticles().add(PreCondition.assertNotNull(elementLocalTag(reader, cache, module, redefine, targetNamespace, new XMLScope(group))));
                                        }
                                        catch (final XMLElementException e)
                                        {
                                            m_errors.error(e.getCause());
                                        }
                                    }
                                    break;
                                    default:
                                    {
                                        throw new AssertionError(compositor);
                                    }
                                }
                                firstElement = false;
                            }
                            else if (LN_GROUP.equals(localName))
                            {
                                try
                                {
                                    group.getParticles().add(PreCondition.assertNotNull(groupParticleTag(new XMLScope(group), reader, cache, module, redefine, targetNamespace)));
                                }
                                catch (final XMLModelGroupUseException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else if (LN_CHOICE.equals(localName))
                            {
                                group.getParticles().add(PreCondition.assertNotNull(compositorOutsideGroupTag(ModelGroup.SmCompositor.Choice, new XMLScope(group), contextName, reader, cache, module, redefine, targetNamespace)));
                                firstElement = false;
                            }
                            else if (LN_SEQUENCE.equals(localName))
                            {
                                group.getParticles().add(PreCondition.assertNotNull(compositorOutsideGroupTag(ModelGroup.SmCompositor.Sequence, new XMLScope(group), contextName, reader, cache, module, redefine, targetNamespace)));
                                firstElement = false;
                            }
                            else if (LN_ANY.equals(localName))
                            {
                                group.getParticles().add(PreCondition.assertNotNull(anyElementTag(reader, targetNamespace, module)));
                                firstElement = false;
                            }
                            else if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, contextName, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        return new XMLParticleWithModelGroupTerm(minOccurs, maxOccurs, group, getFrozenLocation(reader.getLocation()));
    }

    /**
     * xs:sequence, xs:choice or xs:all (within a group)
     */
    private XMLModelGroup compositorWithinGroupTag(final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final XMLModelGroup group, final String contextName, final boolean redefine, final String targetNamespace) throws XMLStreamException, AbortException
    {
        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // the only place to put these attributes is on the parent. we don't do that; discard.
            }
        }

        // If doing a redefine, make a copy of the original group so that we can
        // mutate the original.
        final XMLModelGroup originalGroupCopy;
        if (redefine)
        {
            originalGroupCopy = new XMLModelGroup(group.getName(), group.getScope(), group.getLocation());
            originalGroupCopy.setCompositor(group.getCompositor());
            if (group.getParticles().size() > 0)
            {
                for (final XMLParticle particle : group.getParticles())
                {
                    originalGroupCopy.getParticles().add(particle);
                }
                group.getParticles().clear();
            }
        }
        else
        {
            // This just keeps the syntax checker happy.
            originalGroupCopy = null;
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ELEMENT.equals(localName))
                            {
                                try
                                {
                                    group.getParticles().add(PreCondition.assertNotNull(elementLocalTag(reader, cache, module, redefine, targetNamespace, new XMLScope(group))));
                                }
                                catch (final XMLElementException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else if (LN_GROUP.equals(localName))
                            {
                                switch (group.getCompositor())
                                {
                                    case Choice:
                                    case Sequence:
                                    {
                                        try
                                        {
                                            final XMLParticleWithModelGroupTerm groupParticle = groupParticleTag(new XMLScope(group), reader, cache, module, redefine, targetNamespace);
                                            if (!redefine)
                                            {
                                                group.getParticles().add(PreCondition.assertNotNull(groupParticle));
                                            }
                                            else
                                            {
                                                final XMLModelGroup g = groupParticle.getTerm();
                                                if (group == g)
                                                {
                                                    if (originalGroupCopy.getParticles().size() > 0)
                                                    {
                                                        for (final XMLParticle particle : originalGroupCopy.getParticles())
                                                        {
                                                            group.getParticles().add(PreCondition.assertNotNull(particle));
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    group.getParticles().add(PreCondition.assertNotNull(groupParticle));
                                                }
                                            }
                                        }
                                        catch (final XMLModelGroupUseException e)
                                        {
                                            m_errors.error(e.getCause());
                                        }
                                    }
                                    break;
                                    case All:
                                    {
                                        reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
                                        skipTag(reader);
                                    }
                                    break;
                                    default:
                                    {
                                        throw new AssertionError(group.getCompositor());
                                    }
                                }
                                firstElement = false;
                            }
                            else if (LN_CHOICE.equals(localName))
                            {
                                switch (group.getCompositor())
                                {
                                    case Choice:
                                    case Sequence:
                                    {
                                        try
                                        {
                                            group.getParticles().add(PreCondition.assertNotNull(compositorOutsideGroupTag(ModelGroup.SmCompositor.Choice, new XMLScope(group), localName, reader, cache, module, redefine, targetNamespace)));
                                        }
                                        catch (final XMLCompositorOutsideGroupException e)
                                        {
                                            m_errors.error(e.getCause());
                                        }
                                    }
                                    break;
                                    case All:
                                    {
                                        reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
                                        skipTag(reader);
                                    }
                                    break;
                                    default:
                                    {
                                        throw new AssertionError(group.getCompositor());
                                    }
                                }
                                firstElement = false;
                            }
                            else if (LN_SEQUENCE.equals(localName))
                            {
                                switch (group.getCompositor())
                                {
                                    case Choice:
                                    case Sequence:
                                    {
                                        try
                                        {
                                            group.getParticles().add(PreCondition.assertNotNull(compositorOutsideGroupTag(ModelGroup.SmCompositor.Sequence, new XMLScope(group), localName, reader, cache, module, redefine, targetNamespace)));
                                        }
                                        catch (final XMLCompositorOutsideGroupException e)
                                        {
                                            m_errors.error(e.getCause());
                                        }
                                    }
                                    break;
                                    case All:
                                    {
                                        reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
                                        skipTag(reader);
                                    }
                                    break;
                                    default:
                                    {
                                        throw new AssertionError(group.getCompositor());
                                    }
                                }
                                firstElement = false;
                            }
                            else if (LN_ANY.equals(localName))
                            {
                                switch (group.getCompositor())
                                {
                                    case Choice:
                                    case Sequence:
                                    {
                                        group.getParticles().add(PreCondition.assertNotNull(anyElementTag(reader, targetNamespace, module)));
                                    }
                                    break;
                                    case All:
                                    {
                                        reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
                                        skipTag(reader);
                                    }
                                    break;
                                    default:
                                    {
                                        throw new AssertionError(group.getCompositor());
                                    }
                                }
                                firstElement = false;
                            }
                            else if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, contextName, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        return group;
    }

    private String conditionNamespaceURI(final String namespaceURI)
    {
        return (null != namespaceURI) ? namespaceURI : XMLConstants.NULL_NS_URI;
    }

    /**
     * Use to parse the LN_BLOCK and LN_FINAL attributes that control substitution and derivation.
     */
    private void control(final String strval, final EnumSet<DerivationMethod> allValue, final EnumSet<DerivationMethod> resultSet) throws SimpleTypeException
    {
        resultSet.clear();

        if (strval.equals("#all"))
        {
            resultSet.addAll(allValue);
        }
        else
        {
            final StringTokenizer tokenizer = new StringTokenizer(strval);
            while (tokenizer.hasMoreTokens())
            {
                final String token = tokenizer.nextToken();
                if (token.equals("extension"))
                {
                    if (allValue.contains(DerivationMethod.Extension))
                    {
                        resultSet.add(DerivationMethod.Extension);
                    }
                    else
                    {
                        final DatatypeException cause = new DatatypeException(token, null);
                        throw new SimpleTypeException(strval, null, cause);
                    }
                }
                else if (token.equals("restriction"))
                {
                    if (allValue.contains(DerivationMethod.Restriction))
                    {
                        resultSet.add(DerivationMethod.Restriction);
                    }
                    else
                    {
                        final DatatypeException cause = new DatatypeException(token, null);
                        throw new SimpleTypeException(strval, null, cause);
                    }
                }
                else if (token.equals("substitution"))
                {
                    if (allValue.contains(DerivationMethod.Substitution))
                    {
                        resultSet.add(DerivationMethod.Substitution);
                    }
                    else
                    {
                        final DatatypeException cause = new DatatypeException(token, null);
                        throw new SimpleTypeException(strval, null, cause);
                    }
                }
                else if (token.equals("union"))
                {
                    if (allValue.contains(DerivationMethod.Union))
                    {
                        resultSet.add(DerivationMethod.Union);
                    }
                    else
                    {
                        final DatatypeException cause = new DatatypeException(token, null);
                        throw new SimpleTypeException(strval, null, cause);
                    }
                }
                else if (token.equals("list"))
                {
                    if (allValue.contains(DerivationMethod.List))
                    {
                        resultSet.add(DerivationMethod.List);
                    }
                    else
                    {
                        final DatatypeException cause = new DatatypeException(token, null);
                        throw new SimpleTypeException(strval, null, cause);
                    }
                }
                else
                {
                    final SimpleType atomicType = bootstrap.getAtomicType(NativeType.UNTYPED_ATOMIC);
                    final DatatypeException cause = new DatatypeException(token, atomicType);
                    throw new SimpleTypeException(strval, atomicType, cause);
                }
            }
        }
    }

    /**
     * Copies prefix mappings from the parser to the prefix mapping stack. <br/>
     * This should be called for all elements immediately after the stack has been pushed. The stack should be popped in
     * the
     */
    private void copyNamespaces(final XMLStreamReader parser, final PrefixMappingSupport pms)
    {
        final int namespaceCount = parser.getNamespaceCount();
        for (int i = 0; i < namespaceCount; i++)
        {
            String prefix = parser.getNamespacePrefix(i);
            String uri = parser.getNamespaceURI(i);
            // Normalization required to map StAX to javax.
            prefix = (null == prefix) ? XMLConstants.DEFAULT_NS_PREFIX : prefix;
            if (null != uri)
            {
                m_pms.declarePrefix(prefix, uri);
            }
            else
            {
                m_pms.declarePrefix(prefix, "");
            }
        }
    }

    private XMLAttribute determineLocalAttribute(final String name, final boolean qualified, final QName ref, final XMLSchemaCache cache, final XMLStreamReader parser, final String targetNamespace, final XMLScope scope) throws SchemaException
    {
        if (null != name)
        {
            if (null == ref)
            {
                final XMLAttribute attribute;
                if (qualified)
                {
                    attribute = new XMLAttribute(resolveUsingTargetNamespace(name, targetNamespace, parser.getNamespaceContext()), scope, ANY_SIMPLE_TYPE, getFrozenLocation(parser.getLocation()));
                }
                else
                {
                    attribute = new XMLAttribute(new QName("", name), scope, ANY_SIMPLE_TYPE, getFrozenLocation(parser.getLocation()));
                }
                return attribute;
            }
            else
            {
                throw new SmAttributeRefXorNameException(getFrozenLocation(parser.getLocation()));
            }
        }
        else
        {
            if (null != ref)
            {
                return cache.dereferenceAttribute(ref, parser.getLocation());
            }
            else
            {
                throw new SmAttributeRefXorNameException(getFrozenLocation(parser.getLocation()));
            }
        }
    }

    /**
     * Determines whether the local element is a local definition or a reference. <br/>
     * Imposes the constraint that one of the ref and name must be present, but not both.
     */
    private XMLElement determineLocalElement(final String name, final boolean qualified, final XMLTypeRef typeRef, final QName ref, final XMLSchemaCache cache, final XMLStreamReader parser, final String targetNamespace, final XMLScope scope) throws SchemaException
    {
        if ((null != name) && (null == ref))
        {
            final XMLElement element;
            if (qualified)
            {
                final QName ename = resolveUsingTargetNamespace(name, targetNamespace, parser.getNamespaceContext());
                element = new XMLElement(ename, scope, ANY_TYPE, getFrozenLocation(parser.getLocation()));
            }
            else
            {
                final QName ename = new QName("", name);
                element = new XMLElement(ename, scope, ANY_TYPE, getFrozenLocation(parser.getLocation()));
            }
            if (null != typeRef)
            {
                element.typeRef = typeRef;
            }
            return element;
        }
        else if ((null != ref) && (name == null))
        {
            return cache.dereferenceElement(ref, parser.getLocation());
        }
        else
        {
            throw new SmElementRefXorNameException(getFrozenLocation(parser.getLocation()));
        }
    }

    private void documentationTag(final XMLStreamReader reader) throws XMLStreamException, AbortException
    {
        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_SOURCE.equals(localName))
                {
                    /* final String source = */reader.getAttributeValue(i);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (XMLConstants.XML_NS_URI.equals(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if ("lang".equals(localName))
                {
                    try
                    {
                        /* final String language = */lang(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else
                {
                    reportAttributeInXmlNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // foreign attributes not allowed on documentation tag
                // {any attributes with non-schema namespace}
            }
        }

        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        skipTag(reader);
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    private void elementContent(final XMLElement element, final QName ref, final XMLSchemaModule module, final XMLSchemaCache cache, final XMLStreamReader reader, final boolean redefine, final String targetNamespace, final boolean seenType) throws XMLStreamException, AbortException
    {
        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_COMPLEX_TYPE.equals(localName))
                            {
                                assertRefAbsent(ref, reader.getLocation());
                                if (seenType)
                                {
                                    m_errors.error(new SmElementSimpleTypeXorComplexTypeException(getFrozenLocation(reader.getLocation())));
                                }
                                element.typeRef = complexTypeLocalTag(new XMLScope(element), reader, cache, module, redefine, targetNamespace);
                                firstElement = false;
                            }
                            else if (LN_SIMPLE_TYPE.equals(localName))
                            {
                                assertRefAbsent(ref, reader.getLocation());
                                if (seenType)
                                {
                                    m_errors.error(new SmElementSimpleTypeXorComplexTypeException(getFrozenLocation(reader.getLocation())));
                                }
                                element.typeRef = simpleTypeLocalTag(new XMLScope(element), cache, module, reader, redefine, targetNamespace);
                                firstElement = false;
                            }
                            else if (LN_UNIQUE.equals(localName))
                            {
                                assertRefAbsent(ref, reader.getLocation());
                                try
                                {
                                    element.getIdentityConstraints().add(uniqueTag(cache, reader, targetNamespace, module));
                                }
                                catch (final XMLIdentityConstraintException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else if (LN_KEY.equals(localName))
                            {
                                assertRefAbsent(ref, reader.getLocation());
                                try
                                {
                                    element.getIdentityConstraints().add(keyTag(cache, reader, targetNamespace, module));
                                }
                                catch (final XMLIdentityConstraintException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else if (LN_KEYREF.equals(localName))
                            {
                                assertRefAbsent(ref, reader.getLocation());
                                try
                                {
                                    element.getIdentityConstraints().add(keyrefTag(cache, reader, targetNamespace, module));
                                }
                                catch (final XMLIdentityConstraintException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_ELEMENT, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_ELEMENT, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                    // Ignore.
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    /**
     * xs:element (reference or local definition)
     */
    private XMLParticle elementLocalTag(final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace, final XMLScope scope) throws XMLStreamException, XMLElementException, AbortException
    {
        String name = null;
        XMLTypeRef typeRef = null;
        final QName ref = referenceOptional(reader, LN_REF, module, targetNamespace);
        BigInteger minOccurs = BigInteger.ONE;
        BigInteger maxOccurs = BigInteger.ONE;
        boolean nillable = false;
        final EnumSet<DerivationMethod> block = EnumSet.copyOf(module.blockDefault);
        boolean qualified = module.elementQualified;
        XMLValueConstraint valueConstraint = null;

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_BLOCK.equals(localName))
                {
                    assertRefAbsent(ref, reader.getLocation());
                    try
                    {
                        control(reader.getAttributeValue(i), EnumSet.of(DerivationMethod.Extension, DerivationMethod.Restriction, DerivationMethod.Substitution), block);
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_DEFAULT.equals(localName))
                {
                    assertRefAbsent(ref, reader.getLocation());
                    if (null == valueConstraint)
                    {
                        valueConstraint = new XMLValueConstraint(ValueConstraint.Kind.Default, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
                    }
                    else
                    {
                        m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
                    }
                }
                else if (LN_FIXED.equals(localName))
                {
                    assertRefAbsent(ref, reader.getLocation());
                    if (null == valueConstraint)
                    {
                        valueConstraint = new XMLValueConstraint(ValueConstraint.Kind.Fixed, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
                    }
                    else
                    {
                        m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
                    }
                }
                else if (LN_FORM.equals(localName))
                {
                    assertRefAbsent(ref, reader.getLocation());
                    try
                    {
                        qualified = qualified(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if (LN_MAX_OCCURS.equals(localName))
                {
                    maxOccurs = maxOccurs(reader.getAttributeValue(i), true, reader.getLocation(), reader.getName());
                }
                else if (LN_MIN_OCCURS.equals(localName))
                {
                    minOccurs = minOccurs(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
                }
                else if (LN_NAME.equals(localName))
                {
                    try
                    {
                        name = name(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_NILLABLE.equals(localName))
                {
                    assertRefAbsent(ref, reader.getLocation());
                    try
                    {
                        nillable = trueOrFalse(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_REF.equals(localName))
                {
                    // Already got it.
                }
                else if (LN_TYPE.equals(localName))
                {
                    assertRefAbsent(ref, reader.getLocation());
                    try
                    {
                        typeRef = typeRef(reader.getAttributeValue(i), LN_TYPE, module.isChameleon(), targetNamespace, reader);
                        ensureReferenceType(typeRef.getName(), reader.getLocation(), false, cache);
                    }
                    catch (final SmComplexTypeException e)
                    {
                        m_errors.error(e);
                    }
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        final XMLElement element;
        try
        {
            element = determineLocalElement(name, qualified, typeRef, ref, cache, reader, targetNamespace, scope);
        }
        catch (final SchemaException e)
        {
            skipTag(reader);
            throw new XMLElementException(e);
        }
        if ( (name != null) || (ref == null) )  // not an element reference; new definition 
            element.foreignAttributes.putAll(foreignAttributes);
        foreignAttributes.clear(); // clear regardless.

        if (null == ref)
        {
            element.setNillableFlag(nillable);
            element.getBlock().addAll(block);
            element.m_valueConstraint = valueConstraint;
            elementContent(element, ref, module, cache, reader, redefine, targetNamespace, (null != typeRef));
            return new XMLParticleWithElementTerm(minOccurs, maxOccurs, element, null, getFrozenLocation(reader.getLocation()));
        }
        else
        {
            element.m_valueConstraint = null;
            elementContent(element, null, module, cache, reader, redefine, targetNamespace, (null != typeRef));
            return new XMLParticleWithElementTerm(minOccurs, maxOccurs, element, valueConstraint, getFrozenLocation(reader.getLocation()));
        }
    }

    /**
     * xs:element (global definition)
     */
    private XMLElement elementTag(final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final String targetNamespace) throws XMLStreamException, XMLElementException, AbortException
    {
        final XMLElement element;
        try
        {
            element = cache.registerElement(requiredNCName(LN_NAME, targetNamespace, reader), getFrozenLocation(reader.getLocation()));
        }
        catch (final SchemaException e)
        {
            skipTag(reader);
            throw new XMLElementException(e);
        }

        for (final DerivationMethod derivation : module.blockDefault)
        {
            // TODO: note the comment here and for finalDefault. is more work needed?
            // Note: blockDefault may contain other values than extension,
            // restriction or substitution.
            if (derivation.isExtension() || derivation.isRestriction() || derivation.isSubstitution())
            {
                element.getBlock().add(derivation);
            }
        }

        for (final DerivationMethod derivation : module.finalDefault)
        {
            // Note: finalDefault may contain other values than extension or
            // restriction.
            if (derivation.isExtension() || derivation.isRestriction())
            {
                element.getFinal().add(derivation);
            }
        }

        boolean seenType = false;

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ABSTRACT.equals(localName))
                {
                    try
                    {
                        element.setAbstractFlag(trueOrFalse(reader.getAttributeValue(i)));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_BLOCK.equals(localName))
                {
                    try
                    {
                        control(reader.getAttributeValue(i), EnumSet.of(DerivationMethod.Extension, DerivationMethod.Restriction, DerivationMethod.Substitution), element.getBlock());
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_DEFAULT.equals(localName))
                {
                    if (null == element.m_valueConstraint)
                    {
                        element.m_valueConstraint = new XMLValueConstraint(ValueConstraint.Kind.Default, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
                    }
                    else
                    {
                        m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
                    }
                }
                else if (LN_FIXED.equals(localName))
                {
                    if (null == element.m_valueConstraint)
                    {
                        element.m_valueConstraint = new XMLValueConstraint(ValueConstraint.Kind.Fixed, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
                    }
                    else
                    {
                        m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
                    }
                }
                else if (LN_FINAL.equals(localName))
                {
                    try
                    {
                        control(reader.getAttributeValue(i), EnumSet.of(DerivationMethod.Extension, DerivationMethod.Restriction), element.getFinal());
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if (LN_NAME.equals(localName))
                {
                    // Already known.
                }
                else if (LN_NILLABLE.equals(localName))
                {
                    try
                    {
                        element.setNillableFlag(trueOrFalse(reader.getAttributeValue(i)));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_SUBSTITUTION_GROUP.equals(localName))
                {
                    try
                    {
                    	final QName elemName = module.isChameleon() ?
                    			resolveUsingTargetNamespace(reader.getAttributeValue(i), targetNamespace, reader.getNamespaceContext())
                    			: resolveUsingXMLNamespaces(reader.getAttributeValue(i), reader.getNamespaceContext());
                    	element.substitutionGroup = cache.dereferenceElement(elemName, reader.getLocation());
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_TYPE.equals(localName))
                {
                    seenType = true;
                    try
                    {
                        element.typeRef = typeRef(reader.getAttributeValue(i), LN_TYPE, module.isChameleon(), targetNamespace, reader);
                        ensureReferenceType(element.typeRef.getName(), reader.getLocation(), false, cache);
                    }
                    catch (final SmComplexTypeException e)
                    {
                        m_errors.error(e);
                    }
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                element.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        elementContent(element, null, module, cache, reader, false, targetNamespace, seenType);

        return element;
    }

    /**
     * xs:element (within xs:all) <br/>
     * Reference to a global element declaration or local definition (local definitions cannot be referenced). The
     * number of occurrences can only be zero or one when xs:element is used within xs:all.
     */
    private XMLParticle elementWithinAllTag(final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace, final XMLScope scope) throws XMLStreamException, XMLElementException, AbortException
    {
        String name = null;
        XMLTypeRef typeRef = null;
        final QName ref = referenceOptional(reader, LN_REF, module, targetNamespace);
        BigInteger minOccurs = BigInteger.ONE;
        BigInteger maxOccurs = BigInteger.ONE;
        boolean nillable = false;
        final EnumSet<DerivationMethod> block = EnumSet.copyOf(module.blockDefault);
        boolean qualified = module.elementQualified;
        XMLValueConstraint valueConstraint = null;

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_BLOCK.equals(localName))
                {
                    assertRefAbsent(ref, reader.getLocation());
                    try
                    {
                        control(reader.getAttributeValue(i), EnumSet.of(DerivationMethod.Extension, DerivationMethod.Restriction, DerivationMethod.Substitution), block);
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_DEFAULT.equals(localName))
                {
                    assertRefAbsent(ref, reader.getLocation());
                    if (null == valueConstraint)
                    {
                        valueConstraint = new XMLValueConstraint(ValueConstraint.Kind.Default, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
                    }
                    else
                    {
                        m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
                    }
                }
                else if (LN_FIXED.equals(localName))
                {
                    assertRefAbsent(ref, reader.getLocation());
                    if (null == valueConstraint)
                    {
                        valueConstraint = new XMLValueConstraint(ValueConstraint.Kind.Fixed, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
                    }
                    else
                    {
                        m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
                    }
                }
                else if (LN_FORM.equals(localName))
                {
                    assertRefAbsent(ref, reader.getLocation());
                    try
                    {
                        qualified = qualified(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_NAME.equals(localName))
                {
                    try
                    {
                        name = name(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_TYPE.equals(localName))
                {
                    assertRefAbsent(ref, reader.getLocation());
                    try
                    {
                        typeRef = typeRef(reader.getAttributeValue(i), LN_TYPE, module.isChameleon(), targetNamespace, reader);
                        ensureReferenceType(typeRef.getName(), reader.getLocation(), false, cache);
                    }
                    catch (final SmComplexTypeException e)
                    {
                        m_errors.error(e);
                    }
                }
                else if (LN_REF.equals(localName))
                {
                    // Already got it.
                }
                else if (LN_MAX_OCCURS.equals(localName))
                {
                    maxOccurs = maxOccurs(reader.getAttributeValue(i), false, reader.getLocation(), reader.getName());
                }
                else if (LN_MIN_OCCURS.equals(localName))
                {
                    minOccurs = minOccurs(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
                }
                else if (LN_NILLABLE.equals(localName))
                {
                    assertRefAbsent(ref, reader.getLocation());
                    try
                    {
                        nillable = trueOrFalse(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        final XMLElement element;
        try
        {
            element = determineLocalElement(name, qualified, typeRef, ref, cache, reader, targetNamespace, scope);
        }
        catch (final SchemaException e)
        {
            skipTag(reader);
            throw new XMLElementException(e);
        }
        element.foreignAttributes.putAll(foreignAttributes);
        foreignAttributes.clear();

        element.setNillableFlag(nillable);
        element.getBlock().addAll(block);

        elementContent(element, ref, module, cache, reader, redefine, targetNamespace, (null != typeRef));

        return new XMLParticleWithElementTerm(minOccurs, maxOccurs, element, valueConstraint, getFrozenLocation(reader.getLocation()));
    }

    private void ensureReferenceType(final QName name, final Location location, final boolean mustExist, final XMLSchemaCache cache) throws AbortException
    {
        PreCondition.assertArgumentNotNull(name);

        if (name.getNamespaceURI().equals(XMLConstants.W3C_XML_SCHEMA_NS_URI))
        {
            // Do nothing. This will be caught later if a dangling reference
            // exists.
        }
        else
        {
            try
            {
                cache.dereferenceType(name, location, mustExist);
            }
            catch (final SchemaException e)
            {
                m_errors.error(e);
            }
        }
    }

    /**
     * xs:enumeration
     */
    private XMLEnumeration enumerationTag(final XMLType simpleType, final XMLStreamReader reader, final XMLSchemaModule module) throws XMLStreamException, AbortException
    {
        final XMLEnumeration enumeration = new XMLEnumeration(simpleType, getFrozenLocation(reader.getLocation()));

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ID.equals(localName))
                {
                    enumeration.id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if (LN_VALUE.equals(localName))
                {
                    enumeration.setValue(reader.getAttributeValue(i), m_pms.getPrefixResolverSnapshot());
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // the api schema model collapses all of the enumerations into a list.
                // at least, i think it does. if not, then this should get turned back on.
//                enumeration.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_ENUMERATION, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_ENUMERATION, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        return enumeration;
    }

    /**
     * xs:extension (complex content) <br/>
     * We don't return anything because this affects multiple aspects of the complex type.
     */
    private void extensionInComplexContentTag(final XMLType complexType, final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace) throws XMLStreamException, AbortException
    {
        try
        {
            final XMLType redefineType;
            if (!redefine)
            {
                redefineType = null;
                baseTypeDefinitionInComplexContent(complexType, DerivationMethod.Extension, reader, redefine, cache, module, targetNamespace);
            }
            else
            {
                final QName baseName = requiredQName(LN_BASE, module.isChameleon(), targetNamespace, reader);
                redefineType = cache.dereferenceType(baseName, reader.getLocation(), redefine);
                if (!complexType.getName().equals(baseName))
                {
                    skipTag(reader);
                    throw new SmRedefineTypeSelfReferenceException(complexType.getName(), baseName, getFrozenLocation(reader.getLocation()));
                }
            }

            final int attributeCount = reader.getAttributeCount();
            for (int i = 0; i < attributeCount; i++)
            {
                final String namespaceURI = reader.getAttributeNamespace(i);
                if (isGlobal(namespaceURI))
                {
                    final String localName = reader.getAttributeLocalName(i);
                    if (LN_ID.equals(localName))
                    {
                        checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                    }
                    else if (LN_BASE.equals(localName))
                    {
                        // Already known.
                    }
                    else
                    {
                        reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                    }
                }
                else if (isWXS(namespaceURI))
                {
                    reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
                else
                {
                    // the only place to put foreign atts is on the parent. we don't do that; discard.
                }
            }

            final ContentModelMachine<String> machine = new ContentModelMachine<String>(extensionInComplexContentTable, EPSILON);
            boolean done = false;
            while (!done)
            {
                final int event = reader.next();

                switch (event)
                {
                    case XMLStreamConstants.START_ELEMENT:
                    {
                        m_pms.pushContext();
                        try
                        {
                            copyNamespaces(reader, m_pms);
                            if (isWXS(reader.getNamespaceURI()))
                            {
                                final String localName = reader.getLocalName();
                                if (!machine.step(localName))
                                {
                                    reportUnexpectedElementTag(LN_EXTENSION, reader.getName(), reader.getLocation());
                                    skipTag(reader);
                                }
                                else
                                {
                                    if (LN_GROUP.equals(localName))
                                    {
                                        complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
                                        try
                                        {
                                            complexType.m_contentModel = groupParticleTag(new XMLScope(complexType), reader, cache, module, redefine, targetNamespace);
                                        }
                                        catch (final XMLModelGroupUseException e)
                                        {
                                            m_errors.error(e.getCause());
                                        }
                                    }
                                    else if (LN_ALL.equals(localName))
                                    {
                                        complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
                                        try
                                        {
                                            complexType.m_contentModel = compositorOutsideGroupTag(ModelGroup.SmCompositor.All, new XMLScope(complexType), localName, reader, cache, module, redefine, targetNamespace);
                                        }
                                        catch (final XMLCompositorOutsideGroupException e)
                                        {
                                            m_errors.error(e.getCause());
                                        }
                                    }
                                    else if (LN_CHOICE.equals(localName))
                                    {
                                        complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
                                        try
                                        {
                                            complexType.m_contentModel = compositorOutsideGroupTag(ModelGroup.SmCompositor.Choice, new XMLScope(complexType), localName, reader, cache, module, redefine, targetNamespace);
                                        }
                                        catch (final XMLCompositorOutsideGroupException e)
                                        {
                                            m_errors.error(e.getCause());
                                        }
                                    }
                                    else if (LN_SEQUENCE.equals(localName))
                                    {
                                        try
                                        {
                                            final XMLParticleWithModelGroupTerm contentModel = compositorOutsideGroupTag(ModelGroup.SmCompositor.Sequence, new XMLScope(complexType), localName, reader, cache, module, redefine, targetNamespace);
                                            if (!redefine)
                                            {
                                                complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
                                                complexType.m_contentModel = contentModel;
                                            }
                                            else
                                            {
                                                redefineType.extendContentType(complexType.m_contentKind.isMixed(), contentModel);
                                            }
                                        }
                                        catch (final XMLCompositorOutsideGroupException e)
                                        {
                                            m_errors.error(e.getCause());
                                        }
                                    }
                                    else if (LN_ATTRIBUTE.equals(localName))
                                    {
                                        attributeLocalTag(reader, cache, module, redefine, targetNamespace, complexType.getAttributeUses(), complexType.prohibited, new XMLScope(complexType));
                                    }
                                    else if (LN_ATTRIBUTE_GROUP.equals(localName))
                                    {
                                        try
                                        {
                                            complexType.getAttributeGroups().add(attribGroupRefTag(reader, cache, module, targetNamespace));
                                        }
                                        catch (final XMLAttributeGroupException e)
                                        {
                                            m_errors.error(e.getCause());
                                        }
                                    }
                                    else if (LN_ANY_ATTRIBUTE.equals(localName))
                                    {
                                        if (null == complexType.attributeWildcard)
                                        {
                                            complexType.attributeWildcard = anyAttributeTag(reader, targetNamespace, module);
                                        }
                                        else
                                        {
                                            // xs:anyAttribute can only occur
                                            // zero or once.
                                            reportUnexpectedElementTag(LN_EXTENSION, reader.getName(), reader.getLocation());
                                            skipTag(reader);
                                        }
                                    }
                                    else if (LN_ANNOTATION.equals(localName))
                                    {
                                        annotationTag(reader, module);
                                    }
                                    else
                                    {
                                        throw new AssertionError(reader.getName());
                                    }
                                }
                            }
                            else
                            {
                                skipTag(reader);
                            }
                        }
                        finally
                        {
                            m_pms.popContext();
                        }
                    }
                    break;
                    case XMLStreamConstants.END_ELEMENT:
                    {
                        if (!machine.end())
                        {
                            reportUnexpectedEnd(LN_EXTENSION, reader.getLocation());
                        }
                        done = true;
                    }
                    break;
                    case XMLStreamConstants.CHARACTERS:
                    case XMLStreamConstants.COMMENT:
                    case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    case XMLStreamConstants.SPACE:
                    {
                    }
                    break;
                    default:
                    {
                        throw new UnsupportedOperationException(Integer.toString(event));
                    }
                }
            }
        }
        catch (final SchemaException e)
        {
            m_errors.error(e);
        }
    }

    /**
     * xs:extension (simple content)
     */
    private void extensionInSimpleContentTag(final XMLType complexType, final XMLSchemaModule module, final XMLStreamReader reader, final XMLSchemaCache cache, final boolean redefine, final String targetNamespace) throws XMLStreamException, AbortException
    {
        // When extending simple content we're adding attributes to this complex
        // type.
        try
        {
            final QName baseName = requiredQName(LN_BASE, module.isChameleon(), targetNamespace, reader);
            ensureReferenceType(baseName, reader.getLocation(), redefine, cache);
            complexType.setBase(new XMLTypeRef(baseName), DerivationMethod.Extension);
        }
        catch (final SchemaException e)
        {
            m_errors.error(e);
        }

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ID.equals(localName))
                {

                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if (LN_BASE.equals(localName))
                {
                    // Already known.
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // this is a promotion to the containing type tag
                complexType.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        final ContentModelMachine<String> machine = new ContentModelMachine<String>(extensionInSimpleContentTable, EPSILON);
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (!machine.step(localName))
                            {
                                reportUnexpectedElementTag(LN_EXTENSION, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                            else
                            {
                                if (LN_ATTRIBUTE.equals(localName))
                                {
                                    attributeLocalTag(reader, cache, module, redefine, targetNamespace, complexType.getAttributeUses(), complexType.prohibited, new XMLScope(complexType));
                                }
                                else if (LN_ATTRIBUTE_GROUP.equals(localName))
                                {
                                    try
                                    {
                                        complexType.getAttributeGroups().add(attribGroupRefTag(reader, cache, module, targetNamespace));
                                    }
                                    catch (final XMLAttributeGroupException e)
                                    {
                                        m_errors.error(e.getCause());
                                    }
                                }
                                else if (LN_ANY_ATTRIBUTE.equals(localName))
                                {
                                    complexType.attributeWildcard = anyAttributeTag(reader, targetNamespace, module);
                                }
                                else if (LN_ANNOTATION.equals(localName))
                                {
                                    annotationTag(reader, module);
                                }
                                else
                                {
                                    throw new AssertionError(reader.getName());
                                }
                            }
                        }
                        else
                        {
                            skipTag(reader);
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    if (!machine.end())
                    {
                        reportUnexpectedEnd(LN_EXTENSION, reader.getLocation());
                    }
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    private RestrictedXPath fieldTag(final XMLStreamReader reader, final XMLSchemaModule module) throws XMLStreamException, XMLFieldException, AbortException
    {
        RestrictedXPath xpath = null;
        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_XPATH.equals(localName))
                {
                    try
                    {
                        xpath = xpath(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
                    }
                    catch (final SmAttributeUseException e)
                    {
                        skipTag(reader);
                        throw new XMLFieldException(e);
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // ignore foreign attributes (probably the right thing)
                // {any attributes with non-schema namespace}
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_FIELD, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_FIELD, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        return xpath;
    }

    private boolean fixed(final String strval, final Location location, final QName elementName) throws SmAttributeUseException
    {
        try
        {
            return trueOrFalse(strval);
        }
        catch (final SimpleTypeException e)
        {
            throw new SmAttributeUseException(elementName, new QName(LN_FIXED), getFrozenLocation(location), e);
        }
    }

    /**
     * xs:fractionDigits
     */
    private XMLFractionDigitsFacet fractionDigitsTag(final XMLType simpleType, final XMLStreamReader reader, final XMLSchemaModule module) throws XMLStreamException, SmComplexTypeException, AbortException
    {
        final XMLFractionDigitsFacet facet = new XMLFractionDigitsFacet(simpleType, getFrozenLocation(reader.getLocation()));

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ID.equals(localName))
                {
                    facet.id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if (LN_FIXED.equals(localName))
                {
                    facet.fixed = fixed(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
                }
                else if (LN_VALUE.equals(localName))
                {
                    final String strval = reader.getAttributeValue(i);
                    try
                    {
                        facet.value = nonNegativeInteger(strval);
                    }
                    catch (final SimpleTypeException ignore)
                    {
                        throw new SmAttributeUseException(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()), ignore);
                    }
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                facet.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_FRACTION_DIGITS, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_FRACTION_DIGITS, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        return facet;
    }

    /**
     * xs:group (reference)
     */
    private XMLParticleWithModelGroupTerm groupParticleTag(final XMLScope localScope, final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace) throws XMLStreamException, XMLModelGroupUseException, AbortException
    {
        final QName ref;
        BigInteger minOccurs = BigInteger.ONE;
        BigInteger maxOccurs = BigInteger.ONE;

        try
        {
            ref = requiredQName(LN_REF, module.isChameleon(), targetNamespace, reader);
        }
        catch (final SmComplexTypeException e)
        {
            skipTag(reader);
            throw new XMLModelGroupUseException(e);
        }

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if (LN_MAX_OCCURS.equals(localName))
                {
                    maxOccurs = maxOccurs(reader.getAttributeValue(i), true, reader.getLocation(), reader.getName());
                }
                else if (LN_MIN_OCCURS.equals(localName))
                {
                    minOccurs = minOccurs(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
                }
                else if (LN_REF.equals(localName))
                {
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // don't think we should modify the thing referred to ...
                // {any attributes with non-schema namespace}
            }
        }

        final XMLModelGroup modelGroup;
        if (null != ref)
        {
            try
            {
                modelGroup = cache.dereferenceModelGroup(ref, reader.getLocation(), redefine);
            }
            catch (final SchemaException e)
            {
                skipTag(reader);
                throw new XMLModelGroupUseException(e);
            }
        }
        else
        {
            modelGroup = new XMLModelGroup(ModelGroup.SmCompositor.Sequence, localScope, getFrozenLocation(reader.getLocation()));
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_GROUP, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_GROUP, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                    // Ignore.
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }

        return new XMLParticleWithModelGroupTerm(minOccurs, maxOccurs, modelGroup, getFrozenLocation(reader.getLocation()));
    }

    /**
     * xs:group (definition)
     */
    private XMLModelGroup groupTag(final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace) throws XMLStreamException, XMLModelGroupException, AbortException
    {
        final XMLModelGroup modelGroup;
        if (!redefine)
        {
            try
            {
                modelGroup = cache.registerModelGroup(requiredNCName(LN_NAME, targetNamespace, reader), getFrozenLocation(reader.getLocation()));
            }
            catch (final SchemaException e)
            {
                skipTag(reader);
                throw new XMLModelGroupException(e);
            }
        }
        else
        {
            try
            {
                modelGroup = cache.dereferenceModelGroup(requiredNCName(LN_NAME, targetNamespace, reader), reader.getLocation(), redefine);
            }
            catch (final SchemaException e)
            {
                skipTag(reader);
                throw new XMLModelGroupException(e);
            }
        }

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_NAME.equals(localName))
                {
                    // Already known.
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                modelGroup.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean firstElement = true;
        boolean missingACS = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_SEQUENCE.equals(localName))
                            {
                                missingACS = checkWxsElementMaxOccursUnity(missingACS, LN_GROUP, LN_SEQUENCE, reader.getLocation());
                                modelGroup.setCompositor(ModelGroup.SmCompositor.Sequence);
                                compositorWithinGroupTag(reader, cache, module, modelGroup, localName, redefine, targetNamespace);
                                firstElement = false;
                            }
                            else if (LN_CHOICE.equals(localName))
                            {
                                missingACS = checkWxsElementMaxOccursUnity(missingACS, LN_GROUP, LN_CHOICE, reader.getLocation());
                                modelGroup.setCompositor(ModelGroup.SmCompositor.Choice);
                                compositorWithinGroupTag(reader, cache, module, modelGroup, localName, redefine, targetNamespace);
                                firstElement = false;
                            }
                            else if (LN_ALL.equals(localName))
                            {
                                missingACS = checkWxsElementMaxOccursUnity(missingACS, LN_GROUP, LN_ALL, reader.getLocation());
                                modelGroup.setCompositor(ModelGroup.SmCompositor.All);
                                compositorWithinGroupTag(reader, cache, module, modelGroup, localName, redefine, targetNamespace);
                                firstElement = false;
                            }
                            else if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_GROUP, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_GROUP, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                    // Ignore.
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }

        if (null == modelGroup.getCompositor())
        {
            // Expecting (xs:all | xs:choice | xs:sequence)
            m_errors.error(new SmUnexpectedEndException(reader.getName(), getFrozenLocation(reader.getLocation())));
        }

        return modelGroup;
    }

    private void importTag(final XMLSchemaCache cache, final XMLSchemaModule module, final XMLStreamReader reader, final String targetNamespace) throws XMLStreamException, AbortException
    {
        PreCondition.assertArgumentNotNull(module, "module");
        try
        {
            String schemaLocation = null;
            String namespace = null;
            final int attributeCount = reader.getAttributeCount();
            for (int i = 0; i < attributeCount; i++)
            {
                final String namespaceURI = reader.getAttributeNamespace(i);
                if (isGlobal(namespaceURI))
                {
                    final String localName = reader.getAttributeLocalName(i);
                    if (LN_SCHEMA_LOCATION.equals(localName))
                    {
                        schemaLocation = reader.getAttributeValue(i);
                    }
                    else if (LN_NAMESPACE.equals(localName))
                    {
                        namespace = reader.getAttributeValue(i);
                        if (namespace.equals(targetNamespace))
                        {
                            throw new SmIllegalNamespaceException(LN_IMPORT, targetNamespace, namespace.toString(), new SrcFrozenLocation(reader.getLocation()));
                        }
                    }
                    else if (LN_ID.equals(localName))
                    {
                        checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                    }
                    else
                    {
                        reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                    }
                }
                else if (isWXS(namespaceURI))
                {
                    reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
                else
                {
                    // at the moment, we don't have a way to report these, unless we change XMLSchemaModule to also have
                    // an FAMap, and anyway these will get discarded on transition to the formal schema model.
                    // {any attributes with non-schema namespace}
                }
            }

            annotationContent(LN_IMPORT, reader, module);

            if (m_processRepeatedNamespaces || !cache.m_seenNamespaces.contains(namespace))
            {
                parseExternalModule(cache, module, reader.getLocation(), namespace, schemaLocation, ModuleKind.Import);
            }
        }
        catch (final SmIllegalNamespaceException e)
        {
            m_errors.error(e);
        }
    }

    private void includeTag(final XMLSchemaCache cache, final XMLSchemaModule module, final XMLStreamReader reader) throws XMLStreamException, AbortException
    {
        final int attributeCount = reader.getAttributeCount();
        String schemaLocation = null;
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_SCHEMA_LOCATION.equals(localName))
                {
                    schemaLocation = reader.getAttributeValue(i);
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // same issue as with import tag: no way to let foreign attributes persist.
                // {any attributes with non-schema namespace}
            }
        }

        if (schemaLocation != null)
        {
            parseExternalModule(cache, module, reader.getLocation(), null, schemaLocation, ModuleKind.Include);
        }
        else
        {
            m_errors.error(new SmMissingAttributeException(reader.getName(), new QName(LN_SCHEMA_LOCATION), getFrozenLocation(reader.getLocation())));
        }

        annotationContent(LN_INCLUDE, reader, module);
    }

    /**
     * There seems to be some inconsistency in how implementations of NamespaceContext report an unbound prefix. <br/>
     * This routine helps in assessing the outcome. <br/>
     * This contradicts the API specification. If this gets too troublesome we should copy the prefix mappings to our
     * own utility and use that to do the namespace lookup.
     */
    private boolean isBoundPrefix(final String prefix, final String namespaceURI)
    {
        if (null != namespaceURI)
        {
            if (XMLConstants.NULL_NS_URI.equals(namespaceURI))
            {
                if (XMLConstants.DEFAULT_NS_PREFIX.equals(prefix))
                {
                    return true;
                }
                else
                {
                    // TODO: watch for this problem.
                    // This contradicts the API specification. If this gets too
                    // troublesome
                    // we should copy the prefix mappings to our own utility and
                    // use that
                    // to do the namespace lookup.
                    return true;
                }
            }
            else
            {
                return true;
            }
        }
        else
        {
            // The return value is null. This value is not represented in the
            // API specification
            // but we assume (reasonably) that it means the prefix is not bound,
            // but only in the
            // case that the prefix is not the default namespace prefix.
            return XMLConstants.DEFAULT_NS_PREFIX.equals(prefix);
        }
    }

    /**
     * Determines whether the namespace specified is the global (no-name) namespace. <br/>
     * Note: StaX uses <code>null</code> for the no-name namespace, but this function also accepts the zero-length
     * string.
     */
    private boolean isGlobal(final String namespaceURI)
    {
        return (null == namespaceURI) || XMLConstants.NULL_NS_URI.equals(namespaceURI);
    }

    /**
     * Determines whether the namespace specified is the W3C XML Schema namespace.
     */
    private boolean isWXS(final String namespaceURI)
    {
        return XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(namespaceURI);
    }

    private XMLIdentityConstraint keyrefTag(final XMLSchemaCache cache, final XMLStreamReader reader, final String targetNamespace, final XMLSchemaModule module) throws XMLStreamException, XMLIdentityConstraintException, AbortException
    {
        final XMLIdentityConstraint keyref;
        try
        {
            final QName name = requiredNCName(LN_NAME, targetNamespace, reader);

            module.registerIdentityConstraintName(name, reader.getLocation());

            keyref = cache.registerIdentityConstraint(IdentityConstraintKind.KeyRef, name, getFrozenLocation(reader.getLocation()));
        }
        catch (final SchemaException e)
        {
            skipTag(reader);
            throw new XMLIdentityConstraintException(e);
        }

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_NAME.equals(localName))
                {
                    // Already known.
                }
                else if (LN_REFER.equals(localName))
                {
                    try
                    {
                        /* final String name = */
                        final QName reference = resolveUsingXMLNamespaces(reader.getAttributeValue(i), reader.getNamespaceContext());
                        keyref.keyConstraint = cache.dereferenceIdentityConstraint(reference, reader.getLocation());
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                keyref.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean missingSelector = true;
        boolean missingFields = true;
        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_KEYREF, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else if (LN_SELECTOR.equals(localName))
                            {
                                missingSelector = checkWxsElementMaxOccursUnity(missingSelector, LN_UNIQUE, LN_SELECTOR, reader.getLocation());
                                try
                                {
                                    keyref.selector = selectorTag(reader, module);
                                }
                                catch (final XMLSelectorException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else if (LN_FIELD.equals(localName))
                            {
                                if (missingSelector)
                                {
                                    m_errors.error(new SmUnexpectedElementException(reader.getName(), getFrozenLocation(reader.getLocation()), new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, LN_FIELD), getFrozenLocation(reader.getLocation())));
                                }
                                missingFields = false;
                                try
                                {
                                    keyref.fields.add(fieldTag(reader, module));
                                }
                                catch (final XMLFieldException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_KEYREF, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        if (missingFields)
        {
            m_errors.error(new SmUnexpectedEndException(reader.getName(), getFrozenLocation(reader.getLocation())));
        }
        return keyref;
    }

    private XMLIdentityConstraint keyTag(final XMLSchemaCache cache, final XMLStreamReader reader, final String targetNamespace, final XMLSchemaModule module) throws XMLStreamException, XMLIdentityConstraintException, AbortException
    {
        final XMLIdentityConstraint constraint;
        try
        {
            final QName name = requiredNCName(LN_NAME, targetNamespace, reader);

            module.registerIdentityConstraintName(name, reader.getLocation());

            constraint = cache.registerIdentityConstraint(IdentityConstraintKind.Key, name, getFrozenLocation(reader.getLocation()));
        }
        catch (final SchemaException e)
        {
            skipTag(reader);
            throw new XMLIdentityConstraintException(e);
        }

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_NAME.equals(localName))
                {
                    // Already known.
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                constraint.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean missingSelector = true;
        boolean missingFields = true;
        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_KEY, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else if (LN_SELECTOR.equals(localName))
                            {
                                missingSelector = checkWxsElementMaxOccursUnity(missingSelector, LN_UNIQUE, LN_SELECTOR, reader.getLocation());
                                try
                                {
                                    constraint.selector = selectorTag(reader, module);
                                }
                                catch (final XMLSelectorException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else if (LN_FIELD.equals(localName))
                            {
                                if (missingSelector)
                                {
                                    m_errors.error(new SmUnexpectedElementException(reader.getName(), getFrozenLocation(reader.getLocation()), new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, LN_FIELD), getFrozenLocation(reader.getLocation())));
                                }
                                missingFields = false;
                                try
                                {
                                    constraint.fields.add(fieldTag(reader, module));
                                }
                                catch (final XMLFieldException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_KEY, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        if (missingFields)
        {
            m_errors.error(new SmUnexpectedEndException(reader.getName(), getFrozenLocation(reader.getLocation())));
        }
        return constraint;
    }

    private String lang(final String initialValue) throws SimpleTypeException
    {
        return validateString(initialValue, NativeType.LANGUAGE);
    }

    /**
     * xs:length, xs:maxLength, xs:minLength
     */
    private XMLLength lengthTag(final XMLType type, final boolean minimum, final boolean maximum, final String contextName, final XMLSchemaModule module, final XMLStreamReader reader) throws XMLStreamException, SmComplexTypeException, AbortException
    {
        final XMLLength length = new XMLLength(type, getFrozenLocation(reader.getLocation()));
        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_VALUE.equals(localName))
                {
                    if (minimum)
                    {
                        try
                        {
                            length.minLength = nonNegativeInteger(reader.getAttributeValue(i));
                        }
                        catch (final SimpleTypeException e)
                        {
                            reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                        }
                    }
                    if (maximum)
                    {
                        try
                        {
                            length.maxLength = nonNegativeInteger(reader.getAttributeValue(i));
                        }
                        catch (final SimpleTypeException e)
                        {
                            reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                        }
                    }
                }
                else if (LN_FIXED.equals(localName))
                {
                    length.fixed = fixed(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
                }
                else if (LN_ID.equals(localName))
                {
                    length.id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                length.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, contextName, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        return length;
    }

    /**
     * xs:list
     */
    private void listTag(final XMLType listType, final XMLSchemaModule module, final XMLStreamReader reader, final XMLSchemaCache cache, final boolean redefine, final String targetNamespace) throws XMLStreamException, AbortException
    {
        listType.setBase(ANY_SIMPLE_TYPE, DerivationMethod.List);

        // Use this to detect missing both itemType attribute and <simpleType>
        // child.
        // Note that we have to perform the "clear" rather than assert the
        // emptiness because
        // we are attempting to collect the maximum amount of feedback from this
        // parse.
        listType.itemRef = null;

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ITEM_TYPE.equals(localName))
                {
                    try
                    {
                        listType.itemRef = typeRef(reader.getAttributeValue(i), LN_ITEM_TYPE, module.isChameleon(), targetNamespace, reader);
                        ensureReferenceType(listType.itemRef.getName(), reader.getLocation(), redefine, cache);
                    }
                    catch (final SmComplexTypeException e)
                    {
                        m_errors.error(e);
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // I don't think we want foreign attributes here, do we?
                // {any attributes with non-schema namespace}
            }
        }

        boolean missingST = true;
        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_SIMPLE_TYPE.equals(localName))
                            {
                                if (null == listType.itemRef)
                                {
                                    missingST = checkWxsElementMaxOccursUnity(missingST, LN_LIST, LN_SIMPLE_TYPE, reader.getLocation());
                                    listType.itemRef = simpleTypeLocalTag(new XMLScope(listType), cache, module, reader, false, targetNamespace);
                                    firstElement = false;
                                }
                                else
                                {
                                    m_errors.error(new SmSimpleTypeListException(getFrozenLocation(reader.getLocation())));
                                }
                            }
                            else if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_LIST, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_LIST, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }

        if (null == listType.itemRef)
        {
            m_errors.error(new SmSimpleTypeListException(getFrozenLocation(reader.getLocation())));
        }
    }

    /**
     * Parse the maxOccurs attribute.
     * 
     * @param strval
     *            The attribute value.
     * @param unbounded
     *            Determines whether "unbounded" is an acceptable value.
     * @param location
     *            The parser location.
     * @param elementName
     *            The element name containing the attribute.
     */
    private BigInteger maxOccurs(final String strval, final boolean unbounded, final Location location, final QName elementName) 
        throws AbortException
    {
        if (unbounded)
        {
            if ("unbounded".equals(strval))
            {
                return XMLParticle.UNBOUNDED;
            }
        }
        try
        {
            return nonNegativeInteger(strval);
        }
        catch (final SimpleTypeException e)
        {
            reportAttributeUseError(elementName, new QName(LN_MAX_OCCURS), location, e);
            return BigInteger.ONE;
        }
    }

    /**
     * xs:maxExclusive, xs:maxInclusive, xs:minExclusive, xs:minInclusive
     */
    private XMLMinMaxFacet minmaxTag(final XMLType simpleType, final FacetKind kind, final String elementName, final XMLStreamReader reader, final XMLSchemaModule module) throws XMLStreamException, SmComplexTypeException, AbortException
    {
        final XMLMinMaxFacet minmax = new XMLMinMaxFacet(kind, elementName, simpleType, getFrozenLocation(reader.getLocation()));

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_VALUE.equals(localName))
                {
                    minmax.value = reader.getAttributeValue(i);
                }
                else if (LN_FIXED.equals(localName))
                {
                    minmax.fixed = fixed(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
                }
                else if (LN_ID.equals(localName))
                {
                    minmax.id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                minmax.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, elementName, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(elementName, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        return minmax;
    }

    /**
     * Parse the minOccurs attribute.
     * 
     * @param strval
     *            The attribute value.
     * @param location
     *            The parser location.
     * @param elementName
     *            The element name containing the attribute.
     */
    private BigInteger minOccurs(final String strval, final Location location, QName elementName) 
        throws AbortException
    {
        try
        {
            return nonNegativeInteger(strval);
        }
        catch (final SimpleTypeException e)
        {
            m_errors.error(new SmAttributeUseException(elementName, new QName(LN_MIN_OCCURS), getFrozenLocation(location), e));
            return BigInteger.ONE;
        }
    }

    private String name(final String initialValue) 
        throws SimpleTypeException
    {
        final SimpleType atomicType = bootstrap.getAtomicType(NativeType.NCNAME);
        try
        {
            final List<XmlAtom> value = atomicType.validate(initialValue, atoms);
            if (value.size() > 0)
                return atoms.getString(value.get(0));
        }
        catch (DatatypeException dte)
        {
            throw new SimpleTypeException(initialValue, atomicType, dte);
        }
        return null;
    }

    private NamespaceConstraint namespaces(final String initialValue, final String targetNamespace) 
        throws SimpleTypeException
    {
        final String strval = initialValue.trim();

        if (strval.length() == 0)
        {
            // A reading of the specification might suggest an empty set
            // of namespaces that cannot be matched by any element.
            // The Microsoft interpretation of namespace="" is that
            // it is the same as not having the namespace attribute at all
            // so the default is ##any.
            // http://www.w3.org/Bugs/Public/show_bug.cgi?id=4066
            return NamespaceConstraint.Any();
        }
        else if (strval.equals("##any"))
        {
            return NamespaceConstraint.Any();
        }
        else if (strval.equals("##other"))
        {
            return NamespaceConstraint.exclude(targetNamespace);
        }
        else
        {
            final HashSet<String> namespaces = new HashSet<String>();
            final StringTokenizer tokenizer = new StringTokenizer(strval);
            while (tokenizer.hasMoreTokens())
            {
                final String token = tokenizer.nextToken();
                if (token.equals("##targetNamespace"))
                {
                    namespaces.add(targetNamespace);
                }
                else if (token.equals("##local"))
                {
                    namespaces.add(XMLConstants.NULL_NS_URI);
                }
                else if (!token.startsWith("##"))
                {
                    namespaces.add(token);
                }
                else
                {
                    final DatatypeException cause = new DatatypeException(strval, null);
                    throw new SimpleTypeException(strval, null, cause);
                }
            }
            return NamespaceConstraint.include(namespaces);
        }
    }

    private BigInteger nonNegativeInteger(final String initialValue) 
        throws SimpleTypeException
    {
      final SimpleType atomicType = bootstrap.getAtomicType(NativeType.NON_NEGATIVE_INTEGER);
      try
      {
          final List<XmlAtom> value = atomicType.validate(initialValue, atoms);
          if (value.size() > 0)
              return atoms.getInteger(value.get(0));
      }
      catch (DatatypeException dte)
      {
          throw new SimpleTypeException(initialValue, atomicType, dte);
      }
      return null;
    }

    /**
     * xs:notation
     */
    private XMLNotation notationTag(final XMLSchemaCache cache, final XMLStreamReader reader, final String targetNamespace, final XMLSchemaModule module) throws XMLStreamException, XMLNotationException, AbortException
    {
        final XMLNotation notation;
        try
        {
            notation = cache.registerNotation(requiredNCName(LN_NAME, targetNamespace, reader), getFrozenLocation(reader.getLocation()));
        }
        catch (final SchemaException e)
        {
            skipTag(reader);
            throw new XMLNotationException(e);
        }

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_NAME.equals(localName))
                {
                    // Already have it.
                }
                else if (LN_PUBLIC.equals(localName))
                {
                    try
                    {
                        notation.setPublicId(token(reader.getAttributeValue(i)));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_SYSTEM.equals(localName))
                {
                    notation.setSystemId(reader.getAttributeValue(i));
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                notation.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        if (null == notation.getPublicId() && null == notation.getSystemId())
        {
            // Either public or system should be defined.
            m_errors.error(new SmMissingAttributeException(reader.getName(), new QName(LN_PUBLIC), getFrozenLocation(reader.getLocation())));
        }

        annotationContent(LN_NOTATION, reader, module);

        return notation;
    }

    private QName optionalQName(final String initialValue, final String attributeName, final boolean isChameleon, final String targetNamespace, final XMLStreamReader reader) throws SmComplexTypeException
    {
        if (null != initialValue)
        {
            if (isChameleon)
            {
                try
                {
                    return resolveUsingTargetNamespace(initialValue, targetNamespace, reader.getNamespaceContext());
                }
                catch (final SimpleTypeException e)
                {
                    throw new SmAttributeUseException(reader.getName(), new QName(attributeName), getFrozenLocation(reader.getLocation()), e);
                }
            }
            else
            {
                try
                {
                    return resolveUsingXMLNamespaces(initialValue, reader.getNamespaceContext());
                }
                catch (final SimpleTypeException e)
                {
                    throw new SmAttributeUseException(reader.getName(), new QName(attributeName), getFrozenLocation(reader.getLocation()), e);
                }
            }
        }
        return null;
    }

    private void parseExternalModule(final XMLSchemaCache cache, final XMLSchemaModule parent, final Location location, final String namespace, final String schemaLocation, final ModuleKind moduleKind) throws AbortException
    {
//        PreCondition.assertArgumentNotNull(schemaLocation, "schemaLocation");

        if (null == m_catalog)
        {
            throw new AssertionError("catalog required for include, import or redefine.");
        }
        // TODO: here, we use StringToURIParser on the namespace. we should be able to pass a string, instead.
        String parentSystemId = parent.getSystemId();
        if (parentSystemId == null)
            parentSystemId = "";
        final URI catalogURI = m_catalog.resolveNamespaceAndSchemaLocation(StringToURIParser.parse(parentSystemId), namespace, schemaLocation);

        // If the catalogURI is null, we will not parse (obviously), and we will not raise an error.
        // If the missing schema is a problem, that problem will be evident during component resolution,
        // where an error will be raised.
        if (catalogURI != null)
        {
            try
            {
                if (null == m_resolver)
                {
                    throw new AssertionError("resolver required for include, import or redefine.");
                }
                final InputStream source = m_resolver.resolveInputStream(catalogURI);
                final XMLSchemaModule module = new XMLSchemaModule(parent, schemaLocation, catalogURI.toString());
                switch (moduleKind)
                {
                    case Include:
                    {
                        module.setIncludeFlag();
                    }
                    break;
                    case Import:
                    {
                        module.setImportFlag();
                    }
                    break;
                    case Redefine:
                    {
                        module.setRedefineFlag();
                    }
                    break;
                    default:
                    {
                        throw new AssertionError(moduleKind);
                    }
                }
                final XMLSchemaParser parser = new XMLSchemaParser(bootstrap, m_errors, m_catalog, m_resolver, m_processRepeatedNamespaces);
                parser.parse(catalogURI.toString(), source, cache, module);
            }
            catch (final IOException e)
            {
                // Do nothing. It's not an error.
            }
        }
    }

    /**
     * xs:pattern
     */
    private XMLPatternFacet patternTag(final XMLType simpleType, final XMLStreamReader reader, final XMLSchemaModule module) throws XMLStreamException, AbortException
    {
        final XMLPatternFacet pattern = new XMLPatternFacet(simpleType, getFrozenLocation(reader.getLocation()));

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_VALUE.equals(localName))
                {
                    pattern.value = reader.getAttributeValue(i);
                }
                else if (LN_ID.equals(localName))
                {
                    pattern.id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                pattern.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_PATTERN, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_PATTERN, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        return pattern;
    }

    private BigInteger positiveInteger(final String initialValue) 
        throws SimpleTypeException
    {
      final SimpleType atomicType = bootstrap.getAtomicType(NativeType.POSITIVE_INTEGER);
      try
      {
          final List<XmlAtom> value = atomicType.validate(initialValue, atoms);
          if (value.size() > 0)
              return atoms.getInteger(value.get(0));
      }
      catch (DatatypeException dte)
      {
          throw new SimpleTypeException(initialValue, atomicType, dte);
      }
      return null;
    }

    private ProcessContentsMode processContents(final String strval) throws SimpleTypeException
    {
        if ("lax".equals(strval))
        {
            return ProcessContentsMode.Lax;
        }
        else if ("skip".equals(strval))
        {
            return ProcessContentsMode.Skip;
        }
        else if ("strict".equals(strval))
        {
            return ProcessContentsMode.Strict;
        }
        else
        {
            final DatatypeException cause = new DatatypeException(strval, null);
            throw new SimpleTypeException(strval, null, cause);
        }
    }

    private boolean qualified(final String strval) throws SimpleTypeException
    {
        if ("qualified".equals(strval))
        {
            return true;
        }
        else if ("unqualified".equals(strval))
        {
            return false;
        }
        else
        {
            final DatatypeException cause = new DatatypeException(strval, null);
            throw new SimpleTypeException(strval, null, cause);
        }
    }

    private void redefineTag(final XMLSchemaCache cache, final XMLSchemaModule module, final XMLStreamReader reader, final String targetNamespace) throws XMLStreamException, AbortException
    {
        {
            String schemaLocation = null;
            final int attributeCount = reader.getAttributeCount();
            for (int i = 0; i < attributeCount; i++)
            {
                final String namespaceURI = reader.getAttributeNamespace(i);
                if (isGlobal(namespaceURI))
                {
                    final String localName = reader.getAttributeLocalName(i);
                    if (LN_SCHEMA_LOCATION.equals(localName))
                    {
                        schemaLocation = reader.getAttributeValue(i);
                    }
                    else if (LN_ID.equals(localName))
                    {
                        checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                    }
                    else
                    {
                        reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                    }
                }
                else if (isWXS(namespaceURI))
                {
                    reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
                else
                {
                    // like import and include, there's no way to expose these in the end.
                    // we might reconsider if we want to have the modules available somehow.
                    // {any attributes with non-schema namespace}
                }
            }

            if (schemaLocation != null)
            {
                parseExternalModule(cache, module, reader.getLocation(), null, schemaLocation, ModuleKind.Redefine);
            }
            else
            {
                m_errors.error(new SmMissingAttributeException(reader.getName(), new QName(LN_SCHEMA_LOCATION), getFrozenLocation(reader.getLocation())));
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_REDEFINE, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else if (LN_SIMPLE_TYPE.equals(localName))
                            {
                                try
                                {
                                    simpleTypeGlobalTag(reader, cache, module, true, targetNamespace);
                                }
                                catch (final XMLSimpleTypeException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else if (LN_COMPLEX_TYPE.equals(localName))
                            {
                                try
                                {
                                    complexTypeGlobalTag(reader, cache, module, true, targetNamespace);
                                }
                                catch (final XMLComplexTypeException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else if (LN_GROUP.equals(localName))
                            {
                                try
                                {
                                    groupTag(reader, cache, module, true, targetNamespace);
                                }
                                catch (final XMLModelGroupException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else if (LN_ATTRIBUTE_GROUP.equals(localName))
                            {
                                try
                                {
                                    attribGroupTag(reader, cache, module, true, targetNamespace);
                                }
                                catch (final XMLAttributeGroupException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_REDEFINE, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    private QName referenceOptional(final XMLStreamReader reader, final String localName, final XMLSchemaModule module, final String targetNamespace) throws AbortException
    {
        final String srcval = reader.getAttributeValue(null, localName);
        if (null != srcval)
        {
            if (module.isChameleon())
            {
                try
                {
                    return resolveUsingTargetNamespace(srcval, targetNamespace, reader.getNamespaceContext());
                }
                catch (final SimpleTypeException e)
                {
                    m_errors.error(new SmAttributeUseException(reader.getName(), new QName(localName), getFrozenLocation(reader.getLocation()), e));
                    return null;
                }
            }
            else
            {
                try
                {
                    return resolveUsingXMLNamespaces(srcval, reader.getNamespaceContext());
                }
                catch (final SimpleTypeException e)
                {
                    m_errors.error(new SmAttributeUseException(reader.getName(), new QName(localName), getFrozenLocation(reader.getLocation()), e));
                    return null;
                }
            }
        }
        else
        {
            return null;
        }
    }

    private void reportAttributeInGlobalNamespace(final QName elementName, final QName attributeName, final SrcFrozenLocation location) throws AbortException
    {
        m_errors.error(new CvcUnexpectedAttributeException(elementName, attributeName, location));
    }

    private void reportAttributeInWxsNamespace(final QName elementName, final QName attributeName, final SrcFrozenLocation location) throws AbortException
    {
        m_errors.error(new CvcUnexpectedAttributeException(elementName, attributeName, location));
    }

    private void reportAttributeInXmlNamespace(final QName elementName, final QName attributeName, final SrcFrozenLocation location) throws AbortException
    {
        m_errors.error(new CvcUnexpectedAttributeException(elementName, attributeName, location));
    }

    private void reportAttributeUseError(final QName elementName, final QName attributeName, final Location location, final SimpleTypeException cause) throws AbortException
    {
        m_errors.error(new SmAttributeUseException(elementName, attributeName, getFrozenLocation(location), cause));
    }

    private void reportUnexpectedElementTag(final String contextName, final QName unexpectedName, final Location location) throws AbortException
    {
        m_errors.error(new SmUnexpectedElementException(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, contextName), getFrozenLocation(location), unexpectedName, getFrozenLocation(location)));
    }

    private void reportUnexpectedEnd(final String contextName, final Location location) throws AbortException
    {
        m_errors.error(new SmUnexpectedEndException(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, contextName), getFrozenLocation(location)));
    }

    private void reportUnexpectedNonWhiteSpaceTextInElementOnlyContent(final String contextName, final String text, final Location location) throws AbortException
    {
        m_errors.error(new CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, contextName), text, getFrozenLocation(location)));
    }

    /**
     * Computes the name for a global schema component. <br/>
     * The "name" attribute is validated to exist and to be of type xs:NCName. <br/>
     * The name computed adopts the targetNamespace name of the module being parsed.
     */
    private QName requiredNCName(final String attributeName, final String targetNamespace, final XMLStreamReader reader) 
        throws SmComplexTypeException
    {
        final String name = reader.getAttributeValue(null, attributeName);
        if (name != null)
        {
            final SimpleType type = bootstrap.getAtomicType(NativeType.NCNAME);
            try
            {
                final List<XmlAtom> value = type.validate(name, atoms);
                if (value.size() > 0)
                    return new QName(targetNamespace, atoms.getString(value.get(0)));
            }
            catch (DatatypeException dte)
            {
                SimpleTypeException ste = new SimpleTypeException(name, type, dte);
                throw new SmAttributeUseException(reader.getName(), new QName(attributeName), getFrozenLocation(reader.getLocation()), ste);
            }
        }
        throw new SmMissingAttributeException(reader.getName(), new QName(attributeName), getFrozenLocation(reader.getLocation()));
    }

    /**
     * Obtains the required {@link #LN_BASE} or {@link #LN_REF} attribute as an expanded-QName.
     */
    private QName requiredQName(final String attributeName, final boolean isChameleon, final String targetNamespace, final XMLStreamReader reader) throws SmComplexTypeException
    {
        final String strval = reader.getAttributeValue(null, attributeName);
        if (null != strval)
        {
            return optionalQName(strval, attributeName, isChameleon, targetNamespace, reader);
        }
        else
        {
            throw new SmMissingAttributeException(reader.getName(), new QName(attributeName), getFrozenLocation(reader.getLocation()));
        }
    }

    private QName resolveUsingTargetNamespace(final String name, final String targetNamespace, final NamespaceContext ctxt) throws SimpleTypeException
    {
        PreCondition.assertArgumentNotNull(name);
        final String prefix = getPrefix(name);
        if (prefix.length() > 0)
        {
            final String namespaceURI = ctxt.getNamespaceURI(prefix);
            checkPrefixBound(prefix, namespaceURI, name);
            return new QName(conditionNamespaceURI(namespaceURI), getLocalName(name), prefix);
        }
        else
        {
            return new QName(targetNamespace, name);
        }
    }

    private QName resolveUsingXMLNamespaces(final String initialValue, final NamespaceContext ctxt) throws SimpleTypeException
    {
        PreCondition.assertArgumentNotNull(initialValue);
        final String prefix = getPrefix(initialValue);
        if (prefix.length() > 0)
        {
            final String namespaceURI = ctxt.getNamespaceURI(prefix);
            checkPrefixBound(prefix, namespaceURI, initialValue);
            return new QName(conditionNamespaceURI(namespaceURI), getLocalName(initialValue), prefix);
        }
        else
        {
            final String namespaceURI = ctxt.getNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX);
            checkPrefixBound(prefix, namespaceURI, initialValue);
            return new QName(conditionNamespaceURI(namespaceURI), initialValue);
        }
    }

    /**
     * xs:restriction (in xs:complexContent) <br/>
     * We don't return anything because this affects multiple aspects of the complex type.
     */
    private void restrictionInComplexContentTag(final XMLType complexType, final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace) throws XMLStreamException, AbortException
    {
        if (redefine)
        {
            try
            {
                final QName baseName = requiredQName(LN_BASE, module.isChameleon(), targetNamespace, reader);
                ensureReferenceType(baseName, reader.getLocation(), redefine, cache);
            }
            catch (final SchemaException e)
            {
                m_errors.error(e.getCause());
            }
        }
        else
        {
            try
            {
                baseTypeDefinitionInComplexContent(complexType, DerivationMethod.Restriction, reader, redefine, cache, module, targetNamespace);
            }
            catch (final SchemaException e)
            {
                m_errors.error(e);
            }
        }

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_BASE.equals(localName))
                {
                    // Already known.
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // this moves the attributes up to the type, which may or may not be right.
                complexType.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        final ContentModelMachine<String> machine = new ContentModelMachine<String>(restrictionInComplexContentTable, EPSILON);
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (!machine.step(localName))
                            {
                                reportUnexpectedElementTag(LN_RESTRICTION, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                            else
                            {
                                if (LN_GROUP.equals(localName))
                                {
                                    complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
                                    try
                                    {
                                        complexType.m_contentModel = groupParticleTag(new XMLScope(complexType), reader, cache, module, redefine, targetNamespace);
                                    }
                                    catch (final XMLModelGroupUseException e)
                                    {
                                        m_errors.error(e.getCause());
                                    }
                                }
                                else if (LN_ALL.equals(localName))
                                {
                                    complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
                                    try
                                    {
                                        complexType.m_contentModel = compositorOutsideGroupTag(ModelGroup.SmCompositor.All, new XMLScope(complexType), localName, reader, cache, module, redefine, targetNamespace);
                                    }
                                    catch (final XMLCompositorOutsideGroupException e)
                                    {
                                        m_errors.error(e.getCause());
                                    }
                                }
                                else if (LN_CHOICE.equals(localName))
                                {
                                    complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
                                    try
                                    {
                                        complexType.m_contentModel = compositorOutsideGroupTag(ModelGroup.SmCompositor.Choice, new XMLScope(complexType), localName, reader, cache, module, redefine, targetNamespace);
                                    }
                                    catch (final XMLCompositorOutsideGroupException e)
                                    {
                                        m_errors.error(e.getCause());
                                    }
                                }
                                else if (LN_SEQUENCE.equals(localName))
                                {
                                    complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
                                    try
                                    {
                                        complexType.m_contentModel = compositorOutsideGroupTag(ModelGroup.SmCompositor.Sequence, new XMLScope(complexType), localName, reader, cache, module, redefine, targetNamespace);
                                    }
                                    catch (final XMLCompositorOutsideGroupException e)
                                    {
                                        m_errors.error(e.getCause());
                                    }
                                }
                                else if (LN_ATTRIBUTE.equals(localName))
                                {
                                    attributeLocalTag(reader, cache, module, redefine, targetNamespace, complexType.getAttributeUses(), complexType.prohibited, new XMLScope(complexType));
                                }
                                else if (LN_ATTRIBUTE_GROUP.equals(localName))
                                {
                                    try
                                    {
                                        complexType.getAttributeGroups().add(attribGroupRefTag(reader, cache, module, targetNamespace));
                                    }
                                    catch (final XMLAttributeGroupException e)
                                    {
                                        m_errors.error(e.getCause());
                                    }
                                }
                                else if (LN_ANY_ATTRIBUTE.equals(localName))
                                {
                                    complexType.attributeWildcard = anyAttributeTag(reader, targetNamespace, module);
                                }
                                else if (LN_ANNOTATION.equals(localName))
                                {
                                    annotationTag(reader, module);
                                }
                                else
                                {
                                    throw new AssertionError(reader.getName());
                                }
                            }
                        }
                        else
                        {
                            skipTag(reader);
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    if (!machine.end())
                    {
                        reportUnexpectedEnd(LN_RESTRICTION, reader.getLocation());
                        skipTag(reader);
                    }
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    /**
     * xs:restriction (simple content)
     */
    private void restrictionInSimpleContentTag(final XMLType complexType, final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace) throws XMLStreamException, AbortException
    {
        // We're restriction a simple type by adding facets so it makes sense
        // that we are going to need an
        // anonymous type to hang the facets from
        complexType.simpleType = cache.registerAnonymousType(new XMLScope(complexType), getFrozenLocation(reader.getLocation()));
        complexType.simpleType.setSimpleFlag();

        try
        {
            final QName baseName = requiredQName(LN_BASE, module.isChameleon(), targetNamespace, reader);
            ensureReferenceType(baseName, reader.getLocation(), redefine, cache);
            final XMLTypeRef baseType = new XMLTypeRef(baseName);
            complexType.setBase(baseType, DerivationMethod.Restriction);
        }
        catch (final SchemaException e)
        {
            m_errors.error(e);
        }

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_BASE.equals(localName))
                {
                    // Aleady known.
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                complexType.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        final ContentModelMachine<String> machine = new ContentModelMachine<String>(restrictionInSimpleContentTable, EPSILON);
        boolean missingMaxInclusive = true;
        boolean missingMinExclusive = true;
        boolean missingMinInclusive = true;
        boolean missingLength = true;
        boolean missingMinLength = true;
        boolean missingMaxLength = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (!machine.step(localName))
                            {
                                reportUnexpectedElementTag(LN_COMPLEX_CONTENT, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                            else
                            {
                                if (LN_SIMPLE_TYPE.equals(localName))
                                {
                                    complexType.simpleType.setBase(simpleTypeLocalTag(new XMLScope(complexType), cache, module, reader, redefine, targetNamespace), DerivationMethod.Restriction);
                                }
                                else if (LN_ENUMERATION.equals(localName))
                                {
                                    complexType.simpleType.getEnumerations().add(enumerationTag(complexType.simpleType, reader, module));
                                }
                                else if (LN_MAX_EXCLUSIVE.equals(localName))
                                {
                                    try
                                    {
                                        complexType.simpleType.getMinMaxFacets().add(minmaxTag(complexType.simpleType, FacetKind.MaxExclusive, localName, reader, module));
                                    }
                                    catch (final SmComplexTypeException e)
                                    {
                                        m_errors.error(e);
                                    }
                                }
                                else if (LN_MAX_INCLUSIVE.equals(localName))
                                {
                                    missingMaxInclusive = checkWxsElementMaxOccursUnity(missingMaxInclusive, LN_RESTRICTION, LN_MAX_INCLUSIVE, reader.getLocation());
                                    try
                                    {
                                        complexType.simpleType.getMinMaxFacets().add(minmaxTag(complexType.simpleType, FacetKind.MaxInclusive, localName, reader, module));
                                    }
                                    catch (final SmComplexTypeException e)
                                    {
                                        m_errors.error(e);
                                    }
                                }
                                else if (LN_MIN_EXCLUSIVE.equals(localName))
                                {
                                    missingMinExclusive = checkWxsElementMaxOccursUnity(missingMinExclusive, LN_RESTRICTION, LN_MIN_EXCLUSIVE, reader.getLocation());
                                    try
                                    {
                                        complexType.simpleType.getMinMaxFacets().add(minmaxTag(complexType.simpleType, FacetKind.MinExclusive, localName, reader, module));
                                    }
                                    catch (final SmComplexTypeException e)
                                    {
                                        m_errors.error(e);
                                    }
                                }
                                else if (LN_MIN_INCLUSIVE.equals(localName))
                                {
                                    missingMinInclusive = checkWxsElementMaxOccursUnity(missingMinInclusive, LN_RESTRICTION, LN_MIN_INCLUSIVE, reader.getLocation());
                                    try
                                    {
                                        complexType.simpleType.getMinMaxFacets().add(minmaxTag(complexType.simpleType, FacetKind.MinInclusive, localName, reader, module));
                                    }
                                    catch (final SmComplexTypeException e)
                                    {
                                        m_errors.error(e);
                                    }
                                }
                                else if (LN_LENGTH.equals(localName))
                                {
                                    missingLength = checkWxsElementMaxOccursUnity(missingLength, LN_RESTRICTION, LN_LENGTH, reader.getLocation());
                                    try
                                    {
                                        complexType.simpleType.getLengthFacets().add(lengthTag(complexType, true, true, localName, module, reader));
                                    }
                                    catch (final SmComplexTypeException e)
                                    {
                                        m_errors.error(e);
                                    }
                                }
                                else if (LN_MIN_LENGTH.equals(localName))
                                {
                                    missingMinLength = checkWxsElementMaxOccursUnity(missingMinLength, LN_RESTRICTION, LN_MIN_LENGTH, reader.getLocation());
                                    try
                                    {
                                        complexType.simpleType.getLengthFacets().add(lengthTag(complexType, true, false, localName, module, reader));
                                    }
                                    catch (final SmComplexTypeException e)
                                    {
                                        m_errors.error(e);
                                    }
                                }
                                else if (LN_MAX_LENGTH.equals(localName))
                                {
                                    missingMaxLength = checkWxsElementMaxOccursUnity(missingMaxLength, LN_RESTRICTION, LN_MAX_LENGTH, reader.getLocation());
                                    try
                                    {
                                        complexType.simpleType.getLengthFacets().add(lengthTag(complexType, false, true, localName, module, reader));
                                    }
                                    catch (final SmComplexTypeException e)
                                    {
                                        m_errors.error(e);
                                    }
                                }
                                else if (LN_WHITE_SPACE.equals(localName))
                                {
                                    try
                                    {
                                        complexType.simpleType.setWhiteSpacePolicy(whiteSpaceTag(reader, module));
                                    }
                                    catch (final SmComplexTypeException e)
                                    {
                                        m_errors.error(e);
                                    }
                                }
                                else if (LN_PATTERN.equals(localName))
                                {
                                    complexType.simpleType.getPatternFacets().add(patternTag(complexType.simpleType, reader, module));
                                }
                                else if (LN_FRACTION_DIGITS.equals(localName))
                                {
                                    try
                                    {
                                        complexType.simpleType.getFractionDigitsFacets().add(fractionDigitsTag(complexType.simpleType, reader, module));
                                    }
                                    catch (final SmComplexTypeException e)
                                    {
                                        m_errors.error(e);
                                    }
                                }
                                else if (LN_TOTAL_DIGITS.equals(localName))
                                {
                                    complexType.simpleType.getTotalDigitsFacets().add(totalDigitsTag(complexType.simpleType, reader, module));
                                }
                                else if (LN_ATTRIBUTE.equals(localName))
                                {
                                    attributeLocalTag(reader, cache, module, redefine, targetNamespace, complexType.getAttributeUses(), complexType.prohibited, new XMLScope(complexType));
                                }
                                else if (LN_ATTRIBUTE_GROUP.equals(localName))
                                {
                                    try
                                    {
                                        complexType.getAttributeGroups().add(attribGroupRefTag(reader, cache, module, targetNamespace));
                                    }
                                    catch (final XMLAttributeGroupException e)
                                    {
                                        m_errors.error(e.getCause());
                                    }
                                }
                                else if (LN_ANY_ATTRIBUTE.equals(localName))
                                {
                                    complexType.attributeWildcard = anyAttributeTag(reader, targetNamespace, module);
                                }
                                else if (LN_ANNOTATION.equals(localName))
                                {
                                    annotationTag(reader, module);
                                }
                                else
                                {
                                    throw new AssertionError(reader.getName());
                                }
                            }
                        }
                        else
                        {
                            skipTag(reader);
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    if (!machine.end())
                    {
                        reportUnexpectedEnd(LN_RESTRICTION, reader.getLocation());
                    }
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    /**
     * xs:restriction (simple type)
     */
    private void restrictionTag(final XMLType simpleType, final XMLSchemaModule module, final XMLStreamReader reader, final XMLSchemaCache cache, final boolean redefine, final String targetNamespace) throws XMLStreamException, AbortException
    {
        if (!redefine)
        {
        }
        else
        {
        }

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_BASE.equals(localName))
                {
                    try
                    {
                        final QName name = requiredQName(LN_BASE, module.isChameleon(), targetNamespace, reader);
                        if (!redefine)
                        {
                            ensureReferenceType(name, reader.getLocation(), redefine, cache);
                            simpleType.setBase(new XMLTypeRef(name), DerivationMethod.Restriction);
                        }
                    }
                    catch (final SmComplexTypeException e)
                    {
                        m_errors.error(e);
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // i'm pretty sure we don't want this. ?
                // {any attributes with non-schema namespace}
            }
        }

        boolean missingMaxExclusive = true;
        boolean missingMaxInclusive = true;
        boolean missingMinExclusive = true;
        boolean missingMinInclusive = true;
        boolean missingLength = true;
        boolean missingMinLength = true;
        boolean missingMaxLength = true;

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_SIMPLE_TYPE.equals(localName))
                            {
                                if (null == simpleType.getBaseRef())
                                {
                                    final XMLTypeRef baseType = simpleTypeLocalTag(new XMLScope(simpleType), cache, module, reader, redefine, targetNamespace);
                                    simpleType.setBase(baseType, DerivationMethod.Restriction);
                                }
                                else
                                {
                                    // Already set by either a "base" attribute
                                    // or a preceding xs:simpleType sibling.
                                    m_errors.error(new SmSimpleTypeRestrictionException(getFrozenLocation(reader.getLocation())));
                                }
                                firstElement = false;
                            }
                            else if (LN_ENUMERATION.equals(localName))
                            {
                                simpleType.getEnumerations().add(enumerationTag(simpleType, reader, module));
                                firstElement = false;
                            }
                            else if (LN_MAX_EXCLUSIVE.equals(localName))
                            {
                                missingMaxExclusive = checkWxsElementMaxOccursUnity(missingMaxExclusive, LN_RESTRICTION, LN_MAX_EXCLUSIVE, reader.getLocation());
                                try
                                {
                                    simpleType.getMinMaxFacets().add(minmaxTag(simpleType, FacetKind.MaxExclusive, localName, reader, module));
                                }
                                catch (final SmComplexTypeException e)
                                {
                                    m_errors.error(e);
                                }
                                firstElement = false;
                            }
                            else if (LN_MAX_INCLUSIVE.equals(localName))
                            {
                                missingMaxInclusive = checkWxsElementMaxOccursUnity(missingMaxInclusive, LN_RESTRICTION, LN_MAX_INCLUSIVE, reader.getLocation());
                                try
                                {
                                    simpleType.getMinMaxFacets().add(minmaxTag(simpleType, FacetKind.MaxInclusive, localName, reader, module));
                                }
                                catch (final SmComplexTypeException e)
                                {
                                    m_errors.error(e);
                                }
                                firstElement = false;
                            }
                            else if (LN_MIN_EXCLUSIVE.equals(localName))
                            {
                                missingMinExclusive = checkWxsElementMaxOccursUnity(missingMinExclusive, LN_RESTRICTION, LN_MIN_EXCLUSIVE, reader.getLocation());
                                try
                                {
                                    simpleType.getMinMaxFacets().add(minmaxTag(simpleType, FacetKind.MinExclusive, localName, reader, module));
                                }
                                catch (final SmComplexTypeException e)
                                {
                                    m_errors.error(e);
                                }
                                firstElement = false;
                            }
                            else if (LN_MIN_INCLUSIVE.equals(localName))
                            {
                                missingMinInclusive = checkWxsElementMaxOccursUnity(missingMinInclusive, LN_RESTRICTION, LN_MIN_INCLUSIVE, reader.getLocation());
                                try
                                {
                                    simpleType.getMinMaxFacets().add(minmaxTag(simpleType, FacetKind.MinInclusive, localName, reader, module));
                                }
                                catch (final SmComplexTypeException e)
                                {
                                    m_errors.error(e);
                                }
                                firstElement = false;
                            }
                            else if (LN_LENGTH.equals(localName))
                            {
                                missingLength = checkWxsElementMaxOccursUnity(missingLength, LN_RESTRICTION, LN_LENGTH, reader.getLocation());
                                try
                                {
                                    simpleType.getLengthFacets().add(lengthTag(simpleType, true, true, localName, module, reader));
                                }
                                catch (final SmComplexTypeException e)
                                {
                                    m_errors.error(e);
                                }
                                firstElement = false;
                            }
                            else if (LN_MIN_LENGTH.equals(localName))
                            {
                                missingMinLength = checkWxsElementMaxOccursUnity(missingMinLength, LN_RESTRICTION, LN_MIN_LENGTH, reader.getLocation());
                                try
                                {
                                    simpleType.getLengthFacets().add(lengthTag(simpleType, true, false, localName, module, reader));
                                }
                                catch (final SmComplexTypeException e)
                                {
                                    m_errors.error(e);
                                }
                                firstElement = false;
                            }
                            else if (LN_MAX_LENGTH.equals(localName))
                            {
                                missingMaxLength = checkWxsElementMaxOccursUnity(missingMaxLength, LN_RESTRICTION, LN_MAX_LENGTH, reader.getLocation());
                                try
                                {
                                    simpleType.getLengthFacets().add(lengthTag(simpleType, false, true, localName, module, reader));
                                }
                                catch (final SmComplexTypeException e)
                                {
                                    m_errors.error(e);
                                }
                                firstElement = false;
                            }
                            else if (LN_WHITE_SPACE.equals(localName))
                            {
                                try
                                {
                                    simpleType.setWhiteSpacePolicy(whiteSpaceTag(reader, module));
                                }
                                catch (final SmComplexTypeException e)
                                {
                                    m_errors.error(e);
                                }
                                firstElement = false;
                            }
                            else if (LN_PATTERN.equals(localName))
                            {
                                simpleType.getPatternFacets().add(patternTag(simpleType, reader, module));
                                firstElement = false;
                            }
                            else if (LN_FRACTION_DIGITS.equals(localName))
                            {
                                try
                                {
                                    simpleType.getFractionDigitsFacets().add(fractionDigitsTag(simpleType, reader, module));
                                }
                                catch (final SmComplexTypeException e)
                                {
                                    m_errors.error(e);
                                }
                                firstElement = false;
                            }
                            else if (LN_TOTAL_DIGITS.equals(localName))
                            {
                                simpleType.getTotalDigitsFacets().add(totalDigitsTag(simpleType, reader, module));
                                firstElement = false;
                            }
                            else if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_RESTRICTION, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_RESTRICTION, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    private void schemaTag(final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module) throws XMLStreamException, AbortException
    {
        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_TARGET_NAMESPACE.equals(localName))
                {
                    module.setTargetNamespace(reader.getAttributeValue(i));
                    if (!(module.isInclude() || module.isRedefine()) && 
                        !m_processRepeatedNamespaces && 
                        cache.m_seenNamespaces.contains(module.getTargetNamespace()))
                    {
                        // Ignore this schema.
                        skipTag(reader);
                        return;
                    }
                }
                else if ("elementFormDefault".equals(localName))
                {
                    try
                    {
                        module.elementQualified = qualified(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if ("attributeFormDefault".equals(localName))
                {
                    try
                    {
                        module.attributeQualified = qualified(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if ("blockDefault".equals(localName))
                {
                    try
                    {
                        control(reader.getAttributeValue(i), EnumSet.of(DerivationMethod.Extension, DerivationMethod.Restriction, DerivationMethod.Substitution), module.blockDefault);
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if ("finalDefault".equals(localName))
                {
                    try
                    {
                        control(reader.getAttributeValue(i), EnumSet.of(DerivationMethod.Extension, DerivationMethod.Restriction), module.finalDefault);
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    module.m_id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if ("version".equals(localName))
                {
                    try
                    {
                        module.m_version = token(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (XMLConstants.XML_NS_URI.equals(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if ("lang".equals(localName))
                {
                    try
                    {
                        module.m_lang = lang(reader.getAttributeValue(i));
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else
                {
                    reportAttributeInXmlNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // sadly, because this is a module, it's the same problem as for
                // import and include and redefine, so we can't report foreign attributes usefully.
                // {any attributes with non-schema namespace}
            }
        }

        if (module.isRedefine() || module.isInclude())
        {
            if (module.getTargetNamespace() != null)
            {
                if ((module.getContainingModule().getTargetNamespace() == null) || (!module.getContainingModule().getTargetNamespace().equals(module.getTargetNamespace())))
                {
                    if (module.isRedefine())
                    {
                        m_errors.error(new SmRedefinitionNamespaceMismatchException(getFrozenLocation(reader.getLocation())));
                    }
                    else
                    {
                        m_errors.error(new SmInclusionNamespaceMismatchException(getFrozenLocation(reader.getLocation())));
                    }
                    skipTag(reader);
                    return;
                }
            }
        }

        // Knowing the local targetNamespace, and the ancestors of the module,
        // we can compute the targetNamespace.
        final String targetNamespace = module.computeTargetNamespace();

        if (module.getTargetNamespace() != null)
        {
            cache.m_seenNamespaces.add(module.getTargetNamespace());
        }

        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_COMPLEX_TYPE.equals(localName))
                            {
                                try
                                {
                                    final XMLType complexType = complexTypeGlobalTag(reader, cache, module, false, targetNamespace);
                                    cache.m_globalTypes.put(complexType.getName(), complexType);
                                }
                                catch (final XMLComplexTypeException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                            }
                            else if (LN_SIMPLE_TYPE.equals(localName))
                            {
                                try
                                {
                                    final XMLType simpleType = simpleTypeGlobalTag(reader, cache, module, false, targetNamespace);
                                    cache.m_globalTypes.put(simpleType.getName(), simpleType);
                                }
                                catch (final XMLSimpleTypeException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                            }
                            else if (LN_ELEMENT.equals(localName))
                            {
                                try
                                {
                                    final XMLElement element = elementTag(reader, cache, module, targetNamespace);
                                    cache.m_elements.put(element.getName(), element);
                                }
                                catch (final XMLElementException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                            }
                            else if (LN_ATTRIBUTE.equals(localName))
                            {
                                try
                                {
                                    final XMLAttribute attribute = attributeTag(reader, cache, module, targetNamespace);
                                    cache.m_attributes.put(attribute.getName(), attribute);
                                }
                                catch (final XMLAttributeException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                            }
                            else if (LN_GROUP.equals(localName))
                            {
                                try
                                {
                                    final XMLModelGroup group = groupTag(reader, cache, module, false, targetNamespace);
                                    cache.m_modelGroups.put(group.getName(), group);
                                }
                                catch (final XMLModelGroupException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                            }
                            else if (LN_ATTRIBUTE_GROUP.equals(localName))
                            {
                                try
                                {
                                    final XMLAttributeGroup attributeGroup = attribGroupTag(reader, cache, module, false, targetNamespace);
                                    cache.m_attributeGroups.put(attributeGroup.getName(), attributeGroup);
                                }
                                catch (final XMLAttributeGroupException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                            }
                            else if (LN_INCLUDE.equals(localName))
                            {
                                includeTag(cache, module, reader);
                            }
                            else if (LN_IMPORT.equals(localName))
                            {
                                importTag(cache, module, reader, targetNamespace);
                            }
                            else if (LN_REDEFINE.equals(localName))
                            {
                                redefineTag(cache, module, reader, targetNamespace);
                            }
                            else if (LN_NOTATION.equals(localName))
                            {
                                try
                                {
                                    /* final NotationImpl notation = */
                                    notationTag(cache, reader, targetNamespace, module);
                                }
                                catch (final XMLNotationException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                            }
                            else if (LN_ANNOTATION.equals(localName))
                            {
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_SCHEMA, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                    // Ignore.
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    private RestrictedXPath selectorTag(final XMLStreamReader reader, final XMLSchemaModule module) throws XMLStreamException, XMLSelectorException, AbortException
    {
        RestrictedXPath xpath = null;
        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_XPATH.equals(localName))
                {
                    final String original = reader.getAttributeValue(i);
                    try
                    {
                        xpath = xpath(original, reader.getLocation(), reader.getName());
                    }
                    catch (final SmAttributeUseException e)
                    {
                        skipTag(reader);
                        throw new XMLSelectorException(e);
                    }
                    if (xpath.isAttribute())
                    {
                        final DatatypeException dte = new DatatypeException(original, null);
                        final SimpleTypeException ste = new SimpleTypeException(original, null, dte);
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), ste);
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // nowhere to put foreign attributes.
                // {any attributes with non-schema namespace}
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_SELECTOR, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_SELECTOR, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        return xpath;
    }

    /**
     * xs:simpleContent
     */
    private void simpleContentTag(final XMLType complexType, final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace) throws XMLStreamException, AbortException
    {
        complexType.m_contentKind = XMLContentTypeKind.Simple;

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // the only place to put foreign attributes is the parent. we don't do that; discard.
            }
        }

        final ContentModelMachine<String> machine = new ContentModelMachine<String>(simpleContentTable, EPSILON);
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (!machine.step(localName))
                            {
                                reportUnexpectedElementTag(LN_SIMPLE_CONTENT, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                            else
                            {
                                if (LN_EXTENSION.equals(localName))
                                {
                                    extensionInSimpleContentTag(complexType, module, reader, cache, redefine, targetNamespace);
                                }
                                else if (LN_RESTRICTION.equals(localName))
                                {
                                    restrictionInSimpleContentTag(complexType, reader, cache, module, redefine, targetNamespace);
                                }
                                else if (LN_ANNOTATION.equals(localName))
                                {
                                    annotationTag(reader, module);
                                }
                                else
                                {
                                    throw new AssertionError(reader.getName());
                                }
                            }
                        }
                        else
                        {
                            skipTag(reader);
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    if (!machine.end())
                    {
                        reportUnexpectedEnd(LN_SIMPLE_CONTENT, reader.getLocation());
                    }
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    /**
     * Content for an xs:simpleType (either global or local definition). <br>
     * Content: (xs:annotation?, (xs:restriction | xs:list | xs:union))
     */
    private void simpleTypeContentTag(final XMLType simpleType, final XMLSchemaModule module, final XMLStreamReader reader, final XMLSchemaCache cache, final boolean redefine, final String targetNamespace) throws XMLStreamException, AbortException
    {
        // Derivation property must be null so that we check that we got the
        // required child elements.
        boolean firstElement = true;
        boolean missingRLU = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_RESTRICTION.equals(localName))
                            {
                                missingRLU = checkWxsElementMaxOccursUnity(missingRLU, LN_SIMPLE_TYPE, LN_RESTRICTION, reader.getLocation());
                                restrictionTag(simpleType, module, reader, cache, redefine, targetNamespace);
                                firstElement = false;
                            }
                            else if (LN_LIST.equals(localName))
                            {
                                missingRLU = checkWxsElementMaxOccursUnity(missingRLU, LN_SIMPLE_TYPE, LN_LIST, reader.getLocation());
                                listTag(simpleType, module, reader, cache, redefine, targetNamespace);
                                firstElement = false;
                            }
                            else if (LN_UNION.equals(localName))
                            {
                                missingRLU = checkWxsElementMaxOccursUnity(missingRLU, LN_SIMPLE_TYPE, LN_UNION, reader.getLocation());
                                unionTag(simpleType, module, reader, cache, redefine, targetNamespace);
                                firstElement = false;
                            }
                            else if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_SIMPLE_CONTENT, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_SIMPLE_CONTENT, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                    // Ignore.
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }

        if (missingRLU)
        {
            // Expecting xs:restriction | xs:list | xs:union
            m_errors.error(new SmUnexpectedEndException(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, LN_SIMPLE_TYPE), getFrozenLocation(reader.getLocation())));
        }
    }

    /**
     * xs:simpleType (global definition)
     */
    private XMLType simpleTypeGlobalTag(final XMLStreamReader reader, final XMLSchemaCache cache, final XMLSchemaModule module, final boolean redefine, final String targetNamespace) throws XMLStreamException, XMLSimpleTypeException, AbortException
    {
        final XMLType simpleType;
        if (!redefine)
        {
            try
            {
                simpleType = cache.registerType(requiredNCName(LN_NAME, targetNamespace, reader), getFrozenLocation(reader.getLocation()));
            }
            catch (final SchemaException e)
            {
                skipTag(reader);
                throw new XMLSimpleTypeException(e);
            }
        }
        else
        {
            try
            {
                simpleType = cache.dereferenceType(requiredNCName(LN_NAME, targetNamespace, reader), reader.getLocation(), redefine);
            }
            catch (final SchemaException e)
            {
                skipTag(reader);
                throw new XMLSimpleTypeException(e);
            }
        }
        simpleType.setSimpleFlag();

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_FINAL.equals(localName))
                {
                    try
                    {
                        control(reader.getAttributeValue(i), EnumSet.of(DerivationMethod.List, DerivationMethod.Union, DerivationMethod.Restriction), simpleType.getFinal());
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if (LN_NAME.equals(localName))
                {
                    // Already known.
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                simpleType.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        if (!redefine)
        {
        }
        else
        {
        }
        simpleTypeContentTag(simpleType, module, reader, cache, redefine, targetNamespace);
        return simpleType;
    }

    /**
     * xs:simpleType (local definition)
     */
    private XMLTypeRef simpleTypeLocalTag(final XMLScope scope, final XMLSchemaCache cache, final XMLSchemaModule module, final XMLStreamReader reader, final boolean redefine, final String targetNamespace) throws XMLStreamException, AbortException
    {
        final XMLType simpleType = cache.registerAnonymousType(scope, getFrozenLocation(reader.getLocation()));
        simpleType.setSimpleFlag();

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                simpleType.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        simpleTypeContentTag(simpleType, module, reader, cache, redefine, targetNamespace);

        return new XMLTypeRef(simpleType);
    }

    /**
     * Skips the remaining content and end element (used during development).
     */
    private void skipTag(final XMLStreamReader reader) throws XMLStreamException
    {
        boolean done = false;

        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    skipTag(reader);
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                {
                }
                break;
                case XMLStreamConstants.COMMENT:
                {
                }
                break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                {
                }
                break;
                case XMLStreamConstants.SPACE: //ignorable whitespace is ignorable; who knew?
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
    }

    private String token(final String initialValue) 
        throws SimpleTypeException
    {
        // this is really kinda silly, you know.  major overkill to insure
        // that whitespace is handled.
      final SimpleType atomicType = bootstrap.getAtomicType(NativeType.TOKEN);
      try
      {
          final List<XmlAtom> value = atomicType.validate(initialValue, atoms);
          if (value.size() > 0)
              return atoms.getString(value.get(0));
      }
      catch (DatatypeException dte)
      {
          throw new SimpleTypeException(initialValue, atomicType, dte);
      }
      return null;
    }

    /**
     * xs:totalDigits
     */
    private XMLTotalDigitsFacet totalDigitsTag(final XMLType simpleType, final XMLStreamReader reader, final XMLSchemaModule module) throws XMLStreamException, AbortException
    {
        final XMLTotalDigitsFacet facet = new XMLTotalDigitsFacet(simpleType, getFrozenLocation(reader.getLocation()));

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_ID.equals(localName))
                {
                    facet.id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else if (LN_FIXED.equals(localName))
                {
                    try
                    {
                        facet.fixed = fixed(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
                    }
                    catch (final SmAttributeUseException e)
                    {
                        m_errors.error(e);
                    }
                }
                else if (LN_VALUE.equals(localName))
                {
                    final String strval = reader.getAttributeValue(i);
                    try
                    {
                        facet.value = positiveInteger(strval);
                    }
                    catch (final SimpleTypeException e)
                    {
                        reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
                    }
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                facet.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_TOTAL_DIGITS, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_TOTAL_DIGITS, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        return facet;
    }

    private boolean trueOrFalse(final String initialValue) throws SimpleTypeException
    {
        // this one's actually worthwhile, though.
        final SimpleType type = bootstrap.getAtomicType(NativeType.BOOLEAN);
        try
        {
            final List<XmlAtom> value = type.validate(initialValue, atoms);
            if (value.size() > 0)
                return atoms.getBoolean(value.get(0));
        }
        catch (DatatypeException dte)
        {
            throw new SimpleTypeException(initialValue, type, dte);
        }
        return false;
    }

    /**
     * Obtains a {@link #LN_TYPE}, {@link #LN_ITEM_TYPE} or tokenized {@link #LN_MEMBER_TYPES} attribute value as type
     * reference.
     */
    private XMLTypeRef typeRef(final String initialValue, final String attributeName, final boolean isChameleon, final String targetNamespace, final XMLStreamReader reader) throws SmComplexTypeException
    {
        if (null != initialValue)
        {
            return new XMLTypeRef(optionalQName(initialValue, attributeName, isChameleon, targetNamespace, reader));
        }
        else
        {
            throw new SmMissingAttributeException(reader.getName(), new QName(attributeName), getFrozenLocation(reader.getLocation()));
        }
    }

    private void unionTag(final XMLType unionType, final XMLSchemaModule module, final XMLStreamReader reader, final XMLSchemaCache cache, final boolean redefine, final String targetNamespace) throws XMLStreamException, AbortException
    {
        unionType.setBase(ANY_SIMPLE_TYPE, DerivationMethod.Union);

        // Use this to detect missing both memberTypes attribute and
        // <simpleType> child.
        // Note that we have to perform the "clear" rather than assert the
        // emptiness because
        // we are attempting to collect the maximum amount of feedback from this
        // parse.
        unionType.memberRefs.clear();

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_MEMBER_TYPES.equals(localName))
                {
                    final StringTokenizer tokenizer = new StringTokenizer(reader.getAttributeValue(i), " ");
                    while (tokenizer.hasMoreTokens())
                    {
                        final String token = tokenizer.nextToken();
                        try
                        {
                            final XMLTypeRef memberType = typeRef(token, LN_MEMBER_TYPES, module.isChameleon(), targetNamespace, reader);
                            ensureReferenceType(memberType.getName(), reader.getLocation(), redefine, cache);
                            unionType.memberRefs.add(memberType);
                        }
                        catch (final SmComplexTypeException e)
                        {
                            m_errors.error(e);
                        }
                    }
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                unionType.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_SIMPLE_TYPE.equals(localName))
                            {
                                unionType.memberRefs.add(simpleTypeLocalTag(new XMLScope(unionType), cache, module, reader, false, targetNamespace));
                                firstElement = false;
                            }
                            else if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_UNION, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_UNION, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        if (unionType.memberRefs.isEmpty())
        {
            m_errors.error(new SmSimpleTypeUnionException(getFrozenLocation(reader.getLocation())));
        }
    }

    private XMLIdentityConstraint uniqueTag(final XMLSchemaCache cache, final XMLStreamReader reader, final String targetNamespace, final XMLSchemaModule module) throws XMLStreamException, XMLIdentityConstraintException, AbortException
    {
        final XMLIdentityConstraint unique;
        try
        {
            final QName name = requiredNCName(LN_NAME, targetNamespace, reader);
            module.registerIdentityConstraintName(name, reader.getLocation());
            unique = cache.registerIdentityConstraint(IdentityConstraintKind.Unique, name, getFrozenLocation(reader.getLocation()));
        }
        catch (final SchemaException e)
        {
            skipTag(reader);
            throw new XMLIdentityConstraintException(e);
        }

        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_NAME.equals(localName))
                {
                    // Already known.
                }
                else if (LN_ID.equals(localName))
                {
                    checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                unique.foreignAttributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }

        boolean missingSelector = true;
        boolean missingFields = true;
        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_UNIQUE, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else if (LN_SELECTOR.equals(localName))
                            {
                                missingSelector = checkWxsElementMaxOccursUnity(missingSelector, LN_UNIQUE, LN_SELECTOR, reader.getLocation());
                                try
                                {
                                    unique.selector = selectorTag(reader, module);
                                }
                                catch (final XMLSelectorException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else if (LN_FIELD.equals(localName))
                            {
                                if (missingSelector)
                                {
                                    m_errors.error(new SmUnexpectedElementException(reader.getName(), getFrozenLocation(reader.getLocation()), new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, LN_FIELD), getFrozenLocation(reader.getLocation())));
                                }
                                missingFields = false;
                                try
                                {
                                    unique.fields.add(fieldTag(reader, module));
                                }
                                catch (final XMLFieldException e)
                                {
                                    m_errors.error(e.getCause());
                                }
                                firstElement = false;
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_UNIQUE, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        if (missingFields)
        {
            m_errors.error(new SmUnexpectedEndException(reader.getName(), getFrozenLocation(reader.getLocation())));
        }
        return unique;
    }

    private XMLCardinality use(final String strval) throws SimpleTypeException
    {
        if ("optional".equals(strval))
        {
            return XMLCardinality.OPTIONAL;
        }
        else if ("prohibited".equals(strval))
        {
            return XMLCardinality.NONE;
        }
        else if ("required".equals(strval))
        {
            return XMLCardinality.EXACTLY_ONE;
        }
        else
        {
            final DatatypeException dte = new DatatypeException(strval, null);
            throw new SimpleTypeException(strval, null, dte);
        }
    }

    private String validateString(final String initialValue, final NativeType derivedType) 
        throws SimpleTypeException
    {
        // under what circumstances does one *need* to validate a string?
      final SimpleType type = bootstrap.getAtomicType(derivedType);
      try
      {
          final List<XmlAtom> value = type.validate(initialValue, atoms);
          if (value.size() > 0)
              return atoms.getString(value.get(0));
      }
      catch (DatatypeException dte)
      {
          throw new SimpleTypeException(initialValue, type, dte);
      }
      return null;
    }

    private WhiteSpacePolicy whiteSpaceTag(final XMLStreamReader reader, final XMLSchemaModule module) throws XMLStreamException, SmComplexTypeException, AbortException
    {
        WhiteSpacePolicy policy = WhiteSpacePolicy.PRESERVE;
        final int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++)
        {
            final String namespaceURI = reader.getAttributeNamespace(i);
            if (isGlobal(namespaceURI))
            {
                final String localName = reader.getAttributeLocalName(i);
                if (LN_VALUE.equals(localName))
                {
                    final String value = reader.getAttributeValue(i);
                    if ("preserve".equals(value))
                    {
                        policy = WhiteSpacePolicy.PRESERVE;
                    }
                    else if ("replace".equals(value))
                    {
                        policy = WhiteSpacePolicy.REPLACE;
                    }
                    else if ("collapse".equals(value))
                    {
                        policy = WhiteSpacePolicy.COLLAPSE;
                    }
                    else
                    {
                        final DatatypeException dte = new DatatypeException(value, null);
                        final SimpleTypeException ste = new SimpleTypeException(value, null, dte);
                        throw new SmAttributeUseException(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()), ste);
                    }
                }
                else if (LN_FIXED.equals(localName))
                {
                    // TODO: how can we handle the 'fixed' attribute on the whitespace facet
                    // in the architecture of schema model that defines it as an enumeration?
                    // a completely correct solution would check all ancestor types when encountering
                    // a whitespace policy change in a subtype, and would mandate creation of three
                    // additional values for the WhitespacePolicy enum (preserve_fixed, replace_fixed,
                    // collapse_fixed).
                    // All of that work would be useful once in a few million encounters of the
                    // whitespace tag. The shorter way to 'fix' this is to ignore the fixed attribute:
                    // neither throwing an exception, nor paying attention when it's been set to fixed,
                    // nor checking whether ancestors have a fixed whitespace policy.
                    //
                    // Which is what this empty block now achieves. Win?
                }
                else
                {
                    reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
                }
            }
            else if (isWXS(namespaceURI))
            {
                reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
            }
            else
            {
                // pointless to preserve; there's no place to surface these.
                // {any attributes with non-schema namespace}
            }
        }

        boolean firstElement = true;
        boolean done = false;
        while (!done)
        {
            final int event = reader.next();

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:
                {
                    m_pms.pushContext();
                    try
                    {
                        copyNamespaces(reader, m_pms);
                        if (isWXS(reader.getNamespaceURI()))
                        {
                            final String localName = reader.getLocalName();
                            if (LN_ANNOTATION.equals(localName))
                            {
                                firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_WHITE_SPACE, reader.getLocation());
                                annotationTag(reader, module);
                            }
                            else
                            {
                                reportUnexpectedElementTag(LN_WHITE_SPACE, reader.getName(), reader.getLocation());
                                skipTag(reader);
                            }
                        }
                        else
                        {
                            skipTag(reader);
                            firstElement = false;
                        }
                    }
                    finally
                    {
                        m_pms.popContext();
                    }
                }
                break;
                case XMLStreamConstants.END_ELEMENT:
                {
                    done = true;
                }
                break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                case XMLStreamConstants.SPACE:
                {
                }
                break;
                default:
                {
                    throw new UnsupportedOperationException(Integer.toString(event));
                }
            }
        }
        return policy;
    }

    private RestrictedXPath xpath(final String strval, final Location location, final QName elementName) throws SmAttributeUseException
    {
        try
        {
            final String token = token(strval);
            return m_xp.parseXPath(token, m_pms);
        }
        catch (final SimpleTypeException e)
        {
            throw new SmAttributeUseException(elementName, new QName(LN_XPATH), getFrozenLocation(location), e);
        }
    }

    private enum ModuleKind
    {
        Import, Include, Redefine
    }

    private static SrcFrozenLocation getFrozenLocation(final Location location)
    {
        PreCondition.assertArgumentNotNull(location, "location");
        return new SrcFrozenLocation(location);
    }

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

    /**
     * (xs:annotation?, xs:simpleType)
     */
    private static ContentModelTable<String> makeAttributeTable()
    {
        final ContentModelTable<String> table = new ContentModelTable<String>();

        final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
        ZERO.put(LN_ANNOTATION, 1);
        ZERO.put(LN_SIMPLE_TYPE, ContentModelTable.END);
        ZERO.put(EPSILON, ContentModelTable.END);

        final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
        ONE.put(LN_SIMPLE_TYPE, ContentModelTable.END);
        ONE.put(EPSILON, ContentModelTable.END);

        table.put(0, ZERO);
        table.put(1, ONE);

        return table;
    }

    private static ContentModelTable<String> makeComplexContentTable()
    {
        final ContentModelTable<String> table = new ContentModelTable<String>();

        final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
        ZERO.put(LN_ANNOTATION, 1);
        ZERO.put(LN_RESTRICTION, ContentModelTable.END);
        ZERO.put(LN_EXTENSION, ContentModelTable.END);

        final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
        ONE.put(LN_RESTRICTION, ContentModelTable.END);
        ONE.put(LN_EXTENSION, ContentModelTable.END);

        table.put(0, ZERO);
        table.put(1, ONE);

        return table;
    }

    private static ContentModelTable<String> makeComplexTypeTable()
    {
        final ContentModelTable<String> table = new ContentModelTable<String>();

        final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
        ZERO.put(LN_ANNOTATION, 1);
        ZERO.put(LN_SIMPLE_CONTENT, ContentModelTable.END);
        ZERO.put(LN_COMPLEX_CONTENT, ContentModelTable.END);
        ZERO.put(LN_GROUP, 2);
        ZERO.put(LN_ALL, 2);
        ZERO.put(LN_CHOICE, 2);
        ZERO.put(LN_SEQUENCE, 2);
        ZERO.put(LN_ATTRIBUTE, 2);
        ZERO.put(LN_ATTRIBUTE_GROUP, 2);
        ZERO.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        ZERO.put(EPSILON, ContentModelTable.END);

        final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
        ONE.put(LN_SIMPLE_CONTENT, ContentModelTable.END);
        ONE.put(LN_COMPLEX_CONTENT, ContentModelTable.END);
        ONE.put(LN_GROUP, 2);
        ONE.put(LN_ALL, 2);
        ONE.put(LN_CHOICE, 2);
        ONE.put(LN_SEQUENCE, 2);
        ONE.put(LN_ATTRIBUTE, 2);
        ONE.put(LN_ATTRIBUTE, 2);
        ONE.put(LN_ATTRIBUTE_GROUP, 2);
        ONE.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        ONE.put(EPSILON, ContentModelTable.END);

        final HashMap<String, Integer> TWO = new HashMap<String, Integer>();
        TWO.put(LN_ATTRIBUTE, 2);
        TWO.put(LN_ATTRIBUTE_GROUP, 2);
        TWO.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        TWO.put(EPSILON, ContentModelTable.END);

        table.put(0, ZERO);
        table.put(1, ONE);
        table.put(2, TWO);

        return table;
    }

    private static ContentModelTable<String> makeExtensionInComplexContentTable()
    {
        final ContentModelTable<String> table = new ContentModelTable<String>();

        final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
        ZERO.put(LN_ANNOTATION, 1);
        ZERO.put(LN_GROUP, 2);
        ZERO.put(LN_ALL, 2);
        ZERO.put(LN_CHOICE, 2);
        ZERO.put(LN_SEQUENCE, 2);
        ZERO.put(LN_ATTRIBUTE, 3);
        ZERO.put(LN_ATTRIBUTE_GROUP, 3);
        ZERO.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        ZERO.put(EPSILON, ContentModelTable.END);

        final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
        ONE.put(LN_GROUP, 2);
        ONE.put(LN_ALL, 2);
        ONE.put(LN_CHOICE, 2);
        ONE.put(LN_SEQUENCE, 2);
        ONE.put(LN_ATTRIBUTE, 3);
        ONE.put(LN_ATTRIBUTE_GROUP, 3);
        ONE.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        ONE.put(EPSILON, ContentModelTable.END);

        final HashMap<String, Integer> TWO = new HashMap<String, Integer>();
        TWO.put(LN_ATTRIBUTE, 3);
        TWO.put(LN_ATTRIBUTE_GROUP, 3);
        TWO.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        TWO.put(EPSILON, ContentModelTable.END);

        final HashMap<String, Integer> THREE = new HashMap<String, Integer>();
        THREE.put(LN_ATTRIBUTE, 3);
        THREE.put(LN_ATTRIBUTE_GROUP, 3);
        THREE.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        THREE.put(EPSILON, ContentModelTable.END);

        table.put(0, ZERO);
        table.put(1, ONE);
        table.put(2, TWO);
        table.put(3, THREE);

        return table;
    }

    private static ContentModelTable<String> makeExtensionInSimpleContentTable()
    {
        final ContentModelTable<String> table = new ContentModelTable<String>();

        final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
        ZERO.put(LN_ANNOTATION, 1);
        ZERO.put(LN_ATTRIBUTE, 1);
        ZERO.put(LN_ATTRIBUTE_GROUP, 1);
        ZERO.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        ZERO.put(EPSILON, ContentModelTable.END);

        final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
        ONE.put(LN_ATTRIBUTE, 1);
        ONE.put(LN_ATTRIBUTE_GROUP, 1);
        ONE.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        ONE.put(EPSILON, ContentModelTable.END);

        table.put(0, ZERO);
        table.put(1, ONE);

        return table;
    }

    private static ContentModelTable<String> makeRestrictionInComplexContentTable()
    {
        final ContentModelTable<String> table = new ContentModelTable<String>();

        final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
        ZERO.put(LN_ANNOTATION, 1);
        ZERO.put(LN_GROUP, 2);
        ZERO.put(LN_ALL, 2);
        ZERO.put(LN_CHOICE, 2);
        ZERO.put(LN_SEQUENCE, 2);
        ZERO.put(LN_ATTRIBUTE, 3);
        ZERO.put(LN_ATTRIBUTE_GROUP, 3);
        ZERO.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        ZERO.put(EPSILON, ContentModelTable.END);

        final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
        ONE.put(LN_GROUP, 2);
        ONE.put(LN_ALL, 2);
        ONE.put(LN_CHOICE, 2);
        ONE.put(LN_SEQUENCE, 2);
        ONE.put(LN_ATTRIBUTE, 3);
        ONE.put(LN_ATTRIBUTE_GROUP, 3);
        ONE.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        ONE.put(EPSILON, ContentModelTable.END);

        final HashMap<String, Integer> TWO = new HashMap<String, Integer>();
        TWO.put(LN_ATTRIBUTE, 3);
        TWO.put(LN_ATTRIBUTE_GROUP, 3);
        TWO.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        TWO.put(EPSILON, ContentModelTable.END);

        final HashMap<String, Integer> THREE = new HashMap<String, Integer>();
        THREE.put(LN_ATTRIBUTE, 3);
        THREE.put(LN_ATTRIBUTE_GROUP, 3);
        THREE.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        THREE.put(EPSILON, ContentModelTable.END);

        table.put(0, ZERO);
        table.put(1, ONE);
        table.put(2, TWO);
        table.put(3, THREE);

        return table;
    }

    private static ContentModelTable<String> makeRestrictionInSimpleContentTable()
    {
        final ContentModelTable<String> table = new ContentModelTable<String>();

        final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
        ZERO.put(LN_ANNOTATION, 1);
        ZERO.put(LN_SIMPLE_TYPE, 2);
        ZERO.put(LN_MIN_EXCLUSIVE, 2);
        ZERO.put(LN_MIN_INCLUSIVE, 2);
        ZERO.put(LN_MAX_EXCLUSIVE, 2);
        ZERO.put(LN_MAX_INCLUSIVE, 2);
        ZERO.put(LN_TOTAL_DIGITS, 2);
        ZERO.put(LN_FRACTION_DIGITS, 2);
        ZERO.put(LN_LENGTH, 2);
        ZERO.put(LN_MIN_LENGTH, 2);
        ZERO.put(LN_MAX_LENGTH, 2);
        ZERO.put(LN_ENUMERATION, 2);
        ZERO.put(LN_WHITE_SPACE, 2);
        ZERO.put(LN_PATTERN, 2);
        ZERO.put(LN_ATTRIBUTE, 3);
        ZERO.put(LN_ATTRIBUTE_GROUP, 3);
        ZERO.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        ZERO.put(EPSILON, ContentModelTable.END);

        final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
        ONE.put(LN_SIMPLE_TYPE, 2);
        ONE.put(LN_MIN_EXCLUSIVE, 2);
        ONE.put(LN_MIN_INCLUSIVE, 2);
        ONE.put(LN_MAX_EXCLUSIVE, 2);
        ONE.put(LN_MAX_INCLUSIVE, 2);
        ONE.put(LN_TOTAL_DIGITS, 2);
        ONE.put(LN_FRACTION_DIGITS, 2);
        ONE.put(LN_LENGTH, 2);
        ONE.put(LN_MIN_LENGTH, 2);
        ONE.put(LN_MAX_LENGTH, 2);
        ONE.put(LN_ENUMERATION, 2);
        ONE.put(LN_WHITE_SPACE, 2);
        ONE.put(LN_PATTERN, 2);
        ONE.put(LN_ATTRIBUTE, 3);
        ONE.put(LN_ATTRIBUTE_GROUP, 3);
        ONE.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        ONE.put(EPSILON, ContentModelTable.END);

        final HashMap<String, Integer> TWO = new HashMap<String, Integer>();
        TWO.put(LN_MIN_EXCLUSIVE, 2);
        TWO.put(LN_MIN_INCLUSIVE, 2);
        TWO.put(LN_MAX_EXCLUSIVE, 2);
        TWO.put(LN_MAX_INCLUSIVE, 2);
        TWO.put(LN_TOTAL_DIGITS, 2);
        TWO.put(LN_FRACTION_DIGITS, 2);
        TWO.put(LN_LENGTH, 2);
        TWO.put(LN_MIN_LENGTH, 2);
        TWO.put(LN_MAX_LENGTH, 2);
        TWO.put(LN_ENUMERATION, 2);
        TWO.put(LN_WHITE_SPACE, 2);
        TWO.put(LN_PATTERN, 2);
        TWO.put(LN_ATTRIBUTE, 3);
        TWO.put(LN_ATTRIBUTE_GROUP, 3);
        TWO.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        TWO.put(EPSILON, ContentModelTable.END);

        final HashMap<String, Integer> THREE = new HashMap<String, Integer>();
        THREE.put(LN_ATTRIBUTE, 3);
        THREE.put(LN_ATTRIBUTE_GROUP, 3);
        THREE.put(LN_ANY_ATTRIBUTE, ContentModelTable.END);
        THREE.put(EPSILON, ContentModelTable.END);

        table.put(0, ZERO);
        table.put(1, ONE);
        table.put(2, TWO);
        table.put(3, THREE);

        return table;
    }

    private static ContentModelTable<String> makeSimpleContentTable()
    {
        final ContentModelTable<String> table = new ContentModelTable<String>();

        final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
        ZERO.put(LN_ANNOTATION, 1);
        ZERO.put(LN_RESTRICTION, ContentModelTable.END);
        ZERO.put(LN_EXTENSION, ContentModelTable.END);

        final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
        ONE.put(LN_RESTRICTION, ContentModelTable.END);
        ONE.put(LN_EXTENSION, ContentModelTable.END);

        table.put(0, ZERO);
        table.put(1, ONE);

        return table;
    }

    /**
     * Return the local-name part of the lexical xs:QName. <br/>
     * The input is assumed to be lexically valid.
     * 
     * @param qualifiedName
     *            The lexical xs:QName.
     */
    private static String getLocalName(final String qualifiedName)
    {
        return qualifiedName.substring(qualifiedName.indexOf(":") + 1);
    }

    /**
     * Return the prefix part of the lexical xs:QName. If there is no colon separator, returns the empty string. <br/>
     * The input is assumed to be lexically valid.
     * 
     * @param qualifiedName
     *            The lexical xs:QName.
     */
    private static String getPrefix(final String qualifiedName)
    {
        final int index = qualifiedName.indexOf(':');
        if (index == -1)
        {
            return XMLConstants.DEFAULT_NS_PREFIX;
        }
        else
        {
            return qualifiedName.substring(0, index);
        }
    }

    private static final ContentModelTable<String> attributeTable = makeAttributeTable();
    private static final ContentModelTable<String> complexContentTable = makeComplexContentTable();
    private static final ContentModelTable<String> complexTypeTable = makeComplexTypeTable();

    private static final String EPSILON = "";
    private static final ContentModelTable<String> extensionInComplexContentTable = makeExtensionInComplexContentTable();
    private static final ContentModelTable<String> extensionInSimpleContentTable = makeExtensionInSimpleContentTable();
    private static final ContentModelTable<String> restrictionInComplexContentTable = makeRestrictionInComplexContentTable();
    private static final ContentModelTable<String> restrictionInSimpleContentTable = makeRestrictionInSimpleContentTable();
    private static final ContentModelTable<String> simpleContentTable = makeSimpleContentTable();

    private final FAMap foreignAttributes = new FAMap();

    private final XMLTypeRef ANY_SIMPLE_TYPE;
    private final XMLTypeRef ANY_TYPE;

    private final CanonicalAtomBridge atoms;
    
    private final ComponentProvider bootstrap;

    private final SchemaCatalog m_catalog;

    private final SchemaExceptionHandler m_errors;

    private final PrefixMappingSupport m_pms;

    private final boolean m_processRepeatedNamespaces;

    private final CatalogResolver m_resolver;

    /**
     * Factory is required for
     */
    private final RestrictedXPathParser m_xp;

}
