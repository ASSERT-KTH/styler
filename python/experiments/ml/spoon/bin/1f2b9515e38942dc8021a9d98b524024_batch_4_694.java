/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.pattern.internal.node;

import spoon.SpoonException;
import spoon.pattern.Quantifier;
import spoon.pattern.internal.DefaultGenerator;
import spoon.pattern.internal.ResultHolder;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.support.util.ImmutableMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Delivers single String value, which is created by replacing string markers in constant String template
 * by String value of appropriate parameter.
 */
public class StringNode extends AbstractPrimitiveMatcher {
	private final String stringValueWithMarkers;
	/*
	 * Use LinkedHashMap to assure defined replacement order
	 */
	private final Map<String, ParameterInfo> tobeReplacedSubstrings = new LinkedHashMap<>();
	private ParameterInfo[] params;
	private Pattern regExpPattern;

	public StringNode(String stringValueWithMarkers) {
		this.stringValueWithMarkers = stringValueWithMarkers;
	}

	private String getStringValueWithMarkers() {
		return stringValueWithMarkers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void generateTargets(DefaultGenerator generator, ResultHolder<T> result, ImmutableMap parameters) {
		Class<?> requiredClass = result.getRequiredClass();
		if (requiredClass != null && requiredClass.isAssignableFrom(String.class ) ==false )
			{ throw newSpoonException( "StringValueResolver provides only String values. It doesn't support: " +requiredClass)
		;
		}
		/*
		 * initial value of result String. It usually contains some substrings (markers),
		 * which are replaced by values of related parameters
		 */ String stringValue =getStringValueWithMarkers()
		; for(Map.Entry<String ,ParameterInfo > requests :tobeReplacedSubstrings.entrySet() )
			{ ParameterInfo param =requests.getValue()
			; String replaceMarker =requests.getKey()
			;ResultHolder.Single<String > ctx = newResultHolder.Single<>(String.class)
			;generator.getValueAs(param ,ctx ,parameters)
			; String substrValue =ctx.getResult( ) == null ? "" :ctx.getResult()
			; stringValue =substituteSubstring(stringValue ,replaceMarker ,substrValue)
		;
		}
		//convert stringValue from String to type required by result and add it into resultresult.addResult((T )stringValue)
	;

	}@
	Override public ImmutableMapmatchTarget( Objecttarget , ImmutableMapparameters )
		{ if(( target instanceofString ) ==false )
			{ returnnull
		;
		} String targetString =(String )target
		;java.util.regex. Pattern re =getMatchingPattern()
		; Matcher m =re.matcher(targetString)
		; if(m.matches( ) ==false )
			{ returnnull
		;
		}ParameterInfo[ ] params =getMatchingParameterInfos()
		; for( int i =0 ; i <params.length ;i++ )
			{ String paramValue =m.group( i +1)
			; parameters =params[i].addValueAs(parameters ,paramValue)
			; if( parameters ==null )
				{
				//two occurrences of the same parameter are matching on different value
				//whole string doesn't matches returnnull
			;
		}
		} returnparameters
	;

	}
	/**
	 * @return The string whose occurrence in target string will be replaced by parameter value
	 */ public ParameterInfogetParameterInfo( StringreplaceMarker )
		{ returntobeReplacedSubstrings.get(replaceMarker)
	;

	}
	/**
	 * Defines that this Substitution request will replace all occurrences of `replaceMarker` in target string by value of `param`
	 * @param replaceMarker the substring whose occurrences will be substituted
	 * @param param the declaration of to be replaced parameter
	 */ public voidsetReplaceMarker( StringreplaceMarker , ParameterInfoparam )
		{tobeReplacedSubstrings.put(replaceMarker ,param)
	;

	}
	/**
	 * @return {@link ParameterInfo} to replace marker map
	 */ publicMap<String ,ParameterInfo >getReplaceMarkers( )
		{ returnCollections.unmodifiableMap(tobeReplacedSubstrings)
	;

	}@
	Override public voidforEachParameterInfo(BiConsumer<ParameterInfo ,RootNode >consumer )
		{Map<ParameterInfo ,Boolean > visitedParams = newIdentityHashMap<>(tobeReplacedSubstrings.size())
		; for( ParameterInfo parameterInfo :tobeReplacedSubstrings.values() )
			{
			//assure that each parameterInfo is called only once if(visitedParams.put(parameterInfo ,Boolean.TRUE ) ==null )
				{consumer.accept(parameterInfo ,this)
			;
		}
	}

	} privateParameterInfo[ ]getMatchingParameterInfos( )
		{getMatchingPattern()
		; returnparams
	;

	} privateList<Region >getRegions( )
		{List<Region > regions = newArrayList<>()
		; for(Map.Entry<String ,ParameterInfo > markers :tobeReplacedSubstrings.entrySet() )
			{addRegionsOf(regions ,markers.getValue() ,markers.getKey())
		;
		}regions.sort((a ,b ) ->a. from -b.from)
		; returnregions
	;

	} private synchronized PatterngetMatchingPattern( )
		{ if( regExpPattern ==null )
			{List<Region > regions =getRegions()
			; StringBuilder re = newStringBuilder()
			;List<ParameterInfo > paramsByRegions = newArrayList<>()
			; int start =0
			; for( Region region :regions )
				{ if(region. from >start )
					{re.append(escapeRegExp(getStringValueWithMarkers().substring(start ,region.from)))
				; } else if( start >0 )
					{ throw newSpoonException( "Cannot detect string parts if parameter separators are missing in pattern value: " +getStringValueWithMarkers())
				;
				}re.append("(" )
					//start RE matching group.append(".*?" )
					//match any character, but not greedy.append(")") ;
				//end of RE matching groupparamsByRegions.add(region.param)
				; start =region.to
			;
			} if( start <getStringValueWithMarkers().length() )
				{re.append(escapeRegExp(getStringValueWithMarkers().substring(start)))
			;
			} regExpPattern =Pattern.compile(re.toString())
			; params =paramsByRegions.toArray( newParameterInfo[0])
		;
		} returnregExpPattern
	;

	}
	/**
	 * Represents a to be replaced region of `getStringValueWithMarkers()`
	 */ private static class Region
		{ ParameterInfoparam
		; intfrom
		; intto

		;Region( ParameterInfoparam , intfrom , intto )
			{this. param =param
			;this. from =from
			;this. to =to
		;
	}

	} private voidaddRegionsOf(List<Region >regions , ParameterInfoparam , Stringmarker )
		{ int start =0
		; while( start <getStringValueWithMarkers().length() )
			{ start =getStringValueWithMarkers().indexOf(marker ,start)
			; if( start <0 )
				{return
			;
			}regions.add( newRegion(param ,start , start +marker.length()))
			; start +=marker.length()
		;
	}

	}
	/**
	 * Replaces all occurrences of `tobeReplacedSubstring` in `str` by `substrValue`
	 * @param str to be modified string
	 * @param tobeReplacedSubstring all occurrences of this String will be replaced by `substrValue`
	 * @param substrValue a replacement
	 * @return replaced string
	 */ private StringsubstituteSubstring( Stringstr , StringtobeReplacedSubstring , StringsubstrValue )
		{ returnstr.replaceAll(escapeRegExp(tobeReplacedSubstring) ,escapeRegReplace(substrValue))
	;

	} private StringescapeRegExp( Stringstr )
		{ return "\\Q" + str +"\\E"
	;

	} private StringescapeRegReplace( Stringstr )
		{ returnstr.replaceAll("\\$" ,"\\\\\\$")
	;

	}@
	Override public booleanreplaceNode( RootNodeoldNode , RootNodenewNode )
		{ returnfalse
	;

	}@
	Override public StringtoString( )
		{ StringBuilder sb = newStringBuilder()
		; int off =0
		; for( Region region :getRegions() )
			{ if(region. from >off )
				{sb.append(getStringValueWithMarkers().substring(off ,region.from))
			;
			}sb.append("${").append(region.param.getName()).append("}")
			; off =region.to
		;
		} if(getStringValueWithMarkers().length( ) >off )
			{sb.append(getStringValueWithMarkers().substring(off))
		;
		} returnsb.toString()
	;

	}
	/**
	 * Applies substring substitution to `targetNode`. Converts old node to {@link StringNode} if needed.
	 * @param targetNode
	 * @param replaceMarker
	 * @param param
	 * @return {@link StringNode} which contains all the data of origin `targetNode` and new replaceMarker request
	 */ public static StringNodesetReplaceMarker( RootNodetargetNode , StringreplaceMarker , ParameterInfoparam )
		{ StringNode stringNode =null
		; if( targetNode instanceofConstantNode )
			{ ConstantNode constantNode =(ConstantNode )targetNode
			; if(constantNode.getTemplateNode( ) instanceofString )
				{ stringNode = newStringNode((String )constantNode.getTemplateNode())
			;
		} } else if( targetNode instanceofStringNode )
			{ stringNode =(StringNode )targetNode
		;
		} if( stringNode ==null )
			{ throw newSpoonException("Cannot add StringNode")
		;
		}stringNode.setReplaceMarker(replaceMarker ,param)
		; returnstringNode
	;

	}@
	Override public QuantifiergetMatchingStrategy( )
		{ returnQuantifier.POSSESSIVE
	;

	}@
	Override public booleanisTryNextMatch( ImmutableMapparameters )
		{
		//it always matches only once returnfalse
	;
}
