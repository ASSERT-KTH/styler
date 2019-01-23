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
package spoon.reflect.visitor;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.SpoonClassNotFoundException;
import spoon.support.reflect.reference.CtTypeMemberWildcardImportReferenceImpl;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A scanner that calculates the imports for a given model.
 */
public class ImportScannerImpl extends CtScanner implements ImportScanner {

	private static final Collection<String> namesPresentInJavaLang8 =
			Collections.singletonList("FunctionalInterface");
	private static final Collection<String> namesPresentInJavaLang9 = Arrays.asList(
			"ProcessHandle", "StackWalker", "StackFramePermission");

	protected Map<String, CtTypeReference<?>> classImports = new TreeMap<>();
	protected Map<String, CtFieldReference<?>> fieldImports = new TreeMap<>();
	protected Map<String, CtExecutableReference<?>> methodImports = new TreeMap<>();
	//top declaring type of that import
	protected CtTypeReference<?> targetType;
	private Map<String, Boolean> namesPresentInJavaLang = new HashMap<>();
	private Set<String> fieldAndMethodsNames = new HashSet<>();
	private Set<CtTypeReference> exploredReferences = new HashSet<>(); // list of explored references
	private Map<CtImport, Boolean> usedImport = new HashMap<>(); // defined if imports had been used or not

