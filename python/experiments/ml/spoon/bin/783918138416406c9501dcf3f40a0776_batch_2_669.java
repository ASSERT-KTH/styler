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

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.STATEMENT;

public class CtStatementListImpl<R> extends CtCodeElementImpl implements CtStatementList {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = STATEMENT)
	List<CtStatement> statements = emptyList();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtStatementList(this);
	}

	@Override
	public List< CtStatement
		> getStatements(
	)

	{return
	statements ;} @ Overridepublic < TextendsCtStatementList>TsetStatements (List <
		CtStatement >stmts ) { if (stmts==null||stmts .
			isEmpty() ) {this.statements=CtElementImpl
			. emptyList() ;return
		(
		T)this;}getFactory().getEnvironment().getModelChangeListener() .onListDeleteAll (this,STATEMENT , this.statements,newArrayList<>(this
		.statements));this.statements
		. clear( ) ; for( CtStatement
			stmt:stmts){
		addStatement
		( stmt); }return
	(

	T)
	this ;} @ Overridepublic < TextendsCtStatementList >T addStatement
		( CtStatementstatement){returnthis.addStatement(this.statements .size(
	)

	,statement
	) ;} @ Overridepublic < TextendsCtStatementList >T addStatement (int index
		, CtStatementstatement ) {if (
			statement ==null) {return
		(
		T )this;} if (this.statements==CtElementImpl.<CtStatement >
			emptyList() ) { this.statements=newArrayList<
		>
		(BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY);}statement.
		setParent(this);getFactory().getEnvironment().getModelChangeListener() .onListAdd (this,STATEMENT ,this .statements,
		index,statement);this.statements .add(
		index ,statement) ;return
	(

	T ) this;} private
		void ensureModifiableStatementsList(){ if (this.statements==CtElementImpl.<CtStatement >
			emptyList() ) { this.statements=newArrayList<
		>
	(

	BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY)
	; }} @ Overridepublic < TextendsCtStatementList >T insertBegin
		(CtStatementListstatements)
		{ ensureModifiableStatementsList( ) ; for(CtStatementstatement:statements .
			getStatements()){statement.
			setParent(this);this .addStatement(
		0
		, statement);} if (isImplicit()&&this. statements .size (
			)>1){
		setImplicit
		( false); }return
	(

	T)
	this ;} @ Overridepublic < TextendsCtStatementList >T insertBegin
		(CtStatementstatement)
		{ensureModifiableStatementsList();statement.
		setParent(this);this .addStatement(

		0 ,statement); if (isImplicit()&&this. statements .size (
			)>1){
		setImplicit
		( false); }return
	(

	T)
	this ;} @ Overridepublic < TextendsCtStatementList >T insertEnd
		(CtStatementstatement)
		{ensureModifiableStatementsList();
		addStatement (statement) ;return
	(

	T)
	this ;} @ Overridepublic < TextendsCtStatementList >T insertEnd
		(CtStatementListstatements) { List < CtStatement>tobeInserted=newArrayList<>(statements.
		getStatements
		(
		));//remove statements from the `statementsToBeInserted` before they are added to spoon model//note: one element MUST NOT be part of two models.statements.
		setStatements (null ) ; for( CtStatement
			s:tobeInserted){
		insertEnd
		( s); }return
	(

	T)
	this ;} @ Overridepublic < TextendsCtStatementList>T insertAfter (Filter <? extends CtStatement> insertionPoints
		, CtStatementstatement ) { for(CtStatemente:Query .getElements( this
			,insertionPoints)){e.
		insertAfter
		( statement); }return
	(

	T)
	this ;} @ Overridepublic < TextendsCtStatementList>T insertAfter (Filter <? extends CtStatement> insertionPoints
		, CtStatementListstatements ) { for(CtStatemente:Query .getElements( this
			,insertionPoints)){e.
		insertAfter
		( statements); }return
	(

	T)
	this ;} @ Overridepublic < TextendsCtStatementList>T insertBefore (Filter <? extends CtStatement> insertionPoints
		, CtStatementstatement ) { for(CtStatemente:Query .getElements( this
			,insertionPoints)){e.
		insertBefore
		( statement); }return
	(

	T)
	this ;} @ Overridepublic < TextendsCtStatementList>T insertBefore (Filter <? extends CtStatement> insertionPoints
		, CtStatementListstatements ) { for(CtStatemente:Query .getElements( this
			,insertionPoints)){e.
		insertBefore
		( statements); }return
	(

	T)
	this ;} @ Overridepublic < TextendsCtStatement >T getStatement
		( inti) {return(T)statements.
	get

	(i
	) ;} @ Overridepublic < TextendsCtStatement >
		T getLastStatement() {return(T)statements.get( statements .size(
	)

	-1
	) ; }@Override publicvoid removeStatement
		( CtStatementstatement){ if (this.statements==CtElementImpl.<CtStatement >
			emptyList(
		)
		){return;}getFactory().getEnvironment().getModelChangeListener() .onListDelete (this ,STATEMENT,statements,statements. indexOf(statement
		),statement);statements.
	remove

	(statement
	) ;} @ Overridepublic < EextendsCtElement >E setPosition
		( SourcePositionposition ) { for( CtStatement
			s:statements){s.
		setPosition
		( position); }return
	(

	E)
	this ;}@Override publicIterator< CtStatement
		> iterator(){returnstatements
	.

	iterator(
	) ; }@Override public
		CtStatementList clone() {return(CtStatementList)super
	.

	clone ( );}publicCtStatementListgetSubstitution (CtType <
		? >targetType){
	return
clone
