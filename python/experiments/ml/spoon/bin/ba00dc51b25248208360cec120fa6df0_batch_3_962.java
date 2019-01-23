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
package spoon.reflect.visitor;

import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeElement;
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
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLabelledFlowBreak;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtRHSReceiver;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTargetedExpression;
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
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCodeSnippet;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration
. CtModule;importspoon.reflect.declaration
. CtModuleDirective;importspoon.reflect.declaration
. CtPackageExport;importspoon.reflect.declaration
. CtProvidedService;importspoon.reflect.declaration
. CtModuleRequirement;importspoon.reflect.declaration
. CtMultiTypedElement;importspoon.reflect.declaration
. CtNamedElement;importspoon.reflect.declaration
. CtPackage;importspoon.reflect.declaration
. CtPackageDeclaration;importspoon.reflect.declaration
. CtParameter;importspoon.reflect.declaration
. CtShadowable;importspoon.reflect.declaration
. CtType;importspoon.reflect.declaration
. CtTypeInformation;importspoon.reflect.declaration
. CtTypeMember;importspoon.reflect.declaration
. CtTypeParameter;importspoon.reflect.declaration
. CtTypedElement;importspoon.reflect.declaration
. CtUsedService;importspoon.reflect.declaration
. CtVariable;importspoon.reflect.reference
. CtActualTypeContainer;importspoon.reflect.reference
. CtArrayTypeReference;importspoon.reflect.reference
. CtCatchVariableReference;importspoon.reflect.reference
. CtExecutableReference;importspoon.reflect.reference
. CtFieldReference;importspoon.reflect.declaration
. CtImport;importspoon.reflect.reference
. CtIntersectionTypeReference;importspoon.reflect.reference
. CtLocalVariableReference;importspoon.reflect.reference
. CtModuleReference;importspoon.reflect.reference
. CtPackageReference;importspoon.reflect.reference
. CtParameterReference;importspoon.reflect.reference
. CtReference;importspoon.reflect.reference
. CtTypeParameterReference;importspoon.reflect.reference
. CtTypeReference;importspoon.reflect.reference
. CtUnboundVariableReference;importspoon.reflect.reference
. CtVariableReference;importspoon.reflect.reference
. CtWildcardReference;importspoon.reflect.reference

. CtTypeMemberWildcardImportReference;importjava.lang.annotation
. Annotation;importjava.util

