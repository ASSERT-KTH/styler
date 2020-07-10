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

import javax.xml.namespace.QName;

import org.genxdm.xs.constraints.RestrictedXPath;

/**
 * The status of a streaming XPath evaluation for an xs:selector.
 */
public class IdentitySelectorEvaluation
{
	/**
	 * The XPath expressionfor this xs:selector.
	 */
	private final RestrictedXPath xpath;
	/**
	 * An index into the current XPath step. This value may advance and retreat.
	 */
	private IdentityXPathIndex currentStep;

	public boolean matching = true;

	/**
	 * Used to indicate that the XPath selection is active.
	 */
	public boolean selecting = false;

	/**
	 * This is synonymous with the status not being the first in the list. All status entries after the first were
	 * dynamically added as a result of evaluating the XPath expression in a streaming fashion with a relocation (//).
	 */
	public final boolean removable;

	public IdentitySelectorEvaluation(final RestrictedXPath xpath, final boolean removable)
	{
		this.xpath = xpath;
		this.removable = removable;
		if (removable)
		{
			this.currentStep = new IdentityXPathIndex(0, xpath.getUBoundStep());
		}
		else
		{
			this.currentStep = new IdentityXPathIndex(-1, xpath.getUBoundStep());
		}
	}

	/**
	 * Essentially advances to the next step.
	 * 
	 * When context node steps are present, steps forwards over them.
	 */
	public void advance()
	{
		currentStep.advance();

		if (!currentStep.isBelow())
		{
			// Consume context node (.) steps
			while (currentStep.canAdvance())
			{
				if (onContextNode())
				{
					currentStep.advance();
				}
				else
				{
					break;
				}
			}
		}
	}

	/**
	 * Essentially retreats to the previous step.
	 * 
	 * When context node steps are present, steps backwards over them.
	 */
	public void retreat()
	{
		currentStep.decrement();

		if (currentStep.inBounds())
		{
			while (currentStep.canDecrement())
			{
				if (onContextNode())
				{
					currentStep.decrement();
				}
				else
				{
					break;
				}
			}
		}
	}

	public boolean inBounds()
	{
		return currentStep.inBounds();
	}

	public boolean isSelecting()
	{
		return selecting;
	}

	public void setSelecting(final boolean selecting)
	{
		this.selecting = selecting;
	}

	public boolean onContextNode()
	{
		return xpath.isContextNode(currentStep.value());
	}

	public boolean onLastStep()
	{
		return currentStep.isUpperBound();
	}

	public boolean matchesElement(final QName elementName)
	{
		if (xpath.isAttribute())
		{
			return false;
		}

		if (onContextNode())
		{
			return true;
		}
		else
		{
			final int idxStep = currentStep.value();

			final String stepLN = xpath.getStepLocalName(idxStep);
			final String stepNS = xpath.getStepNamespace(idxStep);

			if (xpath.isWildcardLocalName(idxStep) && (xpath.isWildcardNamespace(idxStep) || stepNS.equals(elementName.getNamespaceURI())))
			{
				return true;
			}
			else
			{
				return elementName.getLocalPart().equals(stepLN) && elementName.getNamespaceURI().equals(stepNS);
			}
		}
	}
}
