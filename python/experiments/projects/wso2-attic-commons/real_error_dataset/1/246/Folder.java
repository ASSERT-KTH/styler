package org.wso2.registry.jackrabbit.nodetype;

import java.util.ArrayList;

import org.apache.jackrabbit.ocm.manager.collectionconverter.impl.NTCollectionConverterImpl;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrType = "nt:folder", extend = HierarchyNode.class, discriminator = false)
public class Folder extends HierarchyNode {

    @Collection(autoUpdate = false, elementClassName = HierarchyNode.class, collectionConverter = NTCollectionConverterImpl.class)
    private java.util.Collection children;

    public Folder() {

    }

    public Folder(String path) {
        this.setPath(path);
    }

    public java.util.Collection getChildren() {
        return children;
    }

    public void setChildren(java.util.Collection children) {
        this.children = children;
    }

    public void addChild(HierarchyNode node) {
        if (children == null) {
            children = new ArrayList();
        }
        children.add(node);
    }

}
