import java_lang_utils as jlu
import tensorflow as tf
import numpy as np
from tensorflow import keras
from javalang import tokenizer
import random
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout, Activation, Flatten
from tensorflow.keras.layers import Conv2D, MaxPooling2D

# tf.logging.set_verbosity(tf.logging.INFO)

files = """../../kth/corpora/spoon/src/main/java/spoon/legacy/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/legacy/NameFilter.java
../../kth/corpora/spoon/src/main/java/spoon/JarLauncher.java
../../kth/corpora/spoon/src/main/java/spoon/support/gui/SpoonObjectFieldsTable.java
../../kth/corpora/spoon/src/main/java/spoon/support/gui/SpoonModelTree.java
../../kth/corpora/spoon/src/main/java/spoon/support/gui/SpoonTreeBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/support/StandardEnvironment.java
../../kth/corpora/spoon/src/main/java/spoon/support/SerializationModelStreamer.java
../../kth/corpora/spoon/src/main/java/spoon/support/SpoonClassNotFoundException.java
../../kth/corpora/spoon/src/main/java/spoon/support/util/ModelList.java
../../kth/corpora/spoon/src/main/java/spoon/support/util/RtHelper.java
../../kth/corpora/spoon/src/main/java/spoon/support/util/EmptyIterator.java
../../kth/corpora/spoon/src/main/java/spoon/support/util/internal/MapUtils.java
../../kth/corpora/spoon/src/main/java/spoon/support/util/QualifiedNameBasedSortedSet.java
../../kth/corpora/spoon/src/main/java/spoon/support/util/SignatureBasedSortedSet.java
../../kth/corpora/spoon/src/main/java/spoon/support/util/ImmutableMap.java
../../kth/corpora/spoon/src/main/java/spoon/support/util/EmptyClearableList.java
../../kth/corpora/spoon/src/main/java/spoon/support/util/ImmutableMapImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/util/ModelSet.java
../../kth/corpora/spoon/src/main/java/spoon/support/util/SortedList.java
../../kth/corpora/spoon/src/main/java/spoon/support/util/ByteSerialization.java
../../kth/corpora/spoon/src/main/java/spoon/support/util/EmptyClearableSet.java
../../kth/corpora/spoon/src/main/java/spoon/support/template/UndefinedParameterException.java
../../kth/corpora/spoon/src/main/java/spoon/support/template/Parameters.java
../../kth/corpora/spoon/src/main/java/spoon/support/template/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/support/comparator/DeepRepresentationComparator.java
../../kth/corpora/spoon/src/main/java/spoon/support/comparator/QualifiedNameComparator.java
../../kth/corpora/spoon/src/main/java/spoon/support/comparator/FixedOrderBasedOnFileNameCompilationUnitComparator.java
../../kth/corpora/spoon/src/main/java/spoon/support/comparator/SignatureComparator.java
../../kth/corpora/spoon/src/main/java/spoon/support/comparator/CtLineElementComparator.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/ActionBasedChangeListenerImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/EmptyModelChangeListener.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/context/Context.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/context/MapContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/context/SetContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/context/ObjectContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/context/CollectionContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/context/ListContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/ActionBasedChangeListener.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/ChangeCollector.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/SourceFragmentCreator.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/FineModelChangeListener.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/action/AddAction.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/action/DeleteAction.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/action/DeleteAllAction.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/action/Action.java
../../kth/corpora/spoon/src/main/java/spoon/support/modelobs/action/UpdateAction.java
../../kth/corpora/spoon/src/main/java/spoon/support/QueueProcessingManager.java
../../kth/corpora/spoon/src/main/java/spoon/support/DefaultCoreFactory.java
../../kth/corpora/spoon/src/main/java/spoon/support/JavaOutputProcessor.java
../../kth/corpora/spoon/src/main/java/spoon/support/Internal.java
../../kth/corpora/spoon/src/main/java/spoon/support/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/support/OutputDestinationHandler.java
../../kth/corpora/spoon/src/main/java/spoon/support/Experimental.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/ProgressLogger.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/VirtualFile.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/FilteringFolder.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/SpoonPom.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/SpoonProgress.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/SnippetCompilationHelper.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/FileSystemFile.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/ZipFolder.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/ZipFile.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/SnippetCompilationError.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/FileSystemFolder.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/JDTTreeBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/ReferenceBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/CompilationUnitFilter.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/ASTPair.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/FileCompilerConfig.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/JDTBasedSpoonCompiler.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/PositionBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/CompilationUnitWrapper.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/ParentExiter.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/JDTTreeBuilderHelper.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/JDTImportBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/ContextBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/JDTSnippetCompiler.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/JDTCommentBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/TreeBuilderCompiler.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/TreeBuilderRequestor.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/JDTBatchCompiler.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/FactoryCompilerConfig.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/jdt/JDTTreeBuilderQuery.java
../../kth/corpora/spoon/src/main/java/spoon/support/compiler/VirtualFolder.java
../../kth/corpora/spoon/src/main/java/spoon/support/CompressionType.java
../../kth/corpora/spoon/src/main/java/spoon/support/RuntimeProcessingManager.java
../../kth/corpora/spoon/src/main/java/spoon/support/DefaultOutputDestinationHandler.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtContinueImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtAssignmentImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtLambdaImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtNewArrayImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtAssertImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtExecutableReferenceExpressionImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtInvocationImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtVariableWriteImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtNewClassImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtVariableAccessImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtConditionalImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtStatementListImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtBinaryOperatorImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtSwitchImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtLiteralImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtOperatorAssignmentImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtThisAccessImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtCatchImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtCommentImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtCodeElementImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtForEachImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtLocalVariableImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtCodeSnippetStatementImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtTargetedExpressionImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtDoImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtFieldReadImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtExpressionImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtCatchVariableImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtTryWithResourceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtFieldAccessImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtTypeAccessImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtArrayReadImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtConstructorCallImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtCaseImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtVariableReadImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtReturnImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtLoopImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtForImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtCodeSnippetExpressionImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtIfImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtAnnotationFieldAccessImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtBlockImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtFieldWriteImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtArrayAccessImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtWhileImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtSynchronizedImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtThrowImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtJavaDocImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtJavaDocTagImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtTryImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtUnaryOperatorImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtBreakImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtArrayWriteImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtStatementImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/code/CtSuperAccessImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/cu/CompilationUnitImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/cu/position/PartialSourcePositionImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/cu/position/BodyHolderSourcePositionImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/cu/position/CompoundSourcePositionImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/cu/position/SourcePositionImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/cu/position/DeclarationSourcePositionImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/CtModifierHandler.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtWildcardReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtExecutableReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtFieldReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtModuleReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtArrayTypeReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtIntersectionTypeReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtUnboundVariableReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtLocalVariableReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtWildcardStaticTypeMemberReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtCatchVariableReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtVariableReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtPackageReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtTypeReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtParameterReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/reference/CtTypeParameterReferenceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/CtExtendedModifier.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtExecutableImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtAnnotationMethodImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtEnumValueImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtInterfaceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtModuleImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtEnumImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtFieldImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtProvidedServiceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtAnnotationTypeImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtPackageExportImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtElementImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtMethodImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtNamedElementImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtModuleRequirementImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtAnonymousExecutableImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtTypeImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtParameterImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtClassImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtConstructorImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtUsedServiceImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtPackageImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/InvisibleArrayConstructorImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtAnnotationImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtImportImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/declaration/CtTypeParameterImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/eval/InlinePartialEvaluator.java
../../kth/corpora/spoon/src/main/java/spoon/support/reflect/eval/VisitorPartialEvaluator.java
../../kth/corpora/spoon/src/main/java/spoon/support/DerivedProperty.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/TokenPrinterEvent.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/MutableTokenWriter.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/AbstractSourceFragmentContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/SourceFragment.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/TokenWriterProxy.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/DirectPrinterHelper.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/AbstractSourceFragmentContextCollection.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/SourceFragmentContextList.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/SourceFragmentContextPrettyPrint.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/SourceFragmentContextSet.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/TokenSourceFragment.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/TokenType.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/SourceFragmentContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/ElementSourceFragment.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/SourceFragmentContextNormal.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/CollectionSourceFragment.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/ElementPrinterEvent.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/PrinterEvent.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/internal/ChangeResolver.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/support/sniper/SniperJavaPrettyPrinter.java
../../kth/corpora/spoon/src/main/java/spoon/support/UnsettableProperty.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/HashcodeVisitor.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/replace/ReplaceSetListener.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/replace/ReplacementVisitor.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/replace/InvalidReplaceException.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/replace/CtListener.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/replace/ReplaceMapListener.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/replace/ReplaceListListener.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/replace/ReplaceListener.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/ProcessingVisitor.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/SignaturePrinter.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/equals/EqualsVisitor.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/equals/NotEqualException.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/equals/EqualsChecker.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/equals/CloneHelper.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/ClassTypingContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/GenericTypeAdapter.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/clone/CloneVisitor.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/clone/CloneBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/AbstractTypingContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/SubInheritanceHierarchyResolver.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/TypeReferenceScanner.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/java/JavaReflectionVisitorImpl.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/java/JavaReflectionVisitor.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/java/JavaReflectionTreeBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/java/internal/ExecutableRuntimeBuilderContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/java/internal/TypeReferenceRuntimeBuilderContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/java/internal/PackageRuntimeBuilderContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/java/internal/TypeRuntimeBuilderContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/java/internal/AnnotationRuntimeBuilderContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/java/internal/VariableRuntimeBuilderContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/java/internal/AbstractRuntimeBuilderContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/java/internal/RuntimeBuilderContext.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/java/reflect/RtParameter.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/java/reflect/RtMethod.java
../../kth/corpora/spoon/src/main/java/spoon/support/visitor/MethodTypingContext.java
../../kth/corpora/spoon/src/main/java/spoon/SpoonException.java
../../kth/corpora/spoon/src/main/java/spoon/Launcher.java
../../kth/corpora/spoon/src/main/java/spoon/template/AbstractTemplate.java
../../kth/corpora/spoon/src/main/java/spoon/template/Substitution.java
../../kth/corpora/spoon/src/main/java/spoon/template/TemplateParameter.java
../../kth/corpora/spoon/src/main/java/spoon/template/TemplateException.java
../../kth/corpora/spoon/src/main/java/spoon/template/Local.java
../../kth/corpora/spoon/src/main/java/spoon/template/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/template/BlockTemplate.java
../../kth/corpora/spoon/src/main/java/spoon/template/StatementTemplate.java
../../kth/corpora/spoon/src/main/java/spoon/template/TypedBlockTemplateParameter.java
../../kth/corpora/spoon/src/main/java/spoon/template/Template.java
../../kth/corpora/spoon/src/main/java/spoon/template/ExtensionTemplate.java
../../kth/corpora/spoon/src/main/java/spoon/template/TemplateBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/template/TemplateMatcher.java
../../kth/corpora/spoon/src/main/java/spoon/template/TypedStatementListTemplateParameter.java
../../kth/corpora/spoon/src/main/java/spoon/template/ExpressionTemplate.java
../../kth/corpora/spoon/src/main/java/spoon/template/Parameter.java
../../kth/corpora/spoon/src/main/java/spoon/processing/AbstractAnnotationProcessor.java
../../kth/corpora/spoon/src/main/java/spoon/processing/Property.java
../../kth/corpora/spoon/src/main/java/spoon/processing/ProcessorProperties.java
../../kth/corpora/spoon/src/main/java/spoon/processing/FactoryAccessor.java
../../kth/corpora/spoon/src/main/java/spoon/processing/AbstractProcessor.java
../../kth/corpora/spoon/src/main/java/spoon/processing/ProcessorPropertiesImpl.java
../../kth/corpora/spoon/src/main/java/spoon/processing/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/processing/ProblemFixer.java
../../kth/corpora/spoon/src/main/java/spoon/processing/SpoonTagger.java
../../kth/corpora/spoon/src/main/java/spoon/processing/AbstractProblemFixer.java
../../kth/corpora/spoon/src/main/java/spoon/processing/ProcessInterruption.java
../../kth/corpora/spoon/src/main/java/spoon/processing/AnnotationProcessor.java
../../kth/corpora/spoon/src/main/java/spoon/processing/TraversalStrategy.java
../../kth/corpora/spoon/src/main/java/spoon/processing/Processor.java
../../kth/corpora/spoon/src/main/java/spoon/processing/ProcessingManager.java
../../kth/corpora/spoon/src/main/java/spoon/processing/FileGenerator.java
../../kth/corpora/spoon/src/main/java/spoon/processing/AbstractManualProcessor.java
../../kth/corpora/spoon/src/main/java/spoon/testing/utils/Check.java
../../kth/corpora/spoon/src/main/java/spoon/testing/utils/ProcessorUtils.java
../../kth/corpora/spoon/src/main/java/spoon/testing/utils/ModelUtils.java
../../kth/corpora/spoon/src/main/java/spoon/testing/FileAssert.java
../../kth/corpora/spoon/src/main/java/spoon/testing/AbstractCtElementAssert.java
../../kth/corpora/spoon/src/main/java/spoon/testing/CtElementAssert.java
../../kth/corpora/spoon/src/main/java/spoon/testing/AbstractAssert.java
../../kth/corpora/spoon/src/main/java/spoon/testing/Assert.java
../../kth/corpora/spoon/src/main/java/spoon/testing/CtPackageAssert.java
../../kth/corpora/spoon/src/main/java/spoon/testing/AbstractFileAssert.java
../../kth/corpora/spoon/src/main/java/spoon/testing/AbstractCtPackageAssert.java
../../kth/corpora/spoon/src/main/java/spoon/OutputType.java
../../kth/corpora/spoon/src/main/java/spoon/refactoring/Refactoring.java
../../kth/corpora/spoon/src/main/java/spoon/refactoring/CtRenameRefactoring.java
../../kth/corpora/spoon/src/main/java/spoon/refactoring/AbstractRenameRefactoring.java
../../kth/corpora/spoon/src/main/java/spoon/refactoring/RefactoringException.java
../../kth/corpora/spoon/src/main/java/spoon/refactoring/CtParameterRemoveRefactoring.java
../../kth/corpora/spoon/src/main/java/spoon/refactoring/CtRefactoring.java
../../kth/corpora/spoon/src/main/java/spoon/refactoring/CtRenameLocalVariableRefactoring.java
../../kth/corpora/spoon/src/main/java/spoon/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/SpoonModelBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/builder/Options.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/builder/SourceOptions.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/builder/AnnotationProcessingOptions.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/builder/JDTBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/builder/ComplianceOptions.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/builder/JDTBuilderImpl.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/builder/ClasspathOptions.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/builder/AdvancedOptions.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/InvalidClassPathException.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/SpoonFile.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/SpoonResourceHelper.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/Environment.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/SpoonResource.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/ModelBuildingException.java
../../kth/corpora/spoon/src/main/java/spoon/compiler/SpoonFolder.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/Generator.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/Pattern.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/Quantifier.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/PatternParameterConfigurator.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/InlinedStatementConfigurator.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/Match.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/matcher/MatchingScanner.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/matcher/Matchers.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/matcher/ChainOfMatchersImpl.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/matcher/TobeMatched.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/SubstitutionRequestProvider.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/InlineNode.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/StringNode.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/AbstractNode.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/SwitchNode.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/MapEntryNode.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/AbstractRepeatableMatcher.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/AbstractPrimitiveMatcher.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/ListOfNodes.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/ElementNode.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/RepeatableMatcher.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/PrimitiveMatcher.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/ConstantNode.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/RootNode.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/ForEachNode.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/node/ParameterNode.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/DefaultGenerator.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/ResultHolder.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/ValueConvertor.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/PatternPrinter.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/ValueConvertorImpl.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/parameter/ListParameterInfo.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/parameter/ParameterComputer.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/parameter/SetParameterInfo.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/parameter/AbstractParameterInfo.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/parameter/ParameterInfo.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/parameter/MapParameterInfo.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/parameter/ComputedParameterInfo.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/internal/parameter/SimpleNameOfTypeReferenceParameterComputer.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/PatternBuilderHelper.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/PatternBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/pattern/ConflictResolutionMode.java
../../kth/corpora/spoon/src/main/java/spoon/SpoonAPI.java
../../kth/corpora/spoon/src/main/java/spoon/experimental/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/CtModelImpl.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/path/CtPathStringBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/path/CtPathBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/path/impl/AbstractPathElement.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/path/impl/CtTypedNameElement.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/path/impl/CtPathElement.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/path/impl/CtNamedPathElement.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/path/impl/CtPathImpl.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/path/impl/CtRolePathElement.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/path/CtPath.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/path/CtPathException.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/path/CtElementPathBuilder.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/path/CtRole.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/ModelStreamer.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtCodeSnippetStatement.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtCatchVariable.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtStatementList.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/BinaryOperatorKind.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtStatement.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtSwitch.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtTargetedExpression.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtAssert.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtCFlowBreak.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtTryWithResource.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtForEach.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtBinaryOperator.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtWhile.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtTypeAccess.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtExecutableReferenceExpression.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtRHSReceiver.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtJavaDoc.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtCodeElement.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtCase.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtConditional.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtSynchronized.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtLabelledFlowBreak.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtLocalVariable.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtNewArray.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtArrayAccess.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtThrow.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtCatch.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtAnnotationFieldAccess.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtVariableWrite.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtFieldWrite.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtConstructorCall.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtBodyHolder.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtBlock.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtThisAccess.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtReturn.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtBreak.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtUnaryOperator.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtAssignment.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtArrayRead.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtLiteral.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtCodeSnippetExpression.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtFieldRead.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtFor.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtContinue.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtOperatorAssignment.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/UnaryOperatorKind.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtComment.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtIf.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtNewClass.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtTry.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtArrayWrite.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtLambda.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtSuperAccess.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtInvocation.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtFieldAccess.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtExpression.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtDo.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtAbstractInvocation.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtJavaDocTag.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtVariableAccess.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtLoop.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/code/CtVariableRead.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/Changes.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/meta/impl/SetHandler.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/meta/impl/ListHandler.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/meta/impl/AbstractRoleHandler.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/meta/impl/MapHandler.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/meta/impl/ModelRoleHandlers.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/meta/impl/SingleHandler.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/meta/impl/RoleHandlerHelper.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/meta/RoleHandler.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/meta/ContainerKind.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/cu/CompilationUnit.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/cu/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/cu/position/NoSourcePosition.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/cu/position/CompoundSourcePosition.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/cu/position/DeclarationSourcePosition.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/cu/position/BodyHolderSourcePosition.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/cu/SourcePosition.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/cu/SourcePositionHolder.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/CoreFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/FieldFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/FactoryImpl.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/ModuleFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/QueryFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/CodeFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/InterfaceFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/EvalFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/ConstructorFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/ClassFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/EnumFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/SubFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/Factory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/ExecutableFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/TypeFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/MethodFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/AnnotationFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/PackageFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/factory/CompilationUnitFactory.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtIntersectionTypeReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtArrayTypeReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtLocalVariableReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtParameterReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtExecutableReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtModuleReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtUnboundVariableReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtWildcardReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtTypeReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtTypeParameterReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtCatchVariableReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtVariableReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtActualTypeContainer.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtPackageReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/reference/CtFieldReference.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/ModelElementContainerDefaultCapacities.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtNamedElement.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtTypedElement.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtElement.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtAnonymousExecutable.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtType.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtConstructor.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtVariable.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtModifiable.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/ParentNotInitializedException.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtTypeParameter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtTypeMember.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtClass.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtModuleRequirement.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtCodeSnippet.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/ModifierKind.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtMultiTypedElement.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtInterface.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtProvidedService.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtTypeInformation.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtAnnotationType.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtEnum.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtImportKind.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtImport.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtPackage.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtAnnotation.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtUsedService.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtEnumValue.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtAnnotatedElementType.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtExecutable.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtField.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtModuleDirective.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtShadowable.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtFormalTypeDeclarer.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtParameter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtPackageExport.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtMethod.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtModule.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/declaration/CtAnnotationMethod.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/eval/PartialEvaluator.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/eval/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/MinimalImportScanner.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/TokenWriter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/Parent.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/CtVisitable.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/DefaultJavaPrettyPrinter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/PrinterHelper.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/printer/CommentOffset.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/ImportScannerImpl.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/AccessibleVariablesFinder.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/OperatorHelper.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/PrettyPrinter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/LiteralHelper.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/CtVisitor.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/CtAbstractVisitor.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/ListPrinter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/CtScanner.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/DefaultTokenWriter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/CtAbstractBiScanner.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/chain/CtConsumableFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/chain/CtQuery.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/chain/CtQueryAware.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/chain/CtQueryImpl.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/chain/CtQueryable.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/chain/CtConsumer.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/chain/CtFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/chain/CtScannerListener.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/chain/ScanningMode.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/chain/QueryFailurePolicy.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/EarlyTerminatingScanner.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/CtInheritanceScanner.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/Child.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/ReferenceTypeFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/OverriddenMethodFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/CtScannerFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/OverridingMethodFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/ParameterScopeFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/VariableScopeFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/SubtypeFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/VariableReferenceFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/CompositeFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/PotentialVariableDeclarationFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/VariableAccessFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/TypeFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/AllTypeMembersFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/SuperInheritanceHierarchyFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/AllMethodsSameSignatureFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/FieldReferenceFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/AbstractFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/ReturnOrThrowFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/CatchVariableScopeFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/SubInheritanceHierarchyFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/LocalVariableReferenceFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/AnnotationFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/ParameterReferenceFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/ParentFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/FieldAccessFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/RegexFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/DirectReferenceFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/CatchVariableReferenceFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/OverriddenMethodQuery.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/LambdaFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/FilteringOperator.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/AbstractReferenceFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/LineFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/SiblingsFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/ExecutableReferenceFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/FieldScopeFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/InvocationFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/NamedElementFilter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/filter/LocalVariableScopeFunction.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/ImportScanner.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/JavaIdentifiers.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/Filter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/Query.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/CacheBasedConflictFinder.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/ModelConsistencyChecker.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/CtBiScannerDefault.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/CtIterator.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/Root.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/PrintingContext.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/ElementPrinterHelper.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/CtDequeScanner.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/CommentHelper.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/visitor/AstParentConsistencyChecker.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/CtModel.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/annotations/PropertySetter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/annotations/PropertyGetter.java
../../kth/corpora/spoon/src/main/java/spoon/reflect/annotations/MetamodelPropertyField.java
../../kth/corpora/spoon/src/main/java/spoon/MavenLauncher.java
../../kth/corpora/spoon/src/main/java/spoon/IncrementalLauncher.java
../../kth/corpora/spoon/src/main/java/spoon/decompiler/CFRDecompiler.java
../../kth/corpora/spoon/src/main/java/spoon/decompiler/Decompiler.java
../../kth/corpora/spoon/src/main/java/spoon/metamodel/MetamodelProperty.java
../../kth/corpora/spoon/src/main/java/spoon/metamodel/MMMethodKind.java
../../kth/corpora/spoon/src/main/java/spoon/metamodel/MetamodelConcept.java
../../kth/corpora/spoon/src/main/java/spoon/metamodel/package-info.java
../../kth/corpora/spoon/src/main/java/spoon/metamodel/MMMethod.java
../../kth/corpora/spoon/src/main/java/spoon/metamodel/ConceptKind.java
../../kth/corpora/spoon/src/main/java/spoon/metamodel/Metamodel.java""".split('\n')[:100]

