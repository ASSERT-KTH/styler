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
package spoon.support.visitor.java.internal;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtShadowable;importspoon
. reflect.declaration.CtType;importspoon
. reflect.declaration.CtTypeParameter;importspoon
. reflect.path.CtRole;importspoon
. reflect.reference.CtTypeReference;importjava

. lang.annotation.Annotation;importjava
. lang.reflect.GenericDeclaration;abstractclass

AbstractRuntimeBuilderContext implements RuntimeBuilderContext { protected AbstractRuntimeBuilderContext

	( CtShadowableelement) {element .
		setShadow(true);}@
	Override

	publicvoid
	addPackage ( CtPackagectPackage) {throw new
		UnsupportedOperationException ( );}@
	Override

	publicvoid
	addType ( CtType<?>aType) {throw new
		UnsupportedOperationException ( );}@
	Override

	publicvoid
	addAnnotation ( CtAnnotation<Annotation>ctAnnotation) {throw new
		UnsupportedOperationException ( );}@
	Override

	publicvoid
	addConstructor ( CtConstructor<?>ctConstructor) {throw new
		UnsupportedOperationException ( );}@
	Override

	publicvoid
	addMethod ( CtMethod<?>ctMethod) {throw new
		UnsupportedOperationException ( );}@
	Override

	publicvoid
	addField ( CtField<?>ctField) {throw new
		UnsupportedOperationException ( );}@
	Override

	publicvoid
	addEnumValue ( CtEnumValue<?>ctEnumValue) {throw new
		UnsupportedOperationException ( );}@
	Override

	publicvoid
	addParameter ( CtParameterctParameter) {throw new
		UnsupportedOperationException ( );}@
	Override

	publicvoid
	addFormalType ( CtTypeParameterparameterRef) {throw new
		UnsupportedOperationException ( );}@
	Override

	publicvoid
	addTypeReference ( CtRolerole, CtTypeReference< ?>ctTypeReference) {throw new
		UnsupportedOperationException ( );}@
	Override

	publicCtTypeParameter
	getTypeParameter ( GenericDeclarationgenericDeclaration, Stringstring ) {return null
		; }}
	