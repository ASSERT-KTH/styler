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
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.visitor.GenericTypeAdapter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static spoon.reflect.path.CtRole.SUPER_TYPE;

public class CtTypeParameterImpl extends CtTypeImpl<Object> implements CtTypeParameter {
	@MetamodelPropertyField(role = SUPER_TYPE)
	CtTypeReference<?> superClass;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtTypeParameter(this);
	}

	@Override
	public CtTypeReference<?> getSuperclass() {
		return superClass;
	}

	@Override
	public <C extends CtType<Object>> C setSuperclass(CtTypeReference<?> superClass) {
		if (superClass

		!= null
			) {superClass . setParent( this
				);}getFactory().
			getEnvironment
			().getModelChangeListener().onObjectUpdate(this,SUPER_TYPE,superClass,this .superClass ); this.superClass=superClass
			;return( C )this
			; }@Override publicString
		getQualifiedName

		()
		{ return simpleName;} @
			Override publicCtTypeParameterReference
		getReference

		()
		{ return getFactory() .
			Type ().createReference(this);}@Overridepublicboolean
		isGenerics

		()
		{ return true;} @
			Override publicCtTypeParameter
		clone

		()
		{ return (CtTypeParameter) super
			. clone() ;}@OverridepublicCtFormalTypeDeclarer
		getTypeParameterDeclarer

		()
		{ try {returngetParent (
			CtFormalTypeDeclarer .
				class );}catch(ParentNotInitializedExceptione
			) { returnnull ;} }
				@ Override@
			UnsettableProperty
		public

		<F
		,C
		extends CtType<Object > > CaddFieldAtTop(CtField< F >field){// unsettable propertyreturn (C )
			this
			; }@Override @UnsettableProperty
		public

		<F
		,C
		extends CtType<Object > > CaddField(CtField< F >field){// unsettable propertyreturn (C )
			this
			; }@Override @UnsettableProperty
		public

		<F
		,C
		extends CtType<Object > > CaddField(intindex , CtField<F >field ){// unsettable propertyreturn (C )
			this
			; }@Override @UnsettableProperty
		public

		<C
		extendsCtType
		< Object> > CsetFields(List< CtField <?>>fields){// unsettable propertyreturn (C )
			this
			; }@Override @DerivedProperty
		public

		<F
		>boolean
		removeField (CtField< F >field){// unsettable propertyreturn false; }
			@
			Override publicCtField
		<

		?>
		getField (Stringname) {returnnull ;} @
			Override @DerivedProperty
		public

		List<
		CtField<
		? >>getFields(){return Collections.emptyList (
			) ;}@Override@UnsettableProperty
		public

		<N
		,C
		extends CtType<Object > > CaddNestedType(CtType< N >nestedType){// unsettable propertyreturn (C )
			this
			; }@Override @UnsettableProperty
		public

		<N
		>boolean
		removeNestedType (CtType< N >nestedType){// unsettable propertyreturn false; }
			@
			Override @UnsettableProperty
		public

		<C
		extendsCtType
		< Object> > CsetNestedTypes(Set< CtType <?>>nestedTypes){// unsettable propertyreturn (C )
			this
			; }@Override public<
		N

		extendsCtType
		< ?> > NgetNestedType(Stringname ) {returnnull ;} @
			Override @DerivedProperty
		public

		Set<
		CtType<
		? >>getNestedTypes(){return Collections.emptySet (
			) ;}@Override@DerivedProperty
		public

		CtPackagegetPackage
		()
		{ return null;} @
			Override publicboolean
		isTopLevel

		()
		{ return false;} @
			Override publicSet
		<

		ModifierKind>
		getModifiers (){return Collections.emptySet (
			) ;}@Overridepublicboolean
		hasModifier

		(ModifierKind
		modifier ) {returnfalse ;} @
			Override @UnsettableProperty
		public

		<C
		extendsCtModifiable
		> CsetModifiers ( Set< ModifierKind >modifiers){// unsettable propertyreturn (C )
			this
			; }@Override @UnsettableProperty
		public

		<C
		extendsCtModifiable
		> CaddModifier ( ModifierKindmodifier ) {// unsettable propertyreturn (C )
			this
			; }@Override @UnsettableProperty
		public

		<C
		extendsCtModifiable
		> CremoveModifier ( ModifierKindmodifier ) {// unsettable propertyreturn (C )
			this
			; }@Override @UnsettableProperty
		public

		<C
		extendsCtModifiable
		> CsetVisibility ( ModifierKindvisibility ) {// unsettable propertyreturn (C )
			this
			; }@Override publicModifierKind
		getVisibility

		()
		{ return null;} @
			Override publicboolean
		isPrimitive

		()
		{ return false;} @
			Override publicboolean
		isAnonymous

		()
		{ return false;} @
			Override publicboolean
		isLocalType

		()
		{ return false;} @
			Override publicList
		<

		CtFieldReference<
		? >>getAllFields(){return Collections.emptyList (
			) ;}@OverridepublicList
		<

		CtFieldReference<
		? >>getDeclaredFields(){return Collections.emptyList (
			) ;}@Overridepublicboolean
		isSubtypeOf

		(CtTypeReference
		< ? >superTypeRef){if( superTypeRefinstanceof CtTypeParameterReference
			) {//the type is type parameter too. Use appropriate sub type checking algorithm CtTypeParameter superTypeParam= (
				CtTypeParameter
				) superTypeRef . getDeclaration() ;returnisSubtypeOf(getFactory(
				) .Type().createTypeAdapter(getTypeParameterDeclarer()),this,superTypeParam); }//type is normal type returngetTypeErasure(
			)
			.
			isSubtypeOf (superTypeRef);}privatestaticbooleanisSubtypeOf
		(

		GenericTypeAdapter typeAdapter , CtTypeParametersubTypeParam, CtTypeParametersuperTypeParam ) {while ( subTypeParam!= null
			) {if ( isSameInSameScope( subTypeParam
				, typeAdapter.adaptType(superTypeParam ))){//both type params are samereturntrue; }
					CtTypeReference
					< ?>
				superTypeOfSubTypeParam
				=subTypeParam.getSuperclass ( ) ;if(superTypeOfSubTypeParam==null
				) {//there is no super type defined, so they are different type parameters return false; }
					if
					( superTypeOfSubTypeParaminstanceof
				CtTypeParameterReference
				) {subTypeParam = (( CtTypeParameterReference
					) superTypeOfSubTypeParam ).getDeclaration( );}else{//the super type is not type parameter. Normal type cannot be a super type of generic parameterreturn
				false ; }
					}
					return false;
				}
			/**
	 * Note: This method expects that both arguments are already adapted to the same scope
	 * @param typeParam a type param 1
	 * @param typeRef a reference to some type 2
	 * @return true if typeParam and typeRef represents same type parameter.
	 */
			private staticboolean
		isSameInSameScope

		(
		CtTypeParameter typeParam , CtTypeReference<? >typeRef ){if( typeRefinstanceof CtTypeParameterReference
			) {return typeParam .getSimpleName (
				) .equals(((CtTypeParameterReference)typeRef).getSimpleName( ));}returnfalse;}
			@
			Override publicCtTypeReference
		<

		?>
		getTypeErasure (){CtTypeReference <?> boundType
			=getBound(this ) ; returnboundType.getTypeErasure(
			) ;}privatestaticCtTypeReference<
		?

		> getBound (CtTypeParametertypeParam) {CtTypeReference< ?> bound
			=typeParam.getSuperclass ( ) ;if(bound==null
			) {bound = typeParam. getFactory
				( ) .Type().OBJECT;}returnbound;}
			@
			Override @UnsettableProperty
		public

		<M
		,C
		extends CtType<Object > > CaddMethod(CtMethod< M >method){// unsettable propertyreturn (C )
			this
			; }@Override @UnsettableProperty
		public

		<M
		>boolean
		removeMethod (CtMethod< M >method){// unsettable propertyreturn false; }
			@
			Override @UnsettableProperty
		public

		<S
		,C
		extends CtType<Object > > CaddSuperInterface(CtTypeReference< S >interfac){// unsettable propertyreturn (C )
			this
			; }@Override @UnsettableProperty
		public

		<S
		>boolean
		removeSuperInterface (CtTypeReference< S >interfac){// unsettable propertyreturn false; }
			@
			Override public<
		R

		>CtMethod
		< R>getMethod (CtTypeReference<R >returnType,Stringname, CtTypeReference< ? >... parameterTypes){returnnull ;} @
			Override public<
		R

		>CtMethod
		< R>getMethod (Stringname, CtTypeReference<? >... parameterTypes){returnnull ;} @
			Override @DerivedProperty
		public

		Set<
		CtMethod<
		? >>getMethods(){return Collections.emptySet (
			) ;}@OverridepublicSet
		<

		CtMethod<
		? >>getMethodsAnnotatedWith(CtTypeReference<? >...annotationTypes){returnCollections .emptySet (
			) ;}@OverridepublicList
		<

		CtMethod<
		? >>getMethodsByName(Stringname) {returnCollections .emptyList (
			) ;}@Override@DerivedProperty
		public

		Set<
		CtTypeReference<
		? >>getSuperInterfaces(){return Collections.emptySet (
			) ;}@Override@UnsettableProperty
		public

		<C
		extendsCtType
		< Object> > CsetMethods(Set< CtMethod <?>>methods){// unsettable propertyreturn (C )
			this
			; }@Override @UnsettableProperty
		public

		<C
		extendsCtType
		< Object> > CsetSuperInterfaces(Set< CtTypeReference <?>>interfaces){// unsettable propertyreturn (C )
			this
			; }@Override @DerivedProperty
		public

		Collection<
		CtExecutableReference<
		? >>getDeclaredExecutables(){return Collections.emptyList (
			) ;}@Override@DerivedProperty
		public

		Collection<
		CtExecutableReference<
		? >>getAllExecutables(){return Collections.emptyList (
			) ;}@Override@DerivedProperty
		public

		Set<
		CtMethod<
		? >>getAllMethods(){return Collections.emptySet (
			) ;}@Override@DerivedProperty
		public

		List<
		CtTypeParameter>
		getFormalCtTypeParameters (){return emptyList() ;
			} @Override@UnsettableProperty
		public

		<C
		extendsCtFormalTypeDeclarer
		> CsetFormalCtTypeParameters ( List< CtTypeParameter >formalTypeParameters){return( C) this
			; }@Override @DerivedProperty
		public

		List<
		CtTypeMember>
		getTypeMembers (){return emptyList() ;
			} @Override@UnsettableProperty
		public

		<C
		extendsCtType
		< Object> > CsetTypeMembers(List< CtTypeMember >members){return( C) this
			; }}