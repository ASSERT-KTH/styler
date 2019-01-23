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
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.eval.PartialEvaluator;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.
CtPackageReference ;importspoon.reflect.reference.
CtParameterReference ;importspoon.reflect.reference.
CtReference ;importspoon.reflect.reference.
CtTypeParameterReference ;importspoon.reflect.reference.
CtTypeReference ;importspoon.reflect.reference.
CtUnboundVariableReference ;importspoon.reflect.reference.
CtVariableReference ;importspoon.reflect.reference.
CtWildcardReference ;importspoon.reflect.reference.
CtTypeMemberWildcardImportReference ;importspoon.reflect.visitor.chain.
CtQuery ;importspoon.support.
DefaultCoreFactory ;importspoon.support.
StandardEnvironment ;importspoon.support.visitor.

GenericTypeAdapter ;importjava.io.
IOException ;importjava.io.
Serializable ;importjava.lang.annotation.
Annotation ;importjava.util.
HashMap ;importjava.util.
List ;importjava.util.
Map ;importjava.util.
Random ;importjava.util.
Set ;importjava.util.concurrent.

ThreadLocalRandom
; /**
 * Implements {@link Factory}
 */ public class FactoryImplimplements Factory ,

	Serializable { private static final long serialVersionUID=

	1L ; private transientFactory

	parentFactory
	; /**
	 * Returns the parent of this factory. When an element is not found in a
	 * factory, it can be looked up in its parent factory using a delegation
	 * model.
	 */ publicFactorygetParentFactory (
		) {return
	parentFactory

	; } private transientAnnotationFactory

	annotation
	;/**
	 * The {@link CtAnnotationType} sub-factory.
	 */
	@ Override publicAnnotationFactoryAnnotation (
		) {if ( annotation== null
			) { annotation =newAnnotationFactory(this
		)
		; }return
	annotation

	; } private transientClassFactory

	clazz
	;/**
	 * The {@link CtClass} sub-factory.
	 */
	@ Override publicClassFactoryClass (
		) {if ( clazz== null
			) { clazz =newClassFactory(this
		)
		; }return
	clazz

	; } private transientCodeFactory

	code
	;/**
	 * The {@link spoon.reflect.code.CtCodeElement} sub-factory.
	 */
	@ Override publicCodeFactoryCode (
		) {if ( code== null
			) { code =newCodeFactory(this
		)
		; }return
	code

	; } private transientConstructorFactory

	constructor
	;/**
	 * The {@link CtConstructor} sub-factory.
	 */
	@ Override publicConstructorFactoryConstructor (
		) {if ( constructor== null
			) { constructor =newConstructorFactory(this
		)
		; }return
	constructor

	; } private transientCoreFactory

	core
	;/**
	 * The core factory.
	 */
	@ Override publicCoreFactoryCore (
		) {if ( core== null
			)
			{ //During deserialization, the transient field core, is null core =newDefaultCoreFactory(
			);core.setMainFactory(this
		)
		; }return
	core

	; } private transientEnumFactory

	enumF
	;/**
	 * The {@link CtEnum} sub-factory.
	 */
	@ Override publicEnumFactoryEnum (
		) {if ( enumF== null
			) { enumF =newEnumFactory(this
		)
		; }return
	enumF

	; } private transientEnvironment

	environment
	;/**
	 * Gets the Spoon environment that encloses this factory.
	 */
	@ Override publicEnvironmentgetEnvironment (
		) {if ( environment== null
			) { environment =newStandardEnvironment(
		)
		; }return
	environment

	; } private transientExecutableFactory

	executable
	;/**
	 * The {@link CtExecutable} sub-factory.
	 */
	@ Override publicExecutableFactoryExecutable (
		) {if ( executable== null
			) { executable =newExecutableFactory(this
		)
		; }return
	executable

	; } private transientEvalFactory

	eval
	;/**
	 * The evaluators sub-factory.
	 */
	@ Override publicEvalFactoryEval (
		) {if ( eval== null
			) { eval =newEvalFactory(this
		)
		; }return
	eval

	; } private transientFieldFactory

	field
	;/**
	 * The {@link CtField} sub-factory.
	 */
	@ Override publicFieldFactoryField (
		) {if ( field== null
			) { field =newFieldFactory(this
		)
		; }return
	field

	;
	} /**
	 * The {@link CtInterface} sub-factory.
	 */ private transientInterfaceFactory

	interfaceF
	;/**
	 * The {@link CtInterface} sub-factory.
	 */
	@ Override publicInterfaceFactoryInterface (
		) {if ( interfaceF== null
			) { interfaceF =newInterfaceFactory(this
		)
		; }return
	interfaceF

	; } private transientMethodFactory

	methodF
	;/**
	 * The {@link CtMethod} sub-factory.
	 */
	@ Override publicMethodFactoryMethod (
		) {if ( methodF== null
			) { methodF =newMethodFactory(this
		)
		; }return
	methodF

	; } private transientPackageFactory

	packageF
	;/**
	 * The {@link CtPackage} sub-factory.
	 */
	@ Override publicPackageFactoryPackage (
		) {if ( packageF== null
			) { packageF =newPackageFactory(this
		)
		; }return
	packageF

	; } private transientCompilationUnitFactory

	compilationUnit
	;/**
	 * The {@link CompilationUnit} sub-factory.
	 */
	@ Override publicCompilationUnitFactoryCompilationUnit (
		) {if ( compilationUnit== null
			) { compilationUnit =newCompilationUnitFactory(this
		)
		; }return
	compilationUnit

	; } private transientTypeFactory

	type
	;/**
	 * The {@link CtType} sub-factory.
	 */
	@ Override publicTypeFactoryType (
		) {if ( type== null
			) { type =newTypeFactory(this
		)
		; }return
	type

	; } private transientQueryFactory

	query
	;/**
	 * The query sub-factory.
	 */
	@ Override publicQueryFactoryQuery (
		) {if ( query== null
			) { query =newQueryFactory(this
		)
		; }return
	query

	; } private transientModuleFactory

	module
	;/**
	 * The module sub-factory
	 */
	@ Override publicModuleFactoryModule (
		) {if ( module== null
			) { module =newModuleFactory(this
		)
		; }return
	module


	;
	} /**
	 * A constructor that takes the parent factory
	 */publicFactoryImpl (CoreFactory coreFactory ,Environment environment ,Factory parentFactory
		){this . environment=
		environment;this . core=
		coreFactory;this.core.setMainFactory(this
		);this . parentFactory=
	parentFactory

	;
	} /**
	 * Should not be called directly. Use {@link spoon.Launcher#createFactory()} instead.
	 */publicFactoryImpl (CoreFactory coreFactory ,Environment environment
		){this( coreFactory, environment,null
	)

	;
	}

	// Deduplication // See http://shipilev.net/talks/joker-Oct2014-string-catechism.pdf private static class
		Dedup{Map< String, String > cache =newHashMap<>(
		) ; Random random=ThreadLocalRandom.current(
	)

	;
	} /**
	 * Note this is an instance field. To avoid memory leaks and dedup being
	 * targeted to each Spoon Launching, that could differ a lot by
	 * frequently used symbols.
	 */ privatetransientThreadLocal< Dedup > threadLocalDedup =newThreadLocal<Dedup> (
		){
		@ Override protectedDedupinitialValue (
			) { returnnewDedup(
		)
	;}

	}
	; /**
	 * Returns a String equal to the given symbol. Performs probablilistic
	 * deduplication.
	 */ publicStringdedup (String symbol
		) { Dedup dedup=threadLocalDedup.get(
		);Map< String, String > cache=dedup.
		cache ;String
		cached ;if( ( cached=cache.get(symbol ) )!= null
			) {return
		cached ; }
			else
			{ // Puts the symbol into cache with 20% probability int prob=( int)(Integer . MIN_VALUE+ ( 0.2* ( 1L<<32))
			) ;if(dedup.random.nextInt ( )< prob
				){cache.put( symbol,symbol
			)
			; }return
		symbol
	;

	}
	} /**
	 * Needed to restore state of transient fields during reading from stream
	 */ privatevoidreadObject(java.io .ObjectInputStream in )throws IOException ,
		ClassNotFoundException { threadLocalDedup =newThreadLocal<Dedup> (
			){
			@ Override protectedDedupinitialValue (
				) { returnnewDedup(
			)
		;}
		};in.defaultReadObject(
	)

	; } private final CtModel model =newCtModelImpl(this

	);
	@ Override publicCtModelgetModel (
		) {return
	model

	;}
	@ Overridepublic < Aextends Annotation>CtAnnotation< A>createAnnotation(CtTypeReference< A> annotationType
		) {returnCode().createAnnotation(annotationType
	)

	;}
	@ Overridepublic< A , Textends A>CtAssignment< A, T>createVariableAssignment(CtVariableReference< A> variable ,boolean isStatic,CtExpression< T> expression
		) {returnCode().createVariableAssignment( variable, isStatic,expression
	)

	;}
	@ Overridepublic< R >CtStatementListcreateStatementList(CtBlock< R> block
		) {returnCode().createStatementList(block
	)

	;}
	@ Overridepublic < Textends CtStatement>CtBlock< ?>createCtBlock (T element
		) {returnCode().createCtBlock(element
	)

	;}
	@ Overridepublic< T>CtBinaryOperator< T>createBinaryOperator(CtExpression< ?> left,CtExpression< ?> right ,BinaryOperatorKind kind
		) {returnCode().createBinaryOperator( left, right,kind
	)

	;}
	@ Overridepublic< T>CtCatchVariable< T>createCatchVariable(CtTypeReference< T> type ,String name, ModifierKind... modifierKinds
		) {returnCode().createCatchVariable( type, name,modifierKinds
	)

	;}
	@ Overridepublic< T>CtCodeSnippetExpression< T>createCodeSnippetExpression (String expression
		) {returnCode().createCodeSnippetExpression(expression
	)

	;}
	@ Overridepublic< T>CtConstructorCall< T>createConstructorCall(CtTypeReference< T> type,CtExpression<? >... parameters
		) {returnCode().createConstructorCall( type,parameters
	)

	;}
	@ Overridepublic< T>CtFieldAccess<Class<T >>createClassAccess(CtTypeReference< T> type
		) {returnCode().createClassAccess(type
	)

	;}
	@ Overridepublic< T>CtInvocation< T>createInvocation(CtExpression< ?> target,CtExecutableReference< T> executable,List<CtExpression<? >> arguments
		) {returnCode().createInvocation( target, executable,arguments
	)

	;}
	@ Overridepublic< T>CtInvocation< T>createInvocation(CtExpression< ?> target,CtExecutableReference< T> executable,CtExpression<? >... arguments
		) {returnCode().createInvocation( target, executable,arguments
	)

	;}
	@ Overridepublic< T>CtLiteral< T>createLiteral (T value
		) {returnCode().createLiteral(value
	)

	;}
	@ Overridepublic< T>CtLocalVariable< T>createLocalVariable(CtTypeReference< T> type ,String name,CtExpression< T> defaultExpression
		) {returnCode().createLocalVariable( type, name,defaultExpression
	)

	;}@SuppressWarnings ( value=
	"unchecked")
	@ Overridepublic< T>CtNewArray<T[ ]>createLiteralArray(T [] value
		) {returnCode().createLiteralArray(value
	)

	;}
	@ Overridepublic< T>CtNewClass< T>createNewClass(CtTypeReference< T> type,CtClass< ?> anonymousClass,CtExpression<? >... parameters
		) {returnCode().createNewClass( type, anonymousClass,parameters
	)

	;}
	@ Overridepublic< T >CtStatementListcreateVariableAssignments(List < ?extendsCtVariable<T >> variables,List < ?extendsCtExpression<T >> expressions
		) {returnCode().createVariableAssignments( variables,expressions
	)

	;}
	@ Overridepublic< T>CtThisAccess< T>createThisAccess(CtTypeReference< T> type
		) {returnCode().createThisAccess(type
	)

	;}
	@ Overridepublic< T>CtThisAccess< T>createThisAccess(CtTypeReference< T> type ,boolean isImplicit
		) {returnCode().createThisAccess( type,isImplicit
	)

	;}
	@ Overridepublic< T>CtTypeAccess< T>createTypeAccess(CtTypeReference< T> accessedType
		) {returnCode().createTypeAccess(accessedType
	)

	;}
	@ Overridepublic< T>CtTypeAccess< T>createTypeAccess(CtTypeReference< T> accessedType ,boolean isImplicit
		) {returnCode().createTypeAccess( accessedType,isImplicit
	)

	;}
	@ Overridepublic< T>CtTypeAccess< T>createTypeAccessWithoutCloningReference(CtTypeReference< T> accessedType
		) {returnCode().createTypeAccessWithoutCloningReference(accessedType
	)

	;}
	@ Overridepublic< T>CtVariableAccess< T>createVariableRead(CtVariableReference< T> variable ,boolean isStatic
		) {returnCode().createVariableRead( variable,isStatic
	)

	;}
	@ Overridepublic< T>CtField< T>createCtField (String name,CtTypeReference< T> type ,String exp, ModifierKind... visibilities
		) {returnCode().createCtField( name, type, exp,visibilities
	)

	;}
	@ Overridepublic< T>CtCatchVariableReference< T>createCatchVariableReference(CtCatchVariable< T> catchVariable
		) {returnCode().createCatchVariableReference(catchVariable
	)

	;}
	@ Overridepublic< T>CtLocalVariableReference< T>createLocalVariableReference(CtLocalVariable< T> localVariable
		) {returnCode().createLocalVariableReference(localVariable
	)

	;}
	@ Overridepublic< T>CtLocalVariableReference< T>createLocalVariableReference(CtTypeReference< T> type ,String name
		) {returnCode().createLocalVariableReference( type,name
	)

	;}
	@ Overridepublic< T>CtTypeReference< T>createCtTypeReference(Class< ?> originalClass
		) {returnCode().createCtTypeReference(originalClass
	)

	;}
	@ OverridepublicList<CtExpression<? >>createVariableReads(List < ?extendsCtVariable<? >> variables
		) {returnCode().createVariableReads(variables
	)

	;}
	@ Override publicCtCatchcreateCtCatch (String nameCatch,Class < ?extends Throwable> exception,CtBlock< ?> ctBlock
		) {returnCode().createCtCatch( nameCatch, exception,ctBlock
	)

	;}
	@ Override publicCtCodeSnippetStatementcreateCodeSnippetStatement (String statement
		) {returnCode().createCodeSnippetStatement(statement
	)

	;}
	@ Override publicCtCommentcreateComment (String content,CtComment .CommentType type
		) {returnCode().createComment( content,type
	)

	;}
	@ Override publicCtCommentcreateInlineComment (String content
		) {returnCode().createInlineComment(content
	)

	;}
	@ Override publicCtJavaDocTagcreateJavaDocTag (String content,CtJavaDocTag .TagType type
		) {returnCode().createJavaDocTag( content,type
	)

	;}
	@ Override publicCtThrowcreateCtThrow (String thrownExp
		) {returnCode().createCtThrow(thrownExp
	)

	;}
	@ Override publicCtPackageReferencecreateCtPackageReference (Package originalPackage
		) {returnCode().createCtPackageReference(originalPackage
	)

	;}
	@ Overridepublic< T>CtConstructor< T>createDefault(CtClass< T> target
		) {returnConstructor().createDefault(target
	)

	;}
	@ Overridepublic < Aextends Annotation>CtAnnotation< A>createAnnotation (
		) {returnCore().createAnnotation(
	)

	;}
	@ Overridepublic< R>CtBlock< R>createBlock (
		) {returnCore().createBlock(
	)

	;}
	@ Overridepublic< R>CtReturn< R>createReturn (
		) {returnCore().createReturn(
	)

	;}
	@ Overridepublic< R >CtStatementListcreateStatementList (
		) {returnCore().createStatementList(
	)

	;}
	@ Overridepublic< S>CtCase< S>createCase (
		) {returnCore().createCase(
	)

	;}
	@ Overridepublic< S>CtSwitch< S>createSwitch (
		) {returnCore().createSwitch(
	)

	;}
	@ Overridepublic < TextendsEnum<? >>CtEnum< T>createEnum (
		) {returnCore().createEnum(
	)

	;}
	@ Overridepublic < Textends Annotation>CtAnnotationType< T>createAnnotationType (
		) {returnCore().createAnnotationType(
	)

	;}
	@ Overridepublic< T , Aextends T>CtAssignment< T, A>createAssignment (
		) {returnCore().createAssignment(
	)

	;}
	@ Overridepublic< T , Aextends T>CtOperatorAssignment< T, A>createOperatorAssignment (
		) {returnCore().createOperatorAssignment(
	)

	;}
	@ Overridepublic< T , EextendsCtExpression<? >>CtExecutableReferenceExpression< T, E>createExecutableReferenceExpression (
		) {returnCore().createExecutableReferenceExpression(
	)

	;}
	@ Overridepublic< T>CtAnnotationFieldAccess< T>createAnnotationFieldAccess (
		) {returnCore().createAnnotationFieldAccess(
	)

	;}
	@ Overridepublic< T>CtArrayRead< T>createArrayRead (
		) {returnCore().createArrayRead(
	)

	;}
	@ Overridepublic< T>CtArrayWrite< T>createArrayWrite (
		) {returnCore().createArrayWrite(
	)

	;}
	@ Overridepublic< T>CtAssert< T>createAssert (
		) {returnCore().createAssert(
	)

	;}
	@ Overridepublic< T>CtBinaryOperator< T>createBinaryOperator (
		) {returnCore().createBinaryOperator(
	)

	;}
	@ Overridepublic< T>CtCatchVariable< T>createCatchVariable (
		) {returnCore().createCatchVariable(
	)

	;}
	@ Overridepublic< T>CtCodeSnippetExpression< T>createCodeSnippetExpression (
		) {returnCore().createCodeSnippetExpression(
	)

	;}
	@ Overridepublic< T>CtConditional< T>createConditional (
		) {returnCore().createConditional(
	)

	;}
	@ Overridepublic< T>CtConstructorCall< T>createConstructorCall (
		) {returnCore().createConstructorCall(
	)

	;}
	@ Overridepublic< T>CtFieldRead< T>createFieldRead (
		) {returnCore().createFieldRead(
	)

	;}
	@ Overridepublic< T>CtFieldWrite< T>createFieldWrite (
		) {returnCore().createFieldWrite(
	)

	;}
	@ Overridepublic< T>CtInvocation< T>createInvocation (
		) {returnCore().createInvocation(
	)

	;}
	@ Overridepublic< T>CtLambda< T>createLambda (
		) {returnCore().createLambda(
	)

	;}
	@ Overridepublic< T>CtLiteral< T>createLiteral (
		) {returnCore().createLiteral(
	)

	;}
	@ Overridepublic< T>CtLocalVariable< T>createLocalVariable (
		) {returnCore().createLocalVariable(
	)

	;}
	@ Overridepublic< T>CtNewArray< T>createNewArray (
		) {returnCore().createNewArray(
	)

	;}
	@ Overridepublic< T>CtNewClass< T>createNewClass (
		) {returnCore().createNewClass(
	)

	;}
	@ Overridepublic< T>CtSuperAccess< T>createSuperAccess (
		) {returnCore().createSuperAccess(
	)

	;}
	@ Overridepublic< T>CtThisAccess< T>createThisAccess (
		) {returnCore().createThisAccess(
	)

	;}
	@ Overridepublic< T>CtTypeAccess< T>createTypeAccess (
		) {returnCore().createTypeAccess(
	)

	;}
	@ Overridepublic< T>CtUnaryOperator< T>createUnaryOperator (
		) {returnCore().createUnaryOperator(
	)

	;}
	@ Overridepublic< T>CtVariableRead< T>createVariableRead (
		) {returnCore().createVariableRead(
	)

	;}
	@ Overridepublic< T>CtVariableWrite< T>createVariableWrite (
		) {returnCore().createVariableWrite(
	)

	;}
	@ Overridepublic< T>CtAnnotationMethod< T>createAnnotationMethod (
		) {returnCore().createAnnotationMethod(
	)

	;}
	@ Overridepublic< T>CtClass< T>createClass (
		) {returnCore().createClass(
	)

	;}
	@ Overridepublic< T>CtConstructor< T>createConstructor (
		) {returnCore().createConstructor(
	)

	;}
	@ Overridepublic< T>CtConstructor< T>createInvisibleArrayConstructor (
		) {returnCore().createInvisibleArrayConstructor(
	)

	;}
	@ Overridepublic< T>CtEnumValue< T>createEnumValue (
		) {returnCore().createEnumValue(
	)

	;}
	@ Overridepublic< T>CtField< T>createField (
		) {returnCore().createField(
	)

	;}
	@ Overridepublic< T>CtInterface< T>createInterface (
		) {returnCore().createInterface(
	)

	;}
	@ Overridepublic< T>CtMethod< T>createMethod (
		) {returnCore().createMethod(
	)

	;}
	@ Overridepublic< T>CtParameter< T>createParameter (
		) {returnCore().createParameter(
	)

	;}
	@ Overridepublic< T>CtArrayTypeReference< T>createArrayTypeReference (
		) {returnCore().createArrayTypeReference(
	)

	;}
	@ Overridepublic< T>CtCatchVariableReference< T>createCatchVariableReference (
		) {returnCore().createCatchVariableReference(
	)

	;}
	@ Overridepublic< T>CtExecutableReference< T>createExecutableReference (
		) {returnCore().createExecutableReference(
	)

	;}
	@ Overridepublic< T>CtFieldReference< T>createFieldReference (
		) {returnCore().createFieldReference(
	)

	;}
	@ Overridepublic< T>CtIntersectionTypeReference< T>createIntersectionTypeReference (
		) {returnCore().createIntersectionTypeReference(
	)

	;}
	@ Overridepublic< T>CtLocalVariableReference< T>createLocalVariableReference (
		) {returnCore().createLocalVariableReference(
	)

	;}
	@ Overridepublic< T>CtParameterReference< T>createParameterReference (
		) {returnCore().createParameterReference(
	)

	;}
	@ Overridepublic< T>CtTypeReference< T>createTypeReference (
		) {returnCore().createTypeReference(
	)

	;}
	@ Overridepublic< T>CtUnboundVariableReference< T>createUnboundVariableReference (
		) {returnCore().createUnboundVariableReference(
	)

	;}
	@ Override publicCtBreakcreateBreak (
		) {returnCore().createBreak(
	)

	;}
	@ Override publicCtCatchcreateCatch (
		) {returnCore().createCatch(
	)

	;}
	@ Override publicCtCodeSnippetStatementcreateCodeSnippetStatement (
		) {returnCore().createCodeSnippetStatement(
	)

	;}
	@ Override publicCtCommentcreateComment (
		) {returnCore().createComment(
	)

	;}
	@ Override publicCtContinuecreateContinue (
		) {returnCore().createContinue(
	)

	;}
	@ Override publicCtDocreateDo (
		) {returnCore().createDo(
	)

	;}
	@ Override publicCtForcreateFor (
		) {returnCore().createFor(
	)

	;}
	@ Override publicCtForEachcreateForEach (
		) {returnCore().createForEach(
	)

	;}
	@ Override publicCtIfcreateIf (
		) {returnCore().createIf(
	)

	;}
	@ Override publicCtSynchronizedcreateSynchronized (
		) {returnCore().createSynchronized(
	)

	;}
	@ Override publicCtThrowcreateThrow (
		) {returnCore().createThrow(
	)

	;}
	@ Override publicCtTrycreateTry (
		) {returnCore().createTry(
	)

	;}
	@ Override publicCtTryWithResourcecreateTryWithResource (
		) {returnCore().createTryWithResource(
	)

	;}
	@ Override publicCtWhilecreateWhile (
		) {returnCore().createWhile(
	)

	;}
	@ Override publicCompilationUnitcreateCompilationUnit (
		) {returnCore().createCompilationUnit(
	)

	;}
	@ Override publicSourcePositioncreateSourcePosition (CompilationUnit compilationUnit ,int startSource ,int end,int [] lineSeparatorPositions
		) {returnCore().createSourcePosition( compilationUnit, startSource, end,lineSeparatorPositions
	)

	;}
	@ Override publicBodyHolderSourcePositioncreateBodyHolderSourcePosition (CompilationUnit compilationUnit ,int startSource ,int end ,int modifierStart ,int modifierEnd ,int declarationStart ,int declarationEnd ,int bodyStart ,int bodyEnd,int [] lineSeparatorPositions
		) {returnCore().createBodyHolderSourcePosition( compilationUnit, startSource, end, modifierStart, modifierEnd, declarationStart, declarationEnd, bodyStart, bodyEnd,lineSeparatorPositions
	)

	;}
	@ Override publicDeclarationSourcePositioncreateDeclarationSourcePosition (CompilationUnit compilationUnit ,int startSource ,int end ,int modifierStart ,int modifierEnd ,int declarationStart ,int declarationEnd,int [] lineSeparatorPositions
		) {returnCore().createDeclarationSourcePosition( compilationUnit, startSource, end, modifierStart, modifierEnd, declarationStart, declarationEnd,lineSeparatorPositions
	)

	;}
	@ Override publicCtAnonymousExecutablecreateAnonymousExecutable (
		) {returnCore().createAnonymousExecutable(
	)

	;}
	@ Override publicCtPackagecreatePackage (
		) {returnCore().createPackage(
	)

	;}
	@ Override publicCtTypeParametercreateTypeParameter (
		) {returnCore().createTypeParameter(
	)

	;}
	@ Override publicCtPackageReferencecreatePackageReference (
		) {returnCore().createPackageReference(
	)

	;}
	@ Override publicCtTypeParameterReferencecreateTypeParameterReference (
		) {returnCore().createTypeParameterReference(
	)

	;}
	@ Override publicCtWildcardReferencecreateWildcardReference (
		) {returnCore().createWildcardReference(
	)

	;}
	@ Override publicPartialEvaluatorcreatePartialEvaluator (
		) {returnEval().createPartialEvaluator(
	)

	;}
	@ Overridepublic< T>CtParameter< T>createParameter(CtExecutable< ?> parent,CtTypeReference< T> type ,String name
		) {returnExecutable().createParameter( parent, type,name
	)

	;}
	@ Overridepublic< T>CtParameterReference< T>createParameterReference(CtParameter< T> parameter
		) {returnExecutable().createParameterReference(parameter
	)

	;}
	@ Override publicCtAnonymousExecutablecreateAnonymous(CtClass< ?> target,CtBlock< Void> body
		) {returnExecutable().createAnonymous( target,body
	)

	;}
	@ Overridepublic< T>CtArrayTypeReference< T>createArrayReference (String qualifiedName
		) {returnType().createArrayReference(qualifiedName
	)

	;}
	@ Overridepublic< T>CtArrayTypeReference<T[ ]>createArrayReference(CtType< T> type
		) {returnType().createArrayReference(type
	)

	;}
	@ Overridepublic< T>CtArrayTypeReference<T[ ]>createArrayReference(CtTypeReference< T> reference
		) {returnType().createArrayReference(reference
	)

	;}
	@ Overridepublic< T>CtIntersectionTypeReference< T>createIntersectionTypeReferenceWithBounds(List<CtTypeReference<? >> bounds
		) {returnType().createIntersectionTypeReferenceWithBounds(bounds
	)

	;}
	@ Override publicGenericTypeAdaptercreateTypeAdapter (CtFormalTypeDeclarer formalTypeDeclarer
		) {returnType().createTypeAdapter(formalTypeDeclarer
	)

	;}
	@ OverridepublicList<CtTypeReference<? >>createReferences(List<Class<? >> classes
		) {returnType().createReferences(classes
	)

	;}
	@ OverridepublicCtArrayTypeReference< ?>createArrayReference(CtTypeReference< ?> reference ,int n
		) {returnType().createArrayReference( reference,n
	)

	;}
	@ Override publicCtTypeParameterReferencecreateTypeParameterReference (String name
		) {returnType().createTypeParameterReference(name
	)

	;}
	@ Override publicCtQuerycreateQuery (
		) {returnQuery().createQuery(
	)

	;}
	@ Override publicCtQuerycreateQuery (Object input
		) {returnQuery().createQuery(input
	)

	;}
	@ Override publicCtQuerycreateQuery(Object [] input
		) {returnQuery().createQuery(input
	)

	;}
	@ Override publicCtQuerycreateQuery(Iterable< ?> input
		) {returnQuery().createQuery(input
	)

	;}
	@ Override publicCtAnnotationTypecreateAnnotationType (String qualifiedName
		) {returnAnnotation().create(qualifiedName
	)

	;}
	@ Override publicCtAnnotationTypecreateAnnotationType (CtPackage owner ,String simpleName
		) {returnAnnotation().create( owner,simpleName
	)

	;}
	@ Override publicCtClasscreateClass (String qualifiedName
		) {returnClass().create(qualifiedName
	)

	;}
	@ Override publicCtClasscreateClass(CtClass< ?> declaringClass ,String simpleName
		) {returnClass().create( declaringClass,simpleName
	)

	;}
	@ Override publicCtClasscreateClass (CtPackage owner ,String simpleName
		) {returnClass().create( owner,simpleName
	)

	;}
	@ Override publicCtConstructorcreateConstructor (CtClass target,CtConstructor< ?> source
		) {returnConstructor().create( target,source
	)

	;}
	@ Override publicCtConstructorcreateConstructor (CtClass target,CtMethod< ?> source
		) {returnConstructor().create( target,source
	)

	;}
	@ Override publicCtConstructorcreateConstructor (CtClass target,Set< ModifierKind> modifiers,List<CtParameter<? >> parameters,Set<CtTypeReference < ?extendsThrowable >> thrownTypes
		) {returnConstructor().create( target, modifiers, parameters,thrownTypes
	)

	;}
	@ Override publicCtConstructorcreateConstructor (CtClass target,Set< ModifierKind> modifiers,List<CtParameter<? >> parameters,Set<CtTypeReference < ?extendsThrowable >> thrownTypes ,CtBlock body
		) {returnConstructor().create( target, modifiers, parameters, thrownTypes,body
	)

	;}
	@ OverridepublicCtEnum< ?>createEnum (String qualifiedName
		) {returnEnum().create(qualifiedName
	)

	;}
	@ OverridepublicCtEnum< ?>createEnum (CtPackage owner ,String simpleName
		) {returnEnum().create( owner,simpleName
	)

	;}
	@ Override publicCtFieldcreateField(CtType< ?> target,Set< ModifierKind> modifiers ,CtTypeReference type ,String name
		) {returnField().create( target, modifiers, type,name
	)

	;}
	@ Override publicCtFieldcreateField(CtType< ?> target,Set< ModifierKind> modifiers ,CtTypeReference type ,String name ,CtExpression defaultExpression
		) {returnField().create( target, modifiers, type, name,defaultExpression
	)

	;}
	@ Override publicCtFieldcreateField(CtType< ?> target ,CtField source
		) {returnField().create( target,source
	)

	;}
	@ Override publicCtInterfacecreateInterface (CtPackage owner ,String simpleName
		) {returnInterface().create( owner,simpleName
	)

	;}
	@ Override publicCtInterfacecreateInterface (CtType owner ,String simpleName
		) {returnInterface().create( owner,simpleName
	)

	;}
	@ Override publicCtInterfacecreateInterface (String qualifiedName
		) {returnInterface().create(qualifiedName
	)

	;}
	@ Override publicCtMethodcreateMethod(CtClass< ?> target,Set< ModifierKind> modifiers ,CtTypeReference returnType ,String name,List<CtParameter<? >> parameters,Set<CtTypeReference < ?extendsThrowable >> thrownTypes ,CtBlock body
		) {returnMethod().create( target, modifiers, returnType, name, parameters, thrownTypes,body
	)

	;}
	@ Override publicCtMethodcreateMethod(CtType< ?> target ,CtMethod source ,boolean redirectReferences
		) {returnMethod().create( target, source,redirectReferences
	)

	;}
	@ Override publicCtMethodcreateMethod(CtType< ?> target,Set< ModifierKind> modifiers ,CtTypeReference returnType ,String name,List<CtParameter<? >> parameters,Set<CtTypeReference < ?extendsThrowable >> thrownTypes
		) {returnMethod().create( target, modifiers, returnType, name, parameters,thrownTypes
	)

	;}
	@ Override publicCtPackagecreatePackage (CtPackage parent ,String simpleName
		) {returnPackage().create( parent,simpleName
	)

	;}
	@ Override publicCtElementcreateElement(Class < ?extends CtElement> klass
		) {returnCore().create(klass
	)

	;}
	@ Override publicCtImportcreateImport (CtReference reference
		) {returnType().createImport(reference
	)

	;}
	@ Override publicCtTypeMemberWildcardImportReferencecreateTypeMemberWildcardImportReference (CtTypeReference typeReference
		) {returnType().createTypeMemberWildcardImportReference(typeReference
	)

	;}
	@ Override publicCtPackageExportcreatePackageExport (CtPackageReference ctPackageReference
		) {returnModule().createPackageExport(ctPackageReference
	)

	;}
	@ Override publicCtProvidedServicecreateProvidedService (CtTypeReference ctTypeReference
		) {returnModule().createProvidedService(ctTypeReference
	)

	;}
	@ Override publicCtModuleRequirementcreateModuleRequirement (CtModuleReference ctModuleReference
		) {returnModule().createModuleRequirement(ctModuleReference
	)

	;}
	@ Override publicCtModulecreateModule (String moduleName
		) {returnModule().getOrCreate(moduleName
	)

	;}
	@ Override publicCtModuleReferencecreateModuleReference (CtModule ctModule
		) {returnModule().createReference(ctModule
	)

	;}
	@ Override publicCtUsedServicecreateUsedService (CtTypeReference typeReference
		) {returnModule().createUsedService(typeReference
	)

	;}
	@ Override publicSourcePositioncreatePartialSourcePosition (CompilationUnit compilationUnit
		) {returnCore().createPartialSourcePosition(compilationUnit
	)

	;}
	@ Override publicCtPackageDeclarationcreatePackageDeclaration (CtPackageReference packageRef
		) {returnPackage().createPackageDeclaration(packageRef
	)
;
