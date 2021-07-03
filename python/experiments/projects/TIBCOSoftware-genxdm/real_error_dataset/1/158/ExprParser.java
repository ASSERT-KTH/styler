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
package org.genxdm.processor.xpath.v10.expressions;

import java.lang.reflect.Array;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.NodeKind;
import org.genxdm.processor.xpath.v10.patterns.PathPatternBase;
import org.genxdm.processor.xpath.v10.patterns.Pattern;
import org.genxdm.processor.xpath.v10.relations.NumericRelation;
import org.genxdm.processor.xpath.v10.relations.Relation;
import org.genxdm.processor.xpath.v10.tests.AttributeTest;
import org.genxdm.processor.xpath.v10.tests.ElementTest;
import org.genxdm.processor.xpath.v10.tests.NamespaceTest;
import org.genxdm.processor.xpath.v10.tests.NodeTypeTest;
import org.genxdm.processor.xpath.v10.tests.ProcessingInstructionTest;
import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.Converter;
import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.ExprParseException;
import org.genxdm.xpath.v10.NodeSetExpr;
import org.genxdm.xpath.v10.NumberExpr;
import org.genxdm.xpath.v10.VariantExpr;
import org.genxdm.xpath.v10.extend.Function;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;
import org.genxdm.xpath.v10.extend.ConvertibleNodeSetExpr;

/**
 * XPath expression parser / compiler extends the lexer ExprTokenizer
 */
