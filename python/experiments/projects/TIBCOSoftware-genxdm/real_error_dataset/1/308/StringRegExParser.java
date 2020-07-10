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
package org.genxdm.processor.w3c.xs.regex.impl.string;

import org.genxdm.processor.w3c.xs.regex.api.RegExBridge;

/**
 * RegExpr is a class of static methods for parsing Regular Expressions. The result of the parse can be used by Pattern
 * to match against text input. The syntax is from XML-Schema (XSDL) which is based on Perl. The code uses the
 * XML-Schema terms for the syntax (branch, piece, atom).
 * <p/>
 * XML name characters are approximated with Unicode properties
 */
public final class StringRegExParser
{
	/**
	 * characters excluded from XmlChar ::= [^\#x2D#x5B#x5D]
	 */
	private static final String XMLCHAR = "-[]";

	/**
	 * characters excluded from Char ::= [^.\?*+()|#x5B#x5D]
	 */
	private static final String CHAR = ".\\?*+()|[]";
	private static final boolean DOT_IS_SPECIAL = true;
	private static final boolean DOT_NORMAL_CHAR = false;

	/**
	 * parse the XML-Schema regular expression, returning a PatternTerm. the result will be null if the expression is
	 * empty.
	 * 
	 * @throws StringRegExException
	 *             if invalid expression.
	 */
	public static StringRegExPatternTerm parse(final String expr) throws StringRegExException
	{
		return StringRegExParser.parse(new StringRegExPatternInput(expr));
	}

	/**
	 * parse the XML-Schema regular expression, returning a PatternTerm. the result will be null if the expression is
	 * empty.
	 * 
	 * @throws StringRegExException
	 *             if invalid expression.
	 */
	public static StringRegExPatternTerm parse(final StringRegExPatternInput input) throws StringRegExException
	{
		if (!input.hasNext())
		{
			final StringRegExPatternTerm empty = new StringRegExPatternTerm(StringRegExPatternTerm.Type.TYPE_SEQ);
			empty.setOccurrence(0, 0);
			return empty;
		}
		final StringRegExPatternTerm term = parseExpr(input);
		if (input.hasNext())
		{
			throw new StringRegExException(StringRegExException.Kind.invalidPattern, null);
		}
		return term;
	}

	/**
	 * expr ::= branch ( '|' branch )*
	 */
	private static StringRegExPatternTerm parseExpr(final StringRegExPatternInput input) throws StringRegExException
	{
		StringRegExPatternTerm branch = parseBranch(input);
		if (branch != null && peekIs(input, OR))
		{
			// if (expr == null)
			final StringRegExPatternTerm expr = new StringRegExPatternTerm(StringRegExPatternTerm.Type.TYPE_CHOICE);
			expr.addSubTerm(branch);
			while (peekIs(input, OR))
			{
				input.next(); // skip OR
				branch = parseBranch(input);
				if (branch == null)
				{
					throw new StringRegExException(StringRegExException.Kind.invalidPatternEmptyBranch, null);
				}
				expr.addSubTerm(branch);
			}
			return expr;
		}
		else
		{
			return branch;
		}
	}

	/**
	 * branch ::= piece ( piece )*
	 */
	private static StringRegExPatternTerm parseBranch(final StringRegExPatternInput input) throws StringRegExException
	{
		StringRegExPatternTerm branch;
		StringRegExPatternTerm piece = parsePiece(input);
		if (piece != null && input.hasNext() && !peekIs(input, OR) && !peekIs(input, RPAREN))
		{
			branch = new StringRegExPatternTerm(StringRegExPatternTerm.Type.TYPE_SEQ);
			branch.addSubTerm(piece);
			while (input.hasNext() && !peekIs(input, OR) && !peekIs(input, RPAREN))
			{
				piece = parsePiece(input);
				branch.addSubTerm(piece);
			}
		}
		else
		{
			branch = piece;
		}
		return branch;
	}