	@Override
	public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
		enter(fieldRead);
		scan(fieldRead.getAnnotations());
		scan(fieldRead.getTypeCasts());
		scan(fieldRead.getVariable());
		scan(fieldRead.getTarget());
		exit(fieldRead);
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		enter(reference);
		scan(reference.getDeclaringType());
		if (reference.isStatic()) {
			addFieldImport(reference);
		} else {
			scan(reference.getDeclaringType());
		}
		exit(reference);
	}

	@Override
	public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
		enter(reference);
		if (reference.isStatic()) {
			addMethodImport(reference);
		} else if (reference.isConstructor()) {
			scan(reference.getDeclaringType());
		}
		scan(reference.getActualTypeArguments());
		exit(reference);
	}

	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		if (!(reference instanceof CtArrayTypeReference)) {
			CtTypeReference typeReference;
			if (reference.getDeclaringType() == null) {
				typeReference = reference;
			} else {
				typeReference = reference.getAccessType();
			}

			if (!typeReference.equals(reference)) {
				if (this.isAlreadyInUsedImport(reference)) {
					super.visitCtTypeReference(reference);
					return;
				}
			}


			if (!this.isTypeInCollision(typeReference, false)) {
				this.addClassImport(typeReference);
			}
		}
		super.visitCtTypeReference(reference);

	}

	@Override
	public void scan(CtElement element) {
		if (element != null) {
			element.accept(this);
		}
	}

	@Override
	public void visitCtJavaDoc(CtJavaDoc ctJavaDoc) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(ctJavaDoc.getContent());

		for (CtJavaDocTag ctJavaDocTag : ctJavaDoc.getTags()) {
			stringBuilder.append("\n").append(ctJavaDocTag.getType()).append(" ").append(ctJavaDocTag.getContent());
		}

		String javadoc = stringBuilder.toString();
		for (CtImport ctImport : this.usedImport.keySet()) {
			switch (ctImport.getImportKind()) {
				case TYPE:
					if (javadoc.contains(ctImport.getReference().getSimpleName()) && ctImport.getReference() instanceof CtTypeReference) {
						//assure that it is not just any occurrence of same substring, but it is real javadoc link to the same type
						if (matchesTypeName(javadoc, (CtTypeReference<?>) ctImport.getReference())) {
							this.setImportUsed(ctImport);
						}
					}
					break;
			}
		}
	}

	private static Set<String> mainTags = new HashSet<>(Arrays.asList("see", "throws", "exception"));
	private static Set<String> inlineTags = new HashSet<>(Arrays.asList("link", "linkplain", "value"));
	private static Pattern tagRE = Pattern.compile("(\\{)?@(\\w+)\\s+([\\w\\.\\$]+)(?:#(\\w+)(?:\\(([^\\)]*)\\)))?");

	private boolean matchesTypeName(String javadoc, CtTypeReference<?> typeRef) {
		Matcher m = tagRE.matcher(javadoc);
		while (m.find()) {
			String bracket = m.group(1);
			String tag = m.group(2);
			if ("{".equals(bracket)) {
				if (inlineTags.contains(tag) == false) {
					continue;
				}
			} else {
				if (mainTags.contains(tag) == false) {
					continue;
				}
			}
			String type = m.group(3);
			String params = m.group(5);

			if (isTypeMatching(type, typeRef)) {
				return true;
			}
			if (params != null) {
				String[] paramTypes = params.split("\\s*,\\s*");
				for (String paramType : paramTypes) {
					if (isTypeMatching(paramType, typeRef)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isTypeMatching(String typeName, CtTypeReference<?> typeRef) {
		if (typeName != null) {
			if (typeName.equals(typeRef.getQualifiedName())) {
				return true;
			}
			if (typeName.equals(typeRef.getSimpleName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {
		addClassImport(annotationType.getReference());
		super.visitCtAnnotationType(annotationType);
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		addClassImport(ctEnum.getReference());
		super.visitCtEnum(ctEnum);
	}

	@Override
	public <T> void visitCtInterface(CtInterface<T> intrface) {
		addClassImport(intrface.getReference());
		for (CtTypeMember t : intrface.getTypeMembers()) {
			if (!(t instanceof CtType)) {
				continue;
			}
			addClassImport(((CtType) t).getReference());
		}
		super.visitCtInterface(intrface);
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		addClassImport(ctClass.getReference());
		for (CtTypeMember t : ctClass.getTypeMembers()) {
			if (!(t instanceof CtType)) {
				continue;
			}
			addClassImport(((CtType) t).getReference());
		}
		super.visitCtClass(ctClass);
	}

	@Override
	public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
		for (CtTypeReference<?> type : catchVariable.getMultiTypes()) {
			addClassImport(type);
		}
		super.visitCtCatchVariable(catchVariable);
	}

	@Override
	public void visitCtInvocation(CtInvocation invocation) {
		this.scan(invocation.getTypeCasts());
		this.scan(invocation.getExecutable());
		if (!this.isImportedInMethodImports(invocation.getExecutable())) {
			this.scan(invocation.getTarget());
		}

		this.scan(invocation.getArguments());
	}

	@Override
	public Set<CtImport> getAllImports( ) { Set <CtImport>listallImports=new

	HashSet <>();for( Map. Entry < CtImport,Boolean>entry:this. usedImport
		. entrySet()){if( entry
			.getValue()){listallImports.add(entry
		.
	getKey

	( )) ; } }for(CtReferencereference:this. classImports
		.values()){listallImports.add(reference.getFactory().Type().
	createImport

	( reference) ) ; }for(CtReferencereference:this. fieldImports
		.values()){listallImports.add(reference.getFactory().Type().
	createImport

	( reference) ) ; }for(CtReferencereference:this. methodImports
		.values()){listallImports.add(reference.getFactory().Type().
	createImport
	( reference)
)

;}
return listallImports ;}@ Overridepublic void
	computeImports
	( CtElementelement ) {//look for top declaring type of that simpleType if
		( element instanceof CtType){ CtTypesimpleType
		= ( CtType)element;targetType=simpleType.getReference(
		).getTopLevelType();addClassImport(simpleType
		.getReference())
	; scan (
		simpleType);} else { CtType<?>type=element.getParent
		( CtType . class ) ; targetType = type==null?null:type.getReference(
		).getTopLevelType()
	;
scan

(element
) ; }}@ Overridepublic boolean
	isImported (CtReference ref ){ if
		( refinstanceofCtFieldReference){ returnisImportedInFieldImports(
	( CtFieldReference ) ref) ; }else if
		( refinstanceofCtExecutableReference){ returnisImportedInMethodImports(
	( CtExecutableReference ) ref) ; }else if
		( refinstanceofCtTypeReference){ returnisImportedInClassImports(
	( CtTypeReference )
		ref );
	}
else

{return
false ; }}@Overridepublicvoid initWithImports( Iterable
	< CtImport> importCollection ) {for (
		CtImportctImport:importCollection){this. usedImport.put(ctImport
	,
Boolean

. FALSE );}}privateboolean isThereAnotherClassWithSameNameInAnotherPackage( CtTypeReference
	< ?> ref ) {for(CtTypeReference typeref
		: this.exploredReferences){if(typeref.getSimpleName().equals( ref .getSimpleName())&&!typeref.getQualifiedName().equals(ref .
			getQualifiedName ()
		)
	)
	{ returntrue
;

}
} return false;}/**
	 * Adds a type to the classImports.
	 */protectedboolean addClassImport( CtTypeReference
	<?>ref){this.exploredReferences
	. add( ref ); if
		( ref==
	null

	) {return false ; } if(targetType!=null&&targetType.getSimpleName().equals( ref .getSimpleName())&&!targetType .
		equals (ref
	)
	) {returnfalse;}if(classImports.containsKey(ref .
		getSimpleName ())){
	return
	isImportedInClassImports
	( ref);}// don't import unnamed package elementsif ( ref . getPackage()==null||ref.getPackage( )
		. isUnnamedPackage(
	)

	) {return false ; } if(targetType!=null&& targetType .canAccess (
		ref
		) ==false
	)

	{ //ref type is not visible in targetType we must not add import for it, java compiler would fail on that.returnfalse;}if(this .
		isThereAnotherClassWithSameNameInAnotherPackage (ref
	)

	)
	{

	return false; } // we want to be sure that we are not importing a class because a static field or method we already imported// moreover we make exception for same package classes to avoid problems in FQN mode if
		( targetType
			!= null ) {try{CtElementparent=
			ref .getParent ( ); if
				( parent !=null){parent=
				parent .getParent ( ); if
					( parent!=null ) {if ( (parent instanceof CtFieldAccess) || (parent instanceof CtExecutable)|| (

						parent instanceofCtInvocation
						) ){
						CtTypeReference declaringType ; CtReferencereference;CtPackageReferencepack=
						targetType .getPackage ( ); if
							( parent instanceof CtFieldAccess){ CtFieldAccessfield
							= ( CtFieldAccess )parent;CtFieldReferencelocalReference=
							field . getVariable();declaringType=
							localReference . getDeclaringType(
						) ; reference =localReference ; }else if
							( parent instanceof CtExecutable){ CtExecutableexec
							= ( CtExecutable )parent;CtExecutableReferencelocalReference=
							exec . getReference();declaringType=
							localReference . getDeclaringType(
						) ; reference =localReference ; }else if
							( parent instanceof CtInvocation){ CtInvocationinvo
							= ( CtInvocation )parent;CtExecutableReferencelocalReference=
							invo . getExecutable();declaringType=
							localReference . getDeclaringType(
						) ; reference
							= localReference ;}
							else { declaringType=
						null

						; reference= null ; } if(reference!=null &&
							isImported
							( reference) ) {// if we are in the **same** package we do the import for test with method isImported if
								( declaringType!=null){if ( declaringType . getPackage()!=null&&!declaringType.getPackage( )
									.
									isUnnamedPackage ()){// ignore java.lang packageif(!"java.lang".equals(declaringType.getPackage() .
										getSimpleName
										( ))){// ignore type in same packageif(declaringType.getPackage
												().getSimpleName().equals(pack .
											getSimpleName())){classImports.put( ref.getSimpleName
											( ),
										ref
									)
								;
							return
							true ;}
						}
					}
				}
			return
		false ; }} }} }
		catch
		( ParentNotInitializedException e ){}CtPackageReferencepack=
		targetType .getPackage ( ) ; if(pack!=null && ref . getPackage()!=null&&!ref.getPackage( )
			.
			isUnnamedPackage ()){// ignore java.lang packageif("java.lang".equals(ref.getPackage() .
				getSimpleName ()
			) ) {
				return
				false ;}else{// ignore type in same packageif(ref.getPackage
						().getSimpleName().equals(pack .
					getSimpleName ()
				)
			)
		{
	return

	false;}}}}classImports.put( ref.getSimpleName
	( ),
ref

) ; returntrue; }private boolean
	setImportUsed(CtImportctImport){this. usedImport.put
	( ctImport,
true

) ; returntrue; }private boolean
	isAlreadyInUsedImport ( CtReference ref)
	{ String refQualifiedName =""

	; CtTypeReference refDeclaringType =null
	; boolean isTypeRef =false
	; boolean isExecRef =false

	; booleanisFieldRef = false; if
		( ref instanceofCtTypeReference){ refQualifiedName=((CtTypeReference)ref
		) . getQualifiedName(
	) ; isTypeRef =true ; }else if
		( ref instanceofCtExecutableReference){ refDeclaringType=((CtExecutableReference)ref
		) . getDeclaringType(
	) ; isExecRef =true ; }else if
		( ref instanceofCtFieldReference){ refDeclaringType=((CtFieldReference)ref
		) . getDeclaringType(); refQualifiedName=((CtFieldReference)ref
		) . getQualifiedName(
	)

	; isFieldRef= true ; }for(CtImportctImport:this. usedImport
		. keySet()){switch( ctImport
			. getImportKind(
				) ){case METHOD
					: if ( isExecRef){ CtExecutableReferenceexecRef=(CtExecutableReference)
					ctImport . getReference ();CtTypeReferencedeclaringType=

					execRef .getDeclaringType();if(execRef.getSimpleName().equals( ref . getSimpleName ( ) )&&declaringType!=null&&declaringType .
						equals (refDeclaringType)){returnthis
					.
				setImportUsed
				(ctImport

			) ;}
				} break;case FIELD
					: if ( isFieldRef){ CtFieldReferenceimportFieldRef=(CtFieldReference)
					ctImport .getReference();if(importFieldRef.getQualifiedName() .
						equals (refQualifiedName)){returnthis
					.
				setImportUsed
				(ctImport

			) ;}
				} break;case ALL_STATIC_MEMBERS :if ( ( isExecRef ||isFieldRef )
					&& refDeclaringType != null){StringqualifiedName=
					refDeclaringType . getQualifiedName (); CtTypeMemberWildcardImportReferenceImplimportRef=(CtTypeMemberWildcardImportReferenceImpl)
					ctImport . getReference ();StringimportRefStr=importRef.getTypeReference(
					) .getQualifiedName();if(qualifiedName .
						equals (importRefStr)){returnthis
					.
				setImportUsed
				(ctImport


			) ;}
				} break;case TYPE
					: if ( isTypeRef){ CtTypeReferencetypeReference=(CtTypeReference)

					ctImport .getReference();if(typeReference.getQualifiedName() .
						equals (refQualifiedName)){returnthis
					.
				setImportUsed
				(ctImport

			) ;}
				} break;case ALL_TYPES
					: if ( isTypeRef){StringtypeImportQualifiedName=ctImport.getReference(

					) .getSimpleName();if(refQualifiedName .
						equals (typeImportQualifiedName)){returnthis
					.
				setImportUsed
				(ctImport
		)
	;
	} }break
;

} } returnfalse;}protectedboolean isImportedInClassImports( CtTypeReference
	< ?>ref){if(this .
		isAlreadyInUsedImport (ref
	)

	) {return true ;} if
		( targetType != null){CtPackageReferencepack=

		targetType
		.
		getPackage () ; // we consider that if a class belongs to java.lang or the same package than the actual class // then it is imported by default  if(pack!=null && ref . getPackage()!=null&&!ref.getPackage( )
			.
			isUnnamedPackage ()){// ignore java.lang packageif(!"java.lang".equals(ref.getPackage() .
				getSimpleName
				( ))){// ignore type in same packageif(ref.getPackage
						().getSimpleName().equals(pack .
					getSimpleName ()
				)
			)
		{
	return

	true ;}}}}if(ref .
		equals (targetType
	)

	) {returntrue;}if(!( ref .isImplicit())&&classImports.containsKey(ref .
		getSimpleName()) ) { CtTypeReference<?>exist=classImports.get(ref
		. getSimpleName());returnexist.getQualifiedName().equals(ref
	.
	getQualifiedName ()
)

;
} return false;} /**
	 * This method is used to check if the declaring type has been already imported, or if it is local
	 * In both case we do not want to import it, even in FQN mode.
	 * @param declaringType
	 * @return true if it is local or imported
	 */private boolean
	declaringTypeIsLocalOrImported (CtTypeReference declaringType ){ if

		( declaringType != null){boolean isInCollision=isTypeInCollision
		( declaringType,false) ;
			if ( ! isInCollision){booleanimportSuccess
			= addClassImport(declaringType )
				; if(
			importSuccess
		)

		{ return true ;}}booleanimportedInClassImports
		= isImportedInClassImports ( declaringType);booleaninJavaLang

		= classNamePresentInJavaLang( declaringType ); if
			( importedInClassImports||
		inJavaLang

		) {return true ;} while
			( declaringType!=null){if(declaringType .
				equals (targetType
			)
			) { returntrue;}declaringType=
		declaringType

	.
	getDeclaringType ()
;

}
} return false;} /**
	 * Test if the given executable reference is targeted a method name which is in collision with a method name of the current class
	 * @param ref
	 * @return
	 */private boolean
	isInCollisionWithLocalMethod(CtExecutableReferenceref ) { CtType<?>typeDecl=ref.getParent

	( CtType. class ); if
		( typeDecl != null){StringmethodName=

		ref .getSimpleName(); for ( CtMethod<?>method: typeDecl
			. getAllMethods()){if(method.getSimpleName() .
				equals (methodName
			)
		)
	{
	return true;
}

} } returnfalse; }protected boolean
	addMethodImport
	( CtExecutableReferenceref){// static import is not supported below java 1.5if(ref.getFactory().getEnvironment ( ). getComplianceLevel
		( )<
	5
	) {returnfalse;}if(this .
		isImportedInMethodImports (ref
	)

	)
	{ returntrue;}// if the whole class is imported: no need to import the method.if(declaringTypeIsLocalOrImported(ref .
		getDeclaringType ()
	)

	) {returnfalse;}if(this .
		isInCollisionWithLocalMethod (ref
	)

	){returnfalse;}methodImports.put( ref.getSimpleName

	(
	) ,ref);// if we are in the same package than target type, we also import class to avoid FQN in FQN mode.if ( ref. getDeclaringType
		( )!=null){if(ref.getDeclaringType ( ). getPackage
			( )!=null){if(ref.getDeclaringType().getPackage().equals(this.targetType .
				getPackage())){addClassImport(ref
			.
		getDeclaringType
	(
	) );
}

} } returntrue;}protectedboolean isImportedInMethodImports( CtExecutableReference
	< ?>ref){if(this .
		isAlreadyInUsedImport (ref
	)

	) {returntrue;}if(!( ref .isImplicit())&&methodImports.containsKey(ref .
		getSimpleName()) ) { CtExecutableReference<?>exist=methodImports.get(ref
		. getSimpleName());returngetSignature(exist).equals(
	getSignature
	( ref)
)

; } returnfalse;}privateString getSignature( CtExecutableReference
	< ?>exist){return ( exist . getDeclaringType()!=null?exist.getDeclaringType ( ).
			getQualifiedName ( ) :"")+"."+
exist

. getSignature (); }protected boolean
	addFieldImport
	( CtFieldReferenceref){// static import is not supported below java 1.5if(ref.getFactory().getEnvironment ( ). getComplianceLevel
		( )<
	5
	) {returnfalse;}if(this.fieldImports.containsKey(ref .
		getSimpleName ())){
	return

	isImportedInFieldImports (ref);}if(declaringTypeIsLocalOrImported(ref .
		getDeclaringType ()
	)

	){returnfalse;}fieldImports.put( ref.getSimpleName
	( ),
ref

) ; returntrue;}protectedboolean isImportedInFieldImports( CtFieldReference
	< ?>ref){if(this .
		isAlreadyInUsedImport (ref
	)

	) {returntrue;}if(!( ref .isImplicit())&&fieldImports.containsKey(ref .
		getSimpleName()) ) { CtFieldReference<?>exist=fieldImports.get(ref
		. getSimpleName
			( ));try{if ( exist . getFieldDeclaration()!=null&&exist.getFieldDeclaration().equals(ref .
				getFieldDeclaration ()
			)
		)
		{ return true; }// in some rare cases we could not access to the field, then we do not import it. }
			catch (SpoonClassNotFoundException
		notfound

	)

	{ returnfalse
;

} } returnfalse;}protectedboolean classNamePresentInJavaLang( CtTypeReference
	< ? > ref){BooleanpresentInJavaLang=namesPresentInJavaLang.get(ref
	. getSimpleName( ) ); if
		(
		presentInJavaLang
		==
		null
		)
		{ // The following procedure of determining if the handle is present in Java Lang or// not produces "false positives" if the analyzed source complianceLevel is > 6.// For example, it reports that FunctionalInterface is present in java.lang even// for compliance levels 6, 7. But this is not considered a bad thing, in opposite,// it makes generated code a little more compatible with future versions of Java.if(namesPresentInJavaLang8.contains(
				ref .getSimpleName())||namesPresentInJavaLang9.contains(ref .
			getSimpleName ( ))
		) { presentInJavaLang
			=
			true ;
				}else{// Assuming Spoon's own runtime environment is Java 7+try { Class.forName("java.lang."+ref
				. getSimpleName ()
			) ; presentInJavaLang= true ; }catch (
				NoClassDefFoundError | ClassNotFoundExceptione
			)
		{
		presentInJavaLang=false;}}namesPresentInJavaLang.put( ref.getSimpleName
	(
	) ,presentInJavaLang
)

