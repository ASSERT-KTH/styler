package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import one.util.streamex.StreamEx;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RoleSetTest
{
    @Test
    public void testRoleSet()
    {
        Project prj = new Project("test");
        prj.addRole("Admin");
        prj.addRole("Guest");
        prj.addRole("User");
        prj.addRole("Manager");
        prj.addRole("Supervisor");
        BeModelCollection<RoleGroup> groups = prj.getRoleGroups();
        RoleGroup management = new RoleGroup("Management", groups);
        management.getRoleSet().addInclusionAll(Arrays.asList("Admin", "Manager", "Supervisor", "None"));
        List<ProjectElementException> errors = management.getErrors();
        assertEquals(1, errors.size());
        assertEquals("test/Security/Role groups/Management: Group contains unknown roles/subgroups: [None]", errors.get(0).getMessage());
        DataElementUtils.save(management);
        assertEquals(StreamEx.of("Admin", "Guest", "User", "Manager", "Supervisor").toSet(), prj.getRoles());
        assertEquals(Arrays.asList("Admin", "Guest", "Manager", "Supervisor", "User", "@AllRoles", "@AllRolesExceptGuest", "@Management"),
                prj.getRolesWithGroups());

        RoleSet roleSet = new RoleSet(prj);
        assertTrue(roleSet.getFinalRoles().isEmpty());
        roleSet.add("@Management");
        assertEquals(StreamEx.of("Admin", "Manager", "Supervisor").toSet(), roleSet.getFinalRoles());
        roleSet.addExclusion("Manager");
        roleSet.add("Guest");
        assertEquals(StreamEx.of("Admin", "Guest", "Supervisor").toSet(), roleSet.getFinalRoles());
        roleSet = new RoleSet(prj);
        roleSet.parseRoles(Arrays.asList("@Management", "+Guest", "-Manager"));
        assertEquals(StreamEx.of("@Management", "Guest").toSet(), roleSet.getAllIncludedValues());
        assertEquals(StreamEx.of("Manager").toSet(), roleSet.getAllExcludedValues());
        assertEquals(StreamEx.of("Admin", "Guest", "Supervisor").toSet(), roleSet.getFinalRoles());
        assertEquals(Arrays.asList("@Management", "Guest", "-Manager"), roleSet.printValues());
        assertEquals("Admin, Guest, Supervisor", roleSet.getFinalRolesString());
        roleSet.setValues(Arrays.asList("@AllRolesExceptGuest"));
        roleSet.setUsePrototype(true);
        assertEquals(Arrays.asList("+@AllRolesExceptGuest", "-Manager"), roleSet.printValues());
        assertEquals(StreamEx.of("Admin", "User", "Supervisor").toSet(), roleSet.getFinalRoles());
        roleSet.setExcludedValues(Arrays.asList("User", "Admin"));
        assertEquals(Arrays.asList("+@AllRolesExceptGuest", "-Admin", "-User"), roleSet.printValues());
        assertEquals(StreamEx.of("Manager", "Supervisor").toSet(), roleSet.getFinalRoles());
        roleSet.setExcludedValues(Collections.emptyList());
        roleSet.setValues(Arrays.asList("Admin", "User", "Manager", "Supervisor"));
        roleSet.foldSystemGroup();
        assertEquals(Arrays.asList("+@AllRolesExceptGuest"), roleSet.printValues());
    }

    @Test
    public void testRoleSetCustomization()
    {
        Project prj = new Project("test", true);
        prj.setRoles(Arrays.asList("Admin", "Guest", "User", "Manager", "Supervisor"));
        Entity e = new Entity("entity", prj.getApplication(), EntityType.TABLE);
        DataElementUtils.save(e);
        Query q = new Query("query", e);
        DataElementUtils.save(q);
        q.getRoles().addInclusionAll(Arrays.asList("Admin", "Guest"));

        Project prj2 = new Project("test2");
        prj2.setRoles(prj.getRoles());
        Module m = new Module("test", prj2.getModules());
        DataElementUtils.save(m);

        Entity e2 = e.clone(m.getOrCreateEntityCollection(), e.getName(), true);
        assertFalse(e2.isCustomized());
        Query q2 = e2.getQueries().get("query");
        assertFalse(q2.isCustomized());
        assertEquals(StreamEx.of("Admin", "Guest").toSet(), q2.getRoles().getFinalRoles());
        assertEquals(Collections.emptyList(), q2.getRoles().printValues());
        q2.getRoles().setValuesArray(new String[]{"User"});
        assertTrue(q2.isCustomized());
        assertEquals(Collections.singleton("roles"), q2.getCustomizedProperties());
        q2.getRoles().setExcludedValuesArray(new String[]{"Guest"});
        assertEquals(StreamEx.of("Admin", "User").toSet(), q2.getRoles().getFinalRoles());
        assertEquals(Arrays.asList("+User", "-Guest"), q2.getRoles().printValues());
        q2.getRoles().setUsePrototype(false);
        assertEquals(StreamEx.of("User").toSet(), q2.getRoles().getFinalRoles());
        assertEquals(Arrays.asList("User", "-Guest"), q2.getRoles().printValues());
    }
}
