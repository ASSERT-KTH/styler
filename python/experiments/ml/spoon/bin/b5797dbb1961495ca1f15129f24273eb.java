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
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.ModuleFactory;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtFunction;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.QueueProcessingManager;
import spoon.support.reflect.declaration.

CtPackageImpl ;importjava.util.
ArrayList ;importjava.util.
Collection ;importjava.util.
Collections ;importjava.util.

List ; public class CtModelImpl implements

	CtModel { private static final long serialVersionUID=

	1L ; private boolean buildModelFinished=

	false;
	@ Overridepublic < Rextends CtElement >CtQueryfilterChildren(Filter< R> filter
		) {returngetUnnamedModule().getFactory().Query().createQuery(this.getAllModules().toArray()).filterChildren(filter
	)

	;}
	@ Overridepublic< I, R >CtQuerymap(CtFunction< I, R> function
		) {returngetUnnamedModule().getFactory().Query().createQuery(this.getAllModules().toArray()).map(function
	)

	;}
	@ Overridepublic< I >CtQuerymap(CtConsumableFunction< I> queryStep
		) {returngetUnnamedModule().getFactory().Query().createQuery(this.getAllModules().toArray()).map(queryStep
	)

	; } public static class CtRootPackage extends
		CtPackageImpl
			{{this.setSimpleName(CtPackage.TOP_LEVEL_PACKAGE_NAME
		)

		;}
		@ Overridepublic < Textends CtNamedElement >TsetSimpleName (String name
			) {if ( name== null
				) {return( T)
			this

			; }if(name.equals(CtPackage.TOP_LEVEL_PACKAGE_NAME )
				) {returnsuper.setSimpleName(name
			)

			; }return( T)
		this

		;}
		@ Override publicStringgetQualifiedName (
			) {return
		""

		;}
		@ Override publicStringtoString (
			) {return
		TOP_LEVEL_PACKAGE_NAME
	;

	} } private finalCtModule

	unnamedModule ;publicCtModelImpl (Factory f
		){this . unnamedModule =newModuleFactory.CtUnnamedModule(
		);this.unnamedModule.setFactory(f
		);this.unnamedModule.setRootPackage (newCtModelImpl.CtRootPackage()
		);getRootPackage().setFactory(f
	)

	;}
	@ Override publicCtPackagegetRootPackage (
		) {returngetUnnamedModule().getRootPackage(
	)


	;}
	@ OverridepublicCollection<CtType<? >>getAllTypes (
		) {finalList<CtType<? > > result =newArrayList<>(
		);getAllPackages().forEach ( ctPackage
			->{result.addAll(ctPackage.getTypes()
		);}
		) ;return
	result


	;}
	@ OverridepublicCollection< CtPackage>getAllPackages (
		) {returnCollections.unmodifiableCollection(getElements (newTypeFilter<>(CtPackage.class))
	)

	;}
	@ Override publicCtModulegetUnnamedModule (
		) {returnthis.
	unnamedModule

	;}
	@ OverridepublicCollection< CtModule>getAllModules (
		) {return((ModuleFactory. CtUnnamedModule)this.unnamedModule).getAllModules(
	)


	;}
	@ Override publicvoidprocessWith(Processor< ?> processor
		) { QueueProcessingManager processingManager =newQueueProcessingManager(getUnnamedModule().getFactory()
		);processingManager.addProcessor(processor
		);processingManager.process(getAllModules()
	)

	;}
	@ Overridepublic < Eextends CtElement>List< E>getElements(Filter< E> filter
		) {returnfilterChildren(filter).list(
	)


	;}
	@ Override publicbooleanisBuildModelFinished (
		) {returnthis.
	buildModelFinished

	;}
	@ Overridepublic < Textends CtModel >TsetBuildModelIsFinished (boolean buildModelFinished
		){this . buildModelFinished=
		buildModelFinished ;return( T)
	this

;
