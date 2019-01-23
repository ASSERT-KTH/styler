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
	public Set<CtImport> getAllImports ( ) {Set<CtImport > listallImports = newHashSet<>()

	; for(Map.Entry<CtImport ,Boolean > entry :this.usedImport.entrySet() )
		{ if(entry.getValue() )
			{listallImports.add(entry.getKey())
		;
	}

	} for( CtReference reference :this.classImports.values() )
		{listallImports.add(reference.getFactory().Type().createImport(reference))
	;

	} for( CtReference reference :this.fieldImports.values() )
		{listallImports.add(reference.getFactory().Type().createImport(reference))
	;

	} for( CtReference reference :this.methodImports.values() )
		{listallImports.add(reference.getFactory().Type().createImport(reference))
	;
	} returnlistallImports
;

}@
Override public voidcomputeImports( CtElementelement )
	{
	//look for top declaring type of that simpleType if( element instanceofCtType )
		{ CtType simpleType =(CtType )element
		; targetType =simpleType.getReference().getTopLevelType()
		;addClassImport(simpleType.getReference())
		;scan(simpleType)
	; } else
		{CtType<? > type =element.getParent(CtType.class)
		; targetType = type == null ? null :type.getReference().getTopLevelType()
		;scan(element)
	;
}

}@
Override public booleanisImported( CtReferenceref )
	{ if( ref instanceofCtFieldReference )
		{ returnisImportedInFieldImports((CtFieldReference )ref)
	; } else if( ref instanceofCtExecutableReference )
		{ returnisImportedInMethodImports((CtExecutableReference )ref)
	; } else if( ref instanceofCtTypeReference )
		{ returnisImportedInClassImports((CtTypeReference )ref)
	; } else
		{ returnfalse
	;
}

}@
Override public voidinitWithImports(Iterable<CtImport >importCollection )
	{ for( CtImport ctImport :importCollection )
		{this.usedImport.put(ctImport ,Boolean.FALSE)
	;
}

} private booleanisThereAnotherClassWithSameNameInAnotherPackage(CtTypeReference<? >ref )
	{ for( CtTypeReference typeref :this.exploredReferences )
		{ if(typeref.getSimpleName().equals(ref.getSimpleName() ) &&!typeref.getQualifiedName().equals(ref.getQualifiedName()) )
			{ returntrue
		;
	}
	} returnfalse
;

}
/**
	 * Adds a type to the classImports.
	 */ protected booleanaddClassImport(CtTypeReference<? >ref )
	{this.exploredReferences.add(ref)
	; if( ref ==null )
		{ returnfalse
	;

	} if( targetType != null &&targetType.getSimpleName().equals(ref.getSimpleName() ) &&!targetType.equals(ref) )
		{ returnfalse
	;
	} if(classImports.containsKey(ref.getSimpleName()) )
		{ returnisImportedInClassImports(ref)
	;
	}
	// don't import unnamed package elements if(ref.getPackage( ) == null ||ref.getPackage().isUnnamedPackage() )
		{ returnfalse
	;

	} if( targetType != null &&targetType.canAccess(ref ) ==false )
		{
		//ref type is not visible in targetType we must not add import for it, java compiler would fail on that. returnfalse
	;

	} if(this.isThereAnotherClassWithSameNameInAnotherPackage(ref) )
		{ returnfalse
	;

	}
	// we want to be sure that we are not importing a class because a static field or method we already imported

	// moreover we make exception for same package classes to avoid problems in FQN mode if( targetType !=null )
		{ try
			{ CtElement parent =ref.getParent()
			; if( parent !=null )
				{ parent =parent.getParent()
				; if( parent !=null )
					{ if(( parent instanceofCtFieldAccess ) ||( parent instanceofCtExecutable ) ||( parent instanceofCtInvocation) )

						{ CtTypeReferencedeclaringType
						; CtReferencereference
						; CtPackageReference pack =targetType.getPackage()
						; if( parent instanceofCtFieldAccess )
							{ CtFieldAccess field =(CtFieldAccess )parent
							; CtFieldReference localReference =field.getVariable()
							; declaringType =localReference.getDeclaringType()
							; reference =localReference
						; } else if( parent instanceofCtExecutable )
							{ CtExecutable exec =(CtExecutable )parent
							; CtExecutableReference localReference =exec.getReference()
							; declaringType =localReference.getDeclaringType()
							; reference =localReference
						; } else if( parent instanceofCtInvocation )
							{ CtInvocation invo =(CtInvocation )parent
							; CtExecutableReference localReference =invo.getExecutable()
							; declaringType =localReference.getDeclaringType()
							; reference =localReference
						; } else
							{ declaringType =null
							; reference =null
						;

						} if( reference != null &&isImported(reference) )
							{
							// if we are in the **same** package we do the import for test with method isImported if( declaringType !=null )
								{ if(declaringType.getPackage( ) != null &&!declaringType.getPackage().isUnnamedPackage() )
									{
									// ignore java.lang package if(!"java.lang".equals(declaringType.getPackage().getSimpleName()) )
										{
										// ignore type in same package if(declaringType.getPackage().getSimpleName(
												).equals(pack.getSimpleName()) )
											{classImports.put(ref.getSimpleName() ,ref)
											; returntrue
										;
									}
								}
							}
							} returnfalse
						;
					}
				}
			}
		} } catch( ParentNotInitializedExceptione )
		{
		} CtPackageReference pack =targetType.getPackage()
		; if( pack != null &&ref.getPackage( ) != null &&!ref.getPackage().isUnnamedPackage() )
			{
			// ignore java.lang package if("java.lang".equals(ref.getPackage().getSimpleName()) )
				{ returnfalse
			; } else
				{
				// ignore type in same package if(ref.getPackage().getSimpleName(
						).equals(pack.getSimpleName()) )
					{ returnfalse
				;
			}
		}
	}

	}classImports.put(ref.getSimpleName() ,ref)
	; returntrue
