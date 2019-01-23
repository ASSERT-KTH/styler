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
package spoon.reflect;

import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;importspoon.reflect.factory
. ModuleFactory;importspoon.reflect.visitor.Filter
; importspoon.reflect.visitor.chain.CtConsumableFunction
; importspoon.reflect.visitor.chain.CtFunction
; importspoon.reflect.visitor.chain.CtQuery
; importspoon.reflect.visitor
. filter.TypeFilter;importspoon.support.QueueProcessingManager

; importspoon.support.reflect
. declaration.CtPackageImpl;importjava
. util.ArrayList;importjava
. util.Collection;importjava

. util . Collections ; import

	java . util . List ; publicclass

	CtModelImpl implements CtModel { privatestatic

	finallong
	serialVersionUID =1L ; privateboolean buildModelFinished =false;@Overridepublic <R extends
		CtElement >CtQueryfilterChildren(Filter<R>filter){returngetUnnamedModule().getFactory().Query().createQuery(this.getAllModules(
	)

	.toArray
	( )). filterChildren( filter );}@Overridepublic <I ,R >
		CtQuery map(CtFunction<I,R>function){returngetUnnamedModule().getFactory().Query().createQuery(this.getAllModules(
	)

	.toArray
	( )). map (function);}@ Overridepublic <
		I >CtQuerymap(CtConsumableFunction<I>queryStep){returngetUnnamedModule().getFactory().Query().createQuery(this.getAllModules(
	)

	. toArray ( ) ) . map
		(
			queryStep);}publicstaticclassCtRootPackageextends
		CtPackageImpl

		{{
		this .setSimpleName ( CtPackage. TOP_LEVEL_PACKAGE_NAME );} @Override public
			< Textends CtNamedElement >T setSimpleName
				( Stringname) {if
			(

			name ==null){return(T)this; }
				if (name.equals(CtPackage.
			TOP_LEVEL_PACKAGE_NAME

			) ){return super.
		setSimpleName

		(name
		) ; }return( T
			) this;
		}

		@Override
		public String getQualifiedName() {
			return "";
		}
	@

	Override public String toString(

	) {returnTOP_LEVEL_PACKAGE_NAME ;} }
		privatefinalCtModule unnamedModule ; publicCtModelImpl(Factoryf)
		{this.unnamedModule=newModuleFactory.CtUnnamedModule
		();this.unnamedModule. setFactory(f);this.
		unnamedModule.setRootPackage(newCtModelImpl.CtRootPackage(
	)

	);
	getRootPackage ( ).setFactory (
		f );}@OverridepublicCtPackagegetRootPackage
	(


	){
	return getUnnamedModule().getRootPackage() ;}@ Override
		public Collection<CtType<?>> getAllTypes ( ) {finalList<CtType<
		?>>result=newArrayList < >
			();getAllPackages().forEach(ctPackage->
		{result.
		addAll (ctPackage
	.


	getTypes(
	) );}) ;returnresult ;
		} @OverridepublicCollection<CtPackage> getAllPackages(){returnCollections.unmodifiableCollection(getElements(
	new

	TypeFilter<
	> ( CtPackage.class )
		) );}@
	Override

	publicCtModule
	getUnnamedModule (){return this.unnamedModule ;
		} @OverridepublicCollection<CtModule >getAllModules(){return((ModuleFactory
	.


	CtUnnamedModule)
	this . unnamedModule).getAllModules() ;} @
		Override public void processWith (Processor<?>processor){QueueProcessingManagerprocessingManager=
		newQueueProcessingManager(getUnnamedModule().
		getFactory());processingManager.addProcessor(
	processor

	);
	processingManager .process ( getAllModules( ));} @Overridepublic<Eextends CtElement> List
		< E>getElements(Filter<E>filter
	)


	{return
	filterChildren ( filter). list
		( );}@
	Override

	publicboolean
	isBuildModelFinished () { returnthis . buildModelFinished;} @Override public
		<Textends CtModel >T
		setBuildModelIsFinished (booleanbuildModelFinished ){
	this

.