	/**
	 * piece ::= atom ( occurrence )? occurrence ::= '?' | '*' | '+' | ( '{' int (',' ( int )?) '}' )
	 */
	private static StringRegExPatternTerm parsePiece(final StringRegExPatternInput input) throws StringRegExException
	{
		final StringRegExPatternTerm atom = parseAtom(input);
		if (atom == null)
		{
			throw new StringRegExException(StringRegExException.Kind.invalidPatternEmptyAtom, null);
		}
		if (input.hasNext())
		{
			String s = input.peek();
			char c = s.charAt(0);
			if (c == ZERO_OR_ONE)
			{
				atom.setOccurrence(0, 1);
				input.next();
			}
			else if (c == ZERO_OR_MORE)
			{
				atom.setOccurrence(0, RegExBridge.UNBOUNDED);
				input.next();
			}
			else if (c == ONE_OR_MORE)
			{
				atom.setOccurrence(1, RegExBridge.UNBOUNDED);
				input.next();
			}
			else if (c == LBRACE)
			{
				input.next(); // skip lbrace
				if (peekIs(input, ','))
				{
					throw new StringRegExException(StringRegExException.Kind.invalidCommaQuantifier, null);
				}
				int min = parseInt(input);
				int max;
				// {n} is allowed, and is used repeated in examples
				// elsewhere in the rec
				if (!peekIs(input, ','))
					max = min;// throw new RuntimeException("invalid qualifier: no comma");
				else
				{
					input.next(); // skip comma
					if (peekIs(input, RBRACE))
					{
						max = RegExBridge.UNBOUNDED;
					}
					else
					{
						max = parseInt(input);
					}
				}
				if (max < min || min < 0)
				{
					throw new StringRegExException(StringRegExException.Kind.invalidRangeQualifier, new Object[] { min, max });
				}
				if (!peekIs(input, RBRACE))
				{
					throw new StringRegExException(StringRegExException.Kind.invalidQualifierNoClosure, null);
				}
				atom.setOccurrence(min, max);
				input.next(); // skip brace
			}
		}
		return atom;
	}

	/**
	 * atom ::= ( '(' expr ')' ) | char-expr | char-escape | normal char | char ref
	 */
	private static StringRegExPatternTerm parseAtom(final StringRegExPatternInput input) throws StringRegExException
	{
		if (peekIs(input, LPAREN))
		{
			input.next(); // skip lparen
			StringRegExPatternTerm expr = new StringRegExPatternTerm(StringRegExPatternTerm.Type.TYPE_SEQ);
			expr.addSubTerm(parseExpr(input));
			if (!peekIs(input, RPAREN))
			{
				throw new StringRegExException(StringRegExException.Kind.invalidAtomNoClosure, null);
			}
			input.next(); // skip rparen
			return expr;
		}
		else if (peekIs(input, LBRACKET))
		{
			// character expression
			return parseCharExpr(input);
		}
		/*
		 * Errata still pending but WorkGroup decided to classify as error
		 * http://www.w3.org/2001/05/xmlschema-rec-comments#pfiregexXmlCharRef
		 * 
		 * else if (peekIs(input, REFERENCE)){ // character reference input.next(); if (!peekIs(input, '#')) throw new
		 * RuntimeException("invalid character reference: missing #"); input.next(); int value = 0; int base = 10; if
		 * (peekIs(input, 'x')){ input.next(); base = 16; } while (input.hasNext() && !peekIs(input, ';')){ String s =
		 * (String) input.next(); char c = s.charAt(0); value = value * base + Character.digit(c, base); } if
		 * (!peekIs(input, ';')) throw new RuntimeException("invalid character reference: missing ;"); input.next();
		 * StringPatternTerm atom = new StringPatternTerm(StringPatternTerm.TYPE_LEAF_POSITIVE); atom.addChar((char)
		 * value); return atom; }
		 */
		else
		{
			if (!input.hasNext())
			{
				throw new StringRegExException(StringRegExException.Kind.invalidEmptyAtom, null);
			}
			StringRegExPatternTerm atom = new StringRegExPatternTerm(StringRegExPatternTerm.Type.TYPE_LEAF_POSITIVE);
			parseChar(input, atom, CHAR, DOT_IS_SPECIAL);
			return atom;
		}
	}

