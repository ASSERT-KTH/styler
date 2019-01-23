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
import spoon.reflect.declaration.CtMethod;importspoon
. reflect.declaration.CtModifiable;importspoon
. reflect.declaration.CtModule;importspoon
. reflect.declaration.CtModuleDirective;importspoon
. reflect.declaration.CtPackageExport;importspoon
. reflect.declaration.CtProvidedService;importspoon
. reflect.declaration.CtModuleRequirement;importspoon
. reflect.declaration.CtMultiTypedElement;importspoon
. reflect.declaration.CtNamedElement;importspoon
. reflect.declaration.CtPackage;importspoon
. reflect.declaration.CtPackageDeclaration;importspoon
. reflect.declaration.CtParameter;importspoon
. reflect.declaration.CtShadowable;importspoon
. reflect.declaration.CtType;importspoon
. reflect.declaration.CtTypeInformation;importspoon
. reflect.declaration.CtTypeMember;importspoon
. reflect.declaration.CtTypeParameter;importspoon
. reflect.declaration.CtTypedElement;importspoon
. reflect.declaration.CtUsedService;importspoon
. reflect.declaration.CtVariable;importspoon
. reflect.reference.CtActualTypeContainer;importspoon
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
. reflect.reference.CtTypeMemberWildcardImportReference;importjava

. lang.annotation.Annotation;importjava
. util.Collection;/**
 * This class provides an abstract implementation of the visitor that allows its
 * subclasses to scans the metamodel elements by recursively using their
 * (abstract) supertype scanning methods. It declares a scan method for each
 * abstract element of the AST and a visit method for each element of the AST.
 */public

abstract
class CtInheritanceScanner implements CtVisitor { /**
	 * Default constructor.
	 */ public

	CtInheritanceScanner
	( ){} public
	<

