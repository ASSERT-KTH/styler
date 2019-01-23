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
import spoon.support.reflect.declaration.CtPackageImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CtModelImpl implements CtModel {

	private static final long serialVersionUID = 1L;

	private boolean buildModelFinished = false;

	@Override
	public <R extends CtElement> CtQuery filterChildren(Filter<R> filter) {
		return getUnnamedModule().getFactory().Query().createQuery(this.getAllModules().toArray()).filterChildren(filter);
	}

	@Override
	public <I, R> CtQuery map(CtFunction<I, R> function) {
		return getUnnamedModule().getFactory().Query().createQuery(this.getAllModules().toArray()).map(function);
	}

	@Override
	public <I> CtQuery map(CtConsumableFunction<I> queryStep) {
		return getUnnamedModule().getFactory().Query().createQuery(this.getAllModules().toArray()).map(queryStep);
	}

	public static class CtRootPackage extends CtPackageImpl {
		{
			this.setSimpleName(CtPackage.TOP_LEVEL_PACKAGE_NAME);
		}

		@Override
		public <T extends CtNamedElement> T setSimpleName(String name) {
			if (name == null) {
				return (T) this;
			}

			if (name.equals(CtPackage.TOP_LEVEL_PACKAGE_NAME)) {
				return super.setSimpleName(name);
			}

			return (T) this;
		}

		@Override
		public String getQualifiedName() {
			return "";
		}

		@Override
		public String toString() {
			return TOP_LEVEL_PACKAGE_NAME;
		}
	}

	private final CtModule unnamedModule;public CtModelImpl( Factory
		f){ this . unnamedModule=newModuleFactory.CtUnnamedModule
		();this.unnamedModule.setFactory(
		f);this.unnamedModule. setRootPackage(newCtModelImpl.CtRootPackage(
		));getRootPackage().setFactory(
	f

	);
	} @ OverridepublicCtPackage getRootPackage
		( ){returngetUnnamedModule().getRootPackage
	(


	);
	} @OverridepublicCollection<CtType< ?>> getAllTypes
		( ){finalList<CtType< ? > > result=newArrayList<>
		();getAllPackages(). forEach (
			ctPackage->{result.addAll(ctPackage.getTypes(
		));
		} );
	return


	result;
	} @OverridepublicCollection <CtPackage> getAllPackages
		( ){returnCollections.unmodifiableCollection( getElements(newTypeFilter<>(CtPackage.class)
	)

	);
	} @ OverridepublicCtModule getUnnamedModule
		( ){returnthis
	.

	unnamedModule;
	} @OverridepublicCollection <CtModule> getAllModules
		( ){return((ModuleFactory .CtUnnamedModule)this.unnamedModule).getAllModules
	(


	);
	} @ OverridepublicvoidprocessWith(Processor <? >
		processor ) { QueueProcessingManager processingManager=newQueueProcessingManager(getUnnamedModule().getFactory(
		));processingManager.addProcessor(
		processor);processingManager.process(getAllModules(
	)

	);
	} @Override public <E extendsCtElement>List <E>getElements(Filter <E >
		filter ){returnfilterChildren(filter).list
	(


	);
	} @ Overridepublicboolean isBuildModelFinished
		( ){returnthis
	.

	buildModelFinished;
	} @Override public <T extends CtModel>T setBuildModelIsFinished( boolean
		buildModelFinished){ this .buildModelFinished
		= buildModelFinished;return (T
	)

this