	/**
	 * char-expr ::= '[' '^'? char-range+ ('-' char-expr)? ']'
	 */
	private static StringRegExPatternTerm parseCharExpr(final StringRegExPatternInput input) throws StringRegExException
	{
		input.next(); // skip lbracket
		final boolean negative = peekIs(input, NOT);
		if (negative)
		{
			input.next(); // skip not
		}
		final StringRegExPatternTerm.Type type = negative ? StringRegExPatternTerm.Type.TYPE_LEAF_NEGATIVE : StringRegExPatternTerm.Type.TYPE_LEAF_POSITIVE;
		final StringRegExPatternTerm atom = new StringRegExPatternTerm(type);
		if (peekIs(input, LBRACKET))
		{
			// must be subtraction
			final StringRegExPatternTerm a = parseCharExpr(input);
			atom.addSubTerm(a);
			if (peekIs(input, RANGE))
			{
				input.next(); // skip subtraction
				final StringRegExPatternTerm b = parseCharExpr(input);
				atom.addNegativeTerm(b);
			}
			if (!peekIs(input, RBRACKET))
			{
				throw new StringRegExException(StringRegExException.Kind.invalidSubtractionNoClosure, null);
			}
		}
		else
		{
			do
			{
				char first = (char)0; // first char of potential range
				if (peekIs(input, MINUS))
				{
					input.next();
					if (peekIs(input, LBRACKET))
					{
						atom.addNegativeTerm(parseCharExpr(input));
						if (!peekIs(input, RBRACKET))
						{
							throw new StringRegExException(StringRegExException.Kind.invalidExprMissing, "]");
						}
					}
					else
					{
						atom.addChar(MINUS);
						first = MINUS;
					}
				}
				else
				{
					first = parseChar(input, atom, XMLCHAR, DOT_NORMAL_CHAR);
				}

				if (first != 0 && peekIs(input, RANGE))
				{
					input.next(); // skip range char
					if (!peekIs(input, RBRACKET))
					{
						final char last = parseChar(input, atom, XMLCHAR, DOT_NORMAL_CHAR);
						if (last == 0)
						{
							throw new StringRegExException(StringRegExException.Kind.invalidCharRange, null);
						}
						if (first > last)
						{
							throw new StringRegExException(StringRegExException.Kind.invalidCharRangeEx, new Object[] { first, last });
						}
						atom.combineRanges();
					}
					else
					{
						atom.addChar(MINUS); // add as normal char
					}
				}
			}
			while (!peekIs(input, RBRACKET) && input.hasNext());
			if (!input.hasNext())
			{
				throw new StringRegExException(StringRegExException.Kind.invalidExprMissing, "]");
			}
		}
		input.next(); // skip rbracket
		return atom;
	}

