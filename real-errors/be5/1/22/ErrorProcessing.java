package com.developmentontheedge.be5.operation.test;

import com.developmentontheedge.be5.operation.model.Operation;
import com.developmentontheedge.be5.operation.model.OperationResult;
import com.developmentontheedge.be5.operation.model.OperationStatus;
import com.developmentontheedge.be5.operation.support.TestOperationSupport;
import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertyBuilder;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import org.junit.Assert;

import java.util.Map;


public class ErrorProcessing extends TestOperationSupport implements Operation
{
    private DynamicPropertySet dps = new DynamicPropertySetSupport();

    @Override
    public Object getParameters(Map<String, Object> presetValues) throws Exception
    {
        if ("withoutParams".equals(presetValues.get("name")))
        {
            return null;
        }

        dps.add(new DynamicPropertyBuilder("name", String.class)
                .value(presetValues.getOrDefault("name", ""))
                .attr(BeanInfoConstants.COLUMN_SIZE_ATTR, 30)
                .get());

        dps.add(new DynamicPropertyBuilder("propertyForAnotherEntity", String.class).value("text").get());

        if (presetValues.containsKey("booleanProperty"))
        {
            dps.add(new DynamicPropertyBuilder("booleanProperty", Boolean.class).value(presetValues.getOrDefault("booleanProperty", false)).get());
        }

        DynamicProperty name = dps.getProperty("name");

        if (name.getValue().equals("generateErrorInProperty"))
        {
            validator.setError(name, "Error in property (getParameters)");
        }

        if (name.getValue().equals("generateErrorStatus"))
        {
            setResult(OperationResult.error("The operation can not be performed."));
        }

        if (name.getValue().equals("generateError"))
        {
            throw new IllegalArgumentException();
        }

        if (name.getValue().equals("generateCall"))
        {
            Assert.assertEquals(OperationStatus.GENERATE, getStatus());
        }

        if (name.getValue().equals("executeErrorInProperty"))
        {
            Assert.assertEquals(OperationStatus.EXECUTE, getStatus());
        }

        return dps;
    }

    @Override
    public void invoke(Object parameters) throws Exception
    {
        dps.remove("propertyForAnotherEntity");

        DynamicProperty name = dps.getProperty("name");

        if (name.getValue().equals("executeErrorInProperty"))
        {
            validator.setError(name, "Error in property (invoke)");
            return;
        }

        if (name.getValue().equals("executeErrorStatus"))
        {
            setResult(OperationResult.error("An error occurred while performing operations."));
            return;
        }

        if (name.getValue().equals("executeError"))
        {
            throw new IllegalArgumentException();
        }

        setResult(OperationResult.finished());
    }
}
