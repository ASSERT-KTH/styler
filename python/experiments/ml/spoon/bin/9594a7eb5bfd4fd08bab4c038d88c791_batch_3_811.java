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
package spoon.reflect.visitor.filter;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
importjava.util.Set;import
spoon .SpoonException;importspoon.reflect.
code .CtLambda;importspoon.reflect.
declaration .CtExecutable;importspoon.reflect.
declaration .CtMethod;importspoon.reflect.
declaration .CtType;importspoon.reflect.visitor.
Filter ;importspoon.reflect.visitor.chain.
CtConsumableFunction ;importspoon.reflect.visitor.chain.
CtConsumer ;importspoon.reflect.visitor.chain.
CtQuery ;importspoon.reflect.visitor.
chain .CtQueryAware;importspoon.support.

visitor
. ClassTypingContext ; import spoon.support.visitor.SubInheritanceHierarchyResolver; /**
 * Returns all methods/lambdas with same signature in related inheritance hierarchies.
 * It can be be used to found all other methods, which has to be changed if signature of method or lambda expression has to be changed.<br>
 *
 * Expects {@link CtExecutable} as input
 * and produces all {@link CtExecutable}s,
 * which have same signature and are declared in sub/super classes or sub/super interfaces of this or related inheritance hierarchy.<br>
 *
 * It makes sense to call this mapping functions for {@link CtMethod} and {@link CtLambda} instances
 * and then it returns {@link CtMethod} and {@link CtLambda} instance which overrides each other or have same signature.
 */ public

	class AllMethodsSameSignatureFunction implements CtConsumableFunction <CtExecutable
	< ? > > ,CtQueryAware
	{ private booleanincludingSelf

	= false;private boolean
	includingLambdas

	=
	true ; privateCtQueryquery ;public AllMethodsSameSignatureFunction
		(){ } /**
	 * @param includingSelf if true then input element is sent to output too. By default it is false.
	 */public
		AllMethodsSameSignatureFunction includingSelf(
	boolean

	includingSelf
	) { this.includingSelf =includingSelf ;
		returnthis; } /**
	 * @param includingLambdas if true then extra search for {@link CtLambda} executables,
	 * with same signature will be processed too.
	 * If false, then it returns only {@link CtMethod} instances.
	 * By default it is true.
	 */public
		AllMethodsSameSignatureFunction includingLambdas(
	boolean

	includingLambdas)
	{ this .includingLambdas= includingLambdas;returnthis ;} @ Overridepublicvoidapply (final CtExecutable
		<
		? > targetExecutable , final CtConsumer<Object>
		outputConsumer ) { //prepare filter for lambda expression. It will be configured by the algorithm below finalLambdaFilterlambdaFilter=newLambdaFilter();finalCtQuerylambdaQuery=targetExecutable.
		getFactory
		().getModel ()
		. filterChildren( lambdaFilter ); //the to be searched method
			CtMethod
			< ?> targetMethod ;if (
				targetExecutableinstanceofCtLambda){//the input is lambdaif
				( includingSelf&&includingLambdas){outputConsumer. accept
					(targetExecutable
				)
			;
			if
			( query .isTerminated()){return ;}}//in case of lambda, the target method is the method implemented by lambdatargetMethod=(
			(CtLambda<?>)targetExecutable
			) .getOverriddenMethod();outputConsumer. accept
				(targetMethod
			)
			;
			if
			(query.isTerminated( )){return;}//the input is the lambda expression, which was already returned or doesn't have to be returned at all because includingSelf == false//add extra filter into lambdaQuery which skips that input lambda expressionlambdaQuery .
				select(
				new Filter <CtLambda<?>> () {
					@ Override public booleanmatches
				(
			CtLambda<?
		> lambda ) {return targetExecutable !=lambda ;
			} }); }
				elseif(targetExecutableinstanceofCtMethod)
				{ if(includingSelf){outputConsumer. accept
					(targetExecutable
				)
			;
			if ( query.isTerminated()) {return
		; } }
			targetMethod
			= (CtMethod< ?
				>)targetExecutable;}else{
			//CtConstructor or CtAnonymousExecutable never overrides other executable. We are done
			if(
		includingSelf

		) {outputConsumer.accept(targetExecutable) ; } return ;}finalList<CtMethod
		<?>>targetMethods=new
		ArrayList<>( ) ; targetMethods.add(targetMethod)
		;CtType<?>declaringType=
		targetMethod
		. getDeclaringType (
			) ;lambdaFilter
		.
		addImplementingInterface ( declaringType ) ; //search for all declarations and implementations of this method in sub and super classes and interfaces of all related hierarchies.classContext{
		boolean
		haveToSearchForSubtypes;} final Contextcontext
		=
		new Context ( ) ; //at the beginning we know that we have to always search for sub types too.context.haveToSearchForSubtypes=true;//Sub inheritance hierarchy function, which remembers visited sub types and does not returns/visits them againfinalSubInheritanceHierarchyResolversubHierarchyFnc=newSubInheritanceHierarchyResolver(declaringType.
		getFactory
		().getModel().
		getRootPackage
		()); //add hierarchy of `targetMethod` as to be checked for sub types of declaring type subHierarchyFnc . addSuperType(declaringType);//unique names of all types whose super inheritance hierarchy was searched for rootType
		Set
		< String>typesCheckedForRootType=newHashSet< > ( ) ;//list of sub types whose inheritance hierarchy has to be checkedfinalList<CtType
		<
		?>>toBeCheckedSubTypes=newArrayList
		< >();//add hierarchy of `targetMethod` as to be checked for super types of declaring typetoBeCheckedSubTypes.add (
			declaringType );while(! toBeCheckedSubTypes . isEmpty( )
				) { for ( CtType<?>subType
				:
				toBeCheckedSubTypes ) {ClassTypingContextctc= newClassTypingContext(
				subType
				);//search for first target method from the same type inheritance hierarchytargetMethod =getTargetMethodOfHierarchy (targetMethods , ctc);//search for all methods with same signature in inheritance hierarchy of `subType`forEachOverridenMethod(ctc,targetMethod ,
					typesCheckedForRootType,
					new CtConsumer <CtMethod<?>> () {
						@Overridepublicvoidaccept(CtMethod
						<?>overriddenMethod){targetMethods
						.add(overriddenMethod ) ; outputConsumer.accept(overriddenMethod)
						;CtType<?>type=
						overriddenMethod.getDeclaringType();lambdaFilter
						.
						addImplementingInterface(type ) ;subHierarchyFnc
					.
				addSuperType(type
				) ;//mark that new super type was added, so we have to search for sub types againcontext.haveToSearchForSubtypes=true ;
					}}
				)
			;
			if(query.isTerminated(
			) ){return;} }
				toBeCheckedSubTypes.clear ( );
				if
				(
				context.haveToSearchForSubtypes){ context.haveToSearchForSubtypes=false;//there are some new super types, whose sub inheritance hierarchy has to be checked//search their inheritance hierarchy for sub typessubHierarchyFnc .
					forEachSubTypeInPackage(
					new CtConsumer <CtType<?>> () {
						@Overridepublicvoidaccept(CtType
					<
				?>type
			)
		{
		toBeCheckedSubTypes .add( type
			)
			;}});}}
		if
	(

	includingLambdas
	) { //search for all lambdas implementing any of the found interfaceslambdaQuery. forEach (outputConsumer ) ;}}/**
	 * calls outputConsumer for each method which is overridden by 'thisMethod' in scope of `ctc`.
	 * There is assured that each method is returned only once.
	 *
	 * @param ctc - class typing context whose scope is searched for overridden methods
	 * @param thisMethod - the
	 * @param distintTypesSet set of qualified names of types which were already visited
	 * @param outputConsumer result handling consumer
	 */ privatevoid forEachOverridenMethod(finalClassTypingContext ctc, final CtMethod<?>thisMethod,Set <String >
		distintTypesSet , final CtConsumer <CtMethod<?>
			>outputConsumer){ finalCtQueryq=ctc.getAdaptationScope().map(new
		AllTypeMembersFunction(CtMethod.class ).distinctSet(distintTypesSet));q .
			forEach(
			new CtConsumer <CtMethod<?>> () {
				@ Overridepublic void accept( CtMethod
					<
					?>
				thatMethod
				)
				{
				if (thisMethod==thatMethod){//do not return scope method return;} //check whether method is overridden by searched method
					/*
				 * note: we are in super inheritance hierarchy of type declaring input `method`, so we do not have to check isSubTypeOf.
				 * Check for isSubSignature is enough
				 */if(ctc.isSubSignature(
					thisMethod ,thatMethod)){outputConsumer. accept
						(thatMethod);if(
					query
				.
			isTerminated
		())
	{

	q .terminate() ;}}}});}private CtMethod< ? >getTargetMethodOfHierarchy (
		List <CtMethod<?> > targetMethods ,ClassTypingContext ctc
			){for( CtMethod < ?>method:targetMethods)
			{ CtType<?>declaringType=method.getDeclaringType(); if
				( ctc.
			isSubtypeOf
		(
		declaringType
		. getReference ())){
	return

	method;
	} } //this should never happenthrownew SpoonException( "No target executable was found in super type hiearchy of class typing context"
		);} @ Overridepublic
	void
setQuery