.
Collection ; /**
 * This class provides an abstract implementation of the visitor that allows its
 * subclasses to scans the metamodel elements by recursively using their
 * (abstract) supertype scanning methods. It declares a scan method for each
 * abstract element of the AST and a visit method for each element of the AST.
 */ public abstract class CtInheritanceScanner

	implements
	CtVisitor {/**
	 * Default constructor.
	 */public CtInheritanceScanner
	(

	) {}public < T>
			voidvisitCtCodeSnippetExpression(CtCodeSnippetExpression <T >
		e){scanCtCodeSnippet(
		e);scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ; }publicvoid visitCtCodeSnippetStatement( CtCodeSnippetStatement
		e){scanCtCodeSnippet(
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	)
	; } /**
	 * Generically scans a collection of meta-model elements.
	 */publicvoidscan( Collection <? extendsCtElement >
		elements ){ if (elements !=
			null ){ for ( CtElemente :
				elements){scan(
			e
		)
	;

	}
	} } /**
	 * Generically scans a meta-model element.
	 */publicvoid scan( CtElement
		element ){ if (element !=
			null){element.accept(
		this
	)

	;
	} }/**
	 * Scans an abstract invocation.
	 */public < T>voidscanCtAbstractInvocation(CtAbstractInvocation <T >
	a

	)
	{ } /**
	 * Scans an abstract control flow break.
	 */publicvoid scanCtCFlowBreak( CtCFlowBreak
	flowBreak

	)
	{ } /**
	 * Scans a labelled control flow break.
	 */publicvoid scanCtLabelledFlowBreak( CtLabelledFlowBreak
	labelledFlowBreak

	)
	{ } /**
	 * Scans an abstract code element.
	 */publicvoid scanCtCodeElement( CtCodeElement

	e

	) { }publicvoid scanCtTypeMember( CtTypeMember
	e

	) { }publicvoid scanCtModuleDirective( CtModuleDirective

	e

	)
	{ } /**
	 * Scans an abstract element.
	 */publicvoid scanCtElement( CtElement
	e

	)
	{ }/**
	 * Scans an abstract executable.
	 */public < R>voidscanCtExecutable(CtExecutable <R >
	e

	)
	{ }/**
	 * Scans an abstract expression.
	 */public < T>voidscanCtExpression(CtExpression <T >
	expression

	)
	{ } /**
	 * Scans a formal type declarer.
	 */publicvoid scanCtFormalTypeDeclarer( CtFormalTypeDeclarer

	e

	) { }publicvoid scanCtVisitable( CtVisitable

	e

	)
	{ } /**
	 * Scans an actual type container..
	 */publicvoid scanCtActualTypeContainer( CtActualTypeContainer
	reference

	)
	{ } /**
	 * Scans an abstract loop.
	 */publicvoid scanCtLoop( CtLoop

	loop

	)
	{ } /**
	 * Scans an abstract modifiable element.
	 */publicvoid scanCtModifiable( CtModifiable

	m

	)
	{ } /**
	 * Scans an abstract named element.
	 */publicvoid scanCtNamedElement( CtNamedElement
	e

	)
	{ } /**
	 * Scans an abstract reference.
	 */publicvoid scanCtReference( CtReference

	reference

	)
	{ } /**
	 * Scans an abstract statement.
	 */publicvoid scanCtStatement( CtStatement
	s

	)
	{ }/**
	 * Scans an abstract targeted expression.
	 */public < T ,EextendsCtExpression< ? >>
			voidscanCtTargetedExpression(CtTargetedExpression <T ,E >
	targetedExpression

	)
	{ }/**
	 * Scans an abstract type.
	 */public < T>voidscanCtType(CtType <T >
	type

	)
	{ }/**
	 * Scans an abstract typed element.
	 */public < T>voidscanCtTypedElement(CtTypedElement <T >
	e

	)
	{ }/**
	 * Scans an abstract variable declaration.
	 */public < T>voidscanCtVariable(CtVariable <T >
	v


	)
	{ }/**
	 * Scans an array access (read and write).
	 */public < T ,EextendsCtExpression< ? >>voidscanCtArrayAccess(CtArrayAccess <T ,E >
	arrayAccess

	)
	{ }/**
	 * Scans a field access (read and write).
	 */public < T>voidscanCtFieldAccess(CtFieldAccess <T >
	fieldAccess

	)
	{ }/**
	 * Scans a variable access (read and write).
	 */public < T>voidscanCtVariableAccess(CtVariableAccess <T >
	variableAccess

	)
	{ }/**
	 * Scans the right-hand side of an assignment
	 */public < T>voidscanCtRHSReceiver(CtRHSReceiver <T >
	ctRHSReceiver

	)
	{ } /**
	 * Scans a shadowable element
	 */publicvoid scanCtShadowable( CtShadowable
	ctShadowable

	)
	{ } /**
	 * Scans a body holder
	 */publicvoid scanCtBodyHolder( CtBodyHolder
	ctBodyHolder

	){
	} @Overridepublic < T>voidvisitCtFieldRead(CtFieldRead <T >
		fieldRead){visitCtVariableRead(
		fieldRead);scanCtFieldAccess(
		fieldRead);scanCtTargetedExpression(
	fieldRead

	);
	} @Overridepublic < T>voidvisitCtFieldWrite(CtFieldWrite <T >
		fieldWrite){visitCtVariableWrite(
		fieldWrite);scanCtFieldAccess(
		fieldWrite);scanCtTargetedExpression(
	fieldWrite

	) ;}public < T>voidvisitCtSuperAccess(CtSuperAccess <T >
		f){visitCtVariableRead(
		f);scanCtTargetedExpression(
	f

	) ; }publicvoid scanCtMultiTypedElement( CtMultiTypedElement
	f

	) {}public < T ,A extends T>
			voidvisitCtOperatorAssignment(CtOperatorAssignment <T ,A >
		e){visitCtAssignment(
	e

	)
	; }/**
	 * Scans an abstract variable reference.
	 */public < T>voidscanCtVariableReference(CtVariableReference <T >
	reference

	)
	{ }/**
	 * Scans an abstract variable reference.
	 */public < T>void scanCtTypeInformation( CtTypeInformation
	typeInfo

	) {} public <A extends Annotation>
			voidvisitCtAnnotation(CtAnnotation <A >
		e){scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtShadowable(
	e

	) ;} public <A extends Annotation>voidvisitCtAnnotationType(CtAnnotationType <A >
		e){scanCtType(
		e);scanCtNamedElement(
		e);scanCtTypeInformation(
		e);scanCtTypeMember(
		e);scanCtFormalTypeDeclarer(
		e);scanCtModifiable(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtShadowable(
	e

	) ; }publicvoid visitCtAnonymousExecutable( CtAnonymousExecutable
		e){scanCtExecutable(
		e);scanCtNamedElement(
		e);scanCtTypedElement(
		e);scanCtTypeMember(
		e);scanCtModifiable(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
	e

	);
	} @Overridepublic < T>voidvisitCtArrayRead(CtArrayRead <T >
		arrayRead){scanCtArrayAccess(
		arrayRead);scanCtTargetedExpression(
		arrayRead);scanCtExpression(
		arrayRead);scanCtCodeElement(
		arrayRead);scanCtTypedElement(
		arrayRead);scanCtElement(
		arrayRead);scanCtVisitable(
	arrayRead

	);
	} @Overridepublic < T>voidvisitCtArrayWrite(CtArrayWrite <T >
		arrayWrite){scanCtArrayAccess(
		arrayWrite);scanCtTargetedExpression(
		arrayWrite);scanCtExpression(
		arrayWrite);scanCtCodeElement(
		arrayWrite);scanCtTypedElement(
		arrayWrite);scanCtElement(
		arrayWrite);scanCtVisitable(
	arrayWrite

	) ;}public < T>voidvisitCtArrayTypeReference(CtArrayTypeReference <T >
		e){visitCtTypeReference(
	e

	) ;}public < T>voidvisitCtAssert(CtAssert <T >
		e){scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < T ,A extends T>
			voidvisitCtAssignment(CtAssignment <T ,A >
		e){scanCtStatement(
		e);scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtRHSReceiver(
	e

	) ;}public < T>voidvisitCtBinaryOperator(CtBinaryOperator <T >
		e){scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < R>voidvisitCtBlock(CtBlock <R >
		e){scanCtStatement(
		e);visitCtStatementList(
	e

	) ; }publicvoid visitCtBreak( CtBreak
		e){scanCtLabelledFlowBreak(
		e);scanCtCFlowBreak(
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < E>voidvisitCtCase(CtCase <E >
		e){scanCtStatement(
		e);visitCtStatementList(
	e

	) ; }publicvoid visitCtCatch( CtCatch
		e){scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
	e

	) ;}public < T>voidvisitCtClass(CtClass <T >
		e){scanCtType(
		e);scanCtStatement(
		e);scanCtTypeInformation(
		e);scanCtFormalTypeDeclarer(
		e);scanCtCodeElement(
		e);scanCtNamedElement(
		e);scanCtTypeMember(
		e);scanCtElement(
		e);scanCtModifiable(
		e);scanCtVisitable(
		e);scanCtShadowable(
	e

	);
	} @ Overridepublicvoid visitCtTypeParameter( CtTypeParameter
		typeParameter){scanCtType(
		typeParameter);scanCtTypeInformation(
		typeParameter);scanCtFormalTypeDeclarer(
		typeParameter);scanCtNamedElement(
		typeParameter);scanCtTypeMember(
		typeParameter);scanCtElement(
		typeParameter);scanCtModifiable(
		typeParameter);scanCtVisitable(
		typeParameter);scanCtShadowable(
	typeParameter

	) ;}public < T>voidvisitCtConditional(CtConditional <T >
		e){scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < T>voidvisitCtConstructor(CtConstructor <T >
		e){scanCtExecutable(
		e);scanCtNamedElement(
		e);scanCtFormalTypeDeclarer(
		e);scanCtTypedElement(
		e);scanCtTypeMember(
		e);scanCtModifiable(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtShadowable(
		e);scanCtBodyHolder(
	e

	) ; }publicvoid visitCtContinue( CtContinue
		e){scanCtLabelledFlowBreak(
		e);scanCtCFlowBreak(
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ; }publicvoid visitCtDo( CtDo
		e){scanCtLoop(
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
	e

	) ;} public <TextendsEnum< ? >>voidvisitCtEnum(CtEnum <T >
		e){visitCtClass(
	e

	) ;}public < T>voidvisitCtExecutableReference(CtExecutableReference <T >
		e){scanCtReference(
		e);scanCtElement(
		e);scanCtActualTypeContainer(
		e);scanCtVisitable(
	e

	) ;}public < T>voidvisitCtField(CtField <T >
		e){scanCtNamedElement(
		e);scanCtVariable(
		e);scanCtTypeMember(
		e);scanCtModifiable(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtRHSReceiver(
		e);scanCtShadowable(
	e

	);
	} @Overridepublic < T>voidvisitCtEnumValue(CtEnumValue <T >
		enumValue){visitCtField(
	enumValue

	) ;}public < T>voidvisitCtThisAccess(CtThisAccess <T >
		e){scanCtTargetedExpression(
		e);scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < T>voidvisitCtFieldReference(CtFieldReference <T >
		e){scanCtVariableReference(
		e);scanCtReference(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ; }publicvoid visitCtFor( CtFor
		e){scanCtLoop(
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
	e

	) ; }publicvoid visitCtForEach( CtForEach
		e){scanCtLoop(
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
	e

	) ; }publicvoid visitCtIf( CtIf
		e){scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < T>voidvisitCtInterface(CtInterface <T >
		e){scanCtType(
		e);scanCtTypeInformation(
		e);scanCtFormalTypeDeclarer(
		e);scanCtNamedElement(
		e);scanCtTypeMember(
		e);scanCtElement(
		e);scanCtModifiable(
		e);scanCtVisitable(
		e);scanCtShadowable(
	e

	) ;}public < T>voidvisitCtInvocation(CtInvocation <T >
		e){scanCtAbstractInvocation(
		e);scanCtStatement(
		e);scanCtActualTypeContainer(
		e);scanCtTargetedExpression(
		e);scanCtElement(
		e);scanCtCodeElement(
		e);scanCtExpression(
		e);scanCtVisitable(
		e);scanCtTypedElement(
	e

	) ;}public < T>voidvisitCtLiteral(CtLiteral <T >
		e){scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < T>voidvisitCtLocalVariable(CtLocalVariable <T >
		e){scanCtStatement(
		e);scanCtVariable(
		e);scanCtCodeElement(
		e);scanCtNamedElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtModifiable(
		e);scanCtVisitable(
		e);scanCtRHSReceiver(
	e

	) ;}public < T>
			voidvisitCtLocalVariableReference(CtLocalVariableReference <T >
		e){scanCtVariableReference(
		e);scanCtReference(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < T>voidvisitCtCatchVariable(CtCatchVariable <T >
		e){scanCtVariable(
		e);scanCtMultiTypedElement(
		e);scanCtCodeElement(
		e);scanCtNamedElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtModifiable(
		e);scanCtVisitable(
	e

	) ;}public < T>voidvisitCtCatchVariableReference(CtCatchVariableReference <T >
		e){scanCtVariableReference(
		e);scanCtReference(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < T>voidvisitCtMethod(CtMethod <T >
		e){scanCtExecutable(
		e);scanCtTypedElement(
		e);scanCtNamedElement(
		e);scanCtFormalTypeDeclarer(
		e);scanCtTypeMember(
		e);scanCtElement(
		e);scanCtModifiable(
		e);scanCtVisitable(
		e);scanCtShadowable(
		e);scanCtBodyHolder(
	e

	);
	} @Overridepublic < T>voidvisitCtAnnotationMethod(CtAnnotationMethod <T >
		annotationMethod){visitCtMethod(
	annotationMethod

	) ;}public < T>voidvisitCtNewArray(CtNewArray <T >
		e){scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	);
	} @Overridepublic < T>voidvisitCtConstructorCall(CtConstructorCall <T >
		e){scanCtTargetedExpression(
		e);scanCtAbstractInvocation(
		e);scanCtStatement(
		e);scanCtActualTypeContainer(
		e);scanCtExpression(
		e);scanCtElement(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtVisitable(
	e

	) ;}public < T>voidvisitCtNewClass(CtNewClass <T >
		e){visitCtConstructorCall(
	e

	);
	} @Overridepublic < T>voidvisitCtLambda(CtLambda <T >
		e){scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtExecutable(
		e);scanCtNamedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
	e

	);
	} @Overridepublic < T ,EextendsCtExpression< ? >>
			voidvisitCtExecutableReferenceExpression(CtExecutableReferenceExpression <T ,E >
		e){scanCtTargetedExpression(
		e);scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < T ,A extends T>
			voidvisitCtOperatorAssignement(CtOperatorAssignment <T ,A >
	assignment

	) { }publicvoid visitCtPackage( CtPackage
		e){scanCtNamedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtShadowable(
	e

	) ; }publicvoid visitCtPackageReference( CtPackageReference
		e){scanCtReference(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < T>voidvisitCtParameter(CtParameter <T >
		e){scanCtNamedElement(
		e);scanCtVariable(
		e);scanCtModifiable(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtShadowable(
	e

	) ;}public < T>voidvisitCtParameterReference(CtParameterReference <T >
		e){scanCtVariableReference(
		e);scanCtReference(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < R>voidvisitCtReturn(CtReturn <R >
		e){scanCtCFlowBreak(
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < R>void visitCtStatementList( CtStatementList
		e){scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < E>voidvisitCtSwitch(CtSwitch <E >
		e){scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ; }publicvoid visitCtSynchronized( CtSynchronized
		e){scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ; }publicvoid visitCtThrow( CtThrow
		e){scanCtCFlowBreak(
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ; }publicvoid visitCtTry( CtTry
		e){scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
	e

	);
	} @ Overridepublicvoid visitCtTryWithResource( CtTryWithResource
		e){visitCtTry(
	e

	) ; }publicvoid visitCtTypeParameterReference( CtTypeParameterReference
		e){visitCtTypeReference(
	e

	);
	} @ Overridepublicvoid visitCtWildcardReference( CtWildcardReference
		wildcardReference){visitCtTypeParameterReference(
	wildcardReference

	);
	} @Overridepublic < T>voidvisitCtIntersectionTypeReference(CtIntersectionTypeReference <T >
		e){visitCtTypeReference(
	e

	) ;}public < T>voidvisitCtTypeReference(CtTypeReference <T >
		e){scanCtReference(
		e);scanCtTypeInformation(
		e);scanCtActualTypeContainer(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtShadowable(
	e

	);
	} @Overridepublic < T>voidvisitCtTypeAccess(CtTypeAccess <T >
		e){scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < T>voidvisitCtUnaryOperator(CtUnaryOperator <T >
		e){scanCtExpression(
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	);
	} @Overridepublic < T>voidvisitCtVariableRead(CtVariableRead <T >
		e){scanCtVariableAccess(
		e);scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	);
	} @Overridepublic < T>voidvisitCtVariableWrite(CtVariableWrite <T >
		e){scanCtVariableAccess(
		e);scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
	e

	);
	} @ Overridepublicvoid visitCtComment( CtComment
		e){scanCtElement(
		e);scanCtVisitable(
		e);scanCtStatement(
		e);scanCtCodeElement(
	e

	);
	} @ Overridepublicvoid visitCtJavaDoc( CtJavaDoc
		e){visitCtComment(
	e

	);
	} @ Overridepublicvoid visitCtJavaDocTag( CtJavaDocTag
		e){scanCtElement(
		e);scanCtVisitable(
	e

	) ;}public < T>
			voidvisitCtAnnotationFieldAccess(CtAnnotationFieldAccess <T >
		e){visitCtVariableRead(
		e);scanCtTargetedExpression(
	e

	) ; }publicvoid visitCtWhile( CtWhile
		e){scanCtLoop(
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
	e

	) ;}public < T>voidvisitCtUnboundVariableReference(CtUnboundVariableReference <T >
		reference){scanCtVariableReference(
		reference);scanCtReference(
		reference);scanCtElement(
		reference);scanCtVisitable(
	reference

	) ; }publicvoid scanCtCodeSnippet( CtCodeSnippet
	snippet

	){
	} @ Overridepublicvoid visitCtImport( CtImport
		ctImport){scanCtElement(
		ctImport);scanCtVisitable(
	ctImport

	);
	} @ Overridepublicvoid visitCtModule( CtModule
		module){scanCtNamedElement(
		module);scanCtVisitable(
		module);scanCtElement(
	module

	);
	} @ Overridepublicvoid visitCtModuleReference( CtModuleReference
		moduleReference){scanCtReference(
		moduleReference);scanCtElement(
		moduleReference);scanCtVisitable(
	moduleReference

	);
	} @ Overridepublicvoid visitCtPackageExport( CtPackageExport
		moduleExport){scanCtElement(
		moduleExport);scanCtVisitable(
		moduleExport);scanCtModuleDirective(
	moduleExport

	);
	} @ Overridepublicvoid visitCtModuleRequirement( CtModuleRequirement
		moduleRequirement){scanCtElement(
		moduleRequirement);scanCtVisitable(
		moduleRequirement);scanCtModuleDirective(
	moduleRequirement

	);
	} @ Overridepublicvoid visitCtProvidedService( CtProvidedService
		moduleProvidedService){scanCtElement(
		moduleProvidedService);scanCtVisitable(
		moduleProvidedService);scanCtModuleDirective(
	moduleProvidedService

	);
	} @ Overridepublicvoid visitCtUsedService( CtUsedService
		usedService){scanCtElement(
		usedService);scanCtVisitable(
		usedService);scanCtModuleDirective(
	usedService

	);
	} @ Overridepublicvoid visitCtCompilationUnit( CtCompilationUnit
		compilationUnit){scanCtElement(
		compilationUnit);scanCtVisitable(
	compilationUnit

	);
	} @ Overridepublicvoid visitCtPackageDeclaration( CtPackageDeclaration
		packageDeclaration){scanCtElement(
		packageDeclaration);scanCtVisitable(
	packageDeclaration

	);
	} @ Overridepublicvoid visitCtTypeMemberWildcardImportReference( CtTypeMemberWildcardImportReference
		wildcardReference){scanCtReference(
		wildcardReference);scanCtElement(
		wildcardReference);scanCtVisitable(
	wildcardReference
)