; }returnpresentInJavaLang; }protectedSet <String >
	lookForLocalVariables(CtElementparent ) { Set <String>result=new

	HashSet
	<
	> () ; // try to get the block container // if the first container is the class, then we are not in a block and we can quit now. while(parent != null&&! (
		parent instanceofCtBlock ) ){ if
			( parentinstanceof
		CtClass
		) { returnresult;}parent=
	parent

	. getParent( ) ;} if
		( parent != null){ CtBlockblock
		= ( CtBlock )parent

		;
		boolean innerClass= false ; // now we have the first container block, we want to check if we're not in an inner class while(parent != null&&! (
			parent instanceof CtClass)){parent=
		parent

		. getParent( ) ;} if
			(
			parent
			!= null){// uhoh it's not a package as a parent, we must in an inner block:// let's find the last block BEFORE the class call: some collision could occur because of variables defined in that blockif(! ( parent.getParent (
				) instanceofCtPackage ) ) { while(parent != null&&! (
					parent instanceof CtBlock)){parent=
				parent

				. getParent( ) ;} if
					( parent !=null) {block
				=
			(
		CtBlock

		) parent ; } }}AccessibleVariablesFinderavf=
		newAccessibleVariablesFinder(block ) ; List<CtVariable>variables=

		avf .find ( ) ;for (
			CtVariablevariable:variables){result.add(variable
		.
	getSimpleName

	( ))
;

}
} return result;} /**
	 * Test if the reference can be imported, i.e. test if the importation could lead to a collision.
	 * @param ref
	 * @return true if the ref should be imported.
	 */protected boolean isTypeInCollision( CtReference
	ref ,boolean fqnMode ) { if(targetType!=null&&targetType.getSimpleName().equals( ref .getSimpleName())&&!targetType .
		equals (ref
	)

	) {
		return true;
		} try{ CtElement parent; if
			( ref instanceofCtTypeReference){parent=
		ref . getParent
			( ) ;}
		else

		{
		parent
		= ref; } // in that case we are trying to import a type because of a literal we are scanning// i.e. a string, an int, etc. if
			( parentinstanceof
		CtLiteral

		){returnfalse ; } Set <String>localVariablesOfBlock=new

		HashSet <> ( ); if
			(parentinstanceofCtField){this.fieldAndMethodsNames. add(((CtField)parent)
		. getSimpleName ( )) ; }else if
			(parentinstanceofCtMethod){this.fieldAndMethodsNames. add(((CtMethod)parent)
		. getSimpleName (
			) ) ;}else{localVariablesOfBlock=this
		.

		lookForLocalVariables (parent); } while(! (
			parent instanceofCtPackage) ) {if ( (parent instanceof CtFieldReference) || (parent instanceof CtExecutableReference)|| (
				parent instanceofCtInvocation
				) ){ CtReference parentType; if
					( parent instanceofCtInvocation){ parentType=((CtInvocation)parent
				) . getExecutable
					( ) ;}else {parentType
				=
				(CtReference)parent ; } LinkedList <String>qualifiedNameTokens=new

				LinkedList
				< >( ) ;// we don't want to test the current ref name, as we risk to create field import and make autoreference if
					(parentType!=parent){qualifiedNameTokens.add(parentType
				.

				getSimpleName ()
				) ;} CtTypeReference typeReference; if
					( parent instanceofCtFieldReference){ typeReference=((CtFieldReference)parent
				) . getDeclaringType () ; }else if
					( parent instanceofCtExecutableReference){ typeReference=((CtExecutableReference)parent
				) . getDeclaringType
					( ) ;}else{ typeReference=((CtInvocation)parent).getExecutable(
				)

				. getDeclaringType( ) ;} if
					(typeReference!=null){qualifiedNameTokens.addFirst(typeReference

					. getSimpleName());if ( typeReference. getPackage
						( ) != null ){StringTokenizertoken=newStringTokenizer(typeReference.getPackage( ).getSimpleName()
						, CtPackage . PACKAGE_SEPARATOR)
						; intindex=0;while( token
							.hasMoreElements()){ qualifiedNameTokens.add(index,token
							.nextToken(
						)
					)
				;
				index ++;}}}if(! qualifiedNameTokens
					.
					isEmpty
					( )){// qualified name token are ordered in the reverse order// if the first package name is a variable name somewhere, it could lead to a collisionif(fieldAndMethodsNames.contains( qualifiedNameTokens .getFirst())||localVariablesOfBlock.contains(qualifiedNameTokens .
						getFirst())){

						qualifiedNameTokens .removeFirst( )
							;
							if
							( fqnMode) { // in case we are testing a type: we should not import it if its entire name is in collision// for example: spoon.Launcher if a field spoon and another one Launcher exists if
								( refinstanceofCtTypeReference){if( qualifiedNameTokens
									. isEmpty(
								)
								)
								{ returntrue ; } // but if the other package names are not a variable name, it's ok to importfor (
									String testedToken:qualifiedNameTokens){if(! fieldAndMethodsNames .contains(testedToken)&&!localVariablesOfBlock .
										contains (testedToken
									)
								)
								{ returntrue

							;
							}
							} return false
								; // However if it is a static method/field, we always accept to import them in this case// It is the last possibility for managing import for us
							}
						else { return
							true
							; }} else { // but if the other package names are not a variable name, it's ok to importfor (
								String testedToken:qualifiedNameTokens){if(! fieldAndMethodsNames .contains(testedToken)&&!localVariablesOfBlock .
									contains (testedToken
								)
							)
							{ returnfalse
						;
					}
				}


			return
			true ; }}}}parent=
		parent
	. getParent () ;} }
		catch (ParentNotInitializedException
	e

	) {return
false
;
