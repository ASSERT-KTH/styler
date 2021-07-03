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
package org.genxdm.processor.w3c.xs.regex.string;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.regex.api.RegExBridge;


/**
 * StringPatternTerm is a kind of PatternTerm for character-based regular expressions. Each leaf may be a character or a
 * set of characters. A leaf term is represented as a collection of ranges and a set of subterms or by a unicode
 * category.
 */
public final class StringRegExPatternTerm
{
	public enum Type
	{
		TYPE_SEQ, TYPE_CHOICE, TYPE_LEAF_POSITIVE, TYPE_LEAF_NEGATIVE, TYPE_LEAF_WILDCARD
	}

	private int minOccurrence = 1;
	private int maxOccurrence = 1;
	private Type type;

	// Support for range of Unicode categories as defined by the Character class
	private int[] m_category;

	/**
	 * List of {@link StringRegExPatternTerm} (for seq or choice)
	 */
	private final List<StringRegExPatternTerm> subTermList = new ArrayList<StringRegExPatternTerm>();

	/**
	 * List of {@link CharRange} (for leaf)
	 */
	private final List<CharRange> rangeList = new ArrayList<CharRange>();

	private StringRegExPatternTerm notTerm;

	public StringRegExPatternTerm(final Type type)
	{
		this.type = type;
	}

	/**
	 * a single character
	 */
	public StringRegExPatternTerm(char c)
	{
		this.type = Type.TYPE_LEAF_POSITIVE;
		rangeList.add(new CharRange(c));
	}

	public void addSubTerm(StringRegExPatternTerm subTerm)
	{
		subTermList.add(subTerm);
	}

	public void addChar(char c)
	{
		rangeList.add(new CharRange(c));
	}

	public void addChar(final int first, final int last)
	{
		rangeList.add(new CharRange(first, last));
	}

	/**
	 * converts the last two range items into one
	 */
	public void combineRanges()
	{
		final CharRange a = rangeList.get(rangeList.size() - 2);
		final CharRange b = rangeList.get(rangeList.size() - 1);
		a.setLast(b.getLast());
		rangeList.remove(rangeList.size() - 1);
	}

	public void addNegativeTerm(final StringRegExPatternTerm term)
	{
		notTerm = term;
	}

	/**
	 * map Unicode category abbr => array of Character constants
	 */
	protected static final HashMap<String, int[]> categoryMap = new HashMap<String, int[]>();

	/**
	 * Maps block name string => Character.UnicodeBlock
	 */
	protected static final HashMap<String, int[]> blockMap = new HashMap<String, int[]>();

