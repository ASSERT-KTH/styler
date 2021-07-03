package org.wso2.registry.jackrabbit.nodetype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.jackrabbit.ocm.manager.collectionconverter.impl.NTCollectionConverterImpl;
import org.apache.jackrabbit.ocm.manager.collectionconverter.impl.ResidualPropertiesCollectionConverterImpl;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrType = "registry:resource", extend = HierarchyNode.class)
public class Resource extends HierarchyNode {

    @Field(jcrName = "registry:content")
    private byte[] content;

    @Field(jcrName = "registry:description")
    private String description;

    @Field(jcrName = "registry:mediaType")
    private String mediaType;

    @Collection(jcrName = "key*", collectionConverter = ResidualPropertiesCollectionConverterImpl.class)
    private Map<String, String> properties;

    @Collection(autoUpdate = false, elementClassName = Comment.class, collectionConverter = NTCollectionConverterImpl.class)
    private ArrayList<Comment> comments;

    public byte[] getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Map getProperties() {
        return properties;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }

    public void setProperty(String key, String value) {
        if (properties == null) {
            properties = new HashMap<String, String>();
        }
        properties.put(key, value);
    }

    public String getProperty(String key) {
        if (properties == null) {
            return null;
        }
        return (String) properties.get(key);
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        if (comments == null) {
            comments = new ArrayList<Comment>();
        }
        comments.add(comment);
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

}