def build_vocabulary(files):
    count = {}
    tokenized_files = [ jlu.tokenize_with_white_space(jlu.open_file(path)) for path in files ]
    whitespace_id = set()

    threshold = 30

    def get_value(token):
        if isinstance(token, tokenizer.Comment):
            return token.__class__.__name__
        if isinstance(token, tokenizer.Literal):
            return token.__class__.__name__
        if isinstance(token, tokenizer.Operator):
            if token.is_infix():
                return "InfixOperator"
            if token.is_prefix():
                return "PrefixOperator"
            if token.is_postfix():
                return "PostfixOperator"
            if token.is_assignment():
                return "AssignmentOperator"

        return token.__class__.__name__

    for spaces, tokens in tokenized_files:
        whitespace_id = set(spaces) | whitespace_id
        for token in tokens:
            name = get_value(token)
            if not name in count:
                count[name] = 0
            count[name] += 1

    litterals = list(filter(lambda key: count[key] >= threshold, count.keys()))
    litterals = { key:value for key, value in zip(litterals, range(len(litterals))) }

    whitespace_id = { key:value for key, value in zip(whitespace_id, range(len(whitespace_id))) }

    len_litterals = len(litterals)
    len_whitespace = len(whitespace_id)
    vec_size = len_litterals + 1 + len_whitespace

    def get_vector(space, token):
        vector = np.array([0]*vec_size)
        if get_value(token) in litterals:
            vector[litterals[get_value(token)]] = 1
        else:
            vector[len_litterals] = 1
        vector[len_litterals + 1 + whitespace_id[space]] = 1
        return vector

    print(litterals.keys())

    return get_vector, whitespace_id


