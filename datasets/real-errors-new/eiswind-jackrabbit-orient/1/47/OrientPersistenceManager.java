/*
 * (c) 2013 by Thomas  Kratz
 */
package de.eiswind.jackrabbit.persistence.orient;

import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexManagerProxy;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.query.OQuery;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.apache.jackrabbit.core.fs.FileSystem;
import org.apache.jackrabbit.core.fs.FileSystemException;
import org.apache.jackrabbit.core.id.NodeId;
import org.apache.jackrabbit.core.id.PropertyId;
import org.apache.jackrabbit.core.persistence.PMContext;
import org.apache.jackrabbit.core.persistence.bundle.AbstractBundlePersistenceManager;
import org.apache.jackrabbit.core.persistence.util.BLOBStore;
import org.apache.jackrabbit.core.persistence.util.BundleBinding;
import org.apache.jackrabbit.core.persistence.util.ErrorHandling;
import org.apache.jackrabbit.core.persistence.util.FileSystemBLOBStore;
import org.apache.jackrabbit.core.persistence.util.NodePropBundle;
import org.apache.jackrabbit.core.state.ChangeLog;
import org.apache.jackrabbit.core.state.ItemStateException;
import org.apache.jackrabbit.core.state.NoSuchItemStateException;
import org.apache.jackrabbit.core.state.NodeReferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.PropertyType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * (C) 2013 by Thomas Kratz
 *  this is experimental code. if you reuse it, you do so at your own risk
 *  if you modify and/or or republish it, you must publish the original authors name with your source code.
 */

/**
 * This is a generic persistence manager that stores the {@link NodePropBundle}s
 * in an orient db datastore.
 * <p>
 */
public class OrientPersistenceManager extends AbstractBundlePersistenceManager {

    private static final int MIN_BLOB_SIZE = 0x1000;
    /**
     * the default logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(OrientPersistenceManager.class);
    private static final int POOL_MAX_SIZE = 50;
    private static final int SB_CAPACITY = 35;
    private static final int THIRTEEN = 13;
    private static final int EIGHTEEN = 18;
    private static final int TWENTYTHREE = 23;
    private static final int EIGHT = 8;

    /**
     * flag indicating if this manager was initialized.
     */
    private boolean initialized;

    /**
     * prefix for orient classnames.
     */
    private String objectPrefix;

    private String url;
    private String user = "admin";
    private String pass = "admin";


    private FileSystem itemFs;

    private ODatabaseDocumentPool pool;

    /**
     * gets the db url.
     *
     * @return the url
     */
    public final String getUrl() {
        return url;
    }

    /**
     * sets the db url.
     *
     * @param myUrl the url
     */
    public final void setUrl(final String myUrl) {
        this.url = myUrl;
    }

    /**
     * gets the object prefix.
     *
     * @return the prefix
     */
    public final String getSchemaObjectPrefix() {
        return objectPrefix;
    }

    /**
     * sets the object prefix.
     *
     * @param objectPrefix1 the prefix
     */
    public final void setSchemaObjectPrefix(final String objectPrefix1) {
        this.objectPrefix = objectPrefix1;

    }

    /**
     * gets the user.
     *
     * @return the user
     */
    public final String getUser() {
        return user;
    }

    /**
     * sets the user.
     *
     * @param user1 the user
     */
    public final void setUser(final String user1) {
        this.user = user1;
    }

    /**
     * gets the password.
     *
     * @return the password
     */
    public final String getPass() {
        return pass;
    }

    /**
     * sets the password.
     *
     * @param pass1 the password
     */
    public final void setPass(final String pass1) {
        this.pass = pass1;
    }


    /**
     * the minimum size of a property until it gets written to the blob store.
     */
    private int minBlobSize = MIN_BLOB_SIZE;

    /**
     * the bundle binding.
     */
    private BundleBinding binding;


    /**
     * flag for error handling.
     */
    private ErrorHandling errorHandling = new ErrorHandling();

    /**
     * the name of this persistence manager.
     */
    private String name = super.toString();


    private String bundleClassName;
    private String refsClassName;

    /**
     * file system where BLOB data is stored.
     */
    private OrientPersistenceManager.CloseableBLOBStore blobStore;


    private int blobFSBlockSize;


    private Map<NodeId, BundleMapper> documentMap = new HashMap<NodeId, BundleMapper>();

    /**
     * Sets the error handling behaviour of this manager. See {@link ErrorHandling}
     * for details about the flags.
     *
     * @param myErrorHandling the error handling
     */
    public final void setErrorHandling(final String myErrorHandling) {
        this.errorHandling = new ErrorHandling(myErrorHandling);
    }