	static
	{
		categoryMap.put("L", new int[] { Character.UPPERCASE_LETTER, Character.LOWERCASE_LETTER, Character.TITLECASE_LETTER, Character.MODIFIER_LETTER, Character.OTHER_LETTER }); // All
		// Letters
		categoryMap.put("Lu", new int[] { Character.UPPERCASE_LETTER }); // Uppercase
		categoryMap.put("Ll", new int[] { Character.LOWERCASE_LETTER }); // Lowercase
		categoryMap.put("Lt", new int[] { Character.TITLECASE_LETTER }); // Titlecase
		categoryMap.put("Lm", new int[] { Character.MODIFIER_LETTER }); // Modifier
		categoryMap.put("Lo", new int[] { Character.OTHER_LETTER }); // Other Letters

		categoryMap.put("M", new int[] { Character.NON_SPACING_MARK, Character.COMBINING_SPACING_MARK, Character.ENCLOSING_MARK }); // All
		// Marks
		categoryMap.put("Mn", new int[] { Character.NON_SPACING_MARK }); // Non-Spacing
		categoryMap.put("Mc", new int[] { Character.COMBINING_SPACING_MARK }); // Spacing Combining
		categoryMap.put("Me", new int[] { Character.ENCLOSING_MARK }); // Enclosing

		categoryMap.put("N", new int[] { Character.DECIMAL_DIGIT_NUMBER, Character.LETTER_NUMBER, Character.OTHER_NUMBER }); // All
		// Numbers
		categoryMap.put("Nd", new int[] { Character.DECIMAL_DIGIT_NUMBER }); // Decimal Digit
		categoryMap.put("Nl", new int[] { Character.LETTER_NUMBER }); // Letter
		categoryMap.put("No", new int[] { Character.OTHER_NUMBER }); // Other Numbers

		categoryMap.put("P", new int[] { Character.CONNECTOR_PUNCTUATION, Character.DASH_PUNCTUATION, Character.START_PUNCTUATION, Character.END_PUNCTUATION, Character.OTHER_PUNCTUATION }); // All
		// Punctuation
		categoryMap.put("Pc", new int[] { Character.CONNECTOR_PUNCTUATION }); // Connector
		categoryMap.put("Pd", new int[] { Character.DASH_PUNCTUATION }); // Dash
		categoryMap.put("Ps", new int[] { Character.START_PUNCTUATION }); // Open
		categoryMap.put("Pe", new int[] { Character.END_PUNCTUATION }); // Close
		categoryMap.put("Pi", new int[] { Character.OTHER_PUNCTUATION }); // Initial quote (Unicode 3)
		categoryMap.put("Pf", new int[] { Character.OTHER_PUNCTUATION }); // Final quote (Unicode 3)
		categoryMap.put("Po", new int[] { Character.OTHER_PUNCTUATION }); // Other Punctuation

		categoryMap.put("Z", new int[] { Character.SPACE_SEPARATOR, Character.LINE_SEPARATOR, Character.PARAGRAPH_SEPARATOR }); // All
		// Separators
		categoryMap.put("Zs", new int[] { Character.SPACE_SEPARATOR }); // Space
		categoryMap.put("Zl", new int[] { Character.LINE_SEPARATOR }); // Line
		categoryMap.put("Zp", new int[] { Character.PARAGRAPH_SEPARATOR }); // Paragraph

		categoryMap.put("S", new int[] { Character.MATH_SYMBOL, Character.CURRENCY_SYMBOL, Character.MODIFIER_SYMBOL, Character.OTHER_SYMBOL }); // All
		// Symbols
		categoryMap.put("Sm", new int[] { Character.MATH_SYMBOL }); // Math
		categoryMap.put("Sc", new int[] { Character.CURRENCY_SYMBOL }); // Currency
		categoryMap.put("Sk", new int[] { Character.MODIFIER_SYMBOL }); // Modifier
		categoryMap.put("So", new int[] { Character.OTHER_SYMBOL }); // Other Symbols

		categoryMap.put("C", new int[] { Character.CONTROL, Character.FORMAT, Character.SURROGATE, Character.PRIVATE_USE, Character.UNASSIGNED }); // All
		// Others
		categoryMap.put("Cc", new int[] { Character.CONTROL }); // Control
		categoryMap.put("Cf", new int[] { Character.FORMAT }); // Format
		categoryMap.put("Cs", new int[] { Character.SURROGATE }); // Surrogate
		categoryMap.put("Co", new int[] { Character.PRIVATE_USE }); // Private Use
		categoryMap.put("Cn", new int[] { Character.UNASSIGNED }); // Not Assigned

		// blocks are coded as ranges to stay compatible with Java 1.1
		// (Java 1.2 adds Character.UnicodeBlock, which would help with Unicode 2 blocks)
		blockMap.put("BasicLatin", new int[] { '\u0000', '\u007F' });
		blockMap.put("Latin-1Supplement", new int[] { '\u0080', '\u00FF' });
		blockMap.put("LatinExtended-A", new int[] { '\u0100', '\u017F' });
		blockMap.put("LatinExtended-B", new int[] { '\u0180', '\u024F' });
		blockMap.put("IPAExtensions", new int[] { '\u0250', '\u02AF' });
		blockMap.put("SpacingModifierLetters", new int[] { '\u02B0', '\u02FF' });
		blockMap.put("CombiningDiacriticalMarks", new int[] { '\u0300', '\u036F' });
		blockMap.put("Greek", new int[] { '\u0370', '\u03FF' });
		blockMap.put("Cyrillic", new int[] { '\u0400', '\u04FF' });
		blockMap.put("Armenian", new int[] { '\u0530', '\u058F' });
		blockMap.put("Hebrew", new int[] { '\u0590', '\u05FF' });
		blockMap.put("Arabic", new int[] { '\u0600', '\u06FF' });
		blockMap.put("Syriac", new int[] { '\u0700', '\u074F' });
		blockMap.put("Thaana", new int[] { '\u0780', '\u07BF' });
		blockMap.put("Devanagari", new int[] { '\u0900', '\u097F' });
		blockMap.put("Bengali", new int[] { '\u0980', '\u09FF' });
		blockMap.put("Gurmukhi", new int[] { '\u0A00', '\u0A7F' });
		blockMap.put("Gujarati", new int[] { '\u0A80', '\u0AFF' });
		blockMap.put("Oriya", new int[] { '\u0B00', '\u0B7F' });
		blockMap.put("Tamil", new int[] { '\u0B80', '\u0BFF' });
		blockMap.put("Telugu", new int[] { '\u0C00', '\u0C7F' });
		blockMap.put("Kannada", new int[] { '\u0C80', '\u0CFF' });
		blockMap.put("Malayalam", new int[] { '\u0D00', '\u0D7F' });
		blockMap.put("Sinhala", new int[] { '\u0D80', '\u0DFF' });
		blockMap.put("Thai", new int[] { '\u0E00', '\u0E7F' });
		blockMap.put("Lao", new int[] { '\u0E80', '\u0EFF' });
		blockMap.put("Tibetan", new int[] { '\u0F00', '\u0FFF' });
		blockMap.put("Myanmar", new int[] { '\u1000', '\u109F' });
		blockMap.put("Georgian", new int[] { '\u10A0', '\u10FF' });
		blockMap.put("HangulJamo", new int[] { '\u1100', '\u11FF' });
		blockMap.put("Ethiopic", new int[] { '\u1200', '\u137F' });
		blockMap.put("Cherokee", new int[] { '\u13A0', '\u13FF' });
		blockMap.put("UnifiedCanadianAboriginalSyllabics", new int[] { '\u1400', '\u167F' });
		blockMap.put("Ogham", new int[] { '\u1680', '\u169F' });
		blockMap.put("Runic", new int[] { '\u16A0', '\u16FF' });
		blockMap.put("Khmer", new int[] { '\u1780', '\u17FF' });
		blockMap.put("Mongolian", new int[] { '\u1800', '\u18AF' });
		blockMap.put("LatinExtendedAdditional", new int[] { '\u1E00', '\u1EFF' });
		blockMap.put("GreekExtended", new int[] { '\u1F00', '\u1FFF' });
		blockMap.put("GeneralPunctuation", new int[] { '\u2000', '\u206F' });
		blockMap.put("SuperscriptsandSubscripts", new int[] { '\u2070', '\u209F' });
		blockMap.put("CurrencySymbols", new int[] { '\u20A0', '\u20CF' });
		blockMap.put("CombiningMarksforSymbols", new int[] { '\u20D0', '\u20FF' });
		blockMap.put("LetterlikeSymbols", new int[] { '\u2100', '\u214F' });
		blockMap.put("NumberForms", new int[] { '\u2150', '\u218F' });
		blockMap.put("Arrows", new int[] { '\u2190', '\u21FF' });
		blockMap.put("MathematicalOperators", new int[] { '\u2200', '\u22FF' });
		blockMap.put("MiscellaneousTechnical", new int[] { '\u2300', '\u23FF' });
		blockMap.put("ControlPictures", new int[] { '\u2400', '\u243F' });
		blockMap.put("OpticalCharacterRecognition", new int[] { '\u2440', '\u245F' });
		blockMap.put("EnclosedAlphanumerics", new int[] { '\u2460', '\u24FF' });
		blockMap.put("BoxDrawing", new int[] { '\u2500', '\u257F' });
		blockMap.put("BlockElements", new int[] { '\u2580', '\u259F' });
		blockMap.put("GeometricShapes", new int[] { '\u25A0', '\u25FF' });
		blockMap.put("MiscellaneousSymbols", new int[] { '\u2600', '\u26FF' });
		blockMap.put("Dingbats", new int[] { '\u2700', '\u27BF' });
		blockMap.put("BraillePatterns", new int[] { '\u2800', '\u28FF' });
		blockMap.put("CJKRadicalsSupplement", new int[] { '\u2E80', '\u2EFF' });
		blockMap.put("KangxiRadicals", new int[] { '\u2F00', '\u2FDF' });
		blockMap.put("IdeographicDescriptionCharacters", new int[] { '\u2FF0', '\u2FFF' });
		blockMap.put("CJKSymbolsandPunctuation", new int[] { '\u3000', '\u303F' });
		blockMap.put("Hiragana", new int[] { '\u3040', '\u309F' });
		blockMap.put("Katakana", new int[] { '\u30A0', '\u30FF' });
		blockMap.put("Bopomofo", new int[] { '\u3100', '\u312F' });
		blockMap.put("HangulCompatibilityJamo", new int[] { '\u3130', '\u318F' });
		blockMap.put("Kanbun", new int[] { '\u3190', '\u319F' });
		blockMap.put("BopomofoExtended", new int[] { '\u31A0', '\u31BF' });
		blockMap.put("EnclosedCJKLettersandMonths", new int[] { '\u3200', '\u32FF' });
		blockMap.put("CJKCompatibility", new int[] { '\u3300', '\u33FF' });
		blockMap.put("CJKUnifiedIdeographsExtensionA", new int[] { '\u3400', '\u4DB5' });
		blockMap.put("CJKUnifiedIdeographs", new int[] { '\u4E00', '\u9FFF' });
		blockMap.put("YiSyllables", new int[] { '\uA000', '\uA48F' });
		blockMap.put("YiRadicals", new int[] { '\uA490', '\uA4CF' });
		blockMap.put("HangulSyllables", new int[] { '\uAC00', '\uD7A3' });
		blockMap.put("HighSurrogates", new int[] { 0xD800, 0xDB7F });
		blockMap.put("HighPrivateUseSurrogates", new int[] { 0xDB80, 0xDBFF });
		blockMap.put("LowSurrogates", new int[] { 0xDC00, 0xDFFF });
		blockMap.put("PrivateUse", new int[] { '\uE000', '\uF8FF' });
		blockMap.put("CJKCompatibilityIdeographs", new int[] { '\uF900', '\uFAFF' });
		blockMap.put("AlphabeticPresentationForms", new int[] { '\uFB00', '\uFB4F' });
		blockMap.put("ArabicPresentationForms-A", new int[] { '\uFB50', '\uFDFF' });
		blockMap.put("CombiningHalfMarks", new int[] { '\uFE20', '\uFE2F' });
		blockMap.put("CJKCompatibilityForms", new int[] { '\uFE30', '\uFE4F' });
		blockMap.put("SmallFormVariants", new int[] { '\uFE50', '\uFE6F' });
		blockMap.put("ArabicPresentationForms-B", new int[] { '\uFE70', '\uFEFE' });
		// blockMap.put("Specials", new int[]{0xFEFF, 0xFEFF}); // Formal
		blockMap.put("HalfwidthandFullwidthForms", new int[] { '\uFF00', '\uFFEF' });
		// blockMap.put("Specials", new int[]{0xFFF0, 0xFFFD}); // Formal
		blockMap.put("Specials", new int[] { 0xFEFF, 0xFFFD }); // Range overlaps.
		blockMap.put("OldItalic", new int[] { 0x10300, 0x1032F });
		blockMap.put("Gothic", new int[] { 0x10330, 0x1034F });
		blockMap.put("Deseret", new int[] { 0x10400, 0x1044F });
		blockMap.put("ByzantineMusicalSymbols", new int[] { 0x1D000, 0x1D0FF });
		blockMap.put("MusicalSymbols", new int[] { 0x1D100, 0x1D1FF });
		blockMap.put("MathematicalAlphanumericSymbols", new int[] { 0x1D400, 0x1D7FF });
		blockMap.put("CJKUnifiedIdeographsExtensionB", new int[] { 0x20000, 0x2A6D6 });
		blockMap.put("CJKCompatibilityIdeographsSupplement", new int[] { 0x2F800, 0x2FA1F });
		blockMap.put("Tags", new int[] { 0xE0000, 0xFFFFD });
	}