	/**
	 * char-range ::= char-group | (char ('-' char)?)
	 */
	// private static void parseCharRange(PatternInput input, StringPatternTerm atom){
	// char first = parseChar(input, atom, XMLCHARINCDASH, DOT_IS_SPECIAL);
	// }
	/**
	 * char-group ::= char | meta-char | char-class
	 * <p/>
	 * if the normal parameter is true then: Normal Character [10] Char ::= [^.\?*+()|#x5B#x5D]
	 */
	private static char parseChar(final StringRegExPatternInput input, final StringRegExPatternTerm atom, final String excluded, final boolean specialDot) throws StringRegExException
	{
		String s = input.next();
		char c = s.charAt(0);
		if (c == '\\' && input.hasNext())
		{
			s = input.next();
			c = s.charAt(0);
			switch (c)
			{
				case 'n':
					atom.addChar(NEWLINE);
				break;
				case 'r':
					atom.addChar(RETURN);
				break;
				case 't':
					atom.addChar(TAB);
				break;
				case '\\':
				case '.':
				case '-':
				case '|':
				case '^':
				case '?':
				case '*':
				case '+':
				case '[':
				case ']':
				case '(':
				case ')':
				case '{':
				case '}':
					atom.addChar(c);
				break;
				case 'p':
				case 'P':
					atom.addSubTerm(createUnicodeBlockTerm(c, input));
					return (char)0;
				default:
					atom.addSubTerm(createXMLTerm(c));
					return (char)0;
			}
			return c;
		}
		if (c == '.' && specialDot)
		{
			// all but line ends
			StringRegExPatternTerm t = new StringRegExPatternTerm(StringRegExPatternTerm.Type.TYPE_LEAF_NEGATIVE);
			t.addChar(NEWLINE);
			t.addChar(RETURN);
			atom.addSubTerm(t);
			return (char)0;
		}
		if (excluded.indexOf((int)c) != -1)
		{
			throw new StringRegExException(StringRegExException.Kind.invalidChar, StringRegExPatternTerm.charString(c));
		}
		atom.addChar(c);
		return c;
	}

	/**
	 * returns a new StringPatternTerm corresponding to the given meta-character.
	 */
	private static StringRegExPatternTerm createXMLTerm(char c) throws StringRegExException
	{
		StringRegExPatternTerm term;
		if (c == 's' || c == 'i' || c == 'c' || c == 'd' || c == 'W')
			term = new StringRegExPatternTerm(StringRegExPatternTerm.Type.TYPE_LEAF_POSITIVE);
		else
			term = new StringRegExPatternTerm(StringRegExPatternTerm.Type.TYPE_LEAF_NEGATIVE);
		c = Character.toLowerCase(c);
		if (c == 's')
		{
			term.addChar(' ');
			term.addChar(NEWLINE);
			term.addChar(RETURN);
			term.addChar(TAB);
		}
		else if (c == 'i')
		{
			// name start char (approximate)
			StringRegExPatternTerm subTerm = createUnicodePropertyTerm("L");
			term.addSubTerm(subTerm);
			subTerm = createUnicodePropertyTerm("Nl");
			term.addSubTerm(subTerm);
			term.addChar(':');
			term.addChar('_');
		}
		else if (c == 'c')
		{
			// name char (approximate)
			StringRegExPatternTerm subTerm = createUnicodePropertyTerm("L");
			term.addSubTerm(subTerm);
			subTerm = createUnicodePropertyTerm("Nl");
			term.addSubTerm(subTerm);
			subTerm = createUnicodePropertyTerm("Nd");
			term.addSubTerm(subTerm);
			subTerm = createUnicodePropertyTerm("Mc");
			term.addSubTerm(subTerm);
			subTerm = createUnicodePropertyTerm("Me");
			term.addSubTerm(subTerm);
			subTerm = createUnicodePropertyTerm("Mn");
			term.addSubTerm(subTerm);
			subTerm = createUnicodePropertyTerm("Lm");
			term.addSubTerm(subTerm);
			term.addSubTerm(subTerm);
			term.addChar(':');
			term.addChar('_');
			term.addChar('-');
			term.addChar('.');
		}
		else if (c == 'd')
		{
			StringRegExPatternTerm subTerm = createUnicodePropertyTerm("Nd");
			term.addSubTerm(subTerm);
		}
		else if (c == 'w')
		{
			StringRegExPatternTerm subTerm = createUnicodePropertyTerm("P");
			term.addSubTerm(subTerm);
			subTerm = createUnicodePropertyTerm("S");
			term.addSubTerm(subTerm);
			subTerm = createUnicodePropertyTerm("C");
			term.addSubTerm(subTerm);
		}
		else
		{
			throw new StringRegExException(StringRegExException.Kind.unrecognizedEscapeChar, c);
		}
		return term;
	}

