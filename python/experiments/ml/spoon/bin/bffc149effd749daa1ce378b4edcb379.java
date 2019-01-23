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
package spoon.reflect.factory;

import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.CtModelImpl;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.BodyHolderSourcePosition;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.eval.PartialEvaluator;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.visitor.GenericTypeAdapter;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implements {@link Factory}
 */
public class FactoryImpl implements Factory, Serializable {

	private static final long serialVersionUID = 1L;

	private transient Factory parentFactory;

	/**
	 * Returns the parent of this factory. When an element is not found in a
	 * factory, it can be looked up in its parent factory using a delegation
	 * model.
	 */
	public Factory getParentFactory() {
		return parentFactory;
	}

	private transient AnnotationFactory annotation;

	/**
	 * The {@link CtAnnotationType} sub-factory.
	 */
	@Override
	public AnnotationFactory Annotation() {
		if (annotation == null) {
			annotation = new AnnotationFactory(this);
		}
		return annotation;
	}

	private transient ClassFactory clazz;

	/**
	 * The {@link CtClass} sub-factory.
	 */
	@Override
	public ClassFactory Class() {
		if (clazz == null) {
			clazz = new ClassFactory(this);
		}
		return clazz;
	}

	private transient CodeFactory code;

	/**
	 * The {@link spoon.reflect.code.CtCodeElement} sub-factory.
	 */
	@Override
	public CodeFactory Code() {
		if (code == null) {
			code = new CodeFactory(this);
		}
		return code;
	}

	private transient ConstructorFactory constructor;

	/**
	 * The {@link CtConstructor} sub-factory.
	 */
	@Override
	public ConstructorFactory Constructor() {
		if (constructor == null) {
			constructor = new ConstructorFactory(this);
		}
		return constructor;
	}

	private transient CoreFactory core;

	/**
	 * The core factory.
	 */
	@Override
	public CoreFactory Core() {
		if (core == null) {
			//During deserialization, the transient field core, is null
			core = new DefaultCoreFactory();
			core.setMainFactory(this);
		}
		return core;
	}

	private transient EnumFactory enumF;

	/**
	 * The {@link CtEnum} sub-factory.
	 */
	@Override
	public EnumFactory Enum() {
		if (enumF == null) {
			enumF = new EnumFactory(this);
		}
		return enumF;
	}

	private transient Environment environment;

	/**
	 * Gets the Spoon environment that encloses this factory.
	 */
	@Override
	public Environment getEnvironment() {
		if (environment == null) {
			environment = new StandardEnvironment();
		}
		return environment;
	}

	private transient ExecutableFactory executable;

	/**
	 * The {@link CtExecutable} sub-factory.
	 */
	@Override
	public ExecutableFactory Executable() {
		if (executable == null) {
			executable = new ExecutableFactory(this);
		}
		return executable;
	}

	private transient EvalFactory eval;

	/**
	 * The evaluators sub-factory.
	 */
	@Override
	public EvalFactory Eval() {
		if (eval == null) {
			eval = new EvalFactory(this);
		}
		return eval;
	}

	private transient FieldFactory field;

	/**
	 * The {@link CtField} sub-factory.
	 */
	@Override
	public FieldFactory Field() {
		if (field == null) {
			field = new FieldFactory(this);
		}
		return field;
	}

	/**
	 * The {@link CtInterface} sub-factory.
	 */
	private transient InterfaceFactory interfaceF;

	/**
	 * The {@link CtInterface} sub-factory.
	 */
	@Override
	public InterfaceFactory Interface() {
		if (interfaceF == null) {
			interfaceF = new InterfaceFactory(this);
		}
		return interfaceF;
	}

	private transient MethodFactory methodF;

	/**
	 * The {@link CtMethod} sub-factory.
	 */
	@Override
	public MethodFactory Method() {
		if (methodF == null) {
			methodF = new MethodFactory(this);
		}
		return methodF;
	}

	private transient PackageFactory packageF;

	/**
	 * The {@link CtPackage} sub-factory.
	 */
	@Override
	public PackageFactory Package() {
		if (packageF == null) {
			packageF = new PackageFactory(this);
		}
		return packageF;
	}

