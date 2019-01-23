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
package spoon.reflect.factory;

import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.CtModelImpl;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.BodyHolderSourcePosition;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;importspoon
. reflect.declaration.CtTypeParameter;importspoon
. reflect.declaration.CtUsedService;importspoon
. reflect.declaration.CtVariable;importspoon
. reflect.declaration.ModifierKind;importspoon
. reflect.eval.PartialEvaluator;importspoon
. reflect.reference.CtArrayTypeReference;importspoon
. reflect.reference.CtCatchVariableReference;importspoon
. reflect.reference.CtExecutableReference;importspoon
. reflect.reference.CtFieldReference;importspoon
. reflect.declaration.CtImport;importspoon
. reflect.reference.CtIntersectionTypeReference;importspoon
. reflect.reference.CtLocalVariableReference;importspoon
. reflect.reference.CtModuleReference;importspoon
. reflect.reference.CtPackageReference;importspoon
. reflect.reference.CtParameterReference;importspoon
. reflect.reference.CtReference;importspoon
. reflect.reference.CtTypeParameterReference;importspoon
. reflect.reference.CtTypeReference;importspoon
. reflect.reference.CtUnboundVariableReference;importspoon
. reflect.reference.CtVariableReference;importspoon
. reflect.reference.CtWildcardReference;importspoon
. reflect.reference.CtTypeMemberWildcardImportReference;importspoon
. reflect.visitor.chain.CtQuery;importspoon
. support.DefaultCoreFactory;importspoon
. support.StandardEnvironment;importspoon
. support.visitor.GenericTypeAdapter;importjava

. io.IOException;importjava
. io.Serializable;importjava
. lang.annotation.Annotation;importjava
. util.HashMap;importjava
. util.List;importjava
. util.Map;importjava
. util.Random;importjava
. util.Set;importjava
. util.concurrent.ThreadLocalRandom;/**
 * Implements {@link Factory}
 */public

