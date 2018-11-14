import java_lang_utils as jlu
import tensorflow as tf
import numpy as np
from tensorflow import keras
from javalang import tokenizer
import random

# tf.logging.set_verbosity(tf.logging.INFO)


def build_vocabulary(files):
    count = {}
    tokenized_files = [ jlu.tokenize_with_white_space(jlu.open_file(path)) for path in files ]
    whitespace_id = set()

    threshold = 180

    def get_value(token):
        if isinstance(token, tokenizer.Comment):
            return token.__class__.__name__
        # if isinstance(token, tokenizer.Literal):
        #     return token.__class__.__name__
        # if isinstance(token, tokenizer.Operator):
        #     if token.is_infix():
        #         return "InfixOperator"
        #     if token.is_prefix():
        #         return "PrefixOperator"
        #     if token.is_postfix():
        #         return "PostfixOperator"
        #     if token.is_assignment():
        #         return "AssignmentOperator"

        return token.value

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
    vec_size = len_litterals + 1 + 2

    def get_vector(space, token):
        vector = np.array([0]*vec_size)
        if get_value(token) in litterals:
            vector[litterals[get_value(token)]] = 1
        else:
            vector[len_litterals] = 1
        vector[len_litterals + 1] = space[0]
        vector[len_litterals + 2] = space[1]
        return vector

    print(len(litterals))

    return get_vector, whitespace_id


def vectorize_file(path, vectorizer):
    spaces, tokens = jlu.tokenize_with_white_space(jlu.open_file(path))

    result = []
    for ws, t in zip(spaces, tokens):
        result.append(vectorizer(ws, t))

    return result

