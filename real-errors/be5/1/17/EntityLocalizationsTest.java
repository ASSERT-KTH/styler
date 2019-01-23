package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.EntityLocalizations.LocalizationRow;
import one.util.streamex.StreamEx;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class EntityLocalizationsTest
{
    @Test
    public void testLocalizations()
    {
        Project proj = new Project("test");
        proj.getApplication().getLocalizations().addLocalization("de", "entity", Arrays.asList("@AllQueries", "topic"), "Hello", "Guten Tag");
        Entity entity = new Entity("entity", proj.getApplication(), EntityType.TABLE);
        DataElementUtils.save(entity);
        DataElementUtils.save(new Query("testQuery", entity));
        DataElementUtils.save(new Query("testQuery2", entity));
        EntityLocalizations el = proj.getApplication().getLocalizations().get("de").get("entity");
        Set<LocalizationRow> expected = StreamEx.of("testQuery", "testQuery2", "topic")
                .map(topic -> new EntityLocalizations.LocalizationRow(topic, "Hello", "Guten Tag")).toSet();
        assertEquals(expected, el.getRawRows());
        assertEquals(expected, el.getRows());
        el.remove("Hello", Collections.singleton("topic"));
        expected = StreamEx.of("testQuery", "testQuery2")
                .map(topic -> new EntityLocalizations.LocalizationRow(topic, "Hello", "Guten Tag")).toSet();
        assertEquals(expected, el.getRawRows());
        assertEquals(expected, el.getRows());
    }
}