    /**
     * Returns the error handling configuration of this manager.
     *
     * @return the error handling configuration of this manager
     */
    public final String getErrorHandling() {
        return errorHandling.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected final BLOBStore getBlobStore() {
        return blobStore;
    }

    /**
     * should blob store be used.
     *
     * @return the if blob store is used
     */
    public final boolean useLocalFsBlobStore() {
        return blobFSBlockSize == 0;
    }


    private BinaryFileSystemHelper fileSystem;

    /**
     * {@inheritDoc}
     */
    public final void init(final PMContext context) throws Exception {
        if (initialized) {
            throw new IllegalStateException("already initialized");
        }
        this.fileSystem = new BinaryFileSystemHelper(context.getFileSystem());

        OGlobalConfiguration.STORAGE_KEEP_OPEN.setValue(false);
        ODatabaseDocumentTx db;

        db = new ODatabaseDocumentTx(url);

        if (!db.exists()) {
            db.create();

        }

        pool = new ODatabaseDocumentPool(url, "admin", "admin");
        pool.setup(1, POOL_MAX_SIZE);
        super.init(context);

        // load namespaces
        binding = new BundleBinding(errorHandling, blobStore, getNsIndex(), getNameIndex(), context.getDataStore());
        binding.setMinBlobSize(minBlobSize);


        this.name = context.getHomeDir().getName();


        runWithDatabase(database -> {

            OSchema schema = database.getMetadata().getSchema();
            bundleClassName = getSchemaObjectPrefix() + name + "Bundle";
            refsClassName = getSchemaObjectPrefix() + name + "Refs";
            OClass bundleClass = schema.getClass(bundleClassName);
            OClass vertexClass = schema.getClass("V");
            if (bundleClass == null) {
                bundleClass = schema.createClass(bundleClassName, vertexClass);
                OProperty id = bundleClass.createProperty("uuid", OType.STRING);
                id.createIndex(OClass.INDEX_TYPE.UNIQUE);
                schema.save();
            }

            OClass refsClass = schema.getClass(refsClassName);
            if (refsClass == null) {
                refsClass = schema.createClass(refsClassName, vertexClass);
                OProperty id = refsClass.createProperty("targetuuid", OType.STRING);
                id.createIndex(OClass.INDEX_TYPE.UNIQUE);
                schema.save();
            }
            return null;
        });


        initialized = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void store(final ChangeLog changeLog) throws ItemStateException {
        documentMap.clear();
        runWithDatabase(db -> {

            try {

                super.store(changeLog);
            } catch (ItemStateException e) {
                LOG.error("jackrabbit", e);
            }
            return null;
        });

        documentMap.clear();
    }


    /**
     * runs in an orient transaction.
     *
     * @param function the lamba.
     * @return the result
     */
    private Object runWithDatabase(final Function<ODatabaseRecord, Object> function) {

        ODatabaseDocumentTx closeDB = null;
        ODatabaseRecord database;
        if (!ODatabaseRecordThreadLocal.INSTANCE.isDefined()) {


            closeDB = pool.acquire(url, user, pass);
            database = closeDB;


        } else {
            database = ODatabaseRecordThreadLocal.INSTANCE.get();
            if (database.isClosed()) {
                database.open(user, pass);
            }
        }

        Object result = null;
        try {
            database.getTransaction().begin();
            result = function.apply(database);
            database.getTransaction().commit();
        } catch (OException x) {
            database.getTransaction().rollback();
            LOG.error("DB error", x);
            throw x;
        } finally {
            if (closeDB != null) {

                closeDB.close();

            }
        }

        return result;

    }

    /**
     * {@inheritDoc}
     */
    public final synchronized void close() throws Exception {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }

        try {

            pool.close();

            super.close();
        } finally {
            initialized = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected final NodePropBundle loadBundle(final NodeId id) throws ItemStateException {
        try {
            return (NodePropBundle) runWithDatabase(database -> {
                String uuid = id.toString();

                ODocument doc = loadBundleDoc(uuid);
                if (doc == null) {
                    return null;
                }

                BundleMapper mapper = new BundleMapper(doc, database, fileSystem);
                NodePropBundle bundle = mapper.read();
                return bundle;
            });
        } catch (Exception e) {
            String msg = "failed to read bundle: " + id + ": " + e;
            LOG.error(msg);
            throw new ItemStateException(msg, e);
        }
    }

    /**
     * Creates the file path for the given node id that is
     * suitable for storing node states in a filesystem.
     *
     * @param pbuf buffer to append to or <code>null</code>
     * @param id   the id of the node
     * @return the buffer with the appended data.
     */
    @Override
    protected final StringBuffer buildNodeFilePath(final StringBuffer pbuf, final NodeId id) {
        StringBuffer buf;
        if (pbuf == null) {
            buf = new StringBuffer();
        } else {
            buf = pbuf;
        }
        buildNodeFolderPath(buf, id);
        buf.append('.');
        buf.append(NODEFILENAME);
        return buf;
    }

    /**
     * Creates the file path for the given references id that is
     * suitable for storing reference states in a filesystem.
     *
     * @param pbuf buffer to append to or <code>null</code>
     * @param id   the id of the node
     * @return the buffer with the appended data.
     */
    @Override
    protected final StringBuffer buildNodeReferencesFilePath(final StringBuffer pbuf, final NodeId id) {
        StringBuffer buf;
        if (pbuf == null) {
            buf = new StringBuffer();
        } else {
            buf = pbuf;
        }
        buildNodeFolderPath(buf, id);
        buf.append('.');
        buf.append(NODEREFSFILENAME);
        return buf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final synchronized void storeBundle(final NodePropBundle bundle) throws ItemStateException {
        runWithDatabase(database -> {

            try {
                ODocument vertex = null;
                if (bundle.isNew()) {

                    vertex = new ODocument(bundleClassName);
                } else {
                    vertex = loadBundleDoc(bundle.getId().toString());
                }
                if (vertex == null) {
                    throw new IllegalStateException("FATAL: Tried to update non existing bundle" + bundle.getId().toString());
                }
                BundleMapper mapper = new BundleMapper(vertex, database, fileSystem);
                mapper.writePhase1(bundle);
                vertex.save();
                NodeId id = bundle.getId();
                // store this for phase2

                documentMap.put(id, mapper);

            } catch (Exception e) {
                String msg = "failed to write bundle: " + bundle.getId();
                OrientPersistenceManager.LOG.error(msg, e);

            }
            return null;
        });

    }

    /**
     * {@inheritDoc}
     */
    protected final synchronized void destroyBundle(final NodePropBundle bundle) throws ItemStateException {

        runWithDatabase(database -> {
            try {
                String uuid = bundle.getId().toString();
                ODocument result = loadBundleDoc(uuid);
                if (result == null) {
                    throw new NullPointerException(uuid + " is missing");
                }
                cascadeDeleteToBlobs(result);

                result.delete();
            } catch (Exception e) {
                String msg = "failed to delete bundle: " + bundle.getId();
                OrientPersistenceManager.LOG.error(msg, e);

            }
            return null;
        });
    }

    /**
     * deletes referenced blobs.
     *
     * @param result the document
     */
    private void cascadeDeleteToBlobs(final ODocument result) {
        List<ODocument> propertyDocs = result.field("properties", OType.EMBEDDEDLIST);
        if (propertyDocs != null) {
            for (ODocument pDoc : propertyDocs) {
                List<ODocument> valDocs = pDoc.field("properties", OType.EMBEDDEDLIST);
                if (valDocs != null) {
                    for (ODocument vDoc : valDocs) {
                        int type = vDoc.field("type", OType.INTEGER);
                        if (PropertyType.BINARY == type) {
                            Boolean embedded = vDoc.field("embedded", OType.BOOLEAN);
                            if (!embedded) {
                                fileSystem.delete(vDoc.field("value", OType.STRING));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * load a bundle doc.
     *
     * @param uuid the id
     * @return the document
     */
    private ODocument loadBundleDoc(final String uuid) {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        return (ODocument) runWithDatabase(database -> {
            OIndexManagerProxy indexManager = database.getMetadata().getIndexManager();
            OIndex<OIdentifiable> index = (OIndex<OIdentifiable>) indexManager.getIndex(bundleClassName + ".uuid");
            OIdentifiable id = index.get(uuid);
            ODocument doc = null;
            if (id != null) {
                doc = id.getRecord();
            }

            return doc;
        });


    }

    /**
     * loads references.
     *
     * @param targetuuid the id
     * @return the refs doc
     */
    private ODocument loadRefsDoc(final String targetuuid) {

        return (ODocument) runWithDatabase(database -> {
            OQuery<ODocument> query =
                    new OSQLSynchQuery<>("select from " + refsClassName + " WHERE targetuuid = '" + targetuuid + "'");
            List<ODocument> result = database.query(query);
            if (result.size() == 0) {
                return null;
            }
            // result must be unique since we have the index
            return result.get(0);
        });
    }

    /**
     * {@inheritDoc}
     */
    public final synchronized NodeReferences loadReferencesTo(final NodeId targetId) throws ItemStateException {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        NodeReferences result = (NodeReferences) runWithDatabase(database -> {
            ODocument refsDoc = loadRefsDoc(targetId.toString());
            if (refsDoc == null) {
                return null;
            }
            NodeReferences refs = new NodeReferences(targetId);
            List<ODocument> refDocs = refsDoc.field("refs", OType.EMBEDDEDLIST);
            for (ODocument rDoc : refDocs) {
                String value = rDoc.field("ref", OType.STRING);
                refs.addReference(PropertyId.valueOf(value));
            }
            return refs;
        });
        if (result == null) {
            throw new NoSuchItemStateException(targetId.toString());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public final synchronized void store(final NodeReferences refs) throws ItemStateException {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        runWithDatabase(database -> {
            ODocument refsDoc = loadRefsDoc(refs.getTargetId().toString());
            if (refsDoc == null) {
                refsDoc = new ODocument(refsClassName);
                refsDoc.field("targetuuid", refs.getTargetId().toString(), OType.STRING);

            }
            List<ODocument> refDocs = new ArrayList<ODocument>();
            for (PropertyId propId : refs.getReferences()) {
                ODocument refDoc = database.newInstance();
                refDoc.field("ref", propId.toString(), OType.STRING);
                refDocs.add(refDoc);

            }
            refsDoc.field("refs", refDocs, OType.EMBEDDEDLIST);
            refsDoc.save();
            return null;
        });
    }

    /**
     * {@inheritDoc}
     */
    public final synchronized void destroy(final NodeReferences refs) throws ItemStateException {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        runWithDatabase(database -> {
            ODocument doc = loadRefsDoc(refs.getTargetId().toString());
            if (doc != null) {
                doc.delete();
            }
            return null;
        });
    }

    /**
     * {@inheritDoc}
     */
    public final synchronized boolean existsReferencesTo(final NodeId targetId) throws ItemStateException {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        return (Boolean) runWithDatabase(database -> {
            ODocument doc = loadRefsDoc(targetId.toString());
            return doc != null;
        });
    }


    /**
     * {@inheritDoc}
     */
    public final String toString() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public final List<NodeId> getAllNodeIds(final NodeId bigger, final int maxCount) throws ItemStateException {
        ArrayList<NodeId> list = new ArrayList<NodeId>();
        try {
            getListRecursive(list, "", bigger, maxCount);
            return list;
        } catch (FileSystemException e) {
            String msg = "failed to read node list: " + bigger + ": " + e;
            LOG.error(msg);
            throw new ItemStateException(msg, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected final NodeId getIdFromFileName(final String fileName) {
        StringBuffer buff = new StringBuffer(SB_CAPACITY);
        if (!fileName.endsWith("." + NODEFILENAME)) {
            return null;
        }
        for (int i = 0; i < fileName.length(); i++) {
            char c = fileName.charAt(i);
            if (c == '.') {
                break;
            }
            if (c != '/') {
                buff.append(c);
                int len = buff.length();
                if (len == EIGHT || len == THIRTEEN || len == EIGHTEEN || len == TWENTYTHREE) {
                    buff.append('-');
                }
            }
        }
        return new NodeId(buff.toString());
    }

    /**
     * gets a list from the filesystem.
     *
     * @param list     the list
     * @param path     the path
     * @param bigger   for recusrion
     * @param maxCount up to max?
     * @throws FileSystemException on fs errors
     */
    private void getListRecursive(final ArrayList<NodeId> list, final String path,
                                  final NodeId bigger, final int maxCount) throws FileSystemException {
        if (maxCount > 0 && list.size() >= maxCount) {
            return;
        }
        String[] files = itemFs.listFiles(path);
        Arrays.sort(files);
        for (int i = 0; i < files.length; i++) {
            String f = files[i];
            NodeId n = getIdFromFileName(path + FileSystem.SEPARATOR + f);
            if (n == null) {
                continue;
            }
            if (bigger != null && bigger.toString().compareTo(n.toString()) >= 0) {
                continue;
            }
            list.add(n);
            if (maxCount > 0 && list.size() >= maxCount) {
                return;
            }
        }
        String[] dirs = itemFs.listFolders(path);
        Arrays.sort(dirs);
        for (int i = 0; i < dirs.length; i++) {
            getListRecursive(list, path + FileSystem.SEPARATOR + dirs[i], bigger, maxCount);
        }
    }

    /**
     * Helper interface for closeable stores.
     */
    protected interface CloseableBLOBStore extends BLOBStore {
        /**
         * close this store.
         */
        void close();
    }

    /**
     * own implementation of the filesystem blob store that uses a different
     * blob-id scheme.
     */
    private class FSBlobStore extends FileSystemBLOBStore implements OrientPersistenceManager.CloseableBLOBStore {

        private FileSystem fs;

        /**
         * init te blobstore.
         *
         * @param pfs the underlying filesystem.
         */
        public FSBlobStore(final FileSystem pfs) {
            super(pfs);
            this.fs = pfs;
        }

        /**
         * create an id.
         *
         * @param id    the id
         * @param index the index.
         * @return the blob path,
         */
        public String createId(final PropertyId id, final int index) {
            return buildBlobFilePath(null, id, index).toString();
        }

        /**
         * {@inheritDoc}
         */
        public final void close() {
            try {
                fs.close();
                fs = null;
            } catch (Exception e) {
                LOG.warn("close blob stors", e);
            }
        }

    }

}