class
FactoryImpl implements Factory , Serializable{ private static

	final long serialVersionUID = 1L ; privatetransient

	Factory parentFactory ; /**
	 * Returns the parent of this factory. When an element is not found in a
	 * factory, it can be looked up in its parent factory using a delegation
	 * model.
	 */public

	Factory
	getParentFactory ( ){return parentFactory
		; }private
	transient

	AnnotationFactory annotation ; /**
	 * The {@link CtAnnotationType} sub-factory.
	 */@

	Override
	publicAnnotationFactory
	Annotation ( ){if (
		annotation ==null ) {annotation =
			new AnnotationFactory ( this);}return
		annotation
		; }private
	transient

	ClassFactory clazz ; /**
	 * The {@link CtClass} sub-factory.
	 */@

	Override
	publicClassFactory
	Class ( ){if (
		clazz ==null ) {clazz =
			new ClassFactory ( this);}return
		clazz
		; }private
	transient

	CodeFactory code ; /**
	 * The {@link spoon.reflect.code.CtCodeElement} sub-factory.
	 */@

	Override
	publicCodeFactory
	Code ( ){if (
		code ==null ) {code =
			new CodeFactory ( this);}return
		code
		; }private
	transient

	ConstructorFactory constructor ; /**
	 * The {@link CtConstructor} sub-factory.
	 */@

	Override
	publicConstructorFactory
	Constructor ( ){if (
		constructor ==null ) {constructor =
			new ConstructorFactory ( this);}return
		constructor
		; }private
	transient

	CoreFactory core ; /**
	 * The core factory.
	 */@

	Override
	publicCoreFactory
	Core ( ){if (
		core ==null ) {//During deserialization, the transient field core, is null core
			=
			new DefaultCoreFactory ( );core.
			setMainFactory(this);}return
		core
		; }private
	transient

	EnumFactory enumF ; /**
	 * The {@link CtEnum} sub-factory.
	 */@

	Override
	publicEnumFactory
	Enum ( ){if (
		enumF ==null ) {enumF =
			new EnumFactory ( this);}return
		enumF
		; }private
	transient

	Environment environment ; /**
	 * Gets the Spoon environment that encloses this factory.
	 */@

	Override
	publicEnvironment
	getEnvironment ( ){if (
		environment ==null ) {environment =
			new StandardEnvironment ( );}return
		environment
		; }private
	transient

	ExecutableFactory executable ; /**
	 * The {@link CtExecutable} sub-factory.
	 */@

	Override
	publicExecutableFactory
	Executable ( ){if (
		executable ==null ) {executable =
			new ExecutableFactory ( this);}return
		executable
		; }private
	transient

	EvalFactory eval ; /**
	 * The evaluators sub-factory.
	 */@

	Override
	publicEvalFactory
	Eval ( ){if (
		eval ==null ) {eval =
			new EvalFactory ( this);}return
		eval
		; }private
	transient

	FieldFactory field ; /**
	 * The {@link CtField} sub-factory.
	 */@

	Override
	publicFieldFactory
	Field ( ){if (
		field ==null ) {field =
			new FieldFactory ( this);}return
		field
		; }/**
	 * The {@link CtInterface} sub-factory.
	 */
	private

	transient
	InterfaceFactory interfaceF ; /**
	 * The {@link CtInterface} sub-factory.
	 */@

	Override
	publicInterfaceFactory
	Interface ( ){if (
		interfaceF ==null ) {interfaceF =
			new InterfaceFactory ( this);}return
		interfaceF
		; }private
	transient

	MethodFactory methodF ; /**
	 * The {@link CtMethod} sub-factory.
	 */@

	Override
	publicMethodFactory
	Method ( ){if (
		methodF ==null ) {methodF =
			new MethodFactory ( this);}return
		methodF
		; }private
	transient

	PackageFactory packageF ; /**
	 * The {@link CtPackage} sub-factory.
	 */@

	Override
	publicPackageFactory
	Package ( ){if (
		packageF ==null ) {packageF =
			new PackageFactory ( this);}return
		packageF
		; }private
	transient

	CompilationUnitFactory compilationUnit ; /**
	 * The {@link CompilationUnit} sub-factory.
	 */@

	Override
	publicCompilationUnitFactory
	CompilationUnit ( ){if (
		compilationUnit ==null ) {compilationUnit =
			new CompilationUnitFactory ( this);}return
		compilationUnit
		; }private
	transient

	TypeFactory type ; /**
	 * The {@link CtType} sub-factory.
	 */@

	Override
	publicTypeFactory
	Type ( ){if (
		type ==null ) {type =
			new TypeFactory ( this);}return
		type
		; }private
	transient

	QueryFactory query ; /**
	 * The query sub-factory.
	 */@

	Override
	publicQueryFactory
	Query ( ){if (
		query ==null ) {query =
			new QueryFactory ( this);}return
		query
		; }private
	transient

	ModuleFactory module ; /**
	 * The module sub-factory
	 */@

	Override
	publicModuleFactory
	Module ( ){if (
		module ==null ) {module =
			new ModuleFactory ( this);}return
		module
		; }/**
	 * A constructor that takes the parent factory
	 */
	public


	FactoryImpl
	( CoreFactorycoreFactory, Environmentenvironment , FactoryparentFactory ) {this .
		environment=environment ; this.
		core=coreFactory ; this.
		core.setMainFactory(this);this.
		parentFactory=parentFactory ; }/**
	 * Should not be called directly. Use {@link spoon.Launcher#createFactory()} instead.
	 */
	public

	FactoryImpl
	( CoreFactorycoreFactory, Environmentenvironment ) {this (
		coreFactory,environment, null) ;}// Deduplication
	// See http://shipilev.net/talks/joker-Oct2014-string-catechism.pdf

	private
	static

	class Dedup { Map <
		String,String> cache= new HashMap < >();Randomrandom
		= ThreadLocalRandom . current();}/**
	 * Note this is an instance field. To avoid memory leaks and dedup being
	 * targeted to each Spoon Launching, that could differ a lot by
	 * frequently used symbols.
	 */
	private

	transient
	ThreadLocal < Dedup>threadLocalDedup= new ThreadLocal < Dedup>(){@ Override
		protectedDedup
		initialValue ( ){return new
			Dedup ( );}}
		;
	/**
	 * Returns a String equal to the given symbol. Performs probablilistic
	 * deduplication.
	 */public

	String
	dedup ( Stringsymbol) {Dedup dedup
		= threadLocalDedup . get();Map<
		String,String> cache= dedup . cache;Stringcached
		; if(
		( cached=cache . get(symbol))!=null ) {return cached
			; }else
		{ // Puts the symbol into cache with 20% probability int
			prob
			= ( int )(Integer .MIN_VALUE+( 0.2 *( 1L <<32 ) ));if(
			dedup .random.nextInt()<prob ) {cache .
				put(symbol,symbol) ;}return
			symbol
			; }}
		/**
	 * Needed to restore state of transient fields during reading from stream
	 */
	private

	void
	readObject ( java.io.ObjectInputStreamin) throwsIOException , ClassNotFoundException{ threadLocalDedup =
		new ThreadLocal < Dedup>(){@ Override
			protectedDedup
			initialValue ( ){return new
				Dedup ( );}}
			;
		in.
		defaultReadObject();}private
	final

	CtModel model = new CtModelImpl ( this);@Override

	publicCtModel
	getModel ( ){return model
		; }@
	Override

	public<
	A extendsAnnotation > CtAnnotation< A>createAnnotation( CtTypeReference<A>annotationType) {return Code
		( ).createAnnotation(annotationType);}@
	Override

	public<
	A ,Textends A > CtAssignment< A,T> createVariableAssignment( CtVariableReference<A>variable, booleanisStatic , CtExpression< T>expression) {return Code
		( ).createVariableAssignment(variable,isStatic, expression) ;}@
	Override

	public<
	R >CtStatementListcreateStatementList ( CtBlock<R>block) {return Code
		( ).createStatementList(block);}@
	Override

	public<
	T extendsCtStatement > CtBlock< ?>createCtBlock( Telement) {return Code
		( ).createCtBlock(element);}@
	Override

	public<
	T >CtBinaryOperator< T>createBinaryOperator( CtExpression<?>left, CtExpression< ?>right, BinaryOperatorKindkind ) {return Code
		( ).createBinaryOperator(left,right, kind) ;}@
	Override

	public<
	T >CtCatchVariable< T>createCatchVariable( CtTypeReference<T>type, Stringname , ModifierKind... modifierKinds) {return Code
		( ).createCatchVariable(type,name, modifierKinds) ;}@
	Override

	public<
	T >CtCodeSnippetExpression< T>createCodeSnippetExpression( Stringexpression) {return Code
		( ).createCodeSnippetExpression(expression);}@
	Override

	public<
	T >CtConstructorCall< T>createConstructorCall( CtTypeReference<T>type, CtExpression< ?>...parameters) {return Code
		( ).createConstructorCall(type,parameters) ;}@
	Override

	public<
	T >CtFieldAccess< Class<T>>createClassAccess( CtTypeReference<T>type) {return Code
		( ).createClassAccess(type);}@
	Override

	public<
	T >CtInvocation< T>createInvocation( CtExpression<?>target, CtExecutableReference< T>executable, List< CtExpression<?>>arguments) {return Code
		( ).createInvocation(target,executable, arguments) ;}@
	Override

	public<
	T >CtInvocation< T>createInvocation( CtExpression<?>target, CtExecutableReference< T>executable, CtExpression< ?>...arguments) {return Code
		( ).createInvocation(target,executable, arguments) ;}@
	Override

	public<
	T >CtLiteral< T>createLiteral( Tvalue) {return Code
		( ).createLiteral(value);}@
	Override

	public<
	T >CtLocalVariable< T>createLocalVariable( CtTypeReference<T>type, Stringname , CtExpression< T>defaultExpression) {return Code
		( ).createLocalVariable(type,name, defaultExpression) ;}@
	SuppressWarnings

	(value="unchecked" ) @Override
	public<
	T >CtNewArray< T[]>createLiteralArray( T[]value) {return Code
		( ).createLiteralArray(value);}@
	Override

	public<
	T >CtNewClass< T>createNewClass( CtTypeReference<T>type, CtClass< ?>anonymousClass, CtExpression< ?>...parameters) {return Code
		( ).createNewClass(type,anonymousClass, parameters) ;}@
	Override

	public<
	T >CtStatementListcreateVariableAssignments ( List<?extendsCtVariable < T>>variables, List< ?extendsCtExpression < T>>expressions) {return Code
		( ).createVariableAssignments(variables,expressions) ;}@
	Override

	public<
	T >CtThisAccess< T>createThisAccess( CtTypeReference<T>type) {return Code
		( ).createThisAccess(type);}@
	Override

	public<
	T >CtThisAccess< T>createThisAccess( CtTypeReference<T>type, booleanisImplicit ) {return Code
		( ).createThisAccess(type,isImplicit) ;}@
	Override

	public<
	T >CtTypeAccess< T>createTypeAccess( CtTypeReference<T>accessedType) {return Code
		( ).createTypeAccess(accessedType);}@
	Override

	public<
	T >CtTypeAccess< T>createTypeAccess( CtTypeReference<T>accessedType, booleanisImplicit ) {return Code
		( ).createTypeAccess(accessedType,isImplicit) ;}@
	Override

	public<
	T >CtTypeAccess< T>createTypeAccessWithoutCloningReference( CtTypeReference<T>accessedType) {return Code
		( ).createTypeAccessWithoutCloningReference(accessedType);}@
	Override

	public<
	T >CtVariableAccess< T>createVariableRead( CtVariableReference<T>variable, booleanisStatic ) {return Code
		( ).createVariableRead(variable,isStatic) ;}@
	Override

	public<
	T >CtField< T>createCtField( Stringname, CtTypeReference< T>type, Stringexp , ModifierKind... visibilities) {return Code
		( ).createCtField(name,type, exp, visibilities) ;}@
	Override

	public<
	T >CtCatchVariableReference< T>createCatchVariableReference( CtCatchVariable<T>catchVariable) {return Code
		( ).createCatchVariableReference(catchVariable);}@
	Override

	public<
	T >CtLocalVariableReference< T>createLocalVariableReference( CtLocalVariable<T>localVariable) {return Code
		( ).createLocalVariableReference(localVariable);}@
	Override

	public<
	T >CtLocalVariableReference< T>createLocalVariableReference( CtTypeReference<T>type, Stringname ) {return Code
		( ).createLocalVariableReference(type,name) ;}@
	Override

	public<
	T >CtTypeReference< T>createCtTypeReference( Class<?>originalClass) {return Code
		( ).createCtTypeReference(originalClass);}@
	Override

	publicList
	< CtExpression<?>>createVariableReads( List<?extendsCtVariable < ?>>variables) {return Code
		( ).createVariableReads(variables);}@
	Override

	publicCtCatch
	createCtCatch ( StringnameCatch, Class< ?extendsThrowable > exception, CtBlock< ?>ctBlock) {return Code
		( ).createCtCatch(nameCatch,exception, ctBlock) ;}@
	Override

	publicCtCodeSnippetStatement
	createCodeSnippetStatement ( Stringstatement) {return Code
		( ).createCodeSnippetStatement(statement);}@
	Override

	publicCtComment
	createComment ( Stringcontent, CtComment. CommentTypetype) {return Code
		( ).createComment(content,type) ;}@
	Override

	publicCtComment
	createInlineComment ( Stringcontent) {return Code
		( ).createInlineComment(content);}@
	Override

	publicCtJavaDocTag
	createJavaDocTag ( Stringcontent, CtJavaDocTag. TagTypetype) {return Code
		( ).createJavaDocTag(content,type) ;}@
	Override

	publicCtThrow
	createCtThrow ( StringthrownExp) {return Code
		( ).createCtThrow(thrownExp);}@
	Override

	publicCtPackageReference
	createCtPackageReference ( PackageoriginalPackage) {return Code
		( ).createCtPackageReference(originalPackage);}@
	Override

	public<
	T >CtConstructor< T>createDefault( CtClass<T>target) {return Constructor
		( ).createDefault(target);}@
	Override

	public<
	A extendsAnnotation > CtAnnotation< A>createAnnotation( ){return Core
		( ).createAnnotation();}@
	Override

	public<
	R >CtBlock< R>createBlock( ){return Core
		( ).createBlock();}@
	Override

	public<
	R >CtReturn< R>createReturn( ){return Core
		( ).createReturn();}@
	Override

	public<
	R >CtStatementListcreateStatementList ( ){return Core
		( ).createStatementList();}@
	Override

	public<
	S >CtCase< S>createCase( ){return Core
		( ).createCase();}@
	Override

	public<
	S >CtSwitch< S>createSwitch( ){return Core
		( ).createSwitch();}@
	Override

	public<
	T extendsEnum < ?>>CtEnum< T>createEnum( ){return Core
		( ).createEnum();}@
	Override

	public<
	T extendsAnnotation > CtAnnotationType< T>createAnnotationType( ){return Core
		( ).createAnnotationType();}@
	Override

	public<
	T ,Aextends T > CtAssignment< T,A> createAssignment( ){return Core
		( ).createAssignment();}@
	Override

	public<
	T ,Aextends T > CtOperatorAssignment< T,A> createOperatorAssignment( ){return Core
		( ).createOperatorAssignment();}@
	Override

	public<
	T ,Eextends CtExpression < ?>>CtExecutableReferenceExpression< T,E> createExecutableReferenceExpression( ){return Core
		( ).createExecutableReferenceExpression();}@
	Override

	public<
	T >CtAnnotationFieldAccess< T>createAnnotationFieldAccess( ){return Core
		( ).createAnnotationFieldAccess();}@
	Override

	public<
	T >CtArrayRead< T>createArrayRead( ){return Core
		( ).createArrayRead();}@
	Override

	public<
	T >CtArrayWrite< T>createArrayWrite( ){return Core
		( ).createArrayWrite();}@
	Override

	public<
	T >CtAssert< T>createAssert( ){return Core
		( ).createAssert();}@
	Override

	public<
	T >CtBinaryOperator< T>createBinaryOperator( ){return Core
		( ).createBinaryOperator();}@
	Override

	public<
	T >CtCatchVariable< T>createCatchVariable( ){return Core
		( ).createCatchVariable();}@
	Override

	public<
	T >CtCodeSnippetExpression< T>createCodeSnippetExpression( ){return Core
		( ).createCodeSnippetExpression();}@
	Override

	public<
	T >CtConditional< T>createConditional( ){return Core
		( ).createConditional();}@
	Override

	public<
	T >CtConstructorCall< T>createConstructorCall( ){return Core
		( ).createConstructorCall();}@
	Override

	public<
	T >CtFieldRead< T>createFieldRead( ){return Core
		( ).createFieldRead();}@
	Override

	public<
	T >CtFieldWrite< T>createFieldWrite( ){return Core
		( ).createFieldWrite();}@
	Override

	public<
	T >CtInvocation< T>createInvocation( ){return Core
		( ).createInvocation();}@
	Override

	public<
	T >CtLambda< T>createLambda( ){return Core
		( ).createLambda();}@
	Override

	public<
	T >CtLiteral< T>createLiteral( ){return Core
		( ).createLiteral();}@
	Override

	public<
	T >CtLocalVariable< T>createLocalVariable( ){return Core
		( ).createLocalVariable();}@
	Override

	public<
	T >CtNewArray< T>createNewArray( ){return Core
		( ).createNewArray();}@
	Override

	public<
	T >CtNewClass< T>createNewClass( ){return Core
		( ).createNewClass();}@
	Override

	public<
	T >CtSuperAccess< T>createSuperAccess( ){return Core
		( ).createSuperAccess();}@
	Override

	public<
	T >CtThisAccess< T>createThisAccess( ){return Core
		( ).createThisAccess();}@
	Override

	public<
	T >CtTypeAccess< T>createTypeAccess( ){return Core
		( ).createTypeAccess();}@
	Override

	public<
	T >CtUnaryOperator< T>createUnaryOperator( ){return Core
		( ).createUnaryOperator();}@
	Override

	public<
	T >CtVariableRead< T>createVariableRead( ){return Core
		( ).createVariableRead();}@
	Override

	public<
	T >CtVariableWrite< T>createVariableWrite( ){return Core
		( ).createVariableWrite();}@
	Override

	public<
	T >CtAnnotationMethod< T>createAnnotationMethod( ){return Core
		( ).createAnnotationMethod();}@
	Override

	public<
	T >CtClass< T>createClass( ){return Core
		( ).createClass();}@
	Override

	public<
	T >CtConstructor< T>createConstructor( ){return Core
		( ).createConstructor();}@
	Override

	public<
	T >CtConstructor< T>createInvisibleArrayConstructor( ){return Core
		( ).createInvisibleArrayConstructor();}@
	Override

	public<
	T >CtEnumValue< T>createEnumValue( ){return Core
		( ).createEnumValue();}@
	Override

	public<
	T >CtField< T>createField( ){return Core
		( ).createField();}@
	Override

	public<
	T >CtInterface< T>createInterface( ){return Core
		( ).createInterface();}@
	Override

	public<
	T >CtMethod< T>createMethod( ){return Core
		( ).createMethod();}@
	Override

	public<
	T >CtParameter< T>createParameter( ){return Core
		( ).createParameter();}@
	Override

	public<
	T >CtArrayTypeReference< T>createArrayTypeReference( ){return Core
		( ).createArrayTypeReference();}@
	Override

	public<
	T >CtCatchVariableReference< T>createCatchVariableReference( ){return Core
		( ).createCatchVariableReference();}@
	Override

	public<
	T >CtExecutableReference< T>createExecutableReference( ){return Core
		( ).createExecutableReference();}@
	Override

	public<
	T >CtFieldReference< T>createFieldReference( ){return Core
		( ).createFieldReference();}@
	Override

	public<
	T >CtIntersectionTypeReference< T>createIntersectionTypeReference( ){return Core
		( ).createIntersectionTypeReference();}@
	Override

	public<
	T >CtLocalVariableReference< T>createLocalVariableReference( ){return Core
		( ).createLocalVariableReference();}@
	Override

	public<
	T >CtParameterReference< T>createParameterReference( ){return Core
		( ).createParameterReference();}@
	Override

	public<
	T >CtTypeReference< T>createTypeReference( ){return Core
		( ).createTypeReference();}@
	Override

	public<
	T >CtUnboundVariableReference< T>createUnboundVariableReference( ){return Core
		( ).createUnboundVariableReference();}@
	Override

	publicCtBreak
	createBreak ( ){return Core
		( ).createBreak();}@
	Override

	publicCtCatch
	createCatch ( ){return Core
		( ).createCatch();}@
	Override

	publicCtCodeSnippetStatement
	createCodeSnippetStatement ( ){return Core
		( ).createCodeSnippetStatement();}@
	Override

	publicCtComment
	createComment ( ){return Core
		( ).createComment();}@
	Override

	publicCtContinue
	createContinue ( ){return Core
		( ).createContinue();}@
	Override

	publicCtDo
	createDo ( ){return Core
		( ).createDo();}@
	Override

	publicCtFor
	createFor ( ){return Core
		( ).createFor();}@
	Override

	publicCtForEach
	createForEach ( ){return Core
		( ).createForEach();}@
	Override

	publicCtIf
	createIf ( ){return Core
		( ).createIf();}@
	Override

	publicCtSynchronized
	createSynchronized ( ){return Core
		( ).createSynchronized();}@
	Override

	publicCtThrow
	createThrow ( ){return Core
		( ).createThrow();}@
	Override

	publicCtTry
	createTry ( ){return Core
		( ).createTry();}@
	Override

	publicCtTryWithResource
	createTryWithResource ( ){return Core
		( ).createTryWithResource();}@
	Override

	publicCtWhile
	createWhile ( ){return Core
		( ).createWhile();}@
	Override

	publicCompilationUnit
	createCompilationUnit ( ){return Core
		( ).createCompilationUnit();}@
	Override

	publicSourcePosition
	createSourcePosition ( CompilationUnitcompilationUnit, intstartSource , intend , int[ ]lineSeparatorPositions) {return Core
		( ).createSourcePosition(compilationUnit,startSource, end, lineSeparatorPositions) ;}@
	Override

	publicBodyHolderSourcePosition
	createBodyHolderSourcePosition ( CompilationUnitcompilationUnit, intstartSource , intend , intmodifierStart , intmodifierEnd , intdeclarationStart , intdeclarationEnd , intbodyStart , intbodyEnd , int[ ]lineSeparatorPositions) {return Core
		( ).createBodyHolderSourcePosition(compilationUnit,startSource, end, modifierStart, modifierEnd, declarationStart, declarationEnd, bodyStart, bodyEnd, lineSeparatorPositions) ;}@
	Override

	publicDeclarationSourcePosition
	createDeclarationSourcePosition ( CompilationUnitcompilationUnit, intstartSource , intend , intmodifierStart , intmodifierEnd , intdeclarationStart , intdeclarationEnd , int[ ]lineSeparatorPositions) {return Core
		( ).createDeclarationSourcePosition(compilationUnit,startSource, end, modifierStart, modifierEnd, declarationStart, declarationEnd, lineSeparatorPositions) ;}@
	Override

	publicCtAnonymousExecutable
	createAnonymousExecutable ( ){return Core
		( ).createAnonymousExecutable();}@
	Override

	publicCtPackage
	createPackage ( ){return Core
		( ).createPackage();}@
	Override

	publicCtTypeParameter
	createTypeParameter ( ){return Core
		( ).createTypeParameter();}@
	Override

	publicCtPackageReference
	createPackageReference ( ){return Core
		( ).createPackageReference();}@
	Override

	publicCtTypeParameterReference
	createTypeParameterReference ( ){return Core
		( ).createTypeParameterReference();}@
	Override

	publicCtWildcardReference
	createWildcardReference ( ){return Core
		( ).createWildcardReference();}@
	Override

	publicPartialEvaluator
	createPartialEvaluator ( ){return Eval
		( ).createPartialEvaluator();}@
	Override

	public<
	T >CtParameter< T>createParameter( CtExecutable<?>parent, CtTypeReference< T>type, Stringname ) {return Executable
		( ).createParameter(parent,type, name) ;}@
	Override

	public<
	T >CtParameterReference< T>createParameterReference( CtParameter<T>parameter) {return Executable
		( ).createParameterReference(parameter);}@
	Override

	publicCtAnonymousExecutable
	createAnonymous ( CtClass<?>target, CtBlock< Void>body) {return Executable
		( ).createAnonymous(target,body) ;}@
	Override

	public<
	T >CtArrayTypeReference< T>createArrayReference( StringqualifiedName) {return Type
		( ).createArrayReference(qualifiedName);}@
	Override

	public<
	T >CtArrayTypeReference< T[]>createArrayReference( CtType<T>type) {return Type
		( ).createArrayReference(type);}@
	Override

	public<
	T >CtArrayTypeReference< T[]>createArrayReference( CtTypeReference<T>reference) {return Type
		( ).createArrayReference(reference);}@
	Override

	public<
	T >CtIntersectionTypeReference< T>createIntersectionTypeReferenceWithBounds( List<CtTypeReference<?>>bounds) {return Type
		( ).createIntersectionTypeReferenceWithBounds(bounds);}@
	Override

	publicGenericTypeAdapter
	createTypeAdapter ( CtFormalTypeDeclarerformalTypeDeclarer) {return Type
		( ).createTypeAdapter(formalTypeDeclarer);}@
	Override

	publicList
	< CtTypeReference<?>>createReferences( List<Class<?>>classes) {return Type
		( ).createReferences(classes);}@
	Override

	publicCtArrayTypeReference
	< ?>createArrayReference( CtTypeReference<?>reference, intn ) {return Type
		( ).createArrayReference(reference,n) ;}@
	Override

	publicCtTypeParameterReference
	createTypeParameterReference ( Stringname) {return Type
		( ).createTypeParameterReference(name);}@
	Override

	publicCtQuery
	createQuery ( ){return Query
		( ).createQuery();}@
	Override

	publicCtQuery
	createQuery ( Objectinput) {return Query
		( ).createQuery(input);}@
	Override

	publicCtQuery
	createQuery ( Object[]input) {return Query
		( ).createQuery(input);}@
	Override

	publicCtQuery
	createQuery ( Iterable<?>input) {return Query
		( ).createQuery(input);}@
	Override

	publicCtAnnotationType
	createAnnotationType ( StringqualifiedName) {return Annotation
		( ).create(qualifiedName);}@
	Override

	publicCtAnnotationType
	createAnnotationType ( CtPackageowner, StringsimpleName ) {return Annotation
		( ).create(owner,simpleName) ;}@
	Override

	publicCtClass
	createClass ( StringqualifiedName) {return Class
		( ).create(qualifiedName);}@
	Override

	publicCtClass
	createClass ( CtClass<?>declaringClass, StringsimpleName ) {return Class
		( ).create(declaringClass,simpleName) ;}@
	Override

	publicCtClass
	createClass ( CtPackageowner, StringsimpleName ) {return Class
		( ).create(owner,simpleName) ;}@
	Override

	publicCtConstructor
	createConstructor ( CtClasstarget, CtConstructor< ?>source) {return Constructor
		( ).create(target,source) ;}@
	Override

	publicCtConstructor
	createConstructor ( CtClasstarget, CtMethod< ?>source) {return Constructor
		( ).create(target,source) ;}@
	Override

	publicCtConstructor
	createConstructor ( CtClasstarget, Set< ModifierKind>modifiers, List< CtParameter<?>>parameters, Set< CtTypeReference<?extendsThrowable > >thrownTypes) {return Constructor
		( ).create(target,modifiers, parameters, thrownTypes) ;}@
	Override

	publicCtConstructor
	createConstructor ( CtClasstarget, Set< ModifierKind>modifiers, List< CtParameter<?>>parameters, Set< CtTypeReference<?extendsThrowable > >thrownTypes, CtBlockbody ) {return Constructor
		( ).create(target,modifiers, parameters, thrownTypes, body) ;}@
	Override

	publicCtEnum
	< ?>createEnum( StringqualifiedName) {return Enum
		( ).create(qualifiedName);}@
	Override

	publicCtEnum
	< ?>createEnum( CtPackageowner, StringsimpleName ) {return Enum
		( ).create(owner,simpleName) ;}@
	Override

	publicCtField
	createField ( CtType<?>target, Set< ModifierKind>modifiers, CtTypeReferencetype , Stringname ) {return Field
		( ).create(target,modifiers, type, name) ;}@
	Override

	publicCtField
	createField ( CtType<?>target, Set< ModifierKind>modifiers, CtTypeReferencetype , Stringname , CtExpressiondefaultExpression ) {return Field
		( ).create(target,modifiers, type, name, defaultExpression) ;}@
	Override

	publicCtField
	createField ( CtType<?>target, CtFieldsource ) {return Field
		( ).create(target,source) ;}@
	Override

	publicCtInterface
	createInterface ( CtPackageowner, StringsimpleName ) {return Interface
		( ).create(owner,simpleName) ;}@
	Override

	publicCtInterface
	createInterface ( CtTypeowner, StringsimpleName ) {return Interface
		( ).create(owner,simpleName) ;}@
	Override

	publicCtInterface
	createInterface ( StringqualifiedName) {return Interface
		( ).create(qualifiedName);}@
	Override

	publicCtMethod
	createMethod ( CtClass<?>target, Set< ModifierKind>modifiers, CtTypeReferencereturnType , Stringname , List< CtParameter<?>>parameters, Set< CtTypeReference<?extendsThrowable > >thrownTypes, CtBlockbody ) {return Method
		( ).create(target,modifiers, returnType, name, parameters, thrownTypes, body) ;}@
	Override

	publicCtMethod
	createMethod ( CtType<?>target, CtMethodsource , booleanredirectReferences ) {return Method
		( ).create(target,source, redirectReferences) ;}@
	Override

	publicCtMethod
	createMethod ( CtType<?>target, Set< ModifierKind>modifiers, CtTypeReferencereturnType , Stringname , List< CtParameter<?>>parameters, Set< CtTypeReference<?extendsThrowable > >thrownTypes) {return Method
		( ).create(target,modifiers, returnType, name, parameters, thrownTypes) ;}@
	Override

	publicCtPackage
	createPackage ( CtPackageparent, StringsimpleName ) {return Package
		( ).create(parent,simpleName) ;}@
	Override

	publicCtElement
	createElement ( Class<?extendsCtElement > klass) {return Core
		( ).create(klass);}@
	Override

	publicCtImport
	createImport ( CtReferencereference) {return Type
		( ).createImport(reference);}@
	Override

	publicCtTypeMemberWildcardImportReference
	createTypeMemberWildcardImportReference ( CtTypeReferencetypeReference) {return Type
		( ).createTypeMemberWildcardImportReference(typeReference);}@
	Override

	publicCtPackageExport
	createPackageExport ( CtPackageReferencectPackageReference) {return Module
		( ).createPackageExport(ctPackageReference);}@
	Override

	publicCtProvidedService
	createProvidedService ( CtTypeReferencectTypeReference) {return Module
		( ).createProvidedService(ctTypeReference);}@
	Override

	publicCtModuleRequirement
	createModuleRequirement ( CtModuleReferencectModuleReference) {return Module
		( ).createModuleRequirement(ctModuleReference);}@
	Override

	publicCtModule
	createModule ( StringmoduleName) {return Module
		( ).getOrCreate(moduleName);}@
	Override

	publicCtModuleReference
	createModuleReference ( CtModulectModule) {return Module
		( ).createReference(ctModule);}@
	Override

	publicCtUsedService
	createUsedService ( CtTypeReferencetypeReference) {return Module
		( ).createUsedService(typeReference);}@
	Override

	publicSourcePosition
	createPartialSourcePosition ( CompilationUnitcompilationUnit) {return Core
		( ).createPartialSourcePosition(compilationUnit);}@
	Override

	publicCtPackageDeclaration
	createPackageDeclaration ( CtPackageReferencepackageRef) {return Package
		( ).createPackageDeclaration(packageRef);}}
	