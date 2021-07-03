/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.oscache.web.tag;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * GroupsTag is a tag that add a comma-delimited list of groups to an ancestor CacheTag's groups.<p>
 *
 * @author <a href="mailto:ltorunski@t-online.de">Lars Torunski</a>
 */
public class GroupsTag extends TagSupport {
    private Object groups = null;

    public int doStartTag() throws JspTagException {
        CacheTag ancestorCacheTag = (CacheTag) TagSupport.findAncestorWithClass(this, CacheTag.class);

        if (ancestorCacheTag == null) {
            throw new JspTagException("GroupsTag cannot be used from outside a CacheTag");
        }

        ancestorCacheTag.addGroups(String.valueOf(groups));
        
        return EVAL_BODY_INCLUDE;
    }

    public void setGroups(Object groups) {
        this.groups = groups;
    }
}