;

} private booleansetImportUsed( CtImportctImport )
	{this.usedImport.put(ctImport ,true)
	; returntrue
;

} private booleanisAlreadyInUsedImport( CtReferenceref )
	{ String refQualifiedName =""
	; CtTypeReference refDeclaringType =null

	; boolean isTypeRef =false
	; boolean isExecRef =false
	; boolean isFieldRef =false

	; if( ref instanceofCtTypeReference )
		{ refQualifiedName =((CtTypeReference )ref).getQualifiedName()
		; isTypeRef =true
	; } else if( ref instanceofCtExecutableReference )
		{ refDeclaringType =((CtExecutableReference )ref).getDeclaringType()
		; isExecRef =true
	; } else if( ref instanceofCtFieldReference )
		{ refDeclaringType =((CtFieldReference )ref).getDeclaringType()
		; refQualifiedName =((CtFieldReference )ref).getQualifiedName()
		; isFieldRef =true
	;

	} for( CtImport ctImport :this.usedImport.keySet() )
		{ switch(ctImport.getImportKind() )
			{ caseMETHOD
				: if(isExecRef )
					{ CtExecutableReference execRef =(CtExecutableReference )ctImport.getReference()
					; CtTypeReference declaringType =execRef.getDeclaringType()

					; if(execRef.getSimpleName().equals(ref.getSimpleName() ) && declaringType != null &&declaringType.equals(refDeclaringType) )
						{ returnthis.setImportUsed(ctImport)
					;
				}
				}break

			; caseFIELD
				: if(isFieldRef )
					{ CtFieldReference importFieldRef =(CtFieldReference )ctImport.getReference()
					; if(importFieldRef.getQualifiedName().equals(refQualifiedName) )
						{ returnthis.setImportUsed(ctImport)
					;
				}
				}break

			; caseALL_STATIC_MEMBERS
				: if(( isExecRef ||isFieldRef ) && refDeclaringType !=null )
					{ String qualifiedName =refDeclaringType.getQualifiedName()
					; CtTypeMemberWildcardImportReferenceImpl importRef =(CtTypeMemberWildcardImportReferenceImpl )ctImport.getReference()
					; String importRefStr =importRef.getTypeReference().getQualifiedName()
					; if(qualifiedName.equals(importRefStr) )
						{ returnthis.setImportUsed(ctImport)
					;
				}
				}break


			; caseTYPE
				: if(isTypeRef )
					{ CtTypeReference typeReference =(CtTypeReference )ctImport.getReference()

					; if(typeReference.getQualifiedName().equals(refQualifiedName) )
						{ returnthis.setImportUsed(ctImport)
					;
				}
				}break

			; caseALL_TYPES
				: if(isTypeRef )
					{ String typeImportQualifiedName =ctImport.getReference().getSimpleName()

					; if(refQualifiedName.equals(typeImportQualifiedName) )
						{ returnthis.setImportUsed(ctImport)
					;
				}
				}break
		;
	}
	} returnfalse
;

} protected booleanisImportedInClassImports(CtTypeReference<? >ref )
	{ if(this.isAlreadyInUsedImport(ref) )
		{ returntrue
	;

	} if( targetType !=null )
		{ CtPackageReference pack =targetType.getPackage()

		;
		// we consider that if a class belongs to java.lang or the same package than the actual class
		// then it is imported by default if( pack != null  &&ref.getPackage( ) != null &&!ref.getPackage().isUnnamedPackage() )
			{
			// ignore java.lang package if(!"java.lang".equals(ref.getPackage().getSimpleName()) )
				{
				// ignore type in same package if(ref.getPackage().getSimpleName(
						).equals(pack.getSimpleName()) )
					{ returntrue
				;
			}
		}
	}

	} if(ref.equals(targetType) )
		{ returntrue
	;

	} if(!(ref.isImplicit() ) &&classImports.containsKey(ref.getSimpleName()) )
		{CtTypeReference<? > exist =classImports.get(ref.getSimpleName())
		; returnexist.getQualifiedName().equals(ref.getQualifiedName())
	;
	} returnfalse