	/**
	 * Handles block escapes, which are of the form: "\p{IsBlockName}" or maybe
	 * "\p{L[ultmo]?|M[nce]?|N[dlo]?|P[cdseifo]?|Z[slp]?|S[mcko]?|C[cfon]?}
	 * <p/>
	 * [27] catEsc ::= '\p{' charProp '}' [28] complEsc ::= '\P{' charProp '}' [29] charProp ::= IsCategory | IsBlock
	 * [30] IsCategory ::= Letters | Marks | Numbers | Punctuation | Separators | Symbols | Others [31] Letters ::= 'L'
	 * [ultmo]? [32] Marks ::= 'M' [nce]? [33] Numbers ::= 'N' [dlo]? [34] Punctuation::= 'P' [cdseifo]? [35] Separators
	 * ::= 'Z' [slp]? [36] Symbols ::= 'S' [mcko]? [37] Others ::= 'C' [cfon]?
	 * 
	 * @param c
	 *            either 'p' or 'P'
	 * @param input
	 *            c has just been consumed
	 */
	private static StringRegExPatternTerm createUnicodeBlockTerm(char c, StringRegExPatternInput input) throws StringRegExException
	{
		if (peekIs(input, '{'))
		{
			input.next();
		}
		else
		{
			throw new StringRegExException(StringRegExException.Kind.expectedBraceAfter, c);
		}
		StringBuilder blockName = new StringBuilder();
		while (input.hasNext() && !peekIs(input, RBRACE))
		{
			blockName.append(input.next());
		}
		if (peekIs(input, RBRACE))
		{
			input.next();
			if (blockName.length() > 0 && blockName.length() <= 2)
			{
				return createUnicodePropertyTerm(blockName.toString());
			}
			else if (blockName.length() > 2 && blockName.charAt(0) == 'I' && blockName.charAt(1) == 's')
			{
				StringRegExPatternTerm term = new StringRegExPatternTerm(c == 'p' ? StringRegExPatternTerm.Type.TYPE_LEAF_POSITIVE : StringRegExPatternTerm.Type.TYPE_LEAF_NEGATIVE);
				term.setBlock(blockName.substring(2));
				return term;
			}
			else
			{
				throw new StringRegExException(StringRegExException.Kind.blockBeginWIs, null);
			}
		}
		else
		{
			throw new StringRegExException(StringRegExException.Kind.invalidExprMissing, "}");
		}
	}

	private static StringRegExPatternTerm createUnicodePropertyTerm(final String propertyName) throws StringRegExException
	{
		final StringRegExPatternTerm term = new StringRegExPatternTerm(StringRegExPatternTerm.Type.TYPE_LEAF_POSITIVE);
		term.setCategory(propertyName);
		return term;
	}

	/**
	 * parse a non-negative int
	 */
	private static int parseInt(final StringRegExPatternInput input)
	{
		int n = 0;
		while (input.hasNext())
		{
			char c = input.peek().charAt(0);
			if (c <= '9' && c >= '0')
			{
				input.next();
				n = 10 * n + ((int)c - (int)'0');
			}
			else
				break;
		}
		return n;
	}

	static boolean peekIs(StringRegExPatternInput input, char c)
	{
		return input.hasNext() && input.peek().charAt(0) == c;
	}

	public static final char NEWLINE = '\n';
	public static final char RETURN = '\r';
	public static final char TAB = '\t';
	static final char ESCAPE = '\\';
	static final char WILDCARD = '.';
	static final char RANGE = '-';
	static final char OR = '|';
	static final char NOT = '^';
	static final char ZERO_OR_ONE = '?';
	static final char ZERO_OR_MORE = '*';
	static final char ONE_OR_MORE = '+';
	static final char LBRACE = '{';
	static final char RBRACE = '}';
	static final char LPAREN = '(';
	static final char RPAREN = ')';
	static final char LBRACKET = '[';
	static final char RBRACKET = ']';
	static final char MINUS = '-';
	// static final char REFERENCE = '&';
}
