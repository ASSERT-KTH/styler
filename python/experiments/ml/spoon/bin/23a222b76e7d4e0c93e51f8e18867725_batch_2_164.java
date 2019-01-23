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
package spoon.support.reflect.code;

import spoon.reflect.annotations
. MetamodelPropertyField;importspoon.reflect.code
. CtCodeElement;importspoon.reflect.code
. CtExpression;importspoon.reflect.code
. CtIf;importspoon.reflect.code
. CtStatement;importspoon.reflect.declaration
. CtType;importspoon.reflect.visitor

. CtVisitor ;importstaticspoon.reflect.path.CtRole
. CONDITION ;importstaticspoon.reflect.path.CtRole
. ELSE ;importstaticspoon.reflect.path.CtRole

. THEN ; public class CtIfImpl extends CtStatementImpl
	implements CtIf { private static final longserialVersionUID

	=1L;@ MetamodelPropertyField (role
	=CONDITION)CtExpression <Boolean

	>condition;@ MetamodelPropertyField (role
	= ELSE)

	CtStatementelseStatement;@ MetamodelPropertyField (role
	= THEN)

	CtStatementthenStatement
	; @ Overridepublicvoid accept( CtVisitor
		visitor){visitor.visitCtIf(
	this

	);
	} @OverridepublicCtExpression <Boolean> getCondition
		( ){
	return

	condition;}@SuppressWarnings
	("unchecked"
	) @Override public <S extends CtStatement>S getElseStatement
		( ){return (S
	)

	elseStatement;}@SuppressWarnings
	("unchecked"
	) @Override public <S extends CtStatement>S getThenStatement
		( ){return (S
	)

	thenStatement;
	} @Override public <T extends CtIf>TsetCondition(CtExpression <Boolean >
		condition ){ if (condition !=
			null){condition.setParent(
		this
		);}getFactory().getEnvironment().getModelChangeListener().onObjectUpdate (this ,CONDITION ,condition,this.
		condition); this .condition
		= condition;return (T
	)

	this;
	} @Override public <T extends CtIf>T setElseStatement( CtStatement
		elseStatement ){ if (elseStatement !=
			null){elseStatement.setParent(
		this
		);}getFactory().getEnvironment().getModelChangeListener().onObjectUpdate (this ,ELSE ,elseStatement,this.
		elseStatement); this .elseStatement
		= elseStatement;return (T
	)

	this;
	} @Override public <T extends CtIf>T setThenStatement( CtStatement
		thenStatement
		) {// then branch might be null: `if (condition) ;` if (thenStatement !=
			null){thenStatement.setParent(
		this
		);}getFactory().getEnvironment().getModelChangeListener().onObjectUpdate (this ,THEN ,thenStatement,this.
		thenStatement); this .thenStatement
		= thenStatement;return (T
	)

	this;
	} @ OverridepublicCtIf clone
		( ){return (CtIf)super.clone
	(

	);
	} @ OverridepublicVoid S
		( ){
	return

	null ; }publicCtCodeElementgetSubstitution(CtType <? >
		targetType ){returnclone
	(
)