	/**
	 * set Unicode category
	 */
	public void setCategory(final String category) throws StringRegExException
	{
		PreCondition.assertArgumentNotNull(category, "abbr");
		m_category = categoryMap.get(category);
		if (m_category == null)
		{
			throw new StringRegExException(StringRegExException.Kind.invalidUnicodeCategory, category);
		}
	}

	/**
	 * set Unicode block to match against.
	 * 
	 * @param blockName
	 *            the XSD-style block name (spaces removed)
	 */
	public void setBlock(final String blockName) throws StringRegExException
	{
		final int range[] = blockMap.get(blockName);
		if (range == null)
		{
			throw new StringRegExException(StringRegExException.Kind.invalidUnicodeBlockName, blockName);
		}
		addChar(range[0], range[1]);
	}

	public void setOccurrence(final int min, final int max)
	{
		minOccurrence = min;
		maxOccurrence = max;
	}

	/**
	 * returns whether this term contains a sequence of one or more subterms
	 */
	public boolean isSequence()
	{
		return type == Type.TYPE_SEQ;
	}

	/**
	 * returns whether this term contains a choice of one or more subterms
	 */
	public boolean isChoice()
	{
		return type == Type.TYPE_CHOICE;
	}

	/**
	 * returns whether this term contains a set of one or more subterms
	 */
	public boolean isInterleave()
	{
		return false;
	}

