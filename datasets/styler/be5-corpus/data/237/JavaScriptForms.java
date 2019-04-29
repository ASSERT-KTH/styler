package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;

public class JavaScriptForms extends BeVectorCollection<JavaScriptForm>
{

    public JavaScriptForms(final Module module)
    {
        super(Module.JS_FORMS, JavaScriptForm.class, module);
    }

    @Override
    public void fireCodeChanged()
    {
        if (getModule().get(getName()) == this)
            getProject().getAutomaticSerializationService().fireCodeChanged(this);
    }

}
