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
import spoon.reflect.factory.
Factory ;importspoon.reflect.
factory .ModuleFactory;importspoon.reflect.
visitor .Filter;importspoon.reflect.
visitor .chain.CtConsumableFunction;importspoon.reflect.
visitor .chain.CtFunction;importspoon.reflect.
visitor .chain.CtQuery;importspoon.reflect.
visitor .filter.TypeFilter;importspoon.support.
QueueProcessingManager ;importspoon.support.
reflect .declaration.CtPackageImpl;importjava.util.

ArrayList ;importjava.util.
Collection ;importjava.util.
Collections ;importjava.util.
List ;publicclassCtModelImplimplementsCtModel

{ private static final long serialVersionUID

	= 1L ; private boolean buildModelFinished =false

	; @ Override public <R

	extendsCtElement
	> CtQueryfilterChildren ( Filter< R >filter){returngetUnnamedModule () .
		getFactory ().Query().createQuery(this.getAllModules().toArray()).filterChildren(filter);}@Overridepublic<
	I

	,R
	> CtQuerymap( CtFunction< I ,R>function){ returngetUnnamedModule () .
		getFactory ().Query().createQuery(this.getAllModules().toArray()).map(function);}@Overridepublic<
	I

	>CtQuery
	map (CtConsumableFunction< I >queryStep){returngetUnnamedModule () .
		getFactory ().Query().createQuery(this.getAllModules().toArray()).map(queryStep);}publicstaticclassCtRootPackage
	extends

	CtPackageImpl { { this . setSimpleName (
		CtPackage
			.TOP_LEVEL_PACKAGE_NAME);}@Overridepublic<
		T

		extendsCtNamedElement
		> TsetSimpleName ( Stringname ) {if( name== null
			) {return ( T) this
				; }if( name.
			equals

			( CtPackage.TOP_LEVEL_PACKAGE_NAME)){returnsuper.setSimpleName (
				name );}return(T)
			this

			; }@Override publicString
		getQualifiedName

		()
		{ return "";} @
			Override publicString
		toString

		()
		{ return TOP_LEVEL_PACKAGE_NAME;} }
			private finalCtModule
		unnamedModule
	;

	public CtModelImpl ( Factoryf

	) {this. unnamedModule= new
		ModuleFactory.CtUnnamedModule ( ) ;this.unnamedModule.setFactory
		(f);this.unnamedModule.setRootPackage
		(newCtModelImpl.CtRootPackage() );getRootPackage().setFactory
		(f);}@OverridepublicCtPackage
	getRootPackage

	()
	{ return getUnnamedModule() .
		getRootPackage ();}@OverridepublicCollection
	<


	CtType<
	? >>getAllTypes(){final List<CtType <
		? >>result=newArrayList< > ( ) ;getAllPackages().forEach
		(ctPackage->{result.addAll ( ctPackage
			.getTypes());});returnresult
		;}@
		Override publicCollection
	<


	CtPackage>
	getAllPackages (){return Collections.unmodifiableCollection (
		getElements (newTypeFilter<>(CtPackage .class)));}@OverridepublicCtModule
	getUnnamedModule

	()
	{ return this.unnamedModule ;
		} @OverridepublicCollection
	<

	CtModule>
	getAllModules (){return ((ModuleFactory .
		CtUnnamedModule )this.unnamedModule). getAllModules();}@Overridepublicvoid
	processWith


	(Processor
	< ? >processor){QueueProcessingManagerprocessingManager =new QueueProcessingManager
		( getUnnamedModule ( ) .getFactory());processingManager.addProcessor(processor
		);processingManager.process(getAllModules
		());}@Overridepublic<
	E

	extendsCtElement
	> List< E >getElements (Filter<E >filter){returnfilterChildren (filter )
		. list();}@Overridepublicboolean
	isBuildModelFinished


	()
	{ return this.buildModelFinished ;
		} @Overridepublic<
	T

	extendsCtModel
	> TsetBuildModelIsFinished ( booleanbuildModelFinished ) {this. buildModelFinished= buildModelFinished
		;return( T )this
		; }}