	/**
	 * returns an Enumeration of this term's subterms
	 */
	public Iterable<StringRegExPatternTerm> getSubTerms()
	{
		return subTermList;
	}

	/**
	 * returns whether this leaf term matches the given token.
	 */
	public boolean matches(final String token)
	{
		if (type == Type.TYPE_LEAF_WILDCARD)
		{
			return true;
		}
		// Debug.assert(type == TYPE_LEAF_POSITIVE || type == TYPE_LEAF_NEGATIVE);
		boolean matched = false;
		if (m_category != null)
		{
			if ((token != null) && token.length() > 0)
			{
				for (int i = 0; i < m_category.length && !matched; i++)
				{
					matched = m_category[i] == Character.getType(token.charAt(0));
				}
			}
		}
		else
		{
			for (final Iterator<CharRange> i = rangeList.iterator(); i.hasNext() && !matched;)
			{
				matched = i.next().matches(token);
			}
			for (final Iterator<StringRegExPatternTerm> i = subTermList.iterator(); i.hasNext() && !matched;)
			{
				matched = i.next().matches(token);
			}
		}
		if (type == Type.TYPE_LEAF_NEGATIVE)
		{
			matched = !matched;
		}
		if (matched && notTerm != null && notTerm.matches(token))
		{
			matched = false;
		}
		return matched;
	}