	private transient CompilationUnitFactory compilationUnit;

	/**
	 * The {@link CompilationUnit} sub-factory.
	 */
	@Override
	public CompilationUnitFactory CompilationUnit() {
		if (compilationUnit == null) {
			compilationUnit = new CompilationUnitFactory(this);
		}
		return compilationUnit;
	}

	private transient TypeFactory type;

	/**
	 * The {@link CtType} sub-factory.
	 */
	@Override
	public TypeFactory Type() {
		if (type == null) {
			type = new TypeFactory(this);
		}
		return type;
	}

	private transient QueryFactory query;

	/**
	 * The query sub-factory.
	 */
	@Override
	public QueryFactory Query() {
		if (query == null) {
			query = new QueryFactory(this);
		}
		return query;
	}

	private transient ModuleFactory module;

	/**
	 * The module sub-factory
	 */
	@Override
	public ModuleFactory Module() {
		if (module == null) {
			module = new ModuleFactory(this);
		}
		return module;
	}


	/**
	 * A constructor that takes the parent factory
	 */
	public FactoryImpl(CoreFactory coreFactory, Environment environment, Factory parentFactory) {
		this.environment = environment;
		this.core = coreFactory;
		this.core.setMainFactory(this);
		this.parentFactory = parentFactory;
	}

	/**
	 * Should not be called directly. Use {@link spoon.Launcher#createFactory()} instead.
	 */
	public FactoryImpl(CoreFactory coreFactory, Environment environment) {
		this(coreFactory, environment, null);
	}

	// Deduplication
	// See http://shipilev.net/talks/joker-Oct2014-string-catechism.pdf

	private static class Dedup {
		Map<String, String> cache = new HashMap<>();
		Random random = ThreadLocalRandom.current();
	}

	/**
	 * Note this is an instance field. To avoid memory leaks and dedup being
	 * targeted to each Spoon Launching, that could differ a lot by
	 * frequently used symbols.
	 */
	private transient ThreadLocal<Dedup> threadLocalDedup = new ThreadLocal<Dedup>() {
		@Override
		protected Dedup initialValue() {
			return new Dedup();
		}
	};

	/**
	 * Returns a String equal to the given symbol. Performs probablilistic
	 * deduplication.
	 */
	public String dedup(String symbol) {
		Dedup dedup = threadLocalDedup.get();
		Map<String, String> cache = dedup.cache;
		String cached;
		if ((cached = cache.get(symbol)) != null) {
			return cached;
		} else {
			// Puts the symbol into cache with 20% probability
			int prob = (int) (Integer.MIN_VALUE + (0.2 * (1L << 32)));
			if (dedup.random.nextInt() < prob) {
				cache.put(symbol, symbol);
			}
			return symbol;
		}
	}

