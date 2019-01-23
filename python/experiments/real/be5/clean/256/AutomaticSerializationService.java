package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;

import java.util.ArrayList;
import java.util.List;

public class AutomaticSerializationService
{
    public interface CodeChangedListener<T extends BeModelElement>
    {
        void codeChanged(T element);
    }

    public interface LocalizationCodeChangedListener extends CodeChangedListener<LanguageLocalizations>
    {
    }

    public interface QueryCodeChangedListener extends CodeChangedListener<Query>
    {
    }

    public interface OperationCodeChangedListener extends CodeChangedListener<Operation>
    {
    }

    public interface EntityCodeChangedListener extends CodeChangedListener<Entity>
    {
    }

    public interface EntityCodeAddedListener
    {
        void codeAdded(Entity entity);
    }

    public interface EntityCodeRemovedListener
    {
        void codeRemoved(Entity entity);
    }

    public interface ScriptCodeAddedListener
    {
        void codeAdded(FreemarkerScript entity);
    }

    public interface ScriptCodeRemovedListener
    {
        void codeRemoved(FreemarkerScript entity);
    }

    public interface ConnectionProfilesCodeChangedListener extends CodeChangedListener<BeConnectionProfiles>
    {
    }

    public interface SecurityCodeChangedListener extends CodeChangedListener<SecurityCollection>
    {
    }

    public interface MassChangesCodeChangedListener extends CodeChangedListener<MassChanges>
    {
    }

    public interface CustomizationsCodeChangedListener extends CodeChangedListener<PageCustomizations>
    {
    }

    public interface DaemonsCodeChangedListener extends CodeChangedListener<Daemons>
    {
    }

    public interface FormsCodeChangedListener extends CodeChangedListener<JavaScriptForms>
    {
    }

    public interface PagesCodeChangedListener extends CodeChangedListener<StaticPages>
    {
    }

    public interface ProjectCodeChangedListener extends CodeChangedListener<Project>
    {
    }

    private final List<QueryCodeChangedListener> queryCodeChangedListeners = new ArrayList<>();
    private final List<OperationCodeChangedListener> operationCodeChangedListeners = new ArrayList<>();
    private final List<EntityCodeChangedListener> entityCodeChangedListeners = new ArrayList<>();
    private final List<EntityCodeAddedListener> entityCodeAddedListeners = new ArrayList<>();
    private final List<EntityCodeRemovedListener> entityCodeRemovedListeners = new ArrayList<>();
    private final List<ScriptCodeAddedListener> scriptCodeAddedListeners = new ArrayList<>();
    private final List<ScriptCodeRemovedListener> scriptCodeRemovedListeners = new ArrayList<>();
    private final List<LocalizationCodeChangedListener> localizationCodeChangedListeners = new ArrayList<>();
    private final List<SecurityCodeChangedListener> securityCodeChangedListeners = new ArrayList<>();
    private final List<MassChangesCodeChangedListener> massChangesChangedListeners = new ArrayList<>();
    private final List<ConnectionProfilesCodeChangedListener> connectionProfilesChangedListeners = new ArrayList<>();
    private final List<CustomizationsCodeChangedListener> customizationsChangedListeners = new ArrayList<>();
    private final List<DaemonsCodeChangedListener> daemonsChangedListeners = new ArrayList<>();
    private final List<FormsCodeChangedListener> formsChangedListeners = new ArrayList<>();
    private final List<PagesCodeChangedListener> pagesChangedListeners = new ArrayList<>();
    private final List<ProjectCodeChangedListener> projectChangedListeners = new ArrayList<>();

