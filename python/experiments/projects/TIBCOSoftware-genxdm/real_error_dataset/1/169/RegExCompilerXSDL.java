package org.genxdm.processor.w3c.xs.impl;

import org.genxdm.xs.exceptions.SchemaRegExCompileException;
import org.genxdm.xs.facets.RegExPattern;
import org.genxdm.xs.facets.SchemaRegExCompiler;

import org.genxdm.processor.w3c.xs.regex.impl.string.StringRegExException;
import org.genxdm.processor.w3c.xs.regex.impl.string.StringRegExPattern;

public final class RegExCompilerXSDL implements SchemaRegExCompiler
{
	public RegExPattern compile(final String regex) 
	    throws SchemaRegExCompileException
	{
		try
		{
			return new RegExPatternXSDL(StringRegExPattern.compile(regex));
		}
		catch (StringRegExException e)
		{
			throw new SchemaRegExCompileException(e, regex);
		}
	}

	public RegExPattern compile(String regex, String flags)
	    throws SchemaRegExCompileException
	{
		return compile(regex);
	}
}