	/**
	 * Needed to restore state of transient fields during reading from stream
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		threadLocalDedup = new ThreadLocal<Dedup>() {
			@Override
			protected Dedup initialValue() {
				return new Dedup();
			}
		};
		in.defaultReadObject();
	}

	private final CtModel model = new CtModelImpl(this);

	@Override
	public CtModel getModel() {
		return model;
	}

	@Override
	public <A extends Annotation> CtAnnotation<A> createAnnotation(CtTypeReference<A> annotationType) {
		return Code().createAnnotation(annotationType);
	}

	@Override
	public <A, T extends A> CtAssignment<A, T> createVariableAssignment(CtVariableReference<A> variable, boolean isStatic, CtExpression<T> expression) {
		return Code().createVariableAssignment(variable, isStatic, expression);
	}

	@Override
	public <R> CtStatementList createStatementList(CtBlock<R> block) {
		return Code().createStatementList(block);
	}

	@Override
	public <T extends CtStatement> CtBlock<?> createCtBlock(T element) {
		return Code().createCtBlock(element);
	}

	@Override
	public <T> CtBinaryOperator<T> createBinaryOperator(CtExpression<?> left, CtExpression<?> right, BinaryOperatorKind kind) {
		return Code().createBinaryOperator(left, right, kind);
	}

	@Override
	public <T> CtCatchVariable<T> createCatchVariable(CtTypeReference<T> type, String name, ModifierKind... modifierKinds) {
		return Code().createCatchVariable(type, name, modifierKinds);
	}

	@Override
	public <T> CtCodeSnippetExpression<T> createCodeSnippetExpression(String expression) {
		return Code().createCodeSnippetExpression(expression);
	}

	@Override
	public <T> CtConstructorCall<T> createConstructorCall(CtTypeReference<T> type, CtExpression<?>... parameters) {
		return Code().createConstructorCall(type, parameters);
	}

	@Override
	public <T> CtFieldAccess<Class<T>> createClassAccess(CtTypeReference<T> type) {
		return Code().createClassAccess(type);
	}

	@Override
	public <T> CtInvocation<T> createInvocation(CtExpression<?> target, CtExecutableReference<T> executable, List<CtExpression<?>> arguments) {
		return Code().createInvocation(target, executable, arguments);
	}

	@Override
	public <T> CtInvocation<T> createInvocation(CtExpression<?> target, CtExecutableReference<T> executable, CtExpression<?>... arguments) {
		return Code().createInvocation(target, executable, arguments);
	}

	@Override
	public <T> CtLiteral<T> createLiteral(T value) {
		return Code().createLiteral(value);
	}

	@Override
	public <T> CtLocalVariable<T> createLocalVariable(CtTypeReference<T> type, String name, CtExpression<T> defaultExpression) {
		return Code().createLocalVariable(type, name, defaultExpression);
	}

	@SuppressWarnings(value = "unchecked")
	@Override
	public <T> CtNewArray<T[]>createLiteralArray(T[ ]value )
		{ returnCode().createLiteralArray(value)
	;

	}@
	Override public<T >CtNewClass<T >createNewClass(CtTypeReference<T >type ,CtClass<? >anonymousClass ,CtExpression<?> ...parameters )
		{ returnCode().createNewClass(type ,anonymousClass ,parameters)
	;

	}@
	Override public<T > CtStatementListcreateVariableAssignments(List< ? extendsCtVariable<T> >variables ,List< ? extendsCtExpression<T> >expressions )
		{ returnCode().createVariableAssignments(variables ,expressions)
	;

	}@
	Override public<T >CtThisAccess<T >createThisAccess(CtTypeReference<T >type )
		{ returnCode().createThisAccess(type)
	;

	}@
	Override public<T >CtThisAccess<T >createThisAccess(CtTypeReference<T >type , booleanisImplicit )
		{ returnCode().createThisAccess(type ,isImplicit)
	;

	}@
	Override public<T >CtTypeAccess<T >createTypeAccess(CtTypeReference<T >accessedType )
		{ returnCode().createTypeAccess(accessedType)
	;

	}@
	Override public<T >CtTypeAccess<T >createTypeAccess(CtTypeReference<T >accessedType , booleanisImplicit )
		{ returnCode().createTypeAccess(accessedType ,isImplicit)
	;

	}@
	Override public<T >CtTypeAccess<T >createTypeAccessWithoutCloningReference(CtTypeReference<T >accessedType )
		{ returnCode().createTypeAccessWithoutCloningReference(accessedType)
	;

	}@
	Override public<T >CtVariableAccess<T >createVariableRead(CtVariableReference<T >variable , booleanisStatic )
		{ returnCode().createVariableRead(variable ,isStatic)
	;

	}@
	Override public<T >CtField<T >createCtField( Stringname ,CtTypeReference<T >type , Stringexp ,ModifierKind ...visibilities )
		{ returnCode().createCtField(name ,type ,exp ,visibilities)
	;

	}@
	Override public<T >CtCatchVariableReference<T >createCatchVariableReference(CtCatchVariable<T >catchVariable )
		{ returnCode().createCatchVariableReference(catchVariable)
	;

	}@
	Override public<T >CtLocalVariableReference<T >createLocalVariableReference(CtLocalVariable<T >localVariable )
		{ returnCode().createLocalVariableReference(localVariable)
	;

	}@
	Override public<T >CtLocalVariableReference<T >createLocalVariableReference(CtTypeReference<T >type , Stringname )
		{ returnCode().createLocalVariableReference(type ,name)
	;

	}@
	Override public<T >CtTypeReference<T >createCtTypeReference(Class<? >originalClass )
		{ returnCode().createCtTypeReference(originalClass)
	;

	}@
	Override publicList<CtExpression<?> >createVariableReads(List< ? extendsCtVariable<?> >variables )
		{ returnCode().createVariableReads(variables)
	;

	}@
	Override public CtCatchcreateCtCatch( StringnameCatch ,Class< ? extendsThrowable >exception ,CtBlock<? >ctBlock )
		{ returnCode().createCtCatch(nameCatch ,exception ,ctBlock)
	;

	}@
	Override public CtCodeSnippetStatementcreateCodeSnippetStatement( Stringstatement )
		{ returnCode().createCodeSnippetStatement(statement)
	;

	}@
	Override public CtCommentcreateComment( Stringcontent ,CtComment. CommentTypetype )
		{ returnCode().createComment(content ,type)
	;

	}@
	Override public CtCommentcreateInlineComment( Stringcontent )
		{ returnCode().createInlineComment(content)
	;

	}@
	Override public CtJavaDocTagcreateJavaDocTag( Stringcontent ,CtJavaDocTag. TagTypetype )
		{ returnCode().createJavaDocTag(content ,type)
	;

	}@
	Override public CtThrowcreateCtThrow( StringthrownExp )
		{ returnCode().createCtThrow(thrownExp)
	;

	}@
	Override public CtPackageReferencecreateCtPackageReference( PackageoriginalPackage )
		{ returnCode().createCtPackageReference(originalPackage)
	;

	}@
	Override public<T >CtConstructor<T >createDefault(CtClass<T >target )
		{ returnConstructor().createDefault(target)
	;

	}@
	Override public< A extendsAnnotation >CtAnnotation<A >createAnnotation( )
		{ returnCore().createAnnotation()
	;

	}@
	Override public<R >CtBlock<R >createBlock( )
		{ returnCore().createBlock()
	;

	}@
	Override public<R >CtReturn<R >createReturn( )
		{ returnCore().createReturn()
	;

	}@
	Override public<R > CtStatementListcreateStatementList( )
		{ returnCore().createStatementList()
	;

	}@
	Override public<S >CtCase<S >createCase( )
		{ returnCore().createCase()
	;

	}@
	Override public<S >CtSwitch<S >createSwitch( )
		{ returnCore().createSwitch()
	;

	}@
	Override public< T extendsEnum<?> >CtEnum<T >createEnum( )
		{ returnCore().createEnum()
	;

	}@
	Override public< T extendsAnnotation >CtAnnotationType<T >createAnnotationType( )
		{ returnCore().createAnnotationType()
	;

	}@
	Override public<T , A extendsT >CtAssignment<T ,A >createAssignment( )
		{ returnCore().createAssignment()
	;

	}@
	Override public<T , A extendsT >CtOperatorAssignment<T ,A >createOperatorAssignment( )
		{ returnCore().createOperatorAssignment()
	;

	}@
	Override public<T , E extendsCtExpression<?> >CtExecutableReferenceExpression<T ,E >createExecutableReferenceExpression( )
		{ returnCore().createExecutableReferenceExpression()
	;

	}@
	Override public<T >CtAnnotationFieldAccess<T >createAnnotationFieldAccess( )
		{ returnCore().createAnnotationFieldAccess()
	;

	}@
	Override public<T >CtArrayRead<T >createArrayRead( )
		{ returnCore().createArrayRead()
	;

	}@
	Override public<T >CtArrayWrite<T >createArrayWrite( )
		{ returnCore().createArrayWrite()
	;

	}@
	Override public<T >CtAssert<T >createAssert( )
		{ returnCore().createAssert()
	;

	}@
	Override public<T >CtBinaryOperator<T >createBinaryOperator( )
		{ returnCore().createBinaryOperator()
	;

	}@
	Override public<T >CtCatchVariable<T >createCatchVariable( )
		{ returnCore().createCatchVariable()
	;

	}@
	Override public<T >CtCodeSnippetExpression<T >createCodeSnippetExpression( )
		{ returnCore().createCodeSnippetExpression()
	;

	}@
	Override public<T >CtConditional<T >createConditional( )
		{ returnCore().createConditional()
	;

	}@
	Override public<T >CtConstructorCall<T >createConstructorCall( )
		{ returnCore().createConstructorCall()
	;

	}@
	Override public<T >CtFieldRead<T >createFieldRead( )
		{ returnCore().createFieldRead()
	;

	}@
	Override public<T >CtFieldWrite<T >createFieldWrite( )
		{ returnCore().createFieldWrite()
	;

	}@
	Override public<T >CtInvocation<T >createInvocation( )
		{ returnCore().createInvocation()
	;

	}@
	Override public<T >CtLambda<T >createLambda( )
		{ returnCore().createLambda()
	;

	}@
	Override public<T >CtLiteral<T >createLiteral( )
		{ returnCore().createLiteral()
	;

	}@
	Override public<T >CtLocalVariable<T >createLocalVariable( )
		{ returnCore().createLocalVariable()
	;

	}@
	Override public<T >CtNewArray<T >createNewArray( )
		{ returnCore().createNewArray()
	;

	}@
	Override public<T >CtNewClass<T >createNewClass( )
		{ returnCore().createNewClass()
	;

	}@
	Override public<T >CtSuperAccess<T >createSuperAccess( )
		{ returnCore().createSuperAccess()
	;

	}@
	Override public<T >CtThisAccess<T >createThisAccess( )
		{ returnCore().createThisAccess()
	;

	}@
	Override public<T >CtTypeAccess<T >createTypeAccess( )
		{ returnCore().createTypeAccess()
	;

	}@
	Override public<T >CtUnaryOperator<T >createUnaryOperator( )
		{ returnCore().createUnaryOperator()
	;

	}@
	Override public<T >CtVariableRead<T >createVariableRead( )
		{ returnCore().createVariableRead()
	;

	}@
	Override public<T >CtVariableWrite<T >createVariableWrite( )
		{ returnCore().createVariableWrite()
	;

	}@
	Override public<T >CtAnnotationMethod<T >createAnnotationMethod( )
		{ returnCore().createAnnotationMethod()
	;

	}@
	Override public<T >CtClass<T >createClass( )
		{ returnCore().createClass()
	;

	}@
	Override public<T >CtConstructor<T >createConstructor( )
		{ returnCore().createConstructor()
	;

	}@
	Override public<T >CtConstructor<T >createInvisibleArrayConstructor( )
		{ returnCore().createInvisibleArrayConstructor()
	;

	}@
	Override public<T >CtEnumValue<T >createEnumValue( )
		{ returnCore().createEnumValue()
	;

	}@
	Override public<T >CtField<T >createField( )
		{ returnCore().createField()
	;

	}@
	Override public<T >CtInterface<T >createInterface( )
		{ returnCore().createInterface()
	;

	}@
	Override public<T >CtMethod<T >createMethod( )
		{ returnCore().createMethod()
	;

	}@
	Override public<T >CtParameter<T >createParameter( )
		{ returnCore().createParameter()
	;

	}@
	Override public<T >CtArrayTypeReference<T >createArrayTypeReference( )
		{ returnCore().createArrayTypeReference()
	;

	}@
	Override public<T >CtCatchVariableReference<T >createCatchVariableReference( )
		{ returnCore().createCatchVariableReference()
	;

	}@
	Override public<T >CtExecutableReference<T >createExecutableReference( )
		{ returnCore().createExecutableReference()
	;

	}@
	Override public<T >CtFieldReference<T >createFieldReference( )
		{ returnCore().createFieldReference()
	;

	}@
	Override public<T >CtIntersectionTypeReference<T >createIntersectionTypeReference( )
		{ returnCore().createIntersectionTypeReference()
	;

	}@
	Override public<T >CtLocalVariableReference<T >createLocalVariableReference( )
		{ returnCore().createLocalVariableReference()
	;

	}@
	Override public<T >CtParameterReference<T >createParameterReference( )
		{ returnCore().createParameterReference()
	;

	}@
	Override public<T >CtTypeReference<T >createTypeReference( )
		{ returnCore().createTypeReference()
	;

	}@
	Override public<T >CtUnboundVariableReference<T >createUnboundVariableReference( )
		{ returnCore().createUnboundVariableReference()
	;

	}@
	Override public CtBreakcreateBreak( )
		{ returnCore().createBreak()
	;

	}@
	Override public CtCatchcreateCatch( )
		{ returnCore().createCatch()
	;

	}@
	Override public CtCodeSnippetStatementcreateCodeSnippetStatement( )
		{ returnCore().createCodeSnippetStatement()
	;

	}@
	Override public CtCommentcreateComment( )
		{ returnCore().createComment()
	;

	}@
	Override public CtContinuecreateContinue( )
		{ returnCore().createContinue()
	;

	}@
	Override public CtDocreateDo( )
		{ returnCore().createDo()
	;

	}@
	Override public CtForcreateFor( )
		{ returnCore().createFor()
	;

	}@
	Override public CtForEachcreateForEach( )
		{ returnCore().createForEach()
	;

	}@
	Override public CtIfcreateIf( )
		{ returnCore().createIf()
	;

	}@
	Override public CtSynchronizedcreateSynchronized( )
		{ returnCore().createSynchronized()
	;

	}@
	Override public CtThrowcreateThrow( )
		{ returnCore().createThrow()
	;

	}@
	Override public CtTrycreateTry( )
		{ returnCore().createTry()
	;

	}@
	Override public CtTryWithResourcecreateTryWithResource( )
		{ returnCore().createTryWithResource()
	;

	}@
	Override public CtWhilecreateWhile( )
		{ returnCore().createWhile()
	;

	}@
	Override public CompilationUnitcreateCompilationUnit( )
		{ returnCore().createCompilationUnit()
	;

	}@
	Override public SourcePositioncreateSourcePosition( CompilationUnitcompilationUnit , intstartSource , intend ,int[ ]lineSeparatorPositions )
		{ returnCore().createSourcePosition(compilationUnit ,startSource ,end ,lineSeparatorPositions)
	;

	}@
	Override public BodyHolderSourcePositioncreateBodyHolderSourcePosition( CompilationUnitcompilationUnit , intstartSource , intend , intmodifierStart , intmodifierEnd , intdeclarationStart , intdeclarationEnd , intbodyStart , intbodyEnd ,int[ ]lineSeparatorPositions )
		{ returnCore().createBodyHolderSourcePosition(compilationUnit ,startSource ,end ,modifierStart ,modifierEnd ,declarationStart ,declarationEnd ,bodyStart ,bodyEnd ,lineSeparatorPositions)
	;

	}@
	Override public DeclarationSourcePositioncreateDeclarationSourcePosition( CompilationUnitcompilationUnit , intstartSource , intend , intmodifierStart , intmodifierEnd , intdeclarationStart , intdeclarationEnd ,int[ ]lineSeparatorPositions )
		{ returnCore().createDeclarationSourcePosition(compilationUnit ,startSource ,end ,modifierStart ,modifierEnd ,declarationStart ,declarationEnd ,lineSeparatorPositions)
	;

	}@
	Override public CtAnonymousExecutablecreateAnonymousExecutable( )
		{ returnCore().createAnonymousExecutable()
	;

	}@
	Override public CtPackagecreatePackage( )
		{ returnCore().createPackage()
	;

	}@
	Override public CtTypeParametercreateTypeParameter( )
		{ returnCore().createTypeParameter()
	;

	}@
	Override public CtPackageReferencecreatePackageReference( )
		{ returnCore().createPackageReference()
	;

	}@
	Override public CtTypeParameterReferencecreateTypeParameterReference( )
		{ returnCore().createTypeParameterReference()
	;

	}@
	Override public CtWildcardReferencecreateWildcardReference( )
		{ returnCore().createWildcardReference()
	;

	}@
	Override public PartialEvaluatorcreatePartialEvaluator( )
		{ returnEval().createPartialEvaluator()
	;

	}@
	Override public<T >CtParameter<T >createParameter(CtExecutable<? >parent ,CtTypeReference<T >type , Stringname )
		{ returnExecutable().createParameter(parent ,type ,name)
	;

	}@
	Override public<T >CtParameterReference<T >createParameterReference(CtParameter<T >parameter )
		{ returnExecutable().createParameterReference(parameter)
	;

	}@
	Override public CtAnonymousExecutablecreateAnonymous(CtClass<? >target ,CtBlock<Void >body )
		{ returnExecutable().createAnonymous(target ,body)
	;

	}@
	Override public<T >CtArrayTypeReference<T >createArrayReference( StringqualifiedName )
		{ returnType().createArrayReference(qualifiedName)
	;

	}@
	Override public<T >CtArrayTypeReference<T[] >createArrayReference(CtType<T >type )
		{ returnType().createArrayReference(type)
	;

	}@
	Override public<T >CtArrayTypeReference<T[] >createArrayReference(CtTypeReference<T >reference )
		{ returnType().createArrayReference(reference)
	;

	}@
	Override public<T >CtIntersectionTypeReference<T >createIntersectionTypeReferenceWithBounds(List<CtTypeReference<?> >bounds )
		{ returnType().createIntersectionTypeReferenceWithBounds(bounds)
	;

	}@
	Override public GenericTypeAdaptercreateTypeAdapter( CtFormalTypeDeclarerformalTypeDeclarer )
		{ returnType().createTypeAdapter(formalTypeDeclarer)
	;

	}@
	Override publicList<CtTypeReference<?> >createReferences(List<Class<?> >classes )
		{ returnType().createReferences(classes)
	;

	}@
	Override publicCtArrayTypeReference<? >createArrayReference(CtTypeReference<? >reference , intn )
		{ returnType().createArrayReference(reference ,n)
	;

	}@
	Override public CtTypeParameterReferencecreateTypeParameterReference( Stringname )
		{ returnType().createTypeParameterReference(name)
	;

	}@
	Override public CtQuerycreateQuery( )
		{ returnQuery().createQuery()
	;

	}@
	Override public CtQuerycreateQuery( Objectinput )
		{ returnQuery().createQuery(input)
	;

	}@
	Override public CtQuerycreateQuery(Object[ ]input )
		{ returnQuery().createQuery(input)
	;

	}@
	Override public CtQuerycreateQuery(Iterable<? >input )
		{ returnQuery().createQuery(input)
	;

	}@
	Override public CtAnnotationTypecreateAnnotationType( StringqualifiedName )
		{ returnAnnotation().create(qualifiedName)
	;

	}@
	Override public CtAnnotationTypecreateAnnotationType( CtPackageowner , StringsimpleName )
		{ returnAnnotation().create(owner ,simpleName)
	;

	}@
	Override public CtClasscreateClass( StringqualifiedName )
		{ returnClass().create(qualifiedName)
	;

	}@
	Override public CtClasscreateClass(CtClass<? >declaringClass , StringsimpleName )
		{ returnClass().create(declaringClass ,simpleName)
	;

	}@
	Override public CtClasscreateClass( CtPackageowner , StringsimpleName )
		{ returnClass().create(owner ,simpleName)
	;

	}@
	Override public CtConstructorcreateConstructor( CtClasstarget ,CtConstructor<? >source )
		{ returnConstructor().create(target ,source)
	;

	}@
	Override public CtConstructorcreateConstructor( CtClasstarget ,CtMethod<? >source )
		{ returnConstructor().create(target ,source)
	;

	}@
	Override public CtConstructorcreateConstructor( CtClasstarget ,Set<ModifierKind >modifiers ,List<CtParameter<?> >parameters ,Set<CtTypeReference< ? extendsThrowable> >thrownTypes )
		{ returnConstructor().create(target ,modifiers ,parameters ,thrownTypes)
	;

	}@
	Override public CtConstructorcreateConstructor( CtClasstarget ,Set<ModifierKind >modifiers ,List<CtParameter<?> >parameters ,Set<CtTypeReference< ? extendsThrowable> >thrownTypes , CtBlockbody )
		{ returnConstructor().create(target ,modifiers ,parameters ,thrownTypes ,body)
	;

	}@
	Override publicCtEnum<? >createEnum( StringqualifiedName )
		{ returnEnum().create(qualifiedName)
	;

	}@
	Override publicCtEnum<? >createEnum( CtPackageowner , StringsimpleName )
		{ returnEnum().create(owner ,simpleName)
	;

	}@
	Override public CtFieldcreateField(CtType<? >target ,Set<ModifierKind >modifiers , CtTypeReferencetype , Stringname )
		{ returnField().create(target ,modifiers ,type ,name)
	;

	}@
	Override public CtFieldcreateField(CtType<? >target ,Set<ModifierKind >modifiers , CtTypeReferencetype , Stringname , CtExpressiondefaultExpression )
		{ returnField().create(target ,modifiers ,type ,name ,defaultExpression)
	;

	}@
	Override public CtFieldcreateField(CtType<? >target , CtFieldsource )
		{ returnField().create(target ,source)
	;

	}@
	Override public CtInterfacecreateInterface( CtPackageowner , StringsimpleName )
		{ returnInterface().create(owner ,simpleName)
	;

	}@
	Override public CtInterfacecreateInterface( CtTypeowner , StringsimpleName )
		{ returnInterface().create(owner ,simpleName)
	;

	}@
	Override public CtInterfacecreateInterface( StringqualifiedName )
		{ returnInterface().create(qualifiedName)
	;

	}@
	Override public CtMethodcreateMethod(CtClass<? >target ,Set<ModifierKind >modifiers , CtTypeReferencereturnType , Stringname ,List<CtParameter<?> >parameters ,Set<CtTypeReference< ? extendsThrowable> >thrownTypes , CtBlockbody )
		{ returnMethod().create(target ,modifiers ,returnType ,name ,parameters ,thrownTypes ,body)
	;

	}@
	Override public CtMethodcreateMethod(CtType<? >target , CtMethodsource , booleanredirectReferences )
		{ returnMethod().create(target ,source ,redirectReferences)
	;

	}@
	Override public CtMethodcreateMethod(CtType<? >target ,Set<ModifierKind >modifiers , CtTypeReferencereturnType , Stringname ,List<CtParameter<?> >parameters ,Set<CtTypeReference< ? extendsThrowable> >thrownTypes )
		{ returnMethod().create(target ,modifiers ,returnType ,name ,parameters ,thrownTypes)
	;

	}@
	Override public CtPackagecreatePackage( CtPackageparent , StringsimpleName )
		{ returnPackage().create(parent ,simpleName)
	;

	}@
	Override public CtElementcreateElement(Class< ? extendsCtElement >klass )
		{ returnCore().create(klass)
	;

	}@
	Override public CtImportcreateImport( CtReferencereference )
		{ returnType().createImport(reference)
	;

	}@
	Override public CtTypeMemberWildcardImportReferencecreateTypeMemberWildcardImportReference( CtTypeReferencetypeReference )
		{ returnType().createTypeMemberWildcardImportReference(typeReference)
	;

	}@
	Override public CtPackageExportcreatePackageExport( CtPackageReferencectPackageReference )
		{ returnModule().createPackageExport(ctPackageReference)
	;

	}@
	Override public CtProvidedServicecreateProvidedService( CtTypeReferencectTypeReference )
		{ returnModule().createProvidedService(ctTypeReference)
	;

	}@
	Override public CtModuleRequirementcreateModuleRequirement( CtModuleReferencectModuleReference )
		{ returnModule().createModuleRequirement(ctModuleReference)
	;

	}@
	Override public CtModulecreateModule( StringmoduleName )
		{ returnModule().getOrCreate(moduleName)
	;

	}@
	Override public CtModuleReferencecreateModuleReference( CtModulectModule )
		{ returnModule().createReference(ctModule)
	;

	}@
	Override public CtUsedServicecreateUsedService( CtTypeReferencetypeReference )
		{ returnModule().createUsedService(typeReference)
	;

	}@
	Override public SourcePositioncreatePartialSourcePosition( CompilationUnitcompilationUnit )
		{ returnCore().createPartialSourcePosition(compilationUnit)
	;

	}@
	Override public CtPackageDeclarationcreatePackageDeclaration( CtPackageReferencepackageRef )
		{ returnPackage().createPackageDeclaration(packageRef)
	;
}