    /**
     * Note that the word 'code' means 'the file containing the source code' here.
     */
    public AutomaticSerializationService()
    {
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(QueryCodeChangedListener listener)
    {
        if (!queryCodeChangedListeners.contains(listener))
            queryCodeChangedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(QueryCodeChangedListener listener)
    {
        queryCodeChangedListeners.remove(listener);
    }

    void fireCodeChanged(Query query)
    {
        for (QueryCodeChangedListener listener : queryCodeChangedListeners)
        {
            listener.codeChanged(query);
        }
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(OperationCodeChangedListener listener)
    {
        if (!operationCodeChangedListeners.contains(listener))
            operationCodeChangedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(OperationCodeChangedListener listener)
    {
        operationCodeChangedListeners.remove(listener);
    }

    void fireCodeChanged(Operation operation)
    {
        for (OperationCodeChangedListener listener : operationCodeChangedListeners)
        {
            listener.codeChanged(operation);
        }
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(EntityCodeChangedListener listener)
    {
        if (!entityCodeChangedListeners.contains(listener))
            entityCodeChangedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(EntityCodeChangedListener listener)
    {
        entityCodeChangedListeners.remove(listener);
    }

    void fireCodeChanged(Entity entity)
    {
        for (EntityCodeChangedListener listener : entityCodeChangedListeners)
        {
            listener.codeChanged(entity);
        }
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(EntityCodeAddedListener listener)
    {
        if (!entityCodeAddedListeners.contains(listener))
            entityCodeAddedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(EntityCodeAddedListener listener)
    {
        entityCodeAddedListeners.remove(listener);
    }

    void fireCodeAdded(Entity entity)
    {
        for (EntityCodeAddedListener listener : entityCodeAddedListeners)
        {
            listener.codeAdded(entity);
        }
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(EntityCodeRemovedListener listener)
    {
        if (!entityCodeRemovedListeners.contains(listener))
            entityCodeRemovedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(EntityCodeRemovedListener listener)
    {
        entityCodeRemovedListeners.remove(listener);
    }

    void fireCodeRemoved(Entity entity)
    {
        for (EntityCodeRemovedListener listener : entityCodeRemovedListeners)
        {
            listener.codeRemoved(entity);
        }
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(ScriptCodeAddedListener listener)
    {
        if (!scriptCodeAddedListeners.contains(listener))
            scriptCodeAddedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(ScriptCodeAddedListener listener)
    {
        scriptCodeAddedListeners.remove(listener);
    }

    void fireCodeAdded(FreemarkerScript script)
    {
        for (ScriptCodeAddedListener listener : scriptCodeAddedListeners)
        {
            listener.codeAdded(script);
        }
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(ScriptCodeRemovedListener listener)
    {
        if (!scriptCodeRemovedListeners.contains(listener))
            scriptCodeRemovedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(ScriptCodeRemovedListener listener)
    {
        scriptCodeRemovedListeners.remove(listener);
    }

    void fireCodeRemoved(FreemarkerScript script)
    {
        scriptCodeRemovedListeners.forEach(listener -> listener.codeRemoved(script));
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(LocalizationCodeChangedListener listener)
    {
        if (!localizationCodeChangedListeners.contains(listener))
            localizationCodeChangedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(LocalizationCodeChangedListener listener)
    {
        localizationCodeChangedListeners.remove(listener);
    }

    void fireCodeChanged(LanguageLocalizations languageLocalizations)
    {
        localizationCodeChangedListeners.forEach(listener -> listener.codeChanged(languageLocalizations));
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(SecurityCodeChangedListener listener)
    {
        if (!securityCodeChangedListeners.contains(listener))
            securityCodeChangedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(SecurityCodeChangedListener listener)
    {
        securityCodeChangedListeners.remove(listener);
    }

    void fireCodeChanged(SecurityCollection languageLocalizations)
    {
        securityCodeChangedListeners.forEach(listener -> listener.codeChanged(languageLocalizations));
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(MassChangesCodeChangedListener listener)
    {
        if (!massChangesChangedListeners.contains(listener))
            massChangesChangedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(MassChangesCodeChangedListener listener)
    {
        massChangesChangedListeners.remove(listener);
    }

    void fireCodeChanged(MassChanges massChanges)
    {
        massChangesChangedListeners.forEach(listener -> listener.codeChanged(massChanges));
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(ConnectionProfilesCodeChangedListener listener)
    {
        if (!connectionProfilesChangedListeners.contains(listener))
            connectionProfilesChangedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(ConnectionProfilesCodeChangedListener listener)
    {
        connectionProfilesChangedListeners.remove(listener);
    }

    void fireCodeChanged(BeConnectionProfiles profiles)
    {
        connectionProfilesChangedListeners.forEach(listener -> listener.codeChanged(profiles));
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(CustomizationsCodeChangedListener listener)
    {
        if (!customizationsChangedListeners.contains(listener))
            customizationsChangedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(CustomizationsCodeChangedListener listener)
    {
        customizationsChangedListeners.remove(listener);
    }

    void fireCodeChanged(PageCustomizations customizations)
    {
        customizationsChangedListeners.forEach(listener -> listener.codeChanged(customizations));
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(DaemonsCodeChangedListener listener)
    {
        if (!daemonsChangedListeners.contains(listener))
            daemonsChangedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(DaemonsCodeChangedListener listener)
    {
        daemonsChangedListeners.remove(listener);
    }

    void fireCodeChanged(Daemons daemons)
    {
        daemonsChangedListeners.forEach(listener -> listener.codeChanged(daemons));
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(FormsCodeChangedListener listener)
    {
        if (!formsChangedListeners.contains(listener))
            formsChangedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(FormsCodeChangedListener listener)
    {
        formsChangedListeners.remove(listener);
    }

    void fireCodeChanged(JavaScriptForms forms)
    {
        formsChangedListeners.forEach(listener -> listener.codeChanged(forms));
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(PagesCodeChangedListener listener)
    {
        if (!pagesChangedListeners.contains(listener))
            pagesChangedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(PagesCodeChangedListener listener)
    {
        pagesChangedListeners.remove(listener);
    }

    void fireCodeChanged(StaticPages languageStaticPages)
    {
        pagesChangedListeners.forEach(listener -> listener.codeChanged(languageStaticPages));
    }

    /**
     * Adds a listener for code changes in this project serialization manager.
     * Has no effect if an identical listener is already registered.
     *
     * @param listener
     */
    public void addListener(ProjectCodeChangedListener listener)
    {
        if (!projectChangedListeners.contains(listener))
            projectChangedListeners.add(listener);
    }

    /**
     * Removes the given code change listener from this project serialization manager.
     * Has no effect if an identical listener is not registered.
     *
     * @param listener
     */
    public void removeListener(ProjectCodeChangedListener listener)
    {
        projectChangedListeners.remove(listener);
    }

    void fireCodeChanged(Project project)
    {
        projectChangedListeners.forEach(listener -> listener.codeChanged(project));
    }
}
