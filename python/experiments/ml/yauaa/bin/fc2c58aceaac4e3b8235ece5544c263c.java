/*
 * Yet Another UserAgent Analyzer
 * Copyright (C) 2013-2018 Niels Basjes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.basjes.parse.useragent.nifi;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import nl.basjes.parse.useragent.UserAgentAnalyzer.UserAgentAnalyzerBuilder;
import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.
ReadsAttribute ;importorg.apache.nifi.annotation.behavior.
ReadsAttributes ;importorg.apache.nifi.annotation.behavior.
SideEffectFree ;importorg.apache.nifi.annotation.documentation.
CapabilityDescription ;importorg.apache.nifi.annotation.documentation.
Tags ;importorg.apache.nifi.annotation.lifecycle.
OnScheduled ;importorg.apache.nifi.components.
PropertyDescriptor ;importorg.apache.nifi.flowfile.
FlowFile ;importorg.apache.nifi.processor.
AbstractProcessor ;importorg.apache.nifi.processor.
ProcessContext ;importorg.apache.nifi.processor.
ProcessSession ;importorg.apache.nifi.processor.
ProcessorInitializationContext ;importorg.apache.nifi.processor.
Relationship ;importorg.apache.nifi.processor.exception.
ProcessException ;importorg.apache.nifi.processor.util.

StandardValidators ;importjava.util.
ArrayList ;importjava.util.
Collections ;importjava.util.
HashSet ;importjava.util.
List ;importjava.util.

Set ; importstaticnl.basjes.parse.useragent.nifi.ParseUserAgent.

USERAGENTSTRING_ATTRIBUTENAME;
@EventDriven
@SideEffectFree@Tags({ "logs", "useragent","webanalytics"
})@CapabilityDescription(
"Extract attributes from the UserAgent string.")@ReadsAttributes({@ReadsAttribute(attribute= USERAGENTSTRING_ATTRIBUTENAME,description="The useragent string that is to be analyzed.")
} ) public class ParseUserAgent extends

    AbstractProcessor { static final String USERAGENTSTRING_ATTRIBUTENAME=
    "UseragentString" ; static final String PROPERTY_PREFIX=
    "Extract." ; static final String ATTRIBUTE_PREFIX=

    "Useragent." ; public static final Relationship SUCCESS =newRelationship.Builder
        ().name(
        "success").description(
        "Here we route all FlowFiles that have been analyzed.").build(

    ) ; public static final Relationship MISSING =newRelationship.Builder
        ().name(
        "missing").description ( "Here we route the FlowFiles that did not have the " + USERAGENTSTRING_ATTRIBUTENAME+
        " attribute set.").build(

    ) ;privateSet< Relationship>

    relationships ; private UserAgentAnalyzer uaa=

    null ; private staticfinalList< String > ALL_FIELD_NAMES =newArrayList<>(
    ) ;privateList< PropertyDescriptor > supportedPropertyDescriptors =newArrayList<>(
    ) ;privateList< String > extractFieldNames =newArrayList<>(

    );
    @ Override protectedvoidinit (ProcessorInitializationContext context
        ){super.init(context

        ) ;synchronized( ALL_FIELD_NAMES
            ) {if(ALL_FIELD_NAMES.isEmpty( )
                ){ALL_FIELD_NAMES.addAll
                    (UserAgentAnalyzer.newBuilder
                    ().hideMatcherLoadStats
                    ().delayInitialization
                    ().dropTests
                    ().build
                    ().getAllPossibleFieldNamesSorted()
            )
        ;
        } }finalSet< Relationship > relationshipsSet =newHashSet<>(
        );relationshipsSet.add(SUCCESS
        );relationshipsSet.add(MISSING
        );this . relationships=Collections.unmodifiableSet(relationshipsSet

        ) ;for (String fieldName: ALL_FIELD_NAMES
            ) { PropertyDescriptor propertyDescriptor =newPropertyDescriptor.Builder
                ().name ( PROPERTY_PREFIX+
                fieldName).description ( "If enabled will extract the " + fieldName+
                " field").required(
                true).allowableValues( "true",
                "false").defaultValue(
                "false").addValidator(StandardValidators.
                BOOLEAN_VALIDATOR).build(
            );supportedPropertyDescriptors.add(propertyDescriptor
        )

    ;

    }}
    @ OverridepublicSet< Relationship>getRelationships (
        ) {returnthis.
    relationships

    ;}
    @ OverrideprotectedList< PropertyDescriptor>getSupportedPropertyDescriptors (
        ) {return
    supportedPropertyDescriptors

    ;}
    @ OnScheduled publicvoidonSchedule (ProcessContext context
        ) {if ( uaa== null
            ){UserAgentAnalyzerBuilder < ?extends UserAgentAnalyzer , ?extends UserAgentAnalyzerBuilder >
                builder
                =UserAgentAnalyzer.newBuilder
                ().hideMatcherLoadStats
                ().dropTests(

            );extractFieldNames.clear(

            ) ;for (PropertyDescriptor propertyDescriptor: supportedPropertyDescriptors
                ) {if(context.getProperty(propertyDescriptor).asBoolean( )
                    ) { String name=propertyDescriptor.getName(
                    ) ;if(name.startsWith(PROPERTY_PREFIX ) )
                        { // Should always pass String fieldName=name.substring(PROPERTY_PREFIX.length()

                        );builder.withField(fieldName
                        );extractFieldNames.add(fieldName
                    )
                ;
            }
            } } uaa=builder.build(
        )
    ;

    }}
    @ Override publicvoidonPropertyModified (PropertyDescriptor descriptor ,String oldValue ,String newValue
        ) { uaa=
    null

    ;}
    @ Override publicvoidonTrigger (ProcessContext context ,ProcessSession session ) throws ProcessException
        { // NOSONAR: Explicitly name the exception FlowFile flowFile=session.get(
        ) ; String userAgentString=flowFile.getAttribute(USERAGENTSTRING_ATTRIBUTENAME
        ) ;if ( userAgentString== null
            ){session.transfer( flowFile,MISSING
        ) ; }
            else { UserAgent userAgent=uaa.parse(userAgentString
            ) ;for ( String fieldName: extractFieldNames
                ) { String fieldValue=userAgent.getValue(fieldName
                ) ; flowFile=session.putAttribute( flowFile , ATTRIBUTE_PREFIX+ fieldName,fieldValue
            )
            ;}session.transfer( flowFile,SUCCESS
        )
        ;}session.commit(
    )

;