final class ExprParser 
    extends ExprTokenizer
{
	private final ExprContextStatic statEnv;
	private final HashMap<String, AxisExpr> axisTable;
	private final HashMap<String, Function> functionTable;
	private final HashMap<String, Relation> relationTable;

	public ExprParser(final String expr, final ExprContextStatic statEnv, final HashMap<String, AxisExpr> axisTable, final HashMap<String, Function> functionTable,
			final HashMap<String, Relation> relationTable)
	{
		super(expr);
		this.statEnv = statEnv;
		this.axisTable = axisTable;
		this.functionTable = functionTable;
		this.relationTable = relationTable;
	}

	//
	// returns an expanded Name from the qName in
	// currentTokenValue
	//
	private QName expandName() throws ExprParseException
	{
		final int index = currentTokenValue.indexOf(':');
		final String prefix = (index == -1) ? XMLConstants.DEFAULT_NS_PREFIX : currentTokenValue.substring(0, index);
		final String localName = currentTokenValue.substring(index + 1);
		if (prefix.length() > 0)
		{
			final String ns = statEnv.getNamespace(prefix); // returns null if not bound.

			if (ns != null)
			{
				return new QName(ns, localName, prefix);
			}
			else
			{
				throw new ExprParseException("prefix '" + prefix + "' is not bound to a namespace.");
			}
		}
		else
		{
			// In XPath 1.0, the default namespace for unqualified QName(s) is the namespace
			// with a zero-length name. Note that in XPath 2.0 we would distinguish between
			// function names, element and type names, and even variable names.
			return new QName(XMLConstants.NULL_NS_URI, localName, prefix);
		}
	}

	//
	// gets the Namespace URI associated with the prefix in
	// currentTokenValue
	//
	private String expandPrefix() throws ExprParseException
	{
		String ns = statEnv.getNamespace(currentTokenValue);
		if (ns == null)
		{
			throw new ExprParseException("undefined prefix");
		}
		return ns;
	}

	//
	// Checks to ensure that the CurrentToken is ')', then
	// lexes the next
	//
	private final void expectRpar() throws ExprParseException
	{
		if (currentToken != TOK_RPAR)
		{
			throw new ExprParseException("expected )");
		}
		next();
	}

	//
	// Checks to ensure that the currentToken is ']', then
	// lexes the next
	//
	private final void expectRsqb() throws ExprParseException
	{
		if (currentToken != TOK_RSQB)
		{
			throw new ExprParseException("expected ]");
		}
		next();
	}

	/**
	 * A ConvertibleExpr allows for the casting of one type to another for the purpose of making a comparison
	 */
	ConvertibleExprImpl makeRelationalExpr(final Relation rel, final ConvertibleExpr e1, final ConvertibleExpr e2) throws ExprParseException
	{
		// OPT: have some more expressions for non-variant cases
		if (e1 instanceof NodeSetExpr || e2 instanceof NodeSetExpr || e1 instanceof VariantExpr || e2 instanceof VariantExpr)
		{
			return new VariantRelationalExpr(rel, e1.makeVariantExpr(statEnv), e2.makeVariantExpr(statEnv));
		}

		if (rel instanceof NumericRelation)
		{
			return new NumberRelationalExpr(rel, e1.makeNumberExpr(statEnv), e2.makeNumberExpr(statEnv));
		}

		if (e1 instanceof BooleanExpr || e2 instanceof BooleanExpr)
		{
			return new BooleanRelationalExpr(rel, e1.makeBooleanExpr(statEnv), e2.makeBooleanExpr(statEnv));
		}

		if (e1 instanceof NumberExpr || e2 instanceof NumberExpr)
		{
			return new NumberRelationalExpr(rel, e1.makeNumberExpr(statEnv), e2.makeNumberExpr(statEnv));
		}

		return new StringRelationalExpr(rel, e1.makeStringExpr(statEnv), e2.makeStringExpr(statEnv));
	}

	//
	// XPath production #25 AdditiveExpr
	//
	private ConvertibleExpr parseAdditiveExpr() throws ExprParseException
	{
		ConvertibleExpr expr = parseMultiplicativeExpr();
		loop: for (;;)
		{
			switch (currentToken)
			{
				case TOK_PLUS:
					next();
					expr = new AddExpr(expr.makeNumberExpr(statEnv), parseMultiplicativeExpr().makeNumberExpr(statEnv));
				break;
				case TOK_MINUS:
					next();
					expr = new SubtractExpr(expr.makeNumberExpr(statEnv), parseMultiplicativeExpr().makeNumberExpr(statEnv));
				break;
				default:
				break loop;
			}
		}
		return expr;
	}

	//
	// XPath production #22
	//
	private ConvertibleExpr parseAndExpr() throws ExprParseException
	{
		ConvertibleExpr expr = parseEqualityExpr();
		while (currentToken == TOK_AND)
		{
			next();
			expr = new AndExpr(expr.makeBooleanExpr(statEnv), parseEqualityExpr().makeBooleanExpr(statEnv));
		}
		return expr;
	}

	//
	// parse the zero or more arguments to a function call
	// XPath Productions #16 FunctionCall, and 17 Argument
	// Production #17 (Argument) is an Expr (Production #14)
	// which is in turn an OrExpr (Production #21)
	//
	// We return the Arguments as an array of ConvertibleExprs
	//
	private ConvertibleExpr[] parseArgs() throws ExprParseException
	{
		if (currentToken == TOK_RPAR)
		{
			next();
			return (ConvertibleExprImpl[])Array.newInstance(ConvertibleExprImpl.class, 0);
		}
		ConvertibleExpr[] args = (ConvertibleExpr[])Array.newInstance(ConvertibleExprImpl.class, 1);
		for (;;)
		{
			args[args.length - 1] = parseOrExpr();
			if (currentToken != TOK_COMMA)
			{
				break;
			}
			next();
			ConvertibleExpr[] oldArgs = args;
			args = (ConvertibleExprImpl[])Array.newInstance(ConvertibleExprImpl.class, oldArgs.length + 1);
			System.arraycopy(oldArgs, 0, args, 0, oldArgs.length);
		}
		expectRpar(); // check currentToken to ensure it's ")"
		return args;
	}

	//
	// XPath Production #23
	//
	private ConvertibleExpr parseEqualityExpr() throws ExprParseException
	{
		ConvertibleExpr expr = parseRelationalExpr();
		loop: for (;;)
		{
			switch (currentToken)
			{
				case TOK_EQUALS:
					next();
					expr = makeRelationalExpr(relationTable.get("="), expr, parseRelationalExpr());
				break;
				case TOK_NOT_EQUALS:
					next();
					expr = makeRelationalExpr(relationTable.get("!="), expr, parseRelationalExpr());
				break;
				default:
				break loop;
			}
		}
		return expr;
	}

	public ConvertibleExpr parseExpr() throws ExprParseException
	{
		next();
		ConvertibleExpr expr = parseOrExpr();
		if (currentToken != TOK_EOF)
		{
			throw new ExprParseException("unexpected token");
		}
		return expr;
	}

	//
	// XPath production #26
	//
	private ConvertibleExpr parseMultiplicativeExpr() throws ExprParseException
	{
		// get the first part
		ConvertibleExpr expr = parseUnaryExpr();
		loop: for (;;)
		{
			switch (currentToken)
			{
				case TOK_DIV:
					next();
					expr = new DivideExpr(expr.makeNumberExpr(statEnv), parseUnaryExpr().makeNumberExpr(statEnv));
				break;
				case TOK_MOD:
					next();
					expr = new ModuloExpr(expr.makeNumberExpr(statEnv), parseUnaryExpr().makeNumberExpr(statEnv));
				break;
				case TOK_MULTIPLY:
					next();
					expr = new MultiplyExpr(expr.makeNumberExpr(statEnv), parseUnaryExpr().makeNumberExpr(statEnv));
				break;
				default:
				break loop;
			}
		}
		return expr;
	}

	//
	// XPath Production #7
	//
	// Compile a node test for an XPath pattern step,
	// up to, but not including any predicates
	//
	// WDL do not return null, even if the test is vacuous (e.g. "node()"
	//
	// TODO: Here we need the principal node kind.
	private PathPatternBase parseNodeTest(final NodeKind principalNodeKind) throws ExprParseException
	{
		PathPatternBase nodeTest;
		switch (currentToken)
		{
			case TOK_QNAME:
			{
				final QName name = expandName();
				switch (principalNodeKind)
				{
					case ELEMENT:
					{
						nodeTest = new ElementTest(name.getNamespaceURI(), name.getLocalPart());
					}
					break;
					case ATTRIBUTE:
					{
						nodeTest = new AttributeTest(name.getNamespaceURI(), name.getLocalPart());
					}
					break;
					case NAMESPACE:
					{
						nodeTest = new NamespaceTest();
					}
					break;
					default:
					{
						throw new AssertionError(principalNodeKind);
					}
				}
			}
			break;
			case TOK_STAR:
			{
				switch (principalNodeKind)
				{
					case ELEMENT:
					{
						nodeTest = new NodeTypeTest(NodeKind.ELEMENT);
					}
					break;
					case ATTRIBUTE:
					{
						nodeTest = null;
					}
					break;
					case NAMESPACE:
					{
						nodeTest = null;
					}
					break;
					default:
					{
						throw new AssertionError(principalNodeKind);
					}
				}
			}
			break;
			case TOK_NAME_COLON_STAR:
			{
				switch (principalNodeKind)
				{
					case ELEMENT:
					{
						nodeTest = new ElementTest(expandPrefix(), null);
					}
					break;
					case ATTRIBUTE:
					{
						nodeTest = new AttributeTest(expandPrefix(), null);
					}
					break;
					case NAMESPACE:
					{
						nodeTest = new NamespaceTest();
					}
					break;
					default:
					{
						throw new AssertionError(principalNodeKind);
					}
				}
			}
			break;
			case TOK_PROCESSING_INSTRUCTION_LPAR:
			{
				next();
				if (currentToken == TOK_LITERAL)
				{
					nodeTest = new ProcessingInstructionTest(expandName().getLocalPart());
					next();
				}
				else
				{
					nodeTest = new NodeTypeTest(NodeKind.PROCESSING_INSTRUCTION);
				}
				expectRpar();
				return nodeTest;
			}
			case TOK_COMMENT_LPAR:
			{
				next();
				expectRpar();
				return new NodeTypeTest(NodeKind.COMMENT);
			}
			case TOK_TEXT_LPAR: // text()
			{
				next();
				expectRpar();
				return new NodeTypeTest(NodeKind.TEXT);
			}
			case TOK_NODE_LPAR: // node()
			{
				next();
				expectRpar();
				switch (principalNodeKind)
				{
					case ELEMENT:
					{
						return new NodeTypeTest(null);
					}
					case ATTRIBUTE:
					{
						return new NodeTypeTest(NodeKind.ATTRIBUTE);
					}
					case NAMESPACE:
					{
						return new NodeTypeTest(NodeKind.NAMESPACE);
					}
					default:
					{
						throw new AssertionError(principalNodeKind);
					}
				}
			}
			default:
			{
				throw new ExprParseException("expected node test");
			}
		}
		next();
		return nodeTest;
	}

	//
	// XPath Production #21
	//
	private ConvertibleExpr parseOrExpr() throws ExprParseException
	{
		ConvertibleExpr expr = parseAndExpr();
		while (currentToken == TOK_OR)
		{
			next();
			expr = new OrExpr(expr.makeBooleanExpr(statEnv), parseAndExpr().makeBooleanExpr(statEnv));
		}
		return expr;
	}

	//
	// XPath production #19
	//
	private ConvertibleExpr parsePathExpr() throws ExprParseException
	{
		if (tokenStartsStep())
		{
			return parseRelativeLocationPath(); // XPath production #3
		}

		if (currentToken == TOK_SLASH)
		{
			next();
			if (tokenStartsStep())
			{
				// XPath production #2
				return new RootExpr(parseRelativeLocationPath());
			}
			// the root, by itself
			return new RootExpr(axisTable.get("self"));
		}
		if (currentToken == TOK_SLASH_SLASH)
		{

			// abbreviated absolute location XPath production #10
			next();
			return new RootExpr(axisTable.get("descendant-or-self").compose(parseRelativeLocationPath()));
		}

		//
		// if none of the above alternatives, we should be looking
		// at a FilterExpression (production #20) followed by
		// either a "/" or "//", and then a RelativeLocationPath
		// (production #3)
		//

		ConvertibleExpr expr = parsePrimaryExpr();

		// Production 20 requires at least one primary expression
		// and any number of predicates
		// the TOK_LSQB ("[") starts a predicate

		while (currentToken == TOK_LSQB)
		{
			next();
			expr = new FilterExpr(expr.makeNodeSetExpr(statEnv), parseOrExpr().makePredicateExpr(statEnv));
			expectRsqb();
		}

		if (currentToken == TOK_SLASH)
		{
			next();
			return expr.makeNodeSetExpr(statEnv).compose(parseRelativeLocationPath());
		}
		else if (currentToken == TOK_SLASH_SLASH)
		{
			next();
			return expr.makeNodeSetExpr(statEnv).compose(axisTable.get("descendant-or-self").compose(parseRelativeLocationPath()));
		}
		else
			return expr;
	}

	//
	// Productions #4 and #8
	//
	private ConvertibleNodeSetExprImpl parsePredicates(final AxisExpr axis, final Pattern nodeTest) throws ExprParseException
	{
		ConvertibleNodeSetExprImpl expr = axis;
		if (nodeTest != null)
		{
			expr = new NodeTestExpr(expr, nodeTest);
		}
		while (currentToken == TOK_LSQB)
		{
			next();
			expr = new FilterExpr(expr, parseOrExpr().makePredicateExpr(statEnv));
			expectRsqb();
		}
		return axis.makeDocumentOrderExpr(expr);
	}

	//
	// XPath Production #15 PrimaryExpr
	// a VariableReference (production #36) OR
	// "(" Expr ")" (prod #14) OR
	// Literal (prod #29) OR
	// Number (prod #30) OR
	// FunctionCall (prod #16)
	//
	private ConvertibleExpr parsePrimaryExpr() throws ExprParseException
	{
		ConvertibleExpr expr;
		switch (currentToken)
		{
			case TOK_VARIABLE_REF:
				// prod #36
			{
				final QName name = expandName();
				if (statEnv.containsVariable(name))
				{
					expr = new VariableRefExpr(name);
				}
				else
				{
					throw new ExprParseException("no such variable: " + name + " in $" + currentTokenValue);
				}
				break;
			}

			case TOK_LPAR:
			{
				// prod #14
				next();
				expr = parseOrExpr();
				expectRpar();
				return expr;
			}

			case TOK_LITERAL:
				// prod #16 (handled by lexer)
				expr = new LiteralExpr(currentTokenValue);
			break;

			case TOK_NUMBER:
				// prod #30 (handled by lexer)
				expr = new NumberConstantExpr(Converter.toNumber(currentTokenValue));
			break;

			case TOK_FUNCTION_LPAR:
				// production #16 FunctionCall
			{
				// try a lookup to find if we have somebody who can make
				// a CallExpression
				Function function = functionTable.get(currentTokenValue);

				if (function == null)
				{
					// "current()" is special, because we'll want
					// to take note of the fact that this expr
					// uses it
					if (!currentTokenValue.equals("current"))
					{
						throw new ExprParseException("no such function: " + currentTokenValue);
					}
					else
					{
						throw new ExprParseException("no such function: " + currentTokenValue);
						// usesCurrentFunction = true;
						// function = currentFunction;
					}
				}
				next();

				return function.makeCallExpr(parseArgs(), statEnv);

			}

				// an extension function
			case TOK_CNAME_LPAR:
				// also prod #16 FunctionCall
			{
				QName name = expandName();
				next();
				// if (XT_NAMESPACE.equals(name.getNamespace()))
				// {
				//
				// // xt: extension functions are constructed and
				// // called just like builtin functions, rather
				// // than the more loosely coupled extension mechanism
				//
				// Function function = (Function)extensionFunctionTable.get(name.getLocalPart());
				// if (function != null)
				// {
				// return function.makeCallExpr(parseArgs(), node);
				// }
				// }
				ConvertibleExpr[] args = parseArgs();
				VariantExpr[] variantArgs = (VariantExpr[])Array.newInstance(VariantExpr.class, args.length);
				for (int i = 0; i < args.length; i++)
				{
					variantArgs[i] = args[i].makeVariantExpr(statEnv);
				}
				return new ExtensionFunctionCallExpr(name, variantArgs);
			}
			default:
			{
				// TODO: This CFG is not yielding helpful messages.
				throw new ExprParseException("Unexpected \"".concat(ExprTokenizer.toString(currentToken)).concat("\""));
			}
		}
		next();
		return expr;
	}

	//
	// XPath Production #24
	//
	// A RelationalExpr is an AdditiveExpr, possibly
	// followed by a comparison operator and another
	// RelationalExpr
	//
	private ConvertibleExpr parseRelationalExpr() throws ExprParseException
	{
		ConvertibleExpr expr = parseAdditiveExpr();
		loop: for (;;)
		{
			switch (currentToken)
			{
				case TOK_GT:
				{
					next();
					expr = makeRelationalExpr(relationTable.get(">"), expr, parseAdditiveExpr());
				}
				break;
				case TOK_GTE:
				{
					next();
					expr = makeRelationalExpr(relationTable.get(">="), expr, parseAdditiveExpr());
				}
				break;
				case TOK_LT:
				{
					next();
					expr = makeRelationalExpr(relationTable.get(">"), parseAdditiveExpr(), expr);
				}
				break;
				case TOK_LTE:
				{
					next();
					expr = makeRelationalExpr(relationTable.get(">="), parseAdditiveExpr(), expr);
				}
				break;
				default:
				break loop;
			}
		}
		return expr;
	}

	//
	// RelativeLocationPath -- XPath production #3
	//
	private ConvertibleNodeSetExpr parseRelativeLocationPath() throws ExprParseException
	{
		ConvertibleNodeSetExprImpl step = parseStep();
		if (currentToken == TOK_SLASH)
		{
			next();
			return step.compose(parseRelativeLocationPath());
		}
		if (currentToken == TOK_SLASH_SLASH)
		{
			next();
			return step.compose(axisTable.get("descendant-or-self").compose(parseRelativeLocationPath()));
		}
		return step;
	}

	//
	// XPath production #4
	//
	private ConvertibleNodeSetExprImpl parseStep() throws ExprParseException
	{
		switch (currentToken)
		{
			case TOK_AXIS:
			{
				AxisExpr axis = axisTable.get(currentTokenValue);
				if (axis == null)
				{
					throw new ExprParseException("no such axis");
				}
				next();
				return parsePredicates(axis, parseNodeTest(axis.getPrincipalNodeKind()));
			}
			case TOK_DOT:
			{
				next();
				return axisTable.get("self");
			}
			case TOK_DOT_DOT:
			{
				next();
				return axisTable.get("parent");
			}
			case TOK_AT:
			{
				next();
				return parsePredicates(axisTable.get("attribute"), parseNodeTest(NodeKind.ATTRIBUTE));
			}
			default:
			{
				return parsePredicates(axisTable.get("child"), parseNodeTest(NodeKind.ELEMENT));
			}
		}
	}

	//
	// XPath production #27
	//
	// we've recognized something which may be a
	// unary operator (-) followed by an expression
	// or a union expression (or group)
	// or a path expression
	//
	private ConvertibleExpr parseUnaryExpr() throws ExprParseException
	{
		if (currentToken == TOK_MINUS)
		{
			next();
			return new NegateExpr(parseUnaryExpr().makeNumberExpr(statEnv));
		}
		return parseUnionExpr();
	}

	//
	// XPath production #18
	//
	// any expression which may contain alternative
	// path expressions (separated by the or operator "|")
	//
	private ConvertibleExpr parseUnionExpr() throws ExprParseException
	{
		ConvertibleExpr expr = parsePathExpr();
		while (currentToken == TOK_VBAR)
		{
			next();
			expr = new UnionExpr(expr.makeNodeSetExpr(statEnv), parsePathExpr().makeNodeSetExpr(statEnv));
		}
		return expr;
	}

	//
	//
	private boolean tokenStartsNodeTest()
	{
		switch (currentToken)
		{
			case TOK_QNAME:
			case TOK_STAR:
			case TOK_NAME_COLON_STAR:
			case TOK_PROCESSING_INSTRUCTION_LPAR:
			case TOK_COMMENT_LPAR:
			case TOK_TEXT_LPAR:
			case TOK_NODE_LPAR:
				return true;
		}
		return false;
	}

	//
	//
	private boolean tokenStartsStep()
	{
		switch (currentToken)
		{
			case TOK_AXIS:
			case TOK_DOT:
			case TOK_DOT_DOT:
			case TOK_AT:
				return true;
		}
		return tokenStartsNodeTest();
	}
}