files = """./test_corpora/java-design-patterns/data/adapter/src/main/java/com/iluwatar/adapter/App.java
./test_corpora/java-design-patterns/data/adapter/src/main/java/com/iluwatar/adapter/FishingBoatAdapter.java
./test_corpora/java-design-patterns/data/adapter/src/main/java/com/iluwatar/adapter/FishingBoat.java
./test_corpora/java-design-patterns/data/adapter/src/main/java/com/iluwatar/adapter/Captain.java
./test_corpora/java-design-patterns/data/adapter/src/main/java/com/iluwatar/adapter/RowingBoat.java
./test_corpora/java-design-patterns/data/adapter/src/test/java/com/iluwatar/adapter/AdapterPatternTest.java
./test_corpora/java-design-patterns/data/adapter/src/test/java/com/iluwatar/adapter/AppTest.java
./test_corpora/java-design-patterns/data/aggregator-microservices/information-microservice/src/main/java/com/iluwatar/information/microservice/InformationApplication.java
./test_corpora/java-design-patterns/data/aggregator-microservices/information-microservice/src/main/java/com/iluwatar/information/microservice/InformationController.java
./test_corpora/java-design-patterns/data/aggregator-microservices/information-microservice/src/test/java/com/iluwatar/information/microservice/InformationControllerTest.java
./test_corpora/java-design-patterns/data/aggregator-microservices/aggregator-service/src/main/java/com/iluwatar/aggregator/microservices/ProductInformationClient.java
./test_corpora/java-design-patterns/data/aggregator-microservices/aggregator-service/src/main/java/com/iluwatar/aggregator/microservices/ProductInventoryClient.java
./test_corpora/java-design-patterns/data/aggregator-microservices/aggregator-service/src/main/java/com/iluwatar/aggregator/microservices/App.java
./test_corpora/java-design-patterns/data/aggregator-microservices/aggregator-service/src/main/java/com/iluwatar/aggregator/microservices/ProductInventoryClientImpl.java
./test_corpora/java-design-patterns/data/aggregator-microservices/aggregator-service/src/main/java/com/iluwatar/aggregator/microservices/Product.java
./test_corpora/java-design-patterns/data/aggregator-microservices/aggregator-service/src/main/java/com/iluwatar/aggregator/microservices/Aggregator.java
./test_corpora/java-design-patterns/data/aggregator-microservices/aggregator-service/src/main/java/com/iluwatar/aggregator/microservices/ProductInformationClientImpl.java
./test_corpora/java-design-patterns/data/aggregator-microservices/aggregator-service/src/test/java/com/iluwatar/aggregator/microservices/AggregatorTest.java
./test_corpora/java-design-patterns/data/aggregator-microservices/inventory-microservice/src/main/java/com/iluwatar/inventory/microservice/InventoryController.java
./test_corpora/java-design-patterns/data/aggregator-microservices/inventory-microservice/src/main/java/com/iluwatar/inventory/microservice/InventoryApplication.java
./test_corpora/java-design-patterns/data/aggregator-microservices/inventory-microservice/src/test/java/com/iluwatar/inventory/microservice/InventoryControllerTest.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/TypeSpec.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/CodeWriter.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/Util.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/ParameterizedTypeName.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/TypeVariableName.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/ParameterSpec.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/ClassName.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/WildcardTypeName.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/TypeName.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/LineWrapper.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/ArrayTypeName.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/MethodSpec.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/FieldSpec.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/CodeBlock.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/AnnotationSpec.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/JavaFile.java
./test_corpora/javapoet/data/main/java/com/squareup/javapoet/NameAllocator.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/Options.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/HelpFormatter.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/Util.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/UnrecognizedOptionException.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/PatternOptionBuilder.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/AlreadySelectedException.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/Parser.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/OptionGroup.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/CommandLine.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/TypeHandler.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/OptionValidator.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/package-info.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/MissingArgumentException.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/GnuParser.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/OptionBuilder.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/ParseException.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/CommandLineParser.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/DefaultParser.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/AmbiguousOptionException.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/MissingOptionException.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/Option.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/BasicParser.java
./test_corpora/commons-cli/data/main/java/org/apache/commons/cli/PosixParser.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/facade/embedded/EmbeddedGraphDatabase.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/facade/GraphDatabaseDependencies.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/facade/GraphDatabaseFacadeFactory.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/facade/spi/ClassicCoreSPI.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/facade/spi/ProcedureGDBFacadeSPI.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/GraphDatabaseFactory.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/GraphDatabaseFactoryState.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/EditionLocksFactories.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/module/PlatformModule.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/module/DataSourceModule.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/module/ProcedureGDSFactory.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/module/id/IdContextFactoryBuilder.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/module/id/IdContextFactory.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/module/id/DatabaseIdContext.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/module/edition/DefaultEditionModule.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/module/edition/context/DatabaseEditionContext.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/module/edition/context/DefaultEditionModuleDatabaseContext.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/module/edition/SecurityModuleDependenciesDependencies.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/module/edition/AbstractEditionModule.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/module/edition/CommunityEditionModule.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/graphdb/factory/module/ModularDatabaseCreationContext.java
./test_corpora/neo4j/data/community/neo4j/src/main/java/org/neo4j/dmbs/database/DefaultDatabaseManager.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/ByteOrderParser.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/FileSystemUtils.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/CopyUtils.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/HexDump.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/ProxyWriter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/TaggedOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/LockableFileWriter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/ThresholdingOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/StringBuilderWriter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/NullOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/ByteArrayOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/ProxyOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/WriterOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/DeferredFileOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/XmlStreamWriter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/DemuxOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/ChunkedOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/CountingOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/FileWriterWithEncoding.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/CloseShieldOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/AppendableOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/NullWriter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/BrokenOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/ChunkedWriter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/ClosedOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/output/TeeOutputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/FileCleaner.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/ThreadMonitor.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/comparator/ReverseComparator.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/comparator/DefaultFileComparator.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/comparator/DirectoryFileComparator.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/comparator/PathFileComparator.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/comparator/CompositeFileComparator.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/comparator/AbstractFileComparator.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/comparator/NameFileComparator.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/comparator/SizeFileComparator.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/comparator/LastModifiedFileComparator.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/comparator/ExtensionFileComparator.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/IOCase.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/FilenameUtils.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/serialization/ValidatingObjectInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/serialization/RegexpClassNameMatcher.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/serialization/FullClassNameMatcher.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/serialization/WildcardClassNameMatcher.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/serialization/ClassNameMatcher.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/LineIterator.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/monitor/FileAlterationListenerAdaptor.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/monitor/FileAlterationObserver.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/monitor/FileAlterationMonitor.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/monitor/FileAlterationListener.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/monitor/FileEntry.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/FileDeleteStrategy.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/IOExceptionWithCause.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/FileUtils.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/IOFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/RegexFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/MagicNumberFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/AgeFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/HiddenFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/OrFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/FileFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/EmptyFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/CanWriteFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/NameFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/WildcardFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/ConditionalFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/CanReadFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/SuffixFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/DirectoryFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/SizeFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/AndFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/AbstractFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/FalseFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/FileFilterUtils.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/WildcardFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/DelegateFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/PrefixFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/NotFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/filefilter/TrueFileFilter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/EndianUtils.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/FileCleaningTracker.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/ProxyInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/XmlStreamReaderException.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/BrokenInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/InfiniteCircularInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/SwappedDataInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/BOMInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/ProxyReader.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/ReversedLinesFileReader.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/TeeInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/CharacterFilterReader.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/XmlStreamReader.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/CloseShieldInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/ClosedInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/AbstractCharacterFilterReader.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/ObservableInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/BoundedInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/AutoCloseInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/Tailer.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/UnixLineEndingInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/CharacterSetFilterReader.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/ClassLoaderObjectInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/WindowsLineEndingInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/CharSequenceReader.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/DemuxInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/ReaderInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/NullInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/TaggedInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/CharSequenceInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/BoundedReader.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/NullReader.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/CountingInputStream.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/TailerListener.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/input/TailerListenerAdapter.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/ByteOrderMark.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/TaggedIOException.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/DirectoryWalker.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/FileExistsException.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/FileSystem.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/IOUtils.java
./test_corpora/commons-io/data/src/main/java/org/apache/commons/io/Charsets.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/Utils.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/notifier/EndProcessNotifier.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/notifier/engines/NotifierEngine.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/notifier/engines/EmailNotifierEngine.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/BuildToBeInspected.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/serializer/Serializer.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/serializer/HardwareInfoSerializer.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/serializer/engines/table/CSVSerializerEngine.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/serializer/engines/SerializerEngine.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/serializer/engines/SerializedData.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/serializer/engines/json/MongoDBSerializerEngine.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/serializer/engines/json/JSONFileSerializerEngine.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/serializer/mongodb/MongoConnection.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/serializer/SerializerType.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/serializer/ProcessSerializer.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/states/PipelineState.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/states/BearsMode.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/states/ScannedBuildStatus.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/states/LauncherMode.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/states/PushState.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/InputBuildId.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/docker/DockerHelper.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/GsonPathTypeAdapter.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/LauncherType.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/config/RepairnatorConfig.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/config/RepairnatorConfigReader.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/config/RepairnatorConfigException.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/PeriodStringParser.java
./test_corpora/repairnator-core/data/src/main/java/fr/inria/spirals/repairnator/LauncherUtils.java
./test_corpora/repairnator-core/data/src/test/java/fr/inria/spirals/repairnator/TestUtils.java
./test_corpora/repairnator-core/data/src/test/java/fr/inria/spirals/repairnator/TestSerializerUtils.java
./test_corpora/repairnator-core/data/src/test/java/fr/inria/spirals/repairnator/config/TestRepairnatorConfig.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/BitField.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/RecursiveToStringStyle.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/Builder.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/DiffResult.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/Diff.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/HashCodeExclude.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/DiffBuilder.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/ToStringSummary.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/CompareToBuilder.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/HashCodeBuilder.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/MultilineRecursiveToStringStyle.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/ReflectionToStringBuilder.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/package-info.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/ReflectionDiffBuilder.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/EqualsBuilder.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/ToStringStyle.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/IDKey.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/EqualsExclude.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/ToStringBuilder.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/StandardToStringStyle.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/ToStringExclude.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/builder/Diffable.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/ArrayUtils.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/CharSequenceUtils.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/CharRange.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/AnnotationUtils.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/ArchUtils.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/CharEncoding.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/arch/package-info.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/arch/Processor.java
./test_corpora/commons-lang/data/src/main/java/org/apache/commons/lang3/BooleanUtils.java
./test_corpora/spoon/data/main/java/spoon/JarLauncher.java
./test_corpora/spoon/data/main/java/spoon/SpoonException.java
./test_corpora/spoon/data/main/java/spoon/Launcher.java
./test_corpora/spoon/data/main/java/spoon/template/AbstractTemplate.java
./test_corpora/spoon/data/main/java/spoon/template/Substitution.java
./test_corpora/spoon/data/main/java/spoon/template/TemplateParameter.java
./test_corpora/spoon/data/main/java/spoon/template/TemplateException.java
./test_corpora/spoon/data/main/java/spoon/template/Local.java
./test_corpora/spoon/data/main/java/spoon/template/package-info.java
./test_corpora/spoon/data/main/java/spoon/template/BlockTemplate.java
./test_corpora/spoon/data/main/java/spoon/template/StatementTemplate.java
./test_corpora/spoon/data/main/java/spoon/template/TypedBlockTemplateParameter.java
./test_corpora/spoon/data/main/java/spoon/template/Template.java
./test_corpora/spoon/data/main/java/spoon/template/ExtensionTemplate.java
./test_corpora/spoon/data/main/java/spoon/template/TemplateBuilder.java
./test_corpora/spoon/data/main/java/spoon/template/TemplateMatcher.java
./test_corpora/spoon/data/main/java/spoon/template/TypedStatementListTemplateParameter.java
./test_corpora/spoon/data/main/java/spoon/template/ExpressionTemplate.java
./test_corpora/spoon/data/main/java/spoon/template/Parameter.java
./test_corpora/spoon/data/main/java/spoon/testing/utils/Check.java
./test_corpora/spoon/data/main/java/spoon/testing/utils/ProcessorUtils.java
./test_corpora/spoon/data/main/java/spoon/testing/utils/ModelUtils.java
./test_corpora/spoon/data/main/java/spoon/testing/FileAssert.java
./test_corpora/spoon/data/main/java/spoon/testing/AbstractCtElementAssert.java
./test_corpora/spoon/data/main/java/spoon/testing/CtElementAssert.java
./test_corpora/spoon/data/main/java/spoon/testing/AbstractAssert.java
./test_corpora/spoon/data/main/java/spoon/testing/Assert.java
./test_corpora/spoon/data/main/java/spoon/testing/CtPackageAssert.java
./test_corpora/spoon/data/main/java/spoon/testing/AbstractFileAssert.java
./test_corpora/spoon/data/main/java/spoon/testing/AbstractCtPackageAssert.java
./test_corpora/spoon/data/main/java/spoon/OutputType.java
./test_corpora/spoon/data/main/java/spoon/package-info.java
./test_corpora/spoon/data/main/java/spoon/SpoonModelBuilder.java
./test_corpora/spoon/data/main/java/spoon/SpoonAPI.java
./test_corpora/spoon/data/main/java/spoon/MavenLauncher.java
./test_corpora/spoon/data/main/java/spoon/IncrementalLauncher.java""".split('\n')