	/**
	 * Returns true if this term and the other term are both leaf terms and there exist some tokens which match both
	 * terms. Used to check for determinism.
	 */
	public boolean intersects(StringRegExPatternTerm other)
	{
		if (isLeaf() && other.isLeaf())
		{
			if (type == Type.TYPE_LEAF_WILDCARD || other.type == Type.TYPE_LEAF_WILDCARD)
				return true;
			if (type == Type.TYPE_LEAF_NEGATIVE)
			{
				if (other.type == Type.TYPE_LEAF_POSITIVE)
					return other.intersects(this); // prefer this being positive
				else
					return false; // both negative => assume intersects
			}
			if (m_category != null)
			{
				if (other.m_category == null)
					return other.intersects(this); // prefer this being non-category
				else
				{
					// only intersect if they share same categories
					for (final int x : m_category)
					{
						for (final int y : other.m_category)
						{
							if (x == y)
							{
								return true;
							}
						}
					}
					return false;
				}
			}
			// fixme should check categories
			// type must be positive (though we could have negative subterms)
			// see if any of our elements match 'other'
			for (final CharRange cr : rangeList)
			{
				for (int c = cr.getFirst(); c <= cr.getLast(); c++)
				{
					if (notTerm != null && notTerm.matches(String.valueOf(c)))
					{
						continue;
					}
					if (other.matches(String.valueOf(c)))
					{
						return true; // they intersect
					}
				}
			}
			for (final StringRegExPatternTerm term : subTermList)
			{
				if (term.intersects(other))
				{
					return true; // they intersect
				}
			}
		}
		return false;
	}

