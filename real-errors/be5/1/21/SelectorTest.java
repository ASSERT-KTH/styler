package com.developmentontheedge.be5.metadata.model.selectors;

import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.EntityType;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.selectors.parser.ParseException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SelectorTest
{
    @Test
    public void testParser() throws ParseException
    {
        SelectorRule rule = UnionSelectorRule.create("a , b, Entity:not(.table#Test) Operation#Insert, Query[name$='4JS']");
        assertEquals("a, b, Entity:not(.table#Test) Operation#Insert, Query[name$=\"4JS\"]", rule.toString());
        rule = UnionSelectorRule.create("Module#mod Query[name=\"All records\"]");
        assertEquals("Module#mod Query[name=\"All records\"]", rule.toString());

        rule = UnionSelectorRule.create("#\\32test");
        assertTrue(rule.matches(new Module("2test", null)));
        rule = UnionSelectorRule.create("#\\32 test");
        assertTrue(rule.matches(new Module("2test", null)));
    }

    @Test
    public void testToString()
    {
        UnionSelectorRule rule = new UnionSelectorRule(
                new HierarchySelectorRule(
                        new ComplexSelectorRule(new ElementClassRule("Entity"), new AttributeRule("name", "gps", AttributeRule.OP_STARTS)),
                        new ComplexSelectorRule(new ElementClassRule("Query"), new AttributeRule("name", "All records"))),
                new HierarchySelectorRule(
                        new ComplexSelectorRule(new ElementClassRule("Operation"), new AttributeRule("name", "Insert"))));
        assertEquals("Entity[name^=gps] Query[name=\"All records\"], Operation#Insert", rule.toString());
    }

    @Test
    public void testMatch() throws ParseException
    {
        Project project = new Project("test");
        Module app = project.getApplication();
        Module mod = new Module("mod", project.getModules());
        DataElementUtils.save(mod);
        Entity appTable = new Entity("appTable", app, EntityType.TABLE);
        DataElementUtils.save(appTable);
        DataElementUtils.save(new Query("All records", appTable));
        Entity appDict = new Entity("appDict", app, EntityType.DICTIONARY);
        DataElementUtils.save(appDict);
        Query dictRecords = new Query("All records", appDict);
        dictRecords.setInvisible(true);
        DataElementUtils.save(dictRecords);
        Entity modTable = new Entity("modTable", mod, EntityType.DICTIONARY);
        DataElementUtils.save(modTable);
        DataElementUtils.save(new Query("All records", modTable));

        SelectorRule rule1 = UnionSelectorRule.create("Query[invisible=true]");
        assertEquals("Query[invisible=true]", rule1.toString());
        checkRule(rule1, "test/application/Entities/appDict/Queries/All records", project);

        SelectorRule rule2 = UnionSelectorRule.create(".table Query[name=\"All records\"]");
        assertEquals(".table Query[name=\"All records\"]", rule2.toString());
        checkRule(rule2, "test/application/Entities/appTable/Queries/All records", project);

        SelectorRule rule3 = UnionSelectorRule.create("Module#mod Query[name=\"All records\"]");
        assertEquals("Module#mod Query[name=\"All records\"]", rule3.toString());
        checkRule(rule3, "test/Modules/mod/Entities/modTable/Queries/All records", project);

        SelectorRule rule4 = UnionSelectorRule.create("Entity:not(.dictionary)");
        assertEquals("Entity:not(.dictionary)", rule4.toString());
        checkRule(rule4, "test/application/Entities/appTable", project);
    }

    private void checkRule(SelectorRule rule, String path, Project project)
    {
        List<BeModelElement> list = SelectorUtils.select(project, rule);
        assertEquals(rule.toString(), 1, list.size());
        assertEquals(rule.toString(), path, list.get(0).getCompletePath().toString());
    }
}