;

}
/**
	 * This method is used to check if the declaring type has been already imported, or if it is local
	 * In both case we do not want to import it, even in FQN mode.
	 * @param declaringType
	 * @return true if it is local or imported
	 */ private booleandeclaringTypeIsLocalOrImported( CtTypeReferencedeclaringType )
	{ if( declaringType !=null )

		{ boolean isInCollision =isTypeInCollision(declaringType ,false)
		; if(!isInCollision )
			{ boolean importSuccess =addClassImport(declaringType)
			; if(importSuccess )
				{ returntrue
			;
		}

		} boolean importedInClassImports =isImportedInClassImports(declaringType)
		; boolean inJavaLang =classNamePresentInJavaLang(declaringType)

		; if( importedInClassImports ||inJavaLang )
			{ returntrue
		;

		} while( declaringType !=null )
			{ if(declaringType.equals(targetType) )
				{ returntrue
			;
			} declaringType =declaringType.getDeclaringType()
		;

	}
	} returnfalse
;

}
/**
	 * Test if the given executable reference is targeted a method name which is in collision with a method name of the current class
	 * @param ref
	 * @return
	 */ private booleanisInCollisionWithLocalMethod( CtExecutableReferenceref )
	{CtType<? > typeDecl =ref.getParent(CtType.class)

	; if( typeDecl !=null )
		{ String methodName =ref.getSimpleName()

		; for(CtMethod<? > method :typeDecl.getAllMethods() )
			{ if(method.getSimpleName().equals(methodName) )
				{ returntrue
			;
		}
	}
	} returnfalse
;

} protected booleanaddMethodImport( CtExecutableReferenceref )
	{
	// static import is not supported below java 1.5 if(ref.getFactory().getEnvironment().getComplianceLevel( ) <5 )
		{ returnfalse
	;
	} if(this.isImportedInMethodImports(ref) )
		{ returntrue
	;

	}
	// if the whole class is imported: no need to import the method. if(declaringTypeIsLocalOrImported(ref.getDeclaringType()) )
		{ returnfalse
	;

	} if(this.isInCollisionWithLocalMethod(ref) )
		{ returnfalse
	;

	}methodImports.put(ref.getSimpleName() ,ref)

	;
	// if we are in the same package than target type, we also import class to avoid FQN in FQN mode. if(ref.getDeclaringType( ) !=null )
		{ if(ref.getDeclaringType().getPackage( ) !=null )
			{ if(ref.getDeclaringType().getPackage().equals(this.targetType.getPackage()) )
				{addClassImport(ref.getDeclaringType())
			;
		}
	}
	} returntrue
;

} protected booleanisImportedInMethodImports(CtExecutableReference<? >ref )
	{ if(this.isAlreadyInUsedImport(ref) )
		{ returntrue
	;

	} if(!(ref.isImplicit() ) &&methodImports.containsKey(ref.getSimpleName()) )
		{CtExecutableReference<? > exist =methodImports.get(ref.getSimpleName())
		; returngetSignature(exist).equals(getSignature(ref))
	;
	} returnfalse
;

} private StringgetSignature(CtExecutableReference<? >exist )
	{ return(exist.getDeclaringType( ) != null ?exist.getDeclaringType().getQualifiedName( ) :""
			) + "." +exist.getSignature()
;

} protected booleanaddFieldImport( CtFieldReferenceref )
	{
	// static import is not supported below java 1.5 if(ref.getFactory().getEnvironment().getComplianceLevel( ) <5 )
		{ returnfalse
	;
	} if(this.fieldImports.containsKey(ref.getSimpleName()) )
		{ returnisImportedInFieldImports(ref)
	;

	} if(declaringTypeIsLocalOrImported(ref.getDeclaringType()) )
		{ returnfalse
	;

	}fieldImports.put(ref.getSimpleName() ,ref)
	; returntrue
;

} protected booleanisImportedInFieldImports(CtFieldReference<? >ref )
	{ if(this.isAlreadyInUsedImport(ref) )
		{ returntrue
	;

	} if(!(ref.isImplicit() ) &&fieldImports.containsKey(ref.getSimpleName()) )
		{CtFieldReference<? > exist =fieldImports.get(ref.getSimpleName())
		; try
			{ if(exist.getFieldDeclaration( ) != null &&exist.getFieldDeclaration().equals(ref.getFieldDeclaration()) )
				{ returntrue
			;
		}
		// in some rare cases we could not access to the field, then we do not import it. } catch( SpoonClassNotFoundExceptionnotfound )
			{ returnfalse
		;

	}

	} returnfalse
;

} protected booleanclassNamePresentInJavaLang(CtTypeReference<? >ref )
	{ Boolean presentInJavaLang =namesPresentInJavaLang.get(ref.getSimpleName())
	; if( presentInJavaLang ==null )
		{
		// The following procedure of determining if the handle is present in Java Lang or
		// not produces "false positives" if the analyzed source complianceLevel is > 6.
		// For example, it reports that FunctionalInterface is present in java.lang even
		// for compliance levels 6, 7. But this is not considered a bad thing, in opposite,
		// it makes generated code a little more compatible with future versions of Java. if(namesPresentInJavaLang8.contains(ref.getSimpleName()
				) ||namesPresentInJavaLang9.contains(ref.getSimpleName()) )
			{ presentInJavaLang =true
		; } else
			{
			// Assuming Spoon's own runtime environment is Java 7+ try
				{Class.forName( "java.lang." +ref.getSimpleName())
				; presentInJavaLang =true
			; } catch( NoClassDefFoundError | ClassNotFoundExceptione )
				{ presentInJavaLang =false
			;
		}
		}namesPresentInJavaLang.put(ref.getSimpleName() ,presentInJavaLang)
	;
	} returnpresentInJavaLang