	protected boolean isLeaf()
	{
		return type == Type.TYPE_LEAF_WILDCARD || type == Type.TYPE_LEAF_POSITIVE || type == Type.TYPE_LEAF_NEGATIVE;
	}

	/**
	 * returns the minimum number of occurrences of this term.
	 */
	public int minOccurs()
	{
		return minOccurrence;
	}

	/**
	 * returns the maximum number of occurrences of this term. returns Integer.MAX_VALUE to indicate unlimited
	 * occurrences.
	 */
	public int maxOccurs()
	{
		return maxOccurrence;
	}

	public String toString()
	{
		String s = "";
		if (isChoice())
		{
			s += "(";
			for (int i = 0; i < subTermList.size(); i++)
			{
				if (i > 0)
					s += " | ";
				s += subTermList.get(i).toString();
			}
			s += ")";
		}
		else if (isSequence())
		{
			s += "(";
			for (int i = 0; i < subTermList.size(); i++)
			{
				if (i > 0)
					s += ", ";
				s += subTermList.get(i).toString();
			}
			s += ")";
		}
		else if (type == Type.TYPE_LEAF_WILDCARD)
		{
			s = "WILDCARD";
		}
		else
		{
			if (type == Type.TYPE_LEAF_POSITIVE && subTermList.size() == 0 && rangeList.size() == 1 && rangeList.get(0).toString().length() == 1)
				s = rangeList.get(0).toString();
			else
			{
				s += "[";
				if (type == Type.TYPE_LEAF_NEGATIVE)
					s += "^";
				if (m_category != null)
				{
					s += "UnicodeProperty(";
					for (int i = 0; i < m_category.length; i++)
						s += (i == 0 ? "" : ",") + m_category[i];
					s += ")";
				}
				for (final CharRange cr : rangeList)
				{
					s += cr.toString();
				}
				for (final StringRegExPatternTerm term : subTermList)
				{
					s += term.toString();
				}
				if (notTerm != null)
					s += "-" + notTerm.toString();
				s += "]";
			}
		}
		if (minOccurrence == 1 && maxOccurrence == 1)
		{
			// Do nothing
		}
		else if (minOccurrence == 0 && maxOccurrence == 1)
		{
			s += "?";
		}
		else if (minOccurrence == 0 && maxOccurrence == RegExBridge.UNBOUNDED)
		{
			s += "*";
		}
		else if (minOccurrence == 1 && maxOccurrence == RegExBridge.UNBOUNDED)
		{
			s += "+";
		}
		else
		{
			s += "{" + minOccurrence + ",";
			if (maxOccurrence != RegExBridge.UNBOUNDED)
			{
				s += maxOccurrence;
			}
			s += "}";
		}
		return s;
	}

	static String ctoUnicodeEsc(final int i)
	{
		char out[] = { '\\', 'u', Character.forDigit((i >> 12) & 0xF, 16), Character.forDigit((i >> 8) & 0xF, 16), Character.forDigit((i >> 4) & 0xF, 16), Character.forDigit(i & 0xF, 16) };
		return new String(out);
	}

	public static String charString(int c)
	{
		if (c < ' ' || c >= 255)
		{
			return ctoUnicodeEsc(c);
		}
		switch (c)
		{
			case StringRegExParser.NEWLINE:
				return "\\n";
			case StringRegExParser.RETURN:
				return "\\r";
			case StringRegExParser.TAB:
				return "\\t";
			case '\\':
				return "\\\\";
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
				return "\\" + c;
		}
		return "" + c;
	}
}
