package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.JavaScriptForm;
import com.developmentontheedge.be5.metadata.model.ManagedFileType;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.serialization.Fields;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;

import java.nio.file.Path;
import java.util.Map;

import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_JS_FORMS;

class FormsDeserializer extends FileDeserializer
{

    private final BeModelCollection<JavaScriptForm> target;

    FormsDeserializer(LoadContext loadContext, Path path, BeModelCollection<JavaScriptForm> target) throws ReadException
    {
        super(loadContext, path, true);
        this.target = target;
    }

    @Override
    protected void doDeserialize(Object serializedRoot) throws ReadException
    {
        final Map<String, Object> serializedForms = asMap(asMap(serializedRoot).get(TAG_JS_FORMS));

        readForms(serializedForms);
        target.getProject().getAutomaticDeserializationService().registerFile(path, ManagedFileType.FORMS);
    }

    private void readForms(final Map<String, Object> serializedForms)
    {
        for (Map.Entry<String, Object> serializedForm : serializedForms.entrySet())
        {
            try
            {
                readForm(serializedForm);
            }
            catch (ReadException e)
            {
                loadContext.addWarning(e.attachElement(target));
            }
        }
    }

    private void readForm(Map.Entry<String, Object> serializedForm) throws ReadException
    {
        final String name = serializedForm.getKey();
        final Map<String, Object> serializedFormBody = asMap(serializedForm.getValue());
        final JavaScriptForm form = new JavaScriptForm(name, target);
        readFields(form, serializedFormBody, Fields.jsForms());
        DataElementUtils.saveQuiet(form);
        final Path file = form.getLinkedFile();

        if (file == null)
        {
            loadContext.addWarning(new ReadException(form, path, "File cannot be resolved for module " + form.getModuleName()));
        }
        else
        {
            try
            {
                form.load();
            }
            catch (ReadException e)
            {
                loadContext.addWarning(e.attachElement(form));
            }
        }
    }

}