;

} protectedSet<String >lookForLocalVariables( CtElementparent )
	{Set<String > result = newHashSet<>()

	;
	// try to get the block container
	// if the first container is the class, then we are not in a block and we can quit now. while( parent != null &&!( parent instanceofCtBlock) )
		{ if( parent instanceofCtClass )
			{ returnresult
		;
		} parent =parent.getParent()
	;

	} if( parent !=null )
		{ CtBlock block =(CtBlock )parent
		; boolean innerClass =false

		;
		// now we have the first container block, we want to check if we're not in an inner class while( parent != null &&!( parent instanceofCtClass) )
			{ parent =parent.getParent()
		;

		} if( parent !=null )
			{
			// uhoh it's not a package as a parent, we must in an inner block:
			// let's find the last block BEFORE the class call: some collision could occur because of variables defined in that block if(!(parent.getParent( ) instanceofCtPackage) )
				{ while( parent != null &&!( parent instanceofCtBlock) )
					{ parent =parent.getParent()
				;

				} if( parent !=null )
					{ block =(CtBlock )parent
				;
			}
		}

		} AccessibleVariablesFinder avf = newAccessibleVariablesFinder(block)
		;List<CtVariable > variables =avf.find()

		; for( CtVariable variable :variables )
			{result.add(variable.getSimpleName())
		;
	}

	} returnresult
;

}
/**
	 * Test if the reference can be imported, i.e. test if the importation could lead to a collision.
	 * @param ref
	 * @return true if the ref should be imported.
	 */ protected booleanisTypeInCollision( CtReferenceref , booleanfqnMode )
	{ if( targetType != null &&targetType.getSimpleName().equals(ref.getSimpleName() ) &&!targetType.equals(ref) )
		{ returntrue
	;

	} try
		{ CtElementparent
		; if( ref instanceofCtTypeReference )
			{ parent =ref.getParent()
		; } else
			{ parent =ref
		;

		}
		// in that case we are trying to import a type because of a literal we are scanning
		// i.e. a string, an int, etc. if( parent instanceofCtLiteral )
			{ returnfalse
		;

		}Set<String > localVariablesOfBlock = newHashSet<>()

		; if( parent instanceofCtField )
			{this.fieldAndMethodsNames.add(((CtField )parent).getSimpleName())
		; } else if( parent instanceofCtMethod )
			{this.fieldAndMethodsNames.add(((CtMethod )parent).getSimpleName())
		; } else
			{ localVariablesOfBlock =this.lookForLocalVariables(parent)
		;

		} while(!( parent instanceofCtPackage) )
			{ if(( parent instanceofCtFieldReference ) ||( parent instanceofCtExecutableReference ) ||( parent instanceofCtInvocation) )
				{ CtReferenceparentType
				; if( parent instanceofCtInvocation )
					{ parentType =((CtInvocation )parent).getExecutable()
				; } else
					{ parentType =(CtReference )parent
				;
				}LinkedList<String > qualifiedNameTokens = newLinkedList<>()

				;
				// we don't want to test the current ref name, as we risk to create field import and make autoreference if( parentType !=parent )
					{qualifiedNameTokens.add(parentType.getSimpleName())
				;

				} CtTypeReferencetypeReference
				; if( parent instanceofCtFieldReference )
					{ typeReference =((CtFieldReference )parent).getDeclaringType()
				; } else if( parent instanceofCtExecutableReference )
					{ typeReference =((CtExecutableReference )parent).getDeclaringType()
				; } else
					{ typeReference =((CtInvocation )parent).getExecutable().getDeclaringType()
				;

				} if( typeReference !=null )
					{qualifiedNameTokens.addFirst(typeReference.getSimpleName())

					; if(typeReference.getPackage( ) !=null )
						{ StringTokenizer token = newStringTokenizer(typeReference.getPackage().getSimpleName() ,CtPackage.PACKAGE_SEPARATOR)
						; int index =0
						; while(token.hasMoreElements() )
							{qualifiedNameTokens.add(index ,token.nextToken())
							;index++
						;
					}
				}
				} if(!qualifiedNameTokens.isEmpty() )
					{
					// qualified name token are ordered in the reverse order
					// if the first package name is a variable name somewhere, it could lead to a collision if(fieldAndMethodsNames.contains(qualifiedNameTokens.getFirst() ) ||localVariablesOfBlock.contains(qualifiedNameTokens.getFirst()) )
						{qualifiedNameTokens.removeFirst()

						; if(fqnMode )
							{
							// in case we are testing a type: we should not import it if its entire name is in collision
							// for example: spoon.Launcher if a field spoon and another one Launcher exists if( ref instanceofCtTypeReference )
								{ if(qualifiedNameTokens.isEmpty() )
									{ returntrue
								;
								}
								// but if the other package names are not a variable name, it's ok to import for( String testedToken :qualifiedNameTokens )
									{ if(!fieldAndMethodsNames.contains(testedToken ) &&!localVariablesOfBlock.contains(testedToken) )
										{ returntrue
									;
								}
								} returnfalse

							;
							// However if it is a static method/field, we always accept to import them in this case
							// It is the last possibility for managing import for us } else
								{ returntrue
							;
						} } else
							{
							// but if the other package names are not a variable name, it's ok to import for( String testedToken :qualifiedNameTokens )
								{ if(!fieldAndMethodsNames.contains(testedToken ) &&!localVariablesOfBlock.contains(testedToken) )
									{ returnfalse
								;
							}
							} returntrue
						;
					}
				}


			}
			} parent =parent.getParent()
		;
	} } catch( ParentNotInitializedExceptione )
		{ returnfalse
	;

	} returnfalse
;
}
