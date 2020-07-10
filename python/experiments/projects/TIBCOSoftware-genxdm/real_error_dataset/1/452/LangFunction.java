/*
 * Portions copyright (c) 1998-1999, James Clark : see copyingjc.txt for
 * license details
 * Portions copyright (c) 2002, Bill Lindsey : see copying.txt for license
 * details
 * 
 * Portions copyright (c) 2009-2011 TIBCO Software Inc.
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
package org.genxdm.processor.xpath.v10.functions;

import javax.xml.XMLConstants;

import org.genxdm.Model;
import org.genxdm.Precursor;
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleBooleanExpr;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleExprImpl;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.ExprParseException;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

public final class LangFunction 
    extends Function1
{
	private static boolean isSubLanguage(final String lang1, final String lang2)
	{
		final int len1 = lang1.length();
		final int len2 = lang2.length();
		if (len1 > len2)
		{
			return false;
		}
		if (len1 < len2 && lang2.charAt(len1) != '-')
		{
			return false;
		}
		for (int i = 0; i < len1; i++)
		{
			final char c1 = lang1.charAt(i);
			final char c2 = lang2.charAt(i);
			switch ((int)c1 - (int)c2)
			{
				case 0:
				case 'a' - 'A':
				case 'A' - 'a':
				break;
				default:
					return false;
			}
		}
		return true;
	}

	private final String LANG;
	private final String XML_NS_URI;

	public LangFunction()
	{
		LANG = "lang";
		XML_NS_URI = XMLConstants.XML_NS_URI;
	}

	private <N> boolean lang(final Model<N> model, N node, final String lang)
	{
		while (node != null)
		{
			final N attribute = model.getAttribute(node, XML_NS_URI, LANG);
			if (null != attribute)
			{
				final String nodeLang = model.getStringValue(attribute);
				if (nodeLang != null)
				{
					return isSubLanguage(lang, nodeLang);
				}
			}
			node = model.getParent(node);
		}
		return false;
	}

    private boolean lang(final TraversingInformer start, final String lang)
    {
        Precursor node = start.newPrecursor();
        
        while (node != null)
        {
            final String nodeLang = node.getAttributeStringValue(XML_NS_URI, LANG);
            if (nodeLang != null)
            {
                return isSubLanguage(lang, nodeLang);
            }
            node.moveToParent();
        }
        return false;
    }

	ConvertibleExprImpl makeCallExpr(final ConvertibleExpr e, final ExprContextStatic statEnv) throws ExprParseException
	{
		final StringExpr se = e.makeStringExpr(statEnv);
		return new ConvertibleBooleanExpr()
		{
            @Override
			public <N> boolean booleanFunction(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) {
				return lang(model, contextNode, se.stringFunction(model, contextNode, dynEnv));
			}

            @Override
            public boolean booleanFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return lang(contextNode, se.stringFunction(contextNode, dynEnv));
            }
		};
	}
}
