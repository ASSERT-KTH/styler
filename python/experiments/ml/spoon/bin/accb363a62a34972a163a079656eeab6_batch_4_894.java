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
package spoon.support.modelobs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ModifierKind;importspoon.reflect.path
. CtRole;importspoon.reflect.visitor.EarlyTerminatingScanner
; importspoon.reflect.visitor.chain.CtScannerListener

;
import spoon . reflect
	. visitor .chain.ScanningMode ;/**
 * Listens on changes  on the spoon model and remembers them
 */publicclassChangeCollector { private final Map<CtElement,Set<
	CtRole > > elementToChangeRole = new IdentityHashMap<>(

	)
	; private final ChangeListenerchangeListener= newChangeListener (
		) ; /**
	 * @param env to be checked {@link Environment}
	 * @return {@link ChangeCollector} attached to the `env` or null if there is none
	 */ publicstaticChangeCollectorgetChangeCollector(Environment
		env ){ FineModelChangeListener mcl= env
			. getModelChangeListener(); if(mclinstanceofChangeListener){
		return
		( (ChangeListener
	)

	mcl
	) . getChangeCollector (); }return null ;} /**
	 * Allows to run code using change collector switched off.
	 * It means that any change of spoon model done by the `runnable` is ignored by the change collector.
	 * Note: it is actually needed to wrap CtElement#toString() calls which sometime modifies spoon model.
	 * See TestSniperPrinter#testPrintChangedReferenceBuilder()
	 * @param env Spoon environment
	 * @param runnable the code to be run
	 */
		public static void runWithoutChangeListener(Environmentenv,Runnable
		runnable ){ FineModelChangeListener mcl= env
			.getModelChangeListener(); if(mclinstanceofChangeListener
			) {
				env.setModelChangeListener(newEmptyModelChangeListener
			( ) )
				;try{runnable.run(
			)
		;
	}

	finally
	{ env .setModelChangeListener( mcl) ;
		}}}/**
	 * Attaches itself to {@link CtModel} to listen to all changes of it's child elements
	 * TODO: it would be nicer if we might listen on changes on {@link CtElement}
	 * @param env to be attached to {@link Environment}
	 * @return this to support fluent API
	 */publicChangeCollectorattachTo
		( Environmentenv
	)

	{
	env .setModelChangeListener(changeListener );return this; }
		/**
	 * @param currentElement the {@link CtElement} whose changes has to be checked
	 * @return set of {@link CtRole}s whose attribute was directly changed on `currentElement` since this {@link ChangeCollector} was attached
	 * The 'directly' means that value of attribute of `currentElement` was changed.
	 * Use {@link #getChanges(CtElement)} to detect changes in child elements too
	 */publicSet< CtRole > getDirectChanges(CtElementcurrentElement){Set
		< CtRole> changes =elementToChangeRole .
			get (currentElement);if(
		changes
		== null){returnCollections.emptySet
	(

	)
	; }returnCollections. unmodifiableSet(changes ); }
		/**
	 * @param currentElement the {@link CtElement} whose changes has to be checked
	 * @return set of {@link CtRole}s whose attribute was changed on `currentElement`
	 * or any child of this attribute was changed
	 * since this {@link ChangeCollector} was attached
	 */ publicSet<CtRole > getChanges ( CtElementcurrentElement){finalSet<CtRole>changes
		= new HashSet < > (getDirectChanges(currentElement
		));finalScanner scanner=new Scanner
			( ) ; scanner.
			setListener (new
			CtScannerListener(
			) { intdepth= 0; CtRole
				checkedRole ;@ Override publicScanningMode enter
					(
					CtElement element ){if(depth==
				0
				) {//we are checking children of role checkedRolecheckedRole=scanner.getScannedRole( )
					;
					} if(changes.
				contains
				( checkedRole)){//we already know that some child of `checkedRole` attribute is modified. Skip othersreturnScanningMode. SKIP_ALL
					;
					}if(elementToChangeRole.containsKey(
					element )){//we have found a modified element in children of `checkedRole`
				changes
				.add(
				checkedRole
				) ;returnScanningMode.
			SKIP_ALL
			;}
			depth ++ ;//continue searching for an modificationreturn ScanningMode. NORMAL
				;}@
			Override
		publicvoidexit
		(CtElementelement){depth--
		; }});currentElement.accept
	(

	scanner ) ; return Collections .unmodifiableSet(changes )
		; }privatestatic class
			Scanner extendsEarlyTerminatingScanner
		<
	Void

	>
	{ CtRole getScannedRole() {return scannedRole ;} }
		/**
	 * Called whenever anything changes in the spoon model
	 * @param currentElement the modified element
	 * @param role the modified attribute of that element
	 */protectedvoidonChange ( CtElement currentElement,CtRolerole){Set
		< CtRole> roles =elementToChangeRole .
			get ( currentElement );if(roles==
			null){roles=new HashSet<>
		(
		) ;elementToChangeRole.put(currentElement , roles) ;
			} if (role.getSuperRole()
		!=
		null){role=role.
	getSuperRole

	( ) ; } roles .
		add ( role); }
			private classChangeListenerimplementsFineModelChangeListener
		{

		privateChangeCollector
		getChangeCollector ( ){return ChangeCollector. this ;} @ Overridepublic void onObjectUpdate( CtElement
			currentElement,CtRolerole ,CtElementnewValue
		,

		CtElementoldValue
		) { onChange(currentElement ,role ) ;} @ Overridepublic void onObjectUpdate( CtElement
			currentElement,CtRolerole ,ObjectnewValue
		,

		ObjectoldValue
		) { onChange(currentElement ,role ) ;} @ Overridepublic void
			onObjectDelete(CtElementcurrentElement ,CtRolerole
		,

		CtElementoldValue
		) { onChange(currentElement ,role ) ;} @ Overridepublic void onListAdd( CtElement
			currentElement,CtRolerole ,Listfield
		,

		CtElementnewValue
		) { onChange(currentElement ,role ) ;} @ Overridepublic void onListAdd( CtElement currentElement, CtRole
			role,Listfield ,intindex
		,

		CtElementnewValue
		) { onChange(currentElement ,role ) ;} @ Overridepublic voidonListDelete( CtElement currentElement, CtRolerole ,
			Listfield,Collection <?extends
		CtElement

		>oldValue
		) { onChange(currentElement ,role ) ;} @ Overridepublic void onListDelete( CtElement currentElement, CtRole
			role,Listfield ,intindex
		,

		CtElementoldValue
		) { onChange(currentElement ,role ) ;} @ Overridepublic void onListDeleteAll( CtElement
			currentElement,CtRolerole ,Listfield
		,

		ListoldValue
		) {onChange( currentElement, role );} @Override public <K ,V>void onMapAdd( CtElementcurrentElement , CtRolerole , Map< K
			,V>field ,Kkey
		,

		CtElementnewValue
		) {onChange( currentElement, role );} @Override public <K ,V>void onMapDeleteAll( CtElementcurrentElement ,CtRolerole, Map< K, V
			>field,Map <K,
		V

		>oldValue
		) { onChange(currentElement ,role ) ;} @ Overridepublic void onSetAdd( CtElement
			currentElement,CtRolerole ,Setfield
		,

		CtElementnewValue
		) {onChange ( currentElement, role );} @Override public <T extends Enum> void onSetAdd( CtElement
			currentElement,CtRolerole ,Setfield
		,

		TnewValue
		) { onChange(currentElement ,role ) ;} @ Overridepublic void onSetDelete( CtElement
			currentElement,CtRolerole ,Setfield
		,

		CtElementoldValue
		) { onChange(currentElement ,role ) ;} @ Overridepublic voidonSetDelete(CtElement currentElement, CtRole
			role,Setfield ,Collection<
		ModifierKind

		>oldValue
		) { onChange(currentElement ,role ) ;} @ Overridepublic void onSetDelete( CtElement
			currentElement,CtRolerole ,Setfield
		,

		ModifierKindoldValue
		) { onChange(currentElement ,role ) ;} @ Overridepublic void onSetDeleteAll( CtElement
			currentElement,CtRolerole ,Setfield
		,
	Set
oldValue
