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
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration
. CtTypeParameter;importspoon.reflect.path
. CtRole;importspoon.reflect.reference

. CtTypeReference;importjava.lang.annotation
. Annotation;importjava.lang.reflect

. GenericDeclaration ; abstract class AbstractRuntimeBuilderContext

	implements RuntimeBuilderContext{protected AbstractRuntimeBuilderContext( CtShadowable
		element){element.setShadow(
	true

	);
	} @ Overridepublicvoid addPackage( CtPackage
		ctPackage ) {thrownewUnsupportedOperationException
	(

	);
	} @ OverridepublicvoidaddType(CtType <? >
		aType ) {thrownewUnsupportedOperationException
	(

	);
	} @ OverridepublicvoidaddAnnotation(CtAnnotation <Annotation >
		ctAnnotation ) {thrownewUnsupportedOperationException
	(

	);
	} @ OverridepublicvoidaddConstructor(CtConstructor <? >
		ctConstructor ) {thrownewUnsupportedOperationException
	(

	);
	} @ OverridepublicvoidaddMethod(CtMethod <? >
		ctMethod ) {thrownewUnsupportedOperationException
	(

	);
	} @ OverridepublicvoidaddField(CtField <? >
		ctField ) {thrownewUnsupportedOperationException
	(

	);
	} @ OverridepublicvoidaddEnumValue(CtEnumValue <? >
		ctEnumValue ) {thrownewUnsupportedOperationException
	(

	);
	} @ Overridepublicvoid addParameter( CtParameter
		ctParameter ) {thrownewUnsupportedOperationException
	(

	);
	} @ Overridepublicvoid addFormalType( CtTypeParameter
		parameterRef ) {thrownewUnsupportedOperationException
	(

	);
	} @ Overridepublicvoid addTypeReference( CtRolerole,CtTypeReference <? >
		ctTypeReference ) {thrownewUnsupportedOperationException
	(

	);
	} @ OverridepublicCtTypeParameter getTypeParameter( GenericDeclaration genericDeclaration, String
		string ){
	return
null
