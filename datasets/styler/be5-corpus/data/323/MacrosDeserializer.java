package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.FreemarkerCatalog;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.ProjectFileStructure;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.serialization.ModuleLoader2;
import com.developmentontheedge.be5.metadata.serialization.ProjectFileSystem;

import java.nio.file.Path;
import java.util.Map;

import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_PROJECT_FILE_STRUCTURE;

class MacrosDeserializer extends FileDeserializer
{

    private YamlDeserializer yamlDeserializer;
    private final Module module;

    MacrosDeserializer(YamlDeserializer yamlDeserializer, LoadContext loadContext, final Module module) throws ReadException
    {
        super(loadContext, ProjectFileSystem.getProjectFile(ModuleLoader2.getModulePath(module.getName())));
        this.yamlDeserializer = yamlDeserializer;
        this.module = module;
    }

    @Override
    protected void doDeserialize(Object serializedRoot) throws ReadException
    {
        final Path root = ModuleLoader2.getModulePath(module.getName());
        final Map<String, Object> serializedModule = asMap(serializedRoot);
        final Map<String, Object> serializedModuleBody = asMap(serializedModule.values().iterator().next());
        final String projectName = root.getFileName().toString(); // not sure if this has any sense
        final boolean isModule = true;
        final Project project = new Project(projectName, isModule); /* dummy project, required to create a file structure */
        project.setLocation(root);

        final Object serializedPfs = serializedModuleBody.get(TAG_PROJECT_FILE_STRUCTURE);

        if (serializedPfs == null)
        {
            project.setProjectFileStructure(new ProjectFileStructure(project));
        }
        else
        {
            project.setProjectFileStructure(yamlDeserializer.readProjectFileStructure(this, asMap(serializedPfs), project));
        }

        yamlDeserializer.fileSystem = new ProjectFileSystem(project);

        if (module.getMacroCollection() == null)
        {
            DataElementUtils.saveQuiet(new FreemarkerCatalog(Module.MACROS, module));
        }

        yamlDeserializer.readMacroFiles(this, serializedModuleBody, module.getMacroCollection());
    }

}
