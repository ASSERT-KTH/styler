package com.developmentontheedge.be5.operation.services;

import com.developmentontheedge.be5.base.services.GroovyRegister;
import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.base.services.ProjectProvider;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.GroovyOperation;
import com.developmentontheedge.be5.metadata.model.Operation;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.developmentontheedge.be5.metadata.model.Operation.OPERATION_TYPE_GROOVY;


public class GroovyOperationLoader
{
    private final Meta meta;
    private final GroovyRegister groovyRegister;

    private Map<String, Operation> groovyOperationsMap;

    @Inject
    public GroovyOperationLoader(ProjectProvider projectProvider, Meta meta, GroovyRegister groovyRegister)
    {
        this.groovyRegister = groovyRegister;
        this.meta = meta;

        projectProvider.addToReload(this::initOperationMap);
        initOperationMap();
    }

    void initOperationMap()
    {
        Map<String, com.developmentontheedge.be5.metadata.model.Operation> newOperationMap = new HashMap<>();
        List<Entity> entities = meta.getOrderedEntities("ru");
        for (Entity entity : entities)
        {
            List<String> operationNames = meta.getOperationNames(entity);
            for (String operationName : operationNames)
            {
                com.developmentontheedge.be5.metadata.model.Operation operation = meta.getOperation(entity.getName(), operationName);
                if (operation.getType().equals(OPERATION_TYPE_GROOVY))
                {
                    GroovyOperation groovyOperation = (GroovyOperation) operation;
                    String fileName = groovyOperation.getFileName().replace("/", ".");
                    newOperationMap.put(fileName, operation);
                }
            }
        }

        groovyOperationsMap = newOperationMap;
    }

    public Operation getByFullName(String name)
    {
        return groovyOperationsMap.get(name.replace("/", "."));
    }

    public List<String> preloadSuperOperation(Operation operationMeta)
    {
        String simpleSuperClassName = getSimpleSuperClassName(operationMeta);
        String superOperationCanonicalName = getCanonicalSuperClassName(operationMeta);

        com.developmentontheedge.be5.metadata.model.Operation superOperation = groovyOperationsMap.get(superOperationCanonicalName);

        if (superOperation != null && superOperation.getType().equals(OPERATION_TYPE_GROOVY))
        {
            if (groovyRegister.getGroovyClasses().getIfPresent(superOperationCanonicalName) == null)
            {
                ArrayList<String> list = new ArrayList<>(preloadSuperOperation(superOperation));

                list.add(superOperationCanonicalName);
                groovyRegister.getClass(superOperationCanonicalName,
                        superOperation.getCode(), simpleSuperClassName + ".groovy");
                return list;
            }
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }

    public Class get(Operation operationMeta)
    {
        preloadSuperOperation(operationMeta);
        GroovyOperation groovyOperation = (GroovyOperation) operationMeta;
        String fileName = groovyOperation.getFileName();
        String canonicalName = fileName.replace("/", ".");
        String simpleName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length() - ".groovy".length()).trim();

        return groovyRegister.getClass(canonicalName, operationMeta.getCode(), simpleName + ".groovy");
    }

    public String getSimpleSuperClassName(Operation operationMeta)
    {
        GroovyOperation groovyOperation = (GroovyOperation) operationMeta;
        String fileName = groovyOperation.getFileName();
        String className = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length() - ".groovy".length()).trim();
        String classBegin = "class " + className + " extends ";

        String code = operationMeta.getCode();
        int superClassBeginPos = code.indexOf(classBegin);
        if (superClassBeginPos == -1) return null;

        superClassBeginPos += classBegin.length();
        int superClassEndPos = Math.min(
                code.indexOf(" ", superClassBeginPos) != -1 ? code.indexOf(" ", superClassBeginPos) : 999999999,
                code.indexOf("\n", superClassBeginPos));


        return code.substring(superClassBeginPos, superClassEndPos).trim();
    }

    public String getCanonicalSuperClassName(Operation operationMeta)
    {
        String code = operationMeta.getCode();
        String superOperationName = getSimpleSuperClassName(operationMeta);

        String superOperationFullName = superOperationName + ".groovy";

        int lineBegin = code.indexOf("package ");
        if (lineBegin != -1)
        {
            int lineEnd = code.indexOf("\n", lineBegin);
            String line = code.substring(lineBegin, lineEnd);
            superOperationFullName = line.replace("package ", "").replace(";", "")
                    + "." + superOperationName + ".groovy";
        }

        lineBegin = code.indexOf("import ");
        while (lineBegin != -1)
        {
            int lineEnd = code.indexOf("\n", lineBegin);
            String line = code.substring(lineBegin, lineEnd);
            if (line.contains("." + superOperationName))
            {
                superOperationFullName = line.replace("import ", "").replace(";", "") + ".groovy";
            }
            lineBegin = code.indexOf("import ", lineEnd);
        }
        return superOperationFullName;
    }

}
