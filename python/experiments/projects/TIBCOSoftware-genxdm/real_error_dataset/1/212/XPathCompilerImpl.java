/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
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

import java.util.HashMap;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.xpath.v10.relations.Relation;
import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.ExprParseException;
import org.genxdm.xpath.v10.NodeSetExpr;
import org.genxdm.xpath.v10.NumberExpr;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.VariantExpr;
import org.genxdm.xpath.v10.XPathCompiler;
import org.genxdm.xpath.v10.extend.Function;

public final class XPathCompilerImpl
    implements XPathCompiler
{
	private final HashMap<String, AxisExpr> axisTable;
	private final HashMap<String, Function> functionTable;
	private final HashMap<String, Relation> relationTable;

	public XPathCompilerImpl(final HashMap<String, AxisExpr> axisTable, final HashMap<String, Function> functionTable, final HashMap<String, Relation> relationTable)
	{
		this.axisTable = axisTable;
		this.functionTable = functionTable;
		this.relationTable = relationTable;
	}

	public VariantExpr compile(final String expression, final ExprContextStatic statEnv) throws ExprParseException
	{
		PreCondition.assertArgumentNotNull(expression, "expression");
		PreCondition.assertArgumentNotNull(statEnv, "statEnv");

		return new ExprParser(expression, statEnv, axisTable, functionTable, relationTable).parseExpr().makeVariantExpr(statEnv);
	}

	public BooleanExpr compileBooleanExpr(final String expression, final ExprContextStatic statEnv) throws ExprParseException
	{
		PreCondition.assertArgumentNotNull(expression, "expression");
		PreCondition.assertArgumentNotNull(statEnv, "statEnv");

		return new ExprParser(expression, statEnv, axisTable, functionTable, relationTable).parseExpr().makeBooleanExpr(statEnv);
	}

	public NodeSetExpr compileNodeSetExpr(final String expression, final ExprContextStatic statEnv) throws ExprParseException
	{
		PreCondition.assertArgumentNotNull(expression, "expression");
		PreCondition.assertArgumentNotNull(statEnv, "statEnv");

		return new ExprParser(expression, statEnv, axisTable, functionTable, relationTable).parseExpr().makeNodeSetExpr(statEnv);
	}

	public NumberExpr compileNumberExpr(final String expression, final ExprContextStatic statEnv) throws ExprParseException
	{
		PreCondition.assertArgumentNotNull(expression, "expression");
		PreCondition.assertArgumentNotNull(statEnv, "statEnv");

		return new ExprParser(expression, statEnv, axisTable, functionTable, relationTable).parseExpr().makeNumberExpr(statEnv);
	}

	public StringExpr compileStringExpr(final String expression, final ExprContextStatic statEnv) throws ExprParseException
	{
		PreCondition.assertArgumentNotNull(expression, "expression");
		PreCondition.assertArgumentNotNull(statEnv, "statEnv");

		return new ExprParser(expression, statEnv, axisTable, functionTable, relationTable).parseExpr().makeStringExpr(statEnv);
	}
}