	T >voidvisitCtCodeSnippetExpression ( CtCodeSnippetExpression<
			T>e) {scanCtCodeSnippet (
		e);scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	void

	visitCtCodeSnippetStatement ( CtCodeSnippetStatemente) {scanCtCodeSnippet (
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}/**
	 * Generically scans a collection of meta-model elements.
	 */
	public

	void
	scan ( Collection<?extendsCtElement > elements) {if (
		elements !=null ) {for (
			CtElement e: elements ) {scan (
				e);}}
			}
		/**
	 * Generically scans a meta-model element.
	 */
	public

	void
	scan ( CtElementelement) {if (
		element !=null ) {element .
			accept(this);}}
		/**
	 * Scans an abstract invocation.
	 */
	public

	<
	T >voidscanCtAbstractInvocation ( CtAbstractInvocation<T>a) {} /**
	 * Scans an abstract control flow break.
	 */
	public

	void
	scanCtCFlowBreak ( CtCFlowBreakflowBreak) {} /**
	 * Scans a labelled control flow break.
	 */
	public

	void
	scanCtLabelledFlowBreak ( CtLabelledFlowBreaklabelledFlowBreak) {} /**
	 * Scans an abstract code element.
	 */
	public

	void
	scanCtCodeElement ( CtCodeElemente) {} public

	void

	scanCtTypeMember ( CtTypeMembere) {} public
	void

	scanCtModuleDirective ( CtModuleDirectivee) {} /**
	 * Scans an abstract element.
	 */

	public

	void
	scanCtElement ( CtElemente) {} /**
	 * Scans an abstract executable.
	 */
	public

	<
	R >voidscanCtExecutable ( CtExecutable<R>e) {} /**
	 * Scans an abstract expression.
	 */
	public

	<
	T >voidscanCtExpression ( CtExpression<T>expression) {} /**
	 * Scans a formal type declarer.
	 */
	public

	void
	scanCtFormalTypeDeclarer ( CtFormalTypeDeclarere) {} public

	void

	scanCtVisitable ( CtVisitablee) {} /**
	 * Scans an actual type container..
	 */

	public

	void
	scanCtActualTypeContainer ( CtActualTypeContainerreference) {} /**
	 * Scans an abstract loop.
	 */
	public

	void
	scanCtLoop ( CtLooploop) {} /**
	 * Scans an abstract modifiable element.
	 */

	public

	void
	scanCtModifiable ( CtModifiablem) {} /**
	 * Scans an abstract named element.
	 */

	public

	void
	scanCtNamedElement ( CtNamedElemente) {} /**
	 * Scans an abstract reference.
	 */
	public

	void
	scanCtReference ( CtReferencereference) {} /**
	 * Scans an abstract statement.
	 */

	public

	void
	scanCtStatement ( CtStatements) {} /**
	 * Scans an abstract targeted expression.
	 */
	public

	<
	T ,Eextends CtExpression < ?>>voidscanCtTargetedExpression ( CtTargetedExpression<
			T,E> targetedExpression) {} /**
	 * Scans an abstract type.
	 */
	public

	<
	T >voidscanCtType ( CtType<T>type) {} /**
	 * Scans an abstract typed element.
	 */
	public

	<
	T >voidscanCtTypedElement ( CtTypedElement<T>e) {} /**
	 * Scans an abstract variable declaration.
	 */
	public

	<
	T >voidscanCtVariable ( CtVariable<T>v) {} /**
	 * Scans an array access (read and write).
	 */
	public


	<
	T ,Eextends CtExpression < ?>>voidscanCtArrayAccess ( CtArrayAccess<T,E> arrayAccess) {} /**
	 * Scans a field access (read and write).
	 */
	public

	<
	T >voidscanCtFieldAccess ( CtFieldAccess<T>fieldAccess) {} /**
	 * Scans a variable access (read and write).
	 */
	public

	<
	T >voidscanCtVariableAccess ( CtVariableAccess<T>variableAccess) {} /**
	 * Scans the right-hand side of an assignment
	 */
	public

	<
	T >voidscanCtRHSReceiver ( CtRHSReceiver<T>ctRHSReceiver) {} /**
	 * Scans a shadowable element
	 */
	public

	void
	scanCtShadowable ( CtShadowablectShadowable) {} /**
	 * Scans a body holder
	 */
	public

	void
	scanCtBodyHolder ( CtBodyHolderctBodyHolder) {} @
	Override

	public<
	T >voidvisitCtFieldRead ( CtFieldRead<T>fieldRead) {visitCtVariableRead (
		fieldRead);scanCtFieldAccess(
		fieldRead);scanCtTargetedExpression(
		fieldRead);}@
	Override

	public<
	T >voidvisitCtFieldWrite ( CtFieldWrite<T>fieldWrite) {visitCtVariableWrite (
		fieldWrite);scanCtFieldAccess(
		fieldWrite);scanCtTargetedExpression(
		fieldWrite);}public
	<

	T >voidvisitCtSuperAccess ( CtSuperAccess<T>f) {visitCtVariableRead (
		f);scanCtTargetedExpression(
		f);}public
	void

	scanCtMultiTypedElement ( CtMultiTypedElementf) {} public
	<

	T ,Aextends T > voidvisitCtOperatorAssignment ( CtOperatorAssignment<
			T,A> e) {visitCtAssignment (
		e);}/**
	 * Scans an abstract variable reference.
	 */
	public

	<
	T >voidscanCtVariableReference ( CtVariableReference<T>reference) {} /**
	 * Scans an abstract variable reference.
	 */
	public

	<
	T >voidscanCtTypeInformation ( CtTypeInformationtypeInfo) {} public
	<

	A extendsAnnotation > voidvisitCtAnnotation ( CtAnnotation<
			A>e) {scanCtExpression (
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtShadowable(
		e);}public
	<

	A extendsAnnotation > voidvisitCtAnnotationType ( CtAnnotationType<A>e) {scanCtType (
		e);scanCtNamedElement(
		e);scanCtTypeInformation(
		e);scanCtTypeMember(
		e);scanCtFormalTypeDeclarer(
		e);scanCtModifiable(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtShadowable(
		e);}public
	void

	visitCtAnonymousExecutable ( CtAnonymousExecutablee) {scanCtExecutable (
		e);scanCtNamedElement(
		e);scanCtTypedElement(
		e);scanCtTypeMember(
		e);scanCtModifiable(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
		e);}@
	Override

	public<
	T >voidvisitCtArrayRead ( CtArrayRead<T>arrayRead) {scanCtArrayAccess (
		arrayRead);scanCtTargetedExpression(
		arrayRead);scanCtExpression(
		arrayRead);scanCtCodeElement(
		arrayRead);scanCtTypedElement(
		arrayRead);scanCtElement(
		arrayRead);scanCtVisitable(
		arrayRead);}@
	Override

	public<
	T >voidvisitCtArrayWrite ( CtArrayWrite<T>arrayWrite) {scanCtArrayAccess (
		arrayWrite);scanCtTargetedExpression(
		arrayWrite);scanCtExpression(
		arrayWrite);scanCtCodeElement(
		arrayWrite);scanCtTypedElement(
		arrayWrite);scanCtElement(
		arrayWrite);scanCtVisitable(
		arrayWrite);}public
	<

	T >voidvisitCtArrayTypeReference ( CtArrayTypeReference<T>e) {visitCtTypeReference (
		e);}public
	<

	T >voidvisitCtAssert ( CtAssert<T>e) {scanCtStatement (
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	T ,Aextends T > voidvisitCtAssignment ( CtAssignment<
			T,A> e) {scanCtStatement (
		e);scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtRHSReceiver(
		e);}public
	<

	T >voidvisitCtBinaryOperator ( CtBinaryOperator<T>e) {scanCtExpression (
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	R >voidvisitCtBlock ( CtBlock<R>e) {scanCtStatement (
		e);visitCtStatementList(
		e);}public
	void

	visitCtBreak ( CtBreake) {scanCtLabelledFlowBreak (
		e);scanCtCFlowBreak(
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	E >voidvisitCtCase ( CtCase<E>e) {scanCtStatement (
		e);visitCtStatementList(
		e);}public
	void

	visitCtCatch ( CtCatche) {scanCtCodeElement (
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
		e);}public
	<

	T >voidvisitCtClass ( CtClass<T>e) {scanCtType (
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
		e);}@
	Override

	publicvoid
	visitCtTypeParameter ( CtTypeParametertypeParameter) {scanCtType (
		typeParameter);scanCtTypeInformation(
		typeParameter);scanCtFormalTypeDeclarer(
		typeParameter);scanCtNamedElement(
		typeParameter);scanCtTypeMember(
		typeParameter);scanCtElement(
		typeParameter);scanCtModifiable(
		typeParameter);scanCtVisitable(
		typeParameter);scanCtShadowable(
		typeParameter);}public
	<

	T >voidvisitCtConditional ( CtConditional<T>e) {scanCtExpression (
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	T >voidvisitCtConstructor ( CtConstructor<T>e) {scanCtExecutable (
		e);scanCtNamedElement(
		e);scanCtFormalTypeDeclarer(
		e);scanCtTypedElement(
		e);scanCtTypeMember(
		e);scanCtModifiable(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtShadowable(
		e);scanCtBodyHolder(
		e);}public
	void

	visitCtContinue ( CtContinuee) {scanCtLabelledFlowBreak (
		e);scanCtCFlowBreak(
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	void

	visitCtDo ( CtDoe) {scanCtLoop (
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
		e);}public
	<

	T extendsEnum < ?>>voidvisitCtEnum ( CtEnum<T>e) {visitCtClass (
		e);}public
	<

	T >voidvisitCtExecutableReference ( CtExecutableReference<T>e) {scanCtReference (
		e);scanCtElement(
		e);scanCtActualTypeContainer(
		e);scanCtVisitable(
		e);}public
	<

	T >voidvisitCtField ( CtField<T>e) {scanCtNamedElement (
		e);scanCtVariable(
		e);scanCtTypeMember(
		e);scanCtModifiable(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtRHSReceiver(
		e);scanCtShadowable(
		e);}@
	Override

	public<
	T >voidvisitCtEnumValue ( CtEnumValue<T>enumValue) {visitCtField (
		enumValue);}public
	<

	T >voidvisitCtThisAccess ( CtThisAccess<T>e) {scanCtTargetedExpression (
		e);scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	T >voidvisitCtFieldReference ( CtFieldReference<T>e) {scanCtVariableReference (
		e);scanCtReference(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	void

	visitCtFor ( CtFore) {scanCtLoop (
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
		e);}public
	void

	visitCtForEach ( CtForEache) {scanCtLoop (
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
		e);}public
	void

	visitCtIf ( CtIfe) {scanCtStatement (
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	T >voidvisitCtInterface ( CtInterface<T>e) {scanCtType (
		e);scanCtTypeInformation(
		e);scanCtFormalTypeDeclarer(
		e);scanCtNamedElement(
		e);scanCtTypeMember(
		e);scanCtElement(
		e);scanCtModifiable(
		e);scanCtVisitable(
		e);scanCtShadowable(
		e);}public
	<

	T >voidvisitCtInvocation ( CtInvocation<T>e) {scanCtAbstractInvocation (
		e);scanCtStatement(
		e);scanCtActualTypeContainer(
		e);scanCtTargetedExpression(
		e);scanCtElement(
		e);scanCtCodeElement(
		e);scanCtExpression(
		e);scanCtVisitable(
		e);scanCtTypedElement(
		e);}public
	<

	T >voidvisitCtLiteral ( CtLiteral<T>e) {scanCtExpression (
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	T >voidvisitCtLocalVariable ( CtLocalVariable<T>e) {scanCtStatement (
		e);scanCtVariable(
		e);scanCtCodeElement(
		e);scanCtNamedElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtModifiable(
		e);scanCtVisitable(
		e);scanCtRHSReceiver(
		e);}public
	<

	T >voidvisitCtLocalVariableReference ( CtLocalVariableReference<
			T>e) {scanCtVariableReference (
		e);scanCtReference(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	T >voidvisitCtCatchVariable ( CtCatchVariable<T>e) {scanCtVariable (
		e);scanCtMultiTypedElement(
		e);scanCtCodeElement(
		e);scanCtNamedElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtModifiable(
		e);scanCtVisitable(
		e);}public
	<

	T >voidvisitCtCatchVariableReference ( CtCatchVariableReference<T>e) {scanCtVariableReference (
		e);scanCtReference(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	T >voidvisitCtMethod ( CtMethod<T>e) {scanCtExecutable (
		e);scanCtTypedElement(
		e);scanCtNamedElement(
		e);scanCtFormalTypeDeclarer(
		e);scanCtTypeMember(
		e);scanCtElement(
		e);scanCtModifiable(
		e);scanCtVisitable(
		e);scanCtShadowable(
		e);scanCtBodyHolder(
		e);}@
	Override

	public<
	T >voidvisitCtAnnotationMethod ( CtAnnotationMethod<T>annotationMethod) {visitCtMethod (
		annotationMethod);}public
	<

	T >voidvisitCtNewArray ( CtNewArray<T>e) {scanCtExpression (
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}@
	Override

	public<
	T >voidvisitCtConstructorCall ( CtConstructorCall<T>e) {scanCtTargetedExpression (
		e);scanCtAbstractInvocation(
		e);scanCtStatement(
		e);scanCtActualTypeContainer(
		e);scanCtExpression(
		e);scanCtElement(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtVisitable(
		e);}public
	<

	T >voidvisitCtNewClass ( CtNewClass<T>e) {visitCtConstructorCall (
		e);}@
	Override

	public<
	T >voidvisitCtLambda ( CtLambda<T>e) {scanCtExpression (
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtExecutable(
		e);scanCtNamedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
		e);}@
	Override

	public<
	T ,Eextends CtExpression < ?>>voidvisitCtExecutableReferenceExpression ( CtExecutableReferenceExpression<
			T,E> e) {scanCtTargetedExpression (
		e);scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	T ,Aextends T > voidvisitCtOperatorAssignement ( CtOperatorAssignment<
			T,A> assignment) {} public
	void

	visitCtPackage ( CtPackagee) {scanCtNamedElement (
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtShadowable(
		e);}public
	void

	visitCtPackageReference ( CtPackageReferencee) {scanCtReference (
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	T >voidvisitCtParameter ( CtParameter<T>e) {scanCtNamedElement (
		e);scanCtVariable(
		e);scanCtModifiable(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtShadowable(
		e);}public
	<

	T >voidvisitCtParameterReference ( CtParameterReference<T>e) {scanCtVariableReference (
		e);scanCtReference(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	R >voidvisitCtReturn ( CtReturn<R>e) {scanCtCFlowBreak (
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	R >voidvisitCtStatementList ( CtStatementListe) {scanCtCodeElement (
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	E >voidvisitCtSwitch ( CtSwitch<E>e) {scanCtStatement (
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	void

	visitCtSynchronized ( CtSynchronizede) {scanCtStatement (
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	void

	visitCtThrow ( CtThrowe) {scanCtCFlowBreak (
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	void

	visitCtTry ( CtTrye) {scanCtStatement (
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
		e);}@
	Override

	publicvoid
	visitCtTryWithResource ( CtTryWithResourcee) {visitCtTry (
		e);}public
	void

	visitCtTypeParameterReference ( CtTypeParameterReferencee) {visitCtTypeReference (
		e);}@
	Override

	publicvoid
	visitCtWildcardReference ( CtWildcardReferencewildcardReference) {visitCtTypeParameterReference (
		wildcardReference);}@
	Override

	public<
	T >voidvisitCtIntersectionTypeReference ( CtIntersectionTypeReference<T>e) {visitCtTypeReference (
		e);}public
	<

	T >voidvisitCtTypeReference ( CtTypeReference<T>e) {scanCtReference (
		e);scanCtTypeInformation(
		e);scanCtActualTypeContainer(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtShadowable(
		e);}@
	Override

	public<
	T >voidvisitCtTypeAccess ( CtTypeAccess<T>e) {scanCtExpression (
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}public
	<

	T >voidvisitCtUnaryOperator ( CtUnaryOperator<T>e) {scanCtExpression (
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}@
	Override

	public<
	T >voidvisitCtVariableRead ( CtVariableRead<T>e) {scanCtVariableAccess (
		e);scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}@
	Override

	public<
	T >voidvisitCtVariableWrite ( CtVariableWrite<T>e) {scanCtVariableAccess (
		e);scanCtExpression(
		e);scanCtCodeElement(
		e);scanCtTypedElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);}@
	Override

	publicvoid
	visitCtComment ( CtCommente) {scanCtElement (
		e);scanCtVisitable(
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);}@
	Override

	publicvoid
	visitCtJavaDoc ( CtJavaDoce) {visitCtComment (
		e);}@
	Override

	publicvoid
	visitCtJavaDocTag ( CtJavaDocTage) {scanCtElement (
		e);scanCtVisitable(
		e);}public
	<

	T >voidvisitCtAnnotationFieldAccess ( CtAnnotationFieldAccess<
			T>e) {visitCtVariableRead (
		e);scanCtTargetedExpression(
		e);}public
	void

	visitCtWhile ( CtWhilee) {scanCtLoop (
		e);scanCtStatement(
		e);scanCtCodeElement(
		e);scanCtElement(
		e);scanCtVisitable(
		e);scanCtBodyHolder(
		e);}public
	<

	T >voidvisitCtUnboundVariableReference ( CtUnboundVariableReference<T>reference) {scanCtVariableReference (
		reference);scanCtReference(
		reference);scanCtElement(
		reference);scanCtVisitable(
		reference);}public
	void

	scanCtCodeSnippet ( CtCodeSnippetsnippet) {} @
	Override

	publicvoid
	visitCtImport ( CtImportctImport) {scanCtElement (
		ctImport);scanCtVisitable(
		ctImport);}@
	Override

	publicvoid
	visitCtModule ( CtModulemodule) {scanCtNamedElement (
		module);scanCtVisitable(
		module);scanCtElement(
		module);}@
	Override

	publicvoid
	visitCtModuleReference ( CtModuleReferencemoduleReference) {scanCtReference (
		moduleReference);scanCtElement(
		moduleReference);scanCtVisitable(
		moduleReference);}@
	Override

	publicvoid
	visitCtPackageExport ( CtPackageExportmoduleExport) {scanCtElement (
		moduleExport);scanCtVisitable(
		moduleExport);scanCtModuleDirective(
		moduleExport);}@
	Override

	publicvoid
	visitCtModuleRequirement ( CtModuleRequirementmoduleRequirement) {scanCtElement (
		moduleRequirement);scanCtVisitable(
		moduleRequirement);scanCtModuleDirective(
		moduleRequirement);}@
	Override

	publicvoid
	visitCtProvidedService ( CtProvidedServicemoduleProvidedService) {scanCtElement (
		moduleProvidedService);scanCtVisitable(
		moduleProvidedService);scanCtModuleDirective(
		moduleProvidedService);}@
	Override

	publicvoid
	visitCtUsedService ( CtUsedServiceusedService) {scanCtElement (
		usedService);scanCtVisitable(
		usedService);scanCtModuleDirective(
		usedService);}@
	Override

	publicvoid
	visitCtCompilationUnit ( CtCompilationUnitcompilationUnit) {scanCtElement (
		compilationUnit);scanCtVisitable(
		compilationUnit);}@
	Override

	publicvoid
	visitCtPackageDeclaration ( CtPackageDeclarationpackageDeclaration) {scanCtElement (
		packageDeclaration);scanCtVisitable(
		packageDeclaration);}@
	Override

	publicvoid
	visitCtTypeMemberWildcardImportReference ( CtTypeMemberWildcardImportReferencewildcardReference) {scanCtReference (
		wildcardReference);scanCtElement(
		wildcardReference);scanCtVisitable(
		wildcardReference);}}
	