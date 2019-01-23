package com.developmentontheedge.be5.metadata.serialization;

import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * http://e-maxx.ru/algo/topological_sort
 */
public class ProjectTopologicalSort
{
    private Map<String, Project> g = new HashMap<>();

    private Map<String, Boolean> used = new HashMap<>();

    private List<Project> ans = new ArrayList<>();

    private void dfs(String v)
    {
        used.put(v, true);

        for (Module module : g.get(v).getModules())
        {
            if (!used.getOrDefault(module.getName(), false))
            {
                if (!g.containsKey(module.getName()))
                {
                    throw new RuntimeException("Module " + module.getName() +
                            " not found, required for " + v + ". Add module to classpath.");
                }
                dfs(module.getName());
            }
        }

        ans.add(g.get(v));
    }

    private void topological_sort()
    {
        projects.forEach(project -> used.put(project.getAppName(), false));
        ans.clear();

        for (Map.Entry<String, Boolean> entry : used.entrySet())
            if (!entry.getValue())
                dfs(entry.getKey());
        Collections.reverse(ans);
    }

    private Collection<Project> projects;

    public ProjectTopologicalSort(Collection<Project> projects)
    {
        Objects.requireNonNull(projects);
        if (projects.size() == 0) throw new RuntimeException("Project not found.");

        this.projects = projects;

        projects.forEach(project -> g.put(project.getAppName(), project));

        topological_sort();
    }

    public Project getRoot()
    {
        return ans.get(0);
    }
}
