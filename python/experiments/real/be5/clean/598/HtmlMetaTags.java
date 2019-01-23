package com.developmentontheedge.be5.server.services;

import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.base.services.UserAwareMeta;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;


public class HtmlMetaTags
{
    private final UserAwareMeta userAwareMeta;
    private final Meta meta;

    @Inject
    public HtmlMetaTags(UserAwareMeta userAwareMeta, Meta meta)
    {
        this.userAwareMeta = userAwareMeta;
        this.meta = meta;
    }

    public Map<String, Object> getTags()
    {
        String title = userAwareMeta.getColumnTitle("index", "page", "title");
        String description = userAwareMeta.getColumnTitle("index", "page", "description");

        Map<String, Object> values = new HashMap<>();

        values.put("lang", meta.getLocale(null));
        values.put("title", title);
        values.put("description", description);

        return values;
    }
}
