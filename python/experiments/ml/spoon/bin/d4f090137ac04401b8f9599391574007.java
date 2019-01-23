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
		if (superClass != null) {
			superClass.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, SUPER_TYPE, superClass, this.superClass);
		this.superClass = superClass;
		return (C) this;
	}

	@Override
	public String getQualifiedName() {
		return simpleName;
	}

	@Override
	public CtTypeParameterReference getReference() {
		return getFactory().Type().createReference(this);
	}

	@Override
	public boolean isGenerics() {
		return true;
	}

	@Override
	public CtTypeParameter clone() {
		return (CtTypeParameter) super.clone();
	}

	@Override
	public CtFormalTypeDeclarer getTypeParameterDeclarer() {
		try {
			return getParent(CtFormalTypeDeclarer.class);
		} catch (ParentNotInitializedException e) {
			return null;
		}
	}

	@Override
	@UnsettableProperty
	public <F, C extends CtType<Object>> C addFieldAtTop(CtField<F> field) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <F, C extends CtType<Object>> C addField(CtField<F> field) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <F, C extends CtType<Object>> C addField(int index, CtField<F> field) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <C extends CtType<Object>> C setFields(List<CtField<?>> fields) {
		// unsettable property
		return (C) this;
	}

	@Override
	@DerivedProperty
	public <F> boolean removeField(CtField<F> field) {
		// unsettable property
		return false;
	}

	@Override
	public CtField<?> getField(String name) {
		return null;
	}

	@Override
	@DerivedProperty
	public List<CtField<?>> getFields() {
		return Collections.emptyList();
	}

	@Override
	@UnsettableProperty
	public <N, C extends CtType<Object>> C addNestedType(CtType<N> nestedType) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <N> boolean removeNestedType(CtType<N> nestedType) {
		// unsettable property
		return false;
	}

	@Override
	@UnsettableProperty
	public <C extends CtType<Object>> C setNestedTypes(Set<CtType<?>> nestedTypes) {
		// unsettable property
		return (C) this;
	}

	@Override
	public <N extends CtType<?>> N getNestedType(String name) {
		return null;
	}

	@Override
	@DerivedProperty
	public Set<CtType<?>> getNestedTypes() {
		return Collections.emptySet();
	}

	@Override
	@DerivedProperty
	public CtPackage getPackage() {
		return null;
	}

	@Override
	public boolean isTopLevel() {
		return false;
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		return Collections.emptySet();
	}

	@Override
	public boolean hasModifier(ModifierKind modifier) {
		return false;
	}

	@Override
	@UnsettableProperty
	public <C extends CtModifiable> C setModifiers(Set<ModifierKind> modifiers) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <C extends CtModifiable> C addModifier(ModifierKind modifier) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <C extends CtModifiable> C removeModifier(ModifierKind modifier) {
		// unsettable property
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <C extends CtModifiable> C setVisibility(ModifierKind visibility) {
		// unsettable property
		return (C) this;
	}

	@Override
	public ModifierKind getVisibility() {
		return null;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public boolean isAnonymous() {
		return false;
	}

	@Override
	public boolean isLocalType() {
		return false;
	}

	@Override
	public List<CtFieldReference<? > >getAllFields( )
		{ returnCollections.emptyList()
	;

	}@
	Override publicList<CtFieldReference<?> >getDeclaredFields( )
		{ returnCollections.emptyList()
	;

	}@
	Override public booleanisSubtypeOf(CtTypeReference<? >superTypeRef )
		{ if( superTypeRef instanceofCtTypeParameterReference )
			{
			//the type is type parameter too. Use appropriate sub type checking algorithm CtTypeParameter superTypeParam =(CtTypeParameter )superTypeRef.getDeclaration()
			; returnisSubtypeOf(getFactory().Type().createTypeAdapter(getTypeParameterDeclarer()) ,this ,superTypeParam)
		;
		}
		//type is normal type returngetTypeErasure().isSubtypeOf(superTypeRef)
	;

	} private static booleanisSubtypeOf( GenericTypeAdaptertypeAdapter , CtTypeParametersubTypeParam , CtTypeParametersuperTypeParam )
		{ while( subTypeParam !=null )
			{ if(isSameInSameScope(subTypeParam ,typeAdapter.adaptType(superTypeParam)) )
				{
				//both type params are same returntrue
			;
			}CtTypeReference<? > superTypeOfSubTypeParam =subTypeParam.getSuperclass()
			; if( superTypeOfSubTypeParam ==null )
				{
				//there is no super type defined, so they are different type parameters returnfalse
			;
			} if( superTypeOfSubTypeParam instanceofCtTypeParameterReference )
				{ subTypeParam =((CtTypeParameterReference )superTypeOfSubTypeParam).getDeclaration()
			; } else
				{
				//the super type is not type parameter. Normal type cannot be a super type of generic parameter returnfalse
			;
		}
		} returnfalse
	;

	}
	/**
	 * Note: This method expects that both arguments are already adapted to the same scope
	 * @param typeParam a type param 1
	 * @param typeRef a reference to some type 2
	 * @return true if typeParam and typeRef represents same type parameter.
	 */ private static booleanisSameInSameScope( CtTypeParametertypeParam ,CtTypeReference<? >typeRef )
		{ if( typeRef instanceofCtTypeParameterReference )
			{ returntypeParam.getSimpleName().equals(((CtTypeParameterReference )typeRef).getSimpleName())
		;
		} returnfalse
	;

	}@
	Override publicCtTypeReference<? >getTypeErasure( )
		{CtTypeReference<? > boundType =getBound(this)
		; returnboundType.getTypeErasure()
	;

	} private staticCtTypeReference<? >getBound( CtTypeParametertypeParam )
		{CtTypeReference<? > bound =typeParam.getSuperclass()
		; if( bound ==null )
			{ bound =typeParam.getFactory().Type().OBJECT
		;
		} returnbound
	;

	}@
	Override@
	UnsettableProperty public<M , C extendsCtType<Object> > CaddMethod(CtMethod<M >method )
		{
		// unsettable property return(C )this
	;

	}@
	Override@
	UnsettableProperty public<M > booleanremoveMethod(CtMethod<M >method )
		{
		// unsettable property returnfalse
	;

	}@
	Override@
	UnsettableProperty public<S , C extendsCtType<Object> > CaddSuperInterface(CtTypeReference<S >interfac )
		{
		// unsettable property return(C )this
	;

	}@
	Override@
	UnsettableProperty public<S > booleanremoveSuperInterface(CtTypeReference<S >interfac )
		{
		// unsettable property returnfalse
	;

	}@
	Override public<R >CtMethod<R >getMethod(CtTypeReference<R >returnType , Stringname ,CtTypeReference<?> ...parameterTypes )
		{ returnnull
	;

	}@
	Override public<R >CtMethod<R >getMethod( Stringname ,CtTypeReference<?> ...parameterTypes )
		{ returnnull
	;

	}@
	Override@
	DerivedProperty publicSet<CtMethod<?> >getMethods( )
		{ returnCollections.emptySet()
	;

	}@
	Override publicSet<CtMethod<?> >getMethodsAnnotatedWith(CtTypeReference<?> ...annotationTypes )
		{ returnCollections.emptySet()
	;

	}@
	Override publicList<CtMethod<?> >getMethodsByName( Stringname )
		{ returnCollections.emptyList()
	;

	}@
	Override@
	DerivedProperty publicSet<CtTypeReference<?> >getSuperInterfaces( )
		{ returnCollections.emptySet()
	;

	}@
	Override@
	UnsettableProperty public< C extendsCtType<Object> > CsetMethods(Set<CtMethod<?> >methods )
		{
		// unsettable property return(C )this
	;

	}@
	Override@
	UnsettableProperty public< C extendsCtType<Object> > CsetSuperInterfaces(Set<CtTypeReference<?> >interfaces )
		{
		// unsettable property return(C )this
	;

	}@
	Override@
	DerivedProperty publicCollection<CtExecutableReference<?> >getDeclaredExecutables( )
		{ returnCollections.emptyList()
	;

	}@
	Override@
	DerivedProperty publicCollection<CtExecutableReference<?> >getAllExecutables( )
		{ returnCollections.emptyList()
	;

	}@
	Override@
	DerivedProperty publicSet<CtMethod<?> >getAllMethods( )
		{ returnCollections.emptySet()
	;

	}@
	Override@
	DerivedProperty publicList<CtTypeParameter >getFormalCtTypeParameters( )
		{ returnemptyList()
	;

	}@
	Override@
	UnsettableProperty public< C extendsCtFormalTypeDeclarer > CsetFormalCtTypeParameters(List<CtTypeParameter >formalTypeParameters )
		{ return(C )this
	;

	}@
	Override@
	DerivedProperty publicList<CtTypeMember >getTypeMembers( )
		{ returnemptyList()
	;

	}@
	Override@
	UnsettableProperty public< C extendsCtType<Object> > CsetTypeMembers(List<CtTypeMember >members )
		{ return(C )this
	;
}
