/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.eiswind.jackrabbit.persistence.orient;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.core.NamespaceRegistryImpl;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.fs.mem.MemoryFileSystem;
import org.apache.jackrabbit.core.id.NodeId;
import org.apache.jackrabbit.core.id.PropertyId;
import org.apache.jackrabbit.core.persistence.PMContext;
import org.apache.jackrabbit.core.persistence.PersistenceManager;
import org.apache.jackrabbit.core.state.*;
import org.apache.jackrabbit.core.value.InternalValue;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.stats.RepositoryStatisticsImpl;

import javax.jcr.PropertyType;
import java.io.File;
import java.util.Arrays;

public class PersistenceManagerTest extends TestCase {

    private static final NodeId NODE_ID = NodeId.randomId();

    private static final NodeId CHILD_ID = NodeId.randomId();

    private static final Name TEST =
        NameFactoryImpl.getInstance().create("", "test");

    private static final PropertyId PROPERTY_ID = new PropertyId(NODE_ID, TEST);

    private File directory;



    protected void setUp() throws Exception {
        directory = File.createTempFile("jackrabbit-persistence-", "-test");
        directory.delete();
        directory.mkdirs();


    }

    protected void tearDown() throws Exception {
        FileUtils.forceDelete(directory);
    }

    public void testOrientPersistenceManager() throws Exception {
        OrientPersistenceManager manager = new OrientPersistenceManager();
        manager.setUrl("memory:graph");
        manager.setUser("admin");
        manager.setPass("admin");
        manager.setSchemaObjectPrefix("Test");
        manager.setCreateDB(true);
        assertPersistenceManager(manager);
    }





    private void assertPersistenceManager(PersistenceManager manager)
            throws Exception {
        manager.init(new PMContext(
                directory,
                new MemoryFileSystem(),
                RepositoryImpl.ROOT_NODE_ID,
                new NamespaceRegistryImpl(new MemoryFileSystem()),
                null,
                null,
                new RepositoryStatisticsImpl()));
        try {
            assertCreateNewNode(manager);
            assertCreateNewProperty(manager);
            assertMissingItemStates(manager);
            assertCreateUpdateDelete(manager);
        } finally {
            manager.close();
        }
    }

    private void assertCreateNewNode(PersistenceManager manager) {
        NodeState state = manager.createNew(NODE_ID);
        assertNotNull(state);
        assertEquals(NODE_ID, state.getId());
        assertEquals(NODE_ID, state.getNodeId());

        assertTrue(state.isNode());
        assertFalse(state.isTransient());
        assertFalse(state.isStale());
        assertFalse(state.isShareable());

        assertNull(state.getContainer());
        assertEquals(0, state.getModCount());
        assertTrue(state.getChildNodeEntries().isEmpty());
        assertTrue(state.getPropertyNames().isEmpty());
    }

    private void assertCreateNewProperty(PersistenceManager manager) {
        PropertyState state = manager.createNew(PROPERTY_ID);
        assertNotNull(state);
        assertEquals(PROPERTY_ID, state.getId());
        assertEquals(PROPERTY_ID, state.getPropertyId());
        assertEquals(NODE_ID, state.getParentId());

        assertFalse(state.isNode());
        assertFalse(state.isTransient());
        assertFalse(state.isStale());
    }

    private void assertMissingItemStates(PersistenceManager manager)
            throws Exception {
        assertFalse(manager.exists(NODE_ID));
        try {
            manager.load(NODE_ID);
            fail();
        } catch (NoSuchItemStateException expected) {
        }

        assertFalse(manager.exists(PROPERTY_ID));
        try {
            manager.load(PROPERTY_ID);
            fail();
        } catch (NoSuchItemStateException expected) {
        }

        assertFalse(manager.existsReferencesTo(NODE_ID));
        try {
            manager.loadReferencesTo(NODE_ID);
            fail();
        } catch (NoSuchItemStateException expected) {
        }
    }

    private void assertCreateUpdateDelete(PersistenceManager manager)
            throws Exception {
        NodeState node = new NodeState(
                NODE_ID, TEST, RepositoryImpl.ROOT_NODE_ID,
                ItemState.STATUS_NEW, true);
        node.addChildNodeEntry(TEST, CHILD_ID);
        node.addPropertyName(NameConstants.JCR_PRIMARYTYPE);
        node.addPropertyName(TEST);

        NodeState child = new NodeState(
                CHILD_ID, TEST, NODE_ID, ItemState.STATUS_NEW, true);
        child.addPropertyName(NameConstants.JCR_PRIMARYTYPE);

        PropertyState property =
            new PropertyState(PROPERTY_ID, ItemState.STATUS_NEW, true);
        property.setType(PropertyType.REFERENCE);
        property.setValues(
                new InternalValue[] { InternalValue.create(CHILD_ID) });

        NodeReferences references = new NodeReferences(CHILD_ID);
        references.addReference(PROPERTY_ID);

        ChangeLog create = new ChangeLog();
        create.added(node);
        create.added(child);
        create.added(property);
        create.modified(references);
        manager.store(create);

        assertTrue(manager.exists(NODE_ID));
        assertTrue(manager.exists(CHILD_ID));
        assertTrue(manager.exists(PROPERTY_ID));
        assertTrue(manager.existsReferencesTo(CHILD_ID));

        assertEquals(node, manager.load(NODE_ID));
        assertEquals(child, manager.load(CHILD_ID));
        assertEquals(property, manager.load(PROPERTY_ID));
        assertEquals(references, manager.loadReferencesTo(CHILD_ID));

        references.removeReference(PROPERTY_ID);
        node.setStatus(ItemState.STATUS_EXISTING);
        ChangeLog update = new ChangeLog();
        update.modified(references);
        node.removePropertyName(TEST);
        update.deleted(property);
        update.modified(node);
        manager.store(update);

        assertTrue(manager.exists(NODE_ID));
        assertTrue(manager.exists(CHILD_ID));
        assertFalse(manager.exists(PROPERTY_ID));
        assertFalse(manager.existsReferencesTo(CHILD_ID));

        assertEquals(node, manager.load(NODE_ID));
        assertEquals(child, manager.load(CHILD_ID));

        ChangeLog delete = new ChangeLog();
        delete.deleted(child);
        delete.deleted(node);
        manager.store(delete);

        assertFalse(manager.exists(NODE_ID));
        assertFalse(manager.exists(CHILD_ID));
        assertFalse(manager.exists(PROPERTY_ID));
        assertFalse(manager.existsReferencesTo(CHILD_ID));
    }

    private void assertEquals(NodeState expected, NodeState actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getNodeId(), actual.getNodeId());
        assertEquals(expected.getNodeTypeName(), actual.getNodeTypeName());
        assertEquals(expected.getMixinTypeNames(), actual.getMixinTypeNames());
        assertEquals(expected.getPropertyNames(), actual.getPropertyNames());
        assertEquals(expected.getChildNodeEntries(), actual.getChildNodeEntries());
    }

    private void assertEquals(PropertyState expected, PropertyState actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getPropertyId(), actual.getPropertyId());
        assertEquals(expected.getType(), actual.getType());
        assertTrue(Arrays.equals(expected.getValues(), actual.getValues()));
    }

    private void assertEquals(NodeReferences expected, NodeReferences actual) {
        assertEquals(expected.getTargetId(), actual.getTargetId());
        assertEquals(expected.getReferences(), actual.getReferences());
    }

}
