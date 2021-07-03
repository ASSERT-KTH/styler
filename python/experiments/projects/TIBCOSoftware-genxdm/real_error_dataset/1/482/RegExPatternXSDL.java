package org.genxdm.processor.w3c.xs.regex;

import java.util.ArrayList;

import org.genxdm.xs.facets.RegExPattern;

import org.genxdm.processor.w3c.xs.regex.string.StringRegExPattern;
import org.genxdm.processor.w3c.xs.regex.string.StringRegExPatternTerm;

final class RegExPatternXSDL
    implements RegExPattern
{
	public RegExPatternXSDL(final StringRegExPattern expr)
	{
		this.expr = expr;
	}

	public boolean matches(final String input)
	{
		final ArrayList<StringRegExPatternTerm> followers = new ArrayList<StringRegExPatternTerm>();
		return expr.matches(input, followers);
	}

	private final StringRegExPattern expr;
}
