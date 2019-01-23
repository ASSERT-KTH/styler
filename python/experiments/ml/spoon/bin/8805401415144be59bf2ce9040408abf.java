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
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;

/**
 * Listens on changes  on the spoon model and remembers them
 */
public class ChangeCollector {
	private final Map<CtElement, Set<CtRole>> elementToChangeRole = new IdentityHashMap<>();
	private final ChangeListener changeListener = new ChangeListener();

	/**
	 * @param env to be checked {@link Environment}
	 * @return {@link ChangeCollector} attached to the `env` or null if there is none
	 */
	public static ChangeCollector getChangeCollector(Environment env) {
		FineModelChangeListener mcl = env.getModelChangeListener();
		if (mcl instanceof ChangeListener) {
			return ((ChangeListener) mcl).
		getChangeCollector
		( );
	}

	return
	null ; } /**
	 * Allows to run code using change collector switched off.
	 * It means that any change of spoon model done by the `runnable` is ignored by the change collector.
	 * Note: it is actually needed to wrap CtElement#toString() calls which sometime modifies spoon model.
	 * See TestSniperPrinter#testPrintChangedReferenceBuilder()
	 * @param env Spoon environment
	 * @param runnable the code to be run
	 */publicstatic voidrunWithoutChangeListener ( Environmentenv ,
		Runnable runnable ) {FineModelChangeListenermcl=env.
		getModelChangeListener () ; if( mcl
			instanceofChangeListener){env .setModelChangeListener(newEmptyModelChangeListener
			( )
				);try{runnable.
			run ( )
				;}finally{env.setModelChangeListener
			(
		mcl
	)

	;
	} } }/**
	 * Attaches itself to {@link CtModel} to listen to all changes of it's child elements
	 * TODO: it would be nicer if we might listen on changes on {@link CtElement}
	 * @param env to be attached to {@link Environment}
	 * @return this to support fluent API
	 */public ChangeCollectorattachTo (
		Environmentenv){env.setModelChangeListener
		( changeListener)
	;

	return
	this ;}/**
	 * @param currentElement the {@link CtElement} whose changes has to be checked
	 * @return set of {@link CtRole}s whose attribute was directly changed on `currentElement` since this {@link ChangeCollector} was attached
	 * The 'directly' means that value of attribute of `currentElement` was changed.
	 * Use {@link #getChanges(CtElement)} to detect changes in child elements too
	 */public Set<CtRole >getDirectChanges (
		CtElementcurrentElement){ Set < CtRole>changes=elementToChangeRole.get
		( currentElement) ; if( changes
			== null){returnCollections.
		emptySet
		( );}returnCollections.unmodifiableSet
	(

	changes
	) ;}/**
	 * @param currentElement the {@link CtElement} whose changes has to be checked
	 * @return set of {@link CtRole}s whose attribute was changed on `currentElement`
	 * or any child of this attribute was changed
	 * since this {@link ChangeCollector} was attached
	 */public Set<CtRole >getChanges (
		CtElement currentElement){final Set < CtRole >changes=newHashSet<>(getDirectChanges(
		currentElement ) ) ; final Scannerscanner=new
		Scanner();scanner .setListener( new
			CtScannerListener ( ) {int
			depth =0
			;CtRole
			checkedRole ; @Overridepublic ScanningModeenter (
				CtElement element) { if( depth
					==
					0 ) {//we are checking children of role checkedRolecheckedRole=scanner.
				getScannedRole
				( );}if(changes.contains (
					checkedRole
					) ){//we already know that some child of `checkedRole` attribute is modified. Skip othersreturn
				ScanningMode
				. SKIP_ALL;}if(elementToChangeRole.containsKey (
					element
					)){//we have found a modified element in children of `checkedRole`changes.add
					( checkedRole);return
				ScanningMode
				.SKIP_ALL;
				}
				depth ++;//continue searching for an modificationreturn
			ScanningMode
			.NORMAL
			; } @Overridepublic voidexit (
				CtElementelement)
			{
		depth--;
		}});currentElement.accept
		( scanner);returnCollections.unmodifiableSet
	(

	changes ) ; } private staticclassScannerextends EarlyTerminatingScanner
		< Void>{ CtRole
			getScannedRole ()
		{
	return

	scannedRole
	; } }/**
	 * Called whenever anything changes in the spoon model
	 * @param currentElement the modified element
	 * @param role the modified attribute of that element
	 */protected voidonChange ( CtElementcurrentElement ,
		CtRolerole){ Set < CtRole>roles=elementToChangeRole.get
		( currentElement) ; if( roles
			== null ) {roles=newHashSet<
			>();elementToChangeRole. put(currentElement
		,
		roles );}if(role . getSuperRole( )
			!= null ){role=role.
		getSuperRole
		();}roles.add
	(

	role ) ; } private class
		ChangeListener implements FineModelChangeListener{private ChangeCollector
			getChangeCollector (){return
		ChangeCollector

		.this
		; } @Overridepublic voidonObjectUpdate ( CtElementcurrentElement , CtRolerole , CtElementnewValue ,
			CtElementoldValue){ onChange(currentElement
		,

		role)
		; } @Overridepublic voidonObjectUpdate ( CtElementcurrentElement , CtRolerole , ObjectnewValue ,
			ObjectoldValue){ onChange(currentElement
		,

		role)
		; } @Overridepublic voidonObjectDelete ( CtElementcurrentElement , CtRolerole ,
			CtElementoldValue){ onChange(currentElement
		,

		role)
		; } @Overridepublic voidonListAdd ( CtElementcurrentElement , CtRolerole , Listfield ,
			CtElementnewValue){ onChange(currentElement
		,

		role)
		; } @Overridepublic voidonListAdd ( CtElementcurrentElement , CtRolerole , Listfield , intindex ,
			CtElementnewValue){ onChange(currentElement
		,

		role)
		; } @Overridepublic voidonListDelete ( CtElementcurrentElement , CtRolerole ,Listfield , Collection< ?extends CtElement
			>oldValue){ onChange(currentElement
		,

		role)
		; } @Overridepublic voidonListDelete ( CtElementcurrentElement , CtRolerole , Listfield , intindex ,
			CtElementoldValue){ onChange(currentElement
		,

		role)
		; } @Overridepublic voidonListDeleteAll ( CtElementcurrentElement , CtRolerole , Listfield ,
			ListoldValue){ onChange(currentElement
		,

		role)
		; }@Override public< K ,V> voidonMapAdd ( CtElementcurrentElement ,CtRolerole, Map< K, V >field , Kkey ,
			CtElementnewValue){ onChange(currentElement
		,

		role)
		; }@Override public< K ,V> voidonMapDeleteAll ( CtElementcurrentElement ,CtRolerole, Map< K, V>field, Map< K, V
			>oldValue){ onChange(currentElement
		,

		role)
		; } @Overridepublic voidonSetAdd ( CtElementcurrentElement , CtRolerole , Setfield ,
			CtElementnewValue){ onChange(currentElement
		,

		role)
		; }@ Override public< T extendsEnum> voidonSetAdd ( CtElementcurrentElement , CtRolerole , Setfield ,
			TnewValue){ onChange(currentElement
		,

		role)
		; } @Overridepublic voidonSetDelete ( CtElementcurrentElement , CtRolerole , Setfield ,
			CtElementoldValue){ onChange(currentElement
		,

		role)
		; } @Overridepublic voidonSetDelete ( CtElementcurrentElement , CtRolerole ,Setfield, Collection< ModifierKind
			>oldValue){ onChange(currentElement
		,

		role)
		; } @Overridepublic voidonSetDelete ( CtElementcurrentElement , CtRolerole , Setfield ,
			ModifierKindoldValue){ onChange(currentElement
		,

		role)
		; } @Overridepublic voidonSetDeleteAll ( CtElementcurrentElement , CtRolerole , Setfield ,
			SetoldValue){ onChange(currentElement
		,
	role
)