if __name__ == "__main__":
    k = 17
    vectorizer, whitespace_id = build_vocabulary(files)

    data = []
    for file in files:
        vector = []
        vector = vectorize_file(file, vectorizer)
        for i in range(k, len(vector) - k, 1):
            io = dict()
            io['input'] = np.array(vector[i-k:i+k+1]).copy()
            io['input'][k][-1] = 0
            io['input'][k][-2] = 0
            # print(io['input'].shape)
            io['output'] = whitespace_id[tuple(vector[i][-2:])]
            # print(io['input'])
            data.append(io)
    random.shuffle(data)
    train_len = int(len(data) * 0.8)
    train_data = data[:train_len]
    test_data = data[train_len:]
    print(f'Train files {train_len}')

    train_input = np.array([d['input'] for d in train_data])
    train_labels = np.array([d['output'] for d in train_data])
    print(train_labels[10:200])
    #
    test_input = np.array([d['input'] for d in test_data])
    test_labels = np.array([d['output'] for d in test_data])
    #
    # # print(train_input)
    #
    model = keras.Sequential([
        keras.layers.Flatten(input_shape=train_input[0].shape),
        keras.layers.Dense(128, activation=tf.nn.relu),
        keras.layers.Dense(128, activation=tf.nn.relu),
        keras.layers.Dense(len(whitespace_id), activation=tf.nn.softmax)
    ])
    model.compile(optimizer=tf.train.AdamOptimizer(),
        loss='sparse_categorical_crossentropy',
        metrics=['accuracy'])
    # # help(model.fit)
    model.fit(train_input, train_labels, epochs=10)
    #
    test_loss, test_acc = model.evaluate(test_input, test_labels)
    #
    print('Test accuracy:', test_acc)


    # tf.app.run()