def vectorize_file(path, vectorizer):
    spaces, tokens = jlu.tokenize_with_white_space(jlu.open_file(path))

    result = []
    for ws, t in zip(spaces, tokens):
        result.append(vectorizer(ws, t))

    return result

if __name__ == "__main__":
    k = 20
    vectorizer, whitespace_id = build_vocabulary(files)
    print(len(whitespace_id))
    data = []
    for file in files:
        vector = []
        vector = vectorize_file(file, vectorizer)
        for i in range(k, len(vector) - k, 1):
            io = dict()
            io['input'] = np.array(vector[i-k:i+k+1]).copy()
            for i in range(len(whitespace_id)):
                io['input'][k][-1-i] = 0
            ws = vector[i][-len(whitespace_id):]
            i = 0
            j = 0
            for a in ws:
                if j == 0 and a == 1:
                    j = i
                i += 1
            # print(io['input'].shape)
            io['output'] = j
            # print(io['input'])
            data.append(io)
    random.shuffle(data)
    train_len = int(len(data) * 0.8)
    train_data = data[:train_len]
    test_data = data[train_len:]
    print(f'Train files {train_len}')

    train_input = np.array([d['input'] for d in train_data])
    train_labels = np.array([d['output'] for d in train_data])
    #
    test_input = np.array([d['input'] for d in test_data])
    test_labels = np.array([d['output'] for d in test_data])

    print(train_input[0])


    # tf.app.run()
