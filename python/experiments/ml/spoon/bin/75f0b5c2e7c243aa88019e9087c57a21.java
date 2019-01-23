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
	public <T> CtNewArray<T[]> createLiteralArray(T[] value) {
		return Code().createLiteralArray(value);
	}

	@Override
	public <T> CtNewClass<T> createNewClass(CtTypeReference<T> type, CtClass<?> anonymousClass, CtExpression<?>... parameters) {
		return Code().createNewClass(type, anonymousClass, parameters);
	}

	@Override
	public <T> CtStatementList createVariableAssignments(List<? extends CtVariable<T>> variables, List<? extends CtExpression<T>> expressions) {
		return Code().createVariableAssignments(variables, expressions);
	}

	@Override
	public <T> CtThisAccess<T> createThisAccess(CtTypeReference<T> type) {
		return Code().createThisAccess(type);
	}

	@Override
	public <T> CtThisAccess<T> createThisAccess(CtTypeReference<T> type, boolean isImplicit) {
		return Code().createThisAccess(type, isImplicit);
	}

	@Override
	public <T> CtTypeAccess<T> createTypeAccess(CtTypeReference<T> accessedType) {
		return Code().createTypeAccess(accessedType);
	}

	@Override
	public <T> CtTypeAccess<T> createTypeAccess(CtTypeReference<T> accessedType, boolean isImplicit) {
		return Code().createTypeAccess(accessedType, isImplicit);
	}

	@Override
	public <T> CtTypeAccess<T> createTypeAccessWithoutCloningReference(CtTypeReference<T> accessedType) {
		return Code().createTypeAccessWithoutCloningReference(accessedType);
	}

	@Override
	public <T> CtVariableAccess<T> createVariableRead(CtVariableReference<T> variable, boolean isStatic) {
		return Code().createVariableRead(variable, isStatic);
	}

	@Override
	public <T> CtField<T> createCtField(String name, CtTypeReference<T> type, String exp, ModifierKind... visibilities) {
		return Code().createCtField(name, type, exp, visibilities);
	}

	@Override
	public <T> CtCatchVariableReference<T> createCatchVariableReference(CtCatchVariable<T> catchVariable) {
		return Code().createCatchVariableReference(catchVariable);
	}

	@Override
	public <T> CtLocalVariableReference<T> createLocalVariableReference(CtLocalVariable<T> localVariable) {
		return Code().createLocalVariableReference(localVariable);
	}

	@Override
	public <T> CtLocalVariableReference<T> createLocalVariableReference(CtTypeReference<T> type, String name) {
		return Code().createLocalVariableReference(type, name);
	}

	@Override
	public <T> CtTypeReference<T> createCtTypeReference(Class<?> originalClass) {
		return Code().createCtTypeReference(originalClass);
	}

	@Override
	public List<CtExpression<?>> createVariableReads(List<? extends CtVariable<?>> variables) {
		return Code().createVariableReads(variables);
	}

	@Override
	public CtCatch createCtCatch(String nameCatch, Class<? extends Throwable> exception, CtBlock<?> ctBlock) {
		return Code().createCtCatch(nameCatch, exception, ctBlock);
	}

	@Override
	public CtCodeSnippetStatement createCodeSnippetStatement(String statement) {
		return Code().createCodeSnippetStatement(statement);
	}

	@Override
	public CtComment createComment(String content, CtComment.CommentType type) {
		return Code().createComment(content, type);
	}

	@Override
	public CtComment createInlineComment(String content) {
		return Code().createInlineComment(content);
	}

	@Override
	public CtJavaDocTag createJavaDocTag(String content, CtJavaDocTag.TagType type) {
		return Code().createJavaDocTag(content, type);
	}

	@Override
	public CtThrow createCtThrow(String thrownExp) {
		return Code().createCtThrow(thrownExp);
	}

	@Override
	public CtPackageReference createCtPackageReference(Package originalPackage) {
		return Code().createCtPackageReference(originalPackage);
	}

	@Override
	public <T> CtConstructor<T> createDefault(CtClass<T> target) {
		return Constructor().createDefault(target);
	}

	@Override
	public <A extends Annotation> CtAnnotation<A> createAnnotation() {
		return Core().createAnnotation();
	}

	@Override
	public <R> CtBlock<R> createBlock() {
		return Core().createBlock();
	}

	@Override
	public <R> CtReturn<R> createReturn() {
		return Core().createReturn();
	}

	@Override
	public <R> CtStatementList createStatementList() {
		return Core().createStatementList();
	}

	@Override
	public <S> CtCase<S> createCase() {
		return Core().createCase();
	}

	@Override
	public <S> CtSwitch<S> createSwitch() {
		return Core().createSwitch();
	}

	@Override
	public <T extends Enum<?>> CtEnum<T> createEnum() {
		return Core().createEnum();
	}

	@Override
	public <T extends Annotation> CtAnnotationType<T> createAnnotationType() {
		return Core().createAnnotationType();
	}

	@Override
	public <T, A extends T> CtAssignment<T, A> createAssignment() {
		return Core().createAssignment();
	}

	@Override
	public <T, A extends T> CtOperatorAssignment<T, A> createOperatorAssignment() {
		return Core().createOperatorAssignment();
	}

	@Override
	public <T, E extends CtExpression<?>> CtExecutableReferenceExpression<T, E> createExecutableReferenceExpression() {
		return Core().createExecutableReferenceExpression();
	}

	@Override
	public <T> CtAnnotationFieldAccess<T> createAnnotationFieldAccess() {
		return Core().createAnnotationFieldAccess();
	}

	@Override
	public <T> CtArrayRead<T> createArrayRead() {
		return Core().createArrayRead();
	}

	@Override
	public <T> CtArrayWrite<T> createArrayWrite() {
		return Core().createArrayWrite();
	}

	@Override
	public <T> CtAssert<T> createAssert() {
		return Core().createAssert();
	}

	@Override
	public <T> CtBinaryOperator<T> createBinaryOperator() {
		return Core().createBinaryOperator();
	}

	@Override
	public <T> CtCatchVariable<T> createCatchVariable() {
		return Core().createCatchVariable();
	}

	@Override
	public <T> CtCodeSnippetExpression<T> createCodeSnippetExpression() {
		return Core().createCodeSnippetExpression();
	}

	@Override
	public <T> CtConditional<T> createConditional() {
		return Core().createConditional();
	}

	@Override
	public <T> CtConstructorCall<T> createConstructorCall() {
		return Core().createConstructorCall();
	}

	@Override
	public <T> CtFieldRead<T> createFieldRead() {
		return Core().createFieldRead();
	}

	@Override
	public <T> CtFieldWrite<T> createFieldWrite() {
		return Core().createFieldWrite();
	}

	@Override
	public <T> CtInvocation<T> createInvocation() {
		return Core().createInvocation();
	}

	@Override
	public <T> CtLambda<T> createLambda() {
		return Core().createLambda();
	}

	@Override
	public <T> CtLiteral<T> createLiteral() {
		return Core().createLiteral();
	}

	@Override
	public <T> CtLocalVariable<T> createLocalVariable() {
		return Core().createLocalVariable();
	}

	@Override
	public <T> CtNewArray<T> createNewArray() {
		return Core().createNewArray();
	}

	@Override
	public <T> CtNewClass<T> createNewClass() {
		return Core().createNewClass();
	}

	@Override
	public <T> CtSuperAccess<T> createSuperAccess() {
		return Core().createSuperAccess();
	}

	@Override
	public <T> CtThisAccess<T> createThisAccess() {
		return Core().createThisAccess();
	}

	@Override
	public <T> CtTypeAccess<T> createTypeAccess() {
		return Core().createTypeAccess();
	}

	@Override
	public <T> CtUnaryOperator<T> createUnaryOperator() {
		return Core().createUnaryOperator();
	}

	@Override
	public <T> CtVariableRead<T> createVariableRead() {
		return Core().createVariableRead();
	}

	@Override
	public <T> CtVariableWrite<T> createVariableWrite() {
		return Core().createVariableWrite();
	}

	@Override
	public <
	T>CtAnnotationMethod <T>createAnnotationMethod (){ return
		Core ().createAnnotationMethod();}
	@

	Overridepublic
	< T>CtClass <T>createClass (){ return
		Core ().createClass();}
	@

	Overridepublic
	< T>CtConstructor <T>createConstructor (){ return
		Core ().createConstructor();}
	@

	Overridepublic
	< T>CtConstructor <T>createInvisibleArrayConstructor (){ return
		Core ().createInvisibleArrayConstructor();}
	@

	Overridepublic
	< T>CtEnumValue <T>createEnumValue (){ return
		Core ().createEnumValue();}
	@

	Overridepublic
	< T>CtField <T>createField (){ return
		Core ().createField();}
	@

	Overridepublic
	< T>CtInterface <T>createInterface (){ return
		Core ().createInterface();}
	@

	Overridepublic
	< T>CtMethod <T>createMethod (){ return
		Core ().createMethod();}
	@

	Overridepublic
	< T>CtParameter <T>createParameter (){ return
		Core ().createParameter();}
	@

	Overridepublic
	< T>CtArrayTypeReference <T>createArrayTypeReference (){ return
		Core ().createArrayTypeReference();}
	@

	Overridepublic
	< T>CtCatchVariableReference <T>createCatchVariableReference (){ return
		Core ().createCatchVariableReference();}
	@

	Overridepublic
	< T>CtExecutableReference <T>createExecutableReference (){ return
		Core ().createExecutableReference();}
	@

	Overridepublic
	< T>CtFieldReference <T>createFieldReference (){ return
		Core ().createFieldReference();}
	@

	Overridepublic
	< T>CtIntersectionTypeReference <T>createIntersectionTypeReference (){ return
		Core ().createIntersectionTypeReference();}
	@

	Overridepublic
	< T>CtLocalVariableReference <T>createLocalVariableReference (){ return
		Core ().createLocalVariableReference();}
	@

	Overridepublic
	< T>CtParameterReference <T>createParameterReference (){ return
		Core ().createParameterReference();}
	@

	Overridepublic
	< T>CtTypeReference <T>createTypeReference (){ return
		Core ().createTypeReference();}
	@

	Overridepublic
	< T>CtUnboundVariableReference <T>createUnboundVariableReference (){ return
		Core ().createUnboundVariableReference();}
	@

	Overridepublic
	CtBreak createBreak (){ return
		Core ().createBreak();}
	@

	Overridepublic
	CtCatch createCatch (){ return
		Core ().createCatch();}
	@

	Overridepublic
	CtCodeSnippetStatement createCodeSnippetStatement (){ return
		Core ().createCodeSnippetStatement();}
	@

	Overridepublic
	CtComment createComment (){ return
		Core ().createComment();}
	@

	Overridepublic
	CtContinue createContinue (){ return
		Core ().createContinue();}
	@

	Overridepublic
	CtDo createDo (){ return
		Core ().createDo();}
	@

	Overridepublic
	CtFor createFor (){ return
		Core ().createFor();}
	@

	Overridepublic
	CtForEach createForEach (){ return
		Core ().createForEach();}
	@

	Overridepublic
	CtIf createIf (){ return
		Core ().createIf();}
	@

	Overridepublic
	CtSynchronized createSynchronized (){ return
		Core ().createSynchronized();}
	@

	Overridepublic
	CtThrow createThrow (){ return
		Core ().createThrow();}
	@

	Overridepublic
	CtTry createTry (){ return
		Core ().createTry();}
	@

	Overridepublic
	CtTryWithResource createTryWithResource (){ return
		Core ().createTryWithResource();}
	@

	Overridepublic
	CtWhile createWhile (){ return
		Core ().createWhile();}
	@

	Overridepublic
	CompilationUnit createCompilationUnit (){ return
		Core ().createCompilationUnit();}
	@

	Overridepublic
	SourcePosition createSourcePosition (CompilationUnitcompilationUnit ,int startSource ,int end ,int []lineSeparatorPositions ){ return
		Core ().createSourcePosition(compilationUnit,startSource ,end ,lineSeparatorPositions );}
	@

	Overridepublic
	BodyHolderSourcePosition createBodyHolderSourcePosition (CompilationUnitcompilationUnit ,int startSource ,int end ,int modifierStart ,int modifierEnd ,int declarationStart ,int declarationEnd ,int bodyStart ,int bodyEnd ,int []lineSeparatorPositions ){ return
		Core ().createBodyHolderSourcePosition(compilationUnit,startSource ,end ,modifierStart ,modifierEnd ,declarationStart ,declarationEnd ,bodyStart ,bodyEnd ,lineSeparatorPositions );}
	@

	Overridepublic
	DeclarationSourcePosition createDeclarationSourcePosition (CompilationUnitcompilationUnit ,int startSource ,int end ,int modifierStart ,int modifierEnd ,int declarationStart ,int declarationEnd ,int []lineSeparatorPositions ){ return
		Core ().createDeclarationSourcePosition(compilationUnit,startSource ,end ,modifierStart ,modifierEnd ,declarationStart ,declarationEnd ,lineSeparatorPositions );}
	@

	Overridepublic
	CtAnonymousExecutable createAnonymousExecutable (){ return
		Core ().createAnonymousExecutable();}
	@

	Overridepublic
	CtPackage createPackage (){ return
		Core ().createPackage();}
	@

	Overridepublic
	CtTypeParameter createTypeParameter (){ return
		Core ().createTypeParameter();}
	@

	Overridepublic
	CtPackageReference createPackageReference (){ return
		Core ().createPackageReference();}
	@

	Overridepublic
	CtTypeParameterReference createTypeParameterReference (){ return
		Core ().createTypeParameterReference();}
	@

	Overridepublic
	CtWildcardReference createWildcardReference (){ return
		Core ().createWildcardReference();}
	@

	Overridepublic
	PartialEvaluator createPartialEvaluator (){ return
		Eval ().createPartialEvaluator();}
	@

	Overridepublic
	< T>CtParameter <T>createParameter (CtExecutable<?>parent ,CtTypeReference <T>type ,String name ){ return
		Executable ().createParameter(parent,type ,name );}
	@

	Overridepublic
	< T>CtParameterReference <T>createParameterReference (CtParameter<T>parameter ){ return
		Executable ().createParameterReference(parameter);}
	@

	Overridepublic
	CtAnonymousExecutable createAnonymous (CtClass<?>target ,CtBlock <Void>body ){ return
		Executable ().createAnonymous(target,body );}
	@

	Overridepublic
	< T>CtArrayTypeReference <T>createArrayReference (StringqualifiedName ){ return
		Type ().createArrayReference(qualifiedName);}
	@

	Overridepublic
	< T>CtArrayTypeReference <T[]>createArrayReference (CtType<T>type ){ return
		Type ().createArrayReference(type);}
	@

	Overridepublic
	< T>CtArrayTypeReference <T[]>createArrayReference (CtTypeReference<T>reference ){ return
		Type ().createArrayReference(reference);}
	@

	Overridepublic
	< T>CtIntersectionTypeReference <T>createIntersectionTypeReferenceWithBounds (List<CtTypeReference<?>>bounds ){ return
		Type ().createIntersectionTypeReferenceWithBounds(bounds);}
	@

	Overridepublic
	GenericTypeAdapter createTypeAdapter (CtFormalTypeDeclarerformalTypeDeclarer ){ return
		Type ().createTypeAdapter(formalTypeDeclarer);}
	@

	Overridepublic
	List <CtTypeReference<?>>createReferences (List<Class<?>>classes ){ return
		Type ().createReferences(classes);}
	@

	Overridepublic
	CtArrayTypeReference <?>createArrayReference (CtTypeReference<?>reference ,int n ){ return
		Type ().createArrayReference(reference,n );}
	@

	Overridepublic
	CtTypeParameterReference createTypeParameterReference (Stringname ){ return
		Type ().createTypeParameterReference(name);}
	@

	Overridepublic
	CtQuery createQuery (){ return
		Query ().createQuery();}
	@

	Overridepublic
	CtQuery createQuery (Objectinput ){ return
		Query ().createQuery(input);}
	@

	Overridepublic
	CtQuery createQuery (Object[]input ){ return
		Query ().createQuery(input);}
	@

	Overridepublic
	CtQuery createQuery (Iterable<?>input ){ return
		Query ().createQuery(input);}
	@

	Overridepublic
	CtAnnotationType createAnnotationType (StringqualifiedName ){ return
		Annotation ().create(qualifiedName);}
	@

	Overridepublic
	CtAnnotationType createAnnotationType (CtPackageowner ,String simpleName ){ return
		Annotation ().create(owner,simpleName );}
	@

	Overridepublic
	CtClass createClass (StringqualifiedName ){ return
		Class ().create(qualifiedName);}
	@

	Overridepublic
	CtClass createClass (CtClass<?>declaringClass ,String simpleName ){ return
		Class ().create(declaringClass,simpleName );}
	@

	Overridepublic
	CtClass createClass (CtPackageowner ,String simpleName ){ return
		Class ().create(owner,simpleName );}
	@

	Overridepublic
	CtConstructor createConstructor (CtClasstarget ,CtConstructor <?>source ){ return
		Constructor ().create(target,source );}
	@

	Overridepublic
	CtConstructor createConstructor (CtClasstarget ,CtMethod <?>source ){ return
		Constructor ().create(target,source );}
	@

	Overridepublic
	CtConstructor createConstructor (CtClasstarget ,Set <ModifierKind>modifiers ,List <CtParameter<?>>parameters ,Set <CtTypeReference<?extends Throwable >>thrownTypes ){ return
		Constructor ().create(target,modifiers ,parameters ,thrownTypes );}
	@

	Overridepublic
	CtConstructor createConstructor (CtClasstarget ,Set <ModifierKind>modifiers ,List <CtParameter<?>>parameters ,Set <CtTypeReference<?extends Throwable >>thrownTypes ,CtBlock body ){ return
		Constructor ().create(target,modifiers ,parameters ,thrownTypes ,body );}
	@

	Overridepublic
	CtEnum <?>createEnum (StringqualifiedName ){ return
		Enum ().create(qualifiedName);}
	@

	Overridepublic
	CtEnum <?>createEnum (CtPackageowner ,String simpleName ){ return
		Enum ().create(owner,simpleName );}
	@

	Overridepublic
	CtField createField (CtType<?>target ,Set <ModifierKind>modifiers ,CtTypeReference type ,String name ){ return
		Field ().create(target,modifiers ,type ,name );}
	@

	Overridepublic
	CtField createField (CtType<?>target ,Set <ModifierKind>modifiers ,CtTypeReference type ,String name ,CtExpression defaultExpression ){ return
		Field ().create(target,modifiers ,type ,name ,defaultExpression );}
	@

	Overridepublic
	CtField createField (CtType<?>target ,CtField source ){ return
		Field ().create(target,source );}
	@

	Overridepublic
	CtInterface createInterface (CtPackageowner ,String simpleName ){ return
		Interface ().create(owner,simpleName );}
	@

	Overridepublic
	CtInterface createInterface (CtTypeowner ,String simpleName ){ return
		Interface ().create(owner,simpleName );}
	@

	Overridepublic
	CtInterface createInterface (StringqualifiedName ){ return
		Interface ().create(qualifiedName);}
	@

	Overridepublic
	CtMethod createMethod (CtClass<?>target ,Set <ModifierKind>modifiers ,CtTypeReference returnType ,String name ,List <CtParameter<?>>parameters ,Set <CtTypeReference<?extends Throwable >>thrownTypes ,CtBlock body ){ return
		Method ().create(target,modifiers ,returnType ,name ,parameters ,thrownTypes ,body );}
	@

	Overridepublic
	CtMethod createMethod (CtType<?>target ,CtMethod source ,boolean redirectReferences ){ return
		Method ().create(target,source ,redirectReferences );}
	@

	Overridepublic
	CtMethod createMethod (CtType<?>target ,Set <ModifierKind>modifiers ,CtTypeReference returnType ,String name ,List <CtParameter<?>>parameters ,Set <CtTypeReference<?extends Throwable >>thrownTypes ){ return
		Method ().create(target,modifiers ,returnType ,name ,parameters ,thrownTypes );}
	@

	Overridepublic
	CtPackage createPackage (CtPackageparent ,String simpleName ){ return
		Package ().create(parent,simpleName );}
	@

	Overridepublic
	CtElement createElement (Class<?extends CtElement >klass ){ return
		Core ().create(klass);}
	@

	Overridepublic
	CtImport createImport (CtReferencereference ){ return
		Type ().createImport(reference);}
	@

	Overridepublic
	CtTypeMemberWildcardImportReference createTypeMemberWildcardImportReference (CtTypeReferencetypeReference ){ return
		Type ().createTypeMemberWildcardImportReference(typeReference);}
	@

	Overridepublic
	CtPackageExport createPackageExport (CtPackageReferencectPackageReference ){ return
		Module ().createPackageExport(ctPackageReference);}
	@

	Overridepublic
	CtProvidedService createProvidedService (CtTypeReferencectTypeReference ){ return
		Module ().createProvidedService(ctTypeReference);}
	@

	Overridepublic
	CtModuleRequirement createModuleRequirement (CtModuleReferencectModuleReference ){ return
		Module ().createModuleRequirement(ctModuleReference);}
	@

	Overridepublic
	CtModule createModule (StringmoduleName ){ return
		Module ().getOrCreate(moduleName);}
	@

	Overridepublic
	CtModuleReference createModuleReference (CtModulectModule ){ return
		Module ().createReference(ctModule);}
	@

	Overridepublic
	CtUsedService createUsedService (CtTypeReferencetypeReference ){ return
		Module ().createUsedService(typeReference);}
	@

	Overridepublic
	SourcePosition createPartialSourcePosition (CompilationUnitcompilationUnit ){ return
		Core ().createPartialSourcePosition(compilationUnit);}
	@

	Overridepublic
	CtPackageDeclaration createPackageDeclaration (CtPackageReferencepackageRef ){ return
		Package ().createPackageDeclaration(packageRef);}
	}
